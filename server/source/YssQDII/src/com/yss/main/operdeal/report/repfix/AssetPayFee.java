package com.yss.main.operdeal.report.repfix;

import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.dsub.BaseBean;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.abchina.cps2.cde.CPS2YSSExporter;
import com.abchina.cps2.cde.IYSSToCPS;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

/**
 * story 3489 add by yeshenghong
 * 
 * */
public class AssetPayFee extends BaseBuildCommonRep {
	public AssetPayFee() {
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getFeeYear() {
		return feeYear;
	}

	public void setFeeYear(String feeYear) {
		this.feeYear = feeYear;
	}

	public String getFeeMonth() {
		return feeMonth;
	}

	public void setFeeMonth(String feeMonth) {
		this.feeMonth = feeMonth;
	}

	private String portCode = "";// 组合代码

	private String feeYear = "";// 费用年份

	private String feeMonth = "";// 费用月份

	private String sOperType = "Search";

	private String updateData = "";

	protected CommonRepBean repBean;

	// private TradeSubBean tdSub;
	// private CommonRepBean repBean;
	// String strShowColor = ""; //显示的颜色
	//
	// private java.util.Date endDate = null;
	// private String oldDays = "";
	// private String sPort = "";
	// /**
	// * buildReport
	// *
	// * @param sType String
	// * @return String
	// */
	// public String buildReport(String sType) throws YssException {
	// String sResult = "";
	// sResult = buildResult(this.endDate, this.sPort, this.oldDays);
	// return sResult;
	// }
	//
	// /**
	// * initBuildReport
	// *
	// * @param bean BaseBean
	// */
	// public void initBuildReport(BaseBean bean) throws YssException {
	// repBean = (CommonRepBean) bean;
	// this.parse(repBean.getRepCtlParam());
	// }
	//
	/**
	 * 完成初始化 initBuildReport
	 * 
	 * @param bean
	 *            BaseBean: 通用报表类
	 */
	public void initBuildReport(BaseBean bean) throws YssException {
		repBean = (CommonRepBean) bean;
		// 解析前台传入的条件字符串
		this.parseRowStr(this.repBean.getRepCtlParam());
	}

	public void parseRowStr(String sRowStr) throws YssException {
		String[] sReq = sRowStr.split("\n");
		try {
			this.portCode = sReq[0].split("\r")[1];
			this.feeYear = sReq[1].split("\r")[1];
			this.feeMonth = sReq[2].split("\r")[1];
			this.sOperType = sReq[3].split("\r")[1];
			if (sReq.length > 4) {
				updateData = sReq[4].split("\r")[1];
			}
		} catch (Exception e) {
			throw new YssException("解析参数出错", e);
		}
	}

	/**
	 * 生成报表
	 * add by yeshenghong story3489 20130217
	 * */
	public String buildReport(String sType) throws YssException {
		// 从前台传入的操作请求为查询时，调用查询数据的方法
		if (sOperType.trim().equals("Search")) {
			return this.searchPayableFeeTable();
		} else if (sOperType.trim().equals("Confirm")) {
			return this.confirmPayableFeeTable();
		} else {
			return this.generatePayableFeeTable();
		}
	}


	// add by yeshenghong 生成应付费用数据 story3489 20130217
	public String generatePayableFeeTable() throws YssException
     {
    	 /***"07004",//应付管理费	  "07005"//应付风险准备金	
			"07012",// 应付托管费   "07013",//应付销售服务费	
			"07019" 应付席位佣金***/
		String strSql = "";
		//两个计算方法一样 故一起遍历计算
//		String[] feeCodes = {
//				"07012",// 应付托管费	
//				"07013",//应付销售服务费	
//				}; //计算方法改变  故不采用遍历   20130321 yeshenghong story3736
		ResultSet rs = null;
		ResultSet rsCal = null;
		ResultSet rsInner = null;
		Boolean bTrans = false;
		PreparedStatement pst = null;
		Connection conn = null;
		String curPort = "";
		String curGroup = "";
		try {
			YssFinance finance = new YssFinance();
			finance.setYssPub(pub);

			String[] portCodes  = this.portCode.split(",");
			conn = dbl.getConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			for(int h=0;h<portCodes.length;h++)
			{
				curGroup = portCodes[h].split("-")[0];
				curPort =  portCodes[h].split("-")[1];
				
				strSql = " select distinct fportname,fassetcode from " + "tb_" + curGroup + "_para_portfolio p join lsetlist l on p.fassetcode = l.fsetid" +  " where fportcode = " + dbl.sqlString(curPort);
				String portName = "";
				String assetCode = "";
				rs = dbl.openResultSet(strSql);
				if(rs.next())
				{
					portName = rs.getString("fportname");
					assetCode = rs.getString("fassetcode");
				}else
				{
					dbl.closeResultSetFinal(rs);//add  by yeshenghong story3715 20130318
					continue;
				}
				dbl.closeResultSetFinal(rs); 
				
				String tablePrefix = "A" + this.feeYear + 
	            YssFun.formatNumber(Integer.parseInt(finance.getBookSetId(curGroup,curPort)), "000");
				
				strSql = " delete from TB_NH_PAYFEEDATA where assertcode = " + dbl.sqlString(assetCode) + " and feeyear = " + this.feeYear;
				dbl.executeSql(strSql);//删除之前的数据
				
				strSql = " insert into TB_NH_PAYFEEDATA (assertcode,assertname, feeyear, feemonth, feetype, " +
				 " feeD, feeJ, feeEndbal, feePayType, feeflag,FEEBY) values (?,?,?,?,?,?,?,?,?,?,?)";
				pst = dbl.getPreparedStatement(strSql);
				
				double feeD = 0;//N月计提
				double feeJ = 0;//N月支付
				double lastFeeSum = 0; //上年总额
				double riskFeeSum = 0;//风险准备金上年余额
				double lastTermFee = 0;
				double lastRiskFee = 0;//风险上期余额
				double riskFeeD = 0;//风险准备金—本年第N月计提
				double riskFeeJ = 0;//风险准备金—本年第N月支付
				
				strSql = " select * from TB_" + curGroup + "_REP_PayFeeParaConfigure where fportcode = " + dbl.sqlString(curPort) + 
						 " and FFeeType  = '07004' and fcheckstate = 1" ;//计算管理费
				rs = dbl.openResultSet(strSql);
				if(rs.next())
				{
					strSql =  " select sum(-1 * b.fendbal) as fstartbal from " + tablePrefix + "lbalance b join " + tablePrefix + "laccount a on b.facctcode = a.facctcode " +
								" where facctdetail = 1 and a.facctattr in ('应付管理人报酬','应付管理人报酬_管理费','应付管理人报酬_业绩报酬') and  fmonth = 0";
					rsCal = dbl.openResultSet(strSql);
					if(rsCal.next())
					{
						lastFeeSum = rsCal.getDouble("fstartbal");
					}else
					{
						lastFeeSum = 0;
					}
					dbl.closeResultSetFinal(rsCal);
					
					strSql = " select * from TB_" + curGroup + "_REP_PayFeeParaConfigure where fportcode = " + dbl.sqlString(curPort) + 
					 " and FFeeType  = '07005' and fcheckstate = 1" ;//计算风险准备金
					rsCal = dbl.openResultSet(strSql);
					double riskRatio = 0;
					boolean bRisk = false;
					String payType = "";
					if(rsCal.next())
					{
						bRisk = true;
						riskRatio = rsCal.getDouble("FRiskGoldScale");
						payType = rsCal.getString("FPayType");
					}
					dbl.closeResultSetFinal(rsCal);
					
					riskFeeSum = YssFun.roundIt(lastFeeSum  * riskRatio, 2);//应付风险金_上年余额=round(管理费总额_上年余额 * X ,  2)
					
					lastTermFee = lastFeeSum - riskFeeSum;//应付管理费_上年余额=管理费总额_上年余额 - 应付风险金_上年余额
					if(bRisk)
					{
						lastRiskFee = riskFeeSum;
						pst.setString(1,assetCode); 
						pst.setString(2, portName);
						pst.setInt(3, Integer.parseInt(this.feeYear));
						pst.setInt(4, 0);
						pst.setString(5, "07005");//风险准备金
						pst.setDouble(6, 0);
						pst.setDouble(7, 0);
						pst.setDouble(8, riskFeeSum);//只存余额
						pst.setString(9,rs.getString("FPayType"));//支付方式
						pst.setString(10,"0");
						pst.setString(11," ");
						pst.addBatch();
					}
					
					pst.setString(1,assetCode); 
					pst.setString(2, portName);
					pst.setInt(3, Integer.parseInt(this.feeYear));
					pst.setInt(4, 0);
					pst.setString(5, "07004");//应付管理费
					pst.setDouble(6, 0);
					pst.setDouble(7, 0);
					pst.setDouble(8, lastTermFee);//只存余额
					pst.setString(9,rs.getString("FPayType"));//计算管理费
					pst.setString(10,"0");
					pst.setString(11," ");
					pst.addBatch();
					
					
					for(int i=1;i<= Integer.parseInt(this.feeMonth);i++)//所有月份均重新计算
					{
						strSql = " select  sum(feeD) as feeD, sum(feeJ) as feeJ from " +
								 " (select v.fkmh,case when fjd = 'D' then fbal else 0 end as feeD,  " + 
								 " case when fjd = 'J' then fbal else 0 end as feeJ " +
								 " from " + tablePrefix + "fcwvch v join (select facctcode from " + tablePrefix + "laccount  where facctdetail = 1 and " +
								 " facctattr in ('应付管理人报酬','应付管理人报酬_管理费','应付管理人报酬_业绩报酬')) a  " +
								 " on v.fkmh = a.facctcode where  " +
								 " v.fterm = " + i + ") ";
						rsCal = dbl.openResultSet(strSql);
						
						if(rsCal.next())
						{
							feeD = rsCal.getDouble("feeD");
							feeJ = rsCal.getDouble("feeJ");
						}else
						{
							feeD = 0;
							feeJ = 0;
							
						}
						
						if(bRisk)
						{
							riskFeeD = YssFun.roundIt(feeD  * riskRatio, 2);
							riskFeeJ = YssFun.roundIt(feeJ  * riskRatio, 2);
							/*********应付风险金_上年余额=round(管理费总额_上年余额 * X ,  2)
							应付风险金_本年第N月计提=round(管理费总额_本年第N月计提 * X ,  2)
							应付风险金_本年第N月支付=round(管理费总额_本年第N月支付 * X ,  2)
							应付风险金_本年第N月应付余额=应付风险金_上年或第N-1月余额+应付风险金_本年第N月计提-应付风险金_本年第N月支付
							**********/
							pst.setString(1,assetCode); 
							pst.setString(2, portName);
							pst.setInt(3, Integer.parseInt(this.feeYear));
							pst.setInt(4, i);
							pst.setString(5, "07005");//风险准备金
							pst.setDouble(6, riskFeeD);
							pst.setDouble(7, riskFeeJ);
							pst.setDouble(8, lastRiskFee + riskFeeD - riskFeeJ);
							
							pst.setString(9,payType);//计算风险准备金
							pst.setString(10,"0");
							pst.setString(11," ");
							pst.addBatch();
							lastRiskFee = lastRiskFee + riskFeeD - riskFeeJ;//上月应付余额
						}
						
						/**应付管理费_上年余额=管理费总额_上年余额 - 应付风险金_上年余额
						应付管理费_本年第N月计提=管理费总额_本年第N月计提 - 应付风险金_本年第N月计提
						应付管理费_本年第N月支付=管理费总额_本年第N月支付 - 应付风险金_本年第N月支付
						应付管理费_本年第N月应付余额=应付管理费_上年或第N-1月余额 + 应付管理费_本年第N月计提 - 应付管理费_本年第N月支付
						*/
						pst.setString(1,assetCode); 
						pst.setString(2, portName);
						pst.setInt(3, Integer.parseInt(this.feeYear));
						pst.setInt(4, i);
						pst.setString(5, "07004");//应付管理费
						pst.setDouble(6, feeD - riskFeeD);
						pst.setDouble(7, feeJ - riskFeeJ);
						pst.setDouble(8, lastTermFee + feeD - riskFeeD - feeJ + riskFeeJ);
						
						pst.setString(9,rs.getString("FPayType"));//计算管理费
						pst.setString(10,"0");
						pst.setString(11," ");
						pst.addBatch();
						lastTermFee = lastTermFee + feeD - riskFeeD - feeJ + riskFeeJ;//更新上期余额
					}
					dbl.closeResultSetFinal(rsCal);
				}
				dbl.closeResultSetFinal(rs);
				
				/**start add by huangqirong 2013-7-8 Bug #8555 重置变量值为0  */
				riskFeeD = 0;//重置
				riskFeeJ = 0;//重置
				/**end add by huangqirong 2013-7-8 Bug #8555 重置变量值为0*/
				
				/** 托管费总额 本月计提：“应付托管费”科目当月累计贷方发生额；
					托管费总额本月支付：“应付托管费”科目当月累计借方发生额；  story3736 add by yeshenghong 20130321
					托管给总额本月余额：“应付托管费”科目当月余额。  */
				strSql = " select * from TB_" + curGroup + "_REP_PayFeeParaConfigure where fportcode = " + dbl.sqlString(curPort) + 
				 " and FFeeType  = '07012' and fcheckstate = 1" ;//计算托管费
				rs = dbl.openResultSet(strSql);
				if(rs.next())
				{
					strSql =  " select sum(-1 * b.fendbal) as fstartbal from " + tablePrefix + "lbalance b join " + tablePrefix + "laccount a on b.facctcode = a.facctcode " +
								" where facctdetail = 1 and facctattr = '应付托管费'  and  fmonth = 0";
					rsCal = dbl.openResultSet(strSql);
					if(rsCal.next())
					{
						lastFeeSum = rsCal.getDouble("fstartbal");
					}else
					{
						lastFeeSum = 0;
					}
					dbl.closeResultSetFinal(rsCal);
					
					strSql = " select * from TB_" + curGroup + "_REP_PayFeeParaConfigure where fportcode = " + dbl.sqlString(curPort) + 
					 " and FFeeType  = '07032' and fcheckstate = 1" ;//计算托管风险准备金
					rsCal = dbl.openResultSet(strSql);
					double riskRatio = 0;
					boolean bRisk = false;
					String payType = "";
					if(rsCal.next())
					{
						bRisk = true;
						riskRatio = rsCal.getDouble("FRiskGoldScale");
						payType = rsCal.getString("FPayType");
					}
					dbl.closeResultSetFinal(rsCal);
					
					riskFeeSum = YssFun.roundIt(lastFeeSum  * riskRatio, 2);//应付托管风险金_上年余额=round(托管费总额_上年余额 * X ,  2)
					
					lastTermFee = lastFeeSum - riskFeeSum;//应付托管费_上年余额 = 托管费总额_上年余额 - 应付托管风险金_上年余额
					if(bRisk)
					{
						lastRiskFee = riskFeeSum;
						pst.setString(1,assetCode); 
						pst.setString(2, portName);
						pst.setInt(3, Integer.parseInt(this.feeYear));
						pst.setInt(4, 0);
						pst.setString(5, "07032");//应付托管风险金
						pst.setDouble(6, 0);
						pst.setDouble(7, 0);
						pst.setDouble(8, riskFeeSum);//只存余额
						pst.setString(9,rs.getString("FPayType"));//支付方式
						pst.setString(10,"0");
						pst.setString(11," ");
						pst.addBatch();
					}
					
					pst.setString(1,assetCode); 
					pst.setString(2, portName);
					pst.setInt(3, Integer.parseInt(this.feeYear));
					pst.setInt(4, 0);
					pst.setString(5, "07012");//应付托管费
					pst.setDouble(6, 0);
					pst.setDouble(7, 0);
					pst.setDouble(8, lastTermFee);//只存余额
					pst.setString(9,rs.getString("FPayType"));//计算托管费
					pst.setString(10,"0");
					pst.setString(11," ");
					pst.addBatch();
					
					
					for(int i=1;i<= Integer.parseInt(this.feeMonth);i++)//所有月份均重新计算
					{
						strSql = " select  sum(feeD) as feeD, sum(feeJ) as feeJ from " +
								 " (select v.fkmh,case when fjd = 'D' then fbal else 0 end as feeD,  " + 
								 " case when fjd = 'J' then fbal else 0 end as feeJ " +
								 " from " + tablePrefix + "fcwvch v join (select facctcode from " + tablePrefix + "laccount  where facctdetail = 1 and " +
								 " facctattr = '应付托管费' ) a  " +
								 " on v.fkmh = a.facctcode where  " +
								 " v.fterm = " + i + ") ";
						rsCal = dbl.openResultSet(strSql);
						
						if(rsCal.next())
						{
							feeD = rsCal.getDouble("feeD");
							feeJ = rsCal.getDouble("feeJ");
						}else
						{
							feeD = 0;
							feeJ = 0;
							
						}
						
						if(bRisk)
						{
							riskFeeD = YssFun.roundIt(feeD  * riskRatio, 2);
							riskFeeJ = YssFun.roundIt(feeJ  * riskRatio, 2);
							/*********应付风险金_上年余额=round(托管费总额_上年余额 * X ,  2)
							应付风险金_本年第N月计提=round(托管费总额_本年第N月计提 * X ,  2)
							应付风险金_本年第N月支付=round(托管费总额_本年第N月支付 * X ,  2)
							应付风险金_本年第N月应付余额=应付风险金_上年或第N-1月余额+应付风险金_本年第N月计提-应付风险金_本年第N月支付
							**********/
							pst.setString(1,assetCode); 
							pst.setString(2, portName);
							pst.setInt(3, Integer.parseInt(this.feeYear));
							pst.setInt(4, i);
							pst.setString(5, "07032");//应付托管风险金
							pst.setDouble(6, riskFeeD);
							pst.setDouble(7, riskFeeJ);
							pst.setDouble(8, lastRiskFee + riskFeeD - riskFeeJ);
							
							pst.setString(9,payType);//计算风险准备金
							pst.setString(10,"0");
							pst.setString(11," ");
							pst.addBatch();
							lastRiskFee = lastRiskFee + riskFeeD - riskFeeJ;//上月应付余额
						}
						
						/**应付托管费_上年余额=托管费总额_上年余额 - 应付托管风险金_上年余额
						应付托管费_本年第N月计提=托管费总额_本年第N月计提 - 应付托管风险金_本年第N月计提
						应付托管费_本年第N月支付=托管费总额_本年第N月支付 - 应付托管风险金_本年第N月支付
						应付托管费_本年第N月应付余额=应付托管费_上年或第N-1月余额 + 应付托管费_本年第N月计提 - 应付托管费_本年第N月支付
						*/
						pst.setString(1,assetCode); 
						pst.setString(2, portName);
						pst.setInt(3, Integer.parseInt(this.feeYear));  //story3736 add by yeshenghong 20130321
						pst.setInt(4, i);
						pst.setString(5, "07012");//应付托管费
						pst.setDouble(6, feeD - riskFeeD);
						pst.setDouble(7, feeJ - riskFeeJ);
						pst.setDouble(8, lastTermFee + feeD - riskFeeD - feeJ + riskFeeJ);
						
						pst.setString(9,rs.getString("FPayType"));//计算托管费
						pst.setString(10,"0");
						pst.setString(11," ");
						pst.addBatch();
						lastTermFee = lastTermFee + feeD - riskFeeD - feeJ + riskFeeJ;//更新上期余额
					}
					dbl.closeResultSetFinal(rsCal);
				}
				dbl.closeResultSetFinal(rs);
		
//				strSql = " select * from TB_" + curGroup + "_REP_PayFeeParaConfigure where fportcode = " + dbl.sqlString(curPort) + 
//				 " and fcheckstate = 1 and FFeeType  = '07012' ";//"07012",// 应付托管费  
//				rs = dbl.openResultSet(strSql);
//				if(rs.next())
//				{
//					strSql =  " select sum(-1 * b.fendbal) as fstartbal from " + tablePrefix + "lbalance b join " + tablePrefix + "laccount a on b.facctcode = a.facctcode where  facctdetail = 1  and  fmonth = 0 and facctattr = '应付托管费' ";
//					rsCal = dbl.openResultSet(strSql);
//					if(rsCal.next())
//					{
//						lastFeeSum = rsCal.getDouble("fstartbal");//上年余额
//					}else
//					{
//						lastFeeSum = 0;
//					}
//					dbl.closeResultSetFinal(rsCal);
//					lastTermFee = lastFeeSum;
//					pst.setString(1,assetCode); 
//					pst.setString(2, portName);
//					pst.setInt(3, Integer.parseInt(this.feeYear));
//					pst.setInt(4, 0);
//					pst.setString(5, "07012");//"07012",// 应付托管费   "07013",//应付销售服务费
//					pst.setDouble(6, 0);
//					pst.setDouble(7, 0);
//					pst.setDouble(8, lastTermFee);//只存余额
//					pst.setString(9,rs.getString("FPayType"));//支付方式
//					pst.setString(10,"0");
//					pst.setString(11," ");
//					pst.addBatch();
//					for(int i=1;i<= Integer.parseInt(this.feeMonth);i++)//所有月份均重新计算
//					{
//						strSql = " select  sum(feeD) as feeD, sum(feeJ) as feeJ from " +
//						 " (select v.fkmh,case when fjd = 'D' then fbal else 0 end as feeD,  " + 
//						 " case when fjd = 'J' then fbal else 0 end as feeJ " +
//						 " from " + tablePrefix + "fcwvch v join (select facctcode from " + tablePrefix + "laccount  where facctdetail = 1 and facctattr = '应付托管费' ) a  " +
//						 " on v.fkmh = a.facctcode where  " +
//						 " v.fterm = "+ i + ") ";
//						rsCal = dbl.openResultSet(strSql);
//						if(rsCal.next())
//						{
//							feeD = rsCal.getDouble("feeD");
//							feeJ = rsCal.getDouble("feeJ");
//						}else
//						{
//							feeD = 0;
//							feeJ = 0;
//							
//						}
//					
//						pst.setString(1,assetCode); 
//						pst.setString(2, portName);
//						pst.setInt(3, Integer.parseInt(this.feeYear));
//						pst.setInt(4, i);
//						pst.setString(5, "07013");////"07012",// 应付托管费   "07013",//应付销售服务费
//						pst.setDouble(6, feeD);
//						pst.setDouble(7, feeJ);
//						pst.setDouble(8, lastTermFee + feeD - feeJ);
//						
//						pst.setString(9,rs.getString("FPayType"));//支付方式
//						pst.setString(10,"0");
//						pst.setString(11," ");
//						pst.addBatch();
//						lastTermFee = lastTermFee + feeD - feeJ;//更新上期余额
//						
//						dbl.closeResultSetFinal(rsCal);
//					}
//				}
//				dbl.closeResultSetFinal(rs);
				
				/*本月计提：“应付销售费”科目当月累计贷方发生额；
				本月支付：“应付销售费”科目当月累计借方发生额；//story3736 add by yeshenghong 20130321
				本月余额：“应付销售费”科目当月余额。*/
				strSql = " select * from TB_" + curGroup + "_REP_PayFeeParaConfigure where fportcode = " + dbl.sqlString(curPort) + 
				 " and fcheckstate = 1 and FFeeType  = '07013' ";// "07013"  应付销售服务费
				rs = dbl.openResultSet(strSql);
				if(rs.next())
				{
					strSql =  " select sum(-1 * b.fendbal) as fstartbal from " + tablePrefix + "lbalance b join " + tablePrefix + "laccount a on b.facctcode = a.facctcode where  facctdetail = 1  and  fmonth = 0 and facctattr = '应付销售费' ";
					rsCal = dbl.openResultSet(strSql);
					if(rsCal.next())
					{
						lastFeeSum = rsCal.getDouble("fstartbal");//上年余额
					}else
					{
						lastFeeSum = 0;
					}
					dbl.closeResultSetFinal(rsCal);
					lastTermFee = lastFeeSum;
					pst.setString(1,assetCode); 
					pst.setString(2, portName);
					pst.setInt(3, Integer.parseInt(this.feeYear));
					pst.setInt(4, 0);
					pst.setString(5, "07013");//"07012",// 应付托管费   "07013",//应付销售服务费
					pst.setDouble(6, 0);
					pst.setDouble(7, 0);
					pst.setDouble(8, lastTermFee);//只存余额
					pst.setString(9,rs.getString("FPayType"));//支付方式
					pst.setString(10,"0");
					pst.setString(11," ");
					pst.addBatch();
					for(int i=1;i<= Integer.parseInt(this.feeMonth);i++)//所有月份均重新计算
					{
						strSql = " select  sum(feeD) as feeD, sum(feeJ) as feeJ from " +
						 " (select v.fkmh,case when fjd = 'D' then fbal else 0 end as feeD,  " + 
						 " case when fjd = 'J' then fbal else 0 end as feeJ " +
						 " from " + tablePrefix + "fcwvch v join (select facctcode from " + tablePrefix + "laccount  where facctdetail = 1 and facctattr = '应付销售费' ) a  " +
						 " on v.fkmh = a.facctcode where  " +
						 " v.fterm = "+ i + ") ";
						rsCal = dbl.openResultSet(strSql);
						if(rsCal.next())
						{
							feeD = rsCal.getDouble("feeD");
							feeJ = rsCal.getDouble("feeJ");
						}else
						{
							feeD = 0;
							feeJ = 0;
							
						}
					
						pst.setString(1,assetCode); 
						pst.setString(2, portName);
						pst.setInt(3, Integer.parseInt(this.feeYear));
						pst.setInt(4, i);
						pst.setString(5, "07013");////"07012",// 应付托管费   "07013",//应付销售服务费
						pst.setDouble(6, feeD);
						pst.setDouble(7, feeJ);
						pst.setDouble(8, lastTermFee + feeD - feeJ);
						
						pst.setString(9,rs.getString("FPayType"));//支付方式
						pst.setString(10,"0");
						pst.setString(11," ");
						pst.addBatch();
						lastTermFee = lastTermFee + feeD - feeJ;//更新上期余额
						
						dbl.closeResultSetFinal(rsCal);
					}
				}
				dbl.closeResultSetFinal(rs);
				
				//计算席位佣金
				
				strSql = " select * from TB_" + curGroup + "_REP_PayFeeParaConfigure where fportcode = " + dbl.sqlString(curPort) + 
				 " and FFeeType = '07019' and fcheckstate = 1";//"07019" 应付席位佣金
				rs = dbl.openResultSet(strSql);
				if(rs.next())
				{
					strSql = "select facctcode, case when facctattrid = 'H' then substr(facctcode,length(facctcode)-4) else  " + 
							" substr(facctcode,length(facctcode)-5) end as fseatcode from " + tablePrefix + "laccount where facctdetail = 1 and facctattr = '应付佣金_席位使用费' ";
					rsInner = dbl.openResultSet(strSql);
					while(rsInner.next())
					{
						strSql = " select sum(-1 * b.fendbal) as fstartbal from " + tablePrefix + "lbalance b where fmonth = 0 and b.facctcode = " + rsInner.getString("facctcode");
	//						" join (select case when facctattrid = 'H' then substr(facctcode,length(facctcode)-4) else  " + 
	//						" substr(facctcode,length(facctcode)-5) end as fseatcode, facctcode from " + tablePrefix + "laccount " + 
	//						" where facctdetail = 1 and facctattr = '应付佣金_席位使用费') a  " + 
	//						" on b.facctcode = a.facctcode group by a.fseatcode ";	 
						rsCal = dbl.openResultSet(strSql);
						if(rsCal.next())
						{
							lastFeeSum = rsCal.getDouble("fstartbal");//上年余额
						}else
						{
							lastFeeSum = 0;
						}
						lastTermFee = lastFeeSum;
						pst.setString(1,assetCode); 
						pst.setString(2, portName);
						pst.setInt(3, Integer.parseInt(this.feeYear));
						pst.setInt(4, 0);
						pst.setString(5, "07019");//"07012",// 应付托管费   "07013",//应付销售服务费
						pst.setDouble(6, 0);
						pst.setDouble(7, 0);
						pst.setDouble(8, lastTermFee);//只存余额
						pst.setString(9,rs.getString("FPayType"));//支付方式
						pst.setString(10,"0");
						pst.setString(11,rsInner.getString("fseatcode"));
						pst.addBatch();
						dbl.closeResultSetFinal(rsCal);
					
					
						for(int i=1;i<= Integer.parseInt(this.feeMonth);i++)//所有月份均重新计算
						{
							strSql = " select sum(feeD) as feeD, sum(feeJ) as feeJ from " +
									" (select case when fjd = 'D' then fbal else 0 end as feeD, " +
									"  case when fjd = 'J' then fbal else 0 end as feeJ " +
									" from " + tablePrefix + "fcwvch v  where v.fkmh =  " + rsInner.getString("facctcode") +
									" and v.fterm = " + i + ") ";
							rsCal = dbl.openResultSet(strSql);
							if(rsCal.next())
							{
								feeD = rsCal.getDouble("feeD");
								feeJ = rsCal.getDouble("feeJ");
							}else
							{
								feeD = 0;
								feeJ = 0;
							}
							
							pst.setString(1,assetCode); 
							pst.setString(2, portName);
							pst.setInt(3, Integer.parseInt(this.feeYear));
							pst.setInt(4, i);
							pst.setString(5, "07019");////"07012",// 应付托管费   "07013",//应付销售服务费
							pst.setDouble(6, feeD);
							pst.setDouble(7, feeJ);
							pst.setDouble(8, lastTermFee + feeD - feeJ);
							
							pst.setString(9,rs.getString("FPayType"));//支付方式
							pst.setString(10,"0");
							pst.setString(11, rsInner.getString("fseatcode"));
							pst.addBatch();
							lastTermFee = lastTermFee + feeD - feeJ;//更新上期余额
							dbl.closeResultSetFinal(rsCal);
						}
					}
					dbl.closeResultSetFinal(rsInner);
				}
				dbl.closeResultSetFinal(rs);
				pst.executeBatch();
			}
			
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
		} catch (Exception e) {
			throw new YssException(e);
		}
		finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
			dbl.endTransFinal(bTrans);
		}
		return this.searchPayableFeeTable();
     }

