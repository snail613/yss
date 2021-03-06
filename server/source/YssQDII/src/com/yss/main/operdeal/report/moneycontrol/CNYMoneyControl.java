package com.yss.main.operdeal.report.moneycontrol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.yss.base.BaseAPOperValue;

import com.yss.dsub.YssPub;
import com.yss.main.dao.IClientOperRequest;
import com.yss.main.operdeal.report.moneycontrol.pojo.CNYMoneyControlBean;
import com.yss.main.operdeal.report.moneycontrol.pojo.MoneyControlBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/***
 * 20100810 MS01461 QDV4华安2010年7月20日01_A
 * 人民币头寸表生成报表数据类
 * @author yanghaiming
 *
 */
public class CNYMoneyControl extends BaseAPOperValue implements
		IClientOperRequest {

	private String portcode = "";// 组合代码
	private Date checkDate;// 报表生成日期
	private Date endDate;// 预测数据截止日期
	private int checkType;// 报表生成方式（0表示预估，1表示确认）
	private String dateWhereSql = "";// 保存查询现金账户的sql
	private ArrayList ary = new ArrayList();// 存放现金账户开户行和币种
	HashMap controlDateParam = new HashMap(128);// 存放人民币头寸数据
	HashMap rateHash = new HashMap(32);//存放T日各币种的汇率数据
	//HashMap ydRateHash = new HashMap(32);//存放T-1日各币种的汇率数据
	HashMap moneyHash = new HashMap(128);//严格按照controlDateParam的key值进行排序，金额根据T-1日至T+10日用\t进行分割
	private String moneyStr = "";//用于拼接各款项T-1日至T+10日的金额
	private CNYMoneyControlBean cnyMoneyControlBean = null;
	private String dateStr = "";
	private double scale = 0;//计算净资产时的比率add by yanghaiming 20100906 MS01688  QDV4华安2010年09月03日01_AB   
	private double fSKIWBanlance = 0;//用于保存T-1日恒生余额的数据
	//controlDateParam中0-40分别表述以下数据
 	//RMB ASSET//人民币可调度部位//BB可用头寸//资金划拨//换汇//T-1日-T+10日//一年内到期政府债及央票(余额)
	//股指期货(初试保证金)//银行存款//应收申购款//应付赎回款//当日人民币应收股债款//当日人民币应付股债款
	//欧美股债应收应付款预估)//当日外币应收股债款//当日外币应付股债款
	//股指期货(初试保证金应收应付)//净资产//申购款（T+3）//赎回款（T+8）//预估净资产
	//一年内到期政府债及央票比例//银行存款比例//現金比例//人民币股票买进金额//人民币股票卖出金额
	//国内固定收益买入总金额//国内固定收益卖出总金额//海外市场（除中国）固定收益买入总金额
	//海外市场（除中国）固定收益卖出总金额//亚太市场买入//亚太市场卖出//欧美市场买入//欧美市场卖出
	//欧美市场预估数据//国外股票变动小计//股票//持股比例//固定收益//固定收益比例//股指期货(初试保证金)比例//比率合计
	private int searchDays;// 查询天数 add by zhaoxianlin 
	
	
	public String checkRequest(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String doOperation(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String buildRowStr() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getOperValue(String sType) throws YssException {
		String[] confirms = null;
		confirms = sType.split("\b\f\b");
		String strResult = ""; 
		this.checkDate = YssFun.toDate(confirms[0].toString());
		this.portcode = confirms[1].toString();
		this.checkType = Integer.parseInt(confirms[2].toString());
		this.scale = Double.parseDouble(confirms[3].toString());
		this.searchDays = Integer.parseInt(confirms[5].toString());//add by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整
		if (confirms[4].equalsIgnoreCase("check")) {
			this.dateWhereSql = " select max(fstartdate) from  "
				+ pub.yssGetTableName("tb_para_cashaccount")
				+ " where fcheckstate=1 and fportcode = " + dbl.sqlString(this.portcode) + " and FBankCode =";
			ary.add("HSBC-USD");
			ary.add("HSBC-HKD");
			ary.add("HSBC-SGD");
			ary.add("HSBC-TWD");
			//add by fangjiang 2011.05.07 STORY #867
			ary.add("HSBC-GBP");
			ary.add("HSBC-KRW");
			ary.add("HSBC-IDR");
			ary.add("HSBC-MYR");
			ary.add("HSBC-PHP");
			ary.add("HSBC-THB");
			//----------------
			ary.add("ICBC-USD");
			ary.add("ICBC-HKD");
			ary.add("ICBC-CNY");
			
			// ADD BY LEE 2011-5-19
			// START ADD
			ary.add("BOCHK-USD");
			ary.add("BOCHK-HKD");
			ary.add("BOCHK-SGD");
			ary.add("BOCHK-TWD");
			ary.add("BOCHK-GBP");
			ary.add("BOCHK-KRW");
			ary.add("BOCHK-IDR");
			ary.add("BOCHK-MYR");
			ary.add("BOCHK-PHP");
			ary.add("BOCHK-THB");
			ary.add("BOC-USD");
			ary.add("BOC-HKD");
			ary.add("BOC-CNY");
			// END ADD

			// ADD BY LEE 2011-12-13
			// START ADD
			ary.add("SSC-USD");
			ary.add("SSC-HKD");
			ary.add("SSC-GBP");
			ary.add("SSC-AUD");
			ary.add("SSC-CAD");
			ary.add("SSC-EUR");
			ary.add("CCB-USD");
			ary.add("CCB-HKD");
			ary.add("CCB-CNY");
			// END ADD
			//这些信息每条记录都一样
			cnyMoneyControlBean = new CNYMoneyControlBean();
			cnyMoneyControlBean.setFdate(this.checkDate);
			cnyMoneyControlBean.setFportcode(this.portcode);
			cnyMoneyControlBean.setReporttype(this.checkType);
			for (int i = 0; i<=45;i++){//edit by yanghaiming 20101111 增加到43行数据   modified by zhaoxianlin  20130129 STORY #2913增加到45行数据
			  moneyHash.put(i+"","0\t");//此处先将hashmap创建完毕，之后可以直接取值赋值
			}
			//在处理之前先获取各币种的汇率
			getRate();
			//getYDRate();
			//在处理之前先删除同一天同一组合相同状态的数据
			beforeCheck(this.checkDate);
			//处理T-1日的数据
			getStockBalance();
			//在循环中处理T日之后数据
			//将T-1日-T+10日的数据赋值
			Calendar c = Calendar.getInstance();
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
			this.endDate = YssFun.addDay(this.checkDate,-1);
			c.setTime(endDate);
			if (c.get(Calendar.DAY_OF_WEEK) == 1) {// 如果为星期日，则日期再减2
				endDate = YssFun.addDay(endDate, -2);
			}
			dateStr += "T-1日(" + format.format(this.endDate) + ")\t";
			//for (int i = 0; i <= 10; i++) {// 需生成T至T+10日的数据
			for (int i = 0; i <=searchDays-1; i++) {//  modified by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整
				if (i == 0) {// 保存日期对应的T+?日
					this.endDate = this.checkDate;
					dateStr += "T日(" + format.format(this.endDate) + ")\t";
				} else {
					dateStr += "T+" + i + "日(" + format.format(this.endDate) + ")\t";
				}
				//此处调用生成T到T+10日数据的方法
				getOtherAmount(i,this.endDate);
				//if(i != 10){//最后一天不对日期进行处理
				if(i != searchDays){//最后一天不对日期进行处理  //modified by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整
					endDate = YssFun.addDay(endDate, 1);// 每天执行完日期加1
					c.setTime(endDate);
					if (c.get(Calendar.DAY_OF_WEEK) == 7) {// 如果为星期六，则日期再加2
						endDate = YssFun.addDay(endDate, 2);
					}
				}
			}
			moneyHash.put("5", dateStr);
			//以上已经将整张报表的数据全部保存在moneyStr
			checkInHash();
			checkIn();//插入数据
		}else if (confirms[4].equalsIgnoreCase("checkstate")){
			strResult = isConfirm();
		}
		
		return strResult;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		// TODO Auto-generated method stub

	}
	
	//获取T日各币种的汇率数据，保存至hashmap中
	private void getRate() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		int countFlag = 0;
		String errorMessage = "";
		try {
			errorMessage = "【执行查询汇率数据时出错......】";
			//modify by fangjiang 2011.05.07 STORY #867
			strSql = "select * from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'USD' and FEXRATEDATE = (select max(FEXRATEDATE) from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'USD' and FEXRATEDATE <= " 
				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
				+ ") union select * from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'HKD' and FEXRATEDATE = (select max(FEXRATEDATE) from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'HKD' and FEXRATEDATE <= " 
				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
				+ ") union select * from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'SGD' and FEXRATEDATE = (select max(FEXRATEDATE) from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'SGD' and FEXRATEDATE <= " 
				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
				+ ") union select * from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'TWD' and FEXRATEDATE = (select max(FEXRATEDATE) from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'TWD' and FEXRATEDATE <= " 
				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate)) 
				+ ") union select * from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'GBP' and FEXRATEDATE = (select max(FEXRATEDATE) from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'GBP' and FEXRATEDATE <= " 
				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
				+ ") union select * from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'KRW' and FEXRATEDATE = (select max(FEXRATEDATE) from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'KRW' and FEXRATEDATE <= " 
				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
				+ ") union select * from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'IDR' and FEXRATEDATE = (select max(FEXRATEDATE) from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'IDR' and FEXRATEDATE <= " 
				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
				+ ") union select * from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'MYR' and FEXRATEDATE = (select max(FEXRATEDATE) from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'MYR' and FEXRATEDATE <= " 
				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
				+ ") union select * from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'PHP' and FEXRATEDATE = (select max(FEXRATEDATE) from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'PHP' and FEXRATEDATE <= " 
				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
				+ ") union select * from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'THB' and FEXRATEDATE = (select max(FEXRATEDATE) from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'THB' and FEXRATEDATE <= " 
				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
				// ADD BY LEE 2011-12-13
				// START ADD
				+ ") union select * from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'EUR' and FEXRATEDATE = (select max(FEXRATEDATE) from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'EUR' and FEXRATEDATE <= " 
				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
				
				+ ") union select * from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'CAD' and FEXRATEDATE = (select max(FEXRATEDATE) from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'CAD' and FEXRATEDATE <= " 
				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
				
				+ ") union select * from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'AUD' and FEXRATEDATE = (select max(FEXRATEDATE) from "
				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'AUD' and FEXRATEDATE <= " 
				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
				// END ADD
				+ ")";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				rateHash.put(rs.getString("FFCURYCODE"), rs.getString("FEXRATE"));
				countFlag ++;
			}
			
			// EDIT BY LEE    countFlag是指币种个数，现增加了3个币种 EUR CAD AUD所以从10改为13
			//if(countFlag != 13){ 
			//	errorMessage = "【汇率数据不完整，请导入汇率后再生成人民币头寸表......】";
			//	throw new YssException();
			//}
			//----------------
		} catch (Exception e) {
			throw new YssException(errorMessage);
		} finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
