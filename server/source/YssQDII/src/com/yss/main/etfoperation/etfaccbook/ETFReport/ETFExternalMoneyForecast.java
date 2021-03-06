package com.yss.main.etfoperation.etfaccbook.ETFReport;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
import com.yss.util.YssUtil;

/**shashijie 2013-5-18 STORY 3857 需求北京-(工银瑞信)QDIIV4.0(高)20130416003,ETF境外头寸预测 */
public class ETFExternalMoneyForecast extends BaseBuildCommonRep {
    
    private CommonRepBean repBean;//报表对象
    
    private String FStartDate = "";//日期
    private String FPortCode = "";//组合代码
    private String FHolidaysCode = "";//节假日代码
    
    //上一日的预测值集合
    private HashMap eMap = new HashMap();
	private BigDecimal property = new BigDecimal(0);//T日的资产净值 
    
	private double ssCash = 0;//汇总日初余额
	private double ssCashB = 0;//汇总日初余额(本币)
	private double sbuyMoney = 0;//汇总待进款
	private double sbuyMoneyB = 0;//汇总待进款(本币)
	private double ssellMoney = 0;//汇总待出款
	private double ssellMoneyB = 0;//汇总待出款(本币)
	private double sFCashBal = 0;//汇总股指期货保证金
	private double sFCashBalB = 0;//汇总股指期货保证金(本币)
	private double sproject = 0;//汇总预测数
	private double sprojectB = 0;//汇总预测数(本币)

	
    public ETFExternalMoneyForecast() {
    }

    /**shashijie 2013-5-18 STORY 3857 程序入口 */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
    	//计算数据集合
        ArrayList list = getCountOperion();
        //获取报表内容
        sResult += getInfo(list);
        
