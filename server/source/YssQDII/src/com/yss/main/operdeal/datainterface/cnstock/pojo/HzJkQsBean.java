package com.yss.main.operdeal.datainterface.cnstock.pojo;

import java.util.Date;

/**
 * QDV4.1赢时胜（上海）2009年4月20日11_A
 * MS00011
 * 交易接口清算表的实体类
 * create by songjie
 * 2009-06-22
 */
public class HzJkQsBean {
   private java.util.Date date = null;//交易日期
   private java.util.Date inDate = null;//系统读数日期
   private String zqdm = null;//转换后的证券代码
   private String oldZqdm = null;//转换前的证券代码
   private String szsh = null;//交易所代码
   private String jyxwh = null;//席位代码
   private String zqbz = null;//证券标志
   private String ywbz = null;//业务标志
   private String qsbz = null;//清算标志
   private String jyfs = null;//交易方式
   private String tzbz = null;//投资标识
   private String gddm = null;//股东代码
   private String portCode = null;//组合代码
   private double bJe = 0;//买金额
   private double sJe = 0;//卖金额
   private double bSl = 0;//买数量
   private double sSl = 0;//卖数量
   private double bYj = 0;//买佣金
   private double sYj = 0;//卖佣金
   private double bJsf = 0;//买经手费
   private double sJsf = 0;//卖经手费
   private double bYhs = 0;//买印花税
   private double sYhs = 0;//卖印花税
   private double bZgf = 0;//买证管费
   private double sZgf = 0;//卖证管费
   private double bGhf = 0;//买过户费
   private double sGhf = 0;//卖过户费
   private double bGzlx = 0;//买国债利息
   private double sGzlx = 0;//卖国债利息
   private double hgGain = 0;//回购收益
   private double bSfje = 0;//买实付金额
   private double sSsje = 0;//卖实收金额
   private double bQtf = 0;//买其他费
   private double sQtf = 0;//卖其他费
   private double bFxj = 0;//买风险金
   private double sFxj = 0;//卖风险金
   private FeeAttributeBean feeAttribute = null;
   /**
    * 构造函数
    */
   public HzJkQsBean() {
   }

   public String getZqdm() {
      return zqdm;
   }

   public String getZqbz() {
      return zqbz;
   }

   public String getYwbz() {
      return ywbz;
   }

   public String getTzbz() {
      return tzbz;
   }

   public String getSzsh() {
      return szsh;
   }

   public double getSZgf() {
      return sZgf;
   }

   public double getSYj() {
      return sYj;
   }

   public double getSYhs() {
      return sYhs;
   }

   public double getSSsje() {
      return sSsje;
   }

   public double getSSl() {
      return sSl;
   }

   public double getSQtf() {
      return sQtf;
   }

   public double getSJsf() {
      return sJsf;
   }

   public double getSJe() {
      return sJe;
   }

   public double getSGzlx() {
      return sGzlx;
   }

   public double getSGhf() {
      return sGhf;
   }

   public double getSFxj() {
      return sFxj;
   }

   public String getQsbz() {
      return qsbz;
   }

   public String getPortCode() {
      return portCode;
   }

   public String getOldZqdm() {
      return oldZqdm;
   }

   public String getJyxwh() {
      return jyxwh;
   }

   public String getJyfs() {
      return jyfs;
   }

   public Date getInDate() {
      return inDate;
   }

   public double getHgGain() {
      return hgGain;
   }

   public String getGddm() {
      return gddm;
   }

   public Date getDate() {
      return date;
   }

   public double getBZgf() {
      return bZgf;
   }

   public double getBYj() {
      return bYj;
   }

   public double getBYhs() {
      return bYhs;
   }

   public double getBSl() {
      return bSl;
   }

   public double getBSfje() {
      return bSfje;
   }

   public double getBQtf() {
      return bQtf;
   }

   public double getBJsf() {
      return bJsf;
   }

   public double getBJe() {
      return bJe;
   }

   public double getBGzlx() {
      return bGzlx;
   }

   public double getBGhf() {
      return bGhf;
   }

   public void setBFxj(double bFxj) {
      this.bFxj = bFxj;
   }

   public void setZqdm(String zqdm) {
      this.zqdm = zqdm;
   }

   public void setZqbz(String zqbz) {
      this.zqbz = zqbz;
   }

   public void setYwbz(String ywbz) {
      this.ywbz = ywbz;
   }

   public void setTzbz(String tzbz) {
      this.tzbz = tzbz;
   }

   public void setSzsh(String szsh) {
      this.szsh = szsh;
   }

   public void setSZgf(double sZgf) {
      this.sZgf = sZgf;
   }

   public void setSYj(double sYj) {
      this.sYj = sYj;
   }

   public void setSYhs(double sYhs) {
      this.sYhs = sYhs;
   }

   public void setSSsje(double sSsje) {
      this.sSsje = sSsje;
   }

   public void setSSl(double sSl) {
      this.sSl = sSl;
   }

   public void setSQtf(double sQtf) {
      this.sQtf = sQtf;
   }

   public void setSJsf(double sJsf) {
      this.sJsf = sJsf;
   }

   public void setSJe(double sJe) {
      this.sJe = sJe;
   }

   public void setSGzlx(double sGzlx) {
      this.sGzlx = sGzlx;
   }

   public void setSGhf(double sGhf) {
      this.sGhf = sGhf;
   }

   public void setSFxj(double sFxj) {
      this.sFxj = sFxj;
   }

   public void setQsbz(String qsbz) {
      this.qsbz = qsbz;
   }

   public void setPortCode(String portCode) {
      this.portCode = portCode;
   }

   public void setOldZqdm(String oldZqdm) {
      this.oldZqdm = oldZqdm;
   }

   public void setJyxwh(String jyxwh) {
      this.jyxwh = jyxwh;
   }

   public void setJyfs(String jyfs) {
      this.jyfs = jyfs;
   }

   public void setInDate(Date inDate) {
      this.inDate = inDate;
   }

   public void setHgGain(double hgGain) {
      this.hgGain = hgGain;
   }

   public void setGddm(String gddm) {
      this.gddm = gddm;
   }

   public void setDate(Date date) {
      this.date = date;
   }

   public void setBZgf(double bZgf) {
      this.bZgf = bZgf;
   }

   public void setBYj(double bYj) {
      this.bYj = bYj;
   }

   public void setBYhs(double bYhs) {
      this.bYhs = bYhs;
   }

   public void setBSl(double bSl) {
      this.bSl = bSl;
   }

   public void setBSfje(double bSfje) {
      this.bSfje = bSfje;
   }

   public void setBQtf(double bQtf) {
      this.bQtf = bQtf;
   }

   public void setBJsf(double bJsf) {
      this.bJsf = bJsf;
   }

   public void setBJe(double bJe) {
      this.bJe = bJe;
   }

   public void setBGzlx(double bGzlx) {
      this.bGzlx = bGzlx;
   }

   public void setBGhf(double bGhf) {
      this.bGhf = bGhf;
   }

    public void setFeeAttribute(FeeAttributeBean feeAttribute) {
        this.feeAttribute = feeAttribute;
    }

    public double getBFxj() {
      return bFxj;
   }

    public FeeAttributeBean getFeeAttribute() {
        return feeAttribute;
    }
}
