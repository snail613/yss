package com.yss.main.parasetting;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.yss.dsub.*;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.pojo.sys.YssPageInationBean;
import com.yss.util.*;

import java.math.BigDecimal;

/**
 *
 * <p>Title:SecurityBean </p>
 * <p>Description: 证券信息设置</p>
 * <p>
 *    调整总股本、流动股本、每手数量的精度取数，使用BigDecimal
 *    之前使用double取数，但数据库中对应的字段类型为Decimal(18,4)，所以会有数据丢失
 *    author : sunkey
 *    date   : 2009-07-03
 *    BugNO  : MS00525 QDV4赢时胜（上海）2009年6月21日01_B
 * <p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class SecurityBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strSecurityCode = "";        //证券代码
    private String strSecurityName = "";        //证券名称
    private String strSecurityShortName = "";   //证券中文简称    BugNo:0000429 edit by jc
    private String strSecurityCorpName = "";    //证券公司名称    BugNo:0000429 edit by jc
    private String strCategoryCode = "";        //品种代码
    private String strCategoryName = "";        //品种名称
    private String strSubCategoryCode = "";     //品种明细代码
    private String strSubCategoryName = "";     //品种明细名称
    private String strExchangeCode = "";        //交易所代码
    private String strExchangeName = "";        //交易所名称
    private String strRegionCode = "";          //地域代码
    private String strRegionName = "";          //地域名称
    private String strCountryCode = "";         //国家代码
    private String strCountryName = "";         //国家名称
    private String strAreaCode = "";            //地区代码
    private String strAreaName = "";            //地区名称
    private String strMarketCode = "";          //上市代码
    private String strTradeCuryCode = "";       //交易货币代码
    private String strTradeCuryName = "";       //交易货币名称
    private String strSectorCode = "";          //行业板块代码
    private String strSectorName = "";          //行业板块名称
    //==========add by yanghaiming 20100426 B股业务
    private String strSyntheticCode = "";		//板块分类代码
    private String strSyntheticName = "";		//板块分类名称
    

	private String strSettleDayType = "";       //结算日期类型
    private int intSettleDays = -1;             //延迟结算延迟天数
    private double dblFactor = -1;              //报价因子

    //下面几个成员变量在数据库中对应的字段都是decimal(18,4),因此要改为BigDecimal取数，否则数据会不完整
    //modify by sunkey 20090703 MS00525:QDV4赢时胜（上海）2009年6月21日01_B
    private BigDecimal dblTotalShare = new BigDecimal(-1.0); //总股本
    private BigDecimal dblCurrentShare = new BigDecimal(-1.0); //流动股本
    private BigDecimal dblHandAmount = new BigDecimal(-1.0); //每手数量
    //===============================End MS00525 ===============================

    //MS00619 QDV4银华2009年08月06日01_A
    private String sMaintainMgr=""; //维护管理人
    private String sOperStyle="";   //运作方式
    //End MS00619 add by pengjinggang 2009_9_1
    
    //add by zhangjun 2012-04-27 ETF联接基金
    private BigDecimal dblMinShare =  new BigDecimal(-1.0);//最小申赎份额
    private int sSGSHCashBalance;////申购赎回现金差额结转
    private int sSGDealReplace;//申购现金替代结转
    
    //add by zhangjun 2012-04-27 ETF联接基金

    private String strIssueCorpCode = "";   //发行人代码
    private String strIssueCorpName = "";   //发行人名称
    private String strCusCatCode = "";      //自定义子品种代码
    private String strCusCatName = "";      //自定义子品种名称
    private String strHolidaysCode = "";    //节假日群代码
    private String strHolidaysName = "";    //节假日群名称
    private String strExternalCode = "";    //外部代码
    private String strIsinCode = "";        //INSI 代码
    private String strDesc = "";            //证券描述
    private String strOldSecurityCode = ""; //修改用证券代码
    private String strIsOnlyColumns = "0";  //有时在显示证券列表时只显示列，不查询数据
    private String settleDayTypeValue = ""; //结算日期类型名称
    private String sRecycled = "";
    private String sAssetGroupCode = "";    //新增字段 QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
    private String sAssetGroupName = "";	//组合群名称 		added by liubo.Story #1770

	private String sOldAssetGroupCode = ""; //QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204

    //2009.02.23 蒋锦 添加 优化估值方法的设置 《QDV4.1赢时胜上海2009年2月1日07_A》BugNO:V4.1-MS00008
    //证券信息在审核和反审核时，自动添入或剔除出估值方法链接，所关联的估值方法代码存入此字段中
    private String sMTVCode = "";

    private SecurityBean filterType;
    private java.util.Date dtStartDate;
    private java.util.Date dtOldStartDate;
    private String multAuditString = "";    //批量处理数据 MS00179 QDV4建行2009年1月07日01_B 2009.02.13 方浩
    private FixInterestBean fixInterest;
	private SingleLogOper logOper;
	
	private BigDecimal db1FaceAmount = new BigDecimal(-1.0);//股票面值  #406  南方东英\2010年12月\14日 需参考3.0版本，在证券信息维护界面增加股票面值字段 ldaolong 20110112
	//--- story 2727 add by zhouwei 20120619 ETF链接基金 start---//
	private int mtReplaceOverDay=0;//赎回必须现金替代款的结转日 	
	
	public int getMtReplaceOverDay() {
		return mtReplaceOverDay;
	}

	public void setMtReplaceOverDay(int mtReplaceOverDay) {
		this.mtReplaceOverDay = mtReplaceOverDay;
	}
	//--- story 2727 add by zhouwei 20120619 ETF链接基金 end---//

	public BigDecimal getDblMinShare() {
		return dblMinShare;
	}

	public void setDblMinShare(BigDecimal dblMinShare) {
		this.dblMinShare = dblMinShare;
	}

	public int getsSGSHCashBalance() {
		return sSGSHCashBalance;
	}

	public void setsSGSHCashBalance(int sSGSHCashBalance) {
		this.sSGSHCashBalance = sSGSHCashBalance;
	}

	public int getsSGDealReplace() {
		return sSGDealReplace;
	}

	public void setsSGDealReplace(int sSGDealReplace) {
		this.sSGDealReplace = sSGDealReplace;
	}

    public String getsAssetGroupName() {
		return sAssetGroupName;
	}

	public void setsAssetGroupName(String sAssetGroupName) {
		this.sAssetGroupName = sAssetGroupName;
	}
	
	public String getStrIssueCorpCode() {
        return strIssueCorpCode;
    }

    public double getDblFactor() {
        return dblFactor;
    }

    public String getStrCusCatCode() {
        return strCusCatCode;
    }

    public String getStrMarketCode() {
        return strMarketCode;
    }

    public String getStrHolidaysCode() {
        return strHolidaysCode;
    }

    public java.util.Date getDtStartDate() {
        return dtStartDate;
    }

    public BigDecimal getDblCurrentShare() {
        return dblCurrentShare;
    }

    public String getStrCountryCode() {
        return strCountryCode;
    }

    public String getStrIssueCorpName() {
        return strIssueCorpName;
    }

    public String getStrCategoryCode() {
        return strCategoryCode;
    }

    public String getStrHolidaysName() {
        return strHolidaysName;
    }

    public BigDecimal getDblHandAmount() {
        return dblHandAmount;
    }

    public String getStrAreaCode() {
        return strAreaCode;
    }

    public String getStrSubCategoryCode() {
        return strSubCategoryCode;
    }

    public String getStrExchangeCode() {
        return strExchangeCode;
    }

    public String getStrIsOnlyColumns() {
        return strIsOnlyColumns;
    }

    public int getIntSettleDays() {
        return intSettleDays;
    }

    public String getStrSectorName() {
        return strSectorName;
    }

    public String getStrRegionName() {
        return strRegionName;
    }

    public String getStrExternalCode() {
        return strExternalCode;
    }

    public String getStrRegionCode() {
        return strRegionCode;
    }

    public String getStrSubCategoryName() {
        return strSubCategoryName;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public SecurityBean getFilterType() {
        return filterType;
    }

    public String getStrCusCatName() {
        return strCusCatName;
    }

    public String getStrSecurityCode() {
        return strSecurityCode;
    }

    public String getStrTradeCuryName() {
        return strTradeCuryName;
    }

    public String getStrSecurityName() {
        return strSecurityName;
    }

    public String getStrSettleDayType() {
        return strSettleDayType;
    }

    public java.util.Date getDtOldStartDate() {
        return dtOldStartDate;
    }

    public String getStrAreaName() {
        return strAreaName;
    }

    public String getStrCategoryName() {
        return strCategoryName;
    }

    public String getStrSectorCode() {
        return strSectorCode;
    }

    public String getStrExchangeName() {
        return strExchangeName;
    }

    public String getStrOldSecurityCode() {
        return strOldSecurityCode;
    }

    public BigDecimal getDblTotalShare() {
        return dblTotalShare;
    }

    public String getStrTradeCuryCode() {
        return strTradeCuryCode;
    }

    public void setStrCountryName(String strCountryName) {
        this.strCountryName = strCountryName;
    }

    public void setStrIssueCorpCode(String strIssueCorpCode) {
        this.strIssueCorpCode = strIssueCorpCode;
    }

    public void setDblFactor(double dblFactor) {
        this.dblFactor = dblFactor;
    }

    public void setStrCusCatCode(String strCusCatCode) {
        this.strCusCatCode = strCusCatCode;
    }

    public void setStrCusCateName(String strCusCatName) {
        this.strCusCatName = strCusCatName;
    }

    public void setStrMarketCode(String strMarketCode) {
        this.strMarketCode = strMarketCode;
    }

    public void setDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public void setStrHolidaysCode(String strHolidaysCode) {
        this.strHolidaysCode = strHolidaysCode;
    }

    public void setDtStartDate(Date dtStartDate) {
        this.dtStartDate = dtStartDate;
    }

    public void setDblCurrentShare(BigDecimal dblCurrentShare) {
        this.dblCurrentShare = dblCurrentShare;
    }

    public void setStrCountryCode(String strCountryCode) {
        this.strCountryCode = strCountryCode;
    }

    public void setStrIssueCorpName(String strIssueCorpName) {
        this.strIssueCorpName = strIssueCorpName;
    }

    public void setStrCategoryCode(String strCategoryCode) {
        this.strCategoryCode = strCategoryCode;
    }

    public void setStrHolidaysName(String strHolidaysName) {
        this.strHolidaysName = strHolidaysName;
    }

    public void setDblHandAmount(BigDecimal dblHandAmount) {
        this.dblHandAmount = dblHandAmount;
    }

    public void setStrAreaCode(String strAreaCode) {
        this.strAreaCode = strAreaCode;
    }

    public void setStrSubCategoryCode(String strSubCategoryCode) {
        this.strSubCategoryCode = strSubCategoryCode;
    }

    public void setStrExchangeCode(String strExchangeCode) {
        this.strExchangeCode = strExchangeCode;
    }

    public void setStrIsOnlyColumns(String strIsOnlyColumns) {
        this.strIsOnlyColumns = strIsOnlyColumns;
    }

    public void setIntSettleDays(int intSettleDays) {
        this.intSettleDays = intSettleDays;
    }

    public void setStrSectorName(String strSectorName) {
        this.strSectorName = strSectorName;
    }

    public void setStrRegionName(String strRegionName) {
        this.strRegionName = strRegionName;
    }

    public void setStrExternalCode(String strExternalCode) {
        this.strExternalCode = strExternalCode;
    }

    public void setStrRegionCode(String strRegionCode) {
        this.strRegionCode = strRegionCode;
    }

    public void setStrSubCategoryName(String strSubCategoryName) {
        this.strSubCategoryName = strSubCategoryName;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public void setFilterType(SecurityBean filterType) {
        this.filterType = filterType;
    }

    public void setStrCusCatName(String strCusCatName) {
        this.strCusCatName = strCusCatName;
    }

    public void setStrSecurityCode(String strSecurityCode) {
        this.strSecurityCode = strSecurityCode;
    }

    public void setSecurityName(String strSecurityName) {
        this.strSecurityName = strSecurityName;
    }

    public void setSubCategoryCode(String strSubCategoryCode) {
        this.strSubCategoryCode = strSubCategoryCode;
    }

    public void setSubCategoryName(String strSubCategoryName) {
        this.strSubCategoryName = strSubCategoryName;
    }

    public void setStrTradeCuryName(String strTradeCuryName) {
        this.strTradeCuryName = strTradeCuryName;
    }

    public void setStrSecurityName(String strSecurityName) {
        this.strSecurityName = strSecurityName;
    }

    public void setStrSettleDayType(String strSettleDayType) {
        this.strSettleDayType = strSettleDayType;
    }

    public void setDtOldStartDate(Date dtOldStartDate) {
        this.dtOldStartDate = dtOldStartDate;
    }

    public void setStrAreaName(String strAreaName) {
        this.strAreaName = strAreaName;
    }

    public void setStrCategoryName(String strCategoryName) {
        this.strCategoryName = strCategoryName;
    }

    public void setStrSectorCode(String strSectorCode) {
        this.strSectorCode = strSectorCode;
    }

    public void setStrExchangeName(String strExchangeName) {
        this.strExchangeName = strExchangeName;
    }

    public void setStrOldSecurityCode(String strOldSecurityCode) {
        this.strOldSecurityCode = strOldSecurityCode;
    }

    public void setDblTotalShare(BigDecimal dblTotalShare) {
        this.dblTotalShare = dblTotalShare;
    }

    public void setStrTradeCuryCode(String strTradeCuryCode) {
        this.strTradeCuryCode = strTradeCuryCode;
    }

    public void setSettleDayTypeValue(String settleDayTypeValue) {
        this.settleDayTypeValue = settleDayTypeValue;
    }

    public void setFixInterest(FixInterestBean fixInterest) {
        this.fixInterest = fixInterest;
    }

    public void setStrSecurityCropName(String strSecurityCorpName) {
        this.strSecurityCorpName = strSecurityCorpName;
    }

    public void setStrSecurityShortName(String strSecurityShortName) {
        this.strSecurityShortName = strSecurityShortName;
    }

    /**
     * QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
     * @param sAssetGroupCode String
     */
    public void setSAssetGroupCode(String sAssetGroupCode) {
        this.sAssetGroupCode = sAssetGroupCode;
    }

    /**
     * QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
     * @param sOldAssetGroupCode String
     */
    public void setSOldAssetGroupCode(String sOldAssetGroupCode) {
        this.sOldAssetGroupCode = sOldAssetGroupCode;
    }

    public void setStrsiInCode(String strInsiCode) {
        this.strIsinCode = strInsiCode;//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
    }

    /**
     * 2009.02.23 蒋锦 添加 优化估值方法的设置 《QDV4.1赢时胜上海2009年2月1日07_A》
     * @param sMTVCode String
     */
    public void setSMTVCode(String sMTVCode) {
        this.sMTVCode = sMTVCode;
    }

    public void setSOperStyle(String sOperStyle) {
        this.sOperStyle = sOperStyle;
    }

    public void setSMaintainMgr(String sMaintainMgr) {
        this.sMaintainMgr = sMaintainMgr;
    }

    /**
     * 2009.02.23 蒋锦 添加 优化估值方法的设置 《QDV4.1赢时胜上海2009年2月1日07_A》
     * @param sMTVCode String
     */
    public String getSMTVCode() {
        return sMTVCode;
    }

    public String getStrCountryName() {
        return strCountryName;
    }

    public String getSettleDayTypeValue() {
        return settleDayTypeValue;
    }

    public String getStrIsinCode() {
        return strIsinCode;
    }

    public FixInterestBean getFixInterest() {
        return fixInterest;
    }

    public String getStrSecurityCropName() {
        return strSecurityCorpName;
    }

    public String getStrSecurityShortName() {
        return strSecurityShortName;
    }

    /**
     * QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
     * @return String
     */
    public String getSAssetGroupCode() {
        return sAssetGroupCode;
    }

    /**
     * QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
     * @return String
     */
    public String getSOldAssetGroupCode() {
        return sOldAssetGroupCode;
    }

    public String getSOperStyle() {
        return sOperStyle;
    }

    public String getSMaintainMgr() {
        return sMaintainMgr;
    }

    public SecurityBean() {
    }

    public String getSecurityCode() {
        return strSecurityCode;
    }

    public void setSecurityCode(String strSecurityCode) {
        this.strSecurityCode = strSecurityCode;
    }

    public java.util.Date getStartDate() {
        return dtStartDate;
    }

    public void setStartDate(java.util.Date dtStartDate) {
        this.dtStartDate = dtStartDate;
    }

    public String getCategoryCode() {
        return strCategoryCode;
    }

    public void setCategoryCode(String strCategoryCode) {
        this.strCategoryCode = strCategoryCode;
    }

    public String getSubCategoryCode() {
        return strSubCategoryCode;
    }

    public String getExchangeCode() {
        return strExchangeCode;
    }

    public void setExchangeCode(String strExchangeCode) {
        this.strExchangeCode = strExchangeCode;
    }

    public String getMarketCode() {
        return strMarketCode;
    }

    public void setMarketCode(String strMarketCode) {
        this.strMarketCode = strMarketCode;
    }

    public String getTradeCuryCode() {
        return strTradeCuryCode;
    }

    public void setTradeCuryCode(String strTradeCuryCode) {
        this.strTradeCuryCode = strTradeCuryCode;
    }

    public String getSectorCode() {
        return strSectorCode;
    }

    public void setSectorCode(String strSectorCode) {
        this.strSectorCode = strSectorCode;
    }

    public String getSettleDayType() {
        return strSettleDayType;
    }

    public void setSettleDayType(String strSettleDayType) {
        this.strSettleDayType = strSettleDayType;
    }

    public int getSettleDays() {
        return intSettleDays;
    }

    public void setSettleDays(int intSettleDays) {
        this.intSettleDays = intSettleDays;
    }

    public double getFactor() {
        return dblFactor;
    }

    public void setFactor(double dblFactor) {
        this.dblFactor = dblFactor;
    }

    public BigDecimal getTotalShare() {
        return dblTotalShare;
    }

    public void setTotalShare(BigDecimal dblTotalShare) {
        this.dblTotalShare = dblTotalShare;
    }

    public BigDecimal getCurrentShare() {
        return dblCurrentShare;
    }

    public void setCurrentShare(BigDecimal dblCurrentShare) {
        this.dblCurrentShare = dblCurrentShare;
    }

    public BigDecimal getHandAmount() {
        return dblHandAmount;
    }

    public void setHandAmount(BigDecimal dblHandAmount) {
        this.dblHandAmount = dblHandAmount;
    }

    public String getIssueCorpCode() {
        return strIssueCorpCode;
    }

    public void setIssueCorpCode(String strIssueCorpCode) {
        this.strIssueCorpCode = strIssueCorpCode;
    }

    public String getCusCatCode() {
        return strCusCatCode;
    }

    public void setCusCatCode(String strCusCatCode) {
        this.strCusCatCode = strCusCatCode;
    }

    public String getHolidaysCode() {
        return strHolidaysCode;
    }

    public void setHolidaysCode(String strHolidaysCode) {
        this.strHolidaysCode = strHolidaysCode;
    }

    public String getExternalCode() {
        return strExternalCode;
    }

    public void setExternalCode(String strExternalCode) {
        this.strExternalCode = strExternalCode;
    }

    public String getStrSyntheticCode() {
		return strSyntheticCode;
	}

	public void setStrSyntheticCode(String strSyntheticCode) {
		this.strSyntheticCode = strSyntheticCode;
	}

	public String getStrSyntheticName() {
		return strSyntheticName;
	}

	public void setStrSyntheticName(String strSyntheticName) {
		this.strSyntheticName = strSyntheticName;
	}
    /**
     * parseRowStr
     * 解析证券信息维护请求
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        //------------------------------------------------------------------------------------------------
        String sMutiAudit = ""; //MS00179 QDV4建行2009年1月07日01_B 2009.02.13 方浩 批量处理的数据
        try {
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
            //MS00179 QDV4建行2009年1月07日01_B 2009.02.13 方浩 提取批量处理数据
            if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) {
                sMutiAudit = sRowStr.split("\f\n\f\n\f\n")[1]; //得到的是从前台传来需要审核与反审核的批量数据
                multAuditString = sMutiAudit; //保存在全局变量中
                sRowStr = sRowStr.split("\f\n\f\n\f\n")[0]; //前台传来的要更新的一些数据
            }
            //-------------------------------------------------------------------------------------------------
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.strSecurityCode = reqAry[0];
            this.strSecurityName = reqAry[1];
            this.dtStartDate = YssFun.toDate(reqAry[2]);
            this.strCategoryCode = reqAry[3];
            this.strSubCategoryCode = reqAry[4];
            this.strExchangeCode = reqAry[5];
            this.strMarketCode = reqAry[6];
            this.strTradeCuryCode = reqAry[7];
            this.strSectorCode = reqAry[8];
            this.strSyntheticCode = reqAry[9];//add by yanghaiming 20100426
            this.strSettleDayType = reqAry[10];
            this.intSettleDays = Integer.parseInt(reqAry[11]);
            this.dblFactor = Double.parseDouble(reqAry[12]);
            this.dblTotalShare = new BigDecimal(reqAry[13]);
            this.dblCurrentShare = new BigDecimal(reqAry[14]);
            this.dblHandAmount = new BigDecimal(reqAry[15]);
            this.strIssueCorpCode = reqAry[16];
            this.strIssueCorpName = reqAry[17];
            this.strCusCatCode = reqAry[18];
            this.strCusCatName = reqAry[19];
            this.strHolidaysCode = reqAry[20];
            this.strExternalCode = reqAry[21];
            
            //------ modify by wangzuochun 2011.06.03 BUG 2003 证券信息维护界面，维护一条证券信息，输入描述信息若含有回车符，清除/还原时报错 
            if (reqAry[22] != null ){
            	if (reqAry[22].indexOf("【Enter】") >= 0){
            		this.strDesc = reqAry[22].replaceAll("【Enter】", "\r\n");
            	}
            	else{
            		this.strDesc = reqAry[22];
            	}
            }
            //----------------- BUG 2003 ----------------//
            
            this.checkStateId = Integer.parseInt(reqAry[23]);
            this.strOldSecurityCode = reqAry[24];
            this.dtOldStartDate = YssFun.toDate(reqAry[25]);
            this.strIsinCode = reqAry[26];
            this.strIsOnlyColumns = reqAry[27];
            this.strSecurityShortName = reqAry[28];
            this.strSecurityCorpName = reqAry[29];
            this.sAssetGroupCode = reqAry[30];      //QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
            this.sAssetGroupName = reqAry[31];		//added by liubo.Story #1770
            this.sOldAssetGroupCode = reqAry[32];   //QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204


            //MS00619 QDV4银华2009年08月06日01_A
            this.sMaintainMgr=reqAry[33];   //维护管理人
            this.sOperStyle=reqAry[34];     //运作方式
            //Emd MS00619 add by pengjinggang 2009_9_1

            //=====#406 南方东英\2010年12月\14日 需参考3.0版本，在证券信息维护界面增加股票面值字段=====
            this.db1FaceAmount = new BigDecimal(reqAry[35]);
            //=======end ldaolong  20110112=====
            
            //add by zhangjun 2012-04-27 ETF联接基金
            this.dblMinShare =  new BigDecimal(YssFun.toNumber(reqAry[36])); 
            if (reqAry[37].length() != 0) {
            	this.sSGSHCashBalance = Integer.parseInt(reqAry[37]);
            }
            if(reqAry[38].length()!=0){
            	this.sSGDealReplace = Integer.parseInt(reqAry[38]);
            }            
            //add by zhangjun 2012-04-27 ETF联接基金
            
           this.mtReplaceOverDay=Integer.parseInt(reqAry[39]);//赎回必须现金替代款的结转日 story 2727 add by zhouwei 20120619 ETF链接基金
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new SecurityBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析证券信息维护出错", e);
        }
    }

    /**
     * buildRowStr
     * 获取数据字符串
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        
        //方法里去掉所有的trim(),这样防止字段中本身有空格字段 by leeyu 20090209 QDV4鹏华基金2008年12月31日01_B MS00133
        buf.append(this.strSecurityCode);
        buf.append("\t");
        buf.append(this.strSecurityName);
        buf.append("\t");
        buf.append(this.dtStartDate == null ? "9998-12-31" : YssFun.formatDate(this.dtStartDate, YssCons.YSS_DATEFORMAT));//modify by nimengjing 2010.12.1 bug#572
        buf.append("\t");
        buf.append(this.strCategoryCode);
        buf.append("\t");
        buf.append(this.strCategoryName);
        buf.append("\t");
        buf.append(this.strSubCategoryCode);
        buf.append("\t");
        buf.append(this.strSubCategoryName);
        buf.append("\t");
        buf.append(this.strExchangeCode);
        buf.append("\t");
        buf.append(this.strExchangeName);
        buf.append("\t");
        buf.append(this.strRegionCode);
        buf.append("\t");
        buf.append(this.strRegionName);
        buf.append("\t");
        buf.append(this.strCountryCode);
        buf.append("\t");
        buf.append(this.strCountryName);
        buf.append("\t");
        buf.append(this.strAreaCode);
        buf.append("\t");
        buf.append(this.strAreaName);
        buf.append("\t");
        buf.append(this.strMarketCode);
        buf.append("\t");
        buf.append(this.strTradeCuryCode);
        buf.append("\t");
        buf.append(this.strTradeCuryName);
        buf.append("\t");
        buf.append(this.strSectorCode);
        buf.append("\t");
        buf.append(this.strSectorName);
        buf.append("\t");
        buf.append(this.strSyntheticCode);//add by yanghaiming 20100426
        buf.append("\t");
        buf.append(this.strSyntheticName);
        buf.append("\t");
        buf.append(this.strSettleDayType);
        buf.append("\t");
        buf.append(this.intSettleDays);
        buf.append("\t");
        buf.append(this.dblFactor);
        buf.append("\t");
        buf.append(this.dblTotalShare);
        buf.append("\t");
        buf.append(this.dblCurrentShare);
        buf.append("\t");
        buf.append(this.dblHandAmount);
        buf.append("\t");
        buf.append(this.strIssueCorpCode);
        buf.append("\t");
        buf.append(this.strIssueCorpName);
        buf.append("\t");
        buf.append(this.strHolidaysCode);
        buf.append("\t");
        buf.append(this.strHolidaysName);
        buf.append("\t");
        buf.append(this.strExternalCode);
        buf.append("\t");
        buf.append(this.strDesc);
        buf.append("\t");
        buf.append(this.strCusCatCode);
        buf.append("\t");
        buf.append(this.strCusCatName);
        buf.append("\t");
        buf.append(this.settleDayTypeValue);
        buf.append("\t");
        buf.append(this.strIsinCode);
        buf.append("\t");
        buf.append(this.strSecurityShortName);
        buf.append("\t");
        buf.append(this.strSecurityCorpName);
        buf.append("\t");
        buf.append(this.sAssetGroupCode).append("\t");  //QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
        buf.append(this.sAssetGroupName).append("\t");	//added by liubo.Story #1770
        buf.append(this.db1FaceAmount).append("\t");  //#406 南方东英\2010年12月\14日  需参考3.0版本，在证券信息维护界面增加股票面值字段 lidaolong 20110112
        buf.append(this.sMaintainMgr).append("\t");     //维护管理人 MS00619 QDV4银华2009年08月06日01_A add by pengjinggang 2009_9_1
        buf.append(this.sOperStyle).append("\t");       //运作方式   MS00619 QDV4银华2009年08月06日01_A add by pengjinggang 2009_9_1
      
        //add by zhangjun 2012-04-27 ETF联接基金
        buf.append(this.dblMinShare).append("\t");
        buf.append(this.sSGSHCashBalance).append("\t");
        buf.append(this.sSGDealReplace).append("\t");
        //add by zhangjun 2012-04-27 ETF联接基金
        buf.append(this.mtReplaceOverDay).append("\t");//赎回必须现金替代款的结转日 story 2727 add by zhouwei 20120619 ETF链接基金
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查证券信息维护数据是否合法
     * @param btOper byte
       }

     */
    public void checkInput(byte btOper) throws YssException {
    	
    	//启用日期已不是主键，从判断中干掉 MS00564 edited by libo 证券信息维护时日期不为主键
//        if ( ( (String) pub.getHtPubParams().get("security")).equalsIgnoreCase(
//            "true")) { //当通用参数传过来的值为真时再给这个变量赋值
//            dbFun.checkInputCommon(btOper, "Tb_Base_Security",
//                                   "FSecurityCode,FASSETGROUPCODE",
//                                   this.strSecurityCode + "," +
//                                   this.sAssetGroupCode,
//                                   this.strOldSecurityCode + "," +
//                                   this.sOldAssetGroupCode); //QDV4建行2008年12月25日01_A  MS00131 by leeyu 20090204
//        } else {
//            dbFun.checkInputCommon(btOper,
//                                   pub.yssGetTableName("Tb_Para_Security"),
//                                   "FSecurityCode",
//                                   this.strSecurityCode,
//                                   this.strOldSecurityCode);
//        }

		String[] sAssetGroup = null;
		if("".equals(sAssetGroupCode.trim())){
    		dbFun.checkInputCommon(btOper,
    					pub.yssGetTableName("Tb_Para_Security"),
	                  "FSecurityCode",
	                  this.strSecurityCode,
	                  this.strOldSecurityCode);
		}else{
			sAssetGroup =  sAssetGroupCode.split(",");
    		for (int i = 0;i < sAssetGroup.length;i++)
    		{
    	    	try
    	    	{
    	    	//---edit by songjie 2012.05.24 BUG 4486 QDV4赢时胜(测试)2012年05月07日01_B start---//
    	    	if(btOper == YssCons.OP_ADD){//添加操作类型判断 将 新增 和 修改情况区分开来
    	    		dbFun.checkInputCommon(YssCons.OP_MutliAdd,
    		                  "Tb_" + sAssetGroup[i] + "_Para_Security",
    		                  "FSecurityCode",
    		                  this.strSecurityCode,
    		                  this.strOldSecurityCode);

    	    	}else{
    	    		dbFun.checkInputCommon(YssCons.OP_MutliEdit,
    	    					"Tb_" + sAssetGroup[i] + "_Para_Security",
    	    					"FSecurityCode",
    	    					this.strSecurityCode,
    	    					this.strOldSecurityCode);

    	    	}
    	    	//---edit by songjie 2012.05.24 BUG 4486 QDV4赢时胜(测试)2012年05月07日01_B end---//
    		}
    	    	catch(Exception e)
    	    	{
    	    		throw new YssException("组合群【" + sAssetGroup[i] + "】已存在证券代码为【" + this.strSecurityCode + "】的数据！");
    	    	}
    		}
		}
    }

    /**
     * 筛选条件
     * @return String
     */
    public String buildFilterSql() throws YssException {
        String sResult = "";
    	//20111027 added by liubo.STORY #1285.  如果要浏览数据，则直接返回
    	//==============================
    	if(pub.isBrown()==true) 
		return " where 1=1";
    	//=============end=================
        try {
            if (this.filterType != null) {
                sResult = " where 1=1 ";
                if (this.strIsOnlyColumns.equalsIgnoreCase("1") && pub.isBrown()==false) {//20111027 modified by liubo.STORY #1285.  如果要浏览数据，则直接返回
                    sResult = sResult + " and 1=2 ";
                    return sResult;
                }
                if (this.filterType.strSecurityCode.length() != 0) {
                    sResult = sResult + " and a.FSecurityCode like '" +
                        filterType.strSecurityCode.replaceAll("'", "''") +
                        "%'";
                }
                if (this.filterType.strSecurityName.length() != 0) {
                    sResult = sResult + " and a.FSecurityName like '" +
                        filterType.strSecurityName.replaceAll("'", "''") +
                        "%'";
                }
				//fanghaoln 20100308 MS01032 QDV4赢时胜(测试)2010年03月17日01_B
				if (this.filterType.dtStartDate != null && this.filterType.dtStartDate.toString().length() != 0 &&
						!YssFun.toSqlDate(this.filterType.dtStartDate).toString().equals("9998-12-31")
						&& !YssFun.toSqlDate(this.filterType.dtStartDate).toString().equals("1900-01-01"))
				{
					sResult = sResult + " and a.FStartDate <= " +//增加一个日期筛选条件
					dbl.sqlDate(YssFun.toSqlDate(this.filterType.dtStartDate));
				}
				//-------------------------end -----MS01032-------------------------------------
                if (this.filterType.strCategoryCode.length() != 0) {
                	if(this.filterType.strCategoryCode.equals("otheropers"))
                	{//modified by yeshenghong  story2917 20121114
                		sResult = sResult + " and a.fcatcode not in ('EQ','FI','TR','RE','OP') ";
                	}else
                	{
	                    sResult = sResult + " and a.FCatCode like '" +
	                        filterType.strCategoryCode.replaceAll("'", "''") +
	                        "%'";
                	}
                }
                if (this.filterType.strSubCategoryCode.length() != 0) {
                    sResult = sResult + " and a.FSubCatCode like '" +
                        filterType.strSubCategoryCode.replaceAll("'",
                        "''") + "%'";
                }
                if (this.filterType.strExchangeCode.length() != 0) {
                    sResult = sResult + " and a.FExchangeCode like '" +
                        filterType.strExchangeCode.replaceAll("'", "''") +
                        "%'";
                }
                if (this.filterType.strMarketCode.length() != 0) {
                    sResult = sResult + " and a.FMarketCode like '" +
                        filterType.strMarketCode.replaceAll("'", "''") +
                        "%'";
                }
                if (this.filterType.strTradeCuryCode.length() != 0) {
                    sResult = sResult + " and a.FTradeCury like '" +
                        filterType.strTradeCuryCode.replaceAll("'", "''") +
                        "%'";
                }
                if (this.filterType.strSectorCode.length() != 0) {
                    sResult = sResult + " and a.FSectorCode like '" +
                        filterType.strSectorCode.replaceAll("'", "''") +
                        "%'";
                }
                if (this.filterType.strSyntheticCode.length() != 0) {//add by yanghaiming 20100426
                    sResult = sResult + " and a.FSYNTHETICCODE like '" +
                        filterType.strSyntheticCode.replaceAll("'", "''") +
                        "%'";
                }
                if (!this.filterType.strSettleDayType.equalsIgnoreCase("ALL") &&
                    this.filterType.strSettleDayType.length() != 0) {
                    sResult = sResult + " and a.FSettleDayType = " +
                        this.filterType.strSettleDayType;
                }
                if (this.filterType.intSettleDays > 0) {
                    sResult = sResult + " and a.FSettleDays = " +
                        this.filterType.intSettleDays;
                }
                if (this.filterType.dblTotalShare.doubleValue() > 0) {
                    sResult = sResult + " and a.FTotalShare = " +
                        this.filterType.dblTotalShare;
                }
                if (this.filterType.dblCurrentShare.doubleValue() > 0) {
                    sResult = sResult + " and a.FCurrentShare = " +
                        this.filterType.dblCurrentShare;
                }
                if (this.filterType.strIssueCorpCode.length() != 0) {
                    sResult = sResult + " and a.FIssueCorpCode = " +
                        dbl.sqlString(this.filterType.strIssueCorpCode);
                }
                if (this.filterType.strCusCatCode.length() != 0) {
                    sResult = sResult + " and a.FCusCatCode = " +
                        dbl.sqlString(this.filterType.strCusCatCode);
                }

                if (this.filterType.dblFactor > 0) {
                    sResult = sResult + " and a.FFactor = " +
                        this.filterType.dblFactor;
                }
                if (this.filterType.dblHandAmount.doubleValue() > 0) {
                    sResult = sResult + " and a.FHandAmount = " +
                        this.filterType.dblHandAmount;
                }
                //===== #406 南方东英\2010年12月\14日 需参考3.0版本，在证券信息维护界面增加股票面值字段 #406 需参考3.0版本，在证券信息维护界面增加股票面值字段
                if (this.filterType.db1FaceAmount.doubleValue() > 0) {
                    sResult = sResult + " and a.FFaceAmount = " +
                        this.filterType.db1FaceAmount;
                }
                //====end lidaolong 20110112=========
                
                //维护管理人 MS00619 QDV4银华2009年08月06日01_A add by pengjinggang 2009_9_1
                if(this.filterType.sMaintainMgr.length()!=0){
                    sResult = sResult + " and a.FMaintainMgr like '" +
                     filterType.sMaintainMgr.replaceAll("'", "''") +
                     "%'";
                }
                //运作方式 MS00619 QDV4银华2009年08月06日01_A add by pengjinggang 2009_9_1
                if(this.filterType.sOperStyle.length()!=0){
                    sResult = sResult + " and a.FOperStyle like '" +
                  filterType.sOperStyle.replaceAll("'", "''") +
                  "%'";
                }
                //add by zhangjun 2012-04-27 ETF联接基金
                if(this.filterType.dblMinShare.doubleValue()> 0){
                    sResult = sResult + " and a.FNormScale = '" +
                    this.filterType.dblMinShare;
                }
                
                //add by zhangjun 2012-04-27 ETF联接基金
                if (this.filterType.strHolidaysCode.length() != 0) {
                    sResult = sResult + " and a.FHolidaysCode like '" +
                        filterType.strHolidaysCode.replaceAll("'", "''") +
                        "%'";
                }
                if (this.filterType.strExternalCode.length() != 0) {
                    sResult = sResult + " and a.FExternalCode like '" +
                        filterType.strExternalCode.replaceAll("'", "''") +
                        "%'";
                }
                if (this.filterType.strDesc.length() != 0) {
                    sResult = sResult + " and a.FDesc like '" +
                        filterType.strDesc.replaceAll("'", "''") + "%'";
                }
                if ( ( (String) pub.getHtPubParams().get("security")).
                    equalsIgnoreCase("true")) { //当通用参数传过来的值为真时再给这个变量赋值
                    if (this.filterType.sAssetGroupCode.trim().length() != 0) {
                        sResult = sResult + " and a.FASSETGROUPCODE ='" +
                            filterType.sAssetGroupCode.replaceAll("'",
                            "''") + "'";
                    }
                } //QDV4建行2008年12月25日01_A  MS00131 by leeyu 20090204
            }
        } catch (Exception e) {
            throw new YssException("筛选组合设置数据出错", e);
        }
        return sResult;
    }

    /**
     * getAllSetting
     * 添加日期：2007-11-16
     * 蒋锦
     * @return String
     */
    public String getAllSetting() {
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        ResultSet rs = null;
        boolean bIsSecurityPub = false; //定义变量存放通用参数 QDV4建行2008年12月25日01_A  MS00131 byleeyu 20090204
        try {
            bIsSecurityPub = Boolean.valueOf( (String) pub.getHtPubParams().get(
                "security")).booleanValue(); //获取PUB中参数的值
            strSql = "select * from " +
                (bIsSecurityPub ? "Tb_Base_Security" :
                 pub.yssGetTableName("Tb_Para_Security")) + //判断 若bISSecurityPub为真，说明采用公共表
                " where FSecurityCode = " +
                dbl.sqlString(this.strSecurityCode);

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                bufShow.append(rs.getString("FSecurityCode")).append("\t");
                bufShow.append(rs.getString("FSecurityName")).append("\t");
                bufShow.append(rs.getDate("FStartDate")).append("\t");
                bufShow.append(rs.getString("FCatCode")).append("\t");
                bufShow.append(rs.getString("FSubCatCode")).append("\t");
                bufShow.append(rs.getString("FExchangeCode")).append("\t");
                bufShow.append(rs.getString("FMarketCode")).append("\t");
                bufShow.append(rs.getString("FTradeCury")).append("\t");
                bufShow.append(rs.getString("FISINCode")).append("\t");
                bufShow.append(rs.getString("FSectorCode")).append("\t");
                bufShow.append(rs.getString("FSYNTHETICCODE")).append("\t");//add by yanghaiming 20100426
                bufShow.append(rs.getString("FSettleDayType")).append("\t");
                bufShow.append(rs.getInt("FSettleDays")).append("\t");
                bufShow.append(rs.getDouble("FFactor")).append("\t");
                bufShow.append(rs.getDouble("FTotalShare")).append("\t");
                bufShow.append(rs.getDouble("FCurrentShare")).append("\t");
                bufShow.append(rs.getDouble("FHandAmount")).append("\t");
            
                bufShow.append(rs.getString("FIssueCorpCode")).append("\t");
                bufShow.append(rs.getString("FCusCatCode")).append("\t");
                bufShow.append(rs.getString("FHolidaysCode")).append("\t");
                bufShow.append(rs.getString("FExternalCode")).append("\t");
                bufShow.append(rs.getString("FDesc")).append("\t");
                bufShow.append(rs.getInt("FCheckState")).append("\t");
                bufShow.append(YssFun.getCheckStateName(rs.getInt("FCheckState"))).append("\t");
                bufShow.append(rs.getString("FCreator")).append("\t");
                bufShow.append(rs.getString("FcreateTime")).append("\t");
                bufShow.append(rs.getString("FCheckUser")).append("\t");
                bufShow.append(rs.getString("FCheckTime")).append("\t");
                bufShow.append(rs.getDouble("FFaceAmount")).append("\t");// #406 需参考3.0版本，在证券信息维护界面增加股票面值字段 lidaolong 20110112
                
                
                bufShow.append(rs.getString("FSecurityShortName")).append("\t"); //BugNo:0000429 edit by jc
                bufShow.append(rs.getString("FSecurityCorpName")).append("\t"); //把\null移到下面
                if (bIsSecurityPub) { //当通用参数值为真时才给它赋值 MS00131 QDV4建行2008年12月25日01_A
                    bufShow.append(rs.getString("FASSETGROUPCODE")).append("\t");
                } else {
                    bufShow.append(" ").append("\t"); // 其他情况赋空格值
                }
           
                bufShow.append("\null");
            }
        } catch (Exception e) {
            throw new YssException("获取证券信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            return bufShow.toString();
        }
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";

        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        
        try {
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_SCY_SDAYTYPE + "," +
                                        YssCons.YSS_FIX_INTORG + "," +
                                        YssCons.YSS_FIX_CALCINSWAY + "," +
                                        YssCons.YSS_FIX_QUOTEWAY + "," +
                                        YssCons.YSS_FIX_Level);

            sHeader = this.getListView1Headers();
            //modify by fangjiang 2010.11.13 bug 254 
            //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            //if (this.strIsOnlyColumns.equalsIgnoreCase("1") || strSql == "") {
            if (this.strIsOnlyColumns.equalsIgnoreCase("1") || strSql .equals("")) {
            //---end---
            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr()+ "\r\f" + "voc" + sVocStr;
            }
            //---add by songjie 2012.10.29 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            if(yssPageInationBean == null){//添加 空指针判断
            	yssPageInationBean = new YssPageInationBean();
            	yssPageInationBean.setYssPub(pub);
            }
            //---add by songjie 2012.10.29 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            yssPageInationBean.setsQuerySQL(strSql);
    		yssPageInationBean.setsTableName("security");
    		rs = dbl.openResultSet(yssPageInationBean);           
            /*if (strSql == "") {
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                    "\r\f" +
                    this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
            }
            rs = dbl.openResultSet(strSql);*/
            //------------------
            while (rs.next()) {
				//modified by liubo.Story #1770.
				//用tmpAssetGroupName变量保存组合群名称，然后使用基类重载的buildRowShowStr方法将组合群名称插入ListView的数据中
				//================================
				String tmpAssetGroupName = this.getGroupNameFromGroupCode(rs.getString("FASSETGROUPCODE"));	
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols(),tmpAssetGroupName)).
                    append(YssCons.YSS_LINESPLITMARK);
                //==============end==================

                setSecurityAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            //modify by fangjiang 2010.11.13 bug 254 
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" +
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr() + "\r\f" + "voc" + sVocStr;
            //----------------
        } catch (Exception e) {
            throw new YssException("获取证券信息维护出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }

    /**
     * 获取证券信息维护数据
     * 此方法已被修改
     * 修改时间：2008年2月20号
     * 修改人：单亮
     * 原方法的功能：查询出证券信息表的数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法不能显示回收站的数据
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        boolean bIsSecurityPub = false; //定义变量存放通用参数 QDV4建行2008年12月25日01_A  MS00131 byleeyu 20090204
        bIsSecurityPub = Boolean.valueOf( (String) pub.getHtPubParams().get(
            "security")).booleanValue(); //获取PUB中参数的值 MS00131
        strSql = "select y.* from " +
            "(select FSecurityCode,FCheckState" +
            (bIsSecurityPub ? ",FASSETGROUPCODE" : "") +
            ",max(FStartDate) as FStartDate from " + //若是采用新表，则要加上FASSETGROUPCODE主键，下面要用到 //MS00131
            (bIsSecurityPub ? "Tb_Base_Security" :
             pub.yssGetTableName("Tb_Para_Security")) + " " + //根据参数取具体的表 QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
            //--- MS00378 QDV4中保2009年04月10日02_B -获取所有的债券信息 -----------
            " where 1=1 " +
            (bIsSecurityPub ?
             " and (FASSETGROUPCODE=' ' or FASSETGROUPCODE like " +
             dbl.sqlString("%" + pub.getAssetGroupCode() + "%") + ")" : "") + //添加上查询条件 MS00131
            " group by FSecurityCode,FCheckState" +
            (bIsSecurityPub ? ",FASSETGROUPCODE" : "") + ") x join" + //因为上面有FASSETGROUPCODE主键，所以这里要按这个字段分组 MS00131
            //---------------end
            " (select a.*, m.FVocName as FSettleDayTypeValue, d.FCatName as FCatName, e.FSubCatName as FSubCatName," +
            " f.FExchangeName as FExchangeName, f.FRegionCode as FRegionCode, " +
            " f.FCountryCode as FCountryCode, f.FAreaCode as FAreaCode, " +
            " fa.FRegionName as FRegionName, fb.FCountryName as FCountryName," +
            " fc.FAreaName as FAreaName, g.FCurrencyName as FCurrencyName, " +
            " h.FSectorName as FSectorName, i.FHolidaysName as FHolidaysName," +
            " j.FCusCatName as FCusCatName,k.FAffCorpName as FIssueCorpName," +
            " ass.FAssetGroupName as FAssetGroupName," +		//added by liubo.Story #1770
            " b.FUserName as FCreatorName,c.FUserName as FCheckUserName,l.fsecclsname as FSYNTHETICNAME from " +//板块分类名称add by yanghaiming 20100426 
            (bIsSecurityPub ? pub.yssGetTableName("Tb_Base_Security"):
             pub.yssGetTableName("Tb_Para_Security")) + " a " + //根据参数取具体的表 QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
            " left join (select FSecClsCode,FSecClsName from " + pub.yssGetTableName("Tb_Para_SectorClass") + " where FCheckState = 1) l on a.fsyntheticcode = l.FSecClsCode" +//板块分类名称add by yanghaiming 20100426 
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d on a.FCatCode = d.FCatCode" +
            " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) e on a.FSubCatCode = e.FSubCatCode" +
            " left join (select FExchangeCode,FExchangeName,FRegionCode,FCountryCode,FAreaCode from Tb_Base_Exchange where FCheckState = 1) f on a.FExchangeCode = f.FExchangeCode" +
            " left join (select FRegionCode,FRegionName from Tb_Base_Region where FCheckState = 1) fa on f.FRegionCode = fa.FRegionCode" +
            " left join (select FCountryCode,FCountryName from Tb_Base_Country where FCheckState = 1) fb on f.FCountryCode = fb.FCountryCode" +
            " left join (select FAssetGroupCode,FAssetGroupName from tb_sys_AssetGroup) ass on a.FAssetGroupCode = ass.FAssetGroupCode" +	//added by liubo.Story #1770
            " left join (select FAreaCode,FAreaName from Tb_Base_Area where FCheckState = 1) fc on f.FAreaCode = fc.FAreaCode" +
            " left join (select FCuryCode,FCuryName as FCurrencyName from " +
            pub.yssGetTableName("Tb_Para_Currency") +
            " where FCheckState = 1) g on a.FTradeCury = g.FCuryCode" +
            " left join (select o.FSectorCode as FSectorCode,o.FSectorName as FSectorName from " +
            pub.yssGetTableName("Tb_Para_Sector") + " o join " +
            "(select FSectorCode,max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Sector") + " " +
            " where " +
            //--- MS00378 QDV4中保2009年04月10日02_B -获取所有的证券 信息 ------
//               " FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//               " and " +
            //-------------------------------------------------------------
            " FCheckState = 1 group by FSectorCode) p " +
            " on o.FSectorCode = p.FSectorCode and o.FStartDate = p.FStartDate) h on a.FSectorCode = h.FSectorCode" +
            " left join (select FHolidaysCode,FHolidaysName from Tb_Base_Holidays where FCheckState = 1) i on a.FHolidaysCode = i.FHolidaysCode" +
            " left join (select FCusCatCode,FCusCatName from " +
            pub.yssGetTableName("Tb_Para_CustomCategory") +
            " where FCheckState = 1) j on  a.FCusCatCode = j.FCusCatCode " +
            " left join (select FAffCorpCode,FAffCorpName from " +
            pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
            " where FCheckState = 1) k on a.FIssueCorpCode = k.FAffCorpCode " +
            //2007.11.29 修改 蒋锦 使用dbl.sqlToChar()处理"a.FSettleDayType"，否则在使用DB2数据库时会报数据类型错误
            " left join Tb_Fun_Vocabulary m on " +
            dbl.sqlToChar("a.FSettleDayType") +
            " = m.FVocCode and m.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_SCY_SDAYTYPE) +

            buildFilterSql() +
            (bIsSecurityPub ?
             (" and (a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE like " +
              dbl.sqlString("%" + pub.getAssetGroupCode() + "%") + ")") : "") + //添加上查询条件 MS00131
            ") y on x.FSecurityCode = y.FSecurityCode and x.FStartDate = y.FStartDate" +
            (bIsSecurityPub ? " and x.FASSETGROUPCODE=y.FASSETGROUPCODE " :
             " ") + //若上面采用新表的话，这里还要用到这个字段进行处理 MS00131
            " order by y.FStartDate desc, y.FCreateTime desc, y.FCheckTime desc, y.FCatCode, y.FCheckState";
        return this.builderListViewData(strSql);
    }
    
    
    /**
     * getListViewData4
     * 获取证券信息维护的全部数据
     * 这个方法不需要分页功能，modify by wangzuochun 2010.12.02  //----- modify by wangzuochun 2010.12.02  BUG #549  分页功能引起交易数据界面的交易证券查询不到100条以后的证券代码，出错
     * @return String
     */
    public String getListViewData4() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        String strSql = "";

        //StringBuffer bufShow = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        //StringBuffer bufAll = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        ResultSet rs = null;
        
        try {
            boolean bIsSecurityPub = false; //定义变量存放通用参数 QDV4建行2008年12月25日01_A  MS00131 byleeyu 20090204
            bIsSecurityPub = Boolean.valueOf( (String) pub.getHtPubParams().get(
                "security")).booleanValue(); //获取PUB中参数的值 MS00131
            strSql =
                "select a.*, m.FVocName as FSettleDayTypeValue, d.FCatName as FCatName, e.FSubCatName as FSubCatName," +
                " f.FExchangeName as FExchangeName, f.FRegionCode as FRegionCode, " +
                " f.FCountryCode as FCountryCode, f.FAreaCode as FAreaCode, " +
                " fa.FRegionName as FRegionName, fb.FCountryName as FCountryName," +
                " fc.FAreaName as FAreaName, g.FCurrencyName as FCurrencyName, " +
                " h.FSectorName as FSectorName, i.FHolidaysName as FHolidaysName," +
                " j.FCusCatName as FCusCatName,k.FAffCorpName as FIssueCorpName," +
                " b.FUserName as FCreatorName,c.FUserName as FCheckUserName,l.fsecclsname as FSYNTHETICNAME from " +
                (bIsSecurityPub ? "Tb_Base_Security" :
                 pub.yssGetTableName("Tb_Para_Security")) + " a " + //根据参数取具体的表 QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
                " left join (select FSecClsCode,FSecClsName from " + pub.yssGetTableName("Tb_Para_SectorClass") + " where FCheckState = 1) l on a.fsyntheticcode = l.FSecClsCode" +//板块分类名称add by yanghaiming 20100426 
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d on a.FCatCode = d.FCatCode" +
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) e on a.FSubCatCode = e.FSubCatCode" +
                " left join (select FExchangeCode,FExchangeName,FRegionCode,FCountryCode,FAreaCode from Tb_Base_Exchange where FCheckState = 1) f on a.FExchangeCode = f.FExchangeCode" +
                " left join (select FRegionCode,FRegionName from Tb_Base_Region where FCheckState = 1) fa on f.FRegionCode = fa.FRegionCode" +
                " left join (select FCountryCode,FCountryName from Tb_Base_Country where FCheckState = 1) fb on f.FCountryCode = fb.FCountryCode" +
                " left join (select FAreaCode,FAreaName from Tb_Base_Area where FCheckState = 1) fc on f.FAreaCode = fc.FAreaCode" +
                " left join (select FCuryCode,FCuryName as FCurrencyName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) g on a.FTradeCury = g.FCuryCode" +
                " left join (select o.FSectorCode as FSectorCode,o.FSectorName as FSectorName from " +
                pub.yssGetTableName("Tb_Para_Sector") + " o join " +
                "(select FSectorCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Sector") + " " +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSectorCode) p " +
                " on o.FSectorCode = p.FSectorCode and o.FStartDate = p.FStartDate) h on a.FSectorCode = h.FSectorCode" +
                " left join (select FHolidaysCode,FHolidaysName from Tb_Base_Holidays where FCheckState = 1) i on a.FHolidaysCode = i.FHolidaysCode" +
                " left join (select FCusCatCode,FCusCatName from " +
                pub.yssGetTableName("Tb_Para_CustomCategory") +
                " ) j on  a.FCusCatCode = j.FCusCatCode " +
                " left join (select FAffCorpCode,FAffCorpName from " +
                pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
                " ) k on a.FIssueCorpCode = k.FAffCorpCode " +
                " left join Tb_Fun_Vocabulary m on " +
                dbl.sqlToChar("a.FSettleDayType") +
                " = m.FVocCode and m.FVocTypeCode = " + //lzp modify 20080123
                dbl.sqlString(YssCons.YSS_SCY_SDAYTYPE) +
                buildFilterSql() +
                (bIsSecurityPub ?
                 (" and (a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE like " +
                  dbl.sqlString("%" + pub.getAssetGroupCode() + "%") + ") ") : " ") + //添加上查询条件 MS00131
                " order by a.FCatCode, a.FCheckState, a.FCreateTime desc";
        	
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_SCY_SDAYTYPE + "," +
                                        YssCons.YSS_FIX_INTORG + "," +
                                        YssCons.YSS_FIX_CALCINSWAY + "," +
                                        YssCons.YSS_FIX_QUOTEWAY + "," +
                                        YssCons.YSS_FIX_Level);

            sHeader = this.getListView1Headers();
            
            //add by songjie 20111.08.23 BUG 2207 QDV4赢时胜(测试)2011年6月30日07_B 添加点击 全部显示按钮查询的分页功能
            return this.builderListViewData(strSql);
