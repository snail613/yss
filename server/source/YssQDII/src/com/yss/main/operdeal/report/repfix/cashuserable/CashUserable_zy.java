package com.yss.main.operdeal.report.repfix.cashuserable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.yss.dsub.BaseBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;
import com.yss.main.operdeal.report.repfix.cashuserable.pojo.CashBean;
import com.yss.main.operdeal.report.repfix.cashuserable.pojo.TaBean;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import java.util.Calendar;

public class CashUserable_zy extends BaseBuildCommonRep {
	public CashUserable_zy() {
	}

	private CommonRepBean repBean;
	private java.util.Date startDate = null; // 期初日期
	private java.util.Date endDate = null; // 期末日期
	private String sPort = ""; // 组合代码
	private String holiday = ""; // 节假日
	private FixPub fixPub = null;

	private String cashAccounts = ""; // 所有从前台传到后台的现金帐户
	private String preScale = ""; // 预估申购款比例
	private String pScale = ""; // 预估赎回款比例

	private HashMap cashEndMap = new HashMap(); // 期末额
	private HashMap foreignMap = new HashMap(); // 外币帐户,key是外币帐户，value是外币折算成人民币的金额
	private HashMap RmbMap = new HashMap(); // 人民币帐户
	private ArrayList preTaList = new ArrayList();
	private ArrayList preCashList = new ArrayList();
	private String preADays = ""; // 预估申购结算延迟天数
	private String preRDays = ""; // 赎回结算延迟天数
	private int days = 0; // 预估的总天数
	private java.util.Date maxNetDate = null; // 获取有最大净值日期的那一天
	private String cnyScale = ""; // 人民币预留金额比例

	private ArrayList listA = new ArrayList();
	private ArrayList listB = new ArrayList();
	private boolean flag = false;

	private boolean day1 = false;
	private boolean day2 = false;
	private int days2 = 0;
	
	private java.util.Date dFiveWorkDay= null;//月末前第5个工作日
    private java.util.Date dTwoWorkDay = null;//初第二个工作日
    private java.util.Date dLastDayOfTheMonth = null;//月末
    private java.util.Date dFirstDayOfTheMonth = null;//当月第一天
	/**
	 * buildReport
	 * 
	 * @param sType
	 *            String
	 * @return String
	 */
	public String buildReport(String sType) throws YssException {
		String sResult = "";
		sResult = buildResult(this.startDate, this.endDate, this.sPort);
		return sResult;
	}

	/**
	 * initBuildReport
	 * 
	 * @param bean
	 *            BaseBean
	 */
	public void initBuildReport(BaseBean bean) throws YssException {
		fixPub = new FixPub();
		fixPub.setYssPub(pub);
		repBean = (CommonRepBean) bean;
		this.parse(repBean.getRepCtlParam());
	}

	public void parse(String str) throws YssException {
		String[] sReq = str.split("\n");
		try {
			this.startDate = YssFun.toDate(sReq[0].split("\r")[1]);
			this.endDate = YssFun.toDate(sReq[1].split("\r")[1]);
			this.sPort = sReq[2].split("\r")[1];
			this.holiday = sReq[3].split("\r")[1];
			this.cashAccounts = sReq[4].split("\r")[1];
			if (sReq.length > 5) {
				this.preScale = sReq[5].split("\r")[1];
			}
			if (sReq.length > 6) {
				this.preADays = sReq[6].split("\r")[1];
			}
			if (sReq.length > 7) {
				this.preRDays = sReq[7].split("\r")[1];
			}
			;
			if (sReq.length > 8) {
				this.pScale = sReq[8].split("\r")[1];
			} else {
				this.pScale = "1";
			}
			if (sReq.length > 9) {
				this.cnyScale = sReq[9].split("\r")[1]; // 预留人民币金额比例
			}
		} catch (Exception e) {
			throw new YssException("解析参数出错", e);
		}
	}