//	//获取T-1日汇率
//	private void getYDRate() throws YssException{
//		String strSql = "";
//		ResultSet rs = null;
//		int countFlag = 0;
//		String errorMessage = "";
//		try {
//			errorMessage = "【执行查询汇率数据时出错......】";
//			strSql = "select * from "
//				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
//				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'USD' and FEXRATEDATE = (select max(FEXRATEDATE) from "
//				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'USD' and FEXRATEDATE < " 
//				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
//				+ ") union (select * from "
//				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
//				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'HKD' and FEXRATEDATE = (select max(FEXRATEDATE) from "
//				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'HKD' and FEXRATEDATE < " 
//				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
//				+ ")) union (select * from "
//				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
//				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'SGD' and FEXRATEDATE = (select max(FEXRATEDATE) from "
//				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'SGD' and FEXRATEDATE < " 
//				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
//				+ ")) union (select * from "
//				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
//				+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'TWD' and FEXRATEDATE = (select max(FEXRATEDATE) from "
//				+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'TWD' and FEXRATEDATE < " 
//				+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate)) + "))";
//			rs = dbl.openResultSet(strSql);
//			while(rs.next()){
//				ydRateHash.put(rs.getString("FFCURYCODE"), rs.getString("FEXRATE"));
//				countFlag ++;
//			}
//			if(countFlag != 4){
//				errorMessage = "【T-1日汇率数据不完整，请导入汇率后再生成外币头寸表......】";
//				throw new YssException();
//			}
//		} catch (Exception e) {
//			throw new YssException(errorMessage);
//		} finally{
//			dbl.closeResultSetFinal(rs);
//		}
//	}
	
	// 生成头寸表之前先将相关数据删除
	private void beforeCheck(Date date) throws YssException {
		String strSql = "";
		try {
			strSql = "delete from "
					+ pub.yssGetTableName("TB_DATA_CNYMONEYCONTROL")
					+ " where FPORTCODE = " + dbl.sqlString(this.portcode)
					+ " and FDATE = " + dbl.sqlDate(YssFun.toSqlDate(date))
					+ " and FREPORTTYPE = " + this.checkType;
			dbl.executeSql(strSql);
		} catch (Exception e) {
			throw new YssException("【执行人民币管控数据删除时出错......】");
		}
	}
	
	//T-1日库存中获取的数据
	private void getStockBalance() throws YssException {
		ResultSet rs = null;
        String query = "";
        double balance;
        double balance1;//只用于保存T-1日定存余额
        double percent1;
        double percent2;
        double percent3;
        double percent4;//股指期货(保证金)比例
		try {
			query = "select fstoragedate,FAccBalance as FMONEY from " 
				+ pub.yssGetTableName("tb_stock_cash")
				+ " where fcheckstate=1 and Fportcode = " + dbl.sqlString(this.portcode) + " and fstoragedate = (select max(fstoragedate) from "
				+ pub.yssGetTableName("tb_stock_cash") + " where fcheckstate = 1 and fstoragedate < "
				+ dbl.sqlDate(this.checkDate) + " and fportcode = " + dbl.sqlString(this.portcode)
				+ ") and fcashacccode=(select fcashacccode from "
				+ pub.yssGetTableName("tb_para_cashaccount") + " where fportcode = " + dbl.sqlString(this.portcode)
//				+ " and fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portcode) + " and FBankCode = 'ICBC' and FCuryCode = 'CNY' and FSUBACCTYPE = '0101')";
				+ " and fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portcode) + " and FBankCode in('ICBC','BOC'," +
				// ADD BY LEE 2011-12-13	
				// START ADD 
				"'CCB'" +
				// END ADD 
						") and FCuryCode = 'CNY' and FSUBACCTYPE = '0101')";
			rs = dbl.openResultSet(query);
			while (rs.next()){
				moneyHash.put("0", YssFun.formatNumber(rs.getDouble("FMONEY"),"#,##0.##") + "\t");
				moneyHash.put("1", YssFun.formatNumber(rs.getDouble("FMONEY"),"#,##0.##") + "\t");//RMB ASSET和人民币可调度部位  的T-1日金额处理完毕
			}
			dbl.closeResultSetFinal(rs);
			query = "select round(a.FPortMarketValue,2) as TotalValue,a.FNAVDATE,round(b.FPortMarketValue,2) as EQVALUE,round(c.FPortMarketValue,2) as FIVALUE,round(d.FPortMarketValue) as FIYEARVALUE from (select FPortMarketValue,FNAVDATE from "
				+ pub.yssGetTableName("tb_data_navdata") + " where FORDERCODE = 'Total1' and FRETYPECODE = 'Total'"
				+ " and FINVMGRCODE = 'total' and FPORTCODE = " + dbl.sqlString(this.portcode)
				+ " and FNAVDATE = (select max(FNAVDATE) from " + pub.yssGetTableName("tb_data_navdata")
				+ " where FORDERCODE = 'Total1' and FPORTCODE = " + dbl.sqlString(this.portcode) + " and FNAVDATE < " + dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
				+ ")) a left join (select FPortMarketValue,FNAVDATE from " 
				+ pub.yssGetTableName("tb_data_navdata") + " where FORDERCODE = 'EQ' and FRETYPECODE = 'Security'"
				+ " and FINVMGRCODE = 'total' and FPORTCODE = " + dbl.sqlString(this.portcode)
				+ ") b on a.FNAVDATE = b.FNAVDATE left join (select FPortMarketValue,FNAVDATE from " 
				+ pub.yssGetTableName("tb_data_navdata") + " where FORDERCODE = 'FI' and FRETYPECODE = 'Security'"
				+ " and FINVMGRCODE = 'total' and FPORTCODE = " + dbl.sqlString(this.portcode) + ") c on a.FNAVDATE = c.FNAVDATE"
				+ " left join (select sum(FPortMarketValue) as FPortMarketValue,FNAVDATE from "
				+ pub.yssGetTableName("tb_data_navdata") + " where FRETYPECODE = 'Security' and FGRADETYPE1 = 'FI' and FGRADETYPE5 is not null"
				+ " and FGRADETYPE6 is null and FPORTCODE = " + dbl.sqlString(this.portcode) + " and FGRADETYPE5 in (select FSECURITYCODE from " + pub.yssGetTableName("Tb_Para_FixInterest")
				+ " where FInsCashDate < " + dbl.sqlDate(YssFun.addYear(this.checkDate,1)) + " and FInsCashDate > "
				+ dbl.sqlDate(this.checkDate) + ") group by FNAVDATE) d on a.FNAVDATE = d.FNAVDATE";	
			rs = dbl.openResultSet(query);
			while (rs.next()){
				moneyHash.put("19", YssFun.formatNumber(rs.getDouble("TotalValue"),"#,##0.##") + "\t");//T-1日的净值
				moneyHash.put("38", YssFun.formatNumber(rs.getDouble("EQVALUE"),"#,##0.##") + "\t");//T-1日的股票
//				moneyHash.put("40", YssFun.formatNumber(rs.getDouble("FIVALUE"),"#,##0.##") + "\t");//T-1日的债券
				moneyHash.put("6", YssFun.formatNumber(rs.getDouble("FIYEARVALUE"),"#,##0.##") + "\t");
			}
			dbl.closeResultSetFinal(rs);
			//股指期货(初试保证金)   二期需求增加	
			query = "select sum(FPORTMARKETVALUE) as FMONEY from "
						+ pub.yssGetTableName("tb_data_navdata")
						+ " where FNAVDATE = (select max(FNAVDATE) from "
						+ pub.yssGetTableName("tb_data_navdata") + " where FNAVDATE < "
						+ dbl.sqlDate(this.checkDate) + " and FPORTCODE = " + dbl.sqlString(this.portcode)
						+ ") and FRETYPECODE = 'Cash' and FINVMGRCODE = 'total' and FPORTCODE = " + dbl.sqlString(this.portcode) 
						+ " and FKEYCODE in (select fcashacccode from "
						+ pub.yssGetTableName("tb_para_cashaccount") + " where fcheckstate = 1" + " and FPortCode = " + dbl.sqlString(this.portcode)
						+ " and FAccType = '03' and FSubAccType = '0301' group by fcashacccode)";
			rs = dbl.openResultSet(query);
			balance = 0;
			while (rs.next()){
				balance += rs.getDouble("FMONEY");
			}
			dbl.closeResultSetFinal(rs);
			moneyHash.put("8", YssFun.formatNumber(balance,"#,##0.##") + "\t");//T-1日的股指期货（初始保证金）
			
			//定存   二期需求增加	
			query = "select sum(FPORTMARKETVALUE) as FMONEY from "
						+ pub.yssGetTableName("tb_data_navdata")
						+ " where FNAVDATE = (select max(FNAVDATE) from "
						+ pub.yssGetTableName("tb_data_navdata") + " where FNAVDATE < "
						+ dbl.sqlDate(this.checkDate) + " and FPORTCODE = " + dbl.sqlString(this.portcode)
						+ ") and FRETYPECODE = 'Cash' and FINVMGRCODE = 'total' and FPORTCODE = " + dbl.sqlString(this.portcode) 
						+ " and FKEYCODE in (select fcashacccode from "
						+ pub.yssGetTableName("tb_para_cashaccount") + " where fcheckstate = 1" + " and FPortCode = " + dbl.sqlString(this.portcode)
						+ " and FAccType = '01' and FSubAccType = '0102' group by fcashacccode)";
			rs = dbl.openResultSet(query);
			balance1 = 0;
			while (rs.next()){
				balance1 += rs.getDouble("FMONEY");
			}
			dbl.closeResultSetFinal(rs);
			moneyHash.put("7", YssFun.formatNumber(balance1,"#,##0.##") + "\t");//T-1日的定存
			
			//add by yanghaiming 20101111  T-1日 固定收益  = T-1日债券的市值+T-1日定存余额
			balance = YssFun.toDouble(moneyHash.get("6").toString().split("\t")[0]) + balance1; 
			moneyHash.put("40", YssFun.formatNumber(balance,"#,##0.##") + "\t");//T-1日 固定收益
			//add by yanghaiming 20101111  T-1日 固定收益
			//modify by fangjiang 2011.05.07 STORY #867
			query = "select "
				+ dbl.sqlDate(this.checkDate)
				+ "-1 as fdate,' '  fweekday,-2 as FINOUT ,a1.FMONEY as FHSBCUSD,a2.FMONEY as FHSBCHKD,a3.FMONEY as FHSBCSGD,"
				+ " a4.FMONEY as FHSBCTWD, a5.FMONEY as FHSBCGBP,a6.FMONEY as FHSBCKRW,a7.FMONEY as FHSBCIDR,a8.FMONEY as FHSBCMYR,a9.FMONEY as FHSBCPHP,a10.FMONEY as FHSBCTHB," 
				+ " a11.FMONEY as FICBCUSD,a12.FMONEY as FICBCHKD,a13.FMONEY as FICBCCNY," 
				// ADD BY LEE 2011-5-19
				// START BY
				+ " a14.FMONEY as FBOCHKUSD,a15.FMONEY as FBOCHKHKD,a16.FMONEY as FBOCHKSGD,"
				+ " a17.FMONEY as FBOCHKTWD, a18.FMONEY as FBOCHKGBP,a19.FMONEY as FBOCHKKRW,"
				+ " a20.FMONEY as FBOCHKIDR,a21.FMONEY as FBOCHKMYR,a22.FMONEY as FBOCHKPHP,a23.FMONEY as FBOCHKTHB," 
				+ " a24.FMONEY as FBOCUSD,a25.FMONEY as FBOCHKD,a26.FMONEY as FBOCCNY,"
				// END ADD

				// ADD BY LEE 2011-12-13
				// START BY
				+ " a27.FMONEY as FSSCUSD,a28.FMONEY as FSSCHKD,a29.FMONEY as FSSCGBP,"
				+ " a30.FMONEY as FSSCAUD,a31.FMONEY as FSSCCAD,a32.FMONEY as FSSCEUR,"
				+ " a33.FMONEY as FCCBUSD,a34.FMONEY as FCCBHKD,a35.FMONEY as FCCBCNY "
				// END ADD
				+" from" 
				// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码	
				+ " (select fportcode from " + pub.yssGetTableName("tb_para_portfolio") + " where fcheckstate=1 and  fportcode = "
				+ dbl.sqlString(this.portcode) + " ) a ";
			//end by lidaolong
			//end by fangjiang 2011.05.07 STORY #867
			for (int i = 1; i <= ary.size(); i++) {//循环中，拼接各个现金账户对应的T-1日的市值
				query += " left join "
					
					
						+ " (select FPORTMARKETVALUE as FMONEY,Fportcode from "
						+ pub.yssGetTableName("tb_data_navdata")
						+ " where FNAVDATE = (select max(FNAVDATE) from "
						+ pub.yssGetTableName("tb_data_navdata") + " where FNAVDATE < "
						+ dbl.sqlDate(this.checkDate) + " and FPORTCODE = " + dbl.sqlString(this.portcode)
						+ ") and FRETYPECODE = 'Cash' and FINVMGRCODE = 'total' and FPORTCODE = " + dbl.sqlString(this.portcode) + " and FKEYCODE=(select fcashacccode from "
						+ pub.yssGetTableName("tb_para_cashaccount")
						+ " where fcheckstate = 1" + " and fportcode = " + dbl.sqlString(this.portcode) + " and FSUBACCTYPE = '0101' and FBankCode = "
						+ dbl.sqlString(ary.get(i - 1).toString().split("-")[0]) + " and FCuryCode = " + dbl.sqlString(ary.get(i - 1).toString().split("-")[1])
//						+ " and fstartdate = (" + this.dateWhereSql
//						+ dbl.sqlString(ary.get(i - 1).toString().split("-")[0]) + " and FCuryCode = " + dbl.sqlString(ary.get(i - 1).toString().split("-")[1])
//						+ " )
						+ ")) a" + i
						+ " on a.fportcode = a" + i + ".fportcode ";
			}
			
			
			rs = dbl.openResultSet(query);
			while(rs.next()){
				balance = YssFun.roundIt(rs.getDouble("FHSBCUSD"),2)// * YssFun.toDouble(ydRateHash.get("USD").toString()),2)
						+ YssFun.roundIt(rs.getDouble("FHSBCHKD"),2)// * YssFun.toDouble(ydRateHash.get("HKD").toString()),2)
						+ YssFun.roundIt(rs.getDouble("FHSBCSGD"),2)// * YssFun.toDouble(ydRateHash.get("SGD").toString()),2)
						
						// ADD BY LEE 2011-5-19  
						// START ADD 
						+ YssFun.roundIt(rs.getDouble("FBOCHKUSD"),2)
						+ YssFun.roundIt(rs.getDouble("FBOCHKHKD"),2)
						+ YssFun.roundIt(rs.getDouble("FBOCHKSGD"),2)
						+ YssFun.roundIt(rs.getDouble("FBOCHKGBP"),2)
						+ YssFun.roundIt(rs.getDouble("FBOCHKKRW"),2)
						+ YssFun.roundIt(rs.getDouble("FBOCHKIDR"),2)
						+ YssFun.roundIt(rs.getDouble("FBOCHKMYR"),2)
						+ YssFun.roundIt(rs.getDouble("FBOCHKPHP"),2)
						+ YssFun.roundIt(rs.getDouble("FBOCHKTHB"),2)
						+ YssFun.roundIt(rs.getDouble("FBOCHKMYR"),2)
						+ YssFun.roundIt(rs.getDouble("FBOCCNY"),2)
						+ YssFun.roundIt(rs.getDouble("FBOCHKD"),2)
						+ YssFun.roundIt(rs.getDouble("FBOCUSD"),2)
						
						// END ADD

						// ADD BY LEE 2011-12-13  
						// START ADD 
						+ YssFun.roundIt(rs.getDouble("FSSCUSD"),2)
						+ YssFun.roundIt(rs.getDouble("FSSCHKD"),2)
						+ YssFun.roundIt(rs.getDouble("FSSCGBP"),2)
						+ YssFun.roundIt(rs.getDouble("FSSCAUD"),2)
						+ YssFun.roundIt(rs.getDouble("FSSCCAD"),2)
						+ YssFun.roundIt(rs.getDouble("FSSCEUR"),2)
						+ YssFun.roundIt(rs.getDouble("FCCBUSD"),2)
						+ YssFun.roundIt(rs.getDouble("FCCBHKD"),2)
						+ YssFun.roundIt(rs.getDouble("FCCBCNY"),2)
						
						+ YssFun.roundIt(rs.getDouble("FHSBCTWD"),2)// * YssFun.toDouble(ydRateHash.get("TWD").toString()),2)
						+ YssFun.roundIt(rs.getDouble("FICBCUSD"),2)// * YssFun.toDouble(ydRateHash.get("USD").toString()),2)
						+ YssFun.roundIt(rs.getDouble("FICBCHKD"),2)// * YssFun.toDouble(ydRateHash.get("HKD").toString()),2)
						+ YssFun.roundIt(rs.getDouble("FICBCCNY"),2);
						
//	cyb	20100909		- balance//二期需求需减去股指期货（初始保证金）
//	cyb	20100909		- balance1;//T-1日的定存
				moneyHash.put("9", YssFun.formatNumber(balance,"#,##0.##") + "\t");//T-1日的银行存款
			}
			dbl.closeResultSetFinal(rs);
			
			
			
			query = "select FMONEY1,FNUM from " + pub.yssGetTableName("tb_data_cnymoneycontrol")
					+ " where FNUM in (26,27,28,29,30,31,32,33,34,35) and fportcode = " + dbl.sqlString(this.portcode) 
						+ " and FDATE = ";//去掉申购款，赎回款 即20.21  增加fprotcode判定条件 Lee 2011-5-18
			Calendar c = Calendar.getInstance();
			c.setTime(this.checkDate);
			if(c.get(Calendar.DAY_OF_WEEK) == 2){//如果是星期一则取周五的日期
				query += dbl.sqlDate(YssFun.addDay(this.checkDate,-3));
			}else{
				query += dbl.sqlDate(YssFun.addDay(this.checkDate,-1));
			}
			query += "  and FREPORTTYPE = " + this.checkType;
			rs = dbl.openResultSet(query);
			while (rs.next()){
				moneyHash.put(rs.getString("FNUM"), rs.getString("FMONEY1") + "\t");
			}
			dbl.closeResultSetFinal(rs); //modify by fangjiang 2011.05.07 STORY #867
			balance = YssFun.roundIt(YssFun.toDouble(moneyHash.get("19").toString().split("\t")[0])
					+ YssFun.toDouble(moneyHash.get("20").toString().split("\t")[0])
					- YssFun.toDouble(moneyHash.get("21").toString().split("\t")[0])//计算T-1日预估净资产
            /** add by zhaoxianlin  20130129 STORY #2913 头寸管控表TA数据的读入接口和人民币外币头寸管控表需要修改--start-*/ 
					+ YssFun.toDouble(moneyHash.get("44").toString().split("\t")[0])
					- YssFun.toDouble(moneyHash.get("45").toString().split("\t")[0]),2);//计算T-1日预估净资产
			/** add by zhaoxianlin  20130129 STORY #2913 头寸管控表TA数据的读入接口和人民币外币头寸管控表需要修改--end-*/ 
			moneyHash.put("22", YssFun.formatNumber(balance,"#,##0.##") + "\t");
			percent1 = YssFun.roundIt(YssFun.toDouble(moneyHash.get("6").toString().split("\t")[0])*100/balance,2);
			percent2 = YssFun.roundIt(YssFun.toDouble(moneyHash.get("9").toString().split("\t")[0])*100/balance,2);
			percent3 = percent1 + percent2;
			moneyHash.put("23", YssFun.roundIt(percent1,2) + "%\t");//T-1日一年内到期政府债及央票比例
			moneyHash.put("24", YssFun.roundIt(percent2,2) + "%\t");//T-1日银行存款比例
			moneyHash.put("25", YssFun.roundIt(percent3,2) + "%\t");//T-1日现金比例
			percent3 = percent2;//add by yanghaiming 20100820 比率合计计算方式变更，现金比例改为银行存款比例
			percent1 = YssFun.roundIt(YssFun.toDouble(moneyHash.get("38").toString().split("\t")[0])*100/balance,2);//T-1日持股比例
			//edit by yanghaiming 20101111 QDV4华安2010年10月08日02_AB 
			percent2 = YssFun.roundIt(YssFun.toDouble(moneyHash.get("40").toString().split("\t")[0])*100/balance,2);//T-1日固定收益比例
			percent4 = YssFun.roundIt(YssFun.toDouble(moneyHash.get("8").toString().split("\t")[0])*100/balance,2);//T-1日股指期货(保证金)比例
			moneyHash.put("39", YssFun.roundIt(percent1,2) + "%\t");//T-1日持股比例
			moneyHash.put("41", YssFun.roundIt(percent2,2) + "%\t");//T-1日固定收益比例
			moneyHash.put("42", YssFun.roundIt(percent4,2) + "%\t");//T-1日股指期货(保证金)比例
			moneyHash.put("43", YssFun.roundIt(percent1+percent2+percent3+percent4,2) + "%\t");//T-1日比率合计
			//edit by yanghaiming 20101111 QDV4华安2010年10月08日02_AB 
			balance = YssFun.roundIt(YssFun.toDouble(moneyHash.get("32").toString().split("\t")[0])
					- YssFun.toDouble(moneyHash.get("33").toString().split("\t")[0])
					+ YssFun.toDouble(moneyHash.get("34").toString().split("\t")[0])
					- YssFun.toDouble(moneyHash.get("35").toString().split("\t")[0]),2);
			moneyHash.put("37", YssFun.formatNumber(balance,"#,##0.##") + "\t");//T-1日国外股票变动小计
			
			//add by yanghaiming 20100908  获取T-1日恒生余额
			query = "select FBANLANCE from " + pub.yssGetTableName("TB_DATA_DIVINESKIWBALANCE")
			+ " where FCHECKSTATE = 1 and FPORTCODE = " + dbl.sqlString(this.portcode) + " and FBARGAINDATE = ";
			if(c.get(Calendar.DAY_OF_WEEK) == 2){//如果是星期一则取周五的日期
				query += dbl.sqlDate(YssFun.addDay(this.checkDate,-3));
			}else{
				query += dbl.sqlDate(YssFun.addDay(this.checkDate,-1));
			}
			rs = dbl.openResultSet(query);
			while (rs.next()){
				this.fSKIWBanlance = rs.getDouble("FBANLANCE");
			}
			dbl.closeResultSetFinal(rs); //modify by fangjiang 2011.05.07 STORY #867
			//add by yanghaiming 20101111 BB可用头寸 T-1日余额
			balance = YssFun.toDouble(moneyHash.get("0").toString().split("\t")[0]) - this.fSKIWBanlance;
			moneyHash.put("2", YssFun.formatNumber(balance,"#,##0.##") + "\t");//BB可用头寸 T-1日余额
			//add by yanghaiming 20101111 BB可用头寸 T-1日余额
		}catch (Exception e) {
			throw new YssException("【执行T-1日数据查询时出错......】");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	private void getOtherAmount(int i, Date date) throws YssException {
		ResultSet rs = null;
        String query = "";
		//处理净资产和债券数据
		double d = 0;//计算时临时保存结果用
		double buyMoney = 0;//计算固定收益，亚太股票买入
		double saleMoney = 0;//计算固定收益，亚太股票卖出
		double buyMoney1 = 0;//国内固定收益买入
		double saleMoney1 = 0;//国内固定收益卖出
		double buyMoney2 = 0;//国内固定收益买入不包含回购
		double saleMoney2 = 0;//国内固定收益卖出不包含回购
		try {
			if(i == 0){//为T日时，处理一些特殊情况下的交易数据
				moneyStr = moneyHash.get("20").toString();//取出申购款（T+3）的金额
				query = "select sum(FSELLMONEY) as FMONEY from " + pub.yssGetTableName("tb_data_divineta")
				//------ modify by wangzuochun 2011.03.30 BUG #1576 华安现金头寸预测表中没有考虑多组合的情况（CNYMoneyControl.java） 
						+ " where FCheckState = 1 and FPortCode = " + dbl.sqlString(this.portcode) + " and FDATATYPE ="
						// modify by lee 2013-09-26  当报表类型为预估或确认时，分别取预估或确认的数据
						+this.checkType
						+" and FSELLTYPE  in ('01','08') and FSETDATE = " + dbl.sqlDate(date)
                    	+ " and FcashAccCode in (select FCashAccCode from "+pub.yssGetTableName("Tb_Para_CashAccount")+" where FCuryCode='CNY' and Fcheckstate = 1)"; //modified by zhaoxianlin  20130129 STORY #2913 这里增加区分币种
				rs = dbl.openResultSet(query);
				if(rs.next()){
					moneyStr += YssFun.formatNumber(YssFun.roundIt(rs.getDouble("FMONEY"),2),"#,##0.##") + "\t";
				}else{
					moneyStr += "0\t";
				}
				moneyHash.put("20", moneyStr);//T日申购款（T+3）
				dbl.closeResultSetFinal(rs);
				moneyStr = moneyHash.get("21").toString();//取出赎回款（T+8）的金额
				query = "select sum(FSELLMONEY) as FMONEY from " + pub.yssGetTableName("tb_data_divineta")
				//------ modify by wangzuochun 2011.03.30 BUG #1576 华安现金头寸预测表中没有考虑多组合的情况（CNYMoneyControl.java） 
						+ " where FCheckState = 1 and FPortCode = " + dbl.sqlString(this.portcode) + " and FDATATYPE = '1' and FSELLTYPE  in ('03','08') and FSETDATE = " + dbl.sqlDate(date)
                        + " and FcashAccCode in (select FCashAccCode from "+pub.yssGetTableName("Tb_Para_CashAccount")+" where FCuryCode='CNY' and Fcheckstate = 1)"; //modified by zhaoxianlin  20130129 STORY #2913 这里增加区分币种
				rs = dbl.openResultSet(query);
				if(rs.next()){
					d = YssFun.roundIt(rs.getDouble("FMONEY"),2);//获取分红和红利再投资金额的和
				}else{
					d = 0;
				}
				dbl.closeResultSetFinal(rs);
				query = "select sum(FSELLMONEY) as FMONEY from " + pub.yssGetTableName("tb_data_divineta")
				//------ modify by wangzuochun 2011.03.30 BUG #1576 华安现金头寸预测表中没有考虑多组合的情况（CNYMoneyControl.java）
						+ " where FCheckState = 1 and FPortCode = " + dbl.sqlString(this.portcode) + " and FDATATYPE = '1' and FSELLTYPE ='02' and FSETDATE = " + dbl.sqlDate(date)
                        + " and FcashAccCode in (select FCashAccCode from "+pub.yssGetTableName("Tb_Para_CashAccount")+" where FCuryCode='CNY' and Fcheckstate = 1)"; //modified by zhaoxianlin  20130129 STORY #2913 这里增加区分币种
				rs = dbl.openResultSet(query);
				if(rs.next()){
					//MS01635 QDV4华安上海2010年08月24日01_AB 算法变更，原先为-d，现改为+d
					moneyStr += YssFun.formatNumber(YssFun.roundIt(rs.getDouble("FMONEY") + d,2),"#,##0.##") + "\t";//如果有确认日期为T日的赎回数据，则用赎回款加上d
				}else{
					moneyStr += 0 + d + "\t";//没有则用0加上d
				}
				moneyHash.put("21", moneyStr);//T日赎回款（T+8）
				dbl.closeResultSetFinal(rs);
              /** add by zhaoxianlin  20130129 STORY #2913 头寸管控表TA数据的读入接口和人民币外币头寸管控表需要修改--start-*/  
				moneyStr = moneyHash.get("44").toString();//取出申购款（T+3）的金额
				query = "select ROUND(sum(FSELLMONEY), 2) as FMONEY from (select FSELLMONEY"+
				        " from "+pub.yssGetTableName("tb_data_divineta")+ " where FCheckState = 1 and FPortCode = "
				        +dbl.sqlString(this.portcode)+" and FDATATYPE = '1' and FSELLTYPE" + "  in ('01','08') and FSETDATE = " 
				        + dbl.sqlDate(date)+ " and FcashAccCode in (select FCashAccCode from "
				        +pub.yssGetTableName("Tb_Para_CashAccount")+" where FCuryCode='USD' and Fcheckstate = 1))"; 
				       	
				rs = dbl.openResultSet(query);
				if(rs.next()){
					moneyStr += YssFun.formatNumber(YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get("USD").toString()),2),"#,##0.##") + "\t";
				}else{
					moneyStr += "0\t";
				}
				moneyHash.put("44", moneyStr);//T日申购款（T+3）
				dbl.closeResultSetFinal(rs);
				moneyStr = moneyHash.get("45").toString();//取出赎回款（T+8）的金额
				query = "select ROUND(sum(FSELLMONEY), 2) as FMONEY from (select FSELLMONEY"+
				        " from "+pub.yssGetTableName("tb_data_divineta")+ " where FCheckState = 1 and FPortCode = "
				        +dbl.sqlString(this.portcode)+" and FDATATYPE = '1' and FSELLTYPE" + "  in ('03','08') and FSETDATE = " 
				        + dbl.sqlDate(date)+ " and FcashAccCode in (select FCashAccCode from "
				        +pub.yssGetTableName("Tb_Para_CashAccount")+" where FCuryCode='USD' and Fcheckstate = 1))";
				rs = dbl.openResultSet(query);
				if(rs.next()){
					d = YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get("USD").toString()),2);//获取分红和红利再投资金额的和
				}else{
					d = 0;
				}
				dbl.closeResultSetFinal(rs);
				query = "select ROUND(sum(FSELLMONEY), 2) as FMONEY from (select FSELLMONEY"+
				        " from "+pub.yssGetTableName("tb_data_divineta")+ " where FCheckState = 1 and FPortCode = "
				        +dbl.sqlString(this.portcode)+" and FDATATYPE = '1' and FSELLTYPE" + " ='02' and FSETDATE = " 
				        + dbl.sqlDate(date)+ " and FcashAccCode in (select FCashAccCode from "
				        +pub.yssGetTableName("Tb_Para_CashAccount")+" where FCuryCode='USD' and Fcheckstate = 1))";
				rs = dbl.openResultSet(query);
				if(rs.next()){
					moneyStr += YssFun.formatNumber(YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get("USD").toString()) + d,2),"#,##0.##") + "\t";
				}else{
					moneyStr += 0 + d + "\t";
				}
				moneyHash.put("45", moneyStr);//T日赎回款（T+8）
				dbl.closeResultSetFinal(rs);
				 /** add by zhaoxianlin  20130129 STORY #2913 头寸管控表TA数据的读入接口和人民币外币头寸管控表需要修改--end-*/  
				//人民币股票买进卖出金额
				//买进
				query = "select sum(FMONEY) as FMONEY,FTRADETYPECODE,FSECURITYTYPE from (select sum(FSettleMoney) as FMONEY,FTRADETYPECODE,FSECURITYTYPE from " + pub.yssGetTableName("tb_data_divinetradedata")
						+ " where FCheckState = 1 and FEXCHANGECODE = 'CH' and FTRADETYPECODE in ('01','02') and FSECURITYTYPE in ('EQ','GC','RE') and FBargainDate = " 
						+ dbl.sqlDate(date) + " and FPORTCODE = " + dbl.sqlString(this.portcode) + " group by FTRADETYPECODE,FSECURITYTYPE"
						+ " union all (select (case when FTRIALMONEY < 0 then FTRIALMONEY * -1 else FTRIALMONEY end) as FTRIALMONEY,"
						+ "(case when FTRIALMONEY > 0 then '02' else '01' end) as FTRADETYPECODE,'EQ' as FSECURITYTYPE from "//加上试算金额  二期需求
						+ pub.yssGetTableName("TB_DATA_DIVINETRIALMONEY") + " where FCheckState = 1 and FEXCHANGECODE = 'CH' and FBargainDate = " + dbl.sqlDate(date)
						+ " and FPORTCODE = " + dbl.sqlString(this.portcode) + ")) group by FTRADETYPECODE,FSECURITYTYPE";
				rs = dbl.openResultSet(query);
				while(rs.next()){
					if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//如果为买入
						if(rs.getString("FSECURITYTYPE").equalsIgnoreCase("EQ")){
							moneyStr = moneyHash.get("26").toString();//T日人民币买入
							moneyStr += YssFun.formatNumber(YssFun.roundIt(rs.getDouble("FMONEY"),2),"#,##0.##") + "\t";
							moneyHash.put("26", moneyStr);
						}else{
							if(rs.getString("FSECURITYTYPE").equalsIgnoreCase("RE")){
								buyMoney1 += rs.getDouble("FMONEY");
							}else{
								buyMoney1 += rs.getDouble("FMONEY");
								buyMoney2 = rs.getDouble("FMONEY");
							}
						}
					}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//如果为卖出
						if(rs.getString("FSECURITYTYPE").equalsIgnoreCase("EQ")){
							moneyStr = moneyHash.get("27").toString();//T日人民币卖出
							moneyStr += YssFun.formatNumber(YssFun.roundIt(rs.getDouble("FMONEY"),2),"#,##0.##") + "\t";
							moneyHash.put("27", moneyStr);
						}else{
							if(rs.getString("FSECURITYTYPE").equalsIgnoreCase("RE")){
								saleMoney1 += rs.getDouble("FMONEY");
							}else{
								saleMoney1 += rs.getDouble("FMONEY");
								saleMoney2 = rs.getDouble("FMONEY");
							}
						}
					}
				}
				dbl.closeResultSetFinal(rs);
				moneyStr = moneyHash.get("28").toString();//T日国内固定收益买入总金额
				moneyStr += YssFun.formatNumber(YssFun.roundIt(buyMoney1,2),"#,##0.##") + "\t";
				moneyHash.put("28", moneyStr);
				moneyStr = moneyHash.get("29").toString();//T日国内固定收益卖出总金额
				moneyStr += YssFun.formatNumber(YssFun.roundIt(saleMoney1,2),"#,##0.##") + "\t";
				moneyHash.put("29", moneyStr);
				for (int j = 26 ; j <=29; j++){//循环上述4个人民币相关的买入卖出数据
					moneyStr = moneyHash.get(j+"").toString();
					//由于这4个数据的T-1日为0，在这里直接判断moneyStr的长度小于4来确定是否有T日的金额拼入字符串中
					if (moneyStr.split("\t").length < 2){//如果数组长度小于2，就表示没有把T日数据拼入字符串中，则拼接0
						moneyStr += "0\t";
						moneyHash.put(j+"", moneyStr);//重新PUT
					}
				}
				//海外市场（除中国）固定收益买入，卖出总金额
				query = "select sum(FSettleMoney) as FMONEY,FTRADETYPECODE,FSECURITYTYPE,FCURYCODE from (select a.FSettleMoney,"
					+ "a.FTRADETYPECODE,a.FSECURITYTYPE,b.FCURYCODE from " + pub.yssGetTableName("tb_data_divinetradedata")
					+ " a left join (select FCURYCODE, FCASHACCCODE from " + pub.yssGetTableName("tb_para_cashaccount")
					+ "  where fcheckstate = 1 and Fportcode = " + dbl.sqlString(this.portcode) + " and FSUBACCTYPE = '0101') b on a.FCASHACCCODE = b.FCASHACCCODE"
					+ " where a.FCheckState = 1 and a.Fportcode = " + dbl.sqlString(this.portcode) + " and a.FSECURITYTYPE = 'GC' and FTRADETYPECODE in ('01', '02') and a.FBargainDate = " + dbl.sqlDate(date)
					+ " and a.FEXCHANGECODE <> 'CH' and a.FCASHACCCODE in (select FCASHACCCODE from " + pub.yssGetTableName("tb_para_cashaccount")
					+ " where Fportcode = " + dbl.sqlString(this.portcode) + " and FSUBACCTYPE = '0101' and fcheckstate = 1))group by FTRADETYPECODE, FSECURITYTYPE, FCURYCODE order by FTRADETYPECODE";//根据交易方式排序
				rs = dbl.openResultSet(query);
				while (rs.next()){
					if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//如果为买入
						buyMoney += YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString()),2);
					}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//如果为卖出
						saleMoney += YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString()),2);
					}
				}
				dbl.closeResultSetFinal(rs);
				moneyStr = moneyHash.get("30").toString();//海外固定收益买入
				moneyStr += YssFun.formatNumber(buyMoney,"#,##0.##") + "\t";
				moneyHash.put("30", moneyStr);
				moneyStr = moneyHash.get("31").toString();//海外固定收益卖出
				moneyStr += YssFun.formatNumber(saleMoney,"#,##0.##") + "\t";
				moneyHash.put("31", moneyStr);
				//亚太市场买入，卖出
				query = "select sum(FSettleMoney) as FMONEY,FTRADETYPECODE,FSECURITYTYPE,FCURYCODE from (select a.FSettleMoney,"
					+ "a.FTRADETYPECODE,a.FSECURITYTYPE,b.FCURYCODE from " + pub.yssGetTableName("tb_data_divinetradedata")
					+ " a left join (select FCURYCODE, FCASHACCCODE from " + pub.yssGetTableName("tb_para_cashaccount")
					+ "  where fcheckstate = 1 and Fportcode = " + dbl.sqlString(this.portcode) + " and FSUBACCTYPE = '0101') b on a.FCASHACCCODE = b.FCASHACCCODE"
					+ " where a.FCheckState = 1 and a.Fportcode = " + dbl.sqlString(this.portcode) + " and a.FSECURITYTYPE = 'EQ' and FTRADETYPECODE in ('01', '02') and a.FBargainDate = " + dbl.sqlDate(date)
					//modify by fangjiang 2011.05.07 STORY #867 增加伦敦GBP(交易所代码LN)
					+ " and a.FEXCHANGECODE not in ('CH','US','LN'," +
					// ADD BY LEE 2011-12-13
					// START ADD
					"'IT','CA','FP','UN','UW','LI'" +
					// END ADD 
							") and a.FCASHACCCODE in (select FCASHACCCODE from " + pub.yssGetTableName("tb_para_cashaccount")
					//------------------------
					+ " where Fportcode = " + dbl.sqlString(this.portcode) + " and fcheckstate=1 and FSUBACCTYPE = '0101'))group by FTRADETYPECODE, FSECURITYTYPE, FCURYCODE order by FTRADETYPECODE";//根据交易方式排序
				rs = dbl.openResultSet(query);
				buyMoney = 0;//将初始值赋为0
				saleMoney = 0;
				while (rs.next()){
					if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//如果为买入
						buyMoney += YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString()),2);
					}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//如果为卖出
						saleMoney += YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString()),2);
					}
				}
				dbl.closeResultSetFinal(rs);
				//亚太市场试算金额
				//modify by fangjiang 2011.05.07 STORY #867
				query = "select sum(FTRIALMONEY) as FMONEY,FTRADETYPECODE,FCURYCODE from (select (case when FTRIALMONEY < 0 then FTRIALMONEY * -1 else FTRIALMONEY end) as FTRIALMONEY,"
					+ "(case when FTRIALMONEY > 0 then '02' else '01' end) as FTRADETYPECODE," 
					+ "(case FEXCHANGECODE when 'TT' then 'TWD' when 'SP' then 'SGD' when 'KS' then 'KRW' when 'IJ' then 'IDR' when 'MK' then 'MYR' when 'PM' then 'PHP' when 'TB' then 'THB'" +
					// ADD BY LEE
					// START ADD
					"when 'AS' then 'AUD'" +
					// END ADD
							" else 'HKD' end) as FCURYCODE from "
					+ pub.yssGetTableName("TB_DATA_DIVINETRIALMONEY") + " where FCheckState = 1 and FEXCHANGECODE in ('TT','SP','HK','KS','IJ','MK','PM','TB'" +
					// ADD BY LEE
					// START ADD
					"'AS'" +
					// END ADD
							")"
					+ "and FBargainDate = " + dbl.sqlDate(date) + " and FPORTCODE = " + dbl.sqlString(this.portcode) + ") group by FTRADETYPECODE,FCURYCODE";
				//---------------
				rs = dbl.openResultSet(query);
				while (rs.next()){
					if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//如果为买入
						buyMoney += rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString());
					}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//如果为卖出
						saleMoney += rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString());
					}
				}
				dbl.closeResultSetFinal(rs);//增加试算金额完毕
				moneyStr = moneyHash.get("32").toString();//亚太市场买入（股票）
				moneyStr += YssFun.formatNumber(buyMoney,"#,##0.##") + "\t";
				moneyHash.put("32", moneyStr);
				moneyStr = moneyHash.get("33").toString();//亚太市场卖出
				moneyStr += YssFun.formatNumber(saleMoney,"#,##0.##") + "\t";
				moneyHash.put("33", moneyStr);
				buyMoney = 0;//将初始值赋为0
				saleMoney = 0;
				if(this.checkType == 1){//如果为确认即早上头寸表，则需将欧美市场买入卖出查询出来，预估数据不查
					//modify by fangjiang 2011.05.07 STORY #867
					query = "select sum(FSettleMoney) as FMONEY,FTRADETYPECODE,FSECURITYTYPE,FCURYCODE from (select a.FSettleMoney,"
						+ "a.FTRADETYPECODE,a.FSECURITYTYPE,b.FCURYCODE from " + pub.yssGetTableName("tb_data_divinetradedata")
						+ " a left join (select FCURYCODE, FCASHACCCODE from " + pub.yssGetTableName("tb_para_cashaccount")
						+ "  where fcheckstate = 1 and Fportcode = " + dbl.sqlString(this.portcode) + " and FSUBACCTYPE = '0101') b on a.FCASHACCCODE = b.FCASHACCCODE"
						+ " where a.FCheckState = 1 and a.Fportcode = " + dbl.sqlString(this.portcode) + " and a.FSECURITYTYPE = 'EQ' and FTRADETYPECODE in ('01', '02') and a.FBargainDate = " + dbl.sqlDate(date)
						+ " and a.FEXCHANGECODE in ('US','LN'," +
						// ADD BY LEE 2011-12-13
						// START ADD
						"'IT','CA','FP','UN','UW','LI'" +
						// END ADD
								") and a.FCASHACCCODE in (select FCASHACCCODE from " + pub.yssGetTableName("tb_para_cashaccount")
						+ " where Fportcode = " + dbl.sqlString(this.portcode) + " and fcheckstate=1 and FSUBACCTYPE = '0101'))group by FTRADETYPECODE, FSECURITYTYPE, FCURYCODE order by FTRADETYPECODE";//根据交易方式排序
					//------------------------
					rs = dbl.openResultSet(query);
					buyMoney = 0;//将初始值赋为0
					saleMoney = 0;
					while(rs.next()){
						if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//如果为买入
							buyMoney += YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString()),2);
						}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//如果为卖出
							saleMoney += YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString()),2);
						}
					}
					dbl.closeResultSetFinal(rs);
					moneyStr = moneyHash.get("34").toString();//欧美市场买入（股票）
					moneyStr += YssFun.formatNumber(buyMoney,"#,##0.##") + "\t";
					moneyHash.put("34", moneyStr);
					moneyStr = moneyHash.get("35").toString();//欧美市场卖出
					moneyStr += YssFun.formatNumber(saleMoney,"#,##0.##") + "\t";
					moneyHash.put("35", moneyStr);
					moneyStr = moneyHash.get("36").toString();//欧美市场预估数据 ,由于是确认报表，因此T日的预估数据直接可以赋值为0
					moneyHash.put("36", moneyStr + "0\t");
				}else{//预估报表
					moneyStr = moneyHash.get("34").toString();//欧美市场买入（股票）
					moneyHash.put("34", moneyStr + "0\t");
					moneyStr = moneyHash.get("35").toString();//欧美市场卖出
					moneyHash.put("35", moneyStr + "0\t");
					//modify by fangjiang 2011.05.07 STORY #867
					query = "select sum(FESTIMATEMONEY) as FESTIMATEMONEY,sum(FCOMMISIONMONEY) as FCOMMISIONMONEY,FTRADETYPECODE, FCURYCODE from "
						//------ modify by wangzuochun 2011.03.30 BUG #1576 华安现金头寸预测表中没有考虑多组合的情况（CNYMoneyControl.java）
						+ "(select FESTIMATEMONEY, FCOMMISIONMONEY, FTRADETYPECODE, FCURYCODE from (select FESTIMATEMONEY, FCOMMISIONMONEY, FTRADETYPECODE, FCASHACCCODE from " + pub.yssGetTableName("TB_DATA_DIVINEESTIMATE") 
						+ " where FCheckState = 1 and FPortCode = " + dbl.sqlString(this.portcode) + " and FEXCHANGECODE in ('US','LN'," +
						// ADD BY LEE 2011-12-13
						// START ADD
						"'IT','CA','FP','UN','UW','LI'" +
						// END ADD
								") and FTRADETYPECODE in ('01','02') and FTRADEDATE = "
						+ dbl.sqlDate(date) + ") a join " + pub.yssGetTableName("tb_para_cashaccount") + " b on a.FCASHACCCODE = b.FCASHACCCODE) group by FTRADETYPECODE,FCURYCODE";
					buyMoney = 0;//将初始值赋为0
					rs = dbl.openResultSet(query);
					while (rs.next()){
						if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//如果为买入
							buyMoney += YssFun.roundIt((rs.getDouble("FESTIMATEMONEY") + rs.getDouble("FCOMMISIONMONEY")) * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString()),2);
						}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//如果为卖出
							buyMoney += YssFun.roundIt((-rs.getDouble("FESTIMATEMONEY") + rs.getDouble("FCOMMISIONMONEY")) * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString()),2);
						}
					}
					//buyMoney = buyMoney * YssFun.toDouble(rateHash.get("USD").toString());
					dbl.closeResultSetFinal(rs);
					moneyStr = moneyHash.get("36").toString();//欧美市场预估数据
					moneyStr += YssFun.formatNumber(buyMoney,"#,##0.##") + "\t";
					moneyHash.put("36", moneyStr);
					//-----------------
				}
				//欧美市场增加试算金额
				//modify by fangjiang 2011.05.07 STORY #867
				query = "select sum(FTRIALMONEY) as FMONEY,FTRADETYPECODE,FCURYCODE from (select (case when FTRIALMONEY < 0 then FTRIALMONEY * -1 else FTRIALMONEY end) as FTRIALMONEY,"
					+ "(case when FTRIALMONEY > 0 then '02' else '01' end) as FTRADETYPECODE,(case FEXCHANGECODE when 'US' then 'USD'" +
					// ADD BY LEE 2011-12-13
					// START ADD 'IT','CA','FP','UN','UW'
					" when 'IT' then 'EUR' " +
					" when 'CA' then 'CAD' " +
					" when 'FP' then 'EUR' " +
					" when 'UN' then 'USD' " +
					" when 'UW' then 'USD' " +
					// END ADD
							" else 'GBP' end) as FCURYCODE from "
					+ pub.yssGetTableName("TB_DATA_DIVINETRIALMONEY") + " where FCheckState = 1 and FEXCHANGECODE in ('US','LN'," +
					// ADD BY LEE 2011-12-13
					// START ADD
					"'IT','CA','FP','UN','UW','LI'" +
					// END ADD
							")"
					+ "and FBargainDate = " + dbl.sqlDate(date) + " and FPORTCODE = " + dbl.sqlString(this.portcode) + ")  group by FTRADETYPECODE,FCURYCODE";
				rs = dbl.openResultSet(query);
				buyMoney = 0;//将初始值赋为0
				saleMoney = 0;
				while(rs.next()){
					if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//如果为买入
						buyMoney += YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString()),2);
					}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//如果为卖出
						saleMoney += YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString()),2);
					}
				}
				//--------------------
				dbl.closeResultSetFinal(rs);
				//加上欧美市场的试算金额
				moneyStr = moneyHash.get("34").toString();//欧美市场买入（股票）
				buyMoney += YssFun.toDouble(moneyStr.split("\t")[1]);
				moneyStr = "0\t" + YssFun.formatNumber(buyMoney,"#,##0.##") + "\t";//重新处理欧美T-1日及T日的买入金额
				moneyHash.put("34", moneyStr);
				moneyStr = moneyHash.get("35").toString();//欧美市场卖出
				saleMoney += YssFun.toDouble(moneyStr.split("\t")[1]);
				moneyStr = "0\t" + YssFun.formatNumber(saleMoney,"#,##0.##") + "\t";//重新处理欧美T-1日及T日的卖出金额
				moneyHash.put("35", moneyStr);
				//处理T日的股票和债券金额
				d = 0;
				moneyStr = moneyHash.get("38").toString();//股票
				d = YssFun.roundIt(YssFun.toDouble(moneyStr.split("\t")[0]) * (1 + this.scale) + YssFun.toDouble(moneyHash.get("26").toString().split("\t")[1])//T-1日股票×（1+1%）+人民币股票买进金额
					- YssFun.toDouble(moneyHash.get("27").toString().split("\t")[1])//人民币股票卖出金额
					+ YssFun.toDouble(moneyHash.get("32").toString().split("\t")[1])//亚太市场买入
					- YssFun.toDouble(moneyHash.get("33").toString().split("\t")[1]),2);//亚太市场卖出
				if(this.checkType == 1){//如果为确认表，需要 +欧美市场买入-欧美市场卖出
					d = YssFun.roundIt(d + YssFun.toDouble(moneyHash.get("34").toString().split("\t")[1])//欧美市场买入
						- YssFun.toDouble(moneyHash.get("35").toString().split("\t")[1]),2);//欧美市场卖出
				}else{//如果为预估表 则 + 预估数据
					d = YssFun.roundIt(d + YssFun.toDouble(moneyHash.get("36").toString().split("\t")[1]),2);
				}
				moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
				//由于股票金额从T+1日至T+10日金额都与T日相同，故直接赋值
				//for(int a = 0 ; a<10 ;a++){     
				for(int a = 0 ; a<searchDays ;a++){  //modified by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整
					moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
				}
				moneyHash.put("38", moneyStr);//股票的数据处理完毕
				d = 0;
				//改为固定收益，在后面处理
