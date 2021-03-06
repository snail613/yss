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
public class CommonPreCashBean {
    public CommonPreCashBean() {
    }

    public void setResume(String Resume) {
        this.Resume = Resume;
    }

    public String getResume() {
        return Resume;
    }

    public void setPayDate(Date PayDate) {
        this.PayDate = PayDate;
    }

    public Date getPayDate() {
        return PayDate;
    }

    public void setMoney(double Money) {
        this.Money = Money;
    }

    public double getMoney() {
        return Money;
    }

    public void setInOut(int InOut) {
        this.InOut = InOut;
    }

    public int getInOut() {
        return InOut;
    }

    public void setEndDate(Date EndDate) {
        this.EndDate = EndDate;
    }

    public Date getEndDate() {
        return EndDate;
    }

    public void setCuryCode(String CuryCode) {
        this.CuryCode = CuryCode;
    }

    public String getCuryCode() {
        return CuryCode;
    }

    public void setCashAccount(String CashAccount) {
        this.CashAccount = CashAccount;
    }

    public String getCashAccount() {
        return CashAccount;
    }

    public void setBeginDate(Date BeginDate) {
        this.BeginDate = BeginDate;
    }

    public Date getBeginDate() {
        return BeginDate;
    }

    public Date getOldPayDate() {
        return oldPayDate;
    }

    public void setOldCashAccount(String oldCashAccount) {
        this.oldCashAccount = oldCashAccount;
    }

    public void setOldPayDate(Date oldPayDate) {
        this.oldPayDate = oldPayDate;
    }

    public void setFilterType(CommonPreCashBean filterType) {
        this.filterType = filterType;
    }

    public String getOldCashAccount() {
        return oldCashAccount;
    }

    public CommonPreCashBean getFilterType() {
        return filterType;
    }
//====by xuxuming,20090909.MS00679,当业务人员修改其中一条记录的金额时，会报主键重复错误,QDV4海富通2009年9月05日01_B===========
    public void setDOldMoney(double Money) {
    this.dOldMoney = Money;
}

public double getDOldMoney() {
    return dOldMoney;
}
//=====================================
    private java.util.Date BeginDate = null; //
    private java.util.Date EndDate = null; //   有效截止日
    private java.util.Date PayDate = null; // 支付日期
    private String Resume = ""; //        摘要
    private String CashAccount = ""; // 现金帐户
    private String CuryCode = ""; //       货币代码
    private double Money = 0; //       金额
    private int InOut = 0; //流入流出 1代表流入;-1代表流出
    private java.util.Date oldPayDate = null;
    private String oldCashAccount = "";
    //====by xuxuming,20090909.MS00679,当业务人员修改其中一条记录的金额时，会报主键重复错误,QDV4海富通2009年9月05日01_B===========
    private double dOldMoney = 0;//       原有的金额
    //===================================================================

    private CommonPreCashBean filterType = null;

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
                        filterType = new CommonPreCashBean();
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
                BeginDate = YssFun.toDate(reqAry[0]);
            }
            if (YssFun.isDate(reqAry[1])) {
                EndDate = YssFun.toDate(reqAry[1]);
            }
            if (YssFun.isDate(reqAry[2])) {
                PayDate = YssFun.toDate(reqAry[2]);
            }
            this.Resume = reqAry[3];
            this.CashAccount = reqAry[4];
            this.CuryCode = reqAry[5];
            if (YssFun.isNumeric(reqAry[6])) {
                this.Money = YssFun.toDouble(reqAry[6]);
            }
            if (YssFun.isNumeric(reqAry[7])) {
                this.InOut = YssFun.toInt(reqAry[7]);
            }
            if (YssFun.isDate(reqAry[8])) {
                this.oldPayDate = YssFun.toDate(reqAry[8]);
            }
            this.oldCashAccount = reqAry[9];
            //====by xuxuming,20090909.MS00679,当业务人员修改其中一条记录的金额时，会报主键重复错误,QDV4海富通2009年9月05日01_B===========
            if (YssFun.isNumeric(reqAry[10])) {
                this.dOldMoney = YssFun.toDouble(reqAry[10]);
            }
           //=================================================================================
        } catch (Exception e) {
            throw new YssException("解析数据出错", e);
        }
    }

}
