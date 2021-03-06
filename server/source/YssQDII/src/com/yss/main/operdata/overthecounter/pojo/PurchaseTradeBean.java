package com.yss.main.operdata.overthecounter.pojo;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.util.*;
import java.math.BigDecimal;

/**
 * add by wangzuochun 2009.06.22
 * MS00014  国内回购业务  QDV4.1赢时胜（上海）2009年4月20日14_A
 * <p>Title: PurchaseTradeAdmin</p>
 *
 * <p>Description: 回购业务实体类</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 */
public class PurchaseTradeBean
    extends BaseDataSettingBean {
    private String num = ""; //回购业务编号
    private String securityCode = ""; //交易证券代码
    private String securityName = ""; //交易证券名称
    private String portCode = ""; //组合代码
    private String portName = ""; //组合名称
    private String invMgrCode = ""; //投资经理代码
    private String invMgrName = ""; //投资经理名称
    private String tradeCode = ""; //交易方式代码
    private String tradeName = ""; //交易方式名称
    private String cashAcctCode = ""; //现金帐户代码
    private String cashAcctName = ""; //现金帐户名称
    private String affCorpCode = ""; //关联交易方代码
    private String affCorpName = ""; //关联交易方名称
    private String bargainDate = "1900-01-01"; //成交日期
    private String bargainTime = "00:00:00"; //成交时间
    private String settleDate = "1900-01-01"; //结算日期
    private String matureDate = "1900-01-01"; //到期日期
    private String matureSettleDate = "1900-01-01"; //到期结算日期
	//---edit by songjie 2011.12.19 BUG 3389 QDV4赢时胜（深圳）2011年12月13日01_B start---//
    private BigDecimal purchaseGain = new BigDecimal(-1.0); //回购得益
    private BigDecimal portCuryRate = new BigDecimal(-1.0); //组合汇率
    private BigDecimal baseCuryRate = new BigDecimal(-1.0); //基础汇率
    private BigDecimal tradeMoney = new BigDecimal(-1.0); //交易金额
    private BigDecimal totalCost = new BigDecimal(-1.0); //实收实付金额
	//---edit by songjie 2011.12.19 BUG 3389 QDV4赢时胜（深圳）2011年12月13日01_B end---//
    private String desc = ""; //交易描述
	//---edit by songjie 2011.12.19 BUG 3389 QDV4赢时胜（深圳）2011年12月13日01_B start---//
    private BigDecimal tradeHandleFee = new BigDecimal(-1.0); //交易手续费
    private BigDecimal bankHandleFee = new BigDecimal(-1.0); //银行手续费
    private BigDecimal setServiceFee = new BigDecimal(-1.0); //结算服务费
	//---edit by songjie 2011.12.19 BUG 3389 QDV4赢时胜（深圳）2011年12月13日01_B end---//
    private String isOnlyColumn = ""; //是否只读取列名的标志
    private PurchaseTradeBean filterType;
    private String sRelaFreezeSec="";//STORY #1509 监控管理－监控结果 添加回购冻结证券数量设置 add by jiangshichao 2011.09.17 
    //add by songjie 2011.12.19 BUG 3389 QDV4赢时胜（深圳）2011年12月13日01_B
    private String fixNum = "";//债券抵用数 
    //---add by songjie 2012.02.17 STORY #2262 QDV4赢时胜(上海开发部)2012年02月17日01_A start---//
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
    //---add by songjie 2012.02.17 STORY #2262 QDV4赢时胜(上海开发部)2012年02月17日01_A end---//
    
	//---add by songjie 2011.12.19 BUG 3389 QDV4赢时胜（深圳）2011年12月13日01_B start---//
    public String getFixNum(){
    	return fixNum;
    }
    
    public void setFixNum(String fixNum){
    	this.fixNum = fixNum;
    }
    //---add by songjie 2011.12.19 BUG 3389 QDV4赢时胜（深圳）2011年12月13日01_B end---//
    
    public String getsRelaFreezeSec() {
		return sRelaFreezeSec;
	}

	public void setsRelaFreezeSec(String sRelaFreezeSec) {
		this.sRelaFreezeSec = sRelaFreezeSec;
	}

	public PurchaseTradeBean() {
    }

    public String getAffCorpCode() {
        return affCorpCode;
    }

    public String getAffCorpName() {
        return affCorpName;
    }

    public String getBargainDate() {
        return bargainDate;
    }

    public String getBargainTime() {
        return bargainTime;
    }

    public BigDecimal getBaseCuryRate() {
        return baseCuryRate;
    }

    public String getCashAcctCode() {
        return cashAcctCode;
    }

    public String getCashAcctName() {
        return cashAcctName;
    }

    public String getDesc() {
        return desc;
    }

    public PurchaseTradeBean getFilterType() {
        return filterType;
    }

    public String getNum() {
        return num;
    }

    public String getPortCode() {
        return portCode;
    }

    public BigDecimal getPortCuryRate() {
        return portCuryRate;
    }

    public String getPortName() {
        return portName;
    }

    public BigDecimal getPurchaseGain() {
        return purchaseGain;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getSecurityName() {
        return securityName;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public BigDecimal getTradeMoney() {
        return tradeMoney;
    }

    public String getTradeName() {
        return tradeName;
    }

    public BigDecimal getTradeHandleFee() {
        return tradeHandleFee;
    }

    public BigDecimal getSetServiceFee() {
        return setServiceFee;
    }

    public BigDecimal getBankHandleFee() {
        return bankHandleFee;
    }

    public String getInvMgrCode() {
        return invMgrCode;
    }

    public String getInvMgrName() {
        return invMgrName;
    }

    public String getMatureDate() {
        return matureDate;
    }

    public String getMatureSettleDate() {
        return matureSettleDate;
    }

    public String getIsOnlyColumn() {
        return isOnlyColumn;
    }

    public void setAffCorpCode(String affCorpCode) {
        this.affCorpCode = affCorpCode;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public void setTradeMoney(BigDecimal tradeMoney) {
        this.tradeMoney = tradeMoney;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setPurchaseGain(BigDecimal purchaseGain) {
        this.purchaseGain = purchaseGain;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setPortCuryRate(BigDecimal portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setFilterType(PurchaseTradeBean filterType) {
        this.filterType = filterType;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setCashAcctName(String cashAcctName) {
        this.cashAcctName = cashAcctName;
    }

    public void setCashAcctCode(String cashAcctCode) {
        this.cashAcctCode = cashAcctCode;
    }

    public void setBaseCuryRate(BigDecimal baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setBargainTime(String bargainTime) {
        this.bargainTime = bargainTime;
    }

    public void setBargainDate(String bargainDate) {
        this.bargainDate = bargainDate;
    }

    public void setAffCorpName(String affCorpName) {
        this.affCorpName = affCorpName;
    }

    public void setTradeHandleFee(BigDecimal tradeHandleFee) {
        this.tradeHandleFee = tradeHandleFee;
    }

    public void setSetServiceFee(BigDecimal setServiceFee) {
        this.setServiceFee = setServiceFee;
    }

    public void setBankHandleFee(BigDecimal bankHandleFee) {
        this.bankHandleFee = bankHandleFee;
    }

    public void setInvMgrCode(String invMgrCode) {
        this.invMgrCode = invMgrCode;
    }

    public void setInvMgrName(String invMgrName) {
        this.invMgrName = invMgrName;
    }

    public void setMatureDate(String matureDate) {
        this.matureDate = matureDate;
    }

    public void setMatureSettleDate(String matureSettleDate) {
        this.matureSettleDate = matureSettleDate;
    }

    public void setIsOnlyColumn(String isOnlyColumn) {
        this.isOnlyColumn = isOnlyColumn;
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
            this.num = reqAry[0];
            this.securityCode = reqAry[1];
            this.portCode = reqAry[2];
            this.invMgrCode = reqAry[3];
            this.tradeCode = reqAry[4];
            this.cashAcctCode = reqAry[5];
            this.affCorpCode = reqAry[6];
            this.bargainDate = reqAry[7];
            this.bargainTime = reqAry[8];
            this.settleDate = reqAry[9];
            this.matureDate = reqAry[10];
            this.matureSettleDate = reqAry[11];
            if (reqAry[12].length() != 0) {
                this.purchaseGain = new BigDecimal(
                    reqAry[12]);
            }
            if (reqAry[13].length() != 0) {
                this.portCuryRate = new BigDecimal(
                    reqAry[13]);
            }
            if (reqAry[14].length() != 0) {
                this.baseCuryRate = new BigDecimal(
                    reqAry[14]);
            }
            if (reqAry[15].length() != 0) {
                this.tradeMoney = new BigDecimal(
                    reqAry[15]);
            }
            if (reqAry[16].length() != 0) {
                this.totalCost = new BigDecimal(
                    reqAry[16]);
            }

            this.desc = reqAry[17];

            if (reqAry[18].length() != 0) {
                this.tradeHandleFee = new BigDecimal(
                    reqAry[18]);
            }
            if (reqAry[19].length() != 0) {
                this.bankHandleFee = new BigDecimal(
                    reqAry[19]);
            }
            if (reqAry[20].length() != 0) {
                this.setServiceFee = new BigDecimal(
                    reqAry[20]);
            }

            this.checkStateId = Integer.parseInt(reqAry[21]);
            this.isOnlyColumn = reqAry[22];
            this.fixNum = reqAry[23];//add by songjie 2011.12.19 BUG 3389 QDV4赢时胜（深圳）2011年12月13日01_B
            //add by songjie 2012.02.17 STORY #2262 QDV4赢时胜(上海开发部)2012年02月17日01_A
            this.strSecIssuerCode = reqAry[24];
            
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new PurchaseTradeBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
              //STORY #1509 监控管理－监控结果 添加回购冻结证券数量设置 add by jiangshichao 2011.09.17 
                if(sRowStr.split("\r\t").length == 3){
                	sRelaFreezeSec =sRowStr.split("\r\t")[2];
                }
                //STORY #1509 监控管理－监控结果 添加回购冻结证券数量设置 end 
            }
        } catch (Exception e) {
            throw new YssException("解析回购业务设置请求出错", e);
        }
    }

    /**
     * 通过拼接字符串来获取数据字符串
     * @return String
     * @throws YssException
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.num).append("\t");
        buf.append(this.securityCode).append("\t");
        buf.append(this.securityName).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.invMgrCode).append("\t");
        buf.append(this.invMgrName).append("\t");
        buf.append(this.tradeCode).append("\t");
        buf.append(this.tradeName).append("\t");
        buf.append(this.cashAcctCode).append("\t");
        buf.append(this.cashAcctName).append("\t");
        buf.append(this.affCorpCode).append("\t");
        buf.append(this.affCorpName).append("\t");
        buf.append(this.bargainDate).append("\t");
        buf.append(this.bargainTime).append("\t");
        buf.append(this.settleDate).append("\t");
        buf.append(this.matureDate).append("\t");
        buf.append(this.matureSettleDate).append("\t");
        buf.append(this.purchaseGain).append("\t");
        buf.append(this.portCuryRate).append("\t");
        buf.append(this.baseCuryRate).append("\t");
        buf.append(this.tradeMoney).append("\t");
        buf.append(this.totalCost).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.tradeHandleFee).append("\t");
        buf.append(this.bankHandleFee).append("\t");
        buf.append(this.setServiceFee).append("\t");
        buf.append(this.fixNum).append("\t");//add by songjie 2011.12.19 BUG 3389 QDV4赢时胜（深圳）2011年12月13日01_B
        //---add by songjie 2012.02.17 STORY #2262 QDV4赢时胜(上海开发部)2012年02月17日01_A start---//
        buf.append(this.strSecIssuerCode).append("\t");
        buf.append(this.strSecIssuerName).append("\t");
        //---add by songjie 2012.02.17 STORY #2262 QDV4赢时胜(上海开发部)2012年02月17日01_A end---//
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * 为各项变量赋值
     * @param rs ResultSet
     * @throws SQLException
     */
    public void setPurchaseTradeAttr(ResultSet rs) throws SQLException,
        YssException {
        this.num = rs.getString("FNum") + "";
        this.securityCode = rs.getString("FSecurityCode") + "";
        this.securityName = rs.getString("FSecurityName") + "";
        this.portCode = rs.getString("FPortCode") + "";
        this.portName = rs.getString("FPortName") + "";
        this.invMgrCode = rs.getString("FInvMgrCode") + "";
        this.invMgrName = rs.getString("FInvMgrName") + "";
        this.tradeCode = rs.getString("FTradeTypeCode") + "";
        this.tradeName = rs.getString("FTradeTypeName") + "";
        this.cashAcctCode = rs.getString("FCashAccCode") + "";
        this.cashAcctName = rs.getString("FCashAccName") + "";
        this.affCorpCode = rs.getString("FAffCorpCode") + "";
        this.affCorpName = rs.getString("FAffCorpName") + "";
        this.bargainDate = rs.getDate("FBargainDate") + "";
        this.bargainTime = rs.getString("FBargainTime") + "";
        this.settleDate = rs.getDate("FSettleDate") + "";
        this.matureDate = rs.getDate("FMatureDate") + "";
        this.matureSettleDate = rs.getDate("FMatureSettleDate") + "";
        this.purchaseGain = rs.getBigDecimal("FPurchaseGain");
        this.portCuryRate = rs.getBigDecimal("FPortCuryRate");
        this.baseCuryRate = rs.getBigDecimal("FBaseCuryRate");
        this.tradeMoney = rs.getBigDecimal("FTradeMoney");
        this.totalCost = rs.getBigDecimal("FTotalCost");
        this.desc = rs.getString("FDesc") + "";
        this.tradeHandleFee = rs.getBigDecimal("FTradeHandleFee");
        this.bankHandleFee = rs.getBigDecimal("FBankHandleFee");
        this.setServiceFee = rs.getBigDecimal("FSetServiceFee");
        this.fixNum = rs.getString("FFixNum");//add by songjie 2011.12.19 BUG 3389 QDV4赢时胜（深圳）2011年12月13日01_B
        //---add by songjie 2012.02.17 STORY #2262 QDV4赢时胜(上海开发部)2012年02月17日01_A start---//
        this.strSecIssuerCode = rs.getString("FSecIssuerCode");
        this.strSecIssuerName = rs.getString("FSecIssuerName");
        //---add by songjie 2012.02.17 STORY #2262 QDV4赢时胜(上海开发部)2012年02月17日01_A end---//
        super.setRecLog(rs);
    }
}