//				moneyStr = moneyHash.get("40").toString();//债券
//				d = YssFun.roundIt(YssFun.toDouble(moneyStr.split("\t")[0]) + buyMoney2//国内固定收益买入总金额不包含回购
//					- saleMoney2//国内固定收益卖出总金额不包含回购
//					+ YssFun.toDouble(moneyHash.get("30").toString().split("\t")[1])//海外市场固定收益买入总金额
//					- YssFun.toDouble(moneyHash.get("31").toString().split("\t")[1]),2);//海外市场固定收益卖出总金额
//				moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
//				//由于债券金额从T+1日至T+10日金额都与T日相同，故直接赋值
//				for(int a = 0 ; a<10 ;a++){
//					moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
//				}
//				moneyHash.put("40", moneyStr);//债券的数据处理完毕
				//将hashmap中key为20-30的数据，T+1日至T+10日赋0
				for (int a = 26; a<=36; a++){
					moneyStr = moneyHash.get(a+"").toString();
					//for(int j = 0 ; j<10 ;j++){
					for(int j = 0 ; j<searchDays ;j++){//modified by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整
						moneyStr += "0\t";
					}
					moneyHash.put(a+"", moneyStr);//重新PUT
				}//20-30的数据处理完毕
				//处理国外股票变动小计
				moneyStr = moneyHash.get("37").toString();
				d = YssFun.roundIt(YssFun.toDouble(moneyHash.get("32").toString().split("\t")[1])//亚太买入
					- YssFun.toDouble(moneyHash.get("33").toString().split("\t")[1]),2);//亚太卖出
				if(this.checkType == 1){
					d = YssFun.roundIt(d + YssFun.toDouble(moneyHash.get("34").toString().split("\t")[1])//欧美买入
						- YssFun.toDouble(moneyHash.get("35").toString().split("\t")[1]),2);//欧美卖出
				}else{
					d = YssFun.roundIt(d + YssFun.toDouble(moneyHash.get("36").toString().split("\t")[1]),2);//欧美市场预估数据
				}
				moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
				//for(int a = 0 ; a<10 ;a++){//T+1日之后的数据均为0
				for(int a = 0 ; a<searchDays ;a++){//T+1日之后的数据均为0  //modified by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整
					moneyStr += "0\t";
				}
				moneyHash.put("37", moneyStr);//国外股票变动小计 处理完毕
