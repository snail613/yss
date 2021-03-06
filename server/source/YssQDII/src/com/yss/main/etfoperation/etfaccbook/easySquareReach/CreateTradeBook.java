package com.yss.main.etfoperation.etfaccbook.easySquareReach;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.etfaccbook.CreateBookPretreatmentAdmin;
import com.yss.main.etfoperation.etfaccbook.CtlETFAccBook;
import com.yss.main.etfoperation.etfaccbook.PretValMktPriceAndExRate;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailRefBean;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/** 根据基础信息(过户,篮子数,交易,回报)生成交易结算和交易结算关联表数据 shashijie 2011.07.28 需求 1434 */
public class CreateTradeBook extends CtlETFAccBook{
	private ArrayList tradeSettleDetail = new ArrayList();//保存操作日期当天明细关联数据
	private ETFParamSetBean paramSet = null;// ETF参数的实体类
	private ETFParamSetAdmin paramSetAdmin = null;
	private HashMap etfParam = null;//保存参数设置
	private String securityCodes = "";//证券代码
	private java.util.Date startDate = null;//开始的申赎日期
	private java.util.Date endDate = null;//结束的申赎日期	
	private java.util.Date tradeDate = null;//估值日期
	private String standingBookType = "";//台账类型：申购-B，赎回-S
	private String portCodes = "";//组合代码
	
	private HashMap dividendMap = null;//保存分红权益信息
	/**add---huhuichao 2013-8-7 STORY  4276  博时：跨境ETF补充增加一类公司行动 */
	private HashMap bonusShareMap = null;//保存送股权益信息(A送A)
	private HashMap bonusShareMapAToB = null;//保存送股权益信息(A送B)
	/**end---huhuichao 2013-8-7 STORY  4276*/
	private HashMap rightsIssueMap = null;//保存配股权益信息
	private HashMap deflationBonusMap = null;//保存缩股权益信息
	
	public CreateTradeBook() {
		super();
	}
	
