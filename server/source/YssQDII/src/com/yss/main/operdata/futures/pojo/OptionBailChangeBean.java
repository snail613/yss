package com.yss.main.operdata.futures.pojo;
import java.math.BigDecimal;
/**
 * <p>Title: </p>
 *
 * <p>Description:期货保证金调整表对应的POJO类 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: Ysstech </p>
 *
 * BugNO:MS00481:QDV4中金2009年06月03日01_A 
 * 
 * @author xuqiji
 * @version 1.0
 */
public class OptionBailChangeBean {
   public OptionBailChangeBean() {
   }

   public String getSBrokerCode() {
      return SBrokerCode;
   }

   public String getSChangeDate() {
      return SChangeDate;
   }

   public String getSDesc() {
      return SDesc;
   }

   public String getSInvMgrCode() {
      return SInvMgrCode;
   }

   public String getSPortCode() {
      return SPortCode;
   }

   public String getSSecurityCode() {
      return SSecurityCode;
   }

   public String getSRecycled() {
      return sRecycled;
   }

    public String getSOldPortCode() {
        return SOldPortCode;
    }
    //delete by jiangshichao 【STORY #863 香港、美国股指期权交易区别  期权需求变更】2011.06.18
//    public String getSOldInvMgrCode() {
//        return SOldInvMgrCode;
//    }

    public String getSOldChangeDate() {
        return SOldChangeDate;
    }

    public String getSOldBrokerCode() {
        return SOldBrokerCode;
    }

    public String getSChageBailAcctName() {
      return SChageBailAcctName;
   }

   public String getSBegBailAcctName() {
      return SBegBailAcctName;
   }

   public String getSChageBailAcctCode() {
      return SChageBailAcctCode;
   }

   public String getSBegBailAcctCode() {
      return SBegBailAcctCode;
   }
   //delete by jiangshichao 【STORY #863 香港、美国股指期权交易区别  期权需求变更】2011.06.18
//   public String getSOldSecurityCode() {
//      return SOldSecurityCode;
//   }

   public String getSSecurityName() {
      return SSecurityName;
   }

   public String getSPortName() {
      return SPortName;
   }

   public String getSInvMgrName() {
      return SInvMgrName;
   }

   public String getSBrokerName() {
      return SBrokerName;
   }

   public BigDecimal getSChangeMoney() {
      return SChangeMoney;
   }

   public void setSBrokerCode(String SBrokerCode) {
      this.SBrokerCode = SBrokerCode;
   }

   public void setSChangeDate(String SChangeDate) {
      this.SChangeDate = SChangeDate;
   }

   public void setSDesc(String SDesc) {
      this.SDesc = SDesc;
   }

   public void setSInvMgrCode(String SInvMgrCode) {
      this.SInvMgrCode = SInvMgrCode;
   }

   public void setSPortCode(String SPortCode) {
      this.SPortCode = SPortCode;
   }

   public void setSSecurityCode(String SSecurityCode) {
      this.SSecurityCode = SSecurityCode;
   }

   public void setSRecycled(String sRecycled) {
      this.sRecycled = sRecycled;
   }

    public void setSOldPortCode(String SOldPortCode) {
        this.SOldPortCode = SOldPortCode;
    }
    //delete by jiangshichao 【STORY #863 香港、美国股指期权交易区别  期权需求变更】2011.06.18
//    public void setSOldInvMgrCode(String SOldInvMgrCode) {
//        this.SOldInvMgrCode = SOldInvMgrCode;
//    }

    public void setSOldChangeDate(String SOldChangeDate) {
        this.SOldChangeDate = SOldChangeDate;
    }

    public void setSOldBrokerCode(String SOldBrokerCode) {
        this.SOldBrokerCode = SOldBrokerCode;
    }

    public void setSChageBailAcctName(String SChageBailAcctName) {
      this.SChageBailAcctName = SChageBailAcctName;
   }

   public void setSBegBailAcctName(String SBegBailAcctName) {
      this.SBegBailAcctName = SBegBailAcctName;
   }