//				moneyStr = moneyHash.get("14").toString();
//				d = YssFun.roundIt(YssFun.toDouble(moneyHash.get("14").toString().split("\t")[0]) * 1.01,2);
//				moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
//				for(int a = 0 ; a<10 ;a++){
//					d = YssFun.roundIt(d * 1.01,2);
//					moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
//				}
//				moneyHash.put("14", moneyStr);//净值处理完毕
//				//欧美股债应收应付款预估)
//				query = "select sum(FESTIMATEMONEY) as FESTIMATEMONEY,sum(FCOMMISIONMONEY) as FCOMMISIONMONEY,FTRADETYPECODE from "
//					+ pub.yssGetTableName("TB_DATA_DIVINEESTIMATE") + " where FEXCHANGECODE = 'US' and FTRADETYPECODE in ('01','02') and FSETTLEDATE = "
//					+ dbl.sqlDate(date) + " group by FTRADETYPECODE";
//				d = 0;//将初始值赋为0
//				rs = dbl.openResultSet(query);
//				while (rs.next()){
//					if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//如果为买入
//						d = YssFun.roundIt(d - rs.getDouble("FESTIMATEMONEY") - rs.getDouble("FCOMMISIONMONEY"),2);
//					}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//如果为卖出
//						d = YssFun.roundIt(d + rs.getDouble("FESTIMATEMONEY") - rs.getDouble("FCOMMISIONMONEY"),2);
//					}
//				}
//				dbl.closeResultSetFinal(rs);
//				moneyStr = moneyHash.get("11").toString();//欧美股债应收应付款预估)
//				moneyStr += buyMoney + "\t";
//				for(int a = 0 ; a<10 ;a++){//T+1日之后的数据均为0
//					moneyStr += "0\t";
//				}
//				moneyHash.put("11", moneyStr);
				
				//处理一年内到期政府债及央票(余额)
				query = "select sum(a.FMONEY) as FMONEY, a.FTRADETYPECODE,b.FCURYCODE from " +
						"(select sum(FSettleMoney) as FMONEY,FTRADETYPECODE,FCASHACCCODE from " + pub.yssGetTableName("tb_data_divinetradedata")
					+ " where FCheckState = 1 and FTRADETYPECODE in ('01','02') and FSECURITYTYPE = 'GC' and FBargainDate = " + dbl.sqlDate(this.checkDate) + " and FPORTCODE = " + dbl.sqlString(this.portcode) 
