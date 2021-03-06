package com.yss.main.etfoperation.etfaccbook.ETFReport;

import java.sql.ResultSet;
import java.util.Date;

import com.yss.dsub.BaseBean;
import com.yss.main.dao.IBuildReport;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**shashijie 2013-5-14 STORY 3713 需求北京-(博时基金)QDIIV4.0(高)20130307001 ,博时ETF核算*/
public class ETFReportBookInvestor extends BaseBean implements IBuildReport, IDataSetting {
	
	private java.util.Date startDate = null;//开始的申赎日期
	private String standingBookType = "";//台账类型
	private String portCodes = "";//组合代码
	
	//合计值
	private double sFSumReturn = 0;//汇总应退合计
	private double lnum = 0;//篮子数
	
	/**
	 * 构造函数
	 */
	public ETFReportBookInvestor(){
		
	}
	
	public String getPortCodes() {
		return portCodes;
	}

	public void setPortCodes(String portCodes) {
		this.portCodes = portCodes;
	}

	public void initBuildReport(BaseBean bean) throws YssException {

	}

	public String saveReport(String sReport) throws YssException {
		return null;
	}

	public String addSetting() throws YssException {
		return null;
	}

	public void checkInput(byte btOper) throws YssException {
		
	}

	public void checkSetting() throws YssException {
		
	}

	public void delSetting() throws YssException {
		
	}

	public void deleteRecycleData() throws YssException {
		
	}

	public String editSetting() throws YssException {
		return null;
	}

	public String getAllSetting() throws YssException {
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		return null;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		return null;
	}

	public String getBeforeEditData() throws YssException {
		return null;
	}

	public String buildRowStr() throws YssException {
		return null;
	}
	
	public String buildReport(String sType) throws YssException {
		String sETFBookData="";//拼接好的ETF数据
		String [] type=null;
		try{
			if (sType == null) {
				return "";
			}
			type = sType.split("/t");//解析前台传来的数据
			this.parseRowStr(type[1]);//用基类的方法解析数据
			//查询台账
			if (sType != null && sType.indexOf("getETFBookData") != -1) {
				sETFBookData = this.getETFBookData();
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage(), e);
		}
		
		return sETFBookData; 
	}

	/**shashijie 2013-5-15 STORY 3713 获取台账显示数据 */
	private String getETFBookData() throws YssException {
		//添加日期头部分
		String value = getTitleValue(this.startDate,this.standingBookType);
		//封装台账数据
		StringBuffer buffAll = getBookList(this.startDate,this.portCodes,this.standingBookType,value);
		//去除最后"\r\n"
		if (buffAll.toString().length()>2) {
			buffAll = buffAll.delete(buffAll.length()-2, buffAll.length());
		}
		return buffAll.toString();
	}
	
