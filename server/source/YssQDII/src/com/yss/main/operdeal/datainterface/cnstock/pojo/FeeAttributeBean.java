package com.yss.main.operdeal.datainterface.cnstock.pojo;

import com.yss.main.operdeal.datainterface.cnstock.CtlStock;
import java.util.*;

/**
 * QDV4.1赢时胜（上海）2009年4月20日11_A
 * MS00011
 * create by songjie
 * 2009-06-22
 * 用于储存计算费用要用到的值
 */
public class FeeAttributeBean {
   private String securitySign = null;//证券标志
   private String businessSign = null;//业务标志
   private String shsz = null;//交易所代码
   private String bs = null;//买卖标志
   private String zqdm = null;//转换后的证券代码
   private String oldZqdm = null;//转换前的证券代码
   private String portCode = null;//组合代码
   private String gsdm = null;//席位号 
   private String seatcode=null;//席位代码  edited by zhouxiang MS01299    接口处理界面导入上海过户库时出现提示信息    QDV4赢时胜(测试)2010年6月12日2_B 
   private java.util.Date date = null;//业务日期
   private java.util.Date inDate = null;//系统读数日期
   private String jyfs = null;//交易方式
   private String gddm = null;//股东代码
   private String tzbz = null;//投资标志
   private CtlStock ctlStock = null;
   private ReadTypeBean readType = null;
   private double cjje = 0;//成交金额
   private double cjsl = 0;//成交数量
   private double cjjg = 0;//成交价格
   private double FJsf = 0;//经手费
   private double FZgf = 0;//证管费
   private double FYhs = 0;//印花税
   private double FGhf = 0;//过户费
   private double FYj = 0;//佣金
   private double Ffxj = 0;//风险金
   private double Fqtf = 0;//结算费
   private double Fhggain = 0;//回购收益
   private double FBeforeGzlx = 0;//税前的国债利息
   private double FGzlx = 0;//税后的国债利息

   private String FSqbh = null;//申请编号
   private boolean comeFromQS = false;//源于汇总到清算的调用
   private String selectedFee = null;//要计算的费用
   HashMap hmReadType = null;//用于储存数据接口参数设置界面的读数处理方式分页的各种参数 key--组合群代码,组合代码
   HashMap hmExchangeBond = null;//用于储存数据接口参数设置界面的交易所债券参数设置分页的各种参数 key--组合群代码, 组合代码, 市场, 品种
   HashMap hmTradeFee = null;//用于储存数据接口参数设置界面的交易费用计算方式分页的各种参数设置 key--组合群代码, 组合代码
   HashMap hmFeeWay = null;//用于储存数据接口参数设置界面的费用承担方向分页的各种参数设置 key--组合群代码, 组合代码, 券商代码, 席位代码
   HashMap hmRateSpeciesType = null;//用于储存各种交易品种费率 key--费率类型, 费率品种
   HashMap hmBrokerRate = null;//用于储存券商佣金利率 key--组合代码, 券商代码, 席位地点（上海或深圳）, 席位号, 品种类型
   HashMap hmBrokerCode = null;//用于储存席位代码对应的券商代码
   //add by songjie 2011.03.07 需求：750 QDV4赢时胜(上海)2011年3月6日01_AB
   private String cjhm = null;//成交号码
   public String getCjhm() {
	   return cjhm;
   }

   public void setCjhm(String cjhm) {
	   this.cjhm = cjhm;
   }
   //add by songjie 2011.03.07 需求：750 QDV4赢时胜(上海)2011年3月6日01_AB
   
   /**
    * 构造函数
    */
   public FeeAttributeBean() {
   }

   public String getZqdm() {
      return zqdm;
   }

   public String getSelectedFee() {
      return selectedFee;
   }

   public String getSecuritySign() {
      return securitySign;
   }

   public ReadTypeBean getReadType() {
      return readType;
   }

   public String getPortCode() {
      return portCode;
   }

   public String getGsdm() {
      return gsdm;
   }
//add by zhouxiang MS01299    接口处理界面导入上海过户库时出现提示信息    QDV4赢时胜(测试)2010年6月12日2_B 
   public String getSeatCode()
   {
	   return seatcode;
   }
   public void setSeatCode(String seatcode)
   {this.seatcode=seatcode;}
 //-----end------------
   public Date getDate() {
      return date;
   }

   public CtlStock getCtlStock() {
      return ctlStock;
   }

   public double getCjsl() {
      return cjsl;
   }

   public double getCjjg() {
      return cjjg;
   }

   public double getCjje() {
      return cjje;
   }

   public String getBusinessSign() {
      return businessSign;
   }

   public void setBs(String bs) {
      this.bs = bs;
   }

   public void setZqdm(String zqdm) {
      this.zqdm = zqdm;
   }

   public void setSelectedFee(String selectedFee) {
      this.selectedFee = selectedFee;
   }

   public void setSecuritySign(String securitySign) {
      this.securitySign = securitySign;
   }