//CYB去掉判断债券	到期日" and FSECURITYCODE in (select FSECURITYCODE from " + pub.yssGetTableName("Tb_Para_FixInterest") + 
//					" where FInsCashDate < " + dbl.sqlDate(YssFun.addYear(date,1)) + " and FInsCashDate > " +
//					dbl.sqlDate(date) + ") " +
					+ "group by FTRADETYPECODE,FCASHACCCODE) a left join (select FCURYCODE, FCASHACCCODE from " + pub.yssGetTableName("tb_para_cashaccount")
					+ "  where fcheckstate = 1 and Fportcode = " + dbl.sqlString(this.portcode) + " and FSUBACCTYPE = '0101') b on a.FCASHACCCODE = b.FCASHACCCODE group by a.FTRADETYPECODE,b.FCURYCODE";
				rs = dbl.openResultSet(query);
				d = 0;
				while(rs.next()){
					if(rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//买入市值增加
						if(!rs.getString("FCURYCODE").equalsIgnoreCase("CNY")){//如果不是人民币则需要通过汇率进行换算
							d += YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString()),2);
						}else{
							d += YssFun.roundIt(rs.getDouble("FMONEY"),2);
						}
					}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//卖出市值减少
						if(!rs.getString("FCURYCODE").equalsIgnoreCase("CNY")){
							d -= YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString()),2);
						}else{
							d -= YssFun.roundIt(rs.getDouble("FMONEY"),2);
						}
					}
				}
				dbl.closeResultSetFinal(rs); //modify by fangjiang 2011.05.07 STORY #867
				moneyStr = moneyHash.get("6").toString();
				d += YssFun.roundIt(YssFun.toDouble(moneyStr.split("\t")[0]),2);//前一日余额
				//for(int a = 0 ; a<=10 ;a++){//T+1日之后的数据均=T日  
				for(int a = 0 ; a<=searchDays ;a++){//T+1日之后的数据均=T日 //modified by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整
					moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
				}
				moneyHash.put("6", moneyStr);//一年内到期政府债及央票(余额) 处理完毕				
			}else if(i == 1){//T+1日时，处理 申购款（T+3） 和 赎回款（T+8）
				moneyStr = moneyHash.get("20").toString();//取出申购款（T+3）的金额
				query = "select sum(FSELLMONEY) as FMONEY from " + pub.yssGetTableName("tb_data_divineta")
				//------ modify by wangzuochun 2011.03.30 BUG #1576 华安现金头寸预测表中没有考虑多组合的情况（CNYMoneyControl.java）
				
				//20130905 modified by liubo.将取数状态由“预估”变为“确认”（临时改法）
						+ " where FCheckState = 1 and FPortCode = " + dbl.sqlString(this.portcode) + " and FDATATYPE = "
						// modify by lee 2013-09-26  当报表类型为预估或确认时，分别取预估或确认的数据
						+this.checkType
						+" and FSELLTYPE  = '01' and FSETDATE = " + dbl.sqlDate(date)
                     	+ " and FcashAccCode in (select FCashAccCode from "+pub.yssGetTableName("Tb_Para_CashAccount")+" where FCuryCode='CNY' and Fcheckstate = 1)"; //modified by zhaoxianlin  20130129 STORY #2913 这里增加区分币种
				rs = dbl.openResultSet(query);
				if(rs.next()){
					moneyStr += YssFun.formatNumber(YssFun.roundIt(rs.getDouble("FMONEY"),2),"#,##0.##") + "\t";
				}else{
					moneyStr += "0\t";
				}
				dbl.closeResultSetFinal(rs);
				//for(int a = 0; a<9; a++){//T日申购款（T+3) 在T+2日之后的数据都为0 
				for(int a = 0; a<searchDays-1; a++){//T日申购款（T+3) 在T+2日之后的数据都为0   //modified by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整
					moneyStr += "0\t";
				}
				moneyHash.put("20", moneyStr);//T日申购款（T+3）处理完毕
				
				moneyStr = moneyHash.get("21").toString();//取出赎回款（T+8）的金额
				query = "select sum(FSELLMONEY) as FMONEY from " + pub.yssGetTableName("tb_data_divineta")
				//------ modify by wangzuochun 2011.03.30 BUG #1576 华安现金头寸预测表中没有考虑多组合的情况（CNYMoneyControl.java）
						+ " where FCheckState = 1 and FPortCode = " + dbl.sqlString(this.portcode) + " and FDATATYPE = '0' and FSELLTYPE ='02' and FSETDATE = " + dbl.sqlDate(date)
	                    + " and FcashAccCode in (select FCashAccCode from "+pub.yssGetTableName("Tb_Para_CashAccount")+" where FCuryCode='CNY' and Fcheckstate = 1)"; //modified by zhaoxianlin  20130129 STORY #2913 这里增加区分币种
				rs = dbl.openResultSet(query);
				if(rs.next()){
					moneyStr += YssFun.formatNumber(YssFun.roundIt(rs.getDouble("FMONEY"),2),"#,##0.##") + "\t";
				}else{
					moneyStr += "0\t";
				}
				dbl.closeResultSetFinal(rs);
				//for(int a = 0; a<9; a++){//T日申购款（T+3) 在T+2日之后的数据都为0
				for(int a = 0; a<searchDays-1; a++){//T日申购款（T+3) 在T+2日之后的数据都为0//modified by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整
					moneyStr += "0\t";
				}
				moneyHash.put("21", moneyStr);//T日赎回款（T+8）处理完毕
	 /** add by zhaoxianlin  20130129 STORY #2913 头寸管控表TA数据的读入接口和人民币外币头寸管控表需要修改--start-*/  
				moneyStr = moneyHash.get("44").toString();//取出申购款（T+3）的金额
				query = "select ROUND(sum(FSELLMONEY), 2) as FMONEY from (select FSELLMONEY"+
				        " from "+pub.yssGetTableName("tb_data_divineta")+ " where FCheckState = 1 and FPortCode = "
				        +dbl.sqlString(this.portcode)+" and FDATATYPE = '0' and FSELLTYPE" + " ='01' and FSETDATE = " 
				        + dbl.sqlDate(date)+ " and FcashAccCode in (select FCashAccCode from "
				        +pub.yssGetTableName("Tb_Para_CashAccount")+" where FCuryCode='USD' and Fcheckstate = 1))";
				rs = dbl.openResultSet(query);
				if(rs.next()){
					moneyStr += YssFun.formatNumber(YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get("USD").toString()),2),"#,##0.##") + "\t";
				}else{
					moneyStr += "0\t";
				}
				dbl.closeResultSetFinal(rs);
				//for(int a = 0; a<9; a++){//T日申购款（T+3) 在T+2日之后的数据都为0 
				for(int a = 0; a<searchDays-1; a++){//T日申购款（T+3) 在T+2日之后的数据都为0   //modified by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整
					moneyStr += "0\t";
				}
				moneyHash.put("44", moneyStr);//T日申购款（T+3）处理完毕
				
				moneyStr = moneyHash.get("45").toString();//取出赎回款（T+8）的金额
				query = "select ROUND(sum(FSELLMONEY), 2) as FMONEY from (select FSELLMONEY"+
				        " from "+pub.yssGetTableName("tb_data_divineta")+ " where FCheckState = 1 and FPortCode = "
				        +dbl.sqlString(this.portcode)+" and FDATATYPE = '0' and FSELLTYPE" + " ='02' and FSETDATE = " 
				        + dbl.sqlDate(date)+ " and FcashAccCode in (select FCashAccCode from "
				        +pub.yssGetTableName("Tb_Para_CashAccount")+" where FCuryCode='USD' and Fcheckstate = 1))";
				rs = dbl.openResultSet(query);
				if(rs.next()){
					moneyStr += YssFun.formatNumber(YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get("USD").toString()),2),"#,##0.##") + "\t";
				}else{
					moneyStr += "0\t";
				}
				dbl.closeResultSetFinal(rs);
				//for(int a = 0; a<9; a++){//T日申购款（T+3) 在T+2日之后的数据都为0
				for(int a = 0; a<searchDays-1; a++){//T日申购款（T+3) 在T+2日之后的数据都为0//modified by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整
					moneyStr += "0\t";
				}
				moneyHash.put("45", moneyStr);//T日赎回款（T+8）处理完毕
				 /** add by zhaoxianlin  20130129 STORY #2913 头寸管控表TA数据的读入接口和人民币外币头寸管控表需要修改--start-*/  
			}
			//欧美股债应收应付款预估)
			//modify by fangjiang 2011.05.07 STORY #867
			query = "select sum(FESTIMATEMONEY) as FESTIMATEMONEY,sum(FCOMMISIONMONEY) as FCOMMISIONMONEY,FTRADETYPECODE,FCURYCODE from ( select FESTIMATEMONEY, FCOMMISIONMONEY, FTRADETYPECODE,(case FEXCHANGECODE when 'US' then 'USD' " +
			// ADD BY LEE 2011-12-13
			// START ADD 'IT','CA','FP','UN','UW'
			" when 'IT' then 'EUR' " +
			" when 'CA' then 'CAD' " +
			" when 'FP' then 'EUR' " +
			" when 'UN' then 'USD' " +
			" when 'UW' then 'USD' " +
			// END ADD
					"else 'GBP' end) as FCURYCODE from "
				//------ modify by wangzuochun 2011.03.30 BUG #1576 华安现金头寸预测表中没有考虑多组合的情况（CNYMoneyControl.java）
				+ pub.yssGetTableName("TB_DATA_DIVINEESTIMATE") + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(this.portcode) + " and FEXCHANGECODE in ('US','LN'," +
				// ADD BY LEE 2011-12-13
				// START ADD
				"'IT','CA','FP','UN','UW','LI'" +
				// END ADD
						") and FTRADETYPECODE in ('01','02') and FSETTLEDATE = "
				+ dbl.sqlDate(date) + " and FTRADEDATE = " + dbl.sqlDate(this.checkDate) + ") group by FTRADETYPECODE,FCURYCODE";
			d = 0;//将初始值赋为0
			rs = dbl.openResultSet(query);
			while (rs.next() && this.checkType == 0){
				if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//如果为买入
					d += YssFun.roundIt((- rs.getDouble("FESTIMATEMONEY") - rs.getDouble("FCOMMISIONMONEY"))*YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString()),2);
				}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//如果为卖出
					d += YssFun.roundIt((rs.getDouble("FESTIMATEMONEY") - rs.getDouble("FCOMMISIONMONEY"))*YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString()),2);
				}
			}
			//d = d * YssFun.toDouble(rateHash.get("USD").toString());
			//---------------------------------
			dbl.closeResultSetFinal(rs);
			moneyStr = moneyHash.get("14").toString();//欧美股债应收应付款预估)
			moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
			moneyHash.put("14", moneyStr);//欧美股债应收应付款预估)处理完毕

			//应收申购款,应付赎回款
			query = "select sum(FSELLMONEY) as FMONEY,FSELLTYPE from " + pub.yssGetTableName("tb_data_divineta")
			//------ modify by wangzuochun 2011.03.30 BUG #1576 华安现金头寸预测表中没有考虑多组合的情况（CNYMoneyControl.java）
				+ " where FCheckState = 1 and FPortCode = " + dbl.sqlString(this.portcode) + " and FDATATYPE = "
				// modify by lee 2013-09-26  当报表类型为预估或确认时，分别取预估或确认的数据
				+this.checkType
				+" and FSELLTYPE in ('01','02','03') and FSETTLEDATE = " + dbl.sqlDate(date) + " and FTRADEDATE <= " 
				+ dbl.sqlDate(this.checkDate) 
				//modify by lee 增加对币种的判断，只取人民币，不取美元
				+" and fcashacccode in (select fcashacccode from "+ pub.yssGetTableName("tb_para_cashaccount") + " where fcurycode='CNY' and fcheckstate=1)"+ " group by FSELLTYPE";
			buyMoney = 0;//将初始值赋为0 应收
			saleMoney = 0;//应付
			rs = dbl.openResultSet(query);
			while (rs.next()){
				if (rs.getString("FSELLTYPE").equalsIgnoreCase("01")){//如果是申购
					buyMoney += YssFun.roundIt(rs.getDouble("FMONEY"),2);
				}else if (rs.getString("FSELLTYPE").equalsIgnoreCase("02") || rs.getString("FSELLTYPE").equalsIgnoreCase("03")){//如果是赎回,现金分红
					saleMoney += YssFun.roundIt(rs.getDouble("FMONEY"),2);
				}
			}
			dbl.closeResultSetFinal(rs);
			/**delete---huhuichao 2013-10-10 STORY  13190 此段代码上面已经实现了 这里再运行会出现数据重复累加 */
