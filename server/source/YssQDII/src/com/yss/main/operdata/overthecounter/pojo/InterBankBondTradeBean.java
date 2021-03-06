package com.yss.main.operdata.overthecounter.pojo;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.util.*;
/**
 *
 * <p>Title: </p>
 * 银行间债券交易实体bean
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: yss</p>
 *
 * @author panjunfang add 20090720
 * @version 1.0
 */
public class InterBankBondTradeBean
    extends BaseDataSettingBean {
    private String strTradeNo = "";//交易编号
    private String strSecurityCode = "";//交易债券代码
    private String strSecurityName = "";//交易债券名称
    private String strBusTypeCode = "";//业务类型代码
    private String strBusTypeName = "";//业务类型名称
    private String strBargainDate = "1900-01-01"; //成交日期
    private String strInvestTypeCode = "";//投资分类
    private String strPortCode = ""; //组合代码
    private String strPortName = ""; //组合名称
    private String strInvMgrCode = ""; //投资经理代码
    private String strInvMgrName = ""; //投资经理名称
    private String strAttrClsCode = "";//属性分类代码
    private String strAttrClsName = "";//属性分类名称
    private String strCashAcctCode = ""; //现金帐户代码
    private String strCashAcctName = ""; //现金帐户名称
    private String strAffCorpCode = ""; //关联交易方代码
    private String strAffCorpName = ""; //关联交易方名称
    private String strSettleDate = "1900-01-01"; //结算日期
    private double dbPortCuryRate; //组合汇率
    private double dbBaseCuryRate; //基础汇率
    private double dbTradeNum;//交易数量
    private double dbTradeMoney;//交易金额
    private double dbBbondInterest;//债券利息
    private double dbPoundageFee;//手续费
    private double dbSettlementFee;//结算费
    private double dbBankFee;//银行费用
    private double dbSquareFee;//清算金额
    private String strStartDate = "";//开始日期，用于查询成交日期在选定范围内的业务数据
    private String strEndDate = "";//结束日期，用于查询成交日期在选定范围内的业务数据
    private String strOldTradeNo = "";//保存修改前的交易编号
    private String bShow = "1"; //标记是否在前台界面打开时将数据加载出来，默认为否		//20111027 modified by liubo.Story #1285.更改此变量的类型，使之与前台界面的基类的m_sIsOnlyColumns的类型吻合
    private InterBankBondTradeBean filterType;

	//---add by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A start---//
    private String strSecIssuerCode = "";//关联机构代码
    private String strSecIssuerName = "";//关联机构名称
    
    public String getStrSecIssuerCode() {
		return strSecIssuerCode;
	}

	public void setStrSecIssuerCode(String strSecIssuerCode) {
		this.strSecIssuerCode = strSecIssuerCode;
	}

	public String getStrSecIssuerName() {
		return strSecIssuerName;
	}

	public void setStrSecIssuerName(String strSecIssuerName) {
		this.strSecIssuerName = strSecIssuerName;
	}
    //---add by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A end---//
    
    public InterBankBondTradeBean() {
    }

    public String getStrTradeNo() {
        return strTradeNo;
    }

    public String getStrSettleDate() {
        return strSettleDate;
    }

    public String getStrSecurityName() {
        return strSecurityName;
    }

    public String getStrSecurityCode() {
        return strSecurityCode;
    }

    public String getStrPortName() {
        return strPortName;
    }

    public String getStrPortCode() {
        return strPortCode;
    }

    public String getStrInvestTypeCode() {
        return strInvestTypeCode;
    }

    public String getStrInvMgrName() {
        return strInvMgrName;
    }

    public String getStrInvMgrCode() {
        return strInvMgrCode;
    }

    public String getStrCashAcctName() {
        return strCashAcctName;
    }

    public String getStrCashAcctCode() {
        return strCashAcctCode;
    }

    public String getStrBusTypeCode() {
        return strBusTypeCode;
    }

    public String getStrBargainDate() {
        return strBargainDate;
    }

    public String getStrAffCorpName() {
        return strAffCorpName;
    }

    public String getStrAffCorpCode() {
        return strAffCorpCode;
    }

    public InterBankBondTradeBean getFilterType() {
        return filterType;
    }

    public double getDbTradeNum() {
        return dbTradeNum;
    }

    public double getDbTradeMoney() {
        return dbTradeMoney;
    }

    public double getDbSquareFee() {
        return dbSquareFee;
    }

    public double getDbSettlementFee() {
        return dbSettlementFee;
    }

    public double getDbPoundageFee() {
        return dbPoundageFee;
    }

    public double getDbPortCuryRate() {
        return dbPortCuryRate;
    }

    public double getDbBbondInterest() {
        return dbBbondInterest;
    }

    public double getDbBaseCuryRate() {
        return dbBaseCuryRate;
    }

    public void setDbBankFee(double dbBankFee) {
        this.dbBankFee = dbBankFee;
    }

    public void setStrTradeNo(String strTradeNo) {
        this.strTradeNo = strTradeNo;
    }

    public void setStrSettleDate(String strSettleDate) {
        this.strSettleDate = strSettleDate;
    }

    public void setStrSecurityName(String strSecurityName) {
        this.strSecurityName = strSecurityName;
    }

    public void setStrSecurityCode(String strSecurityCode) {
        this.strSecurityCode = strSecurityCode;
    }

    public void setStrPortName(String strPortName) {
        this.strPortName = strPortName;
    }

    public void setStrPortCode(String strPortCode) {
        this.strPortCode = strPortCode;
    }

    public void setStrInvestTypeCode(String strInvestTypeCode) {
        this.strInvestTypeCode = strInvestTypeCode;
    }

    public void setStrInvMgrName(String strInvMgrName) {
        this.strInvMgrName = strInvMgrName;
    }

    public void setStrInvMgrCode(String strInvMgrCode) {
        this.strInvMgrCode = strInvMgrCode;
    }

    public void setStrCashAcctName(String strCashAcctName) {
        this.strCashAcctName = strCashAcctName;
    }

    public void setStrCashAcctCode(String strCashAcctCode) {
        this.strCashAcctCode = strCashAcctCode;
    }

    public void setStrBusTypeCode(String strBusTypeCode) {
        this.strBusTypeCode = strBusTypeCode;
    }

    public void setStrBargainDate(String strBargainDate) {
        this.strBargainDate = strBargainDate;
    }

    public void setStrAffCorpName(String strAffCorpName) {
        this.strAffCorpName = strAffCorpName;
    }

    public void setStrAffCorpCode(String strAffCorpCode) {
        this.strAffCorpCode = strAffCorpCode;
    }

    public void setFilterType(InterBankBondTradeBean filterType) {
        this.filterType = filterType;
    }

    public void setDbTradeNum(double dbTradeNum) {
        this.dbTradeNum = dbTradeNum;
    }

    public void setDbTradeMoney(double dbTradeMoney) {
        this.dbTradeMoney = dbTradeMoney;
    }

    public void setDbSquareFee(double dbSquareFee) {
        this.dbSquareFee = dbSquareFee;
    }

    public void setDbSettlementFee(double dbSettlementFee) {
        this.dbSettlementFee = dbSettlementFee;
    }

    public void setDbPoundageFee(double dbPoundageFee) {
        this.dbPoundageFee = dbPoundageFee;
    }

    public void setDbPortCuryRate(double dbPortCuryRate) {
        this.dbPortCuryRate = dbPortCuryRate;
    }

    public void setDbBbondInterest(double dbBbondInterest) {
        this.dbBbondInterest = dbBbondInterest;
    }

    public void setDbBaseCuryRate(double dbBaseCuryRate) {
        this.dbBaseCuryRate = dbBaseCuryRate;
    }

    public void setStrBusTypeName(String strBusTypeName) {
        this.strBusTypeName = strBusTypeName;
    }

    public void setStrStartDate(String strStartDate) {
        this.strStartDate = strStartDate;
    }

    public void setStrEndDate(String strEndDate) {
        this.strEndDate = strEndDate;
    }

    public void setStrOldTradeNo(String strOldTradeNo) {
        this.strOldTradeNo = strOldTradeNo;
    }

    public void setBShow(String bShow) {
        this.bShow = bShow;
    }

    public void setStrAttrClsName(String strAttrClsName) {
        this.strAttrClsName = strAttrClsName;
    }

    public void setStrAttrClsCode(String strAttrClsCode) {
        this.strAttrClsCode = strAttrClsCode;
    }

    public double getDbBankFee() {
        return dbBankFee;
    }

    public String getStrBusTypeName() {
        return strBusTypeName;
    }

    public String getStrStartDate() {
        return strStartDate;
    }

    public String getStrEndDate() {
        return strEndDate;
    }

    public String getStrOldTradeNo() {
        return strOldTradeNo;
    }

    public String isBShow() {
        return bShow;
    }

    public String getStrAttrClsName() {
        return strAttrClsName;
    }

    public String getStrAttrClsCode() {
        return strAttrClsCode;
    }

    /**
     * 解析前台发送来的操作组合设置请求
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
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
            this.strTradeNo = reqAry[0];
            this.strSecurityCode = reqAry[1];
            this.strBusTypeCode = reqAry[2];
            this.strBargainDate = reqAry[3];
            this.strInvestTypeCode = reqAry[4];
            this.strPortCode = reqAry[5];
            this.strInvMgrCode = reqAry[6];
            this.strCashAcctCode = reqAry[7];
            this.strAffCorpCode = reqAry[8];
            this.strSettleDate = reqAry[9];
            if (reqAry[10].length() != 0) {
                this.dbPortCuryRate = Double.parseDouble(
                    reqAry[10]);
            }
            if (reqAry[11].length() != 0) {
                this.dbBaseCuryRate = Double.parseDouble(
                    reqAry[11]);
            }
            if (reqAry[12].length() != 0) {
                this.dbTradeNum = Double.parseDouble(
                    reqAry[12]);
            }
            if (reqAry[13].length() != 0) {
                this.dbTradeMoney = Double.parseDouble(
                    reqAry[13]);
            }
            if (reqAry[14].length() != 0) {
                this.dbBbondInterest = Double.parseDouble(
                    reqAry[14]);
            }
            if (reqAry[15].length() != 0) {
                this.dbPoundageFee = Double.parseDouble(
                    reqAry[15]);
            }
            if (reqAry[16].length() != 0) {
                this.dbSettlementFee = Double.parseDouble(
                    reqAry[16]);
            }
            if (reqAry[17].length() != 0) {
                this.dbBankFee = Double.parseDouble(
                    reqAry[17]);
            }
            if (reqAry[18].length() != 0) {
                this.dbSquareFee = Double.parseDouble(
                    reqAry[18]);
            }
            this.strStartDate = reqAry[19];
            this.strEndDate = reqAry[20];
            this.strOldTradeNo = reqAry[21];
            this.checkStateId = Integer.parseInt(reqAry[22]);
            this.bShow = reqAry[23];
            this.strAttrClsCode = reqAry[24];
            //add by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A
            this.strSecIssuerCode = reqAry[25];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new InterBankBondTradeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析银行间债券交易数据出错！", e);
        }
    }
    /**
     * 通过拼接字符串来获取数据字符串
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strTradeNo).append("\t");
        buf.append(this.strSecurityCode).append("\t");
        buf.append(this.strSecurityName).append("\t");
        buf.append(this.strBusTypeCode).append("\t");
        buf.append(this.strBusTypeName).append("\t");
        buf.append(this.strBargainDate).append("\t");
        buf.append(this.strInvestTypeCode).append("\t");
        buf.append(this.strPortCode).append("\t");
        buf.append(this.strPortName).append("\t");
        buf.append(this.strInvMgrCode).append("\t");
        buf.append(this.strInvMgrName).append("\t");
        buf.append(this.strCashAcctCode).append("\t");
        buf.append(this.strCashAcctName).append("\t");
        buf.append(this.strAffCorpCode).append("\t");
        buf.append(this.strAffCorpName).append("\t");
        buf.append(this.strSettleDate).append("\t");
        buf.append(this.dbPortCuryRate).append("\t");
        buf.append(this.dbBaseCuryRate).append("\t");
        buf.append(this.dbTradeNum).append("\t");
        buf.append(this.dbTradeMoney).append("\t");
        buf.append(this.dbBbondInterest).append("\t");
        buf.append(this.dbPoundageFee).append("\t");
        buf.append(this.dbSettlementFee).append("\t");
        buf.append(this.dbBankFee).append("\t");
        buf.append(this.dbSquareFee).append("\t");
        buf.append(this.strAttrClsCode).append("\t");
        buf.append(this.strAttrClsName).append("\t");
        //---add by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A start---//
        buf.append(this.strSecIssuerCode).append("\t");
        buf.append(this.strSecIssuerName).append("\t");
        //---add by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A end---//
        buf.append(super.buildRecLog());
        return buf.toString();
    }
    /**
     * 为各项变量赋值
     * @param rs ResultSet
     * @throws SQLException
     */
    public void setIntBakBondTradeAttr(ResultSet rs) throws SQLException,
        YssException {
        this.strTradeNo = rs.getString("FNum") + "";
        this.strSecurityCode = rs.getString("FSecurityCode") + "";
        this.strSecurityName = rs.getString("FSecurityName") + "";
        this.strBusTypeCode = rs.getString("FTradeTypeCode") + "";
        this.strBusTypeName = rs.getString("FTradeTypeName") + "";
        this.strBargainDate = rs.getDate("FBARGAINDATE") + "";
        this.strInvestTypeCode = rs.getString("FInvestType") + "";
        this.strPortCode = rs.getString("FPortCode") + "";
        this.strPortName = rs.getString("FPortName") + "";
        this.strInvMgrCode = rs.getString("FInvMgrCode") + "";
        this.strInvMgrName = rs.getString("FInvMgrName") + "";
        this.strCashAcctCode = rs.getString("FCashAccCode") + "";
        this.strCashAcctName = rs.getString("FCashAccName") + "";
        this.strAffCorpCode = rs.getString("FAffCorpCode") + "";
        this.strAffCorpName = rs.getString("FAffCorpName") + "";
        this.strSettleDate = rs.getDate("FSettleDate") + "";
        this.dbPortCuryRate = rs.getDouble("FPortCuryRate");
        this.dbBaseCuryRate = rs.getDouble("FBaseCuryRate");
        this.dbTradeNum = rs.getDouble("FTradeAmount");
        this.dbTradeMoney = rs.getDouble("FTradeMoney");
        this.dbBbondInterest = rs.getDouble("FBondIns");
        this.dbPoundageFee = rs.getDouble("FFee");
        this.dbSettlementFee = rs.getDouble("FSettleFee");
        this.dbBankFee = rs.getDouble("FBankFee");
        this.dbSquareFee = rs.getDouble("FSettleMoney");
        //---add by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A start---//
        this.strSecIssuerCode = rs.getString("FSecIssuerCode");
        this.strSecIssuerName = rs.getString("FSecIssuerName");
        //---add by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A end---//
        super.setRecLog(rs);
    }
}
