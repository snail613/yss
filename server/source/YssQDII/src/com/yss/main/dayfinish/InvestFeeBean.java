package com.yss.main.dayfinish;

import java.util.*;
import java.math.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdeal.income.paid.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;

public class InvestFeeBean
    extends BaseDataSettingBean implements IClientListView, IYssConvert {
    private java.util.Date currentDate; //计提日期
    private String portCode; //组合代码
    private String portName; //组合名称
    private double portNetValue; //组合净值
    private String portCury; //组合货币
    private double investFee = 0; //原币计提费用
    private double baseInvestFee = 0; //基础货币计提费用
    private double portInvestFee = 0; //组合货币计提费用
    private ArrayList InvMgrInvestFee; //投资经理分摊费用的集合
    private String MailInvMgrCode = ""; //尾差投资经理
    private String InvMgrCode = ""; //投资经理代码
    private double portRate = 0; //组合汇率
    private double baseRate = 0; //基础汇率
    private java.util.Date startDate; //计息区间起始日
    private java.util.Date endDate; //计息区间截至日
    private String portCodes; //要计算利息的所有组合的代码，以,分隔
    //start add by huangqirong 2013-02-02 story #3488 是否有 运营收支品种信息
    private boolean isHaveIvpBean = false ; 
    
    
    public boolean isHaveIvpBean() {
		return isHaveIvpBean;
	}

	public void setHaveIvpBean(boolean isHaveIvpBean) {
		this.isHaveIvpBean = isHaveIvpBean;
	}
	//end add by huangqirong 2013-02-02 story #3488 是否有 运营收支品种信息

    private InvestPayBean ivpBean = new InvestPayBean();
    public String getPortCodes() {
        return portCodes;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setPortCodes(String portCodes) {
        this.portCodes = portCodes;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setIvpBean(InvestPayBean ivpBean) {
        this.ivpBean = ivpBean;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setPortNetValue(double portNetValue) {
        this.portNetValue = portNetValue;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setBaseInvestFee(double baseInvestFee) {
        this.baseInvestFee = baseInvestFee;
    }

    public void setPortInvestFee(double portInvestFee) {
        this.portInvestFee = portInvestFee;
    }

    public void setPortRate(double portRate) {
        this.portRate = portRate;
    }

    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }

    public void setPortCury(String portCury) {
        this.portCury = portCury;
    }

    public void setInvestFee(double investFee) {
        this.investFee = investFee;
    }

    public void setInvMgrInvestFee(ArrayList InvMgrInvestFee) {
        this.InvMgrInvestFee = InvMgrInvestFee;
    }

    public void setMailInvMgrCode(String MailInvMgrCode) {
        this.MailInvMgrCode = MailInvMgrCode;
    }

    public void setInvMgrCode(String InvMgrCode) {
        this.InvMgrCode = InvMgrCode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public InvestPayBean getIvpBean() {
        return ivpBean;
    }

    public String getPortCode() {
        return portCode;
    }

    public double getPortNetValue() {
        return portNetValue;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public String getPortName() {
        return portName;
    }

    public double getBaseInvestFee() {
        return baseInvestFee;
    }

    public double getPortInvestFee() {
        return portInvestFee;
    }

    public double getPortRate() {
        return portRate;
    }

    public double getBaseRate() {
        return baseRate;
    }

    public String getPortCury() {
        return portCury;
    }

    public double getInvestFee() {
        return investFee;
    }

    public ArrayList getInvMgrInvestFee() {
        return InvMgrInvestFee;
    }

    public String getMailInvMgrCode() {
        return MailInvMgrCode;
    }

    public String getInvMgrCode() {
        return InvMgrCode;
    }

    public InvestFeeBean() {
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        InvestFeeBean investListItem = null;

        try {
            sHeader = this.getListView1Headers();

            BaseIncomePaid income = (BaseIncomePaid) pub.getOperDealCtx().
                getBean("investfee");
            income.setYssPub(pub);
            income.initIncomeCalculate(null, this.startDate, this.endDate,
                                       this.portCodes, "");
            ArrayList incomes = income.getIncomes();
            if (incomes.size() > 0) {
                for (int i = 0; i < incomes.size(); i++) {
                    investListItem = (InvestFeeBean) incomes.get(i);
                    bufShow.append(investListItem.buildRowStr()).
                        append(YssCons.YSS_LINESPLITMARK);
                    bufAll.append(investListItem.buildRowStr()).
                        append(YssCons.YSS_LINESPLITMARK);
                }
            }

            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取两费信息数据出错！", e);
        }

    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() {
        return "";
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() throws YssException {
        String sHeader = "";

        sHeader = this.getListView1Headers();

        return sHeader + "\r\f\r\f\r\f" + this.getListView1ShowCols();
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.ivpBean.getIvPayCatCode()).append("\t");
        buf.append(this.ivpBean.getIvPayCatName()).append("\t");
        if (this.currentDate != null) {
            buf.append(YssFun.formatDate(this.currentDate)).append("\t");
        } else {
            buf.append("").append("\t");
        }

        buf.append(this.portCode).append("\t");
        //buf.append(this.portName).append("\t");
        buf.append(this.ivpBean.getRoundCode()).append("\t");
        buf.append(this.ivpBean.getRoundName()).append("\t");
        buf.append(this.ivpBean.getPerExpCode()).append("\t");
        buf.append(this.ivpBean.getPerExpName()).append("\t");
        buf.append(this.ivpBean.getPeriodCode()).append("\t");
        buf.append(this.ivpBean.getPeriodName()).append("\t");
        buf.append(this.ivpBean.getFixRate()).append("\t");
        buf.append(this.baseRate).append("\t");
        buf.append(this.portRate).append("\t");

        return buf.toString();
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        InvestPayBean pay = new InvestPayBean();
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            pay.setIvPayCatCode(reqAry[0]);
            pay.setIvPayCatName(reqAry[1]);
            if (!reqAry[2].equals("")) {
                this.currentDate = YssFun.toDate(reqAry[2]);
            }
            this.portCode = reqAry[3];
            this.portName = reqAry[4];
            pay.setRoundCode(reqAry[5]);
            pay.setRoundName(reqAry[6]);
            pay.setPerExpCode(reqAry[7]);
            pay.setPerExpName(reqAry[8]);
            pay.setPeriodCode(reqAry[9]);
            pay.setPeriodName(reqAry[10]);
            if (!reqAry[11].equals("") && YssFun.isNumeric(reqAry[11])) {
                pay.setFixRate(new BigDecimal(reqAry[11]));//修改投资运营收支设置中固定比率精度 panjunfang modify 20090815
            }

            if (!reqAry[13].equals("")) {
                this.startDate = YssFun.toDate(reqAry[13]);
            }
            if (!reqAry[14].equals("")) {
                this.endDate = YssFun.toDate(reqAry[14]);
            }
            if (!reqAry[15].equals("") && YssFun.isNumeric(reqAry[15])) {
                this.baseRate = YssFun.toDouble(reqAry[15]);
            }
            if (!reqAry[16].equals("") && YssFun.isNumeric(reqAry[16])) {
                this.portRate = YssFun.toDouble(reqAry[16]);
            }

            this.portCodes = reqAry[17];

            this.ivpBean = pay;

        } catch (Exception e) {
            throw new YssException("获取两费信息数据出错！", e);
        }

    }

    public String getListViewGroupData1() throws YssException {
        return "";
    }

    public String getListViewGroupData2() throws YssException {
        return "";
    }

    public String getListViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }

}
