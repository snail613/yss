package com.yss.main.operdata;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.commeach.*;
import com.yss.dsub.*;
import com.yss.main.cashmanage.*;
import com.yss.main.dao.*;
import com.yss.main.operdeal.*;
import com.yss.manager.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;

/**
 *
 * <p>Title: 远期外汇交易数据表</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: yss</p>
 * @author not attributable
 * @version 1.0
 */

public class ForwardTradeBean
    extends BaseDataSettingBean implements IDataSetting {
    private String fNum = "";
    private String SecurityCode = "";
    private String SecurityName = "";
    private Date tradeDate;
    private String tradeTime = "00:00:00";
    private Date matureDate;
    private Date settleDate;
    private String settleTime = "00:00:00";
    /**shashijie 2011.04.08 STORY #670 外汇远期交易到期不交收，用反向交易进行平仓，只交收净损益，实现净收益的交收日可选择*/
    private Date tranDate;//交收日期
    private String offCury = "";  //add by fangjiang 2011.09.29 story 1534
    private String offCuryName = ""; //add by fangjiang 2011.09.29 story 1534
    public String getOffCuryName() {
		return offCuryName;
	}

	public void setOffCuryName(String offCuryName) {
		this.offCuryName = offCuryName;
	}

	public String getOffCury() {
		return offCury;
	}

	public void setOffCury(String offCury) {
		this.offCury = offCury;
	}

	public Date getTranDate() {
		return tranDate;
	}

	public void setTranDate(Date tranDate) {
		this.tranDate = tranDate;
	}

	/**end*/
    private double tradeAmount;
    private double tradePrice;
    private BigDecimal trustPrice;//shashijie 2011.3.9 STORY #519 希望根据参数设置远期外汇交易界面价格显示位数
    private BigDecimal matureMoney;//shashijie 2011.3.9 STORY #519 希望根据参数设置远期外汇交易界面价格显示位数
    private double bailMoney;
    private double settleMoney;
    private double feeMoney1;
    private double feeMoney2;
    private double feeMoney3;
    private String bAccDesc = "";
    private String sAccDesc = "";
    private String bailAccDesc = "";
    private String bailInAccDesc = "";
    private String bailOutAccDesc = "";
    private String feeAccDesc1 = "";
    private String feeAccDesc2 = "";
    private String feeAccDesc3 = "";
    private String desc = "";

    private String attrClsCode = "";
    private String attrClsName = "";
    private String catTypeCode = "";
    private String catTypeName = "";

    private String oldFNum = "";
    private ForwardTradeBean filterType;

    private String allSetDatas = "";

    private String analysisCode1 = "";
    private String analysisCode2 = "";
    private String analysisCode3 = "";

    private String analysisName1 = "";
    private String analysisName2 = "";
    private String analysisName3 = "";

    private String portCode = "";
    private String portName = "";
    //fanghaoln MS01305 QDV4华夏2010年06月13日02_A 20100628 
    private String fAffCorpCode = "";
    private String fAffCorpName = "";
    //------------------------------end -----MS01305--------------------------------------
    
    //add by fangjiang 20101.01.12 STORY #262 #393
    private String tradeType = "";
    private String offNum = "";
    //------------------
    
    //add by fangjiang 2011.10.12 BUG 2897
    private BigDecimal amount = new BigDecimal(0.0);
    private BigDecimal cost = new BigDecimal(0.0);
    //------------------
    
    public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
    
    private String isOnlyColumn = "0";
    
	private java.util.Date beginDate;
    private java.util.Date endDate;

    private Hashtable htPubAccInfo = null;
    private YssTradeAcc tradeacc = null;

    private String sRecycled = null; //保存未解析前的字符串

    public void setAllSetDatas(String allSetDatas) {
        this.allSetDatas = allSetDatas;
    }

    public String getAllSetDatas() {
        return allSetDatas;
    }

    public String getFeeAccDesc2() {
        return feeAccDesc2;
    }

    public String getFNum() {
        return fNum;
    }

    public double getFeeMoney1() {
        return feeMoney1;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public double getFeeMoney2() {
        return feeMoney2;
    }

    public double getSettleMoney() {
        return settleMoney;
    }

    public String getSettleTime() {
        return settleTime;
    }

    public String getSecurityName() {
        return SecurityName;
    }

    public Date getMatureDate() {
        return matureDate;
    }

    public double getTradePrice() {
        return tradePrice;
    }

    public BigDecimal getTrustPrice() {
        return trustPrice;
    }

    public String getSecurityCode() {
        return SecurityCode;
    }

    public double getFeeMoney3() {
        return feeMoney3;
    }

    public String getDesc() {
        return desc;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public String getFeeAccDesc3() {
        return feeAccDesc3;
    }

    public String getSAccDesc() {
        return sAccDesc;
    }

    public String getFeeAccDesc1() {
        return feeAccDesc1;
    }

    public String getBailOutAccDesc() {
        return bailOutAccDesc;
    }

    public double getTradeAmount() {
        return tradeAmount;
    }

    public String getBAccDesc() {
        return bAccDesc;
    }

    public double getBailMoney() {
        return bailMoney;
    }

    public String getBailInAccDesc() {
        return bailInAccDesc;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public String getBailAccDesc() {
        return bailAccDesc;
    }

    public void setMatureMoney(BigDecimal matureMoney) {
        this.matureMoney = matureMoney;
    }

    public void setFeeAccDesc2(String feeAccDesc2) {
        this.feeAccDesc2 = feeAccDesc2;
    }

    public void setFNum(String fNum) {
        this.fNum = fNum;
    }

    public void setFeeMoney1(double feeMoney1) {
        this.feeMoney1 = feeMoney1;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public void setFeeMoney2(double feeMoney2) {
        this.feeMoney2 = feeMoney2;
    }

    public void setSettleMoney(double settleMoney) {
        this.settleMoney = settleMoney;
    }

    public void setSettleTime(String settleTime) {
        this.settleTime = settleTime;
    }

    public void setSecurityName(String SecurityName) {
        this.SecurityName = SecurityName;
    }

    public void setMatureDate(Date matureDate) {
        this.matureDate = matureDate;
    }

    public void setTradePrice(double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public void setTrustPrice(BigDecimal trustPrice) {
        this.trustPrice = trustPrice;
    }

    public void setSecurityCode(String SecurityCode) {
        this.SecurityCode = SecurityCode;
    }

    public void setFeeMoney3(double feeMoney3) {
        this.feeMoney3 = feeMoney3;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public void setFeeAccDesc3(String feeAccDesc3) {
        this.feeAccDesc3 = feeAccDesc3;
    }

    public void setSAccDesc(String sAccDesc) {
        this.sAccDesc = sAccDesc;
    }

    public void setFeeAccDesc1(String feeAccDesc1) {
        this.feeAccDesc1 = feeAccDesc1;
    }

    public void setBailOutAccDesc(String bailOutAccDesc) {
        this.bailOutAccDesc = bailOutAccDesc;
    }

    public void setTradeAmount(double tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public void setBAccDesc(String bAccDesc) {
        this.bAccDesc = bAccDesc;
    }

    public void setBailMoney(double bailMoney) {
        this.bailMoney = bailMoney;
    }

    public void setBailInAccDesc(String bailInAccDesc) {
        this.bailInAccDesc = bailInAccDesc;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public void setBailAccDesc(String bailAccDesc) {
        this.bailAccDesc = bailAccDesc;
    }

    public void setOldFNum(String oldFNum) {
        this.oldFNum = oldFNum;
    }

    public void setFilterType(ForwardTradeBean filterType) {
        this.filterType = filterType;
    }

    public void setTradeacc(YssTradeAcc tradeacc) {
        this.tradeacc = tradeacc;
    }

    public void setAnalysisCode3(String analysisCode3) {
        this.analysisCode3 = analysisCode3;
    }

    public void setAnalysisCode2(String analysisCode2) {
        this.analysisCode2 = analysisCode2;
    }

    public void setAnalysisCode1(String analysisCode1) {
        this.analysisCode1 = analysisCode1;
    }

    public void setAnalysisName2(String analysisName2) {
        this.analysisName2 = analysisName2;
    }

    public void setAnalysisName3(String analysisName3) {
        this.analysisName3 = analysisName3;
    }

    public void setAnalysisName1(String analysisName1) {
        this.analysisName1 = analysisName1;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setAttrClsName(String attrClsName) {
        this.attrClsName = attrClsName;
    }

    public void setCatTypeCode(String catTypeCode) {
        this.catTypeCode = catTypeCode;
    }

    public void setAttrClsCode(String attrClsCode) {
        this.attrClsCode = attrClsCode;
    }

    public void setCatTypeName(String catTypeName) {
        this.catTypeName = catTypeName;
    }

    public BigDecimal getMatureMoney() {
        return matureMoney;
    }

    public String getOldFNum() {
        return oldFNum;
    }

    public ForwardTradeBean getFilterType() {
        return filterType;
    }

    public YssTradeAcc getTradeacc() {
        return tradeacc;
    }

    public String getAnalysisCode3() {
        return analysisCode3;
    }

    public String getAnalysisCode2() {
        return analysisCode2;
    }

    public String getAnalysisCode1() {
        return analysisCode1;
    }

    public String getAnalysisName2() {
        return analysisName2;
    }

    public String getAnalysisName3() {
        return analysisName3;
    }

    public String getAnalysisName1() {
        return analysisName1;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getPortName() {
        return portName;
    }

    public String getIsOnlyColumn() {
        return isOnlyColumn;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public String getAttrClsName() {
        return attrClsName;
    }

    public String getCatTypeCode() {
        return catTypeCode;
    }

    public String getAttrClsCode() {
        return attrClsCode;
    }

    public String getCatTypeName() {
        return catTypeName;
    }

    public ForwardTradeBean() {
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.allSetDatas = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.fNum = reqAry[0];
            this.SecurityCode = reqAry[1];
            if (YssFun.isDate(reqAry[2])) {
                this.tradeDate = YssFun.toDate(reqAry[2]);
            }
            this.tradeTime = reqAry[3];
            if (YssFun.isDate(reqAry[4])) {
                this.matureDate = YssFun.toDate(reqAry[4]);
            }
            this.settleTime = reqAry[5];
            if (YssFun.isDate(reqAry[6])) {
                this.settleDate = YssFun.toDate(reqAry[6]);
            }
            this.tradeAmount = YssFun.toDouble(reqAry[7]);
            this.tradePrice = YssFun.toDouble(reqAry[8]);
            if (YssFun.isNumeric(reqAry[9])){//YssFun.toDouble(reqAry[9]);//shashijie 2011.3.9 STORY #519 希望根据参数设置远期外汇交易界面价格显示位数
            	this.trustPrice = new BigDecimal(reqAry[9]);
            } else { 
            	this.trustPrice = new BigDecimal("0"); 
            }
            if (YssFun.isNumeric(reqAry[10])) {//YssFun.toDouble(reqAry[10]);//shashijie 2011.3.9 STORY #519 希望根据参数设置远期外汇交易界面价格显示位数
            	this.matureMoney = new BigDecimal(reqAry[10]);
            } else { 
            	this.matureMoney = new BigDecimal("0"); 
            }
            this.bailMoney = YssFun.toDouble(reqAry[11]);
            this.settleMoney = YssFun.toDouble(reqAry[12]);
            this.feeMoney1 = YssFun.toDouble(reqAry[13]);
            this.feeMoney2 = YssFun.toDouble(reqAry[14]);
            this.feeMoney3 = YssFun.toDouble(reqAry[15]);
            this.bAccDesc = reqAry[16];
            this.sAccDesc = reqAry[17];
            this.bailAccDesc = reqAry[18];
            this.bailInAccDesc = reqAry[19];
            this.bailOutAccDesc = reqAry[20];
            this.feeAccDesc1 = reqAry[21];
            this.feeAccDesc2 = reqAry[22];
            this.feeAccDesc3 = reqAry[23];
            this.desc = reqAry[24];
            this.checkStateId = YssFun.toInt(reqAry[25]);
            this.oldFNum = reqAry[26];
            this.analysisCode1 = reqAry[27];
            this.analysisCode2 = reqAry[28];
            this.analysisCode3 = reqAry[29];
            this.portCode = reqAry[30];
            this.isOnlyColumn = reqAry[31];
            if (YssFun.isDate(reqAry[32])) {
                this.beginDate = YssFun.toDate(reqAry[32]);
            }
            if (YssFun.isDate(reqAry[33])) {
                this.endDate = YssFun.toDate(reqAry[33]);
            }
            //fanghaoln MS01305 QDV4华夏2010年06月13日02_A 20100628 
            this.fAffCorpCode = reqAry[34];
            this.fAffCorpName = reqAry[35];
            //------------------------------end -----MS01305--------------------------------------
            
            //add by fangjiang 20101.01.12 STORY #262 #393
            this.tradeType = reqAry[36];
            this.offNum = reqAry[37];
            //----------------
            
            /**shashijie 2011.04.08 STORY #670 */
            if (YssFun.isDate(reqAry[38])) {
            	this.tranDate = YssFun.toDate(reqAry[38]);
			}
            /**end*/
                        
            this.offCury = reqAry[39]; //add by fangjiang 2011.09.29 story 1534
            
            //add by fangjiang 2011.10.12 BUG 2897
            if("21".equalsIgnoreCase(this.tradeType)){
            	if (getCuryTradeType(this.fNum).equals("1")){ //'0'表示交易货币=买入货币，'1'表示交易货币=卖出货币
            		this.amount = new BigDecimal(this.tradeAmount);
            		this.cost = calAmountOrCost(this.offNum, this.amount, "1");            		
            	}else{
            		this.cost = this.matureMoney;
            		this.amount = calAmountOrCost(this.offNum, this.cost, "0");
            	}
            }
            //-----------------
            
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new ForwardTradeBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析外汇交易数据出错", e);
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.fNum);
        buf.append("\t");
        buf.append(this.SecurityCode);
        buf.append("\t");
        buf.append(this.SecurityName);
        buf.append("\t");
        buf.append(YssFun.formatDate(this.tradeDate));
        buf.append("\t");
        buf.append(this.tradeTime);
        buf.append("\t");
        buf.append(YssFun.formatDate(this.matureDate));
        buf.append("\t");
        buf.append(this.settleTime);
        buf.append("\t");
        buf.append(YssFun.formatDate(this.settleDate));
        buf.append("\t");
        buf.append(this.tradeAmount);
        buf.append("\t");
        buf.append(this.tradePrice);
        buf.append("\t");
        buf.append(this.trustPrice);
        buf.append("\t");
        buf.append(this.matureMoney);
        buf.append("\t");
        buf.append(this.bailMoney);
        buf.append("\t");
        buf.append(this.settleMoney);
        buf.append("\t");
        buf.append(this.feeMoney1);
        buf.append("\t");
        buf.append(this.feeMoney2);
        buf.append("\t");
        buf.append(this.feeMoney3);
        buf.append("\t");
        buf.append(this.bAccDesc);
        buf.append("\t");
        buf.append(this.sAccDesc);
        buf.append("\t");
        buf.append(this.bailAccDesc);
        buf.append("\t");
        buf.append(this.bailInAccDesc);
        buf.append("\t");
        buf.append(this.bailOutAccDesc);
        buf.append("\t");
        buf.append(this.feeAccDesc1);
        buf.append("\t");
        buf.append(this.feeAccDesc2);
        buf.append("\t");
        buf.append(this.feeAccDesc3);
        buf.append("\t");
        buf.append(this.desc);
        buf.append("\t");
        buf.append(this.analysisCode1).append("\t");
        buf.append(this.analysisName1).append("\t");
        buf.append(this.analysisCode2).append("\t");
        buf.append(this.analysisName2).append("\t");
        buf.append(this.analysisCode3).append("\t");
        buf.append(this.analysisName3).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        //fanghaoln MS01305 QDV4华夏2010年06月13日02_A 20100628 
        buf.append(this.fAffCorpCode).append("\t");
        buf.append(this.fAffCorpName).append("\t");
        //------------------------------end -----MS01305--------------------------------------
        
        //add by fangjiang 20101.01.12 STORY #262 #393
        buf.append(this.tradeType).append("\t");
        buf.append(this.offNum).append("\t");
        //-----------------------
        /**shashijie 2011.04.08 STORY #670 */
        buf.append(this.tranDate).append("\t");
        /**end*/
        buf.append(this.offCury).append("\t"); //add by fangjiang 2011.09.29 story 1534
        buf.append(this.offCuryName).append("\t");//add by fangjiang 2011.09.29 story 1534
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Data_ForwardTrade"),
                               "FNum",
                               this.fNum,
                               this.oldFNum);

    }

    public String getAllSetting() {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.filterType != null) {
                if (this.filterType.isOnlyColumn.equalsIgnoreCase("1")) {
                    sResult = " where 1=2";
                    return sResult;
                }
                sResult = " where 1=1 ";
                if (this.filterType.fNum != null&&this.filterType.fNum.length() != 0) {
                    sResult = sResult + " and a.FNum like '" +
                        filterType.fNum.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.SecurityCode != null&&this.filterType.SecurityCode.length() != 0
                    ) {
                    sResult = sResult + " and a.FSecurityCode like '" +
                        filterType.SecurityCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.portCode != null&&this.filterType.portCode.length() != 0 ) {
                    sResult = sResult + " and a.FPortCode like '" +
                        filterType.portCode.replaceAll("'", "''") + "%'";
                }
              //fanghaoln MS01305 QDV4华夏2010年06月13日02_A 20100628
                if (this.filterType.fAffCorpCode != null&&this.filterType.fAffCorpCode.length() != 0 ) {
                        sResult = sResult + " and a.FFAffCorpCode like '" +
                            filterType.fAffCorpCode.replaceAll("'", "''") + "%'";
                    }//增加对手方筛选条件
              //------------------------------end -----MS01305--------------------------------------
                if (this.filterType.tradeDate != null &&
                    !YssFun.formatDate(filterType.tradeDate).equals("9998-12-31")) {
                    sResult = sResult + " and a.FTradeDate = " +
                        dbl.sqlDate(this.filterType.tradeDate);
                }
                if (this.filterType.matureDate != null &&
                    !YssFun.formatDate(filterType.matureDate).equals("9998-12-31")) {
                    sResult = sResult + " and a.FMatureDate = " +
                        dbl.sqlDate(this.filterType.matureDate);
                }
                if (this.filterType.settleDate != null &&
                    !YssFun.formatDate(filterType.settleDate).equals("9998-12-31")) {
                    sResult = sResult + " and a.FSettleDate = " +
                        dbl.sqlDate(this.filterType.settleDate);
                }
                if (this.filterType.endDate != null &&
                    !YssFun.formatDate(filterType.endDate).equals("9998-12-31")) {
                    if (this.filterType.beginDate != null &&
                        !YssFun.formatDate(filterType.beginDate).equals(
                            "9998-12-31")) {
                        sResult += " and a.FTradeDate between " +
                            dbl.sqlDate(this.filterType.beginDate) +
                            " and " + dbl.sqlDate(this.filterType.endDate);
                    }
                }                
                //add by fangjiang 20101.01.12 STORY #262 #393
                if(this.filterType.getTradeType() != null && this.filterType.getTradeType().trim().length()>0) {	
    				if(!this.filterType.getTradeType().equalsIgnoreCase("ALL")) {
    					sResult += " and a.FTradeType = " + dbl.sqlString(this.filterType.getTradeType());
    				}
    			}
                if (this.filterType.offNum != null&&this.filterType.offNum.length() != 0) {
                    sResult = sResult + " and a.FOffNum = " + dbl.sqlString(this.offNum);              
                }
                //------------------
                
            }
        } catch (Exception e) {
            throw new YssException("筛选外汇交易数据出错", e);
        }
        return sResult;
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if (this.filterType.isOnlyColumn.equalsIgnoreCase("1")) {
            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\f" + yssPageInationBean.buildRowStr()+"\r\f";//QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("ForwardTrade");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
            while (rs.next()) {
                setSecurityAttr(rs);
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\f" + yssPageInationBean.buildRowStr()+"\r\f";//QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
        } catch (Exception e) {
            throw new YssException("获取外汇交易数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }

    public String getListViewData1() throws YssException {
        String strSql = "";
        String sAry[] = null;

        sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Security); //获得分析代码
        strSql = "select a.*, " +
            " b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FSecurityName as FSecurityName,e.FPortName as FPortName, f.FCuryName as FCuryName" +
            sAry[0] +
            " from " + pub.yssGetTableName("Tb_Data_ForwardTrade") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FSecurityCode,FSecurityName from " +
            pub.yssGetTableName("Tb_Para_Security") +
            " where FCheckState = 1 ) d on d.FSecurityCode = a.FSecurityCode" +
            
            //------ modify by wangzuochun 2010.08.23  MS01617    远期外汇交易数据,新建数据时，产生两笔一样的数据    QDV4赢时胜(上海开发部)2010年08月13日03_B    
            " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            pub.yssGetTableName("Tb_Para_Portfolio") +
//            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
            " select FPortCode, FPortName, FStartDate, FPortCury from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            pub.yssGetTableName("Tb_Para_Portfolio") + 
            " where FCheckState = 1) e on a.FPortCode = e.FPortCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //------------------------------MS01617----------------------------//
            " left join " + pub.yssGetTableName("Tb_para_currency") + " f on a.FOffCury = f.FCuryCode " +
            sAry[1] +
            buildFilterSql() +
            " order by FNum";

        return this.builderListViewData(strSql);
    }

    
    /**
     * Story #2395 远期交割信息的查询, 客户希望可以从界面中查看某笔交易的交割信息
     * add by zhangjun 2012-04-18     * 
     */
    public String getListViewData4() throws YssException {
    	String sHeader = "";
    	String strSql = "";
    	ResultSet rs = null;
    	StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		String sShowDataStr = "";
		String sAllDataStr = "";
    	try {
    		sHeader = "编号\t远期品种\t交易日期\t到期日期\t组合代码\t交易数量\t交易价格\t到期金额";
    		String sAry[] = null;
	        sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Security); //获得分析代码
	        strSql = " select a.FNum, a.FSecurityCode,a.FPortCode,a.FTradeDate,a.FMatureDate," +
	        		 " a.FTradeAmount ,a.FTradePrice,a.FMatureMoney from " +	        		
					 " (select * from "	+ pub.yssGetTableName("Tb_Data_ForwardTrade")+
					 " where FCheckState = 1 ) a ";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				
				bufShow.append((rs.getString("FNum") + "").trim())
						.append("\t");
				
				bufShow.append((rs.getString("FSecurityCode") + "").trim())
				.append("\t");
				
				bufShow.append((rs.getString("FTradeDate") + "").trim())
				.append("\t");
				
				bufShow.append((rs.getString("FMatureDate") + "").trim())
						.append("\t");
				
				bufShow.append((rs.getString("FPortCode") + "").trim())
				.append("\t");
				
				bufShow.append((rs.getString("FTradeAmount") + "").trim())
				.append("\t");

				bufShow.append((rs.getString("FTradePrice") + "").trim())
						.append("\t");
				
				bufShow.append((rs.getString("FMatureMoney") + "").trim())
						.append("\t");
				
				bufShow.append(YssCons.YSS_LINESPLITMARK);
				
				setAttr(rs);
				
				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK);
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
    	}catch(Exception e){
    		throw new YssException("获取远期外汇交易编号出错！" + "\r\n" + e.getMessage(), e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    

    //modify by fangjiang 2011.01.12 STORY #262 #393
    public String getListViewData2() throws YssException {
    	String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		ResultSet rs = null;
		String strSql = "";
		String offNum = "";
		String tradeNum = "";
		String offLimit = "";
		String settleLimit = "";
		String sAccDesc = "";
		String bAccDesc = "";
		if(this.sAccDesc.length()>0){
			sAccDesc = this.sAccDesc.split("_")[0] + "_" + this.sAccDesc.split("_")[1] + "_BuyCap";
			bAccDesc = this.bAccDesc.split("_")[0] + "_" + this.bAccDesc.split("_")[1] + "_SellCap";
		}
		
		try {
			sHeader = "编号\t远期品种\t交易日期\t到期日期\t组合代码\t交易数量\t交易价格\t到期金额";
			
	        String sAry[] = null;
	        sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Security); //获得分析代码
	        //modify by huangqirong 2011-07-16  story #1353  增加部分平仓功能
	        //strSql = " select FOffNum from " + pub.yssGetTableName("Tb_Data_ForwardTrade") + " where FCheckState = 1 and FTradeType = '21' ";
	        
			strSql = "select a.FNum, a.FTradeAmount as TFTradeAmount,a.FSettleMoney as TFSettleMoney,b.FTradeAmount as PFTradeAmount,b.FSettleMoney as PFSettleMoney from "
					+ "(select * from "
					+ pub.yssGetTableName("Tb_Data_ForwardTrade")
					+ " where FCheckState = 1 and FTradeType = '20') a join (select sum(FTradeAmount) FTradeAmount,sum(FSettleMoney) FSettleMoney,FOffNum from "
					+ pub.yssGetTableName("Tb_Data_ForwardTrade")
					+ " where FCheckState = 1 and FTradeType = '21' group by FOffNum) b on a.FNum=b.FOffNum";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				if (this.getCuryTradeType(rs.getString("FNum").trim()).equals(
						"0")) {
					if (rs.getDouble("TFTradeAmount") == rs
							.getDouble("PFTradeAmount")) {
						offNum += rs.getString("FNum") + ",";
					}
				} else if (this.getCuryTradeType(rs.getString("FNum").trim())
						.equals("1")) {
					if (rs.getDouble("TFSettleMoney") == rs
							.getDouble("PFSettleMoney")) {
						offNum += rs.getString("FNum") + ",";
					}
				}
			}
			if(offNum.equals("")){
				offNum = "' '";
			}else{
				offNum = offNum.substring(0,offNum.length()-1);
			}
			offNum = operSql.sqlCodes(offNum);
			offLimit = " and FNum not in (" + offNum + ")";  //查询未被完全平仓的编号
			//---end---
			dbl.closeResultSetFinal(rs);
			
			strSql = " select FTradeNum from " + pub.yssGetTableName("Tb_Data_FwTradeSettle") + " where FCheckState = 1 ";
	        rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				tradeNum += operSql.sqlCodes(rs.getString("FTradeNum")) + ",";
			}
			if(tradeNum.equals("")){
				tradeNum = "' '";
			}else{
				tradeNum = tradeNum.substring(0,tradeNum.length()-1);
			}
			settleLimit = " and FNum not in (" + tradeNum + ")"; //查询未被交割的编号
			dbl.closeResultSetFinal(rs);
	        
	        strSql = "select a.*, " +
	            " b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FSecurityName as FSecurityName,e.FPortName as FPortName" +
	            sAry[0] +
	            " from " + pub.yssGetTableName("Tb_Data_ForwardTrade") + " a " +
	            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
	            " left join (select FSecurityCode,FSecurityName from " +
	            pub.yssGetTableName("Tb_Para_Security") +
	            " where FCheckState = 1 ) d on d.FSecurityCode = a.FSecurityCode" +
	              
	            " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
	            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//	            pub.yssGetTableName("Tb_Para_Portfolio") +
//	            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
	            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
	            " select FPortCode, FPortName, FStartDate, FPortCury from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
	            pub.yssGetTableName("Tb_Para_Portfolio") + 
	            " where FCheckState = 1) e on a.FPortCode = e.FPortCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
	            sAry[1] +
	            " where a.FMatureDate = " + dbl.sqlDate(this.matureDate) + 
	            " and a.FBACCDESC = " + dbl.sqlString(sAccDesc) + 
	            " and a.FSACCDESC = " + dbl.sqlString(bAccDesc) +
	            " and a.FCheckState = 1 and FTradeType = '20' " + offLimit + settleLimit +
	            " order by FNum ";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				
				bufShow.append((rs.getString("FNum") + "").trim())
						.append("\t");
				
				bufShow.append((rs.getString("FSecurityCode") + "").trim())
				.append("\t");
				
				bufShow.append((rs.getString("FTradeDate") + "").trim())
				.append("\t");
				
				bufShow.append((rs.getString("FMatureDate") + "").trim())
						.append("\t");
				
				bufShow.append((rs.getString("FPortCode") + "").trim())
				.append("\t");
				
				bufShow.append((rs.getString("FTradeAmount") + "").trim())
				.append("\t");

				bufShow.append((rs.getString("FTradePrice") + "").trim())
						.append("\t");
				
				bufShow.append((rs.getString("FMatureMoney") + "").trim())
						.append("\t");
				
				bufShow.append(YssCons.YSS_LINESPLITMARK);
				
				setPCAttr(rs);
				
				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK);
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
			throw new YssException("获取远期外汇信息出错" + "\r\n" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }
    
    //modify by fangjiang 2011.01.12 STORY #262 #393
    //modify by fangjiang 2012.02.06 bug 3793 
    public String getListViewData3() throws YssException {
    	String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		ResultSet rs = null;
		String strSql = "";
		String offNum = "";
		String num = "";
		String offLimit = "";
		String delaySettleLimit = "";
		try {
			sHeader = "编号\t远期品种\t交易日期\t到期日期\t组合代码\t交易数量\t交易价格\t到期金额";
			
			String sAry[] = null;
	        sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Security); //获得分析代码
	        //modify by huangqirong 2011-07-16  story #1353  增加部分平仓功能
	        //strSql = " select FOffNum from " + pub.yssGetTableName("Tb_Data_ForwardTrade") 
	        		// + " where FCheckState = 1 and FTradeType = '21' ";
	        strSql = "select a.FNum, a.FTradeAmount as TFTradeAmount,a.FSettleMoney as TFSettleMoney,b.FTradeAmount as PFTradeAmount,b.FSettleMoney as PFSettleMoney from "
				+ "(select * from "
				+ pub.yssGetTableName("Tb_Data_ForwardTrade")
				+ " where FCheckState = 1 and FTradeType = '20') a join (select sum(FTradeAmount) FTradeAmount,sum(FSettleMoney) FSettleMoney,FOffNum from "
				+ pub.yssGetTableName("Tb_Data_ForwardTrade")
				+ " where FCheckState = 1 and FTradeType = '21' group by FOffNum) b on a.FNum=b.FOffNum";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				if (this.getCuryTradeType(rs.getString("FNum").trim()).equals(
						"0")) {
					if (rs.getDouble("TFTradeAmount") == rs
							.getDouble("PFTradeAmount")) {
						offNum += rs.getString("FNum") + ",";
					}
				} else if (this.getCuryTradeType(rs.getString("FNum").trim())
						.equals("1")) {
					if (rs.getDouble("TFSettleMoney") == rs
							.getDouble("PFSettleMoney")) {
						offNum += rs.getString("FNum") + ",";
					}
				}
			}
			if(offNum.equals("")){
				offNum = "' '";
			}else{
				offNum = offNum.substring(0,offNum.length()-1);
			}
			offNum = operSql.sqlCodes(offNum);
			offLimit = " and FNum not in (" + offNum + ")";  //查询未被完全平仓的编号
			//---end---
			dbl.closeResultSetFinal(rs);
			
			strSql = " select a.FNum from (select FNum, FMatureDate from " + pub.yssGetTableName("Tb_Data_ForwardTrade")
					 + " where FCheckState = 1 and FMatureDate < " + dbl.sqlDate(this.matureDate) //前台界面上的交割日期 > 远期外汇交易的到期日期(即已到期)
					 + " ) a join (select FTradeNum, max(FSettleDate) as FSettleDate from " + pub.yssGetTableName("Tb_Data_FwTradeSettle")
					 + " where FCheckState = 1 group by FTradeNum) b on a.FNum = b.FTradeNum where a.FMatureDate <= b.FSettleDate";
	        rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				num += operSql.sqlCodes(rs.getString("FNum")) + ",";
			}
			if(num.equals("")){
				num = "' '";
			}else{
				num = num.substring(0,num.length()-1);
			}
			delaySettleLimit = " and (FMatureDate >= " + dbl.sqlDate(this.matureDate) + " or FNum in (" + num + "))"; //提前交割或展期交割的条件
			dbl.closeResultSetFinal(rs);
	        
	        strSql = "select * from " + pub.yssGetTableName("Tb_Data_ForwardTrade")
	                 + " where FCheckState = 1 and FTradeType = '20' " + offLimit + delaySettleLimit +
	 	            " order by FNum ";
	        
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				
				bufShow.append((rs.getString("FNum") + "").trim())
						.append("\t");
				
				bufShow.append((rs.getString("FSecurityCode") + "").trim())
				.append("\t");
				
				bufShow.append((rs.getString("FTradeDate") + "").trim())
				.append("\t");
				
				bufShow.append((rs.getString("FMatureDate") + "").trim())
						.append("\t");
				
				bufShow.append((rs.getString("FPortCode") + "").trim())
				.append("\t");
				
				bufShow.append((rs.getString("FTradeAmount") + "").trim())
				.append("\t");

				bufShow.append((rs.getString("FTradePrice") + "").trim())
						.append("\t");
				
				bufShow.append((rs.getString("FMatureMoney") + "").trim())
						.append("\t");
				
				bufShow.append(YssCons.YSS_LINESPLITMARK);
				
				setAttr(rs);
				
				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK);
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
			throw new YssException("获取远期外汇信息出错" + "\r\n" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }
    
    public void setAttr(ResultSet rs) throws SQLException, SQLException,
    	YssException {
	    this.fNum = rs.getString("FNum");
	    this.SecurityCode = rs.getString("FSecurityCode");
	    this.tradeDate = rs.getDate("FTradeDate");
	    this.matureDate = rs.getDate("FMatureDate");
	    this.portCode = rs.getString("FPortCode");
	    this.tradeAmount = rs.getDouble("FTradeAmount");
	    this.tradePrice = rs.getDouble("FTradePrice");
	    this.matureMoney = rs.getBigDecimal("FMatureMoney");
	}
    
    public void setPCAttr(ResultSet rs) throws SQLException, SQLException,
		YssException {
    	boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security"); //判断分析代码存不存在
        boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
        this.fNum = rs.getString("FNum");
        this.SecurityCode = rs.getString("FSecurityCode");
        this.SecurityName = rs.getString("FSecurityName");
        this.tradeDate = rs.getDate("FTradeDate");
        this.tradeTime = rs.getString("FTradeTime");
        this.matureDate = rs.getDate("FMatureDate");
        this.settleTime = rs.getString("FSettleTime");
        this.settleDate = rs.getDate("FSettleDate");
        this.tradeAmount = rs.getDouble("FTradeAmount");
        this.tradePrice = rs.getDouble("FTradePrice");
        this.trustPrice = rs.getBigDecimal("FTrustPrice");//shashijie 2011.3.9 STORY #519 希望根据参数设置远期外汇交易界面价格显示位数
        this.matureMoney = rs.getBigDecimal("FMatureMoney");//shashijie 2011.3.9 STORY #519 希望根据参数设置远期外汇交易界面价格显示位数
        this.bailMoney = rs.getDouble("FBailMoney");
        this.settleMoney = rs.getDouble("FSettleMoney");
        this.feeMoney1 = rs.getDouble("FFeeMoney1");
        this.feeMoney2 = rs.getDouble("FFeeMoney2");
        this.feeMoney3 = rs.getDouble("FFeeMoney3");
        this.bAccDesc = rs.getString("FBAccDesc");
        this.sAccDesc = rs.getString("FSAccDesc");
        this.bailAccDesc = rs.getString("FBailAccDesc");
        this.bailInAccDesc = rs.getString("FBailInAccDesc");
        this.bailOutAccDesc = rs.getString("FBailOutAccDesc");
        this.feeAccDesc1 = rs.getString("FFeeAccDesc1");
        this.feeAccDesc2 = rs.getString("FFeeAccDesc2");
        this.feeAccDesc3 = rs.getString("FFeeAccDesc3");
        this.desc = rs.getString("FDesc");
        this.portCode = rs.getString("FPortCode");
        this.portName = rs.getString("FPortName");
        //fanghaoln MS01305 QDV4华夏2010年06月13日02_A 20100628 
        this.fAffCorpCode=rs.getString("FFAffCorpCode");
        this.fAffCorpName=rs.getString("FFAffCorpName");
        //------------------------------end -----MS01305--------------------------------------
        
        //add by fangjiang 20101.01.12 STORY #262 #393
        this.tradeType = rs.getString("FTradeType");
        this.offNum = rs.getString("FOffNum");
        //------------------
        /**shashijie 2011.04.08 STORY #670 */
        this.tranDate = rs.getDate("FTranDate");
        /**end*/
        this.offCury = rs.getString("FOffCury");  //add by fangjiang 2011.09.29 story 1534
        //this.offCuryName = rs.getString("FCuryName");//add by fangjiang 2011.09.29 story 1534
        if (analy1) {
            this.analysisCode1 = rs.getString("FInvMgrCode") + "";
            this.analysisName1 = rs.getString("FInvMgrName") + "";
        }
        if (analy2) {
            this.analysisCode2 = rs.getString("FBrokerCode") + "";
            this.analysisName2 = rs.getString("FBrokerName") + "";
        }
        super.setRecLog(rs);
	}


    /**
     * getSetting
     *
     * @return IParaSetting
     */
    public IDataSetting getSetting() {
        return null;
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

    public String addSetting() throws YssException {
        String strSql = "";
        String strNumDate = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            addOpe();
            insertAll(allSetDatas);
            /*if (this.bailAccDesc.length() > 0 && this.bailOutAccDesc.length() > 0)
                        {
               inTradeAcc = setCashAccInfo(allSetDatas.split("\r\f")[2]);
               outTradeAcc = setCashAccInfo(allSetDatas.split("\r\f")[4]);
               if (ifInCashAcc(inTradeAcc.getCashAccCode(),outTradeAcc.getCashAccCode(),this.bailMoney))
               {
                  createSavCashTrans(this.fNum);
               }
                        }*/
            createSavSettleCashTrans(this.fNum, this.matureDate, this.settleDate,this.tradeDate,
                    this.settleTime);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加远期外汇交易数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    public void addOpe() throws YssException, YssException,
        SQLException {
        String strSql = "";
        String strNumDate = "";
        try {
            if (this.fNum.length() == 0) {
                strNumDate = YssFun.formatDatetime(this.tradeDate).
                    substring(0, 8);
                this.fNum = strNumDate +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_ForwardTrade"), //
                                           dbl.sqlRight("FNUM", 6), "000000",
                                           " where FNum like 'T"
                                           + strNumDate + "%'", 1);
                this.fNum = "T" + this.fNum;
            }
            
            //modify by fangjiang 20101.01.12 STORY #262 #393
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
                "(FNum,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FSecurityCode,FPortCode, FTradeDate, FTradeTime, FMatureDate," +
                " FSettleTime, FSettleDate,FTradeAmount,FTradePrice,FTrustPrice,FMatureMoney,FBailMoney,FSettleMoney,FFeeMoney1,FFeeMoney2," +
                " FFeeMoney3,FBAccDesc,FSAccDesc,FBailAccDesc,FBailInAccDesc,FBailOutAccDesc,FFeeAccDesc1,FFeeAccDesc2,FFeeAccDesc3,FDesc," +
                // " FAttrClsCode,FCatType," +  // lzp   modify  2007 12.10
                " FCHECKSTATE, FCREATOR, FCREATETIME,FCheckUser,ffAffCorpCode,ffAffCorpName, ftradetype, foffnum" +
                ",FTranDate" + //shashijie 2011.04.08 STORY #670
                ",FOffCury" +  //add by fangjiang 2011.09.29 story 1534
                ",FAmount, FCost" + //add by fangjiang 2011.10.12 BUG 2897
                ") values(" +//fanghaoln MS01305 QDV4华夏2010年06月13日02_A 20100628 
                dbl.sqlString(this.fNum) + "," +
                (this.analysisCode1.length() > 0 ?
                 dbl.sqlString(this.analysisCode1) : dbl.sqlString(" ")) + "," +
                 //modify by nimengjing 2010.12.20 BUG #713 浏览远期外汇交易数据时，“券商代码”无法加载。 
                (this.analysisCode2.length() > 0 ?
                 dbl.sqlString(this.analysisCode2) : dbl.sqlString(" ")) + "," +
                (this.analysisCode3.length() > 0 ?
                 dbl.sqlString(this.analysisCode3) : dbl.sqlString(" ")) + "," +
                 //--------------------------------end bug #713-------------------------------------------------
                dbl.sqlString(this.SecurityCode) + "," +
                dbl.sqlString(this.portCode) + "," +
                dbl.sqlDate(this.tradeDate) + "," +
                dbl.sqlString(this.tradeTime) + "," +
                dbl.sqlDate(this.matureDate) + "," +
                dbl.sqlString(this.settleTime) + "," +
                dbl.sqlDate(this.settleDate) + "," +
                this.tradeAmount + "," +
                this.tradePrice + "," +
                this.trustPrice + "," +
                this.matureMoney + "," +
                this.bailMoney + "," +
                this.settleMoney + "," +
                this.feeMoney1 + "," +
                this.feeMoney2 + "," +
                this.feeMoney3 + "," +
                dbl.sqlString(this.bAccDesc) + "," +
                dbl.sqlString(this.sAccDesc) + "," +
                dbl.sqlString(this.bailAccDesc) + "," +
                dbl.sqlString(this.bailInAccDesc) + "," +
                dbl.sqlString(this.bailOutAccDesc) + "," +
                dbl.sqlString(this.feeAccDesc1) + "," +
                dbl.sqlString(this.feeAccDesc2) + "," +
                dbl.sqlString(this.feeAccDesc3) + "," +
                dbl.sqlString(this.desc) + "," +
                // (this.attrClsCode == null||this.attrClsCode.length() == 0?dbl.sqlString(" "):dbl.sqlString(this.attrClsCode)) + "," +
                //(this.catTypeCode == null||this.catTypeCode.length() == 0?dbl.sqlString(" "):dbl.sqlString(this.catTypeCode)) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +"," +
               //fanghaoln MS01305 QDV4华夏2010年06月13日02_A 20100628 
                 dbl.sqlString(this.getFAffCorpCode()) + "," +
                 dbl.sqlString(this.getFAffCorpName()) + "," +
               //------------------------------end -----MS01305--------------------------------------
                 dbl.sqlString(this.tradeType) + "," +
                 dbl.sqlString(this.offNum) +
                 " , " + dbl.sqlDate(this.tranDate) + //shashijie 2011.04.08 STORY #670
                 " , " + dbl.sqlString(this.offCury) +  //add by fangjiang 2011.09.29 story 1534 
                 " , " + this.amount + //add by fangjiang 2011.10.12 BUG 2897
                 " , " + this.cost +   //add by fangjiang 2011.10.12 BUG 2897
                 ")";
            dbl.executeSql(strSql);
            //---------------------------
        } catch (Exception e) {
            throw new YssException("增加远期外汇交易数据出错", e);
        }
    }

    public void insertAll(String allData) throws YssException, YssException,
        SQLException {
        String allSetDatas = allData;
        String[] str = null;
        String strSql = "";
        String FSettleNum = "";
        YssTradeAcc inTradeAcc = null;
        YssTradeAcc outTradeAcc = null;
        YssTradeAcc settleTradeAcc = null;
        if (this.allSetDatas.length() > 0) {
            str = allSetDatas.split("\r\f");
            for (int i = 0; i < str.length - 1; i++) {
                if (str[i].length() > 11) {
                    tradeacc = new YssTradeAcc();
                    tradeacc.setYssPub(pub);
                    tradeacc.parseRowStr(str[i]);
                    //tradeacc.addSetting();
                    if (tradeacc.getPortCode().length() == 0 ||
                        tradeacc.getCashAccCode().length() == 0) {
                        continue;
                    }
                    strSql = "insert into " +
                        pub.yssGetTableName("Tb_Data_ForwardTradeAcc") +
                        "(FNum, FAccType, FPortCode, FAnalysisCode1, FAnalysisCode2," +
                        " FAnalysisCode3, FCashAccCode) values(" +
                        dbl.sqlString(this.fNum) + "," +
                        dbl.sqlString(tradeacc.getAccType()) + "," +
                        dbl.sqlString(tradeacc.getPortCode()) + "," +
                        ( (tradeacc.getAnalysisCode1().length() > 0) ?
                         dbl.sqlString(tradeacc.getAnalysisCode1()) :
                         dbl.sqlString(" ")) + "," +
                        ( (tradeacc.getAnalysisCode2().length() > 0) ?
                         dbl.sqlString(tradeacc.getAnalysisCode2()) :
                         dbl.sqlString(" ")) + "," +
                        ( (tradeacc.getAnalysisCode3().length() > 0) ?
                         dbl.sqlString(tradeacc.getAnalysisCode3()) :
                         dbl.sqlString(" ")) + "," +
                        dbl.sqlString(tradeacc.getCashAccCode()) +
                        ")";
                    dbl.executeSql(strSql);
                }
            }
            if (this.bailAccDesc.length() > 0 && this.bailOutAccDesc.length() > 0) {
                inTradeAcc = setCashAccInfo(allSetDatas.split("\r\f")[2]);
                settleTradeAcc = setCashAccInfo(allSetDatas.split("\r\f")[3]);
                outTradeAcc = setCashAccInfo(allSetDatas.split("\r\f")[4]);
                if (ifInCashAcc(inTradeAcc.getCashAccCode(),
                                outTradeAcc.getCashAccCode(), this.bailMoney)) {
                    //createSavCashTrans(this.fNum);
                    createSavSettleCashTrans(this.fNum, this.tradeDate,
                                             this.tradeTime, 2, 4);
                    if (ifInCashAcc(settleTradeAcc.getCashAccCode(),
                                    inTradeAcc.getCashAccCode(), this.bailMoney)) {
                        if (this.fNum.length() > 0) {
                            FSettleNum = YssFun.formatDatetime(this.settleDate).
                                substring(0, 8);
                            FSettleNum = FSettleNum +
                                dbFun.getNextInnerCode(pub.yssGetTableName(
                                    "Tb_Data_ForwardTrade"), //
                                dbl.sqlRight("FNUM", 6),
                                "000000",
                                " where FNum like 'T"
                                + FSettleNum + "%'", 1);
                            FSettleNum = "T" + FSettleNum;
                        }

                        createSavSettleCashTrans(FSettleNum, this.settleDate,
                                                 this.tradeTime, 3, 2);
                    }
                }
            }

        }
    }

    public void editDel(String allData) throws YssException, YssException,
        SQLException {
        String allSetDatas = allData;
        String[] str = null;
        String strSql = "";
        if (this.allSetDatas.length() > 0) {
            str = allSetDatas.split("\r\f");
            for (int i = 0; i < str.length; i++) {
                if (str[i].length() > 10) {
                    tradeacc = new YssTradeAcc();
                    tradeacc.setYssPub(pub);
                    tradeacc.parseRowStr(str[i]);
                    strSql = "delete from  " +
                        pub.yssGetTableName("Tb_Data_ForwardTradeAcc") +
                        " where FNum = " + dbl.sqlString(this.fNum) +
                        " and FAccType = " + dbl.sqlString(tradeacc.getAccType());
                    dbl.executeSql(strSql);
                }
            }
        }
    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "delete from " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
                " where FNum = " +
                dbl.sqlString(this.oldFNum);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            addOpe();
            //addSetting();
            //editAll(this.allSetDatas);
            editDel(allSetDatas);
            insertAll(allSetDatas);
            createSavSettleCashTrans(this.fNum, this.matureDate, this.settleDate,this.tradeDate,
                                     this.settleTime); //资金调拨 sj 20071119
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("修改远期外汇交易数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void deleteAll() throws YssException, YssException,
        SQLException {
        String[] str = null;
        String strSql = "";
        strSql = "delete from  " + pub.yssGetTableName("Tb_Data_ForwardTradeAcc") +
            " where FNum = " +
            dbl.sqlString(this.fNum);
     
        dbl.executeSql(strSql);
    }

    public void delSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        int Count = 0;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
                " set FCheckState = 2 where FNum = " +
                dbl.sqlString(this.oldFNum);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            /** 注视原因,删除交易数据时会级联删除关联账户表,导致数据还原的时候关联不出账户信息,
             * shashijie 2011-09-01 BUG 2537已审核、未审核分页浏览数据显示错误 */
            //deleteAll();
            /**-end-*/
            if (this.bailAccDesc.length() > 0 && this.bailOutAccDesc.length() > 0) {
            	 //edited by zhouxiang MS01634 20100924 删除远期外汇交易数据时，还原数据时无法产生资金调拨   
            	/*CashTransAdmin tranAdmin = null;
            	  tranAdmin = new CashTransAdmin();
                tranAdmin.setYssPub(pub);
                //tranAdmin.deleteForward(this.oldFNum,"Forward");
                tranAdmin.delete("", this.oldFNum, "Forward", "", "");*/
				String strDelTran = " update "
						+ pub.yssGetTableName("Tb_Cash_Transfer")
						+ " set Fcheckstate = 2 where Frelanum = "
						+ dbl.sqlString(this.oldFNum);
				dbl.executeSql(strDelTran);
            	 //end-- by zhouxiang MS01634 20100924 删除远期外汇交易数据时，还原数据时无法产生资金调拨   
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除远期外汇交易数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void checkSetting() throws YssException {
        String[] arrData = null;
        Connection con = dbl.loadConnection();

        //是否开始事务
        boolean bTrans = false;

        //sql执行语句
        String strSql = "";

        try {
            con.setAutoCommit(false);
            bTrans = true;
            if (sRecycled != null&&!sRecycled.equalsIgnoreCase("") ) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FNum = " +
                        dbl.sqlString(this.fNum);
                    dbl.executeSql(strSql);
                    //--- MS00463 QDV4赢时胜（上海）2009年05月20日02_B sj --------------
                    synchronizationCheckTransfer(); //同步更新资金调拨数据
                    //---------------------------------------------------------------
                }
            }
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核远期外汇交易数据出错", e);
        } finally {
            dbl.endTransFinal(con, bTrans);
        }

    }

    public void setSecurityAttr(ResultSet rs) throws SQLException, SQLException,
        YssException {
        boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security"); //判断分析代码存不存在
        boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
        this.fNum = rs.getString("FNum");
        this.SecurityCode = rs.getString("FSecurityCode");
        this.SecurityName = rs.getString("FSecurityName");
        this.tradeDate = rs.getDate("FTradeDate");
        this.tradeTime = rs.getString("FTradeTime");
        this.matureDate = rs.getDate("FMatureDate");
        this.settleTime = rs.getString("FSettleTime");
        this.settleDate = rs.getDate("FSettleDate");
        this.tradeAmount = rs.getDouble("FTradeAmount");
        this.tradePrice = rs.getDouble("FTradePrice");
        this.trustPrice = rs.getBigDecimal("FTrustPrice");//shashijie 2011.3.9 STORY #519 希望根据参数设置远期外汇交易界面价格显示位数
        this.matureMoney = rs.getBigDecimal("FMatureMoney");//shashijie 2011.3.9 STORY #519 希望根据参数设置远期外汇交易界面价格显示位数
        this.bailMoney = rs.getDouble("FBailMoney");
        this.settleMoney = rs.getDouble("FSettleMoney");
        this.feeMoney1 = rs.getDouble("FFeeMoney1");
        this.feeMoney2 = rs.getDouble("FFeeMoney2");
        this.feeMoney3 = rs.getDouble("FFeeMoney3");
        this.bAccDesc = rs.getString("FBAccDesc");
        this.sAccDesc = rs.getString("FSAccDesc");
        this.bailAccDesc = rs.getString("FBailAccDesc");
        this.bailInAccDesc = rs.getString("FBailInAccDesc");
        this.bailOutAccDesc = rs.getString("FBailOutAccDesc");
        this.feeAccDesc1 = rs.getString("FFeeAccDesc1");
        this.feeAccDesc2 = rs.getString("FFeeAccDesc2");
        this.feeAccDesc3 = rs.getString("FFeeAccDesc3");
        this.desc = rs.getString("FDesc");
        this.portCode = rs.getString("FPortCode");
        this.portName = rs.getString("FPortName");
        //fanghaoln MS01305 QDV4华夏2010年06月13日02_A 20100628 
        this.fAffCorpCode=rs.getString("FFAffCorpCode");
        this.fAffCorpName=rs.getString("FFAffCorpName");
        //------------------------------end -----MS01305--------------------------------------
        
        //add by fangjiang 20101.01.12 STORY #262 #393
        this.tradeType = rs.getString("FTradeType");
        this.offNum = rs.getString("FOffNum");
        //------------------
        /**shashijie 2011.04.08 STORY #670 */
        this.tranDate = rs.getDate("FTranDate");
        /**end*/
        this.offCury = rs.getString("FOffCury");  //add by fangjiang 2011.09.29 story 1534
        this.offCuryName = rs.getString("FCuryName");//add by fangjiang 2011.09.29 story 1534
        if (analy1) {
            this.analysisCode1 = rs.getString("FInvMgrCode") + "";
            this.analysisName1 = rs.getString("FInvMgrName") + "";
        }
        if (analy2) {
            this.analysisCode2 = rs.getString("FBrokerCode") + "";
            this.analysisName2 = rs.getString("FBrokerName") + "";
        }
        super.setRecLog(rs);
    }

    public String getOperValue(String sType) throws YssException {
        String reStr = "";
        String sqlStr = "";
        ResultSet rs = null;
        String securitycode = "";
        java.util.Date tDate;
        String MatureDate = "";
        String SettleDate = "";
        try {
            if (sType.length() > 0 && sType.equalsIgnoreCase("calDate")) {
                // securitycode = sType.split("\f\f\f")[0];
                // tDate = YssFun.toDate(sType.split("\f\f\f")[1]);
                securitycode = this.SecurityCode;
                tDate = this.tradeDate;
                sqlStr = "select b.FDuration as FDuration,b.FDurUnit as FDurUnit, " +
                    "c.FSettleDays as FSettleDays,c.FHolidaysCode as FHolidaysCode" + //BugNo:0000464  edit by jc
                    " from (select FSecurityCode, FDepdurCode from " +
                    pub.yssGetTableName("Tb_Para_Forward") +
                    " where FSecurityCode = " + dbl.sqlString(securitycode) +
                    " ) a " +
                    " join (select FDepDurCode,FDuration," + " FDurUnit from " +
                    pub.yssGetTableName("Tb_Para_DepositDuration") +
                    " ) b on a.FDepdurCode = b.FDepdurCode " +
                    " join (select FSecurityCode,FSettleDays,FHolidaysCode from  " +
                    pub.yssGetTableName("Tb_Para_Security") +
                    " ) c on a.FSecurityCode = c.FSecurityCode";
                rs = dbl.openResultSet(sqlStr);
                if (rs.next()) {
                    //MatureDate = YssFun.formatDate(YssFun.addDay(tDate,rs.getInt("FDuration")));
                    if (rs.getInt("FDurUnit") == 0) { //Day
                        MatureDate = YssFun.formatDate(YssFun.addDay(tDate,
                            rs.getInt("FDuration")));
                    } else if (rs.getInt("FDurUnit") == 1) { //Week
                        MatureDate = YssFun.formatDate(YssFun.addDay(tDate,
                            rs.getInt("FDuration") * 7));
                    } else if (rs.getInt("FDurUnit") == 2) { //Month
                        MatureDate = YssFun.formatDate(YssFun.addMonth(tDate,
                            rs.getInt("FDuration")));
                    } else if (rs.getInt("FDurUnit") == 3) { //Year
                        MatureDate = YssFun.formatDate(YssFun.addYear(tDate,
                            rs.getInt("FDuration")));
                    }
                    BaseOperDeal operDeal = new BaseOperDeal();
                    operDeal.setYssPub(pub);
                    SettleDate = YssFun.formatDate(operDeal.getWorkDay(rs.getString("FHolidaysCode"),
                        YssFun.toDate(MatureDate), rs.getInt("FSettleDays"))); //BugNo:0000464  edit by jc
                }
                reStr = MatureDate + "\t" + SettleDate;
            }else if (sType.length() > 0 && sType.equalsIgnoreCase("offMoney")){  //add by fangjiang 20101.01.12 STORY #262 #393
            	return checkOffMoney();
            }else if(sType.length() > 0 && sType.equalsIgnoreCase("curyInfo")){  //add by fangjiang 20101.01.12 STORY #262 #393
            	return getCuryTradeType(this.fNum);
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage() /*"获取数据出错"*/);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return reStr;
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    private void createSavSettleCashTrans(String sNum, java.util.Date TransDate,
                                          String TransTime, int in, int out) throws
        YssException { //生成settle资金调拨
        TransferBean tran = new TransferBean();
        TransferSetBean transfersetIn = new TransferSetBean();
        YssTradeAcc tradeaccIn = null;
        TransferSetBean transfersetOut = new TransferSetBean();
        YssTradeAcc tradeaccOut = null;
        ArrayList tranSetList = new ArrayList();

        //增加资金调拨记录
        tran.setYssPub(pub);
        tran.setDtTransDate(TransDate); //存入时间:使用的是"交易日期"
        tran.setDtTransferDate(TransDate); //结算日期
        tran.setStrTsfTypeCode("01"); //  ----- 调拨类型 未知，稍后加入
        tran.setStrSubTsfTypeCode("0106"); //这里还要待定
        tran.setStrTransferTime(TransTime);
//      tran.setDataSource(0);
        tran.setDataSource(1); //这里应为自动标记为1 by leeyu BUG:MS00020 2008-11-24
        tran.setFRelaNum(this.fNum);
        tran.setFNumType("Forward"); //    -----编号类型  未知，稍后加入
        tran.checkStateId = 0;
        tran.creatorTime = YssFun.formatDate(new java.util.Date(),
                                             "yyyyMMdd HH:mm:ss");
//      tran.setDataSource(0); //自动计算标志
        tran.setDataSource(1); //这里应为自动标记为1 by leeyu BUG:MS00020 2008-11-24
        //资金流入流出帐户
        transfersetIn.setDMoney(this.bailMoney); // 金额
        //获取帐户的信息
        tradeaccIn = setCashAccInfo(allSetDatas.split("\r\f")[in]);
        transfersetIn.setSPortCode(tradeaccIn.getPortCode()); // 组合代码
        transfersetIn.setSCashAccCode(tradeaccIn.getCashAccCode()); // 现金帐户代码
        transfersetIn.setSAnalysisCode1(this.analysisCode1); // 分析代码1
        transfersetIn.setSAnalysisCode2(this.analysisCode2); // 分析代码2
        transfersetIn.setDBaseRate(getCuryRate(tradeaccIn.getCashAccCode(),
                                               this.tradeDate, 0,
                                               tradeaccIn.getPortCode()));
        transfersetIn.setDPortRate(getCuryRate(tradeaccIn.getCashAccCode(),
                                               this.tradeDate, 1,
                                               tradeaccIn.getPortCode()));
        transfersetIn.checkStateId = 0;
        transfersetIn.setIInOut(1);
        tranSetList.add(transfersetIn);
//--------------------------------------------------------------------------------------------
        transfersetOut.setDMoney(this.bailMoney); // 金额
        //获取帐户的信息
        tradeaccOut = setCashAccInfo(allSetDatas.split("\r\f")[out]);
        transfersetOut.setSPortCode(tradeaccOut.getPortCode()); // 组合代码
        transfersetOut.setSCashAccCode(tradeaccOut.getCashAccCode()); // 现金帐户代码

        transfersetOut.setSAnalysisCode1(this.analysisCode1); // 分析代码1
        transfersetOut.setSAnalysisCode2(this.analysisCode2); // 分析代码2
        transfersetOut.setDBaseRate(getCuryRate(tradeaccOut.getCashAccCode(),
                                                this.tradeDate, 0,
                                                tradeaccOut.getPortCode()));
        transfersetOut.setDPortRate(getCuryRate(tradeaccOut.getCashAccCode(),
                                                this.tradeDate, 1,
                                                tradeaccOut.getPortCode()));
        transfersetOut.checkStateId = 0;
        transfersetOut.setIInOut(1);
        tranSetList.add(transfersetOut);

        CashTransAdmin tranAdmin = new CashTransAdmin();
        tranAdmin.setYssPub(pub);
        tranAdmin.addList(tran, tranSetList);
        //获得资金调拨编号
        String sTranNum = tranAdmin.getTransNums(sNum, "Forward");
        if (sTranNum.equals("")) {
            tranAdmin.insert();
        } else {
            //然后根据资金调拨编号 进行先删后增
            tranAdmin.insert(sTranNum, 0);
        }

    }

    private void createSavCashTrans(String sNum) throws YssException { //生成资金调拨
        TransferBean tran = new TransferBean();
        TransferSetBean transfersetIn = new TransferSetBean();
        YssTradeAcc tradeaccIn = null;
        TransferSetBean transfersetOut = new TransferSetBean();
        YssTradeAcc tradeaccOut = null;
        ArrayList tranSetList = new ArrayList();

        //增加资金调拨记录
        tran.setYssPub(pub);
        tran.setDtTransDate(this.tradeDate); //存入时间:使用的是"交易日期"
        tran.setDtTransferDate(this.tradeDate); //结算日期
        tran.setStrTsfTypeCode("01"); //  ----- 调拨类型 未知，稍后加入
        tran.setStrSubTsfTypeCode("0106"); //这里还要待定
        tran.setStrTransferTime(this.tradeTime);
//      tran.setDataSource(0);
        tran.setDataSource(1); //这里应为自动标记为1 by leeyu BUG:MS00020 2008-11-24
        tran.setFRelaNum(this.fNum);
        tran.setFNumType("Forward"); //    -----编号类型  未知，稍后加入
        tran.checkStateId = 0;
        tran.creatorTime = YssFun.formatDate(new java.util.Date(),
                                             "yyyyMMdd HH:mm:ss");
//      tran.setDataSource(0); //自动计算标志
        tran.setDataSource(1); //这里应为自动标记为1 by leeyu BUG:MS00020 2008-11-24
        //资金流入流出帐户
        transfersetIn.setDMoney(this.bailMoney); // 金额
        //获取帐户的信息
        tradeaccIn = setCashAccInfo(allSetDatas.split("\r\f")[2]);
        transfersetIn.setSPortCode(tradeaccIn.getPortCode()); // 组合代码
        transfersetIn.setSCashAccCode(tradeaccIn.getCashAccCode()); // 现金帐户代码
        transfersetIn.setSAnalysisCode1(this.analysisCode1); // 分析代码1
        transfersetIn.setSAnalysisCode2(this.analysisCode2); // 分析代码2
        //transferset.setDBaseRate(YssFun.roundIt(this.dBaseCuryRate, 15)); // 基础汇率
        //transferset.setDPortRate(YssFun.roundIt(this.dPortCuryRate, 15)); // 组合汇率
        transfersetIn.setDBaseRate(getCuryRate(tradeaccIn.getCashAccCode(),
                                               this.tradeDate, 0,
                                               tradeaccIn.getPortCode()));
        transfersetIn.setDPortRate(getCuryRate(tradeaccIn.getCashAccCode(),
                                               this.tradeDate, 1,
                                               tradeaccIn.getPortCode()));
        transfersetIn.checkStateId = 0;
        transfersetIn.setIInOut(1);
        tranSetList.add(transfersetIn);
//--------------------------------------------------------------------------------------------
        transfersetOut.setDMoney(this.bailMoney); // 金额
        //获取帐户的信息
        tradeaccOut = setCashAccInfo(allSetDatas.split("\r\f")[4]);
        transfersetOut.setSPortCode(tradeaccOut.getPortCode()); // 组合代码
        transfersetOut.setSCashAccCode(tradeaccOut.getCashAccCode()); // 现金帐户代码

        transfersetOut.setSAnalysisCode1(this.analysisCode1); // 分析代码1
        transfersetOut.setSAnalysisCode2(this.analysisCode2); // 分析代码2
        //transferset.setDBaseRate(YssFun.roundIt(this.dBaseCuryRate, 15));
        //transferset.setDPortRate(YssFun.roundIt(this.dPortCuryRate, 15));
        transfersetOut.setDBaseRate(getCuryRate(tradeaccOut.getCashAccCode(),
                                                this.tradeDate, 0,
                                                tradeaccOut.getPortCode()));
        transfersetOut.setDPortRate(getCuryRate(tradeaccOut.getCashAccCode(),
                                                this.tradeDate, 1,
                                                tradeaccOut.getPortCode()));
        transfersetOut.checkStateId = 0;
        transfersetOut.setIInOut( -1);
        tranSetList.add(transfersetOut);

        CashTransAdmin tranAdmin = new CashTransAdmin();
        tranAdmin.setYssPub(pub);
        tranAdmin.addList(tran, tranSetList);
        //获得资金调拨编号
        String sTranNum = tranAdmin.getTransNums(sNum, "Forward");
        if (sTranNum.equals("")) {
            tranAdmin.insert();
        } else {
            //然后根据资金调拨编号 进行先删后增
            tranAdmin.insert(sTranNum, 0);
        }
    }

    public YssTradeAcc setCashAccInfo(String data) throws YssException {
        YssTradeAcc tradeacc = new YssTradeAcc();
        tradeacc.setYssPub(pub);
        tradeacc.parseRowStr(data);
        return tradeacc;
    }

    public boolean ifInCashAcc(String in, String out, double money) { //sj 20071029 判断是否需要进行资金调拨
        boolean isCashAcc = false;
        if (in.length() > 0 && out.length() > 0 && money > 0) {
            isCashAcc = true;
            return isCashAcc;
        } else {
            return isCashAcc;
        }
    }

    /**
     * getCuryRate
     * author sj  20071029  获取基础\组合汇率
     * @param sMutilRowStr String
     * @return double
     */
    public double getCuryRate(String cashacc, java.util.Date tradeDate,
                              int CuryRateType, String portCode) throws
        YssException {
        String strSql = "";
        double reCuryRate = 0.0;
        String Cury = "";
        ResultSet rs = null;
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------
        try {
            //select FCuryCode from Tb_001_Para_CashAccount where FCashAccCode = 'BONY-CNY' and FCheckState = 1
            strSql = "select FCuryCode from " +
                pub.yssGetTableName("tb_Para_CashAccount") +
                " where FCashAccCode = " +
                dbl.sqlString(cashacc) + " and FCheckState = 1";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                Cury = rs.getString("FCuryCode");
            }
            if (CuryRateType == 0) {
                reCuryRate = this.getSettingOper().getCuryRate(tradeDate, Cury, "",
                    YssOperCons.YSS_RATE_BASE); //基础汇率
            } else if (CuryRateType == 1) {
//            reCuryRate = this.getSettingOper().getCuryRate(tradeDate,
//                  pub.getBaseCury(), portCode,
//                  YssOperCons.YSS_RATE_PORT); //组合汇率
                //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --------------------------
                rateOper.getInnerPortRate(tradeDate, Cury, portCode);
                reCuryRate = rateOper.getDPortRate();
                //-----------------------------------------------------------------------------------

            }
            return reCuryRate;
        } catch (Exception ex) {
            throw new YssException("获取数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    //设置公共的帐户信息
    public Hashtable setHtStr(String parseStr) throws YssException {
        Hashtable htAcc = new Hashtable();
        String[] accInfo = new String[] {
            "BuyCap", "SellCap", "Bail", "OutBail", "InBail", "Fee1", "Fee2",
            "Fee3"};
        YssTradeAcc tradeacc = null;
        if (parseStr != null&&parseStr.length() > 0) {
            for (int i = 0; i < parseStr.split("\r\f").length - 1; i++) {
                tradeacc = setCashAccInfo(parseStr.split("\r\f")[i]);
                if (htAcc.containsKey(accInfo[i])) {
                    htAcc.remove(accInfo[i]);
                    htAcc.put(accInfo[i], tradeacc);
                } else {
                    htAcc.put(accInfo[i], tradeacc);
                }
            }
        }
        return htAcc;
    }


    /**
     * 资金调拨zhouxiang  MS01522 20100823 修改 增加生成远期外汇交易的费用资金调拨和到期处理的资金调拨  
     * @param sNum String
     * @param matureDate Date 远期到期日期
     * @param settleDate Date 远期结算日期
     * @param TradeDate Date 远期交易日期
     * @param TransferTime String
     * @throws YssException
     * @throws SQLException 
     */
    private void createSavSettleCashTrans(String sNum, java.util.Date matureDate, java.util.Date settleDate, java.util.Date TradeDate,
                                          String TransferTime) throws
        YssException, SQLException { //生成settle资金调拨
        Hashtable htAcc = setHtStr(this.allSetDatas);
        TransferSetBean transfersetIn = new TransferSetBean();
        YssTradeAcc tradeaccIn = null;
        TransferSetBean transfersetOut = new TransferSetBean();
        YssTradeAcc tradeaccOut = null;
        ArrayList tranSetList = new ArrayList();
        
        //begin zhouxiang  MS01522 20100823 创建子资金调拨需要的list 传入计算费用的方法产生统一的费用资金调拨
        ArrayList tranFeeList = new ArrayList();
        TransferBean tranfee = new TransferBean();
        TransferBean tran = new TransferBean();
        //增加资金调拨记录
        tran.setYssPub(pub);
        tran.setDtTransDate(matureDate); //业务日期使用的是"到期日期"
        tran.setDtTransferDate(settleDate); //调拨日期使用的是"结算日期"
        //end   zhouxiang  MS01522 20100823 创建子资金调拨需要的list 传入计算费用的方法产生统一的费用资金调拨
        tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount); //"01"
        tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_Forward); //"00005"

        tran.setStrTransferTime(TransferTime);
//      tran.setDataSource(0);
        tran.setDataSource(1); //这里应为自动标记为1 by leeyu BUG:MS00020 2008-11-24
        tran.setFRelaNum(sNum); //关联编号
        tran.setFNumType("Forward"); //    -----编号类型
        tran.checkStateId = 0;
        tran.creatorTime = YssFun.formatDate(new java.util.Date(),
                                             "yyyyMMdd HH:mm:ss");
//      tran.setDataSource(0); //自动计算标志
        tran.setDataSource(1); //这里应为自动标记为1 by leeyu BUG:MS00020 2008-11-24
        //资金流入流出帐户
        
        
        //费用资金调拨主资金调拨设置+流入金额设置 begin zhouxiang MS01522 20100825
        tranfee.setYssPub(pub);
		tranfee.setDtTransDate(tradeDate); // 费用业务日期使用的是"交易日期"
		tranfee.setDtTransferDate(tradeDate); // 费用调拨日期使用的是"交易日期"
		tranfee.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount); // "01"
		tranfee.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBLX_Forward); // "00005"

		tranfee.setStrTransferTime(TransferTime);
		// tran.setDataSource(0);
		tranfee.setDataSource(1);
		tranfee.setFRelaNum(sNum);
		tranfee.setFNumType("Forward");
		tranfee.checkStateId = 0;
		tranfee.creatorTime = YssFun.formatDate(new java.util.Date(),
				"yyyyMMdd HH:mm:ss");
		// tran.setDataSource(0); //自动计算标志
		tranfee.setDataSource(1);
		
		if (getCuryTradeType(sNum).equals("0"))// 此处流入账户：交易货币=买入货币流出使用数量否则使用到期金额
		{
			transfersetIn.setDMoney(this.matureMoney.doubleValue());//shashijie 2011.3.9 STORY #519 希望根据参数设置远期外汇交易界面价格显示位数
		} else {
			transfersetIn.setDMoney(this.tradeAmount);
		} // 结算金额
		//end zhouxiang MS01522费用资金调拨主资金调拨设置+流入金额设置   20100825
        //获取帐户的信息
        tradeaccIn = ( (YssTradeAcc) htAcc.get("BuyCap"));
        transfersetIn.setSPortCode(tradeaccIn.getPortCode()); // 组合代码
        transfersetIn.setSCashAccCode(tradeaccIn.getCashAccCode()); // 现金帐户代码
        transfersetIn.setSAnalysisCode1(this.analysisCode1); // 分析代码1
        transfersetIn.setSAnalysisCode2(this.analysisCode2); // 分析代码2
        transfersetIn.setDBaseRate(getCuryRate(tradeaccIn.getCashAccCode(),
                                               this.tradeDate, 0,
                                               tradeaccIn.getPortCode()));
        transfersetIn.setDPortRate(getCuryRate(tradeaccIn.getCashAccCode(),
                                               this.tradeDate, 1,
                                               tradeaccIn.getPortCode()));
        transfersetIn.checkStateId = 0;
        transfersetIn.setIInOut(1);
        tranSetList.add(transfersetIn);
//--------------------------------------------------------------------------------------------
        //--begin zhouxiang MS01522 20100825---------------------------
		if (getCuryTradeType(sNum).equals("1"))// 此处流出账户设置：交易货币=买入货币时流入使用到期金额，'0'表示交易货币=买入货币，'1'表示交易货币=卖出货币
		{
			transfersetOut.setDMoney(this.matureMoney.doubleValue());//shashijie 2011.3.9 STORY #519 希望根据参数设置远期外汇交易界面价格显示位数
		} else {
			transfersetOut.setDMoney(this.tradeAmount);
		} // 结算金额
		//--end-- zhouxiang MS01522 20100825---------------------------
        //获取帐户的信息
        tradeaccOut = ( (YssTradeAcc) htAcc.get("SellCap"));
        transfersetOut.setSPortCode(tradeaccOut.getPortCode()); // 组合代码
        transfersetOut.setSCashAccCode(tradeaccOut.getCashAccCode()); // 现金帐户代码

        transfersetOut.setSAnalysisCode1(this.analysisCode1); // 分析代码1
        transfersetOut.setSAnalysisCode2(this.analysisCode2); // 分析代码2
        transfersetOut.setDBaseRate(getCuryRate(tradeaccOut.getCashAccCode(),
                                                this.tradeDate, 0,
                                                tradeaccOut.getPortCode()));
        transfersetOut.setDPortRate(getCuryRate(tradeaccOut.getCashAccCode(),
                                                this.tradeDate, 1,
                                                tradeaccOut.getPortCode()));
        transfersetOut.checkStateId = 0;
        transfersetOut.setIInOut( -1);
        tranSetList.add(transfersetOut);

        CashTransAdmin tranAdmin = new CashTransAdmin();
        tranAdmin.setYssPub(pub);
        //--begin zhouxiang MS01522 20100825---------------------------
        tran.setSubTrans(tranSetList);
        //tranAdmin.addList(tran);   //此处先注释，到期的资金调拨放在业务处理中处理 modify by fangjiang 20101.01.12 STORY #262 #393
        //--------------------------------以上为远期交易数据本金的资金调拨----------------------------------------------------
		String feeAcc1="Fee1";
		String feeAcc2="Fee2";
		String feeAcc3="Fee3";
        if (this.feeAccDesc1.length() > 0) {
			getTransFee(sNum,feeAcc1,this.feeMoney1, tranFeeList,
					TransferTime);
		}
		if (this.feeAccDesc2.length() > 0) {
			getTransFee(sNum,  feeAcc2,this.feeMoney2,tranFeeList,
					TransferTime);
		}
		if (this.feeAccDesc3.length() > 0) {
			getTransFee(sNum,  feeAcc3,this.feeMoney3,tranFeeList,
					TransferTime);
		}

        //获得资金调拨编号
		tranfee.setSubTrans(tranFeeList);
		tranAdmin.addList(tranfee);
		//--end-- zhouxiang MS01522 20100825---------------------------
        String sTranNum = tranAdmin.getTransNums(sNum, "Forward");
        if (sTranNum.equals("")) {
            tranAdmin.insert();
        } else {
            //然后根据资金调拨编号 进行先删后增
            tranAdmin.insert(sTranNum, 0);
        }

    }
/**
 * 
 * @throws YssException 
 * @方法名：getTransFee
 * @参数：sNum:外汇交易编号，tradeDate 交易日期，tranFeeList 费用资金调拨ArrayList 存储的是子资金调拨
 * @param TransferTime 交易时间,feeMoney 费用账户对应的费用金额
 * @author ZhouXiang 20100823  MS01522 增加生成远期外汇交易的费用资金调拨和到期处理的资金调拨   新增方法
 * @返回类型：void
 * @说明：使用费用账户和交易日期对tranAdmin进行编辑， 增加费用的资金调拨
 */
	private void getTransFee(String sNum, String feeAcc,
			double feeMoney,ArrayList tranFeeList, String TransferTime)
			throws YssException {
		TransferSetBean transfeeOut = new TransferSetBean();// 费用账户
		Hashtable htAcc = setHtStr(this.allSetDatas);
		YssTradeAcc tradeaccIn = null;
		// 增加资金调拨记录
		
		transfeeOut.setDMoney(feeMoney);
		tradeaccIn = ((YssTradeAcc) htAcc.get(feeAcc));
		transfeeOut.setSPortCode(tradeaccIn.getPortCode()); // 组合代码
		transfeeOut.setSCashAccCode(tradeaccIn.getCashAccCode()); // 现金帐户代码
		transfeeOut.setSAnalysisCode1(this.analysisCode1); // 分析代码1
		transfeeOut.setSAnalysisCode2(this.analysisCode2); // 分析代码2
		transfeeOut.setDBaseRate(getCuryRate(tradeaccIn.getCashAccCode(),
				this.tradeDate, 0, tradeaccIn.getPortCode()));
		transfeeOut.setDPortRate(getCuryRate(tradeaccIn.getCashAccCode(),
				this.tradeDate, 1, tradeaccIn.getPortCode()));
		transfeeOut.checkStateId = 0;
		transfeeOut.setIInOut(-1);// 设置成流出
		tranFeeList.add(transfeeOut);
		
	}

	/***
     * 
     * @方法名：getCuryTradeType
     * @参数：远期交易编号
     * @creator :周翔 20100823  MS01522 增加生成远期外汇交易的费用资金调拨和到期处理的资金调拨   新增方法
     * @返回类型：String
     * @说明：使用远期交易编号查询证券的交易货币等于远期品种设置中的买入货币还是卖出货币
     */
	private String getCuryTradeType(String sNum) throws SQLException,
			YssException {
		String flag = "0";// 返回标识
		// 使用远期交易数据的编号查询该证券对应的交易货币
		ResultSet tradeRs = null;
		ResultSet tradeTypeRs = null;
		String tradeCury = "";
		String tradeSql = "select b.ftradecury,a.* from "// 编号查询交易货币类型
				+ pub.yssGetTableName("Tb_Data_ForwardTrade")
				+ " a left join  (select fsecuritycode,ftradecury from "
				+ pub.yssGetTableName("tb_para_security")
				+ ") b on a.fsecuritycode=b.fsecuritycode where fnum="
				+ dbl.sqlString(sNum);
		String tradeTypeSql = "select b.FSaleCury,b.fbuycury,a.* from "// 编号查询买入卖出货币类型
				+ pub.yssGetTableName("Tb_Data_ForwardTrade")
				+ " a left join  (select fsecuritycode,FBuyCury,FSaleCury from "
				+ pub.yssGetTableName("Tb_Para_Forward")
				+ " ) b on a.fsecuritycode=b.fsecuritycode where fnum="
				+ dbl.sqlString(sNum);
		try {
			tradeRs = dbl.openResultSet(tradeSql);
			if (tradeRs.next()) {
				tradeCury = tradeRs.getString("ftradecury");// 获取交易货币
			}
			tradeTypeRs = dbl.openResultSet(tradeTypeSql);
			if (tradeTypeRs.next()) {
				if (tradeCury.equals(tradeTypeRs.getString("fbuycury"))) {
					flag = "0";
				} else if (tradeCury.equals(tradeTypeRs.getString("FSaleCury"))) {
					flag = "1";
				}
			}

		} catch (Exception e) {
			throw new YssException("远期外汇交易查询交易货币或匹配买入卖出货币出错!", e);
		} finally {
			dbl.closeResultSetFinal(tradeRs);
			dbl.closeResultSetFinal(tradeTypeRs);
		}
		return flag;

	}

	/**
     * deleteRecycleData
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
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Data_ForwardTrade") +
                        " where FNum = " + dbl.sqlString(this.fNum);
                    //执行sql语句
                    dbl.executeSql(strSql);
                    //begin zhouxiang MS01634 删除远期外汇交易数据时，还原数据时无法产生资金调拨   
                    CashTransAdmin tranAdmin = null;
                    tranAdmin = new CashTransAdmin();
                    tranAdmin.setYssPub(pub);
                    tranAdmin.delete("", this.oldFNum, "Forward", "", "");
                  /*  String clearTranfer="delete from "+pub.yssGetTableName("Tb_Cash_Transfer")+
                    					" where Frelanum = "+dbl.sqlString(this.oldFNum);
                    dbl.executeSql(clearTranfer);*/
                    //end-- zhouxiang MS01634 删除远期外汇交易数据时，还原数据时无法产生资金调拨   
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 在操作远期交易时,同步对其资金调拨数据进行审核操作
     * @throws YssException
     * MS00463 QDV4赢时胜（上海）2009年05月20日02_B sj
     */
    private void synchronizationCheckTransfer() throws YssException {
        StringBuffer buf = new StringBuffer();
        //---更新资金调拨子表 --------------------------------------------------
        buf.append("update ");
        buf.append(pub.yssGetTableName("Tb_Cash_SubTransfer"));
        buf.append(" set FCheckState = ");
        buf.append(this.checkStateId);
        buf.append(",FCheckUser = ");
        buf.append(dbl.sqlString(pub.getUserCode()));
        buf.append(",FCheckTime = ");
        buf.append(dbl.sqlString(YssFun.formatDatetime(new java.util.Date())));
        
        //eidted by zhouxiang MS01522 增加生成远期外汇交易的费用资金调拨和到期处理的资金调拨  
        buf.append(" where FNum in (select FNum from ");
        //end--  by zhouxiang MS01522 增加生成远期外汇交易的费用资金调拨和到期处理的资金调拨  
        buf.append(pub.yssGetTableName("Tb_Cash_Transfer"));
        buf.append(" where FRelaNum = ");
        buf.append(dbl.sqlString(this.fNum));
        //------ add by wangzuochun 2010.03.24  MS01024  系统在反审核远期交易数据时，报错    QDV4南方2010年3月12日01_B
        buf.append(" and FNumType = 'Forward'");
        //-------------------------------------------------//
        buf.append(")");
        try {
            dbl.executeSql(buf.toString());
        } catch (Exception e) {
            throw new YssException("审核远期外汇交易之资金调拨子表数据出现异常！", e);
        }
        //---更新资金调拨数据 --------------------------------------------------
        buf.delete(0, buf.length());
        buf.append("update ");
        buf.append(pub.yssGetTableName("Tb_Cash_Transfer"));
        buf.append(" set FCheckState = ");
        buf.append(this.checkStateId);
        buf.append(",FCheckUser = ");
        buf.append(dbl.sqlString(pub.getUserCode()));
        buf.append(",FCheckTime = ");
        buf.append(dbl.sqlString(YssFun.formatDatetime(new java.util.Date())));
        buf.append(" where FRelaNum = ");
        buf.append(dbl.sqlString(this.fNum));
        //------ add by wangzuochun 2010.03.24  MS01024  系统在反审核远期交易数据时，报错    QDV4南方2010年3月12日01_B
        buf.append(" and FNumType = 'Forward'");
        //-------------------------------------------------//
        try {
            dbl.executeSql(buf.toString());
        } catch (Exception ex) {
            throw new YssException("审核远期外汇交易之资金调拨出现异常！", ex);
        }
    }
    
    //add by fangjiang 20101.01.12 STORY #262 #393
    public String checkOffMoney() throws YssException {
    	String result = "0" ; //默认平仓的买入账户金额不等于被平仓的卖出账户金额之和
    	String sql = "";
    	ResultSet rs = null;
    	String[] num = this.offNum.split(",");
    	double tradeAmount = 0.0;
    	BigDecimal matureMoney = null;//这里原先是double类型,shashijie 2011.3.9 STORY #519 希望根据参数设置远期外汇交易界面价格显示位数
    	try {
            sql = " select sum(Ftradeamount) as Ftradeamount, sum(fmaturemoney) as fmaturemoney from " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
                  " where FNum in (" + operSql.sqlCodes(this.offNum) + ")";
            rs =dbl.openResultSet(sql);
            while (rs.next()) {
            	tradeAmount = rs.getDouble("Ftradeamount");
            	matureMoney = rs.getBigDecimal("fmaturemoney");//shashijie 2011.3.9 STORY #519 希望根据参数设置远期外汇交易界面价格显示位数
            	if(getCuryTradeType(num[0]).equals("0")){ //交易货币等于买入货币
            		if(YssD.sub(this.tradeAmount, tradeAmount) > 0){
            			result = "1";
            		}
            	}else{
            		if(YssD.sub(this.matureMoney, matureMoney) > 0){
            			result = "1";
            		}
            	}
            }          
        } catch (Exception e) {
            throw new YssException("获取外汇交易数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return result;
    }
    
    //add by fangjiang 2011.10.12 BUG 2897
    private BigDecimal calAmountOrCost(String offNum, BigDecimal value, String flag) throws YssException{
    	
    	String sql = " select FTrustPrice from " + pub.yssGetTableName("Tb_Data_ForwardTrade") +
    	             " where FNum = " + dbl.sqlString(offNum);
    	ResultSet rs = null;
    	BigDecimal result =new BigDecimal(0);
    	try{
    		rs = dbl.queryByPreparedStatement(sql);
    		while(rs.next()){
    			if("1".equalsIgnoreCase(flag)){
    				result = new BigDecimal(YssD.mul(value, rs.getBigDecimal("FTrustPrice")));
    			}else{
    				result = new BigDecimal(YssD.div(value, rs.getBigDecimal("FTrustPrice")));
    			}
    		}
    	}catch(Exception e){
    		throw new YssException("计算出错", e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}   	 
    	return result;
    }    
    //-----------

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

	public String getFAffCorpCode() {
		return fAffCorpCode;
	}

	public void setFAffCorpCode(String affCorpCode) {
		fAffCorpCode = affCorpCode;
	}
	
	//add by fangjiang 20101.01.12 STORY #262 #393
	public String getFAffCorpName() {
		return fAffCorpName;
	}

	public void setFAffCorpName(String affCorpName) {
		fAffCorpName = affCorpName;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	
	//------------------

}
