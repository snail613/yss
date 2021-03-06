package com.yss.main.etfoperation.pojo;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 2009-11-17
 * @author 宋洁
 * ETF实际
 */
public class ETFRateBatchCreateBean extends BaseDataSettingBean{

	private String PortCode; // 组合代码
	private String PortName; // 投资名称
	private String bookType; //台帐类型
	private double changeRate = 0;//换汇汇率
	private java.util.Date etfBookDate = null;////申赎日期
	private java.util.Date changeRateDate = null;//汇率确认日
	
	private String oldPortCode; // 组合代码
	private String oldPortName; // 投资名称
	private String oldBookType; //台帐类型
	private double oldChangeRate = 0;//换汇汇率
	private java.util.Date oldETFBookDate = null;////申赎日期
	private java.util.Date oldChangeRateDate = null;//汇率确认日
	private boolean bShow = false;
	
	private ETFRateBatchCreateBean filterType;
	
	public boolean isbShow() {
		return bShow;
	}

	public void setbShow(boolean bShow) {
		this.bShow = bShow;
	}

	public ETFRateBatchCreateBean() {
		
	}
	
	public String getPortCode() {
		return PortCode;
	}

	public void setPortCode(String portCode) {
		PortCode = portCode;
	}

	public String getPortName() {
		return PortName;
	}

	public void setPortName(String portName) {
		PortName = portName;
	}

	public String getBookType() {
		return bookType;
	}

	public void setBookType(String bookType) {
		this.bookType = bookType;
	}

	public double getChangeRate() {
		return changeRate;
	}

	public void setChangeRate(double changeRate) {
		this.changeRate = changeRate;
	}

	public java.util.Date getEtfBookDate() {
		return etfBookDate;
	}

	public void setEtfBookDate(java.util.Date etfBookDate) {
		this.etfBookDate = etfBookDate;
	}

	public java.util.Date getChangeRateDate() {
		return changeRateDate;
	}

	public void setChangeRateDate(java.util.Date changeRateDate) {
		this.changeRateDate = changeRateDate;
	}
	
	 /**
     * 为各项变量赋值
     *
     * @param rs
     *            ResultSet
     * @throws SQLException
     */
    public void setETFRateAttr(ResultSet rs) throws SQLException, YssException {
        this.PortCode = rs.getString("FPortCode");
        this.PortName = rs.getString("FPortName");
        this.bookType = rs.getString("FBookType"); //台帐类型
        this.changeRate = rs.getDouble("FExRateValue");//换汇汇率
        this.etfBookDate = rs.getDate("FBuyDate");////申赎日期
        this.changeRateDate = rs.getDate("FExRateDate");//汇率确认日
        
        super.setRecLog(rs);
    }
    
    /**
     * 通过拼接字符串来获取数据字符串
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        
        buf.append(this.changeRate).append("\t");
        buf.append(this.bookType).append("\t");
        buf.append(this.etfBookDate).append("\t");
        buf.append(this.changeRateDate).append("\t");
        buf.append(this.PortCode).append("\t");
        buf.append(this.PortName).append("\t");
        
        buf.append(super.buildRecLog());
        
        return buf.toString();
    }
    
    /**
     * 解析前台发送来的操作组合设置请求
     *
     * @param sRowStr
     *            String
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
            this.changeRate = reqAry[0].trim().length()==0?0:Double.parseDouble(reqAry[0]);
            this.bookType = reqAry[1];
            this.etfBookDate = YssFun.parseDate(reqAry[2].trim().length()==0?"9998-12-31":reqAry[2]);
            this.changeRateDate = YssFun.parseDate(reqAry[3].trim().length()==0?"9998-12-31":reqAry[3]);
            this.PortCode = reqAry[4];
            this.PortName = reqAry[5];
            
            this.oldChangeRate = reqAry[6].trim().length()==0?0:Double.parseDouble(reqAry[6]);
            this.oldBookType = reqAry[7];
            this.oldETFBookDate = YssFun.parseDate(reqAry[8].trim().length()==0?"9998-12-31":reqAry[8]);
            this.oldChangeRateDate = YssFun.parseDate(reqAry[9].trim().length()==0?"9998-12-31":reqAry[9]);
            this.oldPortCode = reqAry[10];
            this.oldPortName = reqAry[11];
            if (reqAry[12].equalsIgnoreCase("true")) {
                this.bShow = true;
            } else {
                this.bShow = false;
            } 
            
            super.parseRecLog();
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new ETFRateBatchCreateBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);    
            }
        } catch (Exception e) {
            throw new YssException("解析ETF参数数据出错！", e);
        }
    }

	public ETFRateBatchCreateBean getFilterType() {
		return filterType;
	}

	public void setFilterType(ETFRateBatchCreateBean filterType) {
		this.filterType = filterType;
	}

	public String getOldPortCode() {
		return oldPortCode;
	}

	public void setOldPortCode(String oldPortCode) {
		this.oldPortCode = oldPortCode;
	}

	public String getOldPortName() {
		return oldPortName;
	}

	public void setOldPortName(String oldPortName) {
		this.oldPortName = oldPortName;
	}

	public String getOldBookType() {
		return oldBookType;
	}

	public void setOldBookType(String oldBookType) {
		this.oldBookType = oldBookType;
	}

	public double getOldChangeRate() {
		return oldChangeRate;
	}

	public void setOldChangeRate(double oldChangeRate) {
		this.oldChangeRate = oldChangeRate;
	}

	public java.util.Date getOldETFBookDate() {
		return oldETFBookDate;
	}

	public void setOldETFBookDate(java.util.Date oldETFBookDate) {
		this.oldETFBookDate = oldETFBookDate;
	}

	public java.util.Date getOldChangeRateDate() {
		return oldChangeRateDate;
	}

	public void setOldChangeRateDate(java.util.Date oldChangeRateDate) {
		this.oldChangeRateDate = oldChangeRateDate;
	}
}