        return sResult;
    }

	/**shashijie 2013-5-22 STORY 3857 获取计算数据集合*/
	private ArrayList getCountOperion() throws YssException {
		//计算数据集合
        ArrayList list = new ArrayList();
        //开始日
        Date startDate = YssFun.toDate(this.FStartDate);
        //结束日
        Date endDate = getWorkDay(this.FHolidaysCode, startDate, 7);
        int diff = YssFun.dateDiff(startDate, endDate);
        //非人民币现金账户T日库存
        ArrayList<CashAccountBean> cashList = new ArrayList<CashAccountBean>();
        //循环,不考虑选择日那天不是节假日的情况
        for (int i = 0; i < diff; i++) {
        	//当前处理日期
        	Date operonDate = YssFun.addDay(startDate, i);
        	//若非工作日则不处理
        	if (!isWorkDate(operonDate, this.FHolidaysCode) && i!=0) {
        		continue;
			}
        	HashMap map = new HashMap();
    		//日期行
    		map.put("operonDate", YssFun.formatDate(operonDate));
    		list.add(map);
    		//T日所有非人民币的现金账户
    		if (i==0) {
				cashList = getCashList(operonDate,this.FPortCode);
			}
    		//清空汇总值
    		removeSummation();
    		for (int j = 0; j < cashList.size(); j++) {
    			CashAccountBean caBean = cashList.get(j);
    			//设置现金账户等值
        		map = getCountData(operonDate,this.FPortCode,caBean,i,startDate);
        		list.add(map);
			}
    		//汇总合计
    		map = getSummation();
			list.add(map);
		}
        return list;
	}

	/**shashijie 2013-5-22 STORY 3857 汇总合计值放入map中*/
	private HashMap getSummation() {
		HashMap<String, Double> map = new HashMap<String, Double>();
		map.put("ssCash",ssCash);//汇总日初余额
		map.put("ssCashB",ssCashB);//汇总日初余额(本币)
		map.put("sbuyMoney",sbuyMoney);//汇总待进款
		map.put("sbuyMoneyB",sbuyMoneyB);//汇总待进款(本币)
		map.put("ssellMoney",ssellMoney);//汇总待出款
		map.put("ssellMoneyB",ssellMoneyB);//汇总待出款(本币)
		map.put("sFCashBal",sFCashBal);//汇总股指期货保证金
		map.put("sFCashBalB",sFCashBalB);//汇总股指期货保证金(本币)
		map.put("sproject",sproject);//汇总预测数
		map.put("sprojectB",sprojectB);//汇总预测数(本币)
		return map;
	}

	/**shashijie 2013-5-22 STORY 3857 清空变量*/
	private void removeSummation() {
		this.ssCash = 0;//汇总日初余额
		this.ssCashB = 0;//汇总日初余额(本币)
		this.sbuyMoney = 0;//汇总待进款
		this.sbuyMoneyB = 0;//汇总待进款(本币)
		this.ssellMoney = 0;//汇总待出款
		this.ssellMoneyB = 0;//汇总待出款(本币)
		this.sFCashBal = 0;//汇总股指期货保证金
		this.sFCashBalB = 0;//汇总股指期货保证金(本币)
		this.sproject = 0;//汇总预测数
		this.sprojectB = 0;//汇总预测数(本币)
	}

	/**shashijie 2013-5-22 STORY 3857 获取处理日期当天非人民币的现金账户库存*/
	private ArrayList<CashAccountBean> getCashList(Date operonDate,String portCode) throws YssException {
		ArrayList<CashAccountBean> list = new ArrayList<CashAccountBean>();
		ResultSet rs = null;//定义游标
		try {
			String query = getCashAccountQuery(operonDate,portCode,"");
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				CashAccountBean cash = new CashAccountBean();
				cash.setYssPub(pub);
				cash.setStrCashAcctCode(rs.getString("Fcashacccode"));
				cash.getSetting();
				list.add(cash);
			}
		} catch (Exception e) {
			throw new YssException("\r\n",e);
		} finally {
			dbl.closeResultSetFinal(rs);//关闭游标
		}
		return list;
	}

	/**shashijie 2013-5-22 STORY 3857 获取现金账户SQL*/
	private String getCashAccountQuery(Date operonDate, String portCode,
			String strCashAcctCode) {
		String where = "";
		if (!YssUtil.isNullOrEmpty(strCashAcctCode)) {
			where = " And a.FCashAccCode = "+dbl.sqlString(strCashAcctCode)+ "  ";
		}
		String sql = " Select a.Fcashacccode,a.FAccBalance "+
			" From "+pub.yssGetTableName("Tb_Stock_Cash")+" a"+
			" Join (Select B1.Fcashacccode,b1.fcurycode" +
			" From "+pub.yssGetTableName("Tb_Para_Cashaccount")+" B1" +
			" Where B1.Fcheckstate = 1) b On a.Fcashacccode = b.Fcashacccode"+
			" Where a.Fcheckstate = 1"+
			" And a.Fstoragedate = "+dbl.sqlDate(operonDate)+
			" And a.Fportcode = "+dbl.sqlString(portCode)+
			" And b.fcurycode <> 'CNY' " + 
			where+
			" ";
		return sql;
	}

	/**shashijie 2013-5-18 STORY 3857 获取计算后每天的数据*/
	private HashMap getCountData(Date operonDate, String fPortCode,
			CashAccountBean caBean,int tag,Date startDate) throws YssException {
		HashMap map = new HashMap();
		String key = "";//上一日预测值KEY
		double sCash = 0;//日初余额
		double sCashB = 0;//日初余额(本币)
		double sCashT = 0;//日初余额(占比)
		double buyMoney = 0;//待进款
		double buyMoneyB = 0;//待进款(本币)
		double sellMoney = 0;//待出款
		double sellMoneyB = 0;//待出款(本币)
		double FCashBal = 0;//股指期货保证金
		double FCashBalB = 0;//股指期货保证金(本币)
		double project = 0;//预测数
		double projectB = 0;//预测数(本币)
		double projectT = 0;//预测数(占比)
		
		//第一日获取现金库存,第二日开始都去上一日预测值
		if (tag==0) {
			sCash = getStockCash(operonDate,fPortCode,caBean.getStrCashAcctCode());
			//计算本币
			sCashB = getPortCuryRate(startDate,fPortCode,caBean.getStrCurrencyCode(),sCash);
			//获取T日的资产净值
			this.property = getDataNetValue(operonDate,fPortCode);
			//本币 / T日资产净值  * 100
			sCashT = YssD.mul(
				YssD.div(new BigDecimal(sCashB), property)
				,100);
		} else {
			//key = 日期 + 账户代码 + 标识
			key = getCashkey(YssFun.formatDate(getWorkDay(this.FHolidaysCode, operonDate, -1)),
					caBean.getStrCashAcctCode(),"eCash");
			sCash = (Double)this.eMap.get(key);//日初余额
			key = getCashkey(YssFun.formatDate(getWorkDay(this.FHolidaysCode, operonDate, -1)),
					caBean.getStrCashAcctCode(),"eCashB");
			sCashB = (Double)this.eMap.get(key);//日初余额(本币)
			key = getCashkey(YssFun.formatDate(getWorkDay(this.FHolidaysCode, operonDate, -1)),
					caBean.getStrCashAcctCode(),"eCashT");
			sCashT = (Double)this.eMap.get(key);//日初余额(占比)
		}
		//待进款FCashInd
		buyMoney = getBuyMoney(operonDate,fPortCode,"1",caBean.getStrCashAcctCode());
		buyMoneyB = getPortCuryRate(startDate,fPortCode,caBean.getStrCurrencyCode(),buyMoney);
		//待出款,负值
		sellMoney = getBuyMoney(operonDate,fPortCode,"-1",caBean.getStrCashAcctCode());
		sellMoneyB = getPortCuryRate(startDate,fPortCode,caBean.getStrCurrencyCode(),sellMoney);
		//股指期货保证金
		if (tag == 0 && 
				(caBean.getStrSubAcctTypeCode().equalsIgnoreCase("0201") ||
				 caBean.getStrSubAcctTypeCode().equalsIgnoreCase("0301"))) {
			FCashBal = sCash;
			FCashBalB = sCashB;
		}
		//预测数 = 日初余额 + 待进款 - 待出款,
		project = YssD.add(sCash, buyMoney, sellMoney);
		projectB = YssD.add(sCashB, buyMoneyB, sellMoneyB);
		projectT = YssD.mul(
				YssD.div(new BigDecimal(projectB), property)
				,100);
		
		map.put("FCashAccCode", caBean.getStrCashAcctCode());//现金账户
		map.put("sCash", sCash);//日初余额
		map.put("sCashB",sCashB);//日初余额(本币)
		map.put("sCashT",sCashT);//日初余额(占比)
		map.put("buyMoney",buyMoney);//待进款
		map.put("buyMoneyB",buyMoneyB);//待进款(本币)
		map.put("sellMoney",sellMoney);//待出款
		map.put("sellMoneyB",sellMoneyB);//待出款(本币)
		map.put("FCashBal",FCashBal);//股指期货保证金
		map.put("FCashBalB",FCashBalB);//股指期货保证金(本币)
		map.put("project",project);//预测数
		map.put("projectB",projectB);//预测数(本币)
		map.put("projectT",projectT);//预测数(占比)
		
		//上一日预测数,key = 日期 + 账户代码 + 标识
		key = getCashkey(YssFun.formatDate(operonDate),caBean.getStrCashAcctCode(),"eCash");
		this.eMap.put(key, project);
		key = getCashkey(YssFun.formatDate(operonDate),caBean.getStrCashAcctCode(),"eCashB");
		this.eMap.put(key, projectB);
		key = getCashkey(YssFun.formatDate(operonDate),caBean.getStrCashAcctCode(),"eCashT");
		this.eMap.put(key, projectT);
		//汇总值
		sumDestroy(sCash,sCashB,buyMoney,buyMoneyB,sellMoney,sellMoneyB,FCashBal,FCashBalB,
				project,projectB);
		
		return map;
	}

	/**shashijie 2013-5-22 STORY 3857 获取上一日预测数的集合key = 日期 + 账户代码 + 标识*/
	private String getCashkey(String date, String cashAcctCode,
			String tag) {
		String key = date + "\t" + cashAcctCode + "\t" + tag;
		return key;
	}

	/**shashijie 2013-5-22 STORY 3857 获取待进待出款实收实付金额*/
	private double getBuyMoney(Date operonDate, String portCode,
			String FCashInd, String strCashAcctCode) throws YssException {
		double value = 0;
		ResultSet rs = null;//定义游标
		try {
			String query = getFTotalcostQuery(operonDate,portCode,FCashInd,strCashAcctCode);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				value = rs.getDouble("Ftotalcost");
			}
		} catch (Exception e) {
			throw new YssException("\r\n",e);
		} finally {
			dbl.closeResultSetFinal(rs);//关闭游标
		}
		return value;
	}

	/**shashijie 2013-5-22 STORY 3857 获取待进待出款SQL*/
	private String getFTotalcostQuery(Date operonDate, String portCode,
			String fCashInd, String strCashAcctCode) {
		String sql = " Select Nvl(Sum(a.Ftotalcost), 0) * "+fCashInd+" As Ftotalcost"+
			" From "+pub.yssGetTableName("Tb_Data_Subtrade")+" a"+
			" Join (Select B1.Ftradetypecode" +
			" From Tb_Base_Tradetype B1" +
			" Where B1.Fcheckstate = 1" +
			" And B1.Fcashind = "+fCashInd+") b On a.Ftradetypecode = b.Ftradetypecode" +
			" Where a.Fcheckstate = 1" +
			" And a.Fsettledate = "+dbl.sqlDate(operonDate)+
			" And a.Fportcode = "+dbl.sqlString(portCode)+
			" And a.Fcashacccode = "+dbl.sqlString(strCashAcctCode)+
			" ";
		return sql;
	}

	/**shashijie 2013-5-22 STORY 3857 通过汇率计算本币*/
	private double getPortCuryRate(Date operonDate, String portCode,
			String strCurrencyCode, double sCash) throws YssException {
		double value = 0;
		//公共获取汇率类
		double FBaseCuryRate = this.getSettingOper().getCuryRate( //基础汇率
				operonDate, //(汇率)日期
				strCurrencyCode, //获取基础汇率传入原币
				portCode, //组合
				YssOperCons.YSS_RATE_BASE); //标示
		//组合汇率
		double FPortCuryRate = this.getSettingOper().getCuryRate( 
				operonDate, //(汇率)日期
				"", //获取组合汇率传入本位币(或不传默认取组合对应的组合货币)
				portCode, //组合
				YssOperCons.YSS_RATE_PORT); //标示
		value = YssD.div( 
			YssD.mul(sCash, FBaseCuryRate)
			, FPortCuryRate);
		return value;
	}

	/**shashijie 2013-5-22 STORY 3857 获取业务日现金库存*/
	private double getStockCash(Date operonDate, String portCode,
			String strCashAcctCode) throws YssException {
		double FAccBalance = 0;
		ResultSet rs = null;//定义游标
		try {
			String query = getCashAccountQuery(operonDate,portCode,strCashAcctCode);
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				FAccBalance = rs.getDouble("FAccBalance");
			}
		} catch (Exception e) {
			throw new YssException("\r\n",e);
		} finally {
			dbl.closeResultSetFinal(rs);//关闭游标
		}
		return FAccBalance;
	}

	/**shashijie 2013-5-22 STORY 3857 汇总每天的值*/
	private void sumDestroy(double sCash, double sCashB, double buyMoney,
			double buyMoneyB, double sellMoney, double sellMoneyB,
			double fCashBal, double fCashBalB, double project, double projectB) {
		this.ssCash = YssD.add(ssCash, sCash);//汇总日初余额
		this.ssCashB = YssD.add(ssCashB, sCashB);//汇总日初余额(本币)
		this.sbuyMoney = YssD.add(sbuyMoney, buyMoney);//汇总待进款
		this.sbuyMoneyB = YssD.add(sbuyMoneyB, buyMoneyB);//汇总待进款(本币)
		this.ssellMoney = YssD.add(ssellMoney, sellMoney);//汇总待出款
		this.ssellMoneyB = YssD.add(ssellMoneyB, sellMoneyB);//汇总待出款(本币)
		this.sFCashBal = YssD.add(sFCashBal, fCashBal);//汇总股指期货保证金
		this.sFCashBalB = YssD.add(sFCashBalB, fCashBalB);//汇总股指期货保证金(本币)
		this.sproject = YssD.add(sproject, project);//汇总预测数
		this.sprojectB = YssD.add(sprojectB, projectB);//汇总预测数(本币)
	}

	/**shashijie 2013-5-18 STORY 3857 获取当天资产净值*/
	private BigDecimal getDataNetValue(Date operonDate, String fPortCode) throws YssException {
		BigDecimal value = new BigDecimal(0);
		ResultSet rs = null;//定义游标
		try {
			String query = getNetValueQuery(operonDate,fPortCode);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				value = rs.getBigDecimal("Fportnetvalue");
			}
		} catch (Exception e) {
			throw new YssException("\r\n",e);
		} finally {
			dbl.closeResultSetFinal(rs);//关闭游标
		}
		return value;
	}

	/**shashijie 2013-5-18 STORY 3857 获取资产净值SQL*/
	private String getNetValueQuery(Date operonDate, String fPortCode) {
		String sql = " Select Fportnetvalue"+
			" From "+pub.yssGetTableName("Tb_Data_Netvalue")+
			" Where Ftype = '01'" +
			" And Finvmgrcode = ' '" +
			" And Fportcode = "+dbl.sqlString(fPortCode)+
			" And Fnavdate = "+dbl.sqlDate(operonDate)+
			" ";
		return sql;
	}

	/**shashijie 2013-5-18 STORY 3857 获取报表内容*/
	private String getInfo(ArrayList list) throws YssException {
		String str = "";
		try {
	        for (int i = 0; i < list.size(); i++) {
	        	HashMap map = (HashMap)list.get(i);
	        	String operonDate = "";//日期
	        	String FCashAccCode = "";//现金账户
	        	double sCash = 0;//日初余额
	        	double sCashB = 0;//日初余额(本币)
	        	double sCashT = 0;//日初余额(占比)
	        	double buyMoney = 0;//待进款
	        	double buyMoneyB = 0;//待进款(本币)
	        	double sellMoney = 0;//待出款
	        	double sellMoneyB = 0;//待出款(本币)
	        	double FCashBal = 0;//股指期货保证金
	        	double FCashBalB = 0;//股指期货保证金(本币)
	        	double project = 0;//预测数
	        	double projectB = 0;//预测数(本币)
	        	double projectT = 0;//预测数(占比)
	        	
	        	double ssCash = 0;//汇总日初余额
	        	double ssCashB = 0;//汇总日初余额(本币)
	        	double sbuyMoney = 0;//汇总待进款
	        	double sbuyMoneyB = 0;//汇总待进款(本币)
	        	double ssellMoney = 0;//汇总待出款
	        	double ssellMoneyB = 0;//汇总待出款(本币)
	        	double sFCashBal = 0;//汇总股指期货保证金
	        	double sFCashBalB = 0;//汇总股指期货保证金(本币)
	        	double sproject = 0;//汇总预测数
	        	double sprojectB = 0;//汇总预测数(本币)
	    		
	    		//日期
	        	if (map.containsKey("operonDate")) {
	        		operonDate = (String)map.get("operonDate");
		    		str += operionStr(operonDate,
		    				" ", 
		    				" ",
							" ",
							" ",
							" ",
							" ",
							" ",
							" ",
							" ",
							" ",
							" ",
							" ");
				}
	        	//现金账户
	    		if (map.containsKey("FCashAccCode")) {
	    			FCashAccCode = (String)map.get("FCashAccCode");//现金账户
	    			sCash = (Double)map.get("sCash");//日初余额
		        	sCashB = (Double)map.get("sCashB");//日初余额(本币)
		        	sCashT = (Double)map.get("sCashT");//日初余额(占比)
		        	buyMoney = (Double)map.get("buyMoney");//待进款
		        	buyMoneyB = (Double)map.get("buyMoneyB");//待进款(本币)
		        	sellMoney = (Double)map.get("sellMoney");//待出款
		        	sellMoneyB = (Double)map.get("sellMoneyB");//待出款(本币)
		        	FCashBal = (Double)map.get("FCashBal");//股指期货保证金
		        	FCashBalB = (Double)map.get("FCashBalB");//股指期货保证金(本币)
		        	project = (Double)map.get("project");//预测数
		        	projectB = (Double)map.get("projectB");//预测数(本币)
		        	projectT = (Double)map.get("projectT");//预测数(占比)
		        	
		        	str += operionStr(FCashAccCode+"",
		    				YssFun.formatNumber(sCash,"0.00"), 
		    				YssFun.formatNumber(sCashB,"0.00"),
    						YssFun.formatNumber(sCashT,"0.00")+"%",
							YssFun.formatNumber(buyMoney,"0.00"),
							YssFun.formatNumber(buyMoneyB,"0.00"),
							YssFun.formatNumber(sellMoney,"0.00"),
							YssFun.formatNumber(sellMoneyB,"0.00"),
							YssFun.formatNumber(FCashBal,"0.00"),
							YssFun.formatNumber(FCashBalB,"0.00"),
							YssFun.formatNumber(project,"0.00"),
							YssFun.formatNumber(projectB,"0.00"),
							YssFun.formatNumber(projectT,"0.00")+"%");
				}
	    		//汇总值
	    		if (map.containsKey("ssCash")) {
	    			ssCash = (Double)map.get("ssCash");//汇总日初余额
		        	ssCashB = (Double)map.get("ssCashB");//汇总日初余额(本币)
		        	sbuyMoney = (Double)map.get("sbuyMoney");//汇总待进款
		        	sbuyMoneyB = (Double)map.get("sbuyMoneyB");//汇总待进款(本币)
		        	ssellMoney = (Double)map.get("ssellMoney");//汇总待出款
		        	ssellMoneyB = (Double)map.get("ssellMoneyB");//汇总待出款(本币)
		        	sFCashBal = (Double)map.get("sFCashBal");//汇总股指期货保证金
		        	sFCashBalB = (Double)map.get("sFCashBalB");//汇总股指期货保证金(本币)
		        	sproject = (Double)map.get("sproject");//汇总预测数
		        	sprojectB = (Double)map.get("sprojectB");//汇总预测数(本币)
		        	
		        	str += operionStr("合计:",
		    				YssFun.formatNumber(ssCash,"0.00"), 
		    				YssFun.formatNumber(ssCashB,"0.00"),
    						" ",
							YssFun.formatNumber(sbuyMoney,"0.00"),
							YssFun.formatNumber(sbuyMoneyB,"0.00"),
							YssFun.formatNumber(ssellMoney,"0.00"),
							YssFun.formatNumber(ssellMoneyB,"0.00"),
							YssFun.formatNumber(sFCashBal,"0.00"),
							YssFun.formatNumber(sFCashBalB,"0.00"),
							YssFun.formatNumber(sproject,"0.00"),
							YssFun.formatNumber(sprojectB,"0.00"),
							" ");
				}
	        }
	     	//去除最后"\r\n"
			if (str.length()>2) {
				str = YssFun.left(str,str.length()-2);
			}
		} catch (Exception e) {
			throw new YssException("\r\n",e);
		} finally {
			//dbl.closeResultSetFinal(rs);
			this.property = BigDecimal.ZERO;
		}
		return str;
	}

	/**shashijie 2013-5-18 STORY 3857 拼接每行数据 */
	private String operionStr(String r1, String r2, String r3,
			String r4,String r5,String r6,String r7,String r8,String r9,String r10,String r11,String r12
			,String r13) {
		String str = "";
		
		str += r1 + "\t" + r2 + "\t" + r3 + "\t" + r4 + "\t" + r5 + "\t" + r6
				+ "\t" + r7 + "\t" + r8 + "\t" + r9 + "\t" + r10 + "\t" + r11
				+ "\t" + r12 + "\t" + r13 + "\t";
		
		try {
			str = buildRowCompResult(str,"ETFTouCunYuCeWai")+"\r\n";
		} catch (Exception e) {
			str = "";
		}
		
		return str;
	}
	
	/**shashijie 2013-5-18 STORY 3857 把内容拼接上格式 */
	private String buildRowCompResult(String str,String code) throws YssException {
        String strReturn = "";
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";//报表格式HashMap的key
        RepTabCellBean rtc = null;//报表格式--单元格设置
        String[] sArry = null;
        try {
            sArry = str.split("\t");
            hmCellStyle = getCellStyles(code);
            for (int i = 0; i < sArry.length; i++) {
                sKey = code + "\tDSF\t-1\t" + i;
                if (hmCellStyle.containsKey(sKey)) {
                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");
                }
                buf.append(sArry[i]).append("\t");
            }
            if (buf.toString().trim().length() > 1) {
            	strReturn = YssFun.getSubString(buf.toString());
            }
            
            return strReturn + "\t\t";
        } catch (Exception e) {
            throw new YssException("拼接报表格式出错!",e);
        } finally {
            //dbl.closeResultSetFinal(rs);
        }
	}

	/**初始数据方法*/
    public void initBuildReport(BaseBean bean) throws YssException {
        String reqAry[] = null;
        repBean = (CommonRepBean) bean;
        reqAry = repBean.getRepCtlParam().split("\n"); //这里是要获得参数
        
        FStartDate = reqAry[0].split("\r")[1];//日期
        FPortCode = reqAry[1].split("\r")[1];//组合
        FHolidaysCode = reqAry[2].split("\r")[1];//节假日
        
    }

    public String saveReport(String sReport) {
        return "";
    }

	public String getFStartDate() {
		return FStartDate;
	}

	public void setFStartDate(String fStartDate) {
		FStartDate = fStartDate;
	}

	public String getFEndDate() {
		return FPortCode;
	}

	public void setFEndDate(String fEndDate) {
		FPortCode = fEndDate;
	}

	public String getFPortCode() {
		return FHolidaysCode;
	}

	public void setFPortCode(String fPortCode) {
		FHolidaysCode = fPortCode;
	}

	/**shashijie 2013-5-18 STORY 3557 获取工作日方法 */
	private Date getWorkDay(String sHolidayCode, Date dDate, int dayInt)
			throws YssException {
		Date mDate = null;// 工作日
		// 公共获取工作日类
		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(pub);
		mDate = operDeal.getWorkDay(sHolidayCode, dDate, dayInt);
		return mDate;
	}
	
	/**shashijie 2013-5-18 STORY 3857 判断是否是工作日,是返回true*/
	private boolean isWorkDate(Date pDate,String sHolidayCode) throws YssException {
		boolean flag = false;
		if (YssFun.dateDiff(pDate,getWorkDay(sHolidayCode, pDate, 0)) == 0) {
			flag = true;
		}
		return flag;
	}

	
}