	// 20130218 added by yeshenghong .Story #3489
	// 查询报表数据
	private String searchPayableFeeTable() throws YssException {
		StringBuffer buff = new StringBuffer();
		String strSql = "";
		ResultSet rs = null;
		String curGroup = "";
		String curPort =  "";
		try {
			String[] portCodes  = this.portCode.split(",");
			for(int h=0;h<portCodes.length;h++)
			{
				curGroup = portCodes[h].split("-")[0];
				curPort =  portCodes[h].split("-")[1];

				strSql = " select distinct feeflag from tb_nh_payfeedata t "
						+ " join tb_" + curGroup + "_para_portfolio"
						+ " p on t.assertcode = p.fassetcode "
						+ " where p.fportcode = " + dbl.sqlString(curPort)
						+ " and t.feeyear = " + this.feeYear;
				rs = dbl.openResultSet(strSql);
				if (rs.next()) {
					buff.append(rs.getString("feeflag"));
				}else
				{
					dbl.closeResultSetFinal(rs);//没有数据则跳过  yeshenghong 3715 20130315
					continue;
				}
				dbl.closeResultSetFinal(rs);
				buff.append(" ").append("\r\t");
	
				strSql = " select fassetcode , fportname as fassetname from "
						+ " tb_" + curGroup + "_para_portfolio"
						+ " where fportcode = " + dbl.sqlString(curPort);
				rs = dbl.openResultSet(strSql);
				if (rs.next()) {
					buff.append(rs.getString("fassetcode")).append("\t");
					buff.append(rs.getString("fassetname")).append("\t");
				}
				dbl.closeResultSetFinal(rs);
				buff.append(" ").append("\r\t");
				strSql = " select case when feemonth = 0 then feetype else '' end as feetype, "
						+ " case when feemonth = 0 then feeby else '' end as feeby, "
						+ " case when feemonth = 0 then fseatname else '' end as fseatname, "
						+ " case when feemonth = 0 then '上年余额' when feemonth = 99 then '小计' else feeyear || '年' || feemonth || '月' end as ftime,  "
						+ " feed,feej,feeendbal, "
						+ " case when feemonth = 0 then feename else '' end as feename, "
						+ " case when feemonth = 0 then feepaytypename else '' end as feepaytypename, "
						+ " case when feemonth = 0 then feepaytype else '' end as feepaytype  "
						+ " from (  "
						+ " select t.feetype, t.feeby,s.fseatname, t.feeyear, "
						+ " t.feed,t.feej,to_char(t.feeendbal) as feeendbal, "
						+ " t.feepaytype, t.feemonth, t.feetype ||t.feeby || t.feemonth  as fordercode,v1.fvocname as feename, v2.fvocname as feepaytypename"
						+ " from "
						+ " TB_NH_PAYFEEDATA "
						+ " t  "
						+ " join (select fassetcode from "
						+ " tb_" + curGroup + "_para_portfolio"
						+ " where fportcode = "
						+ dbl.sqlString(curPort)
						+ ")p on t.assertcode = p.fassetcode "
						+ " join (select fvoccode,fvocname from  tb_fun_vocabulary where fvoctypecode = 'Voc_PayFeeType') v1 on t.feetype = v1.fvoccode "
						+ " join (select fvoccode,fvocname from  tb_fun_vocabulary where fvoctypecode = 'Voc_PayType') v2 on t.feepaytype = v2.fvoccode "
						+ " left join "
						+ " tb_" + curGroup + "_para_tradeseat"
						+ " s on t.feeby = s.fseatcode "
						+ " where  t.feeyear = " + this.feeYear
						+ " union  "
						+ " select t.feetype, t.feeby,' ' fseatname, t.feeyear, sum(t.feed) as feed,sum(t.feej) as feej, "
						+ "       ''  feeendbal,' ' feepaytype, 99 as feemonth, t.feetype || t.feeby || '99' as fordercode,'' as feename,'' as feepaytypename "
						+ " from TB_NH_PAYFEEDATA"
						+ " t  "
						+ " join (select fassetcode from "
						+ " tb_" + curGroup + "_para_portfolio"
						+ " where fportcode = "
						+ dbl.sqlString(curPort)
						+ ")p on t.assertcode = p.fassetcode "
						+ " where  t.feeyear = "
						+ this.feeYear
						+ "  group by feetype,t.feeyear, feeby"
						+ " ) order by fordercode ";
	
				rs = dbl.openResultSet(strSql);
	
				while (rs.next()) {
					buff.append(
							rs.getString("feename") == null ? " " : rs
									.getString("feename")).append("\t");
					buff.append(
							rs.getString("feeby") == null ? " " : rs
									.getString("feeby")).append("\t");
					buff.append(
							rs.getString("fseatname") == null ? " " : rs
									.getString("fseatname")).append("\t");
					buff.append(
							rs.getString("ftime") == null ? " " : rs
									.getString("ftime")).append("\t");
					buff.append(
							YssFun.formatNumber(rs.getDouble("feed"), "#,##0.00"))
							.append("\t");
					buff.append(
							YssFun.formatNumber(rs.getDouble("feej"), "#,##0.00"))
							.append("\t");
					buff.append(
							YssFun.formatNumber(rs.getDouble("feeendbal"),
									"#,##0.00")).append("\t");
					buff.append(
							rs.getString("feepaytypename") == null ? " " : rs
									.getString("feepaytypename")).append("\t");
					buff.append("\r\n");
				}
//				buff.delete(buff.length()-2, buff.length()-2);
//				sAllDataStr = buff.toString().substring(0,
//						buff.toString().length() - 2);
				buff.append("\r\f");//modified by yeshenghong story3715
			}

		} catch (Exception ye) {
			throw new YssException("查询应付费用一览表出错：" + ye.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}

		return buff.toString();
	}

	/**
	 *确认费用表
	 *20130218 added by yeshenghong .Story #3489
	 */
	private String confirmPayableFeeTable() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		String[] sRowData;
		String[] sColData;
		String feeCode = "";
		PreparedStatement pst = null;
		String monthCode = "";
		String seatCode = " ";
		boolean bTrans = false;
		Connection conn = null;
		String[] portData;
		String curPort = "";
		String assetCodes = "";
		String bReturn = "";
		try {
			portData = this.updateData.split("\f\f");
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			strSql = " update TB_NH_PAYFEEDATA "
				+ " set feeflag = ?, feed = ?, feej = ?, "
				+ " feeendbal = ? where assertcode = ? "
				+ " and feeyear = "
				+ this.feeYear
				+ " and feetype = (select distinct fvoccode from "
				+ " tb_fun_vocabulary where fvoctypecode = 'Voc_PayFeeType' and fvocname = ? )  and feemonth = ? and feeby = ? ";
			pst = dbl.openPreparedStatement(strSql);
			for(int h=0;h<portData.length;h++)
			{
				sRowData = portData[h].split("\b\b");
				if(sRowData.length<=0)
				{
					continue;
				}
				curPort = sRowData[0];
				assetCodes = assetCodes + dbl.sqlString(curPort) + ",";
				for (int i = 1; i < sRowData.length; i++) {
					sColData = sRowData[i].split("\t");
					if (sColData.length < 8) {
						continue;
					}
					if (sColData[0] != null && !sColData[0].trim().equals("")) {
						feeCode = sColData[0];// 注意 要通过词汇获取
					}
					if (sColData[1] != null && !sColData[1].trim().equals("")) {
						seatCode = sColData[1];// 注意 要通过词汇获取
					} 
	//				else {
	//					seatCode = " ";
	//				}
					if (sColData[3].equals("上年余额")) {
						monthCode = "0";
					} else if (sColData[3].equals("小计")) {
						continue;
					} else {
						monthCode = sColData[3].substring(
								sColData[3].indexOf("年") + 1, sColData[3]
										.indexOf("月"));
					}
	
					pst.setInt(1, 1);
					pst.setDouble(2, Double.parseDouble(sColData[4]));
					pst.setDouble(3, Double.parseDouble(sColData[5]));
					pst.setDouble(4, Double.parseDouble(sColData[6]));
					pst.setString(5, curPort);
					// pst.setInt(6,Integer.parseInt(this.feeYear));
					pst.setString(6, feeCode);
					pst.setInt(7, Integer.parseInt(monthCode));
					pst.setString(8, seatCode);
					pst.addBatch();
				}
			}
			if(assetCodes.length()>1)//modified by yeshenghong story3715 20130318
			{
				assetCodes = assetCodes.substring(0, assetCodes.length()-1);
				pst.executeBatch();
				conn.commit();
				bTrans = false;
			}
			conn.setAutoCommit(true);
			if(assetCodes.length()>1)//modified by yeshenghong story3715 20130318
			{
				bReturn = parserToXml(assetCodes);
			}
		} catch (Exception ye) {
			throw new YssException("确认资产一览表出错：" + ye.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst); 
			dbl.endTransFinal(conn, bTrans);
		}

		return  bReturn + "\b\b" + this.searchPayableFeeTable();//add by yeshenghong 20130410 story3795
	}
	
