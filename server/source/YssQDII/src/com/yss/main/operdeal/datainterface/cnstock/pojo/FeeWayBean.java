package com.yss.main.operdeal.datainterface.cnstock.pojo;

import com.yss.util.YssCons;

/**
 * QDV4.1赢时胜（上海）2009年4月20日11_A
 * TB_PUB_DAO_FeeWay费用承担方向表的实体类
 * MS00011
 * create by songjie
 * 2009-06-18
 */
public class FeeWayBean {
   public FeeWayBean() {
   }

   public String getAssetGroupCode() {
      return assetGroupCode;
   }

   public String getBrokerBear() {
      return brokerBear;
   }

   public String getBrokerCode() {
      return brokerCode;
   }

   public String getPortCode() {
      return portCode;
   }

   public String getProductBear() {
      return productBear;
   }

   public String getSeatCode() {
      return seatCode;
   }

   public void setSeatCode(String seatCode) {
      this.seatCode = seatCode;
   }

   public void setProductBear(String productBear) {
      this.productBear = productBear;
   }

   public void setPortCode(String portCode) {
      this.portCode = portCode;
   }

   public void setBrokerCode(String brokerCode) {
      this.brokerCode = brokerCode;
   }

   public void setBrokerBear(String brokerBear) {
      this.brokerBear = brokerBear;
   }

   public void setAssetGroupCode(String assetGroupCode) {
      this.assetGroupCode = assetGroupCode;
   }

   private String assetGroupCode = null;//组合群代码
   private String portCode = null;//组合代码
   private String brokerCode = null;//券商代码
   private String seatCode = null;//席位代码
   private String productBear = null;//产品承担
   private String brokerBear = null;//券商承担


   /**
    * 组合数据
    * @return String
    */
   public String buildRowStr() {
      StringBuffer buf = new StringBuffer();
      buf.append(getk2v(productBear)).append(YssCons.YSS_ITEMSPLITMARK1); //组合群代码
      buf.append(getk2v(brokerBear)); //组合代码
      return buf.toString();

   }

   private String getk2v(String key) {
      String[] rmpAry = key.split(",");
      String str = "";
      for (int i = 0; i < rmpAry.length; i++) {
         str += getKey2Value(rmpAry[i]) + YssCons.YSS_LINESPLITMARK;
      }
      if (str.length() > 1) {
         str = str.substring(0, str.length() - 2);
      }
      return str;
   }

   private String getKey2Value(String name) {
      if ("01".equals(name)) {
         return "股票经手费";
      }
      else if ("02".equals(name)) {
         return "债券经手费";
      }
      else if ("03".equals(name)) {
         return "回购经手费";
      }
      else if ("04".equals(name)) {
         return "基金经手费";
      }
      else if ("05".equals(name)) {
         return "股票征管费";
      }
      else if ("06".equals(name)) {
         return "债券征管费";
      }
      else if ("07".equals(name)) {
         return "基金征管费";
      }
      else if ("08".equals(name)) {
         return "权证结算费";
      }
      else if ("09".equals(name)) {
         return "公司债结算费";
      }
      else if ("10".equals(name)) {
         return "股票风险金";
      }
      else if ("11".equals(name)) {
         return "债券风险金";
      }
      else if ("12".equals(name)) {
         return "回购风险金";
      }
      else if ("13".equals(name)) {
         return "基金风险金";
      }
      else if ("14".equals(name)) {
         return "印花税";
      }
      else if ("15".equals(name)) {
         return "过户费";
      }
      else if ("16".equals(name)) {
         return "权证经手费";
      }
      else if ("17".equals(name)) {
         return "权证证管费";
      }
      else if ("18".equals(name)) {
         return "债券结算费";
      }
      else if ("19".equals(name)) {//add by yanghaiming 20100419 新增费用承担方向
    	  return "股票结算费";
      }
      else {
         return ""; //未查到返回空
      }
   }

}
