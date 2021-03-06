package com.yss.main.operdeal.report.moneycontrol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.yss.base.BaseAPOperValue;
import com.yss.main.dao.IClientOperRequest;
import com.yss.main.operdata.TradeSubBean;
import com.yss.main.operdeal.report.moneycontrol.pojo.MoneyControlBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

/***
 * 2010.08.03
 * 为方便报表取数：1表示流入，-1表示流出，-2表示余额
 * @author yanghaiming MS01461 QDV4华安2010年7月20日01_A
 */
public class MoneyControl extends BaseAPOperValue implements IClientOperRequest {

	// ~Properties
	private String portcode = "";// 组合代码
	private Date checkDate;// 报表生成日期
	private Date endDate;// 预测数据截止日期
	private int checkType;// 报表生成方式（0表示预估(晚上)，1表示确认（早上））
	private String dateWhereSql = "";// 保存查询现金账户的sql
	private ArrayList ary = new ArrayList();// 存放现金账户名称
	HashMap dateParam = new HashMap(128);// 存放日期对应的T+?日及星期
	HashMap controlDateParam = new HashMap();// 存放管控流入流出
	HashMap rateHash = new HashMap(32);//存放各币种的汇率数据

	private double dHSBCUsd = 0;//USD余额
	private double dHSBCHkd = 0;//HKD余额
	private double dHSBCSgd = 0;//SGD余额
	private double dHSBCTwd = 0;//TWD余额
	private double dICBCUsd = 0;//工行USD余额
	private double dICBCHkd = 0;//工行HKD余额
	private double dICBCCny = 0;//工行CNY余额
	private double dHSBCCHkd = 0;//港币初始保证金余额

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
		String resultStr = "";
		confirms = sType.split("\b\f\b");
		this.checkDate = YssFun.toDate(confirms[0].toString());
		this.portcode = confirms[1].toString();
		this.checkType = Integer.parseInt(confirms[2].toString());
		if (confirms[3].equalsIgnoreCase("check")) {
			this.dateWhereSql = " select max(fstartdate) from  "
					+ pub.yssGetTableName("tb_para_cashaccount")
					+ " where fcheckstate=1 and fportcode = " + dbl.sqlString(this.portcode) + " and FBankCode =";
			ary.add("HSBC-USD");
			ary.add("HSBC-HKD");
			ary.add("HSBC-SGD");
			ary.add("HSBC-TWD");
			ary.add("ICBC-USD");
			ary.add("ICBC-HKD");
			ary.add("ICBC-CNY");
			getRate();
			Calendar c = Calendar.getInstance();
			beforeCheck(checkDate);
			getBeginBalance();
			for (int i = 0; i <= 10; i++) {// 需生成T至T+10日的数据
				if (i == 0) {// 保存日期对应的T+?数据
					this.endDate = this.checkDate;
					c.setTime(endDate);
					dateParam.put(endDate, "T\t" + c.get(Calendar.DAY_OF_WEEK));
				} else {
					c.setTime(endDate);
					dateParam.put(endDate, "T+" + i + "\t"
							+ c.get(Calendar.DAY_OF_WEEK));
				}
				controlDateParam.put(endDate.toString() + "-1", null);
				controlDateParam.put(endDate.toString() + "1", null);// 预存需插入的流入流出对象
				if(i != 10){//最后一天不对日期进行处理
					endDate = YssFun.addDay(endDate, 1);// 每天执行完日期加1
					c.setTime(endDate);
					if (c.get(Calendar.DAY_OF_WEEK) == 7) {// 如果为星期六，则日期再加2
						endDate = YssFun.addDay(endDate, 2);
					}
				}
			}
			doForecast();// 调用查询T日至T+10日的流入流出数据
			doAddForecase();//调用插入流入流出及余额的方法
		}else if (confirms[3].equalsIgnoreCase("checkstate")) {
			resultStr = isConfirm();
		}
		return resultStr;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		// TODO Auto-generated method stub

	}

	//获取各币种的汇率数据，保存至hashmap中
	private void getRate() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		int countFlag = 0;//判断有无取到汇率
		try {
			strSql = "select * from "
					+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
					+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'USD' and FEXRATEDATE = (select max(FEXRATEDATE) from "
					+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'USD' and FEXRATEDATE <= " 
					+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
					+ ") union (select * from "
					+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
					+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'HKD' and FEXRATEDATE = (select max(FEXRATEDATE) from "
					+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'HKD' and FEXRATEDATE <= " 
					+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
					+ ")) union (select * from "
					+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
					+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'SGD' and FEXRATEDATE = (select max(FEXRATEDATE) from "
					+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'SGD' and FEXRATEDATE <= " 
					+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate))
					+ ")) union (select * from "
					+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE")
					+ " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'TWD' and FEXRATEDATE = (select max(FEXRATEDATE) from "
					+ pub.yssGetTableName("TB_DATA_DIVINEEXRATE") + " where FCheckState = 1 and FDCURYCODE = 'CNY' and FFCURYCODE = 'TWD' and FEXRATEDATE <= " 
					+ dbl.sqlDate(YssFun.toSqlDate(this.checkDate)) + "))";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				rateHash.put(rs.getString("FFCURYCODE"), rs.getString("FEXRATE"));
				if(rs.getString("FFCURYCODE").equalsIgnoreCase("USD") || rs.getString("FFCURYCODE").equalsIgnoreCase("SGD") || rs.getString("FFCURYCODE").equalsIgnoreCase("TWD")){
					countFlag ++;
				}
			}
			if(countFlag!=3){
				throw new YssException("【汇率数据不完整，请导入汇率后再生成外币头寸表......】");
			}
		} catch (YssException e) {
			throw new YssException("【执行查询汇率时出错......】");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new YssException("【执行查询汇率时出错......】");
		} finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	// 生成头寸表之前先将相关数据删除
	private void beforeCheck(Date date) throws YssException {
		String strSql = "";
		try {
			strSql = "delete from "
					+ pub.yssGetTableName("TB_DATA_MONEYCONTROL")
					+ " where FPORTCODE = " + dbl.sqlString(this.portcode)
					+ " and FDATE = " + dbl.sqlDate(YssFun.toSqlDate(date))
					+ " and FREPORTTYPE = " + this.checkType;
			dbl.executeSql(strSql);
		} catch (Exception e) {
			throw new YssException("【执行外币管控数据删除时报错......】");
		}
	}

	// 通过组合代码以及日期获取期初余额
	private void getBeginBalance() throws YssException {
		boolean bTrans = false;
		Connection conn = null;
		ResultSet rs = null;
		String query = "";
		String insertSql = "";
		PreparedStatement pst = null;
		boolean flag = false;
		int count = 0;
		String errorMessage = "";
		try {
			query = " select "
					+ dbl.sqlDate(this.checkDate)
					+ "-1 as fdate,' '  fweekday,-2 as FINOUT ,a1.FMONEY as FHSBCUSD,a2.FMONEY as FHSBCHKD,a3.FMONEY as FHSBCSGD,"
					+ " a4.FMONEY as FHSBCTWD,a5.FMONEY as FICBCUSD,a6.FMONEY as FICBCHKD,a7.FMONEY as FICBCCNY,a8.FMONEY as FHSBCCHKD from "
					+
					// -----------------------------------------------------------------------------------------------------------------------------//
					" (select max(FSTARTDATE) as FSTARTDATE,fportcode from " + pub.yssGetTableName("tb_para_portfolio") + " where fcheckstate=1 and  fportcode = "
					+ dbl.sqlString(this.portcode) + " group by fportcode) a ";
			// -----------------------------------------------------------------------------------------------------------------------------//
			for (int i = 1; i <= ary.size(); i++) {//循环中，拼接各个现金账户对应的期初余额
				query += " left join "
						+ " (select fstoragedate as fstoragedate,FAccBalance as FMONEY,fportcode from "
						+ pub.yssGetTableName("tb_stock_cash")
						+ " where fcheckstate=1 and fstoragedate = (select max(fstoragedate) from "
						+ pub.yssGetTableName("tb_stock_cash") + " where fcheckstate = 1 and fstoragedate < "
						+ dbl.sqlDate(this.checkDate) + " and FPORTCODE = " + dbl.sqlString(this.portcode) 
						+ ") and fcashacccode = (select fcashacccode from "
						+ pub.yssGetTableName("tb_para_cashaccount")
						+ " where fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portcode) + " and FBankCode = "
						+ dbl.sqlString(ary.get(i - 1).toString().split("-")[0]) + " and FCuryCode = " + dbl.sqlString(ary.get(i - 1).toString().split("-")[1])
//						+ " and fstartdate = (" + this.dateWhereSql
//						+ dbl.sqlString(ary.get(i - 1).toString().split("-")[0]) + " and FCuryCode = " + dbl.sqlString(ary.get(i - 1).toString().split("-")[1])
//						+ " ))) a" + i
						+ " and FSUBACCTYPE = '0101')) a" + i
						+ " on a.fportcode = a" + i + ".fportcode ";
			}
			query += " left join "
				+ " (select fstoragedate as fstoragedate,FAccBalance as FMONEY,fportcode from "
				+ pub.yssGetTableName("tb_stock_cash")
				+ " where fcheckstate=1 and fstoragedate = (select max(fstoragedate) from "
				+ pub.yssGetTableName("tb_stock_cash") + " where fcheckstate = 1 and fstoragedate < "
				+ dbl.sqlDate(this.checkDate) + " and FPORTCODE = " + dbl.sqlString(this.portcode) 
				+ ") and fcashacccode = (select fcashacccode from "
				+ pub.yssGetTableName("tb_para_cashaccount")
				+ " where fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portcode) + " and FBankCode = 'HSBC' and FCuryCode = 'HKD'"
				+ " and FSubAccType = '0301')) a8" 
				+ " on a.fportcode = a8.fportcode ";
			rs = dbl.openResultSet(query);

			insertSql = " insert into "
					+ pub.yssGetTableName("tb_data_moneycontrol")
					+ " (FDATE,FFORECAST,FFORECASTDATE,FINOUT,fweekday,FHSBCUSD,FHSBCHKD,FHSBCSGD,FHSBCTWD,FICBCUSD,FICBCHKD,FICBCCNY,FREPORTTYPE,FPORTCODE,FHSBCCHKD) values"
					+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			if (rs.next()) {
				errorMessage = "【执行期初余额数据插入时报错......】";
				if (count == 0) {
					flag = true;
					pst = dbl.openPreparedStatement(insertSql);
				}
				if(pst==null)
				{
					return ;
				}
				pst.setDate(1, YssFun.toSqlDate(this.checkDate));
				pst.setString(2, " ");
				pst.setDate(3, rs.getDate("fdate"));
				pst.setInt(4, rs.getInt("FINOUT"));
				pst.setString(5, rs.getString("fweekday"));
				pst.setDouble(6, rs.getDouble("FHSBCUSD"));
				this.dHSBCUsd = rs.getDouble("FHSBCUSD");//将各币种的期初余额保存下来
				pst.setDouble(7, rs.getDouble("FHSBCHKD"));
				this.dHSBCHkd = rs.getDouble("FHSBCHKD");
				pst.setDouble(8, rs.getDouble("FHSBCSGD"));
				this.dHSBCSgd = rs.getDouble("FHSBCSGD");
				pst.setDouble(9, rs.getDouble("FHSBCTWD"));
				this.dHSBCTwd = rs.getDouble("FHSBCTWD");
				pst.setDouble(10, rs.getDouble("FICBCUSD"));
				this.dICBCUsd = rs.getDouble("FICBCUSD");
				pst.setDouble(11, rs.getDouble("FICBCHKD"));
				this.dICBCHkd = rs.getDouble("FICBCHKD");
				pst.setDouble(12, rs.getDouble("FICBCCNY"));
				this.dICBCCny = rs.getDouble("FICBCCNY");
				pst.setInt(13, this.checkType);
				pst.setString(14, this.portcode);
				pst.setDouble(15, rs.getDouble("FHSBCCHKD"));
				this.dHSBCCHkd = rs.getDouble("FHSBCCHKD");
				count++;
				pst.addBatch();
			}else{
				errorMessage = "【请先产生前一日现金库存，再执行生成头寸表】";
			}

			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			if (flag) {
				pst.executeBatch();
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (YssException e) {
			throw new YssException(errorMessage);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new YssException(errorMessage);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
			dbl.closeStatementFinal(pst);
		}
	}

	// 计算预估流入流出数据，并插入到tb_data_moneycontrol表中
	private void doForecast() throws YssException {
		boolean bTrans = false; 
        Connection conn = null ;
        ResultSet rs = null;
        String query = "";
        boolean flag  = false;
        int count = 0;
        try{
        	//FTRADETYPECODE为01表示流出，02为流入
        	query = "select b.FcastDate as FFORECASTDATE," + dbl.sqlString(this.portcode) +" as FPORTCODE,b.FTRADETYPECODE,a0.FMONEY as FHSBCUSD,a1.FMONEY as FHSBCHKD,a2.FMONEY as FHSBCSGD," +
        			" a3.FMONEY as FHSBCTWD,a4.FMONEY as FICBCUSD,a5.FMONEY as FICBCHKD,a6.FMONEY as FICBCCNY,a7.FMONEY as FHSBCCHKD from (select FBREACHDATE as FcastDate,'01' as FTRADETYPECODE from " +
        			pub.yssGetTableName("tb_data_divineratetrade") + " where fcheckstate = 1 and FBREACHDATE between " + dbl.sqlDate(this.checkDate) + " and " + dbl.sqlDate(this.endDate) +
        			" and FPORTCODE = " + dbl.sqlString(this.portcode) + " group by FBREACHDATE union select FBREACHDATE as FcastDate,'02' as FTRADETYPECODE from " +
        			pub.yssGetTableName("tb_data_divineratetrade") + " where fcheckstate = 1 and FBREACHDATE between " + dbl.sqlDate(this.checkDate) + " and " + dbl.sqlDate(this.endDate) +
        			" and FPORTCODE = " + dbl.sqlString(this.portcode) + " group by FBREACHDATE union select FSettleDate as FcastDate,FTRADETYPECODE from " +
        			pub.yssGetTableName("Tb_Data_DivineTradeData") + " where fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portcode) +
        			" and FTRADETYPECODE in ('01','02') and FSettleDate between " + dbl.sqlDate(this.checkDate) + " and " + dbl.sqlDate(this.endDate) +
        			" group by FSettleDate,FTRADETYPECODE union select FSETTLEDATE," + 
        			" (case FSELLTYPE when '01' then '02' else '01' end) as FTRADETYPECODE from "+
        			pub.yssGetTableName("TB_DATA_DIVINETA") + " where fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portcode);
        	if(this.checkType == 0){//预估则取生成日期之前的确认数据，后面在并上生成日期当日的预估数据
				query += " and FTRADEDATE < " + dbl.sqlDate(this.checkDate);
			}else{//确认报表则取生成日期之前包括当天的TA确认数据
				query += " and FTRADEDATE <= " + dbl.sqlDate(this.checkDate);
			}
        	query += " and FSETTLEDATE between " + dbl.sqlDate(this.checkDate) + " and " + dbl.sqlDate(this.endDate) + " and FDATATYPE = '1' group by FSELLTYPE,FSETTLEDATE ";
        	if (this.checkType == 0){
        		query += " union select FSETTLEDATE,(case FSELLTYPE when '01' then '02' else '01' end) as FTRADETYPECODE from " + 
        			pub.yssGetTableName("TB_DATA_DIVINETA") + " where fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portcode) +
        			" and FTRADEDATE = " + dbl.sqlDate(this.checkDate) + " and FSETTLEDATE between " + dbl.sqlDate(this.checkDate) + " and " + dbl.sqlDate(this.endDate) +
        			" and FDATATYPE = '0' group by FSELLTYPE,FSETTLEDATE union select FSettleDate,FTRADETYPECODE from " +
        			pub.yssGetTableName("TB_DATA_DIVINEESTIMATE") + " where fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portcode) +
        			" and FTRADETYPECODE in ('01','02') and FSettleDate between " + dbl.sqlDate(this.checkDate) + " and " + dbl.sqlDate(this.endDate) + " group by FSettleDate,FTRADETYPECODE";
        	}
        	query += ") b";
        	for (int i = 0; i < ary.size(); i++) {
        		query += " left join (select FFORECASTDATE,sum(FBMONEY) as FMONEY, FPORTCODE, FTRADETYPECODE from (select FBREACHDATE as FFORECASTDATE,FBMONEY as FBMONEY," +
            			" FPORTCODE,'02' as FTRADETYPECODE from " + pub.yssGetTableName("tb_data_divineratetrade") +
            			" where fcheckstate = 1 and FBREACHDATE between " + dbl.sqlDate(this.checkDate) + " and " + dbl.sqlDate(this.endDate) +
            			" and FPORTCODE = " + dbl.sqlString(this.portcode) + " and FBCASHACCCODE = (select fcashacccode from "+ pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1"+
                	    " and FSUBACCTYPE = '0101' and fportcode = " + dbl.sqlString(this.portcode) + " and FBankCode = " + dbl.sqlString(ary.get(i).toString().split("-")[0]) + " and FCuryCode = " + dbl.sqlString(ary.get(i).toString().split("-")[1]) +
                	    //" and fstartdate = (" + this.dateWhereSql + dbl.sqlString(ary.get(i).toString().split("-")[0]) + " and FCuryCode = " + dbl.sqlString(ary.get(i).toString().split("-")[1]) + "))" +//查询外汇交易买入数据，由于为流入方向，方便之后统一处理，故保存交易方式为02卖出
                	    ") union all (select FSettleDate as FFORECASTDATE,FSettleMoney as FBMONEY,FPORTCODE,FTRADETYPECODE from " +
                	    pub.yssGetTableName("Tb_Data_DivineTradeData") + " where fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portcode) +
                	    " and FTRADETYPECODE in ('01','02') and FSettleDate between " + dbl.sqlDate(this.checkDate) + " and " + dbl.sqlDate(this.endDate) +
                	    " and FCASHACCCODE = (select fcashacccode from "+ pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1" +
                	    " and FSUBACCTYPE = '0101' and fportcode = " + dbl.sqlString(this.portcode) + " and FBankCode = " + dbl.sqlString(ary.get(i).toString().split("-")[0]) + " and FCuryCode = " + dbl.sqlString(ary.get(i).toString().split("-")[1]) +
                	    //" and fstartdate = (" + this.dateWhereSql + dbl.sqlString(ary.get(i).toString().split("-")[0]) + " and FCuryCode = " + dbl.sqlString(ary.get(i).toString().split("-")[1]) + ")))" +//查询确认交易数据信息
                	    ")) union all (select FREACHDATE as FFORECASTDATE,FSMONEY as FBMONEY, FPORTCODE,'01' as FTRADETYPECODE from " +
                	    pub.yssGetTableName("tb_data_divineratetrade") + " where fcheckstate = 1 and FPORTCODE = " + dbl.sqlString(this.portcode) +
                	    " and FREACHDATE between " + dbl.sqlDate(this.checkDate) + " and " + dbl.sqlDate(this.endDate) +
                	    " and FSCASHACCCODE = (select fcashacccode from "+ pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1" +
                	    " and FSUBACCTYPE = '0101' and fportcode = " + dbl.sqlString(this.portcode) + " and FBankCode = " + dbl.sqlString(ary.get(i).toString().split("-")[0]) + " and FCuryCode = " + dbl.sqlString(ary.get(i).toString().split("-")[1]) + "))";
                	    //" and fstartdate = (" + this.dateWhereSql + dbl.sqlString(ary.get(i).toString().split("-")[0]) + " and FCuryCode = " + dbl.sqlString(ary.get(i).toString().split("-")[1]) + "))) ";//查询外汇交易卖出数据，由于为流出方向，方便之后统一处理，故保存交易方式为01买入
                if(this.checkType == 0){//如果报表形式为预估，则需要加上预估交易数据表的数据
                	query += " union all (select FSettleDate as FFORECASTDATE,(case FTRADETYPECODE when '01' then FESTIMATEMONEY+FCOMMISIONMONEY else FESTIMATEMONEY-FCOMMISIONMONEY end) as FBMONEY,FportCode,FTRADETYPECODE from " +
    	        	    pub.yssGetTableName("TB_DATA_DIVINEESTIMATE") + " where fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portcode) +
    	        	    " and FTRADETYPECODE in ('01','02') and FSettleDate between " + dbl.sqlDate(this.checkDate) + " and " + dbl.sqlDate(this.endDate) +
    	        	    " and FCASHACCCODE = (select fcashacccode from "+ pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1" +
    	        	    " and FSUBACCTYPE = '0101' and fportcode = " + dbl.sqlString(this.portcode) + " and FBankCode = " + dbl.sqlString(ary.get(i).toString().split("-")[0]) + " and FCuryCode = " + dbl.sqlString(ary.get(i).toString().split("-")[1]) + "))";
                	    //" and fstartdate = (" + this.dateWhereSql + dbl.sqlString(ary.get(i).toString().split("-")[0]) + " and FCuryCode = " + dbl.sqlString(ary.get(i).toString().split("-")[1]) + ")))";
                }
                if(i == ary.size()-1){//工行人民币账户需加上TA数据,并上申请日在报表生成日期之前的确认数据
                	query += " union all (select FSETTLEDATE as FFORECASTDATE,FSELLMONEY as FBMONEY,FPORTCODE,(case FSELLTYPE when '01' then '02' else '01' end) as FTRADETYPECODE from " +
                			pub.yssGetTableName("TB_DATA_DIVINETA") + " where fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portcode);
                			if(this.checkType == 0){//预估则取生成日期之前的确认数据，后面在并上生成日期当日的预估数据
                				query += " and FTRADEDATE < " + dbl.sqlDate(this.checkDate);
                			}else{//确认报表则取生成日期之前包括当天的TA确认数据
                				query += " and FTRADEDATE <= " + dbl.sqlDate(this.checkDate);
                			}
                	query += " and FSETTLEDATE between " + dbl.sqlDate(this.checkDate) + " and " + dbl.sqlDate(this.endDate) +
                			" and FDATATYPE = '1' and FCASHACCCODE = (select fcashacccode from "+ pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1" +
                			//------ modify by wangzuochun 2011.03.30 添加上组合代码条件 BUG #1586 华安现金头寸预测表中没有考虑多组合的情况（MoneyControl.java） 
                			" and FPortCode = " + dbl.sqlString(this.portcode) + " and FSUBACCTYPE = '0101' and FBankCode = " + dbl.sqlString(ary.get(i).toString().split("-")[0]) + " and FCuryCode = " + dbl.sqlString(ary.get(i).toString().split("-")[1]) + "))";
                			//" and fstartdate = (" + this.dateWhereSql + dbl.sqlString(ary.get(i).toString().split("-")[0]) + " and FCuryCode = " + dbl.sqlString(ary.get(i).toString().split("-")[1]) + ")))";
                	if (this.checkType == 0){//预估报表在此处加上当日的预估TA交易数据
                		query += " union all (select FSETTLEDATE as FFORECASTDATE,FSELLMONEY as FBMONEY,FPORTCODE,(case FSELLTYPE when '01' then '02' else '01' end) as FTRADETYPECODE from " +
            					pub.yssGetTableName("TB_DATA_DIVINETA") + " where fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portcode)+
            					" and FTRADEDATE = " + dbl.sqlDate(this.checkDate) + " and FSETTLEDATE between " + dbl.sqlDate(this.checkDate) + " and " + dbl.sqlDate(this.endDate) +
            					" and FDATATYPE = '0' and FCASHACCCODE in (select fcashacccode from "+ pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1" +
            					//------ modify by wangzuochun 2011.03.30 添加上组合代码条件 BUG #1586 华安现金头寸预测表中没有考虑多组合的情况（MoneyControl.java） 
            					" and FPortCode = " + dbl.sqlString(this.portcode) + " and FSUBACCTYPE = '0101' and FBankCode = " + dbl.sqlString(ary.get(i).toString().split("-")[0]) + " and FCuryCode = " + dbl.sqlString(ary.get(i).toString().split("-")[1]) + "))";
                    			//" and fstartdate = (" + this.dateWhereSql + dbl.sqlString(ary.get(i).toString().split("-")[0]) + " and FCuryCode = " + dbl.sqlString(ary.get(i).toString().split("-")[1]) + ")))";
                	}
                }
                query += " )group by FPORTCODE,FTRADETYPECODE,FFORECASTDATE) a" + i + " on b.FTRADETYPECODE = a"+i+".FTRADETYPECODE and b.FcastDate=a"+i+".FFORECASTDATE";
        	}
        	

    		query += " left join (select FFORECASTDATE,sum(FBMONEY) as FMONEY, FPORTCODE, FTRADETYPECODE from (select FBREACHDATE as FFORECASTDATE,FBMONEY as FBMONEY," +
        			" FPORTCODE,'02' as FTRADETYPECODE from " + pub.yssGetTableName("tb_data_divineratetrade") +
        			" where fcheckstate = 1 and FBREACHDATE between " + dbl.sqlDate(this.checkDate) + " and " + dbl.sqlDate(this.endDate) +
        			" and FPORTCODE = " + dbl.sqlString(this.portcode) + " and FBCASHACCCODE = (select fcashacccode from "+ pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1"+
            	    " and FSubAccType = '0301' and fportcode = " + dbl.sqlString(this.portcode) + " and FBankCode = 'HSBC' and FCuryCode = 'HKD'" +
            	    //" and fstartdate = (" + this.dateWhereSql + "'HSBC' and FCuryCode = 'HKD'))" +//查询外汇交易买入数据，由于为流入方向，方便之后统一处理，故保存交易方式为02卖出
            	    ") union all (select FSettleDate as FFORECASTDATE,FSettleMoney as FBMONEY,FPORTCODE,FTRADETYPECODE from " +
            	    pub.yssGetTableName("Tb_Data_DivineTradeData") + " where fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portcode) +
            	    " and FTRADETYPECODE in ('01','02') and FSettleDate between " + dbl.sqlDate(this.checkDate) + " and " + dbl.sqlDate(this.endDate) +
            	    " and FCASHACCCODE = (select fcashacccode from "+ pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1" +
            	    " and FSubAccType = '0301' and fportcode = " + dbl.sqlString(this.portcode) + " and FBankCode = 'HSBC' and FCuryCode = 'HKD'" +
            	    //" and fstartdate = (" + this.dateWhereSql + "'HSBC' and FCuryCode = 'HKD')))" +//查询确认交易数据信息
            	    ")) union all (select FREACHDATE as FFORECASTDATE,FSMONEY as FBMONEY, FPORTCODE,'01' as FTRADETYPECODE from " +
            	    pub.yssGetTableName("tb_data_divineratetrade") + " where fcheckstate = 1 and FPORTCODE = " + dbl.sqlString(this.portcode) +
            	    " and FREACHDATE between " + dbl.sqlDate(this.checkDate) + " and " + dbl.sqlDate(this.endDate) +
            	    " and FSCASHACCCODE = (select fcashacccode from "+ pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1" +
            	    " and FSubAccType = '0301' and fportcode = " + dbl.sqlString(this.portcode) + " and FBankCode = 'HSBC' and FCuryCode = 'HKD'" + "))";
            	    //" and fstartdate = (" + this.dateWhereSql + "'HSBC' and FCuryCode = 'HKD'))) ";//查询外汇交易卖出数据，由于为流出方向，方便之后统一处理，故保存交易方式为01买入
            if(this.checkType == 0){//如果报表形式为预估，则需要加上预估交易数据表的数据
            	query += " union all (select FSettleDate as FFORECASTDATE,(case FTRADETYPECODE when '01' then FESTIMATEMONEY+FCOMMISIONMONEY else FESTIMATEMONEY-FCOMMISIONMONEY end) as FBMONEY,FportCode,FTRADETYPECODE from " +
	        	    pub.yssGetTableName("TB_DATA_DIVINEESTIMATE") + " where fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portcode) +
	        	    " and FTRADETYPECODE in ('01','02') and FSettleDate between " + dbl.sqlDate(this.checkDate) + " and " + dbl.sqlDate(this.endDate) +
	        	    " and FCASHACCCODE = (select fcashacccode from "+ pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate = 1" +
	        	    " and FSubAccType = '0301' and fportcode = " + dbl.sqlString(this.portcode) + " and FBankCode = 'HSBC' and FCuryCode = 'HKD'" + "))";
            	    //" and fstartdate = (" + this.dateWhereSql + "'HSBC' and FCuryCode = 'HKD')))";
            }
            query += " )group by FPORTCODE,FTRADETYPECODE,FFORECASTDATE) a7 on b.FTRADETYPECODE = a7.FTRADETYPECODE and b.FcastDate=a7.FFORECASTDATE";
    	
        	
        	rs = dbl.openResultSet(query);
        	MoneyControlBean mcBean = new MoneyControlBean();
        	mcBean.setFdate(this.checkDate);
        	mcBean.setFportcode(this.portcode);
        	mcBean.setReporttype(this.checkType);
        	while (rs.next()){
        		count = rs.getString("FTRADETYPECODE").equalsIgnoreCase("01") ? -1 : 1;
        		MoneyControlBean mcBean1 = new MoneyControlBean();
        		mcBean1 = (MoneyControlBean)mcBean.clone();
        		mcBean1.setForecastdate(rs.getDate("FFORECASTDATE"));
        		mcBean1.setFinout(count);
        		//edit by yanghaiming 20101126 QDV4华安基金2010年11月10日01_AB 台币为主货币，不转换为美元
//        		mcBean1.setHsbcUSD(YssFun.roundIt(rs.getDouble("FHSBCUSD")+
//        				(rs.getDouble("FHSBCSGD")*YssFun.toDouble(rateHash.get("SGD").toString())/YssFun.toDouble(rateHash.get("USD").toString())) + 
//        				(rs.getDouble("FHSBCTWD")*YssFun.toDouble(rateHash.get("TWD").toString())/YssFun.toDouble(rateHash.get("USD").toString())),2));
        		mcBean1.setHsbcUSD(YssFun.roundIt(rs.getDouble("FHSBCUSD")+
        				(rs.getDouble("FHSBCSGD")*YssFun.toDouble(rateHash.get("SGD").toString())/YssFun.toDouble(rateHash.get("USD").toString())),2));
        		mcBean1.setHsbcHKD(rs.getDouble("FHSBCHKD"));
        		mcBean1.setHsbcSGD(0);
        		//mcBean1.setHsbcTWD(0);
        		mcBean1.setHsbcTWD(rs.getDouble("FHSBCTWD"));//edit by yanghaiming 20101126 QDV4华安基金2010年11月10日01_AB 台币为主货币，不转换为美元
        		mcBean1.setIcbcUSD(rs.getDouble("FICBCUSD"));
        		mcBean1.setIcbcHKD(rs.getDouble("FICBCHKD"));
        		mcBean1.setIcbcCNY(rs.getDouble("FICBCCNY"));
        		mcBean1.setHsbccHKD(rs.getDouble("FHSBCCHKD"));
        		controlDateParam.put(YssFun.toDate(rs.getDate("FFORECASTDATE").toString()).toString()+count, mcBean1);
        	}
        }catch(YssException e){
			throw new YssException("【执行外币管控数据查询时报错......】");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new YssException("【执行外币管控数据查询时报错......】");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	//执行插入操作
	private void doAddForecase() throws YssException {
        PreparedStatement stm=null;
        String sqlStr="";
        Connection conn = null;
		boolean bTrans = false;
		Date fdate = this.checkDate;
		Calendar c = Calendar.getInstance();
		String forecast = "";
		MoneyControlBean mControlBean = new MoneyControlBean();
        
        try{
        	conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            
            sqlStr = " insert into "
            		+ pub.yssGetTableName("tb_data_moneycontrol")
            		+ " (FDATE,FFORECAST,FFORECASTDATE,FINOUT,fweekday,FHSBCUSD,FHSBCHKD,FHSBCSGD,FHSBCTWD,FICBCUSD,FICBCHKD,FICBCCNY,FREPORTTYPE,FPORTCODE,FHSBCCHKD) values"
            		+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            stm = conn.prepareStatement(sqlStr);
            
            for (int i = 0; i <= 10; i++) {// 循环日期，执行插入
				if (i == 0) {// 保存日期对应的T+?数据
					this.endDate = this.checkDate;
				} 
				c.setTime(endDate);
				forecast = dateParam.get(endDate).toString();//取出每一天对应的星期和T+?日
				stm.setDate(1, YssFun.toSqlDate(this.checkDate));
				stm.setString(2, forecast.split("\t")[0]);
				stm.setDate(3, YssFun.toSqlDate(this.endDate));
				stm.setString(5, forecast.split("\t")[1]);
				stm.setInt(13, this.checkType);
				stm.setString(14, this.portcode);
				if(controlDateParam.get(endDate.toString() + "-1") != null){//如果不为null，则表示有记录，执行每日流出数据的插入，并改变余额
					mControlBean = (MoneyControlBean)controlDateParam.get(endDate.toString() + "-1");
					this.dHSBCUsd -= mControlBean.getHsbcUSD();
					this.dHSBCHkd -= mControlBean.getHsbcHKD();
					this.dHSBCSgd -= mControlBean.getHsbcSGD();
					this.dHSBCTwd -= mControlBean.getHsbcTWD();
					this.dICBCUsd -= mControlBean.getIcbcUSD();
					this.dICBCHkd -= mControlBean.getIcbcHKD();
					this.dICBCCny -= mControlBean.getIcbcCNY();
					this.dHSBCCHkd -= mControlBean.getHsbccHKD();
					stm.setInt(4, mControlBean.getFinout());
					stm.setDouble(6, mControlBean.getHsbcUSD());
					stm.setDouble(7, mControlBean.getHsbcHKD());
					stm.setDouble(8, mControlBean.getHsbcSGD());
					stm.setDouble(9, mControlBean.getHsbcTWD());
					stm.setDouble(10, mControlBean.getIcbcUSD());
					stm.setDouble(11, mControlBean.getIcbcHKD());
					stm.setDouble(12, mControlBean.getIcbcCNY());
					stm.setDouble(15, mControlBean.getHsbccHKD());
					stm.addBatch();
				}else{//无流出记录
					stm.setInt(4,-1);
					stm.setDouble(6, 0);
					stm.setDouble(7, 0);
					stm.setDouble(8, 0);
					stm.setDouble(9, 0);
					stm.setDouble(10, 0);
					stm.setDouble(11, 0);
					stm.setDouble(12, 0);
					stm.setDouble(15, 0);
					stm.addBatch();
				}
				if(controlDateParam.get(endDate.toString() + "1") != null){//如果不为null，则表示有记录，执行每日流入数据的插入，并改变余额
					mControlBean = (MoneyControlBean)controlDateParam.get(endDate.toString() + "1");
					this.dHSBCUsd += mControlBean.getHsbcUSD();
					this.dHSBCHkd += mControlBean.getHsbcHKD();
					this.dHSBCSgd += mControlBean.getHsbcSGD();
					this.dHSBCTwd += mControlBean.getHsbcTWD();
					this.dICBCUsd += mControlBean.getIcbcUSD();
					this.dICBCHkd += mControlBean.getIcbcHKD();
					this.dICBCCny += mControlBean.getIcbcCNY();
					this.dHSBCCHkd += mControlBean.getHsbccHKD();
					stm.setInt(4, mControlBean.getFinout());
					stm.setDouble(6, mControlBean.getHsbcUSD());
					stm.setDouble(7, mControlBean.getHsbcHKD());
					stm.setDouble(8, mControlBean.getHsbcSGD());
					stm.setDouble(9, mControlBean.getHsbcTWD());
					stm.setDouble(10, mControlBean.getIcbcUSD());
					stm.setDouble(11, mControlBean.getIcbcHKD());
					stm.setDouble(12, mControlBean.getIcbcCNY());
					stm.setDouble(15, mControlBean.getHsbccHKD());
					stm.addBatch();
				}else{
					stm.setInt(4,1);
					stm.setDouble(6, 0);
					stm.setDouble(7, 0);
					stm.setDouble(8, 0);
					stm.setDouble(9, 0);
					stm.setDouble(10, 0);
					stm.setDouble(11, 0);
					stm.setDouble(12, 0);
					stm.setDouble(15, 0);
					stm.addBatch();
				}
				stm.setInt(4, -2);//每天的余额进行插入
				stm.setDouble(6, this.dHSBCUsd);
				stm.setDouble(7, this.dHSBCHkd);
				stm.setDouble(8, this.dHSBCSgd);
				stm.setDouble(9, this.dHSBCTwd);
				stm.setDouble(10, this.dICBCUsd);
				stm.setDouble(11, this.dICBCHkd);
				stm.setDouble(12, this.dICBCCny);
				stm.setDouble(15, this.dHSBCCHkd);
				stm.addBatch();
				if(i != 10){//最后一天不需要对日期进行处理
					endDate = YssFun.addDay(endDate, 1);// 每天执行完日期加1
					c.setTime(endDate);
					if (c.get(Calendar.DAY_OF_WEEK) == 7) {// 如果为星期六，则日期再加2
						endDate = YssFun.addDay(endDate, 2);
					}
				}
			}
            stm.executeBatch();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }catch(Exception e){
			throw new YssException("【执行外币头寸表插入数据时报错......】");
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
				+ pub.yssGetTableName("TB_DATA_MONEYCONTROL")
				+ " where FDATE = " + dbl.sqlDate(this.checkDate) + " and FPORTCODE = " + dbl.sqlString(this.portcode) + " and FREPORTTYPE = " + this.checkType;
			rs = dbl.openResultSet(strSql);
			if (rs.next()){
				strResult = "true";
			}
			return strResult;
		} catch (Exception e) {
			throw new YssException("【生成外币头寸表时出错....】");
		} finally{
			dbl.closeResultSetFinal(rs);
		}
	}
}