   public void setReadType(ReadTypeBean readType) {
      this.readType = readType;
   }

   public void setPortCode(String portCode) {
      this.portCode = portCode;
   }

   public void setGsdm(String gsdm) {
      this.gsdm = gsdm;
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public void setCtlStock(CtlStock ctlStock) {
      this.ctlStock = ctlStock;
   }

   public void setCjsl(double cjsl) {
      this.cjsl = cjsl;
   }

   public void setCjjg(double cjjg) {
      this.cjjg = cjjg;
   }

   public void setCjje(double cjje) {
      this.cjje = cjje;
   }

   public void setBusinessSign(String businessSign) {
      this.businessSign = businessSign;
   }

   public void setHmRateSpeciesType(HashMap hmRateSpeciesType) {
      this.hmRateSpeciesType = hmRateSpeciesType;
   }

   public void setHmFeeWay(HashMap hmFeeWay) {
      this.hmFeeWay = hmFeeWay;
   }

   public void setHmExchangeBond(HashMap hmExchangeBond) {
      this.hmExchangeBond = hmExchangeBond;
   }

   public void setHmBrokerRate(HashMap hmBrokerRate) {
      this.hmBrokerRate = hmBrokerRate;
   }

   public void setHmBrokerCode(HashMap hmBrokerCode) {
      this.hmBrokerCode = hmBrokerCode;
   }

   public void setOldZqdm(String oldZqdm) {
      this.oldZqdm = oldZqdm;
   }

   public void setJyfs(String jyfs) {
      this.jyfs = jyfs;
   }

   public void setInDate(Date inDate) {
      this.inDate = inDate;
   }

   public void setTzbz(String tzbz) {
      this.tzbz = tzbz;
   }

   public void setGddm(String gddm) {
      this.gddm = gddm;
   }

   public void setShsz(String shsz) {
      this.shsz = shsz;
   }

   public void setComeFromQS(boolean comeFromQS) {
      this.comeFromQS = comeFromQS;
   }

   public void setFSqbh(String FSqbh) {
      this.FSqbh = FSqbh;
   }

   public String getBs() {
      return bs;
   }

   public HashMap getHmRateSpeciesType() {
      return hmRateSpeciesType;
   }

   public HashMap getHmFeeWay() {
      return hmFeeWay;
   }

   public HashMap getHmExchangeBond() {
      return hmExchangeBond;
   }

   public HashMap getHmBrokerRate() {
      return hmBrokerRate;
   }

   public HashMap getHmBrokerCode() {
      return hmBrokerCode;
   }

   public String getOldZqdm() {
      return oldZqdm;
   }

   public String getJyfs() {
      return jyfs;
   }

   public Date getInDate() {
      return inDate;
   }

   public String getTzbz() {
      return tzbz;
   }

   public String getGddm() {
      return gddm;
   }

   public String getShsz() {
      return shsz;
   }

   public boolean isComeFromQS() {
      return comeFromQS;
   }

   public String getFSqbh() {
      return FSqbh;
   }

   public void setFqtf(double Fqtf) {
      this.Fqtf = Fqtf;
   }

   public double getFqtf() {
      return Fqtf;
   }

   public void setFhggain(double Fhggain) {
      this.Fhggain = Fhggain;
   }

   public double getFhggain() {
      return Fhggain;
   }

   public void setFfxj(double Ffxj) {
      this.Ffxj = Ffxj;
   }

   public double getFfxj() {
      return Ffxj;
   }

   public void setFZgf(double FZgf) {
      this.FZgf = FZgf;
   }

   public double getFZgf() {
      return FZgf;
   }

   public void setFYj(double FYj) {
      this.FYj = FYj;
   }

   public double getFYj() {
      return FYj;
   }

   public void setFYhs(double FYhs) {
      this.FYhs = FYhs;
   }

   public double getFYhs() {
      return FYhs;
   }

   public void setFJsf(double FJsf) {
      this.FJsf = FJsf;
   }

   public double getFJsf() {
      return FJsf;
   }

   public void setFGzlx(double FGzlx) {
      this.FGzlx = FGzlx;
   }

   public double getFGzlx() {
      return FGzlx;
   }

   public void setFGhf(double FGhf) {
      this.FGhf = FGhf;
   }

   public double getFGhf() {
      return FGhf;
   }

   public void setFBeforeGzlx(double FBeforeGzlx) {
      this.FBeforeGzlx = FBeforeGzlx;
   }

   public double getFBeforeGzlx() {
      return FBeforeGzlx;
   }
   //---add by songjie 2012.11.08 BUG 6227 QDV4农业银行2012年11月06日01_B start---//
   public HashMap getHmTradeFee(){
	   return hmTradeFee;
   }
   
   public void setHmTradeFee(HashMap hmTradeFee){
	   this.hmTradeFee = hmTradeFee;
   }
   //---add by songjie 2012.11.08 BUG 6227 QDV4农业银行2012年11月06日01_B end---//
}
