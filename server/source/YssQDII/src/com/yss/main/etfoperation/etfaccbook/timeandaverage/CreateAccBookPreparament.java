package com.yss.main.etfoperation.etfaccbook.timeandaverage;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.etfaccbook.CreateBookPretreatmentAdmin;
import com.yss.main.etfoperation.etfaccbook.CtlETFAccBook;
import com.yss.main.etfoperation.etfaccbook.PretValMktPriceAndExRate;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailRefBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * STORY #1789 QDV4中行2011年10月25日01_A
 * 华夏恒指ETF台帐处理类
 */

public class CreateAccBookPreparament extends CtlETFAccBook{
	private ArrayList tradeSettleDetail = new ArrayList();//保存申赎明细数据
	private HashMap tradeSettleDetailMap = new HashMap();//保存补票和权益数据

	//计数器(计算最大编号+1)
	private long maxNum = 0;
	//临时表List类
	private List tempBookList = new ArrayList();
	/**end*/
	
	private ETFParamSetBean paramSet = null;// ETF参数的实体类
	private ETFParamSetAdmin paramSetAdmin = null;
	private HashMap etfParam = null;//保存参数设置
	
	private java.util.Date startDate = null;//开始的申赎日期
	private java.util.Date endDate = null;//结束的申赎日期	
	private String portCodes = "";//组合代码
	
	private HashMap mapRights = null;//当日权益信息
	
	public CreateAccBookPreparament() {
		super();
	}
	