	 /**
	  * 将新增报表中的数据转换为xml格式
	  * 20130218 added by yeshenghong .Story #3489
	  * @return 
	  * @return
	  * @throws YssException
	  */
	 public String parserToXml(String assetCodes) throws YssException{
			YssFinance finance = new YssFinance();
			finance.setYssPub(pub);
			//String fsetCode = finance.getBookSetId(this.portCode);
		    String sb = selReportInfo(assetCodes);
			String sData [] = sb.split("\r\n");
			Document document;
			Element root;
			String sbufferXML = "";
			try {  
				document = DocumentHelper.createDocument();  
				root=document.addElement("cps");
				
				/**start modify by huangqirong 2013-7-8 Bug #8555 VAL_10 -> 1  */
				root.addAttribute("yssdbflag", "1");//给cps添加赢时胜数据库标识属性    QDII 系统固定标识
				/**end modify by huangqirong 2013-7-8 Bug #8555 VAL_10 -> 1*/
			    
			    root.addAttribute("count", sData.length+"");//给cps添加指令内数据条数属性
				if(root!=null){
					Element feeinfointerface=root.addElement("feeinfointerface");//
					feeinfointerface.addAttribute("czlx","0");  //给feeinfointerface添加操作类型属性
					for(int v=0;v<sData.length;v++){
						String xData [] = sData[v].split("\t");
						Element row=feeinfointerface.addElement("row");//
						row.addAttribute("id", v+"");
						Element assetcode=row.addElement("assetcode");//资产代码
						Element feetype=row.addElement("feetype");//费用类型代码
						Element seatcode=row.addElement("seatcode");//席位代码
						Element payterm=row.addElement("payterm");//支付周期
						Element currMonthincur=row.addElement("currMonthincur");//本月计提
						Element currMonthsett=row.addElement("currMonthsett");//本月支付
						Element currbal=row.addElement("currbal");//应付余额
						Element paytype=row.addElement("paytype");//支付方式
						assetcode.setText(xData[0]);//资产代码
						feetype.setText(xData[4]);//modified by yeshenghong 3736  原来的代码是北京传过来的代码    有严重问题  现予以修改
						seatcode.setText(xData[10].trim());//席位代码
						
//						if(xData[4].equalsIgnoreCase("托管费")){
//							feetype.setText("07012");//费用类型代码
//							seatcode.setText("");//席位代码
//						}else if(xData[4].equalsIgnoreCase("管理费")){
//							assetcode.setText(xData[0]);//资产代码
//							feetype.setText("07004");//费用类型代码
//							seatcode.setText("");//席位代码
//						}else if(xData[4].equalsIgnoreCase("风险金")){
//							assetcode.setText(xData[0]);//资产代码
//							feetype.setText("07005");
//							seatcode.setText("");//席位代码
//						}else 
//						if(xData[4].equalsIgnoreCase("07019")){
//							assetcode.setText(xData[0]);//资产代码
//							feetype.setText(xData[4]);
//							seatcode.setText(xData[10].trim());//席位代码
//						}else 
//						{
//							assetcode.setText(xData[0]);//资产代码
//							feetype.setText(xData[4]);//费用类型代码
//							seatcode.setText("");//席位代码
//						}
//							
//							if(xData[4].equalsIgnoreCase("销售服务费")){
//							assetcode.setText(xData[0]);//资产代码
//							feetype.setText("07013");
//							seatcode.setText("");//席位代码
//						}else if(xData[4].equalsIgnoreCase("信息披露费")){
//							assetcode.setText(xData[0]);//资产代码
//							feetype.setText("07014");
//							seatcode.setText("");//席位代码
//						}
						payterm.setText(xData[3]);//支付周期
						currMonthincur.setText(xData[5]);//本月计提
						currMonthsett.setText(xData[6]);//本月支付
						currbal.setText(xData[7]);//应付余额
						paytype.setText(xData[8]);//支付方式
					}
				}
				sbufferXML = document.asXML().toString();
				sbufferXML = sbufferXML.substring(sbufferXML.indexOf("?>")+2,sbufferXML.length());
				IYSSToCPS cpsInter= CPS2YSSExporter.getCPS2YSSExporterInterface();
				//费用接口调用
				String backFlag = cpsInter.exp2cpsFeeInfo(sbufferXML);
				String bkCode = getCPSMsg(backFlag)[0];
				return bkCode;//modified by  yeshenghong 20130411 story3837
				
		      } catch (Exception ex) { 
		    	  throw new YssException("生成费用统计表XML文件出错！",ex);
			 }   
			 //return sbufferXML.toString();
		}
	 
