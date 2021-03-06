package com.yss.main.parasetting;

import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.datainterface.cnstock.BrokerRateBean;
import com.yss.main.datainterface.cnstock.CNInterfaceParamAdmin;
import com.yss.main.datainterface.cnstock.RateSpeciesTypeBean;
import com.yss.main.funsetting.*;
import com.yss.main.operdeal.*;
import com.yss.main.operdeal.datainterface.cnstock.pojo.FeeAttributeBean;
import com.yss.main.operdeal.datainterface.cnstock.pojo.FeeWayBean;
import com.yss.main.operdeal.datainterface.cnstock.shstock.SHGHBean;
import com.yss.main.operdeal.datainterface.cnstock.szstock.SZHBBean;
import com.yss.main.operdeal.linkInfo.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;

/**
 *
 * <p>Title: FeeBean </p>
 * <p>Description: 费用设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class FeeBean
    extends BaseDataSettingBean implements IDataSetting {
    private String feeCode = ""; //费用代码
    private String feeName = ""; //费用名称
    private String feeType = "0"; //费用类型
    private String roundCode = ""; //舍入条件代码
    private String roundName = ""; //舍入条件名称
    private String perExpCode = ""; //比率公式代码
    private String perExpName = ""; //比率公式名称
    private String desc = ""; //费用描述
    private String accountingWay = "0"; //结算方式
    private FeeBean filterType;
    private String isSettle = ""; //清算金额项
    private String assumeMan = ""; //承担者
    private String assumeManName = "";
    private String isSettleName = "";
    private String oldFeeCode;
    private String accountingWayName = ""; //核算方式的名称

    private String securityCode = "";
    private String tradeType = "";
    private String portCode = "";
    private String brokerCode = "";
    private double sumMoney;
    private double amount; //数量
    private double cost; //成本
    private double income; //收入
    private double interest; //利息
    private double fee; //费用
    private String sRecycled = "";
    private String tradeSeatCode="";//story 2092 by zhouwei 20120105 交易席位
    public FeeBean() {
    }

    public String getFeeCode() {
        return feeCode;
    }

    public void setAccountingWayName(String accountingWayName) {
        this.accountingWayName = accountingWayName;
    }

    public String getAccountingWayName() {
        return accountingWayName;
    }

    public void setFeeCode(String feeCode) {
        this.feeCode = feeCode;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public String getPerExpCode() {
        return perExpCode;
    }

    public void setPerExpCode(String perExpCode) {
        this.perExpCode = perExpCode;
    }

    public String getRoundCode() {
        return roundCode;
    }

    public FeeBean getFilterType() {
        return filterType;
    }

    public double getSumMoney() {
        return sumMoney;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getTradeType() {
        return tradeType;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getFeeType() {
        return feeType;
    }

    public double getAmount() {
        return amount;
    }

    public double getInterest() {
        return interest;
    }

    public double getCost() {
        return cost;
    }

    public double getFee() {
        return fee;
    }

    public double getIncome() {
        return income;
    }

    public String getAccountingWay() {
        return accountingWay;
    }

    public String getRoundName() {
        return roundName;
    }

    public String getPerExpName() {
        return perExpName;
    }

    public String getOldFeeCode() {
        return oldFeeCode;
    }

    public String getIsSettle() {
        return isSettle;
    }

    public String getDesc() {
        return desc;
    }

    public String getAssumeMan() {
        return assumeMan;
    }

    public String getIsSettleName() {
        return isSettleName;
    }

    public String getAssumeManName() {
        return assumeManName;
    }

    public void setSumMoney(double dSumMoney) {
        this.sumMoney = dSumMoney;
    }

    public void setRoundCode(String roundCode) {
        this.roundCode = roundCode;
    }

    public void setFilterType(FeeBean filterType) {
        this.filterType = filterType;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public void setAccountingWay(String accountingWay) {
        this.accountingWay = accountingWay;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public void setPerExpName(String perExpName) {
        this.perExpName = perExpName;
    }

    public void setOldFeeCode(String oldFeeCode) {
        this.oldFeeCode = oldFeeCode;
    }

    public void setIsSettle(String isSettle) {
        this.isSettle = isSettle;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setAssumeMan(String assumeMan) {
        this.assumeMan = assumeMan;
    }

    public void setIsSettleName(String isSettleName) {
        this.isSettleName = isSettleName;
    }

    public void setAssumeManName(String assumeManName) {
        this.assumeManName = assumeManName;
    }

    /**
     * parseRowStr
     * 解析费用设置
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String[] reqAry = null;

        try {
            if (sRowStr.equals("")) {
                return;
            }
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.feeCode = reqAry[0];
            this.feeName = reqAry[1];
            this.feeType = reqAry[2];
            this.roundCode = reqAry[3];
            this.perExpCode = reqAry[4];
            //------ modify by wangzuochun 2011.06.03 BUG 2003 证券信息维护界面，维护一条证券信息，输入描述信息若含有回车符，清除/还原时报错 
            if (reqAry[5] != null ){
            	if (reqAry[5].indexOf("【Enter】") >= 0){
            		this.desc = reqAry[5].replaceAll("【Enter】", "\r\n");
            	}
            	else{
            		this.desc = reqAry[5];
            	}
            }
            //----------------- BUG 2003 ----------------//
            this.securityCode = reqAry[6];
            this.portCode = reqAry[7];
            this.brokerCode = reqAry[8];
            this.tradeType = reqAry[9];
            this.sumMoney = Double.parseDouble(reqAry[10]);
            this.amount = Double.parseDouble(reqAry[11]);
            this.cost = Double.parseDouble(reqAry[12]);
            this.income = Double.parseDouble(reqAry[13]);
            this.interest = Double.parseDouble(reqAry[14]);
            this.fee = Double.parseDouble(reqAry[15]);
            this.checkStateId = Integer.parseInt(reqAry[16]);
            this.accountingWay = reqAry[17];
            this.oldFeeCode = reqAry[18];
            this.isSettle = reqAry[19];
            this.assumeMan = reqAry[20];
            this.tradeSeatCode=reqAry[21];//story 2092 by zhouwei 20120105 交易席位
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new FeeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析费用设置请求出错", e);
        }
    }

    /**
     * auditSetting
     */
    public void auditSetting() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.feeCode).append("\t");
        buf.append(this.feeName).append("\t");
        buf.append(this.feeType).append("\t");
        buf.append(this.roundCode).append("\t");
        buf.append(this.roundName).append("\t");
        buf.append(this.perExpCode).append("\t");
        buf.append(this.perExpName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.accountingWay).append("\t");
        buf.append(this.isSettle).append("\t");
        buf.append(this.assumeMan).append("\t");
        buf.append(this.isSettleName).append("\t");
        buf.append(this.assumeManName).append("\t");
        buf.append(this.accountingWayName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     */
    public void checkInput() {
    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.feeCode.length() != 0) {
                sResult = sResult + " and a.FFeeCode like '" +
                    filterType.feeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.feeName.length() != 0) {
                sResult = sResult + " and a.FFeeName like '" +
                    filterType.feeName.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.feeType.equalsIgnoreCase("99") &&
                this.filterType.feeType.length() != 0) {
                sResult = sResult + " and a.FFeeType = " +
                    filterType.feeType;
            }
            if (!this.filterType.accountingWay.equalsIgnoreCase("99") &&
                this.filterType.accountingWay.length() != 0) {
                sResult = sResult + " and a.FAccountingWay = " +
                    filterType.accountingWay;
            }
            if (this.filterType.roundCode.length() != 0) {
                sResult = sResult + " and a.FRoundCode like '" +
                    filterType.roundCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.perExpCode.length() != 0) {
                sResult = sResult + " and a.FPerExpCode like '" +
                    filterType.perExpCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%' ";
            }
            if (this.filterType.isSettle.length() != 0 && !this.filterType.isSettle.equals("99")) {
                sResult += " and a.FIsSettle =" + this.filterType.isSettle;
            } //2008-5-23 单亮 添加！
            if (this.filterType.assumeMan.length() != 0 && !this.filterType.assumeMan.equals("99")) {
                sResult += " and a.FAssumeMan =" + this.filterType.assumeMan;
            }
        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取费用设置信息
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = ""; //,sVocStr1="";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.*, f.FVocName as FFeeTypeValue, b.FRoundName, d.FUserName as FCreatorName,e.FUserName as FCheckUserName" +
                ",c.FPerExpName,g.FVocName as FIsSettleName, h.FVocName as FAssumeManName,i.fvocname as FAccountingWayName " + //2008-5-27 单亮添加 ,i.fvocname as FAccountingWayName
                " from " + pub.yssGetTableName("Tb_Para_Fee") + " a " +
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState = 1) b on a.FRoundCode = b.FRoundCode" +
                " left join (select FFormulaCode,FFormulaName as FPerExpName from " +
                pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState = 1) c on a.FPerExpCode = c.FFormulaCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " left join Tb_Fun_Vocabulary f on " + dbl.sqlToChar("a.FFeeType") + " = f.FVocCode and f.FVocTypeCode = " + //2007.11.28 修改 蒋锦 使用dbl.sqlToChar()处理"a.FFeeType"，否则在使用DB2数据库时会报数据类型错误
                dbl.sqlString(YssCons.YSS_FEE_FEETYPE) +
                " left join Tb_Fun_Vocabulary g on " + dbl.sqlToChar("a.FIsSettle") + " = g.FVocCode and g.FVocTypeCode = " + //2007.11.28 修改 蒋锦 使用dbl.sqlToChar()处理"a.FIsSettle"，否则在使用DB2数据库时会报数据类型错误
                dbl.sqlString(YssCons.YSS_CI_ENDOFDAY) +
                " left join Tb_Fun_Vocabulary h on " + dbl.sqlToChar("a.FAssumeMan") + " = h.FVocCode and h.FVocTypeCode = " + //2007.11.28 修改 蒋锦 使用dbl.sqlToChar()处理"a.FAssumeMan"，否则在使用DB2数据库时会报数据类型错误
                dbl.sqlString(YssCons.YSS_FEE_ASSUMEMAN) +
                " left join Tb_Fun_Vocabulary i on " + dbl.sqlToChar("a.FAccountingWay") + " = i.FVocCode and i.FVocTypeCode = " + //2008-5-27 单亮
                dbl.sqlString(YssCons.YSS_PARA_FACCOUNTINGWAY) + //2008-5-27 单亮
                buildFilterSql() + " order by a.FCheckState,a.FCreateTime desc"; // wdy modify 20070830

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.feeCode = rs.getString("FFeeCode") + "";
                this.feeName = rs.getString("FFeeName") + "";
                this.feeType = rs.getString("FFeeType") + "";
                this.roundCode = rs.getString("FRoundCode");
                this.roundName = rs.getString("FRoundName");
                this.perExpCode = rs.getString("FPerExpCode");
                this.perExpName = rs.getString("FPerExpName");
                this.desc = rs.getString("FDesc");
                this.isSettle = rs.getString("FIsSettle");
                this.assumeMan = rs.getString("FAssumeMan");
                this.isSettleName = rs.getString("FIsSettleName");
                this.assumeManName = rs.getString("FAssumeManName");
                this.accountingWay = rs.getString("FAccountingWay");
                this.accountingWayName = rs.getString("FAccountingWayName"); ////2008-5-27 单亮
                super.setRecLog(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_FEE_FEETYPE + "," +
                                        YssCons.YSS_PARA_FACCOUNTINGWAY + "," +
                                        YssCons.YSS_CI_ENDOFDAY + "," +
                                        YssCons.YSS_FEE_ASSUMEMAN);

            //  VocabularyBean vocabulary1 = new VocabularyBean();
            //   vocabulary1.setYssPub(pub);
            //  sVocStr1 = vocabulary.getVoc(YssCons.YSS_PARA_FACCOUNTINGWAY);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr; //+"\f\f"+sVocStr1;
        } catch (Exception e) {
            throw new YssException("获取费用信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData2
     * 获取已审核的费用设置信息
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "费用代码\t费用名称\t费用描述";
            strSql = "select a.*, f.FVocName as FFeeTypeValue, b.FRoundName, d.FUserName as FCreatorName,e.FUserName as FCheckUserName" +
                ",c.FPerExpName" +
                " from " + pub.yssGetTableName("Tb_Para_Fee") + " a " +
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState = 1) b on a.FRoundCode = b.FRoundCode" +
                " left join (select FFormulaCode,FFormulaName as FPerExpName from " +
                pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState = 1) c on a.FPerExpCode = c.FFormulaCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " left join Tb_Fun_Vocabulary f on " + dbl.sqlToChar("a.FFeeType") + " = f.FVocCode and f.FVocTypeCode = " + // 2007.12.06 修改 蒋锦.
                dbl.sqlString(YssCons.YSS_FEE_FEETYPE) +
                " where a.FCheckState = 1 order by a.FCheckState,a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FFeeCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FFeeName") + "").trim()).append(
                    "\t");
                bufShow.append(YssFun.left( (rs.getString("FDesc") + "").trim(), 40)).
                    append(YssCons.YSS_LINESPLITMARK);

                this.feeCode = rs.getString("FFeeCode") + "";
                this.feeName = rs.getString("FFeeName") + "";
                this.feeType = rs.getString("FFeeType") + "";
                this.roundCode = rs.getString("FRoundCode");
                this.roundName = rs.getString("FRoundName");
                this.perExpCode = rs.getString("FPerExpCode");
                this.perExpName = rs.getString("FPerExpName");
                this.desc = rs.getString("FDesc");
                super.setRecLog(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取可用费用信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() throws YssException {
        ArrayList alFeeBeans = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        FeeBean fee = null;
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sHeader = "";
        double dFeeMoney;
        double dTotalFee = 0;
        YssFeeType feeType = null;
        DecimalFormat format = new DecimalFormat("#,##0.##");
        sHeader = this.getListView3Headers();
        FeeLinkBean FeeLink = null;
        try {
            BaseOperDeal baseOper = this.getSettingOper();
            /*BaseFeeDeal feeOper = (BaseFeeDeal) pub.getOperDealCtx().getBean(
                  "feedeal");
                      baseOper.setYssPub(pub);
                      feeOper.setYssPub(pub);
                      feeOper.setFeeAttr(this.securityCode, this.tradeType, this.portCode,
                               this.brokerCode, this.sumMoney);
                      alFeeBeans = feeOper.getFeeBeans();*/
            BaseLinkInfoDeal feeOper = (BaseLinkInfoDeal) pub.getOperDealCtx().getBean(
                "FeeLinkDeal");
            baseOper.setYssPub(pub);
            feeOper.setYssPub(pub);
            FeeLink = new FeeLinkBean();
            FeeLink.setSecurityCode(this.securityCode);
            FeeLink.setTradeTypeCode(this.tradeType);
            FeeLink.setPortCode(this.portCode);
            FeeLink.setBrokerCode(this.brokerCode);           
            feeOper.setLinkAttr(FeeLink);
            alFeeBeans = feeOper.getLinkInfoBeans();
            if (alFeeBeans != null) {
                feeType = new YssFeeType();
                feeType.setMoney(this.sumMoney);
                feeType.setInterest(this.interest);
                feeType.setAmount(this.amount);
                feeType.setCost(this.cost);
                feeType.setIncome(this.income);
                feeType.setFee(this.fee);

                for (int i = 0; i < alFeeBeans.size(); i++) {
                    fee = (FeeBean) alFeeBeans.get(i);
                    dFeeMoney = baseOper.calFeeMoney(feeType, fee);
                    //dFeeMoney = baseOper.calMoneyByPerExp(fee.getPerExpCode(),
                    //    fee.getRoundCode(), sumMoney);
                    dTotalFee = YssD.add(dTotalFee, dFeeMoney);
                    bufShow.append(fee.getFeeName()).append("\t");
                    bufShow.append(format.format(dFeeMoney)).append(YssCons.
                        YSS_LINESPLITMARK);

//               this.feeCode = fee.getFeeCode();
//               this.feeType = fee.getFeeType();
//               this.roundCode = fee.getRoundCode();
//               this.perExpCode = fee.getPerExpCode();
//               this.accountingWay = fee.getAccountingWay();

                    bufAll.append(fee.getFeeCode()).append(YssCons.
                        YSS_ITEMSPLITMARK2);
                    bufAll.append(fee.getFeeName()).append(YssCons.
                        YSS_ITEMSPLITMARK2);
                    bufAll.append(YssFun.formatNumber(dFeeMoney, "###0.##")).append(
                        YssCons.
                        YSS_ITEMSPLITMARK2);
                    bufAll.append(fee.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }
                if (alFeeBeans.size() != 0) {
                    bufShow.append("Total: ").append("\t");
                    bufShow.append(format.format(dTotalFee)).append(YssCons.
                        YSS_LINESPLITMARK);

                    bufAll.append("total").append(YssCons.YSS_ITEMSPLITMARK2);
                    bufAll.append("Total: ").append(YssCons.YSS_ITEMSPLITMARK2);
                    bufAll.append(YssFun.formatNumber(dTotalFee, "###0.##")).append(
                        YssCons.
                        YSS_ITEMSPLITMARK2);
                    bufAll.append("").append(YssCons.YSS_LINESPLITMARK);
                }
                if (bufShow.toString().length() > 2) {
                    sShowDataStr = bufShow.toString().substring(0,
                        bufShow.toString().length() - 2);
                }

                if (bufAll.toString().length() > 2) {
                    sAllDataStr = bufAll.toString().substring(0,
                        bufAll.toString().length() - 2);
                }
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            //2008.2.13 修改 蒋锦 直接抛出 e 中的异常消息。
            throw new YssException(e.getMessage());
        }
    }
    //story 2092 by zhouwei 20120105 QDV4赢时胜(上海开发部)2011年12月29日01_A
    //检验证券品种是否为国内股票
    private String checkSecurityType() throws YssException{
    	String exchangeCode="";
    	ResultSet rs=null;
    	try{
    		String sql="select * from "+pub.yssGetTableName("Tb_Para_Security")
    		+" where FCatCode='EQ' and ( FExchangeCode='CG' OR FExchangeCode='CS') and fcheckstate=1"
    		+" and FSecurityCode="+dbl.sqlString(this.securityCode);
    		rs=dbl.openResultSet(sql);
    		if(rs.next()){
    			exchangeCode=rs.getString("FExchangeCode");
    		}
    	}catch (Exception e) {
			throw new YssException("获取证券的品种信息出错！"+e.getMessage(),e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    	return exchangeCode;
    }
    //story 2092 by zhouwei 20120106 
    //上交所，股票费用计算
    private String getFeeInfosOfCGEQ() throws YssException{
    	String reStr="";
    	ResultSet rs=null;
    	StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sHeader = "";
        DecimalFormat format = new DecimalFormat("#,##0.##");
        sHeader = this.getListView3Headers();
    	try{

        	if(this.tradeSeatCode==null || this.tradeSeatCode.equals("")){
        		throw new YssException("请选择席位代码！");
        	}
        	String fSeatNum="";//席位号
        	String sql="select * from  "+pub.yssGetTableName("Tb_Para_TradeSeat")+" where FSeatCode="+dbl.sqlString(this.tradeSeatCode)
        	   +" and FBrokerCode="+dbl.sqlString(this.brokerCode)+" and FCheckState=1 ";
        	rs=dbl.openResultSet(sql);
        	if(rs.next()){
        		fSeatNum=rs.getString("fSeatNum");
        	}else{
        		throw new YssException("请选择与券商相匹配的席位代码！");
        	}
        	dbl.closeResultSetFinal(rs);
            //获取券商与席位
        	HashMap hmBrokerCode=new HashMap();
            hmBrokerCode.put(fSeatNum, this.brokerCode);
        	//获取数据接口参数设置的费用承担方向界面设置的参数对应的HashMap
        	CNInterfaceParamAdmin interfaceParam = new CNInterfaceParamAdmin();
    		interfaceParam.setYssPub(pub);
    		HashMap hmFeeWay = (HashMap) interfaceParam.getFeeWayBean();
          //获取数据接口参数设置的交易所债券参数设置界面设置的参数对应的HashMap
    		HashMap hmExchangeBond = (HashMap) interfaceParam.getExchangeBondBean();
           //获取交易费率品种设置界面设置的费率对应的HashMap
        	RateSpeciesTypeBean rateSpeciesType = new RateSpeciesTypeBean();
            rateSpeciesType.setYssPub(pub);
            HashMap hmRateSpeciesType = (HashMap) rateSpeciesType.getRateSpeciesTypeBean(new Date());
           //获取券商佣金利率设置界面设置的券商佣金利率对应的HashMap
            BrokerRateBean brokerRate = new BrokerRateBean();
            brokerRate.setYssPub(pub);
            HashMap hmBrokerRate = (HashMap) brokerRate.getBrokerReateBean(new Date());
            FeeAttributeBean feeAttribute=new FeeAttributeBean();
            feeAttribute.setCjje(this.sumMoney);
            feeAttribute.setCjsl(this.amount);
            if(this.amount!=0){
                feeAttribute.setCjjg(YssD.div(this.sumMoney, this.amount));
            }
            //上交所B股 证券代码已900打头
            if(this.securityCode.length()>3 && this.securityCode.substring(0, 2).equals("900")){
            	feeAttribute.setSecuritySign("B_GP");//证券标志 B股票
            }else{
            	feeAttribute.setSecuritySign("GP");//证券标志A 股票
            }
            feeAttribute.setJyfs("PT");//交易方式  ：大宗交易/普通交易
            feeAttribute.setBusinessSign("GNGP");//业务标志
            feeAttribute.setZqdm(this.securityCode);//证券代码
            feeAttribute.setPortCode(this.portCode);//组合代码
            feeAttribute.setGsdm(fSeatNum);//席位号
            feeAttribute.setSeatCode(fSeatNum);//席位代码 
            feeAttribute.setDate(new Date());
            feeAttribute.setHmBrokerCode(hmBrokerCode);
            feeAttribute.setHmBrokerRate(hmBrokerRate);
            feeAttribute.setHmExchangeBond(hmExchangeBond);
            feeAttribute.setHmFeeWay(hmFeeWay);
            feeAttribute.setHmRateSpeciesType(hmRateSpeciesType);
            feeAttribute.setSelectedFee("FYJ");
            sql="select * from "+pub.yssGetTableName("Tb_Base_TradeType")+" where FTradeTypeCode="+dbl.sqlString(this.tradeType);
       	    rs=dbl.openResultSet(sql);
       	    int inOut=0;
       	    while(rs.next()){
       	    	inOut=rs.getInt("FAmountInd");
       	    }
       	    if(inOut==1){
       	    	feeAttribute.setBs("B");
       	    }else{
       	    	feeAttribute.setBs("S");
       	    }
            SHGHBean shb=new SHGHBean();
            shb.setYssPub(pub);
            shb.calculateFee(feeAttribute);
            List alFeeBeans=new ArrayList();
            FeeBean fb=null;
            fb=new FeeBean();
            fb.setFeeCode("YSS_SHYJ");
            fb.setFeeName("上海佣金");
            fb.setYssPub(pub);
            fb.getSetting();
			if (fb != null) {
				alFeeBeans.add(fb);
			}
            fb=new FeeBean();
            fb.setFeeCode("YSS_SHGH");
            fb.setFeeName("上海过户费");
            fb.setYssPub(pub);
            fb.getSetting();
			if (fb != null) {
				alFeeBeans.add(fb);
			}
            fb=new FeeBean();
            fb.setFeeCode("YSS_SHJS");
            fb.setFeeName("上海经手费");
            fb.setYssPub(pub);
            fb.getSetting();
			if (fb != null) {
				alFeeBeans.add(fb);
			}
            fb=new FeeBean();
            fb.setFeeCode("YSS_SHZG");
            fb.setFeeName("上海证管费");
            fb.setYssPub(pub);
            fb.getSetting();
			if (fb != null) {
				alFeeBeans.add(fb);
			}
            fb=new FeeBean();
            fb.setFeeCode("YSS_SHYH");
            fb.setFeeName("上海印花税");
            fb.setYssPub(pub);
            fb.getSetting();
			if (fb != null) {
				alFeeBeans.add(fb);
			}
//            fb=new FeeBean();
//            fb.setFeeCode("YSS_SHFX");
//            fb.setFeeName("上海股票风险金");
//            alFeeBeans.add(fb);
            if(feeAttribute.getSecuritySign().equals("B_GP")){//B股
            	fb=new FeeBean();
                fb.setFeeCode("YSS_SHJSF");
                fb.setFeeName("上海结算费");
                fb.setYssPub(pub);
                fb.getSetting();
    			if (fb != null) {
    				alFeeBeans.add(fb);
    			}
            }         
            double dTotalFee=0;
            for (int i = 0; i < alFeeBeans.size(); i++) {
                fb = (FeeBean) alFeeBeans.get(i);       
                double dFeeMoney=0;
                if(fb.getFeeCode().equalsIgnoreCase("YSS_SHGH")){
                	dFeeMoney=feeAttribute.getFGhf();
                }else if(fb.getFeeCode().equalsIgnoreCase("YSS_SHJS")){
                	dFeeMoney=feeAttribute.getFJsf();
                }else if(fb.getFeeCode().equalsIgnoreCase("YSS_SHZG")){
                	dFeeMoney=feeAttribute.getFZgf();
                }else if(fb.getFeeCode().equalsIgnoreCase("YSS_SHYJ")){
                	dFeeMoney=feeAttribute.getFYj();
                }else if(fb.getFeeCode().equalsIgnoreCase("YSS_SHYH")){
                	dFeeMoney=feeAttribute.getFYhs();
                }else if(fb.getFeeCode().equalsIgnoreCase("YSS_SHFX")){
                	dFeeMoney=feeAttribute.getFfxj();
                }else if(fb.getFeeCode().equalsIgnoreCase("YSS_SHJSF")){
                	dFeeMoney=feeAttribute.getFJsf();
                }
                dTotalFee = YssD.add(dTotalFee, dFeeMoney);
                bufShow.append(fb.getFeeName()).append("\t");
                bufShow.append(format.format(dFeeMoney)).append(YssCons.
                    YSS_LINESPLITMARK);
                bufAll.append(fb.getFeeCode()).append(YssCons.
                    YSS_ITEMSPLITMARK2);
                bufAll.append(fb.getFeeName()).append(YssCons.
                    YSS_ITEMSPLITMARK2);
                bufAll.append(YssFun.formatNumber(dFeeMoney, "###0.##")).append(
                    YssCons.
                    YSS_ITEMSPLITMARK2);
                bufAll.append(fb.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (alFeeBeans.size() != 0) {
                bufShow.append("Total: ").append("\t");
                bufShow.append(format.format(dTotalFee)).append(YssCons.
                    YSS_LINESPLITMARK);

                bufAll.append("total").append(YssCons.YSS_ITEMSPLITMARK2);
                bufAll.append("Total: ").append(YssCons.YSS_ITEMSPLITMARK2);
                bufAll.append(YssFun.formatNumber(dTotalFee, "###0.##")).append(
                    YssCons.
                    YSS_ITEMSPLITMARK2);
                bufAll.append("").append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;     
    	}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    }
    //story 2092 by zhouwei 20120106 
    //深交所，股票费用计算
    private String getFeeInfosOfCSEQ() throws YssException{
    	String reStr="";
    	ResultSet rs=null;
    	StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sHeader = "";
        DecimalFormat format = new DecimalFormat("#,##0.##");
        sHeader = this.getListView3Headers();
    	try{

        	if(this.tradeSeatCode==null || this.tradeSeatCode.equals("")){
        		throw new YssException("请选择席位代码！");
        	}
        	String fSeatNum="";//席位号
        	String sql="select * from  "+pub.yssGetTableName("Tb_Para_TradeSeat")+" where FSeatCode="+dbl.sqlString(this.tradeSeatCode)
        	   +" and FBrokerCode="+dbl.sqlString(this.brokerCode)+" and FCheckState=1 ";
        	rs=dbl.openResultSet(sql);
        	if(rs.next()){
        		fSeatNum=rs.getString("fSeatNum");
        	}else{
        		throw new YssException("请选择与券商相匹配的席位代码！");
        	}
        	dbl.closeResultSetFinal(rs);
            //获取券商与席位
        	HashMap hmBrokerCode=new HashMap();
            hmBrokerCode.put(fSeatNum, this.brokerCode);
        	//获取数据接口参数设置的费用承担方向界面设置的参数对应的HashMap
        	CNInterfaceParamAdmin interfaceParam = new CNInterfaceParamAdmin();
    		interfaceParam.setYssPub(pub);
    		HashMap hmFeeWay = (HashMap) interfaceParam.getFeeWayBean();
          //获取数据接口参数设置的交易所债券参数设置界面设置的参数对应的HashMap
    		HashMap hmExchangeBond = (HashMap) interfaceParam.getExchangeBondBean();
           //获取交易费率品种设置界面设置的费率对应的HashMap
        	RateSpeciesTypeBean rateSpeciesType = new RateSpeciesTypeBean();
            rateSpeciesType.setYssPub(pub);
            HashMap hmRateSpeciesType = (HashMap) rateSpeciesType.getRateSpeciesTypeBean(new Date());
           //获取券商佣金利率设置界面设置的券商佣金利率对应的HashMap
            BrokerRateBean brokerRate = new BrokerRateBean();
            brokerRate.setYssPub(pub);
            HashMap hmBrokerRate = (HashMap) brokerRate.getBrokerReateBean(new Date());
            FeeAttributeBean feeAttribute=new FeeAttributeBean();
            feeAttribute.setCjje(this.sumMoney);
            feeAttribute.setCjsl(this.amount);
            if(this.amount!=0){
                feeAttribute.setCjjg(YssD.div(this.sumMoney, this.amount));
            }
            //深交所B股 证券代码已20打头
            if(this.securityCode.length()>2 && this.securityCode.substring(0, 1).equals("20")){
            	feeAttribute.setSecuritySign("B_GP");//证券标志 B股票
            }else{
            	feeAttribute.setSecuritySign("GP");//证券标志A 股票
            }
            feeAttribute.setJyfs("PT");//交易方式  ：大宗交易/普通交易
            feeAttribute.setBusinessSign("GNGP");//业务标志
            feeAttribute.setZqdm(this.securityCode);//证券代码
            feeAttribute.setPortCode(this.portCode);//组合代码
            feeAttribute.setGsdm(fSeatNum);//席位号
            feeAttribute.setSeatCode(fSeatNum);//席位代码 
            feeAttribute.setDate(new Date());
            feeAttribute.setHmBrokerCode(hmBrokerCode);
            feeAttribute.setHmBrokerRate(hmBrokerRate);
            feeAttribute.setHmExchangeBond(hmExchangeBond);
            feeAttribute.setHmFeeWay(hmFeeWay);
            feeAttribute.setHmRateSpeciesType(hmRateSpeciesType);
            sql="select * from "+pub.yssGetTableName("Tb_Base_TradeType")+" where FTradeTypeCode="+dbl.sqlString(this.tradeType);
       	    rs=dbl.openResultSet(sql);
       	    int inOut=0;
       	    while(rs.next()){
       	    	inOut=rs.getInt("FAmountInd");
       	    }
       	    if(inOut==1){
       	    	feeAttribute.setBs("B");
       	    }else{
       	    	feeAttribute.setBs("S");
       	    }
       	    
            SZHBBean szb=new SZHBBean();
            szb.setYssPub(pub);
            feeAttribute.setComeFromQS(false);
            feeAttribute.setSelectedFee("FYj");
            szb.calculateFee(feeAttribute);
            feeAttribute.setComeFromQS(true);
            feeAttribute.setSelectedFee("FJsf");
            szb.calculateFee(feeAttribute);
            feeAttribute.setSelectedFee("FZgf");
            szb.calculateFee(feeAttribute);
            List alFeeBeans=new ArrayList();
            FeeBean fb=null;
            fb=new FeeBean();
            fb.setFeeCode("YSS_SZYJ");
            fb.setFeeName("深圳佣金");
            fb.setYssPub(pub);
            fb.getSetting();
			if (fb != null) {
				alFeeBeans.add(fb);
			}
//            fb=new FeeBean();
//            fb.setFeeCode("YSS_SZGH");
//            fb.setFeeName("深圳过户费");
//            alFeeBeans.add(fb);
            fb=new FeeBean();
            fb.setFeeCode("YSS_SZJS");
            fb.setFeeName("深圳经手费");
            fb.setYssPub(pub);
            fb.getSetting();
			if (fb != null) {
				alFeeBeans.add(fb);
			}
            fb=new FeeBean();
            fb.setFeeCode("YSS_SZZG");
            fb.setFeeName("深圳证管费");
            fb.setYssPub(pub);
            fb.getSetting();
			if (fb != null) {
				alFeeBeans.add(fb);
			}
            fb=new FeeBean();
            fb.setFeeCode("YSS_SZYH");
            fb.setFeeName("深圳印花税");
            fb.setYssPub(pub);
            fb.getSetting();
			if (fb != null) {
				alFeeBeans.add(fb);
			}
//            fb=new FeeBean();
//            fb.setFeeCode("YSS_SZFX");
//            fb.setFeeName("深圳股票风险金");
//            alFeeBeans.add(fb);
            if(feeAttribute.getSecuritySign().equals("B_GP")){//B股
            	fb=new FeeBean();
                fb.setFeeCode("YSS_SZJSF");
                fb.setFeeName("深圳结算费");
                fb.setYssPub(pub);
                fb.getSetting();
    			if (fb != null) {
    				alFeeBeans.add(fb);
    			}
            }         
            double dTotalFee=0;
            for (int i = 0; i < alFeeBeans.size(); i++) {
                fb = (FeeBean) alFeeBeans.get(i);       
                double dFeeMoney=0;
                if(fb.getFeeCode().equalsIgnoreCase("YSS_SZGH")){
                	dFeeMoney=feeAttribute.getFGhf();
                }else if(fb.getFeeCode().equalsIgnoreCase("YSS_SZJS")){
                	dFeeMoney=feeAttribute.getFJsf();
                }else if(fb.getFeeCode().equalsIgnoreCase("YSS_SZZG")){
                	dFeeMoney=feeAttribute.getFZgf();
                }else if(fb.getFeeCode().equalsIgnoreCase("YSS_SZYJ")){
                	dFeeMoney=feeAttribute.getFYj();
                }else if(fb.getFeeCode().equalsIgnoreCase("YSS_SZYH")){
                	dFeeMoney=feeAttribute.getFYhs();
                }else if(fb.getFeeCode().equalsIgnoreCase("YSS_SZFX")){
                	dFeeMoney=feeAttribute.getFfxj();
                }else if(fb.getFeeCode().equalsIgnoreCase("YSS_SZJSF")){
                	dFeeMoney=feeAttribute.getFJsf();
                }
                dTotalFee = YssD.add(dTotalFee, dFeeMoney);
                bufShow.append(fb.getFeeName()).append("\t");
                bufShow.append(format.format(dFeeMoney)).append(YssCons.
                    YSS_LINESPLITMARK);
                bufAll.append(fb.getFeeCode()).append(YssCons.
                    YSS_ITEMSPLITMARK2);
                bufAll.append(fb.getFeeName()).append(YssCons.
                    YSS_ITEMSPLITMARK2);
                bufAll.append(YssFun.formatNumber(dFeeMoney, "###0.##")).append(
                    YssCons.
                    YSS_ITEMSPLITMARK2);
                bufAll.append(fb.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (alFeeBeans.size() != 0) {
                bufShow.append("Total: ").append("\t");
                bufShow.append(format.format(dTotalFee)).append(YssCons.
                    YSS_LINESPLITMARK);

                bufAll.append("total").append(YssCons.YSS_ITEMSPLITMARK2);
                bufAll.append("Total: ").append(YssCons.YSS_ITEMSPLITMARK2);
                bufAll.append(YssFun.formatNumber(dTotalFee, "###0.##")).append(
                    YssCons.
                    YSS_ITEMSPLITMARK2);
                bufAll.append("").append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;     
    	}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    }
   
    /**
     * 拆分券商承担的费用数据 story 2092 by zhouwei 20120105
     * @param brokerBear String
     * @return ArrayList
     */
    private ArrayList splitBrokerBear(String brokerBear) {
        String[] brokerBears = brokerBear.split(","); //用逗号拆分数据
        ArrayList alBears = new ArrayList(); //新建ArrayList
        for (int i = 0; i < brokerBears.length; i++) {
            alBears.add(brokerBears[i]); //将费用代码添加到alBears中
        }
        return alBears; //返回储存费用代码的ArrayList
    }
    /**
     * getPartSetting
     *
     * @return String
     */
    public String getPartSetting() {
        return "";
    }

    /**
     * getSetting
     *
     * @return IBaseSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Para_Fee") +
                //" where FFeeCode = " + dbl.sqlString(this.feeCode)+" and FIsSettle=1";
                " where FFeeCode = " + dbl.sqlString(this.feeCode) + 
                " and Fcheckstate = 1 "; // 加上Fcheckstate条件 modify by wangzuochun  2010.09.11 MS01708    交易结算未结算中进行结算时会报错    QDV4建行2010年09月08日01_B    
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.feeCode = rs.getString("FFeeCode") + "";
                this.feeName = rs.getString("FFeeName") + "";
                this.feeType = rs.getString("FFeeType") + "";
                this.accountingWay = rs.getString("FAccountingWay") + "";
                this.roundCode = rs.getString("FRoundCode") + "";
                this.perExpCode = rs.getString("FPerExpCode") + "";
                //-------------2008.01.21 添加 蒋锦 将是否为清算金额字段传到前台-------------//
                this.isSettle = rs.getString("FIsSettle") + "";
                //----------------------------------------------------------------------//
                this.desc = rs.getString("FDesc");
                this.checkStateId = rs.getInt("FCheckState");

                this.assumeMan = rs.getString("FAssumeMan"); // add by leeyu 080702
                this.isSettle = rs.getString("FIsSettle"); // add by leeyu 080702
            }
            //------ add by wangzuochun 2010.09.11 MS01708    交易结算未结算中进行结算时会报错    QDV4建行2010年09月08日01_B    
            else{
            	this.feeCode = null;
            }
            //--------MS01708---------//
            return null;
        } catch (Exception e) {
            throw new YssException("获取费用信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
        return "";
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Fee"),
                               "FFeeCode",
                               this.feeCode, this.oldFeeCode);
    }

    /**
     * saveSetting
     * 新增、修改、删除、审核
     * @param btOper byte
     */
    /*   public void saveSetting(byte btOper) throws YssException {
          String strSql = "";
          boolean bTrans = false; //代表是否开始了事务
          Connection conn = dbl.loadConnection();
          try {
             if (btOper == YssCons.OP_ADD) {
                strSql = "insert into " + pub.yssGetTableName("Tb_Para_Fee") +
                      " (FFeeCode,FFeeName,FRoundCode,FPerExpCode,FFeeType,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                      " values(" + dbl.sqlString(this.feeCode) + "," +
                      dbl.sqlString(this.feeName) + "," +
                      dbl.sqlString(this.roundCode) + " ," +
                      dbl.sqlString(this.perExpCode) + "," +
                      this.feeType + "," +
                      dbl.sqlString(this.desc) + "," +
                      (pub.getSysCheckState() ? "0" : "1") + "," +
                      dbl.sqlString(this.creatorCode) + "," +
                      dbl.sqlString(this.creatorTime) + "," +
                      (pub.getSysCheckState() ? "' '" :
                       dbl.sqlString(this.creatorCode)) + ")";
             }
             else if (btOper == YssCons.OP_EDIT) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_Fee") +
                      " set FFeeCode = " +
                      dbl.sqlString(this.feeCode) + ", FFeeName = " +
                      dbl.sqlString(this.feeName) + " , FRoundCode = " +
                      dbl.sqlString(this.roundCode) + " , FPerExpCode = " +
                      dbl.sqlString(this.perExpCode) + ", FFeeType = " +
                      this.feeType + ", FDesc = " +
                      dbl.sqlString(this.desc) + ",FCheckState = " +
                      (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                      dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                      dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                      (pub.getSysCheckState() ? "' '" :
                       dbl.sqlString(this.creatorCode)) +
                      " where FFeeCode = " +
                      dbl.sqlString(this.oldFeeCode);
             }
             else if (btOper == YssCons.OP_DEL) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_Fee") +
                      " set FCheckState = " + this.checkStateId +
                      ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                      ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                      "'" +
                      " where FFeeCode = " + dbl.sqlString(this.feeCode);

             }
             else if (btOper == YssCons.OP_AUDIT) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_Fee") +
                      " set FCheckState = " +
                      this.checkStateId + ", FCheckUser = " +
                      dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                      YssFun.formatDatetime(new java.util.Date()) + "'" +
                      " where FFeeCode = " + dbl.sqlString(this.feeCode);
             }
             conn.setAutoCommit(false);
             bTrans = true;
             dbl.executeSql(strSql);
             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
          }
          catch (Exception e) {
             throw new YssException("更新费用设置信息出错", e);
          }
          finally {
             dbl.endTransFinal(conn, bTrans);
          }
       }*/
    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_Fee") +
                " (FFeeCode,FFeeName,FRoundCode,FPerExpCode,FFeeType,FAccountingWay,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FIsSettle,FAssumeMan)" +
                " values(" + dbl.sqlString(this.feeCode) + "," +
                dbl.sqlString(this.feeName) + "," +
                dbl.sqlString(this.roundCode) + " ," +
                dbl.sqlString(this.perExpCode) + "," +
                this.feeType + "," +
                this.accountingWay + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '," :
                 dbl.sqlString(this.creatorCode) + ",") + //20130822 modified by liubo.Bug #9136.之前这句语句缺个逗号
                this.isSettle + "," +
                this.assumeMan + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加费用设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_Fee") +
                " set FFeeCode = " +
                dbl.sqlString(this.feeCode) + ", FFeeName = " +
                dbl.sqlString(this.feeName) + " , FRoundCode = " +
                dbl.sqlString(this.roundCode) + " , FPerExpCode = " +
                dbl.sqlString(this.perExpCode) + ", FFeeType = " +
                this.feeType + ", FAccountingWay = " +
                this.accountingWay + ", FDesc = " +
                dbl.sqlString(this.desc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                ",FIsSettle =" + this.isSettle +
                ",FAssumeMan =" + this.assumeMan + //lzp  修改  2007。12。6   不需要转换，转换DB2报错
                " where FFeeCode = " +
                dbl.sqlString(this.oldFeeCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改费用设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除数据，即是放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_Fee") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FFeeCode = " + dbl.sqlString(this.feeCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除费用设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**修改时间：2008年3月20号
     *  修改人：单亮
     *  原方法功能：只能处理期间信息的审核和未审核的单条信息。
     *  新方法功能：可以处理期间信息审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     */
    public void checkSetting() throws YssException {
        //修改前的代码
//    String strSql = "";
//    boolean bTrans = false; //代表是否开始了事务
//    Connection conn = dbl.loadConnection();
//    try {
//       strSql = "update " + pub.yssGetTableName("Tb_Para_Fee") +
//               " set FCheckState = " +
//               this.checkStateId + ", FCheckUser = " +
//               dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
//               YssFun.formatDatetime(new java.util.Date()) + "'" +
//               " where FFeeCode = " + dbl.sqlString(this.feeCode);
//       conn.setAutoCommit(false);
//       bTrans = true;
//       dbl.executeSql(strSql);
//       conn.commit();
//       bTrans = false;
//       conn.setAutoCommit(true);
//    }
//
//    catch (Exception e) {
//       throw new YssException("审核费用设置信息出错", e);
//    }
//    finally {
//       dbl.endTransFinal(conn, bTrans);
//    }
        //修改后的代码
//---------------------------
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Para_Fee") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FFeeCode = " + dbl.sqlString(this.feeCode);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }

            }
            //sRecycled如果sRecycled为空，而feeCode不为空，则按照feeCode来执行sql语句
            else if (feeCode != "" && feeCode != null) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_Fee") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FFeeCode = " + dbl.sqlString(this.feeCode);
                //执行sql语句
                dbl.executeSql(strSql);

            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        }

        catch (Exception e) {
            throw new YssException("审核费用设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
//---------------------------------
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            strSql = "select a.*, f.FVocName as FFeeTypeValue, b.FRoundName, d.FUserName as FCreatorName,e.FUserName as FCheckUserName" +
                ",c.FPerExpName" +
                " from " + pub.yssGetTableName("Tb_Para_Fee") + " a " +
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState = 1) b on a.FRoundCode = b.FRoundCode" +
                " left join (select FFormulaCode,FFormulaName as FPerExpName from " +
                pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState = 1) c on a.FPerExpCode = c.FFormulaCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " left join Tb_Fun_Vocabulary f on " + dbl.sqlToChar("a.FFeeType") + " = f.FVocCode and f.FVocTypeCode = " + //lzp
                dbl.sqlString(YssCons.YSS_FEE_FEETYPE) +
                " where a.FCheckState = 1 order by a.FCheckState,a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FFeeName") + "").trim()).append(
                    "\t");
                bufShow.append(YssFun.left( (rs.getString("FDesc") + "").trim(), 40)).
                    append(YssCons.YSS_LINESPLITMARK);

                this.feeCode = rs.getString("FFeeCode") + "";
                this.feeName = rs.getString("FFeeName") + "";
                this.feeType = rs.getString("FFeeType") + "";
                this.roundCode = rs.getString("FRoundCode");
                this.roundName = rs.getString("FRoundName");
                this.perExpCode = rs.getString("FPerExpCode");
                this.perExpName = rs.getString("FPerExpName");
                this.desc = rs.getString("FDesc");
                super.setRecLog(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取可用费用信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     * @throws YssException 
     */
    public String getOperValue(String sType) throws YssException {
    	//story 2092 by zhouwei 20120120 交易数据的费用计算变化：国内股票的费用计算参照接口导入
    	if(sType!=null && sType.equalsIgnoreCase("getgngpfee")){
    		return getGNGPfees();
    	}
    	//--------------end-
        return "";
    }
    //story 2092 by zhouwei 20120120 交易数据的费用计算变化：国内股票的费用计算参照接口导入
    public String getGNGPfees() throws YssException {
        ArrayList alFeeBeans = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        FeeBean fee = null;
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sHeader = "";
        double dFeeMoney;
        double dTotalFee = 0;
        YssFeeType feeType = null;
        DecimalFormat format = new DecimalFormat("#,##0.##");
        sHeader = this.getListView3Headers();
        FeeLinkBean FeeLink = null;
        try {
            BaseOperDeal baseOper = this.getSettingOper();            
            BaseLinkInfoDeal feeOper = (BaseLinkInfoDeal) pub.getOperDealCtx().getBean(
                "FeeLinkDeal");
            baseOper.setYssPub(pub);
            feeOper.setYssPub(pub);
            FeeLink = new FeeLinkBean();
            FeeLink.setSecurityCode(this.securityCode);
            FeeLink.setTradeTypeCode(this.tradeType);
            FeeLink.setPortCode(this.portCode);
            FeeLink.setBrokerCode(this.brokerCode);
            //story 2092 by zhouwei 20120105 交易席位
            FeeLink.setTradeSeatCode(this.tradeSeatCode);
            //当为国内股票业务时，上海过户费，上海与深圳佣金等按照国内接口处理的方法来计算
            if(checkSecurityType().equalsIgnoreCase("CG"))//上交所
            {
            	return getFeeInfosOfCGEQ();
            }else if(checkSecurityType().equalsIgnoreCase("CS")){//深交所
            	return getFeeInfosOfCSEQ();
            }
            //--------------end-------------
            feeOper.setLinkAttr(FeeLink);
            alFeeBeans = feeOper.getLinkInfoBeans();
            if (alFeeBeans != null) {
                feeType = new YssFeeType();
                feeType.setMoney(this.sumMoney);
                feeType.setInterest(this.interest);
                feeType.setAmount(this.amount);
                feeType.setCost(this.cost);
                feeType.setIncome(this.income);
                feeType.setFee(this.fee);

                for (int i = 0; i < alFeeBeans.size(); i++) {
                    fee = (FeeBean) alFeeBeans.get(i);
                    dFeeMoney = baseOper.calFeeMoney(feeType, fee);
                    //dFeeMoney = baseOper.calMoneyByPerExp(fee.getPerExpCode(),
                    //    fee.getRoundCode(), sumMoney);
                    dTotalFee = YssD.add(dTotalFee, dFeeMoney);
                    bufShow.append(fee.getFeeName()).append("\t");
                    bufShow.append(format.format(dFeeMoney)).append(YssCons.
                        YSS_LINESPLITMARK);

//               this.feeCode = fee.getFeeCode();
//               this.feeType = fee.getFeeType();
//               this.roundCode = fee.getRoundCode();
//               this.perExpCode = fee.getPerExpCode();
//               this.accountingWay = fee.getAccountingWay();

                    bufAll.append(fee.getFeeCode()).append(YssCons.
                        YSS_ITEMSPLITMARK2);
                    bufAll.append(fee.getFeeName()).append(YssCons.
                        YSS_ITEMSPLITMARK2);
                    bufAll.append(YssFun.formatNumber(dFeeMoney, "###0.##")).append(
                        YssCons.
                        YSS_ITEMSPLITMARK2);
                    bufAll.append(fee.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }
                if (alFeeBeans.size() != 0) {
                    bufShow.append("Total: ").append("\t");
                    bufShow.append(format.format(dTotalFee)).append(YssCons.
                        YSS_LINESPLITMARK);

                    bufAll.append("total").append(YssCons.YSS_ITEMSPLITMARK2);
                    bufAll.append("Total: ").append(YssCons.YSS_ITEMSPLITMARK2);
                    bufAll.append(YssFun.formatNumber(dTotalFee, "###0.##")).append(
                        YssCons.
                        YSS_ITEMSPLITMARK2);
                    bufAll.append("").append(YssCons.YSS_LINESPLITMARK);
                }
                if (bufShow.toString().length() > 2) {
                    sShowDataStr = bufShow.toString().substring(0,
                        bufShow.toString().length() - 2);
                }

                if (bufAll.toString().length() > 2) {
                    sAllDataStr = bufAll.toString().substring(0,
                        bufAll.toString().length() - 2);
                }
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            //2008.2.13 修改 蒋锦 直接抛出 e 中的异常消息。
            throw new YssException(e.getMessage());
        }
    }
    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        FeeBean befEditBean = new FeeBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*, f.FVocName as FFeeTypeValue, b.FRoundName, d.FUserName as FCreatorName,e.FUserName as FCheckUserName" +
                ",c.FPerExpName" +
                " from " + pub.yssGetTableName("Tb_Para_Fee") + " a " +
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState = 1) b on a.FRoundCode = b.FRoundCode" +
                " left join (select FFormulaCode,FFormulaName as FPerExpName from " +
                pub.yssGetTableName("Tb_Para_Performula") +
                " where FCheckState = 1) c on a.FPerExpCode = c.FFormulaCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " left join Tb_Fun_Vocabulary f on " + dbl.sqlToChar("a.FFeeType") + " = f.FVocCode and f.FVocTypeCode = " + // lzp
                dbl.sqlString(YssCons.YSS_FEE_FEETYPE) +
                " where  a.FFeeCode =" + dbl.sqlString(this.oldFeeCode) +
                " order by a.FCheckState,a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.feeCode = rs.getString("FFeeCode") + "";
                befEditBean.feeName = rs.getString("FFeeName") + "";
                befEditBean.feeType = rs.getString("FFeeType") + "";
                befEditBean.roundCode = rs.getString("FRoundCode");
                befEditBean.roundName = rs.getString("FRoundName");
                befEditBean.perExpCode = rs.getString("FPerExpCode");
                befEditBean.perExpName = rs.getString("FPerExpName");
                befEditBean.desc = rs.getString("FDesc");
                befEditBean.accountingWay = rs.getString("FAccountingWay");
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    /**
     * 从回收站删除数据，即是彻底删除
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_Para_Fee") +
                        " where FFeeCode = " + dbl.sqlString(this.feeCode);

                    dbl.executeSql(strSql);
                }

            }
            //sRecycled如果sRecycled为空，而feeCode不为空，则按照feeCode来执行sql语句
            else if (feeCode != "" && feeCode != null) {
                strSql = "delete from " + pub.yssGetTableName("Tb_Para_Fee") +
                    " where FFeeCode = " + dbl.sqlString(this.feeCode);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
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
