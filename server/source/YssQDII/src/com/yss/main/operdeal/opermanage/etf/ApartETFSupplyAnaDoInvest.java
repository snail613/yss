package com.yss.main.operdeal.opermanage.etf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;

import com.yss.commeach.EachRateOper;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.operdeal.businesswork.SellTradeRelaCal;
import com.yss.main.operdeal.opermanage.BaseOperManage;
import com.yss.main.operdeal.stgstat.StgSecurity;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * 此类根据台帐表中的补票数量和交易数据中的数量，区分是主动投资还是补票，并把数据查分到ETF交易数据子表（Tb_XXX_ETF_SubTrade）
 * @author xuqiji 20091107 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
 *
 */
public class ApartETFSupplyAnaDoInvest extends BaseOperManage {
	
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
//			apartETFSupplyInverst();
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	/**
	 * ，区分是主动投资还是补票，并把数据查分到ETF交易数据子表（Tb_XXX_ETF_SubTrade）
	 * @throws YssException 
	 *
	 */
	public void apartETFSupplyInverst() throws YssException {
		StringBuffer buff = null;//做拼接SQL语句
		ResultSet rs = null;//结果皆
		boolean bTrans = true;//事物控制标识
		Connection conn = null;//数据库连接
		HashMap bookData = null;//保存台账数据的hash表
		String sKey ="";
		double tradeTotalAmount = 0;//交易总数量
		PreparedStatement pst = null;//预处理
		double baseRate = 1;//基础汇率
		double portRate = 1;//组合汇率
		long sNum=0;//为了产生的编号不重复
		String strNumDate = ""; //保存交易编号
		String [] sSupplyAmout = null;//保存当天每支股票总的补票数量和已经补的数量
		HashMap etfParam = new HashMap();//保存参数设置的hash表
		ETFParamSetAdmin param = null;//参数设置操作类
		String oneGradeMktCode = "";//一级市场代码
		ETFParamSetBean paramSet = null;//参数设置实体bean
		HashMap booklist = new HashMap();//获取估值当天组合代码对应的一级市场代码区分买卖标志的篮子数
		double basketOfSG = 0;////申购的篮子数
		double basketOfSH = 0;//赎回的篮子数
		double dPassiveScale = 1;//被动补票比例
		StgSecurity securityCost = null;//证券库存声明
		SellTradeRelaCal tradeRealCal = null;//计算卖出交易中的关联数据类的声明
		double activeInverstAmount = 0;//主动投资数量
		try{
			param = new ETFParamSetAdmin();//实例化
			param.setYssPub(pub);
			
			etfParam = param.getETFParamInfo(this.sPortCode);//获取保存参数设置实体bean的hash表
			
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//设置为手动提交事物
			buff = new StringBuffer(1000);
			bookData = new HashMap();//实例化
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_ETF_SubTrade"));//给表加锁
			
			//------add by songjie 2009.11.18 V4.1_ETF:MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A-------//
			buff.append(" select mx.*, mxgl.FSupplyAmount from (select FsecurityCode, ")
			.append(" FPortCode, sum((case when FBS = 'B' then 1 else -1 end) * FReplaceAmount) as FTotalSupplyAmount from ")
			.append(pub.yssGetTableName("Tb_Etf_Tradestldtl"))//交易结算明细表
			.append(" where FSecurityCode <> ' ' and FBuyDate = ").append(dbl.sqlDate(this.dDate))
			.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode))
			.append(" group by FPortCode, FSecurityCode) mx ")
			.append(" left join (select sum(trade.Fdatadirection * trade.Fmakeupamount) as FSupplyAmount, ")
			.append(" traderef.Fsecuritycode as FSecurityCode,traderef.FPortCode as FPortCode, ")
			.append(" traderef.fbuydate as FBuyDate from ")
			.append(pub.yssGetTableName("Tb_Etf_Tradstldtlref"))//明细关联表
			.append(" trade left join ").append(pub.yssGetTableName("Tb_Etf_Tradestldtl"))//交易结算明细表
			.append(" traderef on trade.Fnum = traderef.Fnum ")
			.append(" where FSecurityCode <> ' ' and FBuyDate = ")
			.append(dbl.sqlDate(this.dDate)).append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode))
			.append(" and trade.FDatamark <> '1' group by FPortCode, FSecurityCode, FBuyDate) ")
			.append(" mxgl on mx.FSecurityCode = mxgl.FSecurityCode and mx.FPortCode = mxgl.FPortCode");
			//------add by songjie 2009.11.18 V4.1_ETF:MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A-------//
			
			//------delete by songjie 2009.11.18 V4.1_ETF:MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A------//
