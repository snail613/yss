package com.yss.main.operdata.overthecounter.pojo;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class FreezeSecSetBean extends BaseDataSettingBean {

	    private String num = ""; //回购业务编号
	    private String securityCode = ""; //交易证券代码
	    private String securityName = ""; //交易证券名称
	    private String portCode = ""; //组合代码
	    private String portName = ""; //组合名称
	    private String bargainDate = "1900-01-01"; //成交日期
	    private double freezeAmount = 0;
	    private FreezeSecSetBean filterType;
	    
	    
		public String getNum() {
			return num;
		}
		public void setNum(String num) {
			this.num = num;
		}
		public String getSecurityCode() {
			return securityCode;
		}
		public void setSecurityCode(String securityCode) {
			this.securityCode = securityCode;
		}
		public String getSecurityName() {
			return securityName;
		}
		public void setSecurityName(String securityName) {
			this.securityName = securityName;
		}
		public String getPortCode() {
			return portCode;
		}
		public void setPortCode(String portCode) {
			this.portCode = portCode;
		}
		public String getPortName() {
			return portName;
		}
		public void setPortName(String portName) {
			this.portName = portName;
		}
		public String getBargainDate() {
			return bargainDate;
		}
		public void setBargainDate(String bargainDate) {
			this.bargainDate = bargainDate;
		}
		public double getFreezeAmount() {
			return freezeAmount;
		}
		public void setFreezeAmount(double freezeAmount) {
			this.freezeAmount = freezeAmount;
		}
		public FreezeSecSetBean getFilterType() {
			return filterType;
		}
		public void setFilterType(FreezeSecSetBean filterType) {
			this.filterType = filterType;
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
	            } else {
	                sTmpStr = sRowStr;
	            }
	            reqAry = sTmpStr.split("\t");
	            this.num = reqAry[0];
	            this.portCode = reqAry[1];
	            this.securityCode = reqAry[2];
	            this.bargainDate = reqAry[3];
	            if(YssFun.isNumeric(reqAry[4])){
	            	freezeAmount= YssFun.toDouble(reqAry[4]);
	        	}
	            //super.parseRecLog();
	            if (sRowStr.indexOf("\r\t") >= 0) {
	                if (this.filterType == null) {
	                    this.filterType = new FreezeSecSetBean();
	                    this.filterType.setYssPub(pub);
	                }
	                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
	                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
	                }
	            }
	        } catch (Exception e) {
	            throw new YssException("解析回购业务设置请求出错", e);
	        }
	    }

	    /**
	     * 通过拼接字符串来获取数据字符串
	     * @return String
	     * @throws YssException
	     */
	    public String buildRowStr() {
	        StringBuffer buf = new StringBuffer();
	        buf.append(this.num).append("\t");
	        buf.append(this.bargainDate).append("\t");
	        buf.append(this.securityCode).append("\t");
	        buf.append(this.securityName).append("\t");
	        buf.append(this.portCode).append("\t");
	        buf.append(this.portName).append("\t");
	        buf.append(this.freezeAmount).append("\t");
	        //buf.append(super.buildRecLog());
	        return buf.toString();
	    }

	    /**
	     * 为各项变量赋值
	     * @param rs ResultSet
	     * @throws SQLException
	     */
	    public void setfreezeSecSetAttr(ResultSet rs) throws SQLException,
	        YssException {
	        this.num = rs.getString("FNum") + "";
	        this.securityCode = rs.getString("FSecurityCode") + "";
	        this.securityName = rs.getString("FSecurityName") + "";
	        this.portCode = rs.getString("FPortCode") + "";
	        this.portName = rs.getString("FPortName") + "";
	        this.bargainDate = rs.getDate("FDate") + "";
	        this.freezeAmount = rs.getDouble("FFREEZEAMOUNT");
	       // super.setRecLog(rs);
	    }
	    
}
