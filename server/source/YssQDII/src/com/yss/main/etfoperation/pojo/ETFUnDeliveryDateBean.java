package com.yss.main.etfoperation.pojo;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
/**
 * ETF非交收日设置
 * @author yanghaiming
 *
 */
public class ETFUnDeliveryDateBean extends BaseDataSettingBean {
	private ETFUnDeliveryDateBean FilterType = null;

    private String PortCode = "";//组合代码
    private String PortName = "";//组合名称
    private String HolidayCode = "";//节假日群代码
    private String HolidayName = "";//节假日群名称
    private java.util.Date StartDeliveryDate = null;//非交收起始日
    private java.util.Date EndDeliveryDate = null;//非交收截至日
    private String Desc = "";//描述信息
    
    private String OldPortCode = "";//组合代码
    private String OldPortName = "";//组合名称
    private String OldHolidayCode = "";//节假日群代码
    private String OldHolidayName = "";//节假日群名称
    private java.util.Date OldStartDeliveryDate = null;//非交收起始日
    private java.util.Date OldEndDeliveryDate = null;//非交收截至日
    private String OldDesc = "";//描述信息

    public String getOldPortCode() {
		return OldPortCode;
	}

	public void setOldPortCode(String oldPortCode) {
		OldPortCode = oldPortCode;
	}

	public String getOldPortName() {
		return OldPortName;
	}

	public void setOldPortName(String oldPortName) {
		OldPortName = oldPortName;
	}

	public String getOldHolidayCode() {
		return OldHolidayCode;
	}

	public void setOldHolidayCode(String oldHolidayCode) {
		OldHolidayCode = oldHolidayCode;
	}

	public String getOldHolidayName() {
		return OldHolidayName;
	}

	public void setOldHolidayName(String oldHolidayName) {
		OldHolidayName = oldHolidayName;
	}

	public java.util.Date getOldStartDeliveryDate() {
		return OldStartDeliveryDate;
	}

	public void setOldStartDeliveryDate(java.util.Date oldStartDeliveryDate) {
		OldStartDeliveryDate = oldStartDeliveryDate;
	}

	public java.util.Date getOldEndDeliveryDate() {
		return OldEndDeliveryDate;
	}

	public void setOldEndDeliveryDate(java.util.Date oldEndDeliveryDate) {
		OldEndDeliveryDate = oldEndDeliveryDate;
	}

	public String getOldDesc() {
		return OldDesc;
	}

	public void setOldDesc(String oldDesc) {
		OldDesc = oldDesc;
	}

	public boolean isbShow() {
		return bShow;
	}

	public void setbShow(boolean bShow) {
		this.bShow = bShow;
	}

	private boolean bShow = false;

	public ETFUnDeliveryDateBean getFilterType() {
		return FilterType;
	}

	public void setFilterType(ETFUnDeliveryDateBean filterType) {
		FilterType = filterType;
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

	public String getHolidayCode() {
		return HolidayCode;
	}

	public void setHolidayCode(String holidayCode) {
		HolidayCode = holidayCode;
	}

	public String getHolidayName() {
		return HolidayName;
	}

	public void setHolidayName(String holidayName) {
		HolidayName = holidayName;
	}

	public java.util.Date getStartDeliveryDate() {
		return StartDeliveryDate;
	}

	public void setStartDeliveryDate(java.util.Date startDeliveryDate) {
		StartDeliveryDate = startDeliveryDate;
	}

	public java.util.Date getEndDeliveryDate() {
		return EndDeliveryDate;
	}

	public void setEndDeliveryDate(java.util.Date endDeliveryDate) {
		EndDeliveryDate = endDeliveryDate;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public boolean BShow() {
		return bShow;
	}

	public void setM_BShow(boolean mBShow) {
		bShow = mBShow;
	}
	
	
	/**
     * 为各项变量赋值
     *
     * @param rs ResultSet
     * @throws SQLException
     */
    public void setETFRateAttr(ResultSet rs) throws SQLException, YssException {
        this.PortCode = rs.getString("FPortCode");
        this.PortName = rs.getString("FPortName");
        this.HolidayCode = rs.getString("FHolidayCode");
        this.HolidayName = rs.getString("FHolidayName");
        this.StartDeliveryDate = rs.getDate("FUnSettleDate");
        this.Desc = rs.getString("FDesc");
        
        super.setRecLog(rs);
    }
    
    /**
     * 通过拼接字符串来获取数据字符串
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        
        buf.append(this.PortCode).append("\t");
        buf.append(this.PortName).append("\t");
        buf.append(this.HolidayCode).append("\t");
        buf.append(this.HolidayName).append("\t");
        buf.append(this.StartDeliveryDate).append("\t");
        buf.append(this.Desc).append("\t");
        
        buf.append(super.buildRecLog());
        
        return buf.toString();
    }
    
    /**
     * 解析前台发送来的操作组合设置请求
     *
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
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.PortCode = reqAry[0];
            this.PortName = reqAry[1];
            this.HolidayCode = reqAry[2];
            this.HolidayName = reqAry[3];
            this.StartDeliveryDate = YssFun.parseDate(reqAry[4].trim().length()==0?"9998-12-31":reqAry[4]);
            this.EndDeliveryDate = YssFun.parseDate(reqAry[5].trim().length()==0?"9998-12-31":reqAry[5]);
            // modify by fangjiang 2010.09.30 MS01808 QDV4赢时胜(测试)2010年09月25日03_B 
            if (reqAry[6] != null ){
            	if (reqAry[6].indexOf("【Enter】") >= 0){
            		this.Desc = reqAry[6].replaceAll("【Enter】", "\r\n");
            	}
            	else{
            		this.Desc = reqAry[6];
            	}
            }
            //------------------
            this.OldPortCode = reqAry[7];
            this.OldPortName = reqAry[8];
            this.OldHolidayCode = reqAry[9];
            this.OldHolidayName = reqAry[10];
            this.OldStartDeliveryDate = YssFun.parseDate(reqAry[11].trim().length()==0?"9998-12-31":reqAry[11]);
            this.OldEndDeliveryDate = YssFun.parseDate(reqAry[12].trim().length()==0?"9998-12-31":reqAry[12]);
            this.OldDesc = reqAry[13];
            
            if (reqAry[14].equalsIgnoreCase("true")) {
                this.bShow = true;
            } else {
                this.bShow = false;
            } 
            
            super.parseRecLog();
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new ETFUnDeliveryDateBean();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);    
            }
        } catch (Exception e) {
            throw new YssException("解析ETF非交收日数据出错！", e);
        }
    }

}
