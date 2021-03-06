package com.yss.main.operdeal.costcal;

import com.yss.dsub.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;

public class BaseCostCal
    extends BaseBean {
    protected java.util.Date date;
    protected String portCode = "";
    protected String analysisCode1 = "";
    protected String analysisCode2 = "";
    protected String analysisCode3 = "";
    protected String securityCode = "";
    protected double amount;
    protected String tradeNum;

    protected String invmgrField = "";
    protected String brokerField = "";
    public BaseCostCal() {
    }

    public void init(String sTradeNum) throws YssException {
        this.tradeNum = sTradeNum;
    }

    public void init(java.util.Date dDate, String sPortCode,
                     String sAnalysisCode1, String sAnalysisCode2,
                     String sAnalysisCode3,
                     String sSecurityCode, double dAmount,
                     String sTradeNum) throws YssException {
        this.date = dDate;
        this.portCode = sPortCode;
        this.analysisCode1 = sAnalysisCode1;
        this.analysisCode2 = sAnalysisCode2;
        this.analysisCode3 = sAnalysisCode3;
        this.securityCode = sSecurityCode;
        this.amount = dAmount;
        this.tradeNum = sTradeNum;
    }

    public YssCost getCarryCost() throws YssException {
        return null;
    }

    public void roundCost(YssCost cost, int scale) {
        cost.setCost(YssD.round(cost.getCost(), scale));
        cost.setBaseCost(YssD.round(cost.getBaseCost(), scale));
        cost.setPortCost(YssD.round(cost.getPortCost(), scale));

        cost.setMCost(YssD.round(cost.getMCost(), scale));
        cost.setBaseMCost(YssD.round(cost.getBaseMCost(), scale));
        cost.setPortMCost(YssD.round(cost.getPortMCost(), scale));

        cost.setVCost(YssD.round(cost.getVCost(), scale));
        cost.setBaseVCost(YssD.round(cost.getBaseVCost(), scale));
        cost.setPortVCost(YssD.round(cost.getPortVCost(), scale));
    }

}
