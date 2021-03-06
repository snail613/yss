package com.yss.main.etfoperation.etfaccbook.timeanddifference;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.etfaccbook.CtlETFAccBook;
import com.yss.main.etfoperation.etfaccbook.PretValMktPriceAndExRate;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailRefBean;
import com.yss.main.etfoperation.pojo.StandingBookBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 此类事根据明细表和明细关联表中的数据汇总到台账表中（tb_etf_standingbook）
 * @author xuqiji 20091116 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
 *
 */
public class CreateBookData extends CtlETFAccBook{
	private ETFParamSetBean paramSet = null;// ETF参数的实体类
	private ETFParamSetAdmin paramSetAdmin = null;//参数设置操作类
	private HashMap etfParam = null;//保存参数设置
	private String sMaxTradeNum= "00000000000000";//保存最大的当日明细表中的申请编号
	private ArrayList standBookData = new ArrayList();//保存台账bean 数据
	private ArrayList standBookDivideRateData = new ArrayList();//保存台账汇率拆分的数据
	private String securityCodes = "";//证券代码
	private java.util.Date startDate = null;//开始的申赎日期
	private java.util.Date endDate = null;//结束的申赎日期	
	private java.util.Date tradeDate = null;//估值日期
	private String standingBookType = "";//台账类型
	private String portCodes = "";//组合代码
	public java.util.Date getEndDate() {
		return endDate;
	}
	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}
	public String getPortCodes() {
		return portCodes;
	}
	public void setPortCodes(String portCodes) {
		this.portCodes = portCodes;
	}
	public String getSecurityCodes() {
		return securityCodes;
	}
	public void setSecurityCodes(String securityCodes) {
		this.securityCodes = securityCodes;
	}
	public ArrayList getStandBookData() {
		return standBookData;
	}
	public void setStandBookData(ArrayList standBookData) {
		this.standBookData = standBookData;
	}
	public java.util.Date getStartDate() {
		return startDate;
	}
	public void setStartDate(java.util.Date startDate) {
		this.startDate = startDate;
	}
	public java.util.Date getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(java.util.Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	public CreateBookData(){
		super();
	}
	/**
	 * 处理业务的入口方法
	 * @throws YssException
	 */
	public void doManageAll() throws YssException{
		int days = 0;//保存起始日期与截止日期之差
		String[] sPortcode = null;//组合代码
		Date dDate = null;//起始日期
		Date theDate = null;//操作日当天的是否是工作日
		EachExchangeHolidays holiday = null;//节假日代码
		String sRowStr ="";
		try{
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			sPortcode = this.portCodes.split(",");//组合代码
			days = YssFun.dateDiff(this.startDate,this.endDate);//赋值
			dDate = this.startDate;//赋值
			for(int i=0;i<=days;i++){//循环日期
				//拼接参数：节假日代码+当天偏离天数+操作日期
				sRowStr = paramSet.getSHolidayCode() + "\t" + 0 + "\t" + YssFun.formatDate(dDate);
				//解析参数
				holiday.parseRowStr(sRowStr);
				theDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//获取当天的最近一个工作日
				if(YssFun.dateDiff(theDate,dDate)!=0){//判断当天是不是工作日
					dDate = YssFun.addDay(dDate,1);
					continue;
				}
				sMaxTradeNum ="00000000000000";//保存最大的当日明细表中的申请编号,每次循环时都要先赋初始值
				doSelectStandindBookData(dDate);//此方法处理明细数据和明细关联数据汇总到台账bean中保存，并放到集合中
				insertStandingBook(dDate);//此方法汇总明细表和明细关联表数据插入到台帐表中
				updateT4RateBookData(dDate);//更新汇率日期是t+4的台账的实际汇率和换汇日期
				dDate = YssFun.addDay(dDate,1);//每次循环日期加1
			}
			
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
	/**
	 * 更新汇率日期是t+4的台账的实际汇率和换汇日期
	 * @param date
	 */
	private void updateT4RateBookData(Date dDate) throws YssException{
		StringBuffer buff = null;//拼接SQL语句
		Connection conn =null;//数据库连接
		boolean bTrans = true;//事物控制标识
		ResultSet rs =null;//结果集
		HashMap changeRateData = new HashMap();//保存换汇汇率
		HashMap changeRateDate = new HashMap();//保存换汇日期
		String sKey="";
		PreparedStatement pst = null;//预处理
		double dbBuPiao = 0;//补票和强制处理的应退合计
		double dbFenHong = 0;//分红的应退合计
		double dbQuanzheng = 0;//权证的应退合计
		double dbSumRefund = 0;//应退合计
		try{
			buff = new StringBuffer(100);//实例化
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//设置为手动提交事物
			//获取当天的实际汇率
			buff.append(" select * from ").append(pub.yssGetTableName("tb_etf_bookexratedata")).append(" where FCheckState = 1 and FBookType = 'S' and FExRateDate =").append(dbl.sqlDate(dDate));
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			
			while(rs.next()){
				//主键：组合代码+台账类型+申赎日期+汇率类型
				sKey = rs.getString("FPortCode")+"\t"+rs.getString("FBookType") + "\t" + rs.getDate("FBuyDate") + "\t" + "T+4";
				//把实际汇率和换汇日期放到hash表中
				changeRateData.put(sKey,new java.lang.Double(rs.getDouble("FExRateValue")));
				changeRateDate.put(sKey,rs.getDate("FExRateDate"));
			}
			dbl.closeResultSetFinal(rs);
			//根据台账类型，申赎日期，组合代码查询台账表数据
			buff.append(" select * from ").append(pub.yssGetTableName("tb_etf_standingbook")).append(" where FBs = 'S' and FBuyDate < ").append(dbl.sqlDate(dDate));
			buff.append(" and FPortCode in (").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			
			buff.append(" update ").append(pub.yssGetTableName("tb_etf_standingbook"));
			buff.append(" set FFactExRate = ?,FExRateDate = ?, FSumReturn = ?");
			buff.append(" where FPortCode = ? and FBuyDate = ? and FBs = ? and FRateType = ? and FNum =?");
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());			
			while(rs.next()){
				sKey = rs.getString("FPortCode")+ "\t" + rs.getString("FBs") + "\t" +rs.getDate("FBuyDate") + "\t" +rs.getString("FRateType");
				if(changeRateData.containsKey(sKey)){
					//实际应退合计 = 
					//总派息*第二次卖出数量/替代数量*业务日汇率+
					//总派息*强制处理数量/替代数量*业务日汇率+
					dbBuPiao = 
						YssD.add(
								YssD.round(
										YssD.mul(
												YssD.mul(
														rs.getDouble("FOMakeUpCost1"), 
														-1),
												Double.parseDouble(changeRateData.get(sKey).toString())), 
										2), 
								YssD.round(
										YssD.mul(
												YssD.mul(
														rs.getDouble("FOMakeUpCost2"),
														-1),
												Double.parseDouble(changeRateData.get(sKey).toString())),
										2),
								YssD.round(
										YssD.mul(
												YssD.mul(
														rs.getDouble("FOMustMkUpCost"), 
														-1), 
												Double.parseDouble(changeRateData.get(sKey).toString())), 
										2));
					
					dbFenHong =
						YssD.add(
								YssD.round(
										YssD.mul(
												YssD.mul(
														rs.getDouble("FTotalInterest"), 
														-1), 
												YssD.div(//第二次补票数量和替代数量都有方向所以就不需要再乘一个方向了
														rs.getDouble("FMakeUpAmount2"), 
														rs.getDouble("FMakeUpAmount")), 
												Double.parseDouble(changeRateData.get(sKey).toString())), 
										2), 
								YssD.round(
										YssD.mul(
												YssD.mul(
														rs.getDouble("FTotalInterest"), 
														-1), 
												YssD.div(//强制处理数量和替代数量都有方向所以就不需要再乘一个方向了
														rs.getDouble("FMustMkUpAmount"), 
														rs.getDouble("FMakeUpAmount")), 
												Double.parseDouble(changeRateData.get(sKey).toString())), 
										2));
					dbQuanzheng = 
						YssD.add(
								YssD.round(
										YssD.mul(
												YssD.mul(
														rs.getDouble("FWarrantCost"), 
														-1), 
												YssD.div(//第二次补票数量和替代数量都有方向所以就不需要再乘一个方向了
														rs.getDouble("FMakeUpAmount2"), 
														rs.getDouble("FMakeUpAmount")), 
												Double.parseDouble(changeRateData.get(sKey).toString())), 
										2), 
								YssD.round(
										YssD.mul(
												YssD.mul(
														rs.getDouble("FWarrantCost"), 
														-1), 
												YssD.div(//强制处理数量和替代数量都有方向所以就不需要再乘一个方向了
														rs.getDouble("FMustMkUpAmount"), 
														rs.getDouble("FMakeUpAmount")), 
												Double.parseDouble(changeRateData.get(sKey).toString())), 
										2));
					dbSumRefund = YssD.add(dbBuPiao, dbFenHong, dbQuanzheng);
					
					pst.setDouble(1,Double.parseDouble(changeRateData.get(sKey).toString()));
					pst.setDate(2,YssFun.toSqlDate(changeRateDate.get(sKey).toString()));
					pst.setDouble(3,YssD.mul(dbSumRefund, -1));
					pst.setString(4,rs.getString("FPortCode"));
					pst.setDate(5,rs.getDate("FBuyDate"));
					pst.setString(6,rs.getString("FBs"));
					pst.setString(7,rs.getString("FRateType"));
					pst.setString(8,rs.getString("FNum"));
					pst.executeUpdate();
				}
			}
			conn.commit();
			conn.setAutoCommit(true);
			bTrans = false;
			
		}catch (Exception e) {
			throw new YssException("更新汇率日期是t+4的台账的实际汇率和换汇日期出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn,bTrans);
			dbl.closeStatementFinal(pst);
		}
	}
	/**
	 * 此方法做解析前台传来数据
	 */
	public void initData(Date startDate,Date endDate,Date tradeDate ,String portCodes,String standingBookType) throws YssException{
		try{
			this.startDate = startDate;//起始日期
			this.endDate = endDate;//截止日期
			this.tradeDate = tradeDate;//业务日期
			this.portCodes = portCodes;//组合代码
			this.standingBookType = standingBookType;//台账类型
			paramSetAdmin = new ETFParamSetAdmin();//实例化
			paramSetAdmin.setYssPub(pub);//设置pub
			etfParam = paramSetAdmin.getETFParamInfo(this.portCodes); // 根据已选组合代码用于获取相关ETF参数数据
			paramSet = (ETFParamSetBean) etfParam.get(this.portCodes);//参数设置实体bean
		}catch (Exception e) {
			throw new YssException("做解析前台传来数据出错！",e);
		}
	}
	
	/**
	 * 此方法处理明细数据和明细关联数据汇总到台账bean中保存，并放到集合中
	 * @param dDate
	 * @throws YssException
	 */
	private void doSelectStandindBookData(Date dDate) throws YssException{
		StringBuffer buff = null;//拼接SQL语句
		ResultSet rs =null;//结果集
		String sRowStr ="";
		Date yesDate = null;//操作日当天的钱一个工作日
		EachExchangeHolidays holiday = null;
		int DataDirection =0; //数据方向
		StandingBookBean book = null;//台账实体bean
		HashMap iSGOrSH=null;//保存昨天和今天是净申购还是净赎数据
		double SGSHStockList = 0;//当天申赎篮子数
		HashMap mapBookData = null;//保存台账数据
		String sNum = "";
		String oldTradeNum="000000";
		try{
			if(standBookData.size()!=0){//集合不为空时，先清空数据
				standBookData.clear();
			}
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			sRowStr = paramSet.getSHolidayCode() + "\t" + -1 + "\t" + YssFun.formatDate(dDate);//拼接参数：节假日代码+当天的偏离天数+操作日期
			
			holiday.parseRowStr(sRowStr);//解析数据
			yesDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//获取当天的最近一个工作日
			
			iSGOrSH =doJudgeTheDayNetSGOrSH(dDate,yesDate);//保存昨天和今天是净申购还是净赎数据
			
			mapBookData = new HashMap();//实例化
			
			buff = new StringBuffer(500);
			//从明细表关联明细关联表中取数据
			buff.append(" select t1.fnum as supplyNum,t1.FExchangeRate as FSHRate,t1.*, t2.*,t2.FUnitCost as FSupplyUnitCost,t2.FExchangeRate as FSupplyRate from ").append(pub.yssGetTableName("tb_etf_tradestldtl"));
			buff.append(" t1 left join (select * from ").append(pub.yssGetTableName("tb_etf_tradstldtlref")).append(") t2 on t1.fnum = t2.fnum");
			buff.append(" where t1.fportcode in (").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and t1.fbuydate between ").append(dbl.sqlDate(yesDate)).append(" and ").append(dbl.sqlDate(dDate));
			buff.append(" order by t1.fnum,t1.fportcode, t1.fstockholdercode, t1.fsecuritycode, t1.fbs, t1.fbuydate,t2.FDataMark,t2.fmakeupdate");
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			while(rs.next()){
				if(iSGOrSH.containsKey(rs.getString("FPortCode")+"\t"+rs.getDate("fbuydate"))){
					SGSHStockList = Double.parseDouble((String)iSGOrSH.get(rs.getString("FPortCode")+"\t"+rs.getDate("fbuydate")));//获取篮子数
				}
				book = new StandingBookBean();//实例化
				if(SGSHStockList >=0){//当天是净申购和申购等于赎回
					book.setRateType("T+1");//汇率类型
				}else{//净赎回,T日不进行汇率拆分，在T+1日才进行汇率拆分，所以都先打上标识为T+1
					if(rs.getString("FBs").equalsIgnoreCase("B")){
						book.setRateType("T+1");
					}else{
						book.setRateType("T+1");
					}
				}
				DataDirection = Integer.parseInt(rs.getString("FDataDirection"));//方向
				book.setNum(rs.getString("FNum"));//申请编号
//				if(Double.parseDouble(rs.getString("FNum").substring(9,rs.getString("FNum").length()))>Double.parseDouble(sMaxTradeNum.substring(9,sMaxTradeNum.length()))){
//					sMaxTradeNum = rs.getString("FNum");
//				}
				book.setTradeNum(rs.getString("FTradeNum"));//成交编号
				book.setBuyDate(rs.getDate("FBuyDate"));//申赎日期
				book.setBs(rs.getString("FBs"));//台账类型
				book.setPortCode(rs.getString("FPortCode"));//组合代码
				book.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
				book.setStockHolderCode(rs.getString("FStockHolderCode"));//投资者
				book.setBrokerCode(rs.getString("FBrokerCode"));//券商
				book.setSeatCode(rs.getString("FSeatCode"));//交易席位
				book.setMakeUpAmount(YssD.mul(rs.getDouble("FReplaceAmount"),DataDirection));//申赎数量
				book.setUnitCost(rs.getDouble("FUnitCost"));//单位成本
				book.setReplaceCash(rs.getDouble("FHReplaceCash"));//替代金额
				book.setCanReplaceCash(YssD.mul(rs.getDouble("FHCReplaceCash"),DataDirection));//可退替代款
				book.setExRightDate(rs.getDate("FExRightDate"));//权益日期
				book.setSumAmount(YssD.mul(rs.getDouble("FSumAmount"),DataDirection));//总数量
				book.setRealAmount(YssD.mul(rs.getDouble("FRealAmount"),DataDirection));//实际数量
				book.setTotalInterest(YssD.mul(rs.getDouble("FInterest"),DataDirection));//总派息原币
				book.setWarrantCost(YssD.mul(rs.getDouble("FWarrantCost"),DataDirection));//权证价值原币
				book.setBBInterest(YssD.mul(rs.getDouble("FBBInterest"),DataDirection));//总派息本币
				book.setBBWarrantCost(YssD.mul(rs.getDouble("FBBWarrantCost"),DataDirection));//权证价值本币
				book.setRightRate(rs.getDouble("FRightRate"));//权益汇率
				//-------第一次补票数据------------
				book.setMakeUpDate1(rs.getDate("FMakeUpDate"));//第一次补票日期
				book.setMakeUpAmount1(YssD.mul(rs.getDouble("FMakeUpAmount"),DataDirection));//第一次补票数量
				book.setMakeUpUnitCost1(rs.getDouble("FSupplyUnitCost"));//第一次补票单位成本
				book.setoMakeUpCost1(YssD.mul(rs.getDouble("FOMakeUpCost"),DataDirection));//第一次补票总成本原币
				book.sethMakeUpCost1(YssD.mul(rs.getDouble("FHMakeUpCost"),DataDirection));//第一次补票总成本本币
				book.setMakeUpRepCash1(YssD.mul(rs.getDouble("FHPReplaceCash"),DataDirection));//第一次补票可退替代款原币
				book.setCanMkUpRepCash1(YssD.mul(rs.getDouble("FHCPReplaceCash"),DataDirection));//第一次补票可退替代款本币
				book.setExRate1(rs.getDouble("FSupplyRate"));//第一次补票汇率
				//-------第二次补票数据------------
				if(sNum.equalsIgnoreCase(rs.getString("supplyNum"))&&rs.getString("FDataMark").equalsIgnoreCase("0")&&paramSet.getDealDayNum()>0){
					if(mapBookData.containsKey(rs.getString("supplyNum"))){
						book = (StandingBookBean) mapBookData.get(rs.getString("supplyNum"));
						book.setExRightDate(rs.getDate("FExRightDate"));
						book.setSumAmount(YssD.mul(rs.getDouble("FSumAmount"),DataDirection));
						book.setRealAmount(YssD.mul(rs.getDouble("FRealAmount"),DataDirection));
						book.setTotalInterest(YssD.mul(rs.getDouble("FInterest"),DataDirection));
						book.setWarrantCost(YssD.mul(rs.getDouble("FWarrantCost"),DataDirection));
						book.setBBInterest(YssD.mul(rs.getDouble("FBBInterest"),DataDirection));
						book.setBBWarrantCost(YssD.mul(rs.getDouble("FBBWarrantCost"),DataDirection));
						book.setRightRate(rs.getDouble("FRightRate"));
						
						
						book.setMakeUpDate2(rs.getDate("FMakeUpDate"));
						book.setMakeUpAmount2(YssD.mul(rs.getDouble("FMakeUpAmount"),DataDirection));
						book.setMakeUpUnitCost2(rs.getDouble("FSupplyUnitCost"));
						book.setoMakeUpCost2(YssD.mul(rs.getDouble("FOMakeUpCost"),DataDirection));
						book.sethMakeUpCost2(YssD.mul(rs.getDouble("FHMakeUpCost"),DataDirection));
						book.setMakeUpRepCash2(YssD.mul(rs.getDouble("FHPReplaceCash"),DataDirection));
						book.setCanMkUpRepCash2(YssD.mul(rs.getDouble("FHCPReplaceCash"),DataDirection));
						book.setExRate2(rs.getDouble("FSupplyRate"));
						book.setRemaindAmount(rs.getDouble("FRemaindAmount"));
					}
					continue;
				}
				//-------第三次补票数据------------
				if(sNum.equalsIgnoreCase(rs.getString("supplyNum"))&&rs.getString("FDataMark").equalsIgnoreCase("0")&&paramSet.getDealDayNum()>1){
					if(mapBookData.containsKey(rs.getString("supplyNum"))){
						book = (StandingBookBean) mapBookData.get(rs.getString("supplyNum"));
						book.setExRightDate(rs.getDate("FExRightDate"));
						book.setSumAmount(YssD.mul(rs.getDouble("FSumAmount"),DataDirection));
						book.setRealAmount(YssD.mul(rs.getDouble("FRealAmount"),DataDirection));
						book.setTotalInterest(YssD.mul(rs.getDouble("FInterest"),DataDirection));
						book.setWarrantCost(YssD.mul(rs.getDouble("FWarrantCost"),DataDirection));
						book.setBBInterest(YssD.mul(rs.getDouble("FBBInterest"),DataDirection));
						book.setBBWarrantCost(YssD.mul(rs.getDouble("FBBWarrantCost"),DataDirection));
						book.setRightRate(rs.getDouble("FRightRate"));
						
						
						book.setMakeUpDate3(rs.getDate("FMakeUpDate"));
						book.setMakeUpAmount3(YssD.mul(rs.getDouble("FMakeUpAmount"),DataDirection));
						book.setMakeUpUnitCost3(rs.getDouble("FSupplyUnitCost"));
						book.setoMakeUpCost3(YssD.mul(rs.getDouble("FOMakeUpCost"),DataDirection));
						book.sethMakeUpCost3(YssD.mul(rs.getDouble("FHMakeUpCost"),DataDirection));
						book.setMakeUpRepCash3(YssD.mul(rs.getDouble("FHPReplaceCash"),DataDirection));
						book.setCanMkUpRepCash3(YssD.mul(rs.getDouble("FHCPReplaceCash"),DataDirection));
						book.setExRate3(rs.getDouble("FSupplyRate"));
						book.setRemaindAmount(rs.getDouble("FRemaindAmount"));
					}
					continue;
				}
				
				//-------第四次补票数据------------
				if(sNum.equalsIgnoreCase(rs.getString("supplyNum"))&&rs.getString("FDataMark").equalsIgnoreCase("0")&&paramSet.getDealDayNum()>2){
					if(mapBookData.containsKey(rs.getString("supplyNum"))){
						book = (StandingBookBean) mapBookData.get(rs.getString("supplyNum"));
						book.setExRightDate(rs.getDate("FExRightDate"));
						book.setSumAmount(YssD.mul(rs.getDouble("FSumAmount"),DataDirection));
						book.setRealAmount(YssD.mul(rs.getDouble("FRealAmount"),DataDirection));
						book.setTotalInterest(YssD.mul(rs.getDouble("FInterest"),DataDirection));
						book.setWarrantCost(YssD.mul(rs.getDouble("FWarrantCost"),DataDirection));
						book.setBBInterest(YssD.mul(rs.getDouble("FBBInterest"),DataDirection));
						book.setBBWarrantCost(YssD.mul(rs.getDouble("FBBWarrantCost"),DataDirection));
						book.setRightRate(rs.getDouble("FRightRate"));
						
						book.setMakeUpDate4(rs.getDate("FMakeUpDate"));
						book.setMakeUpAmount4(YssD.mul(rs.getDouble("FMakeUpAmount"),DataDirection));
						book.setMakeUpUnitCost4(rs.getDouble("FSupplyUnitCost"));
						book.setoMakeUpCost4(YssD.mul(rs.getDouble("FOMakeUpCost"),DataDirection));
						book.sethMakeUpCost4(YssD.mul(rs.getDouble("FHMakeUpCost"),DataDirection));
						book.setMakeUpRepCash4(YssD.mul(rs.getDouble("FHPReplaceCash"),DataDirection));
						book.setCanMkUpRepCash4(YssD.mul(rs.getDouble("FHCPReplaceCash"),DataDirection));
						book.setExRate4(rs.getDouble("FSupplyRate"));
						book.setRemaindAmount(rs.getDouble("FRemaindAmount"));
					}
					continue;
				}
				
				//-------第五次补票数据------------
				if(sNum.equalsIgnoreCase(rs.getString("supplyNum"))&&rs.getString("FDataMark").equalsIgnoreCase("0")&&paramSet.getDealDayNum()>3){
					if(mapBookData.containsKey(rs.getString("supplyNum"))){
						
						book = (StandingBookBean) mapBookData.get(rs.getString("supplyNum"));
						book.setExRightDate(rs.getDate("FExRightDate"));
						book.setSumAmount(YssD.mul(rs.getDouble("FSumAmount"),DataDirection));
						book.setRealAmount(YssD.mul(rs.getDouble("FRealAmount"),DataDirection));
						book.setTotalInterest(YssD.mul(rs.getDouble("FInterest"),DataDirection));
						book.setWarrantCost(YssD.mul(rs.getDouble("FWarrantCost"),DataDirection));
						book.setBBInterest(YssD.mul(rs.getDouble("FBBInterest"),DataDirection));
						book.setBBWarrantCost(YssD.mul(rs.getDouble("FBBWarrantCost"),DataDirection));
						book.setRightRate(rs.getDouble("FRightRate"));

						book.setMakeUpDate5(rs.getDate("FMakeUpDate"));
						book.setMakeUpAmount5(YssD.mul(rs.getDouble("FMakeUpAmount"),DataDirection));
						book.setMakeUpUnitCost5(rs.getDouble("FSupplyUnitCost"));
						book.setoMakeUpCost5(YssD.mul(rs.getDouble("FOMakeUpCost"),DataDirection));
						book.sethMakeUpCost5(YssD.mul(rs.getDouble("FHMakeUpCost"),DataDirection));
						book.setMakeUpRepCash5(YssD.mul(rs.getDouble("FHPReplaceCash"),DataDirection));
						book.setCanMkUpRepCash5(YssD.mul(rs.getDouble("FHCPReplaceCash"),DataDirection));
						book.setExRate5(rs.getDouble("FSupplyRate"));
						book.setRemaindAmount(rs.getDouble("FRemaindAmount"));
					}
					continue;
				}
				//------------强制处理数据----------
				if(sNum.equalsIgnoreCase(rs.getString("supplyNum"))&&rs.getString("FDataMark").equalsIgnoreCase("1")){
					if(mapBookData.containsKey(rs.getString("supplyNum"))){
						book = (StandingBookBean) mapBookData.get(rs.getString("supplyNum"));
						book.setMustMkUpDate(rs.getDate("FMakeUpDate"));//强制处理日期
						book.setMustMkUpAmount(YssD.mul(rs.getDouble("FMakeUpAmount"),DataDirection));//强制处理数量
						book.setMustMkUpUnitCost(rs.getDouble("FSupplyUnitCost"));//强制处理单位成本
						book.setoMustMkUpCost(YssD.mul(rs.getDouble("FOMakeUpCost"),DataDirection));//强制处理总成本原币
						book.sethMustMkUpCost(YssD.mul(rs.getDouble("FHMakeUpCost"),DataDirection));//强制处理总成本本币
						book.setMustMkUpRepCash(YssD.mul(rs.getDouble("FHPReplaceCash"),DataDirection));//强制处理可退替代款原币
						book.setMustCMkUpRepCash(YssD.mul(rs.getDouble("FHCPReplaceCash"),DataDirection));//强制处理可退替代款本币
						book.setMustExRate(rs.getDouble("FSupplyRate"));//强制处理汇率
						book.setRemaindAmount(rs.getDouble("FRemaindAmount"));//剩余数量
					}
					continue;
				}
				book.setRemaindAmount(YssD.mul(rs.getDouble("FRemaindAmount"),DataDirection));//剩余数量
				book.setSumReturn(YssD.mul(rs.getDouble("FHCRefundSum"),DataDirection));//应退合计
				book.setRefundDate(rs.getDate("FRefundDate"));//退款日期
				book.setExchangeRate(rs.getDouble("FSHRate"));//换汇日期
				
				book.setGradeType1(book.getTradeNum());//排序编号1
				book.setGradeType2(book.getRateType());//排序编号2
				book.setGradeType3(book.getSecurityCode());//排序编号3
				book.setOrderCode(book.getGradeType1()+"##"+book.getGradeType2()+"##"+rs.getString("FMarkType")+"##"+book.getGradeType3());//排序编号
				book.setMarkType(rs.getString("FMarkType"));//标志类型 ：实时time或者钆差difference
				book.setTradeNum(rs.getString("FTradeNum"));//成交编号
				sNum = rs.getString("supplyNum");//申请编号
				
				standBookData.add(book);//保存台账数据到集合中
				mapBookData.put(sNum,book);//保存台账数据到hash表中
			}

			doRateTypeManage(yesDate,dDate);//此方法做汇率类型拆分数据
		}catch (Exception e) {
			throw new YssException("处理明细数据和明细关联数据汇总到台账bean中保存，并放到集合中出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 此方法汇总明细表和明细关联表数据插入到台帐表中
	 * @param dDate
	 * @throws YssException
	 */
	private void insertStandingBook(Date dDate) throws YssException{
		StringBuffer buff = null;//拼接SQL语句
		Connection conn =null;//数据库连接
		boolean bTrans = true;//事物控制标识
		PreparedStatement pst = null;//预处理
		String sRowStr ="";
		Date yesDate = null;//操作日当天的钱一个工作日
		EachExchangeHolidays holiday = null;//节假日代码
		StandingBookBean book = null;//台账实体bean
		ResultSet rs =null;//结果集
		HashMap changeRateData = new HashMap();//保存换汇汇率
		HashMap changeRateDate = new HashMap();//保存换汇日期
		double dbSumRefund = 0;//应退合计
		double dbMarkup1 = 0;//第一次补票的应退款
		double dbMarkup2 = 0;//第二次补票的应退款
		double dbMustup = 0;//强制处理的应退款
		double dbBuPiao = 0;//补票和强制处理的应退合计
		double dbFenHong = 0;//分红的应退合计
		double dbQuanzheng = 0;//权证的应退合计
		long sNum=0;//为了产生的编号不重复
		String strNumDate = ""; //保存交易编号
		try{
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			//拼接参数：节假日代码+当天的偏离天数+操作日期
			sRowStr = paramSet.getSHolidayCode() + "\t" + -1 + "\t" + YssFun.formatDate(dDate);
			
			holiday.parseRowStr(sRowStr);//解析数据
			yesDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//获取当天的最近一个工作日
			
			buff = new StringBuffer(500);
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);//设置为事物手动提交
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("tb_etf_standingbook"));//锁定表
			//根据组合代码，申赎日期删除台账数据
			buff.append(" delete from ").append(pub.yssGetTableName("tb_etf_standingbook"));
			buff.append(" where FportCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and FBuyDate between ").append(dbl.sqlDate(yesDate)).append(" and ").append(dbl.sqlDate(dDate));
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			//获取当天的实际汇率
			buff.append(" select * from ").append(pub.yssGetTableName("tb_etf_bookexratedata")).append(" where FCheckState = 1 and FExRateDate =").append(dbl.sqlDate(dDate));
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			String sKey="";
			while(rs.next()){
				if(rs.getString("FBookType").equalsIgnoreCase("B")){
					sKey = rs.getString("FPortCode")+"\t"+rs.getString("FBookType") + "\t" + rs.getDate("FBuyDate") + "\t" + "T+1";
				}else{//对于赎回的实际汇率都是t+4的实际汇率
					sKey = rs.getString("FPortCode")+"\t"+rs.getString("FBookType") + "\t" + rs.getDate("FBuyDate") + "\t" + "T+4";
				}
				//往hash表中赋值
				changeRateData.put(sKey,new java.lang.Double(rs.getDouble("FExRateValue")));
				changeRateDate.put(sKey,rs.getDate("FExRateDate"));
			}
			
			buff.append(" insert into ").append(pub.yssGetTableName("tb_etf_standingbook"));
			buff.append("(FNum,FBuyDate,FBs,FPortCode,FSecurityCode,FStockHolderCode,FBrokerCode,FSeatCode,");
			buff.append(" FMakeUpAmount,FUnitCost,FReplaceCash,FCanReplaceCash,FExRightDate,FSumAmount,FRealAmount,FTotalInterest,");
			buff.append(" FWarrantCost,FBBInterest,FBBWarrantCost,FRightRate,FMakeUpDate1,FMakeUpAmount1,FMakeUpUnitCost1,FOMakeUpCost1,FHMakeUpCost1,");
			buff.append(" FMakeUpRepCash1,FCanMkUpRepCash1,FMakeUpDate2,FMakeUpAmount2,FMakeUpUnitCost2,FOMakeUpCost2,FHMakeUpCost2,FMakeUpRepCash2,");
			buff.append(" FCanMkUpRepCash2,FMakeUpDate3,FMakeUpAmount3,FMakeUpUnitCost3,FOMakeUpCost3,FHMakeUpCost3,FMakeUpRepCash3,FCanMkUpRepCash3,");
			buff.append(" FMakeUpDate4,FMakeUpAmount4,FMakeUpUnitCost4,FOMakeUpCost4,FHMakeUpCost4,FMakeUpRepCash4,FCanMkUpRepCash4,FMakeUpDate5,");
			buff.append(" FMakeUpAmount5,FMakeUpUnitCost5,FOMakeUpCost5,FHMakeUpCost5,FMakeUpRepCash5,FCanMkUpRepCash5,FMustMkUpDate,FMustMkUpAmount,");
			buff.append(" FMustMkUpUnitCost,FOMustMkUpCost,FHMustMkUpCost,FMustMkUpRepCash,FMustCMkUpRepCash,FRemaindAmount,FSumReturn,FRefundDate,");
			buff.append(" FExchangeRate,FOrderCode,FGradeType1,FGradeType2,FGradeType3,FExRate1,FExRate2,FExRate3,");
			buff.append(" FExRate4,FExRate5,FMustExRate,FFactExRate,FExRateDate,FMarkType,FRateType,FTradeNum,FCreator,FCreateTime)"); 
			buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");//35
			buff.append(" ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");//40
			buff.append(" ?,?,?,?,?,?,?,?)");
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			

			//--------------------拼接交易编号---------------------
			strNumDate = YssFun.formatDatetime(dDate).substring(0, 8);
            strNumDate = strNumDate +
                dbFun.getNextInnerCode(pub.yssGetTableName("tb_etf_standingbook"),
                                       dbl.sqlRight("FNUM", 6), "000000",
                                       " where FNum like 'T" + strNumDate + "%'", 1);
            strNumDate = "T" + strNumDate;
            String s = strNumDate.substring(9, strNumDate.length());
            sNum = Long.parseLong(s);
            //--------------------------------end--------------------------//
			
			for(int i = 0;i < standBookData.size(); i++){//循环保存台账数据的集合
				//--------------------拼接交易编号---------------------
                sNum++;
                String tmp = "";
                for (int j = 0; j < s.length() - String.valueOf(sNum).length(); j++) {
                    tmp += "0";
                }
                strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
                if(Double.parseDouble(strNumDate.substring(9,strNumDate.length()))>Double.parseDouble(sMaxTradeNum.substring(9,sMaxTradeNum.length()))){
					sMaxTradeNum = strNumDate;
				}
                // ------------------------end--------------------------//
				book = (StandingBookBean) standBookData.get(i);
				//以下为预处理赋值
				pst.setString(1,strNumDate);
				pst.setDate(2,YssFun.toSqlDate(book.getBuyDate()));
				pst.setString(3,book.getBs());
				pst.setString(4,book.getPortCode());
				pst.setString(5,book.getSecurityCode());
				pst.setString(6,book.getStockHolderCode());
				pst.setString(7,book.getBrokerCode());
				pst.setString(8,book.getSeatCode());
				pst.setDouble(9,book.getMakeUpAmount());
				pst.setDouble(10,book.getUnitCost());
				pst.setDouble(11,book.getReplaceCash());
				pst.setDouble(12,book.getCanReplaceCash());
				pst.setDate(13,YssFun.toSqlDate(book.getExRightDate()!=null?book.getExRightDate():YssFun.toDate("9998-12-31")));
				pst.setDouble(14,book.getSumAmount());
				pst.setDouble(15,book.getRealAmount());
				pst.setDouble(16,book.getTotalInterest());
				pst.setDouble(17,book.getWarrantCost());
				pst.setDouble(18,book.getBBInterest());
				pst.setDouble(19,book.getBBWarrantCost());
				pst.setDouble(20,book.getRightRate());
				
				pst.setDate(21,YssFun.toSqlDate(book.getMakeUpDate1()!=null?book.getMakeUpDate1():YssFun.toDate("9998-12-31")));
				pst.setDouble(22,book.getMakeUpAmount1());
				pst.setDouble(23,book.getMakeUpUnitCost1());
				pst.setDouble(24,book.getoMakeUpCost1());
				pst.setDouble(25,book.gethMakeUpCost1());
				pst.setDouble(26,book.getMakeUpRepCash1());
				pst.setDouble(27,book.getCanMkUpRepCash1());
				
				pst.setDate(28,YssFun.toSqlDate(book.getMakeUpDate2()!=null?book.getMakeUpDate2():YssFun.toDate("9998-12-31")));
				pst.setDouble(29,book.getMakeUpAmount2());
				pst.setDouble(30,book.getMakeUpUnitCost2());
				pst.setDouble(31,book.getoMakeUpCost2());
				pst.setDouble(32,book.gethMakeUpCost2());
				pst.setDouble(33,book.getMakeUpRepCash2());
				pst.setDouble(34,book.getCanMkUpRepCash2());
				
				pst.setDate(35,YssFun.toSqlDate(book.getMakeUpDate3()!=null?book.getMakeUpDate3():YssFun.toDate("9998-12-31")));
				pst.setDouble(36,book.getMakeUpAmount3());
				pst.setDouble(37,book.getMakeUpUnitCost3());
				pst.setDouble(38,book.getoMakeUpCost3());
				pst.setDouble(39,book.gethMakeUpCost3());
				pst.setDouble(40,book.getMakeUpRepCash3());
				pst.setDouble(41,book.getCanMkUpRepCash3());
				
				pst.setDate(42,YssFun.toSqlDate(book.getMakeUpDate4()!=null?book.getMakeUpDate4():YssFun.toDate("9998-12-31")));
				pst.setDouble(43,book.getMakeUpAmount4());
				pst.setDouble(44,book.getMakeUpUnitCost4());
				pst.setDouble(45,book.getoMakeUpCost4());
				pst.setDouble(46,book.gethMakeUpCost4());
				pst.setDouble(47,book.getMakeUpRepCash4());
				pst.setDouble(48,book.getCanMkUpRepCash4());
				
				pst.setDate(49,YssFun.toSqlDate(book.getMakeUpDate5()!=null?book.getMakeUpDate5():YssFun.toDate("9998-12-31")));
				pst.setDouble(50,book.getMakeUpAmount5());
				pst.setDouble(51,book.getMakeUpUnitCost5());
				pst.setDouble(52,book.getoMakeUpCost5());
				pst.setDouble(53,book.gethMakeUpCost5());
				pst.setDouble(54,book.getMakeUpRepCash5());
				pst.setDouble(55,book.getCanMkUpRepCash5());
				
				pst.setDate(56,YssFun.toSqlDate(book.getMustMkUpDate()!=null?book.getMustMkUpDate():YssFun.toDate("9998-12-31")));
				pst.setDouble(57,book.getMustMkUpAmount());
				pst.setDouble(58,book.getMustMkUpUnitCost());
				pst.setDouble(59,book.getoMustMkUpCost());
				pst.setDouble(60,book.gethMustMkUpCost());
				pst.setDouble(61,book.getMustMkUpRepCash());
				pst.setDouble(62,book.getMustCMkUpRepCash());
				
				pst.setDouble(63,book.getRemaindAmount());
				//计算应退合计
				if(book.getBs().equalsIgnoreCase("S")&&book.getRateType().equalsIgnoreCase("T+1")){//对于汇率类型为t+1的赎回数据均用申购的实际汇率处理
					sKey = book.getPortCode()+"\t"+ "B" +"\t"+YssFun.toSqlDate(book.getBuyDate()) + "\t" + book.getRateType();
				}else{
					sKey = book.getPortCode()+"\t"+ book.getBs() +"\t"+YssFun.toSqlDate(book.getBuyDate()) + "\t" + book.getRateType();
				}
				if(changeRateData.containsKey(sKey)){
					if(book.getBs().equalsIgnoreCase("B")){
						//实际应退合计 = 
						//替代金额本币 *（第一次补票数量/替代数量）- 补票总成本 * 业务日汇率 + 
						//（替代金额 - 总派息 * 业务日汇率 - 权证价值 * 业务日汇率）* （第二次补票数量 /（替代数量 + 权益总数量）） - 第二次补票总成本 * 业务日汇率 +
						//（替代金额 - 总派息 * 业务日汇率 - 权证价值 * 业务日汇率）* （强制处理数量 /（替代数量 + 权益总数量）） - 强制处理数量 * 业务日汇率 +
						//第一次卖出总成本原币*业务日汇率 + 第二次买出总成本原币*业务日汇率+强制处理总成本原币*业务日汇率
						dbMarkup1 = 
							YssD.sub(
							YssD.round(
									YssD.mul(
											book.getReplaceCash(), 
											YssD.div(
													book.getMakeUpAmount1(), 
													book.getMakeUpAmount())),
									2),
							YssD.round(
									YssD.mul(
											book.getoMakeUpCost1(), 
											Double.parseDouble(changeRateData.get(sKey).toString())),
									2));
						dbMarkup2 = 
							YssD.sub(
									YssD.round(
											YssD.mul(
												YssD.sub(
														book.getReplaceCash(),
													YssD.round(
															YssD.mul(
																	book.getTotalInterest(), 
																	Double.parseDouble(changeRateData.get(sKey).toString())), 
															2),
													YssD.round(		
															YssD.mul(
																	book.getWarrantCost(),
																	Double.parseDouble(changeRateData.get(sKey).toString())), 
															2)),
												YssD.div(
														book.getMakeUpAmount2(), 
														YssD.add(
																book.getMakeUpAmount(), 
																book.getSumAmount()))), 
											2),
									YssD.round(
											YssD.mul(
													book.getoMakeUpCost2(), 
													Double.parseDouble(changeRateData.get(sKey).toString())),
											2));
						dbMustup = 
							YssD.sub(
									YssD.round(
											YssD.mul(
												YssD.sub(
													book.getReplaceCash(),
													YssD.round(
															YssD.mul(
																	book.getTotalInterest(), 
																	Double.parseDouble(changeRateData.get(sKey).toString())), 
																2),
														YssD.round(		
																YssD.mul(
																		book.getWarrantCost(),
																		Double.parseDouble(changeRateData.get(sKey).toString())), 
																2)),
													YssD.div(
															book.getMustMkUpAmount(), 
															YssD.add(
																	book.getMakeUpAmount(), 
																	book.getSumAmount()))), 
												2),
										YssD.round(
												YssD.mul(
														book.getoMustMkUpCost(), 
														Double.parseDouble(changeRateData.get(sKey).toString())),
												2));
					
						dbSumRefund = YssD.add(dbMarkup1, dbMarkup2, dbMustup);
						pst.setDouble(64,dbSumRefund);
					}else{
						//实际应退合计 = 
						//总派息*第二次卖出数量/替代数量*业务日汇率+
						//总派息*强制处理数量/替代数量*业务日汇率+
						dbBuPiao = 
							YssD.add(
									YssD.round(
											YssD.mul(
													YssD.mul(
															book.getoMakeUpCost1(), 
															-1),
													Double.parseDouble(changeRateData.get(sKey).toString())), 
											2), 
									YssD.round(
											YssD.mul(
													YssD.mul(
															book.getoMakeUpCost2(),
															-1),
													Double.parseDouble(changeRateData.get(sKey).toString())),
											2),
									YssD.round(
											YssD.mul(
													YssD.mul(
															book.getoMustMkUpCost(), 
															-1), 
													Double.parseDouble(changeRateData.get(sKey).toString())), 
											2));
						
						dbFenHong =
							YssD.add(
									YssD.round(
											YssD.mul(
													YssD.mul(
															book.getTotalInterest(), 
															-1), 
													YssD.div(//第二次补票数量和替代数量都有方向所以就不需要再乘一个方向了
															book.getMakeUpAmount2(), 
															book.getMakeUpAmount()), 
													Double.parseDouble(changeRateData.get(sKey).toString())), 
											2), 
									YssD.round(
											YssD.mul(
													YssD.mul(
															book.getTotalInterest(), 
															-1), 
													YssD.div(//强制处理数量和替代数量都有方向所以就不需要再乘一个方向了
															book.getMustMkUpAmount(), 
															book.getMakeUpAmount()), 
													Double.parseDouble(changeRateData.get(sKey).toString())), 
											2));
						dbQuanzheng = 
							YssD.add(
									YssD.round(
											YssD.mul(
													YssD.mul(
															book.getWarrantCost(), 
															-1), 
													YssD.div(//第二次补票数量和替代数量都有方向所以就不需要再乘一个方向了
															book.getMakeUpAmount2(), 
															book.getMakeUpAmount()), 
													Double.parseDouble(changeRateData.get(sKey).toString())), 
											2), 
									YssD.round(
											YssD.mul(
													YssD.mul(
															book.getWarrantCost(), 
															-1), 
													YssD.div(//强制处理数量和替代数量都有方向所以就不需要再乘一个方向了
															book.getMustMkUpAmount(), 
															book.getMakeUpAmount()), 
													Double.parseDouble(changeRateData.get(sKey).toString())), 
											2));
						dbSumRefund = YssD.add(dbBuPiao, dbFenHong, dbQuanzheng);
						pst.setDouble(64,YssD.mul(dbSumRefund, -1));
					}
				}else{
					pst.setDouble(64,book.getSumReturn());
				}
				pst.setDate(65,YssFun.toSqlDate(book.getRefundDate()!=null?book.getRefundDate():YssFun.toDate("9998-12-31")));
				pst.setDouble(66,book.getExchangeRate());
				pst.setString(67,book.getOrderCode());
				pst.setString(68,book.getGradeType1());
				pst.setString(69,book.getGradeType2());
				pst.setString(70,book.getGradeType3());
				pst.setDouble(71,book.getExRate1());
				pst.setDouble(72,book.getExRate2());
				pst.setDouble(73,book.getExRate3());
				pst.setDouble(74,book.getExRate4());
				pst.setDouble(75,book.getExRate5());
				pst.setDouble(76,book.getMustExRate());
				if(changeRateData.containsKey(sKey)){//实际汇率
					pst.setDouble(77, java.lang.Double.parseDouble(changeRateData.get(sKey).toString()));
				}else{
					pst.setDouble(77,book.getFactExRate());
				}
				if(changeRateDate.containsKey(sKey)){//换汇日期
					pst.setDate(78, YssFun.toSqlDate(changeRateDate.get(sKey).toString()));
				}else{
					pst.setDate(78,YssFun.toSqlDate(book.getExRateDate()!=null?book.getExRateDate():YssFun.toDate("9998-12-31")));
				}
				
				pst.setString(79,book.getMarkType());
				pst.setString(80,book.getRateType());
				pst.setString(81,book.getTradeNum());
				pst.setString(82,pub.getUserCode());
				pst.setString(83,YssFun.formatDate(new Date()));
				pst.addBatch();//增加批处理
			}
			pst.executeBatch();//执行批处理
			
			if(standBookDivideRateData.size()>0){
				insertBookDivideRateData(pst,changeRateData,changeRateDate);//插入台账汇率拆分的数据
			}
			
			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置为自动提交事物
			bTrans = false;
			
			setStandingBookData(dDate);//处理台账汇总数据
			
		}catch (Exception e) {
			throw new YssException("汇总明细表和明细关联表数据插入到台帐表中出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeStatementFinal(pst);
			dbl.closeResultSetFinal(rs);
		}
	}
	/**
	 * 插入台账汇率拆分的数据
	 * @param pst
	 */
	private void insertBookDivideRateData(PreparedStatement pst,HashMap changeRateData,HashMap changeRateDate) throws YssException{
		StandingBookBean book = null;//台账实体bean
		double dbSumRefund = 0;//应退合计
		double dbMarkup1 = 0;//第一次补票的应退款
		double dbMarkup2 = 0;//第二次补票的应退款
		double dbMustup = 0;//强制处理的应退款
		double dbBuPiao = 0;//补票和强制处理的应退合计
		double dbFenHong = 0;//分红的应退合计
		double dbQuanzheng = 0;//权证的应退合计
		String sKey="";
		long sNum = 0;//用于拼接编号
		String strNumDate = "";//申请编号
		try{
			String s = sMaxTradeNum.substring(9, sMaxTradeNum.length());
			sNum = Long.parseLong(s);
			for(int i =0;i < standBookDivideRateData.size(); i++){//循环汇率拆分为t+4的数据
				//--------------------拼接交易编号---------------------
				sNum++;
				String tmp = "";
				for (int t = 0; t < s.length()- String.valueOf(sNum).length(); t++) {
					tmp += "0";
				}
				strNumDate = sMaxTradeNum.substring(0, 9) + tmp + sNum;
				// ------------------------end--------------------------//
				book = (StandingBookBean) standBookDivideRateData.get(i);
				pst.setString(1,strNumDate);
				pst.setDate(2,YssFun.toSqlDate(book.getBuyDate()));
				pst.setString(3,book.getBs());
				pst.setString(4,book.getPortCode());
				pst.setString(5,book.getSecurityCode());
				pst.setString(6,book.getStockHolderCode());
				pst.setString(7,book.getBrokerCode());
				pst.setString(8,book.getSeatCode());
				pst.setDouble(9,book.getMakeUpAmount());
				pst.setDouble(10,book.getUnitCost());
				pst.setDouble(11,book.getReplaceCash());
				pst.setDouble(12,book.getCanReplaceCash());
				pst.setDate(13,YssFun.toSqlDate(book.getExRightDate()!=null?book.getExRightDate():YssFun.toDate("9998-12-31")));
				pst.setDouble(14,book.getSumAmount());
				pst.setDouble(15,book.getRealAmount());
				pst.setDouble(16,book.getTotalInterest());
				pst.setDouble(17,book.getWarrantCost());
				pst.setDouble(18,book.getBBInterest());
				pst.setDouble(19,book.getBBWarrantCost());
				pst.setDouble(20,book.getRightRate());
				
				pst.setDate(21,YssFun.toSqlDate(book.getMakeUpDate1()!=null?book.getMakeUpDate1():YssFun.toDate("9998-12-31")));
				pst.setDouble(22,book.getMakeUpAmount1());
				pst.setDouble(23,book.getMakeUpUnitCost1());
				pst.setDouble(24,book.getoMakeUpCost1());
				pst.setDouble(25,book.gethMakeUpCost1());
				pst.setDouble(26,book.getMakeUpRepCash1());
				pst.setDouble(27,book.getCanMkUpRepCash1());
				
				pst.setDate(28,YssFun.toSqlDate(book.getMakeUpDate2()!=null?book.getMakeUpDate2():YssFun.toDate("9998-12-31")));
				pst.setDouble(29,book.getMakeUpAmount2());
				pst.setDouble(30,book.getMakeUpUnitCost2());
				pst.setDouble(31,book.getoMakeUpCost2());
				pst.setDouble(32,book.gethMakeUpCost2());
				pst.setDouble(33,book.getMakeUpRepCash2());
				pst.setDouble(34,book.getCanMkUpRepCash2());
				
				pst.setDate(35,YssFun.toSqlDate(book.getMakeUpDate3()!=null?book.getMakeUpDate3():YssFun.toDate("9998-12-31")));
				pst.setDouble(36,book.getMakeUpAmount3());
				pst.setDouble(37,book.getMakeUpUnitCost3());
				pst.setDouble(38,book.getoMakeUpCost3());
				pst.setDouble(39,book.gethMakeUpCost3());
				pst.setDouble(40,book.getMakeUpRepCash3());
				pst.setDouble(41,book.getCanMkUpRepCash3());
				
				pst.setDate(42,YssFun.toSqlDate(book.getMakeUpDate4()!=null?book.getMakeUpDate4():YssFun.toDate("9998-12-31")));
				pst.setDouble(43,book.getMakeUpAmount4());
				pst.setDouble(44,book.getMakeUpUnitCost4());
				pst.setDouble(45,book.getoMakeUpCost4());
				pst.setDouble(46,book.gethMakeUpCost4());
				pst.setDouble(47,book.getMakeUpRepCash4());
				pst.setDouble(48,book.getCanMkUpRepCash4());
				
				pst.setDate(49,YssFun.toSqlDate(book.getMakeUpDate5()!=null?book.getMakeUpDate5():YssFun.toDate("9998-12-31")));
				pst.setDouble(50,book.getMakeUpAmount5());
				pst.setDouble(51,book.getMakeUpUnitCost5());
				pst.setDouble(52,book.getoMakeUpCost5());
				pst.setDouble(53,book.gethMakeUpCost5());
				pst.setDouble(54,book.getMakeUpRepCash5());
				pst.setDouble(55,book.getCanMkUpRepCash5());
				
				pst.setDate(56,YssFun.toSqlDate(book.getMustMkUpDate()!=null?book.getMustMkUpDate():YssFun.toDate("9998-12-31")));
				pst.setDouble(57,book.getMustMkUpAmount());
				pst.setDouble(58,book.getMustMkUpUnitCost());
				pst.setDouble(59,book.getoMustMkUpCost());
				pst.setDouble(60,book.gethMustMkUpCost());
				pst.setDouble(61,book.getMustMkUpRepCash());
				pst.setDouble(62,book.getMustCMkUpRepCash());
				
				pst.setDouble(63,book.getRemaindAmount());
				//计算应退合计
				sKey = book.getPortCode()+"\t"+book.getBs()+"\t"+YssFun.toSqlDate(book.getBuyDate());
				if(changeRateData.containsKey(sKey)){
					if(book.getBs().equalsIgnoreCase("B")){
						//实际应退合计 = 
						//替代金额本币 *（第一次补票数量/替代数量）- 补票总成本 * 业务日汇率 + 
						//（替代金额 - 总派息 * 业务日汇率 - 权证价值 * 业务日汇率）* （第二次补票数量 /（替代数量 + 权益总数量）） - 第二次补票总成本 * 业务日汇率 +
						//（替代金额 - 总派息 * 业务日汇率 - 权证价值 * 业务日汇率）* （强制处理数量 /（替代数量 + 权益总数量）） - 强制处理数量 * 业务日汇率 +
						//第一次卖出总成本原币*业务日汇率 + 第二次买出总成本原币*业务日汇率+强制处理总成本原币*业务日汇率
						dbMarkup1 = 
							YssD.sub(
							YssD.round(
									YssD.mul(
											book.getReplaceCash(), 
											YssD.div(
													book.getMakeUpAmount1(), 
													book.getMakeUpAmount())),
									2),
							YssD.round(
									YssD.mul(
											book.getoMakeUpCost1(), 
											Double.parseDouble(changeRateData.get(sKey).toString())),
									2));
						dbMarkup2 = 
							YssD.sub(
									YssD.round(
											YssD.mul(
												YssD.sub(
														book.getReplaceCash(),
													YssD.round(
															YssD.mul(
																	book.getTotalInterest(), 
																	Double.parseDouble(changeRateData.get(sKey).toString())), 
															2),
													YssD.round(		
															YssD.mul(
																	book.getWarrantCost(),
																	Double.parseDouble(changeRateData.get(sKey).toString())), 
															2)),
												YssD.div(
														book.getMakeUpAmount2(), 
														YssD.add(
																book.getMakeUpAmount(), 
																book.getSumAmount()))), 
											2),
									YssD.round(
											YssD.mul(
													book.getoMakeUpCost2(), 
													Double.parseDouble(changeRateData.get(sKey).toString())),
											2));
						dbMustup = 
							YssD.sub(
									YssD.round(
											YssD.mul(
												YssD.sub(
													book.getReplaceCash(),
													YssD.round(
															YssD.mul(
																	book.getTotalInterest(), 
																	Double.parseDouble(changeRateData.get(sKey).toString())), 
																2),
														YssD.round(		
																YssD.mul(
																		book.getWarrantCost(),
																		Double.parseDouble(changeRateData.get(sKey).toString())), 
																2)),
													YssD.div(
															book.getMustMkUpAmount(), 
															YssD.add(
																	book.getMakeUpAmount(), 
																	book.getSumAmount()))), 
												2),
										YssD.round(
												YssD.mul(
														book.getoMustMkUpCost(), 
														Double.parseDouble(changeRateData.get(sKey).toString())),
												2));
					
						dbSumRefund = YssD.add(dbMarkup1, dbMarkup2, dbMustup);
						pst.setDouble(64,dbSumRefund);
					}else{
						//实际应退合计 = 
						//总派息*第二次卖出数量/替代数量*业务日汇率+
						//总派息*强制处理数量/替代数量*业务日汇率+
						dbBuPiao = 
							YssD.add(
									YssD.round(
											YssD.mul(
													YssD.mul(
															book.getoMakeUpCost1(), 
															-1),
													Double.parseDouble(changeRateData.get(sKey).toString())), 
											2), 
									YssD.round(
											YssD.mul(
													YssD.mul(
															book.getoMakeUpCost2(),
															-1),
													Double.parseDouble(changeRateData.get(sKey).toString())),
											2),
									YssD.round(
											YssD.mul(
													YssD.mul(
															book.getoMustMkUpCost(), 
															-1), 
													Double.parseDouble(changeRateData.get(sKey).toString())), 
											2));
						
						dbFenHong =
							YssD.add(
									YssD.round(
											YssD.mul(
													YssD.mul(
															book.getTotalInterest(), 
															-1), 
													YssD.div(//第二次补票数量和替代数量都有方向所以就不需要再乘一个方向了
															book.getMakeUpAmount2(), 
															book.getMakeUpAmount()), 
													Double.parseDouble(changeRateData.get(sKey).toString())), 
											2), 
									YssD.round(
											YssD.mul(
													YssD.mul(
															book.getTotalInterest(), 
															-1), 
													YssD.div(//强制处理数量和替代数量都有方向所以就不需要再乘一个方向了
															book.getMustMkUpAmount(), 
															book.getMakeUpAmount()), 
													Double.parseDouble(changeRateData.get(sKey).toString())), 
											2));
						dbQuanzheng = 
							YssD.add(
									YssD.round(
											YssD.mul(
													YssD.mul(
															book.getWarrantCost(), 
															-1), 
													YssD.div(//第二次补票数量和替代数量都有方向所以就不需要再乘一个方向了
															book.getMakeUpAmount2(), 
															book.getMakeUpAmount()), 
													Double.parseDouble(changeRateData.get(sKey).toString())), 
											2), 
									YssD.round(
											YssD.mul(
													YssD.mul(
															book.getWarrantCost(), 
															-1), 
													YssD.div(//强制处理数量和替代数量都有方向所以就不需要再乘一个方向了
															book.getMustMkUpAmount(), 
															book.getMakeUpAmount()), 
													Double.parseDouble(changeRateData.get(sKey).toString())), 
											2));
						dbSumRefund = YssD.add(dbBuPiao, dbFenHong, dbQuanzheng);
						pst.setDouble(64,dbSumRefund);
					}
				}else{
					pst.setDouble(64,book.getSumReturn());
				}
				pst.setDate(65,YssFun.toSqlDate(book.getRefundDate()!=null?book.getRefundDate():YssFun.toDate("9998-12-31")));
				pst.setDouble(66,book.getExchangeRate());
				pst.setString(67,book.getOrderCode());
				pst.setString(68,book.getGradeType1());
				pst.setString(69,book.getGradeType2());
				pst.setString(70,book.getGradeType3());
				pst.setDouble(71,book.getExRate1());
				pst.setDouble(72,book.getExRate2());
				pst.setDouble(73,book.getExRate3());
				pst.setDouble(74,book.getExRate4());
				pst.setDouble(75,book.getExRate5());
				pst.setDouble(76,book.getMustExRate());
				if(changeRateData.containsKey(sKey)){//实际汇率
					pst.setDouble(77, java.lang.Double.parseDouble(changeRateData.get(sKey).toString()));
				}else{
					pst.setDouble(77,book.getFactExRate());
				}
				if(changeRateDate.containsKey(sKey)){//换汇日期
					pst.setDate(78, YssFun.toSqlDate(changeRateDate.get(sKey).toString()));
				}else{
					pst.setDate(78,YssFun.toSqlDate(book.getExRateDate()!=null?book.getExRateDate():YssFun.toDate("9998-12-31")));
				}
				
				pst.setString(79,book.getMarkType());
				pst.setString(80,book.getRateType());
				pst.setString(81,book.getTradeNum());
				pst.setString(82,pub.getUserCode());
				pst.setString(83,YssFun.formatDate(new Date()));
				pst.addBatch();
			}
			pst.executeBatch();
		}catch (Exception e) {
			throw new YssException("插入台账汇率拆分的数据出错！",e);
		}
		
	}
	/**
	 * 此方法做汇率类型拆分数据
	 * @throws YssException
	 */
	private void doRateTypeManage(Date yesDate,Date dDate) throws YssException{
		StringBuffer buff = null;//做拼接SQL语句
		ResultSet rs =null;//结果集
		String [] portCode = null;//组合代码
		HashMap iSGOrSH=null;//保存昨天和今天是净申购还是净赎数据
		double SGSHStockList = 0;//当天申赎篮子数
		double stockList =0;//股票篮数
		HashMap divideDate=null;//保存差分数据的成交编号
		String sKey = "";//保存键值对
		String[] sDivide=null;
		double secondReplaceAmount =0;//保存最大成交编号的篮子数和当日净赎回的篮子数之差的篮子数
		long sNum = 0;
		String strNumDate = "";//保存申请编号
		double scaleT1 = 1;//T+1汇率拆分比例
		StandingBookBean book = null;//台账实体bean
		StandingBookBean divideBook = null;//拆分的台账实体bean
		int marktypeNum = 1;//标志类型，即同一个证券代码实时补票和钆差补票的个数
		double dTimeSupplyBarkets = 1;//钆差补票的篮子中，实时钆差补票的篮子数
		try{
			if(standBookDivideRateData.size()>0){//保存汇率拆分为t+4的数据的集合不为空时，先清空
				standBookDivideRateData.clear();
			}
			divideDate = new HashMap();//实例化
			iSGOrSH = doJudgeTheDayNetSGOrSH(dDate,yesDate);//赋值
			buff = new StringBuffer(500);
			portCode = this.portCodes.split(",");//拆分组合代码
			for(int i = 0;i < portCode.length; i++){//循环组合代码
				if(iSGOrSH.containsKey(portCode[i]+"\t"+yesDate)){
					SGSHStockList = Double.parseDouble((String)iSGOrSH.get(portCode[i]+"\t"+yesDate));
				}
				if(SGSHStockList < 0){
					buff.append(" select FTradeAmount * -1 as FTradeAmount,FTradeNum from ").append(pub.yssGetTableName("tb_etf_ghinterface"));
					buff.append(" where FPortCode in(").append(this.operSql.sqlCodes(portCode[i])).append(")");
					buff.append(" and FBargainDate =").append(dbl.sqlDate(yesDate));
					buff.append(" and FOperType ='2ndcode'").append(" and FMark ='B' order by FTradeNum desc");
					
					rs =dbl.openResultSet(buff.toString());
					buff.delete(0,buff.length());
					while(rs.next()){
						stockList += rs.getDouble("FTradeAmount");
//						if(SGSHStockList == stockList)
						if(YssD.sub(SGSHStockList, stockList) == 0)
						{
							divideDate.put(rs.getString("FTradeNum")+"\t"+portCode[i],"T+4"+"\t"+stockList);//说明此成交编号的数据用汇率T+4
							break;
						}else if(SGSHStockList > stockList){//说明此成交编号会拆分为t+1和t+4两种汇率类型的数据
							divideDate.put(rs.getString("FTradeNum")+"\t"+portCode[i],"T+1" + "," + "T+4" + "\t"+SGSHStockList + "\t" + stockList +"\t" + rs.getDouble("FTradeAmount")+"\t"+secondReplaceAmount);
							break;
						}else{//说明此成交编号的数据用汇率T+1
							divideDate.put(rs.getString("FTradeNum")+"\t"+portCode[i],"T+4"+ "\t" + SGSHStockList + "\t"+stockList);
							secondReplaceAmount = SGSHStockList - rs.getDouble("FTradeAmount");
						}
					}
					dbl.closeResultSetFinal(rs);
				}
			}
			String s = sMaxTradeNum.substring(9, sMaxTradeNum.length());//此代码暂时没有用
			sNum = Long.parseLong(s);
			for(int i = 0; i <standBookData.size(); i++){//循环保存台账数据的集合
				book = (StandingBookBean) standBookData.get(i);
				SGSHStockList = Double.parseDouble((String)iSGOrSH.get(book.getPortCode()+"\t"+book.getBuyDate()));
				sKey = book.getTradeNum() + "\t" + book.getPortCode();
				if(SGSHStockList<0){//当天是净赎回时，才进行汇率拆分
					if(divideDate.containsKey(sKey)&&book.getBs().equalsIgnoreCase("S")&&YssFun.dateDiff(book.getBuyDate(),dDate)!=0){
						
						buff.append(" select max(count(*)) as marktypeNum from ").append(pub.yssGetTableName("tb_etf_tradestldtl"));
						buff.append(" where fbuydate =").append(dbl.sqlDate(book.getBuyDate()));
						buff.append(" and FPortCode = ").append(dbl.sqlString(book.getPortCode()));
						buff.append(" and ftradenum =").append(dbl.sqlString(book.getTradeNum()));
						buff.append(" and FBs =").append(dbl.sqlString(book.getBs()));
						buff.append(" group by fsecuritycode,ftradenum,fportcode,fbuydate,FBs");
					
						rs = dbl.openResultSet(buff.toString());
						buff.delete(0,buff.length());
						if(rs.next()){
							marktypeNum = rs.getInt("marktypeNum");
						}
						dbl.closeResultSetFinal(rs);
						//--------------------拼接交易编号---------------------
						sNum++;
						String tmp = "";
						for (int t = 0; t < s.length()- String.valueOf(sNum).length(); t++) {
							tmp += "0";
						}
						strNumDate = sMaxTradeNum.substring(0, 9) + tmp + sNum;
						// ------------------------end--------------------------//
						if(marktypeNum == 1){//说明钆差补票里没有汇率拆分的数据
							sDivide =((String)divideDate.get(sKey)).split("\t");
							if(sDivide[0].equalsIgnoreCase("T+4")){
								book.setRateType("T+4");
							}else if(sDivide[0].equalsIgnoreCase("T+1,T+4")){//此时要拆分数据
								divideBook = new StandingBookBean();
								if(Double.parseDouble(sDivide[4])==0){//此时说明最大成交编号的篮子数就是当日需要汇率钆差的编号
									
									scaleT1 = YssD.sub(1,YssD.div(Double.parseDouble(sDivide[1]),Double.parseDouble(sDivide[2])));//T+1汇率拆分比例
									
									setDivideDataValue(divideBook,book,scaleT1,strNumDate);//为拆分的数据赋值
									
									standBookDivideRateData.add(divideBook);
								}else{
									scaleT1 = YssD.div(Double.parseDouble(sDivide[4]),Double.parseDouble(sDivide[3]));//T+1汇率拆分比例
									
									setDivideDataValue(divideBook,book,scaleT1,strNumDate);//为拆分的数据赋值
									
									standBookDivideRateData.add(divideBook);
								}
							}
						}else{//说明钆差补票的数据里有汇率拆分的数据，此时要根据净赎回篮子数和钆差补票中实时补票的篮子数进行比较拆分
							sDivide =((String)divideDate.get(sKey)).split("\t");
							
							buff.append(" select * from ").append(pub.yssGetTableName("tb_etf_tradestldtl"));
							buff.append(" where fbuydate =").append(dbl.sqlDate(book.getBuyDate()));
							buff.append(" and FPortCode = ").append(dbl.sqlString(book.getPortCode()));
							buff.append(" and ftradenum =").append(dbl.sqlString(book.getTradeNum()));
							buff.append(" and FBs =").append(dbl.sqlString(book.getBs()));
							buff.append(" and fmarktype = 'time' and fsecuritycode = ").append(dbl.sqlString(book.getSecurityCode()));
							
							rs = dbl.openResultSet(buff.toString());
							buff.delete(0,buff.length());
							
							if(rs.next()){
								dTimeSupplyBarkets = rs.getDouble("FBRAKETNUM");
							}
							dbl.closeResultSetFinal(rs);
							
							if(YssD.mul(YssD.div(SGSHStockList,paramSet.getNormScale()),-1) >= dTimeSupplyBarkets){//净赎回篮子数大于或者等于钆差补票中实时补票的篮子数时，说明这些实时补票的篮子全部是t+4的汇率
								if(book.getMarkType().equalsIgnoreCase("time")){
									book.setRateType("T+4");
								}
							}else{//此时钆差补票中用开盘价进行补票的篮子，要拆分为"t+1"和"t+4"汇率，其中拆分比例为T+1汇率拆分比例，钆到用t+4汇率的篮子里
								divideBook = new StandingBookBean();
								scaleT1 = YssD.div(YssD.sub(dTimeSupplyBarkets,YssD.mul(YssD.div(SGSHStockList,paramSet.getNormScale()),-1)),dTimeSupplyBarkets);//T+1汇率拆分比例
								setDivideDataValue(divideBook,book,scaleT1,strNumDate);//为拆分的数据赋值
								
								standBookDivideRateData.add(divideBook);
							}
						}
					}else if(!divideDate.containsKey(sKey)&&book.getBs().equalsIgnoreCase("S")&&YssFun.dateDiff(book.getBuyDate(),dDate)!=0){
						book.setRateType("T+1");
					}
				}
			}
		}catch (Exception e) {
			throw new YssException("做汇率类型拆分数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 为拆分的台账数据赋值
	 * @param divideBook
	 * @param book
	 * @param scaleT1
	 * @param strNumDate
	 * @throws YssException
	 */
	private void setDivideDataValue(StandingBookBean divideBook,StandingBookBean book,double scaleT1,String strNumDate) throws YssException{
		try{
//			----------------------设置t+4 汇率拆分的数据-----------------------//
			divideBook.setNum(strNumDate);
			divideBook.setBuyDate(book.getBuyDate());
			divideBook.setBs(book.getBs());
			divideBook.setPortCode(book.getPortCode());
			divideBook.setSecurityCode(book.getSecurityCode());
			divideBook.setStockHolderCode(book.getStockHolderCode());
			divideBook.setBrokerCode(book.getBrokerCode());
			divideBook.setSeatCode(book.getSeatCode());
			divideBook.setMakeUpAmount(YssD.round(YssD.sub(book.getMakeUpAmount(),YssD.round(YssD.mul(book.getMakeUpAmount(),scaleT1),0)),2));
			divideBook.setUnitCost(book.getUnitCost());
			divideBook.setReplaceCash(YssD.round(YssD.sub(book.getReplaceCash(),YssD.round(YssD.mul(book.getReplaceCash(),scaleT1),2)),2));
			divideBook.setCanReplaceCash(YssD.round(YssD.sub(book.getCanReplaceCash(),YssD.round(YssD.mul(book.getCanReplaceCash(),scaleT1),2)),2));
			divideBook.setExRightDate(book.getExRightDate());
			divideBook.setSumAmount(YssD.round(YssD.sub(book.getSumAmount(),YssD.round(YssD.mul(book.getSumAmount(),scaleT1),0)),2));
			divideBook.setRealAmount(YssD.round(YssD.sub(book.getRealAmount(),YssD.round(YssD.mul(book.getRealAmount(),scaleT1),0)),2));
			divideBook.setTotalInterest(YssD.round(YssD.sub(book.getTotalInterest(),YssD.round(YssD.mul(book.getTotalInterest(),scaleT1),2)),2));
			divideBook.setWarrantCost(YssD.round(YssD.sub(book.getWarrantCost(),YssD.round(YssD.mul(book.getWarrantCost(),scaleT1),2)),2));
			divideBook.setBBInterest(YssD.round(YssD.sub(book.getBBInterest(),YssD.round(YssD.mul(book.getBBInterest(),scaleT1),2)),2));
			divideBook.setBBWarrantCost(YssD.round(YssD.sub(book.getBBWarrantCost(),YssD.round(YssD.mul(book.getBBWarrantCost(),scaleT1),2)),2));
			divideBook.setRightRate(book.getRightRate());
			//第一次补票数据
			divideBook.setMakeUpDate1(book.getMakeUpDate1());
			divideBook.setMakeUpAmount1(YssD.round(YssD.sub(book.getMakeUpAmount1(),YssD.round(YssD.mul(book.getMakeUpAmount1(),scaleT1),0)),2));
			divideBook.setMakeUpUnitCost1(book.getMakeUpUnitCost1());
			divideBook.setoMakeUpCost1(YssD.round(YssD.sub(book.getoMakeUpCost1(),YssD.round(YssD.mul(book.getoMakeUpCost1(),scaleT1),2)),2));
			divideBook.sethMakeUpCost1(YssD.round(YssD.sub(book.gethMakeUpCost1(),YssD.round(YssD.mul(book.gethMakeUpCost1(),scaleT1),2)),2));
			divideBook.setMakeUpRepCash1(YssD.round(YssD.sub(book.getMakeUpRepCash1(),YssD.round(YssD.mul(book.getMakeUpRepCash1(),scaleT1),2)),2));
			divideBook.setCanMkUpRepCash1(YssD.round(YssD.sub(book.getCanMkUpRepCash1(),YssD.round(YssD.mul(book.getCanMkUpRepCash1(),scaleT1),2)),2));
			divideBook.setExRate1(book.getExRate1());
			//第二次补票数据
			if(paramSet.getDealDayNum()>0&&book.getMakeUpAmount2()!=0){
				divideBook.setMakeUpDate2(book.getMakeUpDate2());
				divideBook.setMakeUpAmount2(YssD.round(YssD.sub(book.getMakeUpAmount2(),YssD.round(YssD.mul(book.getMakeUpAmount2(),scaleT1),0)),2));
				divideBook.setMakeUpUnitCost2(book.getMakeUpUnitCost2());
				divideBook.setoMakeUpCost2(YssD.round(YssD.sub(book.getoMakeUpCost2(),YssD.round(YssD.mul(book.getoMakeUpCost2(),scaleT1),2)),2));
				divideBook.sethMakeUpCost2(YssD.round(YssD.sub(book.gethMakeUpCost2(),YssD.round(YssD.mul(book.gethMakeUpCost2(),scaleT1),2)),2));
				divideBook.setMakeUpRepCash2(YssD.round(YssD.sub(book.getMakeUpRepCash2(),YssD.round(YssD.mul(book.getMakeUpRepCash2(),scaleT1),2)),2));
				divideBook.setCanMkUpRepCash2(YssD.round(YssD.sub(book.getCanMkUpRepCash2(),YssD.round(YssD.mul(book.getCanMkUpRepCash2(),scaleT1),2)),2));
				divideBook.setExRate2(book.getExRate2());
			}
			//第三次补票数据
			if(paramSet.getDealDayNum()>1&&book.getMakeUpAmount3()!=0){
				divideBook.setMakeUpDate3(book.getMakeUpDate3());
				divideBook.setMakeUpAmount3(YssD.round(YssD.sub(book.getMakeUpAmount3(),YssD.round(YssD.mul(book.getMakeUpAmount3(),scaleT1),0)),2));
				divideBook.setMakeUpUnitCost3(book.getMakeUpUnitCost3());
				divideBook.setoMakeUpCost3(YssD.round(YssD.sub(book.getoMakeUpCost3(),YssD.round(YssD.mul(book.getoMakeUpCost3(),scaleT1),2)),2));
				divideBook.sethMakeUpCost3(YssD.round(YssD.sub(book.gethMakeUpCost3(),YssD.round(YssD.mul(book.gethMakeUpCost3(),scaleT1),2)),2));
				divideBook.setMakeUpRepCash3(YssD.round(YssD.sub(book.getMakeUpRepCash3(),YssD.round(YssD.mul(book.getMakeUpRepCash3(),scaleT1),2)),2));
				divideBook.setCanMkUpRepCash3(YssD.round(YssD.sub(book.getCanMkUpRepCash3(),YssD.round(YssD.mul(book.getCanMkUpRepCash3(),scaleT1),2)),2));
				divideBook.setExRate3(book.getExRate3());
			}
			//第四次补票数据
			if(paramSet.getDealDayNum()>2&&book.getMakeUpAmount4()!=0){
				divideBook.setMakeUpDate4(book.getMakeUpDate4());
				divideBook.setMakeUpAmount4(YssD.round(YssD.sub(book.getMakeUpAmount4(),YssD.round(YssD.mul(book.getMakeUpAmount4(),scaleT1),0)),2));
				divideBook.setMakeUpUnitCost4(book.getMakeUpUnitCost4());
				divideBook.setoMakeUpCost4(YssD.round(YssD.sub(book.getoMakeUpCost4(),YssD.round(YssD.mul(book.getoMakeUpCost4(),scaleT1),2)),2));
				divideBook.sethMakeUpCost4(YssD.round(YssD.sub(book.gethMakeUpCost4(),YssD.round(YssD.mul(book.gethMakeUpCost4(),scaleT1),2)),2));
				divideBook.setMakeUpRepCash4(YssD.round(YssD.sub(book.getMakeUpRepCash4(),YssD.round(YssD.mul(book.getMakeUpRepCash4(),scaleT1),2)),2));
				divideBook.setCanMkUpRepCash4(YssD.round(YssD.sub(book.getCanMkUpRepCash4(),YssD.round(YssD.mul(book.getCanMkUpRepCash4(),scaleT1),2)),2));
				divideBook.setExRate4(book.getExRate4());
			}
			//第五次补票数据
			if(paramSet.getDealDayNum()>3&&book.getMakeUpAmount5()!=0){
				divideBook.setMakeUpDate5(book.getMakeUpDate5());
				divideBook.setMakeUpAmount5(YssD.round(YssD.sub(book.getMakeUpAmount5(),YssD.round(YssD.mul(book.getMakeUpAmount5(),scaleT1),0)),2));
				divideBook.setMakeUpUnitCost5(book.getMakeUpUnitCost5());
				divideBook.setoMakeUpCost5(YssD.round(YssD.sub(book.getoMakeUpCost5(),YssD.round(YssD.mul(book.getoMakeUpCost5(),scaleT1),2)),2));
				divideBook.sethMakeUpCost5(YssD.round(YssD.sub(book.gethMakeUpCost5(),YssD.round(YssD.mul(book.gethMakeUpCost5(),scaleT1),2)),2));
				divideBook.setMakeUpRepCash5(YssD.round(YssD.sub(book.getMakeUpRepCash5(),YssD.round(YssD.mul(book.getMakeUpRepCash5(),scaleT1),2)),2));
				divideBook.setCanMkUpRepCash5(YssD.round(YssD.sub(book.getCanMkUpRepCash5(),YssD.round(YssD.mul(book.getCanMkUpRepCash5(),scaleT1),2)),2));
				divideBook.setExRate5(book.getExRate5());
			}
			//------------强制处理数据----------
			if(book.getMustMkUpAmount()!=0){
				divideBook.setMustMkUpDate(book.getMustMkUpDate());
				divideBook.setMustMkUpAmount(YssD.round(YssD.sub(book.getMustMkUpAmount(),YssD.round(YssD.mul(book.getMustMkUpAmount(),scaleT1),0)),2));
				divideBook.setMustMkUpUnitCost(book.getMustMkUpUnitCost());
				divideBook.setoMustMkUpCost(YssD.round(YssD.sub(book.getoMustMkUpCost(),YssD.round(YssD.mul(book.getoMustMkUpCost(),scaleT1),2)),2));
				divideBook.sethMustMkUpCost(YssD.round(YssD.sub(book.gethMustMkUpCost(),YssD.round(YssD.mul(book.gethMustMkUpCost(),scaleT1),2)),2));
				divideBook.setMustMkUpRepCash(YssD.round(YssD.sub(book.getMustMkUpRepCash(),YssD.round(YssD.mul(book.getMustMkUpRepCash(),scaleT1),2)),2));
				divideBook.setMustCMkUpRepCash(YssD.round(YssD.sub(book.getMustCMkUpRepCash(),YssD.round(YssD.mul(book.getMustCMkUpRepCash(),scaleT1),2)),2));
				divideBook.setMustExRate(book.getMustExRate());
			}
			divideBook.setRemaindAmount(YssD.round(YssD.sub(book.getRemaindAmount(),YssD.round(YssD.mul(book.getRemaindAmount(),scaleT1),0)),2));
			divideBook.setSumReturn(YssD.round(YssD.sub(book.getSumReturn(),YssD.round(YssD.mul(book.getSumReturn(),scaleT1),2)),2));
			divideBook.setRefundDate(book.getRefundDate());
			divideBook.setExchangeRate(book.getExchangeRate());
			divideBook.setGradeType1(book.getGradeType1());
			divideBook.setGradeType3(book.getGradeType3());
			divideBook.setFactExRate(book.getFactExRate());
			divideBook.setExRateDate(book.getExRateDate());
			divideBook.setMarkType(book.getMarkType());
			divideBook.setRateType("T+4");
			divideBook.setTradeNum(book.getTradeNum());
			divideBook.setGradeType2(divideBook.getRateType());
			divideBook.setOrderCode(divideBook.getGradeType1()+"##"+divideBook.getGradeType2()+"##"+divideBook.getGradeType3());
			//----------------------------endt+4 汇率拆分的数据--------------------------//
			
			book.setMakeUpAmount(YssD.round(YssD.mul(book.getMakeUpAmount(),scaleT1),2));
			book.setReplaceCash(YssD.round(YssD.mul(book.getReplaceCash(),scaleT1),2));
			book.setCanReplaceCash(YssD.round(YssD.mul(book.getCanReplaceCash(),scaleT1),2));
			book.setSumAmount(YssD.round(YssD.mul(book.getSumAmount(),scaleT1),2));
			book.setRealAmount(YssD.round(YssD.mul(book.getRealAmount(),scaleT1),2));
			book.setTotalInterest(YssD.round(YssD.mul(book.getTotalInterest(),scaleT1),2));
			book.setWarrantCost(YssD.round(YssD.mul(book.getWarrantCost(),scaleT1),2));
			book.setBBInterest(YssD.round(YssD.mul(book.getBBInterest(),scaleT1),2));
			book.setBBWarrantCost(YssD.round(YssD.mul(book.getBBWarrantCost(),scaleT1),2));
			
			book.setMakeUpAmount1(YssD.round(YssD.mul(book.getMakeUpAmount1(),scaleT1),2));
			book.setoMakeUpCost1(YssD.round(YssD.mul(book.getoMakeUpCost1(),scaleT1),2));
			book.sethMakeUpCost1(YssD.round(YssD.mul(book.gethMakeUpCost1(),scaleT1),2));
			book.setMakeUpRepCash1(YssD.round(YssD.mul(book.getMakeUpRepCash1(),scaleT1),2));
			book.setCanMkUpRepCash1(YssD.round(YssD.mul(book.getCanMkUpRepCash1(),scaleT1),2));
			
			book.setMakeUpAmount2(YssD.round(YssD.mul(book.getMakeUpAmount2(),scaleT1),2));
			book.setoMakeUpCost2(YssD.round(YssD.mul(book.getoMakeUpCost2(),scaleT1),2));
			book.sethMakeUpCost2(YssD.round(YssD.mul(book.gethMakeUpCost2(),scaleT1),2));
			book.setMakeUpRepCash2(YssD.round(YssD.mul(book.getMakeUpRepCash2(),scaleT1),2));
			book.setCanMkUpRepCash2(YssD.round(YssD.mul(book.getCanMkUpRepCash2(),scaleT1),2));
			
			book.setMakeUpAmount3(YssD.round(YssD.mul(book.getMakeUpAmount3(),scaleT1),2));
			book.setoMakeUpCost3(YssD.round(YssD.mul(book.getoMakeUpCost3(),scaleT1),2));
			book.sethMakeUpCost3(YssD.round(YssD.mul(book.gethMakeUpCost3(),scaleT1),2));
			book.setMakeUpRepCash3(YssD.round(YssD.mul(book.getMakeUpRepCash3(),scaleT1),2));
			book.setCanMkUpRepCash3(YssD.round(YssD.mul(book.getCanMkUpRepCash3(),scaleT1),2));
			
			book.setMakeUpAmount4(YssD.round(YssD.mul(book.getMakeUpAmount4(),scaleT1),2));
			book.setoMakeUpCost4(YssD.round(YssD.mul(book.getoMakeUpCost4(),scaleT1),2));
			book.sethMakeUpCost4(YssD.round(YssD.mul(book.gethMakeUpCost4(),scaleT1),2));
			book.setMakeUpRepCash4(YssD.round(YssD.mul(book.getMakeUpRepCash4(),scaleT1),2));
			book.setCanMkUpRepCash4(YssD.round(YssD.mul(book.getCanMkUpRepCash4(),scaleT1),2));
			
			book.setMakeUpAmount5(YssD.round(YssD.mul(book.getMakeUpAmount5(),scaleT1),2));
			book.setoMakeUpCost5(YssD.round(YssD.mul(book.getoMakeUpCost5(),scaleT1),2));
			book.sethMakeUpCost5(YssD.round(YssD.mul(book.gethMakeUpCost5(),scaleT1),2));
			book.setMakeUpRepCash5(YssD.round(YssD.mul(book.getMakeUpRepCash5(),scaleT1),2));
			book.setCanMkUpRepCash5(YssD.round(YssD.mul(book.getCanMkUpRepCash5(),scaleT1),2));
			
			book.setMustMkUpAmount(YssD.round(YssD.mul(book.getMustMkUpAmount(),scaleT1),2));
			book.setoMustMkUpCost(YssD.round(YssD.mul(book.getoMustMkUpCost(),scaleT1),2));
			book.sethMustMkUpCost(YssD.round(YssD.mul(book.gethMustMkUpCost(),scaleT1),2));
			book.setMustMkUpRepCash(YssD.round(YssD.mul(book.getMustMkUpRepCash(),scaleT1),2));
			book.setMustCMkUpRepCash(YssD.round(YssD.mul(book.getMustCMkUpRepCash(),scaleT1),2));
			
			book.setRemaindAmount(YssD.round(YssD.mul(book.getRemaindAmount(),scaleT1),2));
			book.setSumReturn(YssD.round(YssD.mul(book.getSumReturn(),scaleT1),2));
			book.setRateType("T+1");
			book.setGradeType2(book.getRateType());
		}catch (Exception e) {
			throw new YssException("为拆分的台账数据赋值出错！",e);
		}
	}
	
	/**
	 * 判断昨天和今天是净申购还是净赎
	 * @param date
	 * @return
	 * @throws YssException
	 */
	private HashMap doJudgeTheDayNetSGOrSH(Date dDate,Date yesDate) throws YssException{
		HashMap iSGOrSH=null;//保存数据的集合
		StringBuffer buff = null;//做拼接SQL语句
		ResultSet rs = null;//结果集
		try{
			iSGOrSH = new HashMap();//实例化
			buff = new StringBuffer(500);
			buff.append(" select sum(case when FMark = 'S' then FTradeAmount else ");
			buff.append(" FTradeAmount * -1 end) as FTradeAmount,FPortCode,FBargainDate from ");
			buff.append(pub.yssGetTableName("tb_etf_ghinterface"));//过户库
			buff.append(" where FPortCode in (").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and FBargainDate between ").append(dbl.sqlDate(yesDate)).append(" and ").append(dbl.sqlDate(dDate));
			buff.append(" and FOperType =").append(dbl.sqlString("2ndcode"));
			buff.append(" group by FPortCode,FBargainDate");
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			
			while(rs.next()){
				iSGOrSH.put(rs.getString("FPortCode")+"\t"+rs.getDate("FBargainDate"),Double.toString(rs.getDouble("FTradeAmount")));
			}
			
		}catch (Exception e) {
			throw new YssException("判断今天是净申购还是净赎回出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return iSGOrSH;
	}
	
	/**
	 * 此方法处理台账汇总数据
	 * @param pst
	 * @throws YssException
	 */
	private void setStandingBookData(Date dDate) throws YssException{
		StringBuffer buff = null;//做拼接SQL语句
		Connection conn =null;//数据库连接
		boolean bTrans = true;//事物控制标识
		PreparedStatement pst = null;//预处理
		String sRowStr ="";
		Date yesDate = null;//操作日当天的钱一个工作日
		EachExchangeHolidays holiday = null;//节假日代码
		ResultSet rs = null;//结果集
		long sNum=0;//为了产生的编号不重复
		String strNumDate = ""; //保存交易编号
		try{
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			//拼接参数：节假日代码+当天的偏离天数+操作日期
			sRowStr = paramSet.getSHolidayCode() + "\t" + -1 + "\t" + YssFun.formatDate(dDate);
			//解析数据
			holiday.parseRowStr(sRowStr);
			yesDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//获取当天的最近一个工作日
			
			buff = new StringBuffer(500);
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//设置为手动提交事物
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("tb_etf_standingbook"));//给表加锁
			
			buff.append(" select sum(FMakeUpAmount) as FMakeUpAmount,sum(FReplaceCash) as FReplaceCash,");
			buff.append(" sum(FCanReplaceCash) as FCanReplaceCash,max(FExRightDate) as FExRightDate,");
			buff.append(" sum(FSumAmount) as FSumAmount,sum(FRealAmount) as FRealAmount,");
			buff.append(" sum(FTotalInterest) as FTotalInterest,sum(FWarrantCost) as FWarrantCost,");
			buff.append(" sum(FBBInterest) as FBBInterest,sum(FBBWarrantCost) as FBBWarrantCost,");
			buff.append(" max(FRightRate) as FRightRate,max(FMakeUpDate1) as FMakeUpDate1,");
			buff.append(" sum(FMakeUpAmount1) as FMakeUpAmount1,sum(FOMakeUpCost1) as FOMakeUpCost1,");
			buff.append(" sum(FHMakeUpCost1) as FHMakeUpCost1,sum(FMakeUpRepCash1) as FMakeUpRepCash1,");
			buff.append(" sum(FCanMkUpRepCash1) as FCanMkUpRepCash1,max(FMakeUpDate2) as FMakeUpDate2,");
			buff.append(" sum(FMakeUpAmount2) as FMakeUpAmount2,sum(FOMakeUpCost2) as FOMakeUpCost2,");
			buff.append(" sum(FHMakeUpCost2) as FHMakeUpCost2,sum(FMakeUpRepCash2) as FMakeUpRepCash2,");
			buff.append(" sum(FCanMkUpRepCash2) as FCanMkUpRepCash2,max(FMakeUpDate3) as FMakeUpDate3,");
			buff.append(" sum(FMakeUpAmount3) as FMakeUpAmount3,sum(FOMakeUpCost3) as FOMakeUpCost3,");
			buff.append(" sum(FHMakeUpCost3) as FHMakeUpCost3,sum(FMakeUpRepCash3) as FMakeUpRepCash3,");
			buff.append(" sum(FCanMkUpRepCash3) as FCanMkUpRepCash3,max(FMakeUpDate4) as FMakeUpDate4,");
			buff.append(" sum(FMakeUpAmount4) as FMakeUpAmount4,sum(FOMakeUpCost4) as FOMakeUpCost4,");
			buff.append(" sum(FHMakeUpCost4) as FHMakeUpCost4,sum(FMakeUpRepCash4) as FMakeUpRepCash4,");
			buff.append(" sum(FCanMkUpRepCash4) as FCanMkUpRepCash4,max(FMakeUpDate5) as FMakeUpDate5,");
			buff.append(" sum(FMakeUpAmount5) as FMakeUpAmount5,sum(FOMakeUpCost5) as FOMakeUpCost5,");
			buff.append(" sum(FHMakeUpCost5) as FHMakeUpCost5,sum(FMakeUpRepCash5) as FMakeUpRepCash5,");
			buff.append(" sum(FCanMkUpRepCash5) as FCanMkUpRepCash5,max(FMustMkUpDate) as FMustMkUpDate,");
			buff.append(" sum(FMustMkUpAmount) as FMustMkUpAmount,sum(FMustMkUpUnitCost) as FMustMkUpUnitCost,");
			buff.append(" sum(FOMustMkUpCost) as FOMustMkUpCost,sum(FHMustMkUpCost) as FHMustMkUpCost,");
			buff.append(" sum(FMustMkUpRepCash) as FMustMkUpRepCash,sum(FMustCMkUpRepCash) as FMustCMkUpRepCash,");
			buff.append(" sum(FRemaindAmount) as FRemaindAmount,sum(FSumReturn) as FSumReturn,");
			buff.append(" max(FRefundDate) as FRefundDate,max(FExchangeRate) as FExchangeRate,");
			buff.append(" max(FExRate1) as FExRate1,max(FExRate2) as FExRate2,max(FExRate3) as FExRate3,");
			buff.append(" max(FExRate4) as FExRate4,max(FExRate5) as FExRate5,max(FMustExRate) as FMustExRate,");
			buff.append(" max(FFactExRate) as FFactExRate,max(FExRateDate) as FExRateDate,");
			buff.append(" FGradeType1,FStockHolderCode,FTradeNum,FPortcode,FBuyDate,FBs,FBrokerCode,FSeatCode,' ' as FSecurityCode from ");
			buff.append(pub.yssGetTableName("tb_etf_standingbook"));
			buff.append(" where FPortcode in (").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and FBuyDate between ").append(dbl.sqlDate(yesDate)).append(" and ");
			buff.append(dbl.sqlDate(dDate)).append(" group by FGradeType1,FStockHolderCode, FTradeNum, FPortcode, FBuyDate, FBs,FBrokerCode,FSeatCode");
			
			rs =dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			
			buff.append(" insert into ").append(pub.yssGetTableName("tb_etf_standingbook"));
			buff.append("(FNum,FBuyDate,FBs,FPortCode,FSecurityCode,FStockHolderCode,FBrokerCode,FSeatCode,");
			buff.append(" FMakeUpAmount,FUnitCost,FReplaceCash,FCanReplaceCash,FExRightDate,FSumAmount,FRealAmount,FTotalInterest,");
			buff.append(" FWarrantCost,FBBInterest,FBBWarrantCost,FRightRate,FMakeUpDate1,FMakeUpAmount1,FMakeUpUnitCost1,FOMakeUpCost1,FHMakeUpCost1,");
			buff.append(" FMakeUpRepCash1,FCanMkUpRepCash1,FMakeUpDate2,FMakeUpAmount2,FMakeUpUnitCost2,FOMakeUpCost2,FHMakeUpCost2,FMakeUpRepCash2,");
			buff.append(" FCanMkUpRepCash2,FMakeUpDate3,FMakeUpAmount3,FMakeUpUnitCost3,FOMakeUpCost3,FHMakeUpCost3,FMakeUpRepCash3,FCanMkUpRepCash3,");
			buff.append(" FMakeUpDate4,FMakeUpAmount4,FMakeUpUnitCost4,FOMakeUpCost4,FHMakeUpCost4,FMakeUpRepCash4,FCanMkUpRepCash4,FMakeUpDate5,");
			buff.append(" FMakeUpAmount5,FMakeUpUnitCost5,FOMakeUpCost5,FHMakeUpCost5,FMakeUpRepCash5,FCanMkUpRepCash5,FMustMkUpDate,FMustMkUpAmount,");
			buff.append(" FMustMkUpUnitCost,FOMustMkUpCost,FHMustMkUpCost,FMustMkUpRepCash,FMustCMkUpRepCash,FRemaindAmount,FSumReturn,FRefundDate,");
			buff.append(" FExchangeRate,FOrderCode,FGradeType1,FGradeType2,FGradeType3,FExRate1,FExRate2,FExRate3,");
			buff.append(" FExRate4,FExRate5,FMustExRate,FFactExRate,FExRateDate,FMarkType,FRateType,FTradeNum,FCreator,FCreateTime)");
			buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");//35
			buff.append(" ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");//40
			buff.append(" ?,?,?,?,?,?,?,?)");
			
			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0,buff.length());
			
			//--------------------拼接交易编号---------------------
			strNumDate = YssFun.formatDatetime(dDate).substring(0, 8);
            strNumDate = strNumDate +
                dbFun.getNextInnerCode(pub.yssGetTableName("tb_etf_standingbook"),
                                       dbl.sqlRight("FNUM", 6), "000000",
                                       " where FNum like 'T" + strNumDate + "%'", 1);
            strNumDate = "T" + strNumDate;
            String s = strNumDate.substring(9, strNumDate.length());
            sNum = Long.parseLong(s);
            //--------------------------------end--------------------------//
			
			while(rs.next()){
				//--------------------拼接交易编号---------------------
                sNum++;
                String tmp = "";
                for (int j = 0; j < s.length() - String.valueOf(sNum).length(); j++) {
                    tmp += "0";
                }
                strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
                // ------------------------end--------------------------//
				pst.setString(1,strNumDate);
				pst.setDate(2,YssFun.toSqlDate(rs.getDate("FBuyDate")));
				pst.setString(3,rs.getString("FBs"));
				pst.setString(4,rs.getString("FPortCode"));
				pst.setString(5,rs.getString("FSecurityCode"));
				pst.setString(6,rs.getString("FStockHolderCode"));
				pst.setString(7,rs.getString("FBrokerCode"));
				pst.setString(8,rs.getString("FSeatCode"));
				pst.setDouble(9,rs.getDouble("FMakeUpAmount"));
				pst.setDouble(10,0);
				pst.setDouble(11,rs.getDouble("FReplaceCash"));
				pst.setDouble(12,rs.getDouble("FCanReplaceCash"));
				pst.setDate(13,YssFun.toSqlDate(rs.getDate("FExRightDate")!=null?rs.getDate("FExRightDate"):YssFun.toDate("9998-12-31")));
				pst.setDouble(14,rs.getDouble("FSumAmount"));
				pst.setDouble(15,rs.getDouble("FRealAmount"));
				pst.setDouble(16,rs.getDouble("FTotalInterest"));
				pst.setDouble(17,rs.getDouble("FWarrantCost"));
				pst.setDouble(18,rs.getDouble("FBBInterest"));
				pst.setDouble(19,rs.getDouble("FBBWarrantCost"));
				pst.setDouble(20,rs.getDouble("FRightRate"));
				
				pst.setDate(21,YssFun.toSqlDate(rs.getDate("FMakeUpDate1")!=null?rs.getDate("FMakeUpDate1"):YssFun.toDate("9998-12-31")));
				pst.setDouble(22,rs.getDouble("FMakeUpAmount1"));
				pst.setDouble(23,0);
				pst.setDouble(24,rs.getDouble("FOMakeUpCost1"));
				pst.setDouble(25,rs.getDouble("FHMakeUpCost1"));
				pst.setDouble(26,rs.getDouble("FMakeUpRepCash1"));
				pst.setDouble(27,rs.getDouble("FCanMkUpRepCash1"));
				
				pst.setDate(28,YssFun.toSqlDate(rs.getDate("FMakeUpDate2")!=null?rs.getDate("FMakeUpDate2"):YssFun.toDate("9998-12-31")));
				pst.setDouble(29,rs.getDouble("FMakeUpAmount2"));
				pst.setDouble(30,0);
				pst.setDouble(31,rs.getDouble("FOMakeUpCost2"));
				pst.setDouble(32,rs.getDouble("FHMakeUpCost2"));
				pst.setDouble(33,rs.getDouble("FMakeUpRepCash2"));
				pst.setDouble(34,rs.getDouble("FCanMkUpRepCash2"));
				
				pst.setDate(35,YssFun.toSqlDate(rs.getDate("FMakeUpDate3")!=null?rs.getDate("FMakeUpDate3"):YssFun.toDate("9998-12-31")));
				pst.setDouble(36,rs.getDouble("FMakeUpAmount3"));
				pst.setDouble(37,0);
				pst.setDouble(38,rs.getDouble("FOMakeUpCost3"));
				pst.setDouble(39,rs.getDouble("FHMakeUpCost3"));
				pst.setDouble(40,rs.getDouble("FMakeUpRepCash3"));
				pst.setDouble(41,rs.getDouble("FCanMkUpRepCash3"));
				
				pst.setDate(42,YssFun.toSqlDate(rs.getDate("FMakeUpDate4")!=null?rs.getDate("FMakeUpDate4"):YssFun.toDate("9998-12-31")));
				pst.setDouble(43,rs.getDouble("FMakeUpAmount4"));
				pst.setDouble(44,0);
				pst.setDouble(45,rs.getDouble("FOMakeUpCost4"));
				pst.setDouble(46,rs.getDouble("FHMakeUpCost4"));
				pst.setDouble(47,rs.getDouble("FMakeUpRepCash4"));
				pst.setDouble(48,rs.getDouble("FCanMkUpRepCash4"));
				
				pst.setDate(49,YssFun.toSqlDate(rs.getDate("FMakeUpDate5")!=null?rs.getDate("FMakeUpDate5"):YssFun.toDate("9998-12-31")));
				pst.setDouble(50,rs.getDouble("FMakeUpAmount5"));
				pst.setDouble(51,0);
				pst.setDouble(52,rs.getDouble("FOMakeUpCost5"));
				pst.setDouble(53,rs.getDouble("FHMakeUpCost5"));
				pst.setDouble(54,rs.getDouble("FMakeUpRepCash5"));
				pst.setDouble(55,rs.getDouble("FCanMkUpRepCash5"));
				
				pst.setDate(56,YssFun.toSqlDate(rs.getDate("FMustMkUpDate")!=null?rs.getDate("FMustMkUpDate"):YssFun.toDate("9998-12-31")));
				pst.setDouble(57,rs.getDouble("FMustMkUpAmount"));
				pst.setDouble(58,0);
				pst.setDouble(59,rs.getDouble("FOMustMkUpCost"));
				pst.setDouble(60,rs.getDouble("FHMustMkUpCost"));
				pst.setDouble(61,rs.getDouble("FMustMkUpRepCash"));
				pst.setDouble(62,rs.getDouble("FMustCMkUpRepCash"));
				
				pst.setDouble(63,rs.getDouble("FRemaindAmount"));
				pst.setDouble(64,rs.getDouble("FSumReturn"));
				pst.setDate(65,YssFun.toSqlDate(rs.getDate("FRefundDate")!=null?rs.getDate("FRefundDate"):YssFun.toDate("9998-12-31")));
				pst.setDouble(66,rs.getDouble("FExchangeRate"));
				pst.setString(67,rs.getString("FGradeType1"));
				pst.setString(68,rs.getString("FGradeType1"));
				pst.setString(69,"");
				pst.setString(70,"");
				pst.setDouble(71,rs.getDouble("FExRate1"));
				pst.setDouble(72,rs.getDouble("FExRate2"));
				pst.setDouble(73,rs.getDouble("FExRate3"));
				pst.setDouble(74,rs.getDouble("FExRate4"));
				pst.setDouble(75,rs.getDouble("FExRate5"));
				pst.setDouble(76,rs.getDouble("FMustExRate"));
				pst.setDouble(77,rs.getDouble("FFactExRate"));
				pst.setDate(78,YssFun.toSqlDate(rs.getDate("FExRateDate")!=null?rs.getDate("FExRateDate"):YssFun.toDate("9998-12-31")));
				pst.setString(79,"");
				pst.setString(80," ");
				pst.setString(81,rs.getString("FTradeNum"));
				pst.setString(82,pub.getUserCode());
				pst.setString(83,YssFun.formatDate(new Date()));
				pst.addBatch();//增加批处理
			}
			pst.executeBatch();//执行批处理
			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置为自动提交事物
			bTrans = false;
		}catch (Exception e) {
			throw new YssException("处理台账汇总数据出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
		}
	}
}




















