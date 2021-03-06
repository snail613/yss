package com.yss.main.platform.pfoper.commondata.pojo;

import java.util.*;

import com.yss.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CommonPreTABean {
    public CommonPreTABean() {
    }

    public String getOldOperType() {
        return oldOperType;
    }

    public String getOldFundCode() {
        return oldFundCode;
    }

    public Date getOldApplyDate() {
        return oldApplyDate;
    }

    public void setFilterType(CommonPreTABean filterType) {
        this.filterType = filterType;
    }

    public void setOldOperType(String oldOperType) {
        this.oldOperType = oldOperType;
    }

    public void setOldFundCode(String oldFundCode) {
        this.oldFundCode = oldFundCode;
    }

    public void setOldApplyDate(Date oldApplyDate) {
        this.oldApplyDate = oldApplyDate;
    }

    public CommonPreTABean getFilterType() {
        return filterType;
    }

    public void setStandByFlag(int StandByFlag) {
        this.StandByFlag = StandByFlag;
    }

    public int getStandByFlag() {
        return StandByFlag;
    }

    public void setSendDate(Date SendDate) {
        this.SendDate = SendDate;
    }

    public Date getSendDate() {
        return SendDate;
    }

    public void setOperType(String OperType) {
        this.OperType = OperType;
    }

    public String getOperType() {
        return OperType;
    }

    public void setManagerCode(String ManagerCode) {
        this.ManagerCode = ManagerCode;
    }

    public String getManagerCode() {
        return ManagerCode;
    }

    public void setFundCode(String FundCode) {
        this.FundCode = FundCode;
    }

    public String getFundCode() {
        return FundCode;
    }

    public void setApplyStock(double ApplyStock) {
        this.ApplyStock = ApplyStock;
    }

    public double getApplyStock() {
        return ApplyStock;
    }

    public void setApplyDate(Date ApplyDate) {
        this.ApplyDate = ApplyDate;
    }

    public Date getApplyDate() {
        return ApplyDate;
    }

    public void setApplyAmount(double ApplyAmount) {
        this.ApplyAmount = ApplyAmount;
    }

    public double getApplyAmount() {
        return ApplyAmount;
    }

    private String ManagerCode = ""; //            管理人代码
    private String FundCode = ""; //               基金代码
    private java.util.Date ApplyDate = null; //    申请日期
    private String OperType = ""; //               业务类型    1申购,赎回等
    private double ApplyStock = 0; //              申请笔数
    private double ApplyAmount = 0; //             申请数量
    private java.util.Date SendDate = null; //     发送日期     中登发这个文件的日期
    private int StandByFlag = 0; //                备用标识
    private String oldFundCode = "";
    private java.util.Date oldApplyDate = null;
    private String oldOperType = "";
  //modify by zhangfa 20101012 MS01829    特定数据处理界面有问题    QDV4赢时胜(33上线测试)2010年10月9日01_B  
    private double oldApplyAmount = 0; //     
    public double getOldApplyAmount() {
		return oldApplyAmount;
	}
    
	public void setOldApplyAmount(double oldApplyAmount) {
		this.oldApplyAmount = oldApplyAmount;
	}

    
  //--------------------------------------------------------------------------------------------------  
    
   
	private CommonPreTABean filterType = null;

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
                        filterType = new CommonPreTABean();
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
            this.ManagerCode = reqAry[0];
            this.FundCode = reqAry[1];
            if (YssFun.isDate(reqAry[2])) {
                this.ApplyDate = YssFun.toDate(reqAry[2]);
            }
            this.OperType = reqAry[3];
            if (YssFun.isNumeric(reqAry[4])) {
                this.ApplyStock = YssFun.toDouble(reqAry[4]);
            }
            if (YssFun.isNumeric(reqAry[5])) {
                this.ApplyAmount = YssFun.toDouble(reqAry[5]);
            }
            if (YssFun.isDate(reqAry[6])) {
                this.SendDate = YssFun.toDate(reqAry[6]);
            }
            if (YssFun.isNumeric(reqAry[7])) {
                this.StandByFlag = YssFun.toInt(reqAry[7]);
            }
            this.oldFundCode = reqAry[8];
            if (YssFun.isDate(reqAry[9])) {
                this.oldApplyDate = YssFun.toDate(reqAry[9]);
            }
            this.oldOperType = reqAry[10];
          //modify by zhangfa 20101012 MS01829    特定数据处理界面有问题    QDV4赢时胜(33上线测试)2010年10月9日01_B  
            if (YssFun.isNumeric(reqAry[11])) {
            	this.oldApplyAmount = YssFun.toDouble(reqAry[11]);
            }
          //--------------------------------------------------------------------------------------------------  
        } catch (Exception e) {
            throw new YssException("解析数据出错", e);
        }

    }
}
