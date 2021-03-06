package com.yss.main.etfoperation.etfaccbook.gadoliniumETF;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.etfaccbook.CreateBookPretreatmentAdmin;
import com.yss.main.etfoperation.etfaccbook.CtlETFAccBook;
import com.yss.main.etfoperation.etfaccbook.PretValMktPriceAndExRate;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailRefBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**shashijie 2011.07.06 STORY 974 */
public class CreateTradeClose extends CtlETFAccBook{
	private ArrayList tradeSettleDetail = new ArrayList();//保存操作日期当天明细关联数据
	//private ArrayList tradeSettleDetailDifference = new ArrayList();//保存操作日期当天明细关联数据中钆差补票的数据
	//private ArrayList tradeSettleDetailYesDate = new ArrayList();//保存操作日前一天的明细及关联数据
	private ETFParamSetBean paramSet = null;// ETF参数的实体类
	private ETFParamSetAdmin paramSetAdmin = null;
	private HashMap etfParam = null;//保存参数设置
	private HashMap holidays = null;//保存节假日群代码
	/**shashijie 2011-07-16 STORY 974 */
	private String tradeNum = "";//成交编号(申赎编号)
	private String stockHolderCode = "";//股东代码
	private String bs = "";//交易类型
	private String portCode = "";//单个组合代码
	private double baksetCount = 0;//钆差篮子数
	/**end*/
	private String securityCodes = "";//证券代码
	private java.util.Date startDate = null;//开始的申赎日期
	private java.util.Date endDate = null;//结束的申赎日期	
	private java.util.Date tradeDate = null;//估值日期
	private String standingBookType = "";//台账类型：申购-B，赎回-S
	private String portCodes = "";//组合代码
	
	public CreateTradeClose() {
		super();
	}
	