//---delete by songjie 20111.08.23 BUG 2207 QDV4赢时胜(测试)2011年6月30日07_B 添加点击 全部显示按钮查询的分页功能 start---//
//    		rs = dbl.openResultSet(strSql);           
//            
//            while (rs.next()) {
//                bufShow.append(super.buildRowShowStr(rs,
//                    this.getListView1ShowCols())).
//                    append(YssCons.YSS_LINESPLITMARK);
//
//                setSecurityAttr(rs);
//                bufAll.append(this.buildRowStr()).append(YssCons.
//                    YSS_LINESPLITMARK);
//            }
//            if (bufShow.toString().length() > 2) {
//                sShowDataStr = bufShow.toString().substring(0,
//                    bufShow.toString().length() - 2);
//            }
//
//            if (bufAll.toString().length() > 2) {
//                sAllDataStr = bufAll.toString().substring(0,
//                    bufAll.toString().length() - 2);
//            }
//            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
//                "\r\f" +
//                this.getListView1ShowCols() + "\r\f" + this.buildRowStr() + "\r\f" + "voc" + sVocStr;
          //---delete by songjie 20111.08.23 BUG 2207 QDV4赢时胜(测试)2011年6月30日07_B 添加点击 全部显示按钮查询的分页功能 end---//            
        } catch (Exception e) {
            throw new YssException("获取证券信息维护出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData2
     * 获取已审核的证券信息维护数据
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            sHeader = "证券代码\t证券名称\t启用日期";
            if (this.strIsOnlyColumns.equalsIgnoreCase("0")) {
                conn.setAutoCommit(false);
                bTrans = true;

                if (dbl.yssTableExist("Tb_Temp_SecurityBak_" + pub.getUserCode())) {
                	/**shashijie ,2011-10-12 , STORY 1698*/
                    strSql = dbl.doOperSqlDrop("drop table Tb_Temp_SecurityBak_" +
                        pub.getUserCode());
                    /**end*/
                    dbl.executeSql(strSql);
                }

                strSql = "CREATE TABLE Tb_Temp_SecurityBak_" + pub.getUserCode() +
                    " AS ";
                String strSelect = "(select b.* from (select FSecurityCode,FCheckState as FChkState,max(FStartDate) " +
                    " as FStartDate from " +
                    pub.yssGetTableName("Tb_Para_Security") +
                    " where FStartDate <= " +
                    dbl.sqlDate(new java.util.Date()) +
                    " and FCheckState = 1 ";

                if (this.filterType != null &&
                    this.filterType.strSecurityCode.length() != 0) {
                    strSelect = strSelect + " and instr(FSecurityCode,'" +
                        filterType.strSecurityCode.replaceAll("'", "''") +
                        "',1)=1";
                }

                strSelect = strSelect +
                    " group by FSecurityCode,FCheckState) a " +
                    " join (select * from " +
                    pub.yssGetTableName("Tb_Para_Security") + ") b " +
                    " on a.FSecurityCode = b.FSecurityCode and a.FStartDate = b.FStartDate) ";
                //----------------2007.11.29 添加 DB2 的建表方法与 Oracle 不同----------------//
                if (dbl.getDBType() == YssCons.DB_DB2) {
                    dbl.executeSql(strSql + strSelect + " definition only");
                    dbl.executeSql("Insert Into Tb_Temp_SecurityBak_" +
                                   pub.getUserCode() + strSelect);
                }
                //-------------------------------------------------------------------------//
                else if (dbl.getDBType() == YssCons.DB_ORA) {
                    dbl.executeSql( (strSql + strSelect));
                } else {
                    throw new YssException("数据库访问错误。数据库类型不明或选择非系统兼容的数据库！");
                }

                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);

                strSql =
                    "select a.*, d.FCatName as FCatName, e.FSubCatName as FSubCatName," +
                    " f.FExchangeName as FExchangeName, f.FRegionCode as FRegionCode, " +
                    " f.FCountryCode as FCountryCode, f.FAreaCode as FAreaCode, " +
                    " fa.FRegionName as FRegionName, fb.FCountryName as FCountryName," +
                    " fc.FAreaName as FAreaName, g.FCurrencyName as FCurrencyName, " +
                    " h.FSectorName as FSectorName, i.FHolidaysName as FHolidaysName," +
                    " j.FCusCatName as FCusCatName,k.FAffCorpName as FIssueCorpName from " +
                    "Tb_Temp_SecurityBak_" + pub.getUserCode() +
                    " a " +
                    " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d on a.FCatCode = d.FCatCode" +
                    " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) e on a.FSubCatCode = e.FSubCatCode" +
                    " left join (select FExchangeCode,FExchangeName,FRegionCode,FCountryCode,FAreaCode from Tb_Base_Exchange where FCheckState = 1) f on a.FExchangeCode = f.FExchangeCode" +
                    " left join (select FRegionCode,FRegionName from Tb_Base_Region where FCheckState = 1) fa on f.FRegionCode = fa.FRegionCode" +
                    " left join (select FCountryCode,FCountryName from Tb_Base_Country where FCheckState = 1) fb on f.FCountryCode = fb.FCountryCode" +
                    " left join (select FAreaCode,FAreaName from Tb_Base_Area where FCheckState = 1) fc on f.FAreaCode = fc.FAreaCode" +
                    " left join (select FCuryCode,FCuryName as FCurrencyName from " +
                    pub.yssGetTableName("Tb_Para_Currency") +
                    " where FCheckState = 1) g on a.FTradeCury = g.FCuryCode" +
                    " left join (select o.FSectorCode as FSectorCode,o.FSectorName as FSectorName from " +
                    pub.yssGetTableName("Tb_Para_Sector") + " o join " +
                    "(select FSectorCode,max(FStartDate) as FStartDate from " +
                    pub.yssGetTableName("Tb_Para_Sector") + " " +
                    " where FStartDate <= " +
                    dbl.sqlDate(new java.util.Date()) +
                    " and FCheckState = 1 group by FSectorCode) p " +
                    " on o.FSectorCode = p.FSectorCode and o.FStartDate = p.FStartDate) h on a.FSectorCode = h.FSectorCode" +
                    " left join (select FHolidaysCode,FHolidaysName from Tb_Base_Holidays where FCheckState = 1) i on a.FHolidaysCode = i.FHolidaysCode" +
                    " left join (select FCusCatCode,FCusCatName from " +
                    pub.yssGetTableName("Tb_Para_CustomCategory") +
                    " ) j on  a.FCusCatCode = j.FCusCatCode " +
                    " left join (select FAffCorpCode,FAffCorpName from " +
                    pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
                    " ) k on a.FIssueCorpCode = k.FAffCorpCode " +
                    buildFilterSql() +
                    " order by a.FCatCode, a.FCheckState, a.FCreateTime desc";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FSecurityCode") + "")). //这里去掉trim()方法，防止字段中有空格 QDV4鹏华基金2008年12月31日01_B MS00133 by leeyu 20090209
                        append("\t");
                    bufShow.append( (rs.getString("FSecurityName") + "")). //这里去掉trim()方法，防止字段中有空格 QDV4鹏华基金2008年12月31日01_B MS00133 by leeyu 20090209
                        append("\t");
                    bufShow.append( (YssFun.formatDate(rs.getDate("FStartDate"),
                        YssCons.YSS_DATEFORMAT) + "").
                                   trim()).append(YssCons.YSS_LINESPLITMARK);

                    this.strSecurityCode = rs.getString("FSecurityCode") + "";
                    this.strSecurityName = rs.getString("FSecurityName") + "";
                    this.dtStartDate = rs.getDate("FStartDate");
                    this.strCategoryCode = rs.getString("FCatCode") + "";
                    this.strCategoryName = rs.getString("FCatName") + "";
                    this.strSubCategoryCode = rs.getString("FSubCatCode") + "";
                    this.strSubCategoryName = rs.getString("FSubCatName") + "";
                    this.strExchangeCode = rs.getString("FExchangeCode") + "";
                    this.strExchangeName = rs.getString("FExchangeName") + "";
                    this.strRegionCode = rs.getString("FRegionCode") + "";
                    this.strRegionName = rs.getString("FRegionName") + "";
                    this.strCountryCode = rs.getString("FCountryCode") + "";
                    this.strCountryName = rs.getString("FCountryName") + "";
                    this.strAreaCode = rs.getString("FAreaCode") + "";
                    this.strAreaName = rs.getString("FAreaName") + "";
                    this.strMarketCode = rs.getString("FMarketCode") + "";
                    this.strTradeCuryCode = rs.getString("FTradeCury") + "";
                    this.strTradeCuryName = rs.getString("FCurrencyName") + "";
                    this.strSectorCode = rs.getString("FSectorCode") + "";
                    this.strSectorName = rs.getString("FSectorName") + "";
                    this.strSettleDayType = rs.getString("FSettleDayType") + "";
                    this.intSettleDays = rs.getInt("FSettleDays");
                    this.dblFactor = rs.getDouble("FFactor");
                    this.dblTotalShare = rs.getBigDecimal("FTotalShare");
                    this.dblCurrentShare = rs.getBigDecimal("FCurrentShare");
                    this.dblHandAmount = rs.getBigDecimal("FHandAmount");
                    this.strIssueCorpCode = rs.getString("FIssueCorpCode") + "";
                    this.strIssueCorpName = rs.getString("FIssueCorpName") + "";
                    this.strCusCatCode = rs.getString("FCusCatCode") + "";
                    this.strCusCatName = rs.getString("FCusCatName") + "";
                    this.strHolidaysCode = rs.getString("FHolidaysCode") + "";
                    this.strHolidaysName = rs.getString("FHolidaysName") + "";
                    this.strExternalCode = rs.getString("FExternalCode") + "";
                    this.strIsinCode = rs.getString("FISINCode") + ""; //add by xuqiji 20090604:QDV4建行2009年5月15日01_A  MS00466 监控模块针对监控指标和监控所需数据来源需求开发
                    this.strDesc = rs.getString("FDesc") + "";
                    this.checkStateId = rs.getInt("FCheckState");
                    this.checkStateName = YssFun.getCheckStateName(rs.getInt(
                        "FCheckState"));
                    this.creatorCode = rs.getString("FCreator") + "";
                    this.creatorTime = rs.getString("FcreateTime") + "";
                    this.checkUserCode = rs.getString("FCheckUser") + "";
                    this.checkTime = rs.getString("FCheckTime") + "";
                    bufAll.append(this.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }
            }

            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取证券信息维护数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * getListViewData3
     *用于订单制作中显示证券信息
     * @return String
     */
    public String getListViewData3() throws YssException {

    	if(pub.isBrown()==true)//update by guolongchao 20110920 STORY 1285 导入数据后浏览数据
    	{
    		return getListViewData1();
    	}
    	else
           return builderListViewData("");
    }

    /**
     * getSetting
     *
     * @return IParaSetting
     */
    public IDataSetting getSetting() {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FSecurityCode = " +
                dbl.sqlString(this.strSecurityCode) +
                " and FStartDate = (select max(FStartDate) from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FSecurityCode = " +
                dbl.sqlString(this.strSecurityCode) + ")";

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.strSecurityCode = rs.getString("FSecurityCode") + "";
                this.strSecurityName = rs.getString("FSecurityName") + "";
                this.strSecurityShortName = rs.getString("FSecurityShortName") + "";
                this.strSecurityCorpName = rs.getString("FSecurityCorpName") + "";
                this.dtStartDate = rs.getDate("FStartDate");
                this.strCategoryCode = rs.getString("FCatCode") + "";
                this.strSubCategoryCode = rs.getString("FSubCatCode") + "";
                this.strExchangeCode = rs.getString("FExchangeCode") + "";
                this.strMarketCode = rs.getString("FMarketCode") + "";
                this.strTradeCuryCode = rs.getString("FTradeCury") + "";
                this.strSectorCode = rs.getString("FSectorCode") + "";
                this.strSyntheticCode = rs.getString("FSYNTHETICCODE") + "";//add by yanghaiming 20100426
                this.strSettleDayType = rs.getString("FSettleDayType") + "";
                this.intSettleDays = rs.getInt("FSettleDays");
                this.dblFactor = rs.getDouble("FFactor");
                this.dblTotalShare = rs.getBigDecimal("FTotalShare");
                this.dblCurrentShare = rs.getBigDecimal("FCurrentShare");
                this.dblHandAmount = rs.getBigDecimal("FHandAmount");
                this.strIssueCorpCode = rs.getString("FIssueCorpCode") + "";
                this.strCusCatCode = rs.getString("FCusCatCode") + "";
                this.strHolidaysCode = rs.getString("FHolidaysCode") + "";
                this.strExternalCode = rs.getString("FExternalCode") + "";
                this.strDesc = rs.getString("FDesc") + "";
                this.checkStateId = rs.getInt("FCheckState");
                this.strTradeCuryCode = rs.getString("FTradeCury");
                this.checkStateName = YssFun.getCheckStateName(rs.getInt("FCheckState"));
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FcreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime") + "";
            }
        } catch (Exception e) {
            throw new YssException("获取证券信息出错!", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            return null;
        }
    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
        return "";
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        boolean bIsSecurityPub = false; //定义变量存放通用参数 QDV4建行2008年12月25日01_A  MS00131 byleeyu 20090204
        ResultSet rs = null;		//added by liubo.Story #1770
        Connection conn = dbl.loadConnection();
        try {
        	//modified by liubo.Story #1770
        	//该方法会解析组合群代码的明细，根据组合群明细分别往对应的组合群中插入数据

        	String[] strGroupCode = null;

        	strGroupCode = ("".equals(this.sAssetGroupCode.trim()) ? pub.getAssetGroupCode() : this.sAssetGroupCode).split(",");
        	for (int i = 0; i < strGroupCode.length; i++)
        	{
	            bIsSecurityPub = Boolean.valueOf( (String) pub.getHtPubParams().get(
	                "security")).booleanValue(); //获取PUB中参数的值 MS00131
				//2.添加维护管理人、运作方式的处理 MS00619 QDV4银华2009年08月06日01_A add by pengjinggang 2009_9_1
	            //#406 需参考3.0版本，在证券信息维护界面增加股票面值字段  ldaolong 20110112 
	            
	            //added by liubo.Story #1770
	            //在跨组合群插入数据的时候，先确认目标组合群的证券信息表中是否存在相同证券代码的数据
	            //===================================
	            if (checkSecurityCode(strGroupCode[i],this.strSecurityCode))
	            {
	            	continue;
	            }
	            //===================================
	            
	            strSql = "insert into " +
	                (bIsSecurityPub ? "Tb_Base_Security" :
	                //modified by liubo.Story #1770.根据组合群代码取证券表名
	                //--------------------------------------
	                 //pub.yssGetTableName("Tb_Para_Security")) + //判断若是这个参数为真取公共表 QDV4建行2008年12月25日01_A  MS00131 byleeyu 20090204
	                "Tb_" + strGroupCode[i] + "_Para_Security") +
	                //--------------------end------------------
	                "" +
	                "(FSECURITYCODE,FSECURITYNAME,FSECURITYSHORTNAME,FSECURITYCORPNAME," + //BugNo:0000429 edit by jc
	                "FSTARTDATE,FCATCODE,FSUBCATCODE,FEXCHANGECODE,FMARKETCODE,FEXTERNALCODE," +
	                "FTRADECURY,FSETTLEDAYTYPE,FHOLIDAYSCODE,FSETTLEDAYS,FSECTORCODE,FSYNTHETICCODE,FTOTALSHARE," +
	                "FMaintainMgr," +
	                "FOperStyle," +
	                "FAssetGroupCode," +	//added by liubo.Story #1770
	                "FFACEAMOUNT,"+//#406 需参考3.0版本，在证券信息维护界面增加股票面值字段  lidalong 20110112
	                
	                "FNormScale,FBSDifferenceOver ,FReplaceOver,"+ //add by zhangjun ETF联接基金
	                "FMtReplaceOver,"+//赎回必须现金替代款的结转日 story 2727 add by zhouwei 20120619 ETF链接基金
	                "FCURRENTSHARE,FFACTOR,FHANDAMOUNT,FIssueCorpCode,FCusCatCode,FDesc," +
	                "FCheckState,FCreator,FCreateTime,FCheckUser,FISINCode" +
	                (bIsSecurityPub ? ",FASSETGROUPCODE" : "") +
	                ") values(" + //根据上述表的情况，来决定这个字段 MS00131
	                dbl.sqlString(this.strSecurityCode) + "," +
	                dbl.sqlString(this.strSecurityName) + "," +
	                dbl.sqlString(this.strSecurityShortName) + "," +
	                dbl.sqlString(this.strSecurityCorpName) + "," +
	                dbl.sqlDate(this.dtStartDate) + "," +
	                dbl.sqlString(this.strCategoryCode) + "," +
	                dbl.sqlString(this.strSubCategoryCode) + "," +
	                dbl.sqlString(this.strExchangeCode) + "," +
	                dbl.sqlString(this.strMarketCode) + "," +
	                dbl.sqlString(this.strExternalCode) + "," +
	                dbl.sqlString(this.strTradeCuryCode) + "," +
	                this.strSettleDayType + "," +
	                dbl.sqlString(this.strHolidaysCode) + "," +
	                this.intSettleDays + "," +
	                dbl.sqlString(this.strSectorCode) + "," +
	                /**shashijie 2012-7-17 BUG 5024 */
	                //dbl.sqlString(strGroupCode[i]) + "," +//add by yanghaiming 20100426
	                dbl.sqlString(strSyntheticCode) + "," +
	                /**end*/
	                this.dblTotalShare + "," +
	                dbl.sqlString(this.sMaintainMgr) + "," +    //维护管理人
	                dbl.sqlString(this.sOperStyle) + "," +      //运作方式
	                dbl.sqlString(this.sAssetGroupCode) + "," +	//added by liubo.Story #1770.组合群代码
	                this.db1FaceAmount + "," + //股票面值 需求406 南方东英\2010年12月\14日
	                
	                this.dblMinShare +","+ //add by zhangjun 2012-04-27 ETF联接基金
	                this.getsSGSHCashBalance() + ","+//add by zhangjun 2012-04-27 ETF联接基金
	                this.getsSGDealReplace() +","+  //add by zhangjun 2012-04-27 ETF联接基金
	                this.mtReplaceOverDay+","+//赎回必须现金替代款的结转日 story 2727 add by zhouwei 20120619 ETF链接基金	
	                this.dblCurrentShare + "," +
	                this.dblFactor + "," +
	                this.dblHandAmount + "," +
	                dbl.sqlString(this.strIssueCorpCode) + "," +
	                dbl.sqlString(this.strCusCatCode) + "," +
	                dbl.sqlString(this.strDesc) + "," +
	                (pub.getSysCheckState() ? "0" : "1") + "," +
	                dbl.sqlString(this.creatorCode) + "," +
	                dbl.sqlString(this.creatorTime) + "," +
	                (pub.getSysCheckState() ? "' '" :
	                 dbl.sqlString(this.creatorCode)) + "," +
	                dbl.sqlString(this.strIsinCode) +
	                (bIsSecurityPub ?
	                 ("," +
	                  dbl.sqlString(sAssetGroupCode.trim().length() >
	                                0 ? sAssetGroupCode : " ")) : "") + //根据表的情况来决定是否赋值 MS00131
	                ")";
	
	            conn.setAutoCommit(false);
	            bTrans = true;
	            dbl.executeSql(strSql);
            	dbl.closeResultSetFinal(rs);
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
        	}
        	//=============end===================
        }

        catch (Exception e) {
            throw new YssException("增加证券信息出错!", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        	dbl.closeResultSetFinal(rs);
        }

        return null;
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        boolean bIsSecurityPub = false; //定义变量存放通用参数 QDV4建行2008年12月25日01_A  MS00131 byleeyu 20090204
        ResultSet rs = null;
        Connection conn = dbl.loadConnection();
        String[] sOldAssetGroup = null;
        String[] sNewAssetGroup = null;
		 String sTmpAssetGroupCode = "";
        try {
        	//modified by liubo.Story #1770
        	//该方法会解析组合群代码的明细，根据组合群明细分别往对应的组合群中插入数据
        	if (!sOldAssetGroupCode.equals(sAssetGroupCode))
       	 	{
		       	sOldAssetGroup = ("".equals(sOldAssetGroupCode.trim()) ? pub.getAssetGroupCode() : sOldAssetGroupCode).split(",");
		       	sNewAssetGroup = ("".equals(sAssetGroupCode.trim()) ? pub.getAssetGroupCode() : sAssetGroupCode).split(",");
       		 
	       		 for (int i = 0;i < sOldAssetGroup.length;i++)
	       		 {
		        	strSql = "delete from " +
		            "Tb_" + sOldAssetGroup[i] + "_Para_Security" + 
		            " where FSECURITYCODE = " +
		            dbl.sqlString(this.strOldSecurityCode) +
		            " and FStartDate = " +
		            dbl.sqlDate(this.dtOldStartDate) +
		            (bIsSecurityPub ?
		             (" and FASSETGROUPCODE=" +
		              dbl.
		              sqlString(sOldAssetGroupCode.trim().length() > 0 ?
		                        sOldAssetGroupCode : " ")) : " "); 
		        	dbl.executeSql(strSql);
	       		 }
	       		addSetting();
       	 	}
        	else
	        {
        		/**shashijie 2012-7-17 BUG 5024 */
        		String groupCode = "";
        		if ("".equals(this.sAssetGroupCode.trim()))
	        	{
        			groupCode = pub.getAssetGroupCode();
	        	}else {
	        		groupCode = this.sAssetGroupCode.trim();
				}
	        	String[] strGroupCode = groupCode.split(",");
	        	/*if ("".equals(this.sAssetGroupCode.trim()))
	        	{
	        		this.sAssetGroupCode = pub.getAssetGroupCode();
	        	}
	        	String[] strGroupCode = this.sAssetGroupCode.split(",");*/
	        	/**end*/
	        	
	        	for (int i = 0; i < strGroupCode.length; i++)
	        	{	            //added by liubo.Story #1770
		            bIsSecurityPub = Boolean.valueOf( (String) pub.getHtPubParams().get(
		                "security")).booleanValue(); //获取PUB中参数的值 MS00131
		            strSql = "update " +
		                (bIsSecurityPub ? "Tb_Base_Security" :
		                	//modified by liubo.Story #1770.根据组合群代码取证券表名
			                //--------------------------------------
			                 //pub.yssGetTableName("Tb_Para_Security")) + //判断若是这个参数为真取公共表 QDV4建行2008年12月25日01_A  MS00131 byleeyu 20090204
			                "Tb_" + strGroupCode[i] + "_Para_Security") +
			                //--------------------end------------------
		                " set " +
		                "FSECURITYCODE = " + dbl.sqlString(this.strSecurityCode) +
		                ", FSECURITYNAME = " + dbl.sqlString(this.strSecurityName) +
		                ", FSECURITYSHORTNAME = " +
		                dbl.sqlString(this.strSecurityShortName) +
		                ", FSECURITYCORPNAME = " +
		                dbl.sqlString(this.strSecurityCorpName) +
		                ", FSTARTDATE = " + dbl.sqlDate(this.dtStartDate) +
		                ", FCATCODE = " + dbl.sqlString(this.strCategoryCode) +
		                ", FSUBCATCODE = " + dbl.sqlString(this.strSubCategoryCode) +
		                ", FEXCHANGECODE = " + dbl.sqlString(this.strExchangeCode) +
		                ", FISINCODE = " + dbl.sqlString(this.strIsinCode) +
		                ", FMARKETCODE = " + dbl.sqlString(this.strMarketCode) +
		                ", FEXTERNALCODE = " + dbl.sqlString(this.strExternalCode) +
		                ", FTRADECURY = " + dbl.sqlString(this.strTradeCuryCode) +
		                ", FSETTLEDAYTYPE = " + this.strSettleDayType +
		                ", FHOLIDAYSCODE = " + dbl.sqlString(this.strHolidaysCode) +
		                ", FSETTLEDAYS = " + this.intSettleDays +
		                ", FSECTORCODE = " + dbl.sqlString(this.strSectorCode) +
		                ", FSYNTHETICCODE = " + dbl.sqlString(this.strSyntheticCode) +//板块分类代码add by yanghaiming 20100426 
		                ", FTOTALSHARE = " + this.dblTotalShare +
		                ", FCURRENTSHARE = " + this.dblCurrentShare +
		                ", FFACTOR = " + this.dblFactor +
		                ", FFACEAMOUNT = " + this.db1FaceAmount +//股票面值  #406 南方东英\2010年12月\14日  需参考3.0版本，在证券信息维护界面增加股票面值字段 lidaolong 20110112
		                
		                //add by zhangjun 2012-04-27 ETF联接基金
		                ",FNormScale = " + this.dblMinShare +
		                ",FBSDifferenceOver = " + this.getsSGSHCashBalance()+
		                ",FReplaceOver = " + this.getsSGDealReplace() +
		                //add by zhangjun 2012-04-27 ETF联接基金
		                ",FMtReplaceOver ="+this.mtReplaceOverDay+//赎回必须现金替代款的结转日 story 2727 add by zhouwei 20120619 ETF链接基金	
		                ", FHANDAMOUNT = " + this.dblHandAmount +
		                ", FMaintainMgr= " + dbl.sqlString(this.sMaintainMgr) + //维护管理人 MS00619 QDV4银华2009年08月06日01_A add by pengjinggang 2009_9_1
		                ", FOperStyle = " + dbl.sqlString(this.sOperStyle) +    //运作方式   MS00619 QDV4银华2009年08月06日01_A add by pengjinggang 2009_9_1
		                ", FIssueCorpCode = " +
		                dbl.sqlString(this.strIssueCorpCode) +
		                ", FCusCatCode = " + dbl.sqlString(this.strCusCatCode) +
		                ", FDesc = " + dbl.sqlString(this.strDesc) +
		                ", FCheckState = " + (pub.getSysCheckState() ? "0" : "1") +
		                ", FCreator = " + dbl.sqlString(this.creatorCode) +
		                ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
		                ", FCheckUser = " +
		                (pub.getSysCheckState() ? "' '" :
		                 dbl.sqlString(this.creatorCode)) +
	//	                (bIsSecurityPub ?
	//	                 ",FASSETGROUPCODE=" +
	//	                 dbl.sqlString( (sAssetGroupCode.trim().length() > 0 ?
	//	                                 sAssetGroupCode : " ")) : " ") + //若变量值为真，说明采用新表，必须给它的字段赋值 MS00131
		                 ",FAssetGroupCode = " + dbl.sqlString(this.sAssetGroupCode) +	//added by liubo.Story #1770
		                " where FSECURITYCODE = " +
		                dbl.sqlString(this.strOldSecurityCode) +
		                " and FSTARTDATE = " + dbl.sqlDate(this.dtOldStartDate) +
		                (bIsSecurityPub ?
		                 (" and FASSETGROUPCODE=" +
		                  dbl.sqlString(sOldAssetGroupCode.trim().length() > 0 ?
		                                sOldAssetGroupCode : " ")) : " "); //若变量为真，FASSETGROUPCODE字段为主键，还需处理 MS00131
		
		            conn.setAutoCommit(false);
		            bTrans = true;
		            dbl.executeSql(strSql);
		            strSql = "update " + pub.yssGetTableName("Tb_Para_FixInterest") +
		                " set FSECURITYCODE = " +
		                dbl.sqlString(this.strSecurityCode) +
		                " where FSECURITYCODE = " +
		                dbl.sqlString(this.strOldSecurityCode);
		            dbl.executeSql(strSql);
	
	            	dbl.closeResultSetFinal(rs);
		            conn.commit();
		            bTrans = false;
		            conn.setAutoCommit(true);
	        	}
	        }
	        
        	//==============end=====================
        }
        catch (Exception e) {
            throw new YssException("修改证券信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        	dbl.closeResultSetFinal(rs);
        }

        return null;

    }

    /**
     * 删除证券信息维护的数据，即放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        boolean bIsSecurityPub = false; //定义变量存放通用参数 QDV4建行2008年12月25日01_A  MS00131 byleeyu 20090204
        Connection conn = dbl.loadConnection();
        try {
        	//modified by liubo.Story #1770
        	//该方法会解析组合群代码的明细，根据组合群明细分别往对应的组合群中插入数据
        	if ("".equals(this.sAssetGroupCode.trim()))
        	{
        		this.sAssetGroupCode = pub.getAssetGroupCode();
        	}
        	String[] strGroupCode = this.sAssetGroupCode.split(",");
        	for (int i = 0; i < strGroupCode.length; i++)
        	{
	            bIsSecurityPub = Boolean.valueOf( (String) pub.getHtPubParams().get(
	                "security")).booleanValue(); //获取PUB中参数的值 MS00131
	            strSql = "update " +
	                (bIsSecurityPub ? "Tb_Base_Security" :
	                	//modified by liubo.Story #1770.根据组合群代码取证券表名
		                //--------------------------------------
		                 //pub.yssGetTableName("Tb_Para_Security")) + //判断若是这个参数为真取公共表 QDV4建行2008年12月25日01_A  MS00131 byleeyu 20090204
		                "Tb_" + strGroupCode[i] + "_Para_Security") +
		                //--------------------end------------------
	                " set FCheckState = " + this.checkStateId +
	                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	                ", FCheckTime = '" +
	                YssFun.formatDatetime(new java.util.Date()) +
	                "' where FSECURITYCODE = " +
	                dbl.sqlString(this.strSecurityCode) +
	                " and FStartDate = " + dbl.sqlDate(this.dtStartDate) +
	                (bIsSecurityPub ?
	                 (" and FASSETGROUPCODE=" +
	                  dbl.sqlString(sAssetGroupCode.trim().length() > 0 ?
	                                sAssetGroupCode : " ")) : " "); //若参数为真，采用公共表的主键 MS00131
	
	            conn.setAutoCommit(false);
	            bTrans = true;
	            dbl.executeSql(strSql);
	            strSql = "update " + pub.yssGetTableName("Tb_Para_FixInterest") +
	                " set FCheckState = " + this.checkStateId +
	                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	                ", FCheckTime = '" +
	                YssFun.formatDatetime(new java.util.Date()) +
	                "' where FSECURITYCODE = " +
	                dbl.sqlString(this.strSecurityCode);
	            dbl.executeSql(strSql);
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
        	}
        }

        catch (Exception e) {
            throw new YssException("删除证券信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**修改时间：2008年3月20号
     *  修改人：单亮
     *  原方法功能：只能处理期间连接的审核和未审核的单条信息。
     *  新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     */
    public void checkSetting() throws YssException {
        //修改后的代码
        //-------------------begin
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        //add by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A
        String[] assetGroupCodes = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            boolean bIsSecurityPub = false; //定义变量存放通用参数 QDV4建行2008年12月25日01_A  MS00131 byleeyu 20090204
            bIsSecurityPub = Boolean.valueOf( (String) pub.getHtPubParams().get(
                "security")).booleanValue(); //获取PUB中参数的值 MS00131
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]);
					
					//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
					// 若组合群代码不为空
					if (this.sAssetGroupCode.trim().length() > 0) {//添加跨组合群还原功能
						assetGroupCodes = this.sAssetGroupCode.trim().split(",");
						for (int j = 0; j < assetGroupCodes.length; j++) {
							strSql = "update " + (bIsSecurityPub ? "Tb_Base_Security" : "Tb_" + assetGroupCodes[j]+ "_Para_Security")
									+ " set FCheckState = " + this.checkStateId + ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
									+ ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))
									+ " where FSECURITYCODE = " + dbl.sqlString(this.strSecurityCode)
									+ " and FStartDate = " + dbl.sqlDate(this.dtStartDate)
									+ (bIsSecurityPub ? (" and FASSETGROUPCODE=" 
									+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode : " ")) : " "); 

							dbl.executeSql(strSql);
							
							if (this.strCategoryCode.equalsIgnoreCase("FI")) { 
								strSql = " update Tb_" + assetGroupCodes[j] + "_Para_FixInterest" + " set FcheckState = "
										+ this.checkStateId + ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
										+ ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))
										+ " where FSECURITYCODE = " + dbl.sqlString(this.strSecurityCode);
								dbl.executeSql(strSql);
							}
						}
					} else {// 若组合群代码为空
						strSql = "update "
								+ (bIsSecurityPub ? "Tb_Base_Security" : pub.yssGetTableName("Tb_Para_Security"))
								+ // 根据通用参数决定是采用哪张表的 MS00131
								" set FCheckState = " + this.checkStateId + ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
								+ ", FCheckTime = "+ dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))
								+ " where FSECURITYCODE = " + dbl.sqlString(this.strSecurityCode)
								+ " and FStartDate = " + dbl.sqlDate(this.dtStartDate)
								+ (bIsSecurityPub ? (" and FASSETGROUPCODE=" 
										// 根据上表决定是否用新表的这个字段做为参数  MS00131
								+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode : " ")) : " "); 
																						
						dbl.executeSql(strSql);
						
						if (this.strCategoryCode.equalsIgnoreCase("FI")) { // 增加对债券信息审核的同步 by leeyu 080606

							strSql = " update " + pub.yssGetTableName("Tb_Para_FixInterest") + " set FcheckState = "
									+ this.checkStateId + ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) 
									+ ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) 
									+ " where FSECURITYCODE = " + dbl.sqlString(this.strSecurityCode);
							dbl.executeSql(strSql);
						}
					}
					//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
				}
				//如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
			//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            } else if (strSecurityCode != null && (!strSecurityCode.equalsIgnoreCase(""))) {
            	//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
				if (this.sAssetGroupCode.trim().length() > 0) {//添加跨组合群还原功能
					assetGroupCodes = this.sAssetGroupCode.trim().split(",");
					
					for(int j = 0; j < assetGroupCodes.length; j++){
						strSql = "update "
								+ (bIsSecurityPub ? "Tb_Base_Security" : "Tb_" + assetGroupCodes[j] + "_Para_Security")
								+ // 根据通用参数决定是采用哪张表的 MS00131
								" set FCheckState = " + this.checkStateId + ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
								+ ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))
								+ " where FSECURITYCODE = "+ dbl.sqlString(this.strSecurityCode)
								+ " and FStartDate = " + dbl.sqlDate(this.dtStartDate)
								+ (bIsSecurityPub ? (" and FASSETGROUPCODE=" 
								// 根据上表决定是否用新表的这个字段做为参数  MS00131
								+ dbl.sqlString(sAssetGroupCode.trim() .length() > 0 ? sAssetGroupCode : " ")) : " "); 

						// 执行sql语句
						dbl.executeSql(strSql);

						if (this.strCategoryCode.equalsIgnoreCase("FI")) { // 增加对债券信息审核的同步 by leeyu 080606
							strSql = " update Tb_" + assetGroupCodes[j] + "_Para_FixInterest set FcheckState="
									+ this.checkStateId + ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) + ", FCheckTime = "
									+ dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + " where FSECURITYCODE = "
									+ dbl.sqlString(this.strSecurityCode);
							
							dbl.executeSql(strSql);
						}
					}
				} else {
					strSql = "update "
							+ (bIsSecurityPub ? "Tb_Base_Security" : pub.yssGetTableName("Tb_Para_Security"))
							+ // 根据通用参数决定是采用哪张表的 MS00131
							" set FCheckState = " + this.checkStateId + ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
							+ ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))
							+ " where FSECURITYCODE = " + dbl.sqlString(this.strSecurityCode)
							+ " and FStartDate = " + dbl.sqlDate(this.dtStartDate)
							+ (bIsSecurityPub ? (" and FASSETGROUPCODE=" 
							// 根据上表决定是否用新表的这个字段做为参数  MS00131
							+ dbl.sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode : " ")) : " "); 

					// 执行sql语句
					dbl.executeSql(strSql);

					if (this.strCategoryCode.equalsIgnoreCase("FI")) { // 增加对债券信息审核的同步 by leeyu 080606
						strSql = " update " + pub.yssGetTableName("Tb_Para_FixInterest") + " set FcheckState="
								+ this.checkStateId + ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) + ", FCheckTime = "
								+ dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + " where FSECURITYCODE = "
								+ dbl.sqlString(this.strSecurityCode);
						dbl.executeSql(strSql);
					}
				}
				//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
			}
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核证券信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //-------------------end
    }

    /**
     * 设置证券品种其他属性信息
     * @param btOper byte 操作类型
     * @param strCatCode String 其他属性类型
     * @param strSecurityCode String 证券品种代码
     * @param dtStartDate java.util.Date 启用日期
     * @throws YssException
     */
    public void saveOtherData(byte btOper, String strCatCode,
                              String strSecurityCode,
                              java.util.Date dtStartDate) throws
        YssException {
        String strSql = "";
//     boolean bTrans = false;
//     Connection conn = dbl.loadConnection();
        String strTableName = "";
        if (strCatCode.equalsIgnoreCase("FI")) { //债券信息
            strTableName = pub.yssGetTableName("Tb_Para_FixInterest");
        } 
//        else if (strCatCode.equalsIgnoreCase("TR")) { //基金
//        }
        if (strTableName.length() == 0) {
            return;
        }
        try {
            if (btOper == YssCons.OP_ADD) {
                return;
            } else if (btOper == YssCons.OP_EDIT) {
                return;
            } else if (btOper == YssCons.OP_DEL) {
                strSql = "update " + strTableName + " set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FSecurityCode = " +
                    dbl.sqlString(strSecurityCode) +
                    " and FStartDate = " + dbl.sqlDate(dtStartDate);

            } else if (btOper == YssCons.OP_AUDIT) {
                strSql = "update " + strTableName + " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = '" +
                    pub.getUserCode() +
                    "', FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FSecurityCode = " +
                    dbl.sqlString(strSecurityCode) +
                    " and FStartDate = " + dbl.sqlDate(dtStartDate);
            }
//        conn.setAutoCommit(false);
//        bTrans = true;
            dbl.executeSql(strSql);
//        conn.commit();
//        bTrans = false;
//        conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("设置证券品种其他属性信息出错", e);
        } finally {
//        dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 为各项变量赋值
     *
     */
    public void setSecurityAttr(ResultSet rs) throws SQLException {
        this.strSecurityCode = rs.getString("FSecurityCode") + "";
        this.strSecurityName = rs.getString("FSecurityName") + "";
        this.strSecurityShortName = rs.getString("FSecurityShortName") + ""; //BugNo:0000429 edit by jc
        this.strSecurityCorpName = rs.getString("FSecurityCorpName") + ""; //BugNo:0000429 edit by jc
        this.dtStartDate = rs.getDate("FStartDate");
        this.strCategoryCode = rs.getString("FCatCode") + "";
        this.strCategoryName = rs.getString("FCatName") + "";
        this.strSubCategoryCode = rs.getString("FSubCatCode") + "";
        this.strSubCategoryName = rs.getString("FSubCatName") + "";
        this.strExchangeCode = rs.getString("FExchangeCode") + "";
        this.strExchangeName = rs.getString("FExchangeName") + "";
        this.strRegionCode = rs.getString("FRegionCode") + "";
        this.strRegionName = rs.getString("FRegionName") + "";
        this.strCountryCode = rs.getString("FCountryCode") + "";
        this.strCountryName = rs.getString("FCountryName") + "";
        this.strAreaCode = rs.getString("FAreaCode") + "";
        this.strAreaName = rs.getString("FAreaName") + "";
        this.strMarketCode = rs.getString("FMarketCode") + "";
        this.strTradeCuryCode = rs.getString("FTradeCury") + "";
        this.strTradeCuryName = rs.getString("FCurrencyName") + "";
        this.strSectorCode = rs.getString("FSectorCode") + "";
        this.strSectorName = rs.getString("FSectorName") + "";
        this.strSyntheticCode = rs.getString("FSYNTHETICCODE") + "";//板块分类代码add by yanghaiming 20100426 
        this.strSyntheticName = rs.getString("FSYNTHETICNAME") + "";
        this.strSettleDayType = rs.getString("FSettleDayType") + "";
        this.intSettleDays = rs.getInt("FSettleDays");
        this.dblFactor = rs.getDouble("FFactor");
        this.dblTotalShare = rs.getBigDecimal("FTotalShare");
        this.dblCurrentShare = rs.getBigDecimal("FCurrentShare");
        this.dblHandAmount = rs.getBigDecimal("FHandAmount");
        this.strIssueCorpCode = rs.getString("FIssueCorpCode") + "";
        this.strIssueCorpName = rs.getString("FIssueCorpName") + "";
        this.strCusCatCode = rs.getString("FCusCatCode") + "";
        this.strCusCatName = rs.getString("FCusCatName") + "";
        this.strHolidaysCode = rs.getString("FHolidaysCode") + "";
        this.strHolidaysName = rs.getString("FHolidaysName") + "";
        this.strExternalCode = rs.getString("FExternalCode") + "";
        this.strDesc = rs.getString("FDesc") + "";
        this.settleDayTypeValue = rs.getString("FSettleDayTypeValue") + "";
        this.strIsinCode = rs.getString("FISINCode") + "";
        if ( ( (String) pub.getHtPubParams().get("security")).equalsIgnoreCase(
            "true")) { //当通用参数传过来的值为真时再给这个变量赋值
            this.sAssetGroupCode = rs.getString("FASSETGROUPCODE"); //QDV4建行2008年12月25日01_A  MS00131 by leeyu 20090204
        }
        this.sMaintainMgr = rs.getString("FMaintainMgr");   //维护管理人MS00619 QDV4银华2009年08月06日01_A add by pengjinggang 2009_9_1
        this.sOperStyle = rs.getString("FOperStyle");       //运作方式MS00619 QDV4银华2009年08月06日01_A add by pengjinggang 2009_9_1
        this.db1FaceAmount =rs.getBigDecimal("FFaceAmount");// #406 南方东英\2010年12月\14日  需参考3.0版本，在证券信息维护界面增加股票面值字段 lidaolong 20110112
        this.sAssetGroupCode = rs.getString("FAssetGroupCode");		//added by liubo.Story #1770
        
        this.dblMinShare = rs.getString("FNormScale") == null ? BigDecimal.valueOf(0) : rs.getBigDecimal("FNormScale"); //add by zhangjun 2012-04-27 ETF联接基金
        this.sSGSHCashBalance = rs.getString("FBSDifferenceOver") == null ? 0 : rs.getInt("FBSDifferenceOver");//add by zhangjun 2012-04-27 ETF联接基金
        this.sSGDealReplace = rs.getString("FReplaceOver") == null ? 0 : rs.getInt("FReplaceOver");//add by zhangjun 2012-04-27 ETF联接基金
        this.mtReplaceOverDay=rs.getInt("FMtReplaceOver");//赎回必须现金替代款的结转日 story 2727 add by zhouwei 20120619 ETF链接基金
        super.setRecLog(rs);
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        String sShowDataStr = "";
        StringBuffer bufShow = new StringBuffer();

        ResultSet rs = null;
        String strSql = "";
        try {
        	//根据品种类型获取所有的证券代码      add by guolongchao 20120308 STORY 2193 QDV4中银基金2012年02月06日01_A------start
        	if(sType != null && sType.equalsIgnoreCase("getSecurityCodes"))
        	{
		        strSql = " select distinct FSecurityCode from  " +pub.yssGetTableName("Tb_Para_Security") +
		                 " where FCatCode=" + dbl.sqlString(this.strCategoryCode);
		        rs = dbl.openResultSet(strSql);
		        while(rs.next())
		       	    sShowDataStr= sShowDataStr +"'"+rs.getString("FSecurityCode")+"',";
		        
		        if(sShowDataStr!=null&&sShowDataStr.trim().length()>0)
		        	sShowDataStr=sShowDataStr.substring(0,sShowDataStr.length()-1);
		        return sShowDataStr;
        	}
        	//根据品种类型获取所有的证券代码      add by guolongchao 20120308 STORY 2193 QDV4中银基金2012年02月06日01_A------end
        	
            if (sType != null && sType.equalsIgnoreCase("getSetting")) { // 取证券信息数据
                this.getSetting();
                return this.buildRowStr();
            } else if (sType != null &&
                       sType.equalsIgnoreCase("getPubParamSecurity")) { //QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
                return (String) pub.getHtPubParams().get("security");
            } 
            else if(sType != null && sType.equalsIgnoreCase("getFSubCatCode"))//add by guolongchao 20111103 bug 3001 获取品种子类型
            {
            	 strSql = "select fsubcatcode from " +pub.yssGetTableName("Tb_Para_Security") +
                          " where FSecurityCode = " + dbl.sqlString(this.strSecurityCode) +
                          " and FCheckState = 1 ";
	             rs = dbl.openResultSet(strSql);
	             while(rs.next()) 
	             {
	            	 sShowDataStr=rs.getString("fsubcatcode");
	             }	             
            }
            else {
                strSql = "select FSecurityCode,max(FStartDate) " +
                    " as FStartDate from " +
                    pub.yssGetTableName("Tb_Para_Security") +
                    " where FSecurityCode = " + dbl.sqlString(sType) +
                    " and FCheckState = 1  group by FSecurityCode";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FSecurityCode") + "").trim()).
                        append("\t");
                    bufShow.append( (YssFun.formatDate(rs.getDate("FStartDate"),
                        YssCons.YSS_DATEFORMAT) + "").
                                   trim()).append(YssCons.YSS_LINESPLITMARK);
                }
                if (bufShow.toString().length() > 2) {
                    sShowDataStr = bufShow.toString().substring(0,
                        bufShow.toString().length() -
                        2);
                }

            }

            //MS00179 QDV4建行2009年1月07日01_B 2009.02.13 方浩 批量审核/反审核
            if (sType.equalsIgnoreCase("multauditTradeSub")) {      //判断是否要进行批量审核与反审核
                if (multAuditString.length() > 0) {                 //判断批量审核与反审核的内容是否为空
                    return this.auditMutli(this.multAuditString);   //执行批量审核/反审核
                }
            }
        } catch (NullPointerException ex) {//STORY #2475 根据FindBugs工具，对系统进行全面检查  zhangjun
            throw new YssException(ex.getMessage());  
        } catch (Exception e) {
        	throw new YssException(e.getMessage());  
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        return sShowDataStr;
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        SecurityBean befEditBean = new SecurityBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                "(select FSecurityCode,FCheckState,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") + " " +
                " where FStartDate <= " +
                dbl.sqlDate(new java.util.Date()) +
                "and FCheckState <> 2 group by FSecurityCode,FCheckState) x join" +
                " (select a.*, m.FVocName as FSettleDayTypeValue, d.FCatName as FCatName, e.FSubCatName as FSubCatName," +
                " f.FExchangeName as FExchangeName, f.FRegionCode as FRegionCode, " +
                " f.FCountryCode as FCountryCode, f.FAreaCode as FAreaCode, " +
                " fa.FRegionName as FRegionName, fb.FCountryName as FCountryName," +
                " fc.FAreaName as FAreaName, g.FCurrencyName as FCurrencyName, " +
                " h.FSectorName as FSectorName, i.FHolidaysName as FHolidaysName," +
                " j.FCusCatName as FCusCatName,k.FAffCorpName as FIssueCorpName," +
                " b.FUserName as FCreatorName,c.FUserName as FCheckUserName,l.fsecclsname as FSYNTHETICNAME from " +
                pub.yssGetTableName("Tb_Para_Security") + " a " +
                " left join (select FSecClsCode,FSecClsName from " + pub.yssGetTableName("Tb_Para_SectorClass") + " where FCheckState = 1) l on a.fsyntheticcode = l.FSecClsCode" +//板块分类名称add by yanghaiming 20100426 
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d on a.FCatCode = d.FCatCode" +
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) e on a.FSubCatCode = e.FSubCatCode" +
                " left join (select FExchangeCode,FExchangeName,FRegionCode,FCountryCode,FAreaCode from Tb_Base_Exchange where FCheckState = 1) f on a.FExchangeCode = f.FExchangeCode" +
                " left join (select FRegionCode,FRegionName from Tb_Base_Region where FCheckState = 1) fa on f.FRegionCode = fa.FRegionCode" +
                " left join (select FCountryCode,FCountryName from Tb_Base_Country where FCheckState = 1) fb on f.FCountryCode = fb.FCountryCode" +
                " left join (select FAreaCode,FAreaName from Tb_Base_Area where FCheckState = 1) fc on f.FAreaCode = fc.FAreaCode" +
                " left join (select FCuryCode,FCuryName as FCurrencyName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) g on a.FTradeCury = g.FCuryCode" +
                " left join (select o.FSectorCode as FSectorCode,o.FSectorName as FSectorName from " +
                pub.yssGetTableName("Tb_Para_Sector") + " o join " +
                "(select FSectorCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Sector") + " " +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSectorCode) p " +
                " on o.FSectorCode = p.FSectorCode and o.FStartDate = p.FStartDate) h on a.FSectorCode = h.FSectorCode" +
                " left join (select FHolidaysCode,FHolidaysName from Tb_Base_Holidays where FCheckState = 1) i on a.FHolidaysCode = i.FHolidaysCode" +
                " left join (select FCusCatCode,FCusCatName from " +
                pub.yssGetTableName("Tb_Para_CustomCategory") +
                " where FCheckState = 1) j on  a.FCusCatCode = j.FCusCatCode " +
                " left join (select FAffCorpCode,FAffCorpName from " +
                pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
                " where FCheckState = 1) k on a.FIssueCorpCode = k.FAffCorpCode " +
                " left join Tb_Fun_Vocabulary m on " +
                dbl.sqlToChar("a.FSettleDayType") +
                " = m.FVocCode and m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_SCY_SDAYTYPE) +
                " where  FSecurityCode =" +
                dbl.sqlString(this.strOldSecurityCode) +
                " and FStartDate=" + dbl.sqlDate(this.dtOldStartDate) +
                ") y on x.FSecurityCode = y.FSecurityCode and x.FStartDate = y.FStartDate" +
                " order by y.FCatCode, y.FCheckState, y.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.strSecurityCode = rs.getString("FSecurityCode") +
                    "";
                befEditBean.strSecurityName = rs.getString("FSecurityName") +
                    "";
                befEditBean.strSecurityShortName = rs.getString("FSecurityShortName") + "";
                befEditBean.strSecurityCorpName = rs.getString("FSecurityCorpName") + "";
                befEditBean.dtStartDate = rs.getDate("FStartDate");
                befEditBean.strCategoryCode = rs.getString("FCatCode") + "";
                befEditBean.strCategoryName = rs.getString("FCatName") + "";
                befEditBean.strSubCategoryCode = rs.getString("FSubCatCode") + "";
                befEditBean.strSubCategoryName = rs.getString("FSubCatName") + "";
                befEditBean.strExchangeCode = rs.getString("FExchangeCode") + "";
                befEditBean.strExchangeName = rs.getString("FExchangeName") + "";
                befEditBean.strRegionCode = rs.getString("FRegionCode") + "";
                befEditBean.strRegionName = rs.getString("FRegionName") + "";
                befEditBean.strCountryCode = rs.getString("FCountryCode") + "";
                befEditBean.strCountryName = rs.getString("FCountryName") + "";
                befEditBean.strAreaCode = rs.getString("FAreaCode") + "";
                befEditBean.strAreaName = rs.getString("FAreaName") + "";
                befEditBean.strMarketCode = rs.getString("FMarketCode") + "";
                befEditBean.strTradeCuryCode = rs.getString("FTradeCury") + "";
                befEditBean.strTradeCuryName = rs.getString("FCurrencyName") + "";
                befEditBean.strSectorCode = rs.getString("FSectorCode") + "";
                befEditBean.strSectorName = rs.getString("FSectorName") + "";
                befEditBean.strSyntheticCode = rs.getString("FSYNTHETICCODE") + "";//add by yanghaiming 20100426
                befEditBean.strSyntheticName = rs.getString("FSYNTHETICNAME") + "";
                befEditBean.strSettleDayType = rs.getString("FSettleDayType") + "";
                befEditBean.intSettleDays = rs.getInt("FSettleDays");
                befEditBean.dblFactor = rs.getDouble("FFactor");
                befEditBean.dblTotalShare = rs.getBigDecimal("FTotalShare");
                befEditBean.dblCurrentShare = rs.getBigDecimal("FCurrentShare");
                befEditBean.dblHandAmount = rs.getBigDecimal("FHandAmount");
                befEditBean.strIssueCorpCode = rs.getString("FIssueCorpCode") + "";
                befEditBean.strIssueCorpName = rs.getString("FIssueCorpName") + "";
                befEditBean.strCusCatCode = rs.getString("FCusCatCode") + "";
                befEditBean.strCusCatName = rs.getString("FCusCatName") + "";
                befEditBean.strHolidaysCode = rs.getString("FHolidaysCode") + "";
                befEditBean.strHolidaysName = rs.getString("FHolidaysName") + "";
                befEditBean.strExternalCode = rs.getString("FExternalCode") + "";
                befEditBean.strDesc = rs.getString("FDesc") + "";
                befEditBean.settleDayTypeValue = rs.getString("FSettleDayTypeValue") + "";
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    /**
     * 从证券信息维护的回收站删除数据，即彻底清除
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        boolean bIsSecurityPub = false; //定义变量存放通用参数 QDV4建行2008年12月25日01_A  MS00131 byleeyu 20090204
        //获取一个连接
        Connection conn = dbl.loadConnection();
        //add by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A 用于保存组合群数组
        String[] assetGroupCodes = null;
        try {
            bIsSecurityPub = Boolean.valueOf( (String) pub.getHtPubParams().get(
                "security")).booleanValue(); //获取PUB中参数的值 MS00131
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    //---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
                    if(this.sAssetGroupCode.trim().length() > 0){//添加跨组合群清除数据功能
                    	assetGroupCodes = this.sAssetGroupCode.trim().split(",");
                    	for(int j = 0; j < assetGroupCodes.length; j++){
                        	//根据参数来查找是采用哪张表 MS00131
                            strSql = "delete from " + (bIsSecurityPub ? "Tb_Base_Security" : 
                            "Tb_" + assetGroupCodes[j] + "_Para_Security") + 
                            " where FSECURITYCODE = " + dbl.sqlString(this.strSecurityCode) + 
                            " and FStartDate = " + dbl.sqlDate(this.dtStartDate) +
                            (bIsSecurityPub ? (" and FASSETGROUPCODE=" + 
                            //根据上表决定是否用新表的这个字段做为参数 MS00131
                            dbl. sqlString(assetGroupCodes[j].trim().length() > 0 ? assetGroupCodes[j] : " ")) : " "); 
                            
                            //执行sql语句
                            dbl.executeSql(strSql);
                    	}
                    }else{
                    	//根据参数来查找是采用哪张表 MS00131
                        strSql = "delete from " + (bIsSecurityPub ? "Tb_Base_Security" : 
                        pub.yssGetTableName("Tb_Para_Security")) + 
                        " where FSECURITYCODE = " + dbl.sqlString(this.strSecurityCode) + 
                        " and FStartDate = " + dbl.sqlDate(this.dtStartDate) +
                        (bIsSecurityPub ? (" and FASSETGROUPCODE=" + 
                        //根据上表决定是否用新表的这个字段做为参数 MS00131
                        dbl. sqlString(sAssetGroupCode.trim().length() > 0 ? sAssetGroupCode : " ")) : " "); 
                        
                        //执行sql语句
                        dbl.executeSql(strSql);
                    }
                    //---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
                }
            }
            //sRecycled如果sRecycled为空，而strSecurityCode不为空，则按照strSecurityCode来执行sql语句
            else if (strSecurityCode != "" && strSecurityCode != null) {
            	//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
                if(this.sAssetGroupCode.trim().length() > 0){//添加跨组合群清除数据功能
                	assetGroupCodes = this.sAssetGroupCode.trim().split(",");
                	for(int j = 0; j < assetGroupCodes.length; j++){
                		//根据参数来查找是采用哪张表 MS00131
                		strSql = "delete from " + (bIsSecurityPub ? "Tb_Base_Security" : 
                			    "Tb_" + assetGroupCodes[j] + "_Para_Security") + 
                				" where FSECURITYCODE = " + dbl.sqlString(this.strSecurityCode) +
                				" and FStartDate = " + dbl.sqlDate(this.dtStartDate) +
                				(bIsSecurityPub ? (" and FASSETGROUPCODE=" +
                				//根据上表决定是否用新表的这个字段做为参数 MS00131
                				dbl.sqlString(assetGroupCodes[j].trim().length() > 0 ? assetGroupCodes[j] :" ")) : " "); 
                
                		//执行sql语句
                		dbl.executeSql(strSql);
                	}
                }else{
            		strSql = "delete from " + (bIsSecurityPub ? "Tb_Base_Security" :
        				pub.yssGetTableName("Tb_Para_Security")) + //根据参数来查找是采用哪张表 MS00131
        				" where FSECURITYCODE = " + dbl.sqlString(this.strSecurityCode) +
        				" and FStartDate = " + dbl.sqlDate(this.dtStartDate) +
        				(bIsSecurityPub ? (" and FASSETGROUPCODE=" +
        				//根据上表决定是否用新表的这个字段做为参数 MS00131
        				dbl.sqlString(sAssetGroupCode.trim().length() > 0 ?sAssetGroupCode :" ")) : " "); 
        
            		//执行sql语句
            		dbl.executeSql(strSql);
                }
                //---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * MS00179 QDV4建行2009年1月07日01_B 2009.02.13 方浩
     * @param sMutilRowStr String
     * @return String
     * @throws YssException
     * 更新批量审核与反审核数据的内容
     */
    public String auditMutli(String sMutilRowStr) throws YssException {
        Connection conn = null; //建立一个数据库连接
        String sqlStr = ""; //创建一个字符串
        java.sql.PreparedStatement psmt = null;
        boolean bTrans = false; //建一个boolean变量
        SecurityBean data = null; //创建一个证券信息维护pojo类
        String[] multAudit = null; //建一个字符串数组
        //-------2009.02.24 蒋锦 添加 《QDV4.1赢时胜上海2009年2月1日07_A》-----//
        String sResult = ""; //返回值，被审核或反审核后的证券与估值方法绑定的详细信息
        ArrayList alSecurity = new ArrayList(); //被审核或反审核的证券代码
        String sOperType = ""; //操作类型 审核：add；反审核：del
        MTVMethodLinkBean mtvLink = null;
        //-----------------------------------------------------------------//
        try {
            //2009.02.24 蒋锦 添加 《QDV4.1赢时胜上海2009年2月1日07_A》
            //给操作类型变量赋值
            if (checkStateId == 1) {
                sOperType = "add";
            } else {
                sOperType = "del";
            }
            conn = dbl.loadConnection(); //和数据库进行连接
            //deleted by liubo.Story #1770
            //需求要求将同一条数据分别在不同的数据表进行操作，用prepareStatement无法做到这点
            //===============================
            //sqlStr = "update " + pub.yssGetTableName("Tb_Para_Security") +
//            sqlStr = "update ? " +
//                " set FCheckState = " + this.checkStateId +
//                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//                ", FCheckTime = '" +
//                YssFun.formatDatetime(new java.util.Date()) +
//                "' where FSecurityCode = ?"; //更新数据库审核与未审核的SQL语句
//
//            psmt = conn.prepareStatement(sqlStr); //执行SQL语句

            //==============end=================
            
            conn.setAutoCommit(false); //打开一个事物
            bTrans = true;
            if (multAuditString.length() > 0) {
            	//modified by liubo.Story #1770
            	//解析前台传入的组合群代码，根据组合群代码分别拼装成需要的数据表，然后进行操作
            	//===========================
                multAudit = sMutilRowStr.split("\f\f\f\f"); //拆分从前台传来的listview里面的条目
                if (multAudit.length > 0) { //判断传来的审核与反审核条目数量可大于0
                    for (int i = 0; i < multAudit.length; i++) { //循环遍历这些条目
                        data = new SecurityBean(); //new 一个pojo类
                        data.setYssPub(pub); //设置一些基础信息
                        data.parseRowStr(multAudit[i]); //解析前台传来的单个条目信息
                        
                        //--- add by songjie 2012.10.29 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                        //根据证券代码获取审核、反审核、删除操作对应的完整数据
						data.filterType = new SecurityBean();
                        data.filterType.setStrSecurityCode(data.getStrSecurityCode());
                        //delete by songjie 2012.11.02 BUG 6160 QDV4赢时胜(上海开发部)2012年11月1日01_B
                        //data.getListViewData1();
                        //--- add by songjie 2012.10.29 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                        
                        String[] sGroupCode = ("".equals(data.getSAssetGroupCode().trim()) ? pub.getAssetGroupCode() : data.getSAssetGroupCode()).split(",");
                        for (int j = 0;j<sGroupCode.length;j++)
                        {
                        	alSecurity = new ArrayList();
                        	sqlStr = "update " + ("".equals(sGroupCode[j].trim()) ? pub.yssGetTableName("Tb_Para_Security") : "Tb_" + sGroupCode[j] + "_Para_Security") +
                            " set FCheckState = " + this.checkStateId +
                            ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                            ", FCheckTime = '" +
                            YssFun.formatDatetime(new java.util.Date()) +
                            "' where FSecurityCode = " + dbl.sqlString(data.strSecurityCode); //更新数据库审核与未审核的SQL语句
                        		
//                        	psmt = conn.prepareStatement(sqlStr); //执行SQL语句
//	                        psmt.setString(1, data.strSecurityCode); //设置SQL语句的查寻条件
//	                        psmt.addBatch();
	                        //2009.02.24 蒋锦 添加 《QDV4.1赢时胜上海2009年2月1日07_A》
	                        //记录审核或反审核的证券代码
	                        alSecurity.add(data.strSecurityCode);
                        	
                        	dbl.executeSql(sqlStr);
	                        
	                        // ---增加批量删除的日志记录功能----guojianhua add 2010 09 08-------//
	                        logOper = SingleLogOper.getInstance();
	                        
	                        //--- add by songjie 2012.10.29 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	                        //证券信息设置审核、反审核、删除数据 日志缺少功能名称 和 模块名称信息 以及功能调用代码
	                        data.setFunName(this.getFunName());
	                        data.setModuleName(this.getModuleName());
	                        data.setRefName(this.getRefName());
	                        //--- add by songjie 2012.10.29 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	                        
							if (this.checkStateId == 2) {
								logOper.setIData(data, YssCons.OP_DEL, pub);
							} else if (this.checkStateId == 1) {
								data.checkStateId = 1;
								logOper.setIData(data, YssCons.OP_AUDIT, pub);
							} else if (this.checkStateId == 0) {
								data.checkStateId = 0;
								logOper.setIData(data, YssCons.OP_AUDIT, pub);
							}

	                    //data=this;
	                    //============end===============
                        mtvLink = new MTVMethodLinkBean();
                        mtvLink.setYssPub(pub);
                        sResult = mtvLink.operSecurityLinkMtvMethod(alSecurity, sOperType,sGroupCode[j]);
                        updateRelaInfo(conn, psmt, sMutilRowStr,sGroupCode[j]); //审核其他信息
				//-----------------end------------------

                        }
                }
                

//                psmt.executeBatch();
                //--- MS00378 QDV4中保2009年04月10日02_B 同步更新关联信息 -----------------
                //---------------------------------------------------------------------
                //2009.02.24 蒋锦 添加 《QDV4.1赢时胜上海2009年2月1日07_A》
                //执行证券与估值方法的绑定，和证券审核操作在同一事务中
                //sResult = mtvLink.operSecurityLinkMtvMethod(alSecurity, sOperType);
                conn.commit(); //事物提交
                bTrans = false;
                conn.setAutoCommit(true); //关闭事物
                }
            }
        } catch (Exception e) {
            throw new YssException("批量审核证券信息维护表出错!");
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(psmt); //添加关闭语句的处理。20090417
        }
        //2009.02.24 蒋锦 添加 《QDV4.1赢时胜上海2009年2月1日07_A》
        //返回估值方法绑定的详细信息
        return sResult;
    }

    /**
     * 同步更新证券关联的信息，使用同一连接和语句，使事务可控。
     * @param conn Connection
     * @param psmt PreparedStatement
     * @param sMutilRowStr String   多条证券的信息
     * @throws YssException
     * MS00378 QDV4中保2009年04月10日02_B
     */
    private void updateRelaInfo(Connection conn, java.sql.PreparedStatement psmt, String sMutilRowStr) throws
        YssException {
        String sqlStrFi = null; //债券
        String sqlStrFu = null; //股指
        String sqlStrFW = null; //远期
        String sqlStrRe = null; //回购
        SecurityBean security = null;
        try {
            if (null != psmt) {
                psmt.close(); //先关闭语句
            }
        } catch (SQLException ex) {
            throw new YssException("关闭语句出现异常!", ex);
        }
        sqlStrFi = "update " + pub.yssGetTableName("Tb_Para_Fixinterest") +
            " set FCheckState = " + this.checkStateId +
            ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
            ", FCheckTime = '" +
            YssFun.formatDatetime(new java.util.Date()) +
            "' where FSecurityCode = ?"; //更新债券审核与未审核的SQL语句
        sqlStrFu = "update " + pub.yssGetTableName("Tb_Para_IndexFutures") +
            " set FCheckState = " + this.checkStateId +
            ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
            ", FCheckTime = '" +
            YssFun.formatDatetime(new java.util.Date()) +
            "' where FSecurityCode = ?"; //更新股指审核与未审核的SQL语句
        sqlStrFW = "update " + pub.yssGetTableName("Tb_Para_Forward") +
            " set FCheckState = " + this.checkStateId +
            ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
            ", FCheckTime = '" +
            YssFun.formatDatetime(new java.util.Date()) +
            "' where FSecurityCode = ?"; //更新远期审核与未审核的SQL语句
        sqlStrRe = "update " + pub.yssGetTableName("Tb_Para_Purchase") +
            " set FCheckState = " + this.checkStateId +
            ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
            ", FCheckTime = '" +
            YssFun.formatDatetime(new java.util.Date()) +
            "' where FSecurityCode = ?"; //更新回购审核与未审核的SQL语句
        try {
            security = new SecurityBean();
            security.setYssPub(pub); //为了解析证券信息和获取证券代码用
            excutePsmt(conn, psmt, sMutilRowStr, sqlStrFi, security); //执行审核债券信息
            excutePsmt(conn, psmt, sMutilRowStr, sqlStrFu, security); //执行审核股指信息
            excutePsmt(conn, psmt, sMutilRowStr, sqlStrFW, security); //执行审核远期信息
            excutePsmt(conn, psmt, sMutilRowStr, sqlStrRe, security); //执行审核回购信息
        } catch (Exception ex1) {
            throw new YssException(ex1);
        } finally {
            dbl.closeStatementFinal(psmt); //关闭语句
        }
    }

    /**
     * 执行不同类型证券的更新
     * @param conn Connection
     * @param psmt PreparedStatement
     * @param sMutilRowStr String
     * @param psmtStr String           更新语句
     * @param security SecurityBean
     * @throws YssException
     * MS00378 QDV4中保2009年04月10日02_B
     */
    private void excutePsmt(Connection conn, java.sql.PreparedStatement psmt, String sMutilRowStr, String psmtStr, SecurityBean security) throws
        YssException,SQLException {
        String[] multAudit = null; //建一个字符串数组
        try {
            if (null != psmt) {
                psmt.close(); //先关闭语句
            }
            psmt = conn.prepareStatement(psmtStr); //执行SQL语句
            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f"); //拆分从前台传来的listview里面的条目
                if (multAudit.length > 0) { //判断传来的审核与反审核条目数量可大于0
                    for (int i = 0; i < multAudit.length; i++) { //循环遍历这些条目
                        security.parseRowStr(multAudit[i]); //解析前台传来的单个条目信息
                        psmt.setString(1, security.getStrSecurityCode()); //通过反射调用来设置参数值
                        psmt.addBatch();
                    } //end for Fi
                } //end if multAudit
            } //end if multAuditString
            psmt.executeBatch(); //执行批处理
        } catch (Exception e) {
            throw new YssException("审核/反审核证券关联信息出现异常！", e);
        } finally{
        	dbl.closeStatementFinal(psmt);
        	if (null != psmt){
        		psmt.close();
        	}
        }
    }

    
    public String getTreeViewGroupData1() throws YssException {
    	return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

    /**
     * add by songjie 2012.04.17 
     * BUG 3975 QDV4赢时胜(测试)2012年3月6日01_B
     * 交易数据界面查询证券信息数据时 调用该方法 返回所有已审核的证券信息数据
     * 
     */
    public String getListViewGroupData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        String strSql = "";

        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        
        try {
            boolean bIsSecurityPub = false; //定义变量存放通用参数 QDV4建行2008年12月25日01_A  MS00131 byleeyu 20090204
            bIsSecurityPub = Boolean.valueOf( (String) pub.getHtPubParams().get(
                "security")).booleanValue(); //获取PUB中参数的值 MS00131
            strSql =
                "select a.*, m.FVocName as FSettleDayTypeValue, d.FCatName as FCatName, e.FSubCatName as FSubCatName," +
                " f.FExchangeName as FExchangeName, f.FRegionCode as FRegionCode, " +
                " f.FCountryCode as FCountryCode, f.FAreaCode as FAreaCode, " +
                " fa.FRegionName as FRegionName, fb.FCountryName as FCountryName," +
                " fc.FAreaName as FAreaName, g.FCurrencyName as FCurrencyName, " +
                " h.FSectorName as FSectorName, i.FHolidaysName as FHolidaysName," +
                " j.FCusCatName as FCusCatName,k.FAffCorpName as FIssueCorpName," +
                " b.FUserName as FCreatorName,c.FUserName as FCheckUserName,l.fsecclsname as FSYNTHETICNAME from " +
                (bIsSecurityPub ? "Tb_Base_Security" :
                 pub.yssGetTableName("Tb_Para_Security")) + " a " + //根据参数取具体的表 QDV4建行2008年12月25日01_A MS00131 byleeyu 20090204
                " left join (select FSecClsCode,FSecClsName from " + pub.yssGetTableName("Tb_Para_SectorClass") + " where FCheckState = 1) l on a.fsyntheticcode = l.FSecClsCode" +//板块分类名称add by yanghaiming 20100426 
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d on a.FCatCode = d.FCatCode" +
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) e on a.FSubCatCode = e.FSubCatCode" +
                " left join (select FExchangeCode,FExchangeName,FRegionCode,FCountryCode,FAreaCode from Tb_Base_Exchange where FCheckState = 1) f on a.FExchangeCode = f.FExchangeCode" +
                " left join (select FRegionCode,FRegionName from Tb_Base_Region where FCheckState = 1) fa on f.FRegionCode = fa.FRegionCode" +
                " left join (select FCountryCode,FCountryName from Tb_Base_Country where FCheckState = 1) fb on f.FCountryCode = fb.FCountryCode" +
                " left join (select FAreaCode,FAreaName from Tb_Base_Area where FCheckState = 1) fc on f.FAreaCode = fc.FAreaCode" +
                " left join (select FCuryCode,FCuryName as FCurrencyName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) g on a.FTradeCury = g.FCuryCode" +
                " left join (select o.FSectorCode as FSectorCode,o.FSectorName as FSectorName from " +
                pub.yssGetTableName("Tb_Para_Sector") + " o join " +
                "(select FSectorCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Sector") + " " +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSectorCode) p " +
                " on o.FSectorCode = p.FSectorCode and o.FStartDate = p.FStartDate) h on a.FSectorCode = h.FSectorCode" +
                " left join (select FHolidaysCode,FHolidaysName from Tb_Base_Holidays where FCheckState = 1) i on a.FHolidaysCode = i.FHolidaysCode" +
                " left join (select FCusCatCode,FCusCatName from " +
                pub.yssGetTableName("Tb_Para_CustomCategory") +
                " ) j on  a.FCusCatCode = j.FCusCatCode " +
                " left join (select FAffCorpCode,FAffCorpName from " +
                pub.yssGetTableName("Tb_Para_AffiliatedCorp") +
                " ) k on a.FIssueCorpCode = k.FAffCorpCode " +
                " left join Tb_Fun_Vocabulary m on " +
                dbl.sqlToChar("a.FSettleDayType") +
                " = m.FVocCode and m.FVocTypeCode = " + //lzp modify 20080123
                dbl.sqlString(YssCons.YSS_SCY_SDAYTYPE) +
                buildFilterSql() +
                (bIsSecurityPub ?
                 (" and (a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE like " +
                  dbl.sqlString("%" + pub.getAssetGroupCode() + "%") + ") ") : " ") + //添加上查询条件 MS00131
                " order by a.FCatCode, a.FCheckState, a.FCreateTime desc";
        	
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_SCY_SDAYTYPE + "," +
                                        YssCons.YSS_FIX_INTORG + "," +
                                        YssCons.YSS_FIX_CALCINSWAY + "," +
                                        YssCons.YSS_FIX_QUOTEWAY + "," +
                                        YssCons.YSS_FIX_Level);

            sHeader = this.getListView1Headers();
            
    		rs = dbl.openResultSet(strSql);           
            
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setSecurityAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" +
                this.getListView1ShowCols() + "\r\f" + this.buildRowStr() + "\r\f" + "voc" + sVocStr;           
        } catch (Exception e) {
            throw new YssException("获取证券信息维护出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewGroupData2() throws YssException {
        return "";
    }

    public String getListViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }
    
    /**
	 * add baopingping #story 1167 20110717
	 * 查询当前表中所有组合群
	 * return ResultSet
	 * @throws YssException 
	 */
	public String getAssdeGroup() throws YssException{
		ResultSet rs=null;
		String sql=null;
		String FAssetGroupCode="";
		try{
			sql="select * from Tb_Sys_AssetGroup order by FAssetGroupCode";
			rs=dbl.openResultSet(sql);
			while(rs.next())
			{
				FAssetGroupCode+=rs.getString("FAssetGroupCode")+"\t";
			}
			return FAssetGroupCode;
		}
		catch(Exception e){
			throw new YssException("获取组合群出错！\t");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * added by liubo. #story 1770.20111123
	 * 通过此方法，查询出类似“001,002”以逗号分隔开的组合群代号所代表的组合群代码，同样以逗号分隔开
	 * FAssetGroupCode 组合群代码
	 * return String
	 * @throws YssException 
	 */
	
	private String getGroupNameFromGroupCode(String FAssetGroupCode) throws YssException
	{
		String sReturn = "";
		String strSql = "";
		ResultSet rs = null;
		String[] groupCode = null;
		String requestGroupCode = "";
		try
		{
//			groupCode = ("".equals(FAssetGroupCode.trim()) ? pub.getAssetGroupCode() : FAssetGroupCode).split(",");
			groupCode = FAssetGroupCode.split(",");
			for (int i = 0;i<groupCode.length;i++)
			{
				requestGroupCode = requestGroupCode +"'" + groupCode[i] + "',";
			}
			
			strSql = "select * from tb_sys_AssetGroup where FAssetGroupCode in (" + requestGroupCode.substring(0,requestGroupCode.length() - 1) + ")";
			rs = dbl.openResultSet(strSql);
			while(rs.next())
			{
				sReturn = sReturn + rs.getString("FAssetGroupName") + ",";
			}
			
			return sReturn;
		
		}
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	//added by liubo.Story #1770
	//========================================
	/**
     * 同步更新证券关联的信息，使用同一连接和语句，使事务可控。
     * @param conn Connection
     * @param psmt PreparedStatement
     * @param sMutilRowStr String   多条证券的信息
     * @throws YssException
     * MS00378 QDV4中保2009年04月10日02_B
     */
    private void updateRelaInfo(Connection conn, java.sql.PreparedStatement psmt, String sMutilRowStr,String sAssetGroupCode) throws
        YssException {
        String sqlStrFi = null; //债券
        String sqlStrFu = null; //股指
        String sqlStrFW = null; //远期
        String sqlStrRe = null; //回购
        SecurityBean security = null;
        try {
            if (null != psmt) {
                psmt.close(); //先关闭语句
            }
        } catch (SQLException ex) {
            throw new YssException("关闭语句出现异常!", ex);
        }
        sqlStrFi = "update " + "Tb_" + sAssetGroupCode + "_Para_Fixinterest" +
            " set FCheckState = " + this.checkStateId +
            ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
            ", FCheckTime = '" +
            YssFun.formatDatetime(new java.util.Date()) +
            "' where FSecurityCode = ?"; //更新债券审核与未审核的SQL语句
        sqlStrFu = "update " + "Tb_" + sAssetGroupCode +"_Para_IndexFutures" +
            " set FCheckState = " + this.checkStateId +
            ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
            ", FCheckTime = '" +
            YssFun.formatDatetime(new java.util.Date()) +
            "' where FSecurityCode = ?"; //更新股指审核与未审核的SQL语句
        sqlStrFW = "update " + "Tb_" + sAssetGroupCode + "_Para_Forward" +
            " set FCheckState = " + this.checkStateId +
            ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
            ", FCheckTime = '" +
            YssFun.formatDatetime(new java.util.Date()) +
            "' where FSecurityCode = ?"; //更新远期审核与未审核的SQL语句
        sqlStrRe = "update " + "Tb_" + sAssetGroupCode + "_Para_Purchase" +
            " set FCheckState = " + this.checkStateId +
            ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
            ", FCheckTime = '" +
            YssFun.formatDatetime(new java.util.Date()) +
            "' where FSecurityCode = ?"; //更新回购审核与未审核的SQL语句
        try {
            security = new SecurityBean();
            security.setYssPub(pub); //为了解析证券信息和获取证券代码用
            excutePsmt(conn, psmt, sMutilRowStr, sqlStrFi, security); //执行审核债券信息
            excutePsmt(conn, psmt, sMutilRowStr, sqlStrFu, security); //执行审核股指信息
            excutePsmt(conn, psmt, sMutilRowStr, sqlStrFW, security); //执行审核远期信息
            excutePsmt(conn, psmt, sMutilRowStr, sqlStrRe, security); //执行审核回购信息
        } catch (Exception ex1) {
            throw new YssException(ex1);
        } finally {
            dbl.closeStatementFinal(psmt); //关闭语句
        }
    }
    
    //added by liubo.Story #1770
    //此方法用于判断某个组合群是否已经存在了某个证券代码的数据。若存在，返回TRUE。此方法用于在此需求情况下取代checkinput方法
    private boolean checkSecurityCode(String sAssetGroupCode, String sSecurityCode) throws YssException
    {
    	String strSql = "";
    	ResultSet rs = null;
    	boolean bReturn = false;
    	try 
    	{
	        strSql = "select * from " + "Tb_" + sAssetGroupCode + "_Para_Security" + " where FSECURITYCODE = " + dbl.sqlString(sSecurityCode);
			rs = dbl.openResultSet(strSql);
			while(rs.next())
			{
				bReturn = true;
				break;
			}
	    	return bReturn;
    	}
    	
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
    }
    
    /**
     * add by huangqirong 2012-06-27 story #2727
     * 根据证券代码查找所属证券子类型
     * */
    public String getSecSubType(String securitycode) throws YssException{
    	String strSql = "";
    	ResultSet rs = null;
    	String subType = "" ;
    	try {
			strSql = " select * from " + pub.yssGetTableName("tb_para_security") + " where Fcheckstate = 1 and FSecuritycode = " + dbl.sqlString(securitycode);
    		rs = dbl.openResultSet(strSql);
    		if(rs.next()){
    			subType = rs.getString("FSubCatCode");
    		}
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    	return subType;
    }
}
