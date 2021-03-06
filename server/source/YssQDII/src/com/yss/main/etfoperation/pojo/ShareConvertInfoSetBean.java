package com.yss.main.etfoperation.pojo;

import com.yss.dsub.*;

import java.math.BigDecimal;
import java.sql.ResultSet;
import com.yss.util.YssException;
import java.sql.SQLException;

/**
 * <p>Title: 20091116 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A</p>
 *
 * <p>Description: 份额折算实体bean</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ShareConvertInfoSetBean
      extends BaseDataSettingBean {
   private String PortCode; //投资组合
   private String PortName; //投资名称
   private String ConvertDate; //份额折算日
   private BigDecimal ConvertScale; //份额折算比例
   private String Desc; //份额折算信息描述

   private String OldPortCode = "";
   private String OldConvertDate="";//份额折算日
   private ShareConvertInfoSetBean filterType;

   public ShareConvertInfoSetBean() {
   }

   public String getConvertDate() {
      return ConvertDate;
   }

   public BigDecimal getConvertScale() {
      return ConvertScale;
   }

   public String getDesc() {
      return Desc;
   }

   public String getPortCode() {
      return PortCode;
   }

   public String getPortName() {
      return PortName;
   }

   public ShareConvertInfoSetBean getFilterType() {
      return filterType;
   }

   public String getOldPortCode() {
      return OldPortCode;
   }

   public void setConvertDate(String ConvertDate) {
      this.ConvertDate = ConvertDate;
   }

   public void setConvertScale(BigDecimal ConvertScale) {
      this.ConvertScale = ConvertScale;
   }

   public void setDesc(String Desc) {
      this.Desc = Desc;
   }

   public void setPortCode(String PortCode) {
      this.PortCode = PortCode;
   }

   public void setPortName(String PortName) {
      this.PortName = PortName;
   }

   public void setFilterType(ShareConvertInfoSetBean filterType) {
      this.filterType = filterType;
   }

   public void setOldPortCode(String OldPortCode) {
      this.OldPortCode = OldPortCode;
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
         }
         else {
            sTmpStr = sRowStr;
         }
         reqAry = sTmpStr.split("\t");
         this.checkStateId = new Integer(reqAry[0]).intValue();
         this.PortCode = reqAry[1];
         this.ConvertDate = reqAry[2];
         if (reqAry[3].length() != 0) {
            this.ConvertScale = new BigDecimal(reqAry[3]);
         }
         // modify by fangjiang 2010.09.30 MS01810 QDV4赢时胜(测试)2010年09月25日05_B 
         if (reqAry[4] != null ){
         	if (reqAry[4].indexOf("【Enter】") >= 0){
         		this.Desc = reqAry[4].replaceAll("【Enter】", "\r\n");
         	}
         	else{
         		this.Desc = reqAry[4];
         	}
         }
         //------------------
         this.OldPortCode = reqAry[5];
         this.OldConvertDate=reqAry[6];
         super.parseRecLog();
         if (sRowStr.indexOf("\r\t") >= 0) {
            if (this.filterType == null) {
               this.filterType = new ShareConvertInfoSetBean();
               this.filterType.setYssPub(pub);
            }
            if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("null")) {
            	this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
         }
      }
      catch (Exception e) {
         throw new YssException("解析份额折算数据出错！", e);
      }
   }

   /**
    * 通过拼接字符串来获取数据字符串
    * @return String
    */
   public String buildRowStr() {
      StringBuffer buf = new StringBuffer();
      buf.append(this.PortCode).append("\t");
      buf.append(this.PortName).append("\t");
      buf.append(this.ConvertDate).append("\t");
      buf.append(this.ConvertScale).append("\t");
      buf.append(this.Desc).append("\t");
      buf.append(super.buildRecLog());
      return buf.toString();
   }

   /**
    * 为各项变量赋值
    * @param rs ResultSet
    * @throws SQLException
    */
   public void setShareConvertInfoAttr(ResultSet rs) throws SQLException,
         YssException {
      this.PortCode = rs.getString("FPortCode") + "";
      this.PortName = rs.getString("FPortName") + "";
      this.ConvertDate = rs.getDate("FConvertDate") + "";
      this.ConvertScale = rs.getBigDecimal("FConvertScale");
      this.Desc = rs.getString("FDESC") + "";
      super.setRecLog(rs);
   }

public String getOldConvertDate() {
	return OldConvertDate;
}

public void setOldConvertDate(String oldConvertDate) {
	OldConvertDate = oldConvertDate;
}

}
