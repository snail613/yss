package com.yss.main.etfoperation.etfaccbook.ETFReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.dao.IBuildReport;
import com.yss.main.dao.IDataSetting;
import com.yss.main.etfoperation.etfaccbook.SearchAccBook;
import com.yss.main.etfoperation.pojo.StandingBookBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**shashijie 2013-5-14 STORY 3713 需求北京-(博时基金)QDIIV4.0(高)20130307001 ,博时ETF核算*/
public class ETFReportBook extends BaseBean implements IBuildReport, IDataSetting {
	
	private java.util.Date startDate = null;//开始的申赎日期
	private java.util.Date endDate = null;//结束的申赎日期	
	private java.util.Date tradeDate = null;//估值日期
	private String standingBookType = "";//台账类型
	private String portCodes = "";//组合代码
	private Date browDate = null;//浏览日期
	
	//合计值
	private double FSumReturn = 0;//应退合计
	private double FReplaceCash = 0;//替代金额
	private double FWarrantCost = 0;//权证价值（原币）
	private double FBBWarrantCost = 0;//权证价值（本币）
	private double FCanReplaceCash = 0;//可退替代款
	private double FTotalInterest = 0;//派息（原币）
	private double FBBInterest = 0;//派息（本币）
	private double FOMakeUpCost1 = 0;//补票的总成本（原币）
	private double FHMakeUpCost1 = 0;//补票的总成本（本币）
	private double RefundMValue = 0;//可退替代款估值增值
	private double FMakeUpAmount1 = 0;//补票数量--对应页面实际数量
	/**add---huhuichao 2013-8-3 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
	private double FOtherRight = 0;//其他权益（原币）
	private double FBBOtherRight = 0;//其他权益（本币）
	public double getFOtherRight() {
		return FOtherRight;
	}

	public void setFOtherRight(double fOtherRight) {
		FOtherRight = fOtherRight;
	}

	public double getFBBOtherRight() {
		return FBBOtherRight;
	}

	public void setFBBOtherRight(double fBBOtherRight) {
		FBBOtherRight = fBBOtherRight;
	}
	/**end---huhuichao 2013-8-3 STORY  */
	
	/**
	 * 构造函数
	 */
	public ETFReportBook(){
		
	}
	
	public java.util.Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(java.util.Date tradeDate) {
		this.tradeDate = tradeDate;
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
		StringBuffer buffAll = new StringBuffer();
		int diff = YssFun.dateDiff(this.startDate, this.endDate);
		for (int i = 0; i <= diff; i++) {
			Date operDate = YssFun.addDay(this.startDate, i);
			//添加日期头部分
			String value = getTitleValue(operDate,this.standingBookType);
			buffAll.append(value + "\r\n");
			//封装台账数据
			ArrayList bookList = getBookList(operDate,this.browDate,this.portCodes,this.standingBookType);
			//拼接前台前显示数据
			value = doGetBuffAll(bookList,this.standingBookType);
			buffAll.append(value);
		}
		//去除最后"\r\n"
		if (buffAll.toString().length()>2) {
			buffAll = buffAll.delete(buffAll.length()-2, buffAll.length());
		}
		return buffAll.toString();
	}
	
