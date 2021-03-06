package com.yss.main.parasetting;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.dsub.DbTable;
import com.yss.dsub.YssPub;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.main.storagemanage.RollAssetBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 *
 * <p>Title: PortfolioBean</p>
 * <p>Description:组合设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class PortfolioBean
    extends BaseDataSettingBean implements IDataSetting {
    private String portCode = ""; //组合代码
    private String portName = ""; //组合名称
    private String portShortName = ""; //组合简称
    private String assetCode = ""; //资产代码
    private String assetGroupCode = ""; //组合群代码
    private String assetGroupName = ""; //组合群名称
    private String currencyCode = ""; //组合货币代码
    private String currencyName = ""; //组合货币名称
    private String strPortType = ""; //组合类型
    private String strCosting = ""; //核算方法
    private String strEnabled = ""; //是否禁用
    private java.util.Date startDate; //启用日期
    private java.util.Date inceptionDate; //开始日期
    private java.util.Date expirationDate; //终止日期
    private java.util.Date storageInitDate; //库存初始日期
    private String desc = ""; //组合描述
    private String subRelaType = ""; //关联类型
    private String subRelaCode = ""; //关联代码
    private String baseRateSrcCode = ""; //基础汇率来源代码
    private String baseRateSrcName = ""; //基础汇率来源名称
    private String portRateSrcCode = ""; //组合汇率来源代码
    private String portRateSrcName = ""; //组合汇率来源名称
    private String baseRateCode = ""; //基础汇率行情
    private String portRateCode = ""; //组合汇率行情
    private BigDecimal inceptionAsset; //成立资产 // edit by xuqiji 20090428:QDV4赢时胜上海2009年04月27日01_B MS00415 成立资产与滚动金额界面显示数据与数据库中数据不一致
    private PortfolioBean filterType; //筛选对象
    private String assetSource = ""; //资产来源
    private String assetType = ""; //资产类型,panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
    private String subAssetType =""; //资产子类型,panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
    private String oldPortCode; //修改用原组合代码
    private java.util.Date oldStartDate; //修改用原启用日期
    private BigDecimal rollAsset; //滚动金额  // edit by xuqiji 20090428:QDV4赢时胜上海2009年04月27日01_B MS00415 成立资产与滚动金额界面显示数据与数据库中数据不一致
    private String bSubData = "";
    private String aSubData = "";
    private java.util.Date oldInceptionDate; //
    private String sRecycled = "";

    private String curyCode =""; //add by lidaolong 2011.01.24; QDV4上海2010年12月10日02_A 
    private String curyName ="";//add by lidaolong 2011.01.24; QDV4上海2010年12月10日02_A
    
    private String aimETFCode = "";//add by zhangjun 2012-04-26 ETF联接基金
    private String aimETFName = "";//add by zhangjun 2012-04-26 ETF联接基金
    
    private String portSub = "";
    
    private String currentPortGroupCode="";//add by guyichuan STORY #897
    
    private boolean create = false;
    
    //add by huangqirong 2012-08-07 story #2831 识别是什么类型的数据
    private String dataType = "";
    
    
    public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	//---end---

	public String getAimETFCode() {
		return aimETFCode;
	}

	public void setAimETFCode(String aimETFCode) {
		this.aimETFCode = aimETFCode;
	}

	public String getAimETFName() {
		return aimETFName;
	}

	public void setAimETFName(String aimETFName) {
		this.aimETFName = aimETFName;
	}

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public String getPortCode() {
        return this.portCode;
    }

    public String getPortName() {
        return this.portName;
    }

    public void setAssetSource(String assetSource) {
        this.assetSource = assetSource;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public void setSubAssetType(String subAssetType) {
        this.subAssetType = subAssetType;
    }

    public void setBSubData(String bSubData) {
        this.bSubData = bSubData;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setASubData(String aSubData) {
        this.aSubData = aSubData;
    }

    public void setOldStartDate(Date oldStartDate) {
        this.oldStartDate = oldStartDate;
    }

    public void setOldPortCode(String oldPortCode) {
        this.oldPortCode = oldPortCode;
    }

    public void setPortRateCode(String portRateCode) {
        this.portRateCode = portRateCode;
    }

    public void setBaseRateCode(String baseRateCode) {
        this.baseRateCode = baseRateCode;
    }

    public void setBaseRateSrcName(String baseRateSrcName) {
        this.baseRateSrcName = baseRateSrcName;
    }

    public void setPortRateSrcCode(String portRateSrcCode) {
        this.portRateSrcCode = portRateSrcCode;
    }

    public void setPortRateSrcName(String portRateSrcName) {
        this.portRateSrcName = portRateSrcName;
    }

    public void setBaseRateSrcCode(String baseRateSrcCode) {
        this.baseRateSrcCode = baseRateSrcCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public void setStrCosting(String strCosting) {
        this.strCosting = strCosting;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setInceptionDate(Date inceptionDate) {
        this.inceptionDate = inceptionDate;
    }

    public String getAssetSource() {
        return assetSource;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getSubAssetType() {
        return subAssetType;
    }

    public String getBSubData() {
        return bSubData;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getASubData() {
        return aSubData;
    }

    public String getOldPortCode() {
        return oldPortCode;
    }

    public String getPortRateCode() {
        return portRateCode;
    }

    public String getBaseRateCode() {
        return baseRateCode;
    }

    public String getBaseRateSrcName() {
        return baseRateSrcName;
    }

    public String getPortRateSrcCode() {
        return portRateSrcCode;
    }

    public String getPortRateSrcName() {
        return portRateSrcName;
    }

    public String getBaseRateSrcCode() {
        return baseRateSrcCode;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getStrCosting() {
        return strCosting;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }

    public java.util.Date getInceptionDate() {
        return inceptionDate;
    }

    public PortfolioBean() {
    }

    /**
     * buildRowStr
     * 获取数据字符串
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.portCode.trim());
        buf.append("\t");
        buf.append(this.portName.trim());
        buf.append("\t");
        buf.append(YssFun.formatDate(this.startDate, YssCons.YSS_DATEFORMAT));
        buf.append("\t");
        buf.append(this.portShortName.trim());
        buf.append("\t");
        buf.append(this.assetCode.trim());
        buf.append("\t");
        buf.append(this.assetGroupCode.trim());
        buf.append("\t");
        buf.append(this.assetGroupName.trim());
        buf.append("\t");
        buf.append(this.currencyCode.trim());
        buf.append("\t");
        buf.append(this.currencyName.trim());
        buf.append("\t");
        buf.append(this.strPortType);
        buf.append("\t");
        buf.append(this.strCosting);
        buf.append("\t");
        buf.append(this.strEnabled);
        buf.append("\t");
        buf.append(YssFun.formatDate(this.inceptionDate, YssCons.YSS_DATEFORMAT)).
            append("\t");
        buf.append(YssFun.formatDate(this.expirationDate, YssCons.YSS_DATEFORMAT)).
            append("\t");
        buf.append(YssFun.formatDate(this.storageInitDate, YssCons.YSS_DATEFORMAT)).
            append("\t");
        buf.append(this.desc.trim());
        buf.append("\t");
        buf.append(this.baseRateSrcCode.trim());
        buf.append("\t");
        buf.append(this.baseRateCode.trim());
        buf.append("\t");
        buf.append(this.baseRateSrcName.trim());
        buf.append("\t");
        buf.append(this.portRateSrcCode.trim());
        buf.append("\t");
        buf.append(this.portRateCode.trim());
        buf.append("\t");
        buf.append(this.portRateSrcName.trim());
        buf.append("\t");

        buf.append(this.assetSource.trim());
        buf.append("\t");
        buf.append(this.rollAsset);
        buf.append("\t");
        buf.append(this.inceptionAsset);
        buf.append("\t");
        //Begin---panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
        buf.append(this.assetType.trim());//资产类型
        buf.append("\t");
        buf.append(this.subAssetType.trim());//资产子类型
        buf.append("\t");
        //End---panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
       
        buf.append(this.curyCode).append("\t");// add by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A 
        buf.append(this.curyName).append("\t");// add by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
        
        buf.append(this.aimETFCode).append("\t");// add by zhangjun 2012-04-26 ETF联接基金
        buf.append(this.aimETFName).append("\t");// add by zhangjun 2012-04-26 ETF联接基金
        if((""+this.dataType).trim().length() > 0)
        	buf.append(this.dataType).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查组合设置数据是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
    	String yssGetTableName = "Tb_" + this.assetGroupCode + "_Para_Portfolio";
    	/**modify by liuxiaojun stroy 4156 20130814  将参数插入到新的组合群里去*/
//        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Portfolio"),
    	 dbFun.checkInputCommon(btOper, yssGetTableName,
                               "FPortCode",
                               this.portCode ,
                               this.oldPortCode);
    	 /**end stroy 4156*/
    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.filterType != null) {
                sResult = " where 1=1 ";
                if (this.filterType.portCode.length() != 0) { // wdy add 20070901  添加表别名：a
                    sResult = sResult + " and a.FPortCode like '" +
                        filterType.portCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.portName.length() != 0) {
                    sResult = sResult + " and a.FPortName like '" +
                        filterType.portName.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.portShortName.length() != 0) {
                    sResult = sResult + " and a.FPortShortName like '" +
                        filterType.portShortName.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.startDate != null &&
                    !this.filterType.startDate.equals(YssFun.toDate("9998-12-31")) &&
                    !this.filterType.startDate.equals(YssFun.toDate("1900-01-01"))) {
                    sResult = sResult + " and a.FStartDate = " +
                        dbl.sqlDate(filterType.startDate);
                }
                if (this.filterType.assetCode.length() != 0) {
                    sResult = sResult + " and a.FAssetCode like '" +
                        filterType.assetCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.assetGroupCode.length() != 0) {
                    sResult = sResult + " and a.FAssetGroupCode like '" +
                        filterType.assetGroupCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.currencyCode.length() != 0) {
                    sResult = sResult + " and a.FPortCury like '" +
                        filterType.currencyCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strPortType.length() == 1) {
                    sResult = sResult + " and a.FPortType = " +
                        this.filterType.strPortType;
                }
                if (this.filterType.strCosting.length() == 1) {
                    sResult = sResult + " and a.FCosting = " +
                        this.filterType.strCosting;
                }
                if (this.filterType.strEnabled.length() == 1) {
                    sResult = sResult + " and a.FEnabled = " +
                        this.filterType.strEnabled;
                }
                if (this.filterType.assetSource.length() != 0 &&
                    !this.filterType.assetSource.equals("99")) {
                    sResult = sResult + " and a.FAssetSource = " +
                        dbl.sqlString(this.filterType.assetSource);
                }
                //Begin---panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                if (this.filterType.assetType.length() != 0 &&
                    !this.filterType.assetType.equals("99")) {
                    sResult = sResult + " and a.FAssetType = " +
                        dbl.sqlString(this.filterType.assetType);
                }
                if (this.filterType.subAssetType.length() != 0 &&
                    !this.filterType.subAssetType.equals("99")) {
                    sResult = sResult + " and a.FSubAssetType = " +
                        dbl.sqlString(this.filterType.subAssetType);
                }
                //End---panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A

                //---begin--QDV4上海2010年12月10日02_A---
                 if (this.filterType.curyCode.length() != 0){
                	 sResult = sResult + " and a.FCuryCode = " +
                     dbl.sqlString(this.filterType.curyCode);
                 }
                //---end lidaolong 2011.01.24-------
                
               //---add by zhangjun 2012-04-26 ETF联接基金-----
                 if (this.filterType.aimETFCode.length() != 0){
                	 sResult = sResult + " and a.FAimETFCode = " +
                     dbl.sqlString(this.filterType.aimETFCode);
                 }
                //---add by zhangjun 2012-04-26 ETF联接基金-------
                 
                 
                if (this.filterType.inceptionDate != null &&
                    !this.filterType.inceptionDate.equals(YssFun.toDate(
                        "9998-12-31")) &&
                    !this.filterType.inceptionDate.equals(YssFun.toDate("1900-01-01"))) { //modified by yeshenghong 20120301 BUG3753
                    sResult = sResult + " and a.FInceptionDate = " +
                        dbl.sqlDate(filterType.inceptionDate);
                }
                if (this.filterType.expirationDate != null &&
                    !this.filterType.expirationDate.equals(YssFun.toDate(
                        "9998-12-31")) &&
                    !this.filterType.expirationDate.equals(YssFun.toDate("1900-01-01"))) {//startdate 界面上不显示 始终等于"1900-01-01"，将导致无法使用
                    sResult = sResult + " and a.FExpirationDate = " + //modified by yeshenghong 20120301 BUG3753
                        dbl.sqlDate(filterType.expirationDate);
                }
                if (this.filterType.storageInitDate != null &&
                    !this.filterType.storageInitDate.equals(YssFun.toDate(
                        "9998-12-31")) &&
                    !this.filterType.storageInitDate.equals(YssFun.toDate("1900-01-01"))) { //modified by yeshenghong 20120301 BUG3753
                    sResult = sResult + " and a.FStorageInitDate = " +
                        dbl.sqlDate(filterType.storageInitDate);
                }
                if (this.filterType.desc.length() != 0) {
                    sResult = sResult + " and a.FDesc like '" +
                        filterType.desc.replaceAll("'", "''") + "%'";
                }
                //------ add by wangzuochun 2010.07.13  MS01370 组合设置的筛选功能失效
                if (this.filterType.baseRateSrcCode.length() != 0) {
                    sResult = sResult + " and a.FBaseRateSrcCode like '" +
                        filterType.baseRateSrcCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.portRateSrcCode.length() != 0) {
                    sResult = sResult + " and a.FPortRateSrcCode like '" +
                        filterType.portRateSrcCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.inceptionAsset!=null)//Bug5460  there is no result using original asset as a filter modified by yeshenghong 20120907
                {// It is completely a joke to use a number as a filter condition, but I can't change it, just do it !
                	sResult = sResult + " and a.finceptionasset = '" +
                    filterType.inceptionAsset.doubleValue() + "'";
                }
                //-------------------MS01370 组合设置的筛选功能失效-----------------//
                //---edit by songjie 2012.04.28 BUG 4388 QDV4赢时胜(测试)2012年4月26日01_B start---//
                if(this.filterType.rollAsset != null && this.filterType.rollAsset.doubleValue() != 0){
                    sResult = sResult + " and a.FRollAsset = " + this.filterType.rollAsset;
                }
                if(this.filterType.baseRateCode != null && this.filterType.baseRateCode.length() > 0 && !this.filterType.baseRateCode.equals("99")){
                	sResult = sResult + " and a.FBaseRateCode = " + dbl.sqlString(this.filterType.baseRateCode);
                }
                if(this.filterType.portRateCode != null && this.filterType.portRateCode.length() > 0 && !this.filterType.portRateCode.equals("99")){
                	sResult = sResult + " and a.FPortRateCode = " + dbl.sqlString(this.filterType.portRateCode);
                }
                //---edit by songjie 2012.04.28 BUG 4388 QDV4赢时胜(测试)2012年4月26日01_B end---//
            }
        } catch (Exception e) {
            throw new YssException("筛选组合设置数据出错", e);
        }
        return sResult;
    }

    /**
     * 为各项变量赋值
     *
     */
    public void setPortfolioAttr(ResultSet rs) throws SQLException {
        this.portCode = rs.getString("FPortCode") + "";
        this.portName = rs.getString("FPortName") + "";
        this.startDate = rs.getDate("FStartDate");
        this.portShortName = rs.getString("FPortShortName") + "";
        this.assetCode = rs.getString("FAssetCode") + "";
        this.assetGroupCode = rs.getString("FAssetGroupCode") + "";
        this.assetGroupName = rs.getString("FAssetGroupName") + "";
        this.currencyCode = rs.getString("FPortCury") + "";
        this.currencyName = rs.getString("FCurrencyName") + "";
        this.strPortType = rs.getString("FPortType") + "";
        this.strCosting = rs.getString("FCosting") + "";
        this.strEnabled = rs.getString("FEnabled") + "";
        this.inceptionDate = rs.getDate("FInceptionDate");
        this.expirationDate = rs.getDate("FExpirationDate");
        this.storageInitDate = rs.getDate("FStorageInitDate");
        this.desc = rs.getString("FDesc") + "";
        this.baseRateSrcCode = rs.getString("FBaseRateSrcCode") + "";
        this.baseRateCode = rs.getString("FBaseRateCode") + "";
        this.baseRateSrcName = rs.getString("FBaseRateSrcName") + "";

        this.portRateSrcCode = rs.getString("FPortRateSrcCode") + "";
        this.portRateCode = rs.getString("FPortRateCode") + "";
        this.portRateSrcName = rs.getString("FPortRateSrcName") + "";

        this.inceptionAsset = rs.getBigDecimal("FInceptionAsset"); // edit by xuqiji 20090428:QDV4赢时胜上海2009年04月27日01_B MS00415 成立资产与滚动金额界面显示数据与数据库中数据不一致
        this.assetSource = rs.getString("FAssetSource");
        //Start---panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
        this.assetType = rs.getString("FAssetType");//资产类型
        this.subAssetType = rs.getString("FSubAssetType");//资产子类型
        //End---panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
        this.curyCode = rs.getString("FCuryCode");// add by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
        this.curyName = rs.getString("FCuryName");// add by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
        
        this.aimETFCode = rs.getString("FAimETFCode");//add by zhangjun 2012-04-26 ETF联接基金
        this.aimETFName = rs.getString("FAimETFName");//add by zhangjun 2012-04-26 ETF联接基金
        
        
        this.rollAsset = rs.getBigDecimal("FRollAsset"); // edit by xuqiji 20090428:QDV4赢时胜上海2009年04月27日01_B MS00415 成立资产与滚动金额界面显示数据与数据库中数据不一致
        super.setRecLog(rs);
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
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setPortfolioAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            //------ modify by wangzuochun 2010.04.21  MS01089   在组合设置界面，浏览这个组合中的席位时报错    QDV4赢时胜(测试)2010年4月13日2_B 
            sVocStr = vocabulary.getVoc(YssCons.YSS_MTV_EXCHANGERATE + "," + YssCons.YSS_OPER_COST +
                                        "," + YssCons.YSS_PRT_ASSETTYPE + "," + YssCons.YSS_PRT_SUBASSETTYPE + 
                                        /** start modify by huangqirong 2013-6-26 Bug #8337 组合设置内估值方法点击浏览报错 */
                                        "," + YssCons.YSS_TDS_SEATTYPE + 
                                        "," + YssCons.YSS_MTV_INVESTMENTTYPE
                                        );
       //增加资产类型和资产子类型 panjunfang modify 20090729，MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
										/** end modify by huangqirong 2013-6-26 Bug #8337 */
                                        
            //------------------------------------------MS01089-----------------------------------------//
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取组合设置数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 此方法已被修改
     *修改时间：2008年
     * 修改人：单亮
     * 原方法的功能：查询出组合表的数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     */

    public String getListViewData1() throws YssException {
        //修改前的代码
//      String strSql = "";
//      strSql = "select y.* from " +
//            "(select FPortCode,FCheckState,max(FStartDate) as FStartDate from " +
//            pub.yssGetTableName("Tb_Para_Portfolio") + " " +
//            " where FStartDate <= " +
//            dbl.sqlDate(new java.util.Date()) +
//            "and FCheckState <> 2 and FASSETGROUPCODE = " +
//            dbl.sqlString(pub.getAssetGroupCode()) +
//            " group by FPortCode,FCheckState) x join" +
//            " (select a.*, l.FVocName as FEnabledValue, m.FVocName as FCostingValue, n.FVocName as FPortTypeValue,o.FVocName as FAssetSourceValue," +
//            " d.FAssetGroupName as FAssetGroupName, e.FCurrencyName as FCurrencyName,f.FBaseRateSrcName , g.FPortRateSrcName," +
//            " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
//            pub.yssGetTableName("Tb_Para_Portfolio") + " a " +
//            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
//            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
//            " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) d on a.FAssetGroupCode = d.FAssetGroupCode" +
//            " left join (select FCuryCode,FCuryName as FCurrencyName from " +
//            pub.yssGetTableName("Tb_Para_Currency") +
//            " where FCheckState = 1) e on a.FPortCury = e.FCuryCode" +
//            " left join (select FExRateSrcCode,FExRateSrcName as FBaseRateSrcName from " +
//            pub.yssGetTableName("Tb_Para_ExRateSource") +
//            " where FCheckState = 1) f on a.FBaseRateSrcCode = f.FExRateSrcCode" +
//            " left join (select FExRateSrcCode,FExRateSrcName as FPortRateSrcName from " +
//            pub.yssGetTableName("Tb_Para_ExRateSource") +
//            " where FCheckState = 1) g on a.FPortRateSrcCode = g.FExRateSrcCode" +
//
//            " left join Tb_Fun_Vocabulary l on " + dbl.sqlToChar("a.FEnabled") + " = l.FVocCode and l.FVocTypeCode = " +  //2007.11.28 修改 蒋锦 使用dbl.sqlToChar()处理"a.FEnabled"，否则在使用DB2数据库时会报数据类型错误
//            dbl.sqlString(YssCons.YSS_PRT_ENABLED) +
//            " left join Tb_Fun_Vocabulary m on " + dbl.sqlToChar("a.FCosting") + " = m.FVocCode and m.FVocTypeCode = " +  //2007.11.28 修改 蒋锦 使用dbl.sqlToChar()处理"a.FCosting"，否则在使用DB2数据库时会报数据类型错误
//            dbl.sqlString(YssCons.YSS_PRT_COSTING) +
//            " left join Tb_Fun_Vocabulary n on " + dbl.sqlToChar("a.FPortType") + " = n.FVocCode and n.FVocTypeCode = " +  //2007.11.28 修改 蒋锦 使用dbl.sqlToChar()处理"a.FPortType"，否则在使用DB2数据库时会报数据类型错误
//            dbl.sqlString(YssCons.YSS_PRT_PORTTYPE) +
//            " left join Tb_Fun_Vocabulary o on a.FAssetSource = o.FVocCode and o.FVocTypeCode = " +
//            dbl.sqlString(YssCons.YSS_PRT_ASSETSOURCE) +
//            buildFilterSql() +
//            ") y on x.FPortCode = y.FPortCode and x.FStartDate = y.FStartDate" +
//            " order by y.FCheckState, y.FCreateTime desc";

        //修改后的代码
        //---------------------------------------------------------
        String strSql = "";
        strSql = "select y.* from " +
        //delete by songjie 2011.03.10 不根据最大的启用日期获取组合设置数据
//            "(select FPortCode,FCheckState,max(FStartDate) as FStartDate from " +
//            pub.yssGetTableName("Tb_Para_Portfolio") + " " +
//            " where FStartDate <= " +
//            dbl.sqlDate(new java.util.Date()) +
//            "and FASSETGROUPCODE = " +
//            dbl.sqlString(pub.getAssetGroupCode()) +
//            " group by FPortCode,FCheckState) x join" +
        //delete by songjie 2011.03.10 不根据最大的启用日期获取组合设置数据
            " (select a.*, l.FVocName as FEnabledValue, m.FVocName as FCostingValue, n.FVocName as FPortTypeValue,o.FVocName as FAssetSourceValue," +
            " oat.FVocName as FAssetTypeValue,osat.FVocName as FSubAssetTypeValue," +//增加资产类型和资产子类型，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
            " p.FCuryName as FCuryName,"+//add by lidaolong 2011.01.25 ;QDV4上海2010年12月10日02_A
            
            " s.FSecurityName as FAimETFName," + //add by zhangjun 2012-04-26 ETF联接基金
            
            " d.FAssetGroupName as FAssetGroupName, e.FCurrencyName as FCurrencyName,f.FBaseRateSrcName , g.FPortRateSrcName," +
            " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            //---edit by songjie 2011.03.10 不根据最大的启用日期获取组合设置数据---//
            " ( select * from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
            " where FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode()) + ") a " +
            //---edit by songjie 2011.03.10 不根据最大的启用日期获取组合设置数据---//
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) d on a.FAssetGroupCode = d.FAssetGroupCode" +
            " left join (select FCuryCode,FCuryName as FCurrencyName from " +
            pub.yssGetTableName("Tb_Para_Currency") +
            " where FCheckState = 1) e on a.FPortCury = e.FCuryCode" +
            " left join (select FExRateSrcCode,FExRateSrcName as FBaseRateSrcName from " +
            pub.yssGetTableName("Tb_Para_ExRateSource") +
            " where FCheckState = 1) f on a.FBaseRateSrcCode = f.FExRateSrcCode" +
            " left join (select FExRateSrcCode,FExRateSrcName as FPortRateSrcName from " +
            pub.yssGetTableName("Tb_Para_ExRateSource") +
            " where FCheckState = 1) g on a.FPortRateSrcCode = g.FExRateSrcCode" +
            //---//add by lidaolong 2011.01.25 ;QDV4上海2010年12月10日02_A
            "    left join (select FCuryName,FCuryCode from "+ pub.yssGetTableName("Tb_Para_Currency")+" ) p on p.FCuryCode = a.FCuryCode  " +
            //---end QDV4上海2010年12月10日02_A-------------
            
            //-----add by zhangjun 2012-04-26 ETF联接基金 Tb_001_Para_Security 
            " left join ( select FSecurityCode,FSecurityName from " +pub.yssGetTableName("Tb_Para_Security") + ") s on a.FAimETFCode = s.FSecurityCode " +            
            //-----add by zhangjun 2012-04-26 ETF联接基金
            
            " left join Tb_Fun_Vocabulary l on " + dbl.sqlToChar("a.FEnabled") + " = l.FVocCode and l.FVocTypeCode = " + //2007.11.28 修改 蒋锦 使用dbl.sqlToChar()处理"a.FEnabled"，否则在使用DB2数据库时会报数据类型错误
            dbl.sqlString(YssCons.YSS_PRT_ENABLED) +
            " left join Tb_Fun_Vocabulary m on " + dbl.sqlToChar("a.FCosting") + " = m.FVocCode and m.FVocTypeCode = " + //2007.11.28 修改 蒋锦 使用dbl.sqlToChar()处理"a.FCosting"，否则在使用DB2数据库时会报数据类型错误
            dbl.sqlString(YssCons.YSS_PRT_COSTING) +
            " left join Tb_Fun_Vocabulary n on " + dbl.sqlToChar("a.FPortType") + " = n.FVocCode and n.FVocTypeCode = " + //2007.11.28 修改 蒋锦 使用dbl.sqlToChar()处理"a.FPortType"，否则在使用DB2数据库时会报数据类型错误
            dbl.sqlString(YssCons.YSS_PRT_PORTTYPE) +
            " left join Tb_Fun_Vocabulary o on a.FAssetSource = o.FVocCode and o.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_PRT_ASSETSOURCE) +
            //Begin---panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
            " left join Tb_Fun_Vocabulary oat on a.FAssetType = oat.FVocCode and oat.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_PRT_ASSETTYPE) +//从词汇表中关联出资产类型的名称
            " left join Tb_Fun_Vocabulary osat on a.FSubAssetType = osat.FVocCode and osat.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_PRT_SUBASSETTYPE) +//从词汇表汇中关联出资产子类型的名称
            //End---panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
            buildFilterSql() +
            //delete by songjie 2011.03.10 不根据最大的启用日期获取组合设置数据
            //") y on x.FPortCode = y.FPortCode and x.FStartDate = y.FStartDate" +
            //edit by songjie 2011.03.10 不根据最大的启用日期获取组合设置数据
            ") y order by y.FCheckState, y.FCreateTime desc";
//-------------------------------------------------
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     * 获取组合设置数据
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";
        strSql = "select a.*, l.FVocName as FEnabledValue, m.FVocName as FCostingValue, n.FVocName as FPortTypeValue,o.FVocName as FAssetSourceValue," +
            " oat.FVocName as FAssetTypeValue,osat.FVocName as FSubAssetTypeValue," +//增加资产类型和资产子类型，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
            " p.FCuryName as FCuryName,"+//add by lidaolong 2011.01.25 ;QDV4上海2010年12月10日02_A
            " s.FSecurityName as FAimETFName," + //add by zhangjun 2012-04-26 ETF联接基金
            
            " d.FAssetGroupName as FAssetGroupName, e.FCurrencyName as FCurrencyName,f.FBaseRateSrcName as FBaseRateSrcName,g.FPortRateSrcName as FPortRateSrcName," +
            " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Para_Portfolio") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) d on a.FAssetGroupCode = d.FAssetGroupCode" +
            " left join (select FCuryCode,FCuryName as FCurrencyName from " +
            pub.yssGetTableName("Tb_Para_Currency") +
            " where FCheckState = 1) e on a.FPortCury = e.FCuryCode" +
            " left join (select FExRateSrcCode,FExRateSrcName as FBaseRateSrcName  from " +
            pub.yssGetTableName("Tb_Para_ExRateSource") +
            " where FCheckState = 1) f on a.FBaseRateSrcCode = f.FExRateSrcCode" +
            " left join (select FExRateSrcCode,FExRateSrcName as FPortRateSrcName from " +
            pub.yssGetTableName("Tb_Para_ExRateSource") +
            " where FCheckState = 1) g on a.FPortRateSrcCode = g.FExRateSrcCode" +

            " left join Tb_Fun_Vocabulary l on " + dbl.sqlToChar("a.FEnabled") + " = l.FVocCode and l.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_PRT_ENABLED) +
            " left join Tb_Fun_Vocabulary m on " + dbl.sqlToChar("a.FCosting") + " = m.FVocCode and m.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_PRT_COSTING) +
            " left join Tb_Fun_Vocabulary n on " + dbl.sqlToChar("a.FPortType") + " = n.FVocCode and n.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_PRT_PORTTYPE) +

            //---//add by lidaolong 2011.01.25 ;QDV4上海2010年12月10日02_A
            "    left join (select FCuryName,FCuryCode from "+ pub.yssGetTableName("Tb_Para_Currency")+" ) p on p.FCuryCode = a.FCuryCode  " +
            //---end QDV4上海2010年12月10日02_A-------------
            
            //-----add by zhangjun 2012-04-26 ETF联接基金 Tb_001_Para_Security 
            " left join ( select FSecurityCode,FSecurityName from " +pub.yssGetTableName("Tb_Para_Security") + ") s on a.FAimETFCode = s.FSecurityCode " +            
            //-----add by zhangjun 2012-04-26 ETF联接基金
            
            //Begin---panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
            " left join Tb_Fun_Vocabulary oat on a.FAssetType = oat.FVocCode and oat.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_PRT_ASSETTYPE) + //从词汇表中关联出资产类型的名称
            " left join Tb_Fun_Vocabulary osat on a.FSubAssetType = osat.FVocCode and osat.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_PRT_SUBASSETTYPE) + //从词汇表汇中关联出资产子类型的名称
            //End---panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A

            " left join Tb_Fun_Vocabulary o on a.FAssetSource = o.FVocCode and o.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_PRT_ASSETSOURCE);

        if (this.filterType != null) {
            strSql += buildFilterSql() + " and a.FASSETGROUPCODE = " +
                dbl.sqlString(pub.getAssetGroupCode()) +
                " order by a.FCheckState, a.FCreateTime desc";
        } else {
            strSql += " where a.FASSETGROUPCODE = " +
                dbl.sqlString(pub.getAssetGroupCode()) +
                " order by a.FCheckState, a.FCreateTime desc";
        }

        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData2
     * 获取已审核的组合设置数据
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
        try {        	
            sHeader = "组合代码\t组合名称";//不显示启用日期
            if(!this.currentPortGroupCode.equals("")){//add by guyichuan STORY #897
            	pub.setPrefixTB(this.currentPortGroupCode);//将该组合群代码设为表前缀
            }
            
            //---add by songjie 2012.02.27 3864 QDV4赢时胜(测试)2012年2月13日01_B start---//
        	if(!dbl.yssTableExist(pub.yssGetTableName("Tb_Para_Portfolio"))){
        		throw new YssException("请更新完所有的组合群再进行多组合群操作！");
        	}
            //---add by songjie 2012.02.27 3864 QDV4赢时胜(测试)2012年2月13日01_B end---//
        	
            strSql = "select y.* from " +
            //delete by songjie 2011.03.10 不以最大的启用日期来查询组合设置数据
//                "(select FPortCode,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_Portfolio") + " " +
//                " where FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
//                "and FCheckState = 1 and FEnabled = 1 and FASSETGROUPCODE = " +
//                dbl.sqlString(pub.getPrefixTB()) +//2009-05-20 panjunfang 修改 跨组合群时会动态改变 PrefixTB 而不是 AssetGroupCode MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
//                " group by FPortCode) x join" +
            //delete by songjie 2011.03.10 不以最大的启用日期来查询组合设置数据    
            " (select a.*, d.FAssetGroupName as FAssetGroupName, e.FCurrencyName as FCurrencyName,f.FBaseRateSrcName as FBaseRateSrcName,g.FPortRateSrcName as FPortRateSrcName," +
                " p.FCuryName as FCuryName,"+//add by lidaolong 2011.01.25 ;QDV4上海2010年12月10日02_A
                " s.FSecurityName as FAimETFName," + //add by zhangjun 2012-04-26 ETF联接基金
                
                " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                //edit by songjie 2011.03.10 不以最大的启用日期来查询组合设置数据
                "( select * from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
                " where FCheckState = 1 and FEnabled = 1 and FASSETGROUPCODE = " + 
                dbl.sqlString(pub.getPrefixTB()) + ") a " +
                //edit by songjie 2011.03.10 不以最大的启用日期来查询组合设置数据
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //---//add by lidaolong 2011.01.25 ;QDV4上海2010年12月10日02_A
                "    left join (select FCuryName,FCuryCode from "+ pub.yssGetTableName("Tb_Para_Currency")+" ) p on p.FCuryCode = a.FCuryCode  " +
                //---end QDV4上海2010年12月10日02_A-------------
                
                //-----add by zhangjun 2012-04-26 ETF联接基金 Tb_001_Para_Security 
                " left join ( select FSecurityCode,FSecurityName from " +pub.yssGetTableName("Tb_Para_Security") + ") s on a.FAimETFCode = s.FSecurityCode " +            
                //-----add by zhangjun 2012-04-26 ETF联接基金
                
                " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) d on a.FAssetGroupCode = d.FAssetGroupCode" +
                " left join (select FCuryCode,FCuryName as FCurrencyName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) e on a.FPortCury = e.FCuryCode" +
                " left join (select FExRateSrcCode,FExRateSrcName as FBaseRateSrcName from " +
                pub.yssGetTableName("Tb_Para_ExRateSource") +
                " where FCheckState = 1) f on a.FBaseRateSrcCode = f.FExRateSrcCode" +
                " left join (select FExRateSrcCode,FExRateSrcName as FPortRateSrcName from " +
                pub.yssGetTableName("Tb_Para_ExRateSource") +
                " where FCheckState = 1) g on a.FPortRateSrcCode = g.FExRateSrcCode" +

                this.buildFilterSql() +
                //delete by songjie 2011.03.10 不以最大的启用日期来查询组合设置数据    
                //") y on x.FPortCode = y.FPortCode and x.FStartDate = y.FStartDate " +
                // "  join (select  FRightCode,fusercode,FAssetGroupCode from tb_sys_userright  where frightind =  'Port' " +
                // " and fuserCode= " + dbl.sqlString(pub.getUserCode()) + " and fassetgroupcode= " + dbl.sqlString(pub.getAssetGroupCode()) + ") z on x.FPortCode = z.FRightCode " +
                //edit by songjie 2011.03.10 不以最大的启用日期来查询组合设置数据    
                ") y order by y.FPortCode, y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FPortCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FPortName") + "").trim()).append(
                		YssCons.YSS_LINESPLITMARK);//edit by songjie 2011.03.15 不显示启用日期
                //----delete by songjie 2011.03.15 不显示启用日期----//
