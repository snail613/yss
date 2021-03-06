package com.yss.main.platform.pfoper.commondata.pojo;

import com.yss.util.YssException;
import com.yss.util.YssFun;

public class CommonExchangeInOutBean {
	
	private String portCode = "";             //组合代码       
    private java.util.Date tradeDate = null;  //交易日期   
    private java.util.Date settleDate = null; //结算日期    
    private String type = "";                 //类型
    private String curyCode = "";             //币种代码
    private double money = 0;                 //金额
    
    private String oldPortCode = "";              
    private java.util.Date oldTradeDate = null; 
    private java.util.Date oldSettleDate = null; 
    private String oldType = "";                
    private String oldCuryCode = "";
    
    private CommonExchangeInOutBean filterType = null;

    public CommonExchangeInOutBean getFilterType() {
		return filterType;
	}

	public void setFilterType(CommonExchangeInOutBean filterType) {
		this.filterType = filterType;
	}

	public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") > 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (filterType == null) {
                        filterType = new CommonExchangeInOutBean();
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.portCode = reqAry[0];
            if (YssFun.isDate(reqAry[1])) {
                this.tradeDate = YssFun.toDate(reqAry[1]);
            }
            if (YssFun.isDate(reqAry[2])) {
                this.settleDate = YssFun.toDate(reqAry[2]);
            }
            this.type = reqAry[3];
            this.curyCode = reqAry[4];
            if (YssFun.isNumeric(reqAry[5])) {
                this.money = YssFun.toDouble(reqAry[5]);
            }
            
            this.oldPortCode = reqAry[6];
            if (YssFun.isDate(reqAry[7])) {
                this.oldTradeDate = YssFun.toDate(reqAry[7]);
            }
            if (YssFun.isDate(reqAry[8])) {
                this.oldSettleDate = YssFun.toDate(reqAry[8]);
            }
            this.oldType = reqAry[9];
            this.oldCuryCode = reqAry[10];
            
        } catch (Exception e) {
            throw new YssException("解析数据出错", e);
        }
    }
    
	public String getPortCode() {
		return portCode;
	}
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}
	public java.util.Date getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(java.util.Date tradeDate) {
		this.tradeDate = tradeDate;
	}
	public java.util.Date getSettleDate() {
		return settleDate;
	}
	public void setSettleDate(java.util.Date settleDate) {
		this.settleDate = settleDate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCuryCode() {
		return curyCode;
	}
	public void setCuryCode(String curyCode) {
		this.curyCode = curyCode;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	public String getOldPortCode() {
		return oldPortCode;
	}
	public void setOldPortCode(String oldPortCode) {
		this.oldPortCode = oldPortCode;
	}
	public java.util.Date getOldTradeDate() {
		return oldTradeDate;
	}
	public void setOldTradeDate(java.util.Date oldTradeDate) {
		this.oldTradeDate = oldTradeDate;
	}
	public java.util.Date getOldSettleDate() {
		return oldSettleDate;
	}
	public void setOldSettleDate(java.util.Date oldSettleDate) {
		this.oldSettleDate = oldSettleDate;
	}
	public String getOldType() {
		return oldType;
	}
	public void setOldType(String oldType) {
		this.oldType = oldType;
	}
	public String getOldCuryCode() {
		return oldCuryCode;
	}
	public void setOldCuryCode(String oldCuryCode) {
		this.oldCuryCode = oldCuryCode;
	}      
}