	/**shashijie 2013-5-16 STORY 封装台账对象数据*/
	private StringBuffer getBookList(Date operDate,String portCodes, String bookType,
			String val) throws YssException  {
		StringBuffer buffAll = new StringBuffer();
		ResultSet rs = null;//定义游标
		//清空合计项目,篮子数与退补款金额
		setAllToldSum(0,0);
		try {
			String title = "";//标题
			//申购
			if (bookType.equalsIgnoreCase("B")) {
				title = "投资者\t申购篮子数量\t退补款金额\t参与券商\t退补款日期\t结算会员\t机构代码\r\n";
			} else {//赎回
				title = "投资者\t赎回篮子数量\t退款金额\t参与券商\t退款日期\t结算会员\t机构代码\r\n";
			}
			String query = getStandingBookBeanQuery(operDate,portCodes,bookType);
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				//列头的单位篮子现金差额,拼接第一行标题
				if (rs.isFirst()) {
					val += Math.abs(rs.getDouble("Fsumreturn"))+"\t \r\n";
					buffAll.append(val);
					buffAll.append(title);
				}
				//拼接每行数据
				setStandingBookData(rs,buffAll);
			}
			//获取计算后的汇总值
			if (buffAll.length()>0) {
				setSumData(buffAll);
			}
		} catch (Exception e) {
			throw new YssException("\r\n",e);
		} finally {
			//清空合计项目
			setAllToldSum(0,0);
			dbl.closeResultSetFinal(rs);//关闭游标
		}
		return buffAll;
	}

	/**shashijie 2013-5-27 STORY 3713 获取计算后的汇总值*/
	private void setSumData(StringBuffer buffAll) {
		buffAll.append("合计").append("\t");
		buffAll.append(this.lnum).append("\t");//篮子数
		buffAll.append(Math.abs(this.sFSumReturn)).append("\t");//退补款金额
		buffAll.append(" ").append("\t");
		buffAll.append(" ").append("\t");
		buffAll.append(" ").append("\t");
		buffAll.append(" ").append("\r\n");
	}

	/**shashijie 2013-5-27 STORY 3713 获取SQL*/
	private String getStandingBookBeanQuery(Date operDate, String portCode,
			String bookType) {
		String sql = " Select Js.Fbargaindate,"+
			" Js.Fstockholdercode,"+
			" Js.Fclearcode,"+
			" Js.Num / p.Fnormscale As Num, /*篮子数*/"+
			" Book.Frefunddate, /*退款日期*/"+
			" Book.Fsumreturn As Fsumreturn, /*单位篮子退补款*/"+
			" Round(Book.Fsumreturn * Js.Num / p.Fnormscale, 2) As Returns, /*总退补款*/"+
			" Nvl(b.Fagencycode,' ') As Fagencycode ,"+
			" Nvl(b.Fbrokername,' ') As Fbrokername "+
			" From (Select a.Fportcode," +
			" a.Fbargaindate," +
			" a.Fstockholdercode," +
			" a.Fclearcode," +
			" Sum(a.Ftradeamount) As Num" +
			" From "+pub.yssGetTableName("Tb_Etf_Jsmxinterface")+" a /*ETF结算明细*/"+
			" Where a.Frecordtype = '003'" +
			" And a.Fresultcode = '0000'" +
			" And a.Fbargaindate = "+dbl.sqlDate(operDate)+
			" And a.Ftradetypecode = "+(bookType.equals("B") ? " '102' " : " '103' ")+
			" And a.Fportcode = "+dbl.sqlString(portCode)+
			" Group By a.Fportcode," +
			" a.Fbargaindate," +
			" a.Fstockholdercode," +
			" a.Fclearcode) Js" +
			" Join (Select p.Fnormscale, p.Fportcode" +
			" From "+pub.yssGetTableName("Tb_Etf_Param")+" p "+
			" Where p.Fcheckstate = 1" +
			" And p.Fportcode = "+dbl.sqlString(portCode)+" ) p On Js.Fportcode = p.Fportcode" +
			" /*台账,股票蓝*/" +
			" Join (Select Fbuydate," +
			" Case" +
			" When Bb.Fbs = 'B' Then" +
			" Fsumreturn" +
			" Else" +
			" Fsumreturn - " + dbl.sqlIsNull("s.Money") + 
			" End As Fsumreturn,/*应退合计*/" +
			" Frefunddate," +
			dbl.sqlIsNull("s.Money") + //必须现金替代总金额
			" From (Select k.Fbs," +
			" k.Frefunddate," +
			" k.Fbuydate," +
			" Sum(k.Fsumreturn) As Fsumreturn" +
			" From "+pub.yssGetTableName("Tb_Etf_Standingbook")+" k" +
			" Where k.Fbuydate = "+dbl.sqlDate(operDate)+
            " And k.Fbs = "+dbl.sqlString(bookType)+
            " And k.Fsecuritycode != ' '" +
            " And k.Fportcode = "+dbl.sqlString(portCode)+
            " Group By k.Frefunddate, k.Fbuydate, k.Fbs) Bb" +
            /**Start---panjunfang 2013-7-22  工银上线测试发现的问题，无BUG编号  */
            //股票篮中不一定有替代标识为6的数据，这里不能用join，改为left join
            " Left Join (Select s.Fdate, Sum(s.Ftotalmoney) As Money" +
			/**End---panjunfang 2013-7-22 BUG 工银上线测试发现的问题，无BUG编号*/
            " From "+pub.yssGetTableName("Tb_Etf_Stocklist")+" s" +
    		" Where s.Fdate = "+dbl.sqlDate(operDate)+
    		" And s.Freplacemark = '6'" +
    		" And s.Fportcode = "+dbl.sqlString(portCode)+
    		" Group By s.Fdate) s On s.Fdate = Bb.Fbuydate" +
    		" ) Book On Book.Fbuydate = Js.Fbargaindate" +
    		" Left Join "+pub.yssGetTableName("Tb_Etf_Broker")+" b On Js.Fclearcode = b.Fseatcode";
		return sql;
	}

	/**shashijie 2013-5-16 STORY 3713 计算汇总项与赋值其他关联信息*/
	private void setStandingBookData(ResultSet rs, StringBuffer buffAll) throws Exception {
		buffAll.append(rs.getString("FStockholdercode")).append("\t");//投资者
		buffAll.append(rs.getString("Num")).append("\t");//申购篮子数量
		buffAll.append(Math.abs(rs.getDouble("Returns"))).append("\t");//退补款金额
		buffAll.append(rs.getString("FBrokerName")).append("\t");//参与券商
		buffAll.append(YssFun.formatDate(rs.getDate("Frefunddate"))).append("\t");//退补款日期
		buffAll.append(rs.getString("Fclearcode")).append("\t");//结算会员
		buffAll.append(rs.getString("FAgencycode")).append("\r\n");//机构代码

		
		this.sFSumReturn += rs.getDouble("Returns");//汇总应退合计
		this.lnum += rs.getDouble("Num");//篮子数
	}

	/**shashijie 2013-5-16 STORY 3713 清空合计项目*/
	private void setAllToldSum(double sFSumReturn, double lnum) {
		this.sFSumReturn = sFSumReturn;//应退合计
		this.lnum = lnum;//篮子数
	}

	/**shashijie 2013-5-15 STORY 3713 获取表头部分 */
	private String getTitleValue(Date operDate, String bookType) {
		String value = "";
		if (bookType.trim().equalsIgnoreCase("B")) {
			value = "申购日期：\t";
		} else {
			value = "赎回日期：\t";
		}
		value += YssFun.formatDate(operDate)+"\t \t \t单位篮子退补款金额：\t";
		return value;
	}

	public String getOperValue(String sType) throws YssException {
		return "";
	}
	
	public void parseRowStr(String sRowStr)throws YssException {
		String[] reqAry = null;
		try {
			if(sRowStr.equals("")){
				return;
			}
			
			reqAry = sRowStr.split(",");
			
			this.startDate = YssFun.toDate(reqAry[0]);// 开始的申赎日期
			this.portCodes = reqAry[1]; // 已选组合代码
			this.standingBookType = reqAry[2];
			
		} catch (Exception e) {
			throw new YssException("解析台帐相关数据出错！", e);
		}
	}

	public String getListViewData1() throws YssException {
		return null;
	}

	public String getListViewData2() throws YssException {
		return null;
	}

	public String getListViewData3() throws YssException {
		return null;
	}

	public String getListViewData4() throws YssException {
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		return null;
	}

	public String getTreeViewData1() throws YssException {
		return null;
	}

	public String getTreeViewData2() throws YssException {
		return null;
	}

	public String getTreeViewData3() throws YssException {
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		return null;
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

    public String checkReportBeforeSearch(String sReportType){
    	return "";
    }
	
}