//                bufShow.append( (YssFun.formatDate(rs.getDate("FStartDate"),
//                    YssCons.YSS_DATEFORMAT) + "").
//                               trim()).append(YssCons.YSS_LINESPLITMARK);
                //----delete by songjie 2011.03.15 不显示启用日期----//
                setPortfolioAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
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
            throw new YssException("获取组合设置数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * added by liubo.Story #1770.
     * 此方法根据从前台传回的组合群号获取该组合群下的组合
     * @return String
     */
    public String getPortCodeByAssetGroupCode(String sAssetGroupCode) throws YssException
	{
    	String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        try {
            sHeader = "组合代码\t组合名称";//不显示启用日期
            
            //判断从前台传回的组合群号，若为空，则表示当前组合群
            String strPrefixTB = ("".equals(sAssetGroupCode.trim()) ? this.currentPortGroupCode : sAssetGroupCode);//将该组合群代码设为表前缀
            
            strSql = "select y.* from " +
            //delete by songjie 2011.03.10 不以最大的启用日期来查询组合设置数据
//                "(select FPortCode,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_Portfolio") + " " +
//                " where FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
//                "and FCheckState = 1 and FEnabled = 1 and FASSETGROUPCODE = " +
//                dbl.sqlString(pub.getPrefixTB()) +//2009-05-20 panjunfang 修改 跨组合群时会动态改变 PrefixTB 而不是 AssetGroupCode MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
//                " group by FPortCode) x join" +
            //delete by songjie 2011.03.10 不以最大的启用日期来查询组合设置数据    
            " (select a.*, d.FAssetGroupName as FAssetGroupName, e.FCurrencyName as FCurrencyName,f.FBaseRateSrcName as FBaseRateSrcName,g.FPortRateSrcName as FPortRateSrcName," +
                " p.FCuryName as FCuryName,"+//add by lidaolong 2011.01.25 ;QDV4上海2010年12月10日02_A
                
                " s.FSecurityName as FAimETFName," + //add by zhangjun 2012-04-26 ETF联接基金
                
                " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                //edit by songjie 2011.03.10 不以最大的启用日期来查询组合设置数据
                "( select * from Tb_" + strPrefixTB + "_Para_Portfolio" + 
                " where FCheckState = 1 and FEnabled = 1 and FASSETGROUPCODE = " + 
                dbl.sqlString(strPrefixTB) + ") a " +
                //edit by songjie 2011.03.10 不以最大的启用日期来查询组合设置数据
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //---//add by lidaolong 2011.01.25 ;QDV4上海2010年12月10日02_A
                "    left join (select FCuryName,FCuryCode from Tb_" + strPrefixTB + "_Para_Currency) p on p.FCuryCode = a.FCuryCode  " +
                //---end QDV4上海2010年12月10日02_A-------------
                
                //-----add by zhangjun 2012-04-26 ETF联接基金 Tb_001_Para_Security 
                " left join ( select FSecurityCode,FSecurityName from " +pub.yssGetTableName("Tb_Para_Security") + ") s on a.FAimETFCode = s.FSecurityCode " +            
                //-----add by zhangjun 2012-04-26 ETF联接基金
                
                " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) d on a.FAssetGroupCode = d.FAssetGroupCode" +
                " left join (select FCuryCode,FCuryName as FCurrencyName from " +
                "Tb_" + strPrefixTB + "_Para_Currency" +
                " where FCheckState = 1) e on a.FPortCury = e.FCuryCode" +
                " left join (select FExRateSrcCode,FExRateSrcName as FBaseRateSrcName from " +
                "Tb_" + strPrefixTB + "_Para_ExRateSource" +
                " where FCheckState = 1) f on a.FBaseRateSrcCode = f.FExRateSrcCode" +
                " left join (select FExRateSrcCode,FExRateSrcName as FPortRateSrcName from " +
                "Tb_" + strPrefixTB + "_Para_ExRateSource" +
                " where FCheckState = 1) g on a.FPortRateSrcCode = g.FExRateSrcCode" +

                this.buildFilterSql() +
                //delete by songjie 2011.03.10 不以最大的启用日期来查询组合设置数据    
                //") y on x.FPortCode = y.FPortCode and x.FStartDate = y.FStartDate " +
                // "  join (select  FRightCode,fusercode,FAssetGroupCode from tb_sys_userright  where frightind =  'Port' " +
                // " and fuserCode= " + dbl.sqlString(pub.getUserCode()) + " and fassetgroupcode= " + dbl.sqlString(pub.getAssetGroupCode()) + ") z on x.FPortCode = z.FRightCode " +
                //edit by songjie 2011.03.10 不以最大的启用日期来查询组合设置数据    
                ") y order by y.FPortCode, y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FPortCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FPortName") + "").trim()).append(
                		YssCons.YSS_LINESPLITMARK);//edit by songjie 2011.03.15 不显示启用日期
                //----delete by songjie 2011.03.15 不显示启用日期----//
