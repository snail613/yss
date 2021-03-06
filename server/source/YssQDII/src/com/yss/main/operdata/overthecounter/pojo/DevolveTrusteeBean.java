package com.yss.main.operdata.overthecounter.pojo;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.util.*;
/**
 *
 * <p>Title: </p>
 * 债券转托管实体bean
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: yss</p>
 *
 * @author panjunfang create 20090722
 * @version 1.0
 */

public class DevolveTrusteeBean
    extends BaseDataSettingBean {
    private String strTradeNo = "";//交易编号
    private String strSecurityCode = "";//交易债券代码
    private String strSecurityName = "";//交易债券名称
    private String strBargainDate = "1900-01-01"; //成交日期
    private String strInvestTypeCode = "";//投资分类
    private String strPortCode = ""; //组合代码
    private String strPortName = ""; //组合名称
    private String strInvMgrCode = ""; //投资经理代码
    private String strInvMgrName = ""; //投资经理名称
    private String strAttrClsCode = "";//属性分类代码
    private String strAttrClsName = "";//属性分类名称
    private String strSrcExchangeCode = ""; //交易市场代码
    private String strSrcExchangeName = ""; //交易市场名称
    private String strTgtExchangeCode = ""; //托管市场代码
    private String strTgtExchangeName = ""; //托管市场名称
    private double dbPortCuryRate; //组合汇率
    private double dbBaseCuryRate; //基础汇率
    private double dbOutAmount;//转出数量
    private double dbOutMoney;//转出金额
    private double dbOutValInc;//转出估值增值
    private double dbOutDiscount;//转出溢折价
    private double dbOutInverest;//转出利息
    private String strStartDate = "";//开始日期，用于查询成交日期在选定范围内的业务数据
    private String strEndDate = "";//结束日期，用于查询成交日期在选定范围内的业务数据
    private String bShow = "0"; //标记是否在前台界面打开时将数据加载出来，默认为否		//20111027 modified by liubo.Story #1285.更改此变量的类型，使之与前台界面的基类的m_sIsOnlyColumns的类型吻合
    private String strBondTradeType = "";//债券业务交易类型 xuqiji 20100412	
    private String inSecurityCode="";//转入证券 add by zhouwei 20120419
    private String inSecurityName="";
    private String inInvestTypeCode="";//转入投资类型 add by zhouwei 20120424
    private String inAttrClsCode="";
    private String inAttrClsName="";
    private DevolveTrusteeBean filterType;

    public DevolveTrusteeBean() {
    }

    public String getInInvestTypeCode() {
		return inInvestTypeCode;
	}

	public void setInInvestTypeCode(String inInvestTypeCode) {
		this.inInvestTypeCode = inInvestTypeCode;
	}

	public String getInAttrClsCode() {
		return inAttrClsCode;
	}

	public void setInAttrClsCode(String inAttrClsCode) {
		this.inAttrClsCode = inAttrClsCode;
	}

	public String getInAttrClsName() {
		return inAttrClsName;
	}

	public void setInAttrClsName(String inAttrClsName) {
		this.inAttrClsName = inAttrClsName;
	}

    public String getStrTradeNo() {
        return strTradeNo;
    }

    public String getStrTgtExchangeName() {
        return strTgtExchangeName;
    }

    public String getStrTgtExchangeCode() {
        return strTgtExchangeCode;
    }

    public String getStrSrcExchangeName() {
        return strSrcExchangeName;
    }

    public String getStrSrcExchangeCode() {
        return strSrcExchangeCode;
    }

    public String getStrSecurityName() {
        return strSecurityName;
    }

    public String getStrSecurityCode() {
        return strSecurityCode;
    }

    public String getStrPortName() {
        return strPortName;
    }

    public String getStrPortCode() {
        return strPortCode;
    }

    public String getStrInvestTypeCode() {
        return strInvestTypeCode;
    }

    public String getStrInvMgrName() {
        return strInvMgrName;
    }

    public String getStrInvMgrCode() {
        return strInvMgrCode;
    }

    public String getStrBargainDate() {
        return strBargainDate;
    }

    public DevolveTrusteeBean getFilterType() {
        return filterType;
    }

    public double getDbPortCuryRate() {
        return dbPortCuryRate;
    }

    public double getDbOutValInc() {
        return dbOutValInc;
    }

    public double getDbOutMoney() {
        return dbOutMoney;
    }

    public double getDbOutInverest() {
        return dbOutInverest;
    }

    public double getDbOutDiscount() {
        return dbOutDiscount;
    }

    public double getDbOutAmount() {
        return dbOutAmount;
    }

    public void setDbBaseCuryRate(double dbBaseCuryRate) {
        this.dbBaseCuryRate = dbBaseCuryRate;
    }

    public void setStrTradeNo(String strTradeNo) {
        this.strTradeNo = strTradeNo;
    }

    public void setStrTgtExchangeName(String strTgtExchangeName) {
        this.strTgtExchangeName = strTgtExchangeName;
    }

    public void setStrTgtExchangeCode(String strTgtExchangeCode) {
        this.strTgtExchangeCode = strTgtExchangeCode;
    }

    public void setStrSrcExchangeName(String strSrcExchangeName) {
        this.strSrcExchangeName = strSrcExchangeName;
    }

    public void setStrSrcExchangeCode(String strSrcExchangeCode) {
        this.strSrcExchangeCode = strSrcExchangeCode;
    }

    public void setStrSecurityName(String strSecurityName) {
        this.strSecurityName = strSecurityName;
    }

    public void setStrSecurityCode(String strSecurityCode) {
        this.strSecurityCode = strSecurityCode;
    }

    public void setStrPortName(String strPortName) {
        this.strPortName = strPortName;
    }

    public void setStrPortCode(String strPortCode) {
        this.strPortCode = strPortCode;
    }

    public void setStrInvestTypeCode(String strInvestTypeCode) {
        this.strInvestTypeCode = strInvestTypeCode;
    }

    public void setStrInvMgrName(String strInvMgrName) {
        this.strInvMgrName = strInvMgrName;
    }

    public void setStrInvMgrCode(String strInvMgrCode) {
        this.strInvMgrCode = strInvMgrCode;
    }

    public void setStrBargainDate(String strBargainDate) {
        this.strBargainDate = strBargainDate;
    }

    public void setFilterType(DevolveTrusteeBean filterType) {
        this.filterType = filterType;
    }

    public void setDbPortCuryRate(double dbPortCuryRate) {
        this.dbPortCuryRate = dbPortCuryRate;
    }

    public void setDbOutValInc(double dbOutValInc) {
        this.dbOutValInc = dbOutValInc;
    }

    public void setDbOutMoney(double dbOutMoney) {
        this.dbOutMoney = dbOutMoney;
    }

    public void setDbOutInverest(double dbOutInverest) {
        this.dbOutInverest = dbOutInverest;
    }

    public void setDbOutDiscount(double dbOutDiscount) {
        this.dbOutDiscount = dbOutDiscount;
    }

    public void setDbOutAmount(double dbOutAmount) {
        this.dbOutAmount = dbOutAmount;
    }

    public void setStrStartDate(String strStartDate) {
        this.strStartDate = strStartDate;
    }

    public void setStrEndDate(String strEndDate) {
        this.strEndDate = strEndDate;
    }

    public void setBShow(String bShow) {
        this.bShow = bShow;
    }

    public void setStrAttrClsName(String strAttrClsName) {
        this.strAttrClsName = strAttrClsName;
    }

    public void setStrAttrClsCode(String strAttrClsCode) {
        this.strAttrClsCode = strAttrClsCode;
    }

    public double getDbBaseCuryRate() {
        return dbBaseCuryRate;
    }

    public String getStrStartDate() {
        return strStartDate;
    }

    public String getStrEndDate() {
        return strEndDate;
    }

    public String isBShow() {
        return bShow;
    }

    public String getStrAttrClsName() {
        return strAttrClsName;
    }

    public String getStrAttrClsCode() {
        return strAttrClsCode;
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
            this.strTradeNo = reqAry[0];
            this.strSecurityCode = reqAry[1];
            this.strBargainDate = reqAry[2];
            this.strInvestTypeCode = reqAry[3];
            this.strPortCode = reqAry[4];
            this.strInvMgrCode = reqAry[5];
            this.strSrcExchangeCode = reqAry[6];
            this.strTgtExchangeCode = reqAry[7];
            if (reqAry[8].length() != 0) {
                this.dbPortCuryRate = Double.parseDouble(
                    reqAry[8]);
            }
            if (reqAry[9].length() != 0) {
                this.dbBaseCuryRate = Double.parseDouble(
                    reqAry[9]);
            }
            if (reqAry[10].length() != 0) {
                this.dbOutAmount = Double.parseDouble(
                    reqAry[10]);
            }
            if (reqAry[11].length() != 0) {
                this.dbOutMoney = Double.parseDouble(
                    reqAry[11]);
            }
            if (reqAry[12].length() != 0) {
                this.dbOutValInc = Double.parseDouble(
                    reqAry[12]);
            }
            if (reqAry[13].length() != 0) {
                this.dbOutDiscount = Double.parseDouble(
                    reqAry[13]);
            }
            if (reqAry[14].length() != 0) {
                this.dbOutInverest = Double.parseDouble(
                    reqAry[14]);
            }
            this.strStartDate = reqAry[15];
            this.strEndDate = reqAry[16];
            this.checkStateId = Integer.parseInt(reqAry[17]);
            this.bShow = reqAry[18];
            this.strAttrClsCode = reqAry[19];
            this.strBondTradeType =  reqAry[20];//xuqiji 20100412
            this.inSecurityCode=reqAry[21];
            this.inInvestTypeCode=reqAry[22];//转入投资类型 add by zhouwei 20120424
            this.inAttrClsCode=reqAry[23];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DevolveTrusteeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析债券转托管数据出错！", e);
        }
    }
    /**
     * 通过拼接字符串来获取数据字符串
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strTradeNo).append("\t");
        buf.append(this.strSecurityCode).append("\t");
        buf.append(this.strSecurityName).append("\t");
        buf.append(this.strBargainDate).append("\t");
        buf.append(this.strInvestTypeCode).append("\t");
        buf.append(this.strPortCode).append("\t");
        buf.append(this.strPortName).append("\t");
        buf.append(this.strInvMgrCode).append("\t");
        buf.append(this.strInvMgrName).append("\t");
        buf.append(this.strSrcExchangeCode).append("\t");
        buf.append(this.strSrcExchangeName).append("\t");
        buf.append(this.strTgtExchangeCode).append("\t");
        buf.append(this.strTgtExchangeName).append("\t");
        buf.append(this.dbPortCuryRate).append("\t");
        buf.append(this.dbBaseCuryRate).append("\t");
        buf.append(this.dbOutAmount).append("\t");
        buf.append(this.dbOutMoney).append("\t");
        buf.append(this.dbOutValInc).append("\t");
        buf.append(this.dbOutDiscount).append("\t");
        buf.append(this.dbOutInverest).append("\t");
        buf.append(this.strAttrClsCode).append("\t");
        buf.append(this.strAttrClsName).append("\t");
        buf.append(this.strBondTradeType).append("\t");//xuqiji 20100412
        buf.append(this.inSecurityCode).append("\t");
        buf.append(this.inSecurityName).append("\t");
        buf.append(this.inInvestTypeCode).append("\t");
        buf.append(this.inAttrClsCode).append("\t");
        buf.append(this.inAttrClsName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }
    /**
     * 为各项变量赋值
     * @param rs ResultSet
     * @throws SQLException
     */
    public void setDevolveTrusteeAttr(ResultSet rs) throws SQLException,
        YssException {
        this.strTradeNo = rs.getString("FNum") + "";
        this.strSecurityCode = rs.getString("FSecurityCode") + "";
        this.strSecurityName = rs.getString("FSecurityName") + "";
        this.strBargainDate = rs.getDate("FBARGAINDATE") + "";
        this.strInvestTypeCode = rs.getString("FInvestType") + "";
        this.strPortCode = rs.getString("FPortCode") + "";
        this.strPortName = rs.getString("FPortName") + "";
        this.strInvMgrCode = rs.getString("FInvMgrCode") + "";
        this.strInvMgrName = rs.getString("FInvMgrName") + "";
        this.strSrcExchangeCode = rs.getString("FOutExchangeCode") + "";
        this.strSrcExchangeName = rs.getString("FOutExchangeName") + "";
        this.strTgtExchangeCode = rs.getString("FInExchangeCode") + "";
        this.strTgtExchangeName = rs.getString("FInExchangeName") + "";
        this.dbPortCuryRate = rs.getDouble("FPortCuryRate");
        this.dbBaseCuryRate = rs.getDouble("FBaseCuryRate");
        this.dbOutAmount = rs.getDouble("FAmount");
        this.dbOutMoney = rs.getDouble("FMoney");
        this.dbOutValInc = rs.getDouble("FApprec");
        this.dbOutDiscount = rs.getDouble("FDiscount");
        this.dbOutInverest = rs.getDouble("FBondIns");
        //-----------------------------xuqiji 20100412------------------------//
        this.strBondTradeType = rs.getString("FBondTradeType");
        this.strAttrClsCode = rs.getString("FATTRCLSCODE");
        this.strAttrClsName = rs.getString("FATTRCLSNAME");
        this.inSecurityCode=rs.getString("FinSecurityCode");
        this.inSecurityName=rs.getString("FinSecurityName");
        this.inInvestTypeCode=rs.getString("FInINVESTTYPE");
        this.inAttrClsCode=rs.getString("FInATTRCLSCODE");
        this.inAttrClsName=rs.getString("FInAttrClsName");
        super.setRecLog(rs);
    }
    public String getInSecurityCode() {
		return inSecurityCode;
	}

	public void setInSecurityCode(String inSecurityCode) {
		this.inSecurityCode = inSecurityCode;
	}

	public String getInSecurityName() {
		return inSecurityName;
	}

	public void setInSecurityName(String inSecurityName) {
		this.inSecurityName = inSecurityName;
	}

	//--------------------xuqiji 20100412-------------------------//
	public String getStrBondTradeType() {
		return strBondTradeType;
	}

	public void setStrBondTradeType(String strBondTradeType) {
		this.strBondTradeType = strBondTradeType;
	}
	//------------------------------end--------------------------//
}
