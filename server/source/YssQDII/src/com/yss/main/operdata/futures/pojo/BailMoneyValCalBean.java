package com.yss.main.operdata.futures.pojo;

import com.yss.dsub.*;

/**
 * <p>Title:xuqiji 20090810 QDV4招商证券2009年07月06日01_A  MS00562 期权和期货结算估值的保证金账户需要独立的界面让用户指定 </p>
 *
 * <p>Description: 保证金账户设置实体类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BailMoneyValCalBean extends BaseDataSettingBean{
    private String sCashAccCode="";//账户代码
    private String sCashAccName="";//账户名称
    private String sPortCode="";//组合代码
    private String sPortName="";//组合名称
	private String sExchageCode="";//交易所代码
    private String sExchageName="";//交易所名称
    private String sMarkType="";//标志类型
    private String sDesc="";//描述
    private String strIsOnlyColumns = "0"; //在初始登陆时是否只显示列，不查询数据
    private String sOldCashAccCode="";//账户代码
    private String sOldPortCode="";//组合代码
    private String sOldMarkType="";//标志类型
    private String sOldExchageCode="";//#1279 使用当前版本设置【期权期货保证金账户设置】时错误  add by jiangshichao 2011.03.22
    
  //add by huangqirong 2012-12-07 story #3371 
    private String sStartCashAccCode = "";  //初始保证金账户
    private String sStartCashAccName = "";
    private String sBrokeCode = ""; //券商
    private String sBrokeName = "";

    private String sOldStartCashAccCode = "";
    private String sOldBrokeCode = "";
    
    public String getStartCashAccCode (){
    	return this.sStartCashAccCode;
    }
    
    public void setStartCashAccCode(String startCashAccCode){
    	this.sStartCashAccCode = startCashAccCode;
    }
    
    
    public String getStartCashAccName(){
    	return this.sStartCashAccName;
    }
    
    public void setStartCashAccName(String startCashAccName){
    	this.sStartCashAccName = startCashAccName;
    }
    
    
    public String getBrokeCode(){
    	return this.sBrokeCode;
    }
    
    public void setBrokeCode(String brokeCode){
    	this.sBrokeCode = brokeCode;
    }
    
    
    public String getBrokeName(){
    	return this.sBrokeName;
    }
    
    public void setBrokeName(String brokeName){
    	this.sBrokeName = brokeName;
    }
    
    
    public String getOldStartCashAccCode(){
    	return this.sOldStartCashAccCode;
    }
    
    public void setOldStartCashAccCode(String oldStartCashAccCode){
    	this.sOldStartCashAccCode = oldStartCashAccCode;
    }
    
    public String getOldBrokeCode(){
    	return this.sOldBrokeCode;
    }
    
    public void setOldBrokeCode(String oldBrokeCode){
    	this.sOldBrokeCode = oldBrokeCode;
    }
    //---end---
    public BailMoneyValCalBean() {
    }
    public String getSCashAccCode() {
        return sCashAccCode;
    }

    public String getSCashAccName() {
        return sCashAccName;
    }

    public String getSDesc() {
        return sDesc;
    }

    public String getSExchageCode() {
        return sExchageCode;
    }

    public String getSExchageName() {
        return sExchageName;
    }

    public String getSMarkType() {
        return sMarkType;
    }

    public String getSOldCashAccCode() {
        return sOldCashAccCode;
    }

    public String getSOldMarkType() {
        return sOldMarkType;
    }

    public String getSOldPortCode() {
        return sOldPortCode;
    }

    public String getSPortCode() {
        return sPortCode;
    }

    public String getSPortName() {
        return sPortName;
    }

    public void setSCashAccCode(String sCashAccCode) {
        this.sCashAccCode = sCashAccCode;
    }

    public void setSCashAccName(String sCashAccName) {
        this.sCashAccName = sCashAccName;
    }

    public void setSDesc(String sDesc) {
        this.sDesc = sDesc;
    }

    public void setSExchageCode(String sExchageCode) {
        this.sExchageCode = sExchageCode;
    }

    public void setSExchageName(String sExchageName) {
        this.sExchageName = sExchageName;
    }

    public void setSMarkType(String sMarkType) {
        this.sMarkType = sMarkType;
    }

    public void setSOldCashAccCode(String sOldCashAccCode) {
        this.sOldCashAccCode = sOldCashAccCode;
    }

    public void setSOldMarkType(String sOldMarkType) {
        this.sOldMarkType = sOldMarkType;
    }

    public void setSOldPortCode(String sOldPortCode) {
        this.sOldPortCode = sOldPortCode;
    }

    public void setSPortCode(String sPortCode) {
        this.sPortCode = sPortCode;
    }

    public void setSPortName(String sPortName) {
        this.sPortName = sPortName;
    }
	public String getStrIsOnlyColumns() {
		return strIsOnlyColumns;
	}
	public void setStrIsOnlyColumns(String strIsOnlyColumns) {
		this.strIsOnlyColumns = strIsOnlyColumns;
	}
	//#1279 使用当前版本设置【期权期货保证金账户设置】时错误  add by jiangshichao 2011.03.22
    public String getsOldExchageCode() {
		return sOldExchageCode;
	}
	public void setsOldExchageCode(String sOldExchageCode) {
		this.sOldExchageCode = sOldExchageCode;
	}
	//#1279 使用当前版本设置【期权期货保证金账户设置】时错误  end ------------------------
}
