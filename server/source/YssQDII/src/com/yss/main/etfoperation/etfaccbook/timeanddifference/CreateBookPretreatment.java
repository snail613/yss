package com.yss.main.etfoperation.etfaccbook.timeanddifference;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.etfaccbook.CreateBookPretreatmentAdmin;
import com.yss.main.etfoperation.etfaccbook.CtlETFAccBook;
import com.yss.main.etfoperation.etfaccbook.PretValMktPriceAndExRate;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFSubTradeBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailRefBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * xuqiji 20091116 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
 * 此类做生成台账预处理，主要是生成交易明细和交易明细关联数据，保存到明细表（tb_etf_tradestldtl）和明细关联表（tb_etf_tradstldtlref）
 * @author 
 *
 */
public class CreateBookPretreatment extends CtlETFAccBook{
	private ArrayList tradeSettleDetail = new ArrayList();//保存操作日期当天明细关联数据
	private ArrayList tradeSettleDetailDifference = new ArrayList();//保存操作日期当天明细关联数据中钆差补票的数据
	private ArrayList tradeSettleDetailYesDate = new ArrayList();//保存操作日前一天的明细及关联数据
	private ArrayList etfSubTradeAL= new ArrayList();//保存主动投资和被动投资数据
	private ETFParamSetBean paramSet = null;// ETF参数的实体类
	private ETFParamSetAdmin paramSetAdmin = null;
	private HashMap etfParam = null;//保存参数设置
	private HashMap holidays = null;//保存节假日群代码
	
	private String securityCodes = "";//证券代码
	private java.util.Date startDate = null;//开始的申赎日期
	private java.util.Date endDate = null;//结束的申赎日期	
	private java.util.Date tradeDate = null;//估值日期
	private String standingBookType = "";//台账类型：申购-B，赎回-S
	private String portCodes = "";//组合代码
	