	/** 处理业务的入口方法 */
	public void doManageAll() throws YssException{
		int days = 0;//保存循环日期之差
		Date dDate = null;//日期
		PretValMktPriceAndExRate marketValue = null;//预处理估值行情和估值汇率
		CreateBookPretreatmentAdmin booPreAdmin = null;//明细数据和明细关联数据 保存数据操作类
		EachExchangeHolidays holiday = null;//节假日代码
		String sRowStr = "";
		Date theDate = null;//操作日当天的是否是工作日
		try{
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			
			booPreAdmin = new CreateBookPretreatmentAdmin();
			booPreAdmin.setYssPub(pub);
			
			marketValue = new PretValMktPriceAndExRate();//实例化
			marketValue.setYssPub(pub);//设置pub
			
			days = YssFun.dateDiff(this.getStartDate(),this.getEndDate());//循环日期时，保存最大日期与最小日期的差
			dDate = this.getStartDate();//得到操作的起始日期
			marketValue.getValMktPriceAndExRateBy(this.getPortCodes(),dDate);//获取估值行情
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
				doTradestldtl(dDate);//此方法处理交易结算明细表数据-申赎数据
				//------------------------往明细表和关联表中插入数据-------------------------//
				if(tradeSettleDetail.size()>0){
					booPreAdmin.insertTheDateData(dDate,this.portCodes,tradeSettleDetail);//插入当天申购赎回数据和关联数据
				}
				//-------------------------------end----------------------------------------//
				/**shashijie 2011.07.04 STORY 974 */
				//清空
				tradeSettleDetail.removeAll(tradeSettleDetail);
				doMakeTradeSettleDelRef(dDate);//处理钆差补票数据,强制处理,权益数据
				if(tradeSettleDetail.size()>0){
					Date makeDate = getWorkDayMake(dDate,paramSet.getBeginSupply());//获取倒退出的补票工作日
					booPreAdmin.insertTheDateData(makeDate,this.portCodes,tradeSettleDetail);//插入当天申购赎回数据和关联数据
				}
				/**end*/
				dDate = YssFun.addDay(dDate,1);//每一次循环把日期加一天
			}
			
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	/**
	 * 此方法做解析前台传来数据，在调用此方法时就实例化一些全局变量和类
	 */
	public void initData(Date startDate,Date endDate,Date tradeDate ,String portCodes,String standingBookType)
				throws YssException{
		try{
			this.startDate = startDate;//操作起始日期
			this.endDate = endDate;//操作结束日期
			this.tradeDate = tradeDate;//业务日期
			this.portCodes = portCodes;//组合代码
			this.standingBookType = standingBookType;//台账类型
			paramSetAdmin = new ETFParamSetAdmin();//实例化参数设置的操作类
			paramSetAdmin.setYssPub(pub);//设置pub
			etfParam = paramSetAdmin.getETFParamInfo(this.portCodes); // 根据已选组合代码用于获取相关ETF参数数据
			paramSet = (ETFParamSetBean) etfParam.get(this.portCodes);//根据组合代码获取参数设置的实体bean
			holidays = paramSet.getHoildaysRela();//获取保存节假日代码的hash表
		}catch (Exception e) {
			throw new YssException("做解析前台传来数据出错！",e);
		}
	}

	/**此方法处理交易结算明细表数据-申赎数据及当天的补票数据*/
	private void doTradestldtl(Date dDate) throws YssException{
		StringBuffer buff = null;//做拼接SQL语句
		ResultSet rs = null;//声明结果集
		long sNum = 0;//做拼接申请编号用
		String strNumDate = "";//保存申请编号
		ETFTradeSettleDetailBean etfTradeSettleDetail= null;//交易明细
		//ETFTradeSettleDetailRefBean tradeSettleDelRef = null;//交易明细关联
		
		try{
			if(tradeSettleDetail.size()!=0){//如果集合不为空时，要先清空
				tradeSettleDetail.clear();
			}
			
			buff = new StringBuffer(2000);

			buff.append(" select gh.*,js.FStockholderCode as FStockholder,js.FClearCode,");
			buff.append(" (case when sub.FtradeAmount is null then 0 else sub.FtradeAmount end)as FsubAmount, ");
			buff.append(" sub.FTotalCost,mk.FPrice,ra.* from ").append(" (select * from ");
			buff.append(pub.yssGetTableName("tb_etf_ghinterface")).append(" b ");//过户库
			buff.append(" left join (select st.fsecuritycode as securitycode,st.FAmount,st.fpremiumscale," +
					"st.FTotalMoney,st.fportcode as portCode from ");
			buff.append(pub.yssGetTableName("tb_etf_stocklist")).append(" st ").append(" where st.FCheckState = 1");
			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and st.FDate = ").append(dbl.sqlDate(dDate));
			buff.append(" ) a on b.fportcode = a.portCode");
			buff.append(" where b.fcheckstate = 1 and b.fportcode in(").append(
					this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and b.FOperType = '2ndcode'").append(" and b.fbargaindate = ").append(dbl.sqlDate(dDate));
			buff.append(" ) gh");
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_data_subtrade"));//业务资料表
			buff.append(" where FCheckState = 1 and FAppDate =").append(dbl.sqlDate(dDate));
			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and fbargaindate =").append(dbl.sqlDate(dDate)).append(
					" ) sub on sub.fsecuritycode = gh.securitycode");
			buff.append(" and sub.fportcode = gh.portcode and gh.ftradenum = sub.FDealNum ");

			buff.append(" left join(select * from ").append(pub.yssGetTableName("Tb_Data_PretValMktPrice"));//行情预处理表
			
			buff.append(" where FCheckState = 1 and FValDate =").append(dbl.sqlDate(dDate));
			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(") mk on gh.SecurityCode =mk.fsecuritycode and gh.FPortCode = mk.FPortCode");

			buff.append(" left join(select * from ").append(pub.yssGetTableName("Tb_ETF_JSMXInterface"));//中登结算明细库
			buff.append(" where FCheckState =1 and FClearDate = ").append(dbl.sqlDate(dDate));
			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" ) js on gh.ftradenum = js.FTradeNum");
			
			buff.append(" join( select * from ").append(pub.yssGetTableName("tb_para_security"));
			buff.append(" where FCheckState = 1 and FCatCode = 'EQ') se on gh.securitycode = se.Fsecuritycode ");
			
			buff.append(" left join (select FBaseRate,FPortRate,FValDate,FCuryCode,FPortCode,");
			buff.append(" FOTBaseRate1,FOTBaseRate2,FOTBaseRate3 from ");
			buff.append(pub.yssGetTableName("Tb_Data_PretValRate"));//汇率预处理表
			buff.append(" where FCheckState = 1 and FPortCode =").append(dbl.sqlString(this.portCodes));
			buff.append(" and FValDate =").append(dbl.sqlDate(dDate));
			buff.append(" ) ra on gh.FPortCode = ra.FPortCode and se.FTradeCury = ra.FCuryCode");

			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0, buff.length());
			
			//--------------------拼接交易编号---------------------
			strNumDate = YssFun.formatDatetime(dDate).substring(0, 8);
			strNumDate = strNumDate
					+ dbFun.getNextInnerCode(pub.yssGetTableName("Tb_ETF_TradeStlDtl"), 
							dbl.sqlRight("FNUM", 6), "000000", " where FNum like 'T"
							+ strNumDate + "%'", 1);
			strNumDate = "T" + strNumDate;
			String s = strNumDate.substring(9, strNumDate.length());
			sNum = Long.parseLong(s);
			// --------------------------------end--------------------------//
			while (rs.next()) {
				
				//--------------------拼接交易编号---------------------
				sNum++;
				String tmp = "";
				for (int t = 0; t < s.length()- String.valueOf(sNum).length(); t++) {
					tmp += "0";
				}
				strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
				// ------------------------end--------------------------//
				etfTradeSettleDetail = new ETFTradeSettleDetailBean();//交易结算明细表
				//tradeSettleDelRef = new ETFTradeSettleDetailRefBean();//交易结算明细关联表
				//---------------------------交易明细赋值---------------------//
				etfTradeSettleDetail.setNum(strNumDate);//申请编号
				
				etfTradeSettleDetail.setPortCode(rs.getString("FPortCode"));//组合代码
				etfTradeSettleDetail.setSecurityCode(rs.getString("securitycode"));//证券代码
				etfTradeSettleDetail.setStockHolderCode(rs.getString("FStockHolder"));//投资者
				etfTradeSettleDetail.setBrokerCode(rs.getString("FClearCode"));//券商
				etfTradeSettleDetail.setSeatCode(rs.getString("FSeatNum"));//交易席位
				etfTradeSettleDetail.setBs(rs.getString("FMark").equalsIgnoreCase("S")?"B":"S");//台账类型
				etfTradeSettleDetail.setBuyDate(dDate);//申赎日期
				//替代数量 = (申赎份额/参数设置中基准比例)* 股票篮文件中证券数量
				etfTradeSettleDetail.setReplaceAmount(
						YssD.mul(YssD.div(rs.getDouble("FTradeAmount"),
								paramSet.getNormScale()),rs.getDouble("FAmount")));
				etfTradeSettleDetail.setBraketNum(YssD.div(rs.getDouble("FTradeAmount"),paramSet.getNormScale()));//篮子数
				etfTradeSettleDetail.setUnitCost(rs.getDouble("FPrice"));//单位成本
				//汇率
				etfTradeSettleDetail.setExchangeRate(YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate")));
				if(rs.getString("FMark").equalsIgnoreCase("S")){ //s-是过户库中申购（系统中表示赎回）
					//替代金额本币 = round(股票篮中替代金额 * （1+溢价比例）,2)* 篮子数
					etfTradeSettleDetail.setHReplaceCash(YssD.mul(YssD.round(YssD.mul(rs.getDouble("FTotalMoney"),
							YssD.add(1,rs.getDouble("fpremiumscale"))),2),etfTradeSettleDetail.getBraketNum()));
					//替代金额原币
					etfTradeSettleDetail.setOReplaceCash(YssD.round(YssD.div(
							YssD.mul(etfTradeSettleDetail.getHReplaceCash(),
									rs.getDouble("FPortRate")),rs.getDouble("FBaseRate")),2));
					//可退替代款原币 =替代金额原币 - 替代数量 * 单位成本
					etfTradeSettleDetail.setOcReplaceCash(
							YssD.round(YssD.sub(etfTradeSettleDetail.getOReplaceCash(),
									YssD.mul(etfTradeSettleDetail.getReplaceAmount()
											,etfTradeSettleDetail.getUnitCost())),2));
					//可退替代款本币 = 替代金额本币 - 替代数量 * 单位成本 * 基础汇率/组合汇率
					etfTradeSettleDetail.setHcReplaceCash(
							YssD.sub(etfTradeSettleDetail.getHReplaceCash(),YssD.round(
									YssD.div(YssD.mul(etfTradeSettleDetail.getReplaceAmount(),
											etfTradeSettleDetail.getUnitCost(),rs.getDouble("FBaseRate")),
											rs.getDouble("FPortRate")),2)));
				
				}else{
					//应退赎回款（原币） = 篮子中的股票数量*股票的T日收盘价
					etfTradeSettleDetail.setOReplaceCash(YssD.round(YssD.mul(
							etfTradeSettleDetail.getReplaceAmount(),rs.getDouble("FPrice")),2));
					//应退赎回款（本币） = 篮子中的股票数量*股票的T日收盘价*T日的估值汇率 
					etfTradeSettleDetail.setHReplaceCash(YssD.round(YssD.div(YssD.mul(
							etfTradeSettleDetail.getReplaceAmount(),rs.getDouble("FPrice"),
							rs.getDouble("FBaseRate")),rs.getDouble("FPortRate")),2));
					//可退替代款原币 =替代金额原币
					etfTradeSettleDetail.setOcReplaceCash(etfTradeSettleDetail.getOReplaceCash());
					//可退替代款本币 = 替代金额本币
					etfTradeSettleDetail.setHcReplaceCash(etfTradeSettleDetail.getHReplaceCash());
				}				
				etfTradeSettleDetail.setTradeNum(rs.getString("FTradeNum"));//成交编号
				
				//华宝这里写死钆差补票
				etfTradeSettleDetail.setMarktype("difference");//标志类型 difference = 钆差补票
				//-----------------------------------end--------------------------------//
				
				tradeSettleDetail.add(etfTradeSettleDetail);//把明细数据存入集合中
			}
			
		}catch (Exception e) {
			throw new YssException("处理交易结算明细表数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/** 为操作日期前一个工作日的申赎数据中剩余数量为0的明细关联数据设置为已清算 */
	private void doSettleMarkUpdate(Date yesDate,Date SGReplaceOver,Date SHReplaceOver) throws YssException{
		StringBuffer buff =null;//做拼接SQL语句
		Connection conn = null;//数据库连接
		boolean bTrans = true;//事物控制标识
		try{
			buff = new StringBuffer();//实例化
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//设置为手动提交事物
			buff.append(" update ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));
			buff.append(" set FSettleMark = 'Y'");
			buff.append(" where FRemaindAmount = 0 and FMakeUpDate in (select FBuyDate from ");
			buff.append(pub.yssGetTableName("tb_etf_tradestldtl")).append(" where FBuyDate =").append(dbl.sqlDate(yesDate));
			buff.append(" and FBs =").append(dbl.sqlString("B"));
			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append("))");
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
			buff.append(" update ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));
			buff.append(" set FSettleMark = 'Y'");
			buff.append(" where FRemaindAmount = 0 and FMakeUpDate in (select FBuyDate from ");
			buff.append(pub.yssGetTableName("tb_etf_tradestldtl")).append(" where FBuyDate =").append(dbl.sqlDate(yesDate));
			buff.append(" and FBs =").append(dbl.sqlString("S"));
			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append("))");
			
			dbl.executeSql(buff.toString());
			buff.delete(0,buff.length());
			
			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置为自动提交事物
			bTrans = false;
			
		}catch (Exception e) {
			throw new YssException("为操作日期前一个工作日的申赎数据中剩余数量为0的明细关联数据设置为已清算出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
		}
	}
	
	/** 此方法处理分红数据 */
	private void doDivdend(ETFTradeSettleDetailRefBean tradeSettleDelRef,ResultSet rs) throws YssException{
		double divValue = 0;//分红总派息
		try{
			if(rs.getDouble("DivRatio")!=0){
				//分红总派息 = 申赎数量* 分红权益比例
				divValue = YssD.mul(rs.getDouble("FReplaceAmount"),rs.getDouble("DivRatio"));
				
				tradeSettleDelRef.setInterest(YssD.round(divValue,2));//分红总派息（原币）
				//分红总派息（本币）= 分红总派息（原币）* 汇率
				tradeSettleDelRef.setBbinterest(YssD.round(YssD.mul(divValue,tradeSettleDelRef.getRightRate()),2));
			}
			
		}catch (Exception e) {
			throw new YssException("处理分红数据出错！",e);
		}
	}
	
	/** 此方法处理送股数据 */
	private void doBonusShare(ETFTradeSettleDetailRefBean tradeSettleDelRef,ResultSet rs) throws YssException{
		double shareAllAmount = 0;//送股总数量
		double factAmount = 0;// 实际数量
		try{
			if(rs.getDouble("shareRatio")!=0){
				//送股总数量 = 申赎数量* 送股权益比例
				shareAllAmount = YssD.mul(rs.getDouble("FReplaceAmount"),rs.getDouble("shareRatio"));
				//实际数量= 申赎数量 * 送股权益比例(原本是拿第一次补票后的剩余数量*比例,这里不考虑)
				factAmount = YssD.mul(rs.getDouble("FReplaceAmount"),rs.getDouble("shareRatio"));
				
				tradeSettleDelRef.setSumAmount(shareAllAmount);//送股总数量
				
				tradeSettleDelRef.setRealAmount(factAmount);//实际数量
			}
			
		}catch (Exception e) {
			throw new YssException("处理送股数据出错！",e);
		}
	}
	
	/** 此方法处理配股数据 */
	private void doRightIssue(ETFTradeSettleDetailRefBean tradeSettleDelRef,ResultSet rs) throws YssException{
		double rightValue = 0;//权证价值
		double dPrice = 0;//当日收盘价 - 权证价格
		try{
			if(rs.getDouble("RigFRatio")!=0){
				//当日收盘价 - 权证价格
				dPrice = YssD.sub(rs.getDouble("FPrice"),rs.getDouble("FRIPrice"));
				if(dPrice<=0){
					rightValue = 0;
				}else{
					//权证价值 = 申赎数量 * （当日收盘价 - 权证价格）* 配股权益比例
					rightValue = YssD.mul(rs.getDouble("FReplaceAmount"),dPrice,rs.getDouble("RigFRatio"));
				}
				
				tradeSettleDelRef.setWarrantCost(rightValue);//权证价值（原币）
				//权证价值（本币）= 权证价值（原币） * 汇率
				tradeSettleDelRef.setBbwarrantCost(YssD.mul(rightValue,tradeSettleDelRef.getRightRate()));
				
			}
			
		}catch (Exception e) {
			throw new YssException("处理配股数据出错！",e);
		}
	}
	
	/** 此方法处理强制处理数据  STORY 974
	 * @param price 单位成本
	 * @param allCost 总成本原币*/
	private void doForceManage(ETFTradeSettleDetailBean etfTradeSettleDetail,
			ETFTradeSettleDetailRefBean trade ,ResultSet rs,Date dDate,
			Date SGReplaceOver,Date SHReplaceOver,double price,double allCost) throws YssException{
		ETFTradeSettleDetailRefBean tradeRef = null;//交易结算明细关联bean
		try{
			tradeRef = new ETFTradeSettleDetailRefBean();//实例化
			tradeRef.setNum(etfTradeSettleDetail.getNum());//申请编号
			tradeRef.setRefNum("3");//关联编号 主要是区分：1-第一次补票，2-第二次补票，3-强制处理
			tradeRef.setMakeUpDate(dDate);//补票日期
			
			//单位成本（原币） = 没有成交价则当日收盘价 
			tradeRef.setUnitCost(price);
			
			//数量 = 剩余数量 = 申赎数量 + 权益数据实际数量 - 第一次补票数量
			tradeRef.setMakeUpAmount(trade.getRemaindAmount());
			//汇率
			tradeRef.setExchangeRate(trade.getExchangeRate());
			
			//总成本（原币）= 单位成本（原币）* 数量
			tradeRef.setoMakeUpCost(allCost);
			
			//总成本（本币）= 总成本（原币）* 汇率
			double hMakeUpCost = YssD.round(YssD.mul(tradeRef.getoMakeUpCost(),tradeRef.getExchangeRate()),2);
			tradeRef.sethMakeUpCost(hMakeUpCost);
			
			if(rs.getString("FBs").equalsIgnoreCase("B")){
				//可退替代款（原币） = 申赎数据可退替代款 （原币）- 第一次补票可退替代款 （原币）
				tradeRef.setOcReplaceCash(YssD.round(YssD.sub(etfTradeSettleDetail.getOcReplaceCash(),
						trade.getOcReplaceCash()),2));
				//可退替代款（本币） = 申赎数据可退替代款 （本币）- 第一次补票可退替代款 （本币）
				tradeRef.setHcReplaceCash(YssD.round(YssD.sub(etfTradeSettleDetail.getHcReplaceCash(),
						trade.getHcReplaceCash()),2));
				System.out.println(rs.getString("FSecuritycode")+"----------------------------------------证券");
				//申赎数据替代金额（原币）-权益数据总派息（原币）- 权益数据权证价值（原币）
				double repCash = YssD.sub(rs.getDouble("FOReplaceCash"),trade.getInterest(),trade.getWarrantCost());
				//强制处理数量 /（申赎数量+ 权益数据总数量）
				double amount = YssD.div(tradeRef.getMakeUpAmount(),
									YssD.add(rs.getDouble("FReplaceAmount"),trade.getSumAmount()));
				/*应付替代款（原币） = （申赎数据替代金额（原币）-权益数据总派息（原币）- 权益数据权证价值（原币））* 
				*					        强制处理数量 /（申赎数量+ 权益数据总数量） - 强制处理总成本（原币）*/
				tradeRef.setOpReplaceCash(YssD.sub(YssD.round(YssD.mul(repCash,amount),2),tradeRef.getoMakeUpCost()));
				
				//（申赎数据替代金额（本币）-权益数据总派息（本币）- 权益数据权证价值（本币））
				repCash = YssD.sub(rs.getDouble("FHReplaceCash"),trade.getBbinterest(),trade.getBbwarrantCost());
				//强制处理数量 /（申赎数量+ 权益数据总数量）
				amount =YssD.div(tradeRef.getMakeUpAmount(),
									YssD.add(rs.getDouble("FReplaceAmount"),trade.getSumAmount()));
				/*应付替代款（本币） = （申赎数据替代金额（本币）-权益数据总派息（本币）- 权益数据权证价值（本币））* 
				*					        强制处理数量 /（申赎数量+ 权益数据总数量） - 强制处理总成本（本币）*/
				tradeRef.setHpReplaceCash(YssD.sub(YssD.round(YssD.mul(repCash,amount),2),tradeRef.gethMakeUpCost()));
				//数据方向
				tradeRef.setDataDirection("1");
				//退款日期
				tradeRef.setRefundDate(SGReplaceOver);
			}else{
				//可退替代款（原币） = 申赎数据可退替代款 （原币）- 第一次补票可退替代款 （原币）
				tradeRef.setOcReplaceCash(YssD.round(YssD.sub(etfTradeSettleDetail.getOcReplaceCash(),
						trade.getOcReplaceCash()),2));
				//可退替代款（本币） = 申赎数据可退替代款 （本币）- 第一次补票可退替代款 （本币）
				tradeRef.setHcReplaceCash(YssD.round(YssD.sub(etfTradeSettleDetail.getHcReplaceCash(),
						trade.getHcReplaceCash()),2));
				
				//总派息原币* 强制处理数量/(申赎数量 + 权益实际数量) 
				double interest = YssD.mul(trade.getInterest(),
						YssD.div(tradeRef.getMakeUpAmount(),
								YssD.add(rs.getDouble("FReplaceAmount"),trade.getRealAmount())));
				//权证价值原币* 强制处理数量/(申赎数量 + 权益实际数量)
				double warrant = YssD.mul(trade.getWarrantCost(),
						YssD.div(tradeRef.getMakeUpAmount(), 
								YssD.add(rs.getDouble("FReplaceAmount"), trade.getRealAmount())));
				/*应付替代款（原币） = 补票总成本原币 + 总派息原币* 强制处理数量/(申赎数量 + 权益实际数量) 
				*					+ 权证价值原币* 强制处理数量/(申赎数量 + 权益实际数量)*/
				tradeRef.setOpReplaceCash(YssD.add(tradeRef.getoMakeUpCost(),
						YssD.round(interest,2),YssD.round(warrant,2)));
				
				//总派息本币* 强制处理数量/(申赎数量+ 权益实际数量) 
				interest = YssD.mul(trade.getBbinterest(), 
						YssD.div(tradeRef.getMakeUpAmount(), 
								YssD.add(rs.getDouble("FReplaceAmount"),trade.getRealAmount())));
				//权证价值本币* 强制处理数量/(申赎数量+ 权益实际数量)
				warrant = YssD.mul(trade.getBbwarrantCost(), 
						YssD.div(tradeRef.getMakeUpAmount(), 
								YssD.add(rs.getDouble("FReplaceAmount"),trade.getRealAmount())));
				/*应付替代款（本币） = 补票总成本本币 + 
				 * 						总派息本币* 强制处理数量/(申赎数量+ 权益实际数量)  + 
				 * 						权证价值本币* 强制处理数量/(申赎数量+ 权益实际数量) */
				tradeRef.setHpReplaceCash(YssD.add(tradeRef.gethMakeUpCost(),
						YssD.round(interest,2),YssD.round(warrant,2)));
				//数据方向
				tradeRef.setDataDirection("-1");
				//退款日期
				tradeRef.setRefundDate(SHReplaceOver);
			}
			//数据标识
			tradeRef.setDataMark("1");
			//清算标识
			tradeRef.setSettleMark("Y");
			
			etfTradeSettleDetail.getAlTradeSettleDelRef().add(tradeRef);//把明细关联数据保存到明细bean的集合中
		}catch (Exception e) {
			throw new YssException("处理强制处理数据出错！",e);
		}
	}

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

	public String getStandingBookType() {
		return standingBookType;
	}

	public void setStandingBookType(String standingBookType) {
		this.standingBookType = standingBookType;
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
	
	/**shashijie,2011-7-8 处理钆差补票 ,处理明细关联数据赋值,STORY 974*/
	private void doMakeTradeSettleDelRef(Date dDate) throws YssException {
		String strSql = "";
		Date makeDate = getWorkDayMake(dDate,paramSet.getBeginSupply());//获取倒退出的补票工作日
		Date SGReplaceOver = null;//申购应付替代结转
		Date SHReplaceOver = null;//赎回应付替代结转
		EachExchangeHolidays holiday = null;//节假日代码类
		ResultSet rs = null;
		
		try {
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//
			//设置节假日对象(日期,应付替代结转,应付替代结转天数)
			this.setHoliDay(holiday,dDate,YssOperCons.YSS_ETF_OVERTYPE_SGDEALREPLACE,paramSet.getISGDealReplace());
			SGReplaceOver = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//申购应付替代结转日期
			//设置节假日对象(日期,应付替代结转,应付替代结转天数)
			this.setHoliDay(holiday,dDate,YssOperCons.YSS_ETF_OVERTYPE_SHDEALREPLACE,paramSet.getISHDealReplace());
			SHReplaceOver = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//赎回应付替代结转日期
			
			//根据组合,日期,获取那天的申购赎回数量
			HashMap hmbasket = this.getTotalETFTradeAmout(makeDate,portCodes,"2ndcode");
			//判断是净申购还是净赎回
			String[] portCodesValue = this.portCodes.split(",");
			for (int i = 0; i < portCodesValue.length; i++) {
				String portCode = portCodesValue[i];
				//key (组合+申赎标示+市场代码)
				String bkey = portCode + "\t" + "B" + "\t" + paramSet.getTwoGradeMktCode();//过户表中B表示赎回
				String skey = portCode + "\t" + "S" + "\t" + paramSet.getTwoGradeMktCode();//过户表中S表示申购
				if (hmbasket==null || hmbasket.isEmpty()) {
					return;
				} else {
					double bBasketCount;// 赎回篮子数
					double sBasketCount;// 申购篮子数
					if (!hmbasket.containsKey(bkey) && !hmbasket.containsKey(skey)) {
						bBasketCount = 0;
						sBasketCount = 0;
					}else if(!hmbasket.containsKey(bkey)){//无赎回
						bBasketCount = 0;
						sBasketCount = Double.valueOf((String)hmbasket.get(skey)).doubleValue();
					}else if (!hmbasket.containsKey(skey)) {//无申购
						bBasketCount = Double.valueOf((String)hmbasket.get(bkey)).doubleValue();
						sBasketCount = 0;
					} else {
						bBasketCount = Double.valueOf((String)hmbasket.get(bkey)).doubleValue();
						sBasketCount = Double.valueOf((String)hmbasket.get(skey)).doubleValue();
					}
					//获取钆差补票sql
					strSql = getStrSqlOfGCBP(dDate,makeDate,portCode);
					rs = dbl.openResultSet(strSql);
					
					if (bBasketCount==sBasketCount) {//正好钆平
						equalityMakeAmount(rs,dDate,SGReplaceOver,SHReplaceOver);
					} else if (bBasketCount < sBasketCount) {//净申购
						baksetCount = bBasketCount;
						buyAmountMuch(rs,dDate,SGReplaceOver,SHReplaceOver,sBasketCount,bBasketCount);
					} else if (bBasketCount > sBasketCount) {//净赎回
						baksetCount = sBasketCount;
						buyAmountMall(rs,dDate,SGReplaceOver,SHReplaceOver);
					} else {
						continue;
					}
					
				} 
			}
		}catch (Exception e) {
			throw new YssException("处理交易结算明细表数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		
	}

	/**shashijie,2011-7-11 设置交易明细,交易关联对象到集合中 ,STORY 974*/
	private void equalityMakeAmount(ResultSet rs,Date dDate,Date SGReplaceOver,Date SHReplaceOver) 
			throws YssException,SQLException {
		
		while (rs.next()) {
			
			ETFTradeSettleDetailBean etfTradeSettleDetail = new ETFTradeSettleDetailBean();//交易明细
			ETFTradeSettleDetailRefBean trade = new ETFTradeSettleDetailRefBean();//交易结算关联
			setETFTradeSettleDetailBean(rs,etfTradeSettleDetail);//初始交易明细对象
			//获取补票数量
			double replaceAmount = getReplacAmount(rs);
			//原币总成本 = 单位成本（原币）* 数量
			double OMakeUpCost = YssD.round(YssD.mul(rs.getDouble("FPrice"),replaceAmount),2);
			//初始交易结算关联对象,基本信息(数量,单位成本等)
			setETFTradeSettleDetailRefBean(rs,trade,dDate,rs.getDouble("FPrice"),replaceAmount,OMakeUpCost);
			
			//-----------------------------以下处理权益数据---------------------------------//
			//如果分红比例、送股比例或者配股比例不为0时，说明当天有权益
			if(rs.getDouble("DivRatio")!=0||rs.getDouble("shareRatio")!=0
					||rs.getDouble("RigFRatio")!=0){
					trade.setExRightDate(dDate);//权益日期
					trade.setRightRate(YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate")));//权益汇率
			}
			this.doDivdend(trade,rs);//处理分红权益
			this.doBonusShare(trade,rs);//处理送股权益
			this.doRightIssue(trade,rs);//处理配股权益
			//--------------------------------end-------------------------------------------//
			
			//申购
			if(rs.getString("FBS").equalsIgnoreCase("B")){
				//计算可退替代款与应对替代款
				sgCountReplaceCash(rs,trade,dDate,SGReplaceOver);
			}else{//赎回
				//计算可退提贷款与应付替代款
				shCountReplaceCash(rs,trade,dDate,SHReplaceOver);
			}
			//清算标识	清算 -- Y ,未清算 -- N
			trade.setSettleMark("N");
			//数据标识	状态一，补票 -- 0 ,状态二，强制 -- 1
			trade.setDataMark("0");

			etfTradeSettleDetail.getAlTradeSettleDelRef().add(trade);//保存明细关联数据
			tradeSettleDetail.add(etfTradeSettleDetail);//把明细数据存入集合中
		}
	}

	/**shashijie 2011.07.12 STORY 974 ,设置交易明细对象*/
	private void setETFTradeSettleDetailBean(ResultSet rs,
			ETFTradeSettleDetailBean etf) throws YssException {
		try {
			
			etf.setNum(rs.getString("FNum"));//申请编号
			etf.setPortCode(rs.getString("FPortCode"));//组合代码
			etf.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
			etf.setStockHolderCode(rs.getString("FStockHolderCode"));//股东代码
			etf.setBrokerCode(rs.getString("FBrokerCode"));//参与券商
			etf.setSeatCode(rs.getString("FSeatCode"));//交易席位
			etf.setBs(rs.getString("FBs"));//交易类型:类型一，申购-B; 类型二，赎回-S
			etf.setBuyDate(rs.getDate("FBuyDate"));//申购日期
			etf.setReplaceAmount(rs.getDouble("FReplaceAmount"));//替代数量
			etf.setBraketNum(rs.getDouble("FBraketNum"));//篮子数
			etf.setUnitCost(rs.getDouble("FUnitCost"));//单位成本
			etf.setOReplaceCash(rs.getDouble("FOReplaceCash"));//替代金额(原币)
			etf.setHReplaceCash(rs.getDouble("FHReplaceCash"));//替代金额(本币)
			etf.setOcReplaceCash(rs.getDouble("FOCReplaceCash"));//可退替代款(原币)
			etf.setHcReplaceCash(rs.getDouble("FHCReplaceCash"));//可退替代款(本币)
			etf.setExchangeRate(rs.getDouble("FExchangeRate"));//汇率
			etf.setTradeNum(rs.getString("FTradeNum"));//成交编号
			etf.setMarktype(rs.getString("FMarkType"));//标志类型  time = 实时补票  difference = 钆差补票


		} catch (Exception e) {
			dbl.closeResultSetFinal(rs);
			throw new YssException("初始交易结算对象出错！", e);
		}

		
	}

	/**shashijie,2011-7-11,获取钆差补票sql   @param Date 补票日期,makeDate 申赎日期,STORY 974*/
	private String getStrSqlOfGCBP(Date dDate,Date makeDate,String portCode) throws YssException {
		CtlPubPara pubPara=new CtlPubPara();//通用参数实例化
        pubPara.setYssPub(pub);//设置Pub
		String rightsRatioMethods = (String) pubPara.getRightsRatioMethods(portCode);//获取通用参数值
		String strSql = "";
		strSql = "select a.*,b.*, (case when c.FBuyFTradePrice is null then 0 else c.FBuyFTradePrice " +
		     " end) as FBuyFTradePrice ,(case when c.FBuyTradeAmount is null then 0 else c.FBuyTradeAmount " +
		     " end) as FBuyTradeAmount , (case when c.FSaleFTradePrice is null then 0 else c.FSaleFTradePrice " +
		     " end) as FSaleFTradePrice , (case when c.FSaleTradeAmount is null then 0 else c.FSaleTradeAmount " +
		     " end) as FSaleTradeAmount , c.FTradeFee , d.* ,e.FPrice , g.Famount , h.FAllBraketNum , " +
		     (rightsRatioMethods.equalsIgnoreCase("PreTaxRatio") ? //税前利率
			 " f.FPreTaxRatio as DivRatio,sh.FPreTaxRatio as shareRatio,ri.FPreTaxRatio as RigFRatio,"  : 
			 " f.FAfterTaxRatio as DivRatio,sh.FAfterTaxRatio as shareRatio,ri.FAfterTaxRatio as RigFRatio,")+//税后利率
			 " ri.FRIPrice from "+pub.yssGetTableName("Tb_ETF_Tradestldtl")+" a " +//ETF交易结算
		     " left join (select aa1.fsecuritycode,aa1.FTradeCury From "+
		     pub.yssGetTableName("tb_para_security")+" aa1 where aa1.FCheckState = 1 ) b " +//证券信息
		     " on a.fsecuritycode = b.fsecuritycode "+
		     " left join (Select sum((case when a1.FTradeTypeCode = 01 then a1.FTradePrice  end)) as FBuyFTradePrice , "+
		     " sum((case when a1.FTradeTypeCode = 01 then a1.FTradeAmount end)) as FBuyTradeAmount , " +
		     " sum((case when a1.FTradeTypeCode = 02 then a1.FTradePrice  end)) as FSaleFTradePrice ,"+
		     " sum((case when a1.FTradeTypeCode = 02 then a1.FTradeAmount end)) as FSaleTradeAmount ,"+
		     " sum (nvl(a1.FTradeFee1,0) + nvl(a1.FTradeFee2,0)+nvl(a1.FTradeFee3,0)+nvl(a1.FTradeFee4,0)+ " +
             " nvl(a1.FTradeFee5,0)+nvl(a1.FTradeFee6,0)+nvl(a1.FTradeFee7,0)+nvl(a1.FTradeFee8,0)) as FTradeFee , "+
		     " a1.fsecuritycode From "+pub.yssGetTableName("tb_data_subtrade")+" a1 "+
             " where a1.fbargaindate = "+dbl.sqlDate(dDate)+" And a1.FCheckState = 1 "+
             " group by a1.FSecurityCode ) c on a.FSecuritycode = c.FSecuritycode " +
		     " Left Join ( Select a2.FBaseRate,a2.FPortRate,a2.FPortCode,a2.FcuryCode From " +
		     pub.yssGetTableName("tb_data_pretvalrate")+" a2 where a2.FCheckState = 1 " +//估值预处理汇率表
		     " and a2.FValDate = " + dbl.sqlDate(dDate) + " and a2.FPortCode in (" + 
		     this.operSql.sqlCodes(this.portCodes) +" ) ) d  " +
		     " on ( d.FPortCode = a.FPortCode and d.FCuryCode = b.FTradeCury )" + 
		     " Left Join ( select a3.fsecuritycode, a3.fprice ,a3.FValdate From " +
		     pub.yssGetTableName("Tb_Data_PretValMktPrice")+" a3 ,( select max(a7.FValdate) as FValdate " +
		     " ,a7.FSecuritycode From "+pub.yssGetTableName("Tb_Data_PretValMktPrice")+" a7 where a7.FValdate <= " +
		     dbl.sqlDate(dDate)+" Group by a7.fsecuritycode ) a8 where a3.fcheckstate = 1 " +
		     " and a8.FSecuritycode = a3.FSecuritycode and a3.FValdate = a8.FValdate " +
		     " ) e on a.fsecuritycode = e.FSecurityCode " + //估值预处理行情表(查询最新收盘价)
		     " left join (select * from "+pub.yssGetTableName("tb_data_dividend") + //分红数据表
 			 " where FCheckState = 1 and FDividendDate <= "+dbl.sqlDate(dDate)+
 			 " and FDividendDate >= "+dbl.sqlDate(makeDate)+
 			 " ) f on a.fsecuritycode = f.fsecuritycode and a.fbuydate < f.FDividendDate " +
 			 " left join (select * from "+pub.yssGetTableName("tb_data_bonusshare") + //送股数据表
 			 " where FCheckState = 1 and FExrightDate <= " + dbl.sqlDate(dDate)+
 			 " and FExrightDate >= " + dbl.sqlDate(makeDate)+
 			 " ) sh on a.fsecuritycode = sh.FSSecurityCode and a.fbuydate < sh.FExrightDate" +
 			 " left join (select * from "+pub.yssGetTableName("tb_data_rightsissue") + //配股数据表
 			 " where FCheckState = 1 and FExrightDate <= " + dbl.sqlDate(dDate)+
 			 " and FExrightDate >= " + dbl.sqlDate(makeDate)+
 			 " ) ri on a.fsecuritycode = ri.fsecuritycode and a.fbuydate < ri.FExrightDate " +
 			 " left join ( select a4.fsecuritycode,a4.famount from "+pub.yssGetTableName("Tb_ETF_StockList") +//股票篮
 			 " a4 where a4.fdate = " + dbl.sqlDate(makeDate) + " and a4.FCheckState = 1 ) g " +
 			 " on g.fsecuritycode = a.fsecuritycode " +
 			 " left join ( select sum(a6.FBraketnum) as FAllBraketNum From ( select distinct(a5.FTradeNum) ," +
 			 " a5.FBraketNum From "+pub.yssGetTableName("Tb_ETF_Tradestldtl")+" a5 "+//查询申赎篮子总数
             " where a5.FBuyDate = "+dbl.sqlDate(makeDate)+" ) a6 ) h on 1=1 "+
 			 " where a.FBuyDate = " + dbl.sqlDate(makeDate) + " and a.FPortCode in (" + 
		     this.operSql.sqlCodes(this.portCodes)+" ) " +
		     " order by a.FTradeNum,a.FBs,a.FStockHolderCode,a.FSecurityCode ";
		return strSql;
	}

	/**shashijie,2011-7-8 向前推出int BeginSupply个工作日,STORY 974*/
	private Date getWorkDayMake(Date dDate, int beginSupply) throws YssException {
		BaseOperDeal operDeal = new BaseOperDeal();
        operDeal.setYssPub(pub);
		Date makeDate = operDeal.getWorkDay(paramSet.getSHolidayCode(), dDate, beginSupply*-1);
		return makeDate;
	}
	
	/**shashijie 2011.07.11  获取组合代码对应的市场代码区分买卖标志的篮子数 ,STORY 974*/
	public HashMap getTotalETFTradeAmout(Date bargainDate,String portCodes,String OperType) throws YssException {
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
		try {
			strSql = " select sum(FTradeAmount) as FTradeAmount,FSecurityCode,FMark,FPortCode from " + 
					pub.yssGetTableName("Tb_ETF_GHInterface") + " where FBargainDate = " + 
					dbl.sqlDate(bargainDate) + " and FOperType = "+dbl.sqlString(OperType)+" and FPortCode in( " + 
					operSql.sqlCodes(portCodes) + " ) group by FSecurityCode,FMark,FPortCode ";

			rs = dbl.openResultSet(strSql);
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
	
	/**shashijie 2011.07.11 设置节假日对象 ,STORY 974*/
	private void setHoliDay(EachExchangeHolidays holiday,Date dDate,String key,int replace) throws YssException {
		String sRowStr ="";//拼接参数
		
		//拼接参数：节假日代码+当天的偏离天数+操作日期
		sRowStr = (holidays.containsKey(key)?
					(String)holidays.get(key):paramSet.getSHolidayCode()) 
					+ "\t" + replace+ "\t" + YssFun.formatDate(dDate);
		holiday.parseRowStr(sRowStr);//解析参数
		
	}
	
	/**shashijie,2011-7-14  净申购,STORY 974*/
	private void buyAmountMuch(ResultSet rs, Date dDate, Date sGReplaceOver,
			Date sHReplaceOver ,double SGBasketCount,double SHBasketCount) throws YssException,SQLException {
		while (rs.next()) {
			double allcost = 0;//补票总成本原币
			double unitCost = 0;//单位成本
			ETFTradeSettleDetailBean etfTradeSettleDetail = new ETFTradeSettleDetailBean();//交易明细
			ETFTradeSettleDetailRefBean trade = new ETFTradeSettleDetailRefBean();//交易结算关联
			setETFTradeSettleDetailBean(rs,etfTradeSettleDetail);//初始交易明细对象
			//记录第一笔申购数据(方便后面区分是已钆部分还是未钆部分)
			if (rs.getString("FBS").equalsIgnoreCase("B") && tradeNum.equals("")
					&& stockHolderCode.equals("") && bs.equals("")) {
				setNumValue(rs);//存入交易编号,股东代码,申赎类型,组合
			}
			//------------------------以下处理已钆,未钆的单位成本,补票数量----------------------//
			//获取补票数量(算上送股数量)
			double replaceAmount = getReplacAmount(rs);
			//若没有成交价就用收盘价
			if (rs.getDouble("FBuyFTradePrice")==0) {
				//单位成本 = 收盘价
				unitCost = rs.getDouble("FPrice");
				//总成本 = 单位成本 * 补票数量
				allcost = YssD.round(YssD.mul(unitCost,replaceAmount),2);
			} else {
				//净申赎得先计算总原币成本再计算单位成本
				allcost = getAllCost(rs.getDouble("FBuyFTradePrice"),replaceAmount,rs.getDouble("FAllBraketNum")
						,rs.getDouble("FBraketNum"),rs.getDouble("FTradeFee"));
				//获取补票单位成本
				unitCost = getUnitCost(allcost,replaceAmount);
			}
			
			//申购
			if (rs.getString("FBS").equals("B")) {
				//若处理下一笔申购数据时则重新计算已钆未钆处理
				if (!tradeNum.equals(rs.getString("FTradeNum")) ||
						!stockHolderCode.equals(rs.getString("FStockHolderCode")) ||
						!bs.equals(rs.getString("FBs")) || !portCode.equals(rs.getString("FPortCode"))) {
					setNumValue(rs);//存入交易编号,股东代码,申赎类型,组合
				}
				//判断申购篮子书是否在已钆范围内
				if (baksetCount > 0) {//已钆范围
					setETFTradeSettleDetailRefBean(rs,trade,dDate,unitCost,replaceAmount,allcost);
				} else {//未钆范围
					if (baksetCount == 0) {//正好钆完
						//获取补票单位成本
						unitCost = getUnitCost(allcost,rs.getDouble("FBuyTradeAmount"));
						setETFTradeSettleDetailRefBean(rs,trade,dDate,unitCost,rs.getDouble("FBuyTradeAmount"),allcost);
					} else {//若有剩余,则吧买入数量加上已钆掉的数量
						//送股数量
						double amount = YssD.mul(rs.getDouble("Famount"),rs.getDouble("shareRatio"));
						//补票数量 = 买入数量+已钆数量+送股数量
						double TradeAmount = YssD.add(rs.getDouble("FBuyTradeAmount"),rs.getDouble("Famount"),amount);
						//费用所占比例(费用*补票数量/(申赎数+权益实际数量))
						double tradefee = YssD.mul(rs.getDouble("FTradeFee"), YssD.div(TradeAmount, replaceAmount));
						//获取补票总成本
						allcost = getAllCost(rs.getDouble("FBuyFTradePrice"), TradeAmount, 
								rs.getDouble("FAllBraketNum"),rs.getDouble("FBraketNum"),tradefee);
						//获取补票单位成本
						unitCost = getUnitCost(allcost,TradeAmount);
						//净申购时未钆掉的部分用买入成交价计算单位成本(传入参数,单位成本,交易数量即补票数量)
						setETFTradeSettleDetailRefBean(rs,trade,dDate,unitCost,TradeAmount,allcost);
					}
				}
			} else {//赎回
				setETFTradeSettleDetailRefBean(rs,trade,dDate,unitCost,replaceAmount,allcost);
			}
			//------------------------end---处理已钆,未钆的单位成本,补票数量--------------------//
			//-----------------------------以下处理权益数据---------------------------------//
			//如果分红比例、送股比例或者配股比例不为0时，说明当天有权益
			if(rs.getDouble("DivRatio")!=0||rs.getDouble("shareRatio")!=0
					||rs.getDouble("RigFRatio")!=0){
					trade.setExRightDate(dDate);//权益日期
					trade.setRightRate(YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate")));//权益汇率
			}
			this.doDivdend(trade,rs);//处理分红权益
			this.doBonusShare(trade,rs);//处理送股权益
			this.doRightIssue(trade,rs);//处理配股权益
			//--------------------------------end-------------------------------------------//
			//-----------------------------计算可退替代款与应付替代款-------------------------//
			//申购
			if(rs.getString("FBS").equalsIgnoreCase("B")){
				//计算可退替代款与应付替代款
				sgCountReplaceCash(rs,trade,dDate,sGReplaceOver);
			}else {//赎回
				//计算可退替代款与应付替代款
				shCountReplaceCash(rs,trade,dDate,sHReplaceOver);
			}
			//清算标识	清算 -- Y ,未清算 -- N
			trade.setSettleMark("N");
			//数据标识	状态一，补票 -- 0 ,状态二，强制 -- 1
			trade.setDataMark("0");
			//----------------------end----计算可退替代款与应付替代款------------------------//
			//------------------------处理强制处理数据-------------------------------------//
			if(trade.getRemaindAmount()!=0){//剩余数量不为0时，进行强制处理
				//若没有成交价就用收盘价
				if (rs.getDouble("FBuyFTradePrice")==0) {
					//单位成本 = 收盘价
					unitCost = rs.getDouble("FPrice");
					//总成本 = 单位成本 * 剩余数量
					allcost = YssD.round(YssD.mul(unitCost,trade.getRemaindAmount()),2);
				} else {
					//费用所占比例(费用*剩余数量/(申赎数+权益实际数量))
					double tradefee = YssD.mul(rs.getDouble("FTradeFee"), 
							YssD.div(trade.getRemaindAmount(), replaceAmount));
					//净申赎得先计算总原币成本再计算单位成本
					allcost = getAllCost(rs.getDouble("FBuyFTradePrice"),trade.getRemaindAmount()
							,rs.getDouble("FAllBraketNum"),rs.getDouble("FBraketNum"),tradefee);
					//获取单位成本
					unitCost = getUnitCost(allcost,trade.getRemaindAmount());
				}
				//产生强制处理数据
				this.doForceManage(etfTradeSettleDetail,trade,rs,dDate,sGReplaceOver,sHReplaceOver
						,unitCost,allcost);
			}
			//------------------------end 强制处理数据 ------------------------------------//
			
			etfTradeSettleDetail.getAlTradeSettleDelRef().add(trade);//保存明细关联数据
			tradeSettleDetail.add(etfTradeSettleDetail);//把明细数据存入集合中
		}
	}

	/**shashijie,2011-7-14  净赎回,STORY 974*/
	private void buyAmountMall(ResultSet rs, Date dDate, Date sGReplaceOver,
			Date sHReplaceOver) throws YssException,SQLException {
		while (rs.next()) {
			double allcost = 0;//补票总成本原币
			double unitCost = 0;//单位成本
			ETFTradeSettleDetailBean etfTradeSettleDetail = new ETFTradeSettleDetailBean();//交易明细
			ETFTradeSettleDetailRefBean trade = new ETFTradeSettleDetailRefBean();//交易结算关联
			setETFTradeSettleDetailBean(rs,etfTradeSettleDetail);//初始交易明细对象
			//记录第一笔申购数据(方便后面区分是已钆部分还是未钆部分)
			if (rs.getString("FBS").equalsIgnoreCase("B") && tradeNum.equals("")
					&& stockHolderCode.equals("") && bs.equals("")) {
				setNumValue(rs);//存入交易编号,股东代码,申赎类型,组合
			}
			//------------------------以下处理已钆,未钆的单位成本,补票数量----------------------//
			//获取补票数量(算上送股数量)
			double replaceAmount = getReplacAmount(rs);
			//若没有成交价就用收盘价
			if (rs.getDouble("FSaleFTradePrice")==0) {
				//单位成本 = 收盘价
				unitCost = rs.getDouble("FPrice");
				//总成本 = 单位成本 * 补票数量
				allcost = YssD.round(YssD.mul(unitCost,replaceAmount),2);
			} else {
				//净申赎得先计算总原币成本再计算单位成本
				allcost = getAllCost(rs.getDouble("FSaleFTradePrice"),replaceAmount,rs.getDouble("FAllBraketNum")
						,rs.getDouble("FBraketNum"),rs.getDouble("FTradeFee"));
				//获取补票单位成本
				unitCost = getUnitCost(allcost,replaceAmount);
			}
			
			//申购
			if (rs.getString("FBS").equals("S")) {
				//若处理下一笔申购数据时则重新计算已钆未钆处理
				if (!tradeNum.equals(rs.getString("FTradeNum")) ||
						!stockHolderCode.equals(rs.getString("FStockHolderCode")) ||
						!bs.equals(rs.getString("FBs")) || !portCode.equals(rs.getString("FPortCode"))) {
					setNumValue(rs);//存入交易编号,股东代码,申赎类型,组合
				}
				//判断申购篮子书是否在已钆范围内
				if (baksetCount > 0) {//已钆范围
					setETFTradeSettleDetailRefBean(rs,trade,dDate,unitCost,replaceAmount,allcost);
				} else {//未钆范围
					if (baksetCount == 0) {//正好钆完
						//获取补票单位成本
						unitCost = getUnitCost(allcost,rs.getDouble("FSaleTradeAmount"));
						setETFTradeSettleDetailRefBean(rs,trade,dDate,unitCost,rs.getDouble("FSaleTradeAmount"),allcost);
					} else {//若有剩余,则吧买入数量加上已钆掉的数量
						//送股数量
						double amount = YssD.mul(rs.getDouble("Famount"),rs.getDouble("shareRatio"));
						//补票数量 = 买入数量+已钆数量+送股数量
						double TradeAmount = YssD.add(rs.getDouble("FSaleTradeAmount"),rs.getDouble("Famount"),amount);
						//费用所占比例(费用*补票数量/(申赎数+权益实际数量))
						double tradefee = YssD.mul(rs.getDouble("FTradeFee"), YssD.div(TradeAmount, replaceAmount));
						//获取补票总成本
						allcost = getAllCost(rs.getDouble("FSaleFTradePrice"), TradeAmount, 
								rs.getDouble("FAllBraketNum"),rs.getDouble("FBraketNum"),tradefee);
						//获取补票单位成本
						unitCost = getUnitCost(allcost,TradeAmount);
						//净申购时未钆掉的部分用买入成交价计算单位成本(传入参数,单位成本,交易数量即补票数量)
						setETFTradeSettleDetailRefBean(rs,trade,dDate,unitCost,TradeAmount,allcost);
					}
				}
			} else {//赎回
				setETFTradeSettleDetailRefBean(rs,trade,dDate,unitCost,replaceAmount,allcost);
			}
			//------------------------end---处理已钆,未钆的单位成本,补票数量--------------------//
			//-----------------------------以下处理权益数据---------------------------------//
			//如果分红比例、送股比例或者配股比例不为0时，说明当天有权益
			if(rs.getDouble("DivRatio")!=0||rs.getDouble("shareRatio")!=0
					||rs.getDouble("RigFRatio")!=0){
					trade.setExRightDate(dDate);//权益日期
					trade.setRightRate(YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate")));//权益汇率
			}
			this.doDivdend(trade,rs);//处理分红权益
			this.doBonusShare(trade,rs);//处理送股权益
			this.doRightIssue(trade,rs);//处理配股权益
			//--------------------------------end-------------------------------------------//
			//--------------------------计算可退替代款与应付替代款----------------------------//
			//申购
			if(rs.getString("FBS").equalsIgnoreCase("B")){
				//计算可退替代款与应付替代款
				sgCountReplaceCash(rs,trade,dDate,sGReplaceOver);
			}else {//赎回
				//计算可退替代款与应付替代款
				shCountReplaceCash(rs,trade,dDate,sHReplaceOver);
			}
			//清算标识	清算 -- Y ,未清算 -- N
			trade.setSettleMark("N");
			//数据标识	状态一，补票 -- 0 ,状态二，强制 -- 1
			trade.setDataMark("0");
			//------------------end----计算可退替代款与应付替代款------------------------------//
			//------------------------处理强制处理数据-------------------------------------//
			if(trade.getRemaindAmount()!=0){//剩余数量不为0时，进行强制处理
				//若没有成交价就用收盘价
				if (rs.getDouble("FSaleFTradePrice")==0) {
					//单位成本 = 收盘价
					unitCost = rs.getDouble("FPrice");
					//总成本 = 单位成本 * 剩余数量
					allcost = YssD.round(YssD.mul(unitCost,trade.getRemaindAmount()),2);
				} else {
					//费用所占比例(费用*剩余数量/(申赎数+权益实际数量))
					double tradefee = YssD.mul(rs.getDouble("FTradeFee"), 
							YssD.div(trade.getRemaindAmount(), replaceAmount));
					//净申赎得先计算总原币成本再计算单位成本
					allcost = getAllCost(rs.getDouble("FSaleFTradePrice"),trade.getRemaindAmount()
							,rs.getDouble("FAllBraketNum"),rs.getDouble("FBraketNum"),tradefee);
					//获取单位成本
					unitCost = getUnitCost(allcost,trade.getRemaindAmount());
				}
				//产生强制处理数据
				this.doForceManage(etfTradeSettleDetail,trade,rs,dDate,sGReplaceOver,sHReplaceOver
						,unitCost,allcost);
			}
			//--------------------------end 强制处理数据 ------------------------------------//
			
			etfTradeSettleDetail.getAlTradeSettleDelRef().add(trade);//保存明细关联数据
			tradeSettleDetail.add(etfTradeSettleDetail);//把明细数据存入集合中
		}
	}
	
	/**shashijie,2011-7-15 初始交易结算关联对象,基本信息(数量,单位成本等) STORY 974
	 * @param FPrice 单位成本
	 * @param dDate 申赎日期
	 * @param ReplaceAmount 补票数量
	 * @param OMakeUpCost 总成本*/
	private void setETFTradeSettleDetailRefBean(ResultSet rs,
			ETFTradeSettleDetailRefBean trade,Date dDate,double price,double ReplaceAmount,
			double OMakeUpCost) throws YssException,SQLException {
		trade.setNum(rs.getString("FNum"));//明细关联数据的申请编号
		trade.setRefNum("1");//关联编号：主要是区分：1-第一次补票，2-第二次补票，3-强制处理
		trade.setMakeUpDate(dDate);//补票日期
		trade.setMakeUpAmount(ReplaceAmount);//补票数量
		//单位成本原币 = price(收盘价,成交价计算结果,卖出价计算结果)
		trade.setUnitCost(price);
		//原币总成本 = 单位成本（原币）* 数量
		trade.setoMakeUpCost(OMakeUpCost);
		//估值汇率       基础汇率/组合汇率
		double exchangeRate = YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate"));
		trade.setExchangeRate(exchangeRate);
		//本币总成本 = 原币总成本 * 汇率  
		double HMakeUpCost = YssD.round(YssD.mul(trade.getoMakeUpCost(),trade.getExchangeRate()),2);
		trade.sethMakeUpCost(HMakeUpCost);
		//送股总数量 = 申赎数量* 送股权益比例
		double shareAllAmount = YssD.mul(rs.getDouble("FReplaceAmount"),rs.getDouble("shareRatio"));
		//剩余数量 = （申赎数量 + 送股实际数量） - 第一次补票数量 
		trade.setRemaindAmount(YssD.sub(YssD.add(rs.getDouble("FReplaceAmount"),
				shareAllAmount),trade.getMakeUpAmount()));
	}
	
	

	/**shashijie,2011-7-15 计算可退替代款与应付替代款(申购)STORY 974*/
	/**shashijie,2011-7-16,计算申购可退替代款与应付替代款 STORY 974*/
	private void sgCountReplaceCash(ResultSet rs,
			ETFTradeSettleDetailRefBean trade, Date dDate,Date SGReplaceOver) 
			throws YssException,SQLException{
		//替代款金额原币    =	替代金额原币 - 派息（原币）- 权证价值（原币）
		double OReplaceCash = YssD.sub(rs.getDouble("FOReplaceCash"),trade.getInterest(),
				trade.getWarrantCost());
		//申赎数量	=	替代数量    +  总数量
		double amount = YssD.add(rs.getDouble("FReplaceAmount"),trade.getSumAmount());
		//应付替代款（原币） = 替代金额原币 * 补票数量/申赎数量 - 补票总成本原币 
		trade.setOpReplaceCash(YssD.sub(YssD.round(YssD.div(
				YssD.mul(OReplaceCash,trade.getMakeUpAmount()),amount),2),
				trade.getoMakeUpCost()));
		
		//替代款金额本币    =  申赎数据替代金额（本币）-权益数据总派息（本币）- 权益数据权证价值（本币）
		double HReplaceCash = YssD.sub(rs.getDouble("FHReplaceCash"),trade.getBbinterest(), 
				trade.getBbwarrantCost());
		//申赎数量   =  替代数量+ 权益数据总数量
		amount = YssD.add(rs.getDouble("FReplaceAmount"),trade.getSumAmount());
		//应付替代款（本币） = 替代金额本币 * 补票数量/申赎数量 - 补票总成本本币
		trade.setHpReplaceCash(YssD.sub(YssD.round(YssD.div(
				YssD.mul(HReplaceCash,trade.getMakeUpAmount()),amount),2),
				trade.gethMakeUpCost()));
		//补票可退替代款 （原币）     =   补票数量     /  申赎数量+(权益)送股总数量     *  申赎可退替代款(原币)
		double replaceOC = YssD.mul(YssD.div(trade.getMakeUpAmount(),amount),
				rs.getDouble("FOCReplaceCash"));
		trade.setOcReplaceCash(replaceOC);
		//可退替代款余额（原币） = 申赎数据可退替代款 （原币）- 补票可退替代款 （原币）
		trade.setOCanRepCash(YssD.round(YssD.sub(replaceOC,
				rs.getDouble("FOCReplaceCash")),2));
		//补票可退替代款 （本币）     =   补票数量     /  申赎数量 +(权益)送股总数量    *  申赎可退替代款(本币)
		double replaceHC = YssD.mul(YssD.div(trade.getMakeUpAmount(),amount),
				rs.getDouble("FHCReplaceCash"));
		trade.setHcReplaceCash(replaceHC);
		//可退替代款余额（本币） = 申赎数据可退替代款 （本币）- 补票可退替代款 （本币）
		trade.setHCanRepCash(YssD.round(YssD.sub(replaceHC,
				rs.getDouble("FHCReplaceCash")),2));
		
		//数据方向
		trade.setDataDirection("1");
		//退款日期							
		trade.setRefundDate(SGReplaceOver);
	}
	
	
	/**shashijie,2011-7-15  计算可退替代款与应付替代款(赎回)STORY 974*/
	/**shashijie,2011-7-16,计算赎回可退替代款与应付替代款STORY 974*/
	private void shCountReplaceCash(ResultSet rs,
			ETFTradeSettleDetailRefBean trade, Date dDate, Date sHReplaceOver) 
			throws YssException,SQLException {
		//应付替代款（原币） = 补票总成本原币 + 总派息原币* 卖出数量/申赎数量  + 权证价值原币* 卖出数量/申赎数量 
		trade.setOpReplaceCash(YssD.add(
				trade.getoMakeUpCost(),
				YssD.round(YssD.div(YssD.mul(trade.getMakeUpAmount(),trade.getInterest()),
									rs.getDouble("FReplaceAmount")),
									2),
				YssD.round(YssD.div(YssD.mul(trade.getMakeUpAmount(),trade.getWarrantCost()),
									rs.getDouble("FReplaceAmount")),
									2)));
		
		//应付替代款（本币） = 补票总成本本币 + 总派息本币* 卖出数量/申赎数量 * 卖出汇率 + 权证价值本币* 卖出数量/申赎数量 * 卖出汇率
		trade.setHpReplaceCash(YssD.add(
				trade.gethMakeUpCost(),
				YssD.round(YssD.mul(YssD.div(
									YssD.mul(trade.getMakeUpAmount(),trade.getBbinterest()),
									rs.getDouble("FReplaceAmount")),
									trade.getExchangeRate()),
									2),
				YssD.round(YssD.mul(YssD.div(
									YssD.mul(trade.getMakeUpAmount(),trade.getBbwarrantCost()),
									rs.getDouble("FReplaceAmount")),
									trade.getExchangeRate()),
									2)));
		
		//补票可退替代款(原币) = 卖出数量 /(赎回数量+权益实际数量)*可退赎回款（原币）
		double OcReplaceCash = YssD.mul(YssD.div(trade.getMakeUpAmount(), 
				YssD.add(rs.getDouble("FReplaceAmount"),trade.getRealAmount())),
				rs.getDouble("FOCReplaceCash"));
		trade.setOcReplaceCash(OcReplaceCash);
		//可退替代款余额（原币） = 申赎数据可退替代款 （原币）- 第一次补票可退替代款 （原币） 
		trade.setOCanRepCash(YssD.round(YssD.sub(OcReplaceCash,
				rs.getDouble("FOCReplaceCash")),2));
		//补票可退替代款(本币) = 卖出数量 /(赎回数量+权益实际数量)*可退赎回款（本币）
		double HcReplaceCash = YssD.mul(YssD.div(trade.getMakeUpAmount(), 
				YssD.add(rs.getDouble("FReplaceAmount"),trade.getRealAmount())),
				rs.getDouble("FHCReplaceCash"));
		trade.setHcReplaceCash(HcReplaceCash);
		//可退替代款余额（本币） = 申赎数据可退替代款 （本币）- 第一次补票可退替代款 （本币）
		trade.setHCanRepCash(YssD.round(YssD.sub( HcReplaceCash,
				rs.getDouble("FHCReplaceCash")),2));
		
		//数据方向
		trade.setDataDirection("-1");
		//退款日期
		trade.setRefundDate(sHReplaceOver);
	}
	
	
	/**获取补票数量(算上送股数量) STORY 974*/
	private double getReplacAmount(ResultSet rs) throws YssException,SQLException {
		double replaceAmount = rs.getDouble("FReplaceAmount");//申赎数量
		if(rs.getDouble("shareRatio")!=0){//若有送股则补票数量得加上送股数量
			replaceAmount += YssD.mul(replaceAmount,rs.getDouble("shareRatio"));
		}
		return replaceAmount;
	}

	/**shashijie,2011-7-16,存入交易编号,股东代码,申赎类型,组合 STORY 974*/
	private void setNumValue(ResultSet rs) throws YssException , SQLException {
		tradeNum = rs.getString("FTradeNum");//交易编号
		stockHolderCode = rs.getString("FStockHolderCode");//股东代码
		bs = rs.getString("FBs");//交易方式
		portCode = rs.getString("FPortCode");//组合代码
		baksetCount = YssD.sub(baksetCount , rs.getDouble("FBraketNum"));//钆差篮子数
	}

	/**shashijie 2011-07-18,计算单位成本  STORY 974
	 * @param allCost 总成本 
	 * @param replaceAmount 补票数量
	 * */
	private double getUnitCost(double allCost, double replaceAmount) throws YssException {
		//单位成本	=  总成本/补票数量
		double unitCost = YssD.round(YssD.div(allCost,replaceAmount),2);
		return unitCost;
	}
	
	/**shashijie,2011-7-18 ,计算总原币成本,STORY 974 
	 * @param TradePrice 交易成交价格
	 * @param replaceAmount 补票数量
	 * @param allBraketNum 总申赎篮子数
	 * @param braketNum 单次申赎篮子数
	 * @param tradeFee 费用*/
	private double getAllCost(double TradePrice, double replaceAmount,
			double allBraketNum, double braketNum,double tradeFee) throws YssException {
		//总成本 = 成交价*补票数量+均摊费用
		double allCost = 0;
		//均摊费用    =  费用/总申赎篮子书*单笔申赎篮子数
		double fee = YssD.mul(YssD.div(tradeFee, allBraketNum),braketNum);
		//成交价*补票数量
		double cost = YssD.mul(TradePrice, replaceAmount);
		//总成本
		allCost = YssD.add(cost, fee);
		return allCost;
	}

}