	/**shashijie 2013-5-16 STORY 3713 拼接前台前显示数据*/
	private String doGetBuffAll(ArrayList bookList,String bookType) throws YssException {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < bookList.size(); i++) {
			//判断是汇总项还是明细项
			if (bookList.get(i) instanceof StandingBookBean) {
				//明细拼接数据
				StandingBookBean book = (StandingBookBean)bookList.get(i);
				//获取页面显示数据
				buff = buff.append(
					getShowInfoValue(book.getFexchangecode(),//市场代码
						book.getFexchangename(),//市场名称
						book.getSecurityCode(),//股票代码
						book.getFsecurityname(),//股票名称
						YssFun.formatDate(book.getBuyDate()).equals("9998-12-31") ? "" : 
							YssFun.formatDate(book.getBuyDate())
						,//申购申请日
						book.getMakeUpAmount() + "",//单位篮子成分股数量
						book.getUnitCost() + "",//单位价格(原币)
						//若是赎回溢价比例列不显示
						(bookType.equals("B")?YssFun.formatNumber(
								YssFun.toNumber(book.getFpremiumscale()),"#,##0.00####"):" "),//溢价比例
						book.getExchangeRate() + "",//汇率
						Math.abs(book.getReplaceCash()) + "",//替代金额
						book.getCanReplaceCash() + "",//可退替代款
						book.getMakeUpAmount1() + "",//补票数量--对应页面实际数量
						YssFun.formatDate(book.getExRightDate()).equals("9998-12-31") ? "" : 
							YssFun.formatDate(book.getExRightDate())
						,//权益日期
						book.getRightRate() + "",//权益汇率
						book.getTotalInterest() + "",// 总派息原币
						book.getBBInterest() + "",// 总派息本币
						book.getWarrantCost() + "",// 权证价值原币
						book.getBBWarrantCost() + "",// 权证价值本币
						/**add---huhuichao 2013-8-3 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
						book.getOtherRight() + "",//其他权益原币
						book.getbBOtherRight() + "",//其他权益本币
						/**end---huhuichao 2013-8-3 STORY  4276*/
						YssFun.formatDate(book.getMakeUpDate1()).equals("9998-12-31") ? "" : 
							YssFun.formatDate(book.getMakeUpDate1())
						,//补票日期
						book.getMakeUpUnitCost1() + "",// 补票单位成本(原币)
						book.getFMmakeupunitcost1() + "",//补票单位成本(本币)
						Math.abs(book.getoMakeUpCost1()) + "",// 第一次补票总成本原币
						Math.abs(book.gethMakeUpCost1()) + "",// 第一次补票总成本本币
						book.getRemaindAmount() + "",// 剩余数量
						book.getSumReturn() + "",// 应退合计
						book.getRefundMValue() + "",//可退替代款估值增值
						YssFun.formatDate(book.getRefundDate()).equals("9998-12-31") ? "" : 
							YssFun.formatDate(book.getRefundDate()) // 退款日期
						));
			} else if (bookList.get(i) instanceof HashMap) {
				//汇总拼接数据
				HashMap map = (HashMap)bookList.get(i);
				buff = buff.append(
					getShowInfoValue(" ", " ", " ", " ", " ", " ", " ", " ", " ", 
						(String)map.get("FReplaceCash"),//替代金额 
						(String)map.get("FCanReplaceCash"),//可退替代款, 
						(String)map.get("FMakeUpAmount1"),//补票数量--对应页面实际数量 
						" ",
						" ",
						(String)map.get("FTotalInterest"),//派息（原币）
						(String)map.get("FBBInterest"),//派息（本币）
						(String)map.get("FWarrantCost"),//权证价值（原币）
						(String)map.get("FBBWarrantCost"),//权证价值（本币）
						/**add---huhuichao 2013-8-3 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
						(String)map.get("FOtherRight"),//其他权益（原币）
						(String)map.get("FBBOtherRight"),//其他权益（本币）
						/**add---huhuichao 2013-8-3 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
						" ", " ", " ", 
						(String)map.get("FOMakeUpCost1"),//补票的总成本（原币）
						(String)map.get("FHMakeUpCost1"),//补票的总成本（本币）
						" ", 
						(String)map.get("FSumReturn"),//应退合计
						(String)map.get("RefundMValue"),//可退替代款估值 
						" "
						));
			}
		}
		return buff.toString();
	}

	/**shashijie 2013-5-16 STORY 3713 拼接前台每个单元格的数据*/
	private StringBuffer getShowInfoValue(String s1,
			String s2, String s3, String s4,
			String s5, String s6, String s7, String s8,
			String s9, String s10, String s11, String s12,
			String s13, String s14, String s15,
			String s16, String s17, String s18, String s19,
			String s20, String s21, String s22,
			String s23, String s24, String s25, String s26,
			String s27, String s28, String s29) {
		StringBuffer buff = new StringBuffer();
		buff.append(s1).append("\t");
		buff.append(s2).append("\t");
		buff.append(s3).append("\t");
		buff.append(s4).append("\t");
		buff.append(s5).append("\t");
		buff.append(s6).append("\t");
		buff.append(s7).append("\t");
		buff.append(s8).append("\t");
		buff.append(s9).append("\t");
		buff.append(s10).append("\t");
		buff.append(s11).append("\t");
		buff.append(s12).append("\t");
		buff.append(s13).append("\t");
		buff.append(s14).append("\t");
		buff.append(s15).append("\t");
		buff.append(s16).append("\t");
		buff.append(s17).append("\t");
		buff.append(s18).append("\t");
		buff.append(s19).append("\t");
		buff.append(s20).append("\t");
		buff.append(s21).append("\t");
		buff.append(s22).append("\t");
		buff.append(s23).append("\t");
		buff.append(s24).append("\t");
		buff.append(s25).append("\t");
		buff.append(s26).append("\t");
		buff.append(s27).append("\t");
		buff.append(s28).append("\t");
		buff.append(s29).append("\t");
		
		buff.append("\r\n");
		return buff;
	}

	/**shashijie 2013-5-16 STORY 封装台账对象数据*/
	private ArrayList getBookList(Date operDate, Date endDate,
			String portCodes, String bookType) throws YssException  {
		ArrayList list = new ArrayList();
		ResultSet rs = null;//定义游标
		SearchAccBook accBook = new SearchAccBook();//台账关联表
		accBook.setYssPub(pub);
		//清空合计项目
		setAllToldSum(0,0,0,0,0,0,0,0,0,0,0);
		try {
			String query = getStandingBookBeanQuery(operDate,endDate,portCodes,bookType);
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				//台账实体BEAN
				StandingBookBean book = new StandingBookBean();
				//申购
				if (bookType.equalsIgnoreCase("B")) {
					//赋值对象
					accBook.setStandingBookData2(rs, book, 1);
				} else {//赎回
					accBook.setStandingBookData2(rs, book, -1);
				}
				//计算汇总项与赋值其他关联信息
				setStandingBookData(rs, book);
				list.add(book);
			}
			//获取计算后的汇总值
			HashMap map = getStandingBookData(bookType);
			list.add(map);
		} catch (Exception e) {
			throw new YssException("\r\n",e);
		} finally {
			//清空合计项目
			setAllToldSum(0,0,0,0,0,0,0,0,0,0,0);
			dbl.closeResultSetFinal(rs);//关闭游标
		}
		return list;
	}

	/**shashijie 2013-5-16 STORY 3713 获取SQL
	 * @throws YssException 
	 * @throws SQLException */
	private String getStandingBookBeanQuery(Date operDate, Date endDate,
			String portCodes, String bookType) throws SQLException, YssException {
		String makeupCost = "";
		//申赎补票总成本取法分别对应数据库字段
		if (bookType.equals("B")) {
			makeupCost = " 0 ";
		} else {
			//赎回的必须现金替代取股票蓝总金额
			makeupCost = " Ls.Ftotalmoney ";
		}
		/**add---huhuichao 2013-8-9 STORY  4276  博时：跨境ETF补充增加一类公司行动*/
		Date confimDate = null;
		ResultSet rs = null;
		String sql1 = "select t.fconfimdate from "
				+ pub.yssGetTableName("tb_ta_trade")
				+ " t where t.ftradedate = " + dbl.sqlDate(operDate);
		rs = dbl.openResultSet(sql1);
		while (rs.next()) {
			confimDate = rs.getDate("FConfimDate");
		}
		dbl.closeResultSetFinal(rs);
		/**edit---huhuichao 2013-8-8 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
		/**add---huhuichao 2013-8-3 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
		String sql = " Select Ls.Fsecuritycode ,/*证券代码*/"+//证券代码
			" s.Fsecurityname ,/*证券名称*/"+
			" s.Fexchangecode ,/*市场代码*/"+
			" e.Fexchangename ,/*市场名称*/"+
			" Ls.Fdate As FBuyDate ,/*申购日期*/"+
			" Ls.Famount As FMakeUpAmount, /*篮子数量*/"+
			" Ls.Fpremiumscale , /*溢价比例*/"+
			" b.Funitcost , /*申购日收盘价*/"+
			" b.Fexchangerate ,/*申购汇率*/"+
			" Case"+
			" When Ls.Freplacemark = '5' Then"+
			" b.Freplacecash"+
			" Else"+
			" Ls.Ftotalmoney"+
			" End As FReplaceCash , /*替代总金额*/"+
			" Case"+
	        " When b.Fbs = 'B' And Ls.Freplacemark = '5' " +
	        " Then b.Fcanreplacecash"+
	        " Else 0"+
	        " End As FCanReplaceCash,/*可退替代款*/"+
			" Bs.Fpretaxratio As FRealAmount ,/*送股数量*/"+
			" NVL(Case"+
			" When Df.Fexrightdate Is Null And "+
			" b.Fexrightdate = To_Date('9998-12-31', 'yyyy-MM-dd') Then"+
			" Null"+
			" When Df.Fexrightdate Is Null And"+
			" b.Fexrightdate <> To_Date('9998-12-31', 'yyyy-MM-dd') And "+
			dbl.sqlDate(endDate)+" < b.Fexrightdate Then "+
			" Null"+
			" When b.Fexrightdate <= b.Fmakeupdate1 And "+
			" b.Fmakeupdate1 <> To_Date('9998-12-31', 'yyyy-MM-dd') And "+
			dbl.sqlDate(endDate)+" > b.Fmakeupdate1 Then"+
			" b.Fmakeupdate1"+
			" When b.Fexrightdate <= b.Fmakeupdate1 And"+
			" b.Fmakeupdate1 <> To_Date('9998-12-31', 'yyyy-MM-dd') Then "+
			dbl.sqlDate(endDate)+
			" Else"+
			" b.Fmakeupdate1"+
			" End, To_Date('9998-12-31', 'yyyy-MM-dd')) As FExRightDate,/*权益日期*/"+
			" Case"+
			" When "+dbl.sqlDate(endDate)+" > b.Fmakeupdate1 Then"+
			" R2.Fbaserate"+
			" Else"+
			" r.Fbaserate"+
			" End As FRightRate,/*权益日汇率*/"+
			" (Case"+
			" When "+dbl.sqlDate(endDate)+" >= b.Fmakeupdate1 Then"+
			" Nvl(b.Ftotalinterest, 0)"+
			" Else"+
			" Nvl(Df.Finterest, 0)"+
			" End) As FTotalInterest,/*分红原币*/"+
			" Nvl((Case"+
			" When "+dbl.sqlDate(endDate)+" > b.Fmakeupdate1 Then"+
			" R2.Fbaserate"+
			" Else"+
			" r.Fbaserate"+
			" End) * (Case"+
			" When "+dbl.sqlDate(endDate)+" >= b.Fmakeupdate1 Then"+
			" Nvl(b.Ftotalinterest, 0)"+
			" Else"+
			" Nvl(Df.Finterest, 0)"+
			" End),"+
			" 0) As FBBInterest,/*分红本位币*/"+
			" Case"+
			" When "+dbl.sqlDate(endDate)+" >= b.Fmakeupdate1 Then"+
			" Nvl(b.Fwarrantcost, 0)"+
			" Else"+
			" Nvl(Df.Fwarrantcost, 0)"+
			" End As FWarrantCost,/*权证原币价值*/"+
			" Nvl((Case "+
			" When "+dbl.sqlDate(endDate)+" > b.Fmakeupdate1 Then"+
			" R2.Fbaserate"+
			" Else"+
			" r.Fbaserate"+
			" End) * (Case"+
			" When "+dbl.sqlDate(endDate)+" >= b.Fmakeupdate1 Then"+
			"               Nvl(b.Fwarrantcost, 0)"+
			" Else"+
			"               Nvl(Df.Fwarrantcost, 0)"+
			" End),"+
			" 0) As FBBWarrantCost,/*权证本币价值*/"+
			" Nvl(b.Fmakeupdate1,To_Date('9998-12-31', 'yyyy-MM-dd')) As Fmakeupdate1,/*补票日期*/"+
			" Case"+
			"  When Ls.Freplacemark = '5' Then"+
			"   Case"+
			"  When "+dbl.sqlDate(endDate)+" >= b.Fmakeupdate1 Then"+
			"   b.Fmakeupamount1"+
			"  Else"+
			"   Case "+
			"       When d.fbs = 'B' Then "+
			"         b.Fmakeupamount + Nvl(Df.Fsumamount, 0) -"+
			"         Nvl(Df.Fdeflationamount, 0)"+
			"       Else"+
			"         - (b.Fmakeupamount - Nvl(Df.Fsumamount, 0) + "+
			"         Nvl(Df.Fdeflationamount, 0))"+
			"   End"+
			" End Else 0 End As FMakeUpAmount1,/*补票数量--对应页面实际数量*/"+
			" b.Fmakeupunitcost1 ,/*补票单位原币成本*/"+
			" b.Fmakeupunitcost1 * b.Fexrate1 As FMmakeupunitcost1,/*补票单位本币成本*/"+
			" b.Fexrate1 As FExchangeRate,/*补票汇率*/"+
			" /*由于必须现金替代的数据在台账中的申赎类型为Null所以这里申购取0赎回取股票蓝总金额由程序判断*/ "+
			" Case"+
			" When b.Fbs = 'B' And Ls.Freplacemark = '5' Then"+
			" b.Fomakeupcost1"+
			" When b.Fbs = 'S' And Ls.Freplacemark = '5' Then"+
			" b.Fomakeupcost1"+
			" Else"+
			makeupCost+
			" End As Fomakeupcost1, /*补票总成本原币*/"+
			" Case"+
			" When b.Fbs = 'B' And Ls.Freplacemark = '5' Then"+
			" b.Fhmakeupcost1"+
			" When b.Fbs = 'S' And Ls.Freplacemark = '5' Then"+
			" b.Fhmakeupcost1"+
			" Else"+
			makeupCost+
			" End As Fhmakeupcost1, /*补票总成本本位币*/"+
			" Case"+
			" When b.Fmakeupdate1 <= "+dbl.sqlDate(endDate)+" Then"+
			" 0"+
			" Else"+
			" Nvl(Df.Fremaindamount, b.Fmakeupamount)"+
			" End As FRemaindAmount,/*补票剩余数量*/"+
			" /*区分申赎,申购 = 替代金额 - 第一次补票的总成本（本币）"+
			" 赎回 = 第一次补票的总成本（本币） 或 必须现金替代的总金额 */"+
			" Case"+
			" When b.Fbs = 'B' And Ls.Freplacemark = '6' Then"+
			" 0"+
			" When b.Fbs = 'B' And Ls.Freplacemark = '5' And b.Fhmakeupcost1 = 0 Then"+
			" 0"+
			" When b.Fbs = 'B' And Ls.Freplacemark = '5' And b.Fhmakeupcost1 != 0 And"+
			" b.Fmakeupdate1 > "+dbl.sqlDate(endDate)+" Then"+
			" 0"+
			" When b.Fbs = 'B' And Ls.Freplacemark = '5' And b.Fhmakeupcost1 != 0 And"+
			" b.Fmakeupdate1 <= "+dbl.sqlDate(endDate)+" Then"+
			" b.Freplacecash - b.Fhmakeupcost1"+
			" When b.Fbs = 'S' And Ls.Freplacemark = '5' Then"+
			" b.Fhmakeupcost1"+
			" When b.Fbs = 'S' And Ls.Freplacemark = '6' Then"+
			" Ls.Ftotalmoney"+
			" Else"+
			makeupCost+
			" End As Fsumreturn,/*退补款合计*/"+
			" 0 As Num,"+
			" /*估值增值金额  / 销售数量 * 基准比例*/"+
			" Case"+
			" When b.Fmakeupdate1 > "+dbl.sqlDate(endDate)+" Then"+
			" v.Fvalue / t.Fsellamount *  "+
			" (Select p.Fnormscale"+
			" From "+pub.yssGetTableName("Tb_Etf_Param")+" p"+
			" Where p.Fportcode = "+dbl.sqlString(portCodes)+" ) "+
			" Else"+
			" 0"+
			" End As RefundMValue,/*可退替代款估值增值*/"+
			" Nvl(b.Frefunddate,To_Date('9998-12-31', 'yyyy-MM-dd')) As Frefunddate ,/*退款日期*/ "+
			" b.FNum,"+
			" b.FBs ,"+
			" b.FPortCode, "+
			"  Case When to_date('2012-12-20', 'yyyy-MM-dd') >= b.Fmakeupdate1 Then Nvl(b.Fotherright, 0)  Else "+
			"  Nvl(Df.Fotherright, 0)  End As fotherright, /*其他权益原币*/"+
			"  Nvl((Case When to_date('2012-12-20', 'yyyy-MM-dd') > b.Fmakeupdate1 Then R2.Fbaserate Else  r.Fbaserate"+
			"  End) * (Case When to_date('2012-12-20', 'yyyy-MM-dd') >= b.Fmakeupdate1 Then Nvl(b.fotherright, 0) "+
			"  Else  Nvl(Df.fotherright, 0) End),0) As fbbotherright /*其他权益本币*/"+
//			" Nvl(aftersource.otherright,0) as fotherright,"+
//			" Nvl(aftersource.otherright*(Case When "+dbl.sqlDate(endDate)+" > b.Fmakeupdate1 Then R2.Fbaserate"+
//			" Else r.Fbaserate End),0) as fbbotherright "+

			//一篮子股票表
			" From "+pub.yssGetTableName("Tb_Etf_Stocklist")+" Ls"+
			" Join (Select s1.Fsecurityname,"+
			" s1.Fexchangecode,"+
			" s1.Fsecuritycode,"+
			" s1.Ftradecury From "+pub.yssGetTableName("Tb_Para_Security")+
			" s1) s On Ls.Fsecuritycode = s.Fsecuritycode"+
			" Left Join "+pub.yssGetTableName("Tb_Etf_Standingbook")+" b On Ls.Fdate = b.Fbuydate"+
			" And b.Fsecuritycode = Ls.Fsecuritycode"+
			" /*ETF交易结算*/"+
			" Left Join (Select d1.Fnum,"+
			" d1.Fbuydate,"+
			" d1.Fsecuritycode,"+
			" d1.Fbs"+
			" From "+pub.yssGetTableName("Tb_Etf_Tradestldtl")+" d1 )d On b.Fbuydate = d.Fbuydate"+
			" And b.Fsecuritycode = d.Fsecuritycode"+
			" And d.Fbs = "+dbl.sqlString(bookType)+
			" /*ETF结算关联(权益数据)*/"+
			" Left Join "+pub.yssGetTableName("Tb_Etf_Tradstldtlref")+" Df On (Df.Fnum = d.Fnum And"+
			" Df.Fmakeupdate = To_Date('9998-12-31', 'yyyy-MM-dd') And"+
			" Df.Fexrightdate = "+dbl.sqlDate(confimDate)+" ) "+
			" /*TA交易数据*/"+
			" Join (Select t1.Ftradedate,"+
			" t1.Fportcode,"+
			" t1.Fselltype,"+
			" t1.Fcheckstate,"+
			" t1.Fconfimdate,"+
			" t1.Fsellamount"+
			" From "+pub.yssGetTableName("Tb_Ta_Trade")+" t1"+
			" Where t1.fcheckstate = 1) t On Ls.Fdate = t.Ftradedate"+
			" And t.Fportcode = Ls.Fportcode"+
			" And t.Fselltype = "+(bookType.equals("B")?"'01'":"'02'")+
			" And t.Fcheckstate = 1"+
			" /*可退替代款估值增值*/"+
			" Left Join (Select Sum(v.Fvalue) As Fvalue, v.Fsecuritycode"+
			" From "+pub.yssGetTableName("Tb_Etf_Refundmv")+" v"+
			" Where v.Fbuydate = "+dbl.sqlDate(operDate)+
			" And v.Fbs = "+dbl.sqlString(bookType)+
			" And v.Fsecuritycode != ' '"+
			" And v.Fvaldate >= "+dbl.sqlDate(operDate)+
			" And v.Fvaldate <= "+dbl.sqlDate(confimDate)+
			" Group By v.Fsecuritycode) v On v.Fsecuritycode = b.Fsecuritycode"+
			" /*估值预处理汇率表*/"+
			" Left Join (Select r1.Fbaserate,"+
			" r1.Fvaldate,"+
			" r1.Fcurycode,"+
			" r1.Fportcode"+
			" From "+pub.yssGetTableName("Tb_Data_Pretvalrate")+" r1 ) r On r.Fvaldate = "+
			dbl.sqlDate(confimDate)+
			" And r.Fcurycode = s.Ftradecury"+
			" And r.Fportcode = b.Fportcode"+
			" Left Join (Select r3.Fbaserate,"+
			" r3.Fvaldate,"+
			" r3.Fcurycode,"+
			" r3.Fportcode"+
			" From "+pub.yssGetTableName("Tb_Data_Pretvalrate")+" r3 ) R2 On R2.Fvaldate = b.Fmakeupdate1"+
			" And R2.Fcurycode = s.Ftradecury"+
			" And R2.Fportcode = b.Fportcode"+
			" /*交易所*/"+
			" Left Join (Select e1.FexchangeCode,"+
			" e1.Fexchangename"+
			" From Tb_Base_Exchange e1 "+
			" Where e1.fcheckstate = 1) e On s.Fexchangecode = e.Fexchangecode"+
			" /*送股权益*/"+
			" Left Join (Select Bs.Ftsecuritycode Fsecuritycode,"+
			" Sum(Bs.Fpretaxratio) Fpretaxratio"+
			" From "+pub.yssGetTableName("Tb_Data_Bonusshare")+" Bs"+
			" Where Bs.Fexrightdate > "+dbl.sqlDate(operDate)+
			" And Bs.Fexrightdate <= "+dbl.sqlDate(endDate)+
			" And Bs.FCheckState = 1"+
			" Group By Bs.Ftsecuritycode) Bs On Bs.Fsecuritycode = s.Fsecuritycode"+
//			" left join (select fportcode,/*组合代码*/ ftsecuritycode,/*标的证券*/ fsecuritycode,/*目标证券*/ "+
//			" famount * faftertaxratio * fclosingprice as otherright,/*送股权益产生的原币价值*/ fdate"+
//			" from (select * from (select sl.fportcode, sl.fsecuritycode, sl.famount, sl.fdate from "+
//			pub.yssGetTableName("Tb_Etf_Stocklist")+" sl"+" where fcheckstate = 1) tt /*股票篮*/"+
//			" left join (select bs.ftsecuritycode,bs.faftertaxratio, bs.fssecuritycode,bs.fexrightdate from "+
//			pub.yssGetTableName("Tb_Data_BonusShare")+" bs"+" where bs.fexrightdate = "+dbl.sqlDate(endDate)+
//			" and ftsecuritycode <> fssecuritycode and fcheckstate = 1) t /*送股权益数据*/"+
//			" on tt.fsecuritycode = t.fssecuritycode left join (select mtv.fmtvcode, mtv.flinkcode, mtv.fstartdate from "+
//			pub.yssGetTableName("Tb_Para_MTVMethodLink")+" mtv"+" where mtv.fstartdate <= "+dbl.sqlDate(endDate)+" ) tttt"+
//			" /*估值方法链接设置*/"+
//			" on tttt.flinkcode = tt.fsecuritycode left join (select mtvm.fmtvcode, mtvm.fstartdate, mtvm.fmktsrccode "+
//			" from "+
//			pub.yssGetTableName("Tb_Para_MTVMethod mtvm ")+"  where mtvm.fstartdate <= "+
//			dbl.sqlDate(endDate)+" ) ttttt /*估值方法设置*/"+" on ttttt.fmtvcode = tttt.fmtvcode"+
//			" left join (select mv.fmktsrccode,mv.fsecuritycode as fsecuritycode1,mv.fclosingprice,mv.fmktvaluedate"+
//			" from "+pub.yssGetTableName("Tb_Data_MarketValue")+" mv "+" where mv.fmktvaluedate = " +
//			dbl.sqlDate(endDate)+" and fcheckstate = 1) ttt /*行情信息*/"+"  on ttt.fsecuritycode1 = t.ftsecuritycode"+
//			" and ttt.fmktsrccode = ttttt.fmktsrccode"+" where tt.fdate = "+dbl.sqlDate(endDate)+
//			" and  tt.Fportcode = "+dbl.sqlString(portCodes)+" ))aftersource"+
//			" on Ls.Fsecuritycode = aftersource.fsecuritycode"+
			" Where Ls.Fdate = "+dbl.sqlDate(operDate)+
			" And "+dbl.sqlDate(operDate)+" < " +dbl.sqlDate(endDate)+
		    " And "+dbl.sqlDate(endDate)+" >= t.Fconfimdate" +
    		" And (b.Fbs = "+dbl.sqlString(bookType)+" Or b.Fbs Is Null)" +
			" And Ls.Fportcode = "+dbl.sqlString(portCodes)+
			" Order By b.Fsecuritycode";
		/**end---huhuichao 2013-8-3 STORY  4276 */
		/**end---huhuichao 2013-8-8 STORY  4276 */
		/**end---huhuichao 2013-8-9 STORY  4276*/
		return sql;
	}

	/**shashijie 2013-5-16 STORY 3713 获取汇总值并封装成HashMap*/
	private HashMap getStandingBookData(String bookType) {
		HashMap map = new HashMap();
		//默认申购
		int num = 1;
		//赎回
		/*if (bookType.equalsIgnoreCase("S")) {
			num = -1;
		}*/
		map.put("FSumReturn", getDoubleToString(FSumReturn,num));//应退合计
		map.put("FReplaceCash",getDoubleToString(FReplaceCash ,num));//替代金额
		map.put("FWarrantCost", getDoubleToString(FWarrantCost , num));// 权证价值（原币）
		map.put("FBBWarrantCost", getDoubleToString(FBBWarrantCost , num));// 权证价值（本币）
		map.put("FCanReplaceCash", getDoubleToString(FCanReplaceCash , num));// 可退替代款
		map.put("FTotalInterest", getDoubleToString(FTotalInterest , num));// 派息（原币）
		map.put("FBBInterest", getDoubleToString(FBBInterest , num));// 派息（本币）
		map.put("FOMakeUpCost1", getDoubleToString(FOMakeUpCost1 ,num));// 补票的总成本（原币）
		map.put("FHMakeUpCost1", getDoubleToString(FHMakeUpCost1 ,num));// 补票的总成本（本币）
		map.put("RefundMValue", getDoubleToString(RefundMValue , num));//可退替代款估值增值
		map.put("FMakeUpAmount1",getDoubleToString(FMakeUpAmount1 , num));//补票数量--对应页面实际数量
		/**add---huhuichao 2013-8-3 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
		map.put("FOtherRight", getDoubleToString(FOtherRight , num));// 其他权益（原币）
		map.put("FBBOtherRight", getDoubleToString(FBBOtherRight , num));// 其他权益（本币）
		/**end---huhuichao 2013-8-3 STORY  4276*/
		return map;
	}

	/**shashijie 2013-5-17 STORY 3713 两数相乘取其积,并转换成String*/
	private String getDoubleToString(double value, double num) {
		double ret = YssD.mul(value, num);
		return ret + "";
	}

	/**shashijie 2013-5-16 STORY 3713 计算汇总项与赋值其他关联信息*/
	private void setStandingBookData(ResultSet rs, StandingBookBean book) throws Exception {
		//判断结果集是否存在某字段
		if (dbl.isFieldExist(rs, "Fsecurityname")) {
			book.setFsecurityname(rs.getString("Fsecurityname"));//证券名称
		}
		if (dbl.isFieldExist(rs, "Fexchangecode")) {
			book.setFexchangecode(rs.getString("Fexchangecode"));//市场代码
		}
		if (dbl.isFieldExist(rs, "Fexchangename")) {
			book.setFexchangename(rs.getString("Fexchangename"));//市场名称
		}
		if (dbl.isFieldExist(rs, "Fpremiumscale")) {
			book.setFpremiumscale(rs.getString("Fpremiumscale"));//溢价比例
		}
		if (dbl.isFieldExist(rs, "FMmakeupunitcost1")) {
			book.setFMmakeupunitcost1(rs.getDouble("FMmakeupunitcost1"));//补票单位成本本币
		}
		if (dbl.isFieldExist(rs, "RefundMValue")) {//可退替代款估值增值
			book.setRefundMValue(rs.getDouble("RefundMValue"));
		}
		/**add---huhuichao 2013-8-1 BUG  8926 ETF申赎台账中，当“实际补券总成本”大于“替代金额”时，“退款合计”项的金额不正确*/
		FSumReturn += rs.getDouble("FSumReturn");//应退合计
		/**end---huhuichao 2013-8-1 BUG  8926*/
		FReplaceCash += Math.abs(rs.getDouble("FReplaceCash"));//替代金额
		FWarrantCost += Math.abs(rs.getDouble("FWarrantCost"));//权证价值（原币）
		FBBWarrantCost += Math.abs(rs.getDouble("FBBWarrantCost"));//权证价值（本币）
		FCanReplaceCash += Math.abs(rs.getDouble("FCanReplaceCash"));//可退替代款
		FTotalInterest += Math.abs(rs.getDouble("FTotalInterest"));//派息（原币）
		FBBInterest += Math.abs(rs.getDouble("FBBInterest"));//派息（本币）
		/**add---huhuichao 2013-8-3 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
		FOtherRight += Math.abs(rs.getDouble("FOtherRight"));//其他权证（原币）
		FBBOtherRight += Math.abs(rs.getDouble("FBBOtherRight"));//其他权证（本位币）
		/**end---huhuichao 2013-8-3 STORY  */
		FOMakeUpCost1 += Math.abs(rs.getDouble("FOMakeUpCost1"));//补票的总成本（原币）
		FHMakeUpCost1 += Math.abs(rs.getDouble("FHMakeUpCost1"));//补票的总成本（本币）
		RefundMValue += Math.abs(rs.getDouble("RefundMValue"));//可退替代款估值增值
		FMakeUpAmount1 += Math.abs(rs.getDouble("FMakeUpAmount1"));//补票数量--对应页面实际数量
		
		
	}

	/**shashijie 2013-5-16 STORY 3713 清空合计项目*/
	private void setAllToldSum(double FSumReturn, double FReplaceCash, double FWarrantCost, double FBBWarrantCost, 
			double FCanReplaceCash, double FTotalInterest, double FBBInterest, double FOMakeUpCost1, 
			double FHMakeUpCost1,double RefundMValue,double FMakeUpAmount1) {
		this.FSumReturn = FSumReturn;//应退合计
		this.FReplaceCash = FReplaceCash;//替代金额
		this.FWarrantCost = FWarrantCost;//权证价值（原币）
		this.FBBWarrantCost = FBBWarrantCost;//权证价值（本币）
		this.FCanReplaceCash = FCanReplaceCash;//可退替代款
		this.FTotalInterest = FTotalInterest;//派息（原币）
		this.FBBInterest = FBBInterest;//派息（本币）
		this.FOMakeUpCost1 = FOMakeUpCost1;//补票的总成本（原币）
		this.FHMakeUpCost1 = FHMakeUpCost1;//补票的总成本（本币）
		this.RefundMValue = RefundMValue;//可退替代款估值增值
		this.FMakeUpAmount1 = FMakeUpAmount1;//补票数量--对应页面实际数量
	}

	/**shashijie 2013-5-15 STORY 3713 获取表头部分 */
	private String getTitleValue(Date operDate, String bookType) {
		String value = "";
		if (bookType.trim().equalsIgnoreCase("B")) {
			value = "申购日期\t";
		} else {
			value = "赎回日期\t";
		}
		value += YssFun.formatDate(operDate);
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
			this.endDate = YssFun.toDate(reqAry[1]);// 结束的申赎日期
			this.browDate = YssFun.toDate(reqAry[2]);// 浏览日期
			this.portCodes = reqAry[3]; // 已选组合代码
			this.standingBookType = reqAry[4];
			
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

	public java.util.Date getEndDate() {
		return endDate;
	}

	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
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

	public Date getBrowDate() {
		return browDate;
	}

	public void setBrowDate(Date browDate) {
		this.browDate = browDate;
	}
	
}