	/**处理业务的入口方法*/
	public void doManageAll() throws YssException{
		int days = 0;//保存循环日期之差
		Date dDate = null;//业务日期
		Date dTradeDate = null;//申赎日期
		PretValMktPriceAndExRate marketValue = null;//预处理估值行情和估值汇率
		CreateBookPretreatmentAdmin booPreAdmin = null;
		try{
			
			booPreAdmin = new CreateBookPretreatmentAdmin();
			booPreAdmin.setYssPub(pub);
			
			TempBook tempBook = new TempBook();
			tempBook.setYssPub(pub);
			
			marketValue = new PretValMktPriceAndExRate();//实例化
			marketValue.setYssPub(pub);//设置pub 
			days = YssFun.dateDiff(this.startDate,this.endDate);//循环日期时，保存最大日期与最小日期的差
			dDate = this.startDate;//得到操作的起始日期
			marketValue.getValMktPriceAndExRateBy(this.portCodes,dDate);//获取估值行情
			
			for(int i=0;i<=days;i++){//循环日期
				dTradeDate = getTradeDate(dDate);//根据申赎确认日期（即当前业务日期）获取申赎日期
								
				if(null != dTradeDate){//如果没有申赎数据就不需要处理了
					this.doTradestldtl(dTradeDate);//获取申赎明细数据
					booPreAdmin.insertTradeData(dTradeDate,this.portCodes,tradeSettleDetail);//保存申赎数据
					
					this.doTradeSettleDelRef(dTradeDate);//根据T日指定交易数据进行第一次补票
					booPreAdmin.insertTradeRefData(dTradeDate,this.portCodes,tradeSettleDetail,"MakeUp","1");//保存第一次补票数据
				}
				
				//权益处理
				doRightRef(dDate);
				//权证估值，更新台帐权证价值
				valRightsIssue(dDate);
				// 保存权益数据
				booPreAdmin.insertTradeRefData(dDate, this.portCodes,
											tradeSettleDetail, "Right", 
												YssFun.formatDate(dDate,"yyyyMMdd") + YssFun.formatDate(dDate,"yyyyMMdd"));
				
				//强制处理
				doMustMakeUp(dDate);
				booPreAdmin.insertTradeRefData(dDate,this.portCodes,tradeSettleDetail,"MakeUp","99");//保存强制处理数据

				//T日申请当天，生成临时台账数据
				//根据回报库,当先申赎日期与浏览日期显示同一天时就取此表信息显示
				doETFTempStandingBook(dDate);
				tempBook.Insert(dDate, this.portCodes, tempBookList," And FGradeType3 <> 'totaldata' ");//保存临时台账明细数据
				//汇总临时台账数据
				doTotalTempBook(dDate);
				tempBook.Insert(dDate, this.portCodes, tempBookList," And FGradeType3 = 'totaldata' ");//保存汇总数据

				dDate = YssFun.addDay(dDate,1);//每一次循环把日期加一天
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}		
	}

	/**解析前台传来的数据，实例化一些全局变量和类*/
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
		}catch (Exception e) {
			throw new YssException("做解析前台传来数据出错！",e);
		}
	}
	
	/**
	 * 根据申赎确认日期获取申赎申请日期
	 * @param dDate 确认日期
	 * @return
	 * @throws YssException
	 */
	private Date getTradeDate(Date dDate) throws YssException {
		Date dTradeDate = null;
		StringBuffer buffer = new StringBuffer(100);
		ResultSet rs = null;
		try{
			buffer.append("SELECT  t.FTradeDate FROM ").append(pub.yssGetTableName("Tb_TA_Trade"))
					.append(" t WHERE t.FConfimDate = ").append(dbl.sqlDate(dDate))//确认日
					.append(" AND t.FPortCode in ( ").append(operSql.sqlCodes(portCodes) + " ) ")//组合
					.append(" AND t.FSellType in ('01','02')")//销售类型为：申购、赎回
					.append(" AND t.FCheckState = 1");
			rs = dbl.openResultSet(buffer.toString());
			buffer.delete(0, buffer.length());
			
			if (rs.next()){
				dTradeDate = rs.getDate("FTradeDate");
			}
		}catch (Exception e) {
			throw new YssException("获取申购、赎回申请日期出错 ！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return dTradeDate;		
	}
	
	/**
	 * 根据申赎结果数据库生成申赎明细数据
	 * @param dDate
	 * @throws YssException
	 */
	private void doTradestldtl(Date dDate) throws YssException{
		ResultSet rs = null;//声明结果集
		long sNum = 0;//做拼接申请编号用
		String strNumDate = "";//保存申请编号
		String strKey = "";
		ETFTradeSettleDetailBean etfTradeSettleDetail= null;//投资者申赎明细
		ETFTradeSettleDetailBean etfTradeSettleDetailHz = null;//按股票汇总投资者申赎数据
		HashMap hzMap = new HashMap();
		StringBuffer tmpbuff = new StringBuffer();
		try{
			if(tradeSettleDetail.size()!=0){//如果集合不为空时，要先清空
				tradeSettleDetail.clear();
			}
			//做拼接SQL语句
			String sql = getdoTradestldtlSql(dDate);
			rs = dbl.openResultSet(sql);
			
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
				for (int t = 0; t < s.length()- String.valueOf(sNum).length(); t++) {
					tmpbuff.append("0");
				}
				
				strNumDate = strNumDate.substring(0, 9) + tmpbuff.toString() + sNum;
				tmpbuff.delete(0, tmpbuff.length());
				// ------------------------end--------------------------//
				etfTradeSettleDetail = new ETFTradeSettleDetailBean();
				
				etfTradeSettleDetail.setNum(strNumDate);//申请编号				
				etfTradeSettleDetail.setPortCode(rs.getString("FPortCode"));//组合代码
				etfTradeSettleDetail.setSecurityCode(rs.getString("FStockCode"));//证券代码
				etfTradeSettleDetail.setStockHolderCode(rs.getString("FOtherStockholder"));//投资者
				etfTradeSettleDetail.setBrokerCode(rs.getString("FBrokerCode"));//券商
				etfTradeSettleDetail.setSeatCode(rs.getString("FSeatNum"));//交易席位
				etfTradeSettleDetail.setBs(rs.getString("FTradeTypeCode").equalsIgnoreCase("KS")?"B":"S");//台账类型
				etfTradeSettleDetail.setBuyDate(dDate);//申赎日期
				//替代数量 = (申赎份额/参数设置中基准比例)* 股票篮文件中证券数量
				etfTradeSettleDetail.setReplaceAmount(YssD.mul(YssD.div(rs.getDouble("FTradeAmount"),
						paramSet.getNormScale()),rs.getDouble("FAmount")));
				etfTradeSettleDetail.setBraketNum(YssD.div(rs.getDouble("FTradeAmount"),paramSet.getNormScale()));//篮子数
				
				/**shashijie 2013-1-5 STORY 3328  修改:根据基础参数来源获取汇率*/
				double rate = getRightRate(dDate,rs.getString("Ftradecury"),
						rs.getString("FbaseRateSrcBPCode"), rs.getString("FbaseRateBPCode"),
						rs.getString("FportRateSrcBPCode"), rs.getString("FportRateBPCode"),
						rs.getString("Fportcode"));
				
				//单位成本 = 收盘价*基础汇率/组合汇率
				etfTradeSettleDetail.setUnitCost(YssD.round(YssD.mul(rs.getDouble("FPrice"), rate),2));
				//汇率
				etfTradeSettleDetail.setExchangeRate(rate);
				/**end shashijie 2013-1-5 STORY 3328 */
				
				if(rs.getString("FTradeTypeCode").equalsIgnoreCase("KS")){ //申购
					//替代金额原币 = round(股票篮中替代金额 ,2)* 篮子数
					etfTradeSettleDetail.setOReplaceCash(YssD.mul(YssD.round(YssD.div(rs.getDouble("FTotalMoney"),
																			etfTradeSettleDetail.getExchangeRate()),2),
																	etfTradeSettleDetail.getBraketNum()));
					//替代金额本币
					etfTradeSettleDetail.setHReplaceCash(YssD.round(YssD.mul(rs.getDouble("FTotalMoney"),
							etfTradeSettleDetail.getBraketNum()),2));
					//可退替代款原币 =替代金额原币 - 替代数量 * 收盘价
					etfTradeSettleDetail.setOcReplaceCash(YssD.round(YssD.sub(etfTradeSettleDetail.getOReplaceCash(),
							YssD.mul(etfTradeSettleDetail.getReplaceAmount(),rs.getDouble("FPrice"))),2));
					//可退替代款本币 = 替代金额本币 - 替代数量 * 单位成本 
					etfTradeSettleDetail.setHcReplaceCash(YssD.round(YssD.sub(etfTradeSettleDetail.getHReplaceCash(),
							YssD.mul(etfTradeSettleDetail.getReplaceAmount(),etfTradeSettleDetail.getUnitCost())),2));
				
				}else{
					//应退赎回款（原币） = 篮子中的股票数量*股票的T日收盘价
					etfTradeSettleDetail.setOReplaceCash(YssD.round(YssD.mul(etfTradeSettleDetail.getReplaceAmount(),
							rs.getDouble("FPrice")),2));
					//应退赎回款（本币） = round(round(股票篮中该股票的数量*单位成本，2）*赎回的篮子数,2)
					etfTradeSettleDetail.setHReplaceCash(YssD.round(YssD.mul(YssD.round(YssD.mul(
							rs.getDouble("FAmount"),etfTradeSettleDetail.getUnitCost()),2),
							etfTradeSettleDetail.getBraketNum()),2));
					//可退替代款原币 = 应退赎回款（原币）- 篮子中的股票数量*股票的T日收盘价
					etfTradeSettleDetail.setOcReplaceCash(0);
					//可退替代款本币 = 应退赎回款（本币）- 篮子中的股票数量*股票的T日收盘价 （本币）
					etfTradeSettleDetail.setHcReplaceCash(0);
				}
				
				//成交号码 + 席位号 + 合同序号
				etfTradeSettleDetail.setTradeNum(rs.getString("FTRADENUM") + "-" 
												+ rs.getString("FOTHERSEAT") + "-" 
												+ rs.getString("FContractNum"));

				tradeSettleDetail.add(etfTradeSettleDetail);//把投资者申赎数据存入集合中
				
				//按股票汇总投资者申赎数据
				strKey = rs.getString("FPortCode") + "\t" +							
								YssFun.toSqlDate(dDate) + "\t" + 
								(rs.getString("FTradeTypeCode").equalsIgnoreCase("KS")?"B":"S") + "\t" + 
								rs.getString("FStockCode");//组合代码+申赎日期+交易类型+证券代码
				
				if(hzMap.containsKey(strKey)){
					etfTradeSettleDetailHz = (ETFTradeSettleDetailBean) hzMap.get(strKey);
					etfTradeSettleDetailHz.setReplaceAmount(//汇总替代数量
											YssD.add(etfTradeSettleDetailHz.getReplaceAmount(),
													etfTradeSettleDetail.getReplaceAmount()));
					etfTradeSettleDetailHz.setBraketNum(YssD.div(
														etfTradeSettleDetailHz.getReplaceAmount(), 
														paramSet.getNormScale()));//篮子数
					etfTradeSettleDetailHz.setOReplaceCash(//汇总各投资者替代金额（原币）
							YssD.add(etfTradeSettleDetailHz.getOReplaceCash(),
									etfTradeSettleDetail.getOReplaceCash()));
					etfTradeSettleDetailHz.setHReplaceCash(//汇总各投资者替代金额（本位币）
							YssD.add(etfTradeSettleDetailHz.getHReplaceCash(),
									etfTradeSettleDetail.getHReplaceCash()));				
					etfTradeSettleDetailHz.setOcReplaceCash(//可退替代款汇总金额（原币）
									YssD.round(YssD.sub(etfTradeSettleDetailHz.getOReplaceCash(), 
												YssD.mul(etfTradeSettleDetailHz.getReplaceAmount(), 
															rs.getDouble("FPrice"))),2));
					etfTradeSettleDetailHz.setHcReplaceCash(//可退替代款汇总金额（本位币）
									YssD.round(YssD.sub(etfTradeSettleDetailHz.getHReplaceCash(), 
												YssD.mul(etfTradeSettleDetailHz.getReplaceAmount(), 
															etfTradeSettleDetailHz.getUnitCost())),2));
					
					hzMap.put(strKey, etfTradeSettleDetailHz);	
				}else{
					etfTradeSettleDetailHz = (ETFTradeSettleDetailBean) etfTradeSettleDetail.clone();
					//-----重新获取交易编号------//
					sNum++;				
					for (int t = 0; t < s.length()- String.valueOf(sNum).length(); t++) {
						tmpbuff.append("0");
					}					
					strNumDate = strNumDate.substring(0, 9) + tmpbuff.toString() + sNum;
					tmpbuff.delete(0, tmpbuff.length());
					// -------end---------------//
					etfTradeSettleDetailHz.setNum(strNumDate);
					etfTradeSettleDetailHz.setStockHolderCode(" ");//汇总数据投资者字段置为空格
					etfTradeSettleDetailHz.setBrokerCode(" ");//汇总数据券商字段置为空格
					etfTradeSettleDetailHz.setSeatCode(" ");//汇总数据席位字段置为空格
					etfTradeSettleDetailHz.setTradeNum(" ");//汇总数据合同序号字段置为空格
					hzMap.put(strKey, etfTradeSettleDetailHz);
				}
								
				tradeSettleDetailMap.put(strKey, etfTradeSettleDetail);
			}
			
			Iterator iter = hzMap.keySet().iterator();
			while(iter.hasNext()){//将汇总数据添加到申赎明细数据集合中
				strKey = (String) iter.next();
				tradeSettleDetail.add((ETFTradeSettleDetailBean)hzMap.get(strKey));
			}
			
		}catch(Exception e){
			throw new YssException("获取申赎明细数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 根据指定交易数据处理第一次补票
	 * @param dDate
	 * @throws YssException
	 */
	private void doTradeSettleDelRef(Date dDate) throws YssException {
		StringBuffer buff = null;//做拼接SQL语句
		ResultSet rs = null;//声明结果集
		ETFTradeSettleDetailBean etfTradeSettleDetail = null;//交易明细
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;//交易明细关联
		ETFTradeSettleDetailRefBean tradeSettleDelRefHz = null;
		HashMap hzMap = new HashMap();
		EachExchangeHolidays holiday = null;//节假日代码类
		String strKey = "";
		double dMakeUpAmount = 0;//补票数量
		
		try{
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			Date dSGReturnDate = getSGReplaceOver(dDate);//推算申购退款日期
			Date dSHReturnDate = getSHReplaceOver(dDate);//推算赎回退款日期
			
			if(tradeSettleDetail.size()!=0){//如果集合不为空时，要先清空
				tradeSettleDetail.clear();
			}
			
			buff = new StringBuffer(1000);			
			buff.append("select a.*, cj.FTradeFee, ")
				.append(dbl.sqlIsNull("cj.FTradeAmount","0")).append(" as tradeAmount,")
				.append(" cj.FTradeMoney as tradeMoney,")
				
				/**shashijie 2013-1-5 STORY 3328  获取ETF基础参数设置 */
				.append(" ra.FBaseRate, ra.FPortRate,p.* from (select t.*, se.FTradeCury from ")
				/**end shashijie 2013-1-5 STORY */
				
				.append(pub.yssGetTableName("tb_etf_tradestldtl"))
				.append(" t left join (select FTradeCury, fsecuritycode from ")
				.append(pub.yssGetTableName("tb_para_security"))
				.append(" where FCheckState = 1) se on t.fsecuritycode = se.fsecuritycode ")
				.append(" where t.fportcode in(").append(this.operSql.sqlCodes(this.portCodes))
				.append(") and t.fbuydate = ").append(dbl.sqlDate(dDate))
				.append(") a left join (select FTradeAmount,FTradeMoney,FTradeFee,")
				.append("fportcode,fsecuritycode,fcontractnum,fstockercode,fbs from ")
				.append(pub.yssGetTableName("tb_etf_cjmxinterface"))
				.append(" where FCheckState = 1 and FPortCode in (").append(this.operSql.sqlCodes(this.portCodes))
				.append(") and FBargainDate = ").append(dbl.sqlDate(dDate))
				.append(") cj on a.fsecuritycode = cj.fsecuritycode and a.fportcode = cj.fportcode")
				.append(" and substr(a.ftradenum,-15,15) = cj.fcontractnum and a.fstockholdercode = cj.fstockercode")
				.append(" and a.fbs = cj.fbs")
				.append(" left join (select FBaseRate,FPortRate,FCuryCode,FPortCode from ")
				.append(pub.yssGetTableName("tb_data_pretvalrate"))
				.append(" where FCheckState = 1 and FPortCode in (").append(this.operSql.sqlCodes(this.portCodes))
				.append(") and FValDate = ").append(dbl.sqlDate(dDate))
				.append(") ra on a.fportcode = ra.fportcode and a.FTradeCury = ra.FCuryCode")
				
				/**shashijie 2013-1-5 STORY 3328  获取ETF基础参数设置*/
				.append(" Join (Select * From "+pub.yssGetTableName("Tb_ETF_Param")+
						" ) p On p.fportcode = a.Fportcode ")
				/**end shashijie 2013-1-5 STORY 3328 */
						
				.append(" order by a.ftradenum");				
				
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			
			while(rs.next()){								
				if(rs.getDouble("tradeAmount") != 0 || //有指定交易数据才进行第一次补票
						rs.getString("FStockHolderCode").trim().length() == 0){//汇总数据不能排除

					etfTradeSettleDetail = new ETFTradeSettleDetailBean();
					tradeSettleDelRef = new ETFTradeSettleDetailRefBean();		
					
					tradeSettleDelRef.setNum(rs.getString("FNum"));//明细关联数据的申请编号
					tradeSettleDelRef.setRefNum("1");//关联编号：主要是区分：1-第一次补票，2-第二次补票...99-强制处理
					tradeSettleDelRef.setMakeUpDate(dDate);//补票日期
					
					//补票数量
					//如果券商回报数据中补票数量超出替代数量，则将替代数量赋值给补票数量
					dMakeUpAmount = rs.getDouble("tradeAmount") < rs.getDouble("FReplaceAmount") ? 
											rs.getDouble("tradeAmount") : rs.getDouble("FReplaceAmount");
											
					tradeSettleDelRef.setMakeUpAmount(dMakeUpAmount);//补票数量
					
					/**shashijie 2013-1-5 STORY 3328  修改:根据基础参数来源获取汇率*/
					double rate = getRightRate(dDate,rs.getString("Ftradecury"),
							rs.getString("FbaseRateSrcBPCode"), rs.getString("FbaseRateBPCode"),
							rs.getString("FportRateSrcBPCode"), rs.getString("FportRateBPCode"),
							rs.getString("Fportcode"));
					
					/**end shashijie 2013-1-5 STORY 3328 */
					
					if(rs.getString("FBs").equalsIgnoreCase("B") && //申购
							rs.getString("fstockholdercode").trim().length() > 0){//如果是汇总数据，不计算
						
						/**shashijie 2013-1-5 STORY 3328  修改:根据基础参数来源获取汇率*/
						//单位成本本币 = 券商明细表 round(round((金额+费率)*汇率,2)/数量,2)
						tradeSettleDelRef.setUnitCost(YssD.round(
							YssD.div(
								YssD.round(YssD.mul(YssD.add(
										rs.getDouble("tradeMoney"),rs.getDouble("FTradeFee")),
										rate),2),
											rs.getDouble("tradeAmount")),2));
						/**end shashijie 2013-1-5 STORY 3328 */
						
						//原币总成本原币 = 单位成本本币 * 补票数量
						tradeSettleDelRef.setoMakeUpCost(YssD.mul(tradeSettleDelRef.getUnitCost(),
																	tradeSettleDelRef.getMakeUpAmount()));						
						//应付替代款（原币） = 替代款金额原币 * 补票数量/申赎数量 - 补票数量*单位成本原币
						tradeSettleDelRef.setOpReplaceCash(YssD.round(YssD.sub(
										YssD.div(
												YssD.mul(
														rs.getDouble("FOReplaceCash"),
														tradeSettleDelRef.getMakeUpAmount()),
											rs.getDouble("FReplaceAmount")),
									YssD.mul(tradeSettleDelRef.getMakeUpAmount(),
											YssD.div(YssD.add(rs.getDouble("tradeMoney"),rs.getDouble("FTradeFee")),
													tradeSettleDelRef.getMakeUpAmount()))),2));
						//应付替代款（本币） = 替代款金额本币 * 补票数量/申赎数量 - 补票数量*单位成本本币
						tradeSettleDelRef.setHpReplaceCash(YssD.round(YssD.sub(
										YssD.div(
												YssD.mul(
														rs.getDouble("FHReplaceCash"),
														tradeSettleDelRef.getMakeUpAmount()),
													rs.getDouble("FReplaceAmount")),
											YssD.mul(tradeSettleDelRef.getMakeUpAmount(),
													tradeSettleDelRef.getUnitCost())),2));
						//数据方向
						tradeSettleDelRef.setDataDirection("1");
						
					}else if(rs.getString("FBs").equalsIgnoreCase("S") && //赎回
								rs.getString("fstockholdercode").trim().length() > 0){//如果是汇总数据，不计算
						/**shashijie 2013-1-5 STORY 3328  修改:根据基础参数来源获取汇率*/
						//单位成本本币 = 券商明细表 round(round((金额-费率)*汇率,2)/数量,2)
						tradeSettleDelRef.setUnitCost(YssD.round(
							YssD.div(
								YssD.round(YssD.mul(YssD.sub(
										rs.getDouble("tradeMoney"),rs.getDouble("FTradeFee")),
										rate),2)
											,rs.getDouble("tradeAmount")),2));
						/**end shashijie 2013-1-5 STORY 3328 */
						
						//原币总成本原币 = 单位成本本币 * 补票数量
						tradeSettleDelRef.setoMakeUpCost(YssD.mul(tradeSettleDelRef.getUnitCost(),
																	tradeSettleDelRef.getMakeUpAmount()));
						//应付替代款（原币） =补票数量*单位成本原币 - 替代款金额原币 * 补票数量/申赎数量 
						tradeSettleDelRef.setOpReplaceCash(
								YssD.sub(
										YssD.mul(tradeSettleDelRef.getMakeUpAmount(),
												YssD.round(YssD.div(
														YssD.add(rs.getDouble("tradeMoney"),rs.getDouble("FTradeFee")),
														tradeSettleDelRef.getMakeUpAmount()),2)),
										YssD.round(
												YssD.div(
														YssD.mul(
																rs.getDouble("FOReplaceCash"),
																tradeSettleDelRef.getMakeUpAmount()),
																rs.getDouble("FReplaceAmount")),2)));
						//应付替代款（本币） = 补票数量*单位成本本币 - 可退替代款本币 * 补票数量/申赎数量 
						tradeSettleDelRef.setHpReplaceCash(YssD.round(
								YssD.sub(
										YssD.mul(tradeSettleDelRef.getMakeUpAmount(),tradeSettleDelRef.getUnitCost()),
												YssD.div(
														YssD.mul(
																rs.getDouble("FHReplaceCash"),
																tradeSettleDelRef.getMakeUpAmount()),
																rs.getDouble("FReplaceAmount"))),2));

						//数据方向
						tradeSettleDelRef.setDataDirection("-1");
					}
					//本币总成本 = 原币总成本 * 基础汇率/组合汇率
					tradeSettleDelRef.sethMakeUpCost(YssD.round(YssD.mul(
							tradeSettleDelRef.getoMakeUpCost(),tradeSettleDelRef.getExchangeRate()),2));
					
					//可退替代款（原币）=可退替代款原币 * 补票数量/申赎数量
					double ocReplaceCash = getHcReplaceCash(rs.getDouble("FOCReplaceCash"), 
							tradeSettleDelRef.getMakeUpAmount(), rs.getDouble("FReplaceAmount"), 0);
					tradeSettleDelRef.setOcReplaceCash(ocReplaceCash);
					//可退替代款（本币）=可退替代款本币 * 补票数量/申赎数量 
					double hcReplaceCash = getHcReplaceCash(rs.getDouble("FHCReplaceCash"), 
							tradeSettleDelRef.getMakeUpAmount(), rs.getDouble("FReplaceAmount"), 0);
					tradeSettleDelRef.setHcReplaceCash(hcReplaceCash);
					
					//可退替代款余额（原币）
					tradeSettleDelRef.setOCanRepCash(YssD.sub(rs.getDouble("FOCReplaceCash"),
															tradeSettleDelRef.getOcReplaceCash()));
					
					//可退替代款余额（本位币）
					tradeSettleDelRef.setOCanRepCash(YssD.sub(rs.getDouble("FHCReplaceCash"),
															tradeSettleDelRef.getHcReplaceCash()));
					
					/**shashijie 2013-1-5 STORY 3328  修改:根据基础参数来源获取汇率*/
					//估值汇率
					tradeSettleDelRef.setExchangeRate(rate);
					/**end shashijie 2013-1-5 STORY 3328 */
							
					//剩余数量 = 申赎数量 - 第一次补票数量
					tradeSettleDelRef.setRemaindAmount(YssD.sub(rs.getDouble("FReplaceAmount"),
							tradeSettleDelRef.getMakeUpAmount()));

					if(tradeSettleDelRef.getRemaindAmount() == 0){
						//若补票完成，推算出退款日期
						if(rs.getString("FBs").equalsIgnoreCase("B")){
							tradeSettleDelRef.setRefundDate(dSGReturnDate);
						}else{
							tradeSettleDelRef.setRefundDate(dSHReturnDate);
						}						
					}

					//清算标识
					tradeSettleDelRef.setSettleMark("N");
					//数据标识
					tradeSettleDelRef.setDataMark("0");

					if(rs.getString("FStockHolderCode").trim().length() > 0){//保存补票明细数据，汇总数据在最后添加
						etfTradeSettleDetail.getAlTradeSettleDelRef().add(tradeSettleDelRef);//保存明细关联数据
						tradeSettleDetail.add(etfTradeSettleDetail);//把明细数据存入集合中
					}
					
					//以下按股票汇总投资者补票数据
					strKey = rs.getString("FPortCode") + "\t" +	
									YssFun.toSqlDate(dDate) + "\t" + 
									rs.getString("FBS") + "\t" + 
									rs.getString("FSecurityCode");//组合代码+申赎日期+交易类型+证券代码
					if(hzMap.containsKey(strKey)){
						tradeSettleDelRefHz = (ETFTradeSettleDetailRefBean) hzMap.get(strKey);
						if(rs.getString("fstockholdercode").trim().length() == 0){
							tradeSettleDelRefHz.setNum(rs.getString("FNum"));
							hzMap.put(strKey, tradeSettleDelRefHz);
							continue;
						}
						//汇总补票数量
						tradeSettleDelRefHz.setMakeUpAmount(YssD.add(
															tradeSettleDelRefHz.getMakeUpAmount(),
																	tradeSettleDelRef.getMakeUpAmount()));
						//汇总应付替代款（原币）
						tradeSettleDelRefHz.setOpReplaceCash(YssD.add(
															tradeSettleDelRefHz.getOpReplaceCash(),
																tradeSettleDelRef.getOpReplaceCash()));
						//汇总应付替代款（本位币）
						tradeSettleDelRefHz.setHpReplaceCash(YssD.add(
															tradeSettleDelRefHz.getHpReplaceCash(),
																tradeSettleDelRef.getHpReplaceCash()));
						//汇总可退替代款（原币）
						tradeSettleDelRefHz.setOcReplaceCash(YssD.add(
															tradeSettleDelRefHz.getOcReplaceCash(),
																tradeSettleDelRef.getOcReplaceCash()));
						
						//汇总可退替代款（本位币）
						tradeSettleDelRefHz.setHcReplaceCash(YssD.add(
															tradeSettleDelRefHz.getHcReplaceCash(),
																tradeSettleDelRef.getHcReplaceCash()));
						
						tradeSettleDelRefHz.setRefundDate(null);//汇总数据不设置退款日期
						hzMap.put(strKey, tradeSettleDelRefHz);
					}else{
						tradeSettleDelRefHz = (ETFTradeSettleDetailRefBean) tradeSettleDelRef.clone();
						if(rs.getString("fstockholdercode").trim().length() == 0){//如果为汇总数据
							tradeSettleDelRefHz.setMakeUpAmount(0);//补票数量
							tradeSettleDelRefHz.sethMakeUpCost(0);//补票成本
							tradeSettleDelRefHz.setOcReplaceCash(0);//可退替代款（原币）
							tradeSettleDelRefHz.setHcReplaceCash(0);//可退替代款（本位币）
							tradeSettleDelRefHz.setOCanRepCash(0);//可退替代款余额（原币）
							tradeSettleDelRefHz.setHCanRepCash(0);//可退替代款余额（本位币）
							tradeSettleDelRefHz.setRemaindAmount(0);//剩余数量								
							tradeSettleDelRefHz.setDataDirection(rs.getString("FBS").equals("B") ? "1" : "-1");//数据方向
						}
						hzMap.put(strKey, tradeSettleDelRefHz);
					}
				}
			}
			
			Iterator iter = hzMap.keySet().iterator();
			while(iter.hasNext()){//将汇总数据添加到补票数据集合中
				strKey = (String) iter.next();
				etfTradeSettleDetail = new ETFTradeSettleDetailBean();
				tradeSettleDelRef = (ETFTradeSettleDetailRefBean)hzMap.get(strKey);
				if(tradeSettleDelRef.getMakeUpAmount() == 0){
					continue;//如果汇总数据补票数量为空，说明当日没有补票，因此也不用生成汇总数据
				}
				etfTradeSettleDetail.getAlTradeSettleDelRef().add(tradeSettleDelRef);
				tradeSettleDetail.add(etfTradeSettleDetail);
			}
			
		}catch (Exception e) {
			throw new YssException("处理第一次指定交易补票数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 台帐权益处理
	 * @param dDate 除权日期（当前业务日期）
	 * @throws YssException
	 */
	private void doRightRef(Date dDate) throws YssException {
		StringBuffer buff = null;//做拼接SQL语句
		ResultSet rs = null;//声明结果集
		ETFTradeSettleDetailBean etfTradeSettleDetail = null;//交易明细
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;//交易明细关联
		ETFTradeSettleDetailRefBean tradeSettleDelRefHz = null;
		HashMap hzMap = new HashMap();
		EachExchangeHolidays holiday = null;//节假日代码类
		String strKey = "";
		PreparedStatement pst = null;
		double dBonusshareRatio = 0.0;
		double dDividendRatio = 0.0;
		double dRightsissueRatio = 0.0;
		double dRIPrice = 0.0;
		
		try{
			if(tradeSettleDetail.size()!=0){//如果集合不为空时，要先清空
				tradeSettleDetail.clear();
			}
			
			getExRightsInfo(dDate);	//获取权益信息
			if(null == mapRights || mapRights.size() == 0){
				return;//当天如果没有权益，不做处理
			}
				
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			
			/**shashijie 2013-1-5 STORY 3328 使用新获取工作日方法,考虑境内境外*/
			Date dBuydate = getWorkDayMakeBack(dDate,
					paramSet.getLastestDealDayNum()*-1,(String)paramSet.getHoildaysRela().get("lastestdealdaynum"),
					paramSet.getLastestDealDayNum2()*-1,(String)paramSet.getHoildaysRela().get("lastestdealdaynum2"));
			
			//测试
			//Date dBuydate2 = getWorkDayByWhere(paramSet.getsCrossHolidayCode(), dDate, -2);//获取申赎日期
			/**end shashijie 2013-1-5 STORY 3328 */
			
			buff = new StringBuffer(1000);		
			buff.append("select t.*,ref.fsummakeupamount,ref.fsumrealamount,")
			
				/**shashijie 2013-1-5 STORY 3328  获取ETF基础参数设置*/
				.append("Mk.FPrice,ra.FBaseRate, ra.FPortRate,Se.*,p.* from ")
				/**end shashijie 2013-1-5 STORY */
				
				.append(pub.yssGetTableName("tb_etf_tradestldtl"))
				.append(" t left join (select FTradeCury, fsecuritycode from ")
				.append(pub.yssGetTableName("tb_para_security"))
				.append(" where FCheckState = 1) se on t.fsecuritycode = se.fsecuritycode")
				.append(" left join (select fnum,sum(nvl(fmakeupamount,0)) as fsummakeupamount,")
				.append("sum(nvl(frealamount,0)) as fsumrealamount from ")
				.append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" where fmakeupdate < ").append(dbl.sqlDate(dDate))
				.append(" or fexrightdate < ").append(dbl.sqlDate(dDate))
				.append(" group by fnum ) ref on ref.fnum = t.fnum")
				.append(" left join(Select FPortCode,FSecurityCode,FPrice from ")
				.append(pub.yssGetTableName("tb_data_pretvalmktprice"))
				.append(" Where Fcheckstate = 1 And Fvaldate = ").append(dbl.sqlDate(dDate))
				.append(" And Fportcode In (").append(this.operSql.sqlCodes(this.portCodes))
				.append(")) Mk On t.Fsecuritycode = Mk.Fsecuritycode And t.Fportcode = Mk.Fportcode")
				.append(" left join (select FBaseRate, FPortRate, FCuryCode, FPortCode from ")
				.append(pub.yssGetTableName("tb_data_pretvalrate"))
				.append(" where FCheckState = 1 ")
				.append(" And Fportcode In (").append(this.operSql.sqlCodes(this.portCodes))
				.append(") And Fvaldate = ").append(dbl.sqlDate(dDate))
				.append(") ra on t.fportcode = ra.fportcode and se.FTradeCury = ra.FCuryCode")
				
				/**shashijie 2013-1-5 STORY 3328  获取ETF基础参数设置*/
				.append(" Join (Select * From "+pub.yssGetTableName("Tb_ETF_Param")+
						" ) p On p.fportcode = t.Fportcode ")
				/**end shashijie 2013-1-5 STORY 3328 */
				
				.append(" where t.fbuydate between ").append(dbl.sqlDate(dBuydate))
				.append(" and ").append(dbl.sqlDate(YssFun.addDay(dDate, -1)))
				.append(" and t.fportcode in (").append(this.operSql.sqlCodes(this.portCodes))
				.append(") and t.fsecuritycode = ?");
				
			pst = dbl.getPreparedStatement(buff.toString());
			buff.delete(0, buff.length());
			
			while(YssFun.dateDiff(dBuydate, dDate) > 0){
				Iterator iter = mapRights.keySet().iterator();
				while(iter.hasNext()){
					String strSecurityCode = (String)iter.next();
					String sValue = (String)mapRights.get(strSecurityCode);
					String[] sRights = sValue.split("\f");
					for(int i = 0; i < sRights.length; i++){
						String[] sRightsValue = sRights[i].split("\t");
						if(sRightsValue[0].equals("bonusshare")){
							dBonusshareRatio = Double.parseDouble(sRightsValue[1]);
						}else if(sRights[i].split("\t")[0].equals("dividend")){
							dDividendRatio = Double.parseDouble(sRightsValue[1]);
						}else if(sRights[i].split("\t")[0].equals("rightsissue")){
							dRightsissueRatio = Double.parseDouble(sRightsValue[1]);
							dRIPrice = Double.parseDouble(sRightsValue[2]);
						}
					}
					
					pst.setString(1, strSecurityCode);
					
					rs = pst.executeQuery();
					while(rs.next()){
						
						etfTradeSettleDetail = new ETFTradeSettleDetailBean();
						tradeSettleDelRef = new ETFTradeSettleDetailRefBean();
						
						tradeSettleDelRef.setNum(rs.getString("FNum"));//申请编号
						//关联编号：存放 权益日期 + 当前业务日期（预留对发生多次权益的情况进行处理）
						tradeSettleDelRef.setRefNum(
									YssFun.formatDate(dDate, "yyyyMMdd") + 
										YssFun.formatDate(dDate, "yyyyMMdd"));
						tradeSettleDelRef.setExRightDate(dDate);//除权日期		
						
						/**shashijie 2013-1-5 STORY 3328  修改:根据基础参数来源获取汇率*/
						double rate = getRightRate(dDate,rs.getString("Ftradecury"),
								rs.getString("FbaseRateSrcBPCode"), rs.getString("FbaseRateBPCode"),
								rs.getString("FportRateSrcBPCode"), rs.getString("FportRateBPCode"),
								rs.getString("Fportcode"));
						
						tradeSettleDelRef.setRightRate(rate);//除权日汇率
						/**end shashijie 2013-1-5 STORY 3328 */
						
						tradeSettleDelRef.setSumAmount(YssD.round(YssD.mul(rs.getDouble("FReplaceAmount"), dBonusshareRatio),0));//权益总数量
						//除权日前一日的剩余未补完数量
						double dRemaindAmount = YssD.sub(YssD.add(rs.getDouble("FReplaceAmount"),
								rs.getDouble("fsumrealamount")), rs.getDouble("fsummakeupamount"));
						tradeSettleDelRef.setRealAmount(YssD.round(YssD.mul(dRemaindAmount,
								dBonusshareRatio), 0));// 权益实际数量 = 除权日前一日的剩余未补完数量 * 权益比例						
						
						tradeSettleDelRef.setInterest(YssD.round(YssD.mul(rs.getDouble("FReplaceAmount"), 
									dDividendRatio),2));//分红（原币）
						
						/**shashijie 2013-1-5 STORY 3328  修改:根据基础参数来源获取汇率*/
						tradeSettleDelRef.setBbinterest(YssD.round(YssD.mul(rs.getDouble("FReplaceAmount"),
									dDividendRatio,
								rate), 2));//分红（本位币）
						/**end shashijie 2013-1-5 STORY 3328 */
						
						//如果（当日收盘价－配股价）<＝0，当日权证价值＝0
						double dSub = YssD.sub(rs.getDouble("FPrice"), dRIPrice) > 0 ? 
												YssD.sub(rs.getDouble("FPrice"), dRIPrice) : 0.0;
						tradeSettleDelRef.setWarrantCost(YssD.round(YssD.mul(rs.getDouble("FReplaceAmount"),
													dRightsissueRatio, dSub), 2));//权证价值（原币）
						
						/**shashijie 2013-1-5 STORY 3328  修改:根据基础参数来源获取汇率*/
						double dPortPrice = YssD.round(YssD.mul(rs.getDouble("FPrice"),rate),2);
						double dPortRIPrice = YssD.round(YssD.mul(dRIPrice,rate),2);
						/**end shashijie 2013-1-5 STORY 3328 */
						
						double dPortSub = YssD.sub(dPortPrice,dPortRIPrice) > 0 ? 
													YssD.sub(dPortPrice, dPortRIPrice) : 0.0;
						tradeSettleDelRef.setBbwarrantCost(YssD.round(YssD.mul(rs.getDouble("FReplaceAmount"),
								dRightsissueRatio, dPortSub),2));//权证价值（本位币）
						
						//数据方向
						tradeSettleDelRef.setDataDirection(rs.getString("FBS").equals("B") ? "1" : "-1");
						//清算标识
						tradeSettleDelRef.setSettleMark("N");
						//数据标识
						tradeSettleDelRef.setDataMark("0");
						
						if(rs.getString("FStockHolderCode").trim().length() > 0){//保存权益明细数据，汇总数据在最后添加
							etfTradeSettleDetail.getAlTradeSettleDelRef().add(tradeSettleDelRef);
							tradeSettleDetail.add(etfTradeSettleDetail);//把明细数据存入集合中
						}
						
						//以下处理汇总数据
						strKey = rs.getString("FPortCode") + "\t" +	
									rs.getDate("FBuyDate") + "\t" + 
										rs.getString("FBS") + "\t" + 
											rs.getString("FSecurityCode");//组合代码+申赎日期+交易类型+证券代码
						if(hzMap.containsKey(strKey)){
							tradeSettleDelRefHz = (ETFTradeSettleDetailRefBean) hzMap.get(strKey);
							if(rs.getString("fstockholdercode").trim().length() == 0){
								tradeSettleDelRef.setRealAmount(0);
								double dTemp = tradeSettleDelRefHz.getRealAmount();
								tradeSettleDelRefHz = (ETFTradeSettleDetailRefBean) tradeSettleDelRef.clone();
								tradeSettleDelRefHz.setRealAmount(dTemp);
							}
							//实际数量 = sum（各投资者权益）
							tradeSettleDelRefHz.setRealAmount(YssD.add(tradeSettleDelRefHz.getRealAmount(), tradeSettleDelRef.getRealAmount()));
							hzMap.put(strKey, tradeSettleDelRefHz);
						}else{
							tradeSettleDelRefHz = (ETFTradeSettleDetailRefBean) tradeSettleDelRef.clone();
							if(rs.getString("fstockholdercode").trim().length() == 0){//如果为汇总数据
								tradeSettleDelRefHz.setRealAmount(0);//实际数量 = sum（各投资者权益）
							}
							hzMap.put(strKey, tradeSettleDelRefHz);
						}
					}
					
					//重置，获取下一个权益的相关权益数据
					dBonusshareRatio = 0.0;
					dDividendRatio = 0.0;
					dRightsissueRatio = 0.0;
					dRIPrice = 0.0;
										
					Iterator iterator = hzMap.keySet().iterator();
					while(iterator.hasNext()){//将汇总数据添加到补票数据集合中
						String sKey = (String) iterator.next();
						etfTradeSettleDetail = new ETFTradeSettleDetailBean();
						etfTradeSettleDetail.getAlTradeSettleDelRef().add((ETFTradeSettleDetailRefBean)hzMap.get(sKey));
						tradeSettleDetail.add(etfTradeSettleDetail);
					}
					hzMap.clear();
				}
				dBuydate = YssFun.addDay(dDate, 1);
			}
			
		}catch (Exception e) {
			throw new YssException("处理台帐权益出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
		}
	}
	
	/**
	 * 获取权益信息
	 * @param dDate
	 * @throws YssException
	 */
	private void getExRightsInfo(java.util.Date dDate) throws YssException {
		mapRights = new HashMap();
		ResultSet rs = null;
		StringBuffer buff = null;
		String sKey = "";
		String sValue = "";
		
		try{
			buff = new StringBuffer(200);
			buff.append("select FAfterTaxRatio,FPreTaxRatio,FExrightDate,")
				.append("0 as FRIPrice,FSSecurityCode,'bonusshare' as rightsType from ")
				.append(pub.yssGetTableName("tb_data_bonusshare"))
				.append(" where FCheckState = 1 and FExrightDate = ").append(dbl.sqlDate(dDate))
				.append(" union all select FAfterTaxRatio,FPreTaxRatio,FDividendDate as FExrightDate,")
				.append("0 as FRIPrice,fsecuritycode as FSSecurityCode,'dividend' as rightsType from ")
				.append(pub.yssGetTableName("tb_data_dividend"))
				.append(" where FCheckState = 1 and FDividendDate = ").append(dbl.sqlDate(dDate))
				.append(" union all select FAfterTaxRatio,FPreTaxRatio,FExrightDate,")
				.append("FRIPrice,fsecuritycode as FSSecurityCode,'rightsissue' as rightsType from ")
				.append(pub.yssGetTableName("tb_data_rightsissue"))
				.append(" where FCheckState = 1 and FExrightDate = ").append(dbl.sqlDate(dDate));
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0, buff.length());
			
			while(rs.next()){		
				sKey = rs.getString("FSSecurityCode");
				
				sValue = rs.getString("rightsType") + "\t" + 
							rs.getDouble("FAfterTaxRatio") + "\t" +
							  rs.getDouble("FRIPrice");
				
				if(mapRights.containsKey(sKey)){
					sValue = (String)mapRights.get(sKey) + "\f" + sValue ;
				}
				mapRights.put(sKey, sValue);
			}
		}catch (Exception e) {
			throw new YssException("获取权益信息出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}		
	}	
	
	/**
	 * 根据最新行情和汇率估值台帐权证价值
	 * @param dDate
	 * @throws YssException
	 */
	private void valRightsIssue(Date dDate) throws YssException {
		StringBuffer buff = null;//做拼接SQL语句
		Connection conn = null;//数据库连接声明
		boolean bTrans = true;//事物控制标识
		ResultSet rs = null;//声明结果集
		PreparedStatement pst = null;
		EachExchangeHolidays holiday = null;//节假日代码类
		ETFTradeSettleDetailBean etfTradeSettleDetail = null;//交易明细
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;//交易明细关联
		HashMap tempMap = null;
		
		try{
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//设置为事物手动提交
			
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("tb_etf_tradstldtlref"));//给表加锁
			
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			
			/**shashijie 2013-1-5 STORY 3328 使用新获取工作日方法,考虑境内境外*/
			Date buyDate = getWorkDayMakeBack(dDate,
					0,(String)paramSet.getHoildaysRela().get("lastestdealdaynum"),
					0,(String)paramSet.getHoildaysRela().get("lastestdealdaynum2"));
			//测试
			//Date buyDate2 = getWorkDayByWhere(paramSet.getsCrossHolidayCode(), dDate, 0);
			if(YssFun.dateDiff(dDate,buyDate) != 0){//如果是境外节假日，就不用更新权证价值了
				return;
			}
			buyDate = getWorkDayMakeBack(dDate,
					paramSet.getLastestDealDayNum()*-1,(String)paramSet.getHoildaysRela().get("lastestdealdaynum"),
					paramSet.getLastestDealDayNum2()*-1,(String)paramSet.getHoildaysRela().get("lastestdealdaynum2"));
			//测试
			//buyDate2 = getWorkDayByWhere(paramSet.getsCrossHolidayCode(), dDate, -2);//获取申赎日期
			/**end shashijie 2013-1-5 STORY 3328 */
			
			tempMap = new HashMap();
			
			buff = new StringBuffer(1000);
			buff.append("select ref.*,t.fsecuritycode,t.fportcode,t.freplaceamount,")
				.append("r.faftertaxratio,r.friprice,r.fexrightdate as ffexrightdate,")
				
				/**shashijie 2013-1-5 STORY 3328  获取ETF基础参数设置*/
				.append("Mk.FPrice,ra.FBaseRate,ra.FPortRate,Se.*,p.* From ")
				/**end shashijie 2013-1-5 STORY */
				
				.append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" ref join (select fnum,fsecuritycode,fportcode,freplaceamount From ")
				.append(pub.yssGetTableName("tb_etf_tradestldtl"))
				.append(" where fportcode in (").append(this.operSql.sqlCodes(this.portCodes))
				.append(") and fbuydate between ").append(dbl.sqlDate(buyDate))
				.append(" and ").append(dbl.sqlDate(dDate))				
				.append(") t on ref.fnum = t.fnum")
				.append(" left join(select fsecuritycode,fexrightdate,faftertaxratio,friprice from ")
				.append(pub.yssGetTableName("tb_data_rightsissue"))
				.append(" where fcheckstate = 1 ) r ")
				.append(" on r.fsecuritycode = t.fsecuritycode and ref.fexrightdate = r.fexrightdate")
				.append(" left join (select FTradeCury, fsecuritycode from ")
				.append(pub.yssGetTableName("tb_para_security"))
				.append(" where FCheckState = 1) se on t.fsecuritycode = se.fsecuritycode")				
				.append(" left join(Select FPortCode,FSecurityCode,FPrice from ")
				.append(pub.yssGetTableName("tb_data_pretvalmktprice"))
				.append(" Where Fcheckstate = 1 And Fvaldate = ").append(dbl.sqlDate(dDate))
				.append(" And Fportcode In (").append(this.operSql.sqlCodes(this.portCodes))
				.append(")) Mk On t.Fsecuritycode = Mk.Fsecuritycode And t.Fportcode = Mk.Fportcode")
				.append(" left join (select FBaseRate, FPortRate, FCuryCode, FPortCode from ")
				.append(pub.yssGetTableName("tb_data_pretvalrate"))
				.append(" where FCheckState = 1 ")
				.append(" And Fportcode In (").append(this.operSql.sqlCodes(this.portCodes))
				.append(") And Fvaldate = ").append(dbl.sqlDate(dDate))
				.append(") ra on t.fportcode = ra.fportcode and se.FTradeCury = ra.FCuryCode")
				
				/**shashijie 2013-1-5 STORY 3328  获取ETF基础参数设置*/
				.append(" Join (Select * From "+pub.yssGetTableName("Tb_ETF_Param")+
						" ) p On p.fportcode = t.Fportcode ")
				/**end shashijie 2013-1-5 STORY 3328 */
				
				.append(" where ref.fexrightdate < ").append(dbl.sqlDate(dDate));
			
			rs = dbl.queryByPreparedStatement(buff.toString());
						
			buff.delete(0, buff.length());
			
			buff.append("delete from ").append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" where fnum = ? and frefnum = ? ");
			pst = dbl.getPreparedStatement(buff.toString());
			buff.delete(0, buff.length());			
				
			while(rs.next()){
				
				if(null == rs.getDate("ffexrightdate")){
					continue;//有配股权益才做更新
				}
				
				pst.setString(1, rs.getString("fnum"));
				pst.setString(2, rs.getString("frefnum"));
				pst.execute();
				
				String sKey = rs.getString("fnum") + rs.getDate("ffexrightdate");
				if(tempMap.containsKey(sKey)){//同一笔权益更新一次就可以了，防止产生重复数据
					continue;
				}
				
				etfTradeSettleDetail = new ETFTradeSettleDetailBean();
				tradeSettleDelRef = new ETFTradeSettleDetailRefBean();
				
				tradeSettleDelRef.setNum(rs.getString("FNum"));//申请编号
				//关联编号：更新为：权益除权日期 + 当前业务日期
				tradeSettleDelRef.setRefNum(
						YssFun.formatDate(rs.getDate("FexrightDate"), "yyyyMMdd")
										+ YssFun.formatDate(dDate, "yyyyMMdd"));
				tradeSettleDelRef.setExRightDate(rs.getDate("FexrightDate"));//除权日期
				
				/**shashijie 2013-1-5 STORY 3328  修改:根据基础参数来源获取汇率*/
				double rate = getRightRate(rs.getDate("FexrightDate"),rs.getString("Ftradecury"),
						rs.getString("FbaseRateSrcBPCode"), rs.getString("FbaseRateBPCode"),
						rs.getString("FportRateSrcBPCode"), rs.getString("FportRateBPCode"),
						rs.getString("Fportcode"));
				/**end shashijie 2013-1-5 STORY 3328 */
				
				tradeSettleDelRef.setSumAmount(rs.getDouble("FSumAmount"));//权益总数量
				tradeSettleDelRef.setRealAmount(rs.getDouble("FRealAmount"));// 权益实际数量				
				
				tradeSettleDelRef.setInterest(rs.getDouble("FInterest"));//分红（原币）
				tradeSettleDelRef.setBbinterest(rs.getDouble("FBbInterest"));//分红（本位币）
				
				//如果（当日收盘价－配股价）<＝0，当日权证价值＝0
				double dSub = YssD.sub(rs.getDouble("FPrice"), rs.getDouble("friprice")) > 0 ? 
										YssD.sub(rs.getDouble("FPrice"), rs.getDouble("friprice")) : 0.0;
				tradeSettleDelRef.setWarrantCost(YssD.round(YssD.mul(rs.getDouble("FReplaceAmount"),
											rs.getDouble("faftertaxratio"), dSub), 2));//权证价值（原币）
				
				/**shashijie 2013-1-5 STORY 3328  修改:根据基础参数来源获取汇率*/
				double dPortPrice = YssD.round(YssD.mul(rs.getDouble("FPrice"),rate),2);
				double dPortRIPrice = YssD.round(YssD.mul(rs.getDouble("friprice"),rate),2);
				/**end shashijie 2013-1-5 STORY 3328 */
				
				double dPortSub = YssD.sub(dPortPrice,dPortRIPrice) > 0 ? 
											YssD.sub(dPortPrice, dPortRIPrice) : 0.0;
				tradeSettleDelRef.setBbwarrantCost(YssD.round(YssD.mul(rs.getDouble("FReplaceAmount"),
						rs.getDouble("faftertaxratio"), dPortSub),2));//权证价值（本位币）
				
				//数据方向
				tradeSettleDelRef.setDataDirection(rs.getString("fdatadirection"));
				//清算标识
				tradeSettleDelRef.setSettleMark("N");
				//数据标识
				tradeSettleDelRef.setDataMark("0");
				
				etfTradeSettleDetail.getAlTradeSettleDelRef().add(tradeSettleDelRef);
				tradeSettleDetail.add(etfTradeSettleDetail);//把明细数据存入集合中
				
				tempMap.put(sKey, tradeSettleDelRef);				
			}
			
			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置为自动提交事物
			bTrans = false;
			
		}catch (Exception e) {
			throw new YssException("计算台帐权证价值出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn,bTrans);
			dbl.closeStatementFinal(pst);
		}	
	}
	
	/**
	 * 强制补票
	 * @param dDate
	 * @throws YssException
	 */
	private void doMustMakeUp(Date dDate) throws YssException{
		StringBuffer buff = null;//做拼接SQL语句
		ResultSet rs = null;//声明结果集
		ETFTradeSettleDetailBean etfTradeSettleDetail = null;//交易明细
		ETFTradeSettleDetailRefBean tradeSettleDelRef = null;//交易明细关联
		ETFTradeSettleDetailRefBean tradeSettleDelRefHz = null;
		HashMap hzMap = new HashMap();
		EachExchangeHolidays holiday = null;//节假日代码类
		String strKey = "";
		
		try{
			holiday = new EachExchangeHolidays();//实例化
			holiday.setYssPub(pub);//设置pub
			
			/**shashijie 2013-1-5 STORY 3328 使用新获取工作日方法,考虑境内境外*/
			Date buyDate = getWorkDayMakeBack(dDate,
					0,(String)paramSet.getHoildaysRela().get("lastestdealdaynum"),
					0,(String)paramSet.getHoildaysRela().get("lastestdealdaynum2"));
			//测试
			//Date buyDate2 = getWorkDayByWhere(paramSet.getsCrossHolidayCode(), dDate, 0);
			if(YssFun.dateDiff(dDate,buyDate) != 0){//如果是境外节假日，不处理强制补票
				return;
			}
			//由于恒指ETF申赎开放日为深交所和港交所的共同交易日，且强制处理以港交所交易日进行推算（T+2）
			//因此此处可以通过境外节假日（港交所）倒退出申赎日期
			//后续如果产品方案有变更，如申赎开放日仅参照深交所交易日的话，此处再做相应调整
			buyDate = getWorkDayMakeBack(dDate,
					paramSet.getLastestDealDayNum()*-1,(String)paramSet.getHoildaysRela().get("lastestdealdaynum"),
					paramSet.getLastestDealDayNum2()*-1,(String)paramSet.getHoildaysRela().get("lastestdealdaynum2"));
			//测试
			//buyDate2 = getWorkDayByWhere(paramSet.getsCrossHolidayCode(), dDate, -2);//获取申赎日期
			/**end shashijie 2013-1-5 STORY 3328 */
			
			Date dSGReturnDate = getSGReplaceOver(dDate);//推算申购退款日期
			Date dSHReturnDate = getSHReplaceOver(dDate);//推算赎回退款日期
			
			if(tradeSettleDetail.size()!=0){//如果集合不为空时，要先清空
				tradeSettleDetail.clear();
			}
			
			buff = new StringBuffer(1000);			
			buff.append("select a.*,")
				.append(dbl.sqlIsNull("b.fsummakeupamount","0")).append(" as fsummakeupamount,")
				.append(dbl.sqlIsNull("b.fsumrealamount","0")).append(" as fsumrealamount,")
				.append(dbl.sqlIsNull("b.fsumocpreplacecash","0")).append(" as fsumocpreplacecash,")
				.append(dbl.sqlIsNull("b.fsumhcpreplacecash","0")).append(" as fsumhcpreplacecash,")
				.append(dbl.sqlIsNull("b.fsumsumamount","0")).append(" as fsumsumamount,")
				.append(dbl.sqlIsNull("b.fsuminterest","0")).append(" as fsuminterest,")
				.append(dbl.sqlIsNull("b.fsumwarrantcost","0")).append(" as fsumwarrantcost,")
				.append(dbl.sqlIsNull("b.fsumbbinterest","0")).append(" as fsumbbinterest,")
				.append(dbl.sqlIsNull("b.fsumbbwarrantcost","0")).append(" as fsumbbwarrantcost,")
				
				/**shashijie 2013-1-5 STORY 3328  获取ETF基础参数设置*/
				.append("Mk.FPrice,ra.FBaseRate, ra.FPortRate,p.* from ")
				/**end shashijie 2013-1-5 STORY */
				
				.append("(select t.*, se.FTradeCury from ")
				.append(pub.yssGetTableName("tb_etf_tradestldtl"))
				.append(" t left join (select FTradeCury, fsecuritycode from ")
				.append(pub.yssGetTableName("tb_para_security"))
				.append(" where FCheckState = 1) se on t.fsecuritycode = se.fsecuritycode")
				.append(" where t.fportcode in (").append(this.operSql.sqlCodes(this.portCodes))
				.append(") and t.fbuydate = ").append(dbl.sqlDate(buyDate))
				.append(") a left join (select ref.fnum,sum(")
				.append(dbl.sqlIsNull("ref.fmakeupamount","0")).append(") as fsummakeupamount,sum(")
				.append(dbl.sqlIsNull("ref.frealamount","0")).append(") as fsumrealamount, sum(")
				.append(dbl.sqlIsNull("ref.focpreplacecash","0")).append(") as fsumocpreplacecash, sum(")
				.append(dbl.sqlIsNull("ref.fhcpreplacecash","0")).append(") as fsumhcpreplacecash, sum(")
				.append(dbl.sqlIsNull("ref.fsumamount","0")).append(") as fsumsumamount,sum(")
				.append(dbl.sqlIsNull("ref.finterest","0")).append(") as fsuminterest, sum(")
				.append(dbl.sqlIsNull("ref.fwarrantcost","0")).append(") as fsumwarrantcost, sum(")
				.append(dbl.sqlIsNull("ref.fbbinterest","0")).append(") as fsumbbinterest, sum(")
				.append(dbl.sqlIsNull("ref.fbbwarrantcost","0")).append(") as fsumbbwarrantcost from ")
				.append(pub.yssGetTableName("tb_etf_tradstldtlref"))
				.append(" ref where ref.frefnum <> '99' group by ref.fnum")
				.append(") b on a.fnum = b.fnum")
				.append(" left join(Select FPortCode,FSecurityCode,FPrice from ")
				.append(pub.yssGetTableName("tb_data_pretvalmktprice"))
				.append(" Where Fcheckstate = 1 And Fvaldate = ").append(dbl.sqlDate(dDate))
				.append(" And Fportcode In (").append(this.operSql.sqlCodes(this.portCodes))
				.append(")) Mk On a.Fsecuritycode = Mk.Fsecuritycode And a.Fportcode = Mk.Fportcode")
				.append(" left join (select FBaseRate, FPortRate, FCuryCode, FPortCode from ")
				.append(pub.yssGetTableName("tb_data_pretvalrate"))
				.append(" where FCheckState = 1 ")
				.append(" And Fportcode In (").append(this.operSql.sqlCodes(this.portCodes))
				.append(") And Fvaldate = ").append(dbl.sqlDate(dDate))
				.append(") ra on a.fportcode = ra.fportcode and a.FTradeCury = ra.FCuryCode")
				
				/**shashijie 2013-1-5 STORY 3328  获取ETF基础参数设置*/
				.append(" Join (Select * From "+pub.yssGetTableName("Tb_ETF_Param")+
						" ) p On p.fportcode = a.Fportcode ")
				/**end shashijie 2013-1-5 STORY 3328 */
				
				.append(" order by a.fstockholdercode,a.ftradenum");
				
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			
			while(rs.next()){
				
				double dMustMakeUpAmount = YssD.sub(YssD.add(rs.getDouble("FReplaceAmount"),
															rs.getDouble("fsumrealamount")) ,
													rs.getDouble("fsummakeupamount"));
				if(dMustMakeUpAmount == 0){
					//如果前几次补票已经补完，就不需要处理强制补票
					continue;
				}

				etfTradeSettleDetail = new ETFTradeSettleDetailBean();
				tradeSettleDelRef = new ETFTradeSettleDetailRefBean();		
				
				tradeSettleDelRef.setNum(rs.getString("FNum"));//明细关联数据的申请编号
				tradeSettleDelRef.setRefNum("99");//关联编号：主要是区分：1-第一次补票，2-第二次补票...99-强制处理
				tradeSettleDelRef.setMakeUpDate(dDate);//补票日期
				
				tradeSettleDelRef.setMakeUpAmount(dMustMakeUpAmount);//补票数量
				
				/**shashijie 2013-1-5 STORY 3328  修改:根据基础参数来源获取汇率*/
				double rate = getRightRate(dDate,rs.getString("Ftradecury"),
						rs.getString("FbaseRateSrcBPCode"), rs.getString("FbaseRateBPCode"),
						rs.getString("FportRateSrcBPCode"), rs.getString("FportRateBPCode"),
						rs.getString("Fportcode"));
				
				tradeSettleDelRef.setUnitCost(YssD.round(//补票单位成本，取当日收盘价
						YssD.mul(rs.getDouble("FPrice"),
									rate),2));
				/**end shashijie 2013-1-5 STORY 3328 */
				
				//总成本原币 = 补票数量 * 补票单位成本
				tradeSettleDelRef.setoMakeUpCost(YssD.round(
						YssD.mul(tradeSettleDelRef.getMakeUpAmount(),
								rs.getDouble("FPrice")), 2));
				//本币总成本 = 原币总成本 * 基础汇率/组合汇率
				tradeSettleDelRef.sethMakeUpCost(YssD.round(YssD.mul(
						tradeSettleDelRef.getoMakeUpCost(),tradeSettleDelRef.getExchangeRate()),2));
				if(rs.getString("FBs").equalsIgnoreCase("B")){//申购

					//应付替代款（原币） = round((替代款金额原币 - 分红金额原币 - 权证价值原币) * 补票数量/(申赎数量 + 权益总数量) - 补票数量*单位成本原币,2)
					tradeSettleDelRef.setOpReplaceCash(YssD.round(YssD.sub(YssD.div(
											YssD.mul(YssD.sub(rs.getDouble("FOReplaceCash"),
														rs.getDouble("fsuminterest"),
														rs.getDouble("fsumwarrantcost")),
													tradeSettleDelRef.getMakeUpAmount()),
										YssD.add(rs.getDouble("FReplaceAmount"),rs.getDouble("fsumsumamount"))),
										YssD.mul(rs.getDouble("FPrice"), tradeSettleDelRef.getMakeUpAmount())),2));
					//应付替代款（本币） = round((替代款金额本币 - 分红金额本币 - 权证价值本币) * 补票数量/(申赎数量 + 权益总数量) - 补票数量*单位成本本币,2)
					tradeSettleDelRef.setHpReplaceCash(YssD.round(YssD.sub(YssD.div(
											YssD.mul(YssD.sub(rs.getDouble("FHReplaceCash"),
														rs.getDouble("fsumbbinterest"),
														rs.getDouble("fsumbbwarrantcost")),
													tradeSettleDelRef.getMakeUpAmount()),
										YssD.add(rs.getDouble("FReplaceAmount"),rs.getDouble("fsumsumamount"))),
										YssD.mul(tradeSettleDelRef.getUnitCost(), tradeSettleDelRef.getMakeUpAmount())),2));
					//数据方向
					tradeSettleDelRef.setDataDirection("1");	
					
					//退款日期（申赎退款日期规则相同）
					tradeSettleDelRef.setRefundDate(dSGReturnDate);
				}else {//赎回
					//应付替代款（原币） = round(补票数量*单位成本原币 - (替代款金额原币 - 分红金额原币 - 权证价值原币) * 补票数量/(申赎数量 + 权益总数量) ,2)
					tradeSettleDelRef.setOpReplaceCash(YssD.round(YssD.sub(
							YssD.mul(rs.getDouble("FPrice"), tradeSettleDelRef.getMakeUpAmount()),
									YssD.div(
											YssD.mul(YssD.sub(rs.getDouble("FOReplaceCash"),
														rs.getDouble("fsuminterest"),
														rs.getDouble("fsumwarrantcost")),
													tradeSettleDelRef.getMakeUpAmount()),
										YssD.add(rs.getDouble("FReplaceAmount"),rs.getDouble("fsumsumamount")))
										),2));
					//应付替代款（本币） = round(补票数量*单位成本本币 - (替代款金额本币 - 分红金额本币 - 权证价值本币) * 补票数量/(申赎数量 + 权益总数量),2)
					tradeSettleDelRef.setHpReplaceCash(YssD.round(YssD.sub(
									YssD.mul(tradeSettleDelRef.getUnitCost(), tradeSettleDelRef.getMakeUpAmount()),
										YssD.div(
											YssD.mul(YssD.sub(rs.getDouble("FHReplaceCash"),
														rs.getDouble("fsumbbinterest"),
														rs.getDouble("fsumbbwarrantcost")),
													tradeSettleDelRef.getMakeUpAmount()),
										YssD.add(rs.getDouble("FReplaceAmount"),rs.getDouble("fsumsumamount")))
										),2));

					//数据方向
					tradeSettleDelRef.setDataDirection("-1");
					

					//退款日期（申赎退款日期规则相同）
					tradeSettleDelRef.setRefundDate(dSHReturnDate);
				}
				
				if(rs.getString("fstockholdercode").trim().length() == 0){
					//如果是汇总项，不用设置退款日期，重置退款日期
					tradeSettleDelRef.setRefundDate(null);
				}
				
				//可退替代款（原币）= 可退替代款总金额（原币）- 各次补票可退替代款累计值（原币）
				double ocReplaceCash = YssD.sub(rs.getDouble("FOCReplaceCash"), 
													rs.getDouble("fsumocpreplacecash"));
				tradeSettleDelRef.setOcReplaceCash(ocReplaceCash);
				//可退替代款（本币）= 可退替代款总金额（本位币）- 各次补票可退替代款累计值（本位币）
				double hcReplaceCash = YssD.sub(rs.getDouble("FHCReplaceCash"), 
													rs.getDouble("fsumhcpreplacecash"));
				tradeSettleDelRef.setHcReplaceCash(hcReplaceCash);
				
				//可退替代款余额（原币）
				tradeSettleDelRef.setOCanRepCash(0);
				
				//可退替代款余额（本位币）
				tradeSettleDelRef.setOCanRepCash(0);
				
				/**shashijie 2013-1-5 STORY 3328  修改:根据基础参数来源获取汇率*/
				//估值汇率
				tradeSettleDelRef.setExchangeRate(rate);
				/**end shashijie 2013-1-5 STORY 3328 */
						
				//剩余数量 
				tradeSettleDelRef.setRemaindAmount(0);

				//清算标识
				tradeSettleDelRef.setSettleMark("N");
				//数据标识
				tradeSettleDelRef.setDataMark("1");

				//以下按股票汇总投资者补票数据
				strKey = rs.getString("FPortCode") + "\t" +	
								rs.getDate("FBuyDate") + "\t" + 
								rs.getString("FBS") + "\t" + 
								rs.getString("FSecurityCode");//组合代码+申赎日期+交易类型+证券代码
				if(hzMap.containsKey(strKey)){
					tradeSettleDelRefHz = (ETFTradeSettleDetailRefBean) hzMap.get(strKey);
					
					//汇总项与明细项倒减计算补票数量
					tradeSettleDelRefHz.setMakeUpAmount(YssD.sub(
														tradeSettleDelRefHz.getMakeUpAmount(),
																tradeSettleDelRef.getMakeUpAmount()));
					
					if(tradeSettleDelRefHz.getMakeUpAmount() == 0){//如果倒减后补票数量为0，说明当前记录为最后一笔投资者明细数据
						//最后一个投资者倒挤
						tradeSettleDelRef.setOpReplaceCash(tradeSettleDelRefHz.getOpReplaceCash());
						tradeSettleDelRef.setHpReplaceCash(tradeSettleDelRefHz.getHpReplaceCash());
					}
					
					//汇总项与明细项倒减计算倒减应付替代款（原币）
					tradeSettleDelRefHz.setOpReplaceCash(YssD.sub(
														tradeSettleDelRefHz.getOpReplaceCash(),
															tradeSettleDelRef.getOpReplaceCash()));
					//汇总项与明细项倒减计算倒减应付替代款（本位币）
					tradeSettleDelRefHz.setHpReplaceCash(YssD.sub(
														tradeSettleDelRefHz.getHpReplaceCash(),
															tradeSettleDelRef.getHpReplaceCash()));

					hzMap.put(strKey, tradeSettleDelRefHz);
					
				}else{
					tradeSettleDelRefHz = (ETFTradeSettleDetailRefBean) tradeSettleDelRef.clone();
/*					if(rs.getString("fstockholdercode").trim().length() > 0){//如果是明细数据（备注：如果是汇总数据，字段值为空格）
						//补票数量
						tradeSettleDelRefHz.setMakeUpAmount(-1 * tradeSettleDelRef.getMakeUpAmount());
						//应付替代款（原币）
						tradeSettleDelRefHz.setOpReplaceCash(-1 * tradeSettleDelRef.getOpReplaceCash());
						//应付替代款（本位币）
						tradeSettleDelRefHz.setHpReplaceCash(-1 * tradeSettleDelRef.getHpReplaceCash());
					}*/
					hzMap.put(strKey, tradeSettleDelRefHz);
				}
				
				etfTradeSettleDetail.getAlTradeSettleDelRef().add(tradeSettleDelRef);//保存明细关联数据
				tradeSettleDetail.add(etfTradeSettleDetail);//把明细数据存入集合中
			}
						
			hzMap.clear();
			
		}catch (Exception e) {
			throw new YssException("处理强制补票出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**shashijie 2013-1-5 STORY 3328 获取工作日,向后推出int BeginSupply个工作日(考虑国内国外)*/
	private Date getWorkDayMakeBack(Date dDate, int num1, String holidayCode1,
			int num2, String holidayCode2) throws YssException {
		Date makeDate = dDate;
		String sDate = paramSetAdmin.getWorkDay(dDate, holidayCode1, num1, holidayCode2, num2);
		makeDate = YssFun.toDate(sDate);
		return makeDate;
	}

	/**获取赎回应付替代结转日期
	 * @param dDate
	 * @throws YssException
	 * @author shashijie ,2011-11-17 , STORY 1789
	 */
	private Date getSHReplaceOver(Date dDate) throws YssException {
		
		/**shashijie 2013-1-5 STORY 3328 使用新获取工作日方法,考虑境内境外*/
		Date SGReplaceOverNew = getWorkDayMakeBack(dDate,
				paramSet.getISGDealReplace(),(String)paramSet.getHoildaysRela().get("sgdealreplace"),
				paramSet.getiSGDealReplace2(),(String)paramSet.getHoildaysRela().get("sgdealreplace2"));
		
		/*int i = (int) YssD.sub(paramSet.getISHDealReplace(), 2);
		//先向后推算出2个港交所工作日,境外
		Date SHReplaceOver = getWorkDayByWhere(paramSet.getsCrossHolidayCode(), dDate, 2);
		
		if(YssFun.dateDiff(getWorkDayByWhere(paramSet.getSHolidayCode(), SHReplaceOver, 0),
					SHReplaceOver) != 0){
			//如果T+2（香港交易日）当天为深圳非交易日，则退款日为T+2（香港交易日）+3（深圳交易日）
			//否则退款日为T+2（香港交易日）+2（深圳交易日）
			//panjunfang modify 20120420
			i = (int) YssD.add(i, 1);
		}
		//再在这个基础上向后推算出2个深交所工作日,境内
		SHReplaceOver = getWorkDayByWhere(paramSet.getSHolidayCode(), SHReplaceOver, i);*///赎回应付替代结转日期

		/**end shashijie 2013-1-5 STORY 3328 */
		
		return SGReplaceOverNew;
	}

	/**获取申购应付替代结转日期
	 * @param dDate
	 * @author shashijie ,2011-11-17 , STORY 1789
	 */
	private Date getSGReplaceOver(Date dDate) throws YssException {
		
		/**shashijie 2013-1-5 STORY 3328 使用新获取工作日方法,考虑境内境外*/
		Date SGReplaceOverNew = getWorkDayMakeBack(dDate,
				paramSet.getISGDealReplace(),(String)paramSet.getHoildaysRela().get("sgdealreplace"),
				paramSet.getiSGDealReplace2(),(String)paramSet.getHoildaysRela().get("sgdealreplace2"));
		
		/*int i = (int) YssD.sub(paramSet.getISGDealReplace(), 2);
		//先向后推算出2个港交所工作日,境外
		Date SGReplaceOver = getWorkDayByWhere(paramSet.getsCrossHolidayCode(), dDate, 2);
		
		if(YssFun.dateDiff(getWorkDayByWhere(paramSet.getSHolidayCode(), SGReplaceOver, 0),
				SGReplaceOver) != 0){
			//如果T+2（香港交易日）当天为深圳非交易日，则退款日为T+2（香港交易日）+3（深圳交易日）
			//否则退款日为T+2（香港交易日）+2（深圳交易日）
			//panjunfang modify 20120420
			i = (int) YssD.add(i, 1);
		}
		//再在这个基础上向后推算出2个深交所工作日,境内
		SGReplaceOver = getWorkDayByWhere(paramSet.getSHolidayCode(), SGReplaceOver, i);*///赎回应付替代结转日期
		
		/**end shashijie 2013-1-5 STORY 3328 */
		
		return SGReplaceOverNew;
	}

	/**应付替代款（本币） = （替代款金额本币 - 总派息本币—权证价值本币）* 补票数量/（申赎数量 +送股总数量） - 补票数量* 单位成本本币
	 * @param hReplaceCash 替代款金额本币
	 * @param bbinterest 总派息本币
	 * @param bbwarrantCost 权证价值本币
	 * @param makeUpAmount 补票数量
	 * @param replaceAmount 申赎数量
	 * @param sumAmount 送股总数量
	 * @param unitCost 单位成本本币
	 * @author shashijie ,2011-11-17 , STORY 1789
	 */
	private double getHpReplaceCash(double hReplaceCash, double bbinterest,
			double bbwarrantCost, double makeUpAmount, double replaceAmount,
			double sumAmount, double unitCost) {
		//补票应付替代款（本币）
		double value = YssD.round(YssD.sub(
			YssD.div(YssD.mul(YssD.sub(hReplaceCash, bbinterest, bbwarrantCost), 
					makeUpAmount), YssD.add(replaceAmount, sumAmount)), 
			YssD.mul(makeUpAmount,unitCost)), 2);
		return value;
	}
	

	/**补票可退替代款（本币）=申赎可退替代款本币*补票数量/(申赎数量+ 权益总数量)
	 * @param hcReplaceCash 可退替代款本币
	 * @param makeUpAmount 第N次补票数量
	 * @param replaceAmount 申赎数量
	 * @param sumAmount 权益总数量
	 * @return 可退替代款（本币）
	 * @author shashijie ,2011-11-17 , STORY 1789
	 * @modified 
	 */
	private double getHcReplaceCash(double hcReplaceCash, double makeUpAmount,
			double replaceAmount, double sumAmount) {
		//可退替代款（本币）=可退替代款本币*补票数量/(申赎数量+ 权益总数量)
		double hcReplaceCashValue = YssD.round(
			YssD.div(
					YssD.mul(hcReplaceCash,makeUpAmount),
					YssD.add(replaceAmount,sumAmount)
					),2);
		return hcReplaceCashValue;
	}

	/**处理当日申赎数据
	 * @author shashijie ,2011-11-28 , STORY 1789
	 */
	private String getdoTradestldtlSql(Date dDate) {
		//取数总体逻辑：申赎结果数据 left join 股票篮（取成份股）left join 行情、汇率
		String sqlString = 
			/**shashijie 2013-1-5 STORY 3328  获取ETF基础参数设置*/
			"Select Hb.*, h.*, Ra.Fbaserate,Ra.Fportrate,p.* From " + 
			/**end shashijie 2013-1-5 STORY 3328 */
			
			pub.yssGetTableName("Tb_ETF_JGInterface") + 
			" Hb Left Join (Select s.Fportcode As Portcode, s.Famount As Famount, s.Fpremiumscale, s.Ftotalmoney," +			
			" s.Fsecuritycode As Fstockcode, Mk.Fprice, Se.Ftradecury From " + pub.yssGetTableName("Tb_Etf_Stocklist")+
			" s Left Join (Select FPortCode,FSecurityCode,FPrice From " + pub.yssGetTableName("Tb_Data_Pretvalmktprice")+
			" Where Fcheckstate = 1 And Fvaldate = "+dbl.sqlDate(dDate) + 
			" And Fportcode In ("+operSql.sqlCodes(portCodes) +
			" )) Mk On s.Fsecuritycode = Mk.Fsecuritycode And s.Fportcode = Mk.Fportcode " +
			" Left Join (Select Ftradecury, Fsecuritycode From "+pub.yssGetTableName("Tb_Para_Security")+
			" Where Fcheckstate = 1) Se On s.Fsecuritycode = Se.Fsecuritycode " +
			" Where s.Fcheckstate = 1 And s.Fportcode In ("+operSql.sqlCodes(portCodes) + 
			") And s.Fdate = " + dbl.sqlDate(dDate) + 
			" And s.Freplacemark = '1') h On Hb.Fportcode = h.Portcode " + //只处理替代标识为1的成份股（2为必须现金替代）
			" Left Join (Select Fbaserate, Fportrate, Fcurycode, Fportcode From " + pub.yssGetTableName("Tb_Data_Pretvalrate") + 
			" Where Fcheckstate = 1 And Fvaldate = " + dbl.sqlDate(dDate) + 
			" And FPortcode In ("+operSql.sqlCodes(portCodes)+" )) Ra " +
			" On Hb.Fportcode = Ra.Fportcode And h.Ftradecury = Ra.Fcurycode " +
			
			/**shashijie 2013-1-5 STORY 3328  获取ETF基础参数设置*/
			" Join (Select * From "+pub.yssGetTableName("Tb_ETF_Param")+" ) p On p.fportcode = Hb.Fportcode "+
			/**end shashijie 2013-1-5 STORY 3328 */
			
			" Where Hb.Fcheckstate = 1 " +
			" And Hb.Fportcode In ("+operSql.sqlCodes(portCodes) + 
			" ) And Hb.Fbargaindate = "+dbl.sqlDate(dDate)+
			" And Hb.Fopertype = '2ndcode' Order By Hb.Fcontractnum";//取一级市场申赎数据记录（资金记录不处理）
		return sqlString;
	}
	
	/**生成临时台账数据,根据回报库,当先申赎日期与浏览日期显示同一天时就取此表信息显示
	 * @param dDate 操作日
	 * @author shashijie ,2011-11-28 , STORY 1789
	 */
	private void doETFTempStandingBook(Date dDate) throws YssException {
		//复原编号生成计数器
		if (maxNum!=0) {
			maxNum = 0;
		}
		if (!tempBookList.isEmpty()) {
			tempBookList.clear();
		}
		ResultSet rs = null;//声明结果集
		try {
			String sqlStr = getTempBookSql(dDate);
			rs = dbl.openResultSet(sqlStr);
			//获取表中最大编号
			String strNumDate = getStrNumDate(dDate,"Tb_ETF_TempStandingBook");
			Date fSGRefundDate = getSGReplaceOver(dDate);//申购退款日期
			Date fSHRefundDate = getSHReplaceOver(dDate);//赎回退款日期
			Date fRefundDate = null;//退款日期
			
			while (rs.next()) {
				if(rs.getString("FTradeTypeCode").equalsIgnoreCase("KS")){//申购
					fRefundDate = fSGRefundDate;
				}else{
					fRefundDate = fSHRefundDate;
				}
				doOperTempBook(rs,strNumDate,fRefundDate);
			}
			
		} catch (Exception e) {
			throw new YssException("生成临时台账数据出错！",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**根据回报数据生成临时台账表数据
	 * @param dDate
	 * @author shashijie ,2011-11-29 , STORY 1789
	 */
	private String getTempBookSql(Date dDate) {
		String sqlString = "Select Hb.*, h.*, Ra.*,c.Ftradeamount As Tradeamount,"+
		" c.Ftrademoney As Trademoney ,c.FTradeFee  From "+
		pub.yssGetTableName("Tb_ETF_Hbinterface")+" Hb "+
		" Left Join (Select s.Fportcode As Portcode, s.Famount As Famount, s.Fpremiumscale, s.Ftotalmoney," +
		
		/**shashijie 2013-1-5 STORY 3328  获取ETF基本信息参数*/
		" s.Fsecuritycode As Fstockcode, Mk.Fprice, Se.Ftradecury,p.* From "+pub.yssGetTableName("Tb_Etf_Stocklist")+
		/**end shashijie 2013-1-5 STORY 3328 */
		
		" s Join (Select * From "+pub.yssGetTableName("Tb_Etf_Param")+" Where Fcheckstate = 1 And Fportcode In ( "+
		operSql.sqlCodes(portCodes)+" )) p On s.Fportcode = p.Fportcode And s.Fsecuritycode <> p.Fcapitalcode " +
		" Left Join (Select * From "+pub.yssGetTableName("Tb_Data_Pretvalmktprice")+" Where Fcheckstate = 1 " +
		" And Fvaldate = "+dbl.sqlDate(dDate)+" And Fportcode In ("+operSql.sqlCodes(portCodes)+
		" )) Mk On s.Fsecuritycode = Mk.Fsecuritycode And s.Fportcode = Mk.Fportcode " +
		" Join (Select Ftradecury, Fsecuritycode From "+pub.yssGetTableName("Tb_Para_Security")+
		" Where Fcheckstate = 1 And Fcatcode = 'EQ') Se On s.Fsecuritycode = Se.Fsecuritycode " +
		"Where s.Fcheckstate = 1 And s.Fportcode In ("+operSql.sqlCodes(portCodes)+") And s.Fdate = " +
		dbl.sqlDate(dDate)+" And s.Freplacemark = '1') h On Hb.Fportcode = h.Portcode " +
		" Left Join (Select Fbaserate, Fportrate, Fcurycode, Fportcode, Fotbaserate1, Fotbaserate2, Fotbaserate3 "+
		" From "+pub.yssGetTableName("Tb_Data_Pretvalrate")+" Where Fcheckstate = 1 And Fvaldate = " +
		dbl.sqlDate(dDate)+" And FPortcode In ("+operSql.sqlCodes(portCodes)+" )) Ra " +
		" On Hb.Fportcode = Ra.Fportcode And h.Ftradecury = Ra.Fcurycode " +
		" Left Join ( Select Ftradeamount,Ftrademoney,FTradeFee,"+
		"FPortCode,FSecurityCode,FContractNum,FStockerCode,case when fbs = 'B' then 'KS' else 'KB' end as fbs"+
        " From "+pub.yssGetTableName("Tb_ETF_CJMXInterface")+" c1 Where c1.FBargainDate = "+
        dbl.sqlDate(dDate)+") c"+
        " On c.FPortCode = HB.FPortCode And c.FSecurityCode = h.FStockCode "+
        " And c.FContractNum = HB.FOTHERSEAT||'-'||HB.FContractNum And c.FStockerCode = HB.FOtherstockHolder And c.fbs = HB.FTRADETYPECODE"+
		" Where Hb.Fcheckstate = 1 " +
		" And Hb.Fportcode In ("+operSql.sqlCodes(portCodes)+" ) And Hb.Fbargaindate = "+dbl.sqlDate(dDate)+
		" And Hb.Fopertype = '2ndcode' Order By Hb.Fcontractnum";
		return sqlString;
	}

	/**获取表中最大编号
	 * @param dDate 操作日
	 * @param tableName 表名
	 * @author shashijie ,2011-11-28 , STORY 1789
	 */
	private String getStrNumDate(Date dDate ,String tableName) throws YssException {
		String strNumDate = YssFun.formatDatetime(dDate).substring(0, 8);
		strNumDate = strNumDate
				+ dbFun.getNextInnerCode(pub.yssGetTableName(tableName), 
						dbl.sqlRight("FNUM", 6), "000000", " where FNum like 'T"
						+ strNumDate + "%'", 1);
		strNumDate = "T" + strNumDate;
		return strNumDate;
	}

	/**根据回报数据生成临时台账表数据
	 * @author shashijie ,2011-11-28 , STORY 1789
	 */
	private void doOperTempBook(ResultSet rs,String strNumDate,Date fRefundDate) throws Exception {
		maxNum += 1;
		//获取可增加的编号(最大编号+1)
		String fNum = getFnum(strNumDate,maxNum);
		//---------------------------------申赎----------------------------//
		TempBook book = new TempBook();
		book.setFNum(fNum);//申请编号
		book.setFBuyDate(rs.getDate("FBargainDate"));//申购日期
		book.setFBs(rs.getString("FTradeTypeCode").equals("KS") ? "B" : "S");//交易类型(KS申购)
		book.setFPortCode(rs.getString("FPortCode"));//组合代码
		if(null == rs.getString("Fstockcode")){
			throw new YssException("当日股票篮文件未读，请先读入！");
		}
		book.setFSecurityCode(rs.getString("Fstockcode"));//证券代码
		book.setFStockHolderCode(rs.getString("FOtherStockholder"));//股东代码
		book.setFBrokerCode(rs.getString("FBrokerCode"));//参与券商
		book.setFSeatCode(rs.getString("FSeatNum"));//席位号
		
		double braket = YssD.div(rs.getDouble("FTradeAmount"),paramSet.getNormScale());//篮子数
		
		//替代数量 = (申赎份额/参数设置中基准比例)* 股票篮文件中证券数量
		book.setFMakeUpAmount(YssD.mul(braket,rs.getDouble("FAmount")));//申赎补票数量
		
		/**shashijie 2013-1-5 STORY 3328  修改:根据基础参数来源获取汇率*/
		double rate = getRightRate(rs.getDate("FBargainDate"),rs.getString("Ftradecury"),
				rs.getString("FbaseRateSrcBPCode"), rs.getString("FbaseRateBPCode"),
				rs.getString("FportRateSrcBPCode"), rs.getString("FportRateBPCode"),
				rs.getString("Fportcode"));
		
		//单位成本 = 收盘价*基础汇率/组合汇率
		book.setFUnitCost(YssD.round(YssD.mul(rs.getDouble("FPrice"),
						rate),2));//单位成本
		
		book.setFExchangeRate(rate);//申述数据对应的汇率
		/**end shashijie 2013-1-5 STORY 3328 */
		
		double DataDirection = 1;//数据方向
		double fReplaceCash = 0;//替代金额(应退赎回款)
		double fCanReplaceCash = 0;//可退替代款(可退替代款本币)
		
		
		if(rs.getString("FTradeTypeCode").equalsIgnoreCase("KS")){//申购
			//替代金额  =  股票篮中替代金额   * 篮子数 
			fReplaceCash = YssD.round(YssD.mul(rs.getDouble("FTotalMoney"),braket),2);
			
			//可退替代款 = 替代金额本币 - 替代数量 * 单位成本 
			fCanReplaceCash = YssD.round(YssD.sub(fReplaceCash,
					YssD.mul(book.getFMakeUpAmount(),book.getFUnitCost())),2);
			//退款日期
			//fRefundDate = getSGReplaceOver(rs.getDate("FBargainDate"));
			DataDirection = 1;//数据方向
		}else{
			//应退赎回款（本币） = round(round(股票篮中该股票的数量*单位成本，2）*赎回的篮子数,2)
			fReplaceCash = YssD.round(YssD.mul(YssD.round(YssD.mul(
					rs.getDouble("FAmount"),book.getFUnitCost()),2),
					braket),2);
			//可退替代款本币 = 替代金额本币 - 单位成本*替代数量
			fCanReplaceCash = 0;
			//退款日期
			//fRefundDate = getSHReplaceOver(rs.getDate("FBargainDate"));
			DataDirection = -1;//数据方向
		}
		//剩余数量
		double fRemaindAmount = YssD.sub(book.getFMakeUpAmount(), rs.getDouble("TradeAmount"));
		
		//存入应退,可退,日期等金额
		setTempBook(book,fReplaceCash,fCanReplaceCash,fRefundDate,fRemaindAmount,DataDirection);
		
		//排序编号
		String fOrderCode = 
			getfOrderCode(book.getFSecurityCode(),book.getFStockHolderCode(),rs.getString("FContractNum"));
		book.setFOrderCode(fOrderCode);
		book.setFGradeType1(book.getFSecurityCode());//分级类型1(证券)
		book.setFGradeType2(book.getFStockHolderCode());//分级类型2(股东)
		//分级类型3：交易编号 +　席位号 + 合同序号
		book.setFGradeType3(rs.getString("FTRADENUM") + "-" + rs.getString("FOTHERSEAT") + "-" + rs.getString("FContractNum"));
		//------------------------------------------申赎end------------------------------------------//
		
		//----------------------------------------第一次补票数据-------------------------------------//
		double fMakeUpAmount1 = 0;//第一次补票的数量
		double fMakeUpUnitCost1 = 0;//第一次补票的单位成本
		double fOMakeUpCost1 = 0;//第一次补票的总成本（原币）
		double fHMakeUpCost1 = 0;//第一次补票的总成本（本币）
		double fMakeUpRepCash1 = 0;//第一次补票的应付替代款
		double fCanMkUpRepCash1 = 0;//第一次补票的可退替代款
		/**shashijie 2013-1-5 STORY 3328  修改:根据基础参数来源获取汇率*/
		double fExRate1 = rate;//第一次补票汇率
		/**end shashijie 2013-1-5 STORY 3328 */
		double fTradeUnitCost1 = 0;//第一次补票的成交单价
		double fFeeUnitCost1 = 0;//第一次补票的费用单价
		double fSumReturn = 0;//应退合计
		
		//fExRate1 = YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate"));//第一次补票汇率
		//补票数量
		//如果券商回报数据中补票数量超出替代数量，则将替代数量赋值给补票数量
		fMakeUpAmount1 = rs.getDouble("tradeAmount") < book.getFMakeUpAmount() ? 
									rs.getDouble("tradeAmount") : book.getFMakeUpAmount();

		if(rs.getString("FTradeTypeCode").equalsIgnoreCase("KS")){//申购
			/**shashijie 2013-1-5 STORY 3328  修改:根据基础参数来源获取汇率*/
			//单位成本本币 = 券商明细表表中 【（成交金额+费用）*基础汇率/组合汇率）】/交易数量
			fMakeUpUnitCost1 = YssD.round(
				YssD.div(
					YssD.round(YssD.mul(
							YssD.add(rs.getDouble("tradeMoney"),rs.getDouble("FTradeFee")),
								rate),2),
									rs.getDouble("tradeAmount")),2);
			/**end shashijie 2013-1-5 STORY 3328 */
			
			//总成本原币 = 单位成本本币 * 补票数量
			fOMakeUpCost1 = YssD.mul(fMakeUpUnitCost1,fMakeUpAmount1);
			
			//应付替代款（本币） = 替代款金额本币 * 补票数量/申赎数量 - 补票数量*单位成本本币
			fMakeUpRepCash1 = getHpReplaceCash(fReplaceCash, 0, 0, fMakeUpAmount1, 
					book.getFMakeUpAmount(), 0, fMakeUpUnitCost1);
			//数据方向
			DataDirection = 1;
		}else{
			/**shashijie 2013-1-5 STORY 3328  修改:根据基础参数来源获取汇率*/
			//单位成本本币 = 券商明细表表中 【（成交金额-费用）*基础汇率/组合汇率）】/交易数量
			fMakeUpUnitCost1 = YssD.round(
				YssD.div(
					YssD.round(YssD.mul(
							YssD.sub(rs.getDouble("tradeMoney"),rs.getDouble("FTradeFee")),
							rate),2),
								rs.getDouble("tradeAmount")),2);
			/**end shashijie 2013-1-5 STORY 3328 */
			
			//原币总成本原币 = 单位成本本币 * 补票数量
			fOMakeUpCost1 = YssD.mul(fMakeUpUnitCost1,fMakeUpAmount1);
			
			//应付替代款（本币） = 补票数量*单位成本本币 - 可退替代款本币 * 补票数量/申赎数量 
			//算法与申购差不多只是减数被减数颠倒,所以这里乘以-1
			fMakeUpRepCash1 = YssD.mul(getHpReplaceCash(fReplaceCash, 0, 0, fMakeUpAmount1, 
					book.getFMakeUpAmount(), 0, fMakeUpUnitCost1),-1);

			//数据方向
			DataDirection = -1;
		}
		
		//可退替代款（本币）=可退替代款本币 * 补票数量/申赎数量 
		fCanMkUpRepCash1 = getHcReplaceCash(fCanReplaceCash,fMakeUpAmount1, book.getFMakeUpAmount(), 0);
		
		//本币总成本 = 原币总成本 * 基础汇率/组合汇率
		fHMakeUpCost1 = YssD.round(YssD.mul(fOMakeUpCost1,fExRate1),2);
		
		
		//剩余数量为零计算应退合计,否则则存零
		//if (fRemaindAmount==0) {    //edit by zhaoxianlin 20121024  
			
			/**shashijie 2011-12-27 STORY 1789 应退合计计算方式变更 */
				if (rs.getString("FTradeTypeCode").equalsIgnoreCase("KS")) {//申购时,应退合计 = 补票的应付替代款
					fSumReturn = fMakeUpRepCash1;
				} else {//赎回时,应退合计 = 补票应付替代款 + 赎回的替代金额
					fSumReturn = YssD.add(fMakeUpRepCash1,fReplaceCash);
				}
			/**end*/
			
		//}
		//存入第一次补票数据
		setOneBuyEQ(book,rs.getDate("FBargainDate"),fMakeUpAmount1,fMakeUpUnitCost1,fOMakeUpCost1,
				fHMakeUpCost1,fMakeUpRepCash1,fCanMkUpRepCash1,fExRate1,fTradeUnitCost1,fFeeUnitCost1,fSumReturn,
				DataDirection);
		//----------------------------------------第一次补票数据end-------------------------------------//
		/**最后更新申赎的数量,中间过程参与运算所以最后乘以方向*/
		book.setFMakeUpAmount(YssD.mul(book.getFMakeUpAmount(), DataDirection));
		tempBookList.add(book);
	}

	/**存入第一次补票数据
	 * @param book
	 * @param fMakeUpDate1 第一次补票的日期
	 * @param fMakeUpAmount1 第一次补票的数量
	 * @param fMakeUpUnitCost1 第一次补票的单位成本
	 * @param fOMakeUpCost1 第一次补票的总成本（原币）
	 * @param fHMakeUpCost1 第一次补票的总成本（本币）
	 * @param fMakeUpRepCash1 第一次补票的应付替代款
	 * @param fCanMkUpRepCash1 第一次补票的可退替代款
	 * @param fExRate1 第一次补票汇率
	 * @param fTradeUnitCost1 第一次补票的成交单价
	 * @param fFeeUnitCost1 第一次补票的费用单价
	 * @param fSumReturn 应退合计
	 * @param DataDirection 数据方向
	 * @author shashijie ,2011-11-29 , STORY 1789
	 * @modified
	 */
	private void setOneBuyEQ(TempBook book, java.sql.Date fMakeUpDate1,
			double fMakeUpAmount1, double fMakeUpUnitCost1,
			double fOMakeUpCost1, double fHMakeUpCost1, double fMakeUpRepCash1,
			double fCanMkUpRepCash1, double fExRate1, double fTradeUnitCost1,
			double fFeeUnitCost1, double fSumReturn,double DataDirection) {
		if (book==null) {
			return;
		}
		book.setFMakeUpDate1(fMakeUpDate1);//第一次补票的日期
		book.setFMakeUpAmount1(YssD.mul(fMakeUpAmount1,DataDirection));//第一次补票的数量
		book.setFMakeUpUnitCost1(fMakeUpUnitCost1);//第一次补票的单位成本
		book.setFOMakeUpCost1(YssD.mul(fOMakeUpCost1,DataDirection));//第一次补票的总成本（原币）
		book.setFHMakeUpCost1(YssD.mul(fHMakeUpCost1,DataDirection));//第一次补票的总成本（本币）
		book.setFMakeUpRepCash1(YssD.mul(fMakeUpRepCash1,DataDirection));//第一次补票的应付替代款
		book.setFCanMkUpRepCash1(YssD.mul(fCanMkUpRepCash1,DataDirection));//第一次补票的可退替代款
		book.setFExRate1(fExRate1);//第一次补票汇率
		book.setFTradeUnitCost1(fTradeUnitCost1);//第一次补票的成交单价
		book.setFFeeUnitCost1(fFeeUnitCost1);//第一次补票的费用单价
		book.setFSumReturn(fSumReturn);//应退合计
	}

	/**存入替代金额(应退赎回款),可退替代款,退款日期,剩余数量
	 * @param book 对象
	 * @param fReplaceCash 替代金额(应退赎回款)
	 * @param fCanReplaceCash 可退替代款
	 * @param fRefundDate 退款日期
	 * @param fRemaindAmount 剩余数量
	 * @param DataDirection 数据方向
	 * @author shashijie ,2011-11-29 , STORY 1789
	 * @modified 
	 */
	private void setTempBook(TempBook book, double fReplaceCash, double fCanReplaceCash,
			Date fRefundDate,double fRemaindAmount,double DataDirection) {
		if (book == null) {
			return;
		}
		book.setFReplaceCash(YssD.mul(fReplaceCash,DataDirection));//替代金额
		book.setFCanReplaceCash(YssD.mul(fCanReplaceCash,DataDirection));//可退替代款
		book.setFRemaindAmount(fRemaindAmount);//剩余数量
		book.setFRefundDate(fRefundDate);//退款日期
	}

	/**获取排序编号
	 * @param fSecurityCode 证券
	 * @param fStockHolderCode 股东
	 * @param FContractNum 合同号
	 * @author shashijie ,2011-11-29 , STORY 1789
	 */
	private String getfOrderCode(String fSecurityCode, String fStockHolderCode,
			String FContractNum) {
		String orerCode = fSecurityCode+"##"+fStockHolderCode+"##"+FContractNum;
		return orerCode;
	}

	/**获取可增加的编号(最大编号+1)
	 * @author shashijie ,2011-11-28 , STORY 1789
	 */
	private String getFnum(String strNumDate, long count) throws YssException {
		String fnumString = "";
		try {
			//截取后六位并转换成long
			String s = strNumDate.substring(9, strNumDate.length());
			long sNum = Long.parseLong(s) + count;
			//补足六位数
			String tmp = "";
			for (int t = 0; t < s.length()- String.valueOf(sNum).length(); t++) {
				tmp += "0";
			}
			fnumString = strNumDate.substring(0, 9) + tmp + sNum;
		} catch (Exception e) {
			fnumString = "";
			throw new YssException("获取可增加的编号(最大编号+1)出错！",e);
		}
		return fnumString;
	}

	/**汇总临时台账数据
	 * @param dDate
	 * @author shashijie ,2011-11-30 , STORY 1789
	 */
	private void doTotalTempBook(Date dDate) throws YssException {
		//清空集合
		if (!tempBookList.isEmpty()) {
			tempBookList.clear();
		}
		//复原编号生成计数器
		if (maxNum!=0) {
			maxNum = 0;
		}
		ResultSet rs = null;
		try{
			String sql = getTotalTempBookSql(dDate);
			rs = dbl.openResultSet(sql);
			//获取表中最大记录编号
			String strNumDate = getStrNumDate(dDate, "Tb_Etf_Tempstandingbook");
			while(rs.next()){
				//处理数据
				doTotalTempBookPresser(rs,strNumDate,dDate);
			}
		}catch (Exception e) {
			throw new YssException("汇总临时台账数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}

	/**处理汇总临时台账数据
	 * @param rs
	 * @param strNumDate
	 * @author shashijie ,2011-11-30 , STORY 1789 
	 */
	private void doTotalTempBookPresser(ResultSet rs, String strNumDate,Date dDate) throws Exception {
		maxNum += 1;
		//获取可增加的编号(最大编号+1)
		String fNum = getFnum(strNumDate,maxNum);
		
		TempBook book = new TempBook();
		book.setFNum(fNum);//申请编号
		book.setFPortCode(rs.getString("FPortCode"));//组合代码
		book.setFSecurityCode(rs.getString("Fsecuritycode"));//证券代码
		book.setFStockHolderCode(" ");//投资者
		book.setFBrokerCode("");//券商
		book.setFSeatCode("");//交易席位
		book.setFBs(rs.getString("FBs"));//台账类型
		book.setFBuyDate(dDate);//申赎日期
		book.setFMakeUpAmount(rs.getDouble("FMakeUpAmount"));//替代数量
		book.setFUnitCost(YssD.round((rs.getDouble("FUnitCost")),2));//单位成本
		book.setFExchangeRate(rs.getDouble("FExchangeRate"));//汇率
		
		double DataDirection = 0;//数据方向
		if (rs.getString("FBs").equals("B")) {//申购
			DataDirection = 1;
		} else {
			/**shashijie 2011-12-27 STORY 1789 赎回数据之前已经乘以方向了,这里汇总不需要在乘以-1*/
			DataDirection = 1;
			/**end*/
		}
		//申赎数据
		setTempBook(book, rs.getDouble("FReplaceCash"), rs.getDouble("FCanReplaceCash"), rs.getDate("FRefundDate"), 
				rs.getDouble("FRemaindAmount"), DataDirection);
		
		//第一次补票汇率
		book.setFExRate1(rs.getDouble("FExRate1"));
		//第一次补票数据
		setOneBuyEQ(book, rs.getDate("FMakeUpDate1"), rs.getDouble("FMakeUpAmount1"), 0, 
				rs.getDouble("FOMakeUpCost1"), rs.getDouble("FHMakeUpCost1"), rs.getDouble("FMakeUpRepCash1"), 
				rs.getDouble("FCanMkUpRepCash1"), rs.getDouble("FExRate1"), 0, 
				0, rs.getDouble("FSumReturn"), DataDirection);
		//获取排序编号
		String FOrderCode = 
			getfOrderCode(book.getFSecurityCode(), book.getFStockHolderCode(), rs.getString("Ftradenum"));
		book.setFOrderCode(FOrderCode);//排序编号--分级类型相连用”##”分割
		book.setFGradeType1(book.getFSecurityCode());//分级类型1--证券代码
		book.setFGradeType2(book.getFStockHolderCode());//分级类型2--股东代码
		book.setFGradeType3(rs.getString("Ftradenum"));//分级类型3--合同号,汇总的是: totaldata
		
		tempBookList.add(book);
	}

	/**汇总临时台账SQL
	 * @param dDate
	 * @author shashijie ,2011-11-30 , STORY 1789
	 */
	private String getTotalTempBookSql(Date dDate) {
		String sqlString = "Select Sum(t.Fmakeupamount) As Fmakeupamount, Max(t.Fexchangerate) As Fexchangerate," +
			" Max(t.Funitcost) As Funitcost, Sum(t.Freplacecash) As Freplacecash, " +
			" Sum(t.Fcanreplacecash) As FCanReplaceCash, Sum(t.Fmakeupamount1) As Fmakeupamount1," +
			" Sum(t.Fomakeupcost1) As Fomakeupcost1, Sum(t.Fhmakeupcost1) As Fhmakeupcost1, " +
			" Sum(t.Fmakeuprepcash1) As Fmakeuprepcash1, Sum(t.Fcanmkuprepcash1) As Fcanmkuprepcash1," +
			" Max(t.Frefunddate) As Frefunddate, Sum(t.Fremaindamount) As Fremaindamount, " +
			" Sum(t.Fsumreturn) As Fsumreturn, Max(t.Fmakeupdate1) As Fmakeupdate1, " +
			" Max(t.FExRate1) As FExRate1,  " +
			" t.FSecurityCode, t.FBs," +
			" t.FPortCode, 'totaldata' As FTradeNum From "+pub.yssGetTableName("Tb_Etf_Tempstandingbook")+" t " +
			" Where t.FPortcode In ("+operSql.sqlCodes(portCodes)+") And t.FBuydate = " +
			dbl.sqlDate(dDate)+" And t.FGradeType3 <> 'totaldata' Group By t.FSecurityCode, t.FBs, t.FPortCode ";
		return sqlString;
	}

	/**shashijie 2013-1-5  STORY 3328  根据基础参数来源获取汇率  */
	private double getRightRate(Date exchangeRate,String FTradeCury,String FbaseRateSrcBPCode,
			String FbaseRateBPCode,String FportRateSrcBPCode,String FportRateBPCode,String Fportcode) throws YssException {
		double rate = 1;
		try {
			/**shashijie 2013-1-5 STORY 3328 根据基础参数来源获取汇率*/
			//补票基础汇率
			double baseRate = paramSetAdmin.getExchangeRateValue(exchangeRate, FbaseRateSrcBPCode,
					FbaseRateBPCode,FTradeCury, Fportcode,
					YssOperCons.YSS_RATE_BASE);
			//补票组合汇率
			double portRate = paramSetAdmin.getExchangeRateValue(exchangeRate,FportRateSrcBPCode,
					FportRateBPCode,"",Fportcode,YssOperCons.YSS_RATE_PORT);
			rate = YssD.div(baseRate,portRate);
			/**end shashijie 2013-1-5 STORY 3328*/
			
		} catch (Exception e) {
			throw new YssException("获取汇率出错",e);
		} finally {

		}
		return rate;
	}
	
	/**shashijie 2013-1-5 STORY 3328 测试使用,旧模式获取工作日 */
	/*private Date getWorkDayByWhere(String sHolidayCode, Date dDate, int dayInt) throws YssException {
		Date mDate = null;//到推出的补票工作日
		//公共获取工作日类
		BaseOperDeal operDeal = new BaseOperDeal();
        operDeal.setYssPub(pub);
        mDate = operDeal.getWorkDay(sHolidayCode, dDate, dayInt);
        return mDate;
	}*/
}