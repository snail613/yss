package com.yss.pojo.cache;

import com.yss.main.dao.*;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class YssCost
    implements IYssConvert {
    private double cost; //原币核算成本
    private double mCost; //原币管理成本
    private double vCost; //原币估值成本
    private double baseCost; //基础货币核算成本
    private double baseMCost; //基础货币管理成本
    private double baseVCost; //基础货币估值成本
    private double portCost; //组合货币核算成本
    private double portMCost; //组合货币管理成本
    private double portVCost; //组合货币估值成本
    private double amount; //核算成本的数量
    private double mAmount; //管理成本的数量
    private double costPAD; //溢折价成本
    private double baseCostPAD; //基础货币溢折价成本
    private double portCostPAD; //组合货币溢折价成本

    public double getPortCost() {
        return portCost;
    }

    public double getPortMCost() {
        return portMCost;
    }

    public double getCost() {
        return cost;
    }

    public double getBaseMCost() {
        return baseMCost;
    }

    public double getPortVCost() {
        return portVCost;
    }

    public double getBaseVCost() {
        return baseVCost;
    }

    public double getVCost() {
        return vCost;
    }

    public double getMCost() {
        return mCost;
    }

    public void setBaseCost(double baseCost) {
        this.baseCost = baseCost;
    }

    public void setPortCost(double portCost) {
        this.portCost = portCost;
    }

    public void setPortMCost(double portMCost) {
        this.portMCost = portMCost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setBaseMCost(double baseMCost) {
        this.baseMCost = baseMCost;
    }

    public void setPortVCost(double portVCost) {
        this.portVCost = portVCost;
    }

    public void setBaseVCost(double baseVCost) {
        this.baseVCost = baseVCost;
    }

    public void setVCost(double vCost) {
        this.vCost = vCost;
    }

    public void setMCost(double mCost) {
        this.mCost = mCost;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setMAmount(double mAmount) {
        this.mAmount = mAmount;
    }

    public void setBaseCostPAD(double baseCostPAD) {
        this.baseCostPAD = baseCostPAD;
    }

    public void setCostPAD(double costPAD) {
        this.costPAD = costPAD;
    }

    public void setPortCostPAD(double portCostPAD) {
        this.portCostPAD = portCostPAD;
    }

    public double getBaseCost() {
        return baseCost;
    }

    public double getAmount() {
        return amount;
    }

    public double getMAmount() {
        return mAmount;
    }

    public double getBaseCostPAD() {
        return baseCostPAD;
    }

    public double getCostPAD() {
        return costPAD;
    }

    public double getPortCostPAD() {
        return portCostPAD;
    }

    public YssCost() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.cost).append("\n");
        buf.append(this.mCost).append("\n");
        buf.append(this.vCost).append("\n");
        buf.append(this.baseCost).append("\n");
        buf.append(this.baseMCost).append("\n");
        buf.append(this.baseVCost).append("\n");
        buf.append(this.portCost).append("\n");
        buf.append(this.portMCost).append("\n");
        buf.append(this.portVCost);
        return buf.toString();
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
            sTmpStr = sRowStr;

            reqAry = sTmpStr.split("\n");
            if (YssFun.isNumeric(reqAry[0])) {
                this.cost = Double.parseDouble(reqAry[0]);
            }
            if (YssFun.isNumeric(reqAry[1])) {
                this.mCost = Double.parseDouble(reqAry[1]);
            }
            if (YssFun.isNumeric(reqAry[2])) {
                this.vCost = Double.parseDouble(reqAry[2]);
            }
            if (YssFun.isNumeric(reqAry[3])) {
                this.baseCost = Double.parseDouble(reqAry[3]);
            }
            if (YssFun.isNumeric(reqAry[4])) {
                this.baseMCost = Double.parseDouble(reqAry[4]);
            }
            if (YssFun.isNumeric(reqAry[5])) {
                this.baseVCost = Double.parseDouble(reqAry[5]);
            }
            if (YssFun.isNumeric(reqAry[6])) {
                this.portCost = Double.parseDouble(reqAry[6]);
            }
            if (YssFun.isNumeric(reqAry[7])) {
                this.portMCost = Double.parseDouble(reqAry[7]);
            }
            if (YssFun.isNumeric(reqAry[8])) {
                this.portVCost = Double.parseDouble(reqAry[8]);
            }
        } catch (Exception e) {
            throw new YssException("解析成本数据出错", e);
        }
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

}