//                bufShow.append( (YssFun.formatDate(rs.getDate("FStartDate"),
//                    YssCons.YSS_DATEFORMAT) + "").
//                               trim()).append(YssCons.YSS_LINESPLITMARK);
                //----delete by songjie 2011.03.15 不显示启用日期----//
                setPortfolioAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
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
            throw new YssException("获取组合设置数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
		
	}

    /**
     * getListViewData3
     * 获取
     * 1.有关联表限制条件的组合设置数据
     * 2.已审核的没有被汇总组合关联的明细组合设置数据
     * @return String
     */
    public String getListViewData3() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        try {
            if (this.subRelaType.length() > 0 && this.subRelaCode.length() > 0) {
                sHeader = "组合代码\t明细组合名称\t启用日期";
                strSql =
                    "select a.*, f.FAssetGroupName as FAssetGroupName, g.FCurrencyName as FCurrencyName,g.FCuryName as FCuryName,f.FBaseRateSrcName as FBaseRateSrcName,g.FPortRateSrcName as FPortRateSrcName," +
                    
                    " s.FSecurityName as FAimETFName," + //add by zhangjun 2012-04-26 ETF联接基金
                    
                    " d.FUserName as FCreatorName,e.FUserName as FCheckUserName from" +
                    //----delete by songjie 2011.03.10 不以最大的启用日期查询组合设置数据----//
//                    " (select FPortCode, max(FStartDate) As FStartDate from " +
//                    pub.yssGetTableName("Tb_Para_Portfolio") +
//                    " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                    " and FAssetGroupCode = " +
//                    dbl.sqlString(pub.getPrefixTB()) + //2009-05-20 蒋锦 修改 跨组合群时会动态改变 PrefixTB 而不是 AssetGroupCode MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合
//                    " and FCheckState = 1 and FEnabled = 1 group by FPortCode) c " +
                    //----delete by songjie 2011.03.10 不以最大的启用日期查询组合设置数据----//
                    " (select * from " +
                    pub.yssGetTableName("Tb_Para_Portfolio") +
                    //----edit by songjie 2011.03.10 不以最大的启用日期查询组合设置数据----//
                    " where FAssetGroupCode = " + dbl.sqlString(pub.getPrefixTB()) + 
                    " and FCheckState = 1 and FEnabled = 1 ) a " +
                    " join (select FPortCode from " +
                    //----edit by songjie 2011.03.10 不以最大的启用日期查询组合设置数据----//
                    pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
                    " where FSubCode = " + dbl.sqlString(this.subRelaCode) +
                    " and FRelaType = " + dbl.sqlString(this.subRelaType) +
                    //edit by songjie 2011.03.10 不以最大的启用日期查询组合设置数据
                    " and FCheckState = 1 group by FPortCode) b on a.FPortCode = b.FPortCode " +
                    
                    //-----add by zhangjun 2012-04-26 ETF联接基金 Tb_001_Para_Security 
                    " left join ( select FSecurityCode,FSecurityName from " +pub.yssGetTableName("Tb_Para_Security") + ") s on a.FAimETFCode = s.FSecurityCode " +            
                    //-----add by zhangjun 2012-04-26 ETF联接基金
                    
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                    " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) f on a.FAssetGroupCode = f.FAssetGroupCode" +
                    " left join (select FCuryCode,FCuryName as FCurrencyName,FCuryName  from " +//edit by lidaolong 374 QDV4国泰2010年12月02日01_A
                    pub.yssGetTableName("Tb_Para_Currency") +
                    " where FCheckState = 1) g on a.FPortCury = g.FCuryCode" +
                    " left join (select FExRateSrcCode,FExRateSrcName as FBaseRateSrcName from " +
                    pub.yssGetTableName("Tb_Para_ExRateSource") +
                    " where FCheckState = 1) f on a.FBaseRateSrcCode = f.FExRateSrcCode" +
                    " left join (select FExRateSrcCode,FExRateSrcName as FPortRateSrcName from " +
                    pub.yssGetTableName("Tb_Para_ExRateSource") +
                    " where FCheckState = 1) g on a.FPortRateSrcCode = g.FExRateSrcCode" +

                    this.buildFilterSql();
            } else {  	
            	//edit by songjie 2011.03.14 不显示启用日期栏
                sHeader = "明细组合代码\t明细组合名称";
                strSql = "select y.* from " +
                //delete by songjie 2011.03.10 不以最大的启用日期查询组合设置数据
//                    "(select FPortCode,FCheckState as FChkState,max(FStartDate) as FStartDate from " +
//                    pub.yssGetTableName("Tb_Para_Portfolio") + " " +
//                    " where FStartDate <= " +
//                    dbl.sqlDate(new java.util.Date()) +
//                    "and FCheckState = 1 and FEnabled = 1 and FPortType= 0 and FASSETGROUPCODE = " +
//                    dbl.sqlString(pub.getPrefixTB()) + //2009-05-20 蒋锦 修改 跨组合群时会动态改变 PrefixTB 而不是 AssetGroupCode MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合
//                    " group by FPortCode,FCheckState) x join " +
                //delete by songjie 2011.03.10 不以最大的启用日期查询组合设置数据
                    " (select a.*, d.FAssetGroupName as FAssetGroupName, e.FCurrencyName as FCurrencyName,e.FCuryName,f.FExRateSrcName as FBaseRateSrcName,g.FPortRateSrcName as FPortRateSrcName," +
                    " FLinkCode, h.FSubCode as FSubCode," +
                    
                    " s.FSecurityName as FAimETFName," + //add by zhangjun 2012-04-26 ETF联接基金

                    " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                    " (select * from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                    " where FCheckState = 1 and FPortType = 0 " +
                    " and FASSETGROUPCODE = " +
                    dbl.sqlString(pub.getPrefixTB()) + ") a " + //2009-05-20 蒋锦 修改 跨组合群时会动态改变 PrefixTB 而不是 AssetGroupCode MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) d on a.FAssetGroupCode = d.FAssetGroupCode" +
                    
                    //-----add by zhangjun 2012-04-26 ETF联接基金 Tb_001_Para_Security 
                    " left join ( select FSecurityCode,FSecurityName from " +pub.yssGetTableName("Tb_Para_Security") + ") s on a.FAimETFCode = s.FSecurityCode " +            
                    //-----add by zhangjun 2012-04-26 ETF联接基金
                    
                    " left join (select FCuryCode,FCuryName as FCurrencyName,FCuryName from " +
                    pub.yssGetTableName("Tb_Para_Currency") +
                    " where FCheckState = 1) e on a.FPortCury = e.FCuryCode" +
                    " left join (select FExRateSrcCode,FExRateSrcName from " +
                    pub.yssGetTableName("Tb_Para_ExRateSource") +
                    " where FCheckState = 1) f on a.FBaseRateSrcCode = f.FExRateSrcCode" +
                    " left join (select FExRateSrcCode,FExRateSrcName as FPortRateSrcName from " +
                    pub.yssGetTableName("Tb_Para_ExRateSource") +
                    " where FCheckState = 1) g on a.FPortRateSrcCode = g.FExRateSrcCode" +

                    " left join (select FPortCode as FLinkCode, FSubCode from " +
                    pub.yssGetTableName("Tb_Para_Portfolio_Relaship") + " where FRelaType = 'PortLink' and FCheckState <> 2 ) h on a.FPortCode = h.FSubCode" +
                    //  " where FSubCode IS NULL " +  " OR
                    buildFilterSql() + //2009-05-20 蒋锦 修改 添加筛选条件 MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合
                    //delete by songjie 2011.03.10 不以最大的启用日期查询组合设置数据
                    //") y on x.FPortCode = y.FPortCode and x.FStartDate = y.FStartDate " +
                    //edit by songjie 2011.03.10 不以最大的启用日期查询组合设置数据
                    ") y order by y.FCheckState, y.FCreateTime desc";
            }
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	//update by guolongchao 20111209 bug 3200 加载放大镜数据时出错
            	if (this.subRelaType.length() > 0 && this.subRelaCode.length() > 0)
            	{
            		bufShow.append( (rs.getString("FPortCode") + "").trim()).append("\t");                     
                    bufShow.append( (rs.getString("FPortName") + "").trim()).append("\t");
                    bufShow.append( (YssFun.formatDate(rs.getDate("FStartDate"),YssCons.YSS_DATEFORMAT) + "").trim()).append(YssCons.YSS_LINESPLITMARK);
            	}
            	else
            	{
            		 bufShow.append( (rs.getString("FPortCode") + "").trim()).append("\t");                     
                     bufShow.append( (rs.getString("FPortName") + "").trim()).append(YssCons.YSS_LINESPLITMARK);
            	}
//                bufShow.append( (rs.getString("FPortCode") + "").trim()).append(
//                    "\t");
//                //----edit by songjie 2011.03.14 不获取启用日期数据----//
//                bufShow.append( (rs.getString("FPortName") + "").trim()).
//                append(YssCons.YSS_LINESPLITMARK);
//                //----edit by songjie 2011.03.14 不获取启用日期数据----//
//                //----delete by songjie 2011.03.14 不获取启用日期数据----//
////                bufShow.append( (YssFun.formatDate(rs.getDate("FStartDate"),
////                    YssCons.YSS_DATEFORMAT) + "").
////                               trim()).append(YssCons.YSS_LINESPLITMARK);
//                //----delete by songjie 2011.03.14 不获取启用日期数据----//
                setPortfolioAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
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
            throw new YssException("获取组合设置数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 检查当前组合群是否有指定的组合代码，如有返回true
     * 也可根据组合代码判断这个组合是否在当前的组合群中
     * MS00131 QDV4建行2008年12月25日01_A by leeyu 20090205
     * @return boolean
     */
    /**shashijie 2012-11-16 STORY 3187 改为共有,报表解析数据源与拼接控件组名需要用到*/
    public boolean checkThisGroupPort() throws YssException {
        boolean bCheck = false; //默认为不存在
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select '1' as FCheck from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FPortCode=" + dbl.sqlString(this.portCode);
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                bCheck = true;
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return bCheck;
    }

    /**
     * 获取系统组合群下的全部已审核组合
     * by leeyu add MS00131 QDV4建行2008年12月25日01_A
     * 前台用listView 解析
     * @return String
     * @throws YssException
     */
    public String getTreeViewData1() throws YssException {
        String strSql = "";
        String sShowDataStr = "", sAllDataStr = "";
        ResultSet rs = null;
        ResultSet prs = null;
        StringBuffer bufAll = new StringBuffer();
        StringBuffer bufShow = new StringBuffer();
        String sHeader = "";
        Hashtable htAllPort = new Hashtable(); //存放全部组合
        PortfolioBean portfolio = null; //定义本身，下面要用到
        try {
            sHeader = "组合代码\t组合名称\t启用日期";
            strSql = "select FAssetGroupCode,FAssetGroupName,FTabPreFix  from tb_sys_assetgroup where FLocked=0 and FTabInd=1 order by FAssetGroupCode ";
            rs = dbl.openResultSet(strSql); //取组合群
            while (rs.next()) {
                //查找出可用的组合表
                if (dbl.yssTableExist("Tb_" + rs.getString("FTabPreFix") + "_Para_Portfolio")) {
                    strSql = "select * from " +
                        "Tb_" + rs.getString("FTabPreFix") + "_Para_Portfolio" +
                        " where FCheckState = 1 and FEnabled = 1 and FASSETGROUPCODE=" + dbl.sqlString(rs.getString("FAssetGroupCode"));
                    prs = dbl.openResultSet(strSql);
                    while (prs.next()) {
                        portfolio = new PortfolioBean();
                        portfolio.portCode = prs.getString("FPortCode") + "";
                        portfolio.portName = prs.getString("FPortName") + "";
                        portfolio.startDate = prs.getDate("FStartDate");
                        portfolio.assetGroupCode = prs.getString("FASSETGROUPCODE") + "";
                        portfolio.inceptionDate = prs.getDate("FStartDate");
                        portfolio.expirationDate = prs.getDate("FStartDate");
                        portfolio.storageInitDate = prs.getDate("FStartDate");
                        htAllPort.put(portfolio.portCode, portfolio); //这里先不分是哪个组合群的组合，若代码相同就取一次。
                    } //end port while
                }
            } //end group while
            Set keySet = htAllPort.keySet();
            Iterator i = keySet.iterator();
            while (i.hasNext()) {
                portfolio = (PortfolioBean) htAllPort.get(i.next());
                bufShow.append(portfolio.portCode).append("\t");
                bufShow.append(portfolio.portName).append("\t");
                bufShow.append(portfolio.startDate).append(YssCons.YSS_LINESPLITMARK);
                bufAll.append(portfolio.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
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
        } catch (Exception ex) {
            throw new YssException("获取系统组合群下全部已审核组合信息出错", ex);
        } finally {
            bufAll = null;
            bufShow = null;
            dbl.closeResultSetFinal(prs);
            dbl.closeResultSetFinal(rs);
        }

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
     * parseRowStr
     * 解析组合设置请求
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
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
        	
            if (sRowStr.equals("")) {
                return;
            }else if(sRowStr.indexOf("FENGKONG_CURRENTGROUPCODE")!=-1){//add by guyichuan STORY #897
            	this.currentPortGroupCode=sRowStr.split("\t")[1].trim();
            	return;
            }
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.portSub = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.portCode = reqAry[0];
            this.portName = reqAry[1];
            this.startDate = YssFun.toDate(reqAry[2]);
            this.portShortName = reqAry[3];
            this.assetCode = reqAry[4];
            this.assetGroupCode = reqAry[5];
            this.currencyCode = reqAry[6];
            this.strPortType = reqAry[7];
            this.strCosting = reqAry[8];
            this.strEnabled = reqAry[9];
            this.inceptionDate = YssFun.toDate(reqAry[10]);
            this.expirationDate = YssFun.toDate(reqAry[11]);
            this.storageInitDate = YssFun.toDate(reqAry[12]);
            this.desc = reqAry[13];
            this.checkStateId = Integer.parseInt(reqAry[14]);
            this.oldPortCode = reqAry[15];
            this.oldStartDate = YssFun.toDate(reqAry[16]);
            this.subRelaType = reqAry[17];
            this.subRelaCode = reqAry[18];

            this.baseRateSrcCode = reqAry[19];
            this.baseRateCode = reqAry[20];
            this.baseRateSrcName = reqAry[21];

            this.portRateSrcCode = reqAry[22];
            this.portRateCode = reqAry[23];
            this.portRateSrcName = reqAry[24];

            this.assetSource = reqAry[25];
            if (YssFun.isNumeric(reqAry[26]) && reqAry[26].length() > 0) {
                this.rollAsset = new BigDecimal(reqAry[26].replaceAll(",", "")); // edit by xuqiji 20090428:QDV4赢时胜上海2009年04月27日01_B MS00415 成立资产与滚动金额界面显示数据与数据库中数据不一致
            } else {
                this.rollAsset = new BigDecimal("0"); // edit by xuqiji 20090428:QDV4赢时胜上海2009年04月27日01_B MS00415 成立资产与滚动金额界面显示数据与数据库中数据不一致
            }
            if(reqAry[27].equals("null"))//modified by yeshenghong 20121223 #1829
            {
            	reqAry[27] = "0";
            }
            if (YssFun.isNumeric(reqAry[27])) {
                this.inceptionAsset = new BigDecimal(reqAry[27]); // edit by xuqiji 20090428:QDV4赢时胜上海2009年04月27日01_B MS00415 成立资产与滚动金额界面显示数据与数据库中数据不一致
            }
            this.oldInceptionDate = YssFun.toDate(reqAry[28]);
            this.assetType = reqAry[29];//资产类型，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
            this.subAssetType = reqAry[30];//资产子类型，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
            this.curyCode = reqAry[31];// 基础货币代码　add by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
            this.curyName = reqAry[32];// 基础货币名称　add by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
            this.aimETFCode = reqAry[33];  //add by zhangjun 2012-04-26 ETF联接基金
            this.aimETFName = reqAry[34]; //add by zhangjun 2012-04-26 ETF联接基金
            
            
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new PortfolioBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析组合设置请求出错", e);
        }

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
     * saveSetting
     * 更新组合设置信息
     * @param btOper byte
     */
    /*  public void saveSetting(byte btOper) throws YssException {
         String strSql = "";
         boolean bTrans = false;
         Connection conn = dbl.loadConnection();
         try {
            if (btOper == YssCons.OP_ADD) {
     strSql = "insert into " + pub.yssGetTableName("Tb_Para_Portfolio") + "" +
     "(FPortCode,FPortName,FStartDate,FPortShortName,FAssetCode,FAssetGroupCode," +
                     "FPortCury,FExRateSrcCode,FExRateCode,FPortType,FCosting,FEnabled,FInceptionDate," +
                     "FExpirationDate,FStorageInitDate,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                     " values(" + dbl.sqlString(this.portCode) + "," +
                     dbl.sqlString(this.portName) + "," +
                     dbl.sqlDate(this.startDate) +
                     " ," + dbl.sqlString(this.portShortName) + "," +
                     dbl.sqlString(this.assetCode) + "," +
                     dbl.sqlString(this.assetGroupCode) + "," +
                     dbl.sqlString(this.currencyCode) + "," +
                     dbl.sqlString(this.exRateSrcCode) + "," +
                     dbl.sqlString(this.exRateCode) + "," +
                     this.strPortType + "," + this.strCosting + "," +
                     this.strEnabled + "," +
                     dbl.sqlDate(this.inceptionDate) + "," +
                     dbl.sqlDate(this.expirationDate) + "," +
                     dbl.sqlDate(this.storageInitDate) + "," +
                     dbl.sqlString(this.desc) + "," +
                     (pub.getSysCheckState()?"0":"1") + "," +
                     dbl.sqlString(this.creatorCode) + "," +
                     dbl.sqlString(this.creatorTime) + "," +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
            }
            else if (btOper == YssCons.OP_EDIT) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_Portfolio") + " set FPortCode = " +
                     dbl.sqlString(this.portCode) + ", FPortName = " +
                     dbl.sqlString(this.portName) +
                     " , FStartDate = " +
                     dbl.sqlDate(this.startDate) +
                     ", FPortShortName = " + dbl.sqlString(this.portShortName) +
                     ", FAssetCode = " + dbl.sqlString(this.assetCode) +
     ", FAssetGroupCode = " + dbl.sqlString(this.assetGroupCode) +
                     ", FPortCury = " + dbl.sqlString(this.currencyCode) +
                     ", FExRateSrcCode = " + dbl.sqlString(this.exRateSrcCode) +
                     ", FExRateCode = " + dbl.sqlString(this.exRateCode) +
                     ", FPortType = " + this.strPortType +
                     ", FCosting = " + this.strCosting +
                     ", FEnabled = " + this.strEnabled +
                     ", FInceptionDate = " + dbl.sqlDate(this.inceptionDate) +
                     ", FExpirationDate = " + dbl.sqlDate(this.expirationDate) +
     ", FStorageInitDate = " + dbl.sqlDate(this.storageInitDate) +
                     ", FDesc = " + dbl.sqlString(this.desc) +
                     ", FCheckState = " + (pub.getSysCheckState()?"0":"1") +
                     ", FCreator = " + dbl.sqlString(this.creatorCode) +
                     ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                     ", FCheckUser = " +  (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                     " where FPortCode = " +
                     dbl.sqlString(this.oldPortCode) +
                     " and FStartDate = " +
                     dbl.sqlDate(this.oldStartDate);
            }
            else if (btOper == YssCons.OP_DEL) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_Portfolio") + " set FCheckState = 2 " +
                     "where FPortCode = " +
                     dbl.sqlString(this.portCode) +
                     " and FStartDate = " +
                     dbl.sqlDate(this.startDate);
            }
            else if (btOper == YssCons.OP_AUDIT) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_Portfolio") + " set FCheckState = " +
                     this.checkStateId;
               if (this.checkStateId == 1) {
                  strSql += ", FCheckUser = '" +
                        pub.getUserCode() + "' , FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'";
               }
               strSql += " where FPortCode = " +
                     dbl.sqlString(this.portCode) +
                     " and FStartDate = " +
                     dbl.sqlDate(this.startDate);
            }

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            if (btOper == YssCons.OP_ADD || btOper == YssCons.OP_EDIT) {
               if (this.portSub != null && ! (this.portSub.length() ==0)) {
                  PortfolioSubBean portSub = new PortfolioSubBean();
                  portSub.setYssPub(pub);
                  portSub.saveMutliSetting(this.portSub);
               }
            }
            else if (btOper == YssCons.OP_DEL) {
               strSql =
                     "update " + pub.yssGetTableName("Tb_Para_Portfolio_Relaship") + " set FCheckState = 2 " +
                     "where FPortCode = " +
                     dbl.sqlString(this.portCode) +
                     " and FStartDate = " +
                     dbl.sqlDate(this.startDate);

               dbl.executeSql(strSql);
            }
            else if (btOper == YssCons.OP_AUDIT) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_Portfolio_Relaship") + " set FCheckState = " +
                     this.checkStateId;
               if (this.checkStateId == 1) {
                  strSql += ", FCheckUser = '" +
                        pub.getUserCode() + "' , FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'";
               }
               strSql += " where FPortCode = " +
                     dbl.sqlString(this.portCode) +
                     " and FStartDate = " +
                     dbl.sqlDate(this.startDate);

               dbl.executeSql(strSql);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
         }
         catch (Exception e) {
            dbl.endTransFinal(conn, bTrans);
            throw new YssException("更新组合设置信息出错\r\n" + e.getMessage(), e);
         }
      }
     */


    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_Portfolio") +
                "" +
                "(FPortCode,FPortName,FStartDate,FPortShortName,FAssetCode,FAssetGroupCode," +
                "FPortCury,FBaseRateSrcCode,FBaseRateCode,FPortRateSrcCode,FPortRateCode,FPortType,FCosting,FEnabled,FInceptionDate,FInceptionAsset,FAssetSource," +
                "FAssetType,FSubAssetType," + //增加资产类型和资产子类型，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                "FCuryCode,"+ //add by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                "FAimETFCode," +// add by zhangjun 2012-04-26 ETF联接基金
                "FExpirationDate,FStorageInitDate,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FRollAsset)" +
                " values(" + dbl.sqlString(this.portCode) + "," +
                dbl.sqlString(this.portName) + "," +
                dbl.sqlDate(this.startDate) +
                " ," + dbl.sqlString(this.portShortName) + "," +
                dbl.sqlString(this.assetCode) + "," +
                dbl.sqlString(this.assetGroupCode) + "," +
                dbl.sqlString(this.currencyCode) + "," +
                dbl.sqlString(this.baseRateSrcCode) + "," +
                dbl.sqlString(this.baseRateCode) + "," +
                dbl.sqlString(this.portRateSrcCode) + "," +
                dbl.sqlString(this.portRateCode) + "," +
                this.strPortType + "," + this.strCosting + "," +
                this.strEnabled + "," +
                dbl.sqlDate(this.inceptionDate) + "," +
                this.inceptionAsset + "," +
                dbl.sqlString(this.assetSource) + "," +
                dbl.sqlString(this.assetType) + "," + //资产类型，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                dbl.sqlString(this.subAssetType) + "," +//资产子类型，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                dbl.sqlString(this.curyCode) + "," +//基础货币 add by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                dbl.sqlString(this.aimETFCode) + "," + //目标ETF代码  add  by zhangjun 2012-04-26 ETF联接基金
                dbl.sqlDate(this.expirationDate) + "," +
                dbl.sqlDate(this.storageInitDate) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                "," +
                this.rollAsset +
                ")";

            dbl.executeSql(strSql);
            if (this.portSub != null && ! (this.portSub.length() == 0)) {
                PortfolioSubBean portSub = new PortfolioSubBean();
                portSub.setYssPub(pub);
                portSub.saveMutliSetting(this.portSub);
                //-------------------------------------------------------------
                PortfolioSubBean aFilterType = null;
                String aSubData = "";
                String aTmp = "";
                String[] aMutilRowAry = null;
                aMutilRowAry = this.portSub.split(YssCons.YSS_LINESPLITMARK);
                for (int i = 1; i <= aMutilRowAry.length; i++) {
                    if (i == aMutilRowAry.length) {
                        aTmp = aMutilRowAry[0];
                    } else {
                        aTmp = aMutilRowAry[i];
                    }
//                    if (aMutilRowAry[i - 1].split("\t")[1].equals(aTmp.split("\t")[1])) {
//                        continue;
//                    }
                    aFilterType = new PortfolioSubBean();
                    aFilterType.setYssPub(pub);
                    aFilterType.parseRowStr(aMutilRowAry[i - 1]);
                    aSubData += aFilterType.getListViewData1() + "\n\n";
                }
                this.setASubData(aSubData);

            }
            RollAssetBean asset = new RollAssetBean();
            asset.setYssPub(pub);
            //初始化滚动资产各成员变量;
            String str = YssFun.formatDate(this.inceptionDate) + "\t" +
                this.portCode + "\t" +
                YssFun.formatDate(this.inceptionDate, "yyyyMM") + "\t" +
                this.currencyCode + "\t" +
                this.inceptionAsset + "\t" +
                this.rollAsset + "\t" + "1\t1\t" +
                YssFun.formatDate(this.inceptionDate) + "\t" +
                this.portCode + "\t" +
                YssFun.formatDate(inceptionDate, "yyyyMM") + "\t" +
                super.buildRecLog() + "\tnull";

            //     asset.setAttr(this.inceptionDate.toString().substring(0,5),
            //                   this.portCode,this.inceptionDate.toString(),this.rollAsset);
            asset.saveMutliSetting(str);
            
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
            pub.addPortBaseCury(portCode, curyCode);// QDV4上海2010年12月10日02_A lidaolong 2011.01.25
        }

        catch (Exception e) {
            throw new YssException("增加组合信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
    }
    
    /**
     * addSetting
     *#1829 add   by yeshenghong 20111206
     * @return String
     */
    public String createSetting(String preCode) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        int setCode = 0;
        try {//BUG3733 modified by yeshenghong 20120222
        	rs = dbl.openResultSet(
  	               "select count(1) from LSetlist where upper(FSetID)='" +
  	               this.assetCode + "' or upper(FSetName)='" + this.portName.toUpperCase() + "'");
  	        if(rs.next())
  	        {
  	         setCode = rs.getInt(1);
  	         rs.getStatement().close();
  	        }
  	        if (setCode > 0)
  	           throw new YssException("资产代码/名称和现有套账重复！");

  	        rs = dbl.openResultSet(
  	               "select max(fsetcode) as fsetcode from lsetlist");
  	        if(rs.next()) {
  	           setCode = rs.getInt("fsetcode") + 1;
  	           rs.getStatement().close();
  	        }
  	        if (setCode > 999)
  	            throw new YssException("无法容纳更多套账，请使用另一个数据库！");
  	        
//            conn.setAutoCommit(false);
//            bTrans = true;
            String preAssetCode = this.getPortfolioInfo(preCode);//BUG5458 yeshenghong 20120906
            if(!this.assetCode.equals(preAssetCode))//资产代码不一致新建套账
            {               
				Calendar today = Calendar.getInstance();
				int year = today.get(Calendar.YEAR);
				int[] setInfos = this.getAssetSetInfo(preAssetCode, year);				
				int startYear = setInfos[0];
				int startMonth = setInfos[1];
				int preSetYear = setInfos[2];
				int preSetCode = setInfos[3];
				setCode = this.CreateSetTable(startYear, startMonth,
						preSetYear, preSetCode,conn,bTrans);//modified by yeshenghong BUG5458 改为先创建套帐 这样可以避免创建失败仍然插入组合信息
				strSql = "insert into "
//						+ pub.yssGetTableName("Tb_Para_Portfolio")
						+ "Tb_" + this.assetGroupCode + "_Para_Portfolio"
						+ ""
						+ "(FPortCode,FPortName,FStartDate,FPortShortName,FAssetCode,FAssetGroupCode,"
						+ "FPortCury,FBaseRateSrcCode,FBaseRateCode,FPortRateSrcCode,FPortRateCode,FPortType,FCosting,FEnabled,FInceptionDate,FInceptionAsset,FAssetSource,"
						+ "FAssetType,FSubAssetType,"
						+ // 增加资产类型和资产子类型，panjunfang add 20090729,MS00003
							// 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
						"FCuryCode,"
						+ // add by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
						"FAimETFCode,"
						+ // add by zhangjun 2012-04-26 ETF联接基金
						"FExpirationDate,FStorageInitDate,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FRollAsset)"
						+ " values("
						+ dbl.sqlString(this.portCode)
						+ ","
						+ dbl.sqlString(this.portName)
						+ ","
						+ dbl.sqlDate(this.startDate)
						+ " ,"
						+ dbl.sqlString(this.portShortName)
						+ ","
						+ dbl.sqlString(this.assetCode)
						+ ","
						+ dbl.sqlString(this.assetGroupCode)
						+ ","
						+ dbl.sqlString(this.currencyCode)
						+ ","
						+ dbl.sqlString(this.baseRateSrcCode)
						+ ","
						+ dbl.sqlString(this.baseRateCode)
						+ ","
						+ dbl.sqlString(this.portRateSrcCode)
						+ ","
						+ dbl.sqlString(this.portRateCode)
						+ ","
						+ this.strPortType
						+ ","
						+ this.strCosting
						+ ","
						+ this.strEnabled
						+ ","
						+ dbl.sqlDate(this.inceptionDate)
						+ ","
						+ this.inceptionAsset
						+ ","
						+ dbl.sqlString(this.assetSource)
						+ ","
						+ dbl.sqlString(this.assetType)
						+ ","
						+ // 资产类型，panjunfang add 20090729,MS00003 数据接口参数设置
							// QDV4.1赢时胜（上海）2009年4月20日03_A
						dbl.sqlString(this.subAssetType)
						+ ","
						+ // 资产子类型，panjunfang add 20090729,MS00003 数据接口参数设置
							// QDV4.1赢时胜（上海）2009年4月20日03_A
						dbl.sqlString(this.curyCode)
						+ ","
						+ // 基础货币 add by lidaolong
							// 2011.01.24;QDV4上海2010年12月10日02_A
						dbl.sqlString(this.aimETFCode)
						+ ","
						+ // 目标ETF代码 add by zhangjun ETF联接基金
						dbl.sqlDate(this.expirationDate)
						+ ","
						+ dbl.sqlDate(this.storageInitDate)
						+ ","
						+ dbl.sqlString(this.desc)
						+ ","
						+ "1,"
						+ // 直接在已审核分页
						dbl.sqlString(this.creatorCode)
						+ ","
						+ dbl.sqlString(this.creatorTime)
						+ ","
						+ (pub.getSysCheckState() ? "' '" : dbl
								.sqlString(this.creatorCode)) + ","
						+ this.rollAsset + ")";

				dbl.executeSql(strSql);
				if (this.inceptionAsset == null) {
					this.inceptionAsset = new BigDecimal(0);
				}
				if (this.portSub != null && !(this.portSub.length() == 0)) {
					PortfolioSubBean portSub = new PortfolioSubBean();
					portSub.setYssPub(pub);
					portSub.saveMutliSetting(this.portSub);
					// -------------------------------------------------------------
					PortfolioSubBean aFilterType = null;
					String aSubData = "";
					String aTmp = "";
					String[] aMutilRowAry = null;
					aMutilRowAry = this.portSub
							.split(YssCons.YSS_LINESPLITMARK);
					for (int i = 1; i <= aMutilRowAry.length; i++) {
						if (i == aMutilRowAry.length) {
							aTmp = aMutilRowAry[0];
						} else {
							aTmp = aMutilRowAry[i];
						}
						if (aMutilRowAry[i - 1].split("\t")[1].equals(aTmp
								.split("\t")[1])) {
							continue;
						}
						aFilterType = new PortfolioSubBean();
						aFilterType.setYssPub(pub);
						aFilterType.parseRowStr(aMutilRowAry[i - 1]);
						aSubData += aFilterType.getListViewData1() + "\n\n";
					}
					this.setASubData(aSubData);

				}
				RollAssetBean asset = new RollAssetBean();
				asset.setYssPub(pub);
				// 初始化滚动资产各成员变量;
				String str = YssFun.formatDate(this.inceptionDate) + "\t"
						+ this.portCode + "\t"
						+ YssFun.formatDate(this.inceptionDate, "yyyyMM")
						+ "\t" + this.currencyCode + "\t" + this.inceptionAsset
						+ "\t" + this.rollAsset + "\t" + "1\t1\t"
						+ YssFun.formatDate(this.inceptionDate) + "\t"
						+ this.portCode + "\t"
						+ YssFun.formatDate(inceptionDate, "yyyyMM") + "\t"
						+ super.buildRecLog() + "\tnull";
				asset.saveMutliSetting(str);
				
				
				/**add by liuxiaojun 20130815  stroy 4156  复制组合级的数据*/
				if(assetGroupCode.equals(pub.getPrefixTB()))
				{
				}else {
					String sql = "drop table Tb_" +assetGroupCode + "_Para_Receiver";
					dbl.executeSql(sql);
		            sql = "create table Tb_" +assetGroupCode + "_Para_Receiver as select * from " + pub.yssGetTableName("Tb_Para_Receiver");
		            dbl.executeSql(sql);
		            sql = "update Tb_" +assetGroupCode + "_Para_Receiver set FPortCode= " + portCode;
		            dbl.executeSql(sql);
				}
				
				if(assetGroupCode.equals(pub.getPrefixTB()))
				{
				}else {
		            String sql = "drop table Tb_" +assetGroupCode + "_Para_ValuationDays";
					dbl.executeSql(sql);
		            sql = "create table Tb_" +assetGroupCode + "_Para_ValuationDays as select * from " + pub.yssGetTableName("Tb_Para_ValuationDays");
		            dbl.executeSql(sql);
		            sql = "update Tb_" +assetGroupCode + "_Para_ValuationDays set FPortCode= " + portCode;
		            dbl.executeSql(sql);
				}
				
				if(assetGroupCode.equals(pub.getPrefixTB()))
				{
				}else {
					String sql = "drop table Tb_" +assetGroupCode + "_Para_CashAccount";
					dbl.executeSql(sql);
		            sql = "create table Tb_" +assetGroupCode + "_Para_CashAccount as select * from " + pub.yssGetTableName("Tb_Para_CashAccount");
		            dbl.executeSql(sql);
		            sql = "update Tb_" +assetGroupCode + "_Para_CashAccount set FPortCode= " + portCode;
		            dbl.executeSql(sql);
				}
				
				if(assetGroupCode.equals(pub.getPrefixTB()))
				{
				}else {
					String sql = "drop table Tb_" +assetGroupCode + "_Para_TradeFeeLink";
					dbl.executeSql(sql);
		            sql = "create table Tb_" +assetGroupCode + "_Para_TradeFeeLink as select * from " + pub.yssGetTableName("Tb_Para_TradeFeeLink");
		            dbl.executeSql(sql);
		            sql = "update Tb_" +assetGroupCode + "_Para_TradeFeeLink set FPortCode= " + portCode;
		            dbl.executeSql(sql);
				}
				
				if(assetGroupCode.equals(pub.getPrefixTB()))
				{
				}else {
					String sql = "drop table Tb_" +assetGroupCode + "_Para_CashAccLink";
					dbl.executeSql(sql);
		            sql = "create table Tb_" +assetGroupCode + "_Para_CashAccLink as select * from " + pub.yssGetTableName("Tb_Para_CashAccLink");
		            dbl.executeSql(sql);
		            sql = "update Tb_" +assetGroupCode + "_Para_CashAccLink set FPortCode= " + portCode;
		            dbl.executeSql(sql);
				}
				if(assetGroupCode.equals(pub.getPrefixTB()))
				{
				}else {
					String sql = "drop table Tb_" +assetGroupCode + "_Para_CurrencyWay";
					dbl.executeSql(sql);
		            sql = "create table Tb_" +assetGroupCode + "_Para_CurrencyWay as select * from " + pub.yssGetTableName("Tb_Para_CurrencyWay");
		            dbl.executeSql(sql);
		            sql = "update Tb_" +assetGroupCode + "_Para_CurrencyWay set FPortCode= " + portCode;
		            dbl.executeSql(sql);
				}
	            
				if(assetGroupCode.equals(pub.getPrefixTB()))
				{
				}else {
					String sql = "drop table Tb_" +assetGroupCode + "_PARA_FIXEDFEECFG";
					dbl.executeSql(sql);
		            sql = "create table Tb_" +assetGroupCode + "_PARA_FIXEDFEECFG as select * from " + pub.yssGetTableName("Tb_PARA_FIXEDFEECFG");
		            dbl.executeSql(sql);
		            sql = "update Tb_" +assetGroupCode + "_PARA_FIXEDFEECFG set FPortCode= " + portCode;
		            dbl.executeSql(sql);
				}
	            
				if(assetGroupCode.equals(pub.getPrefixTB()))
				{
				}else {
					String sql = "drop table Tb_" +assetGroupCode + "_DAO_BrokerRate";
					dbl.executeSql(sql);
		            sql = "create table Tb_" +assetGroupCode + "_DAO_BrokerRate as select * from " + pub.yssGetTableName("Tb_DAO_BrokerRate");
		            dbl.executeSql(sql);
		            sql = "update Tb_" +assetGroupCode + "_DAO_BrokerRate set FPortCode= " + portCode;
		            dbl.executeSql(sql);
				}
	            /**end stroy 4156*/

           }
            
            
            if(bTrans)
            {
            	conn.commit();
            	conn.setAutoCommit(true);
            	bTrans = false;
            }
            
            pub.addPortBaseCury(portCode, curyCode);// QDV4上海2010年12月10日02_A lidaolong 2011.01.25
        }
        catch (Exception e) {
            throw new YssException("创建组合信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }
    
    private int[] getAssetSetInfo(String preAssetCode,int year) throws YssException{
    	 ResultSet rs = null;
         String sqlStr = "";
         int[] setInfos = new int[4];
         try {
         	//FLinkCode,FLinkName,FPortCode,FBookSetCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser
        	 sqlStr = "Select fyear, fsetcode,fstartyear,fstartmonth from LSetList where fsetid = " +  
        	 		dbl.sqlString(preAssetCode) + " order by fyear desc";
        	 rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
        	 if(rs.next())
        	 {
        		
    			int preSetYear = rs.getInt("fyear");
    			 //同年没有 则返回上年
    			setInfos[0] = rs.getInt("fstartyear");
    			setInfos[1] = rs.getInt("fstartmonth");
    			if(year>=preSetYear){
    				setInfos[2] = preSetYear;
    			}else
    			{
    				setInfos[2] = year;
    			}
    			setInfos[3] = rs.getInt("fsetcode");
        	 }
        	 return setInfos;
         } catch (Exception e) {
             throw new YssException("", e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
    	
    }
    
    /**
     * add by huangqirong 2012-11-28 bug #6256
     * */
    private String getAssetSet(String preAssetCode) throws YssException{
   	 	ResultSet rs = null;
        String sqlStr = "";
        String result = "";
        try {        	
       	 sqlStr = "Select * from LSetList where fsetid = " +  
       	 		dbl.sqlString(preAssetCode) + " order by fyear desc";
       	 rs = dbl.queryByPreparedStatement(sqlStr);
       	 if(rs.next())
       	 {
       		result = rs.getString("FSetCode");
       	 }
       	 return result;
        } catch (Exception e) {
            throw new YssException("", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
   	
   }

	/**
	 * add huhuichao 2013-7-24 BUG 8602 新建组合群后，系统没有自动创建电子划款指令表
	 * @方法名：yssSetPrefix 获取套账号前缀
	 * @param int lnSet
	 * @return String
	 */
	public String yssSetPrefix(int lnSet) {
		return getTablePrefix(false, true, 0, lnSet);
	}

	/**
	 * add huhuichao 2013-7-24 BUG 8602 新建组合群后，系统没有自动创建电子划款指令表
	 * 
	 * @方法名：getTablePrefix 获取套账号前缀
	 * @param boolean bYear, boolean bSet, int lYear, int lnSet
	 * @return String
	 */
	public String getTablePrefix(boolean bYear, boolean bSet, int lYear,
			int lnSet) {
		String stmp;

		if ((lYear > 999 || !bYear) && (lnSet != 0 || !bSet)) { // 年份四位
			stmp = "A" + (bYear ? "" + lYear : "")
					+ (bSet ? (new DecimalFormat("000")).format(lnSet) : "");
			return (stmp.length() == 1) ? "" : stmp;
		}
		return "";
	}

	/**
	 * add huhuichao 2013-7-24 BUG 8602 新建组合群后，系统没有自动创建电子划款指令表
	 * 
	 * @方法名：createTransferOrder 根据套账号新建电子划拨表
	 * @throws YssException
	 * @throws SQLException
	 * @param
	 * @return void
	 */
	private void createTransferOrder(int lLoop) throws SQLException,
			YssException {
		String sql = "";
		try {
			String tableName = this.yssSetPrefix(lLoop) + "jjhkzl";
			if (dbl.dbType == YssCons.DB_DB2) {
				if (!dbl.yssTableExist(tableName)) {
					sql = "Create table " + tableName
							+ "(FZLDATE	TIMESTAMP	not null,"
							+ " FHKDATE	TIMESTAMP	not null,"
							+ " FNUM		VARCHAR(20)	not null,"
							+ " FDZDATE	TIMESTAMP	not null,"
							+ " FHKREN	VARCHAR(200)	not null,"
							+ " FHKBANK	VARCHAR(200)	not null,"
							+ " FHKACCT	VARCHAR(200)	not null,"
							+ " FHKJE	DECIMAL (18, 4)	not null,"
							+ " FHKREMARK	VARCHAR(400)	not null,"
							+ " FSKREN	VARCHAR(100)	not null,"
							+ " FSKBANK	VARCHAR(100)	not null,"
							+ " FSKACCT	VARCHAR(200)	not null,"
							+ " FSKYT		VARCHAR(200)	not null,"
							+ " FDELBZ	VARCHAR(1)	,				 "
							+ " FZLTYPE	DECIMAL (1, 0)		not null,"
							+ " FHKTYPE	VARCHAR(100)	not null,"
							+ " FHKTYPE2	VARCHAR(50),			 "
							+ " FSN		VARCHAR(30),			 "
							+ " SEQ_NO	VARCHAR(50)	not null,"
							+ " RESULT	VARCHAR(30),			 "
							+ " REMARK	VARCHAR(200),			 "
							+ " CHECKER_CODE	VARCHAR(30),		 "
							+ " FPK_BOOKMARK	VARCHAR(100),		 "
							+ " TIMESTMP	VARCHAR(50)	not null,"
							+ " OPERATION_TYPE	VARCHAR(50)	not null,"
							+ " FYHSN	VARCHAR(50)	,"
							+ " FSH	VARCHAR(30)	not null,"
							+ " FZZR	VARCHAR(30)	not null,"
							+ " FCHK	VARCHAR(30)	not null,"
							+ " FHKREMARKN	VARCHAR(200)	," + "constraint PK_"
							+ tableName
							+ " primary key (FZLDATE, FNUM, FZLTYPE, FSH))";
					dbl.executeSql(sql);
				}
			}
			if (dbl.dbType == YssCons.DB_ORA) {
				if (!dbl.yssTableExist(tableName)) {
					sql = "Create table " + tableName
							+ "(FZLDATE	DATE	not null,"
							+ " FHKDATE	DATE	not null,"
							+ " FNUM		VARCHAR2(20)	not null,"
							+ " FDZDATE	DATE	not null,"
							+ " FHKREN	VARCHAR2(100)	not null,"
							+ " FHKBANK	VARCHAR2(100)	not null,"
							+ " FHKACCT	VARCHAR2(100)	not null,"
							+ " FHKJE		NUMBER(18,4)	not null,"
							+ " FHKREMARK	VARCHAR2(200)	not null,"
							+ " FSKREN	VARCHAR2(100)	not null,"
							+ " FSKBANK	VARCHAR2(100)	not null,"
							+ " FSKACCT	VARCHAR2(200)	not null,"
							+ " FSKYT		VARCHAR2(200)	not null,"
							+ " FDELBZ	VARCHAR2(1)	,				 "
							+ " FZLTYPE	NUMBER(1)		not null,"
							+ " FHKTYPE	VARCHAR2(100)	not null,"
							+ " FHKTYPE2	VARCHAR2(50),			 "
							+ " FSN		VARCHAR2(30),			 "
							+ " SEQ_NO	VARCHAR2(50)	not null,"
							+ " RESULT	VARCHAR2(30),			 "
							+ " REMARK	VARCHAR2(200),			 "
							+ " CHECKER_CODE	VARCHAR2(30),		 "
							+ " FPK_BOOKMARK	VARCHAR2(100),		 "
							+ " TIMESTMP	VARCHAR2(50)	not null,"
							+ " OPERATION_TYPE	VARCHAR2(50)	not null,"
							+ " FYHSN	VARCHAR2(50)	,"
							+ " FSH	VARCHAR2(30)	not null,"
							+ " FZZR	VARCHAR2(30)	not null,"
							+ " FCHK	VARCHAR2(30)	not null,"
							+ " FHKREMARKN	VARCHAR2(200)	," + "constraint PK_"
							+ tableName
							+ " primary key (FZLDATE, FNUM, FZLTYPE, FSH))";
					dbl.executeSql(sql);
				}
			}
		} catch (Exception e) {
			throw new YssException("根据套账号新建电子划拨表出错！");
		} finally {
		}
	}

   public int CreateSetTable(int startYear, int startMonth, int preSetYear,int preSetCode,Connection conn,boolean bTrans) throws YssException {
	      ResultSet rs = null;
	      String BackStr = null;
	      String SqlStr = null;
	      int setCode = 0;
	      Calendar today = Calendar.getInstance();
  		  int year = today.get(Calendar.YEAR);//当年
  		  String tableName = "";
	      try {
	         rs = dbl.openResultSet(
	               "select count(1) from LSetlist where upper(FSetID)='" +
	               this.assetCode + "' or upper(FSetName)='" + this.portName.toUpperCase() + "'");
	         rs.next();
	         setCode = rs.getInt(1);
	         rs.getStatement().close();
	         if (setCode > 0)
	            throw new YssException("资产代码/名称和现有套账重复！");

	         rs = dbl.openResultSet(
	               "select max(fsetcode) as fsetcode from lsetlist");
	         if(rs.next()) {
	            setCode = rs.getInt("fsetcode") + 1;
	         }
	         rs.getStatement().close();
	         if (setCode > 999)
	            throw new YssException("无法容纳更多套账，请使用另一个数据库！");
	         new DbTable(pub).createSet(getTablePrefix(year, setCode)); //创建套账表
	         conn.setAutoCommit(false);
	         bTrans = true;
	         SqlStr = " insert into lsetlist(FYEAR,FSETID,FSETCODE,FSETNAME, FMANAGER, " +  //插入套账设置
 					"FSTARTYEAR,FSTARTMONTH, FMONTH,  FACCLEN, FSTARTED, FDJJBZ, FPSETCODE,FSETLEVEL, FTSETCODE)" +
 					"select distinct " +
 					"'"+ year +"'," + 
 					dbl.sqlString(this.assetCode)+ " ," +
 					"'"+setCode+"'," +
 					dbl.sqlString(this.portName)+
 					", FMANAGER, FSTARTYEAR, FSTARTMONTH ," + 
 					" FMONTH,  FACCLEN, FSTARTED, FDJJBZ, FPSETCODE,FSETLEVEL, FTSETCODE from lsetlist " +  
 					"where fsetcode = " + preSetCode + " and fyear = " + preSetYear;
	         dbl.executeSql(SqlStr);
	         //创建新套账后要创建相应的会计期间信息
	         for (int i = startMonth; i <= 12; i++) {
	        	 String sql = "insert into AccountingPeriod values(" + setCode + "," + year + "," + i + "," +
                 dbl.sqlDate(YssFun.toDate(year + "-" + YssFun.formatNumber(i, "00") + "-01")) + "," +
                 dbl.sqlDate(YssFun.toDate(year + "-" + YssFun.formatNumber(i, "00") + "-" + YssFun.endOfMonth(year, i))) + ") ";
	            dbl.executeSql(sql);
	         }
	         
	         SqlStr = "insert into " + getTablePrefix(year, setCode) + "LAccount" +//科目设置
	         " select * from " + getTablePrefix(preSetYear, preSetCode) + "LAccount";
	         tableName = "科目设置表";//BUG7128 modified by yeshenghong  20130225
	         dbl.executeSql(SqlStr);
      
	         SqlStr = "insert into " + getTablePrefix(year, setCode) + "LCurrency" + //币种设置 
 			 " select * from " + getTablePrefix(preSetYear, preSetCode) + "LCurrency";
	         tableName = "币种设置表";//BUG7128 modified by yeshenghong  20130225
	         dbl.executeSql(SqlStr);
	         
	         SqlStr = "insert into " + getTablePrefix(year, setCode) + "AuxiAccSet" +//辅助核算项
	         " select * from " + getTablePrefix(preSetYear, preSetCode) + "AuxiAccSet";
	         tableName = "辅助核算项表";//BUG7128 modified by yeshenghong  20130225
	         dbl.executeSql(SqlStr);
	         
	         SqlStr = "insert into " + getTablePrefix(year, setCode) + "LTabCell" +//报表格式
	         " select * from " + getTablePrefix(preSetYear, preSetCode) + "LTabCell";
	         tableName = "报表格式表";//BUG7128 modified by yeshenghong  20130225
	         dbl.executeSql(SqlStr);
	         
	         SqlStr = "insert into " + getTablePrefix(year, setCode) + "LTabList" +//报表目录
	         " select * from " + getTablePrefix(preSetYear, preSetCode) + "LTabList";
	         tableName = "报表目录表";//BUG7128 modified by yeshenghong  20130225
	         dbl.executeSql(SqlStr);
	         
	         /**add---huhuichao 2013-7-17 BUG  8602 根据套账号新建电子划拨表*/
	 		this.createTransferOrder(setCode);
	 		/**end---huhuichao 2013-7-17 BUG  8602*/
	         return setCode;
//	         return "套账【" + this.portName + "】创建成功！"; //目前是成功返回字符串，失败不返回，
	      }
	      catch (Exception es) {
	         BackStr = "创建套账出错！" + "请检查" + tableName + "字段信息！";//BUG7128 modified by yeshenghong  20130225
	         new DbTable(pub).dropSet(getTablePrefix(year, setCode));
	         throw new YssException(BackStr, es);
	      }
	      finally { 
	         dbl.closeResultSetFinal(rs);
	      }
	   }
   
   private  String getTablePrefix(int lYear, int lsetcode) {
	      String stmp;

	      if ((lYear > 999) && (lsetcode != 0)) { //年份四位
	         stmp = "A" + "" + lYear  +
	               ((new DecimalFormat("000")).format(lsetcode));
	         return (stmp.length() == 1) ? "" : stmp;
	      }
	      return "";
	   }

    protected String getPortfolioInfo(String preCode) throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        String portInfo = "";
        try {
        	//FLinkCode,FLinkName,FPortCode,FBookSetCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser
            sqlStr = "select distinct fassetcode from " +
            		pub.yssGetTableName("Tb_Para_Portfolio") +
            		" where FPortCode = " + dbl.sqlString(preCode);
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            if (rs.next()) {
            	portInfo = rs.getString("fassetcode");
            }
           
            return portInfo;
        } catch (Exception e) {
            throw new YssException("", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    //modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
//    protected String getBookSetLink(String preCode, String setCode) throws YssException{
//        ResultSet rs = null;
//        String sqlStr = "";
//        String booksets = "";
//        try {
//            sqlStr = "select distinct flinkcode, fdesc, flinkname,fcreator," +
//            		"fcreatetime,fcheckuser,fchecktime from " +
//                pub.yssGetTableName("Tb_Vch_PortSetLink") +
//                " where FPortCode = " +
//                dbl.sqlString(preCode);
//            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
//            //FLinkCode,FLinkName,FPortCode,FBookSetCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime
//            if (rs.next()) {
//                booksets += "'" + rs.getString("flinkcode") + "',";
//                booksets += "'" + rs.getString("flinkname") + "',";
//                booksets += "'" + this.portCode + "',";
//                booksets += "'" + setCode + "',";
//                booksets += rs.getString("fdesc")==null ? "'',":"'" + rs.getString("fdesc") + "',";
//                booksets += "'1',";
//                booksets += "'" + rs.getString("fcreator") + "',"; 
//  	          	booksets += "'" + rs.getString("fcreatetime") + "',"; 
//  	          	booksets += "'" + rs.getString("fcheckuser") + "',"; 
//  	          	booksets += "'" + rs.getString("fchecktime") + "'"; 
//            }
//           
//            return booksets;
//        } catch (Exception e) {
//            throw new YssException("", e);
//        } finally {
//            dbl.closeResultSetFinal(rs);
//        }
//    }
    
    
    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {

            if (this.portSub != null && ! (this.portSub.length() == 0)) {
                String[] bMutilRowAry = null;
                bMutilRowAry = this.portSub.split(YssCons.YSS_LINESPLITMARK);
                String bSubData = "";
                String bTmp = "";
                PortfolioSubBean bFilterType = null;
                for (int i = 1; i <= bMutilRowAry.length; i++) {
                    if (i == bMutilRowAry.length) {
                        bTmp = bMutilRowAry[0];
                    } else {
                        bTmp = bMutilRowAry[i];
                    }
                    if (bMutilRowAry[i - 1].split("\t")[1].equals(bTmp.split("\t")[1])) {
                        continue;
                    }
                    bFilterType = new PortfolioSubBean();
                    bFilterType.setYssPub(pub);
                    bFilterType.parseRowStr(bMutilRowAry[i - 1]);
                    bFilterType.setPortCode(this.oldPortCode);
                    bFilterType.setStartDate(this.oldStartDate);
                    bSubData += bFilterType.getListViewData1() + "<port>";
                }
                this.setBSubData(bSubData);
            }
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " set FPortCode = " +
                dbl.sqlString(this.portCode) + ", FPortName = " +
                dbl.sqlString(this.portName) +
                ", FStartDate = " + dbl.sqlDate(this.startDate) +
                ", FPortShortName = " + dbl.sqlString(this.portShortName) +
                ", FAssetCode = " + dbl.sqlString(this.assetCode) +
                ", FAssetGroupCode = " + dbl.sqlString(this.assetGroupCode) +
                ", FPortCury = " + dbl.sqlString(this.currencyCode) +
                ", FBaseRateSrcCode = " + dbl.sqlString(this.baseRateSrcCode) +
                ", FBaseRateCode = " + dbl.sqlString(this.baseRateCode) +
                ", FPortRateSrcCode = " + dbl.sqlString(this.portRateSrcCode) +
                ", FPortRateCode = " + dbl.sqlString(this.portRateCode) +
                ", FPortType = " + this.strPortType +
                ", FCosting = " + this.strCosting +
                ", FEnabled = " + this.strEnabled +
                ", FInceptionDate = " + dbl.sqlDate(this.inceptionDate) +
                ", FInceptionAsset = " + this.inceptionAsset +
                ", FAssetSource = " + dbl.sqlString(this.assetSource) +
                ", FAssetType = " + dbl.sqlString(this.assetType) + //资产类型，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                ", FSubAssetType = " + dbl.sqlString(this.subAssetType) + //资产子类型，panjunfang add 20090729,MS00003 数据接口参数设置 QDV4.1赢时胜（上海）2009年4月20日03_A
                ", FCuryCode = " + dbl.sqlString(this.curyCode) + //基础货币 add by lidaolong 2011.01.24 QDV4上海2010年12月10日02_A
                
                ", FAimETFCode = " + dbl.sqlString(this.aimETFCode)+//目标ETF代码 add by zhangjun ETF
                
                ", FExpirationDate = " + dbl.sqlDate(this.expirationDate) +
                ", FStorageInitDate = " + dbl.sqlDate(this.storageInitDate) +
                ", FDesc = " + dbl.sqlString(this.desc) +
                ", FRollAsset = " + this.rollAsset +
                ", FCheckState = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCreator = " + dbl.sqlString(this.creatorCode) +
                ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ", FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FPortCode = " +
                dbl.sqlString(this.oldPortCode);//edit by songjie 2011.03.22 不以启用日期更新数据
                //----delete by songjie 2011.03.22 不以启用日期更新数据----//
//                " and FStartDate = " +
//                dbl.sqlDate(this.oldStartDate);
                //----delete by songjie 2011.03.22 不以启用日期更新数据----//
            dbl.executeSql(strSql);
            if (this.portSub != null && ! (this.portSub.length() == 0)) {
                PortfolioSubBean portSubBean = new PortfolioSubBean();
                portSubBean.setYssPub(pub);
                portSubBean.saveMutliSetting(this.portSub);

                String aSubData = "";
                String aTmp = "";
                PortfolioSubBean aFilterType = null;
                String[] aMutilRowAry = null;
                aMutilRowAry = this.portSub.split(YssCons.YSS_LINESPLITMARK);
                for (int i = 1; i <= aMutilRowAry.length; i++) {
                    if (i == aMutilRowAry.length) {
                        aTmp = aMutilRowAry[0];
                    } else {
                        aTmp = aMutilRowAry[i];
                    }
                    if (aMutilRowAry[i - 1].split("\t")[1].equals(aTmp.split("\t")[1])) {
                        continue;
                    }
                    aFilterType = new PortfolioSubBean();
                    aFilterType.setYssPub(pub);
                    aFilterType.parseRowStr(aMutilRowAry[i - 1]);
                    aSubData += aFilterType.getListViewData1() + "<port>";
                }
                this.setASubData(aSubData);
            }
            RollAssetBean asset = new RollAssetBean();
            asset.setYssPub(pub);
            //初始化滚动资产各成员变量;
            String str = YssFun.formatDate(this.inceptionDate) + "\t" +
                this.portCode + "\t" +
                YssFun.formatDate(this.inceptionDate, "yyyyMM") + "\t" +
                this.currencyCode + "\t" +
                this.inceptionAsset + "\t" +
                this.rollAsset + "\t" + "1\t1\t" +
                YssFun.formatDate(this.oldInceptionDate) + "\t" +
                this.oldPortCode + "\t" +
                YssFun.formatDate(this.oldInceptionDate, "yyyyMM") + "\t" +
                super.buildRecLog() + "\r\tnull";
            //     asset.setAttr(this.inceptionDate.toString().substring(0,5),
            //                   this.portCode,this.inceptionDate.toString(),this.rollAsset);
            asset.saveMutliSetting(str);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
            pub.delPortBaseCury(oldPortCode);// QDV4上海2010年12月10日02_A lidaolong 2011.01.25
            pub.addPortBaseCury(portCode, curyCode);// QDV4上海2010年12月10日02_A lidaolong 2011.01.25
        }

        catch (Exception e) {
            throw new YssException("修改组合信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除数据，即放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " set FCheckState = 2 " +
                "where FPortCode = " +
                dbl.sqlString(this.portCode) +
                " and FStartDate = " +
                dbl.sqlDate(this.startDate);
            dbl.executeSql(strSql);
            strSql =
                "update " + pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                " set FCheckState = 2 " +
                "where FPortCode = " +
                dbl.sqlString(this.portCode) +
                " and FStartDate = " +
                dbl.sqlDate(this.startDate);
            dbl.executeSql(strSql);
            strSql = " delete from " + pub.yssGetTableName("Tb_Stock_RollAsset") +
                " where FStorageDate = " +
                dbl.sqlDate(YssFun.formatDate(this.oldInceptionDate)) + " and " +
                " FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(this.oldInceptionDate, "yyyyMM")) +
                " and " +
                " FPortCode = " + dbl.sqlString(this.oldPortCode);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("删除组合信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 此方法已被修改
     * 修改时间：2008年3月20号
     * 修改人：单亮
     * 原方法功能：只能处理组合设置的审核和未审核的单条信息。
     * 新方法功能：可以处理组合设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 修改后不影响原方法的功能
     */

    public void checkSetting() throws YssException {
        //修改前的代码
//      String strSql = "";
//     boolean bTrans = false; //代表是否开始了事务
//     Connection conn = dbl.loadConnection();
//     try {
//        conn.setAutoCommit(false);
//        bTrans = true;
//        strSql = "update " + pub.yssGetTableName("Tb_Para_Portfolio") +
//              " set FCheckState = " +
//              this.checkStateId;
//        if (this.checkStateId == 1) {
//           strSql += ", FCheckUser = '" +
//                 pub.getUserCode() + "' , FCheckTime = '" +
//                 YssFun.formatDatetime(new java.util.Date()) + "'";
//        }
//        strSql += " where FPortCode = " +
//              dbl.sqlString(this.portCode) +
//              " and FStartDate = " +
//              dbl.sqlDate(this.startDate);
//
//        dbl.executeSql(strSql);
//        strSql = "update " + pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
//              " set FCheckState = " +
//              this.checkStateId;
//        if (this.checkStateId == 1) {
//           strSql += ", FCheckUser = '" +
//                 pub.getUserCode() + "' , FCheckTime = '" +
//                 YssFun.formatDatetime(new java.util.Date()) + "'";
//        }
//        strSql += " where FPortCode = " +
//              dbl.sqlString(this.portCode) +
//              " and FStartDate = " +
//              dbl.sqlDate(this.startDate);
//
//        dbl.executeSql(strSql);
//        conn.commit();
//        bTrans = false;
//        conn.setAutoCommit(true);
//     }
//
//     catch (Exception e) {
//        throw new YssException("审核组合信息出错", e);
//     }
//     finally {
//        dbl.endTransFinal(conn, bTrans);
//     }

        //修改后的代码
        //---------------------------------------------------------
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = null;
        String[] arrData = null;
        String sqlStr = "";

        try {
            conn = dbl.loadConnection();
            arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);
            bTrans = true;
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);

                strSql = "update " + pub.yssGetTableName("Tb_Para_Portfolio") +
                    " set FCheckState = " +
                    this.checkStateId;
                if (this.checkStateId == 1) {
                    strSql += ", FCheckUser = '" +
                        pub.getUserCode() + "' , FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'";
                }
                strSql += " where FPortCode = " +
                    dbl.sqlString(this.portCode) +
                    " and FStartDate = " +
                    dbl.sqlDate(this.startDate);
                //执行sql语句
                dbl.executeSql(strSql);

                strSql = "update " +
                    pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                    " set FCheckState = " +
                    this.checkStateId;
                if (this.checkStateId == 1) {
                    strSql += ", FCheckUser = '" +
                        pub.getUserCode() + "' , FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'";
                }
                strSql += " where FPortCode = " +
                    dbl.sqlString(this.portCode) +
                    " and FStartDate = " +
                    dbl.sqlDate(this.startDate);
                //执行sql语句
                dbl.executeSql(strSql);

            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("审核组合信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //---------------------------------------------------------

    }

    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from (" +
                " select * from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FPortCode = " +
                (portCode == null ? "''" : dbl.sqlString(this.portCode)) +
                //edit by songjie 2011.03.10 不以最大的启用日期查询组合设置数据
                " and FCheckState = 1 ) a " +
                //delete by songjie 2011.03.10 不以最大的启用日期查询组合设置数据
//                pub.yssGetTableName("Tb_Para_Portfolio") + " where FPortCode = " +
//               //add by zhouxiang  MS01345    组合设置中存在同一组合代码，不同启用日期的非审核状态的记录    QDV4赢时胜上海2010年06月23日01_B 
//                (portCode == null ? "''" : dbl.sqlString(this.portCode)) +" and fcheckstate =1"+
//                " and fstartdate<="+dbl.sqlDate(new java.util.Date())+"))a " +
                //delete by songjie 2011.03.10 不以最大的启用日期查询组合设置数据
                //------------end-----------------
                " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) d on a.FAssetGroupCode = d.FAssetGroupCode" +
                " left join (select FCuryCode,FCuryName as FCurrencyName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) e on a.FPortCury = e.FCuryCode";

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.portCode = rs.getString("FPortCode") + "";
                this.portName = rs.getString("FPortName") + "";
                this.startDate = rs.getDate("FStartDate");
                this.portShortName = rs.getString("FPortShortName") + "";
                this.assetCode = rs.getString("FAssetCode") + "";
                this.assetGroupCode = rs.getString("FAssetGroupCode") + "";
                this.assetGroupName = rs.getString("FAssetGroupName") + "";
                this.currencyCode = rs.getString("FPortCury") + "";
                this.currencyName = rs.getString("FCurrencyName") + "";
                this.strPortType = rs.getString("FPortType") + "";
                this.strCosting = rs.getString("FCosting") + "";
                this.strEnabled = rs.getString("FEnabled") + "";
                this.inceptionDate = rs.getDate("FInceptionDate");
                this.assetSource = rs.getString("FAssetSource");
                this.expirationDate = rs.getDate("FExpirationDate");
                this.storageInitDate = rs.getDate("FStorageInitDate");
                this.baseRateSrcCode = rs.getString("FBaseRateSrcCode") + "";
                this.baseRateCode = rs.getString("FBaseRateCode") + "";
                this.portRateSrcCode = rs.getString("FPortRateSrcCode") + "";
                this.portRateCode = rs.getString("FPortRateCode") + "";
                this.desc = rs.getString("FDesc") + "";
                this.rollAsset = rs.getBigDecimal("FRollAsset"); // edit by xuqiji 20090428:QDV4赢时胜上海2009年04月27日01_B MS00415 成立资产与滚动金额界面显示数据与数据库中数据不一致
            }
        } catch (Exception e) {
            throw new YssException("获取证券信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            return null;
        }
    }
    
    /**add by liuxiaojun stroy 4156  判断组合群是否初始化*/
    public String getFlag(String sPre) throws YssException{
    	System.out.println(sPre);
    	sPre = sPre.substring(5, sPre.length());
    	System.out.println(sPre);
    	String tableName = "Tb_" +sPre+ "_Para_Portfolio";
    	System.out.println(tableName);
    	String flag="";
    	boolean sFlag = dbl.yssTableExist(tableName);
    	System.out.println(sFlag);
    	if(!sFlag){
    		flag = "already";
    	}
    	
    	return flag;
    }
    /**end stroy 4156*/
    
    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        //判断是否需要的是显示套帐信息的listview数据，如果是的话，调用特定方法将值返回
        //sunkey 20081218 BugNO:MS00072
    	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
    	if (sType != null && sType.startsWith("create")) {
    		String prePortCode = sType.substring(6,sType.length());
            return createSetting(prePortCode); //add by yeshenghong 20111207 #1829
        }
    	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
        if (sType != null && sType.equalsIgnoreCase("listViewAsset")) {
            return getListViewAsset();
        }
        //--------BEGIN: add by wangzuochun MS00893 行情资料中点击当前组合群专用时，系统给出提示 QDV4赢时胜上海2009年12月25日02_B -----------//
        if (sType != null && sType.equalsIgnoreCase("checkPorts")) {
            return checkThisGroupPort() + "";
        }
        //------------END MS00893------------//
        else if (sType != null && sType.equalsIgnoreCase("getport")){//add by yanghaiming 20100712 MS01374 QDV4汇添富2010年06月28日01_AB
        	return getListViewPort();
        }else if(sType != null && sType.equalsIgnoreCase("getPortCodes")){
        	return getPortCodes ();
        }else if(sType != null && sType.equalsIgnoreCase("getFundCode")){ // add by fangjiang 2011.07.13 STORY #1279 
            ResultSet rs = null;
            String sqlStr = "";
            String fundCode = "";
            try {
                sqlStr = " select FAssetCode from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                         " where FPortCode = " + dbl.sqlString(this.portCode);
                rs = dbl.openResultSet(sqlStr);
                while(rs.next()){
                	fundCode = rs.getString("FAssetCode");
                }
                return fundCode;
            } catch (Exception ex) {
                throw new YssException(ex.getMessage());
            } finally {
                dbl.closeResultSetFinal(rs);
            }    
        // add by guolongchao 20120130 STORY 1284 跨组合群获取组合信息---------------------start
        }/*  modify huangqirong 2012-03-29 bug #4084 这段代码没用了
        else if(sType != null && sType.equalsIgnoreCase("getAllPorts")){ 
            ResultSet rs = null;
            ResultSet rs1 = null;
            String sqlStr = "";
            String tabPreFix = "";
            String CurrrentTabPreFix = "";
            String sHeader = "组合代码\t组合名称";  
            StringBuffer bufShow=new StringBuffer();
            StringBuffer bufAll=new StringBuffer();
            String sShowDataStr = "";
            String sAllDataStr = "";
            try 
            {
            	CurrrentTabPreFix=pub.getPrefixTB();//获取当前pub中的表前缀，待循环结束之后将其还原
                sqlStr = "select FTabPreFix  from tb_sys_assetgroup where FLocked=0 and FSysCheck = 1 and FTabInd=1 order by FAssetGroupCode ";
                rs = dbl.openResultSet(sqlStr);                              
                while(rs.next())
                {
                	tabPreFix = rs.getString("FTabPreFix");  
                	if(tabPreFix==null||tabPreFix.trim().length()<=0)
                		continue;
                	pub.setPrefixTB(tabPreFix);
                	sqlStr = "select y.* from " +                    
                        " (select a.*, d.FAssetGroupName as FAssetGroupName, e.FCurrencyName as FCurrencyName,f.FBaseRateSrcName as FBaseRateSrcName,g.FPortRateSrcName as FPortRateSrcName," +
                        " p.FCuryName as FCuryName,"+
                        " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +                        
                        "( select * from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
                        " where FCheckState = 1 and FEnabled = 1 and FASSETGROUPCODE = " + 
                        dbl.sqlString(pub.getPrefixTB()) + ") a " +                        
                        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +                        
                        "    left join (select FCuryName,FCuryCode from "+ pub.yssGetTableName("Tb_Para_Currency")+" ) p on p.FCuryCode = a.FCuryCode  " +                        
                        " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) d on a.FAssetGroupCode = d.FAssetGroupCode" +
                        " left join (select FCuryCode,FCuryName as FCurrencyName from " +
                        pub.yssGetTableName("Tb_Para_Currency") +
                        " where FCheckState = 1) e on a.FPortCury = e.FCuryCode" +
                        " left join (select FExRateSrcCode,FExRateSrcName as FBaseRateSrcName from " +
                        pub.yssGetTableName("Tb_Para_ExRateSource") +
                        " where FCheckState = 1) f on a.FBaseRateSrcCode = f.FExRateSrcCode" +
                        " left join (select FExRateSrcCode,FExRateSrcName as FPortRateSrcName from " +
                        pub.yssGetTableName("Tb_Para_ExRateSource") +
                        " where FCheckState = 1) g on a.FPortRateSrcCode = g.FExRateSrcCode" +
                        this.buildFilterSql() +                        
                        ") y order by y.FPortCode, y.FCheckState, y.FCreateTime desc";
                	rs1 = dbl.openResultSet(sqlStr);
                    while (rs1.next()) {
                        bufShow.append( (rs1.getString("FPortCode") + "").trim()).append("\t");
                        bufShow.append( (rs1.getString("FPortName") + "").trim()).append(YssCons.YSS_LINESPLITMARK);
                        setPortfolioAttr(rs1);
                        bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                    }  
                    dbl.closeResultSetFinal(rs1);
                }
                if (bufShow.toString().length() > 2) 
                    sShowDataStr = bufShow.toString().substring(0,bufShow.toString().length() - 2);               
                if (bufAll.toString().length() > 2) 
                    sAllDataStr = bufAll.toString().substring(0,bufAll.toString().length() - 2);                
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
            } catch (Exception ex) {
                throw new YssException(ex.getMessage());
            } finally {
                dbl.closeResultSetFinal(rs);
                pub.setPrefixTB(CurrrentTabPreFix);
            }           
        }*/
        // add by guolongchao 20120130 STORY 1284 跨组合群获取组合信息---------------------end
        // add by huangqirong 20120305 story #1964
        else if(sType != null && sType.equalsIgnoreCase("groupportcodes")){ 
            ResultSet rs = null;
            ResultSet rs1 = null;
            String sqlStr = "";
            String tabPreFix = "";
            String CurrrentTabPreFix = "";
            String sHeader = "组合群代码-组合代码\t组合名称";
            StringBuffer bufShow=new StringBuffer();
            StringBuffer bufAll=new StringBuffer();
            String sShowDataStr = "";
            String sAllDataStr = "";
            try 
            {
            	CurrrentTabPreFix=pub.getPrefixTB();//获取当前pub中的表前缀，待循环结束之后将其还原
                sqlStr = "select FTabPreFix  from tb_sys_assetgroup where FLocked=0 and FSysCheck = 1 and FTabInd=1 order by FAssetGroupCode ";
                rs = dbl.openResultSet(sqlStr);                              
                while(rs.next())
                {
                	tabPreFix = rs.getString("FTabPreFix");  
                	if(tabPreFix==null||tabPreFix.trim().length()<=0)
                		continue;
                	pub.setPrefixTB(tabPreFix);
                	sqlStr = "select y.* from " +                    
                        " (select a.*, d.FAssetGroupName as FAssetGroupName, e.FCurrencyName as FCurrencyName,f.FBaseRateSrcName as FBaseRateSrcName,g.FPortRateSrcName as FPortRateSrcName," +
                        " p.FCuryName as FCuryName,"+
                        
                        " s.FSecurityName as FAimETFName," + //add by zhangjun 2012-04-26 ETF联接基金
                        
                        " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +                        
                        "( select * from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
                        " where FCheckState = 1 and FEnabled = 1 and FASSETGROUPCODE = " + 
                        dbl.sqlString(pub.getPrefixTB()) + ") a " +                        
                        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +                        
                        "    left join (select FCuryName,FCuryCode from "+ pub.yssGetTableName("Tb_Para_Currency")+" ) p on p.FCuryCode = a.FCuryCode  " +                        
                        " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) d on a.FAssetGroupCode = d.FAssetGroupCode" +
                        " left join (select FCuryCode,FCuryName as FCurrencyName from " +
                        pub.yssGetTableName("Tb_Para_Currency") +
                        " where FCheckState = 1) e on a.FPortCury = e.FCuryCode" +
                        
                        //-----add by zhangjun 2012-04-26 ETF联接基金 Tb_001_Para_Security 
                        " left join ( select FSecurityCode,FSecurityName from " +pub.yssGetTableName("Tb_Para_Security") + ") s on a.FAimETFCode = s.FSecurityCode " +            
                        //-----add by zhangjun 2012-04-26 ETF联接基金
                        
                        " left join (select FExRateSrcCode,FExRateSrcName as FBaseRateSrcName from " +
                        pub.yssGetTableName("Tb_Para_ExRateSource") +
                        " where FCheckState = 1) f on a.FBaseRateSrcCode = f.FExRateSrcCode" +
                        " left join (select FExRateSrcCode,FExRateSrcName as FPortRateSrcName from " +
                        pub.yssGetTableName("Tb_Para_ExRateSource") +
                        " where FCheckState = 1) g on a.FPortRateSrcCode = g.FExRateSrcCode" +
                        this.buildFilterSql() +                        
                        ") y order by y.FPortCode, y.FCheckState, y.FCreateTime desc";
                	rs1 = dbl.openResultSet(sqlStr);
                    while (rs1.next()) {
                        bufShow.append(rs.getString("FTabPreFix")+"-"+(rs1.getString("FPortCode") + "").trim()).append("\t");
                        bufShow.append( (rs1.getString("FAssetGroupName")+"-"+rs1.getString("FPortName") + "").trim()).append(YssCons.YSS_LINESPLITMARK);
                        setPortfolioAttr(rs1);
                        this.portCode = rs.getString("FTabPreFix") +"-"+ this.portCode;
                        bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                    }
                    dbl.closeResultSetFinal(rs1);
                }
                if (bufShow.toString().length() > 2) 
                    sShowDataStr = bufShow.toString().substring(0,bufShow.toString().length() - 2);               
                if (bufAll.toString().length() > 2) 
                    sAllDataStr = bufAll.toString().substring(0,bufAll.toString().length() - 2);                
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
            } catch (Exception ex) {
                throw new YssException(ex.getMessage());
            } finally {
                dbl.closeResultSetFinal(rs);
                pub.setPrefixTB(CurrrentTabPreFix);
            }           
        }
        // ---end---
        //add by huangqirong 2012-08-03 story #2831
        else if(sType != null && sType.equalsIgnoreCase("assetgroupPortcode")){
        	String sHeader = "";
            String sShowDataStr = "";
            String sAllDataStr = "";
            StringBuffer bufShow = new StringBuffer();
            StringBuffer bufAll = new StringBuffer();
            ResultSet rs = null;
            String strSql = "";
            String sql = "";
            String tabPreFix = "";
            String currPreFix = pub.getPrefixTB();
            ResultSet rsAssetGroup = null;
            try {
                    sHeader = "组合群代码\t明细组合代码\t明细组合名称";
                    
                    sql = "select FTabPreFix  from tb_sys_assetgroup where FLocked=0 and FSysCheck = 1 and FTabInd=1 order by FAssetGroupCode ";
                    rsAssetGroup = dbl.openResultSet(sql);
                    
                    while(rsAssetGroup.next())
                    {
                    	tabPreFix = rsAssetGroup.getString("FTabPreFix");  
                    	if(tabPreFix==null||tabPreFix.trim().length()<=0)
                    		continue;
                    	pub.setPrefixTB(tabPreFix);
	                    strSql = "select y.* from " +                    
	                        " (select a.*, d.FAssetGroupName as FAssetGroupName, e.FCurrencyName as FCurrencyName,e.FCuryName,f.FExRateSrcName as FBaseRateSrcName,g.FPortRateSrcName as FPortRateSrcName," +
	                        " FLinkCode, h.FSubCode as FSubCode," +
	                        
	                        " s.FSecurityName as FAimETFName," +
	
	                        " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
	                        " (select * from " + pub.yssGetTableName("Tb_Para_Portfolio") +
	                        " where FCheckState = 1 and FPortType = 0 " +
	                        " and FASSETGROUPCODE = " +
	                        dbl.sqlString(pub.getPrefixTB()) + ") a " + 
	                        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	                        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
	                        " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) d on a.FAssetGroupCode = d.FAssetGroupCode" +
	                        " left join ( select FSecurityCode,FSecurityName from " +pub.yssGetTableName("Tb_Para_Security") + ") s on a.FAimETFCode = s.FSecurityCode " +            
	                        " left join (select FCuryCode,FCuryName as FCurrencyName,FCuryName from " +
	                        pub.yssGetTableName("Tb_Para_Currency") +
	                        " where FCheckState = 1) e on a.FPortCury = e.FCuryCode" +
	                        " left join (select FExRateSrcCode,FExRateSrcName from " +
	                        pub.yssGetTableName("Tb_Para_ExRateSource") +
	                        " where FCheckState = 1) f on a.FBaseRateSrcCode = f.FExRateSrcCode" +
	                        " left join (select FExRateSrcCode,FExRateSrcName as FPortRateSrcName from " +
	                        pub.yssGetTableName("Tb_Para_ExRateSource") +
	                        " where FCheckState = 1) g on a.FPortRateSrcCode = g.FExRateSrcCode" +
	
	                        " left join (select FPortCode as FLinkCode, FSubCode from " +
	                        pub.yssGetTableName("Tb_Para_Portfolio_Relaship") + " where FRelaType = 'PortLink' and FCheckState <> 2 ) h on a.FPortCode = h.FSubCode" +
	                        
	                        buildFilterSql() + 
	                        
	                        ") y order by y.FCheckState, y.FCreateTime desc";
	                
	                rs = dbl.openResultSet(strSql);
	                while (rs.next()) {
	                	bufShow.append( (rs.getString("fassetgroupcode") + "").trim()).append("\t");
	                	bufShow.append( (rs.getString("FPortCode") + "").trim()).append("\t");                     
	                	bufShow.append( (rs.getString("FPortName") + "").trim()).append(YssCons.YSS_LINESPLITMARK);                	
	                    setPortfolioAttr(rs);
	                    this.dataType = "operport";
	                    bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
	                }
	                dbl.closeResultSetFinal(rs);
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
                throw new YssException("获取组合设置数据出错", e);
            } finally {
                dbl.closeResultSetFinal(rs);
                dbl.closeResultSetFinal(rsAssetGroup);
                pub.setPrefixTB(currPreFix);
            }
        }
        //  --- end ---
        //add by huangqirong 2012-08-13 story #2831
        else if(sType != null && sType.equalsIgnoreCase("changeassetgroupcode")){
        	pub.setPrefixTB(this.assetGroupCode);
        	pub.setAssetGroupCode(this.assetGroupCode);
        	return "ok";
        }
        //---end---
        
        /**shashijie 2012-11-7 STORY 3187 获取所有组合群下的所有组合*/
        else if(sType != null && sType.equalsIgnoreCase("getAllGroupPortCode")){
        	return getAllGroupPortCode();
        }
		/**end shashijie 2012-11-7 STORY */
        //add by huangqirong 2012-11-28 bug #6256
        else if(sType != null && sType.equalsIgnoreCase("ishaveassetcode")){        	
        	String result = this.getAssetSet(this.assetCode);
			if(result == null || result.trim().length() == 0)
				return "nopreset";
			else
				return "havepreset";
        }
        //---end---
        String strCuryCode = "";
        String str = ""; //查询组合的币种的查询语句
        ResultSet rs = null;
        //Connection conn = dbl.loadConnection();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        try {
            str = "select * from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FPortCode =" + dbl.sqlString(sType);
            rs = dbl.openResultSet(str);
            while (rs.next()) {
                strCuryCode = rs.getString("FPortCury");
            }
        } catch (Exception e) {
            throw new YssException("获取组合的币种出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return strCuryCode;

    }

    /**shashijie 2012-11-7 STORY 3187 获取所有组合群下的所有组合 */
	private String getAllGroupPortCode() throws YssException {
        String sShowDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        String sql = "";
        String tabPreFix = "";
        String currPreFix = pub.getPrefixTB();
        ResultSet rsAssetGroup = null;
        try {
    		/*数据格式:
    		 * 组合群代码-组合群名称\r\n组合代码-组合名称\t组合代码-组合名称\t......\f\f
    		 * 组合群代码2-组合群名称2\r\n组合代码2-组合名称2\t组合代码2-组合名称2\t......\f\f
    		 */
            
            sql = "select FTabPreFix,FAssetGroupCode,FAssetGroupName from tb_sys_assetgroup " +
            		" where FLocked=0 and FSysCheck = 1 and FTabInd=1 order by FAssetGroupCode ";
            rsAssetGroup = dbl.openResultSet(sql);
                
            while(rsAssetGroup.next())
            {
            	tabPreFix = rsAssetGroup.getString("FTabPreFix");  
            	if(tabPreFix==null||tabPreFix.trim().length()<=0)
            		continue;
            	pub.setPrefixTB(tabPreFix);
            	//拼接组合群
            	bufShow.append( (rsAssetGroup.getString("FAssetGroupCode") + "-" +
            			rsAssetGroup.getString("FAssetGroupName")).trim()).append("\r\n");
            	//获取SQL
                strSql = getAllGroupPortCodeSql();
                rs = dbl.openResultSet(strSql,true);
                while (rs.next()) {
                	//拼接组合
                	if (rs.isLast()) {
                		bufShow.append( (rs.getString("FPortCode").trim() + "-" +
	                			rs.getString("FPortName")).trim());
                		
                		bufShow.append(YssCons.YSS_LINESPLITMARK);
					} else {
						bufShow.append( (rs.getString("FPortCode").trim() + "-" +
	                			rs.getString("FPortName")).trim());
						
            			bufShow.append("\t");
					}
                }
                dbl.closeResultSetFinal(rs);
            }
            
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            return sShowDataStr;
        } catch (Exception e) {
            throw new YssException("获取组合群组合数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rsAssetGroup);
            pub.setPrefixTB(currPreFix);
        }
	}

	/**shashijie 2012-11-7 STORY 3187 获取组合群组合SQL */
	private String getAllGroupPortCodeSql() throws YssException {
		String query = " select y.* from " +
	        " (select a.*, d.FAssetGroupName as FAssetGroupName, e.FCurrencyName as FCurrencyName," +
	        " e.FCuryName,f.FExRateSrcName as FBaseRateSrcName,g.FPortRateSrcName as FPortRateSrcName," +
	        " FLinkCode, h.FSubCode as FSubCode," +
	        " s.FSecurityName as FAimETFName," +
	        " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
	        " (select * from " + pub.yssGetTableName("Tb_Para_Portfolio") +
	        " where FCheckState = 1 and FPortType = 0 " +
	        " and FASSETGROUPCODE = " +
	        dbl.sqlString(pub.getPrefixTB()) + ") a " + 
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
	        " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) d on a.FAssetGroupCode = d.FAssetGroupCode" +
	        " left join ( select FSecurityCode,FSecurityName from " +
	        	pub.yssGetTableName("Tb_Para_Security") + ") s on a.FAimETFCode = s.FSecurityCode " +
	        " left join (select FCuryCode,FCuryName as FCurrencyName,FCuryName from " +
	        pub.yssGetTableName("Tb_Para_Currency") +
	        " where FCheckState = 1) e on a.FPortCury = e.FCuryCode" +
	        " left join (select FExRateSrcCode,FExRateSrcName from " +
	        pub.yssGetTableName("Tb_Para_ExRateSource") +
	        " where FCheckState = 1) f on a.FBaseRateSrcCode = f.FExRateSrcCode" +
	        " left join (select FExRateSrcCode,FExRateSrcName as FPortRateSrcName from " +
	        pub.yssGetTableName("Tb_Para_ExRateSource") +
	        " where FCheckState = 1) g on a.FPortRateSrcCode = g.FExRateSrcCode" +
	        " left join (select FPortCode as FLinkCode, FSubCode from " +
	        pub.yssGetTableName("Tb_Para_Portfolio_Relaship") + " where FRelaType = 'PortLink' and " +
	        		" FCheckState <> 2 ) h on a.FPortCode = h.FSubCode" +
	        
	        buildFilterSql() + 
	        
	        ") y order by y.FCheckState, y.FCreateTime desc";
		
		return query;
	}

	/**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        PortfolioBean befEditBean = new PortfolioBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                //----delete by songjie 2011.03.10 不再以最大的启用日期查询组合设置数据----//
//                "(select FPortCode,FCheckState,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_Portfolio") + " " +
//                " where FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
//                "and FCheckState <> 2 and FASSETGROUPCODE = " +
//                dbl.sqlString(pub.getAssetGroupCode()) +
//                " group by FPortCode,FCheckState) x join" +
                //----delete by songjie 2011.03.10 不再以最大的启用日期查询组合设置数据----//
                " (select a.*, l.FVocName as FEnabledValue, m.FVocName as FCostingValue, n.FVocName as FPortTypeValue," +
                " d.FAssetGroupName as FAssetGroupName, e.FCurrencyName as FCurrencyName,f.FBaseRateSrcName as FBaseRateSrcName,g.FPortRateSrcName," +
                " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                //----edit by songjie 2011.03.10 不再以最大的启用日期查询组合设置数据----//
                " ( select * from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
                " where FCheckState <> 2 and FASSETGROUPCODE = " + dbl.sqlString(pub.getAssetGroupCode()) + " )a " +
                //----edit by songjie 2011.03.10 不再以最大的启用日期查询组合设置数据----//
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) d on a.FAssetGroupCode = d.FAssetGroupCode" +
                " left join (select FCuryCode,FCuryName as FCurrencyName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) e on a.FPortCury = e.FCuryCode" +
                " left join (select FExRateSrcCode,FExRateSrcName as FBaseRateSrcName from " +
                pub.yssGetTableName("Tb_Para_ExRateSource") +
                " where FCheckState = 1) f on a.FBaseRateSrcCode = f.FExRateSrcCode" +
                " left join (select FExRateSrcCode,FExRateSrcName as FPortRateSrcName from " +
                pub.yssGetTableName("Tb_Para_ExRateSource") +
                " where FCheckState = 1) g on a.FPortRateSrcCode = g.FExRateSrcCode" +
                " left join Tb_Fun_Vocabulary l on " + dbl.sqlToChar("a.FEnabled") + " = l.FVocCode and l.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PRT_ENABLED) +
                " left join Tb_Fun_Vocabulary m on " + dbl.sqlToChar("a.FCosting") + " = m.FVocCode and m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PRT_COSTING) +
                " left join Tb_Fun_Vocabulary n on " + dbl.sqlToChar("a.FPortType") + " = n.FVocCode and n.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PRT_PORTTYPE) +
                " where  FPortCode =" + dbl.sqlString(this.oldPortCode) +
                ") y " +//edit by songjie 2011.03.10 不再依据启用日期筛选数据
                " order by y.FCheckState, y.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.portCode = rs.getString("FPortCode") + "";
                befEditBean.portName = rs.getString("FPortName") + "";
                befEditBean.startDate = rs.getDate("FStartDate");
                befEditBean.portShortName = rs.getString("FPortShortName") + "";
                befEditBean.assetCode = rs.getString("FAssetCode") + "";
                befEditBean.assetGroupCode = rs.getString("FAssetGroupCode") + "";
                befEditBean.assetGroupName = rs.getString("FAssetGroupName") + "";
                befEditBean.currencyCode = rs.getString("FPortCury") + "";
                befEditBean.currencyName = rs.getString("FCurrencyName") + "";
                befEditBean.strPortType = rs.getString("FPortType") + "";
                befEditBean.strCosting = rs.getString("FCosting") + "";
                befEditBean.strEnabled = rs.getString("FEnabled") + "";
                befEditBean.inceptionDate = rs.getDate("FInceptionDate");
                befEditBean.expirationDate = rs.getDate("FExpirationDate");
                befEditBean.storageInitDate = rs.getDate("FStorageInitDate");
                befEditBean.desc = rs.getString("FDesc") + "";
                befEditBean.baseRateSrcCode = rs.getString("FBaseRateSrcCode") + "";
                befEditBean.baseRateCode = rs.getString("FBaseRateCode") + "";
                befEditBean.baseRateSrcName = rs.getString("FBaseRateSrcName") + "";

                befEditBean.portRateSrcCode = rs.getString("FPortRateSrcCode") + "";
                befEditBean.portRateCode = rs.getString("FPortRateCode") + "";
                befEditBean.portRateSrcName = rs.getString("FPortRateSrcName") + "";

                befEditBean.rollAsset = rs.getBigDecimal("FRollAsset"); // edit by xuqiji 20090428:QDV4赢时胜上海2009年04月27日01_B MS00415 成立资产与滚动金额界面显示数据与数据库中数据不一致
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    /**
     * 从回收站彻底删除数据,单条信息 和多条都可以
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            bTrans = true;
            arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);

                strSql = "delete from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                    " where FPortCode = '" + this.portCode + "'" +
                    " and FStartDate = " +
                    dbl.sqlDate(this.startDate);
                System.out.print(strSql);
                //执行sql语句
                dbl.executeSql(strSql);
                pub.delPortBaseCury(portCode);// QDV4上海2010年12月10日02_A lidaolong 2011.01.25 ,如果删除失败，此处不能回滚会影响到基础货币的取值
                
                strSql = "delete from " + pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                    " where FPortCode = '" + this.portCode + "'" +
                    " and FStartDate = " +
                    dbl.sqlDate(this.startDate);
                //执行sql语句
                dbl.executeSql(strSql);

                strSql = "delete from " + pub.yssGetTableName("Tb_Stock_RollAsset") +
                    " where FStorageDate = " +
                    dbl.sqlDate(YssFun.formatDate(this.oldInceptionDate)) + " and " +
                    " FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(this.oldInceptionDate, "yyyyMM")) +
                    " and " +
                    " FPortCode = " + dbl.sqlString(this.oldPortCode);
                //执行sql语句
                dbl.executeSql(strSql);
                
                //20120619 added by liubo.Story #2723
                //清空回收站时，判断是否有存在以View_组合群代码_组合代码命名的视图。若存在，则进行删除
                //===============================
                if (dbl.yssViewExist("View_" + pub.getPrefixTB() + "_" + this.portCode))
                {
                	strSql = "drop view View_" + pub.getPrefixTB() + "_" + this.portCode;
                }
                dbl.executeSql(strSql);
                //==============end=================
                
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
     * 以ListView的数据格式获取套帐信息，用于在listview中显示数据
     * @return String
     * @throws YssException
     * @version sunkey 20081218 BugNO:MS00072
     */
    private String getListViewAsset() throws YssException {
        String sHeader = ""; //存储listview的列表头
        String sShowDataStr = ""; //存储用于在客户端listview中显示的值
        String sAllDataStr = ""; //存储所有数据，但是此方法中数据都是不需的，这个的主要功能就是为了满足前台的解析
        StringBuffer bufShow = new StringBuffer(); //用户累加存储显示在客户端的信息
        StringBuffer bufAll = new StringBuffer(); //用户存储所有信息，表头\r\f显示的数据\r\f所有数据
        ResultSet rs = null; //结果集
        String strSql = ""; //SQL语句
        try {
            //设置表头信息
            sHeader = "组合代码\t组合名称\t会计年度";
            //构造查询语句，从bookset连接lsetlist，通过套帐代码匹配，因为年份是不好传进来的，暂时采用系统时间作为判断依据
            strSql = "SELECT FSETID, FSETNAME, FYEAR , 1 as FCHECKSTATE FROM  LSETLIST ";//modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
            //通过sql语句调用系统处理方法，打开数据集
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                //添加套帐编号
                bufShow.append( (rs.getString("FSETID") + "").trim()).append("\t");
                //添加套帐名称
                bufShow.append( (rs.getString("FSETNAME") + "").trim()).append("\t");
                //添加会计年度
                bufShow.append( (rs.getString("FYEAR") + "").trim()).append("\t").append(YssCons.YSS_LINESPLITMARK);
                //下面两个是实际的数据值，在进行数据持久化的时候会用到的
                this.portCode = rs.getString("FSETID"); //套帐代码
                this.portName = rs.getString("FSETNAME"); //套帐名称
                //下面的参数都是无用的，但是在buildRowStr（）方法中，如果不设置的话，日期转化将会出现异常。
                this.startDate = new java.util.Date();
                this.inceptionDate = this.startDate;
                this.expirationDate = this.startDate;
                this.storageInitDate = this.startDate;
                
                //20120724 added by liubo.Bug #5052
                //将审核状态返回给前台，以避免前台在检查录入数据时一概认为所有组合数据均是未审核状态而出现问题
                //======================================
                this.checkStateId = rs.getInt("FCHECKSTATE");	
                //=================end=====================

                //将所有数据添加到Buf中，备用 YssCons.YSS_LINESPLITMARK=\f\f
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            //如果显示数据的长度>1，要删除最后的\t
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }

            //如果所有数据>2,要删除最后的\f\f
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }
            //将查询出来的数据拼装返回
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("对不起，获取套帐信息时出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
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

    public String getListViewGroupData1() throws YssException {
        return "";
    }
    /**
     * MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
     * panjunfang modify 20090903 跨组合群获取组合列表
     * @return String
     * @throws YssException
     */
    public String getListViewGroupData2() throws YssException {
        String strRe = "";//存放返回到前台的字符串
        String sPrefixTB = pub.getPrefixTB(); //保存当前的组合群代码即表前缀
        try{
            pub.setPrefixTB(this.assetGroupCode);//将前台传过来的组合群代码设置为表前缀
            strRe = this.getListViewData2();//调用原有获取投资组合列表的方法
        }catch(Exception e){
            throw new YssException("获取投资组合信息出错", e);
        }finally{
            pub.setPrefixTB(sPrefixTB);//还原公共变的里的表前缀
        }
        return strRe;
    }

    /**
     * 2009-05-20 蒋锦 修改 进行跨组合群查询组合信息
     * MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合
     * @return String
     * @throws YssException
     */
    public String getListViewGroupData3() throws YssException {
        String sGroups = "";
        String sPrefixTB = pub.getPrefixTB();
        String[] assetGroupCodes = this.filterType.assetGroupCode.split(YssCons.
            YSS_GROUPSPLITMARK);
        for (int i = 0; i < assetGroupCodes.length; i++) {
            this.assetGroupCode = assetGroupCodes[i];
            pub.setPrefixTB(this.assetGroupCode);
            String sGroup = this.getListViewData3();
            sGroups = sGroups + sGroup + YssCons.YSS_GROUPSPLITMARK;
        }
        pub.setPrefixTB(sPrefixTB);
        if (sGroups.length() > 7) {
            sGroups = sGroups.substring(0, sGroups.length() - 7);
        }
        return sGroups;
    }
    /**
     * MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
     * panjunfang modify 20090903 跨组合群获取组合列表，用于跨组合群收益支付的修改中
     * @return String
     * @throws YssException
     */
    public String getListViewGroupData4() throws YssException {
        String strRe = "";//存放返回到前台的字符串
        String sPrefixTB = pub.getPrefixTB(); //保存当前的组合群代码即表前缀
        try{
            pub.setPrefixTB(this.assetGroupCode);//将前台传过来的组合群代码设置为表前缀
            strRe = this.getListViewData3();//调用原有获取投资组合列表的方法
        }catch(Exception e){
            throw new YssException("获取投资组合信息出错", e);
        }finally{
            pub.setPrefixTB(sPrefixTB);//还原公共变的里的表前缀
        }
        return strRe;
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }
    
    //add by yanghaiming 20100712 MS01374 QDV4汇添富2010年06月28日01_AB
    //用于查询轧差组合代码
    private String getListViewPort() throws YssException {
    	String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        try {
            sHeader = "组合代码\t组合名称\t启用日期";
            strSql = "select y.* from " +
                        //----delete by songjie 2011.03.10 不以最大的启用日期查询组合设置数据----//
//            			"(select FPortCode,max(FStartDate) as FStartDate from " +
//            			pub.yssGetTableName("Tb_Para_Portfolio") + " " +
//            			" where FStartDate <= " +
//            			dbl.sqlDate(new java.util.Date()) +
//            			"and FCheckState = 1 and FEnabled = 1 and FASSETGROUPCODE = " +
//            			dbl.sqlString(pub.getPrefixTB()) +
//            			" group by FPortCode) x join" +
                        //----delete by songjie 2011.03.10 不以最大的启用日期查询组合设置数据----//
            			" (select a.*, d.FAssetGroupName as FAssetGroupName, e.FCurrencyName as FCurrencyName,f.FBaseRateSrcName as FBaseRateSrcName,g.FPortRateSrcName as FPortRateSrcName," +
            			 " p.FCuryName as FCuryName,"+//add by lidaolong 2011.01.25 ;QDV4上海2010年12月10日02_A
            			 
            			 " s.FSecurityName as FAimETFName," + //add by zhangjun 2012-04-26 ETF联接基金
            			 
            			" b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            			//----edit by songjie 2011.03.10 不以最大的启用日期查询组合设置数据----//
                        " ( select * from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
                        " where FCheckState = 1 and FEnabled = 1 and FASSETGROUPCODE = " + 
                        dbl.sqlString(pub.getPrefixTB()) + ") a " +
                        //----edit by songjie 2011.03.10 不以最大的启用日期查询组合设置数据----//
                        //---//add by lidaolong 2011.01.25 ;QDV4上海2010年12月10日02_A
                        "    left join (select FCuryName,FCuryCode from "+ pub.yssGetTableName("Tb_Para_Currency")+" ) p on p.FCuryCode = a.FCuryCode  " +
                        //---end QDV4上海2010年12月10日02_A-------------
                        

                        //-----add by zhangjun 2012-04-26 ETF联接基金 Tb_001_Para_Security 
                        " left join ( select FSecurityCode,FSecurityName from " +pub.yssGetTableName("Tb_Para_Security") + ") s on a.FAimETFCode = s.FSecurityCode " +            
                        //-----add by zhangjun 2012-04-26 ETF联接基金
                        
                        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                        " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) d on a.FAssetGroupCode = d.FAssetGroupCode" +
                        " left join (select FCuryCode,FCuryName as FCurrencyName from " +
                        pub.yssGetTableName("Tb_Para_Currency") +
                        " where FCheckState = 1) e on a.FPortCury = e.FCuryCode" +
                        " left join (select FExRateSrcCode,FExRateSrcName as FBaseRateSrcName from " +
                        pub.yssGetTableName("Tb_Para_ExRateSource") +
                        " where FCheckState = 1) f on a.FBaseRateSrcCode = f.FExRateSrcCode" +
                        " left join (select FExRateSrcCode,FExRateSrcName as FPortRateSrcName from " +
                        pub.yssGetTableName("Tb_Para_ExRateSource") +
                        " where FCheckState = 1) g on a.FPortRateSrcCode = g.FExRateSrcCode" +
            			" join (select fsubcode from " + pub.yssGetTableName("tb_para_portfolio_relaship") + 
            			" where frelatype = 'PortLink' and fcheckstate = 1 and fportcode = " + dbl.sqlString(this.filterType.portCode) +
            			//edit by songjie 2011.03.10 不以最大的启用日期查询组合设置数据
            			" ) h on a.fportcode = h.fsubcode where a.fcheckstate = 1) y " +
            			" order by y.FPortCode, y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FPortCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FPortName") + "").trim()).append(
                    "\t");
                bufShow.append( (YssFun.formatDate(rs.getDate("FStartDate"),
                    YssCons.YSS_DATEFORMAT) + "").
                               trim()).append(YssCons.YSS_LINESPLITMARK);
                setPortfolioAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
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
            throw new YssException("获取组合设置数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

 /*********************************************************
     * MS01421 QDV4华安2010年07月07日01_AB 
     * 系统根据界面中勾选的数据接口,相应导入数据中心的表中   
     * add by jiangshichao 2010.07.20
     * 
     * 获取套帐列表
     * @return
     * @throws YssException
     */
    public String getPortCodes () throws YssException{
    	 StringBuffer buff = new StringBuffer();
         ResultSet rs = null;
         String strSql = "";
         String portCodes = "";
         
         String sReturn = "";
         try {
        	 //----edit by songjie 2011.03.10 不以最大的启用日期查询组合设置数据----//
             strSql = "select FPortCode,fportname, FStartDate from " +pub.yssGetTableName("Tb_Para_Portfolio") +
                      " where FCheckState = 1 and FEnabled = 1 and FASSETGROUPCODE = " +
                    //----edit by songjie 2011.03.10 不以最大的启用日期查询组合设置数据----//
                      dbl.sqlString(pub.getPrefixTB());
             rs = dbl.openResultSet(strSql);
             while (rs.next()) {
                 buff.append(rs.getString("FPortCode")+"_"+rs.getString("fportname")).append("\t");
             }

             if(buff.toString().length()>1){
            	 sReturn = buff.toString().substring(0,buff.toString().length()-1);
             }
            return  sReturn;
         } catch (Exception e) {
             throw new YssException("获取组合套帐出错", e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
    	
    }

public String getTreeViewData3() throws YssException {
	// TODO Auto-generated method stub
	return null;
}

}
