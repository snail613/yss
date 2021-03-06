package com.yss.main.operdeal.report.moneycontrol.pojo;

import java.util.Date;

/***
 * 
 * 人民币头寸表 pojo类
 * @author yanghaiming
 *20100810 MS01461 QDV4华安2010年7月20日01_A
 */
public class CNYMoneyControlBean implements Cloneable{
	private Date fdate;//生成报表的日期
	private int reporttype;//报表类型（0表示预估，1表示确认）
	private String fportcode;//组合代码
	private int fNum;//款项编号
	private String fNumName;//款项名称
	private String fmoney0 = "0";//T-1日金额，由于数据中会存在百分比，在此定义为String类型
	private String fmoney1 = "0";//T日金额
	private String fmoney2 = "0";//T+1日金额
	private String fmoney3 = "0";//T+2日金额
	private String fmoney4 = "0";//T+3日金额
	private String fmoney5 = "0";//T+4日金额
	private String fmoney6 = "0";//T+5日金额
	private String fmoney7 = "0";//T+6日金额
	private String fmoney8 = "0";//T+7日金额
	private String fmoney9 = "0";//T+8日金额
	private String fmoney10 = "0";//T+9日金额
	private String fmoney11 = "0";//T+10日金额
	//----add by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整---start---//
	private String fmoney12 = "0";//T+11日金额，由于数据中会存在百分比，在此定义为String类型
	private String fmoney13 = "0";//T+12日金额
	private String fmoney14 = "0";//T+13日金额
	private String fmoney15 = "0";//T+14日金额
	private String fmoney16= "0";//T+15日金额
	private String fmoney17= "0";//T+16日金额
	private String fmoney18= "0";//T+17日金额
	private String fmoney19= "0";//T+18日金额
	private String fmoney20= "0";//T+19日金额
	private String fmoney21= "0";//T+20日金额
	//----add by  zhaoxianlin 20130118 STORY #3487 人民币头寸管控表调整---end---//
	
	
	
	
	public Date getFdate() {
		return fdate;
	}
	public String getFmoney12() {
		return fmoney12;
	}
	public void setFmoney12(String fmoney12) {
		this.fmoney12 = fmoney12;
	}
	public String getFmoney13() {
		return fmoney13;
	}
	public void setFmoney13(String fmoney13) {
		this.fmoney13 = fmoney13;
	}
	public String getFmoney14() {
		return fmoney14;
	}
	public void setFmoney14(String fmoney14) {
		this.fmoney14 = fmoney14;
	}
	public String getFmoney15() {
		return fmoney15;
	}
	public void setFmoney15(String fmoney15) {
		this.fmoney15 = fmoney15;
	}
	public String getFmoney16() {
		return fmoney16;
	}
	public void setFmoney16(String fmoney16) {
		this.fmoney16 = fmoney16;
	}
	public String getFmoney17() {
		return fmoney17;
	}
	public void setFmoney17(String fmoney17) {
		this.fmoney17 = fmoney17;
	}
	public String getFmoney18() {
		return fmoney18;
	}
	public void setFmoney18(String fmoney18) {
		this.fmoney18 = fmoney18;
	}
	public String getFmoney19() {
		return fmoney19;
	}
	public void setFmoney19(String fmoney19) {
		this.fmoney19 = fmoney19;
	}
	public String getFmoney20() {
		return fmoney20;
	}
	public void setFmoney20(String fmoney20) {
		this.fmoney20 = fmoney20;
	}
	public String getFmoney21() {
		return fmoney21;
	}
	public void setFmoney21(String fmoney21) {
		this.fmoney21 = fmoney21;
	}
	public void setFdate(Date fdate) {
		this.fdate = fdate;
	}
	public int getReporttype() {
		return reporttype;
	}
	public void setReporttype(int reporttype) {
		this.reporttype = reporttype;
	}
	public String getFportcode() {
		return fportcode;
	}
	public void setFportcode(String fportcode) {
		this.fportcode = fportcode;
	}
	public int getfNum() {
		return fNum;
	}
	public void setfNum(int fNum) {
		this.fNum = fNum;
	}
	public String getfNumName() {
		return fNumName;
	}
	public void setfNumName(String fNumName) {
		this.fNumName = fNumName;
	}
	public String getFmoney0() {
		return fmoney0;
	}
	public void setFmoney0(String fmoney0) {
		this.fmoney0 = fmoney0;
	}
	public String getFmoney1() {
		return fmoney1;
	}
	public void setFmoney1(String fmoney1) {
		this.fmoney1 = fmoney1;
	}
	public String getFmoney2() {
		return fmoney2;
	}
	public void setFmoney2(String fmoney2) {
		this.fmoney2 = fmoney2;
	}
	public String getFmoney3() {
		return fmoney3;
	}
	public void setFmoney3(String fmoney3) {
		this.fmoney3 = fmoney3;
	}
	public String getFmoney4() {
		return fmoney4;
	}
	public void setFmoney4(String fmoney4) {
		this.fmoney4 = fmoney4;
	}
	public String getFmoney5() {
		return fmoney5;
	}
	public void setFmoney5(String fmoney5) {
		this.fmoney5 = fmoney5;
	}
	public String getFmoney6() {
		return fmoney6;
	}
	public void setFmoney6(String fmoney6) {
		this.fmoney6 = fmoney6;
	}
	public String getFmoney7() {
		return fmoney7;
	}
	public void setFmoney7(String fmoney7) {
		this.fmoney7 = fmoney7;
	}
	public String getFmoney8() {
		return fmoney8;
	}
	public void setFmoney8(String fmoney8) {
		this.fmoney8 = fmoney8;
	}
	public String getFmoney9() {
		return fmoney9;
	}
	public void setFmoney9(String fmoney9) {
		this.fmoney9 = fmoney9;
	}
	public String getFmoney10() {
		return fmoney10;
	}
	public void setFmoney10(String fmoney10) {
		this.fmoney10 = fmoney10;
	}
	public String getFmoney11() {
		return fmoney11;
	}
	public void setFmoney11(String fmoney11) {
		this.fmoney11 = fmoney11;
	}
	
	public Object clone(){
		CNYMoneyControlBean o = null;
        try{
            o = (CNYMoneyControlBean)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return o;
	}
}
