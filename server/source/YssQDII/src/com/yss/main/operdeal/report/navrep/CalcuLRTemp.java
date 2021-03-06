package com.yss.main.operdeal.report.navrep;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yss.base.BaseAPOperValue;
import com.yss.main.dao.IClientOperRequest;
import com.yss.main.funsetting.FlowBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class CalcuLRTemp extends BaseAPOperValue implements IClientOperRequest {
	private Object obj = null;
	private java.util.Date dStartDate = null;
	private java.util.Date dEndDate = null;
	String startDate = "";
	String endDate = "";
	private String portCode = "";
	int lMonth = 0;
	String YssTablePrefix = "";
	int iYears = 0;

	public int getlMonth() {
		return lMonth;
	}

	public void setlMonth(int lMonth) {
		this.lMonth = lMonth;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public java.util.Date getdStartDate() {
		return dStartDate;
	}

	public void setdStartDate(java.util.Date dStartDate) {
		this.dStartDate = dStartDate;
	}

	public java.util.Date getdEndDate() {
		return dEndDate;
	}

	public void setdEndDate(java.util.Date dEndDate) {
		this.dEndDate = dEndDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getYssTablePrefix() {
		return YssTablePrefix;
	}

	public void setYssTablePrefix(String yssTablePrefix) {
		YssTablePrefix = yssTablePrefix;
	}

	/**
	 * add by huhuichao story 3899 指定日期资产负债表和期间段利润表 解析前台控件中的日期段、组合代码
	 * 
	 * @param Object
	 *            bean
	 * @throws YssException
	 */
	public void init(Object bean) throws YssException {
		String reqAry[] = null;
		String reqAry1[] = null;
		String sRowStr = (String) bean;
		if (sRowStr.trim().length() == 0) {
			return;
		}
		reqAry = sRowStr.split("\n");
		reqAry1 = reqAry[0].split("\r");
		this.dStartDate = YssFun.toDate(reqAry1[1]);
		this.startDate = reqAry1[1];
		reqAry1 = reqAry[1].split("\r");
		this.dEndDate = YssFun.toDate(reqAry1[1]);
		this.endDate = reqAry1[1];
		reqAry1 = reqAry[2].split("\r");
		this.portCode = reqAry1[1];
	}

	/**
	 * add by huhuichao story 3899 指定日期资产负债表和期间段利润表 存储临时利润表
	 * 建立视图 存放临时利润表 
	 * @return Object
	 * @throws YssException
	 */
	public Object invokeOperMothed() throws YssException {
		iYears = YssFun.yearDiff(this.dStartDate, this.dEndDate);
		// ---2009.04.18 蒋锦 添加 流程控制中适用组合的处理---//
		// 参数布局散乱不便操作 MS00003
		// 判断是否在组合中执行
		if (pub.getFlow() != null
				&& pub.getFlow().keySet().contains(pub.getUserCode())) {
			// 插入已执行组合
			((FlowBean) pub.getFlow().get(pub.getUserCode()))
					.setFPortCodes(portCode);
		}
		this.createView();//add huihuichao story 3899 建立视图，此视图保存各年度对应套帐的凭证表
		return null;
	}

	/**
	 * add by huhuichao story 3899 指定日期资产负债表和期间段利润表 存储临时利润表 获取套账号
	 * 获取套账号
	 * @return int
	 * @throws YssException
	 */
	public int getSetCode() throws YssException {
		String SqlStr = "";
		ResultSet rs = null;
		int lset = 0;
		try {
			// 得到套账号
			SqlStr = "select distinct FSetCode from lsetlist where FSetId = "
					+ "(select FASSETCODE from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where fportcode = " + "'" + this.portCode + "'" + ")";
			rs = dbl.openResultSet(SqlStr);
			if (rs.next()) {
				lset = rs.getInt("FSetCode");
			}
		} catch (SQLException es) {
			throw new YssException("Error:\r\n", es);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return lset;
	}

	/**
	 * add by huhuichao story 3899 指定日期资产负债表和期间段利润表 存储临时利润表 获取套账号
	 * 构成获取凭证数据的SQL
	 * @return String
	 * @throws YssException
	 */
	public String getFcwvchDateSQL() throws YssException {
		java.util.Date dDate = null;
		int lYear = 0;
		int lset = 0;
		String SqlStr = "";
		try {
			lset = this.getSetCode();
			dDate = this.dStartDate;
			for (int i = 0; i <= iYears; i++) {
				lYear = YssFun.getYear(dDate);
				YssTablePrefix = getTablePrefix(true, true, lYear, lset);
				if (i == 0 && i==iYears){
				SqlStr = "select a.* , "+"'"+pub.getUserCode()+"'"+" as usercode from " + YssTablePrefix 
				        + "fcwvch a join " + YssTablePrefix 
				        + "LAccount b on a.fkmh = b.facctcode where b.facctclass like '损益%'"
				        + " and a.fdate between " +dbl.sqlDate(this.dStartDate)+ " and "+ dbl.sqlDate(this.dEndDate);
				}
				else if(i == 0 && i!=iYears){
					SqlStr = "select a.* , "+"'"+pub.getUserCode()+"'"+" as usercode from " + YssTablePrefix 
			        + "fcwvch a join " + YssTablePrefix 
			        + "LAccount b on a.fkmh = b.facctcode where b.facctclass like '损益%'"
			        + " and a.fdate >= " +dbl.sqlDate(this.dStartDate);
				}
				else if(i !=0 && i!=iYears){
					SqlStr +=  "union all select a.* , "+"'"+pub.getUserCode()+"'"
					       +" as usercode from " + YssTablePrefix + "fcwvch a join " + YssTablePrefix 
					       + "LAccount b on a.fkmh = b.facctcode where b.facctclass like '损益%'";	
				}
				else if (i==iYears){
					SqlStr +=  "union all select a.* , "+"'"+pub.getUserCode()+"'"
				       +" as usercode from " + YssTablePrefix + "fcwvch a join " + YssTablePrefix 
				       + "LAccount b on a.fkmh = b.facctcode where b.facctclass like '损益%'"
				       + " and a.fdate <= " +dbl.sqlDate(this.dEndDate);
				}
				dDate = YssFun.addYear(dDate, 1);
			}	
		} catch (Exception e) {
			throw new YssException(e);
		}
		return SqlStr;
}
	/**
	 * add by huhuichao story 3899 指定日期资产负债表和期间段利润表 存储临时利润表 获取套账号
	 * 建立视图，此视图保存各年度对应套帐的凭证表
	 * @return void
	 * @throws YssException
	 */
	public void createView() throws YssException {
		String SqlStr = "";
		try {
			SqlStr = "CREATE or REPLACE VIEW " + "VIEW_LR_"+pub.getUserCode()+" as "
			          +this.getFcwvchDateSQL();
			dbl.executeSql(SqlStr);
		} catch (Exception e) {
			throw new YssException("请检查所选期间段的套账是否都存在");
		}
	}
	
	// 以下是表名前缀函数系列*******************************************************
	// 表名前缀，这里为了使用static方法，不再使用默认lYear和lnSet
	public static String getTablePrefix(boolean bYear, boolean bSet, int lYear,
			int lnSet) {
		String stmp;

		if ((lYear > 999 || !bYear) && (lnSet != 0 || !bSet)) { // 年份四位
			stmp = "A" + (bYear ? "" + lYear : "")
					+ (bSet ? (new DecimalFormat("000")).format(lnSet) : "");
			return (stmp.length() == 1) ? "" : stmp;
		}
		return "";
	}

	public String checkRequest(String sType) throws YssException {
		return null;
	}

	public String doOperation(String sType) throws YssException {
		return null;
	}

	public String buildRowStr() throws YssException {
		return null;
	}

	public String getOperValue(String sType) throws YssException {
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
	}

}