//			query = "select sum(FSELLMONEY) as FMONEY,FSELLTYPE from " + pub.yssGetTableName("tb_data_divineta")
//			//------ modify by wangzuochun 2011.03.30 BUG #1576 华安现金头寸预测表中没有考虑多组合的情况（CNYMoneyControl.java）
//			//modify by lee 修改FDATATYPE=1，取确认数据
//				+ " where FCheckState = 1 and FPortCode = " + dbl.sqlString(this.portcode) + " and FDATATYPE = '1' and FSELLTYPE in ('01','02') and FSETTLEDATE = " + dbl.sqlDate(date) + " and FTRADEDATE = " 
//				+ dbl.sqlDate(this.checkDate) 
//				//modify by lee 增加对币种的判断，只取人民币，不取美元
//				+" and fcashacccode in (select fcashacccode from "+ pub.yssGetTableName("tb_para_cashaccount") + " where fcurycode='CNY' and fcheckstate=1)"+ " group by FSELLTYPE";
//			rs = dbl.openResultSet(query);
//			while (rs.next()){
//				if (rs.getString("FSELLTYPE").equalsIgnoreCase("01")){//如果是申购
//					buyMoney += YssFun.roundIt(rs.getDouble("FMONEY"),2);
//				}else if (rs.getString("FSELLTYPE").equalsIgnoreCase("02")){//如果是赎回
//					saleMoney += YssFun.roundIt(rs.getDouble("FMONEY"),2);
//				}
//			}
//			dbl.closeResultSetFinal(rs);
			/**end---huhuichao 2013-10-10 STORY  13190*/
			moneyStr = moneyHash.get("10").toString();//应收申购款
			moneyStr += YssFun.formatNumber(buyMoney,"#,##0.##") + "\t";
			moneyHash.put("10", moneyStr);//应收申购款处理完毕
			moneyStr = moneyHash.get("11").toString();//应付赎回款
			moneyStr += YssFun.formatNumber(saleMoney,"#,##0.##") + "\t";
			moneyHash.put("11", moneyStr);//应付赎回款处理完毕
			
			//外汇数据处理
			//edit by yanghaiming MS01817 头寸表中没有显示换汇业务数据 QDV4华安2010年09月28日02_B 
			query = "select sum(FBMONEY) as FMONEY, FTRADETYPECODE from (select FBREACHDATE as FFORECASTDATE,FBMONEY as FBMONEY," +
        			" FPORTCODE,'02' as FTRADETYPECODE from " + pub.yssGetTableName("tb_data_divineratetrade") +
        			" where fcheckstate = 1 and FBREACHDATE = " + dbl.sqlDate(date) + " and FPORTCODE = " + dbl.sqlString(this.portcode) + 
        			" and FBCASHACCCODE = (select fcashacccode from "+ pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1"+
     //       	    " and FBankCode = 'ICBC' and FCuryCode = 'CNY' and FSUBACCTYPE = '0101' and fportcode = " + dbl.sqlString(this.portcode) + ") and FSCASHACCCODE not in " +
        			" and FBankCode in('ICBC','BOC'," +
        			// ADD BY LEE 2011-12-13
        			// START ADD
        			"'CCB'" +
        			// END ADD
        			") and FCuryCode = 'CNY' and FSUBACCTYPE = '0101' and fportcode = " + dbl.sqlString(this.portcode) + ") and FSCASHACCCODE not in " +
        			" (select fcashacccode from "+ pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1"+
            	    " and FSUBACCTYPE = '0102' and fportcode = " + dbl.sqlString(this.portcode) + ")" +
            	    " union all (select FREACHDATE as FFORECASTDATE,FSMONEY as FBMONEY, FPORTCODE,'01' as FTRADETYPECODE from " +
                	pub.yssGetTableName("tb_data_divineratetrade") + " where fcheckstate = 1 and FPORTCODE = " + dbl.sqlString(this.portcode) +
                	" and FREACHDATE = " + dbl.sqlDate(date) + " and FSCASHACCCODE = (select fcashacccode from "+ 
   //             	pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1 and FBankCode = 'ICBC' and FCuryCode = 'CNY' and fportcode = " + dbl.sqlString(this.portcode) +
                	pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1 and FBankCode in('ICBC','BOC'," +
                	// ADD BY LEE 2011-12-13
                	// START ADD
                	"'CCB'" +
                	// END ADD
                			") and FCuryCode = 'CNY' and fportcode = " + dbl.sqlString(this.portcode) +
                	" and FSUBACCTYPE = '0101') and FBCASHACCCODE not in (select fcashacccode from "+
                	pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1"+
                	" and FSUBACCTYPE = '0102' and fportcode = " + dbl.sqlString(this.portcode) + ")" +
                	")) group by FTRADETYPECODE";
			rs = dbl.openResultSet(query);
			d = 0;
			while (rs.next()){
				if(rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//流入
					d += YssFun.roundIt(rs.getDouble("FMONEY"),2);
				}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//流出
					d -= YssFun.roundIt(rs.getDouble("FMONEY"),2);
				}
			}
			dbl.closeResultSetFinal(rs); //modify by fangjiang 2011.05.07 STORY #867
			moneyStr = moneyHash.get("4").toString();
			moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
			moneyHash.put("4", moneyStr);//换汇处理完毕
			
			//当日人民币应收,应付股债款
			query = "select sum(FMONEY) as FMONEY,FTRADETYPECODE from (select sum(FSettleMoney) as FMONEY,FTRADETYPECODE from " 
				+ pub.yssGetTableName("tb_data_divinetradedata")
				//------ modify by wangzuochun 2011.03.30 BUG #1576 华安现金头寸预测表中没有考虑多组合的情况（CNYMoneyControl.java）
				+ " where FCheckState = 1 and FPortCode = " + dbl.sqlString(this.portcode) + " and FEXCHANGECODE = 'CH' and FTRADETYPECODE in ('01','02') and FSECURITYTYPE in ('EQ','GC','RE') and FSettleDate = " 
				+ dbl.sqlDate(date) + "group by FTRADETYPECODE"
				+ " union all (select (case when FTRIALMONEY < 0 then FTRIALMONEY * -1 else FTRIALMONEY end) as FTRIALMONEY,"
				+ "(case when FTRIALMONEY > 0 then '02' else '01' end) as FTRADETYPECODE from "//加上试算金额  二期需求
				//------ modify by wangzuochun 2011.03.30 BUG #1576 华安现金头寸预测表中没有考虑多组合的情况（CNYMoneyControl.java）
				+ pub.yssGetTableName("TB_DATA_DIVINETRIALMONEY") + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(this.portcode) + " and FEXCHANGECODE = 'CH' and FSETTLEDATE = " + dbl.sqlDate(date)
				+ " and FBARGAINDATE = " + dbl.sqlDate(this.checkDate) + ")) group by FTRADETYPECODE";
			rs = dbl.openResultSet(query);
			buyMoney = 0;//将初始值赋为0
			saleMoney = 0;
			while (rs.next()){
				if(rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//买入，应该为应付
					buyMoney += YssFun.roundIt(rs.getDouble("FMONEY"),2);
				}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//卖出，应该为应收
					saleMoney += YssFun.roundIt(rs.getDouble("FMONEY"),2);
				}
			}
			dbl.closeResultSetFinal(rs);
			moneyStr = moneyHash.get("12").toString();//当日人民币应收股债款
			moneyStr += YssFun.formatNumber(saleMoney,"#,##0.##") + "\t";
			moneyHash.put("12", moneyStr);
			moneyStr = moneyHash.get("13").toString();//当日人民币应付股债款
			moneyStr += YssFun.formatNumber(buyMoney,"#,##0.##") + "\t";
			moneyHash.put("13", moneyStr);
			//当日外币应收,应付股债款
			query = "select sum(FSettleMoney) as FMONEY,FTRADETYPECODE,FCURYCODE from (select a.FSettleMoney,"
				+ "a.FTRADETYPECODE,a.FSECURITYTYPE,b.FCURYCODE from " + pub.yssGetTableName("tb_data_divinetradedata")
				+ " a left join (select FCURYCODE, FCASHACCCODE from " + pub.yssGetTableName("tb_para_cashaccount")
				+ "  where fcheckstate = 1 and Fportcode = " + dbl.sqlString(this.portcode) + " and FSUBACCTYPE = '0101') b on a.FCASHACCCODE = b.FCASHACCCODE"
				+ " where a.FCheckState = 1 and a.Fportcode = " + dbl.sqlString(this.portcode) + " and a.FSECURITYTYPE in ('GC','EQ') and FTRADETYPECODE in ('01', '02') and a.FSettleDate = " + dbl.sqlDate(date)
				+ " and a.FEXCHANGECODE <> 'CH' and a.FCASHACCCODE in (select FCASHACCCODE from " + pub.yssGetTableName("tb_para_cashaccount")
				+ " where Fportcode = " + dbl.sqlString(this.portcode) + " and fcheckstate=1 and FSUBACCTYPE = '0101'))group by FTRADETYPECODE, FCURYCODE order by FTRADETYPECODE";//根据交易方式排序
			rs = dbl.openResultSet(query);
			buyMoney = 0;//将初始值赋为0
			saleMoney = 0;
			while(rs.next()){
				if(rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//买入，应该为应付
					buyMoney += YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString()),2);
				}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//卖出，应该为应收
					saleMoney += YssFun.roundIt(rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString()),2);
				}
			}
			dbl.closeResultSetFinal(rs);
			//外币试算金额包括US,TT,SP,HK市场
			//modify by fangjiang 2011.05.07 STORY #867
			query = "select sum(FTRIALMONEY) as FMONEY,FTRADETYPECODE,FCURYCODE from (select (case when FTRIALMONEY < 0 then FTRIALMONEY * -1 else FTRIALMONEY end) as FTRIALMONEY,"
				+ "(case when FTRIALMONEY > 0 then '02' else '01' end) as FTRADETYPECODE," 
				+ "(case FEXCHANGECODE when 'TT' then 'TWD' when 'SP' then 'SGD' when 'US' then 'USD' when 'LN' then 'GBP' when 'KS' then 'KRW' when 'IJ' then 'IDR' when 'MK' then 'MYR' when 'PM' then 'PHP' when 'TB' then 'THB'" +
				// ADD BY LEE 2011-12-13
				// START ADD   'IT','CA','FP','UN','UW'
				"when 'IT' then 'EUR'" +
				"when 'CA' then 'CAD'" +
				"when 'FP' then 'EUR'" +
				"when 'UN' then 'USD'" +
				"when 'UW' then 'USD'" +
				"when 'LI' then 'USD'" +
				"when 'AS' then 'AUD'" +
				// END ADD
						"  else 'HKD' end) as FCURYCODE from "
				+ pub.yssGetTableName("TB_DATA_DIVINETRIALMONEY") + " where FCheckState = 1 and FEXCHANGECODE in ('TT','SP','HK','US','LN','KS','IJ','MK','PM','TB'," +
				// ADD BY LEE
				// START ADD
				"'IT','CA','FP','UN','UW','AS','LI'" +
				// END ADD
						") and FBARGAINDATE = " + dbl.sqlDate(this.checkDate)				
				+ " and fsettledate = " + dbl.sqlDate(date) + " and FPORTCODE = " + dbl.sqlString(this.portcode) + ") group by FTRADETYPECODE,FCURYCODE";
			//----------------
			rs = dbl.openResultSet(query);
			while (rs.next()){
				if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//如果为买入
					buyMoney += rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString());
				}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//如果为卖出
					saleMoney += rs.getDouble("FMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCURYCODE")).toString());
				}
			}
			dbl.closeResultSetFinal(rs);//增加外币试算金额完毕
			moneyStr = moneyHash.get("15").toString();//当日外币应收股债款
			moneyStr += YssFun.formatNumber(saleMoney,"#,##0.##") + "\t";
			moneyHash.put("15", moneyStr);
			moneyStr = moneyHash.get("16").toString();//当日外币应付股债款
			moneyStr += YssFun.formatNumber(buyMoney,"#,##0.##") + "\t";
			moneyHash.put("16", moneyStr);//当日外币应收,应付股债款 处理完毕
			
			//处理定存应收应付
			query = "select a.FBMONEY, a.FTRADETYPECODE,a.FCASHACCCODE,b.FCuryCode from (select FBMONEY as FBMONEY,FBCASHACCCODE as FCASHACCCODE," +
				" FPORTCODE,'02' as FTRADETYPECODE from " + pub.yssGetTableName("tb_data_divineratetrade") +
				" where fcheckstate = 1 and FBREACHDATE = " + dbl.sqlDate(date) + " and FPORTCODE = " + dbl.sqlString(this.portcode) + 
				" and FBCASHACCCODE in (select fcashacccode from " + 
				pub.yssGetTableName("tb_para_cashaccount") + " where fcheckstate = 1" + " and FPortCode = " + dbl.sqlString(this.portcode) + 
				" and FAccType = '01' and FSubAccType = '0102' group by fcashacccode)" +
	    	    " union all (select FSMONEY as FBMONEY,FSCASHACCCODE as FCASHACCCODE, FPORTCODE,'01' as FTRADETYPECODE from " +
	        	pub.yssGetTableName("tb_data_divineratetrade") + " where fcheckstate = 1 and FPORTCODE = " + dbl.sqlString(this.portcode) +
	        	" and FREACHDATE = " + dbl.sqlDate(date) + " and FSCASHACCCODE in (select fcashacccode from " +
	        	pub.yssGetTableName("tb_para_cashaccount") + " where fcheckstate = 1" + " and FPortCode = " + dbl.sqlString(this.portcode) +
	        	" and FAccType = '01' and FSubAccType = '0102' group by fcashacccode)" +
	        	")) a left join (select FCashAccCode,FCuryCode from " + pub.yssGetTableName("tb_para_cashaccount") +
	        	" where FPortCode = " + dbl.sqlString(this.portcode) + " and FSUBACCTYPE = '0102' group by FCashAccCode,FCuryCode) b on a.FCASHACCCODE = b.FCashAccCode";
			rs = dbl.openResultSet(query);
			d = 0;
			while (rs.next()){
				if (rs.getString("FCuryCode").equalsIgnoreCase("CNY")){
					if(rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//流入
						d += YssFun.roundIt(rs.getDouble("FBMONEY"),2);
					}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//流出
						d -= YssFun.roundIt(rs.getDouble("FBMONEY"),2);
					}
				}else{
					if(rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//流入
						d += YssFun.roundIt(rs.getDouble("FBMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCuryCode")).toString()),2);
					}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//流出
						d -= YssFun.roundIt(rs.getDouble("FBMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCuryCode")).toString()),2);
					}
				}
			}
			dbl.closeResultSetFinal(rs);
			moneyStr = moneyHash.get("17").toString();
			moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
			moneyHash.put("17", moneyStr);//定存应收应付处理完毕
			
			//处理股指期货(初试保证金应收应付)
			query = "select a.FBMONEY, a.FTRADETYPECODE,a.FCASHACCCODE,b.FCuryCode from (select FBMONEY as FBMONEY,FBCASHACCCODE as FCASHACCCODE," +
        			" FPORTCODE,'02' as FTRADETYPECODE from " + pub.yssGetTableName("tb_data_divineratetrade") +
        			" where fcheckstate = 1 and FBREACHDATE = " + dbl.sqlDate(date) + " and FPORTCODE = " + dbl.sqlString(this.portcode) + 
        			" and FBCASHACCCODE in (select fcashacccode from " + 
        			pub.yssGetTableName("tb_para_cashaccount") + " where fcheckstate = 1" + " and FPortCode = " + dbl.sqlString(this.portcode) + 
        			" and FAccType = '03' and FSubAccType = '0301' group by fcashacccode)" +
            	    " union all (select FSMONEY as FBMONEY,FSCASHACCCODE as FCASHACCCODE, FPORTCODE,'01' as FTRADETYPECODE from " +
                	pub.yssGetTableName("tb_data_divineratetrade") + " where fcheckstate = 1 and FPORTCODE = " + dbl.sqlString(this.portcode) +
                	" and FREACHDATE = " + dbl.sqlDate(date) + " and FSCASHACCCODE in (select fcashacccode from " +
                	pub.yssGetTableName("tb_para_cashaccount") + " where fcheckstate = 1" + " and FPortCode = " + dbl.sqlString(this.portcode) +
                	" and FAccType = '03' and FSubAccType = '0301' group by fcashacccode)" +
                	")) a left join (select FCashAccCode,FCuryCode from " + pub.yssGetTableName("tb_para_cashaccount") +
                	" where FPortCode = " + dbl.sqlString(this.portcode) + " and FSUBACCTYPE = '0301' group by FCashAccCode,FCuryCode) b on a.FCASHACCCODE = b.FCashAccCode";
			rs = dbl.openResultSet(query);
			d = 0;
			while (rs.next()){
				if(rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//流入
					d += YssFun.roundIt(rs.getDouble("FBMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCuryCode")).toString()),2);
				}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//流出
					d -= YssFun.roundIt(rs.getDouble("FBMONEY") * YssFun.toDouble(rateHash.get(rs.getString("FCuryCode")).toString()),2);
				}
			}
			dbl.closeResultSetFinal(rs);
			moneyStr = moneyHash.get("18").toString();
			moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
			moneyHash.put("18", moneyStr);//股指期货(初试保证金应收应付)处理完毕
			
			//处理资金划拨
			query = "select sum(FAMOUNTALLOCATED) as FMONEY from " + pub.yssGetTableName("TB_CASH_CAPITALALLOCATION")
				+ " where FCHECKSTATE = 1 and FPORTCODE = " + dbl.sqlString(this.portcode) + " and FSETTLEDATE = " + dbl.sqlDate(date);
			d = 0;
			rs = dbl.openResultSet(query);
			while (rs.next()){
				d += YssFun.roundIt(rs.getDouble("FMONEY"),2);
			}
			dbl.closeResultSetFinal(rs);
			moneyStr = moneyHash.get("3").toString();
			moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
			moneyHash.put("3", moneyStr);//资金划拨处理完毕
			
			//处理定存
			moneyStr = moneyHash.get("7").toString();
			d = YssFun.roundIt(YssFun.toDouble(moneyStr.split("\t")[i])//前一日余额
				+ YssFun.toDouble(moneyHash.get("17").toString().split("\t")[i+1]),2);//定存应收应付
			moneyStr += YssFun.formatNumber(YssFun.roundIt(d,2),"#,##0.##") + "\t";
			moneyHash.put("7", moneyStr);
			
			//处理股指期货(初试保证金)
			moneyStr = moneyHash.get("8").toString();
			d = YssFun.roundIt(YssFun.toDouble(moneyStr.split("\t")[i])//前一日余额
				+ YssFun.toDouble(moneyHash.get("18").toString().split("\t")[i+1]),2);//股指期货(初试保证金应收应付)
			moneyStr += YssFun.formatNumber(YssFun.roundIt(d,2),"#,##0.##") + "\t";
			moneyHash.put("8", moneyStr);
			
			//处理RMB ASSET
			moneyStr = moneyHash.get("0").toString();
			d = YssFun.roundIt(YssFun.toDouble(moneyStr.split("\t")[i])//前一日余额
				+ YssFun.toDouble(moneyHash.get("10").toString().split("\t")[i+1])//应收申购款
				- YssFun.toDouble(moneyHash.get("11").toString().split("\t")[i+1])//应付赎回款
				+ YssFun.toDouble(moneyHash.get("12").toString().split("\t")[i+1])//当日人民币应收股债
				- YssFun.toDouble(moneyHash.get("13").toString().split("\t")[i+1])//当日人民币应付股债
				+ YssFun.toDouble(moneyHash.get("4").toString().split("\t")[i+1])//换汇
				- YssFun.toDouble(moneyHash.get("17").toString().split("\t")[i+1]),2);//定存应收应付
			moneyStr += YssFun.formatNumber(YssFun.roundIt(d,2),"#,##0.##") + "\t";
			moneyHash.put("0", moneyStr);
			
			//处理人民币可调度部位
			moneyStr = moneyHash.get("1").toString();
			d = YssFun.roundIt(YssFun.toDouble(moneyStr.split("\t")[i])//前一日余额
				+ YssFun.toDouble(moneyHash.get("12").toString().split("\t")[i])//前一日人民币应收股债
				+ YssFun.toDouble(moneyHash.get("10").toString().split("\t")[i])//前一日应收申购款
				- YssFun.toDouble(moneyHash.get("11").toString().split("\t")[i+1])//应付赎回款
				- YssFun.toDouble(moneyHash.get("13").toString().split("\t")[i+1])//当日人民币应付股债
				+ YssFun.toDouble(moneyHash.get("4").toString().split("\t")[i+1])//换汇
				- YssFun.toDouble(moneyHash.get("17").toString().split("\t")[i+1]),2);//定存应收应付
			moneyStr += YssFun.formatNumber(YssFun.roundIt(d,2),"#,##0.##") + "\t";
			moneyHash.put("1", moneyStr);
			
			//处理BB可用头寸
			if(i == 0){//如果是T日
/*				query = "select sum(FAMOUNTALLOCATED) as FMONEY from " + pub.yssGetTableName("TB_CASH_CAPITALALLOCATION")
				+ " where FCHECKSTATE = 1 and FPORTCODE = " + dbl.sqlString(this.portcode) + " and FSETTLEDATE <= " + dbl.sqlDate(date);
				d = 0;
				rs = dbl.openResultSet(query);
				while (rs.next()){
					d = d + rs.getDouble("FMONEY");//减资金划拨
				}
				dbl.closeResultSetFinal(rs);
				//国内固定收益买入卖出
				query = "select sum(FSettleMoney) as FMONEY,FTRADETYPECODE from " 
					+ pub.yssGetTableName("tb_data_divinetradedata")
					+ " where FCheckState = 1 and FEXCHANGECODE = 'CH' and FTRADETYPECODE in ('01','02') and FSECURITYTYPE = 'GC' and FSettleDate <= " 
					+ dbl.sqlDate(date) + "group by FTRADETYPECODE";
				rs = dbl.openResultSet(query);
				while (rs.next()){
					if(rs.getString("FTRADETYPECODE").equalsIgnoreCase("01")){//买入，应该为应付
						d = d - rs.getDouble("FMONEY");//减固定收益买入
					}else if (rs.getString("FTRADETYPECODE").equalsIgnoreCase("02")){//卖出，应该为应收
						d = d + rs.getDouble("FMONEY");//加固定收益卖出
					}
				}
				dbl.closeResultSetFinal(rs);
				d = YssFun.roundIt(YssFun.toDouble(moneyHash.get("1").toString().split("\t")[i])+ d,2); //T-1日人民币可调度部位+d
				*/
				d = YssFun.toDouble(moneyHash.get("0").toString().split("\t")[i])//T-1日的RMB ASSET余额
					+ YssFun.toDouble(moneyHash.get("4").toString().split("\t")[i+1])//+T日的换汇金额
					+ YssFun.toDouble(moneyHash.get("3").toString().split("\t")[i+1])//+T日资金划拨发生额
//CYB 20100909	   	- YssFun.toDouble(moneyHash.get("17").toString().split("\t")[i+1])//-T日定存应收应付
					- this.fSKIWBanlance;//(T-1日)恒生余额
			}else{//T日之后的处理方式
				d = YssFun.toDouble(moneyHash.get("2").toString().split("\t")[i]);//BB可用头寸前一日余额
			}
			// + 应收申购款-应付赎回款
			d = YssFun.roundIt(d + YssFun.toDouble(moneyHash.get("10").toString().split("\t")[i+1])//应收申购款
				- YssFun.toDouble(moneyHash.get("11").toString().split("\t")[i+1]),2);//应付赎回款
			moneyStr = moneyHash.get("2").toString();
			moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
			moneyHash.put("2", moneyStr);//BB可用头寸处理完毕
			
			//处理银行存款
			moneyStr = moneyHash.get("9").toString();
			d = YssFun.roundIt(YssFun.toDouble(moneyStr.split("\t")[i])//前一日余额
				+ YssFun.toDouble(moneyHash.get("10").toString().split("\t")[i+1])//应收申购款
				- YssFun.toDouble(moneyHash.get("11").toString().split("\t")[i+1])//应付赎回款
				+ YssFun.toDouble(moneyHash.get("12").toString().split("\t")[i+1])//当日人民币应收股债
				- YssFun.toDouble(moneyHash.get("13").toString().split("\t")[i+1])//当日人民币应付股债
				+ YssFun.toDouble(moneyHash.get("15").toString().split("\t")[i+1])//当日外币应收股债
				- YssFun.toDouble(moneyHash.get("16").toString().split("\t")[i+1])//当日外币应付股债
				+ YssFun.toDouble(moneyHash.get("14").toString().split("\t")[i+1])//欧美股债应收应付（预估）
				- YssFun.toDouble(moneyHash.get("17").toString().split("\t")[i+1])//定存应收应付
				- YssFun.toDouble(moneyHash.get("18").toString().split("\t")[i+1]),2);//股指期货(初试保证金应收应付)
			moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
			moneyHash.put("9", moneyStr);
			//处理净资产
			moneyStr = moneyHash.get("19").toString();
			//add by yanghaiming 20101014 MS01828 QDV4华安2010年10月08日01_AB 
			//算法变更，比例需要加上T-1日持股比例
			//d = YssFun.roundIt(YssFun.toDouble(moneyHash.get("22").toString().split("\t")[i]) * (1 + this.scale + YssFun.toDouble(moneyHash.get("39").toString().split("\t")[i])),2);
			//edit by yanghaiming 20101201 净资产算法变更
			d = 0;
			if(i == 0){//如果是T日
				d = YssFun.roundIt(YssFun.toDouble(moneyHash.get("22").toString().split("\t")[i]) + (YssFun.toDouble(moneyHash.get("38").toString().split("\t")[i]) * this.scale), 2);
			}else{
				d = YssFun.roundIt(YssFun.toDouble(moneyHash.get("22").toString().split("\t")[i]),2);
			}
			moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
			moneyHash.put("19", moneyStr);//净资产处理完毕
			//预估净资产
			moneyStr = moneyHash.get("22").toString();
