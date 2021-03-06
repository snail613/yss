package com.yss.main.operdata;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.operdeal.*;
import com.yss.main.operdeal.bond.*;
import com.yss.main.parasetting.*;
import com.yss.manager.*;
import com.yss.pojo.param.bond.*;
import com.yss.util.*;

public class TradeBean
    extends BaseDataSettingBean implements IDataSetting {
    private String num = ""; //交易数据流水号
    private String securityCode = ""; //交易证券代码
    private String securityName = ""; //交易证券名称
    private String portCode = ""; //组合代码
    private String portName = ""; //组合名称
    private String brokerCode = ""; //券商代码
    private String brokerName = ""; //券商名称
    private String invMgrCode = ""; //投资经理代码
    private String invMgrName = ""; //投资经理名称
    private String tradeCode = ""; //交易方式代码
    private String tradeName = ""; //交易方式名称
    private String cashAcctCode = ""; //现金帐户代码
    private String cashAcctName = ""; //现金帐户名称
    private String attrClsCode = ""; //所属分类代码
    private String attrClsName = ""; //所属分类名称
    private String bargainDate = "1900-01-01"; //成交日期
    private String bargainTime = "00:00:00"; //成交时间
    private String settleDate = "1900-01-01"; //结算日期
    private String settleTime = "00:00:00"; //结算时间
    private String autoSettle = "0"; //自动结算
    private double portCuryRate; //组合汇率
    private double baseCuryRate; //基础汇率
    private double tradeAmount; //交易数量
    private double tradePrice; //交易价格
    private double tradeMoney; //交易总额
    private double unitCost; //单位成本
    private double accruedInterest; //应计利息
    private double allotFactor; //分配因子
    private double totalCost; //投资总成本
    private String orderNum = ""; //订单代码
    private String desc = ""; //交易描述
    private double handAmount; //每手股数
    private double haveAmount; //持有股数/现金
    //private String isOnlyColumns = ""; //是否只读取列名的标志
    private String tradeCuryCode = ""; //交易货币代码
    private String portCuryCode = ""; //组合货币代码
    private double factor; //报价因子
    private String fees = "";
    //===============合并中保版本
    private double dFactRate = 0; //实际利率，债券持有到期时用到
    private String tmpSec = "";
    //===========================
    private String FFeeCode1 = ""; //为了直接在后台进行汇总计算
    private String FFeeCode2 = "";
    private String FFeeCode3 = "";
    private String FFeeCode4 = "";
    private String FFeeCode5 = "";
    private String FFeeCode6 = "";
    private String FFeeCode7 = "";
    private String FFeeCode8 = "";

    private double FTradeFee1; //为了直接在后台进行汇总计算
    private double FTradeFee2;
    private double FTradeFee3;
    private double FTradeFee4;
    private double FTradeFee5;
    private double FTradeFee6;
    private double FTradeFee7;
    private double FTradeFee8;
    //------------------------------------------------
    //------------------------------------------------
    private double bailMoney; //保证金金额
    private TradeBean filterType;
    private String tradesub;
    private String sRecycled = ""; //为增加还原和删除功能加的一个中介字符串变量 bug MS00149 2009.01.13 方浩
    /*xuqiji 20090825 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
     *操作类型 界面上输入'HD_JK' FDataSouce=0，权益处理:'HD_QY' FDataSouce=0
     *接口：读入'ZD_JK'   FDataSource=1 ，权益处理 'ZD_QY'  FDataSource=1
     */
    private String dsType = "";

    public void setDsType(String dsType) {
        this.dsType = dsType;
    }

    public String getDsType() {
        return dsType;
    }
    //--------------------------------end-------------------------------//

    public String getTradeCode() {
        return tradeCode;
    }

    public String getPortCode() {
        return portCode;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public String getCashAcctCode() {
        return cashAcctCode;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public double getPortCuryRate() {
        return portCuryRate;
    }

    public String getBargainDate() {
        return bargainDate;
    }

    public double getTradeMoney() {
        return tradeMoney;
    }

    public String getNum() {
        return num;
    }

    public String getSettleTime() {
        return settleTime;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public double getAllotFactor() {
        return allotFactor;
    }

    public double getTradePrice() {
        return tradePrice;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getInvMgrCode() {
        return invMgrCode;
    }

    public String getAutoSettle() {
        return autoSettle;
    }

    public String getBargainTime() {
        return bargainTime;
    }

    public String getDesc() {
        return desc;
    }

    public double getTradeAmount() {
        return tradeAmount;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public double getAccruedInterest() {
        return accruedInterest;
    }

    public String getFees() {
        return fees;
    }

    public double getBaseCuryRate() {
        return baseCuryRate;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setAttrClsCode(String attrClsCode) {
        this.attrClsCode = attrClsCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public void setCashAcctCode(String cashAcctCode) {
        this.cashAcctCode = cashAcctCode;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public void setPortCuryRate(double portCuryRate) {
        this.portCuryRate = portCuryRate;
    }

    public void setBargainDate(String bargainDate) {
        this.bargainDate = bargainDate;
    }

    public void setTradeMoney(double tradeMoney) {
        this.tradeMoney = tradeMoney;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setSettleTime(String settleTime) {
        this.settleTime = settleTime;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public void setAllotFactor(double allotFactor) {
        this.allotFactor = allotFactor;
    }

    public void setTradePrice(double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setInvMgrCode(String invMgrCode) {
        this.invMgrCode = invMgrCode;
    }

    public void setAutoSettle(String autoSettle) {
        this.autoSettle = autoSettle;
    }

    public void setBargainTime(String bargainTime) {
        this.bargainTime = bargainTime;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setTradeAmount(double tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public void setAccruedInterest(double accruedInterest) {
        this.accruedInterest = accruedInterest;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public void setBaseCuryRate(double baseCuryRate) {
        this.baseCuryRate = baseCuryRate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public void setHandAmount(double handAmount) {
        this.handAmount = handAmount;
    }

    public void setHaveAmount(double haveAmount) {
        this.haveAmount = haveAmount;
    }

    public void setBailMoney(double bailMoney) {
        this.bailMoney = bailMoney;
    }

    public void setFFeeCode5(String FFeeCode5) {
        this.FFeeCode5 = FFeeCode5;
    }

    public void setFFeeCode6(String FFeeCode6) {
        this.FFeeCode6 = FFeeCode6;
    }

    public void setFTradeFee4(double FTradeFee4) {
        this.FTradeFee4 = FTradeFee4;
    }

    public void setFTradeFee6(double FTradeFee6) {
        this.FTradeFee6 = FTradeFee6;
    }

    public void setFFeeCode3(String FFeeCode3) {
        this.FFeeCode3 = FFeeCode3;
    }

    public void setFFeeCode2(String FFeeCode2) {
        this.FFeeCode2 = FFeeCode2;
    }

    public void setFTradeFee8(double FTradeFee8) {
        this.FTradeFee8 = FTradeFee8;
    }

    public void setFFeeCode1(String FFeeCode1) {
        this.FFeeCode1 = FFeeCode1;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public void setFTradeFee5(double FTradeFee5) {
        this.FTradeFee5 = FTradeFee5;
    }

    public void setFFeeCode4(String FFeeCode4) {
        this.FFeeCode4 = FFeeCode4;
    }

    public void setFFeeCode7(String FFeeCode7) {
        this.FFeeCode7 = FFeeCode7;
    }

    public void setFTradeFee2(double FTradeFee2) {
        this.FTradeFee2 = FTradeFee2;
    }

    public void setFTradeFee3(double FTradeFee3) {
        this.FTradeFee3 = FTradeFee3;
    }

    public void setFTradeFee1(double FTradeFee1) {
        this.FTradeFee1 = FTradeFee1;
    }

    public void setPortCuryCode(String portCuryCode) {
        this.portCuryCode = portCuryCode;
    }

    public void setFTradeFee7(double FTradeFee7) {
        this.FTradeFee7 = FTradeFee7;
    }

    public void setFFeeCode8(String FFeeCode8) {
        this.FFeeCode8 = FFeeCode8;
    }

    public String getAttrClsCode() {
        return attrClsCode;
    }

    public double getHandAmount() {
        return handAmount;
    }

    public double getHaveAmount() {
        return haveAmount;
    }

    public double getBailMoney() {
        return bailMoney;
    }

    public String getFFeeCode5() {
        return FFeeCode5;
    }

    public String getFFeeCode6() {
        return FFeeCode6;
    }

    public double getFTradeFee4() {
        return FTradeFee4;
    }

    public double getFTradeFee6() {
        return FTradeFee6;
    }

    public String getFFeeCode3() {
        return FFeeCode3;
    }

    public String getFFeeCode2() {
        return FFeeCode2;
    }

    public double getFTradeFee8() {
        return FTradeFee8;
    }

    public String getFFeeCode1() {
        return FFeeCode1;
    }

    public double getFactor() {
        return factor;
    }

    public double getFTradeFee5() {
        return FTradeFee5;
    }

    public String getFFeeCode4() {
        return FFeeCode4;
    }

    public String getFFeeCode7() {
        return FFeeCode7;
    }

    public double getFTradeFee2() {
        return FTradeFee2;
    }

    public double getFTradeFee3() {
        return FTradeFee3;
    }

    public double getFTradeFee1() {
        return FTradeFee1;
    }

    public String getPortCuryCode() {
        return portCuryCode;
    }

    public double getFTradeFee7() {
        return FTradeFee7;
    }

    public String getFFeeCode8() {
        return FFeeCode8;
    }

    public TradeBean() {
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
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
                if (sRowStr.split("\r\t").length == 3) {
                    this.tradesub = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //bug MS00149 2009.01.13 方浩
            reqAry = sTmpStr.split("\t");
            this.num = reqAry[0];
            this.securityCode = reqAry[1];
            this.portCode = reqAry[2];
            this.brokerCode = reqAry[3];
            this.invMgrCode = reqAry[4];
            this.tradeCode = reqAry[5];
            this.cashAcctCode = reqAry[6];
            this.attrClsCode = reqAry[7];
            this.bargainDate = reqAry[8];
            this.bargainTime = reqAry[9];
            this.settleDate = reqAry[10];
            this.settleTime = reqAry[11];
            this.autoSettle = reqAry[12];
            if (reqAry[13].length() != 0) {
                this.portCuryRate = Double.parseDouble(
                    reqAry[13]);
            }
            if (reqAry[14].length() != 0) {
                this.baseCuryRate = Double.parseDouble(
                    reqAry[14]);
            }
            if (reqAry[15].length() != 0) {
                this.tradeAmount = Double.parseDouble(
                    reqAry[15]);
            }
            if (reqAry[16].length() != 0) {
                this.tradePrice = Double.parseDouble(
                    reqAry[16]);
            }
            if (reqAry[17].length() != 0) {
                this.tradeMoney = Double.parseDouble(
                    reqAry[17]);
            }
            if (reqAry[18].length() != 0) {
                this.unitCost = Double.parseDouble(reqAry[
                    18]);
            }
            if (reqAry[19].length() != 0) {
                this.accruedInterest = Double.parseDouble(
                    reqAry[19]);
            }
            if (reqAry[20].length() != 0) {
                this.allotFactor = Double.parseDouble(
                    reqAry[20]);
            }
            if (reqAry[21].length() != 0) {
                this.totalCost = Double.parseDouble(
                    reqAry[21]);
            }
            this.orderNum = reqAry[22];
            this.tradeCuryCode = reqAry[23];
            this.portCuryCode = reqAry[24];
            this.desc = reqAry[25];
            this.fees = reqAry[26].replaceAll("~", "\t");
            if (reqAry[27].length() != 0) {
                this.bailMoney = Double.parseDouble(
                    reqAry[27]);
            }
            this.checkStateId = Integer.parseInt(reqAry[28]);
            this.isOnlyColumns = reqAry[29];
            //=======合并中保版本
            if (YssFun.isNumeric(reqAry[30])) {
                this.dFactRate = YssFun.toDouble(reqAry[30]);
            }
            //=========================
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TradeBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }

        } catch (Exception e) {
            throw new YssException("解析交易数据设置请求出错", e);
        }

    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if(pub.isBrown()==true) //add by ysh 20111025 STORY 1285  如果要浏览数据，则直接返回
			return " where 1=1";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.isOnlyColumns.equals("1")) {
                sResult = sResult + " and 1=2 ";
                return sResult;
            }
            if (this.filterType.portCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.portCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.tradeCode.length() != 0) {
                sResult = sResult + " and a.FTradeTypeCode = '" +
                    filterType.tradeCode.replaceAll("'", "''") + "'";
            }
            if (this.filterType.bargainDate.length() != 0 &&
                !this.filterType.bargainDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FBargainDate = " +
                    dbl.sqlDate(filterType.bargainDate);
            }
            if (this.filterType.securityCode.length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.securityCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.brokerCode.length() == 1) {
                sResult = sResult + " and FBrokerCode = " +
                    filterType.brokerCode;
            }
            if (this.filterType.invMgrCode.length() != 0) {
                sResult = sResult + " and a.FInvMgrCode like '" +
                    filterType.invMgrCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.cashAcctCode.length() != 0) {
                sResult = sResult + " and a.FCashAccCode like '" +
                    filterType.cashAcctCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.attrClsCode.length() != 0) {
                sResult = sResult + " and a.FAttrClsCode like '" +
                    filterType.attrClsCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.settleDate.length() != 0 &&
                !this.filterType.settleDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FSettleDate = " +
                    dbl.sqlDate(filterType.settleDate);
            }
            if (this.filterType.autoSettle.length() == 1) { 
                sResult = sResult + " and a.FAutoSettle =" +
                    filterType.autoSettle;
            }
        }
        return sResult;

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
        String strSql = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ITradeOperation tradeOper = (ITradeOperation) pub.getOperDealCtx().
            getBean("tradedeal");
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            tradeOper.setYssPub(pub);
            //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if (this.filterType != null && this.filterType.isOnlyColumns.equals("1")&&!(pub.isBrown())) {
            	VocabularyBean vocabulary = new VocabularyBean();
                vocabulary.setYssPub(pub);
                sVocStr = vocabulary.getVoc(YssCons.YSS_TRD_AUTO);
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                    "\r\f" +
                    this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr()+"\r\f" + "voc" + sVocStr + //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
                    (tmpSec.length() == 0 ? "" : "\r\f" + tmpSec);
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            strSql =
                "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
                " d.FPortName, e.FSecurityName, e.FHandAmount, f.FInvMgrName, g.FTradeTypeName, " +
                " h.FBrokerName as FBrokerName, o.FCashAccName, p.FAttrClsName,e.FFactor,e.FTradeCury,e.FFactRate " +
                " from " + pub.yssGetTableName("Tb_Data_Trade") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
        
                " left join (select FPortCode,FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +" where FCheckState = 1 ) d on a.FPortCode = d.FPortCode "+
                //end by lidaolong 
                //----------------------------------------------------------------------------------------------------
                //" left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                " left join (select eb.*,case when ec.FFactRate is null then 0 else ec.FFactRate end FFactRate from (select FSecurityCode, max(FStartDate) as FStartDate from " + //合并中保版本
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate, " +
                " FHandAmount,FFactor,FTradeCury from " +
                pub.yssGetTableName("Tb_Para_Security") +
                //") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode " +
                ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate " +
                " left join(select * from " +
                pub.yssGetTableName("Tb_Para_Fixinterest") + " ) ec on ea.FSecurityCode = ec.FSecurityCode) e on a.FSecurityCode = e.FSecurityCode " +
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                /*" left join (select fb.* from (select FInvMgrCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_InvestManager") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FInvMgrCode) fa join (select FInvMgrCode, FInvMgrName, FStartDate from " +
                pub.yssGetTableName("Tb_Para_InvestManager") +
                ") fb on fa.FInvMgrCode = fb.FInvMgrCode and fa.FStartDate = fb.FStartDate) f on a.FInvMgrCode = f.FInvMgrCode " +
               */
                
                " left join (select FInvMgrCode, FInvMgrName from " +
                pub.yssGetTableName("Tb_Para_InvestManager") +
                " where  FCheckState = 1) f on a.FInvMgrCode = f.FInvMgrCode " +
               
                //end by lidaolong 
                //----------------------------------------------------------------------------------------------------
                " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode" +
                //----------------------------------------------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
             /*   " left join (select hb.* from (select FBrokerCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Broker") +
                " where FStartDate <= " +
                dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FBrokerCode) ha join (select FBrokerCode, FBrokerName, FStartDate from " +
                pub.yssGetTableName("Tb_Para_Broker") +
                ") hb on ha.FBrokerCode = hb.FBrokerCode and ha.FStartDate = hb.FStartDate) h on a.FBrokerCode = h.FBrokerCode " +
                */
                " left join (select FBrokerCode, FBrokerName from " +
                pub.yssGetTableName("Tb_Para_Broker") +
                " where  FCheckState = 1 ) h on a.FBrokerCode = h.FBrokerCode " +
                
                
                //end by lidaolong 
                //----------------------------------------------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
            /*    " left join (select ob.* from (select FCashAccCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FCashAccCode) oa join (select FCashAccCode, FCashAccName, FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                ") ob on oa.FCashAccCode = ob.FCashAccCode and oa.FStartDate = ob.FStartDate) o on a.FCashAccCode = o.FCashAccCode " +
               */
                " left join (select FCashAccCode, FCashAccName, FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1) o on a.FCashAccCode = o.FCashAccCode " +
               
                //----------------------------------------------------------------------------------------------------
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                " where FCheckState = 1) p on a.FAttrClsCode = p.FAttrClsCode " +
                buildFilterSql() +
                " order by a.FCheckState, a.FCreateTime desc";
            //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("Trade");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
                this.dFactRate = rs.getDouble("FFactRate");
                //转载已经维护的交易数据，为什么还要进行交易操作的初始化呢？持有数量没用的？fazmm20070906
                //tradeOper.initTradeOperation(this.securityCode, this.brokerCode,
                //                             this.invMgrCode,
                //                             this.tradeCode, this.orderNum,
                //                             YssFun.toDate(this.bargainDate));
                //this.haveAmount = tradeOper.getHave(this.portCode);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_TRD_AUTO);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" +
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr()+"\r\f" + "voc" + sVocStr + //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
                (tmpSec.length() == 0 ? "" : "\r\f" + tmpSec);
        } catch (Exception e) {
            throw new YssException("获取交易数据信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
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
    public String getListViewData3() {
        return "";
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
    }

    /*
        //配置ListView显示列时对特殊列的操作
        public void beforeBuildRowShowStr(YssCancel bCancel, String sColName,
                                      ResultSet rs, StringBuffer buf) throws
          SQLException {
       String sFieldName = "";
       String sFieldFormat = "";
       if (sColName.indexOf("FTotalCost") >= 0) {
          if (sColName.indexOf(";") > 0) {
             sFieldName = sColName.split(";")[0];
             sFieldFormat = sColName.split(";")[1];
          }
          else {
             sFieldName = sColName;
          }
          if (rs.getDouble("FFactor")==0){
             buf.append(rs.getString("FTotalCost")).append("\t");
          }else{
     buf.append(YssFun.formatNumber(YssFun.roundIt(YssD.add(YssD.div(YssD.mul(rs.
                   getDouble("FTradeAmount"), rs.getDouble("FTradePrice")),rs.getDouble("FFactor")),
                   rs.getDouble("FAccruedinterest")), 2), sFieldFormat) +
                   "").append("\t");
          }
          bCancel.setCancel(true);
       }
        }
     */


    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        String strNumDate = "";
        String sStartNum="0";//编号按业务类型生成 by leeyu 201006702 QDV4中保2010年07月02日01_B 合并太平版本代码
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
        	//编号按业务类型生成 by leeyu 201006702 QDV4中保2010年07月02日01_B 合并太平版本代码
        	if(this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_Buy)){//买入
        		sStartNum = "2";
        	}else if(this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)){//卖出
        		sStartNum = "9";
        	}else if(this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PX)||//派息
        			this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_SG)||//送股
        			this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_PG)){//配股
        		sStartNum = "1";
        	}else if(this.tradeCode.equalsIgnoreCase(YssOperCons.Yss_JYLX_ZQ)){//债券兑付
        		sStartNum = "8";
        	}else{
        		sStartNum = "0";
        	}
        	//编号按业务类型生成 by leeyu 201006702 QDV4中保2010年07月02日01_B 合并太平版本代码
            if (this.num.length() == 0) {
                strNumDate = YssFun.formatDatetime(YssFun.toDate(this.
                    bargainDate)).
                    substring(0, 8);
                this.num = strNumDate +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_Trade"),
                                           dbl.sqlRight("FNUM", 6),
                                           //"000000",
                                           sStartNum + "00000",//编号按业务类型生成 by leeyu 201006702 QDV4中保2010年07月02日01_B 合并太平版本代码
                                           " where FNum like 'T"
                                           //+ strNumDate + "%'", 1);
                                           + strNumDate + sStartNum + "%'", 1);//编号按业务类型生成 by leeyu 201006702 QDV4中保2010年07月02日01_B 合并太平版本代码
                this.num = "T" + this.num;
            }
            conn.setAutoCommit(false);
            bTrans = true;
            if (this.num.length() > 0) {
                strSql = "delete from " +
                    pub.yssGetTableName("tb_cash_subtransfer") +
                    " a " +
                    "where  exists(select * from " +
                    pub.yssGetTableName("tb_cash_transfer") + " b " +
                    "where a.fnum = b.fnum and b.ftradenum like '" +
                    this.num +
                    "%' )";
                dbl.executeSql(strSql);

                strSql = "delete from " +
                    pub.yssGetTableName("tb_cash_transfer") +
                    " where FTradeNum like '" + this.num + "%'";
                dbl.executeSql(strSql);

                strSql = "delete from " + pub.yssGetTableName("Tb_Data_Trade") +
                    " where FNum = " + dbl.sqlString(this.num);
                dbl.executeSql(strSql);

                strSql = "delete from " +
                         pub.yssGetTableName("Tb_Data_SubTrade") +
                         //--- MS00813 QDV4中保2009年11月16日01_B  蒋世超  2009.11.27 添加 ---------------
                         " where FNum like '" + this.num + "%' and fordernum<>' '"; 
                         //--- MS00813 QDV4中保2009年11月16日01_B end ---------------------------------
                dbl.executeSql(strSql);
            }
            if (attrClsCode != null &&
                this.attrClsCode.equalsIgnoreCase(YssOperCons.Yss_JYLX_CYDQ)) { //处理债券持有到期
                //2008.10.26 蒋锦 修改 使用反射实例化
                FixInterestBean fixInterest = (FixInterestBean) pub.
                    getParaSettingCtx().getBean(
                        "fixinterest");
                fixInterest.setYssPub(pub);
                fixInterest.setStrSecurityCode(this.securityCode);
                fixInterest.getSetting();
                fixInterest.setDFactRate(this.dFactRate);
                //fixInterest.setStrIssuePrice(this.tradePrice);
                fixInterest.setStrIssuePrice(new java.math.BigDecimal(tradePrice)); //修改精度QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090211
                //2008.10.26 蒋锦 为公共信息赋值
                ( (BaseDataSettingBean) fixInterest).parseRecLog();
                if (fixInterest.getDStartDate() == null &&
                    fixInterest.getDtIssueDate() == null) {
                    throw new YssException("代码为:" + securityCode +
                                           "债券信息不存在,请先补充!");
                }
                this.securityCode = fixInterest.addFixInterestCYDQ(fixInterest,
                    YssFun.toDate(bargainDate));
                tmpSec = securityCode;
            }
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_Trade") +
                "(FNUM,FSECURITYCODE,FPORTCODE,FBROKERCODE,FINVMGRCODE,FTRADETYPECODE," +
                " FCASHACCCODE,FATTRCLSCODE,FBARGAINDATE,FBARGAINTIME," +
                " FSETTLEDATE,FSETTLETIME,FAUTOSETTLE,FPORTCURYRATE,FBASECURYRATE,FALLOTFACTOR," +
                " FTRADEAMOUNT,FTRADEPRICE,FTRADEMONEY,FUNITCOST,FACCRUEDINTEREST," +
                " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4," +
                " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8," +
                " FTotalCost, FOrderNum, FBailMoney,FDesc, FCheckState, FCreator, FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.num) + "," +
                dbl.sqlString(this.securityCode) + "," +
                dbl.sqlString(this.portCode) + "," +
                dbl.sqlString(this.brokerCode) + "," +
                dbl.sqlString(this.invMgrCode.length() != 0 ?
                              this.invMgrCode :
                              " ") + "," +
                dbl.sqlString(this.tradeCode) + "," +
                dbl.sqlString(this.cashAcctCode.length() == 0 ? " " :
                              this.cashAcctCode) + "," +
                dbl.sqlString(this.attrClsCode) + "," +
                dbl.sqlDate(this.bargainDate) + "," +
                dbl.sqlString(this.bargainTime) + "," +
                dbl.sqlDate(this.settleDate) + "," +
                dbl.sqlString(this.settleTime) + "," +
                this.autoSettle + "," +
                this.portCuryRate + "," +
                this.baseCuryRate + "," +
                this.allotFactor + "," +
                this.tradeAmount + "," +
                this.tradePrice + "," +
                this.tradeMoney + "," +
                this.unitCost + "," +
                this.accruedInterest + "," +
                this.operSql.buildSaveFeesSql(YssCons.OP_ADD, this.fees) +
                this.totalCost + "," +
                dbl.sqlString(this.orderNum) + "," +
                this.bailMoney + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                ")";

            dbl.executeSql(strSql);

            if (this.tradesub != null) {
                TradeSubBean tradesub = new TradeSubBean();
                tradesub.setYssPub(pub);
                tradesub.saveMutliOperData(this.tradesub, true, this.num, this);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
//         return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增交易数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {
    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        /*    Connection conn = dbl.loadConnection();
            boolean bTrans = false;
            String strSql = "";
            try {
               strSql = "update " + pub.yssGetTableName("Tb_Data_Trade") +
                     " set FCheckState = " +
                     this.checkStateId + ", FCheckUser = " +
                     dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" +
                     YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where FNum = " +
                     dbl.sqlString(this.num);

               conn.setAutoCommit(false);
               bTrans = true;
               dbl.executeSql(strSql);

               strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
                     " set FCheckState = " +
                     this.checkStateId + ", FCheckUser = " +
                     dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" +
                     YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where FNum like '" +
                     this.num + "%'";
               // 审核证券应收应付
               this.operSecRecPay(this.num);

               dbl.executeSql(strSql);
               conn.commit();
               bTrans = false;
               conn.setAutoCommit(true);
            }

            catch (Exception e) {
               throw new YssException("审核交易数据信息出错", e);
            }
            finally {
               dbl.endTransFinal(conn, bTrans);
            }
         */
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环删除
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //打开一个数据库联接
        try {
            conn.setAutoCommit(false); //开启一个事物
            bTrans = true; //代表是否关闭事务
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if ( sRecycled != null&&(!sRecycled.equalsIgnoreCase(""))) { //判断传来的内容是否为空
                arrData = sRecycled.split("\r\n"); //解析它，把它还原成条目放在数组里。
                for (int i = 0; i < arrData.length; i++) { //循环数组，也就是循环还原条目
                    if (arrData[i].length() == 0) {
                        continue; //如果数组里没有内容就执行下一个内容
                    }
                    this.parseRowStr(arrData[i]); //解析这个数组里的内容
                    strSql = "update " + pub.yssGetTableName("Tb_Data_Trade") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FNum = " +
                        dbl.sqlString(this.num); //更新数据的SQL语句
                    dbl.executeSql(strSql); //执行更新操作
                }
            }
            //如果sRecycled为空，而num不为空，则按照num来执行sql语句
            else if ( num != null&&(!num.equalsIgnoreCase("")) ) {
                strSql = "update Tb_Base_AccountType set FCheckState = " +
                    this.checkStateId + ",FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ",FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FAccTypeCode = " +
                    dbl.sqlString(this.num); //更新数据的SQL语句
                dbl.executeSql(strSql); //执行更新操作
            }
            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核交易数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }
        //----------------end

    }

    //根据交易数据的一个编号 来获得业务数据  by sunny
    public void operSecRecPay(String strNum) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String tsfTypeCode = "";
        String subTsfTypeCode = "";
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " where SUBSTR(FNUM,1,15) in" +
                " (select FNum from " +
                pub.yssGetTableName("Tb_Data_Trade") + " where FNum like " +
                dbl.sqlString(strNum) + ")";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (rs.getString("FTradeTypeCode").equalsIgnoreCase("01")) {
                    tsfTypeCode = "06";
                    subTsfTypeCode = "06FI_B";
                } else {
                    tsfTypeCode = "07";
                    subTsfTypeCode = "07FI";
                }
                SecRecPayAdmin secrecpay = new SecRecPayAdmin();
                secrecpay.setYssPub(pub);
                if (this.checkStateId == 2) {
                    secrecpay.delete("", rs.getDate("FBargainDate"),
                                     rs.getDate("FBargainDate"), tsfTypeCode,
                                     subTsfTypeCode,
                                     rs.getString("FSecurityCode"),
                                     operFun.getSecCuryCode(rs.getString(
                                         "FSecurityCode")),
                                     rs.getString("FPortCode"),
                                     rs.getString("FInvMgrCode"),
                                     rs.getString("FBrokerCode"),
                                     "", 0);
                } else {
                    secrecpay.checkSecRecPay("", rs.getDate("FBargainDate"),
                                             rs.getDate("FBargainDate"),
                                             tsfTypeCode, subTsfTypeCode,
                                             rs.getString("FSecurityCode"),
                                             operFun.getSecCuryCode(rs.
                        getString("FSecurityCode")),
                                             rs.getString("FPortCode"),
                                             rs.getString("FInvMgrCode"),
                                             rs.getString("FBrokerCode"), "",
                                             0, this.checkStateId);
                }
            }
        } catch (Exception e) {
            throw new YssException("获取交易数据信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Data_Trade") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FNum = " +
                dbl.sqlString(this.num);
            dbl.executeSql(strSql);

            strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FNum like '" +
                this.num + "%'";
            dbl.executeSql(strSql);

            strSql = "update " + pub.yssGetTableName("tb_cash_transfer") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FTradeNum in (" +
                "select FNum from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FNum like '" + this.num + "%')";
            dbl.executeSql(strSql);
            //By sunny
            this.operSecRecPay(this.num);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除交易数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

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
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Data_Trade") +
                " set FSETTLEDATE = " + dbl.sqlDate(this.settleDate) +
                //--------增加交易数据可修改的部分。sj edit 20081016 暂无 bug ---//
                ",FBROKERCODE = " + dbl.sqlString(this.brokerCode) +
                ",FINVMGRCODE = " + dbl.sqlString(this.invMgrCode==null||this.invMgrCode.length()==0?" ":this.invMgrCode) +//合并太平版本调整 by leeyu 20100812
                ",FATTRCLSCODE = " + dbl.sqlString(this.attrClsCode) +
                ",FAUTOSETTLE = " + dbl.sqlString(this.autoSettle) +
                //---------------------------------------------------------//
                /* " set FSECURITYCODE = " + dbl.sqlString(this.securityCode) +
                                      ",FPORTCODE = " + dbl.sqlString(this.portCode) +
                                      ",FBROKERCODE = " + dbl.sqlString(this.brokerCode) +
                                      ",FINVMGRCODE = " + dbl.sqlString(this.invMgrCode) +
                                      ",FTRADETYPECODE = " + dbl.sqlString(this.tradeCode) +
                                   ",FCASHACCCODE = " +
                                      dbl.sqlString(this.cashAcctCode.length() == 0 ? " " :
                           this.cashAcctCode) +
                                      ",FATTRCLSCODE = " + dbl.sqlString(this.attrClsCode) +
                                      ",FBARGAINDATE = " + dbl.sqlDate(this.bargainDate) +
                                      ",FBARGAINTIME = " + dbl.sqlString(this.bargainTime) +
                                      ",FSETTLEDATE = " + dbl.sqlDate(this.settleDate) +
                                      ",FSETTLETIME = " + dbl.sqlString(this.settleTime) +
                                      ",FAUTOSETTLE = " + dbl.sqlString(this.autoSettle) +
                                      ",FPORTCURYRATE = " + this.portCuryRate +
                                      ",FBASECURYRATE = " + this.baseCuryRate +
                                      ",FALLOTFACTOR = " + this.allotFactor +
                                      ",FTRADEAMOUNT = " + this.tradeAmount +
                                   ",FTRADEPRICE = " + this.tradePrice +
                                   ",FTRADEMONEY = " + this.tradeMoney +
                                   ",FUNITCOST = " + this.unitCost +
                                      ",FACCRUEDINTEREST = " + this.accruedInterest +
                                   ",FTOTALCOST = " + this.totalCost +
                                      ",FORDERNUM = " + dbl.sqlString(this.orderNum) +
                                      ",FDESC = " + dbl.sqlString(this.desc) +
                                      "," + this.operSql.buildSaveFeesSql(YssCons.OP_EDIT, this.fees) +
                                      " FCREATOR = " + dbl.sqlString(this.creatorCode) +
                                      ",FCREATETIME = " + dbl.sqlString(this.creatorTime) +*/
                " where FNUM = " + dbl.sqlString(this.num);
            dbl.executeSql(strSql);
            strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " set FSettleDate=" + dbl.sqlDate(this.settleDate) +
                //--------增加拆分数据可修改的部分。sj edit 20081016 暂无 bug ---//
                ",FBROKERCODE = " + dbl.sqlString(this.brokerCode) +
                //update by guolongchao 20111209 bug 3200 若投资经理为null,则使用空格代替
                ",FINVMGRCODE = " + dbl.sqlString(this.invMgrCode==null||this.invMgrCode.length()==0?" ":this.invMgrCode) +
                ",FATTRCLSCODE = " + dbl.sqlString(this.attrClsCode) +
                //----------------------------------------------------------//
                ",FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FNum like '" +
                this.num + "%'";
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改交易数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

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
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Data_Trade") +
                " where FNum = " + this.num;
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.securityCode = rs.getString("FSecurityCode") + "";
                this.portCode = rs.getString("FPortCode") + "";
                this.brokerCode = rs.getString("FBrokerCode") + "";
                this.invMgrCode = rs.getString("FInvMgrCode") + "";
                this.tradeCode = rs.getString("FTradeTypeCode") + "";
                this.cashAcctCode = rs.getString("FCashAccCode") + "";
                this.attrClsCode = rs.getString("FAttrClsCode") + "";
                this.bargainDate = rs.getDate("FBargainDate") + "";
                this.bargainTime = rs.getString("FBargainTime") + "";
                this.settleDate = rs.getDate("FSettleDate") + "";
                this.settleTime = rs.getString("FSettleTime") + "";
                this.autoSettle = rs.getString("FAutoSettle") + "";
                this.portCuryRate = rs.getDouble("FPortCuryRate");
                this.baseCuryRate = rs.getDouble("FBaseCuryRate");
                this.allotFactor = rs.getDouble("FAllotFactor");
                this.tradeAmount = rs.getDouble("FTradeAmount");
                this.tradePrice = rs.getDouble("FTradePrice");
                this.tradeMoney = rs.getDouble("FTradeMoney");
                this.unitCost = rs.getDouble("FUnitCost");
                this.accruedInterest = rs.getDouble("FAccruedInterest");
                this.totalCost = rs.getDouble("FTotalCost");
                this.orderNum = rs.getString("FOrderNum");
                this.desc = rs.getString("FDesc");
            }
            return this;
        } catch (Exception e) {
            throw new YssException("获取交易数据信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

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
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.num).append("\t");
        buf.append(this.securityCode).append("\t");
        buf.append(this.securityName).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.brokerCode).append("\t");
        buf.append(this.brokerName).append("\t");
        buf.append(this.invMgrCode).append("\t");
        buf.append(this.invMgrName).append("\t");
        buf.append(this.tradeCode).append("\t");
        buf.append(this.tradeName).append("\t");
        buf.append(this.cashAcctCode).append("\t");
        buf.append(this.cashAcctName).append("\t");
        buf.append(this.attrClsCode).append("\t");
        buf.append(this.attrClsName).append("\t");
        buf.append(this.bargainDate).append("\t");
        buf.append(this.bargainTime).append("\t");
        buf.append(this.settleDate).append("\t");
        buf.append(this.settleTime).append("\t");
        buf.append(this.autoSettle).append("\t");
        buf.append(this.portCuryRate).append("\t");
        buf.append(this.baseCuryRate).append("\t");
        buf.append(this.tradeAmount).append("\t");
        buf.append(this.tradePrice).append("\t");
        buf.append(this.tradeMoney).append("\t");
        buf.append(this.unitCost).append("\t");
        buf.append(this.accruedInterest).append("\t");
        buf.append(this.allotFactor).append("\t");
        buf.append(this.totalCost).append("\t");
        buf.append(this.orderNum).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.handAmount).append("\t");
        buf.append(this.haveAmount).append("\t");
        buf.append(this.fees).append("\t");
        buf.append(this.factor).append("\t");
        buf.append(this.tradeCuryCode).append("\t");
        buf.append(this.bailMoney).append("\t"); //alter by sunny
        buf.append(this.dFactRate).append("\t"); // by leeyu
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        //------------彭鹏 2008.03.10 BUG0000066 手工录业务资料,带出的基础汇率不正确----------//
        if (sType.equalsIgnoreCase("CuryExchange")) {
            this.baseCuryRate = this.getSettingOper().getCuryExchange(YssFun.
                toDate(this.
                       bargainDate),
                (this.bargainTime == null) ? "" : this.bargainTime.trim(),
                this.tradeCuryCode, this.portCode,
                YssOperCons.YSS_RATE_BASE);
        }
        //--------------------------------------------------------------------------//
        if (sType.equalsIgnoreCase("BailMoney")) { //alter by sunny  10-21
            BaseOperDeal baseOper = this.getSettingOper();
            baseOper.setYssPub(pub);
            IndexFuturesBean fu = new IndexFuturesBean();
            fu.setYssPub(pub);
            fu.setSecurityCode(securityCode);
            fu.getSetting();
            this.bailMoney = baseOper.calcFuturesBail(fu.getBailType(),
                this.tradeAmount, this.tradePrice,
                fu.getMultiple(), fu.getBailScale(), fu.getBailFix());
            this.tradeMoney = YssD.mul(YssD.mul(tradeAmount, tradePrice),
                                       fu.getMultiple());
        }
        if (sType.equalsIgnoreCase("HaveAmount")) {
            ITradeOperation tradeOper = (ITradeOperation) pub.getOperDealCtx().
                getBean("tradedeal");
            tradeOper.setYssPub(pub);
            tradeOper.initTradeOperation(this.securityCode, this.brokerCode,
                                         this.invMgrCode,
                                         this.tradeCode, this.orderNum,
                                         YssFun.toDate(this.bargainDate), "");
            this.haveAmount = tradeOper.getHave(this.portCode);
        }
        if (sType.equalsIgnoreCase("BaseCuryRate")) {
            this.baseCuryRate = this.getSettingOper().getCuryRate(YssFun.toDate(this.
                bargainDate),
                (this.bargainTime == null) ? "" : this.bargainTime.trim(),
                this.tradeCuryCode, this.portCode,
                YssOperCons.YSS_RATE_BASE);
            //为什么要保留4位？？fazmm20070906
            //this.baseCuryRate = YssD.round(this.baseCuryRate, 4);
        }
        if (sType.equalsIgnoreCase("PortCuryRate")) {
            this.portCuryRate = this.getSettingOper().getCuryRate(YssFun.toDate(this.
                bargainDate),
                (this.bargainTime == null) ? "" : this.bargainTime.trim(),
                this.portCuryCode, this.portCode,
                YssOperCons.YSS_RATE_PORT);
            //为什么要保留4位？？fazmm20070906
            //this.portCuryRate = YssD.round(this.portCuryRate, 4);
        }
        if (sType.equalsIgnoreCase("settledate")) {
            this.settleDate = YssFun.formatDate(this.getSettingOper().
                                                getWorkDay(this.
                securityCode, YssFun.toDate(this.bargainDate)));
        }
        /*if (sType.equalsIgnoreCase("Interest")) {
           this.accruedInterest = this.getSettingOper().getInterest(this.
         securityCode, this.tradeAmount, YssFun.toDate(this.bargainDate));
               }*/
        if (sType.equalsIgnoreCase("calFixInterestt")) { //sj 20071122 通过点击窗口按钮计算应计利息的方法
            BaseBondOper bondOper = null;
            BaseOperDeal operDeal = new BaseOperDeal();
            operDeal.setYssPub(pub);
            YssBondIns bondIns = new YssBondIns();
            //MS00022    国内债券业务    QDV4.1赢时胜（上海）2009年4月20日22_A
            //2009.07.03 蒋锦 添加 新增债券交易类型
            if (this.tradeCode.equals(YssOperCons.YSS_JYLX_Buy) ||
                this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_XZWSZQ) ||
                this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_ZQLGDPS)) {
                bondOper = operDeal.getSpringRe(this.securityCode, "Buy"); //生成BaseBondOper
                bondIns.setInsType("Buy");
            } else if (this.tradeCode.equals(YssOperCons.YSS_JYLX_Sale) ||
                this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_ZZG) ||
                this.tradeCode.equalsIgnoreCase(YssOperCons.YSS_JYLX_KZZHS)) {
                bondOper = operDeal.getSpringRe(this.securityCode, "Sell"); //生成BaseBondOper
                bondIns.setInsType("Sell");
            }
            //----------2008.01.24 添加 蒋锦-------------//
            if (bondOper == null) {
                return "";
            }
            //------------------------------------------//
            bondIns.setSecurityCode(this.securityCode);
            
            
            //--- edit by songjie 2013.04.10 STORY 3806 需求深圳-(南方基金)QDII估值系统V4.0(高)20130402001 start---//
            SecurityBean security = new SecurityBean();
            security.setYssPub(pub);
            security.setSecurityCode(this.securityCode);
            security.getSetting();
            //如果交易所为 上交所 或 深交所  则  取 成交日期  为 计息截止日 ,否则 取 结算日 为 计息截止日 来计算买入卖出计息
            if(security.getStrExchangeCode() != null && 
               (security.getStrExchangeCode().equals("CG") || 
            	security.getStrExchangeCode().equals("CS"))){
            	bondIns.setInsDate(YssFun.toDate(this.bargainDate));
            }else{
            	bondIns.setInsDate(YssFun.toDate(this.settleDate));
            }
            bondOper.setCalPerHundred(true);
            //--- edit by songjie 2013.04.10 STORY 3806 需求深圳-(南方基金)QDII估值系统V4.0(高)20130402001 end---//
            bondIns.setInsAmount(this.tradeAmount);
            bondIns.setPortCode(this.portCode);
            bondOper.setYssPub(pub);
            bondOper.init(bondIns);
            this.accruedInterest = bondOper.calBondInterest();
        }

        return buildRowStr();

    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        TradeBean befEditBean = new TradeBean();
        try {
            strSql =
                "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
                " d.FPortName, e.FSecurityName, e.FHandAmount, f.FInvMgrName, g.FTradeTypeName, " +
                " h.FBrokerName as FBrokerName, o.FCashAccName, p.FAttrClsName,e.FFactor,e.FTradeCury" +
                " from " + pub.yssGetTableName("Tb_Data_Trade") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
/*                " left join (select db.* from (select FPortCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FPortCode) da join (select FPortCode, FPortName, FStartDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                ") db on da.FPortCode = db.FPortCode and da.FStartDate = db.FStartDate) d on a.FPortCode = d.FPortCode " +*/
                
                " left join (select FPortCode, FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1 ) d on a.FPortCode = d.FPortCode " +
                //end by lidaolong
                //----------------------------------------------------------------------------------------------------
                " left join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ea join (select FSecurityCode, FSecurityName, FStartDate, " +
                " FHandAmount,FFactor,FTradeCury from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") eb on ea.FSecurityCode = eb.FSecurityCode and ea.FStartDate = eb.FStartDate) e on a.FSecurityCode = e.FSecurityCode " +
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
               /* " left join (select fb.* from (select FInvMgrCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_InvestManager") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FInvMgrCode) fa join (select FInvMgrCode, FInvMgrName, FStartDate from " +
                pub.yssGetTableName("Tb_Para_InvestManager") +
                ") fb on fa.FInvMgrCode = fb.FInvMgrCode and fa.FStartDate = fb.FStartDate) f on a.FInvMgrCode = f.FInvMgrCode " +
              */
                " left join (select FInvMgrCode, FInvMgrName from " +
                pub.yssGetTableName("Tb_Para_InvestManager") +
                " where FCheckState = 1) f on a.FInvMgrCode = f.FInvMgrCode " +               
                //end by lidaolong 
                //----------------------------------------------------------------------------------------------------
                " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode" +
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
            /*    " left join (select hb.* from (select FBrokerCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Broker") +
                " where FStartDate <= " +
                dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FBrokerCode) ha join (select FBrokerCode, FBrokerName, FStartDate from " +
                pub.yssGetTableName("Tb_Para_Broker") +
                ") hb on ha.FBrokerCode = hb.FBrokerCode and ha.FStartDate = hb.FStartDate) h on a.FBrokerCode = h.FBrokerCode " +*/
                " left join (select FBrokerCode, FBrokerName from " +
                pub.yssGetTableName("Tb_Para_Broker") +
                " where  FCheckState = 1 ) h on a.FBrokerCode = h.FBrokerCode " +
                
                // end by lidaolong
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码               
           /*     " left join (select ob.* from (select FCashAccCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FCashAccCode) oa join (select FCashAccCode, FCashAccName, FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                ") ob on oa.FCashAccCode = ob.FCashAccCode and oa.FStartDate = ob.FStartDate) o on a.FCashAccCode = o.FCashAccCode " +
               */
                " left join (select FCashAccCode, FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1) o on a.FCashAccCode = o.FCashAccCode " +
                //end by lidaolong 
                
                //----------------------------------------------------------------------------------------------------
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                " where FCheckState = 1) p on a.FAttrClsCode = p.FAttrClsCode " +
                " where a.FNUM = " + dbl.sqlString(this.num);
            //  buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {

                befEditBean.num = rs.getString("FNum") + "";
                befEditBean.securityCode = rs.getString("FSecurityCode") + "";
                befEditBean.securityName = rs.getString("FSecurityName") + "";
                befEditBean.portCode = rs.getString("FPortCode") + "";
                befEditBean.portName = rs.getString("FPortName") + "";
                befEditBean.brokerCode = rs.getString("FBrokerCode") + "";
                befEditBean.brokerName = rs.getString("FBrokerName") + "";
                befEditBean.invMgrCode = rs.getString("FInvMgrCode") + "";
                befEditBean.invMgrName = rs.getString("FInvMgrName") + "";
                befEditBean.tradeCode = rs.getString("FTradeTypeCode") + "";
                befEditBean.tradeName = rs.getString("FTradeTypeName") + "";
                befEditBean.cashAcctCode = rs.getString("FCashAccCode") + "";
                befEditBean.cashAcctName = rs.getString("FCashAccName") + "";
                befEditBean.attrClsCode = rs.getString("FAttrClsCode") + "";
                befEditBean.attrClsName = rs.getString("FAttrClsName") + "";
                befEditBean.bargainDate = rs.getDate("FBargainDate") + "";
                befEditBean.bargainTime = rs.getString("FBargainTime") + "";
                befEditBean.settleDate = rs.getDate("FSettleDate") + "";
                befEditBean.settleTime = rs.getString("FSettleTime") + "";
                befEditBean.autoSettle = rs.getString("FAutoSettle") + "";
                befEditBean.portCuryRate = rs.getDouble("FPortCuryRate");
                befEditBean.baseCuryRate = rs.getDouble("FBaseCuryRate");
                befEditBean.allotFactor = rs.getDouble("FAllotFactor");
                befEditBean.tradeAmount = rs.getDouble("FTradeAmount");
                befEditBean.tradePrice = rs.getDouble("FTradePrice");
                befEditBean.tradeMoney = rs.getDouble("FTradeMoney");
                befEditBean.unitCost = rs.getDouble("FUnitCost");
                befEditBean.accruedInterest = rs.getDouble("FAccruedInterest");
                befEditBean.totalCost = rs.getDouble("FTotalCost");
                befEditBean.orderNum = rs.getString("FOrderNum") + "";
                befEditBean.desc = rs.getString("FDesc") + "";
                befEditBean.handAmount = rs.getDouble("FHandAmount");
                befEditBean.factor = rs.getDouble("FFactor");
                befEditBean.tradeCuryCode = rs.getString("FTradeCury") + "";
                loadFees(rs);

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException("获取交易数据信息出错：" + e.getMessage(), e);
        }

    }

    public void setResultSetAttr(ResultSet rs) throws SQLException,
        YssException {
        this.num = rs.getString("FNum") + "";
        this.securityCode = rs.getString("FSecurityCode") + "";
        this.securityName = rs.getString("FSecurityName") + "";
        this.portCode = rs.getString("FPortCode") + "";
        this.portName = rs.getString("FPortName") + "";
        this.brokerCode = rs.getString("FBrokerCode") + "";
        this.brokerName = rs.getString("FBrokerName") + "";
        this.invMgrCode = rs.getString("FInvMgrCode") + "";
        this.invMgrName = rs.getString("FInvMgrName") + "";
        this.tradeCode = rs.getString("FTradeTypeCode") + "";
        this.tradeName = rs.getString("FTradeTypeName") + "";
        this.cashAcctCode = rs.getString("FCashAccCode") + "";
        this.cashAcctName = rs.getString("FCashAccName") + "";
        this.attrClsCode = rs.getString("FAttrClsCode") + "";
        this.attrClsName = rs.getString("FAttrClsName") + "";
        this.bargainDate = rs.getDate("FBargainDate") + "";
        this.bargainTime = rs.getString("FBargainTime") + "";
        this.settleDate = rs.getDate("FSettleDate") + "";
        this.settleTime = rs.getString("FSettleTime") + "";
        this.autoSettle = rs.getString("FAutoSettle") + "";
        this.portCuryRate = rs.getDouble("FPortCuryRate");
        this.baseCuryRate = rs.getDouble("FBaseCuryRate");
        this.allotFactor = rs.getDouble("FAllotFactor");
        this.tradeAmount = rs.getDouble("FTradeAmount");
        this.tradePrice = rs.getDouble("FTradePrice");
        this.tradeMoney = rs.getDouble("FTradeMoney");
        this.unitCost = rs.getDouble("FUnitCost");
        this.accruedInterest = rs.getDouble("FAccruedInterest");
        this.bailMoney = rs.getDouble("FBailMoney"); //alter by sunny
        this.totalCost = rs.getDouble("FTotalCost");
        this.orderNum = rs.getString("FOrderNum") + "";
        this.desc = rs.getString("FDesc") + "";
        this.handAmount = rs.getDouble("FHandAmount");
        this.factor = rs.getDouble("FFactor");
        this.tradeCuryCode = rs.getString("FTradeCury") + "";
        loadFees(rs);
        super.setRecLog(rs);
    }

    public void loadFees(ResultSet rs) throws SQLException, YssException {
        String sName = "";
        double dFeeMoney = 0;
        double dTotalFee = 0;
        StringBuffer buf = new StringBuffer();
        FeeBean fee = new FeeBean();
        fee.setYssPub(pub);

        for (int i = 1; i <= 8; i++) {
            if (rs.getString("FFeeCode" + i) != null &&
                rs.getString("FFeeCode" + i).trim().length() > 0) {
                fee.setFeeCode(rs.getString("FFeeCode" + i));
                fee.getSetting();
                //------ add by wangzuochun 2010.09.11 MS01708    交易结算未结算中进行结算时会报错    QDV4建行2010年09月08日01_B    
                //------ 根据交易子表中的费用代码去查费用设置中的费用，若费用不存在，则跳过此次循环；
                if (fee.getFeeCode() == null){
                	continue;
                }
                //----------MS01708-----------//
                sName = fee.getFeeName();
                if (rs.getString("FTradeFee" + i) != null) {
                    dFeeMoney = rs.getDouble("FTradeFee" + i);
                }
                dTotalFee = YssD.add(dTotalFee, dFeeMoney);
                buf.append(rs.getString("FFeeCode" + i)).append("\n");
                buf.append(sName).append("\n");
                buf.append(dFeeMoney).append("\n");
                buf.append(fee.buildRowStr().replaceAll("\t", "~")).append(
                    "\f\n");
            }
        }
        if (buf.toString().length() > 2) {
            buf.append("total").append("\n");
            buf.append("Total: ").append("\n");
            buf.append(dTotalFee).append("\n");
            fee.setAccountingWay("0"); //不计入成本
            buf.append(fee.buildRowStr().replaceAll("\t", "~"));
            this.fees = buf.toString();
        } else {
            this.fees = "";
        }
    }

    /**
     * deleteRecycleData

         public void deleteRecycleData() {
         }
     */
    /**
     * bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
     * 修改人：方浩
     * 回收站的删除功能调用此方法deleteRecycleData()
     * 从数据库删除数据，即彻底删除数据
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = ""; //定义一个放SQL语句的字符串
        String[] arrData = null; //定义一个字符数组来循环删除
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
                        pub.yssGetTableName("Tb_Data_Trade") +
                        " where FNum = " +
                        dbl.sqlString(this.num); //SQL语句
                    //执行sql语句
                    dbl.executeSql(strSql);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Data_SubTrade") +
                        " where FNum like '" +
                        this.num + "%'";
                    dbl.executeSql(strSql); //删除交易数据子表

                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Data_TradeRela") +
                        " where FNum = " +
                        dbl.sqlString(this.num);
                    dbl.executeSql(strSql); //删除交易关联表

                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Data_TradeRelaSub") +
                        " where FNum = " +
                        dbl.sqlString(this.num);
                    dbl.executeSql(strSql); //删除交易关联子表

                    strSql = "delete from " +
                        pub.yssGetTableName("tb_cash_transfer") +
                        " where FNum like '" + this.num + "%'";
                    dbl.executeSql(strSql); //删除资金调拨表
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Cash_SubTransfer") +
                        " where FNum like '" + this.num + "%'";
                    dbl.executeSql(strSql); //删除资金调拨子表

                }
            }
            //sRecycled如果sRecycled为空，而num不为空，则按照num来执行sql语句
            else if (num != "" && num != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_Trade") +
                    " where FNum = " +
                    dbl.sqlString(this.num); //SQL语句
                //执行sql语句
                dbl.executeSql(strSql);
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_SubTrade") +
                    " where FNum like '" +
                    this.num + "%'";
                dbl.executeSql(strSql); //删除交易数据子表

                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_TradeRela") +
                    " where FNum = " +
                    dbl.sqlString(this.num);
                dbl.executeSql(strSql); //删除交易关联表

                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_TradeRelaSub") +
                    " where FNum = " +
                    dbl.sqlString(this.num);
                dbl.executeSql(strSql); //删除交易关联子表

                strSql = "delete from " +
                    pub.yssGetTableName("tb_cash_transfer") +
                    " where FNum like '" + this.num + "%'";
                dbl.executeSql(strSql); //删除资金调拨表
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Cash_SubTransfer") +
                    " where FNum like '" + this.num + "%'";
                dbl.executeSql(strSql); //删除资金调拨子表
            }
            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
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
