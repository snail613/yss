package com.yss.pojo.sys;

import com.yss.dsub.YssPub;
/**
 * 系统查询大批量数据时采用分页处理模式，此类为POJO类
 * QDV4赢时胜上海2009年12月21日06_B MS00884
 * @author leeyu
 *
 */
public class YssPageInationBean {
	public YssPageInationBean(){		
	}
	
	//private int iPageCount=100;			//每页固定行数 xuqiji 20100319 QDV4赢时胜上海2009年12月21日06_B MS00884
	private int iAuditPageCount=-1;				//总页数  已审核
	private int iUnAuditPageCount=-1;			//总页数  未审核
	private int iRecycledPageCount=-1;			//总页数  回收站
	private int iAuditCurrPage=1;				//当前页  已审核
	private int iUnAuditCurrPage=1;				//当前页  未审核
	private int iRecycledCurrPage=1;			//当前页  回收站
	private boolean bCreateView=true; 			//创建视图，true－创建；false－不创建  默认创建
	private String sTableName="";				//表名（关键字段）
	private String sQuerySQL="";				//原查询的SQL语句
	
	private YssPub pub =null;
	
	/**shashijie 2011.03.18 TASK #3331::在交易结算和TA交易结算页面增加分页显示的功能*/
	private int iSettlePageCount = -1; //已结算 总页数
	private int iSettleCurrPage = 1;   //已结算 当前页
	
	private int iSettleNoPageCount = -1;//未结算 总页数
	private int iSettleNoCurrPage = 1;//		当前页
	
	private int iSettleDelayPageCount = -1;//延迟 总页数
	private int iSettleDelayCurrPage = 1;//		   当前页
	
	private int iSettleBackPageCount = -1;//回转 总页数
	private int iSettleBackCurrPage = 1;//		当前页
	/**~~~~~~~~~~~~~~~~~~~end~~~~~~~~~~~~~~~~~~*/
	
	public int getiSettlePageCount() {
		return iSettlePageCount;
	}
	public void setiSettlePageCount(int iSettlePageCount) {
		this.iSettlePageCount = iSettlePageCount;
	}
	public int getiSettleCurrPage() {
		return iSettleCurrPage;
	}
	public void setiSettleCurrPage(int iSettleCurrPage) {
		this.iSettleCurrPage = iSettleCurrPage;
	}
	public int getiSettleNoPageCount() {
		return iSettleNoPageCount;
	}
	public void setiSettleNoPageCount(int iSettleNoPageCount) {
		this.iSettleNoPageCount = iSettleNoPageCount;
	}
	public int getiSettleNoCurrPage() {
		return iSettleNoCurrPage;
	}
	public void setiSettleNoCurrPage(int iSettleNoCurrPage) {
		this.iSettleNoCurrPage = iSettleNoCurrPage;
	}
	public int getiSettleDelayPageCount() {
		return iSettleDelayPageCount;
	}
	public void setiSettleDelayPageCount(int iSettleDelayPageCount) {
		this.iSettleDelayPageCount = iSettleDelayPageCount;
	}
	public int getiSettleDelayCurrPage() {
		return iSettleDelayCurrPage;
	}
	public void setiSettleDelayCurrPage(int iSettleDelayCurrPage) {
		this.iSettleDelayCurrPage = iSettleDelayCurrPage;
	}
	public int getiSettleBackPageCount() {
		return iSettleBackPageCount;
	}
	public void setiSettleBackPageCount(int iSettleBackPageCount) {
		this.iSettleBackPageCount = iSettleBackPageCount;
	}
	public int getiSettleBackCurrPage() {
		return iSettleBackCurrPage;
	}
	public void setiSettleBackCurrPage(int iSettleBackCurrPage) {
		this.iSettleBackCurrPage = iSettleBackCurrPage;
	}
	public int getPageCount(){
		return pub.getIPageCount();//xuqiji 20100319 QDV4赢时胜上海2009年12月21日06_B MS00884
	}
	public int getiAuditPageCount() {
		return iAuditPageCount;
	}
	public void setiAuditPageCount(int iAuditPageCount) {
		this.iAuditPageCount = iAuditPageCount;
	}
	public int getiUnAuditPageCount() {
		return iUnAuditPageCount;
	}
	public void setiUnAuditPageCount(int iUnAuditPageCount) {
		this.iUnAuditPageCount = iUnAuditPageCount;
	}
	public int getiRecycledPageCount() {
		return iRecycledPageCount;
	}
	public void setiRecycledPageCount(int iRecycledPageCount) {
		this.iRecycledPageCount = iRecycledPageCount;
	}
	public int getiAuditCurrPage() {
		return iAuditCurrPage;
	}
	public void setiAuditCurrPage(int iAuditCurrPage) {
		this.iAuditCurrPage = iAuditCurrPage;
	}
	public int getiUnAuditCurrPage() {
		return iUnAuditCurrPage;
	}
	public void setiUnAuditCurrPage(int iUnAuditCurrPage) {
		this.iUnAuditCurrPage = iUnAuditCurrPage;
	}
	public int getiRecycledCurrPage() {
		return iRecycledCurrPage;
	}
	public void setiRecycledCurrPage(int iRecycledCurrPage) {
		this.iRecycledCurrPage = iRecycledCurrPage;
	}
	public boolean isbCreateView() {
		return bCreateView;
	}
	public void setbCreateView(boolean bCreateView) {
		this.bCreateView = bCreateView;
	}
	public String getsTableName() {
		return sTableName;
	}
	public void setsTableName(String sTableName) {
		this.sTableName = sTableName;
	}
	public String getsQuerySQL() {
		return sQuerySQL;
	}
	public void setsQuerySQL(String sQuerySQL) {
		this.sQuerySQL = sQuerySQL;
	}
	public YssPub getYssPub() {
		return pub;
	}
	public void setYssPub(YssPub pub) {
		this.pub = pub;
	}
	
	public String buildRowStr(){
		StringBuffer buf=new StringBuffer();
		buf.append("PageInation");
		buf.append(iAuditPageCount).append("\t");
		buf.append(iAuditCurrPage).append("\t");
		
		buf.append(iUnAuditPageCount).append("\t");
		buf.append(iUnAuditCurrPage).append("\t");
		
		buf.append(iRecycledPageCount).append("\t");
		buf.append(iRecycledCurrPage).append("\t");
		
		buf.append(iSettlePageCount).append("\t");
		buf.append(iSettleCurrPage).append("\t");
		
		buf.append(iSettleNoPageCount).append("\t");
		buf.append(iSettleNoCurrPage).append("\t");
		
		buf.append(iSettleDelayPageCount).append("\t");
		buf.append(iSettleDelayCurrPage).append("\t");
		
		buf.append(iSettleBackPageCount).append("\t");
		buf.append(iSettleBackCurrPage).append("\t");
		
		return buf.toString();
	}
}