	 /**
	 *解析CPS接口返回值
	 *add by yeshenghong story3795  20130410
	 */
	private String[] getCPSMsg(String cpsMsg) {
		StringReader sr = new StringReader(cpsMsg);
		SAXReader reader = new SAXReader();
		Document document = null;
		List<Element> es = null;
		String[] arr = new String[2];
		try {
			document = reader.read(sr);
			Element root = document.getRootElement();
			es = ((Element) root.elements("fkxx").get(0)).elements();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			sr.close();
		}
		int i = 0;
		for (Element e : es) {
			arr[i] = e.getText();
			i++;
		}
		return arr;
	}

	 /**
	 *获取报表数据
	 *add by yeshenghong story3795  20130410
	 */
	 public String selReportInfo(String assetCodes) throws YssException {
		 StringBuffer buffer = new StringBuffer();
		 String fyear = this.feeYear;
		 String fmonth = this.feeMonth;
		 ResultSet rs = null;
		 String sql = "";
		 try{
			 sql = " select ASSERTCODE,ASSERTNAME,FEEYEAR,feeyear || trim(to_char(feemonth,'00')) FEEMONTH,FEETYPE,FEED,FEEJ,FEEENDBAL,c.fvocname as FEEPAYTYPE,FEEFLAG,FEEBY  from " +
			 		" TB_NH_PAYFEEDATA t " +//新需求要求将年份和月份一起带上  yeshenghong 20130314 story3715
			 		" join (select fvoccode,fvocname from tb_fun_vocabulary where fvoctypecode = 'Voc_PayType') c on t.FEEPAYTYPE = c.fvoccode " +
			 		" where t.ASSERTCODE in (" + assetCodes +
			 		" ) and t.FEEYEAR = "+YssFun.toInt(fyear)+" and t.FEEMONTH = "+YssFun.toInt(fmonth)+ " order by ASSERTCODE ";
			 rs = dbl.openResultSet(sql);
			 while(rs.next()){
				 buffer.append(rs.getString("ASSERTCODE")).append("\t");
				 buffer.append(rs.getString("ASSERTNAME")).append("\t");
				 buffer.append(rs.getInt("FEEYEAR")).append("\t");
				 buffer.append(rs.getInt("FEEMONTH")).append("\t");
				 buffer.append(rs.getString("FEETYPE")).append("\t");
				 buffer.append(rs.getDouble("FEED")).append("\t");
				 buffer.append(rs.getDouble("FEEJ")).append("\t");
				 buffer.append(rs.getDouble("FEEENDBAL")).append("\t");
				 buffer.append(rs.getString("FEEPAYTYPE")).append("\t");
				 buffer.append(rs.getString("FEEFLAG")).append("\t");
				 buffer.append(rs.getString("FEEBY")).append("\r\n");
			 }
		 }catch (Exception se) {
             throw new YssException("获取统计报表信息出错！", se); //注意这里抛出异常的方式
         }finally{
        	 dbl.closeResultSetFinal(rs);
         }
		 return buffer.toString();
	 }

}