	/** 处理业务的入口方法 */
	public void doManageAll() throws YssException{
		int days = 0;//保存循环日期之差
		Date dDate = null;//日期
		PretValMktPriceAndExRate marketValue = null;//预处理估值行情和估值汇率
		CreateBookPretreatmentAdmin booPreAdmin = null;//明细数据和明细关联数据 保存数据操作类
		try{
			booPreAdmin = new CreateBookPretreatmentAdmin();
			booPreAdmin.setYssPub(pub);
			
			marketValue = new PretValMktPriceAndExRate();//实例化
			marketValue.setYssPub(pub);//设置pub
			
			days = YssFun.dateDiff(this.getStartDate(),this.getEndDate());//循环日期时，保存最大日期与最小日期的差
			dDate = this.getStartDate();//得到操作的起始日期
			marketValue.getValMktPriceAndExRateBy(this.getPortCodes(),dDate);//获取估值行情
			for(int i=0;i<=days;i++){//循环日期
				//确认日生成申赎数据，需要判断当天是否是境内节假日  panjunfang modify 20110907
				//if(!isChinaHoliday(paramSet.getSHolidayCode(),dDate)){
					//获取交易日(申赎日)					
					Date date = getBuyDate(dDate);
					if(date != null){
						doTradestldtl(date);//此方法处理交易结算明细表数据-申赎数据
						//------------------------往明细表和关联表中插入数据-------------------------//
						if(tradeSettleDetail.size()>0){
							booPreAdmin.insertTheDateData(date,this.portCodes,tradeSettleDetail);//插入当天申购赎回数据和关联数据
						}
						//-------------------------------end----------------------------------------//
					}
				//}
				/**shashijie 2011.07.04 STORY 1434 */
				//清空
				tradeSettleDetail.removeAll(tradeSettleDetail);
				doMakeTradeSettleDelRef(dDate);//处理钆差补票数据,权益数据
				if(tradeSettleDetail.size()>0){
					//插入当天交易结算明细关联数据
					booPreAdmin.insertRefData(dDate, this.portCodes,tradeSettleDetail);
					//booPreAdmin.insertTheDateData(makeDate,this.portCodes,tradeSettleDetail);
				}
				/**end*/
				dDate = YssFun.addDay(dDate,1);//每一次循环把日期加一天
			}
			
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	/** 此方法做解析前台传来数据，在调用此方法时就实例化一些全局变量和类 */
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

	/**此方法处理交易结算明细表数据-申赎数据及当天的补票数据*/
	private void doTradestldtl(Date dDate) throws YssException{
		String strSql = null;//做拼接SQL语句
		ResultSet rs = null;//声明结果集
		long sNum = 0;//做拼接申请编号用
		String strNumDate = "";//保存申请编号
		
		try{
			if(tradeSettleDetail.size()!=0){//如果集合不为空时，要先清空
				tradeSettleDetail.clear();
			}
			
			strSql = getSqlDate(dDate);
			rs = dbl.openResultSet(strSql);
			
			//--------------------拼接交易编号---------------------
			strNumDate = YssFun.formatDatetime(dDate).substring(0, 8);//日期
			//获取表中最大编号  + 1
			strNumDate = strNumDate + dbFun.getNextInnerCode(pub.yssGetTableName("Tb_ETF_TradeStlDtl"), 
							dbl.sqlRight("FNUM", 6), "000000", " where FNum like 'T"
							+ strNumDate + "%'", 1);
			strNumDate = "T" + strNumDate;//最大编号
			String s = strNumDate.substring(9, strNumDate.length());//后6为编号
			sNum = Long.parseLong(s);//后6为编号数值
			// --------------------------------end--------------------------//
			//根据组合,日期,获取那天的申购赎回数量
			HashMap hmbasket = this.getTotalETFTradeAmout(dDate,portCodes,"2ndcode");
			//判断是净申购还是净赎回
			String[] portCodesValue = this.portCodes.split(",");
			for (int i = 0; i < portCodesValue.length; i++) {
				String portCode = portCodesValue[i];
				//key (组合+申赎标示+市场代码)
				String bkey = portCode + "\t" + "B" + "\t" + paramSet.getTwoGradeMktCode();//过户表中B表示赎回
				String skey = portCode + "\t" + "S" + "\t" + paramSet.getTwoGradeMktCode();//过户表中S表示申购
				if (hmbasket==null || hmbasket.isEmpty()) { return ; }
				double bBasketCount = 0;// 赎回篮子数
				double sBasketCount = 0;// 申购篮子数
				if (hmbasket.containsKey(bkey)) {
					bBasketCount = Double.valueOf((String)hmbasket.get(bkey)).doubleValue();
				}
				if(hmbasket.containsKey(skey)){
					sBasketCount = Double.valueOf((String)hmbasket.get(skey)).doubleValue();
				}
				while (rs.next()) {
					/**shashijie 2011-11-01 STORY 1434 */
					//若替代标示==6,则表示次证券不需要补票,不需要产生台账申赎数据
					if (rs.getString("FReplaceMark").equals("6")) {
						continue;
					}
					/**end*/
					//拼接交易编号
					sNum++;
					strNumDate = getSNum(strNumDate,sNum,s);
					
					/**shashijie 2012-12-10 STORY 3328  修改:根据基础参数来源获取汇率*/
					double rate = getRightRate(dDate,rs.getString("FCuryCode"),
							rs.getString("FbaseRateSrcSSCode"), rs.getString("FbaseRateSSCode"),
							rs.getString("FportRateSrcSSCode"), rs.getString("FportRateSSCode"),
							rs.getString("Fportcode"));
					//测试,直接从估值行情汇率表中取
					//double aaa = YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate"));
					/**end shashijie 2012-12-10 STORY 3328*/
					
					ETFTradeSettleDetailBean detail = new ETFTradeSettleDetailBean();//交易结算明细表
					ETFTradeSettleDetailBean detail2 = new ETFTradeSettleDetailBean();//交易结算明细表
					/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~申购数据~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
					if (sBasketCount>0) {//赋值一批申购数据
						/**shashijie 2012-12-10 STORY 3328 根据基础参数来源获取汇率*/
						setETFTradeSettleDetailSH(detail,strNumDate,rs.getString("FPortCode")
								,rs.getString("FSecurityCode"),
								"B",dDate,rs.getDouble("FAmount"),rs.getDouble("FPrice"),
								/*YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate"))*/
								rate,sBasketCount);
						/**end*/
						
						//替代金额原币  = 收盘价  * 申赎股票数    * （1+溢价比例） 
						//替代金额(原币) = 总金额  / 汇率
						double OReplaceCash = //YssD.round(
								YssD.div(rs.getDouble("FTotalMoney"),detail.getExchangeRate());//,2,false);
						//替代金额本币 = 收盘价  * 申赎股票数   * 汇率 * （1+溢价比例） 
						//这里直接取:总金额  = 替代金额(本币)
						double HReplaceCash = //YssD.round(
								rs.getDouble("FTotalMoney");//, 2,false);
						//可退替代款原币 =替代金额原币 - 收盘价  * 申赎股票数
						double OcReplaceCash = YssD.sub(OReplaceCash,
								YssD.round(YssD.mul(detail.getReplaceAmount(),detail.getUnitCost())
								,2,false));
						//可退替代款本币 = 替代金额本币 - 收盘价  * 申赎股票数 * 汇率
						double HcRepLaceCash = YssD.sub(HReplaceCash,
								YssD.round(YssD.mul(detail.getReplaceAmount(),
											detail.getUnitCost(),detail.getExchangeRate())
								,2,false));
						
						setETFTradeSettleDetailSHMake(detail,OReplaceCash,HReplaceCash,OcReplaceCash,HcRepLaceCash);
						
						tradeSettleDetail.add(detail);//把明细数据存入集合中
					}
					/**~~~~~~~~~~~~~~~~~~~~~~~~~~~end~~~~~~~~~~~~~~~~~~~~~~~~~~*/
					
					/**~~~~~~~~~~~~~~~~~~~~~~~~~~赎回数据~~~~~~~~~~~~~~~~~~~~~~~~~~*/
					if (bBasketCount>0) {//赋值一批赎回数据
						//拼接交易编号
						sNum++;
						strNumDate = getSNum(strNumDate,sNum,s);
						
						/**shashijie 2012-12-10 STORY 3328 根据基础参数来源获取汇率*/
						setETFTradeSettleDetailSH(detail2,strNumDate,rs.getString("FPortCode")
								,rs.getString("FSecurityCode"),
								"S",dDate,rs.getDouble("FAmount"),rs.getDouble("FPrice"),
								/*YssD.div(rs.getDouble("FBaseRate"),rs.getDouble("FPortRate"))*/
								rate,bBasketCount);
						/**end*/
						
						//应退赎回款（原币） = 篮子中的股票数量*股票的T日收盘价
						double OcReplaceCash = YssD.round(
								YssD.mul(detail2.getReplaceAmount(),rs.getDouble("FPrice"))
								,2,false);
						//应退赎回款（本币） = 篮子中的股票数量*股票的T日收盘价*T日的估值汇率 
						double HReplaceCash = YssD.round(
								YssD.mul(detail2.getReplaceAmount(),rs.getDouble("FPrice"),detail2.getExchangeRate())
								,2,false);
						
						setETFTradeSettleDetailSHMake(detail2,OcReplaceCash,HReplaceCash,OcReplaceCash,HReplaceCash);
						tradeSettleDetail.add(detail2);
					}
					/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~end~~~~~~~~~~~~~~~~~~~~~~~*/

				}
			}
		}catch (Exception e) {
			throw new YssException("处理交易结算明细表数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**shashijie ,2011-8-4 拼接交易编号 STORY 1434 */
	private String getSNum(String strNumDate, long sNum,String s) {
		String tmp = "";
		for (int t = 0; t < s.length()- String.valueOf(sNum).length(); t++) {
			tmp += "0";
		}
		strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
		return strNumDate;
	}

	/**shashijie ,2011-8-3 存入替代金额,可退替代款(应退赎回款), STORY 1434 
	 * @param oReplaceCash 替代金额原币
	 * @param hReplaceCash 替代金额本币
	 * @param ocReplaceCash 可退替代款原币
	 * @param hcRepLaceCash 可退替代款本币
	 * @param detail 交易结算明细对象
	 * */
	private void setETFTradeSettleDetailSHMake(ETFTradeSettleDetailBean detail,
			double oReplaceCash, double hReplaceCash, double ocReplaceCash,
			double hcRepLaceCash) {
		//替代金额原币  = 收盘价  * 申赎股票数    * （1+溢价比例） 
		detail.setOReplaceCash(oReplaceCash);
		//替代金额本币 = 收盘价  * 申赎股票数   * 汇率 * （1+溢价比例） 
		detail.setHReplaceCash(hReplaceCash);
		//可退替代款原币 =替代金额原币 - 收盘价  * 申赎股票数
		detail.setOcReplaceCash(ocReplaceCash);
		//可退替代款本币 = 替代金额本币 - 替代数量 * 单位成本 * 基础汇率/组合汇率
		detail.setHcReplaceCash(hcRepLaceCash);
	}

	/**shashijie,2011-8-3 获取申赎处理sql STORY 1434 */
	/**shashijie,2012-12-10 STORY 3328 修改,关联出ETF基本信息参数表*/
	private String getSqlDate(Date dDate) {
		/**add---huhuichao 2013-7-25 BUG  8749 BUG8749ETF成分券证券信息品种类型为存托时，ETF申赎台账表无法显示完整的信息 */
		String sqlString = " select st.*,(case when sub.FtradeAmount is null then 0 else sub.FtradeAmount " +
			" end) as FsubAmount, sub.FTotalCost, mk.FPrice, ra.*, h.* From /*股票蓝 */ (select st.* From "+
			pub.yssGetTableName("Tb_ETF_StockList")+" st where st.FCheckState = 1 "+
			" and st.FPortCode in ( "+operSql.sqlCodes(this.portCodes)+" ) and st.FDate = " +
			dbl.sqlDate(dDate)+" ) st " + //股票蓝 
			" /*交易数据子表*/ Left Join (select * From "+pub.yssGetTableName("Tb_Data_SubTrade")+" where FCheckState = 1 "+
			" and FAppDate = "+dbl.sqlDate(dDate)+" and FPortCode in ("+operSql.sqlCodes(this.portCodes)+")"+
			" and FBargainDate = "+dbl.sqlDate(dDate)+" ) sub on sub.FSecurityCode = st.FSecurityCode "+
	        " and sub.FPortcode = st.FPortcode "+//交易数据子表
	        " /*估值预处理行情表*/ Left Join (select * From "+pub.yssGetTableName("Tb_Data_PretValMktPrice")+
	        " where FCheckState = 1 and FValDate = "+dbl.sqlDate(dDate)+" and FPortCode in ( " +
	        operSql.sqlCodes(this.portCodes)+" )) mk on st.FSecurityCode = mk.fsecuritycode "+
	        " and st.FPortCode = mk.FPortCode "+  //估值预处理行情表
	        " /*证券信息设置*/ Join (select * From "+pub.yssGetTableName("Tb_para_security")+" where FCheckState = 1 "+
	        ") se on st.FSecurityCode = se.FSecurityCode "+ //证券信息设置
	        " /*估值预处理汇率表*/ Left Join (select FBaseRate,FPortRate,FValDate,FCuryCode," +
	        " FPortCode,FOTBaseRate1,FOTBaseRate2, "+
	        " FOTBaseRate3 From "+pub.yssGetTableName("Tb_Data_PretValRate")+//估值预处理汇率表
	        " where FCheckState = 1 and FPortCode in ( "+operSql.sqlCodes(portCodes)+ " ) and FValDate = " +
	        dbl.sqlDate(dDate)+" ) ra on st.FPortCode = ra.FPortCode and se.FTradeCury = ra.FCuryCode " +
	        //ETF基本信息参数
	        " /*ETF基本信息参数*/ Left Join (Select H1.Fportcode, H1.FbaseRateSrcSSCode, H1.FbaseRateSSCode," +
	        " H1.FportRateSrcSSCode, H1.FportRateSSCode, H1.FbaseRateSrcBPCode, H1.FbaseRateBPCode," +
	        " H1.FportRateSrcBPCode, H1.FportRateBPCode From "+pub.yssGetTableName("Tb_Etf_Param")+" H1" +
    		" Where H1.Fcheckstate = 1) h On h.fportcode = st.fportcode"+
	        " Order by st.FSecurityCode ";
		/**end---huhuichao 2013-7-25 BUG  8749*/
		return sqlString;
	}

	/** 此方法处理分红数据 */
	private void doDivdend(ETFTradeSettleDetailRefBean tradeSettleDelRef, double dAmount, String sKey) 
		throws YssException{
		double divValue = 0;//分红总派息(原币)
		double bbinterest = 0;//分红总派息（本币）
		try{
			//分红总派息（原币） = 申赎数量* 分红权益比例
			divValue = YssD.mul(dAmount,(Double)dividendMap.get(sKey));
			//分红总派息（原币）
			tradeSettleDelRef.setInterest(YssD.add(divValue,tradeSettleDelRef.getInterest()));
			//分红总派息（本币）= 分红总派息（原币）* 除权日汇率
			bbinterest = YssD.mul(tradeSettleDelRef.getInterest(),tradeSettleDelRef.getRightRate());
			//分红总派息（本币）
			tradeSettleDelRef.setBbinterest(bbinterest);

		}catch (Exception e) {
			throw new YssException("处理分红数据出错！",e);
		}
	}
	
	/** 此方法处理送股数据 */
	private void doBonusShare(ETFTradeSettleDetailRefBean tradeSettleDelRef, double dAmount, String sKey) throws YssException{
		double shareAllAmount = 0;//送股总数量
		try{

			//送股总数量 = 申赎数量* 送股权益比例
			shareAllAmount = YssD.mul(dAmount,(Double)bonusShareMap.get(sKey));
			tradeSettleDelRef.setSumAmount(YssD.add(tradeSettleDelRef.getSumAmount(),shareAllAmount));//送股总数量
			//实际数量= 申赎数量 * 送股权益比例(原本是拿第一次补票后的剩余数量*比例,这里不考虑)
			tradeSettleDelRef.setRealAmount(YssD.add(tradeSettleDelRef.getRealAmount(),shareAllAmount));

		}catch (Exception e) {
			/**add by huhuichao 2013-8-8 story 4276 博时：跨境ETF补充增加一类公司行动*/
			throw new YssException("处理送股（A送A）数据出错！",e);
			/**end by huhuichao 2013-8-8 story 4276 */
		}
	}
	
	/**shashijie,2011-8-7 此方法处理缩股数据*/
	private void doDeflationBonus(ETFTradeSettleDetailRefBean trade, double dAmount, String sKey) throws YssException {
		double deflationAmount = 0;//缩股数量
		try{

			//缩股总数量 = 申赎数量  * 缩股权益比例
			deflationAmount = YssD.mul(dAmount,(Double)this.deflationBonusMap.get(sKey));
			trade.setDeflationAmount(YssD.add(deflationAmount,trade.getDeflationAmount()));//缩股总数量
			
		}catch (Exception e) {
			throw new YssException("处理缩股数据出错！",e);
		}
	}
	
	/** 此方法处理配股数据 */
	private void doRightIssue(ETFTradeSettleDetailRefBean tradeSettleDelRef, String portCode, double dAmount, String sKey, Date dDate) throws YssException{
		double rightValue = 0;//权证价值
		//double dPrice = 0;//当日收盘价
		double dPriceSub = 0;//若权证有行情，为权证行情，否则 = 当日收盘价 - 权证价格
		//double dOPPrice = 0;//权证行情
		double dRatio = 0;//权证比例
		double dExPrice = 0;//配股价		
		try{
			String[] s = null;
			String sValue = (String)rightsIssueMap.get(sKey);
			s = sValue.split("\t");
			dRatio = Double.valueOf(s[2]).doubleValue();
			dExPrice = Double.valueOf(s[3]).doubleValue();
			dPriceSub = getRightsIssuePrice(portCode, dDate, s[0], s[1], dExPrice);

			rightValue = YssD.mul(dAmount,dPriceSub,dRatio);

			tradeSettleDelRef.setWarrantCost(YssD.add(rightValue,tradeSettleDelRef.getWarrantCost()));//权证价值（原币）
			//权证价值（本币）= 权证价值（原币） * 除权日汇率
			tradeSettleDelRef.setBbwarrantCost(YssD.mul(tradeSettleDelRef.getWarrantCost(),tradeSettleDelRef.getRightRate()));
			
		}catch (Exception e) {
			throw new YssException("处理配股数据出错！",e);
		}
	}
	
	/**
	 * add by huhuichao 2013-8-8 story 4276 博时：跨境ETF补充增加一类公司行动 此方法处理送股数据（A送B）
	 * 
	 * @param ETFTradeSettleDetailRefBean
	 *            tradeSettleDelRef
	 * @param double dAmount
	 * @param String
	 *            sKey
	 * @param Date
	 *            dDate
	 * @throws YssException
	 */
	private void doBonusShareAToB(
			ETFTradeSettleDetailRefBean tradeSettleDelRef, String portCode,
			double dAmount, String sKey, Date dDate) throws YssException {
		double dRatio = 0;// 权益比例
		double dPriceSub = 0;// 标的证券行情价
		double otherRightValue = 0;// 其他权益价值（送股权益A送B）
		try {
			String[] s = null;
			String sValue = (String) bonusShareMapAToB.get(sKey);
			s = sValue.split("\t");
			dRatio = Double.valueOf(s[2]).doubleValue();
			dPriceSub = getTSecurityCodePrice(portCode, dDate, s[0], s[1]);
			otherRightValue = YssD.mul(dAmount, dPriceSub, dRatio);
			tradeSettleDelRef.setOtherRight(YssD.add(otherRightValue,
					tradeSettleDelRef.getOtherRight()));// 其他权益(原币)
			// 其他权益（本币）= 其他权益（原币） * 除权日汇率
			tradeSettleDelRef.setbBOtherRight(YssD.mul(tradeSettleDelRef
					.getOtherRight(), tradeSettleDelRef.getRightRate()));
		} catch (Exception e) {
			throw new YssException("处理送股（A送B）数据出错！", e);
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
	
	/**shashijie,2011-7-8 处理钆差补票 ,处理明细关联数据赋值,STORY 1434*/
	private void doMakeTradeSettleDelRef(Date dDate) throws YssException {
		String strSql = "";
		
		/**shashijie 2012-12-10 STORY 3328 使用新获取工作日方法,考虑境内境外*/
		ArrayList buyDates = this.getBuyDates(dDate,
				paramSet.getBeginSupply()*-1,(String)paramSet.getHoildaysRela().get("beginsupply"),
				paramSet.getBeginSupply2()*-1,(String)paramSet.getHoildaysRela().get("beginsupply2"));//获取倒退出的补票工作日
		/**end shashijie 2012-12-10 STORY 3328 */
		
		Date makeDate = null;
		Date SGReplaceOver = null;//申购应付替代结转日
		Date SHReplaceOver = null;//赎回应付替代结转日
		boolean bMake = false;//是否需要补票
		ResultSet rs = null;		
		try {
			for(int j = 0; j< buyDates.size() ; j++){
				makeDate = (Date)buyDates.get(j);
				/**shashijie 2012-12-10 STORY 3328 使用新获取工作日方法,考虑境内境外*/
				//获取正推出的申购应付替代结转日期
				SGReplaceOver = getWorkDayMakeBack(makeDate,
						paramSet.getISGDealReplace(),(String)paramSet.getHoildaysRela().get("sgdealreplace"),
						paramSet.getiSGDealReplace2(),(String)paramSet.getHoildaysRela().get("sgdealreplace2"));
				//获取正推出的赎回应付替代结转日期
				SHReplaceOver = getWorkDayMakeBack(makeDate,
						paramSet.getISHDealReplace(),(String)paramSet.getHoildaysRela().get("shdealreplace"),
						paramSet.getiSHDealReplace2(),(String)paramSet.getHoildaysRela().get("shdealreplace2"));
				/**end shashijie 2012-12-10 STORY 3328 */
				
				//根据组合,日期,获取那天的申购赎回数量
				HashMap hmbasket = this.getTotalETFTradeAmout(makeDate,portCodes,"2ndcode");				
				//判断是净申购还是净赎回
				String[] portCodesValue = this.portCodes.split(",");
				for (int i = 0; i < portCodesValue.length; i++) {
					HashMap TradeSettleDetailMap = new HashMap();
					String portCode = portCodesValue[i];
					//key (组合+申赎标示+市场代码)
					String bkey = portCode + "\t" + "B" + "\t" + paramSet.getTwoGradeMktCode();//过户表中B表示赎回
					String skey = portCode + "\t" + "S" + "\t" + paramSet.getTwoGradeMktCode();//过户表中S表示申购
					if (hmbasket==null || hmbasket.isEmpty()) {
						break;
					} else {
						double bBasketCount = 0;// 赎回篮子数
						double sBasketCount = 0;// 申购篮子数
						if (hmbasket.containsKey(bkey)) {
							bBasketCount = Double.valueOf((String)hmbasket.get(bkey)).doubleValue();
						}
						if(hmbasket.containsKey(skey)){
							sBasketCount = Double.valueOf((String)hmbasket.get(skey)).doubleValue();
						}
						//获取钆差补票sql
						strSql = getStrSqlOfGCBP(dDate,makeDate,portCode);
						rs = dbl.openResultSet(strSql);
						
						//获取权益信息
						this.getExRightsInfo(makeDate, dDate, portCode);
						
						//是否T+2,T为申赎日期，2表示境内外均为工作日的情况下才补票
						if (YssFun.dateDiff(
								/**shashijie 2012-12-10 STORY 3328 使用新获取工作日方法,考虑境内境外*/
								getWorkDayMakeBack(makeDate,
										paramSet.getBeginSupply(),(String)paramSet.getHoildaysRela().get("beginsupply"),
										paramSet.getBeginSupply2(),(String)paramSet.getHoildaysRela().get("beginsupply2"))
								/**end shashijie 2012-12-10 STORY 3328 */
								,dDate) == 0) {
							bMake = true;
						} else{
							bMake = false;
						}
						
						if (bBasketCount==sBasketCount) {//正好钆平
							TradeSettleDetailMap = equalityMakeAmount(rs,dDate,SGReplaceOver,SHReplaceOver,bMake);
						} else if (bBasketCount < sBasketCount) {//净申购
							TradeSettleDetailMap = buyAmountMuch(rs,dDate,SGReplaceOver,SHReplaceOver,sBasketCount,bBasketCount,bMake);
						} else if (bBasketCount > sBasketCount) {//净赎回
							TradeSettleDetailMap = buyAmountMall(rs,dDate,SGReplaceOver,SHReplaceOver,sBasketCount,bBasketCount,bMake);
						} else {
							continue;
						}
						Iterator iter = TradeSettleDetailMap.entrySet().iterator(); 
						while (iter.hasNext()) { 
						    Map.Entry entry = (Map.Entry) iter.next(); 
						    tradeSettleDetail.add((ETFTradeSettleDetailBean)entry.getValue());
						} 
					}
				}
			}
		}catch (Exception e) {
			throw new YssException("处理交易结算明细表数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		
	}

	/**shashijie 2011.07.12 STORY 1434 ,设置交易明细对象*/
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
			
			/**shashijie 2012-12-10 STORY 3328  修改:根据基础参数来源获取汇率*/
			double rate = getRightRate(rs.getDate("Fcurrentdate"),rs.getString("FCuryCode"),
					rs.getString("FbaseRateSrcBPCode"), rs.getString("FbaseRateBPCode"),
					rs.getString("FportRateSrcBPCode"), rs.getString("FportRateBPCode"),
					rs.getString("Fportcode"));
			etf.setExchangeRate(rate);//汇率
			//测试
			/*double aaa = rs.getDouble("FExchangeRate");
			System.out.println(aaa+"~~~~~~~~~~~~~~~~汇率");*/
			/**end shashijie 2012-12-10 STORY 3328*/
			
			etf.setTradeNum(rs.getString("FTradeNum"));//成交编号
			etf.setMarktype(rs.getString("FMarkType"));//标志类型  time = 实时补票  difference = 钆差补票


		} catch (Exception e) {
			dbl.closeResultSetFinal(rs);
			throw new YssException("初始交易结算对象出错！", e);
		}		
	}

	/**shashijie,2011-7-11,获取钆差补票sql   @param Date 补票日期,makeDate 申赎日期,STORY 1434*/
	/**shashijie,2012-12-10 STORY 3328 修改,关联出ETF基本信息参数表*/
	private String getStrSqlOfGCBP(Date dDate,Date makeDate,String portCode) throws YssException {
		CtlPubPara pubPara=new CtlPubPara();//通用参数实例化
        pubPara.setYssPub(pub);//设置Pub
		//String rightsRatioMethods = (String) pubPara.getRightsRatioMethods(portCode);//获取通用参数值
		String strSql = "";
		strSql = "select a.*,b.*, (case when c.FBuyFFactSettleMoney is null then 0 else c.FBuyFFactSettleMoney " +
		     " end) as FBuyFFactSettleMoney ,(case when c.FBuyTradeAmount is null then 0 else c.FBuyTradeAmount " +
		     " end) as FBuyTradeAmount , (case when c.FSaleFFactSettleMoney is null then 0 else " +
		     " c.FSaleFFactSettleMoney " +
		     " end) as FSaleFFactSettleMoney , (case when c.FSaleTradeAmount is null then 0 else c.FSaleTradeAmount " +
		     " end) as FSaleTradeAmount , c.FTradeFee , d.* ,e.FPrice , g.Famount , " +
			 " (case when FREMAINDAMOUNT is null then a.freplaceamount else FREMAINDAMOUNT end) as FREMAINDAMOUNT," + 
			 dbl.sqlDate(dDate) + " as fcurrentdate , h.* " + 
			 
			 " From "+pub.yssGetTableName("Tb_ETF_Tradestldtl")+" a " +//ETF交易结算
			 " left join ( select FNUM ,FExrightDate as fmaxexrightdate,FREMAINDAMOUNT," + 
			 " FINTEREST,FWARRANTCOST,FDEFLATIONAMOUNT,FSUMAMOUNT from " + 
			 pub.yssGetTableName("Tb_ETF_Tradstldtlref") + //关联业务日期前的权益数据（原币），按业务日期当天汇率汇总计算权益数据（本位币）
			 " tr where exists (select ddate from (select  max(FExrightDate) as ddate,fnum from " + 
			 pub.yssGetTableName("Tb_ETF_Tradstldtlref") + 
			 " where fexrightdate < " + dbl.sqlDate(dDate) + 
			 " group by fnum) tr2 where tr2.ddate = tr.fexrightdate and tr2.fnum = tr.fnum)) et on et.fnum = a.fnum" + 
		     " Left Join (select aa1.FSecurityCode,aa1.FTradeCury From "+
		     pub.yssGetTableName("Tb_Para_Security")+" aa1 where aa1.FCheckState = 1 ) b " +//证券信息
		     " on a.FSecurityCode = b.FSecurityCode "+
		     " Left Join (Select sum((case when a1.FTradeTypeCode = '01' then a1.FFactSettleMoney  end)) as " +
		     " FBuyFFactSettleMoney , "+
		     " sum((case when a1.FTradeTypeCode = '01' then a1.FTradeAmount end)) as FBuyTradeAmount , " +
		     " sum((case when a1.FTradeTypeCode = '02' then a1.FFactSettleMoney  end)) as FSaleFFactSettleMoney ,"+
		     " sum((case when a1.FTradeTypeCode = '02' then a1.FTradeAmount end)) as FSaleTradeAmount ,"+
		     " sum (nvl(a1.FTradeFee1,0) + nvl(a1.FTradeFee2,0)+nvl(a1.FTradeFee3,0)+nvl(a1.FTradeFee4,0)+ " +
             " nvl(a1.FTradeFee5,0)+nvl(a1.FTradeFee6,0)+nvl(a1.FTradeFee7,0)+nvl(a1.FTradeFee8,0)) as FTradeFee , "+
		     " a1.FSecurityCode,a1.fportcode From "+pub.yssGetTableName("Tb_Data_Subtrade")+" a1 "+//交易数据子表
             " where a1.FBargaindate = "+dbl.sqlDate(dDate)+" And a1.FCheckState = 1 "+
             " and a1.FPortCode = " + dbl.sqlString(portCode) +
             " Group By a1.fportcode,a1.FSecurityCode ) c " + 
             " on a.fportcode = c.fportcode and a.FSecuritycode = c.FSecuritycode" +
		     " Left Join ( Select a2.FBaseRate,a2.FPortRate,a2.FPortCode,a2.FcuryCode From " +
		     pub.yssGetTableName("tb_data_pretvalrate")+" a2 where a2.FCheckState = 1 " +//估值预处理汇率表
		     " and a2.FValDate = " + dbl.sqlDate(dDate) + " and a2.FPortCode = " + 
		     dbl.sqlString(portCode) +" ) d  " +
		     " on ( d.FPortCode = a.FPortCode and d.FCuryCode = b.FTradeCury )" + 
		     " Left Join ( select a3.fsecuritycode, a3.fprice ,a3.FValdate,a3.fportcode From " +
		     pub.yssGetTableName("Tb_Data_PretValMktPrice")+" a3 ,( select max(a7.FValdate) as FValdate " +
		     " ,a7.fportcode,a7.FSecuritycode From "+pub.yssGetTableName("Tb_Data_PretValMktPrice") + 
		     " a7 where a7.FValdate <= " +
		     dbl.sqlDate(dDate)+" Group by a7.fportcode,a7.fsecuritycode ) a8 where a3.fcheckstate = 1 " +
		     " and a3.fportcode = " + dbl.sqlString(portCode) +
		     " and a8.fportcode = a3.fportcode and a8.FSecuritycode = a3.FSecuritycode and a3.FValdate = a8.FValdate " +
		     " ) e on a.fportcode = e.fportcode and a.fsecuritycode = e.FSecurityCode " + //估值预处理行情表(查询最新收盘价)
 			 " left join ( select a4.fportcode,a4.fsecuritycode,a4.famount from " +
 			 pub.yssGetTableName("Tb_ETF_StockList") +//股票篮
 			 " a4 where a4.fdate = " + dbl.sqlDate(makeDate) + 
 			 " and a4.fportcode = " + dbl.sqlString(portCode) + 
 			 " and a4.FCheckState = 1 ) g " +
 			 " on g.fportcode = a.fportcode and g.fsecuritycode = a.fsecuritycode " +
	 		 //ETF基本信息参数
	 	     " /*ETF基本信息参数*/ Left Join (Select H1.Fportcode, H1.FbaseRateSrcSSCode, H1.FbaseRateSSCode," +
	 	     " H1.FportRateSrcSSCode, H1.FportRateSSCode, H1.FbaseRateSrcBPCode, H1.FbaseRateBPCode," +
	 	     " H1.FportRateSrcBPCode, H1.FportRateBPCode From "+pub.yssGetTableName("Tb_Etf_Param")+" H1" +
	 	     " Where H1.Fcheckstate = 1) h On h.fportcode = a.fportcode"+
	 	     " where a.FBuyDate = " + dbl.sqlDate(makeDate) + " and a.FPortCode = " + dbl.sqlString(portCode) +
		     " order by a.FTradeNum,a.FBs,a.FStockHolderCode,a.FSecurityCode ";
		return strSql;
	}

	/**shashijie,2011-7-19 向后推出int BeginSupply个工作日(考虑国内国外),STORY 1434*/
	private Date getWorkDayMakeBack(Date dDate, int num1, String holidayCode1,
			int num2, String holidayCode2) throws YssException {
		Date makeDate = dDate;
		String sDate = paramSetAdmin.getWorkDay(dDate, holidayCode1, num1, holidayCode2, num2);
		makeDate = YssFun.toDate(sDate);
		return makeDate;
	}
	
	/**shashijie 2012-12-10 STORY 3328 修改:使用新获取工作日方法,考虑境内境外*/
	private ArrayList getBuyDates(Date dDate,int num1, String holidayCode1,
			int num2, String holidayCode2) throws YssException {
        ArrayList dateList = new ArrayList();
        String sDate = paramSetAdmin.getWorkDay(dDate, holidayCode1, num1, holidayCode2, num2);
        Date mDate = YssFun.toDate(sDate);
        //TA交易数据传入申赎日,业务日,获得:申赎日>=交易日期<=业务日;的所有交易日
        dateList = getBuyDate(dDate,mDate);
		return dateList;
	}
	
	/**shashijie ,2012-12-12,STORY 3328 TA交易数据传入申赎日,业务日,获得:申赎日>=交易日期<=业务日;的所有交易日*/
	private ArrayList getBuyDate(Date dDate,Date mDate) throws YssException {
		ArrayList list = new ArrayList();
		Date dBuyDate = null;//交易日
		StringBuffer buffer = new StringBuffer(100);
		ResultSet rs = null;
		try{
			buffer.append("SELECT Distinct (FTradeDate) From ").append(pub.yssGetTableName("Tb_TA_Trade"))
				.append(" WHERE FTradeDate >= ").append(dbl.sqlDate(mDate))//申赎日
				.append(" And FTradeDate < ").append(dbl.sqlDate(dDate))//业务日
				.append(" AND FPortCode in ( ").append(operSql.sqlCodes(portCodes) + " ) ")//组合
				.append(" AND FSellType in ( ").append(operSql.sqlCodes("01,02") + " ) ")//销售类型(只查询申购赎回)
				.append(" AND FcheckState = 1");
			rs = dbl.openResultSet(buffer.toString());
			while (rs.next()){
				dBuyDate = rs.getDate("FTradeDate");
				list.add(dBuyDate);
			}
		}catch (Exception e) {
			throw new YssException("获取交易日期出错 ！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return list;
	}
	
	/**shashijie 2011.07.11  获取组合代码对应的市场代码区分买卖标志的篮子数 ,STORY 1434*/
	public HashMap getTotalETFTradeAmout(Date bargainDate,String portCodes,String OperType) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		HashMap hmbasket = new HashMap();
		ETFParamSetBean paramSet = null;
		double basicRate = 0;// 基准比例
		String bs = "";// 买卖标志
		String securityCode = "";//市场代码
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
	
	/**shashijie,2011-7-11 设置交易明细,交易关联对象到集合中 ,STORY 1434*/
	private HashMap equalityMakeAmount(ResultSet rs,Date dDate,Date SGReplaceOver,Date SHReplaceOver,
			boolean bMake) throws YssException,SQLException {
		HashMap TradeSettleDetailMap = new HashMap();
		while (rs.next()) {
			String sKey = rs.getString("FNum") + "\t" + rs.getString("FPortCode") + 
						"\t" +rs.getString("FSecurityCode") + "\t" + rs.getString("FBs");
			ETFTradeSettleDetailBean etfTradeSettleDetail = new ETFTradeSettleDetailBean();//交易明细
			ETFTradeSettleDetailRefBean trade = new ETFTradeSettleDetailRefBean();//交易结算关联
			ETFTradeSettleDetailRefBean oldTrade = new ETFTradeSettleDetailRefBean();
			setETFTradeSettleDetailBean(rs,etfTradeSettleDetail);//初始交易明细对象
			
			//-----------------------------以下处理权益数据---------------------------------//
			treatRightsAndInterests(trade, rs, dDate);
			if(!bMake){
				if(trade.getExRightDate() == null){
					continue;
				}
				trade.setNum(rs.getString("FNum"));//明细关联数据的申请编号
				//关联编号：主要是区分：1-第一次补票，2-第二次补票，3-强制处理 ， yyyymmdd（除权日期）-权益数据
				trade.setRefNum(YssFun.formatDate(trade.getExRightDate(),"yyyyMMdd"));
				//剩余数量 = （申赎数量 + 送、缩股实际数量）
				trade.setRemaindAmount(YssD.sub(YssD.add(rs.getDouble("FReplaceAmount"),trade.getSumAmount()),
						trade.getDeflationAmount()));
				if(TradeSettleDetailMap.containsKey(sKey)){//汇总权益（当天同一权益有多笔的情况）
					oldTrade = (ETFTradeSettleDetailRefBean)((ETFTradeSettleDetailBean)TradeSettleDetailMap.get(sKey))
																.getAlTradeSettleDelRef().get(0);
					setTradeSettleDetail(oldTrade,trade);
				}
				etfTradeSettleDetail.getAlTradeSettleDelRef().add(trade);//保存明细关联数据
				TradeSettleDetailMap.put(sKey, etfTradeSettleDetail);
				continue;
			}
			
			//============补票BEGIN==============================//
			if(TradeSettleDetailMap.containsKey(sKey)){//汇总权益（当天同一权益有多笔的情况）
				oldTrade = (ETFTradeSettleDetailRefBean)((ETFTradeSettleDetailBean)TradeSettleDetailMap.get(sKey))
															.getAlTradeSettleDelRef().get(0);
				setTradeSettleDetail(oldTrade,trade);
			}
			//获取补票数量(算上送股数量与缩股数量)
			double replaceAmount = getReplacAmount(rs,trade);
			/**add---huhuichao 2013-8-7 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
			//总原币成本=(单位成本*补票数量)+总派息+权证价值+其他权益
			double OMakeUpCost = getAllCost(rs.getDouble("FPrice"),
					replaceAmount,trade.getInterest(),trade.getWarrantCost(),trade.getOtherRight());
			//初始交易结算关联对象,基本信息(数量,单位成本等)
			setETFTradeSettleDetailRefBean(rs,trade,dDate,rs.getDouble("FPrice"),replaceAmount,OMakeUpCost);
			/**end---huhuichao 2013-8-7 STORY  4276 */
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
			//===============补票END================================//
			etfTradeSettleDetail.getAlTradeSettleDelRef().add(trade);//保存明细关联数据
			TradeSettleDetailMap.put(sKey, etfTradeSettleDetail);
		}
		return TradeSettleDetailMap;
	}
	
	/**shashijie,2011-7-14  净申购,STORY 1434*/
	private HashMap buyAmountMuch(ResultSet rs, Date dDate, Date sGReplaceOver,Date sHReplaceOver ,
			double SGBasketCount,double SHBasketCount,boolean bMake) throws YssException,SQLException {
		HashMap TradeSettleDetailMap = new HashMap();
		while (rs.next()) {
			String sKey = rs.getString("FNum") + "\t" + rs.getString("FPortCode") + 
							"\t" +rs.getString("FSecurityCode") + "\t" + rs.getString("FBs");
			double allcost = 0;//补票总成本原币
			double unitCost = 0;//单位成本
			ETFTradeSettleDetailBean etfTradeSettleDetail = new ETFTradeSettleDetailBean();//交易明细
			ETFTradeSettleDetailRefBean trade = new ETFTradeSettleDetailRefBean();//交易结算关联
			ETFTradeSettleDetailRefBean oldTrade = new ETFTradeSettleDetailRefBean();
			setETFTradeSettleDetailBean(rs,etfTradeSettleDetail);//初始交易明细对象
			//------------------------以下处理权益数据------------------------//
			treatRightsAndInterests(trade, rs, dDate);

			//------------------------以下补票----------------------//
			if(!bMake){
				if(trade.getExRightDate() == null){
					continue;
				}
				trade.setNum(rs.getString("FNum"));//明细关联数据的申请编号
				//关联编号：主要是区分：1-第一次补票，2-第二次补票，3-强制处理 ， yyyymmdd（除权日期）-权益数据
				trade.setRefNum(YssFun.formatDate(trade.getExRightDate(),"yyyyMMdd"));
				//剩余数量 = （申赎数量 + 送、缩股实际数量）
				trade.setRemaindAmount(YssD.sub(YssD.add(rs.getDouble("FReplaceAmount"),trade.getSumAmount()),
												trade.getDeflationAmount()));
				if(TradeSettleDetailMap.containsKey(sKey)){//汇总权益（当天同一权益有多笔的情况）
					oldTrade = (ETFTradeSettleDetailRefBean)((ETFTradeSettleDetailBean)TradeSettleDetailMap.get(sKey))
																.getAlTradeSettleDelRef().get(0);
					setTradeSettleDetail(oldTrade,trade);
				}
				etfTradeSettleDetail.getAlTradeSettleDelRef().add(trade);//保存明细关联数据
				TradeSettleDetailMap.put(sKey, etfTradeSettleDetail);
				continue;
			}
			if(TradeSettleDetailMap.containsKey(sKey)){//汇总权益（当天同一权益有多笔的情况）
				oldTrade = (ETFTradeSettleDetailRefBean)((ETFTradeSettleDetailBean)TradeSettleDetailMap.get(sKey))
															.getAlTradeSettleDelRef().get(0);
				setTradeSettleDetail(oldTrade,trade);
			}
			//获取补票数量(算上送股数量与缩股数量)
			double replaceAmount = getReplacAmount(rs,trade);
			//若没有成交价就用收盘价
			if (rs.getDouble("FBuyFFactSettleMoney")==0) {
				//单位成本 = 收盘价
				unitCost = rs.getDouble("FPrice");
				/**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动 */
				//总原币成本=(单位成本*补票数量)+总派息+权证价值+其他权益
				allcost = getAllCost(unitCost,replaceAmount,trade.getInterest(),trade.getWarrantCost(),trade.getOtherRight());
				/**end---huhuichao 2013-8-8 STORY  4276*/
			} else {
				//公共数量  = 赎回篮子数	*  （单位篮子替代证券数量 + 送股数量 - 缩股数量）
				double reAmount = YssD.mul(SHBasketCount, replaceAmount);
				//净数量  = 申购篮子数量   * （单位篮子替代证券数量+ 送股数量 - 缩股数量） - 公共数量 
				double allBraketAmount = YssD.sub(YssD.mul(SGBasketCount,replaceAmount),reAmount);
				//获取补票单位成本	(交易实收价格,交易数量,净申购(赎回)数量,公共数量,收盘价)
				unitCost = getUnitCost(rs.getDouble("FBuyFFactSettleMoney"),rs.getDouble("FBuyTradeAmount")
						,allBraketAmount,reAmount,rs.getDouble("FPrice"));
				/**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动 */
				//总原币成本=(单位成本*补票数量)+总派息+权证价值+其他权益
				allcost = getAllCost(unitCost,replaceAmount,trade.getInterest(),trade.getWarrantCost(),trade.getOtherRight());
				/**end---huhuichao 2013-8-8 STORY  4276*/
			}
			
			//申购
			if (rs.getString("FBS").equals("B")) {
				setETFTradeSettleDetailRefBean(rs,trade,dDate,unitCost,replaceAmount,allcost);
			} else {//赎回
				//单位成本 = 收盘价
				unitCost = rs.getDouble("FPrice");
				/**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动 */
				//总原币成本=(单位成本*补票数量)+总派息+权证价值+其他权益
				allcost = getAllCost(unitCost,replaceAmount,trade.getInterest(),trade.getWarrantCost(),trade.getOtherRight());
				/**end---huhuichao 2013-8-8 STORY  4276*/
				setETFTradeSettleDetailRefBean(rs,trade,dDate,unitCost,replaceAmount,allcost);
			}
			//------------------------end------------------------//
			
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
			
			etfTradeSettleDetail.getAlTradeSettleDelRef().add(trade);//保存明细关联数据
			TradeSettleDetailMap.put(sKey, etfTradeSettleDetail);
		}
		return TradeSettleDetailMap;
	}

	/**shashijie,2011-7-14  净赎回,STORY 1434*/
	private HashMap buyAmountMall(ResultSet rs, Date dDate,Date sGReplaceOver,Date sHReplaceOver,
			double SGBasketCount,double SHBasketCount,boolean bMake) throws YssException,SQLException {
		HashMap TradeSettleDetailMap = new HashMap();
		while (rs.next()) {
			String sKey = rs.getString("FNum") + "\t" + rs.getString("FPortCode") + 
							"\t" +rs.getString("FSecurityCode") + "\t" + rs.getString("FBs");
			double allcost = 0;//补票总成本原币
			double unitCost = 0;//单位成本
			ETFTradeSettleDetailBean etfTradeSettleDetail = new ETFTradeSettleDetailBean();//交易明细
			ETFTradeSettleDetailRefBean trade = new ETFTradeSettleDetailRefBean();//交易结算关联
			ETFTradeSettleDetailRefBean oldTrade = new ETFTradeSettleDetailRefBean();
			setETFTradeSettleDetailBean(rs,etfTradeSettleDetail);//初始交易明细对象
			
			//-----------------------------以下处理权益数据---------------------------------//
			treatRightsAndInterests(trade, rs, dDate);
			
			//------------补票begin------------以下处理已钆,未钆的单位成本,补票数量----------------------//
			if(!bMake){
				if(trade.getExRightDate() == null){
					continue;
				}
				trade.setNum(rs.getString("FNum"));//明细关联数据的申请编号
				//关联编号：主要是区分：1-第一次补票，2-第二次补票，3-强制处理 ， yyyymmdd（除权日期）-权益数据
				trade.setRefNum(YssFun.formatDate(trade.getExRightDate(),"yyyyMMdd"));
				//剩余数量 = （申赎数量 + 送、缩股实际数量）
				trade.setRemaindAmount(YssD.sub(YssD.add(rs.getDouble("FReplaceAmount"),trade.getSumAmount()),
						trade.getDeflationAmount()));
				if(TradeSettleDetailMap.containsKey(sKey)){//汇总权益（当天同一权益有多笔的情况）
					oldTrade = (ETFTradeSettleDetailRefBean)((ETFTradeSettleDetailBean)TradeSettleDetailMap.get(sKey))
																.getAlTradeSettleDelRef().get(0);
					setTradeSettleDetail(oldTrade,trade);
				}
				etfTradeSettleDetail.getAlTradeSettleDelRef().add(trade);//保存明细关联数据
				TradeSettleDetailMap.put(sKey, etfTradeSettleDetail);
				continue;
			}
			if(TradeSettleDetailMap.containsKey(sKey)){//汇总权益（当天同一权益有多笔的情况）
				oldTrade = (ETFTradeSettleDetailRefBean)((ETFTradeSettleDetailBean)TradeSettleDetailMap.get(sKey))
															.getAlTradeSettleDelRef().get(0);
				setTradeSettleDetail(oldTrade,trade);
			}
			//获取补票数量(算上送股数量与缩股数量)
			double replaceAmount = getReplacAmount(rs,trade);
			
			//若没有成交价就用收盘价
			if (rs.getDouble("FSaleFFactSettleMoney")==0) {
				//单位成本 = 收盘价
				unitCost = rs.getDouble("FPrice");
				/**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动 */
				//总原币成本=(单位成本*补票数量)+总派息+权证价值+其他权益
				allcost = getAllCost(unitCost,replaceAmount,trade.getInterest(),trade.getWarrantCost(),trade.getOtherRight());
				/**end---huhuichao 2013-8-8 STORY  4276 */
			} else {
				//公共数量  = 申购篮子数	*  （单位篮子替代证券数量 + 送股数量 - 缩股数量）
				double reAmount = YssD.mul(SGBasketCount, replaceAmount);
				//净数量  = 赎回篮子数量 * （单位篮子替代证券数量 + 送股数量 - 缩股数量） - 公共数量
				double allBraketAmount = YssD.sub(YssD.mul(SHBasketCount,replaceAmount),reAmount);
				//获取补票单位成本:(交易实收价格,交易数量,净申购(赎回)数量,公共数量,收盘价)
				unitCost = getUnitCost(rs.getDouble("FSaleFFactSettleMoney"),rs.getDouble("FSaleTradeAmount")
						,allBraketAmount,reAmount,rs.getDouble("FPrice"));
				/**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动 */
				//总原币成本
				allcost = getAllCost(unitCost,replaceAmount,trade.getInterest(),trade.getWarrantCost(),trade.getOtherRight());
				/**end---huhuichao 2013-8-8 STORY  4276  */
			}
			
			//净赎回,先处理赎回
			if (rs.getString("FBS").equals("S")) {
				setETFTradeSettleDetailRefBean(rs,trade,dDate,unitCost,replaceAmount,allcost);
			} else {//申购
				//单位成本 = 收盘价
				unitCost = rs.getDouble("FPrice");
				/**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动 */
				//总原币成本=(单位成本*补票数量)+总派息+权证价值+其他权益
				allcost = getAllCost(unitCost,replaceAmount,trade.getInterest(),trade.getWarrantCost(),trade.getOtherRight());
				/**end---huhuichao 2013-8-8 STORY  4276   */
				setETFTradeSettleDetailRefBean(rs,trade,dDate,unitCost,replaceAmount,allcost);
			}
			//------------------------end---处理已钆,未钆的单位成本,补票数量--------------------//
			
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
			//------------------补票end----计算可退替代款与应付替代款-----------------------------//
			
			etfTradeSettleDetail.getAlTradeSettleDelRef().add(trade);//保存明细关联数据
			TradeSettleDetailMap.put(sKey, etfTradeSettleDetail);
		}
		return TradeSettleDetailMap;
	}
	
	/**shashijie,2011-7-15 初始交易结算关联对象,基本信息(数量,单位成本等) STORY 1434
	 * @param rs 结果集
	 * @param trade 交易结算对象
	 * @param dDate 申赎日期
	 * @param price 单位成本
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
		/**shashijie 2012-12-10 STORY 3328  修改:根据基础参数来源获取汇率*/
		double exchangeRate = getRightRate(dDate,rs.getString("FTradeCury"),
				rs.getString("FbaseRateSrcBPCode"), rs.getString("FbaseRateBPCode"),
				rs.getString("FportRateSrcBPCode"), rs.getString("FportRateBPCode"),
				rs.getString("Fportcode"));
		trade.setExchangeRate(exchangeRate);
		/**end shashijie 2012-12-10 STORY 3328*/
        /**add---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动 */
		//本币总成本 = (原币单位成本 * 汇率 )保留12位  * 补票数量  + 总派息本币  +  权证价值本币 + 其他权益本币
		double HMakeUpCost = YssD.add(
				YssD.mul(YssD.mul(trade.getUnitCost(),trade.getExchangeRate())
				,trade.getMakeUpAmount())
			,trade.getBbinterest(),trade.getBbwarrantCost(),trade.getbBOtherRight());
		/**end---huhuichao 2013-8-8 STORY  4276*/
		trade.sethMakeUpCost(HMakeUpCost);
		//由于是一次性补完，剩余数量 = 0 
		trade.setRemaindAmount(0);
	}
	
	

	/**shashijie,2011-7-15 计算可退替代款与应付替代款(申购)STORY 1434*/
	/**shashijie,2011-7-16,计算申购可退替代款与应付替代款 STORY 1434*/
	private void sgCountReplaceCash(ResultSet rs,
			ETFTradeSettleDetailRefBean trade, Date dDate,Date SGReplaceOver) 
			throws YssException,SQLException{
		//应付替代款（原币） = 替代金额原币  - 补票总成本原币
		double replaceCash = YssD.sub(rs.getDouble("FOReplaceCash"),trade.getoMakeUpCost());
		trade.setOpReplaceCash(replaceCash);
		
		//应付替代款（本币） = 替代金额本币 - 补票总成本本币
		replaceCash = YssD.sub(rs.getDouble("FHReplaceCash"),trade.gethMakeUpCost());
		trade.setHpReplaceCash(replaceCash);
		
		//申赎数量  = 申赎数量  + 送股数量  - 缩股数量
		double amount = YssD.sub(YssD.add(rs.getDouble("FReplaceAmount"),trade.getSumAmount()),
				trade.getDeflationAmount());
		//补票可退替代款 （原币）     =   补票数量(算上送股,缩股) / 申赎数量(算上送股,缩股)  * 申赎可退替代款(原币)
		double replaceOC = YssD.mul(YssD.div(trade.getMakeUpAmount(),amount),
				rs.getDouble("FOCReplaceCash"));
		trade.setOcReplaceCash(replaceOC);
		//可退替代款余额（原币） = 申赎数据可退替代款 （原币）- 补票可退替代款 （原币）
		trade.setOCanRepCash(YssD.round(YssD.sub(replaceOC,
				rs.getDouble("FOCReplaceCash")),2));
		
		//补票可退替代款 （本币）     =   补票数量(算上送股,缩股)  / 申赎数量(算上送股,缩股)  * 申赎可退替代款(本币)
		double replaceHC = YssD.mul(YssD.div(trade.getMakeUpAmount(),amount),
				rs.getDouble("FHCReplaceCash"));
		trade.setHcReplaceCash(replaceHC);
		//可退替代款余额（本币） = 申赎数据可退替代款 （本币）- 补票可退替代款 （本币）
		trade.setHCanRepCash(YssD.round(YssD.sub(replaceHC,
				rs.getDouble("FHCReplaceCash")),2));
		
		//应退合计（原币）= 应付替代款（原币）
		trade.setOcRefundSum(trade.getOpReplaceCash());
		//应退合计（本币）= 应付替代款（本币）
		trade.setHcRefundSum(trade.getHpReplaceCash());
		//数据方向
		trade.setDataDirection("1");
		//退款日期							
		trade.setRefundDate(SGReplaceOver);
	}
	
	
	/**shashijie,2011-7-15  计算可退替代款与应付替代款(赎回)STORY 1434*/
	/**shashijie,2011-7-16,计算赎回可退替代款与应付替代款STORY 1434*/
	private void shCountReplaceCash(ResultSet rs,
			ETFTradeSettleDetailRefBean trade, Date dDate, Date sHReplaceOver) 
			throws YssException,SQLException {
		//应付替代款（原币） = 补票总成本原币 + 总派息原币  + 权证价值原币
		trade.setOpReplaceCash(YssD.add(trade.getoMakeUpCost(),trade.getInterest(),trade.getWarrantCost()));
		
		//补票总成本原币 = 卖出数量  * (单位成本 * 汇率 ,保留12位)
		
		//应付替代款（本币） = 补票总成本本币 + 总派息本币  + 权证价值本币  
		trade.setHpReplaceCash(trade.gethMakeUpCost());//之前计算总成本时已经加了权益,所以这里直接等于
		
		//赎回数量  = 申赎数量  + 送股数量  - 缩股数量
		double amount = YssD.sub(YssD.add(rs.getDouble("FReplaceAmount"),trade.getSumAmount()),
				trade.getDeflationAmount());
		//补票可退替代款(原币) = 卖出数量(算上送股数) / 赎回数量   * 应退赎回款（原币）
		double OcReplaceCash = YssD.mul(YssD.div(trade.getMakeUpAmount(),amount),
				rs.getDouble("FOCReplaceCash"));
		trade.setOcReplaceCash(OcReplaceCash);
		
		//可退替代款余额（原币） = 申赎数据可退替代款 （原币）- 补票可退替代款 （原币） 
		trade.setOCanRepCash(YssD.round(YssD.sub(OcReplaceCash,
				rs.getDouble("FOCReplaceCash")),2));
		
		//补票可退替代款(本币) = 卖出数量(算上送股数)  / 赎回数量   * 应退赎回款（本币）
		double HcReplaceCash = YssD.mul(YssD.div(trade.getMakeUpAmount(),amount),
				rs.getDouble("FHCReplaceCash"));
		trade.setHcReplaceCash(HcReplaceCash);
		
		//可退替代款余额（本币） = 申赎数据可退替代款 （本币）- 补票可退替代款 （本币）
		trade.setHCanRepCash(YssD.round(YssD.sub( HcReplaceCash,
				rs.getDouble("FHCReplaceCash")),2));
		
		//应退合计（原币）= 应付替代款（原币）
		trade.setOcRefundSum(trade.getOpReplaceCash());
		//应退合计（本币）= 应付替代款（本币）
		trade.setHcRefundSum(trade.getHpReplaceCash());
		//数据方向
		trade.setDataDirection("-1");
		//退款日期
		trade.setRefundDate(sHReplaceOver);
	}
	
	
	/**获取补票数量(算上送股数量与缩股数量) STORY 1434*/
	private double getReplacAmount(ResultSet rs,ETFTradeSettleDetailRefBean trade) throws YssException,SQLException {
		double replaceAmount = rs.getDouble("FReplaceAmount");//申赎数量
		//若有送股则补票数量得加上送股数量
		//若有缩股则补票数量得减去缩股数量
		replaceAmount = YssD.sub(YssD.add(rs.getDouble("FReplaceAmount"),trade.getSumAmount()),
							trade.getDeflationAmount());
				
		return replaceAmount;
	}

	/**edit---huhuichao 2013-8-8 STORY  4276  博时：跨境ETF补充增加一类公司行动 */
	/** 获取总成本(单位成本*补票数量)+总派息+权证价值  shashijie ,2011-7-29 STORY 1434 */
	private double getAllCost(double unitcost, double replaceAmount,double interest,double warrantCost,double otherRight) {
		//单位成本*补票数量
		double cost = YssD.mul(unitcost, replaceAmount);
		//单位成本*补票数量+总派息+权证价值
		double allCost = YssD.add(cost, interest ,warrantCost,otherRight);
		return allCost;//YssD.round(allCost,2,false);
	}
	/**end---huhuichao 2013-8-8 STORY  4276*/
	
	/**shashijie,2011-7-18 ,计算单位原币成本,STORY 1434 
	 * @param TradePrice 交易实收价格
	 * @param replaceAmount 交易数量
	 * @param allBraketAmount 净申购(赎回)数量
	 * @param braketAmount 公共数量
	 * @param closingPrice 收盘价*/
	private double getUnitCost(double TradePrice, double replaceAmount,
			double allBraketAmount, double braketAmount,double closingPrice ) throws YssException {
		//单位成本(原币) = 实收金额/成交数量*(净数量-未补足数量) + (公共数量+未补足数量) * 收盘价 / 申赎替代数量
		double unitCost = 0;
		//未补足数量
		double noAmount = YssD.sub(allBraketAmount, replaceAmount);
		if (noAmount < 0) {
			noAmount = 0;
		}
		//净数量-未补足数量
		double reAmount = YssD.sub(allBraketAmount, noAmount);
		//实收金额/成交数量*买入(出)数量
		double unit = YssD.mul(YssD.div(TradePrice, replaceAmount),reAmount);
		//(公共数量+未补足数量) * 收盘价
		double cost = YssD.mul(YssD.add(braketAmount, noAmount), closingPrice);
		//总成本(原币) = 实收金额/成交数量*(净数量-未补足数量) + (公共数量+未补足数量) * 收盘价
		double allCost = YssD.add(unit, cost);
		//总成本(原币)/申赎替代总数量
		unitCost = YssD.div(allCost, YssD.add(allBraketAmount, braketAmount));
		return unitCost;
	}

	/**shashijie ,2011-8-3 设置交易结算明细对象 STORY 1434
	 * @param count 篮子数
	 * @param Rate 汇率
	 * @param FPrice 单位成本
	 * @param FAmount 替代数量
	 * @param dDate 申赎日期
	 * @param BS 申赎标示
	 * @param FSecurityCode 证券代码
	 * @param FPortCode 组合代码
	 * @param strNumDate 申请编号 */
	private void setETFTradeSettleDetailSH(ETFTradeSettleDetailBean detail, String strNumDate, String FPortCode,
			String FSecurityCode, String BS, Date dDate, double FAmount, double FPrice, double Rate,
			double count) {
		detail.setNum(strNumDate);//申请编号
		detail.setPortCode(FPortCode);//组合代码
		detail.setSecurityCode(FSecurityCode);//证券代码
		detail.setStockHolderCode(null);//股东代码
		detail.setBrokerCode(" ");//券商(清算编号)
		detail.setSeatCode(" ");//交易席位
		detail.setTradeNum(" ");//成交编号
		detail.setBs(BS);//台账类型:B申购S赎回
		detail.setBuyDate(dDate);//申赎日期
		//替代数量 = 股票篮文件中证券数量
		detail.setReplaceAmount(FAmount);
		detail.setBraketNum(count);//篮子数
		detail.setUnitCost(FPrice);//单位成本
		//汇率
		detail.setExchangeRate(Rate);
		//这里写死钆差补票
		detail.setMarktype("difference");//标志类型 difference = 钆差补票
		
	}

	/**shashijie ,2011-8-5 获取除权日汇率
	 * @param exchangeRate 汇率日期
	 * @param FTradeCury 交易货币*/
	/**shashijie 2012-12-10 STORY 3328  修改:根据基础参数来源获取汇率*/
	private double getRightRate(Date exchangeRate,String FTradeCury,String FbaseRateSrcBPCode,
			String FbaseRateBPCode,String FportRateSrcBPCode,String FportRateBPCode,String Fportcode) throws YssException {
		double rate = 1;
		try {
			/**shashijie 2012-12-10 STORY 3328 根据基础参数来源获取汇率*/
			//补票基础汇率
			double baseRate = paramSetAdmin.getExchangeRateValue(exchangeRate, FbaseRateSrcBPCode,
					FbaseRateBPCode,FTradeCury, Fportcode,
					YssOperCons.YSS_RATE_BASE);
			//补票组合汇率
			double portRate = paramSetAdmin.getExchangeRateValue(exchangeRate,FportRateSrcBPCode,
					FportRateBPCode,"",Fportcode,YssOperCons.YSS_RATE_PORT);
			rate = YssD.div(baseRate,portRate);
			/**end shashijie 2012-12-10 STORY 3328*/
			
		} catch (Exception e) {
			throw new YssException("获取汇率出错",e);
		} finally {

		}
		return rate;
	}

	/**shashijie,2011-8-5 处理权益数据*/
	private void treatRightsAndInterests(ETFTradeSettleDetailRefBean trade,
			ResultSet rs, java.util.Date dDate) throws YssException{
		try {
			Date dTempDate = YssFun.addDay(rs.getDate("FBuyDate"),1);
			String sKey = "";
			double dRemaidAmout = rs.getDouble("FReplaceAmount");
			while(YssFun.dateDiff(dTempDate, dDate) >= 0){
				sKey = new java.sql.Date(dTempDate.getTime()) + "\t" + rs.getString("fsecuritycode");
				/**add---huhuichao 2013-8-8 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
				if(dividendMap.containsKey(sKey) || 
						bonusShareMap.containsKey(sKey) || 
						rightsIssueMap.containsKey(sKey) || 
						deflationBonusMap.containsKey(sKey)||
						bonusShareMapAToB.containsKey(sKey)){
				/**end---huhuichao 2013-8-8 STORY  4276*/
					/**shashijie 2012-12-10 STORY 3328  修改:根据基础参数来源获取汇率*/
					//获取除权日的汇率:(除权日,交易货币,基础来源,基础来源字段,组合来源,组合来源字段,组合)
					trade.setExRightDate(dDate);//权益日期
					double rate = getRightRate(dDate,rs.getString("FTradeCury"),
							rs.getString("FbaseRateSrcSSCode"), rs.getString("FbaseRateSSCode"),
							rs.getString("FportRateSrcSSCode"), rs.getString("FportRateSSCode"),
							rs.getString("Fportcode"));
					trade.setRightRate(rate);//权益汇率
					/**end shashijie 2012-12-10 STORY 3328*/
					
					if(dividendMap.containsKey(sKey)){
						this.doDivdend(trade,dRemaidAmout,sKey);//处理分红权益
					}
					if(bonusShareMap.containsKey(sKey)){
						this.doBonusShare(trade,dRemaidAmout,sKey);//处理送股权益
					}
					if(rightsIssueMap.containsKey(sKey)){
						this.doRightIssue(trade,rs.getString("FPortCode"),dRemaidAmout,sKey,dDate);//处理配股权益
					}
					if(deflationBonusMap.containsKey(sKey)){
						this.doDeflationBonus(trade,dRemaidAmout,sKey);//处理缩股权益
					}
					/**add---huhuichao 2013-8-8 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
					if(bonusShareMapAToB.containsKey(sKey)){
						this.doBonusShareAToB(trade,rs.getString("FPortCode"),dRemaidAmout,sKey,dDate);//处理送股权益（A送B）
					}
					/**end---huhuichao 2013-8-8 STORY  4276*/
					dRemaidAmout = YssD.add(dRemaidAmout, YssD.sub(trade.getSumAmount(),trade.getDeflationAmount()));
				}
				dTempDate = YssFun.addDay(dTempDate,1);
			}

		} catch (Exception e) {
			 throw new YssException("处理权益数据错误\r\n" , e);
		}
	}

	/** shashijie,2011-8-7,是否是节假日,是返回true
	 * @param sHolidayCode 节假日代码
	 * @param dDate 日期*/
	public boolean isChinaHoliday(String sHolidayCode, Date dDate) throws YssException {
		EachExchangeHolidays holiday = new EachExchangeHolidays();//节假日代码
		holiday.setYssPub(pub);//设置pub
		//拼接参数：节假日代码+当天偏离天数+操作日期
		String sRowStr = sHolidayCode + "\t" + 0 + "\t" + YssFun.formatDate(dDate);
		//解析参数
		try {
			holiday.parseRowStr(sRowStr);
			Date theDate = YssFun.toSqlDate(holiday.getOperValue("getWorkDate"));//获取当天的最近一个工作日
			if(YssFun.dateDiff(theDate,dDate)!=0){//判断当天是不是工作日
				return true;
			}
		} catch (Exception e) {
			throw new YssException("判断节假日时出现错误\r\n",e);
		}
		return false;
	}

	/**shashijie ,2011-8-12,STORY 1434 ,根据TA交易数据的确认日查询出交易日期,这里的交易日期就是补票的申赎日期*/
	private Date getBuyDate(Date dDate) throws YssException {
		Date dBuyDate = null;//交易日,即申赎日
		StringBuffer buffer = new StringBuffer(100);
		ResultSet rs = null;
		try{
			buffer.append("SELECT  FTradeDate From ").append(pub.yssGetTableName("Tb_TA_Trade"))
				.append(" WHERE FConfimDate = ").append(dbl.sqlDate(dDate))//确认日
				.append(" AND FPortCode in ( ").append(operSql.sqlCodes(portCodes) + " ) ")//组合
				.append(" AND FSellType in ( ").append(operSql.sqlCodes("01,02") + " ) ")//销售类型(只查询申购赎回)
				.append(" AND FcheckState = 1");
			rs = dbl.openResultSet(buffer.toString());
			if (rs.next()){
				dBuyDate = rs.getDate("FTradeDate");
			}
		}catch (Exception e) {
			throw new YssException("获取申赎日期出错 ！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return dBuyDate;
	}
	
	/**汇总权益数据 shashijie ,2011-9-4 , STORY 1434 */
	private void setTradeSettleDetail(ETFTradeSettleDetailRefBean oldBean,ETFTradeSettleDetailRefBean tdr) {
		//送股
		double sumAmount = YssD.add(oldBean.getSumAmount(), tdr.getSumAmount());//总数量
		double realAmount = YssD.add(oldBean.getRealAmount(), tdr.getRealAmount());//实际数量
		//分红
		double interest = YssD.add(oldBean.getInterest(), tdr.getInterest());//派息（原币）
		double bbinterest = YssD.add(oldBean.getBbinterest(), tdr.getBbinterest());//派息(本币)
		//配股
		double warrantCost = YssD.add(oldBean.getWarrantCost(), tdr.getWarrantCost());//权证价值（原币）
		double bbwarrantCost = YssD.add(oldBean.getBbwarrantCost(), tdr.getBbwarrantCost());//权证价值(本币)
		/**add---huhuichao 2013-8-9 STORY  4276  博时：跨境ETF补充增加一类公司行动*/
		//送股（A送B）
		double otherRight = YssD.add(oldBean.getOtherRight(), tdr.getOtherRight());//其他权益（原币）
		double bbOtherRight = YssD.add(oldBean.getbBOtherRight(), tdr.getbBOtherRight());//其他权益(本币)
		/**end---huhuichao 2013-8-9 STORY  4276*/
		//缩骨
		double deflationAmount = YssD.add(oldBean.getDeflationAmount(), tdr.getDeflationAmount());//缩股数量

		tdr.setSumAmount(sumAmount);
		tdr.setRealAmount(realAmount);
		tdr.setInterest(interest);
		tdr.setBbinterest(bbinterest);
		tdr.setWarrantCost(warrantCost);
		tdr.setBbwarrantCost(bbwarrantCost);
		tdr.setDeflationAmount(deflationAmount);
		/**add---huhuichao 2013-8-9 STORY  4276  博时：跨境ETF补充增加一类公司行动*/
		tdr.setOtherRight(otherRight);
		tdr.setbBOtherRight(bbOtherRight);
		/**end---huhuichao 2013-8-9 STORY  4276*/
	}
	
	/**
	 * 获取权益信息
	 * 权益包括：T日（不含当日）至当前业务日期（含当日）境外开市日的权益。（T为申赎日期。）
	 * @param dBuyDate 申赎日期
	 * @param dDate 业务日期
	 * @param sPortCode 组合代码
	 * @throws YssException
	 */
	private void getExRightsInfo(java.util.Date dBuyDate,java.util.Date dDate,String sPortCode) throws YssException {
		dividendMap = new HashMap();
		bonusShareMap = new HashMap();
		rightsIssueMap = new HashMap();
		deflationBonusMap = new HashMap();
		/**add---huhuichao 2013-8-7 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
		bonusShareMapAToB = new HashMap();
		/**end---huhuichao 2013-8-7 STORY  4276*/
		ResultSet rs = null;
		StringBuffer buff = null;
		try{
			buff = new StringBuffer(200);
			//分红权益信息
			buff.append("select s.fsecuritycode,f.FAfterTaxRatio as FRatio,f.FDividendDate as FRightDate from ")
				.append(pub.yssGetTableName("Tb_Etf_Stocklist"))
				.append(" s join (select FPreTaxRatio,FAfterTaxRatio,FDividendDate,fsecuritycode from ")
				.append(pub.yssGetTableName("tb_data_dividend"))
				.append(" where FCheckState = 1 and FDividendDate > ").append(dbl.sqlDate(dBuyDate))
				.append(" and FDividendDate <= ").append(dbl.sqlDate(dDate))
				.append(") f on s.fsecuritycode = f.fsecuritycode")
				.append(" where s.fdate = ").append(dbl.sqlDate(dBuyDate))
				.append(" and s.FPortCode = ").append(dbl.sqlString(sPortCode));
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0, buff.length());
			while(rs.next()){
				String sKey = rs.getDate("FRightDate") + "\t" + rs.getString("fsecuritycode");
				dividendMap.put(sKey, rs.getDouble("FRatio"));
			}
			dbl.closeResultSetFinal(rs);
			/**add---huhuichao 2013-8-7 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
			//送股权益信息(A送A的情况)
			buff.append("select s.fsecuritycode,f.FAfterTaxRatio as FRatio,f.FExrightDate as FRightDate from ")
				.append(pub.yssGetTableName("Tb_Etf_Stocklist"))
				.append(" s join (select FAfterTaxRatio,FPreTaxRatio,FExrightDate,FSSecurityCode,ftsecuritycode from ")
				.append(pub.yssGetTableName("tb_data_bonusshare"))
				.append(" where FCheckState = 1 and FExrightDate > ").append(dbl.sqlDate(dBuyDate))
				.append(" and FExrightDate <= ").append(dbl.sqlDate(dDate))
				.append(" and FSSecurityCode=ftsecuritycode) f on s.fsecuritycode = f.fssecuritycode")
				.append(" where s.fdate = ").append(dbl.sqlDate(dBuyDate))
				.append(" and s.FPortCode = ").append(dbl.sqlString(sPortCode));
			/**end---huhuichao 2013-8-7 STORY  4276*/
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0, buff.length());
			while(rs.next()){
				String sKey = rs.getDate("FRightDate") + "\t" + rs.getString("fsecuritycode");
				bonusShareMap.put(sKey, rs.getDouble("FRatio"));
			}
			dbl.closeResultSetFinal(rs);
			
			//配股权益信息
			buff.append("select s.fsecuritycode,f.FTSecurityCode,f.FAfterTaxRatio as FRatio,")
				.append("f.FExrightDate as FRightDate,f.FRIPrice From ").append(pub.yssGetTableName("Tb_Etf_Stocklist"))
				.append(" s join (select FAfterTaxRatio,FPreTaxRatio,FExrightDate,FRIPrice,fsecuritycode,FTSecurityCode from ")
				.append(pub.yssGetTableName("tb_data_rightsissue"))
				.append(" where FCheckState = 1 and FExrightDate > ").append(dbl.sqlDate(dBuyDate))
				.append(" and FExrightDate <= ").append(dbl.sqlDate(dDate))
				.append(") f on s.fsecuritycode = f.fsecuritycode")
				.append(" where s.fdate = ").append(dbl.sqlDate(dBuyDate))
				.append(" and s.FPortCode = ").append(dbl.sqlString(sPortCode));
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0, buff.length());
			while(rs.next()){
				String sKey = rs.getDate("FRightDate") + "\t" + rs.getString("fsecuritycode");	
				//证券代码  + 权证代码  + 权益比例 + 配股价 
				rightsIssueMap.put(sKey, rs.getString("fsecuritycode") + "\t" 
												+ rs.getString("FTSecurityCode") + "\t" 
												+ rs.getDouble("FRatio") + "\t"
												+ rs.getDouble("FRIPrice"));
			}
			dbl.closeResultSetFinal(rs);
			
			//缩股权益信息
			buff.append("select s.fsecuritycode,f.FAfterTaxRatio as FRatio,f.FExrightDate as FRightDate from ")
				.append(pub.yssGetTableName("Tb_Etf_Stocklist"))
				.append(" s join (select FAfterTaxRatio,FPreTaxRatio,FExrightDate,FSSecurityCode from ")
				.append(pub.yssGetTableName("tb_data_deflationbonus"))
				.append(" where FCheckState = 1 and FExrightDate > ").append(dbl.sqlDate(dBuyDate))
				.append(" and FExrightDate <= ").append(dbl.sqlDate(dDate))
				.append(") f on s.fsecuritycode = f.fssecuritycode")
				.append(" where s.fdate = ").append(dbl.sqlDate(dBuyDate))
				.append(" and s.FPortCode = ").append(dbl.sqlString(sPortCode));
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0, buff.length());
			while(rs.next()){
				String sKey = rs.getDate("FRightDate") + "\t" + rs.getString("fsecuritycode");
				deflationBonusMap.put(sKey, rs.getDouble("FRatio"));
			}
			dbl.closeResultSetFinal(rs);
			
			/**add---huhuichao 2013-8-7 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
			//送股权益信息(A送B的情况)
			buff.append("select s.fsecuritycode,f.FAfterTaxRatio as" +
					" FRatio,f.FExrightDate as FRightDate,f.ftsecuritycode from ")
				.append(pub.yssGetTableName("Tb_Etf_Stocklist"))
				.append(" s join (select FAfterTaxRatio,FPreTaxRatio,FExrightDate,FSSecurityCode,ftsecuritycode from ")
				.append(pub.yssGetTableName("tb_data_bonusshare"))
				.append(" where FCheckState = 1 and FExrightDate > ").append(dbl.sqlDate(dBuyDate))
				.append(" and FExrightDate <= ").append(dbl.sqlDate(dDate))
				.append(" and FSSecurityCode<>ftsecuritycode) f on s.fsecuritycode = f.fssecuritycode")
				.append(" where s.fdate = ").append(dbl.sqlDate(dBuyDate))
				.append(" and s.FPortCode = ").append(dbl.sqlString(sPortCode));
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0, buff.length());
			while(rs.next()){
				String sKey = rs.getDate("FRightDate") + "\t" + rs.getString("fsecuritycode");
				//证券代码  + 权证代码  + 税后权益比例 
				bonusShareMapAToB.put(sKey, rs.getString("fsecuritycode") + "\t" 
						                + rs.getString("FTSecurityCode") + "\t" 
						                + rs.getDouble("FRatio"));
			}
			dbl.closeResultSetFinal(rs);
			/**end---huhuichao 2013-8-7 STORY  4276*/
		}catch (Exception e) {
			throw new YssException("获取权益信息出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}		
	}	
	
	/**
	 * 获取配股权益中正股和权证的行情
	 * @param portcode 组合代码
	 * @param valDate 业务日期
	 * @param securityCode 正股代码
	 * @param tSecurityCode 权证代码
	 * @param dPrice 正股行情
	 * @param dOPPrice 权证行情
	 * @throws YssException
	 */
	private double getRightsIssuePrice(String portcode, Date valDate,
			String securityCode, String tSecurityCode, double dExPrice) throws YssException {
		ResultSet rs = null;
		StringBuffer buff = null;
		double FPriceSub = 0;
		try{
			buff = new StringBuffer(200);
			buff.append("select a3.fsecuritycode, a3.fprice as FRiMktPrice ,a4.fprice as FCsMarketPrice,a3.FValdate,")
				.append("a3.fmarketvaluedate as FRiMktDate,a4.fmarketvaluedate as FMktValueDate from ")
				.append(pub.yssGetTableName("Tb_Data_PretValMktPrice"))
				.append(" a3 Left Join (select fsecuritycode, fprice, FValdate,fmarketvaluedate from ")
				.append(pub.yssGetTableName("Tb_Data_PretValMktPrice"))
				.append(" where FCheckstate = 1")
				.append(" and FPortCode = ").append(dbl.sqlString(portcode))
				.append(" and fvaldate = ").append(dbl.sqlDate(valDate))
				.append(" and fsecuritycode = ").append(dbl.sqlString(tSecurityCode))
				.append(" ) a4 on a4.FValdate = a3.FValdate")
				.append(" where a3.FCheckstate = 1")
				.append(" and a3.FPortCode = ").append(dbl.sqlString(portcode))
				.append(" and a3.fvaldate = ").append(dbl.sqlDate(valDate))
				.append(" and a3.fsecuritycode = ").append(dbl.sqlString(securityCode));
			rs = dbl.openResultSet(buff.toString());
			while(rs.next()){
				//如果权证有行情,且权证行情日期更接近当前业务日期
                if (rs.getDate("FMktValueDate") != null &&  
                        rs.getDate("FMktValueDate").compareTo(rs.getDate(
                            "FRiMktDate")) >= 0) { 
                	FPriceSub = rs.getDouble("FCsMarketPrice");//权证行情
                } else {
                	FPriceSub = YssD.sub(rs.getDouble("FRiMktPrice"),dExPrice); //正股收盘价 - 配股价
                	if(FPriceSub < 0){
                		FPriceSub = 0;
                	}
                }
			}
			return FPriceSub;
		}catch (Exception e) {
			throw new YssException("获取权证行情出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}	
	}
	
	/**
	 * add by huhuichao 2013-08-08 story 博时：跨境ETF补充增加一类公司行动 获取配股权益中正股和权证的行情
	 * 
	 * @param portcode
	 *            组合代码
	 * @param dDate
	 *            业务日期
	 * @param securityCode
	 *            目标代码
	 * @param tSecurityCode
	 *            标的代码
	 * @return double FPriceSub 标的证券行情价
	 * @throws YssException
	 */
	private double getTSecurityCodePrice(String portCode, Date dDate,
			String securityCode, String tSecurityCode) throws YssException {
		ResultSet rs = null;
		StringBuffer buff = null;
		double FPriceSub = 0;
		try {
			buff = new StringBuffer(200);
			buff
					.append(
							"select a3.fsecuritycode, a3.fprice as FRiMktPrice ,a4.fprice as FCsMarketPrice,a3.FValdate,")
					.append(
							"a3.fmarketvaluedate as FRiMktDate,a4.fmarketvaluedate as FMktValueDate from ")
					.append(pub.yssGetTableName("Tb_Data_PretValMktPrice"))
					.append(
							" a3 Left Join (select fsecuritycode, fprice, FValdate,fmarketvaluedate from ")
					.append(pub.yssGetTableName("Tb_Data_PretValMktPrice"))
					.append(" where FCheckstate = 1").append(
							" and FPortCode = ")
					.append(dbl.sqlString(portCode)).append(" and fvaldate = ")
					.append(dbl.sqlDate(dDate)).append(" and fsecuritycode = ")
					.append(dbl.sqlString(tSecurityCode)).append(
							" ) a4 on a4.FValdate = a3.FValdate").append(
							" where a3.FCheckstate = 1").append(
							" and a3.FPortCode = ").append(
							dbl.sqlString(portCode)).append(
							" and a3.fvaldate = ").append(dbl.sqlDate(dDate))
					.append(" and a3.fsecuritycode = ").append(
							dbl.sqlString(securityCode));
			rs = dbl.openResultSet(buff.toString());
			while (rs.next()) {
				FPriceSub = rs.getDouble("FCsMarketPrice");// 标的证券行情
			}
			return FPriceSub;
		} catch (Exception e) {
			throw new YssException("获取权证行情出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
}