	protected String buildResult(java.util.Date startDate,
			java.util.Date endDate, String sPort) throws YssException {
		String strResult = "";
		ResultSet rs = null;
		String strSql = "";
		String result[] = null;
		BaseOperDeal deal = new BaseOperDeal();
		deal.setYssPub(pub);
		//List list = new ArrayList();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
		java.util.Date titleDate = null;
		StringBuffer buf = null;
		StringBuffer finBuf = new StringBuffer();
		String[] arrAcc = null; // 由于从前台传过来的现金帐户是 帐户1,帐户2这样的行式的，所有要用，分割后放入数组中;
		int days = 0;
		try {

			getMaxNetValue();
			getDays(); // 获取在头表上显示的天数
			getPreTaData(); // 获取需要预估的TA数据
			//getPreCashData(); // 获取需要预估的现金数据
			//getPreCashData(this.startDate); // 获取需要预估的现金数据
			arrAcc = this.cashAccounts.split(",");

			for (int i = 0; i < this.days; i++) {
				if (i == this.days - 1) {
					flag = true;
				}
				titleDate = YssFun.addDay(this.startDate, i);
				getPreCashData(titleDate); // 获取需要预估的现金数据 add by  zhangjun 
				buf = new StringBuffer();
				buf.append(YssFun.formatDate(titleDate)).append(",");
				buf.append(" ").append(",");
				buf.append(" ").append(",");
				buf.append(" ").append(",");
				buf.append(" ").append(",");
				buf.append(" ").append(",");
				finBuf.append(fixPub.buildRowCompResult(buf.toString(), "DS00155"))//edit by wuweiqi BUG #1162 中银基金头寸预测表格式同样也有问题 
						.append("\r\n");
				for (int k = 0; k < arrAcc.length; k++) {
					result = this.getSingleData(titleDate, arrAcc[k]);
					for (int j = 0; j < result.length; j++) {
						finBuf.append(fixPub.buildRowCompResult(result[j],"DS00155")).append("\r\n");//edit by wuweiqi BUG #1162 中银基金头寸预测表格式同样也有问题 
					}
				}
				getFinlData(finBuf);
			}
			if (finBuf.toString().length() > 2) {
				strResult = finBuf.toString().substring(0,
						finBuf.toString().length() - 2);
			}
			return strResult;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	public void delPreCash(java.util.Date titleDate,java.util.Date dDndDate) throws YssException {
		String strSql = "";
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		try {
			strSql = "delete from preCash where fcashaccount in " +
					"(select fcashacccode from "+pub.yssGetTableName("tb_para_cashaccount")
					+" where fportcode=" + dbl.sqlString(this.sPort)+") and FBeginDate >= "
					+ dbl.sqlDate(YssFun.addDay(titleDate, -1))+ " and FBeginDate <= "+ dbl.sqlDate(dDndDate);

			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除预估现金表出错!", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
	/*
	public void delPreCash() throws YssException {
		String strSql = "";
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		try {
			strSql = "delete from preCash where fcashaccount in " +
					"(select fcashacccode from "+pub.yssGetTableName("tb_para_cashaccount")
					+" where fportcode=" + dbl.sqlString(this.sPort)+") and FBeginDate = "
					+ dbl.sqlDate(YssFun.addDay(this.startDate, -1));

			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除预估现金表出错!", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}*/

	public double getInvestValue(java.util.Date titleDate ) throws YssException {
		String strSql = "", strReturn = "";
		ResultSet rs = null;

		//StringBuffer buf = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题

		double money = 0.0;
		String year = null;//业务日期的年份
    	String month = null;//获取业务日期对应的月份
		String maxDay =null;
    	java.util.Date FirstDateOfNextMonth = null;
    	
    	int days = 0; //当月已计提的天数
    	java.util.Date EndDateOfLastMonth = null;//业务日期所在月的上个月份的最后一个自然日
		
		try {
			//STORY #2728 头寸表预测表的两费（管理费和托管费）预估方法调整 modify by zhangjun 2012.06.25
			year = YssFun.formatNumber(titleDate.getYear() + 1900, "0000");//获取业务日期的年份
            month = YssFun.formatNumber(titleDate.getMonth() , "00");//获取业务日期对应的上个月月份
            
            if(titleDate.getMonth() == 0){//若估值日为1月份的日期，则获取去年12月份最后一个自然日的净值数据
            	month = "12";
            	year = YssFun.formatNumber(titleDate.getYear() + 1899, "0000");
            }
            Calendar calendar = convert(titleDate);
            calendar.setTime(YssFun.parseDate(year + "-" + month + "-01"));
            maxDay = YssFun.formatNumber(calendar.getActualMaximum(Calendar.DAY_OF_MONTH),"00");//获取业务日期对应月的最大天数
            
            EndDateOfLastMonth = YssFun.parseDate(year + "-" + month + "-" + maxDay);//获得业务日期所在月的上个月份的最后一个自然日
            
           
            
            if(YssFun.dateDiff(dFiveWorkDay,titleDate)>=0 && YssFun.dateDiff(titleDate,dLastDayOfTheMonth)>=0){//当起始日处于“月末前第5个工作日至月末”期间内
            	//两费金额=起始日前一自然日两费库存/ 当月已计提天数*当月总天数。
            	
            	days = YssFun.dateDiff(this.dFirstDayOfTheMonth, titleDate) ; // 当月已计提的天数  = 起始日 -  当月第一天
            	
            	strSql = " select sum(fmoney) as money from "
					+ pub.yssGetTableName("tb_data_investpayrec")				  
					+ " where ftransdate between "+ dbl.sqlDate(this.dFirstDayOfTheMonth)// 当月第一天 
					+ "and "+ dbl.sqlDate(YssFun.addDay(titleDate, -1))  //起始日的前一自然日
					+ " and FIVPAYCATCODE IN('IV001','IV002') and ftsftypecode='07' and fsubtsftypecode='07IV' " 
					+ "and fcheckstate=1 and fportcode=" + dbl.sqlString(this.sPort);
            	rs = dbl.openResultSet(strSql);
            	while (rs.next()) {
    				money = rs.getDouble("money");
    			}
            	
            	money = money/days*YssFun.endOfMonth(titleDate);
            	
            }else if(YssFun.dateDiff(dFirstDayOfTheMonth,titleDate)>=0 && YssFun.dateDiff(titleDate,dTwoWorkDay)>=0  ){//当起始日处于“月初至月初第二个工作日”期间内
            	//两费金额  = 上月实际计提总金额（上月末两费库存）。
            	
            	strSql = " select sum(FBal) as money from "
					+ pub.yssGetTableName("tb_stock_investpayrec")				  
					+ " where fstoragedate = "+ dbl.sqlDate(EndDateOfLastMonth)// 上月末					
					+ " and FIVPAYCATCODE IN('IV001','IV002') and ftsftypecode='07' and fsubtsftypecode='07IV' " 
					+ "and fcheckstate=1 and fportcode=" + dbl.sqlString(this.sPort);
            	rs = dbl.openResultSet(strSql);
            	while (rs.next()) {
    				money = rs.getDouble("money");
    			}
            	
            	
            }
            /*
            strSql = " select sum(fmoney) as money from "
					+ pub.yssGetTableName("tb_data_investpayrec")
				  //+ " where ftransdate between "+ dbl.sqlDate(YssFun.addDay(this.startDate, -21))//-zhouss 20110422 应该取1号到20号的数据，只减20天
					+ " where ftransdate between "+ dbl.sqlDate(YssFun.addDay(this.startDate, -20))
					+ "and "+ dbl.sqlDate(YssFun.addDay(this.startDate, -1))
					+ " and FIVPAYCATCODE IN('IV001','IV002') and ftsftypecode='07' and fsubtsftypecode='07IV' " 
					+ "and fcheckstate=1 and fportcode=" + dbl.sqlString(this.sPort);
			rs = dbl.openResultSet(strSql);
			
			while (rs.next()) {
				money = rs.getDouble("money");
			}
			*/
            
            //STORY #2728 头寸表预测表的两费（管理费和托管费）预估方法调整 modify by zhangjun 2012.06.25
			
			return  money;
		} catch (SQLException ex) {
            throw new YssException(ex.getMessage());
		} catch (Exception e) {
			throw new YssException("获取金额出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	public String getCashAccount() throws YssException {
		String strSql = "", strReturn = "";
		ResultSet rs = null;
		//StringBuffer buf = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
		String cashAccount = "";
		try {
			strSql = " select distinct(a.fcashacccode) from "+ pub.yssGetTableName("tb_para_investpay ")
			+ " a where   a.fcheckstate=1 and a.fcashacccode <> ' ' and fportcode=" + dbl.sqlString(this.sPort);//-zhoushusheng 20110402 只获取运营款项的账户
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				cashAccount = rs.getString("fcashacccode");
			}
			return cashAccount;
		} catch (Exception e) {
			throw new YssException("获取现金帐户出错!", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
/*
	public void insertPreCash()throws  Exception
    {
    	String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; // 代表是否开始了事务
        java.util.Date payDate=null;
        java.util.Date endDate=null;
        BaseOperDeal deal = new BaseOperDeal();
        double money=0.0;
        String cashAccount="";
        //SimpleDateFormat sformat=new SimpleDateFormat("yyyy-MM-01");  //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        try {
        	deal.setYssPub(pub);
        	delPreCash();
        	//payDate=deal.getWorkDay(this.holiday,YssFun.addDay(YssFun.toDate(YssFun.getYear(this.startDate)+"-"+YssFun.getMonth(this.startDate)+"-01"),YssFun.endOfMonth(this.startDate)),1);
        	//zhouss 20110504 将支付日期改为每个月的第2个工作日
        	payDate=deal.getWorkDay(this.holiday,YssFun.addDay(YssFun.toDate(YssFun.getYear(this.startDate)+"-"+YssFun.getMonth(this.startDate)+"-01"),YssFun.endOfMonth(this.startDate)),2);
        	endDate=YssFun.addDay(payDate, 31);//-zhoushusheng 20110402 截止日默认增加30天 解决日期选择到截止日之后的时候 该笔款失效未扣除
        	money=this.getInvestValue();
        	cashAccount=this.getCashAccount();
            strSql = "insert into preCash(FBeginDate,FEndDate,FPayDate,FCashAccount,"+
                     " FResume,FMoney,FInOut,FCURYCODE) values(";
            strSql = strSql + dbl.sqlDate(YssFun.addDay(this.startDate, -1)) + "," +
                dbl.sqlDate(endDate)
//                + "," + dbl.sqlDate(payDate) + ","+dbl.sqlString(cashAccount)+","+ dbl.sqlString("应付费用")+","+money+","+"-1,'CNY')";
                //zhouss 20110519 预估费用从当月21号就开始扣除 一直扣到下个月初 第2个工作日  
                + "," + dbl.sqlDate(this.startDate) + ","+dbl.sqlString(cashAccount)+","+ dbl.sqlString("应付两费")+","+money+","+"-1,'CNY')";
          
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (SQLException se) {
            throw new YssException("增加预估现金出错!", se);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
                	
    }
    */

	/**
	 * 2012.06.27 story#2728
	 * zhangjun
	 * @param titleDate
	 * @throws Exception
	 */
	public void insertPreCash(java.util.Date titleDate , java.util.Date  dDndDate)throws  Exception
    {
    	String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; // 代表是否开始了事务
        java.util.Date payDate=null;
        java.util.Date endDate=null;
        BaseOperDeal deal = new BaseOperDeal();
        double money=0.0;
        String cashAccount="";
        SimpleDateFormat sformat=new SimpleDateFormat("yyyy-MM-01");  
        try {
        	deal.setYssPub(pub);
        	//delPreCash();
        	delPreCash(titleDate,dDndDate);
        	//zhouss 20110504 将支付日期改为每个月的第2个工作日
        	payDate=deal.getWorkDay(this.holiday,YssFun.addDay(YssFun.toDate(YssFun.getYear(titleDate)+"-"+YssFun.getMonth(titleDate)+"-01"),YssFun.endOfMonth(titleDate)),2);
        	endDate=YssFun.addDay(payDate, 31);//-zhoushusheng 20110402 截止日默认增加30天 解决日期选择到截止日之后的时候 该笔款失效未扣除
        	//money=this.getInvestValue();
        	money=this.getInvestValue(titleDate);
        	cashAccount=this.getCashAccount();
            strSql = "insert into preCash(FBeginDate,FEndDate,FPayDate,FCashAccount,"+
                     " FResume,FMoney,FInOut,FCURYCODE) values(";
            strSql = strSql + dbl.sqlDate(YssFun.addDay(titleDate, -1)) + "," +
                dbl.sqlDate(endDate)
//                + "," + dbl.sqlDate(payDate) + ","+dbl.sqlString(cashAccount)+","+ dbl.sqlString("应付费用")+","+money+","+"-1,'CNY')";
                //zhouss 20110519 预估费用从当月21号就开始扣除 一直扣到下个月初 第2个工作日  
                + "," + dbl.sqlDate(titleDate) + ","+dbl.sqlString(cashAccount)+","+ dbl.sqlString("应付两费")+","+money+","+"-1,'CNY')";
          
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (SQLException se) {
            throw new YssException("增加预估现金出错!", se);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
                	
    }
    public void getPreCashData(java.util.Date titleDate) throws YssException {
        String strSql = "", acc = "";//MS00815 QDV4交银2009年11月18日01_AB  add by chenyibo 
        ResultSet rs = null;
        CashBean tempCash = null;
        String[] arrAcc = null; ////MS00815 QDV4交银2009年11月18日01_AB  add by chenyibo 
        
        //STORY #2728 头寸表预测表的两费（管理费和托管费）预估方法调整 add by zhangjun 
        String year = null;//业务日期的年份
    	String month = null;//获取业务日期对应的月份
    	String nextMonth = null;//获取业务日期对应的下个月月份
    	String nextyear = null;//业务日期的年份
    	String maxDay = null;//上个月的最大天数
    	Date endDateOfTheMonth = null;//业务日期所在月的最后一个自然日
    	//Date sDate = null;//当月月末前第5个工作日
    	//Date eDate = null;//下月第2个工作日
        
        try {
		//--- MS00815 QDV4交银2009年11月18日01_AB  add by chenyibo-------------------- 
            if (dbl.yssTableExist("preCash")) { //判断是否有preCash表存在
            	delPreCashInvest(); //-zhoushusheng 20110402 先删除已实际支付的运营款项
            	
            	//STORY #2728 头寸表预测表的两费（管理费和托管费）预估方法调整    modify by zhangjun 2012.06.25 
            	// 由“每月21日”调整为“起始日处于当月月末前第5个工作日和下月第2个工作日之间”
            	BaseOperDeal deal = new BaseOperDeal();
    			deal.setYssPub(pub);
            	
            	year = YssFun.formatNumber(titleDate.getYear() + 1900, "0000");//获取业务日期的年份
                month = YssFun.formatNumber(titleDate.getMonth() + 1, "00");//获取业务日期对应的月份
                
                
                Calendar calendar = convert(titleDate);
                calendar.setTime(YssFun.parseDate(year + "-" + month + "-01"));
                maxDay = YssFun.formatNumber(calendar.getActualMaximum(Calendar.DAY_OF_MONTH),"00");//获取业务日期对应月的最大天数
                
                endDateOfTheMonth = YssFun.parseDate(year + "-" + month + "-" + maxDay);//得到业务日期当月的最后一个自然日
                
                if(isHoliday(this.holiday,endDateOfTheMonth)){
                	this.dFiveWorkDay = deal.getWorkDay(this.holiday, endDateOfTheMonth, -5) ;//当月月末前第5个工作日
                }else{
                	this.dFiveWorkDay = deal.getWorkDay(this.holiday, endDateOfTheMonth, -4) ;//当月月末前第5个工作日
                }
                
                
                this.dLastDayOfTheMonth = endDateOfTheMonth;
                this.dFirstDayOfTheMonth = YssFun.parseDate(year + "-" + month + "-01");
                this.dTwoWorkDay = deal.getWorkDay(this.holiday,YssFun.parseDate(year + "-" + month + "-01"),2);//本月的第2个工作日
                //dateDiff(date1,date2)返回两个日期相差date2-date1的天数
                //起始日处于“月初至月初第二个工作日” 或  “月末前第5个工作日至月末”
                /*if ( (YssFun.dateDiff(dFirstDayOfTheMonth,titleDate)>=0 && YssFun.dateDiff(titleDate,dTwoWorkDay)>=0 )
                		|| (YssFun.dateDiff(dFiveWorkDay,titleDate)>=0 && YssFun.dateDiff(titleDate,dLastDayOfTheMonth)>=0)) {
					insertPreCash(titleDate);
				}*/
                
                if ( titleDate.equals(startDate)) {
					insertPreCash(titleDate,endDate);
				}
                /*
            	if (YssFun.getDay(this.startDate) == 21) {
					insertPreCash();
				}*/
            	//STORY #2728 头寸表预测表的两费（管理费和托管费）预估方法调整  modify by zhangjun 2012.06.25 
                arrAcc = this.cashAccounts.split(",");
                for (int k = 0; k < arrAcc.length; k++) {
                	acc = acc+"'"+arrAcc[k]+"',";
                }
                if (acc.toString().length() > 1) {
                	acc = acc.toString().substring(0,acc.toString().length() - 1);
                }
                strSql = "select * from preCash where fcashaccount in ("+acc+")"; //读取预估的现金
		//---MS00815 QDV4交银2009年11月18日01_AB  end --------------------------------
                rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					tempCash = new CashBean();
					tempCash.setBeginDate(rs.getDate("FBeginDate"));
					tempCash.setEndDate(rs.getDate("FEndDate"));
					tempCash.setPayDate(rs.getDate("FPayDate"));
					tempCash.setAccount1(rs.getString("FCashAccount"));
					// tempCash.setAccount2(rs.getString("cashAccount2"));
					tempCash.setResume(rs.getString("FResume"));
					tempCash.setMoney(rs.getDouble("FMoney"));
					tempCash.setCashWay(rs.getString("FInOut"));
					preCashList.add(tempCash); // 把读到的预估数据放入list中
				}
			}
			dbl.closeResultSetFinal(rs); // 关闭记录集

		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}
    
    /**
     * 判断是否为节假日
     * add by zhangjun 
     * @param sPerExpCode String
     * @param dDate Date
     * @return double
     * @throws YssException
     */
    private boolean isHoliday(String strHolidaycode ,java.util.Date endDateOfTheMonth
                               ) throws
        YssException {
        boolean dResult =false;
        java.util.Date settleDate=null;
        int days=0;
        BaseOperDeal deal = new BaseOperDeal();
        deal.setYssPub(pub);
        try {
        	
            settleDate=deal.getWorkDay(strHolidaycode, endDateOfTheMonth,0);
            days=YssFun.dateDiff(endDateOfTheMonth, settleDate);
            if(days!=0){
            	dResult=true;
            }
            else{
            	dResult=false;
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException("判断是否为节假日出错", e);
        } finally {
            
        }
    }
    
    private static Calendar convert(java.util.Date date) {   
        Calendar calendar = Calendar.getInstance();   
        calendar.setTime(date);   
        return calendar;   
    }  


	// -----------------------------------获取头寸表上的最后一天
	public void getDays() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		java.util.Date endDate = null;
		try {
			BaseOperDeal deal = new BaseOperDeal();
			deal.setYssPub(pub);
			// ---------------------------------计算在头寸表上显示的最后一天---------------------
			if (dbl.yssTableExist("preTA")) { // 判断是否有tempta表存在
                strSql = "select max(FApplyDate) as sqdate from preTA where ffundcode = (select fassetcode from  "+pub.yssGetTableName("Tb_Para_Portfolio")+" where fportcode = '"+sPort+"')";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					if (rs.getDate("sqdate") != null) {
						endDate = deal.getWorkDay(this.holiday, // 如果有预估的ta数据就根据节假日群代码和预估数据中的最大日期推算出现金头寸表上的最后一天
					    //edit by licai 20101207 BUG #534 现金头寸预测表日期段太长时，日期未起作用 
//								rs.getDate("sqdate"), 18);
						rs.getDate("sqdate"), deal.workDateDiff(this.startDate, this.endDate, this.holiday, 0));
						//edit by licai 20101207 BUG #534===================================end
					}
				}
			}
			if (endDate == null) {
				 //edit by licai 20101207 BUG #534 现金头寸预测表日期段太长时，日期未起作用 
//				endDate = YssFun.addDay(this.startDate, 18); // 如果没有预估的TA数据,那么在头寸表上体现的日期是开始日期+10天
				//endDate = deal.getWorkDay(this.holiday, this.endDate); // 如果没有预估的TA数据,那么在头寸表上体现的日期是开始日期到结束日期之间的工作日
				//edit by licai 20101207 BUG #534==================================end
				endDate = this.endDate;
			}
//			this.days = deal.workDateDiff(this.startDate, this.endDate, this.holiday, 0);
			//edit by qiuxufeng 20110112 485 QDV4中银2011年01月07日01_AB 预估天数为开始日期到结束日期的所有自然日（包括开始和结束日期） 
			this.days = YssFun.dateDiff(this.startDate, this.endDate) + 1;
			this.days2 = YssFun.dateDiff(this.endDate, endDate);
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	public void getPreTaData() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		TaBean tempTa = null;
		try {
            if (dbl.yssTableExist("preTA")) { //判断是否有tempta表存在
			//--- MS00815 QDV4交银2009年11月18日01_AB  add by chenyibo -------
                strSql =
                    "select * from preTA where FApplyDate not in(select FTRADEDATE from " +pub.yssGetTableName("tb_ta_trade") +
                    " where FOperType<>'00') and ffundcode = (select fassetcode from  "+pub.yssGetTableName("Tb_Para_Portfolio")+" where fportcode = '"+sPort+"') order by FApplyDate";
			//--- MS00815 QDV4交银2009年11月18日01_AB  end -------------------		
                rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					tempTa = new TaBean();
					tempTa.setFundCode(rs.getString("FFundCode")); // 基金代码
					tempTa.setTradeDate(rs.getDate("FApplyDate")); // 申请日期
					tempTa.setTradeType(rs.getString("FOperType")); // 交易类型
					if (rs.getString("FOperType").equals("022")) {
						tempTa.setTradeAmount(rs.getDouble("FApplyAmount")
								* YssFun.toDouble(this.preScale));
					} else {
						tempTa.setTradeAmount(rs.getDouble("FApplyAmount")
								* YssFun.toDouble(this.pScale)
								* YssD.round(
										this.getUnitValue(this.maxNetDate), 3)); // 赎回有的客户要有比例，有的客户不需要
					}
					preTaList.add(tempTa);
				}
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	public String[] getSingleData(java.util.Date titleDate, String cashAccCode)
			throws YssException {
		String strSql = "";
		String[] arrOccurMoney = null; // 发生额
		double[] arrTotalMoney = null; // 库存
		StringBuffer buf = new StringBuffer();
		// ----------------------------------同一个帐户的期初金额----------------------------------
		try {
			setTotalMoney(titleDate, cashAccCode, buf); // 获取汇总数据
			arrOccurMoney = this.getOccurMoney(titleDate, cashAccCode, buf); // 获取发生额
			setFinalData(titleDate, cashAccCode); // 设置人民币+外币折算成人民币的金额
			return arrOccurMoney;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	// --------------------------------------获取汇总数据---------------------------------
	public void setTotalMoney(java.util.Date titleDate, String cashAccCode,
			StringBuffer buf) throws YssException {
		//double[] money = new double[4]; // 里面放的是昨日余额，当日汇总的流出，当日汇总的流入，当日的期末余额//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
		double beginMoney = 0.0; // 期初余额
		double endMoney = 0.0; // 期末余额
		double inMoney = 0.0; // 流入金额
		double outMoney = 0.0; // 流出金额
		ResultSet rs = null;
		String strSql = "";

		Iterator it = null;
		TaBean tempTa = null;
		CashBean tempCash = null;
		java.util.Date taSettleDate = null;
		double preAMoney = 0.0;
		double preRMoney = 0.0;
		String curyCode = "";
		double preMoney = 0.0; // 预留人民币金额
		double reMoney = 0.0; // 逆回购到期流入金额

		double preMoneyB = 0.0; // 提前获取预留人民币金额

		try {

			preMoneyB = this.getNetValue(YssFun
					.addDay(startDate, this.days - 1)); //

			//curyCode = getCuryCode(cashAccCode);
			BaseOperDeal deal = new BaseOperDeal();
			deal.setYssPub(pub);
			it = preTaList.iterator();
			while (it.hasNext()) {
				// ----------------------------------------------------------预估TA--------------------------
				tempTa = (TaBean) it.next();
				if (tempTa.getTradeType().equals("022")) { // 申购
					taSettleDate = deal.getWorkDay(this.holiday, tempTa
							.getTradeDate(), YssFun.toInt(this.preADays));
					if (taSettleDate.equals(titleDate)) {
						if (this.checkTAAcc(cashAccCode)) {
							preAMoney = tempTa.getTradeAmount();
						}
					}
				} else { // 赎回
					taSettleDate = deal.getWorkDay(this.holiday, tempTa
							.getTradeDate(), YssFun.toInt(this.preRDays));
					if (taSettleDate.equals(titleDate)) {
						if (this.checkTAAcc(cashAccCode)) {
							preRMoney = tempTa.getTradeAmount();
						}
					}
				}
			}
			if (this.checkTAAcc(cashAccCode)//zhouss 20110601 增加对基金成立 账户判断以区分定存
					&& YssFun.addDay(startDate, this.days - 1)
							.equals(titleDate)) {
				preMoney = this.getNetValue(startDate);
			}
			if (titleDate.equals(this.startDate)) { // 如果是头寸表上的第一天，那么昨日余额=前一天的库寸
				strSql = " select FCashAccCode,FAccBalance from "
						+ pub.yssGetTableName("tb_stock_cash")
						+ " where FCashAccCode="
						+ dbl.sqlString(cashAccCode)
						+ " and FStorageDate="
						+ dbl.sqlDate(maxNetDate)
						+ " and FPORTCODE="
						+ dbl.sqlString(this.sPort)
						+ " and FYearMonth = "
						+ dbl.sqlString(YssFun.formatDate(maxNetDate, "yyyy")
								+ YssFun.formatDate(maxNetDate, "MM"))
						+ " and FCheckState=1";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					beginMoney = rs.getDouble("FAccBalance");

				}
				dbl.closeResultSetFinal(rs); // 关闭记录集
			} else { // 如果不是头寸表上的第一天那么期初余额=前一天的期末余额
				beginMoney = ((Double) cashEndMap.get(cashAccCode))
						.doubleValue();
			}
			// ------------------------------------同一个帐户的流入--------------------------
			strSql = " select sum(b.inMoney) as inMoney from"
					+ " (select Fnum from "
					+ pub.yssGetTableName("tb_cash_transfer")
					+ " where FTransferdate="
					+ dbl.sqlDate(titleDate)
					//edit by songjie 2011.06.27 BUG 2131 QDV4中银基金2011年6月21日01_B
					+ " and FCheckState=1 ) a "//zhouss 不加分红但是应该加上存款利息收入
					+ // 20090616现金头寸预测表中不需要增加股票分红的数据
					" join(select FNum,FMoney as inMoney from "
					+ pub.yssGetTableName("tb_cash_subtransfer")
					+ " where FCashAccCode=" + dbl.sqlString(cashAccCode)
					+ " and FInout=1 and FCheckState=1)b on b.FNum=a.FNum";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				inMoney = rs.getDouble("inMoney") + preAMoney;
			}
			dbl.closeResultSetFinal(rs);
			// ------------------------------------同一个帐户的流出------------------------
			strSql = " select sum(b.outMoney) as outMoney from"
					+ " (select Fnum from "
					+ pub.yssGetTableName("tb_cash_transfer")
					+ " where FTransferdate=" + dbl.sqlDate(titleDate)
					+ " and FCheckState=1)a"
					+ " join(select FNum,FMoney as outMoney from "
					+ pub.yssGetTableName("tb_cash_subtransfer")
					+ " where FCashAccCode=" + dbl.sqlString(cashAccCode)
					+ " and FInout=-1 and FCheckState=1)b on b.FNum=a.FNum";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				outMoney = rs.getDouble("outMoney") + preRMoney + preMoney;
			}
			dbl.closeResultSetFinal(rs);

			// --------------------------------------------处理逆回购到期流入的问题-----------------------
			strSql = " select * from (select (FTRADEMONEY+FACCRUEDINTEREST) as inMoney ,FCashAccCode,FMATURESETTLEDATE from "
					+ pub.yssGetTableName("tb_data_subtrade")
					+ // 修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306
					" where FMATURESETTLEDATE= "
					+ dbl.sqlDate(titleDate)
					+ " and  FCheckState = 1 and FCashAccCode ="
					+ dbl.sqlString(cashAccCode)
					+ " )m left join (select b.inMoney as TransferMoney,"
					+ " a.FTRANSFERDATE as TransferDate,b.FCashAccCode TransferAccCode"
					+ " from  (select Fnum ,FTRANSFERDATE from "
					+ pub.yssGetTableName("tb_cash_transfer")
					+ " where FTransferdate ="
					+ // 修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306
					dbl.sqlDate(titleDate)
					+ " and FCheckState = 1) a  join (select FNum, FMoney as inMoney ,FCashAccCode from "
					+ pub.yssGetTableName("tb_cash_subtransfer")
					+ " where FCashAccCode ="
					+ // 修改表组合群前缀自动生成，而非写死001 sunkey 20090310 BugNO:MS00306
					dbl.sqlString(cashAccCode)
					+ "and FInout = 1 and FCheckState = 1) b on b.FNum = a.FNum "
					+ ")n on n.TransferMoney=m.inMoney and n.TransferDate=m.FMATURESETTLEDATE and m.FCashAccCode=n.TransferAccCode";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				if (rs.getDate("TransferDate") == null
						&& rs.getDouble("TransferMoney") == 0.0
						&& rs.getString("TransferAccCode") == null) {
					inMoney = inMoney + rs.getDouble("inMoney");
				}
			}
			dbl.closeResultSetFinal(rs);
			// ---------------------------------------------------------------------------------------
			it = preCashList.iterator();
			while (it.hasNext()) {
				tempCash = (CashBean) it.next();
				if (YssFun.dateDiff(tempCash.getBeginDate(), endDate) >= 0
						&& YssFun.dateDiff(tempCash.getEndDate(), endDate) <= 0) {
					if (cashAccCode.equalsIgnoreCase(tempCash.getAccount1())) {
						if (titleDate.equals(tempCash.getPayDate())) {
							if (tempCash.getCashWay().equalsIgnoreCase("1")) {
								inMoney = inMoney + tempCash.getMoney();
							} else {
								outMoney = outMoney + tempCash.getMoney();
							}
						}
					}
				}
			}
			// ------------------------------------------同一个帐户的期末金额-------------------

			endMoney = beginMoney + inMoney - outMoney; // 期末余额=期初余额+流入-流出
			if (cashEndMap.containsKey(cashAccCode)) {
				cashEndMap.remove(cashAccCode);
			}
			cashEndMap.put(cashAccCode, new Double(endMoney));
			buf.append(" ").append(",");
			buf.append(". " + cashAccCode).append(",");
			buf.append(" ").append(",");
			buf.append(beginMoney).append(",");
			buf.append(outMoney).append(",");
			buf.append(inMoney).append(",");
			buf.append(endMoney).append("\f\f");

			// --------------------------------------------------------------------------------

			if (this.checkTAAcc(cashAccCode)) {//zhouss 20110601 增加对基金成立 账户判断以区分定存
				if (deal.getWorkDay(this.holiday, this.endDate, 1).equals(
						titleDate)) {
					this.day1 = true;
				}
				if (deal.getWorkDay(this.holiday, this.endDate, 2).equals(
						titleDate)) {
					this.day2 = true;
				}
				if (this.day1) {
					if (YssFun.dateDiff(this.endDate, titleDate) > 0) { // 之前用的是startDate
						if (YssFun.dateDiff(titleDate, YssFun.addDay(
								this.endDate, this.days2 - 1)) > 0) { // 之前用的是startDate
							double endMoneyA = beginMoney - outMoney; // 期末人民币金额=期初-当日流出
							double moneyA = endMoney - preMoneyB; // 当日人民币日末余额－最后一天的预留人民币金额流出
							double A = Math.min(endMoneyA, moneyA);
							listA.add(new Double(A));
						}
					}
				}
				if (this.day2) {
					if (YssFun.dateDiff(this.endDate, titleDate) > 1) { // 之前用的是startDate
						if (YssFun.dateDiff(titleDate, YssFun.addDay(
								this.endDate, this.days2 - 1)) > 0) {
							double endMoneyB = beginMoney - outMoney; // 期末人民币金额=期初-当日流出
							double moneyB = endMoney - preMoneyB; // 当日人民币日末余额－最后一天的预留人民币金额流出
							double B = Math.min(endMoneyB, moneyB);
							listB.add(new Double(B));
						}
					}
				}
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	// ----------------------------------------------------------------------------------------------------

	// --------------------------------------获取发生额----------------------------------
	public String[] getOccurMoney(java.util.Date titleDate, String cashAccCode,
			StringBuffer buf) throws YssException {
		ResultSet rs = null;
		String strSql = "";
		String[] arrResult = null;
		Iterator it = null;
		TaBean tempTa = null;
		String preTaStr1 = "";
		String preTaStr2 = "";
		java.util.Date taSettleDate = null;
		String curyCode = "";
		String preRMB = "";
		double preMoney = 0.0;
		CashBean tempCash = null;
		StringBuffer preCashBuf = new StringBuffer();
		String preStr = "";
		String preCashStr = "";
		String[] arrpreCashStr = null;
		double unSettleMoney = 0.0;
		double taTradeMoney = 0.0;
		String unSettle = "";
		try {

			curyCode = getCuryCode(cashAccCode);
			BaseOperDeal deal = new BaseOperDeal();
			deal.setYssPub(pub);
			it = preTaList.iterator();
			while (it.hasNext()) {
				// ----------------------------------------------------------预估TA--------------------------
				tempTa = (TaBean) it.next();
				if (tempTa.getTradeType().equals("022")) { // 申购
					if (this.checkTAAcc(cashAccCode)) {//zhouss 2011-06-01 预估数据也要判断是否是 基金成立账户 
						taSettleDate = deal.getWorkDay(this.holiday, tempTa
								.getTradeDate(), YssFun.toInt(this.preADays));
						if (taSettleDate.equals(titleDate)) {
							preTaStr1 = " union "
									+ " select '预估申购款(流入)' as FCuryCode,+"
									+ tempTa.getTradeAmount()
									+ " as inMoney, 0 as outMoney from dual";
						}
					}
				} else { // 赎回
					if (this.checkTAAcc(cashAccCode)) {//zhouss 2011-06-01 预估数据也要判断是否是 基金成立账户 
						taSettleDate = deal.getWorkDay(this.holiday, tempTa
								.getTradeDate(), YssFun.toInt(this.preRDays));
						if (taSettleDate.equals(titleDate)) {
							preTaStr2 = " union "
									+ " select '预估赎回款(流出)' as FCuryCode, 0 as inMoney,"
									+ tempTa.getTradeAmount()
									+ " as outMoney from dual";
						}
					}
				}
			}
			//zhouss 20110601 去掉预留人民币 中银没有提过相关需求
//			if (this.checkTAAcc(cashAccCode)
//					&& YssFun.addDay(startDate, this.days - 1)
//							.equals(titleDate)) {
//				preMoney = this.getNetValue(startDate);
//				unSettleMoney = this.getUnSettleMoney();
//				taTradeMoney = this.getTaConfigMoney();
//				preRMB = " union "
//						+ " select '预留人民币金额(流出)' as FCuryCode, 0 as outMoney, "
//						+ preMoney + " as outMoney from dual";
//
//				/*
//				 * unSettle = " union " + " select '未清算金额' as FCuryCode, 0 as
//				 * outMoney, " + (unSettleMoney - taTradeMoney) + " as outMoney
//				 * from dual";
//				 */
//			}
			it = preCashList.iterator();
			while (it.hasNext()) {
				tempCash = (CashBean) it.next();
				//if (YssFun.dateDiff(tempCash.getBeginDate(), this.startDate) > 0
						//&& YssFun.dateDiff(tempCash.getEndDate(), this.startDate) < 0) {
				if (YssFun.dateDiff(tempCash.getBeginDate(), titleDate) > 0
					     && YssFun.dateDiff(tempCash.getEndDate(), titleDate) < 0) {
					if (cashAccCode.equalsIgnoreCase(tempCash.getAccount1())) {
						if (titleDate.equals(tempCash.getPayDate())) {
							if (tempCash.getCashWay().equalsIgnoreCase("1")) {
								preStr = " union select "
										+ dbl.sqlString(tempCash.getResume())
										+ " as FCuryCode,"
										+ tempCash.getMoney()
										+ " as inMoney,0 as outMoney from dual ";
							} else {
								preStr = " union select "
										+ dbl.sqlString(tempCash.getResume())
										+ " as FCuryCode,0 as inMoney,"
										+ tempCash.getMoney()
										+ " as outMoney from dual ";
							}
							preCashBuf.append(preStr).append("\t");
						}
					}
				}
			}

			// -------------------------------------------基金成立(流入)=----------------------------
			strSql = " select '基金成立(流入)' as FCuryCode,FSettleMoney as inMoney, 0 as outMoney from "
					+ pub.yssGetTableName("tb_ta_trade ")
					+ " where FSettleDate="
					+ dbl.sqlDate(titleDate)
					+ " and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FSellType='00' and  "
					+ " FCashAccCode="
					+ dbl.sqlString(cashAccCode)
					+ "and FCheckState=1 "
					+
					// ---------------------------------------------申购款(流入)----------------------------------
					" union "
					+ " select '申购款(流入)' as FCuryCode,sum(FSettleMoney) as inMoney, sum(0) as outMoney from "
					+ pub.yssGetTableName("tb_ta_trade ")
					+ " where FSettleDate="
					+ dbl.sqlDate(titleDate)
					+ " and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FSellType='01' and  "
					+ " FCashAccCode="
					+ dbl.sqlString(cashAccCode)
					+ "and FCheckState=1 group by FCashAccCode "
					+
					// ---------------------------------------------赎回款(流出)----------------------------------
					" union "
					+ " select '赎回款(流出)' as FCuryCode,sum(0) as inMoney ,sum(FSettleMoney) as outMoney from "
					+ pub.yssGetTableName("tb_ta_trade ")
					+ " where FSettleDate="
					+ dbl.sqlDate(titleDate)
					+ " and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FSellType='02' and  "
					+ " FCashAccCode="
					+ dbl.sqlString(cashAccCode)
					+ "and FCheckState=1 group by FCashAccCode "
					+
					// ------------------------------------------外汇交易买入(流入)--------------------------
					" union "
					+ " select '外汇交易(买入)' as FCuryCode, sum(FBMoney) as inMoney ,sum(0) as outMoney from "
					+ pub.yssGetTableName("tb_data_ratetrade")
					+ " where FBSettleDate= "
					+ dbl.sqlDate(titleDate)
					+ " and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FBCashAccCode="
					+ dbl.sqlString(cashAccCode)
					+ "and FCheckState=1 group by FBCashAccCode "
					+
					// ------------------------------------------外汇交易卖出(流出)--------------------------
					" union "
					+ " select '外汇交易(卖出)' as FCuryCode, sum(0) as inMoney ,sum(FSMoney) as outMoney from "
					+ pub.yssGetTableName("tb_data_ratetrade")
					+ " where FSettleDate= "
					+ dbl.sqlDate(titleDate)
					+ " and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FSCashAccCode="
					+ dbl.sqlString(cashAccCode)
					+ "and FCheckState=1 group by FSCashAccCode "
					+
					// ------------------------------------------股票证券清算款(流入)--------------------------
					" union "
					+ " select  '股票证券清算款(流入)' as FCuryCode,  sum(FTotalCost) as inMoney ,sum(0) as outMoney from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FCheckState = 1  and  FTradeTypeCode='02' and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FCashAccCode = "
					+ dbl.sqlString(cashAccCode)
					//edit by songjie 2011.06.15 BUG 2065 QDV4中银基金2011年6月9日01_B 将FSettleDate 改为 FFactSettleDate
					+ " and FFactSettleDate ="
					+ dbl.sqlDate(titleDate)
					+ "group by FCashAccCode "
					+
					// ------------------------------------------股票证券清算款(流出)--------------------------
					" union "
					+ " select  '股票证券清算款(流出)' as FCuryCode, sum(0) as inMoney,sum(FTotalCost) as outMoney from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FCheckState = 1  and  FTradeTypeCode='01' and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FCashAccCode = "
					+ dbl.sqlString(cashAccCode)
					//edit by songjie 2011.06.15 BUG 2065 QDV4中银基金2011年6月9日01_B 将FSettleDate 改为 FFactSettleDate
					+ " and FFactSettleDate ="
					+ dbl.sqlDate(titleDate)
					+ "group by FCashAccCode "
					+
					// ------------------------------------------逆回购证券清算款(流出)--------------------------
					" union "
					+ " select  '逆回购证券清算款(流出)' as FCuryCode,sum(0) as inMoney, sum(FTotalCost) as outMoney from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FCheckState = 1  and  FTradeTypeCode='25' and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FCashAccCode = "
					+ dbl.sqlString(cashAccCode)
					//edit by songjie 2011.06.15 BUG 2065 QDV4中银基金2011年6月9日01_B 将FSettleDate 改为 FFactSettleDate
					+ " and FFactSettleDate ="
					+ dbl.sqlDate(titleDate)
					+ "group by FCashAccCode"
					+
					// ------------------------------------------正回购证券清算款(流入)--------------------------
					" union "
					+ " select  '正回购证券清算款(流入)' as FCuryCode, sum(FTotalCost) as inMoney ,sum(0) as outMoney from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FCheckState = 1  and  FTradeTypeCode='24' and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FCashAccCode = "
					+ dbl.sqlString(cashAccCode)
					//edit by songjie 2011.06.15 BUG 2065 QDV4中银基金2011年6月9日01_B 将FSettleDate 改为 FFactSettleDate
					+ " and FFactSettleDate ="
					+ dbl.sqlDate(titleDate)
					+ "group by FCashAccCode"
					+
					// ------------------------------------------逆回购到期(流入)--------------------------
					" union "
					+ " select  '逆回购到期(流入)' as FCuryCode, sum((FTRADEMONEY+FACCRUEDINTEREST)) as inMoney ,sum(0) as outMoney from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FCheckState = 1  and  FTradeTypeCode='25' and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FCashAccCode = "
					+ dbl.sqlString(cashAccCode)
					+ " and FMATURESETTLEDATE ="
					+ dbl.sqlDate(titleDate)
					+ "group by FCashAccCode "
					
					// ------------------------------------------股票分红到帐(流入)--------------------------------
					//---edit by songjie 2011.06.25 BUG 2131 QDV4中银基金2011年6月21日01_B---//
					+ " union select '股票分红到帐(流入)' as FCuryCode,"
					+ " sum(FAccruedinterest) as inMoney ,sum(0) as outMoney from "
					+ pub.yssGetTableName("Tb_Data_SubTrade") + " where "
					+ " FCheckState = 1 and FTradeTypeCode = '06' and "
					+ " FPortCode = " + dbl.sqlString(this.sPort)
					+ " and FCashAccCode = " + dbl.sqlString(cashAccCode) 
					+ " and FFactSettleDate = " + dbl.sqlDate(titleDate) 
					+ " group by FCashAccCode " +
					//---edit by songjie 2011.06.25 BUG 2131 QDV4中银基金2011年6月21日01_B---// 
					// 20090616现金头寸预测表中不需要增加股票分红的数据
					// -------------------------------------------债券兑付(流入)-------------------------------
					" union "
					+ " select  '债券兑付' as FCuryCode, sum(FTradeMoney) as inMoney ,sum(0) as outMoney from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FCheckState = 1  and  FTradeTypeCode='17' and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FCashAccCode = "
					+ dbl.sqlString(cashAccCode)
					//edit by songjie 2011.06.15 BUG 2065 QDV4中银基金2011年6月9日01_B 将FSettleDate 改为 FFactSettleDate
					+ " and FFactSettleDate ="
					+ dbl.sqlDate(titleDate)
					+ "group by FCashAccCode "
					+
					// ------------------------------------------存款利息收入--------------------------------------
					/*
					 * " union " + " select '存款利息' as FCuryCode, sum(b.inMoney)
					 * as inMoney,sum(0) as outMoney from " + " (select Fnum
					 * ,Fsubtsftypecode from " +
					 * pub.yssGetTableName("tb_cash_transfer") + " where
					 * FTransferdate= "+dbl.sqlDate(titleDate)+ " and
					 * FCheckState=1 and FTsFtypecode='02' ) a " + " join(select
					 * FNum,FMoney as inMoney,FCashAccCode from
					 * "+pub.yssGetTableName("tb_cash_subtransfer") + " where
					 * FCashAccCode = " +dbl.sqlString(cashAccCode) + " and
					 * FInout=1 and FCheckState=1)b on b.FNum=a.FNum " + " group
					 * by FCashAccCode " +
					 */
					//zhouss 20110528
					// -------------------------------------------定期存款(流入)-------------------------------
					" union "
					+ " select  '定存流入' as FCuryCode, sum(b.fmoney) as inMoney ,sum(0) as outMoney from "
				    +pub.yssGetTableName("tb_cash_transfer")
				    +" a left join " + pub.yssGetTableName("tb_cash_subtransfer") + " b on a.fnum = b.fnum "
					+ " where a.ftsftypecode = '01' and a.fsubtsftypecode = '0003' and b.finout = 1"
					+" and a.ftransferdate = "+ dbl.sqlDate(titleDate)
					+" and b.FCashAccCode = "+ dbl.sqlString(cashAccCode)
					+" and b.fportcode = "+ dbl.sqlString(this.sPort)
					+ " group by FCashAccCode "
					+
					
					//zhouss 20110528
					// -------------------------------------------存款利息收入-------------------------------
					" union "
					+ " select  '存款利息收入' as FCuryCode, sum(b.fmoney) as inMoney ,sum(0) as outMoney from "
				    +pub.yssGetTableName("tb_cash_transfer")
				    +" a left join " + pub.yssGetTableName("tb_cash_subtransfer") + " b on a.fnum = b.fnum "
					+ " where a.ftsftypecode = '02' and a.fsubtsftypecode = '02DE' and b.finout = 1"
					+" and a.ftransferdate = "+ dbl.sqlDate(titleDate)
					+" and b.FCashAccCode = "+ dbl.sqlString(cashAccCode)
					+" and b.fportcode = "+ dbl.sqlString(this.sPort)
					+ " group by FCashAccCode "
					+
					
					//zhouss 20110528
					// -------------------------------------------定期存款(流出)-------------------------------
					" union "
					+ " select  '定存流出' as FCuryCode,sum(0) as inMoney , sum(b.fmoney) as outMoney from "
				    +pub.yssGetTableName("tb_cash_transfer")
				    +" a left join " + pub.yssGetTableName("tb_cash_subtransfer") + " b on a.fnum = b.fnum "
					+ " where a.ftsftypecode = '01' and a.fsubtsftypecode = '0003' and b.finout = -1"
					+" and a.ftransferdate = "+ dbl.sqlDate(titleDate)
					+" and b.FCashAccCode = "+ dbl.sqlString(cashAccCode)
					+" and b.fportcode = "+ dbl.sqlString(this.sPort)
					+ " group by FCashAccCode "
					+
					
					// ------------------------------------------费用支出------------------------------------------
					" union "
					+ " select '支付运营款项' as FCuryCode, sum(0) as inMoney,sum(b.outMoney) as outMoney from "
					+ " (select Fnum ,Fsubtsftypecode from "
					+ pub.yssGetTableName("tb_cash_transfer")
					+ " where FTransferdate= "
					+ dbl.sqlDate(titleDate)
					+ " and FCheckState=1 and FTsFtypecode='03') a "
					+ " join(select FNum,FMoney as outMoney,FCashAccCode from "
					+ pub.yssGetTableName("tb_cash_subtransfer")
					+ " where FCashAccCode = "
					+ dbl.sqlString(cashAccCode)
					+ " and FInout=-1 and FCheckState=1)b on b.FNum=a.FNum "
					+ " group by  FCashAccCode "
					+ preTaStr1
					+ preTaStr2
					+ preRMB + unSettle;
			if (preCashBuf.length() > 1) {
				preCashStr = preCashBuf.toString().substring(0,
						preCashBuf.toString().length() - 1);
				arrpreCashStr = preCashStr.split("\t");
				for (int i = 0; i < arrpreCashStr.length; i++) {
					strSql = strSql + arrpreCashStr[i];
				}
			}
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				buf.append(" ").append(",");
				buf.append(" ").append(",");
				buf.append(". " + rs.getString("FCuryCode")).append(",");
				buf.append("0").append(",");
				buf.append(rs.getDouble("outMoney")).append(",");
				buf.append(rs.getDouble("inMoney")).append(",");
				buf.append("0").append("\f\f");
			}
			arrResult = buf.toString().split("\f\f");
			return arrResult;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	public String getCuryCode(String cashAccCode) throws YssException {
		try {
			String curyCode = ""; // 现金帐户的货币是为了取汇率
			CashAccountBean cash = new CashAccountBean();
			cash.setYssPub(pub);
			cash.setStrCashAcctCode(cashAccCode);
			cash.getSetting();
			curyCode = cash.getStrCurrencyCode();
			return curyCode;

		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	public void setFinalData(java.util.Date titleDate, String cashAccCode)
			throws YssException {

		double baseRate = 0.0;
		double portRate = 0.0;
		double rmbMoney = 0.0;
		double foreignMoney = 0.0;
		String curyCode = "";
		try {
			curyCode = getCuryCode(cashAccCode);
			baseRate = this.getExchangeRate(curyCode, this.sPort, "base");
			portRate = this.getExchangeRate(curyCode, this.sPort, "port");
			if (curyCode.equals("CNY")) {
				if (this.cashEndMap.containsKey(cashAccCode)) {
					rmbMoney = ((Double) this.cashEndMap.get(cashAccCode))
							.doubleValue();
				}
				if (this.RmbMap.containsKey(cashAccCode)) {
					this.RmbMap.remove(cashAccCode);
				}
				RmbMap.put(cashAccCode, new Double(rmbMoney));
			} else {
				if (this.cashEndMap.containsKey(cashAccCode)) {
					foreignMoney = ((Double) this.cashEndMap.get(cashAccCode))
							.doubleValue();
//					foreignMoney = foreignMoney * baseRate / portRate;
					if(portRate!=0.0){
						foreignMoney = foreignMoney * baseRate / portRate;
					}
					//modify by wuweiqi 20110303 BUG #1162 中银基金头寸预测表格式同样也有问题 
				}
				if (this.foreignMap.containsKey(cashAccCode)) {
					this.foreignMap.remove(cashAccCode);
				}
				this.foreignMap.put(cashAccCode, new Double(foreignMoney));
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	public void getFinlData(StringBuffer finBuf) throws YssException {
		StringBuffer bufForeign = null;
		StringBuffer bufCny = null;

		StringBuffer buf2 = null;
		StringBuffer buf3 = null;
		double money2 = 0.0;
		double money3 = 0.0;
		Set set = null;
		Iterator it = null;
		double foreignMoney = 0.0;
		double rmbMoney = 0.0;
		try {
			BaseOperDeal deal = new BaseOperDeal();
			deal.setYssPub(pub);
			//buf2 = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
			//buf3 = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
			bufForeign = new StringBuffer();
			bufCny = new StringBuffer();
			set = foreignMap.keySet();
			it = set.iterator();
			while (it.hasNext()) {
				foreignMoney = foreignMoney
						+ ((Double) foreignMap.get((String) it.next()))
								.doubleValue();
			}
			set = RmbMap.keySet();
			it = set.iterator();
			while (it.hasNext()) {
				rmbMoney = rmbMoney
						+ ((Double) RmbMap.get((String) it.next()))
								.doubleValue();
			}
			bufForeign.append(" ").append(",");
			bufForeign.append(" ").append(",");
			bufForeign.append(". 外币折算人民币小计").append(",");
			bufForeign.append(" ").append(",");
			bufForeign.append(" ").append(",");
			bufForeign.append(" ").append(",");
			bufForeign.append(String.valueOf(foreignMoney)).append(",");
			finBuf
					.append(
							fixPub.buildRowCompResult(bufForeign.toString(),
									"DS00155")).append("\r\n"); //edit by wuweiqi  BUG #1162 中银基金头寸预测表格式同样也有问题 
			// -----------------------------------计算(所有外币+所有人民币)折算成人民币的金额------
			bufCny.append(" ").append(",");
			bufCny.append(" ").append(",");
			bufCny.append(". 折算人民币小计").append(",");
			bufCny.append(" ").append(",");
			bufCny.append(" ").append(",");
			bufCny.append(" ").append(",");
			bufCny.append(String.valueOf(foreignMoney + rmbMoney)).append(",");
			finBuf.append(
					fixPub.buildRowCompResult(bufCny.toString(), "DS00155"))
					.append("\r\n");
			// --------------------------------T+2日最小可用人民币交割头寸-----------------------
			//edit by qiuxufeng 20110112 485 QDV4中银2011年01月07日01_AB 中银不需要显示最小可用人民币这一项
//			if (flag == true) {
//				//QDV4赢时胜上海2010年07月13日01_B by wxl:在查询时若选择的账户无人民币账户，因listA为null会抛错
//				if(listA.size()>0) money2 = ((Double) listA.get(0)).doubleValue();
//				for (int i = 1; i < listA.size(); i++) {
//					money2 = Math.min(money2, ((Double) listA.get(i))
//							.doubleValue());
//				}
//				buf2.append(" ").append(",");
//				buf2.append(" ").append(",");
//				buf2.append(
//						YssFun.formatDate(
//								deal.getWorkDay(this.holiday, this.endDate, 1))
//								.toString()
//								+ "最小可用人民币").append(",");
//				buf2.append(" ").append(",");
//				buf2.append(" ").append(",");
//				buf2.append(" ").append(",");
//				buf2.append(String.valueOf(money2)).append(",");
//				finBuf.append(
//						fixPub.buildRowCompResult(buf2.toString(), "DS00156")) //edit by licai 20101207 BUG #534 现金头寸预测表日期段太长时，日期未起作用 
//						.append("\r\n");
//				// --------------------------------T+3日最小可用人民币交割头寸-----------------------
//				//QDV4赢时胜上海2010年07月13日01_B by wxl:在查询时若选择的账户无人民币账户，，因listB为null会抛错
//				if(listB.size()>0) money3 = ((Double) listB.get(0)).doubleValue();
//				for (int i = 1; i < listB.size(); i++) {
//					money3 = Math.min(money3, ((Double) listB.get(i))
//							.doubleValue());
//				}
//
//				buf3.append(" ").append(",");
//				buf3.append(" ").append(",");
//				buf3.append(
//						YssFun.formatDate(
//								deal.getWorkDay(this.holiday, this.endDate, 2))
//								.toString()
//								+ "最小可用人民币").append(",");
//				buf3.append(" ").append(",");
//				buf3.append(" ").append(",");
//				buf3.append(" ").append(",");
//				buf3.append(String.valueOf(money3)).append(",");
//				finBuf.append(
//						fixPub.buildRowCompResult(buf3.toString(), "DS00156")) //edit by licai 20101207 BUG #534 现金头寸预测表日期段太长时，日期未起作用 
//						.append("\r\n");
//			}
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	// ----------------------------------------获取汇率-------------------------------
	public double getExchangeRate(String curyCode, String portCode,
			String rateType) throws YssException {
		double sResult = 0.0;
		java.util.Date inceptionDate = null;
		java.util.Date navDate = null;
		try {
			inceptionDate = this.getInceptionDate();
			if (this.startDate.equals(inceptionDate)) {
				navDate = startDate;
			} else {
				navDate = YssFun.addDay(startDate, -1);
			}
			BaseOperDeal operDeal = new BaseOperDeal();
			operDeal.setYssPub(pub);
			sResult = operDeal.getCuryRate(navDate, curyCode, portCode,
					rateType);
			return sResult;

		} catch (Exception e) {
			throw new YssException("获取汇率出错!", e);
		}
	}

	// --------------------------------------获取资产净值------------------------------
	public double getNetValue(java.util.Date startDate) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		double sResult = 0.0;
		java.util.Date inceptionDate = null;
		java.util.Date navDate = null;
		try {

			inceptionDate = this.getInceptionDate();
			if (this.startDate.equals(inceptionDate)) {
				navDate = startDate;
			} else {
				navDate = maxNetDate;
			}
			//------ 添加组合代码条件 modify by wangzuochun 2011.03.21 BUG #1520 头寸表区分组合
			strSql = " select FPortMarketValue from "
					+ pub.yssGetTableName("tb_data_navdata")
					+ " where FKeyCode='TotalValue' and  FPortCode = " + dbl.sqlString(this.sPort) + " and FNAVDATE = "
					+ dbl.sqlDate(navDate);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				sResult = rs.getDouble("FPortMarketValue");
			}
			sResult = YssD.mul(YssD.round(sResult, 2), YssFun
					.toDouble(this.cnyScale)); // 取出来的资产净值保留2位,因为净值表上是保留2位的方便验证数据-20080904
			return sResult;
		} catch (Exception e) {
			throw new YssException("获取净值出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	// ---------------------------------获取单位净值----------------------------------
	public double getUnitValue(java.util.Date startDate) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		double sResult = 0.0;
		java.util.Date inceptionDate = null;
		java.util.Date navDate = null;
		try {
			inceptionDate = this.getInceptionDate();
			if (this.startDate.equals(inceptionDate)) {
				navDate = startDate;
			} else {
				navDate = maxNetDate;
			}
			//------ 添加组合代码条件 modify by wangzuochun 2011.03.21 BUG #1520 头寸表区分组合
			strSql = " select FPrice from "
					+ pub.yssGetTableName("tb_data_navdata")
					+ " where FKeyCode='Unit' and  FPortCode = " + dbl.sqlString(this.sPort) + " and FNAVDATE = "
					+ dbl.sqlDate(navDate);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				sResult = rs.getDouble("FPrice");
			}
			return sResult;
		} catch (Exception e) {
			throw new YssException("获取单位净值出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	public java.util.Date getInceptionDate() throws YssException {
		PortfolioBean port = new PortfolioBean();
		try {
			port.setYssPub(pub);
			port.setPortCode(this.sPort);
			port.getSetting();
			return port.getInceptionDate();

		} catch (Exception e) {
			throw new YssException("获取成立日期报错");
		}
	}

	public void getMaxNetValue() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		try {
			//------ 添加组合代码条件 modify by wangzuochun 2011.03.21 BUG #1520 头寸表区分组合
			strSql = " select FNAVDATE as navdate from "
					+ pub.yssGetTableName("tb_data_navdata")
					+ " where FretypeCode='Total' and FKEYCODE='Unit' and FPortCode = " + dbl.sqlString(this.sPort) + " and FNAVDATE = "
					+ dbl.sqlDate(YssFun.addDay(this.startDate, -1));
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				this.maxNetDate = rs.getDate("navdate");
			}
			if(this.maxNetDate == null){
            	this.maxNetDate = this.startDate;
            }
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	public double getUnSettleMoney() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		double money = 0.0;
		try {
			/*
			 * strSql = " select sum(FTotalCost) as money from " +
			 * pub.yssGetTableName("tb_data_subtrade") + " where FsettleState=0
			 * and FBARGAINDATE<=" + dbl.sqlDate(this.startDate);
			 */
			strSql = " select FCashAccCode,FAccBalance from "
					+ pub.yssGetTableName("tb_stock_cash")
					+ " where FCuryCode="
					+ dbl.sqlString("CNY")
					+ " and FStorageDate="
					+ dbl.sqlDate(maxNetDate)
					+ " and FPORTCODE="
					+ dbl.sqlString(this.sPort)
					+ " and FYearMonth = "
					+ dbl.sqlString(YssFun.formatDate(maxNetDate, "yyyy")
							+ YssFun.formatDate(maxNetDate, "MM"))
					+ " and FCheckState=1";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				money = rs.getDouble("FAccBalance");
			}
			return money;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	public double getTaConfigMoney() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		double money = 0.0;
		try {
			strSql = " select  sum(FSettleMoney) as money from "
					+ pub.yssGetTableName("tb_ta_trade")
					+ " where  (FConfimDate="
					+ dbl.sqlDate(YssFun.addDay(this.maxNetDate, -4))
					+ " or FConfimDate="
					+ dbl.sqlDate(YssFun.addDay(this.maxNetDate, -3)) + ")"
					+ " and FConfimDate<" + dbl.sqlDate(this.startDate)
					+ " and FSELLType='02'";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				money = rs.getDouble("money");
			}
			strSql = " select  sum(FSettleMoney) as money from "
					+ pub.yssGetTableName("tb_ta_trade")
					+ " where  FConfimDate="
					+ dbl.sqlDate(YssFun.addDay(this.maxNetDate, -2))
					+ " and FConfimDate<" + dbl.sqlDate(this.startDate)
					+ " and FSELLType='01'";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				money = rs.getDouble("money") + money;
			}
			return money;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
    //add by fangjiang 2010.12.22 STORY #301 需在进行现金头寸预测表查询之前，先对以下数据进行检查
    public String checkReportBeforeSearch(String para) throws YssException {
        String result = "";
        String strSql = "";
        ResultSet rs = null;
//      String[] sReq = para.split("\n");
//      java.util.Date beginDate = YssFun.toDate(sReq[0].split("\r")[1]);   
//      String headDays = sReq[1].split("\r")[1];
//      String portCode = sReq[2].split("\r")[1];
//      String cashAccounts = sReq[4].split("\r")[1]; 
//      java.util.Date endDate = YssFun.addDay(beginDate, Integer.parseInt(headDays)-1);
        java.util.Date beginDate = null, endDate = null;
        String portCode = "";
        String cashAccounts = "";
        String[] sReq = para.split("\f\t")[1].split("\n");
        String[] reAry = null;
        for (int i = 0; i < sReq.length; i++) {
        	reAry = sReq[i].split("\r");
			if(reAry[0].equalsIgnoreCase("1")) {
				beginDate = YssFun.toDate(reAry[1]);
			} else if(reAry[0].equalsIgnoreCase("2")) {
				endDate = YssFun.toDate(reAry[1]);
			} else if(reAry[0].equalsIgnoreCase("3")) {
				portCode = reAry[1];
			} else if(reAry[0].equalsIgnoreCase("5")) {
				cashAccounts = reAry[1];
			}
		}
        
        try {
        	//在交易数据中查询结算日期为开始日期 到 （开始日期 + 头寸预估天数）的未审核数据
        	strSql = " select FPortCode, FSecurityCode, FBargainDate, FTradeTypeCode, FNum from " +
            		 pub.yssGetTableName("Tb_Data_SubTrade") +
                     " where FCheckState = 0 and FPortCode = " + dbl.sqlString(portCode) +
                     " and FCashAccCode in ( " + operSql.sqlCodes(cashAccounts) +
                     //edit by songjie 2011.06.15 BUG 2065 QDV4中银基金2011年6月9日01_B 将FSettleDate 改为 FFactSettleDate
                     " ) and FFactSettleDate between " + dbl.sqlDate(beginDate) + " and " + dbl.sqlDate(endDate);
        	rs = dbl.openResultSet_antReadonly(strSql);
        	rs.last();
        	if( rs.getRow() > 0){
        		result += "以下交易数据未审核：\r\n" ;
        		rs.beforeFirst();
            	while(rs.next()){
            		result += "投资组合:" + rs.getString("FPortCode") + "  "
            			      + "证券代码:" + rs.getString("FSecurityCode") + "  "
            			      + "交易日期:" + rs.getDate("FBargainDate") + "  "
            			      + "交易类型:" + rs.getString("FTradeTypeCode") + "  "
            			      + "交易编号:" + rs.getString("FNum") + "\r\n";
            	}      	
        	}	
        	dbl.closeResultSetFinal(rs);
        	//在TA交易数据中查询结算日期为开始日期 到 （开始日期 + 头寸预估天数）的未审核数据
        	strSql = " select a.FPortCode as FPortCode, b.FSellTypeName as FSellTypeName, a.FTradeDate as FTradeDate, a.FConfimDate as FConfimDate from " +
            		 pub.yssGetTableName("Tb_TA_Trade") +
            		 " a join " + pub.yssGetTableName("Tb_TA_SellType") +
            		 " b on a.FSellType = b.FSellTypeCode " +
                     " where a.FCheckState = 0 and a.FPortCode = " + dbl.sqlString(portCode) +
                     " and a.FCashAccCode in ( " + operSql.sqlCodes(cashAccounts) +
                     " ) and a.FSettleDate between " + dbl.sqlDate(beginDate) + " and " + dbl.sqlDate(endDate);
        	rs = dbl.openResultSet_antReadonly(strSql);
        	rs.last();
        	if( rs.getRow() > 0){
        		result += "\r\n";
        		result += "以下TA交易数据未审核：\r\n" ;
        		rs.beforeFirst();
        		while(rs.next()){
            		result += "投资组合:" + rs.getString("FPortCode") + "  "
            			      + "销售类型:" + rs.getString("FSellTypeName") + "  "
            			      + "成交日期:" + rs.getDate("FTradeDate") + "  "
            			      + "确认日期:" + rs.getDate("FConfimDate") + "\r\n";
            	}
        	}
        	dbl.closeResultSetFinal(rs);
        	//在外汇交易数据中查询买入结算日期或卖出结算日期为开始日期 到 （开始日期 + 头寸预估天数）的未审核数据
        	strSql = " select FPortCode, FBCashAccCode, FSCashAccCode, FTradeDate, FBSettleDate, FSettleDate from " +
            		 pub.yssGetTableName("tb_data_ratetrade") +
                     " where FCheckState = 0 and FPortCode = " + dbl.sqlString(portCode) +
                     " and ( FBCashAccCode in ( " + operSql.sqlCodes(cashAccounts) + 
                     " ) or FSCashAccCode in ( " + operSql.sqlCodes(cashAccounts) + " ) ) " +
                     " and ( FBSettleDate between " + dbl.sqlDate(beginDate) + " and " + dbl.sqlDate(endDate) + 
                     " or FSettleDate between " + dbl.sqlDate(beginDate) + " and " + dbl.sqlDate(endDate) + " ) ";
        	rs = dbl.openResultSet_antReadonly(strSql);
        	rs.last();
        	if( rs.getRow() > 0){
        		result += "\r\n";
        		result += "以下外汇交易数据未审核：\r\n" ;
        		rs.beforeFirst();
        		while(rs.next()){
            		result += "投资组合:" + rs.getString("FPortCode") + "  "
            			      + "买入现金账户:" + rs.getString("FBCashAccCode") + "  "
            			      + "卖出现金账户:" + rs.getString("FSCashAccCode") + "  "
            			      + "交易日期:" + rs.getDate("FTradeDate") + "  "
            			      + "卖出结算日期:" + rs.getDate("FSettleDate") + "  "
            			      + "买入结算日期:" + rs.getDate("FBSettleDate") + "\r\n";
            	}
        	}
        	dbl.closeResultSetFinal(rs);
        	//在资金调拨数据中查询调拨日期为开始日期 到 （开始日期 + 头寸预估天数）的未审核数据
        	strSql = " select b.FPortCode as FPortCode, a.FNum as FNum, a.FTransDate as FTransDate, a.FTransferDate as FTransferDate, a.FTsfTypeCode as FTsfTypeCode, c.FTsfTypeName as FTsfTypeName from " +
            		 pub.yssGetTableName("tb_cash_transfer") +
            		 " a join " + pub.yssGetTableName("tb_cash_subtransfer") +
            		 " b on a.fnum = b.fnum join " + pub.yssGetTableName("Tb_Base_TransferType") +
            		 " c on a.FTsfTypeCode = c.FTsfTypeCode" +
                     " where a.FCheckState = 0 and b.FPortCode = " + dbl.sqlString(portCode) +
                     " and b.FCashAccCode in ( " + operSql.sqlCodes(cashAccounts) +
                     " ) and a.FTransferDate between " + dbl.sqlDate(beginDate) + " and " + dbl.sqlDate(endDate);
        	rs = dbl.openResultSet_antReadonly(strSql);
        	rs.last();
        	if( rs.getRow() > 0){
        		result += "\r\n";
        		result += "以下资金调拨数据未审核：\r\n" ;
        		rs.beforeFirst();
        		while(rs.next()){
            		result += "投资组合:" + rs.getString("FPortCode") + "  "
            			      + "调拨编号:" + rs.getString("FNum") + "  "
            			      + "业务日期:" + rs.getDate("FTransDate") + "  "
            			      + "调拨日期:" + rs.getDate("FTransferDate") + "  "
            			      + "调拨类型:" + rs.getString("FTsfTypeCode") + " " + rs.getString("FTsfTypeName") + "\r\n";
            	}
        	} 
        	dbl.closeResultSetFinal(rs);
        	//在交易数据中查询结算日期为开始日期 到 （开始日期 + 头寸预估天数）的已审核未结算数据
        	strSql = " select FPortCode, FSecurityCode, FBargainDate, FTradeTypeCode, FNum from " +
            		 pub.yssGetTableName("Tb_Data_SubTrade") +
                     " where FCheckState = 1 and FSettleState = 0 and FPortCode = " + dbl.sqlString(portCode) +
                     " and FCashAccCode in ( " + operSql.sqlCodes(cashAccounts) +
                     //edit by songjie 2011.06.15 BUG 2065 QDV4中银基金2011年6月9日01_B 将FSettleDate 改为 FFactSettleDate
                     " ) and FFactSettleDate between " + dbl.sqlDate(beginDate) + " and " + dbl.sqlDate(endDate);
        	rs = dbl.openResultSet_antReadonly(strSql);
        	rs.last();
        	if( rs.getRow() > 0){
        		result += "\r\n";
        		result += "以下交易数据未结算：\r\n" ;
        		rs.beforeFirst();
        		while(rs.next()){
            		result += "投资组合:" + rs.getString("FPortCode") + "  "
            			      + "证券代码:" + rs.getString("FSecurityCode") + "  "
            			      + "交易日期:" + rs.getDate("FBargainDate") + "  "
            			      + "交易类型:" + rs.getString("FTradeTypeCode") + "  "
            			      + "交易编号:" + rs.getString("FNum") + "\r\n";
            	}     
        	}
        	dbl.closeResultSetFinal(rs);
        	//在TA交易数据中查询结算日期为开始日期 到 （开始日期 + 头寸预估天数）的已审核未结算数据
        	strSql = " select FPortCode, FSellTypeName, FTradeDate, FConfimDate from " +
            		 pub.yssGetTableName("Tb_TA_Trade") +
            		 " a join " + pub.yssGetTableName("Tb_TA_SellType") +
            		 " b on a.FSellType = b.FSellTypeCode " +
                     " where a.FCheckState = 1 and a.FSettleState = 0 and a.FPortCode = " + dbl.sqlString(portCode) +
                     " and a.FCashAccCode in ( " + operSql.sqlCodes(cashAccounts) +
                     " ) and a.FSettleDate between " + dbl.sqlDate(beginDate) + " and " + dbl.sqlDate(endDate);
        	rs = dbl.openResultSet_antReadonly(strSql);
        	rs.last();
        	if( rs.getRow() > 0){
        		result += "\r\n";
        		result += "以下TA交易数据未结算：\r\n" ;
        		rs.beforeFirst();
        		while(rs.next()){
            		result += "投资组合:" + rs.getString("FPortCode") + "  "
            			      + "销售类型:" + rs.getString("FSellTypeName") + "  "
            			      + "成交日期:" + rs.getDate("FTradeDate") + "  "
            			      + "确认日期:" + rs.getDate("FConfimDate") + "\r\n";
            	}
        	}
        	if(result.length() == 0)
        	{
        		result = "现金头寸预测表相关的所有数据均已审核和已结算！";
        	}
            return result;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
  //-zhoushusheng 20110402 出头寸表时，先发实际已经支付的运营款项从预估现金表中删除
    public void delPreCashInvest() throws YssException {
		String strSql = "";
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		try {
			strSql  = "delete  from preCash where exists (select * from preCash  pre join  "+
//				+pub.yssGetTableName("tb_cash_transfer")+
//					" a on pre.fpaydate = a.ftransferdate  " +
//					"join "+pub.yssGetTableName("tb_cash_subtransfer")+" b on a.fnum = b.fnum and pre.fcashaccount = b.fcashacccode" +
//					" and a.fsubtsftypecode = '03IV' where a.fcheckstate = 1)";
				 "(select max(a.ftransferdate) as maxpaydate,fcashacccode from " +pub.yssGetTableName("tb_cash_transfer")+ " a join "+pub.yssGetTableName("tb_cash_subtransfer")+" b on a.fnum = b.fnum " +
				 "where a.fcheckstate = 1 " +
				 "and a.fsubtsftypecode = '03IV'" +
				 "group by  fcashacccode )a on pre.fpaydate < a.maxpaydate  and pre.fcashaccount = a.fcashacccode)";
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除预估现金表中预估运营款项出错!", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
    
    //zhouss 20110528 增加检查是否为TA账户的判断 以区分人民币定存账户
	public boolean checkTAAcc(String accCode) throws YssException {
		boolean result = false;
		ResultSet rs = null;
		try {
			//------ 添加组合代码条件 modify by wangzuochun 2011.03.21 BUG #1520 头寸表区分组合
		String strSql = " select * from "+pub.yssGetTableName("tb_ta_trade")+" a " +
				"where a.fselltype = '00' and a.fportcode ="+ dbl.sqlString(this.sPort)
				+"and a.fcashacccode = '" +accCode+"'";
		rs = dbl.openResultSet_antReadonly(strSql);
			while (rs.next()) {
				result = true;
			}
			return result;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
}