	public CreateBookPretreatment() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 处理业务的入口方法
	 * @throws YssException
	 */
	public void doManageAll() throws YssException{
		int days = 0;//保存循环日期之差
		String[] sPortcode = null;//保存多个组合代码的数据
		Date dDate = null;//日期
		PretValMktPriceAndExRate marketValue = null;//预处理估值行情和估值汇率
		CreateBookPretreatmentAdmin booPreAdmin = null;//明细数据和明细关联数据 保存数据操作类
		EachExchangeHolidays holiday = null;//节假日代码
		String sRowStr ="";
		Date theDate = null;//操作日当天的是否是工作日
		try{
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			
			booPreAdmin = new CreateBookPretreatmentAdmin();
			booPreAdmin.setYssPub(pub);
			
			marketValue = new PretValMktPriceAndExRate();//实例化
			marketValue.setYssPub(pub);//设置pub 
			sPortcode = this.getPortCodes().split(",");//拆分多个组合代码
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
				this.doTradestldtl(dDate);//此方法处理交易结算明细表数据-申赎数据及当天的补票数据
				this.doTimeOrdifferenceDivide(dDate);//处理钆差补票的数据拆分
				if(paramSet.getDealDayNum()>0){//补票完成天数参数设置大于0时，才可以进行多次补票
					this.doYesDatetimeSupplyData(dDate);//此方法处理操作日前一个工作日的实时补票数据，权益数据和强制处理数据
					this.doYesDatedifferenceSupplyData(dDate);//此方法处理操作日前一个工作日的钆差补票数据，权益数据和强制处理数据
				}
				//------------------------往明细表和关联表中插入数据-------------------------//
				if(tradeSettleDetail.size()>0){
					booPreAdmin.insertTheDateData(dDate,this.portCodes,tradeSettleDetail);//插入当天申购赎回数据和关联数据
				}
				if(tradeSettleDetailYesDate.size()>0){
					booPreAdmin.insertYesDateData(dDate,this.portCodes,tradeSettleDetailYesDate,paramSet);//插入前一日申购赎回的补票数据，权益数据和强制处理数据
				}
				//---------------------------end-----------------------------------------//
				dDate = YssFun.addDay(dDate,1);//每一次循环把日期加一天
			}
			
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	/**
	 * 此方法做解析前台传来数据，在调用此方法时就实例化一些全局变量和类
	 */
	public void initData(Date startDate,Date endDate,Date tradeDate ,String portCodes,String standingBookType) throws YssException{
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

	/**
	 * 此方法处理交易结算明细表数据-申赎数据及当天的补票数据
	 * @throws YssException
	 */
	private void doTradestldtl(Date dDate) throws YssException{
		StringBuffer buff = null;//做拼接SQL语句
		ResultSet rs = null;//声明结果集
		long sNum = 0;//做拼接申请编号用
		String strNumDate = "";//保存申请编号
		ETFTradeSettleDetailBean etfTradeSettleDetail= null;//交易明细
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;//交易明细关联
		double makeUpAmount = 0;//补票数量
		double replaceScale = 0;//被动投资比例
		EachExchangeHolidays holiday = null;//节假日代码类
		String sRowStr ="";
		Date SGReplaceOver = null;//申购应付替代结转
		Date SHReplaceOver = null;//赎回应付替代结转

		try{
			if(tradeSettleDetail.size()!=0){//如果集合不为空时，要先清空
				tradeSettleDetail.clear();
			}
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			//拼接参数：节假日代码+当天的偏离天数+操作日期
			sRowStr = (holidays.containsKey(YssOperCons.YSS_ETF_OVERTYPE_SGDEALREPLACE)?(String)holidays.get(YssOperCons.YSS_ETF_OVERTYPE_SGDEALREPLACE):paramSet.getSHolidayCode()) + "\t" + paramSet.getISGDealReplace()+ "\t" + YssFun.formatDate(dDate);
			holiday.parseRowStr(sRowStr);//解析参数
			SGReplaceOver = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//申购应付替代结转日期
			//拼接参数：节假日代码+当天的偏离天数+操作日期
			sRowStr = (holidays.containsKey(YssOperCons.YSS_ETF_OVERTYPE_SHDEALREPLACE)?(String)holidays.get(YssOperCons.YSS_ETF_OVERTYPE_SHDEALREPLACE):paramSet.getSHolidayCode()) + "\t" + paramSet.getISHDealReplace() + "\t" + YssFun.formatDate(dDate);
			holiday.parseRowStr(sRowStr);//解析参数
			SHReplaceOver = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//赎回应付替代结转日期
			
			
			buff = new StringBuffer(2000);

			buff.append(" select gh.*,js.FStockholderCode as FStockholder,js.FClearCode,");
			buff.append(" (case when sub.FtradeAmount is null then 0 else sub.FtradeAmount end)as FsubAmount, ");
			buff.append(" sub.FTotalCost,mk.FPrice,ra.* from ").append(" (select * from ");
			buff.append(pub.yssGetTableName("tb_etf_ghinterface")).append(" b ");//过户库
			buff.append(" left join (select st.fsecuritycode as securitycode,st.FAmount,st.fpremiumscale,st.FTotalMoney,st.fportcode as portCode from ");
			buff.append(pub.yssGetTableName("tb_etf_stocklist")).append(" st ").append(" where st.FCheckState = 1");
			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and st.FDate = ").append(dbl.sqlDate(dDate));
			buff.append(" ) a on b.fportcode = a.portCode");
			buff.append(" where b.fcheckstate = 1 and b.fportcode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and b.FOperType = '2ndcode'").append(" and b.fbargaindate = ").append(dbl.sqlDate(dDate));
			buff.append(" ) gh");
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_data_subtrade"));//业务资料表
			buff.append(" where FCheckState = 1 and FAppDate =").append(dbl.sqlDate(dDate));
			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and fbargaindate =").append(dbl.sqlDate(dDate)).append(" ) sub on sub.fsecuritycode = gh.securitycode");
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
				tradeSettleDelRef = new ETFTradeSettleDetailRefBean();//交易结算明细关联表
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
				etfTradeSettleDetail.setReplaceAmount(YssD.mul(YssD.div(rs.getDouble("FTradeAmount"),paramSet.getNormScale()),rs.getDouble("FAmount")));
				etfTradeSettleDetail.setBraketNum(YssD.div(rs.getDouble("FTradeAmount"),paramSet.getNormScale()));//篮子数
				etfTradeSettleDetail.setUnitCost(rs.getDouble("FPrice"));//单位成本
				//汇率
				etfTradeSettleDetail.setExchangeRate(YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate")));
				if(rs.getString("FMark").equalsIgnoreCase("S")){ //s-是过户库中申购（系统中表示赎回）
					//替代金额本币 = round(股票篮中替代金额 * （1+溢价比例）,2)* 篮子数
					etfTradeSettleDetail.setHReplaceCash(YssD.mul(YssD.round(YssD.mul(rs.getDouble("FTotalMoney"),YssD.add(1,rs.getDouble("fpremiumscale"))),2),etfTradeSettleDetail.getBraketNum()));
					//替代金额原币
					etfTradeSettleDetail.setOReplaceCash(YssD.round(YssD.div(YssD.mul(etfTradeSettleDetail.getHReplaceCash(),rs.getDouble("FPortRate")),rs.getDouble("FBaseRate")),2));
					//可退替代款原币 =替代金额原币 - 替代数量 * 单位成本
					etfTradeSettleDetail.setOcReplaceCash(YssD.round(YssD.sub(etfTradeSettleDetail.getOReplaceCash(),YssD.mul(etfTradeSettleDetail.getReplaceAmount(),etfTradeSettleDetail.getUnitCost())),2));
					//可退替代款本币 = 替代金额本币 - 替代数量 * 单位成本 * 基础汇率/组合汇率
					etfTradeSettleDetail.setHcReplaceCash(YssD.sub(etfTradeSettleDetail.getHReplaceCash(),YssD.round(YssD.div(YssD.mul(etfTradeSettleDetail.getReplaceAmount(),etfTradeSettleDetail.getUnitCost(),rs.getDouble("FBaseRate")),rs.getDouble("FPortRate")),2)));
				
				}else{
					//应退赎回款（原币） = 篮子中的股票数量*股票的T日收盘价
					etfTradeSettleDetail.setOReplaceCash(YssD.round(YssD.mul(etfTradeSettleDetail.getReplaceAmount(),rs.getDouble("FPrice")),2));
					//应退赎回款（本币） = 篮子中的股票数量*股票的T日收盘价*T日的估值汇率 
					etfTradeSettleDetail.setHReplaceCash(YssD.round(YssD.div(YssD.mul(etfTradeSettleDetail.getReplaceAmount(),rs.getDouble("FPrice"),rs.getDouble("FBaseRate")),rs.getDouble("FPortRate")),2));
					//可退替代款原币 =替代金额原币
					etfTradeSettleDetail.setOcReplaceCash(etfTradeSettleDetail.getOReplaceCash());
					//可退替代款本币 = 替代金额本币
					etfTradeSettleDetail.setHcReplaceCash(etfTradeSettleDetail.getHReplaceCash());
				}
				
				etfTradeSettleDetail.setTradeNum(rs.getString("FTradeNum"));//成交编号
				if(rs.getDouble("FsubAmount")!=0){//标志类型
					etfTradeSettleDetail.setMarktype("time");//实时补票
				}else{
					etfTradeSettleDetail.setMarktype("difference");//钆差补票
				}
				//-----------------------------------end--------------------------------//
				//-----------------------------------明细关联数据赋值------------------------------//
				if(rs.getDouble("FsubAmount")!=0){//如果当天交易数据的成交数量不为0
					tradeSettleDelRef.setNum(strNumDate);//明细关联数据的申请编号
					tradeSettleDelRef.setRefNum("1");//关联编号：主要是区分：1-第一次补票，2-第二次补票，3-强制处理
					tradeSettleDelRef.setMakeUpDate(dDate);//补票日期
					//该股票的申赎数量 与 当天交易数据的成交数量之差
					makeUpAmount = YssD.sub(etfTradeSettleDetail.getReplaceAmount(),rs.getDouble("FsubAmount"));
					//补票数量
					if(makeUpAmount <=0){//当天该笔证券的交易数量大于申赎数量，即有主动投资时
						tradeSettleDelRef.setMakeUpAmount(etfTradeSettleDetail.getReplaceAmount());
						//被动投资比例= 申赎数量/当天该股票的交易数量
						replaceScale = YssD.div(etfTradeSettleDetail.getReplaceAmount(),rs.getDouble("FsubAmount"));
						//单位成本原币 = 交易表中 实收实付金额/交易数量
						tradeSettleDelRef.setUnitCost(YssD.round(YssD.div(rs.getDouble("FTotalCost"),rs.getDouble("FsubAmount")),2));
						//原币总成本 = 总成本（原币）* 被动投资比例
						tradeSettleDelRef.setoMakeUpCost(YssD.round(YssD.mul(rs.getDouble("FTotalCost"),replaceScale),2));
						//估值汇率
						tradeSettleDelRef.setExchangeRate(YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate")));
						
						//本币总成本 = 原币总成本 * 基础汇率/组合汇率 * 被动投资比例
						tradeSettleDelRef.sethMakeUpCost(YssD.round(YssD.mul(YssD.round(YssD.mul(rs.getDouble("FTotalCost"),tradeSettleDelRef.getExchangeRate()),2),replaceScale),2));
						
						//剩余数量 = 申赎数量 - 第一次补票数量
						tradeSettleDelRef.setRemaindAmount(0);
						
					}else{
						replaceScale = 1;
						tradeSettleDelRef.setMakeUpAmount(rs.getDouble("FsubAmount"));
						//单位成本原币 = 交易表中 实收实付金额/交易数量
						tradeSettleDelRef.setUnitCost(YssD.round(YssD.div(rs.getDouble("FTotalCost"),rs.getDouble("FsubAmount")),2));
						//原币总成本
						tradeSettleDelRef.setoMakeUpCost(rs.getDouble("FTotalCost"));
						//估值汇率
						tradeSettleDelRef.setExchangeRate(YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate")));
						
						//本币总成本 = 原币总成本 * 基础汇率/组合汇率
						tradeSettleDelRef.sethMakeUpCost(YssD.round(YssD.mul(rs.getDouble("FTotalCost"),tradeSettleDelRef.getExchangeRate()),2));
						
						//剩余数量 = 申赎数量 - 第一次补票数量
						tradeSettleDelRef.setRemaindAmount(YssD.sub(etfTradeSettleDetail.getReplaceAmount(),tradeSettleDelRef.getMakeUpAmount()));

					}
					if(rs.getString("FMark").equalsIgnoreCase("S")){
						//应付替代款（原币） = 替代款金额原币 * 补票数量/申赎数量 - 补票总成本原币 
						tradeSettleDelRef.setOpReplaceCash(YssD.sub(YssD.round(YssD.div(YssD.mul(etfTradeSettleDetail.getOReplaceCash(),tradeSettleDelRef.getMakeUpAmount()),etfTradeSettleDetail.getReplaceAmount()),2),tradeSettleDelRef.getoMakeUpCost()));
						//应付替代款（本币） = 可退替代款本币 * 补票数量/申赎数量 - 补票总成本本币
						tradeSettleDelRef.setHpReplaceCash(YssD.sub(YssD.round(YssD.div(YssD.mul(etfTradeSettleDetail.getHReplaceCash(),tradeSettleDelRef.getMakeUpAmount()),etfTradeSettleDetail.getReplaceAmount()),2),tradeSettleDelRef.gethMakeUpCost()));
						//数据方向
						tradeSettleDelRef.setDataDirection("1");
						//退款日期							
						tradeSettleDelRef.setRefundDate(SGReplaceOver);
					}else{
						//应付替代款（原币） = 补票总成本原币 
						tradeSettleDelRef.setOpReplaceCash(tradeSettleDelRef.getoMakeUpCost());
						//应付替代款（本币） = 补票总成本本币
						tradeSettleDelRef.setHpReplaceCash(tradeSettleDelRef.gethMakeUpCost());
						
						//数据方向
						tradeSettleDelRef.setDataDirection("-1");
						//退款日期
						tradeSettleDelRef.setRefundDate(SHReplaceOver);
					}
					//可退替代款（原币）=可退替代款原币 * 补票数量/申赎数量  
					tradeSettleDelRef.setOcRefundSum(YssD.round(YssD.div(YssD.mul(etfTradeSettleDetail.getOcReplaceCash(),tradeSettleDelRef.getMakeUpAmount()),etfTradeSettleDetail.getReplaceAmount()),2));
					//可退替代款（本币）=可退替代款本币 * 补票数量/申赎数量 
					tradeSettleDelRef.setHcReplaceCash(YssD.round(YssD.div(YssD.mul(etfTradeSettleDetail.getHcReplaceCash(),tradeSettleDelRef.getMakeUpAmount()),etfTradeSettleDetail.getReplaceAmount()),2));
					
					//清算标识
					tradeSettleDelRef.setSettleMark("N");
					//数据标识
					tradeSettleDelRef.setDataMark("0");
					//----------------------------------end-----------------------------------------//
					etfTradeSettleDetail.getAlTradeSettleDelRef().add(tradeSettleDelRef);//保存明细关联数据
				}else{//如果当天没有补票的交易数据
					tradeSettleDelRef.setNum(strNumDate);//明细关联数据的申请编号
					tradeSettleDelRef.setRefNum("1");//关联编号：主要是区分：1-第一次补票，2-第二次补票，3-强制处理
					tradeSettleDelRef.setMakeUpDate(dDate);//补票日期
					tradeSettleDelRef.setMakeUpAmount(0);
					//单位成本原币 = 交易表中 实收实付金额/交易数量
					tradeSettleDelRef.setUnitCost(0);
					//原币总成本 = 总成本（原币）* 被动投资比例
					tradeSettleDelRef.setoMakeUpCost(0);
					//估值汇率
					tradeSettleDelRef.setExchangeRate(YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate")));
					
					//本币总成本 = 原币总成本 * 基础汇率/组合汇率 * 被动投资比例
					tradeSettleDelRef.sethMakeUpCost(0);
					
					//剩余数量 = 申赎数量 
					tradeSettleDelRef.setRemaindAmount(etfTradeSettleDetail.getReplaceAmount());
					
					if(rs.getString("FMark").equalsIgnoreCase("S")){
						//应付替代款（原币） = 替代款金额原币 * 补票数量/申赎数量 - 补票总成本原币 
						tradeSettleDelRef.setOpReplaceCash(0);
						//应付替代款（本币） = 可退替代款本币 * 补票数量/申赎数量 - 补票总成本本币
						tradeSettleDelRef.setHpReplaceCash(0);
						//数据方向
						tradeSettleDelRef.setDataDirection("1");
						//退款日期							
						tradeSettleDelRef.setRefundDate(SGReplaceOver);
					}else{
						//应付替代款（原币） = 补票总成本原币 
						tradeSettleDelRef.setOpReplaceCash(0);
						//应付替代款（本币） = 补票总成本本币
						tradeSettleDelRef.setHpReplaceCash(0);
						
						//数据方向
						tradeSettleDelRef.setDataDirection("-1");
						//退款日期
						tradeSettleDelRef.setRefundDate(SHReplaceOver);
					}
					//可退替代款（原币）=可退替代款原币 * 补票数量/申赎数量  
					tradeSettleDelRef.setOcRefundSum(0);
					//可退替代款（本币）=可退替代款本币 * 补票数量/申赎数量 
					tradeSettleDelRef.setHcReplaceCash(0);
					
					//清算标识
					tradeSettleDelRef.setSettleMark("N");
					//数据标识
					tradeSettleDelRef.setDataMark("0");
					//----------------------------------end-----------------------------------------//
					etfTradeSettleDetail.getAlTradeSettleDelRef().add(tradeSettleDelRef);//保存明细关联数据
				}
				tradeSettleDetail.add(etfTradeSettleDetail);//把明细数据存入集合中
			}			
		}catch (Exception e) {
			throw new YssException("处理交易结算明细表数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	/**
	 * 处理钆差补票的数据拆分
	 * @param date
	 */
	private void doTimeOrdifferenceDivide(Date dDate) throws YssException{
		StringBuffer buff =null;
		ResultSet rs = null;
		double dSHBarketAmount = 0;//赎回篮子数
		double dSGBarketAmount = 0;//申购篮子数
		double dBarketAmount = 0;//申赎篮子数，即当天是净申购还是净赎回
		String sSHMark = "";//申赎标志，表示当天是净申购还是净赎回
		ETFTradeSettleDetailBean etfTradeSettleDetail= null;//交易明细
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;//交易明细关联

		double dDivideAmount = 0;//多个投资者时，哪个投资者要拆分多少篮子数
		long sNum = 0;//做拼接申请编号用
		String strNumDate = "";//保存申请编号
		double dBarketsSacle = 1;//拆分篮子比例
		try{
			buff = new StringBuffer(500);
			buff.append(" select sum((case when fmark = 'S' then ftradeamount else -ftradeamount end) / fnormscale) as ftradeamount,");
			buff.append(" (case when fmark = 'S' then 'B' else 'S' end) as fmark from (select gh.*, pa.fnormscale from ");
			buff.append(pub.yssGetTableName("tb_etf_ghinterface")).append(" gh ");
			buff.append(" join (select * from ").append(pub.yssGetTableName("tb_etf_param"));
			buff.append(" where FCheckState = 1 and fportcode in (").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" ) pa on gh.fportcode = pa.fportcode where fbargaindate = ").append(dbl.sqlDate(dDate));
			buff.append(" and gh.fcheckstate = 1 and gh.fportcode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and FOperType = '2ndcode' and not exists (select * from ").append(pub.yssGetTableName("tb_data_subtrade")).append(" su ");
			buff.append(" where fbargaindate = ").append(dbl.sqlDate(dDate));
			buff.append(" and fcheckstate = 1 and fportcode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and su.fdealnum = gh.ftradenum and su.fappdate = gh.fbargaindate)) a  group by a.fportcode, a.fmark");
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			
			while(rs.next()){
				if(rs.getString("fmark").equalsIgnoreCase("S")){//赎回
					dSHBarketAmount = rs.getDouble("ftradeamount");
				}else{
					dSGBarketAmount = rs.getDouble("ftradeamount");
				}
				dBarketAmount += rs.getDouble("ftradeamount");
			}
			if(dBarketAmount != 0){
				if(dBarketAmount > 0){
					sSHMark = "S";//申购
				}else{
					sSHMark = "B";
				}
				dbl.closeResultSetFinal(rs);
				
				buff.append(" select gh.ftradenum,gh.ftradeamount / pa.fnormscale as ftradeamount,gh.fmark from ");
				buff.append(pub.yssGetTableName("tb_etf_ghinterface")).append(" gh ");
				buff.append(" join (select * from ").append(pub.yssGetTableName("tb_etf_param"));
				buff.append(" where FCheckState = 1 and fportcode in (").append(this.operSql.sqlCodes(this.portCodes)).append(")");
				buff.append(" ) pa on gh.fportcode = pa.fportcode where fbargaindate = ").append(dbl.sqlDate(dDate));
				buff.append(" and gh.fcheckstate = 1 and gh.fportcode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
				buff.append(" and fmark = ").append(dbl.sqlString(sSHMark));
				buff.append(" and FOperType = '2ndcode' and not exists (select * from ").append(pub.yssGetTableName("tb_data_subtrade")).append(" su ");
				buff.append(" where fbargaindate = ").append(dbl.sqlDate(dDate));
				buff.append(" and fcheckstate = 1 and fportcode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
				buff.append(" and su.fdealnum = gh.ftradenum and su.fappdate = gh.fbargaindate) order by gh.ftradenum ");
				
				rs = dbl.openResultSet(buff.toString());
				buff.delete(0,buff.length());
				
				while(rs.next()){
					dDivideAmount += rs.getDouble("ftradeamount");
					if(dBarketAmount > 0){//当天为净申购
						if(YssD.mul(dSHBarketAmount,-1) < dDivideAmount){//钆掉的篮子数据是否是在该成交编号的申赎里,此时要把钆差补票的数据拆分为，实时补和用开盘价补票
							for(int i = 0; i < tradeSettleDetail.size(); i++){
								etfTradeSettleDetail = (ETFTradeSettleDetailBean) tradeSettleDetail.get(i);
								tradeSettleDelRef = (ETFTradeSettleDetailRefBean) etfTradeSettleDetail.getAlTradeSettleDelRef().get(0);
								if(etfTradeSettleDetail.getTradeNum().equalsIgnoreCase(rs.getString("ftradenum"))){
									dBarketsSacle =YssD.div(YssD.sub(rs.getDouble("ftradeamount"),YssD.mul(dSHBarketAmount,-1)),rs.getDouble("ftradeamount"));//未钆掉的篮子所占比例
									//为拆分实时补票和钆差补票的数据赋值
									setDivideDatatimeOrDifference(etfTradeSettleDetail,tradeSettleDelRef,dBarketsSacle,dBarketAmount,rs);
								}
							}
							break;
						}
					}else{
						if(dSGBarketAmount < dDivideAmount){//钆掉的篮子数据是否是在该成交编号的申赎里,此时要把钆差补票的数据拆分为，实时补和用开盘价补票
							for(int i = 0; i < tradeSettleDetail.size(); i++){
								etfTradeSettleDetail = (ETFTradeSettleDetailBean) tradeSettleDetail.get(i);
								tradeSettleDelRef = (ETFTradeSettleDetailRefBean) etfTradeSettleDetail.getAlTradeSettleDelRef().get(0);
								if(etfTradeSettleDetail.getTradeNum().equalsIgnoreCase(rs.getString("ftradenum"))){
									dBarketsSacle =YssD.div(YssD.sub(rs.getDouble("ftradeamount"),dSGBarketAmount),rs.getDouble("ftradeamount"));//未钆掉的篮子所占比例
									//为拆分实时补票和钆差补票的数据赋值
									setDivideDatatimeOrDifference(etfTradeSettleDetail,tradeSettleDelRef,dBarketsSacle,dBarketAmount,rs);
								}
							}
							break;
						}
					}
				}
				//把数据转移到保存操作日期当天明细关联数据的集合中
				for(int i = 0; i < tradeSettleDetailDifference.size(); i++){
					etfTradeSettleDetail = (ETFTradeSettleDetailBean) tradeSettleDetailDifference.get(i);
					tradeSettleDetail.add(etfTradeSettleDetail);
				}
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
				for(int i = 0;i < tradeSettleDetail.size();i++){
					//--------------------拼接交易编号---------------------
					sNum++;
					String tmp = "";
					for (int t = 0; t < s.length()- String.valueOf(sNum).length(); t++) {
						tmp += "0";
					}
					strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
					// ------------------------end--------------------------//
					etfTradeSettleDetail = (ETFTradeSettleDetailBean) tradeSettleDetail.get(i);
					tradeSettleDelRef = (ETFTradeSettleDetailRefBean) etfTradeSettleDetail.getAlTradeSettleDelRef().get(0);
					
					etfTradeSettleDetail.setNum(strNumDate);
					tradeSettleDelRef.setNum(strNumDate);
				}
			}
		}catch (Exception e) {
			throw new YssException("处理钆差补票的数据拆分出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 为拆分实时补票和钆差补票的数据赋值
	 * @param etfTradeSettleDetail 交易结算明细
	 * @param tradeSettleDelRef 交易结算明细关联
	 * @param dBarketsSacle 拆分篮子比例
	 * @param dBarketAmount 钆差后剩余篮子数
	 * @param rs 游标
	 * @throws YssException
	 */
	private void setDivideDatatimeOrDifference(ETFTradeSettleDetailBean etfTradeSettleDetail,ETFTradeSettleDetailRefBean tradeSettleDelRef,
			double dBarketsSacle,double dBarketAmount,ResultSet rs) throws YssException{
		ETFTradeSettleDetailBean etfTradeSettleDetailDifference= null;//交易明细
		ETFTradeSettleDetailRefBean tradeSettleDelRefDifference = null;//交易明细关联
		try{
			//---------------------赋值钆掉的篮子的明细和明细关联数据---------------------------//
			//钆掉的篮子用第二天用开发价进行补票，属于钆差补票--difference,尾差放到钆差补票里
			//-------------------------------明细数据----------------------------//
			etfTradeSettleDetailDifference = new ETFTradeSettleDetailBean();
			tradeSettleDelRefDifference = new ETFTradeSettleDetailRefBean();
			
			etfTradeSettleDetailDifference.setPortCode(etfTradeSettleDetail.getPortCode());//组合代码
			etfTradeSettleDetailDifference.setSecurityCode(etfTradeSettleDetail.getSecurityCode());//证券代码
			etfTradeSettleDetailDifference.setStockHolderCode(etfTradeSettleDetail.getStockHolderCode());//投资者
			etfTradeSettleDetailDifference.setBrokerCode(etfTradeSettleDetail.getBrokerCode());//券商
			etfTradeSettleDetailDifference.setSeatCode(etfTradeSettleDetail.getSeatCode());//交易席位
			etfTradeSettleDetailDifference.setBs(etfTradeSettleDetail.getBs());//台账类型
			etfTradeSettleDetailDifference.setBuyDate(etfTradeSettleDetail.getBuyDate());//申赎日期
			//替代数量 = (申赎份额/参数设置中基准比例)* 股票篮文件中证券数量
			etfTradeSettleDetailDifference.setReplaceAmount(YssD.sub(etfTradeSettleDetail.getReplaceAmount(),
							YssD.round(
									YssD.mul(etfTradeSettleDetail.getReplaceAmount(),
											dBarketsSacle),0)));
			etfTradeSettleDetailDifference.setBraketNum(YssD.sub(rs.getDouble("ftradeamount"),dBarketAmount > 0?dBarketAmount:YssD.mul(dBarketAmount,-1)));//篮子数
			etfTradeSettleDetailDifference.setUnitCost(etfTradeSettleDetail.getUnitCost());//单位成本
			//汇率
			etfTradeSettleDetailDifference.setExchangeRate(etfTradeSettleDetail.getExchangeRate());
			
			//替代金额本币 = round(股票篮中替代金额 * （1+溢价比例）,2)* 篮子数
			etfTradeSettleDetailDifference.setHReplaceCash(YssD.sub(etfTradeSettleDetail.getHReplaceCash(),
					YssD.round(
							YssD.mul(etfTradeSettleDetail.getHReplaceCash(),
									dBarketsSacle),2)));
			//替代金额原币
			etfTradeSettleDetailDifference.setOReplaceCash(YssD.sub(etfTradeSettleDetail.getOReplaceCash(),
					YssD.round(
							YssD.mul(etfTradeSettleDetail.getOReplaceCash(),
									dBarketsSacle),2)));
			//可退替代款原币 =替代金额原币 - 替代数量 * 单位成本
			etfTradeSettleDetailDifference.setOcReplaceCash(YssD.sub(etfTradeSettleDetail.getOcReplaceCash(),
					YssD.round(
							YssD.mul(etfTradeSettleDetail.getOcReplaceCash(),
									dBarketsSacle),2)));
			//可退替代款本币 = 替代金额本币 - 替代数量 * 单位成本 * 基础汇率/组合汇率
			etfTradeSettleDetailDifference.setHcReplaceCash(YssD.sub(etfTradeSettleDetail.getHcReplaceCash(),
					YssD.round(
							YssD.mul(etfTradeSettleDetail.getHcReplaceCash(),
									dBarketsSacle),2)));
			etfTradeSettleDetailDifference.setTradeNum(etfTradeSettleDetail.getTradeNum());//成交编号
			etfTradeSettleDetailDifference.setMarktype("difference");
			//-------------------------end 明细------------------------------//
			//----------------------------明细关联数据------------------------//
			tradeSettleDelRefDifference.setRefNum(tradeSettleDelRef.getRefNum());//关联编号：主要是区分：1-第一次补票，2-第二次补票，3-强制处理
			tradeSettleDelRefDifference.setMakeUpDate(tradeSettleDelRef.getMakeUpDate());//补票日期
			tradeSettleDelRefDifference.setMakeUpAmount(0);
			//单位成本原币 = 交易表中 实收实付金额/交易数量
			tradeSettleDelRefDifference.setUnitCost(0);
			//原币总成本 = 总成本（原币）* 被动投资比例
			tradeSettleDelRefDifference.setoMakeUpCost(0);
			//估值汇率
			tradeSettleDelRefDifference.setExchangeRate(tradeSettleDelRef.getExchangeRate());
			
			//本币总成本 = 原币总成本 * 基础汇率/组合汇率 * 被动投资比例
			tradeSettleDelRefDifference.sethMakeUpCost(0);
			
			//剩余数量 = 申赎数量 
			tradeSettleDelRefDifference.setRemaindAmount(YssD.sub(tradeSettleDelRef.getRemaindAmount(),
					YssD.round(
							YssD.mul(tradeSettleDelRef.getRemaindAmount(),
									dBarketsSacle),0)));
			if(etfTradeSettleDetail.getBs().equalsIgnoreCase("B")){
				//应付替代款（原币） = 替代款金额原币 * 补票数量/申赎数量 - 补票总成本原币 
				tradeSettleDelRefDifference.setOpReplaceCash(0);
				//应付替代款（本币） = 可退替代款本币 * 补票数量/申赎数量 - 补票总成本本币
				tradeSettleDelRefDifference.setHpReplaceCash(0);
				//数据方向
				tradeSettleDelRefDifference.setDataDirection("1");
				//退款日期							
				tradeSettleDelRefDifference.setRefundDate(tradeSettleDelRef.getRefundDate());
			}else{
				//应付替代款（原币） = 补票总成本原币 
				tradeSettleDelRefDifference.setOpReplaceCash(0);
				//应付替代款（本币） = 补票总成本本币
				tradeSettleDelRefDifference.setHpReplaceCash(0);
				
				//数据方向
				tradeSettleDelRefDifference.setDataDirection("-1");
				//退款日期
				tradeSettleDelRefDifference.setRefundDate(tradeSettleDelRef.getRefundDate());
			}
			//可退替代款（原币）=可退替代款原币 * 补票数量/申赎数量  
			tradeSettleDelRefDifference.setOcRefundSum(0);
			//可退替代款（本币）=可退替代款本币 * 补票数量/申赎数量 
			tradeSettleDelRefDifference.setHcReplaceCash(0);
			
			//清算标识
			tradeSettleDelRefDifference.setSettleMark("N");
			//数据标识
			tradeSettleDelRefDifference.setDataMark("0");
			//----------------------------------end-----------------------------------------//
			etfTradeSettleDetailDifference.getAlTradeSettleDelRef().add(tradeSettleDelRefDifference);//保存明细关联数据

			//----------------------------end 明细关联-----------------------//
			
			tradeSettleDetailDifference.add(etfTradeSettleDetailDifference);
			//-----------------------------------end ------------------------------------//
	
			//---------------------赋值未钆掉的篮子的明细和明细关联数据-------------------------//
			//未钆掉的篮子第二天实时补票。属于实时补票--time
			//替代数量 = (申赎份额/参数设置中基准比例)* 股票篮文件中证券数量
			etfTradeSettleDetail.setReplaceAmount(YssD.round(YssD.mul(etfTradeSettleDetail.getReplaceAmount(),
											dBarketsSacle),0));
			etfTradeSettleDetail.setBraketNum(dBarketAmount > 0?dBarketAmount:YssD.mul(dBarketAmount,-1));//篮子数
			
			//替代金额本币 = round(股票篮中替代金额 * （1+溢价比例）,2)* 篮子数
			etfTradeSettleDetail.setHReplaceCash(YssD.round(
					YssD.mul(etfTradeSettleDetail.getHReplaceCash(),dBarketsSacle),2));
			//替代金额原币
			etfTradeSettleDetail.setOReplaceCash(YssD.round(
							YssD.mul(etfTradeSettleDetail.getOReplaceCash(),
									dBarketsSacle),2));
			//可退替代款原币 =替代金额原币 - 替代数量 * 单位成本
			etfTradeSettleDetail.setOcReplaceCash(YssD.round(
							YssD.mul(etfTradeSettleDetail.getOcReplaceCash(),
									dBarketsSacle),2));
			//可退替代款本币 = 替代金额本币 - 替代数量 * 单位成本 * 基础汇率/组合汇率
			etfTradeSettleDetail.setHcReplaceCash(YssD.round(
							YssD.mul(etfTradeSettleDetail.getHcReplaceCash(),
									dBarketsSacle),2));
			etfTradeSettleDetail.setMarktype("time");
			//-------------------------end 明细------------------------------//
			//----------------------------明细关联数据------------------------//
			
			//剩余数量 = 申赎数量 
			tradeSettleDelRef.setRemaindAmount(YssD.round(
							YssD.mul(tradeSettleDelRef.getRemaindAmount(),
									dBarketsSacle),0));
			//---------------------------end 明细关联-----------------------//
			//-----------------------------------end-------------------------------------//
		}catch (Exception e) {
			throw new YssException("为拆分实时补票和钆差补票的数据赋值出错！",e);
		}
	}
	
	/**
	 * 此方法处理操作日前一个工作日的实时补票数据，权益数据
	 * @throws YssException
	 */
	private void doYesDatetimeSupplyData(Date dDate) throws YssException{
		StringBuffer buff =null;
		ResultSet rs =null;
		EachExchangeHolidays holiday = null;//节假日代码类
		String sRowStr ="";
		Date yesDate = null;//操作日当天的钱一个工作日
		ETFTradeSettleDetailBean etfTradeSettleDetail= null;//交易结算明细bean
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;//交易结算明细关联bean
		double makeUpAmount = 0;//补票数量
		Date SGReplaceOver = null;//申购应付替代结转
		Date SHReplaceOver = null;//赎回应付替代结转
		double replaceScale = 0;//被动投资比例
		try{
			if(tradeSettleDetailYesDate.size()!=0){//如果集合有值，要先清空
				tradeSettleDetailYesDate.clear();
			}
			
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			//拼接参数：节假日代码+当天的偏离天数+操作日期
			sRowStr = paramSet.getSHolidayCode() + "\t" + -1 + "\t" + YssFun.formatDate(dDate);
			
			holiday.parseRowStr(sRowStr);//解析参数
			yesDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//获取操作日期当天的最近一个工作日
			//拼接参数：节假日代码+当天的偏离天数+操作日期
			sRowStr = (holidays.containsKey(YssOperCons.YSS_ETF_OVERTYPE_SGDEALREPLACE)?(String)holidays.get(YssOperCons.YSS_ETF_OVERTYPE_SGDEALREPLACE):paramSet.getSHolidayCode()) + "\t" + paramSet.getISGDealReplace()+ "\t" + YssFun.formatDate(dDate);
			holiday.parseRowStr(sRowStr);
			SGReplaceOver = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//申购应付替代结转日期
			//拼接参数：节假日代码+当天的偏离天数+操作日期
			sRowStr = (holidays.containsKey(YssOperCons.YSS_ETF_OVERTYPE_SHDEALREPLACE)?(String)holidays.get(YssOperCons.YSS_ETF_OVERTYPE_SHDEALREPLACE):paramSet.getSHolidayCode()) + "\t" + paramSet.getISHDealReplace() + "\t" + YssFun.formatDate(dDate);
			holiday.parseRowStr(sRowStr);
			SHReplaceOver = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//赎回应付替代结转日期
			
			//====QDV4华宝2010年12月17日01_B ==== panjunfang modify 20101217-=========//
            CtlPubPara pubPara=new CtlPubPara();//通用参数实例化
            pubPara.setYssPub(pub);//设置Pub
            String rightsRatioMethods=(String)pubPara.getRightsRatioMethods(this.portCodes);//获取通用参数值
			
			buff = new StringBuffer();
			buff.append(" select t1.*,su.FtradeAmount as FsubAmount,su.FTotalCost,t2.fmakeupamount,");
			buff.append(" t2.FRemaindAmount,t2.FOCPReplaceCash,t2.FHCPReplaceCash,");
			//====QDV4华宝2010年12月17日01_B ==== panjunfang modify 20101217-=========//
			buff.append(rightsRatioMethods.equalsIgnoreCase("PreTaxRatio") ? "d.FPreTaxRatio as DivRatio,sh.FPreTaxRatio as shareRatio,ri.FPreTaxRatio as RigFRatio," : "d.FAfterTaxRatio as DivRatio,sh.FAfterTaxRatio as shareRatio,ri.FAfterTaxRatio as RigFRatio,");
			buff.append(" ri.FRIPrice,b.FPrice,b.Fotprice1,ra.* from ");
			buff.append(pub.yssGetTableName("tb_etf_tradestldtl"));//交易结算明细库
			buff.append(" t1  join (select * from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));//交易结算明细关联库
			buff.append(" where FRemaindAmount <> 0 and FDataMark = '0'").append(" and fmakeupdate = ").append(dbl.sqlDate(yesDate)).append(") t2 on t1.FNum = t2.FNum");
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_data_subtrade"));//业务资料表
			buff.append(" where FCheckState = 1 ").append(" and FBargaindate =").append(dbl.sqlDate(dDate));
			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" ) su on t1.ftradenum =su.FDealNum and t1.fportcode = su.fportcode and t1.fbuydate = su.FAppDate and t1.FSecuritycode = su.FSecuritycode");
			
			buff.append(" join( select * from ").append(pub.yssGetTableName("tb_para_security"));
			buff.append(" where FCheckState = 1 and FCatCode = 'EQ') se on t1.FSecuritycode = se.Fsecuritycode ");
			
			buff.append(" left join (select FBaseRate, FPortRate,FPortCode,FCuryCode from ");
			buff.append(pub.yssGetTableName("Tb_Data_PretValRate"));//汇率预处理表
			buff.append(" where FCheckState = 1 and FValDate =").append(dbl.sqlDate(dDate));
			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" ) ra ").append(" on t1.FPortCode = ra.FPortCode and se.FTradeCury = ra.FCuryCode ");
			
			buff.append(" left join (select * from ").append(pub.yssGetTableName("Tb_Data_PretValMktPrice"));//行情预处理表
			buff.append(" where FValDate = ").append(dbl.sqlDate(dDate));
			buff.append(" and FPortCode in (").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and FCheckState = 1 ) b on t1.fsecuritycode =b.fsecuritycode and t1.FPortCode = b.FPortCode");
			
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_data_dividend"));//分红数据表
			buff.append(" where FCheckState = 1 and FDividendDate = ").append(dbl.sqlDate(dDate));
			buff.append(" ) d on t1.fsecuritycode =d.fsecuritycode  and t1.fbuydate < d.FDividendDate");
			
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_data_bonusshare"));//送股数据表
			buff.append(" where FCheckState = 1 and FExrightDate =").append(dbl.sqlDate(dDate));
			buff.append(" ) sh on t1.fsecuritycode =sh.FSSecurityCode   and t1.fbuydate < sh.FExrightDate");
			
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_data_rightsissue"));//配股数据表
			buff.append(" where FCheckState = 1 and FExrightDate = ").append(dbl.sqlDate(dDate));
			buff.append(" ) ri on t1.fsecuritycode = ri.fsecuritycode and t1.fbuydate < ri.FExrightDate");
			buff.append(" and ").append(dbl.sqlDate(dDate)).append(" >=ri.FExrightDate");
			buff.append(" and ").append(dbl.sqlDate(dDate)).append(" <=ri.FExpirationDate");
			
			buff.append(" where t1.FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and t1.FBuyDate = ").append(dbl.sqlDate(yesDate));
			buff.append(" and t1.fmarktype = 'time' order by t1.fnum");
			
			rs= dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			while(rs.next()){
				etfTradeSettleDetail = new ETFTradeSettleDetailBean();
				tradeSettleDelRef = new ETFTradeSettleDetailRefBean();
				
				etfTradeSettleDetail.setPortCode(rs.getString("FPortCode"));//组合代码
				etfTradeSettleDetail.setNum(rs.getString("FNum"));//申请标号
				etfTradeSettleDetail.setReplaceAmount(rs.getDouble("FReplaceAmount"));//申赎数量
				etfTradeSettleDetail.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
				etfTradeSettleDetail.setOReplaceCash(rs.getDouble("FOReplaceCash"));//替代金额(原币)
				etfTradeSettleDetail.setHReplaceCash(rs.getDouble("FHReplaceCash"));//替代金额(本币)
				etfTradeSettleDetail.setOcReplaceCash(rs.getDouble("FOCReplaceCash"));//可退替代款（原币）
				etfTradeSettleDetail.setHcReplaceCash(rs.getDouble("FHCReplaceCash"));//可退替代款（本币）
				
				//-----------------------------以下处理权益数据---------------------------------//
				//如果分红比例、送股比例或者配股比例不为0时，说明当天有权益
				if(rs.getDouble("DivRatio")!=0||rs.getDouble("shareRatio")!=0
						||rs.getDouble("RigFRatio")!=0){
						tradeSettleDelRef.setExRightDate(dDate);//权益日期
						tradeSettleDelRef.setRightRate(YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate")));//权益汇率
					}						
				this.doDivdend(tradeSettleDelRef,rs);//处理分红权益
				this.doBonusShare(tradeSettleDelRef,rs);//处理送股权益
				this.doRightIssue(tradeSettleDelRef,rs);//处理配股权益
				//------------------------------end 处理权益数据---------------------------------//
				
				//-------------------------------设置第二次补票数据------------------------------------//
				tradeSettleDelRef.setNum(rs.getString("FNum"));//申请编号
				tradeSettleDelRef.setRefNum("2");//关联编号：主要是区分：1-第一次补票，2-第二次补票，3-强制处理
				tradeSettleDelRef.setMakeUpDate(dDate);//补票日期
				
				//第一次补票后的剩余数量+权益数据中的实际数量 与 当天交易数据中的成交数量之差
				makeUpAmount = YssD.sub(YssD.add(rs.getDouble("FRemaindAmount"),tradeSettleDelRef.getRealAmount()),rs.getDouble("FsubAmount"));
				
				if(makeUpAmount <= 0){//当天该笔证券的交易数量大于申赎数量，即有主动投资时
					tradeSettleDelRef.setMakeUpAmount(YssD.add(rs.getDouble("FRemaindAmount"),tradeSettleDelRef.getRealAmount()));
					//被动投资比例 = 第二次补票数量/当天该股票的交易数量
					replaceScale = YssD.div(YssD.add(rs.getDouble("FRemaindAmount"),tradeSettleDelRef.getRealAmount()),rs.getDouble("FsubAmount"));
					//单位成本原币 = 交易表中 实收实付金额/交易数量
					tradeSettleDelRef.setUnitCost(YssD.round(YssD.div(rs.getDouble("FTotalCost"),rs.getDouble("FsubAmount")),2));
					//原币总成本（原币）= 实收实付金额 * 被动投资比例
					tradeSettleDelRef.setoMakeUpCost(YssD.round(YssD.mul(rs.getDouble("FTotalCost"),replaceScale),2));
					//估值汇率 = 基础汇率/组合汇率
					tradeSettleDelRef.setExchangeRate(YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate")));
					//本币总成本 = 原币总成本 * 基础汇率/组合 * 被动投资比例
					tradeSettleDelRef.sethMakeUpCost(YssD.round(YssD.mul(YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalCost"),rs.getDouble("FBaseRate")),rs.getDouble("FPortRate")),2),replaceScale),2));
					
					//剩余数量 = （申赎数量 + 送股实际数量）-第二次补票数量 - 第一次补票数量 
					tradeSettleDelRef.setRemaindAmount(0);
					//可退替代款（原币）=可退替代款原币 - 第一次补票可退替代款原币
					tradeSettleDelRef.setOcReplaceCash(YssD.sub(etfTradeSettleDetail.getOcReplaceCash(),rs.getDouble("FOCPReplaceCash")));
					//可退替代款（本币）=可退替代款本币 * 第一次补票可退替代款本币 
					tradeSettleDelRef.setHcReplaceCash(YssD.sub(etfTradeSettleDetail.getHcReplaceCash(),rs.getDouble("FHCPReplaceCash")));
					
					
				}else{//当天该笔证券的交易数量大于申赎数量，即只有被动投资时
					tradeSettleDelRef.setMakeUpAmount(rs.getDouble("FsubAmount"));
					//单位成本原币 = 交易表中 实收实付金额/交易数量
					tradeSettleDelRef.setUnitCost(YssD.round(YssD.div(rs.getDouble("FTotalCost"),rs.getDouble("FsubAmount")),2));
					//原币总成本（原币）
					tradeSettleDelRef.setoMakeUpCost(rs.getDouble("FTotalCost"));
					//估值汇率 = 基础汇率/组合汇率
					tradeSettleDelRef.setExchangeRate(YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate")));
					//本币总成本 = 原币总成本 * 基础汇率/组合
					tradeSettleDelRef.sethMakeUpCost(YssD.round(YssD.div(YssD.mul(rs.getDouble("FTotalCost"),rs.getDouble("FBaseRate")),rs.getDouble("FPortRate")),2));
					
					//剩余数量 = （申赎数量 + 送股实际数量）-第二次补票数量 - 第一次补票数量 
					tradeSettleDelRef.setRemaindAmount(YssD.sub(YssD.add(etfTradeSettleDetail.getReplaceAmount(),tradeSettleDelRef.getRealAmount()),tradeSettleDelRef.getMakeUpAmount(),rs.getDouble("fmakeupamount")));
					
					//--------------------处理强制处理数据-------------------------------------//
					if(tradeSettleDelRef.getRemaindAmount()!=0){//剩余数量不为0时，进行强制处理
						//可退替代款（原币）=可退替代款原币*第二次补票数量/申赎数量
						tradeSettleDelRef.setOcReplaceCash(YssD.round(
								YssD.div(
										YssD.mul(etfTradeSettleDetail.getOcReplaceCash(),
												tradeSettleDelRef.getMakeUpAmount()),
												etfTradeSettleDetail.getReplaceAmount()
										),
										2));
						//可退替代款（本币）=可退替代款本币*第二次补票数量/申赎数量
						tradeSettleDelRef.setHcReplaceCash(YssD.round(
								YssD.div(
										YssD.mul(etfTradeSettleDetail.getHcReplaceCash(),
												tradeSettleDelRef.getMakeUpAmount()),
												etfTradeSettleDetail.getReplaceAmount()
										),
										2));
						//产生强制处理数据
						this.doForceManage(etfTradeSettleDetail,tradeSettleDelRef,rs,dDate,SGReplaceOver,SHReplaceOver);
					}else{
						//可退替代款（原币）=可退替代款原币 - 第一次补票可退替代款原币
						tradeSettleDelRef.setOcReplaceCash(YssD.sub(etfTradeSettleDetail.getOcReplaceCash(),rs.getDouble("FOCPReplaceCash")));
						//可退替代款（本币）=可退替代款本币 * 第一次补票可退替代款本币 
						tradeSettleDelRef.setHcReplaceCash(YssD.sub(etfTradeSettleDetail.getHcReplaceCash(),rs.getDouble("FHCPReplaceCash")));
						
					}
					//--------------------end 强制处理数据 ------------------------------------//
				}			
				
				if(rs.getString("FBs").equalsIgnoreCase("B")){//申购
					//应付替代款（原币） = （替代款金额原币-总派息原币—权证价值原币） * 第二次补票数量/（申赎数量 +送股总数量）- 补票总成本原币 
					tradeSettleDelRef.setOpReplaceCash(YssD.sub(YssD.round(YssD.div(YssD.mul(YssD.sub(etfTradeSettleDetail.getOReplaceCash(),tradeSettleDelRef.getInterest(),tradeSettleDelRef.getWarrantCost()),tradeSettleDelRef.getMakeUpAmount()),
							YssD.add(etfTradeSettleDetail.getReplaceAmount(),tradeSettleDelRef.getSumAmount())),2),tradeSettleDelRef.getoMakeUpCost()));
					//应付替代款（本币） = （替代款金额本币 - 总派息本币—权证价值本币）* 第二次补票数量/（申赎数量 +送股总数量） - 补票总成本本币
					tradeSettleDelRef.setHpReplaceCash(YssD.sub(YssD.round(YssD.div(YssD.mul(YssD.sub(etfTradeSettleDetail.getHReplaceCash(),tradeSettleDelRef.getBbinterest(),tradeSettleDelRef.getBbwarrantCost()),tradeSettleDelRef.getMakeUpAmount()),
							YssD.add(etfTradeSettleDetail.getReplaceAmount(),tradeSettleDelRef.getSumAmount())),2),tradeSettleDelRef.gethMakeUpCost()));
					//数据方向
					tradeSettleDelRef.setDataDirection("1");
					//退款日期							
					tradeSettleDelRef.setRefundDate(SGReplaceOver);
				}else{
					//应付替代款（原币） = 补票总成本原币 + 总派息原币* 第二次补票数量/申赎数量  + 权证价值原币* 第二次补票数量/申赎数量 
					tradeSettleDelRef.setOpReplaceCash(YssD.add(
							tradeSettleDelRef.getoMakeUpCost(),
							YssD.round(
										YssD.div(
												YssD.mul(tradeSettleDelRef.getMakeUpAmount(),tradeSettleDelRef.getInterest()),
												rs.getDouble("FReplaceAmount")),
												2),
							YssD.round(
										YssD.div(
												YssD.mul(tradeSettleDelRef.getMakeUpAmount(),tradeSettleDelRef.getWarrantCost()),
												rs.getDouble("FReplaceAmount")),
												2)));
					//应付替代款（本币） = 补票总成本本币 + 总派息原币* 第二次补票数量/申赎数量 * 第二次补票汇率 + 权证价值原币* 第二次补票数量/申赎数量 * 第二次补票汇率
					tradeSettleDelRef.setHpReplaceCash(YssD.add(
							tradeSettleDelRef.gethMakeUpCost(),
							YssD.round(
									YssD.mul(
											YssD.div(
													YssD.mul(tradeSettleDelRef.getMakeUpAmount(),tradeSettleDelRef.getInterest()),
													rs.getDouble("FReplaceAmount")),
													tradeSettleDelRef.getExchangeRate()),
													2),
							YssD.round(
									YssD.mul(
											YssD.div(
													YssD.mul(tradeSettleDelRef.getMakeUpAmount(),tradeSettleDelRef.getWarrantCost()),
													rs.getDouble("FReplaceAmount")),
													tradeSettleDelRef.getExchangeRate()),2)));
					
					//数据方向
					tradeSettleDelRef.setDataDirection("-1");
					//退款日期
					tradeSettleDelRef.setRefundDate(SHReplaceOver);
				}
				if(tradeSettleDelRef.getRemaindAmount()!=0){
					//清算标识
					tradeSettleDelRef.setSettleMark("N");
				}else{
					//清算标识
					tradeSettleDelRef.setSettleMark("Y");
				}
				//数据标识
				tradeSettleDelRef.setDataMark("0");
				//------------------------------------------end第二次补票数据----------------------------------//
				etfTradeSettleDetail.getAlTradeSettleDelRef().add(tradeSettleDelRef);//把明细关联数据保存到明细bean的集合中
				
				tradeSettleDetailYesDate.add(etfTradeSettleDetail);//把明细数据保存到集合中
			}
			//-------------为操作日期前一个工作日的申赎数据中剩余数量为0的明细关联数据设置为已清算---------//
			this.doSettleMarkUpdate(yesDate,SGReplaceOver,SHReplaceOver);
			//-----------------------------------end------------------------------------------//
		}catch (Exception e) {
			throw new YssException("处理操作日前一个工作日的实时补票数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 此方法处理操作日前一个工作日的钆差补票数据，权益数据
	 * @throws YssException
	 */
	private void doYesDatedifferenceSupplyData(Date dDate) throws YssException{
		StringBuffer buff =null;
		ResultSet rs =null;
		EachExchangeHolidays holiday = null;//节假日代码类
		String sRowStr ="";
		Date yesDate = null;//操作日当天的钱一个工作日
		ETFTradeSettleDetailBean etfTradeSettleDetail= null;//交易结算明细bean
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;//交易结算明细关联bean
		Date SGReplaceOver = null;//申购应付替代结转
		Date SHReplaceOver = null;//赎回应付替代结转
		try{
			
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			//拼接参数：节假日代码+当天的偏离天数+操作日期
			sRowStr = paramSet.getSHolidayCode() + "\t" + -1 + "\t" + YssFun.formatDate(dDate);
			
			holiday.parseRowStr(sRowStr);//解析参数
			yesDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//获取操作日期当天的最近一个工作日
			//拼接参数：节假日代码+当天的偏离天数+操作日期
			sRowStr = (holidays.containsKey(YssOperCons.YSS_ETF_OVERTYPE_SGDEALREPLACE)?(String)holidays.get(YssOperCons.YSS_ETF_OVERTYPE_SGDEALREPLACE):paramSet.getSHolidayCode()) + "\t" + paramSet.getISGDealReplace()+ "\t" + YssFun.formatDate(dDate);
			holiday.parseRowStr(sRowStr);
			SGReplaceOver = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//申购应付替代结转日期
			//拼接参数：节假日代码+当天的偏离天数+操作日期
			sRowStr = (holidays.containsKey(YssOperCons.YSS_ETF_OVERTYPE_SHDEALREPLACE)?(String)holidays.get(YssOperCons.YSS_ETF_OVERTYPE_SHDEALREPLACE):paramSet.getSHolidayCode()) + "\t" + paramSet.getISHDealReplace() + "\t" + YssFun.formatDate(dDate);
			holiday.parseRowStr(sRowStr);
			SHReplaceOver = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//赎回应付替代结转日期
			
			//====QDV4华宝2010年12月17日01_B ==== panjunfang modify 20101217-=========//
            CtlPubPara pubPara=new CtlPubPara();//通用参数实例化
            pubPara.setYssPub(pub);//设置Pub
            String rightsRatioMethods=(String)pubPara.getRightsRatioMethods(this.portCodes);//获取通用参数值
			
			buff = new StringBuffer();
			buff.append(" select t1.*,t2.FRemaindAmount,t2.FOCPReplaceCash,t2.FHCPReplaceCash,");
			//====QDV4华宝2010年12月17日01_B ==== panjunfang modify 20101217-=========//
			buff.append(rightsRatioMethods.equalsIgnoreCase("PreTaxRatio") ? "d.FPreTaxRatio as DivRatio,sh.FPreTaxRatio as shareRatio,ri.FPreTaxRatio as RigFRatio," : "d.FAfterTaxRatio as DivRatio,sh.FAfterTaxRatio as shareRatio,ri.FAfterTaxRatio as RigFRatio,");			
			buff.append(" ri.FRIPrice,b.FPrice,b.Fotprice1,ra.* from ");
			buff.append(pub.yssGetTableName("tb_etf_tradestldtl"));//交易结算明细库
			buff.append(" t1  join (select * from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"));//交易结算明细关联库
			buff.append(" where FRemaindAmount <> 0 and FDataMark = '0'").append(" and fmakeupdate = ").append(dbl.sqlDate(yesDate)).append(") t2 on t1.FNum = t2.FNum");
//			buff.append(" join (select * from ").append(pub.yssGetTableName("tb_data_subtrade"));//业务资料表
//			buff.append(" where FCheckState = 1 ").append(" and FBargaindate =").append(dbl.sqlDate(dDate));
//			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
//			buff.append(" ) su on t1.ftradenum =su.FDealNum and t1.fportcode = su.fportcode and t1.fbuydate = su.FAppDate and t1.FSecuritycode = su.FSecuritycode");
			
			buff.append(" join( select * from ").append(pub.yssGetTableName("tb_para_security"));
			buff.append(" where FCheckState = 1 and FCatCode = 'EQ') se on t1.FSecuritycode = se.Fsecuritycode ");
			
			buff.append(" left join (select FBaseRate, FPortRate,FPortCode,FCuryCode from ");
			buff.append(pub.yssGetTableName("Tb_Data_PretValRate"));//汇率预处理表
			buff.append(" where FCheckState = 1 and FValDate =").append(dbl.sqlDate(dDate));
			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" ) ra ").append(" on t1.FPortCode = ra.FPortCode and se.FTradeCury = ra.FCuryCode ");
			
			buff.append(" left join (select * from ").append(pub.yssGetTableName("Tb_Data_PretValMktPrice"));//行情预处理表
			buff.append(" where FValDate = ").append(dbl.sqlDate(dDate));
			buff.append(" and FPortCode in (").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and FCheckState = 1 ) b on t1.fsecuritycode =b.fsecuritycode and t1.FPortCode = b.FPortCode");
			
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_data_dividend"));//分红数据表
			buff.append(" where FCheckState = 1 and FDividendDate = ").append(dbl.sqlDate(dDate));
			buff.append(" ) d on t1.fsecuritycode =d.fsecuritycode  and t1.fbuydate < d.FDividendDate");
			
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_data_bonusshare"));//送股数据表
			buff.append(" where FCheckState = 1 and FExrightDate =").append(dbl.sqlDate(dDate));
			buff.append(" ) sh on t1.fsecuritycode =sh.FSSecurityCode   and t1.fbuydate < sh.FExrightDate");
			
			buff.append(" left join (select * from ").append(pub.yssGetTableName("tb_data_rightsissue"));//配股数据表
			buff.append(" where FCheckState = 1 and FExrightDate = ").append(dbl.sqlDate(dDate));
			buff.append(" ) ri on t1.fsecuritycode = ri.fsecuritycode and t1.fbuydate < ri.FExrightDate");
			buff.append(" and ").append(dbl.sqlDate(dDate)).append(" >=ri.FExrightDate");
			buff.append(" and ").append(dbl.sqlDate(dDate)).append(" <=ri.FExpirationDate");
			
			buff.append(" where t1.FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
			buff.append(" and t1.FBuyDate = ").append(dbl.sqlDate(yesDate)).append(" and t1.FMarkType = 'difference'");
//			buff.append(" and not exists(select tt.FNum from ").append(pub.yssGetTableName("tb_etf_tradestldtl"));//交易结算明细库
//			buff.append(" tt where tt.FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
//			buff.append(" and tt.FMarkType = 'difference'").append(" and tt.FBuyDate = ").append(dbl.sqlDate(yesDate));
//			buff.append(" and exists (select fdealnum from ").append(pub.yssGetTableName("tb_data_subtrade"));
//			buff.append(" st where FCheckState = 1 ").append(" and FBargaindate =").append(dbl.sqlDate(dDate));
//			buff.append(" and FPortCode in(").append(this.operSql.sqlCodes(this.portCodes)).append(")");
//			buff.append(" and st.fappdate = ").append(dbl.sqlDate(yesDate));
//			buff.append(" and tt.ftradenum = st.fdealnum)");
			buff.append(" order by t1.fnum");
			
			rs= dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			while(rs.next()){
				etfTradeSettleDetail = new ETFTradeSettleDetailBean();
				tradeSettleDelRef = new ETFTradeSettleDetailRefBean();
				
				etfTradeSettleDetail.setPortCode(rs.getString("FPortCode"));//组合代码
				etfTradeSettleDetail.setNum(rs.getString("FNum"));//申请标号
				etfTradeSettleDetail.setReplaceAmount(rs.getDouble("FReplaceAmount"));//申赎数量
				etfTradeSettleDetail.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
				etfTradeSettleDetail.setOReplaceCash(rs.getDouble("FOReplaceCash"));//替代金额(原币)
				etfTradeSettleDetail.setHReplaceCash(rs.getDouble("FHReplaceCash"));//替代金额(本币)
				etfTradeSettleDetail.setOcReplaceCash(rs.getDouble("FOCReplaceCash"));//可退替代款（原币）
				etfTradeSettleDetail.setHcReplaceCash(rs.getDouble("FHCReplaceCash"));//可退替代款（本币）
				
				//-----------------------------以下处理权益数据---------------------------------//
				//如果分红比例、送股比例或者配股比例不为0时，说明当天有权益
				if(rs.getDouble("DivRatio")!=0||rs.getDouble("shareRatio")!=0
						||rs.getDouble("RigFRatio")!=0){
						tradeSettleDelRef.setExRightDate(dDate);//权益日期
						tradeSettleDelRef.setRightRate(YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate")));//权益汇率
					}						
				this.doDivdend(tradeSettleDelRef,rs);//处理分红权益
				this.doBonusShare(tradeSettleDelRef,rs);//处理送股权益
				this.doRightIssue(tradeSettleDelRef,rs);//处理配股权益
				//------------------------------end 处理权益数据---------------------------------//
				
				//-------------------------------设置第二次补票数据------------------------------------//
				tradeSettleDelRef.setNum(rs.getString("FNum"));//申请编号
				tradeSettleDelRef.setRefNum("2");//关联编号：主要是区分：1-第一次补票，2-第二次补票，3-强制处理
				tradeSettleDelRef.setMakeUpDate(dDate);//补票日期

				tradeSettleDelRef.setMakeUpAmount(YssD.add(rs.getDouble("FRemaindAmount"),tradeSettleDelRef.getRealAmount()));
				//单位成本原币 = 开盘价
				tradeSettleDelRef.setUnitCost(rs.getDouble("Fotprice1"));
				//原币总成本（原币）= 单位成本原币 * 补票数量
				tradeSettleDelRef.setoMakeUpCost(YssD.round(YssD.mul(tradeSettleDelRef.getMakeUpAmount(),tradeSettleDelRef.getUnitCost()),2));
				//估值汇率 = 基础汇率/组合汇率
				tradeSettleDelRef.setExchangeRate(YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate")));
				//本币总成本 = 单位成本原币 * 补票数量 * 基础汇率/组合 
				tradeSettleDelRef.sethMakeUpCost(YssD.round(
										YssD.div(
												YssD.mul(
														YssD.mul(tradeSettleDelRef.getMakeUpAmount(),tradeSettleDelRef.getUnitCost()),
														rs.getDouble("FBaseRate")),
												rs.getDouble("FPortRate")),2));
				
				//剩余数量 = （申赎数量 + 送股实际数量）-第二次补票数量 - 第一次补票数量 
				tradeSettleDelRef.setRemaindAmount(0);
				//可退替代款（原币）=可退替代款原币 - 第一次补票可退替代款原币
				tradeSettleDelRef.setOcReplaceCash(YssD.sub(etfTradeSettleDetail.getOcReplaceCash(),rs.getDouble("FOCPReplaceCash")));
				//可退替代款（本币）=可退替代款本币 * 第一次补票可退替代款本币 
				tradeSettleDelRef.setHcReplaceCash(YssD.sub(etfTradeSettleDetail.getHcReplaceCash(),rs.getDouble("FHCPReplaceCash")));

				if(rs.getString("FBs").equalsIgnoreCase("B")){//申购
					//应付替代款（原币） = （替代款金额原币-总派息原币—权证价值原币） * 第二次补票数量/（申赎数量 +送股总数量）- 补票总成本原币 
					tradeSettleDelRef.setOpReplaceCash(YssD.sub(YssD.round(YssD.div(YssD.mul(YssD.sub(etfTradeSettleDetail.getOReplaceCash(),tradeSettleDelRef.getInterest(),tradeSettleDelRef.getWarrantCost()),tradeSettleDelRef.getMakeUpAmount()),
							YssD.add(etfTradeSettleDetail.getReplaceAmount(),tradeSettleDelRef.getSumAmount())),2),tradeSettleDelRef.getoMakeUpCost()));
					//应付替代款（本币） = （替代款金额本币 - 总派息本币—权证价值本币）* 第二次补票数量/（申赎数量 +送股总数量） - 补票总成本本币
					tradeSettleDelRef.setHpReplaceCash(YssD.sub(YssD.round(YssD.div(YssD.mul(YssD.sub(etfTradeSettleDetail.getHReplaceCash(),tradeSettleDelRef.getBbinterest(),tradeSettleDelRef.getBbwarrantCost()),tradeSettleDelRef.getMakeUpAmount()),
							YssD.add(etfTradeSettleDetail.getReplaceAmount(),tradeSettleDelRef.getSumAmount())),2),tradeSettleDelRef.gethMakeUpCost()));
					//数据方向
					tradeSettleDelRef.setDataDirection("1");
					//退款日期							
					tradeSettleDelRef.setRefundDate(SGReplaceOver);
				}else{
					//应付替代款（原币） = 补票总成本原币 + 总派息原币* 第二次补票数量/申赎数量  + 权证价值原币* 第二次补票数量/申赎数量 
					tradeSettleDelRef.setOpReplaceCash(YssD.add(
							tradeSettleDelRef.getoMakeUpCost(),
							YssD.round(
										YssD.div(
												YssD.mul(tradeSettleDelRef.getMakeUpAmount(),tradeSettleDelRef.getInterest()),
												rs.getDouble("FReplaceAmount")),
												2),
							YssD.round(
										YssD.div(
												YssD.mul(tradeSettleDelRef.getMakeUpAmount(),tradeSettleDelRef.getWarrantCost()),
												rs.getDouble("FReplaceAmount")),
												2)));
					//应付替代款（本币） = 补票总成本本币 + 总派息原币* 第二次补票数量/申赎数量 * 第二次补票汇率 + 权证价值原币* 第二次补票数量/申赎数量 * 第二次补票汇率
					tradeSettleDelRef.setHpReplaceCash(YssD.add(
							tradeSettleDelRef.gethMakeUpCost(),
							YssD.round(
									YssD.mul(
											YssD.div(
													YssD.mul(tradeSettleDelRef.getMakeUpAmount(),tradeSettleDelRef.getInterest()),
													rs.getDouble("FReplaceAmount")),
													tradeSettleDelRef.getExchangeRate()),
													2),
							YssD.round(
									YssD.mul(
											YssD.div(
													YssD.mul(tradeSettleDelRef.getMakeUpAmount(),tradeSettleDelRef.getWarrantCost()),
													rs.getDouble("FReplaceAmount")),
													tradeSettleDelRef.getExchangeRate()),2)));
					
					//数据方向
					tradeSettleDelRef.setDataDirection("-1");
					//退款日期
					tradeSettleDelRef.setRefundDate(SHReplaceOver);
				}
				if(tradeSettleDelRef.getRemaindAmount()!=0){
					//清算标识
					tradeSettleDelRef.setSettleMark("N");
				}else{
					//清算标识
					tradeSettleDelRef.setSettleMark("Y");
				}
				//数据标识
				tradeSettleDelRef.setDataMark("0");
				//------------------------------------------end第二次补票数据----------------------------------//
				etfTradeSettleDetail.getAlTradeSettleDelRef().add(tradeSettleDelRef);//把明细关联数据保存到明细bean的集合中
				
				tradeSettleDetailYesDate.add(etfTradeSettleDetail);//把明细数据保存到集合中
			}
			//-------------为操作日期前一个工作日的申赎数据中剩余数量为0的明细关联数据设置为已清算---------//
			this.doSettleMarkUpdate(yesDate,SGReplaceOver,SHReplaceOver);
			//-----------------------------------end------------------------------------------//
		}catch (Exception e) {
			throw new YssException("处理操作日前一个工作日的钆差补票数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 为操作日期前一个工作日的申赎数据中剩余数量为0的明细关联数据设置为已清算
	 * @throws YssException
	 */
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
	
	/**
	 * 此方法处理分红数据
	 * @throws YssException
	 */
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
	
	/**
	 * 此方法处理送股数据
	 * @throws YssException
	 */
	private void doBonusShare(ETFTradeSettleDetailRefBean tradeSettleDelRef,ResultSet rs) throws YssException{
		double shareAllAmount = 0;//送股总数量
		double factAmount = 0;// 实际数量
		try{
			if(rs.getDouble("shareRatio")!=0){
				//送股总数量 = 申赎数量* 送股权益比例
				shareAllAmount = YssD.mul(rs.getDouble("FReplaceAmount"),rs.getDouble("shareRatio"));
				// 实际数量=第一次补票后的剩余数量 * 送股权益比例
				factAmount = YssD.mul(rs.getDouble("FRemaindAmount"),rs.getDouble("shareRatio"));
				
				tradeSettleDelRef.setSumAmount(shareAllAmount);//送股总数量
				
				tradeSettleDelRef.setRealAmount(factAmount);//实际数量
			}
			
		}catch (Exception e) {
			throw new YssException("处理送股数据出错！",e);
		}
	}
	/**
	 * 此方法处理配股数据
	 * @throws YssException
	 */
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
	
	/**
	 * 此方法处理强制处理数据
	 * @throws YssException
	 */
	private void doForceManage(ETFTradeSettleDetailBean etfTradeSettleDetail,
			ETFTradeSettleDetailRefBean tradeSettleDelRef,ResultSet rs,Date dDate,Date SGReplaceOver,Date SHReplaceOver) throws YssException{
		ETFTradeSettleDetailRefBean tradeRef = null;//交易结算明细关联bean
		try{
			tradeRef = new ETFTradeSettleDetailRefBean();//实例化
			tradeRef.setNum(etfTradeSettleDetail.getNum());//申请编号
			tradeRef.setRefNum("3");//关联编号 主要是区分：1-第一次补票，2-第二次补票，3-强制处理
			tradeRef.setMakeUpDate(dDate);//补票日期
			//单位成本（原币） = 当日收盘价 
			tradeRef.setUnitCost(rs.getDouble("FPrice"));
			//数量 = 申赎数量 + 权益数据实际数量 - 第一次补票数量 - 第二次补票数量
			tradeRef.setMakeUpAmount(YssD.sub(YssD.add(rs.getDouble("FReplaceAmount"),tradeSettleDelRef.getRealAmount()),
					rs.getDouble("FMakeUpAmount"),tradeSettleDelRef.getMakeUpAmount()));
			//汇率
			tradeRef.setExchangeRate(tradeSettleDelRef.getExchangeRate());
			//总成本（原币）= 单位成本（原币）* 数量
			tradeRef.setoMakeUpCost(YssD.round(YssD.mul(tradeRef.getUnitCost(),tradeRef.getMakeUpAmount()),2));
			//总成本（本币）= 总成本（原币）* 汇率
			tradeRef.sethMakeUpCost(YssD.round(YssD.mul(tradeRef.getoMakeUpCost(),tradeRef.getExchangeRate()),2));
			if(rs.getString("FBs").equalsIgnoreCase("B")){
				//可退替代款（原币） = 申赎数据可退替代款 （原币）- 第一次补票可退替代款 （原币）-第二次补票可退替代款 （原币）
				tradeRef.setOcReplaceCash(YssD.round(YssD.sub(etfTradeSettleDetail.getOcReplaceCash(),rs.getDouble("FOCPReplaceCash"),tradeSettleDelRef.getOcReplaceCash()),2));
				//可退替代款（本币） = 申赎数据可退替代款 （本币）- 第一次补票可退替代款 （本币）-第二次补票可退替代款 （本币）
				tradeRef.setHcReplaceCash(YssD.round(YssD.sub(etfTradeSettleDetail.getHcReplaceCash(),rs.getDouble("FHCPReplaceCash"),tradeSettleDelRef.getHcReplaceCash()),2));
				//应付替代款（原币） = （申赎数据替代金额（原币）-权益数据总派息（原币）- 权益数据权证价值（原币））* 强制处理数量 /（申赎数量+ 权益数据总数量） - 强制处理总成本（原币）
				tradeRef.setOpReplaceCash(YssD.sub(YssD.round(YssD.div(YssD.mul(YssD.sub(rs.getDouble("FOReplaceCash"),tradeSettleDelRef.getInterest(),tradeSettleDelRef.getWarrantCost()),tradeRef.getMakeUpAmount()),
						YssD.add(rs.getDouble("FReplaceAmount"),tradeSettleDelRef.getSumAmount())),2),tradeRef.getoMakeUpCost()));
				//应付替代款（本币） = （申赎数据替代金额（本币）-权益数据总派息（本币）- 权益数据权证价值（本币））* 强制处理数量 /（申赎数量+ 权益数据总数量） - 强制处理总成本（本币）
				tradeRef.setHpReplaceCash(YssD.sub(YssD.round(YssD.div(YssD.mul(YssD.sub(rs.getDouble("FHReplaceCash"),tradeSettleDelRef.getBbinterest(),tradeSettleDelRef.getBbwarrantCost()),tradeRef.getMakeUpAmount()),
						YssD.add(rs.getDouble("FReplaceAmount"),tradeSettleDelRef.getSumAmount())),2),tradeRef.gethMakeUpCost()));
				//数据方向
				tradeRef.setDataDirection("1");
				//退款日期
				tradeRef.setRefundDate(SGReplaceOver);
			}else{
				//可退替代款（原币） = 申赎数据可退替代款 （原币）- 第一次补票可退替代款 （原币）-第二次补票可退替代款 （原币）
				tradeRef.setOcReplaceCash(YssD.round(YssD.sub(etfTradeSettleDetail.getOcReplaceCash(),rs.getDouble("FOCPReplaceCash"),tradeSettleDelRef.getOcReplaceCash()),2));
				//可退替代款（本币） = 申赎数据可退替代款 （本币）- 第一次补票可退替代款 （本币）-第二次补票可退替代款 （本币）
				tradeRef.setHcReplaceCash(YssD.round(YssD.sub(etfTradeSettleDetail.getHcReplaceCash(),rs.getDouble("FHCPReplaceCash"),tradeSettleDelRef.getHcReplaceCash()),2));
				//应付替代款（原币） = 补票总成本原币 + 总派息原币* 强制处理数量/申赎数量  + 权证价值原币* 强制处理数量/申赎数量 
				tradeRef.setOpReplaceCash(YssD.add(
						tradeRef.getoMakeUpCost(),
						YssD.round(
									YssD.div(
											YssD.mul(tradeRef.getMakeUpAmount(),tradeSettleDelRef.getInterest()),
											rs.getDouble("FReplaceAmount")),
											2),
						YssD.round(
									YssD.div(
											YssD.mul(tradeRef.getMakeUpAmount(),tradeSettleDelRef.getWarrantCost()),
											rs.getDouble("FReplaceAmount")),
											2)));
				//应付替代款（本币） = 补票总成本本币 + 总派息本币* 强制处理数量/申赎数量 * 强制处理汇率 + 权证价值本币* 强制处理数量/申赎数量 * 强制处理汇率
				tradeRef.setHpReplaceCash(YssD.add(
						tradeRef.gethMakeUpCost(),
						YssD.round(
								YssD.mul(
										YssD.div(
												YssD.mul(tradeRef.getMakeUpAmount(),tradeSettleDelRef.getInterest()),
												rs.getDouble("FReplaceAmount")),
												tradeRef.getExchangeRate()),
												2),
						YssD.round(
								YssD.mul(
										YssD.div(
												YssD.mul(tradeRef.getMakeUpAmount(),tradeSettleDelRef.getWarrantCost()),
												rs.getDouble("FReplaceAmount")),
												tradeRef.getExchangeRate()),2)));
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
}






















