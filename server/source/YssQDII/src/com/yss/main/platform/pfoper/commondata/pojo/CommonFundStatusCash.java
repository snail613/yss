package com.yss.main.platform.pfoper.commondata.pojo;

import com.yss.util.YssException;
import com.yss.util.YssFun;

public class CommonFundStatusCash {

	private String fundCode = "";        //基金代码       
    private java.util.Date date = null;  //日期       
    private String cashCode = "";        //现金账户代码
    private String subtsftypeCode = "";  //调拨子类型
    private double marketValue = 0;      //市值
    private String assetType = "";       //资产类别
    
    private String oldFundCode = "";              
    private java.util.Date oldDate = null; 
    private String oldCashCode = ""; 
    private String oldSubtsftypeCode = "";                
    private String oldAssetType = "";
    
    private CommonFundStatusCash filterType = null;

	public String getFundCode() {
		return fundCode;
	}

	public void setFundCode(String fundCode) {
		this.fundCode = fundCode;
	}

	public java.util.Date getDate() {
		return date;
	}

	public void setDate(java.util.Date date) {
		this.date = date;
	}

	public String getCashCode() {
		return cashCode;
	}

	public void setCashCode(String cashCode) {
		this.cashCode = cashCode;
	}

	public String getSubtsftypeCode() {
		return subtsftypeCode;
	}

	public void setSubtsftypeCode(String subtsftypeCode) {
		this.subtsftypeCode = subtsftypeCode;
	}

	public double getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(double marketValue) {
		this.marketValue = marketValue;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getOldFundCode() {
		return oldFundCode;
	}

	public void setOldFundCode(String oldFundCode) {
		this.oldFundCode = oldFundCode;
	}

	public java.util.Date getOldDate() {
		return oldDate;
	}

	public void setOldDate(java.util.Date oldDate) {
		this.oldDate = oldDate;
	}

	public String getOldCashCode() {
		return oldCashCode;
	}

	public void setOldCashCode(String oldCashCode) {
		this.oldCashCode = oldCashCode;
	}

	public String getOldSubtsftypeCode() {
		return oldSubtsftypeCode;
	}

	public void setOldSubtsftypeCode(String oldSubtsftypeCode) {
		this.oldSubtsftypeCode = oldSubtsftypeCode;
	}

	public String getOldAssetType() {
		return oldAssetType;
	}

	public void setOldAssetType(String oldAssetType) {
		this.oldAssetType = oldAssetType;
	}

	public CommonFundStatusCash getFilterType() {
		return filterType;
	}

	public void setFilterType(CommonFundStatusCash filterType) {
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
                        filterType = new CommonFundStatusCash();
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
            if (YssFun.isDate(reqAry[0])) {
                this.date = YssFun.toDate(reqAry[0]);
            }
            this.fundCode = reqAry[1];
            this.cashCode = reqAry[2];         
            this.subtsftypeCode = reqAry[3];
            if (YssFun.isNumeric(reqAry[4])) {
                this.marketValue = YssFun.toDouble(reqAry[4]);
            }
            this.assetType = reqAry[5];           
           
            if (YssFun.isDate(reqAry[6])) {
                this.oldDate = YssFun.toDate(reqAry[6]);
            }
            this.oldFundCode = reqAry[7];
            this.oldCashCode = reqAry[8];
            this.oldSubtsftypeCode = reqAry[9];
            this.oldAssetType = reqAry[10];
            
        } catch (Exception e) {
            throw new YssException("解析数据出错", e);
        }
    }
}
