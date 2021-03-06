package com.yss.main.operdeal.report.reptab.valrep.pojo;

import java.util.*;

import com.yss.dsub.*;

public class ValRepBean extends BaseBean{
   public ValRepBean() {
   }

   public double getYKVBal() {
      return yKVBal;
   }

   public double getVstorageCost() {
      return vstorageCost;
   }

   public double getSyvBaseCuryBal() {
      return syvBaseCuryBal;
   }

   public double getStorageAmount() {
      return storageAmount;
   }

   public String getSecurityName() {
      return securityName;
   }

   public String getSecurityCode() {
      return securityCode;
   }

   public double getMvalue() {
      return mvalue;
   }

   public double getMarketPrice() {
      return marketPrice;
   }

   public double getLXVBal() {
      return lXVBal;
   }

   public Date getInsStartDate() {
      return insStartDate;
   }

   public Date getInsEndDate() {
      return insEndDate;
   }

   public double getFundAllotProportion() {
      return fundAllotProportion;
   }

   public double getFactRate() {
      return factRate;
   }

   public String getCuryCode() {
      return curyCode;
   }

   public double getBoughtInt() {
      return boughtInt;
   }

   public double getBaseCuryRate() {
      return baseCuryRate;
   }

   public double getBFlxBal() {
      return bFlxBal;
   }

   public void setAvgCost(double avgCost) {
      this.avgCost = avgCost;
   }

   public void setYKVBal(double yKVBal) {
      this.yKVBal = yKVBal;
   }

   public void setVstorageCost(double vstorageCost) {
      this.vstorageCost = vstorageCost;
   }

   public void setSyvBaseCuryBal(double syvBaseCuryBal) {
      this.syvBaseCuryBal = syvBaseCuryBal;
   }

   public void setStorageAmount(double storageAmount) {
      this.storageAmount = storageAmount;
   }

   public void setSecurityName(String securityName) {
      this.securityName = securityName;
   }

   public void setSecurityCode(String securityCode) {
      this.securityCode = securityCode;
   }

   public void setMvalue(double mvalue) {
      this.mvalue = mvalue;
   }

   public void setMarketPrice(double marketPrice) {
      this.marketPrice = marketPrice;
   }

   public void setLXVBal(double lXVBal) {
      this.lXVBal = lXVBal;
   }

   public void setInsStartDate(Date insStartDate) {
      this.insStartDate = insStartDate;
   }

   public void setInsEndDate(Date insEndDate) {
      this.insEndDate = insEndDate;
   }

   public void setFundAllotProportion(double fundAllotProportion) {
      this.fundAllotProportion = fundAllotProportion;
   }

   public void setFactRate(double factRate) {
      this.factRate = factRate;
   }

   public void setCuryCode(String curyCode) {
      this.curyCode = curyCode;
   }

   public void setBoughtInt(double boughtInt) {
      this.boughtInt = boughtInt;
   }

   public void setBaseCuryRate(double baseCuryRate) {
      this.baseCuryRate = baseCuryRate;
   }

   public void setBFlxBal(double bFlxBal) {
      this.bFlxBal = bFlxBal;
   }

   public void setPortCuryRate(double portCuryRate) {
      this.portCuryRate = portCuryRate;
   }

   public void setOtherCost(double otherCost) {
      this.otherCost = otherCost;
   }

   public void setTotalCost(double totalCost) {
      this.totalCost = totalCost;
   }

   public double getAvgCost() {
      return avgCost;
   }

   public double getPortCuryRate() {
      return portCuryRate;
   }

   public double getOtherCost() {
      return otherCost;
   }

   public double getTotalCost() {
      return totalCost;
   }

   public void setOrder(String Order) {
      this.Order = Order;
   }

   public String getOrder() {
      return Order;
   }

   private String securityCode="";
   private String securityName="";
   private double storageAmount=0;
   private String curyCode="";
   private double baseCuryRate=0;
   private double portCuryRate=0;
   private double factRate=0;
   private java.util.Date insStartDate=null;
   private java.util.Date insEndDate=null;
   private double avgCost=0;
   private double marketPrice=0;
   private double vstorageCost=0;
   private double totalCost=0;
   private double boughtInt=0;
   private double mvalue=0;
   private double  lXVBal=0;
   private double bFlxBal=0;
   private double yKVBal=0;
   private double syvBaseCuryBal=0;
   private double fundAllotProportion=0;
   private double otherCost=0;
   private String  Order="";
}