d = YssFun.roundIt(YssFun.toDouble(moneyHash.get("19").toString().split("\t")[i+1])//净资产
				+ YssFun.toDouble(moneyHash.get("20").toString().split("\t")[i+1])//人民币申购款
				- YssFun.toDouble(moneyHash.get("21").toString().split("\t")[i+1])//人民币赎回款
				/** add by zhaoxianlin  20130129 STORY #2913 头寸管控表TA数据的读入接口和人民币外币头寸管控表需要修改--start-*/  
			   + YssFun.toDouble(moneyHash.get("44").toString().split("\t")[i+1])//美元申购款
			   - YssFun.toDouble(moneyHash.get("45").toString().split("\t")[i+1]),2);//美元赎回款
			/** add by zhaoxianlin  20130129 STORY #2913 头寸管控表TA数据的读入接口和人民币外币头寸管控表需要修改--end-*/  
			moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
			moneyHash.put("22", moneyStr);
			
			//add by yanghaiming 20101111 QDV4华安2010年10月08日02_AB 
			//处理固定收益
			moneyStr = moneyHash.get("40").toString();
			d = YssFun.roundIt(YssFun.toDouble(moneyHash.get("40").toString().split("\t")[i])//T-1日固定收益
				+ YssFun.toDouble(moneyHash.get("28").toString().split("\t")[i+1])//国内固定收益买入
				- YssFun.toDouble(moneyHash.get("29").toString().split("\t")[i+1])//国内固定收益卖出
				+ YssFun.toDouble(moneyHash.get("30").toString().split("\t")[i+1])//海外市场固定收益买入总金额
				- YssFun.toDouble(moneyHash.get("31").toString().split("\t")[i+1])//海外市场固定收益卖出总金额
				+ YssFun.toDouble(moneyHash.get("17").toString().split("\t")[i+1]),2);//定存应收应付
			moneyStr += YssFun.formatNumber(d,"#,##0.##") + "\t";
			moneyHash.put("40", moneyStr);
			//add by yanghaiming 20101111 QDV4华安2010年10月08日02_AB 
			
			
			//一年内到期政府债及央票比例
			buyMoney = YssFun.roundIt(YssFun.toDouble(moneyHash.get("6").toString().split("\t")[i+1])//一年内到期政府债及央票
				/ YssFun.toDouble(moneyHash.get("22").toString().split("\t")[i+1]),4);//预估净资产
			buyMoney = buyMoney * 100;
			moneyStr = moneyHash.get("23").toString();
			moneyStr += YssFun.formatNumber(YssFun.roundIt(buyMoney,2),"#,##0.##") + "%\t";
			moneyHash.put("23", moneyStr);
			
			//银行存款比例
			saleMoney = YssFun.roundIt(YssFun.toDouble(moneyHash.get("9").toString().split("\t")[i+1])//银行存款
				/ YssFun.toDouble(moneyHash.get("22").toString().split("\t")[i+1]),4);//预估净资产
			saleMoney = saleMoney * 100;
			moneyStr = moneyHash.get("24").toString();
			moneyStr += YssFun.roundIt(saleMoney,2) + "%\t";
			moneyHash.put("24", moneyStr);
			
			//現金比例
			d = buyMoney + saleMoney;
			moneyStr = moneyHash.get("25").toString();
			moneyStr += YssFun.roundIt(d,2) + "%\t";
			moneyHash.put("25", moneyStr);
			
			//持股比例
			buyMoney = YssFun.roundIt(YssFun.toDouble(moneyHash.get("38").toString().split("\t")[i+1])//一年内到期政府债及央票
					/ YssFun.toDouble(moneyHash.get("22").toString().split("\t")[i+1]),4);//预估净资产
			buyMoney = buyMoney * 100;
			moneyStr = moneyHash.get("39").toString();
			moneyStr += YssFun.roundIt(buyMoney,2) + "%\t";
			moneyHash.put("39", moneyStr);
			
			//edit by yanghaiming 20101111 QDV4华安2010年10月08日02_AB 
			//固定收益比例
			saleMoney = YssFun.roundIt(YssFun.toDouble(moneyHash.get("40").toString().split("\t")[i+1])//固定收益比例
					/ YssFun.toDouble(moneyHash.get("22").toString().split("\t")[i+1]),4);//预估净资产
			saleMoney = saleMoney * 100;
			moneyStr = moneyHash.get("41").toString();
			moneyStr += YssFun.roundIt(saleMoney,2) + "%\t";
			moneyHash.put("41", moneyStr);
			
			//股指期货(保证金)比例
			buyMoney1 = YssFun.roundIt(YssFun.toDouble(moneyHash.get("8").toString().split("\t")[i+1])//股指期货(保证金)比例
					/ YssFun.toDouble(moneyHash.get("22").toString().split("\t")[i+1]),4);//预估净资产
			buyMoney1 = buyMoney1 * 100;
			moneyStr = moneyHash.get("42").toString();
			moneyStr += YssFun.roundIt(buyMoney1,2) + "%\t";
			moneyHash.put("42", moneyStr);
			//edit by yanghaiming 20101111 QDV4华安2010年10月08日02_AB 
			
			//比率合计
			d = YssFun.toDouble(moneyHash.get("24").toString().split("\t")[i+1])*100 + buyMoney + saleMoney + buyMoney1;
			moneyStr = moneyHash.get("43").toString();
			moneyStr += YssFun.roundIt(d,2) + "%\t";
			moneyHash.put("43", moneyStr);
			
		}catch (Exception e) {
			throw new YssException("【执行生成头寸表时出错......】");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	//将数据处理到存放CNYMoneyControlBean对象的controlDateParam中
	private void checkInHash() throws YssException {
		 String[] aryMoney = {};
	for (int i = 0;i<=45;i++){  //modified by zhaoxianlin  20130129 STORY #2913 增加两行统计项 43-->45
			CNYMoneyControlBean cmcBean = new CNYMoneyControlBean();
			cmcBean = (CNYMoneyControlBean)cnyMoneyControlBean.clone();
			switch(i){
				case 0:
					cmcBean.setfNumName("RMB ASSET");
					break;
				case 1:
					cmcBean.setfNumName("人民币可调度部位");
					break;
				case 2:
					cmcBean.setfNumName("BB可用头寸");
					break;
				case 3:
					cmcBean.setfNumName("资金划拨");
					break;
				case 4:
					cmcBean.setfNumName("换汇");
					break;
				case 5:
					cmcBean.setfNumName(" ");
					break;
				case 6:
					cmcBean.setfNumName("一年内到期政府债及央票(余额)");
					break;
				case 7:
					cmcBean.setfNumName("定存");
					break;
				case 8:
					cmcBean.setfNumName("股指期货(保证金)");
					break;
				case 9:
					cmcBean.setfNumName("银行存款(人+外)");
					break;
				case 10:
					cmcBean.setfNumName("应收申购款");
					break;
				case 11:
					cmcBean.setfNumName("应付赎回款");
					break;
				case 12:
					cmcBean.setfNumName("当日人民币应收股债款");
					break;
				case 13:
					cmcBean.setfNumName("当日人民币应付股债款");
					break;
				case 14:
					cmcBean.setfNumName("欧美股债应收应付款(预估)");
					break;
				case 15:
					cmcBean.setfNumName("当日外币应收股债款");
					break;
				case 16:
					cmcBean.setfNumName("当日外币应付股债款");
					break;
				case 17:
					cmcBean.setfNumName("定存应收应付");
					break;
				case 18:
					cmcBean.setfNumName("股指期货(初始保证金应收应付)");
					break;
				case 19:
					cmcBean.setfNumName("净资产");
					break;
                case 20:
					cmcBean.setfNumName("人民币份额申购款（T+3）");//modified by zhaoxianlin  20130129 STORY #2913申购款（T+3）-->人民币份额申购款（T+3）
					break;
				case 21:
					cmcBean.setfNumName("人民币份额赎回款（T+8）");////modified by zhaoxianlin  20130129 STORY #2913赎回款（T+8）-->人民币份额赎回款（T+8）
					break;
				case 22:
					cmcBean.setfNumName("预估净资产");
					break;
				case 23:
					cmcBean.setfNumName("一年内到期政府债及央票比例");
					break;
				case 24:
					cmcBean.setfNumName("银行存款比例");
					break;
				case 25:
					cmcBean.setfNumName("現金比例");
					break;
				case 26:
					cmcBean.setfNumName("人民币股票买进金额");
					break;
				case 27:
					cmcBean.setfNumName("人民币股票卖出金额");
					break;
				case 28:
					cmcBean.setfNumName("国内固定收益买入总金额");
					break;
				case 29:
					cmcBean.setfNumName("国内固定收益卖出总金额");
					break;
				case 30:
					cmcBean.setfNumName("海外市场（除中国）固定收益买入总金额");
					break;
				case 31:
					cmcBean.setfNumName("海外市场（除中国）固定收益卖出总金额");
					break;
				case 32:
					cmcBean.setfNumName("亚太市场买入");
					break;
				case 33:
					cmcBean.setfNumName("亚太市场卖出");
					break;
				case 34:
					cmcBean.setfNumName("欧美市场买入");
					break;
				case 35:
					cmcBean.setfNumName("欧美市场卖出");
					break;
				case 36:
					cmcBean.setfNumName("欧美市场预估数据");
					break;
				case 37:
					cmcBean.setfNumName("国外股票变动小计");
					break;
				case 38:
					cmcBean.setfNumName("股票");
					break;
				case 39:
					cmcBean.setfNumName("持股比例");
					break;
				case 40:
					cmcBean.setfNumName("固定收益");
					break;
				case 41:
					cmcBean.setfNumName("固定收益比例");
					break;
				case 42:
					cmcBean.setfNumName("股指期货(保证金)比例");
					break;
				case 43:
					cmcBean.setfNumName("比率合计");
					break;
              /** add by zhaoxianlin  20130129 STORY #2913 头寸管控表TA数据的读入接口和人民币外币头寸管控表需要修改--start-*/  
				case 44:
				     cmcBean.setfNumName("美元份额申购款（T+3）");
				     break;
				case 45:
				     cmcBean.setfNumName("美元份额赎回款（T+8）");
				     break;
					/** add by zhaoxianlin  20130129 STORY #2913 头寸管控表TA数据的读入接口和人民币外币头寸管控表需要修改--start-*/  
				default: cmcBean.setfNumName(" ");
			}
			cmcBean.setfNum(i);
			moneyStr = moneyHash.get(i+"").toString();
			aryMoney = moneyStr.split("\t");
			doMoneyStr(aryMoney,cmcBean);//add by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整
//			cmcBean.setFmoney0(moneyStr.split("\t")[0]);
//			cmcBean.setFmoney1(moneyStr.split("\t")[1]);
//			cmcBean.setFmoney2(moneyStr.split("\t")[2]);
//			cmcBean.setFmoney3(moneyStr.split("\t")[3]);
//			cmcBean.setFmoney4(moneyStr.split("\t")[4]);
//			cmcBean.setFmoney5(moneyStr.split("\t")[5]);
//			cmcBean.setFmoney6(moneyStr.split("\t")[6]);
//			cmcBean.setFmoney7(moneyStr.split("\t")[7]);
//			cmcBean.setFmoney8(moneyStr.split("\t")[8]);
//			cmcBean.setFmoney9(moneyStr.split("\t")[9]);
//			cmcBean.setFmoney10(moneyStr.split("\t")[10]);
//			cmcBean.setFmoney11(moneyStr.split("\t")[11]);
			
			controlDateParam.put(i+"", cmcBean);
		}
	}
	//----add by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整---start---//
	/**
	 * 预估固定11天数据调整为前台传入的参数控制，这里赋值调整
	 */
	public void doMoneyStr(String[] aryMoney,CNYMoneyControlBean cmcBean) throws YssException{
		try{
			for(int i=0;i<=searchDays;i++){
				setFmoney(i,aryMoney[i],cmcBean);
			}
		}catch(Exception e){
			throw new YssException("生成查询天数对应的头寸金额出错！\n", e);
		}finally{
		}		
	}
	public void setFmoney(int i,String aryMoney,CNYMoneyControlBean cmcBean){
		if(i==0){
			cmcBean.setFmoney0(aryMoney);
		}else if(i==1){
			cmcBean.setFmoney1(aryMoney);
		}else if(i==2){
			cmcBean.setFmoney2(aryMoney);
		}else if(i==3){
			cmcBean.setFmoney3(aryMoney);
		}else if(i==4){
			cmcBean.setFmoney4(aryMoney);
		}else if(i==5){
			cmcBean.setFmoney5(aryMoney);
		}else if(i==6){
			cmcBean.setFmoney6(aryMoney);
		}else if(i==7){
			cmcBean.setFmoney7(aryMoney);
		}else if(i==8){
			cmcBean.setFmoney8(aryMoney);
		}else if(i==9){
			cmcBean.setFmoney9(aryMoney);
		}else if(i==10){
			cmcBean.setFmoney10(aryMoney);
		}else if(i==11){
			cmcBean.setFmoney11(aryMoney);
		}else if(i==12){
			cmcBean.setFmoney12(aryMoney);
		}else if(i==13){
			cmcBean.setFmoney13(aryMoney);
		}else if(i==14){
			cmcBean.setFmoney14(aryMoney);
		}else if(i==15){
			cmcBean.setFmoney15(aryMoney);
		}else if(i==16){
			cmcBean.setFmoney16(aryMoney);
		}else if(i==17){
			cmcBean.setFmoney17(aryMoney);
		}else if(i==18){
			cmcBean.setFmoney18(aryMoney);
		}else if(i==19){
			cmcBean.setFmoney19(aryMoney);
		}else if(i==20){
			cmcBean.setFmoney20(aryMoney);
		}else if(i==21){
			cmcBean.setFmoney21(aryMoney);
		}
	}
	//----add by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整---end---//
	
	//执行插入数据
	private void checkIn() throws YssException{
		PreparedStatement stm=null;
        String sqlStr="";
        Connection conn = null;
		boolean bTrans = false;
        try{
        	conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            
            sqlStr = "insert into " + pub.yssGetTableName("TB_DATA_CNYMONEYCONTROL")
            	+ " (FDATE,FPORTCODE,FREPORTTYPE,FNUM,FNUMNAME,FMONEY0,FMONEY1,FMONEY2,FMONEY3,FMONEY4,FMONEY5,FMONEY6,FMONEY7,FMONEY8,FMONEY9,FMONEY10,FMONEY11"+
            	  ",FMONEY12,FMONEY13,FMONEY14,FMONEY15,FMONEY16,FMONEY17,FMONEY18,FMONEY19,FMONEY20,FMONEY21) values" //modified by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整 增加Fmoney12-Fmoney21字段
            	+ " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            stm = conn.prepareStatement(sqlStr);
            for (int i = 0; i <= 45; i++) {
            	cnyMoneyControlBean = (CNYMoneyControlBean)controlDateParam.get(i+"");
            	stm.setDate(1, YssFun.toSqlDate(this.checkDate));
            	stm.setString(2, this.portcode);
            	stm.setInt(3, this.checkType);
            	stm.setInt(4, cnyMoneyControlBean.getfNum());
            	stm.setString(5, cnyMoneyControlBean.getfNumName());
            	stm.setString(6, cnyMoneyControlBean.getFmoney0());
            	stm.setString(7, cnyMoneyControlBean.getFmoney1());
            	stm.setString(8, cnyMoneyControlBean.getFmoney2());
            	stm.setString(9, cnyMoneyControlBean.getFmoney3());
            	stm.setString(10, cnyMoneyControlBean.getFmoney4());
            	stm.setString(11, cnyMoneyControlBean.getFmoney5());
            	stm.setString(12, cnyMoneyControlBean.getFmoney6());
            	stm.setString(13, cnyMoneyControlBean.getFmoney7());
            	stm.setString(14, cnyMoneyControlBean.getFmoney8());
            	stm.setString(15, cnyMoneyControlBean.getFmoney9());
            	stm.setString(16, cnyMoneyControlBean.getFmoney10());
            	stm.setString(17, cnyMoneyControlBean.getFmoney11());
            	//----add by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整---start---//
            	stm.setString(18, cnyMoneyControlBean.getFmoney12());
            	stm.setString(19, cnyMoneyControlBean.getFmoney13());
            	stm.setString(20, cnyMoneyControlBean.getFmoney14());
            	stm.setString(21, cnyMoneyControlBean.getFmoney15());
            	stm.setString(22, cnyMoneyControlBean.getFmoney16());
            	stm.setString(23, cnyMoneyControlBean.getFmoney17());
            	stm.setString(24, cnyMoneyControlBean.getFmoney18());
            	stm.setString(25, cnyMoneyControlBean.getFmoney19());
            	stm.setString(26, cnyMoneyControlBean.getFmoney20());
            	stm.setString(27, cnyMoneyControlBean.getFmoney21());
            	//----add by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整---start---//
            	stm.addBatch();
            }
            stm.executeBatch();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }catch(Exception e){
			throw new YssException("【执行人民币头寸表插入数据时报错......】");
		}finally{
			dbl.closeStatementFinal(stm);
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	private String isConfirm() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		String strResult = "";
		try {
			strSql = "select * from "
				+ pub.yssGetTableName("TB_DATA_CNYMONEYCONTROL")
				+ " where FDATE = " + dbl.sqlDate(this.checkDate) + " and FPORTCODE = " + dbl.sqlString(this.portcode) + " and FREPORTTYPE = " + this.checkType;
			rs = dbl.openResultSet(strSql);
			if (rs.next()){
				strResult = "true";
			}
			return strResult;
		} catch (Exception e) {
			throw new YssException("【生成人民币头寸表时出错....】");
		} finally{
			dbl.closeResultSetFinal(rs);
		}
	}
}
