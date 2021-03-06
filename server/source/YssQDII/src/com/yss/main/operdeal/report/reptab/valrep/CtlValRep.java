package com.yss.main.operdeal.report.reptab.valrep;

import com.yss.base.*;
import com.yss.main.dayfinish.OffAcctBean;
import com.yss.util.*;

public class CtlValRep
    extends BaseAPOperValue {
    public CtlValRep() {
    }

    private BaseValRep val = null;
    private java.util.Date dBeginDate = null;
    private java.util.Date dEndDate = null;
    private String sPortCode = "";
    private String sMake = "";

	public void init(Object bean) throws YssException {
        String reqAry[] = null;
        String reqAry1[] = null;
        String sRowStr = (String) bean;
        if (sRowStr.trim().length() == 0) {
            return;
        }
        reqAry = sRowStr.split("\n");
        reqAry1 = reqAry[0].split("\r");
        this.dBeginDate = YssFun.toDate(reqAry1[1]);
        reqAry1 = reqAry[1].split("\r");
        this.dEndDate = YssFun.toDate(reqAry1[1]);
        reqAry1 = reqAry[2].split("\r");
        this.sPortCode = reqAry1[1];
        reqAry1 = reqAry[3].split("\r");
        this.sMake = reqAry1[1];
        val = new BaseValRep();
        val.setYssPub(pub);
        if(!"0".equals(sMake)){
        	val.set(sPortCode, dBeginDate, dEndDate);
        }
    }

	public Object invokeOperMothed() throws YssException {
    	if("0".equals(sMake))return null;
    	
  	    //===============增加封账状态的判断，如已封账，返回封账信息 edit by qiuxufeng 20101108 QDV4太平2010年09月16日03_A
  	  	OffAcctBean offAcct = new OffAcctBean();
		offAcct.setYssPub(this.pub);
  	  	String tmpDate = YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "~n~" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd");
  	  	String tmpInfo = offAcct.getOffAcctInfo(tmpDate, this.sPortCode);
  	  	if(!tmpInfo.trim().equalsIgnoreCase("")) {
  	  		return "<OFFACCT>" + tmpInfo;
  	  	}
  	  	//=================end=================
    	
        invokeOper("cashsaving"); //现金:　活期存款
        invokeOper("fixdepositcash"); //现金： 定期存款
        invokeOper("stockval"); //证券: 股票/基金_估值
        invokeOper("accintval");//证券：股票分红
        invokeOper("bondbill"); //证券：债券票据
        invokeOper("managerfee"); //管理费 新添加 by leeyu 20090708 MS005439:QDV4中保2009年06月25日01_A
        return null;
    }

    private void invokeOper(String beanid) throws YssException {
        BaseValRep val = (BaseValRep) pub.getOperDealCtx().getBean(beanid);
        val.setYssPub(pub);
        val.init(this.val);
        val.insert(val.getValRepData());
        val.afterValRepData();
    }
}
