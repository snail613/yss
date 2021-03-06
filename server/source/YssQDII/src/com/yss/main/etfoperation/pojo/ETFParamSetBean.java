package com.yss.main.etfoperation.pojo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;

/**
 * <p>
 * Title:20091116 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
 * </p>
 *
 * <p>
 * Description: 参数设置实体bean
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ETFParamSetBean
    extends BaseDataSettingBean {
    private String PortCode; // 组合代码
    private String PortName; // 投资名称
    private String MktSrcCode; // 行情来源代码
    private String MktSrcName; // 行情来源名称
    private String OneGradeMktCode; // 一级市场代码
    private String TwoGradeMktCode; // 二级市场代码
    private String CapitalCode; // 资金代码
    /**shashijie 2011-10-14 BUG 2917 在“基准比例”的文本框内输入10位大于1的数字报错 */
    private double NormScale; // 基准比例
    /**end*/
    private String ETFSeat; // ETF席位
    private String SupplyMode; // 补票方式

    private String CashAccCode; // 应付替代款结转账户
    private String CashAccName; // 应付替代款结转账户名称
    private String ClearAccCode;//清算备付金账户
    private String ClearAccName;//清算备付金账户名称

    private String sSubscribeData;//ETF台账报表申购数据列参数设置
    private String sRedeemData;//ETF台账报表赎回数据列参数设置
    private String sRightData;//ETF台账报表权益数据列参数设置
    private String sSupplyAndForceData;//ETF台账报表补票和强制处理数据列参数设置
    private String sQuitMoneyValue;//ETF台账报表应退款估值增值数据列参数设置
    private String sOtherData;//ETF台账报表其它数据列参数设置
     
    private String sBookTotalType;//台帐汇总方式
    private int sUnitdigit;//单位成本保留位数
    
    private int BeginSupply;//开始补票 结转天数
    private int DealDayNum;//几个交易日内补票完成 结转天数
    private int LastestDealDayNum;//最长几个交易日内补票完成 结转天数
    private int sSGBalanceOver;//申购现金差额 结转天数
    private int sSGReplaceOver;//申购现金替代款 结转天数
    private int sSHBalanceOver;//赎回现金差额的 结转天数
    private int sSHReplaceOver;//赎回现金替代款 结转天数
    private int iSHDealReplace;//赎回应付替代款 结转天数
    private int iSGDealReplace;//申购应付替代 结转天数
    
    //---add by songjie 2012.12.05 STORY #3328需求深圳-[易方达基金]QDV4.0[高]20121123001 start---//
    private int BeginSupply2 = -1;//开始补票 结转天数2
    private int DealDayNum2 = -1;//几个交易日内补票完成 结转天数2
    private int LastestDealDayNum2 = -1;//最长几个交易日内补票完成 结转天数2
    private int sSGBalanceOver2 = -1;//申购现金差额 结转天数2
    private int sSGReplaceOver2 = -1;//申购现金替代款 结转天数2
    private int iSGDealReplace2 = -1;//申购应付替代 结转天数2
    private int sSHBalanceOver2 = -1;//赎回现金差额的 结转天数2
    private int sSHReplaceOver2 = -1;//赎回现金替代款 结转天数2
    private int iSHDealReplace2 = -1;//赎回应付替代款 结转天数2
    
    private String BeginSupplyHD = "";//开始补票 结转 节假日群
    private String DealDayNumHD = "";//几个交易日内补票完成 结转 节假日群
    private String LastestDealDayNumHD = "";//最长几个交易日内补票完成 结转 节假日群
    private String sSGBalanceOverHD = "";//申购现金差额 结转 节假日群
    private String sSGReplaceOverHD = "";//申购现金替代款 结转 节假日群
    private String sSHBalanceOverHD = "";//赎回现金差额的 结转 节假日群
    private String sSHReplaceOverHD = "";//赎回现金替代款 结转 节假日群
    private String iSHDealReplaceHD = "";//赎回应付替代款 结转 节假日群
    private String iSGDealReplaceHD = "";//申购应付替代 结转 节假日群
    
    private String BeginSupplyHD2 = "";//开始补票 结转2 节假日群
    private String DealDayNumHD2 = "";//几个交易日内补票完成 结转2 节假日群
    private String LastestDealDayNumHD2 = "";//最长几个交易日内补票完成 结转2 节假日群
    private String sSGBalanceOverHD2 = "";//申购现金差额 结转2 节假日群
    private String sSGReplaceOverHD2 = "";//申购现金替代款 结转2 节假日群
    private String sSHBalanceOverHD2 = "";//赎回现金差额的 结转2 节假日群
    private String sSHReplaceOverHD2 = "";//赎回现金替代款 结转2 节假日群
    private String iSHDealReplaceHD2 = "";//赎回应付替代款 结转2 节假日群
    private String iSGDealReplaceHD2 = "";//申购应付替代 结转2 节假日群
    
    private String FbaseRateSrcSSCode = "";//基础汇率来源代码 申赎
    private String baseRateSrcSSName = "";//基础汇率来源名称 申赎
    private String FbaseRateSSCode = "";//基础汇率行情 申赎
    private String FportRateSrcSSCode = "";//组合汇率来源代码 申赎
    private String portRateSrcSSName = "";//组合汇率来源名称 申赎
    private String FportRateSSCode = "";//组合汇率行情 申赎
    
    private String FbaseRateSrcBPCode = "";//基础汇率来源代码 补票
    private String baseRateSrcBPName = "";//基础汇率来源名称 补票
    private String FbaseRateBPCode = "";//基础汇率行情 补票
    private String FportRateSrcBPCode = "";//组合汇率来源代码 补票
    private String portRateSrcBPName = "";//组合汇率来源名称 补票
    private String FportRateBPCode = "";//组合汇率行情 补票
    //---add by songjie 2012.12.05 STORY #3328需求深圳-[易方达基金]QDV4.0[高]20121123001 end---//
    //add by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B
    private String clearNum = "";//清算编号
    
	private String sCrossHolidayCode;//跨境节假日代码
    private String sCrossHolidayName;//跨境节假日名称
    private String sDomesticOverType = "";//国内结转类型，多个用逗号隔开
    private String sCrossOverType = "";	//跨境结转类型，多个用逗号隔开
    
    /**shashijie 2012-12-10 STORY 3328 隐藏节假日群设置分页*/
    private String sHolidayCode = " ";//国内节假日代码
    private String sHolidayName;//国内节假日名称
	/**end shashijie 2012-12-10 STORY */

    private HashMap hoildaysRela = new HashMap();//节假日关联关系，以结转类型为Key，节假日代码为 Value
    
	private String OldPortCode = "";

    private ETFParamSetBean filterType;

    public ETFParamSetBean() {
    }

    //--- add by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B start---//
    public String getClearNum(){
    	return this.clearNum;
    }
    
    public void setClearNum(String clearNum){
    	this.clearNum = clearNum;
    }
    //--- add by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B end---//
    
    //---add by songjie 2012.12.05 STORY #3328需求深圳-[易方达基金]QDV4.0[高]20121123001 start---//
    public String getBaseRateSrcSSName() {
		return baseRateSrcSSName;
	}

	public void setBaseRateSrcSSName(String baseRateSrcSSName) {
		this.baseRateSrcSSName = baseRateSrcSSName;
	}

	public String getPortRateSrcSSName() {
		return portRateSrcSSName;
	}

	public void setPortRateSrcSSName(String portRateSrcSSName) {
		this.portRateSrcSSName = portRateSrcSSName;
	}

	public String getBaseRateSrcBPName() {
		return baseRateSrcBPName;
	}

	public void setBaseRateSrcBPName(String baseRateSrcBPName) {
		this.baseRateSrcBPName = baseRateSrcBPName;
	}

	public String getPortRateSrcBPName() {
		return portRateSrcBPName;
	}

	public void setPortRateSrcBPName(String portRateSrcBPName) {
		this.portRateSrcBPName = portRateSrcBPName;
	}

	public String getBaseRateSrcSSCode() {
		return FbaseRateSrcSSCode;
	}

	public void setBaseRateSrcSSCode(String FbaseRateSrcSSCode) {
		this.FbaseRateSrcSSCode = FbaseRateSrcSSCode;
	}

	public String getBaseRateSSCode() {
		return FbaseRateSSCode;
	}

	public void setBaseRateSSCode(String FbaseRateSSCode) {
		this.FbaseRateSSCode = FbaseRateSSCode;
	}

	public String getPortRateSrcSSCode() {
		return FportRateSrcSSCode;
	}

	public void setPortRateSrcSSCode(String FportRateSrcSSCode) {
		this.FportRateSrcSSCode = FportRateSrcSSCode;
	}

	public String getPortRateSSCode() {
		return FportRateSSCode;
	}

	public void setPortRateSSCode(String FportRateSSCode) {
		this.FportRateSSCode = FportRateSSCode;
	}

	public String getBaseRateSrcBPCode() {
		return FbaseRateSrcBPCode;
	}

	public void setBaseRateSrcBPCode(String FbaseRateSrcBPCode) {
		this.FbaseRateSrcBPCode = FbaseRateSrcBPCode;
	}

	public String getBaseRateBPCode() {
		return FbaseRateBPCode;
	}

	public void setBaseRateBPCode(String FbaseRateBPCode) {
		this.FbaseRateBPCode = FbaseRateBPCode;
	}

	public String getPortRateSrcBPCode() {
		return FportRateSrcBPCode;
	}

	public void setPortRateSrcBPCode(String FportRateSrcBPCode) {
		this.FportRateSrcBPCode = FportRateSrcBPCode;
	}

	public String getPortRateBPCode() {
		return FportRateBPCode;
	}

	public void setPortRateBPCode(String FportRateBPCode) {
		this.FportRateBPCode = FportRateBPCode;
	}
    
	public int getBeginSupply2() {
		return BeginSupply2;
	}

	public void setBeginSupply2(int beginSupply2) {
		BeginSupply2 = beginSupply2;
	}

	public int getDealDayNum2() {
		return DealDayNum2;
	}

	public void setDealDayNum2(int dealDayNum2) {
		DealDayNum2 = dealDayNum2;
	}

	public int getLastestDealDayNum2() {
		return LastestDealDayNum2;
	}

	public void setLastestDealDayNum2(int lastestDealDayNum2) {
		LastestDealDayNum2 = lastestDealDayNum2;
	}

	public int getsSGBalanceOver2() {
		return sSGBalanceOver2;
	}

	public void setsSGBalanceOver2(int sSGBalanceOver2) {
		this.sSGBalanceOver2 = sSGBalanceOver2;
	}

	public int getsSGReplaceOver2() {
		return sSGReplaceOver2;
	}

	public void setsSGReplaceOver2(int sSGReplaceOver2) {
		this.sSGReplaceOver2 = sSGReplaceOver2;
	}

	public int getsSHBalanceOver2() {
		return sSHBalanceOver2;
	}

	public void setsSHBalanceOver2(int sSHBalanceOver2) {
		this.sSHBalanceOver2 = sSHBalanceOver2;
	}

	public int getsSHReplaceOver2() {
		return sSHReplaceOver2;
	}

	public void setsSHReplaceOver2(int sSHReplaceOver2) {
		this.sSHReplaceOver2 = sSHReplaceOver2;
	}

	public int getiSHDealReplace2() {
		return iSHDealReplace2;
	}

	public void setiSHDealReplace2(int iSHDealReplace2) {
		this.iSHDealReplace2 = iSHDealReplace2;
	}

	public int getiSGDealReplace2() {
		return iSGDealReplace2;
	}

	public void setiSGDealReplace2(int iSGDealReplace2) {
		this.iSGDealReplace2 = iSGDealReplace2;
	}

	public String getBeginSupplyHD() {
		return BeginSupplyHD;
	}

	public void setBeginSupplyHD(String beginSupplyHD) {
		BeginSupplyHD = beginSupplyHD;
	}

	public String getDealDayNumHD() {
		return DealDayNumHD;
	}

	public void setDealDayNumHD(String dealDayNumHD) {
		DealDayNumHD = dealDayNumHD;
	}

	public String getLastestDealDayNumHD() {
		return LastestDealDayNumHD;
	}

	public void setLastestDealDayNumHD(String lastestDealDayNumHD) {
		LastestDealDayNumHD = lastestDealDayNumHD;
	}

	public String getsSGBalanceOverHD() {
		return sSGBalanceOverHD;
	}

	public void setsSGBalanceOverHD(String sSGBalanceOverHD) {
		this.sSGBalanceOverHD = sSGBalanceOverHD;
	}

	public String getsSGReplaceOverHD() {
		return sSGReplaceOverHD;
	}

	public void setsSGReplaceOverHD(String sSGReplaceOverHD) {
		this.sSGReplaceOverHD = sSGReplaceOverHD;
	}

	public String getsSHBalanceOverHD() {
		return sSHBalanceOverHD;
	}

	public void setsSHBalanceOverHD(String sSHBalanceOverHD) {
		this.sSHBalanceOverHD = sSHBalanceOverHD;
	}

	public String getsSHReplaceOverHD() {
		return sSHReplaceOverHD;
	}

	public void setsSHReplaceOverHD(String sSHReplaceOverHD) {
		this.sSHReplaceOverHD = sSHReplaceOverHD;
	}

	public String getiSHDealReplaceHD() {
		return iSHDealReplaceHD;
	}

	public void setiSHDealReplaceHD(String iSHDealReplaceHD) {
		this.iSHDealReplaceHD = iSHDealReplaceHD;
	}

	public String getiSGDealReplaceHD() {
		return iSGDealReplaceHD;
	}

	public void setiSGDealReplaceHD(String iSGDealReplaceHD) {
		this.iSGDealReplaceHD = iSGDealReplaceHD;
	}

	public String getBeginSupplyHD2() {
		return BeginSupplyHD2;
	}

	public void setBeginSupplyHD2(String beginSupplyHD2) {
		BeginSupplyHD2 = beginSupplyHD2;
	}

	public String getDealDayNumHD2() {
		return DealDayNumHD2;
	}

	public void setDealDayNumHD2(String dealDayNumHD2) {
		DealDayNumHD2 = dealDayNumHD2;
	}

	public String getLastestDealDayNumHD2() {
		return LastestDealDayNumHD2;
	}

	public void setLastestDealDayNumHD2(String lastestDealDayNumHD2) {
		LastestDealDayNumHD2 = lastestDealDayNumHD2;
	}

	public String getsSGBalanceOverHD2() {
		return sSGBalanceOverHD2;
	}

	public void setsSGBalanceOverHD2(String sSGBalanceOverHD2) {
		this.sSGBalanceOverHD2 = sSGBalanceOverHD2;
	}

	public String getsSGReplaceOverHD2() {
		return sSGReplaceOverHD2;
	}

	public void setsSGReplaceOverHD2(String sSGReplaceOverHD2) {
		this.sSGReplaceOverHD2 = sSGReplaceOverHD2;
	}

	public String getsSHBalanceOverHD2() {
		return sSHBalanceOverHD2;
	}

	public void setsSHBalanceOverHD2(String sSHBalanceOverHD2) {
		this.sSHBalanceOverHD2 = sSHBalanceOverHD2;
	}

	public String getsSHReplaceOverHD2() {
		return sSHReplaceOverHD2;
	}

	public void setsSHReplaceOverHD2(String sSHReplaceOverHD2) {
		this.sSHReplaceOverHD2 = sSHReplaceOverHD2;
	}

	public String getiSHDealReplaceHD2() {
		return iSHDealReplaceHD2;
	}

	public void setiSHDealReplaceHD2(String iSHDealReplaceHD2) {
		this.iSHDealReplaceHD2 = iSHDealReplaceHD2;
	}

	public String getiSGDealReplaceHD2() {
		return iSGDealReplaceHD2;
	}

	public void setiSGDealReplaceHD2(String iSGDealReplaceHD2) {
		this.iSGDealReplaceHD2 = iSGDealReplaceHD2;
	}
    //---add by songjie 2012.12.05 STORY #3328需求深圳-[易方达基金]QDV4.0[高]20121123001 end---//
    
    public HashMap getHoildaysRela() {
		return hoildaysRela;
	}

	public void setHoildaysRela(HashMap hoildaysRela) {
		this.hoildaysRela = hoildaysRela;
	}
    
    public String getCapitalCode() {
        return CapitalCode;
    }

    public void setCapitalCode(String CapitalCode) {
        this.CapitalCode = CapitalCode;
    }

    public int getDealDayNum() {
        return DealDayNum;
    }

    public void setDealDayNum(int DealDayNum) {
        this.DealDayNum = DealDayNum;
    }

    public String getETFSeat() {
        return ETFSeat;
    }

    public void setFilterType(ETFParamSetBean filterType) {
        this.filterType = filterType;
    }

    public void setOldPortCode(String OldPortCode) {
        this.OldPortCode = OldPortCode;
    }

    public void setCashAccName(String CashAccName) {
        this.CashAccName = CashAccName;
    }

    public void setCashAccCode(String CashAccCode) {
        this.CashAccCode = CashAccCode;
    }

    public void setBeginSupply(int BeginSupply) {
        this.BeginSupply = BeginSupply;
    }

    public void setLastestDealDayNum(int LastestDealDayNum) {
        this.LastestDealDayNum = LastestDealDayNum;
    }

    public void setSupplyMode(String SupplyMode) {
        this.SupplyMode = SupplyMode;
    }

    public void setTwoGradeMktCode(String TwoGradeMktCode) {
        this.TwoGradeMktCode = TwoGradeMktCode;
    }

    public void setPortName(String PortName) {
        this.PortName = PortName;
    }
    
    public String getsCrossHolidayCode() {
		return sCrossHolidayCode;
	}

	public void setsCrossHolidayCode(String sCrossHolidayCode) {
		this.sCrossHolidayCode = sCrossHolidayCode;
	}

	public String getsCrossHolidayName() {
		return sCrossHolidayName;
	}

	public void setsCrossHolidayName(String sCrossHolidayName) {
		this.sCrossHolidayName = sCrossHolidayName;
	}

	public String getsDomesticOverType() {
		return sDomesticOverType;
	}

	public void setsDomesticOverType(String sDomesticOverType) {
		this.sDomesticOverType = sDomesticOverType;
	}

	public String getsCrossOverType() {
		return sCrossOverType;
	}

	public void setsCrossOverType(String sCrossOverType) {
		this.sCrossOverType = sCrossOverType;
	}
	
    public void setPortCode(String PortCode) {
        this.PortCode = PortCode;
    }

    public void setMktSrcCode(String MktSrcCode) {
        this.MktSrcCode = MktSrcCode;
    }

    public void setMktSrcName(String MktSrcName) {
        this.MktSrcName = MktSrcName;
    }

    public void setOneGradeMktCode(String OneGradeMktCode) {
        this.OneGradeMktCode = OneGradeMktCode;
    }
    /**shashijie 2011-10-14 BUG 2917 在“基准比例”的文本框内输入10位大于1的数字报错 */
    public void setNormScale(double NormScale) {
        this.NormScale = NormScale;
    }
    /**end*/
    public void setETFSeat(String ETFSeat) {
        this.ETFSeat = ETFSeat;
    }

    public ETFParamSetBean getFilterType() {
        return filterType;
    }

    public String getOldPortCode() {
        return OldPortCode;
    }

    public String getCashAccName() {
        return CashAccName;
    }

    public String getCashAccCode() {
        return CashAccCode;
    }

    public int getBeginSupply() {
        return BeginSupply;
    }

    public int getLastestDealDayNum() {
        return LastestDealDayNum;
    }

    public String getSupplyMode() {
        return SupplyMode;
    }

    public String getTwoGradeMktCode() {
        return TwoGradeMktCode;
    }

    public String getPortName() {
        return PortName;
    }

    public String getPortCode() {
        return PortCode;
    }

    public String getMktSrcCode() {
        return MktSrcCode;
    }

    public String getMktSrcName() {
        return MktSrcName;
    }

    public String getOneGradeMktCode() {
        return OneGradeMktCode;
    }
    /**shashijie 2011-10-14 BUG 2917 在“基准比例”的文本框内输入10位大于1的数字报错 */
    public double getNormScale() {
        return NormScale;
    }
    /**end*/
    
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
            this.checkStateId = Integer.parseInt(reqAry[0]);
            this.PortCode = reqAry[1];
            this.MktSrcCode = reqAry[2];
            this.OneGradeMktCode = reqAry[3];
            if (reqAry[4].length() != 0) {
            	/**shashijie 2011-10-14 BUG 2917 int长度不够,只能改为Double */
                this.NormScale = Double.valueOf(reqAry[4]).doubleValue();
                /**end*/
            }

            this.TwoGradeMktCode = reqAry[5];
            this.ETFSeat = reqAry[6];
            this.SupplyMode = reqAry[7];
            this.CapitalCode = reqAry[8];

            this.CashAccCode = reqAry[9];
            if (reqAry[10].length() != 0) {
                this.BeginSupply = Integer.parseInt(reqAry[10]);
            }
            if (reqAry[11].length() != 0) {
                this.DealDayNum = Integer.parseInt(reqAry[11]);
            }
            if (reqAry[12].length() != 0) {
                this.LastestDealDayNum = Integer.parseInt(reqAry[12]);
            }

            this.OldPortCode = reqAry[13];
            this.sSubscribeData = reqAry[14];
            this.sRightData = reqAry[15];
            this.sSupplyAndForceData = reqAry[16];
            this.sOtherData = reqAry[17];
            this.sRedeemData = reqAry[18];
            
            this.sBookTotalType = reqAry[19];
            if(reqAry[20].length()!=0){
            	this.sUnitdigit = Integer.parseInt(reqAry[20]);
            }
            if (reqAry[21].length() != 0) {
            	this.sSGBalanceOver = Integer.parseInt(reqAry[21]);
            }
            if(reqAry[22].length()!=0){
            	this.sSGReplaceOver = Integer.parseInt(reqAry[22]);
            }
            if(reqAry[23].length()!=0){
            	this.sSHBalanceOver = Integer.parseInt(reqAry[23]);
            }
            if(reqAry[24].length()!=0){
            	this.sSHReplaceOver = Integer.parseInt(reqAry[24]);
            }
            /**shashijie 2012-12-10 STORY 3328 隐藏节假日群设置分页*/
            if (reqAry[25]==null || reqAry[25].trim().equals("")
				|| reqAry[25].trim().equalsIgnoreCase("null")){
            	this.sHolidayCode = " ";
			} else {
				this.sHolidayCode = reqAry[25];
			}
            
			/**end shashijie 2012-12-10 STORY */
            
            this.sHolidayName = reqAry[26];
            if(reqAry[27].length()!=0){
            	this.iSHDealReplace = Integer.parseInt(reqAry[27]);
            }
            if(reqAry[28].length()!=0){
            	this.iSGDealReplace = Integer.parseInt(reqAry[28]);
            }
            this.sQuitMoneyValue = reqAry[29];
            
            this.sDomesticOverType=reqAry[30];
            this.sCrossHolidayCode=reqAry[31];
            this.sCrossOverType=reqAry[32];
            this.ClearAccCode = reqAry[33];
            //---add by songjie 2012.12.05 STORY #3328需求深圳-[易方达基金]QDV4.0[高]20121123001 start---//
            if(reqAry[34].trim().length() > 0){
            	this.sSGBalanceOver2 = Integer.parseInt(reqAry[34]);//SG现金差额结转2
            }
            if(reqAry[35].trim().length() > 0){
            	this.sSGReplaceOver2 = Integer.parseInt(reqAry[35]);//SG现金替代款结转2
            }
            if(reqAry[36].trim().length() > 0){
            	this.sSHBalanceOver2 = Integer.parseInt(reqAry[36]);//SH现金差额的结转2
            }
            if(reqAry[37].trim().length() > 0){
            	this.sSHReplaceOver2 = Integer.parseInt(reqAry[37]);//SH现金替代款结转2
            }
            if(reqAry[38].trim().length() > 0){
            	this.iSHDealReplace2 = Integer.parseInt(reqAry[38]);//赎回替代款结转2
            }
            if(reqAry[39].trim().length() > 0){
            	this.iSGDealReplace2 = Integer.parseInt(reqAry[39]);//申购应付替代结转2
            }
            if(reqAry[40].trim().length() > 0){
            	this.BeginSupply2 = Integer.parseInt(reqAry[40]);//开始补票 结转2
            }
            if(reqAry[41].trim().length() > 0){
            	this.DealDayNum2 = Integer.parseInt(reqAry[41]);//几个交易日内补票完成 结转2
            }
            if(reqAry[42].trim().length() > 0){
            	this.LastestDealDayNum2 = Integer.parseInt(reqAry[42]);//最长几个交易日内补票完成 结转2
            }
            this.sSGBalanceOverHD = reqAry[43];//SG现金差额 结转 节假日群代码
            this.sSGReplaceOverHD = reqAry[44];//SG现金替代款 结转 节假日群代码
            this.sSHBalanceOverHD = reqAry[45];//SH现金差额 结转 节假日群代码
            this.sSHReplaceOverHD = reqAry[46];//SH现金替代 款 结转 节假日群代码
            this.iSHDealReplaceHD = reqAry[47];//赎回替代款结转 节假日群代码
            this.iSGDealReplaceHD = reqAry[48];//申购应付替代 结转 节假日群代码
            this.BeginSupplyHD = reqAry[49];//开始补票 结转 节假日群代码
            this.DealDayNumHD = reqAry[50];//几个交易日内补票完成 结转 节假日群代码
            this.LastestDealDayNumHD = reqAry[51];//最长几个交易日内补票完成 结转 节假日群代码
            this.sSGBalanceOverHD2 = reqAry[52];//SG现金差额 结转 节假日群代码2
            this.sSGReplaceOverHD2 = reqAry[53];//SG现金替代款 结转 节假日群代码2
            this.sSHBalanceOverHD2 = reqAry[54];//SH现金差额 结转 节假日群代码2
            this.sSHReplaceOverHD2 = reqAry[55];//SH现金替代 款 结转 节假日群代码2
            this.iSHDealReplaceHD2 = reqAry[56];//赎回替代款结转 节假日群代码2
            this.iSGDealReplaceHD2 = reqAry[57];//申购应付替代 结转 节假日群代码2
            this.BeginSupplyHD2 = reqAry[58];//开始补票 结转 节假日群代码2
            this.DealDayNumHD2 = reqAry[59];//几个交易日内补票完成 结转 节假日群代码2
            this.LastestDealDayNumHD2 = reqAry[60];//最长几个交易日内补票完成 结转 节假日群代码2
            this.FbaseRateSrcSSCode = reqAry[61];//基础汇率代码 申赎
            this.FbaseRateSSCode = reqAry[62];//基础汇率行情 申赎
            this.FportRateSrcSSCode = reqAry[63];//组合汇率代码 申赎
            this.FportRateSSCode = reqAry[64];//组合汇率行情 申赎
            this.FbaseRateSrcBPCode = reqAry[65];//基础汇率代码 补票
            this.FbaseRateBPCode = reqAry[66];//基础汇率行情 补票
            this.FportRateSrcBPCode = reqAry[67];//组合汇率代码 补票
            this.FportRateBPCode = reqAry[68];//组合汇率行情 补票
            //---add by songjie 2012.12.05 STORY #3328需求深圳-[易方达基金]QDV4.0[高]20121123001 end---//
            //add by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B
            this.clearNum = reqAry[69];
            
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new ETFParamSetBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析ETF参数数据出错！", e);
        }
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
        buf.append(this.MktSrcCode).append("\t");
        buf.append(this.MktSrcName).append("\t");
        buf.append(this.OneGradeMktCode).append("\t");
        
        buf.append(this.NormScale).append("\t");
        buf.append(this.TwoGradeMktCode).append("\t");
        buf.append(this.ETFSeat).append("\t");
        buf.append(this.SupplyMode).append("\t");
        buf.append(this.CapitalCode).append("\t");
        buf.append(this.CashAccCode).append("\t");
        buf.append(this.CashAccName).append("\t");
        buf.append(this.BeginSupply).append("\t");
        buf.append(this.DealDayNum).append("\t");
        buf.append(this.LastestDealDayNum).append("\t");
        buf.append(this.sSubscribeData).append("\t");
        buf.append(this.sRightData).append("\t");
        buf.append(this.sSupplyAndForceData).append("\t");
        buf.append(this.sOtherData).append("\t");
        buf.append(this.sRedeemData).append("\t");
        buf.append(this.sBookTotalType).append("\t");
        buf.append(this.sUnitdigit).append("\t");
        buf.append(this.sSGBalanceOver).append("\t");
        buf.append(this.sSGReplaceOver).append("\t");
        buf.append(this.sSHBalanceOver).append("\t");
        buf.append(this.sSHReplaceOver).append("\t");
        buf.append(this.sHolidayCode).append("\t");
        buf.append(this.sHolidayName).append("\t");
        buf.append(this.iSHDealReplace).append("\t");
        buf.append(this.iSGDealReplace).append("\t");
        buf.append(this.sQuitMoneyValue).append("\t");
        
        buf.append(this.sCrossHolidayCode).append("\t");
        buf.append(this.sCrossHolidayName).append("\t");
        buf.append(this.sDomesticOverType).append("\t");
        buf.append(this.sCrossOverType).append("\t");
        buf.append(this.ClearAccCode).append("\t");
        buf.append(this.ClearAccName).append("\t");

        //---add by songjie 2012.12.05 STORY #3328 需求深圳-[易方达基金]QDV4.0[高]20121123001 start---//
        buf.append(this.BeginSupply2).append("\t");//开始补票 结转天数2
        buf.append(this.DealDayNum2).append("\t");//几个交易日内补票完成 结转天数2
        buf.append(this.LastestDealDayNum2).append("\t");//最长几个交易日内补票完成 结转天数2
        buf.append(this.sSGBalanceOver2).append("\t");//申购现金差额 结转天数2
        buf.append(this.sSGReplaceOver2).append("\t");//申购现金替代款 结转天数2
        buf.append(this.iSGDealReplace2).append("\t");//申购应付替代 结转天数2
        buf.append(this.sSHBalanceOver2).append("\t");//赎回现金差额的 结转天数2
        buf.append(this.sSHReplaceOver2).append("\t");//赎回现金替代款 结转天数2
        buf.append(this.iSHDealReplace2).append("\t");//赎回应付替代款 结转天数2
        
        buf.append(this.BeginSupplyHD).append("\t");//开始补票 结转 节假日群
        buf.append(this.DealDayNumHD).append("\t");//几个交易日内补票完成 结转 节假日群
        buf.append(this.LastestDealDayNumHD).append("\t");//最长几个交易日内补票完成 结转 节假日群
        buf.append(this.sSGBalanceOverHD).append("\t");//申购现金差额 结转 节假日群
        buf.append(this.sSGReplaceOverHD).append("\t");//申购现金替代款 结转 节假日群
        buf.append(this.iSGDealReplaceHD).append("\t");//申购应付替代 结转 节假日群
        buf.append(this.sSHBalanceOverHD).append("\t");//赎回现金差额的 结转 节假日群
        buf.append(this.sSHReplaceOverHD).append("\t");//赎回现金替代款 结转 节假日群
        buf.append(this.iSHDealReplaceHD).append("\t");//赎回应付替代款 结转 节假日群
        
        buf.append(this.BeginSupplyHD2).append("\t");//开始补票 结转2 节假日群
        buf.append(this.DealDayNumHD2).append("\t");//几个交易日内补票完成 结转2 节假日群
        buf.append(this.LastestDealDayNumHD2).append("\t");//最长几个交易日内补票完成 结转2 节假日群
        buf.append(this.sSGBalanceOverHD2).append("\t");//申购现金差额 结转2 节假日群
        buf.append(this.sSGReplaceOverHD2).append("\t");//申购现金替代款 结转2 节假日群
        buf.append(this.iSGDealReplaceHD2).append("\t");//申购应付替代 结转2 节假日群
        buf.append(this.sSHBalanceOverHD2).append("\t");//赎回现金差额的 结转2 节假日群
        buf.append(this.sSHReplaceOverHD2).append("\t");//赎回现金替代款 结转2 节假日群
        buf.append(this.iSHDealReplaceHD2).append("\t");//赎回应付替代款 结转2 节假日群
        
        buf.append(this.FbaseRateSrcSSCode).append("\t");//基础汇率来源代码 申赎
        buf.append(this.baseRateSrcSSName).append("\t");//基础汇率来源名称 申赎
        buf.append(this.FbaseRateSSCode).append("\t");//基础汇率行情 申赎
        buf.append(this.FportRateSrcSSCode).append("\t");//组合汇率来源代码 申赎
        buf.append(this.portRateSrcSSName).append("\t");//组合汇率来源名称 申赎
        buf.append(this.FportRateSSCode).append("\t");//组合汇率行情 申赎
        
        buf.append(this.FbaseRateSrcBPCode).append("\t");//基础汇率来源代码 补票
        buf.append(this.baseRateSrcBPName).append("\t");//基础汇率来源名称 补票
        buf.append(this.FbaseRateBPCode).append("\t");//基础汇率行情 补票
        buf.append(this.FportRateSrcBPCode).append("\t");//组合汇率来源代码 补票
        buf.append(this.portRateSrcBPName).append("\t");//组合汇率来源名称 补票
        buf.append(this.FportRateBPCode).append("\t");//组合汇率行情 补票        
        //---add by songjie 2012.12.05 STORY #3328 需求深圳-[易方达基金]QDV4.0[高]20121123001 end---//
        //add by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B
        buf.append(this.clearNum).append("\t");//清算编号
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
    public void setETFParamAttr(ResultSet rs) throws SQLException, YssException {
        this.PortCode = rs.getString("FPortCode");
        this.PortName = rs.getString("FPortName");
        this.MktSrcCode = rs.getString("FMktSrcCode");
        this.MktSrcName = rs.getString("FMktSrcName");
        this.OneGradeMktCode = rs.getString("FONEGRADEMKTCODE");
        /**shashijie 2011-10-14 BUG 2917 在“基准比例”的文本框内输入10位大于1的数字报错 */
        this.NormScale = rs.getDouble("FNORMSCALE");
        /**end*/
        this.TwoGradeMktCode = rs.getString("FTWOGRADEMKTCODE");
        this.ETFSeat = rs.getString("FETFSEAT");
        this.SupplyMode = rs.getString("FSupplyMode");
        this.CapitalCode = rs.getString("FCAPITALCODE");
        this.CashAccCode = rs.getString("FCashAccCode");
        this.CashAccName = rs.getString("FCashAccName");
        this.ClearAccCode = rs.getString("FClearAccCode");
        this.ClearAccName = rs.getString("FClearAccName");
        this.BeginSupply = rs.getInt("FBeginSupply");
        this.DealDayNum = rs.getInt("FDEALDAYNUM");
        this.LastestDealDayNum = rs.getInt("FLastestDealDayNum");
        this.sSubscribeData = rs.getString("FSubscribe");
        this.sRightData = rs.getString("FRight");
        this.sSupplyAndForceData = rs.getString("FSupplyAndForce");
        this.sOtherData = rs.getString("FOther");
        this.sRedeemData = rs.getString("FRedeem");
        this.sBookTotalType = rs.getString("FBookTotalType");
        this.sUnitdigit =rs.getInt("FUnitdigit");
        this.sSGBalanceOver =rs.getInt("FSGBalanceOver");
        this.sSGReplaceOver =rs.getInt("FSGReplaceOver");
        this.sSHBalanceOver =rs.getInt("FSHBalanceOver");
        this.sSHReplaceOver =rs.getInt("FSHReplaceOver");
        this.sHolidayCode = rs.getString("FHolidaysCode");
        this.sHolidayName = rs.getString("domesticHolidaysName");
        this.sCrossHolidayName=rs.getString("corssHolidaysname");
        this.iSHDealReplace = rs.getInt("FSHDealReplace");
        this.iSGDealReplace = rs.getInt("FSGDealReplace");
        this.sQuitMoneyValue = rs.getString("FQuitMoneyValue");
        this.sCrossHolidayCode=rs.getString("FCROSSHOLIDAYSCODE");
        
        /**shashijie 2012-12-7 STORY 3328 新增字段赋值对应关系,共17个字段 */
        this.BeginSupply2 = rs.getInt("FBeginSupply2");//开始补票 结转天数2
        this.DealDayNum2 = rs.getInt("FDealDayNum2");//几个交易日内补票完成 结转天数2
        this.LastestDealDayNum2 = rs.getInt("FLastestDealDayNum2");//最长几个交易日内补票完成 结转天数2
        this.sSGBalanceOver2 = rs.getInt("FSGBalanceOver2");//申购现金差额 结转天数2
        this.sSGReplaceOver2 = rs.getInt("FSGReplaceOver2");//申购现金替代款 结转天数2
        this.iSGDealReplace2 = rs.getInt("FSGDealReplace2");//申购应付替代 结转天数2
        this.sSHBalanceOver2 = rs.getInt("FSHBalanceOver2");//赎回现金差额的 结转天数2
        this.sSHReplaceOver2 = rs.getInt("FSHReplaceOver2");//赎回现金替代款 结转天数2
        this.iSHDealReplace2 = rs.getInt("FSHDealReplace2");//赎回应付替代款 结转天数2
		//申赎汇率来源
        this.FbaseRateSrcSSCode = rs.getString("FbaseRateSrcSSCode");//基础汇率来源代码 申赎
        this.baseRateSrcSSName = rs.getString("baseRateSrcSSName");//基础汇率来源名称 申赎
        this.FbaseRateSSCode = rs.getString("FbaseRateSSCode");//基础汇率行情 申赎
        this.FportRateSrcSSCode = rs.getString("FportRateSrcSSCode");//组合汇率来源代码 申赎
        this.portRateSrcSSName = rs.getString("portRateSrcSSName");//组合汇率来源名称 申赎
        this.FportRateSSCode = rs.getString("FportRateSSCode");//组合汇率行情 申赎
        //补票汇率来源
        this.FbaseRateSrcBPCode = rs.getString("FbaseRateSrcBPCode");//基础汇率来源代码 补票
        this.baseRateSrcBPName = rs.getString("baseRateSrcBPName");//基础汇率来源名称 补票
        this.FbaseRateBPCode = rs.getString("FbaseRateBPCode");//基础汇率行情 补票
        this.FportRateSrcBPCode = rs.getString("FportRateSrcBPCode");//组合汇率来源代码 补票
        this.portRateSrcBPName = rs.getString("portRateSrcBPName");//组合汇率来源名称 补票
        this.FportRateBPCode = rs.getString("FportRateBPCode");//组合汇率行情 补票
		/**end shashijie 2012-12-7 STORY */
        
        //add by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B
        this.clearNum = rs.getString("FClearNum");//清算编号
        
        super.setRecLog(rs);
    }

	public String getSOtherData() {
		return sOtherData;
	}

	public void setSOtherData(String otherData) {
		sOtherData = otherData;
	}

	public String getSRightData() {
		return sRightData;
	}

	public void setSRightData(String rightData) {
		sRightData = rightData;
	}

	public String getSSubscribeData() {
		return sSubscribeData;
	}

	public void setSSubscribeData(String subscribeData) {
		sSubscribeData = subscribeData;
	}

	public String getSSupplyAndForceData() {
		return sSupplyAndForceData;
	}

	public void setSSupplyAndForceData(String supplyAndForceData) {
		sSupplyAndForceData = supplyAndForceData;
	}

	public String getSRedeemData() {
		return sRedeemData;
	}

	public void setSRedeemData(String redeemData) {
		sRedeemData = redeemData;
	}

	public String getSBookTotalType() {
		return sBookTotalType;
	}

	public void setSBookTotalType(String bookTotalType) {
		sBookTotalType = bookTotalType;
	}

	public int getSSGBalanceOver() {
		return sSGBalanceOver;
	}

	public void setSSGBalanceOver(int balanceOver) {
		sSGBalanceOver = balanceOver;
	}

	public int getSSGReplaceOver() {
		return sSGReplaceOver;
	}

	public void setSSGReplaceOver(int replaceOver) {
		sSGReplaceOver = replaceOver;
	}

	public int getSSHBalanceOver() {
		return sSHBalanceOver;
	}

	public void setSSHBalanceOver(int balanceOver) {
		sSHBalanceOver = balanceOver;
	}

	public int getSSHReplaceOver() {
		return sSHReplaceOver;
	}

	public void setSSHReplaceOver(int replaceOver) {
		sSHReplaceOver = replaceOver;
	}

	public int getSUnitdigit() {
		return sUnitdigit;
	}

	public void setSUnitdigit(int unitdigit) {
		sUnitdigit = unitdigit;
	}

	public int getISHDealReplace() {
		return iSHDealReplace;
	}

	public void setISHDealReplace(int dealReplace) {
		iSHDealReplace = dealReplace;
	}

	public String getSHolidayCode() {
		return sHolidayCode;
	}

	public void setSHolidayCode(String holidayCode) {
		sHolidayCode = holidayCode;
	}

	public String getSHolidayName() {
		return sHolidayName;
	}

	public void setSHolidayName(String holidayName) {
		sHolidayName = holidayName;
	}

	public int getISGDealReplace() {
		return iSGDealReplace;
	}

	public void setISGDealReplace(int dealReplace) {
		iSGDealReplace = dealReplace;
	}

	public String getSQuitMoneyValue() {
		return sQuitMoneyValue;
	}

	public void setSQuitMoneyValue(String quitMoneyValue) {
		sQuitMoneyValue = quitMoneyValue;
	}

	public String getClearAccCode() {
		return ClearAccCode;
	}

	public void setClearAccCode(String clearAccCode) {
		ClearAccCode = clearAccCode;
	}

	public String getClearAccName() {
		return ClearAccName;
	}

	public void setClearAccName(String clearAccName) {
		ClearAccName = clearAccName;
	}

	/**shashijie 2012-12-7 STORY 3328 下拉框节假日代码赋值 */
	public void setETFParamAttrParamhoildays(ResultSet rs) throws Exception {
		if (rs.getString("Fovertype").equalsIgnoreCase("beginsupply")) {
			this.BeginSupplyHD = rs.getString("FHolidaysCode");//开始补票 结转 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("dealdaynum")) {
			this.DealDayNumHD = rs.getString("FHolidaysCode");//几个交易日内补票完成 结转 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("lastestdealdaynum")) {
			this.LastestDealDayNumHD = rs.getString("FHolidaysCode");//最长几个交易日内补票完成 结转 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("sgbalanceover")) {
			this.sSGBalanceOverHD = rs.getString("FHolidaysCode");//申购现金差额 结转 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("sgreplaceover")) {
			this.sSGReplaceOverHD = rs.getString("FHolidaysCode");//申购现金替代款 结转 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("sgdealreplace")) {
			this.iSGDealReplaceHD = rs.getString("FHolidaysCode");//申购应付替代 结转 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("shbalanceover")) {
			this.sSHBalanceOverHD = rs.getString("FHolidaysCode");//赎回现金差额的 结转 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("shreplaceover")) {
			this.sSHReplaceOverHD = rs.getString("FHolidaysCode");//赎回现金替代款 结转 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("shdealreplace")) {
			this.iSHDealReplaceHD = rs.getString("FHolidaysCode");//赎回应付替代款 结转 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("beginsupply2")) {
			this.BeginSupplyHD2 = rs.getString("FHolidaysCode");//开始补票 结转2 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("dealdaynum2")) {
			this.DealDayNumHD2 = rs.getString("FHolidaysCode");//几个交易日内补票完成 结转2 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("lastestdealdaynum2")) {
			this.LastestDealDayNumHD2 = rs.getString("FHolidaysCode");//最长几个交易日内补票完成 结转2 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("sgbalanceover2")) {
			this.sSGBalanceOverHD2 = rs.getString("FHolidaysCode");//申购现金差额 结转2 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("sgreplaceover2")) {
			this.sSGReplaceOverHD2 = rs.getString("FHolidaysCode");//申购现金替代款 结转2 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("sgdealreplace2")) {
			this.iSGDealReplaceHD2 = rs.getString("FHolidaysCode");//申购应付替代 结转2 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("shbalanceover2")) {
			this.sSHBalanceOverHD2 = rs.getString("FHolidaysCode");//赎回现金差额的 结转2 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("shreplaceover2")) {
			this.sSHReplaceOverHD2 = rs.getString("FHolidaysCode");//赎回现金替代款 结转2 节假日群
		}
		if (rs.getString("Fovertype").equalsIgnoreCase("shdealreplace2")) {
			this.iSHDealReplaceHD2 = rs.getString("FHolidaysCode");//赎回应付替代款 结转2 节假日群
		}
		
	}

	/**shashijie 2012-12-10 STORY 3328 清空节假日代码,否则循环中会叠(累)加出其他组合的节假日 */
	public void clearHoildays() {
		this.BeginSupplyHD = " ";//开始补票 结转 节假日群
		this.DealDayNumHD = " ";//几个交易日内补票完成 结转 节假日群
		this.LastestDealDayNumHD = " ";//最长几个交易日内补票完成 结转 节假日群
		this.sSGBalanceOverHD = " ";//申购现金差额 结转 节假日群
		this.sSGReplaceOverHD = " ";//申购现金替代款 结转 节假日群
		this.iSGDealReplaceHD = " ";//申购应付替代 结转 节假日群
		this.sSHBalanceOverHD = " ";//赎回现金差额的 结转 节假日群
		this.sSHReplaceOverHD = " ";//赎回现金替代款 结转 节假日群
		this.iSHDealReplaceHD = " ";//赎回应付替代款 结转 节假日群
		this.BeginSupplyHD2 = " ";//开始补票 结转2 节假日群
		this.DealDayNumHD2 = " ";//几个交易日内补票完成 结转2 节假日群
		this.LastestDealDayNumHD2 = " ";//最长几个交易日内补票完成 结转2 节假日群
		this.sSGBalanceOverHD2 = " ";//申购现金差额 结转2 节假日群
		this.sSGReplaceOverHD2 = " ";//申购现金替代款 结转2 节假日群
		this.iSGDealReplaceHD2 = " ";//申购应付替代 结转2 节假日群
		this.sSHBalanceOverHD2 = " ";//赎回现金差额的 结转2 节假日群
		this.sSHReplaceOverHD2 = " ";//赎回现金替代款 结转2 节假日群
		this.iSHDealReplaceHD2 = " ";//赎回应付替代款 结转2 节假日群
	}

}