   public void setSChageBailAcctCode(String SChageBailAcctCode) {
      this.SChageBailAcctCode = SChageBailAcctCode;
   }

   public void setSBegBailAcctCode(String SBegBailAcctCode) {
      this.SBegBailAcctCode = SBegBailAcctCode;
   }
   //delete by jiangshichao 【STORY #863 香港、美国股指期权交易区别  期权需求变更】2011.06.18
//   public void setSOldSecurityCode(String SOldSecurityCode) {
//      this.SOldSecurityCode = SOldSecurityCode;
//   }

   public void setSSecurityName(String SSecurityName) {
      this.SSecurityName = SSecurityName;
   }

   public void setSPortName(String SPortName) {
      this.SPortName = SPortName;
   }

   public void setSInvMgrName(String SInvMgrName) {
      this.SInvMgrName = SInvMgrName;
   }

   public void setSBrokerName(String SBrokerName) {
      this.SBrokerName = SBrokerName;
   }

   public void setSChangeMoney(BigDecimal SChangeMoney) {
      this.SChangeMoney = SChangeMoney;
   }
   
	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	
   //【STORY #863 香港、美国股指期权交易区别  期权需求变更】add by jiangshichao 2011.06.18 start
   public String getsExchangeCode() {
		return sExchangeCode;
	}

	public void setsExchangeCode(String sExchangeCode) {
		this.sExchangeCode = sExchangeCode;
	}

	public String getsExchangeName() {
		return sExchangeName;
	}

	public void setsExchangeName(String sExchangeName) {
		this.sExchangeName = sExchangeName;
	}

	public String getSOldExchangeCode() {
		return SOldExchangeCode;
	}

	public void setSOldExchangeCode(String sOldExchangeCode) {
		SOldExchangeCode = sOldExchangeCode;
	}
	//【STORY #863 香港、美国股指期权交易区别  期权需求变更】add by jiangshichao 2011.06.18 end


private String sRecycled = ""; //回收站数据

   private String SSecurityCode;//证券代码
   private String SPortCode;//组合代码
   private String SBrokerCode;//券商代码
   private String sExchangeCode = "";//交易所代码 add by jiangshichao 【STORY #863 香港、美国股指期权交易区别  期权需求变更】 2011.06.18
   private String SInvMgrCode;//投资经理
   private String SChangeDate; //调整日期
   private BigDecimal SChangeMoney;//调整金额
   private String SDesc;//描述

   private String SBegBailAcctCode="";//初始保证金帐户
   private String SChageBailAcctCode="";//变动保证金帐户

   private String SSecurityName;//证券名称
   private String SPortName;//组合名称
   private String SBrokerName;//券商名称
   private String SInvMgrName;//投资经理名

   private String SBegBailAcctName="";//初始保证金帐户名
   private String SChageBailAcctName="";//变动保证金帐户名

   private String sExchangeName = "";//交易所代码 add by jiangshichao 【STORY #863 香港、美国股指期权交易区别  期权需求变更】 2011.06.18
   
  //private String SOldSecurityCode="";//旧证券代码  delete by jiangshichao 【STORY #863 香港、美国股指期权交易区别  期权需求变更】2011.06.18
   private String SOldPortCode="";  //旧投资组合
   private String SOldBrokerCode="";//旧投资券商
   private String SOldExchangeCode = "";//旧交易所代码 add by jiangshichao 【STORY #863 香港、美国股指期权交易区别  期权需求变更】 2011.06.18
   //private String SOldInvMgrCode="";//旧投资经理 delete by jiangshichao  【STORY #863 香港、美国股指期权交易区别  期权需求变更】2011.06.18
   private String SOldChangeDate="";//旧调整日期
   //-----------20100427 蒋锦 添加 南方东英期权需求----------//
   //MS01134 增加股票期权和股指期权业务
   private String categoryCode = "";        //品种代码
   //-----------------------------------------------------//

}
