package com.yss.main.operdeal.datainterface.cnstock.pojo;

import java.util.*;
import com.yss.util.YssException;
import com.yss.util.YssCons;

/**
 * QDV4.1赢时胜（上海）2009年4月20日11_A
 * MS00011
 * TB_PUB_DAO_ReadType 读数处理方式参数表的实体类
 * create by songjie
 * 2009-06-17
 */
public class ReadTypeBean {
   public ReadTypeBean() {
   }

   public String getAssetClass() {
      return assetClass;
   }

   public String getAssetGroupCode() {
      return assetGroupCode;
   }

   public int getExchangePreci() {
      return exchangePreci;
   }

   public String getParameter() {
      return parameter;
   }

   public String getPortCode() {
      return portCode;
   }

   public String getWBSBelong() {
      return wBSBelong;
   }

   public String getShNum() {
      return shNum;
   }

   public List getParameters() {
      return parameters;
   }

   public void setAssetClass(String assetClass) {
      this.assetClass = assetClass;
   }

   public void setAssetGroupCode(String assetGroupCode) {
      this.assetGroupCode = assetGroupCode;
   }

   public void setExchangePreci(int exchangePreci) {
      this.exchangePreci = exchangePreci;
   }

   public void setParameter(String parameter) {
      this.parameter = parameter;
   }

   public void setPortCode(String portCode) {
      this.portCode = portCode;
   }

   public void setWBSBelong(String wBSBelong) {
      this.wBSBelong = wBSBelong;
   }

   public void setShNum(String shNum) {
      this.shNum = shNum;
   }

   public void setParameters(List parameters) {
      this.parameters = parameters;
   }
   public int getExchangeFhggain() {
	      return exchangeFhggain;
	   }
   public void setExchangeFhggain(int exchangeFhggain) {
	      this.exchangeFhggain = exchangeFhggain;
	   }

   /**
    * 组装数据
    * @return String
    * @throws YssException
    */
   public String buildRowStr() throws YssException {
      StringBuffer buf = new StringBuffer();
      buf.append(assetClass).append(YssCons.YSS_ITEMSPLITMARK1); //默认资产分类
      buf.append(wBSBelong).append(YssCons.YSS_ITEMSPLITMARK1); //可分离债归入
      buf.append(exchangePreci).append(YssCons.YSS_ITEMSPLITMARK1); //债券利息保留位数交易所每百元
      buf.append(shNum).append(YssCons.YSS_ITEMSPLITMARK1); //上海对账库信箱号
      buf.append(parameter).append(YssCons.YSS_ITEMSPLITMARK1); //参数
      buf.append(holidaysCode).append(YssCons.YSS_ITEMSPLITMARK1);//节假日群代码  add by yanghaiming 20100223 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
      //fanghaoln 20100427 MS01079 QDV4招商基金2010年4月9日01_B 
      buf.append(exchangeFhggain).append(YssCons.YSS_ITEMSPLITMARK1);//回购收益保留位数//edit by yanghaiming 20100517增加分隔符；
      //---------------------------end ---MS01079-----
      //add by yanghaiming 20100417 B股业务
      buf.append(holidaysName).append(YssCons.YSS_ITEMSPLITMARK1);
      buf.append(holidaysCodeSH).append(YssCons.YSS_ITEMSPLITMARK1);
      buf.append(holidaysNameSH).append(YssCons.YSS_ITEMSPLITMARK1);
      buf.append(holidaysCodeSZ).append(YssCons.YSS_ITEMSPLITMARK1);
      buf.append(holidaysNameSZ).append(YssCons.YSS_ITEMSPLITMARK1);
      buf.append(invMgrCodeSH).append(YssCons.YSS_ITEMSPLITMARK1);
      buf.append(invMgrCodeSZ).append(YssCons.YSS_ITEMSPLITMARK1);
      buf.append(currencyCodeA).append(YssCons.YSS_ITEMSPLITMARK1);
      buf.append(currencyNameA).append(YssCons.YSS_ITEMSPLITMARK1);
      buf.append(currencyCodeSHB).append(YssCons.YSS_ITEMSPLITMARK1);
      buf.append(currencyNameSHB).append(YssCons.YSS_ITEMSPLITMARK1);
      buf.append(currencyCodeSZB).append(YssCons.YSS_ITEMSPLITMARK1);
      buf.append(currencyNameSZB).append(YssCons.YSS_ITEMSPLITMARK1);
      buf.append(delayDateA).append(YssCons.YSS_ITEMSPLITMARK1);
      buf.append(delayDateB);
      //add by yanghaiming 20100417 B股业务
      return buf.toString();
   }

