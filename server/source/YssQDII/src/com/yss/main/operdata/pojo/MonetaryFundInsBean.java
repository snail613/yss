package com.yss.main.operdata.pojo;

import java.math.*;
import java.sql.*;

import com.yss.dsub.*;
import com.yss.util.*;

public class MonetaryFundInsBean extends BaseDataSettingBean {
	private String strSecurityCode = "";// 交易债券代码
	private String strSecurityName = "";// 交易债券名称
	private String strReadDate = "9998-12-31";// 公告日期
	private String strBargainDate = "9998-12-31";// 净值日期
	private BigDecimal bdFundRate = new BigDecimal(0);// 每万份收益
	private String strDesc = "";// 描述
	private String strOldSecurityCode = "";
	private String strOldBargainDate = "";
	private MonetaryFundInsBean filterType;
	private boolean bShow = false; // 标记是否在前台界面打开时将数据加载出来，默认为否

	public MonetaryFundInsBean() {
	}

	public String getStrSecurityName() {
		return strSecurityName;
	}

	public String getStrSecurityCode() {
		return strSecurityCode;
	}

	public String getStrReadDate() {
		return strReadDate;
	}

	public String getStrOldSecurityCode() {
		return strOldSecurityCode;
	}

	public String getStrOldBargainDate() {
		return strOldBargainDate;
	}

	public String getStrDesc() {
		return strDesc;
	}

	public String getStrBargainDate() {
		return strBargainDate;
	}

	public MonetaryFundInsBean getFilterType() {
		return filterType;
	}

	public void setBdFundRate(BigDecimal bdFundRate) {
		this.bdFundRate = bdFundRate;
	}

	public void setStrSecurityName(String strSecurityName) {
		this.strSecurityName = strSecurityName;
	}

	public void setStrSecurityCode(String strSecurityCode) {
		this.strSecurityCode = strSecurityCode;
	}

	public void setStrReadDate(String strReadDate) {
		this.strReadDate = strReadDate;
	}

	public void setStrOldSecurityCode(String strOldSecurityCode) {
		this.strOldSecurityCode = strOldSecurityCode;
	}

	public void setStrOldBargainDate(String strOldBargainDate) {
		this.strOldBargainDate = strOldBargainDate;
	}

	public void setStrDesc(String strDesc) {
		this.strDesc = strDesc;
	}

	public void setStrBargainDate(String strBargainDate) {
		this.strBargainDate = strBargainDate;
	}

	public void setFilterType(MonetaryFundInsBean filterType) {
		this.filterType = filterType;
	}

	public void setBShow(boolean bShow) {
		this.bShow = bShow;
	}

	public BigDecimal getBdFundRate() {
		return bdFundRate;
	}

	public boolean isBShow() {
		return bShow;
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
			this.strSecurityCode = reqAry[0];
			this.strReadDate = reqAry[1];
			this.strBargainDate = reqAry[2];
			if (YssFun.isNumeric(reqAry[3])) {
				this.bdFundRate = new BigDecimal(reqAry[3]);
			}
			// ------ modify by nimengjing 2010.12.02 BUG #535
			// 指数行情设置界面描述字段中存在回车符时，清除/还原报错
			if (reqAry[4] != null) {
				if (reqAry[4].indexOf("【Enter】") >= 0) {
					this.strDesc = reqAry[4].replaceAll("【Enter】", "\r\n");
				} else {
					this.strDesc = reqAry[4];
				}
			}
			// ----------------- BUG #533 ----------------//
			this.checkStateId = Integer.parseInt(reqAry[5]);
			this.strOldSecurityCode = reqAry[6];
			this.strOldBargainDate = reqAry[7];
			if (reqAry[8].equalsIgnoreCase("true")) {
				this.bShow = true;
			} else {
				this.bShow = false;
			}
			super.parseRecLog();
			if (sRowStr.indexOf("\r\t") >= 0) {
				if (this.filterType == null) {
					this.filterType = new MonetaryFundInsBean();
					this.filterType.setYssPub(pub);
				}
				this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
			}
		} catch (Exception e) {
			throw new YssException("解析货币基金每万份收益率数据出错！", e);
		}
	}

	/**
	 * 通过拼接字符串来获取数据字符串
	 * 
	 * @return String
	 */
	public String buildRowStr() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.strSecurityCode).append("\t");
		buf.append(this.strSecurityName).append("\t");
		buf.append(this.strReadDate).append("\t");
		buf.append(this.strBargainDate).append("\t");
		buf.append(this.bdFundRate).append("\t");
		buf.append(this.strDesc).append("\t");
		buf.append(super.buildRecLog());
		return buf.toString();
	}

	/**
	 * 为各项变量赋值
	 * 
	 * @param rs
	 *            ResultSet
	 * @throws SQLException
	 */
	public void setMonetaryFundInsAttr(ResultSet rs) throws SQLException,
			YssException {
		this.strSecurityCode = rs.getString("FSecurityCode") + "";
		this.strSecurityName = rs.getString("FSecurityName") + "";
		this.strReadDate = rs.getString("FReadDate") + "";
		this.strBargainDate = rs.getString("FBargainDate") + "";
		this.bdFundRate = rs.getBigDecimal("FFundRate");
		this.strDesc = rs.getString("FDesc") + "";
		super.setRecLog(rs);
	}

}