//			buff.append(" select sum(book.FMakeUpAmount) as FTotalSupplyAmount,sum(book.FMakeUpAmount1) as FSupplyAmount,");
//			buff.append(" book.fportcode,book.fsecuritycode from ").append(pub.yssGetTableName("tb_etf_standingbook"));
//			buff.append(" book where book.FBuyDate = ").append(dbl.sqlDate(this.dDate));
//			buff.append(" and book.FPortCode = ").append(dbl.sqlString(this.sPortCode));
//			buff.append(" and book.FSecurityCode <> ' ' group by book.fportcode, book.fsecuritycode");
			//------delete by songjie 2009.11.18 V4.1_ETF:MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A------//
			
			rs = dbl.queryByPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			while(rs.next()){
				sKey = rs.getString("fportcode") + "\t" + rs.getString("fsecuritycode");

				bookData.put(sKey,rs.getDouble("FTotalSupplyAmount")+ "\t" + rs.getDouble("FSupplyAmount"));
			}
			
			dbl.closeResultSetFinal(rs);
			//接口导入交易数据没有计算成本，以下是计算交易数据的成本----------------------//
			securityCost = new StgSecurity();
			securityCost.setYssPub(pub);
			securityCost.refreshTradeCost(this.dDate,this.dDate,this.sPortCode);
			//------------------------------end---------------------------------//
			
			//--------------以下计算当天交易的估值增值 ------------------------------//
			tradeRealCal = new SellTradeRelaCal(); 
			tradeRealCal.setYssPub(pub);
			tradeRealCal.setWorkDate(this.dDate);
			tradeRealCal.setPortCodes(this.sPortCode);
			tradeRealCal.doOperation("do");
			//--------------------end -----------------------------------------//
			
			buff.append(" select a.*, po.fportcury,se.ftradecury from  ").append("(select ");
			buff.append(" sum(case  when FTradeTypeCode = '01' then FTradeAmount else FTradeAmount * -1 end) as FtotalTradeAmount,");
			buff.append(" sum(FCost) as FAllCost,");
			buff.append(" sum(FBaseCuryCost) as FBaseCuryAllCost,");
			buff.append(" sum(FPortCuryCost) as FPortCuryAllCost,");
			//modify by ctq 为费用加上NVL，否则当有其中一个费用为空时则整个SUM结果为空
			//buff.append(" sum(FTradeFee1 + FTradeFee2 + FTradeFee3 + FTradeFee4 + FTradeFee5 + FTradeFee6 + FTradeFee7 + FTradeFee8) as FTotalTradeFee,");
			buff.append(" sum(" + dbl.sqlIsNull("FTradeFee1") + " + " + dbl.sqlIsNull("FTradeFee2") + " + " + dbl.sqlIsNull("FTradeFee3")
					+ " + " + dbl.sqlIsNull("FTradeFee4") + " + " + dbl.sqlIsNull("FTradeFee5") + " + " + dbl.sqlIsNull("FTradeFee6")
					+ " + " + dbl.sqlIsNull("FTradeFee7") + " + " + dbl.sqlIsNull("FTradeFee8") + ") as FTotalTradeFee,");
			buff.append(" sum(FTradeMoney) as FTotalTradeMoney,");
			buff.append(" sum(FTotalCost) as FTotalCost,");
			buff.append(" sum(FAppreciation) as FVMoney,");
			buff.append(" sum(FPortAppreciation) as FVBBMoney,");
			buff.append(" FPortCode, FSecurityCode,FCashAccCode,FTradetypecode from (");
			buff.append(" select sub.*, re.FAppreciation, re.FPortAppreciation from ");
			buff.append(pub.yssGetTableName("tb_data_subtrade")).append(" sub ");//业务资料表
			buff.append(" left join (select sum(FAppreciation) as FAppreciation,sum(FPortAppreciation) as FPortAppreciation,fNum from ").append(pub.yssGetTableName("tb_data_tradesellrela"));
			buff.append(" group by fNum) re on sub.fnum = re.fnum");
			buff.append(" where FBargaindate = ").append(dbl.sqlDate(this.dDate));
			buff.append(" and FPortCode = ").append(dbl.sqlString(this.sPortCode));
			buff.append(" and FCheckState = 1)group by FPortCode, FSecurityCode, FCashAccCode,FTradetypecode) a");
			buff.append(" join (select * from ").append(pub.yssGetTableName("tb_para_portfolio"));//组合设置表
			buff.append(" where FCheckState = 1) po on a.FPortCode = po.fportcode");
			buff.append(" join (select * from ").append(pub.yssGetTableName("tb_para_security"));//证券信息设置表
			buff.append(" where FCheckState = 1) se on a.FSecurityCode = se.FSecurityCode");
			
			rs = dbl.queryByPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			buff.append(" insert into ").append(pub.yssGetTableName("Tb_ETF_SubTrade"));
			buff.append("(FNum,FSecurityCode,FPortCode,FTradeTypeCode,FCashAccCode,FBargainDate,FBargainTime,");
			buff.append(" FPortCuryRate,FBaseCuryRate,FTradeAmount,FTradePrice,FTradeMoney,FTradeFee1,");
			buff.append(" FTotalCost,FCost,FBaseCuryCost,FPortCuryCost,FETFTradeWayCode,FBBTotalCost,FBBTradeFee,FVMoney,FVBBMoney)");
			buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			pst = dbl.getPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			//插入之前先删除数据
			buff.append(" delete from ").append(pub.yssGetTableName("Tb_ETF_SubTrade"));
			buff.append(" where FPortCode = ").append(dbl.sqlString(this.sPortCode));
			buff.append(" and FBargainDate = ").append(dbl.sqlDate(this.dDate));
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			//--------------------拼接交易编号---------------------
			strNumDate = YssFun.formatDatetime(this.dDate).substring(0, 8);
            strNumDate = strNumDate +
                dbFun.getNextInnerCode(pub.yssGetTableName("Tb_ETF_SubTrade"),
                                       dbl.sqlRight("FNUM", 6), "000000",
                                       " where FNum like 'T" + strNumDate + "%'", 1);
            strNumDate = "T" + strNumDate;
            String s = strNumDate.substring(9, strNumDate.length());
            sNum = Long.parseLong(s);
            //--------------------------------end--------------------------//
            
            booklist = getTotalETFTradeAmount();//获取估值当天组合代码对应的一级市场代码区分买卖标志的篮子数
            paramSet = (ETFParamSetBean)etfParam.get(this.sPortCode);//获取实体bean
            if (paramSet != null) {
				oneGradeMktCode = paramSet.getOneGradeMktCode();//一级市场代码
			}
            if(booklist.get(this.sPortCode + "\tS\t" + oneGradeMktCode) != null){
				basketOfSG = Double.parseDouble((String) booklist.get(this.sPortCode + "\tS\t" + oneGradeMktCode));//申购的篮子数
			}
			if(booklist.get(this.sPortCode + "\tB\t" + oneGradeMktCode) != null){
				basketOfSH = Double.parseDouble((String) booklist.get(this.sPortCode + "\tB\t" + oneGradeMktCode));//赎回的篮子数
			}
			while(rs.next()){
				//--------------------拼接交易编号---------------------
                sNum++;
                String tmp = "";
                for (int i = 0; i < s.length() - String.valueOf(sNum).length(); i++) {
                    tmp += "0";
                }
                strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
                // ------------------------end--------------------------//
				baseRate = this.getSettingOper().getCuryRate(// 基础汇率
						this.dDate, rs.getString("ftradecury"),
						rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);

				EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
				rateOper.setYssPub(pub);// 设置PUB
				rateOper.getInnerPortRate(this.dDate,
						rs.getString("fportcury"), rs.getString("FPortCode"));
				portRate = rateOper.getDPortRate();// 组合汇率
				
				sKey = rs.getString("FPortCode") + "\t" + rs.getString("FSecurityCode");
				
				if(bookData.containsKey(sKey)){
					pst.setString(1,strNumDate);//编号
					pst.setString(2,rs.getString("FSecurityCode"));//证券代码
					pst.setString(3,this.sPortCode);//组合代码
					//净申购
					if (basketOfSG > basketOfSH) {
						pst.setString(4,"01");
					}
					// 净赎回
					if (basketOfSG < basketOfSH) {
						pst.setString(4,"02");
					}
					
					pst.setString(5,rs.getString("FCashAccCode"));//现金账户
					pst.setDate(6,YssFun.toSqlDate(this.dDate));//成交日期
					pst.setString(7,"00:00:00");//成交时间
					pst.setDouble(8,portRate);//组合汇率
					pst.setDouble(9,baseRate);//基础汇率
					sSupplyAmout = bookData.get(sKey).toString().split("\t");
					if(basketOfSG > basketOfSH){//净申购
						if(rs.getString("FTradetypecode").equalsIgnoreCase("01")){//买入
							if(rs.getDouble("FtotalTradeAmount") <= Double.parseDouble(sSupplyAmout[1])){//当天买入的股票数量小于或等于股票的总的补票数量
								pst.setDouble(10,rs.getDouble("FtotalTradeAmount"));//数量
								//成交价格 = 成交金额/交易数量
								pst.setDouble(11,YssD.round(YssD.div(rs.getDouble("FTotalTradeMoney"),rs.getDouble("FtotalTradeAmount")),2));
								pst.setDouble(12,YssD.round(rs.getDouble("FTotalTradeMoney"),2));//成交金额
								pst.setDouble(13,YssD.round(rs.getDouble("FTotalTradeFee"),2));//交易费用
								pst.setDouble(14,YssD.round(rs.getDouble("FTotalCost"),2));//实际成交金额
								pst.setDouble(15,YssD.round(rs.getDouble("FAllCost"),2));//原币成本
								pst.setDouble(16,YssD.round(rs.getDouble("FBaseCuryAllCost"),2));//基础货币成本
								pst.setDouble(17,YssD.round(rs.getDouble("FPortCuryAllCost"),2));//组合货币成本
								pst.setString(18,"REPLACE");//投资类型 ： 主动或被动
								//本币实际成交金额 = 原币实际成交金额*基础汇率/组合汇率
								pst.setDouble(19,YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalCost"),baseRate),portRate),2));
								//本币交易费用 = 原币实际成交金额*基础汇率/组合汇率
								pst.setDouble(20,YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalTradeFee"),baseRate),portRate),2));
								//原币估值增值
								pst.setDouble(21,YssD.round(rs.getDouble("FVMoney"),2));
								//本币估值增值
								pst.setDouble(22,YssD.round(rs.getDouble("FVBBMoney"),2));
								
							}else{//当天买入的股票数量大于或等于股票的总的补票数量
								//主动投资数量=当天交易总数量- 补票总数量
								activeInverstAmount = YssD.sub(rs.getDouble("FtotalTradeAmount"),Double.parseDouble(sSupplyAmout[1]));
								//主动投资比例  = （当天交易总数量- 补票总数量)/当天交易数量
								dPassiveScale = YssD.div(activeInverstAmount,rs.getDouble("FtotalTradeAmount"));
								
								pst.setDouble(10,activeInverstAmount);//数量
								//成交价格 = 成交金额/交易数量
								pst.setDouble(11,YssD.round(YssD.div(YssD.mul(dPassiveScale,rs.getDouble("FTotalTradeMoney")),activeInverstAmount),2));
								pst.setDouble(12,YssD.round(YssD.mul(dPassiveScale,rs.getDouble("FTotalTradeMoney")),2));//成交金额
								pst.setDouble(13,YssD.round(YssD.mul(dPassiveScale,rs.getDouble("FTotalTradeFee")),2));//交易费用
								pst.setDouble(14,YssD.round(YssD.mul(rs.getDouble("FTotalCost"),dPassiveScale),2));//实际成交金额
								pst.setDouble(15,YssD.round(YssD.mul(rs.getDouble("FAllCost"),dPassiveScale),2));//原币成本
								pst.setDouble(16,YssD.round(YssD.mul(rs.getDouble("FBaseCuryAllCost"),dPassiveScale),2));//基础货币成本
								pst.setDouble(17,YssD.round(YssD.mul(rs.getDouble("FPortCuryAllCost"),dPassiveScale),2));//组合货币成本
								pst.setString(18,"ACTIVE");//投资类型 ： 主动或被动
								//本币实际成交金额 = 原币实际成交金额*基础汇率/组合汇率 * 被动投资比例 
								pst.setDouble(19,YssD.round(YssD.mul(YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalCost"),baseRate),portRate),2),dPassiveScale),2));
								//本币交易费用 = 原币实际成交金额*基础汇率/组合汇率 * 被动投资比例 
								pst.setDouble(20,YssD.round(YssD.mul(YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalTradeFee"),baseRate),portRate),2),dPassiveScale),2));
								//被动投资原币估值增值 = 原币估值增值 * 被动投资比例
								pst.setDouble(21,YssD.round(YssD.mul(rs.getDouble("FVMoney"),dPassiveScale),2));
								//被动投资本币估值增值 = 本币估值增值 * 被动投资比例
								pst.setDouble(22,YssD.round(YssD.mul(rs.getDouble("FVBBMoney"),dPassiveScale),2));
								
								pst.addBatch();
								//设置被动投资数据
								setActiveInvest(pst,rs,dPassiveScale,basketOfSG,basketOfSH,strNumDate,baseRate,portRate,sSupplyAmout); 
								sNum++;
							}
						}else{//卖出-卖出数据全部作为 主动投资
							setTradeDataActiveInvest(pst,rs,dPassiveScale,basketOfSG,basketOfSH,strNumDate,baseRate,portRate,sSupplyAmout);
							sNum++;
						}
					}else if(basketOfSG < basketOfSH){// 净赎回
						if(rs.getString("FTradetypecode").equalsIgnoreCase("02")){//卖出
							if(YssD.mul(rs.getDouble("FtotalTradeAmount"),-1) <= YssD.mul(Double.parseDouble(sSupplyAmout[1]),-1)){//当天交易的股票数量小于股票的总的补票数量
								pst.setDouble(10,YssD.mul(rs.getDouble("FtotalTradeAmount"),-1));//数量
								//成交价格 = 成交金额/交易数量
								pst.setDouble(11,YssD.round(YssD.div(rs.getDouble("FTotalTradeMoney"),YssD.mul(rs.getDouble("FtotalTradeAmount"),-1)),2));
								pst.setDouble(12,YssD.round(rs.getDouble("FTotalTradeMoney"),2));//成交金额
								pst.setDouble(13,YssD.round(rs.getDouble("FTotalTradeFee"),2));//交易费用
								pst.setDouble(14,YssD.round(rs.getDouble("FTotalCost"),2));//实际成交金额
								pst.setDouble(15,YssD.round(rs.getDouble("FAllCost"),2));//原币成本
								pst.setDouble(16,YssD.round(rs.getDouble("FBaseCuryAllCost"),2));//基础货币成本
								pst.setDouble(17,YssD.round(rs.getDouble("FPortCuryAllCost"),2));//组合货币成本
								pst.setString(18,"REPLACE");//投资类型 ： 主动或被动
								//本币实际成交金额 = 原币实际成交金额*基础汇率/组合汇率
								pst.setDouble(19,YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalCost"),baseRate),portRate),2));
								//本币交易费用 = 原币实际成交金额*基础汇率/组合汇率
								pst.setDouble(20,YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalTradeFee"),baseRate),portRate),2));
								//原币估值增值
								pst.setDouble(21,YssD.round(rs.getDouble("FVMoney"),2));
								//本币估值增值
								pst.setDouble(22,YssD.round(rs.getDouble("FVBBMoney"),2));
								
							}else{
								//主动投资数量=当天交易总数量- 补票总数量
								activeInverstAmount = YssD.sub(YssD.mul(rs.getDouble("FtotalTradeAmount"),-1),Double.parseDouble(sSupplyAmout[1])<0?YssD.mul(Double.parseDouble(sSupplyAmout[1]),-1):Double.parseDouble(sSupplyAmout[1]));
								//主动投资比例 = （当天交易总数量- 补票总数量)/当天交易数量
								dPassiveScale = YssD.div(activeInverstAmount,YssD.mul(rs.getDouble("FtotalTradeAmount"),-1));
								
								pst.setDouble(10,activeInverstAmount);//数量
								//成交价格 = 成交金额/交易数量
								pst.setDouble(11,YssD.round(YssD.div(YssD.mul(dPassiveScale,rs.getDouble("FTotalTradeMoney")),activeInverstAmount),2));
								pst.setDouble(12,YssD.round(YssD.mul(dPassiveScale,rs.getDouble("FTotalTradeMoney")),2));//成交金额
								pst.setDouble(13,YssD.round(YssD.mul(dPassiveScale,rs.getDouble("FTotalTradeFee")),2));//交易费用
								pst.setDouble(14,YssD.round(YssD.mul(rs.getDouble("FTotalCost"),dPassiveScale),2));//实际成交金额
								pst.setDouble(15,YssD.round(YssD.mul(rs.getDouble("FAllCost"),dPassiveScale),2));//原币成本
								pst.setDouble(16,YssD.round(YssD.mul(rs.getDouble("FBaseCuryAllCost"),dPassiveScale),2));//基础货币成本
								pst.setDouble(17,YssD.round(YssD.mul(rs.getDouble("FPortCuryAllCost"),dPassiveScale),2));//组合货币成本
								pst.setString(18,"ACTIVE");//投资类型 ： 主动
								//本币实际成交金额 = 原币实际成交金额*基础汇率/组合汇率 * 被动投资比例 
								pst.setDouble(19,YssD.round(YssD.mul(YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalCost"),baseRate),portRate),2),dPassiveScale),2));
								//本币交易费用 = 原币实际成交金额*基础汇率/组合汇率 * 被动投资比例 
								pst.setDouble(20,YssD.round(YssD.mul(YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalTradeFee"),baseRate),portRate),2),dPassiveScale),2));
								//被动投资原币估值增值 = 原币估值增值 * 被动投资比例
								pst.setDouble(21,YssD.round(YssD.mul(rs.getDouble("FVMoney"),dPassiveScale),2));
								//被动投资本币估值增值 = 本币估值增值 * 被动投资比例
								pst.setDouble(22,YssD.round(YssD.mul(rs.getDouble("FVBBMoney"),dPassiveScale),2));
								
								pst.addBatch();
								//设置被动投资数据
								setActiveInvest(pst,rs,dPassiveScale,basketOfSG,basketOfSH,strNumDate,baseRate,portRate,sSupplyAmout); 
								sNum++;
							}
						}else{//买入
							//此方法把交易数据中数据设置为主动投资数据
							setTradeDataActiveInvest(pst,rs,dPassiveScale,basketOfSG,basketOfSH,strNumDate,baseRate,portRate,sSupplyAmout);
							sNum++;
						}
					}else{
						/**shashijie 2011.06.23 STORY #974 按照华宝兴业ETF的产品规则，定义出符合该产品的功能需求*/
						//若申购等于赎回则不做任何处理
						conn.rollback();
						conn.setAutoCommit(true);
						return;
						/**end*/
					}
				}else{//当天没有申赎数据
					setSubTradeData(pst,rs,strNumDate,baseRate,portRate);
				}
				pst.addBatch();
 			}
			pst.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
		}catch (Exception e) {
			throw new YssException("拆分ETF交易数据出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
		}
		
	}
	/**
	 * 如果当天没有申赎数据，业务资料表中的数据均为主动投资数据
	 * @param pst
	 * @param rs
	 * @param strNumDate
	 * @param portRate
	 * @param baseRate
	 * @throws YssException 
	 */
	private void setSubTradeData(PreparedStatement pst, ResultSet rs, String strNumDate, 
			 double baseRate,double portRate) throws YssException {
		try{
			pst.setString(1,strNumDate);//编号
			pst.setString(2,rs.getString("FSecurityCode"));//证券代码
			pst.setString(3,this.sPortCode);//组合代码
			pst.setString(4,rs.getString("FTradetypecode"));//交易类型
			pst.setString(5,rs.getString("FCashAccCode"));//现金账户
			pst.setDate(6,YssFun.toSqlDate(this.dDate));//成交日期
			pst.setString(7,"00:00:00");//成交时间
			pst.setDouble(8,portRate);//组合汇率
			pst.setDouble(9,baseRate);//基础汇率
			
			pst.setDouble(10,rs.getDouble("FtotalTradeAmount")<0?YssD.mul(rs.getDouble("FtotalTradeAmount"),-1):rs.getDouble("FtotalTradeAmount"));//数量
			//成交价格 = 成交金额/交易数量
			pst.setDouble(11,YssD.round(YssD.div(rs.getDouble("FTotalTradeMoney"),rs.getDouble("FtotalTradeAmount")<0?YssD.mul(rs.getDouble("FtotalTradeAmount"),-1):rs.getDouble("FtotalTradeAmount")),2));
			pst.setDouble(12,YssD.round(rs.getDouble("FTotalTradeMoney"),2));//成交金额
			pst.setDouble(13,YssD.round(rs.getDouble("FTotalTradeFee"),2));//交易费用
			pst.setDouble(14,YssD.round(rs.getDouble("FTotalCost"),2));//实际成交金额
			pst.setDouble(15,YssD.round(rs.getDouble("FAllCost"),2));//原币成本
			pst.setDouble(16,YssD.round(rs.getDouble("FBaseCuryAllCost"),2));//基础货币成本
			pst.setDouble(17,YssD.round(rs.getDouble("FPortCuryAllCost"),2));//组合货币成本
			pst.setString(18,"ACTIVE");//投资类型 ： 主动
			//本币实际成交金额 = 原币实际成交金额*基础汇率/组合汇率
			pst.setDouble(19,YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalCost"),baseRate),portRate),2));
			//本币交易费用 = 原币实际成交金额*基础汇率/组合汇率
			pst.setDouble(20,YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalTradeFee"),baseRate),portRate),2));
			//原币估值增值
			pst.setDouble(21,YssD.round(rs.getDouble("FVMoney"),2));
			//本币估值增值
			pst.setDouble(22,YssD.round(rs.getDouble("FVBBMoney"),2));
		}catch (Exception e) {
			throw new YssException("把业务资料表中的数据插入到ETF交易子表中出错！",e);
		}
		
	}
	/**
	 * 此方法把交易数据中数据设置为主动投资数据
	 * @param pst
	 * @param rs
	 * @param passiveScale
	 * @param basketOfSG
	 * @param basketOfSH
	 * @param strNumDate
	 * @param baseRate
	 * @param portRate
	 * @param supplyAmout
	 * @throws YssException 
	 */
	private void setTradeDataActiveInvest(PreparedStatement pst, ResultSet rs, double passiveScale, double basketOfSG, 
			double basketOfSH, String strNumDate, double baseRate, double portRate, String[] supplyAmout) throws YssException {
		try{
			pst.setString(1,strNumDate);
			pst.setString(2,rs.getString("FSecurityCode"));
			pst.setString(3,this.sPortCode);
			//净申购
			if (basketOfSG > basketOfSH) {
				pst.setString(4,"02");
			}
			// 净赎回
			if (basketOfSG < basketOfSH) {
				pst.setString(4,"01");
			}
			
			pst.setString(5,rs.getString("FCashAccCode"));
			pst.setDate(6,YssFun.toSqlDate(this.dDate));
			pst.setString(7,"00:00:00");
			pst.setDouble(8,portRate);
			pst.setDouble(9,baseRate);
			
			pst.setDouble(10,rs.getDouble("FtotalTradeAmount")<0?YssD.mul(rs.getDouble("FtotalTradeAmount"),-1):rs.getDouble("FtotalTradeAmount"));//数量 = 交易数量
			//成交价格 = （总金额 ）/（交易总数量 ）
			pst.setDouble(11,YssD.round(YssD.div(rs.getDouble("FTotalTradeMoney"),rs.getDouble("FtotalTradeAmount")<0?YssD.mul(rs.getDouble("FtotalTradeAmount"),-1):rs.getDouble("FtotalTradeAmount")),2));
			// 成交金额 = 总金额 
			pst.setDouble(12,YssD.round(rs.getDouble("FTotalTradeMoney"),2));
			//交易费用 = 交易总费用 
			pst.setDouble(13,YssD.round(rs.getDouble("FTotalTradeFee"),2));
			//实际成交金额 = 总实际成交金额 
			pst.setDouble(14,YssD.round(rs.getDouble("FTotalCost"),2));
			//成本 = 总成本 
			pst.setDouble(15,YssD.round(rs.getDouble("FAllCost"),2));
			//基础货币成本 = 总基础货币成本 
			pst.setDouble(16,YssD.round(rs.getDouble("FBaseCuryAllCost"),2));
			//组合货币成本 = 总组合货币成本
			pst.setDouble(17,YssD.round(rs.getDouble("FPortCuryAllCost"),2));
			pst.setString(18,"ACTIVE");//投资类型 ： 主动或被动
			//本币实际成交金额 = 本币总实际成交金额 
			pst.setDouble(19,YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalCost"),baseRate),portRate),2));
			//本币交易费用 = 本币总交易费用 
			pst.setDouble(20,YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalTradeFee"),baseRate),portRate),2));			
			//主动投资原币估值增值 = 原币估值增值 
			pst.setDouble(21,YssD.round(rs.getDouble("FVMoney"),2));
			//主动投资本币估值增值 = 本币估值增值
			pst.setDouble(22,YssD.round(rs.getDouble("FVBBMoney"),2));
			
		}catch (Exception e) {
			throw new YssException("把交易数据中数据设置为主动投资数据出错！",e);
		}
		
	}
	/**
	 * 设置被动投资数据 - 以下计算都是用轧差计算
	 * @param rs
	 * @param passiveScale
	 * @param basketOfSG
	 * @param basketOfSH
	 * @throws YssException
	 */
	private void setActiveInvest(PreparedStatement pst,ResultSet rs, 
			double dPassiveScale,double basketOfSG,double basketOfSH,
			String strNumDate,double baseRate,double portRate,String [] sSupplyAmout) throws YssException {
		long sNum = 0;
		double activeInverstAmount = 0;//被动投资数量
		try{
			//--------------------拼接交易编号---------------------
			String s = strNumDate.substring(9, strNumDate.length());
            sNum = Long.parseLong(s);

            sNum++;
            String tmp = "";
            for (int i = 0; i < s.length() - String.valueOf(sNum).length(); i++) {
                tmp += "0";
            }
            strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
            // ------------------------end--------------------------//
            
			pst.setString(1,strNumDate);
			pst.setString(2,rs.getString("FSecurityCode"));
			pst.setString(3,this.sPortCode);
			//净申购
			if (basketOfSG > basketOfSH) {
				pst.setString(4,"01");
			}
			// 净赎回
			if (basketOfSG < basketOfSH) {
				pst.setString(4,"02");
			}
			
			pst.setString(5,rs.getString("FCashAccCode"));
			pst.setDate(6,YssFun.toSqlDate(this.dDate));
			pst.setString(7,"00:00:00");
			pst.setDouble(8,portRate);
			pst.setDouble(9,baseRate);
			activeInverstAmount = Double.parseDouble(sSupplyAmout[1]);
			pst.setDouble(10,activeInverstAmount<0?YssD.mul(activeInverstAmount,-1):activeInverstAmount);//数量 = 交易总数量 - 补票总数量
			//成交价格 = （总金额 - （被动比例×总金额））/（交易总数量 - 补票总数量）
			pst.setDouble(11,YssD.round(YssD.div(YssD.sub(rs.getDouble("FTotalTradeMoney"),YssD.mul(dPassiveScale,rs.getDouble("FTotalTradeMoney"))),
					activeInverstAmount<0?YssD.mul(activeInverstAmount,-1):activeInverstAmount),2));
			// 成交金额 = 总金额 - （被动比例×总金额）
			pst.setDouble(12,YssD.round(YssD.sub(rs.getDouble("FTotalTradeMoney"),YssD.mul(dPassiveScale,rs.getDouble("FTotalTradeMoney"))),2));
			//交易费用 = 交易总费用 - （补票费用（即 被动比例× 交易总费用））
			pst.setDouble(13,YssD.round(YssD.sub(rs.getDouble("FTotalTradeFee"),YssD.mul(dPassiveScale,rs.getDouble("FTotalTradeFee"))),2));
			//实际成交金额 = 总实际成交金额 - 被动投资的实际成交金额
			pst.setDouble(14,YssD.round(YssD.sub(rs.getDouble("FTotalCost"),YssD.mul(rs.getDouble("FTotalCost"),dPassiveScale)),2));
			//成本 = 总成本 - 被动投资的成本
			pst.setDouble(15,YssD.round(YssD.sub(rs.getDouble("FAllCost"),YssD.mul(rs.getDouble("FAllCost"),dPassiveScale)),2));
			//基础货币成本 = 总基础货币成本 - 被动投资的基础货币成本
			pst.setDouble(16,YssD.round(YssD.sub(rs.getDouble("FBaseCuryAllCost"),YssD.mul(rs.getDouble("FBaseCuryAllCost"),dPassiveScale)),2));
			//组合货币成本 = 总组合货币成本 - 被动投资的组合货币成本
			pst.setDouble(17,YssD.round(YssD.sub(rs.getDouble("FPortCuryAllCost"),YssD.mul(rs.getDouble("FPortCuryAllCost"),dPassiveScale)),2));
			pst.setString(18,"REPLACE");//投资类型 ： 被动
			//本币实际成交金额 = 本币总实际成交金额 - 原币实际成交金额*基础汇率/组合汇率 * 被动投资比例 
			pst.setDouble(19,YssD.sub(YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalCost"),baseRate),portRate),2),YssD.round(YssD.mul(YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalCost"),baseRate),portRate),2),dPassiveScale),2)));
			//本币交易费用 = 本币总交易费用 - 原币实际成交金额*基础汇率/组合汇率 * 被动投资比例 
			pst.setDouble(20,YssD.sub(YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalTradeFee"),baseRate),portRate),2),YssD.round(YssD.mul(YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalTradeFee"),baseRate),portRate),2),dPassiveScale),2)));			
			//主动投资原币估值增值 = 原币估值增值 -被动投资原币估值增值(原币估值增值 * 被动投资比例)
			pst.setDouble(21,YssD.round(YssD.sub(rs.getDouble("FVMoney"),YssD.round(YssD.mul(rs.getDouble("FVMoney"),dPassiveScale),2)),2));
			//主动投资本币估值增值 = 本币估值增值-被动投资本币估值增值（本币估值增值 * 被动投资比例）
			pst.setDouble(22,YssD.round(YssD.sub(rs.getDouble("FVBBMoney"),YssD.round(YssD.mul(rs.getDouble("FVBBMoney"),dPassiveScale),2)),2));
			
		}catch (Exception e) {
			throw new YssException("设置被动投资数据出错！",e);
		}
		
	}

	/**
	 * 获取估值当天组合代码对应的一级市场代码区分买卖标志的篮子数
	 * 
	 * @return
	 * @throws YssException
	 */
	public HashMap getTotalETFTradeAmount() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		HashMap hmbasket = new HashMap();
		ETFParamSetBean paramSet = null;
		double basicRate = 0;// 基准比例
		String bs = "";// 买卖标志
		String securityCode = "";// 证券代码
		double sumTradeAmount = 0;// 汇总后的成交数量
		String portCode = "";// 组合代码
		double basketCount = 0;// 篮子数
		HashMap etfParam = new HashMap();
		try {
			strSql = " select sum(FTradeAmount) as FTradeAmount,FSecurityCode,FMark,FPortCode from " + pub.yssGetTableName("Tb_ETF_GHInterface")
					+ " where FBargainDate = " + dbl.sqlDate(this.dDate) + " and FOperType = '1stdcode'" + " and FPortCode in("
					+ operSql.sqlCodes(this.sPortCode) + ") group by FSecurityCode,FMark,FPortCode ";

			rs = dbl.queryByPreparedStatement(strSql);
			while (rs.next()) {
				bs = rs.getString("FMark");
				securityCode = rs.getString("FSecurityCode");
				sumTradeAmount = rs.getDouble("FTradeAmount");
				portCode = rs.getString("FPortCode");

				paramSet = (ETFParamSetBean) etfParam.get(portCode);// 根据组合代码获取ETF参数设置
				if (paramSet != null) {
					basicRate = paramSet.getNormScale();
				}

				basketCount = Math.abs(YssD.div(sumTradeAmount, basicRate));// 篮子数

				hmbasket.put(portCode + "\t" + bs + "\t" + securityCode, String.valueOf(basketCount));
			}
			return hmbasket;
		} catch (Exception e) {
			throw new YssException("根据组合代码查询ETF份额出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

}


