   private String assetGroupCode = "";
   private String portCode = "";
   private String assetClass = "";
   private String wBSBelong = "";
   private int exchangePreci = 0;
   private String shNum = "";
   private String parameter = "";
   private List parameters;
   private String holidaysCode = "";//节假日群代码   add by yanghaiming 20100223 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
 //fanghaoln 20100427 MS01079 QDV4招商基金2010年4月9日01_B 
   private int exchangeFhggain = 0;//回购收益保留位数
   //---------------------------end ---MS01079---------------------------
   //add by yanghaiming 20100417 B股业务
   private String holidaysName = "";
   private String holidaysCodeSH = "";
   private String holidaysNameSH = "";
   private String holidaysCodeSZ = "";
   private String holidaysNameSZ = "";
   private String invMgrCodeSH = "";
   private String invMgrCodeSZ = "";
   private String currencyCodeA = "";
   private String currencyNameA = "";
   private String currencyCodeSHB = "";
   private String currencyNameSHB = "";
   private String currencyCodeSZB = "";
   private String currencyNameSZB = "";
   private int delayDateA = 0;
   private int delayDateB = 0;
   //add by yanghaiming 20100417 B股业务
   
   
   public String getHolidaysName() {
		return holidaysName;
	}

	public void setHolidaysName(String holidaysName) {
		this.holidaysName = holidaysName;
	}

	public String getHolidaysNameSH() {
		return holidaysNameSH;
	}

	public void setHolidaysNameSH(String holidaysNameSH) {
		this.holidaysNameSH = holidaysNameSH;
	}

	public String getHolidaysNameSZ() {
		return holidaysNameSZ;
	}

	public void setHolidaysNameSZ(String holidaysNameSZ) {
		this.holidaysNameSZ = holidaysNameSZ;
	}
	
   public String getHolidaysCodeSH() {
	   return holidaysCodeSH;
   }

	public void setHolidaysCodeSH(String holidaysCodeSH) {
		this.holidaysCodeSH = holidaysCodeSH;
	}
	
	public String getHolidaysCodeSZ() {
		return holidaysCodeSZ;
	}
	
	public void setHolidaysCodeSZ(String holidaysCodeSZ) {
		this.holidaysCodeSZ = holidaysCodeSZ;
	}
	
	public String getInvMgrCodeSH() {
		return invMgrCodeSH;
	}
	
	public void setInvMgrCodeSH(String invMgrCodeSH) {
		this.invMgrCodeSH = invMgrCodeSH;
	}
	
	public String getInvMgrCodeSZ() {
		return invMgrCodeSZ;
	}
	
	public void setInvMgrCodeSZ(String invMgrCodeSZ) {
		this.invMgrCodeSZ = invMgrCodeSZ;
	}
	
	public String getCurrencyCodeA() {
		return currencyCodeA;
	}
	
	public void setCurrencyCodeA(String currencyCodeA) {
		this.currencyCodeA = currencyCodeA;
	}
	
	public String getCurrencyCodeSHB() {
		return currencyCodeSHB;
	}
	
	public void setCurrencyCodeSHB(String currencyCodeSHB) {
		this.currencyCodeSHB = currencyCodeSHB;
	}
	
	public String getCurrencyCodeSZB() {
		return currencyCodeSZB;
	}
	
	public void setCurrencyCodeSZB(String currencyCodeSZB) {
		this.currencyCodeSZB = currencyCodeSZB;
	}
	
	public int getDelayDateA() {
		return delayDateA;
	}
	
	public void setDelayDateA(int delayDateA) {
		this.delayDateA = delayDateA;
	}
	
	public int getDelayDateB() {
		return delayDateB;
	}
	
	public void setDelayDateB(int delayDateB) {
		this.delayDateB = delayDateB;
	}
	
	public String getHolidaysCode() {
		return holidaysCode;
	}
	
	public void setHolidaysCode(String holidaysCode) {
		this.holidaysCode = holidaysCode;
	}
	
	public String getCurrencyNameA() {
		return currencyNameA;
	}

	public void setCurrencyNameA(String currencyNameA) {
		this.currencyNameA = currencyNameA;
	}

	public String getCurrencyNameSHB() {
		return currencyNameSHB;
	}

	public void setCurrencyNameSHB(String currencyNameSHB) {
		this.currencyNameSHB = currencyNameSHB;
	}

	public String getCurrencyNameSZB() {
		return currencyNameSZB;
	}

	public void setCurrencyNameSZB(String currencyNameSZB) {
		this.currencyNameSZB = currencyNameSZB;
	}
	
}
