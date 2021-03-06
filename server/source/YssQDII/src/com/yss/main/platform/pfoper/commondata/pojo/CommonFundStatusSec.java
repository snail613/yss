package com.yss.main.platform.pfoper.commondata.pojo;

import com.yss.util.YssException;
import com.yss.util.YssFun;

public class CommonFundStatusSec {

	private String fundCode = "";        //基金代码       
    private java.util.Date date = null;  //日期       
    private String securityCode = "";    //证券代码
    private String subtsftypeCode = "";  //调拨子类型
    private double aparamt = 0;          //证券数量
    private String assetType = "";       //资产类别
    
    private String oldFundCode = "";              
    private java.util.Date oldDate = null; 
    private String oldSecurityCode = ""; 
    private String oldSubtsftypeCode = "";                
    private String oldAssetType = "";
    
    private CommonFundStatusSec filterType = null;

    public CommonFundStatusSec getFilterType() {
		return filterType;
	}

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

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getSubtsftypeCode() {
		return subtsftypeCode;
	}

	public void setSubtsftypeCode(String subtsftypeCode) {
		this.subtsftypeCode = subtsftypeCode;
	}

	public double getAparamt() {
		return aparamt;
	}

	public void setAparamt(double aparamt) {
		this.aparamt = aparamt;
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

	public String getOldSecurityCode() {
		return oldSecurityCode;
	}

	public void setOldSecurityCode(String oldSecurityCode) {
		this.oldSecurityCode = oldSecurityCode;
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

	public void setFilterType(CommonFundStatusSec filterType) {
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
                        filterType = new CommonFundStatusSec();
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
            this.securityCode = reqAry[2];         
            this.subtsftypeCode = reqAry[3];
            if (YssFun.isNumeric(reqAry[4])) {
                this.aparamt = YssFun.toDouble(reqAry[4]);
            }
            this.assetType = reqAry[5];           
           
            if (YssFun.isDate(reqAry[6])) {
                this.oldDate = YssFun.toDate(reqAry[6]);
            }
            this.oldFundCode = reqAry[7];
            this.oldSecurityCode = reqAry[8];
            this.oldSubtsftypeCode = reqAry[9];
            this.oldAssetType = reqAry[10];
            
        } catch (Exception e) {
            throw new YssException("解析数据出错", e);
        }
    }
}
