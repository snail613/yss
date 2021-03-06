package com.yss.main.parasetting;

import java.sql.*;
import java.sql.Date;
//MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009.04.08
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 *
 * <p>Title:MTVMethodBean </p>
 * <p>Description: 估值方法设置</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class MTVMethodBean
    extends BaseDataSettingBean implements IDataSetting {
    private String securityCode = ""; //证券代码
    private String securityName = ""; //证券名称
    private String catCode = ""; //品种代码
    private String catName = ""; //品种名称
    private String subCatCode = ""; //品种明细代码
    private String subCatName = ""; //品种明细名称
    private String mTVCode = ""; //估值方法代码
    private String mTVName = ""; //估值方法名称
    private String mktSrcCode = ""; //行情来源代码
    private String mktSrcName = ""; //行情来源名称
    // private String exRateSrcCode = ""; //汇率来源代码
    // private String exRateSrcName = ""; //汇率来源名称
    private String mktPriceCode = ""; //估值行情
    private String sRecycled = "";
    //无用注释
    //private String exRateCode = ""; // private String exRateCode = ""; //汇率行情
    private String mTVMethod = ""; //估值方法
    private String sectorCode = ""; //行业板块代码
    private String sectorName = ""; //行业板块名称
    private String cusCatCode = ""; //自定义子品种代码
    private String cusCatName = ""; //自定义子品种名称
    // private String FExRateSrcCode = "";//汇率代码
    //private String FExRateCode = "";//汇率来源代码
    private String desc = ""; //描述说明
    private String oldMTVCode = ""; //修改用估值方法代码
    private String isOnlyColumns = "0"; //有时在显示证券列表时只显示列，不查询数据
    private String mTVMethodLinks = "";

    private String baseRateSrcCode = "";
    private String baseRateSrcName = "";
    private String baseRateCode = "";
    private String portRateSrcCode = "";
    private String portRateSrcName = "";
    private String portRateCode = "";

    private MTVMethodBean filterType;
    private java.util.Date startDate;
    private java.util.Date oldStartDate;
    private String sMTVSelCondSet = "";
    private HashSet MTVMethodLinkCode = null; //MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009.04.03

    /**add---shashijie 2013-3-30 STORY 3528 投资类型*/
	private String FInvestmentType = "";
	/**end---shashijie 2013-3-30 STORY 3528*/
    
    //  public String getExRateSrcName() {
//      return exRateSrcName;
//   }

    public HashSet getMTVMethodLinkCode() {
        return MTVMethodLinkCode;
    }

    public void setMTVMethodLinkCode(HashSet MTVMethodLinkCode) {
        this.MTVMethodLinkCode = MTVMethodLinkCode;
    }

    public String getSubCatName() {
        return subCatName;
    }

    public String getMktPriceCode() {
        return mktPriceCode;
    }

    public String getCatCode() {
        return catCode;
    }

    public String getCatName() {
        return catName;
    }

    public String getMktSrcName() {
        return mktSrcName;
    }

    public String getCusCatName() {
        return cusCatName;
    }

    public String getIsOnlyColumns() {
        return isOnlyColumns;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getOldMTVCode() {
        return oldMTVCode;
    }

    // public String getExRateSrcCode() {
    //     return exRateSrcCode;
    //  }

    public String getDesc() {
        return desc;
    }

    public java.util.Date getOldStartDate() {
        return oldStartDate;
    }

    public String getSubCatCode() {
        return subCatCode;
    }

    public MTVMethodBean getFilterType() {
        return filterType;
    }

    public String getSectorName() {
        return sectorName;
    }

    public String getSecurityName() {
        return securityName;
    }

    public String getCusCatCode() {
        return cusCatCode;
    }

    public String getMTVName() {
        return mTVName;
    }

    public String getMktSrcCode() {
        return mktSrcCode;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }

    public String getSectorCode() {
        return sectorCode;
    }

    public String getMTVMethod() {
        return mTVMethod;
    }

    public void setMTVCode(String mTVCode) {
        this.mTVCode = mTVCode;
    }

    // public void setExRateSrcName(String exRateSrcName) {
    //     this.exRateSrcName = exRateSrcName;
    // }

    public void setSubCatName(String subCatName) {
        this.subCatName = subCatName;
    }

    public void setMktPriceCode(String mktPriceCode) {
        this.mktPriceCode = mktPriceCode;
    }

    public void setCatCode(String catCode) {
        this.catCode = catCode;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public void setMktSrcName(String mktSrcName) {
        this.mktSrcName = mktSrcName;
    }

    public void setCusCatName(String cusCatName) {
        this.cusCatName = cusCatName;
    }

    public void setIsOnlyColumns(String isOnlyColumns) {
        this.isOnlyColumns = isOnlyColumns;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setOldMTVCode(String oldMTVCode) {
        this.oldMTVCode = oldMTVCode;
    }

    // public void setExRateSrcCode(String exRateSrcCode) {
    //    this.exRateSrcCode = exRateSrcCode;
    // }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOldStartDate(java.util.Date oldStartDate) {
        this.oldStartDate = oldStartDate;
    }

    public void setSubCatCode(String subCatCode) {
        this.subCatCode = subCatCode;
    }

    public void setFilterType(MTVMethodBean filterType) {
        this.filterType = filterType;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setCusCatCode(String cusCatCode) {
        this.cusCatCode = cusCatCode;
    }

    public void setMTVName(String mTVName) {
        this.mTVName = mTVName;
    }

    public void setMktSrcCode(String mktSrcCode) {
        this.mktSrcCode = mktSrcCode;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    public void setSectorCode(String sectorCode) {
        this.sectorCode = sectorCode;
    }

    public void setMTVMethod(String mTVMethod) {
        this.mTVMethod = mTVMethod;
    }

    public void setMTVMethodLinks(String mTVMethodLinks) {
        this.mTVMethodLinks = mTVMethodLinks;
    }

    public void setPortRateCode(String portRateCode) {
        this.portRateCode = portRateCode;
    }

    public void setBaseRateCode(String baseRateCode) {
        this.baseRateCode = baseRateCode;
    }

    public void setPortRateSrcCode(String portRateSrcCode) {
        this.portRateSrcCode = portRateSrcCode;
    }

    public void setBaseRateSrcCode(String baseRateSrcCode) {
        this.baseRateSrcCode = baseRateSrcCode;
    }

    // public void setExRateCode(String exRateCode) {
    //    this.exRateCode = exRateCode;
    //  }

    public void setBaseRateSrcName(String baseRateSrcName) {
        this.baseRateSrcName = baseRateSrcName;
    }

    public void setPortRateSrcName(String portRateSrcName) {
        this.portRateSrcName = portRateSrcName;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getMTVCode() {
        return mTVCode;
    }

    public String getMTVMethodLinks() {
        return mTVMethodLinks;
    }

    public String getPortRateCode() {
        return portRateCode;
    }

    public String getBaseRateCode() {
        return baseRateCode;
    }

    public String getPortRateSrcCode() {
        return portRateSrcCode;
    }

    public String getBaseRateSrcCode() {
        return baseRateSrcCode;
    }

    // public String getExRateCode() {
    //    return exRateCode;
    // }

    public String getBaseRateSrcName() {
        return baseRateSrcName;
    }

    public String getPortRateSrcName() {
        return portRateSrcName;
    }

    public int getCheckStateId() {
        return this.checkStateId;
    }

    public void setCheckStateId(int checkStateId) {
        this.checkStateId = checkStateId;
    }

    public MTVMethodBean() {
    }

    /**
     * MS00008 edit by 宋洁 2009-02-13
     * 解析方法需要修改，前台将传过来比原来多一段的内容组成的字符串
     * 需要取到估值方法筛选条件的字符串内容
     * parseRowStr
     * 解析估值方法维护请求
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        sRecycled = sRowStr; //MS00008 add by 宋洁  2009-03-06
        try {
            if (sRowStr.equals("")) {
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
                //MS00008 add by 宋洁 2009-03-04 用于获取估值方法筛选条件信息字符串
                if (sRowStr.split("\r\t").length > 1 &&
                    !sRowStr.split("\r\t")[1].equals("[null]")) {
                    if ( (sRowStr.split("\r\t")[1]).split("\t").length >= 3) {
                        if (! (sRowStr.split("\r\t")[1]).split("\t")[2].equals("")) { //判断估值方法筛选条件界面的品种类型是否为空
                            this.sMTVSelCondSet = sRowStr.split("\r\t")[1];
                        }
                    }
                }
                //MS00008 add by 宋洁 2009-03-04 用于获取估值方法筛选条件信息字符串
                //MS00008 delete by 宋洁 2009-03-04 前台已经不传估值方法链接信息过来了
                //if (sRowStr.split("\r\t").length > 3) {
                //    this.mTVMethodLinks = sRowStr.split("\r\t")[3];
                //}
                //MS00008 delete by 宋洁 2009-03-04 前台已经不传估值方法链接信息过来了
            } else {
                //sRecycled = sRowStr;MS00008 delete by 宋洁 2009-03-06
                sTmpStr = sRowStr;
            }
            this.autoParseRowStr(sTmpStr);
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = (MTVMethodBean) pub.getParaSettingCtx().
                        getBean("mtvmethod");
                    this.filterType.setYssPub(pub);
                }
                //MS00008 edit by 宋洁 2009-03-04 查询类型的字符串信息拼接顺序有所改变
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (sRowStr.split("\r\t").length > 2) {
                        this.filterType.parseRowStr(sRowStr.split("\r\t")[2]);
                    }
                }
                //MS00008 edit by 宋洁 2009-03-04 查询类型的字符串信息拼接顺序有所改变
            }
        } catch (Exception e) {
            throw new YssException("解析估值方法设置请求信息出错", e);
        }
    }

    /**
     * buildRowStr
     * 获取数据字符串
     * @return String
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        /*   buf.append(this.autoBuildRowStr(this.getBuilderRowFields1())).
                 append("\t");
           buf.append(super.buildRecLog());
         */

        buf.append(this.mTVCode).append("\t");
        buf.append(this.mTVName).append("\t");
        buf.append(YssFun.formatDate(this.startDate)).append("\t");
        buf.append(this.mktSrcCode).append("\t");
        buf.append(this.mktSrcName).append("\t");
        buf.append(this.mktPriceCode).append("\t");
        //  buf.append(this.exRateSrcCode).append("\t");
        //  buf.append(this.exRateSrcName).append("\t");
        //  buf.append(this.exRateCode).append("\t");
        buf.append(this.mTVMethod).append("\t");
        buf.append(this.catCode).append("\t");
        buf.append(this.catName).append("\t");
        buf.append(this.subCatCode).append("\t");
        buf.append(this.subCatName).append("\t");
        buf.append(this.cusCatCode).append("\t");
        buf.append(this.cusCatName).append("\t");
        buf.append(this.securityCode).append("\t");
        buf.append(this.securityName).append("\t");
        buf.append(this.sectorCode).append("\t");
        buf.append(this.sectorName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.baseRateSrcCode).append("\t");
        buf.append(this.baseRateSrcName).append("\t");
        buf.append(this.baseRateCode).append("\t");
        buf.append(this.portRateSrcCode).append("\t");
        buf.append(this.portRateSrcName).append("\t");
        buf.append(this.portRateCode).append("\t");
        /**add---shashijie 2013-3-30 STORY 3528 投资类型*/
        buf.append(this.FInvestmentType).append("\t");
		/**end---shashijie 2013-3-30 STORY 3528*/
        /*   buf.append(super.checkStateId).append("\t");
           buf.append(this.oldMTVCode).append("\t");
           buf.append(this.oldStartDate).append("\t");
           buf.append(this.isOnlyColumns).append("\t");
         */
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查估值方法维护数据是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        //MS00008 add by 宋洁 2009-03-06 新增状态时，不传oldMTVCode到checkInputCommon
        if (btOper == YssCons.OP_ADD) {
            dbFun.checkInputCommon(btOper,
                                   pub.yssGetTableName("Tb_Para_MTVMethod"),
                                   "FMTVCode", this.mTVCode, "");

        } else {
            //MS00008 add by 宋洁 2009-03-06 新增状态时，不传oldMTVCode到checkInputCommon
            dbFun.checkInputCommon(btOper,
                                   pub.yssGetTableName("Tb_Para_MTVMethod"),
                                   //MS00008 delete by 宋洁 2009-02-27 不用判断启用日期是否重复
                                   /*"FMTVCode,FStartDate",
                                                                    this.mTVCode + "," +
                                                                    YssFun.formatDate(this.startDate),
                                                                    this.oldMTVCode + "," +
                                                                    YssFun.formatDate(this.oldStartDate)*/
                                   //MS00008 delete by 宋洁 2009-02-27 不用判断启用日期是否重复
                                   //MS00008 add by 宋洁 2009-02-27 只用判断估值方法代码是否重复
                                   "FMTVCode", this.mTVCode, this.oldMTVCode
                //MS00008 add by 宋洁 2009-02-27 只用判断估值方法代码是否重复
                );
            //MS00008 add by 宋洁 2009-03-06 新增状态时，不传oldMTVCode到checkInputCommon
        }
        //MS00008 add by 宋洁 2009-03-06 新增状态时，不传oldMTVCode到checkInputCommon
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = " where 1=1 ";
        if (this.isOnlyColumns.equalsIgnoreCase("1")) {
            sResult = sResult + " and 1=2 ";
            return sResult;
        }
        try {
            if (this.filterType != null) {
                /*if (this.filterType.startDate != null &&
                    !this.filterType.startDate.equals(YssFun.toDate(
                            "9998-12-31"))) {
                    sResult = sResult + " and a.FStartDate = " +
                              dbl.sqlDate(filterType.startDate);
                                 }*/
                //MS00008 delete by 宋洁 2009-03-06 估值方法界面上已经没有启用日期了
            	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.mTVCode != null && this.filterType.mTVCode.length() != 0) {
                    /*sResult = sResult + " and a.FMTVCode like '" +
                              filterType.mTVCode.replaceAll("'", "''") + "%'";*/
                    //MS00008 delete by 宋洁 2009-03-06 模糊查找改为精确查找
                    sResult = sResult + " and a.FMTVCode='" +
                        filterType.mTVCode.replaceAll("'", "''") + "'"; //MS00008 add by 宋洁 2009-03-06 模糊查找改为精确查找
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.mTVName != null && this.filterType.mTVName.length() != 0) {
                    /*sResult = sResult + " and a.FMTVName like '" +
                              filterType.mTVName.replaceAll("'", "''") + "%'";*/
                    //MS00008 delete by 宋洁 2009-03-06 模糊查找改为精确查找
                    sResult = sResult + " and a.FMTVName='" +
                        filterType.mTVName.replaceAll("'", "''") + "'"; //MS00008 add by 宋洁 2009-03-06 模糊查找改为精确查找
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.mktSrcCode != null && this.filterType.mktSrcCode.length() != 0) {
                    /*sResult = sResult + " and a.FMktSrcCode like '" +
                              filterType.mktSrcCode.replaceAll("'", "''") +"%'";*/
                    //MS00008 delete by 宋洁 2009-03-06 模糊查找改为精确查找
                    sResult = sResult + " and a.FMktSrcCode='" +
                        filterType.mktSrcCode.replaceAll("'", "''") + "'"; //MS00008 add by 宋洁 2009-03-06 模糊查找改为精确查找
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.mktPriceCode != null && this.filterType.mktPriceCode.length() != 0 &&
                    !this.filterType.mktPriceCode.equalsIgnoreCase("ALL")) {
                    sResult = sResult + " and a.FMktPriceCode = " +
                        dbl.sqlString(filterType.mktPriceCode);
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.mTVMethod != null && this.filterType.mTVMethod.length() != 0 &&
                    !this.filterType.mTVMethod.equalsIgnoreCase("ALL")) {
                    sResult = sResult + " and a.FMTVMethod = " +
                        dbl.sqlString(filterType.mTVMethod);
                }
                //  if (this.filterType.exRateSrcCode.length() != 0){
                //       sResult = sResult + " and a.FExRateSrcCode = " +
                //            dbl.sqlString(this.filterType.exRateSrcCode);
                //    }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.desc != null && this.filterType.desc.length() != 0) {
                    sResult = sResult + " and a.FDesc like '" +
                        filterType.desc.replaceAll("'", "''") + "%'";
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.baseRateCode != null && !this.filterType.baseRateCode.equals("99") &&
                    this.filterType.baseRateCode.length() != 0) {
                    sResult += " and a.FBaseRateCode = " +
                        dbl.sqlString(filterType.baseRateCode);
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.baseRateSrcCode != null && this.filterType.baseRateSrcCode.length() != 0) {
                    sResult += " and a.FBaseRateSrcCode like '" +
                        filterType.baseRateSrcCode.replaceAll("'", "''") +
                        "%'";
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.portRateCode != null && !this.filterType.portRateCode.equals("99") &&
                    this.filterType.portRateCode.length() != 0) {
                    sResult += " and a.FPortRateCode = " +
                        dbl.sqlString(filterType.portRateCode);
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.portRateSrcCode != null && this.filterType.portRateSrcCode.length() != 0) {
                    sResult += " and a.FPortRateSrcCode like '" +
                        filterType.portRateSrcCode.replaceAll("'", "''") +
                        "%'";
                }
                /**add---shashijie 2013-3-30 STORY 3528 投资类型*/
                if (this.filterType.FInvestmentType != null && this.filterType.FInvestmentType.length() != 0) {
                    sResult += " and a.FInvestmentType like '" +
                        filterType.FInvestmentType.replaceAll("'", "''") +
                        "%'";
                }
        		/**end---shashijie 2013-3-30 STORY 3528*/
            }
        } catch (Exception e) {
            throw new YssException("筛选估值方法设置数据出错", e);
        }
        return sResult;
    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_MTV_MARKETVALUE + "," +
                                        YssCons.YSS_MTV_MTVMETHOD + "," +
                                        YssCons.YSS_MTV_EXCHANGERATE
                                        /**add---shashijie 2013-3-30 STORY 3528 增加获取词汇"投资类型"*/
										+","+YssCons.YSS_MTV_INVESTMENTTYPE
										/**end---shashijie 2013-3-30 STORY 3528*/
            							);

            sHeader = this.getListView1Headers();
          //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
          //if (strSql == "") {
          if (strSql.equals("")) {
          //---end---
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                    "\r\f" + this.getListView1ShowCols() + "\r\f" + "voc" +
                    sVocStr;
            }
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setMTVMethodAttr(rs);
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
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取估值方法信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取估值方法维护数据
     * 此方法已被修改
     * 修改时间：2008年2月20号
     * 修改人：单亮
     * 原方法的功能：查询出股东表的数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select y.*,xx.FExRateSrcName as FBaseRateSrcName,xxx.FExRateSrcName as FPortRateSrcName " +
    		" from " +
            "(select FMTVCode,FCheckState,max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_MTVMethod") + " " +
            " where FStartDate <= " +
            dbl.sqlDate(new java.util.Date()) +
            //修改前的方法
//            "and FCheckState <> 2 group by FMTVCode,FCheckState) x join" +
            //修改后的方法
            //--------------------begin
            "group by FMTVCode,FCheckState) x join" +
            //-------------------end
            " (select a.*, f.FVocName as FMktPriceCodeValue," +
            /**add---shashijie 2013-3-30 STORY 3528 投资类型名称*/
        	" i.FVocName as FinvestmentTypeName ,"+
			/**end---shashijie 2013-3-30 STORY 3528*/
            " h.FVocName as FMTVMethodValue," +
            " d.FMktSrcName, i.FSecurityName,j.FCatName," +
            " k.FSubCatName,m.FCusCatName,n.FSectorName," +
            " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Para_MTVMethod") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            //----------------------------------------------------------------------------------------------
            " left join (select FMktSrcCode,FMktSrcName from " +
            pub.yssGetTableName("Tb_Para_MarketSource") +
            " ) d on a.FMktSrcCode = d.FMktSrcCode" +
            //----------------------------------------------------------------------------------------------
            " left join (select ib.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") +
            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 group by FSecurityCode) ia join (select FSecurityCode, FSecurityName, FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") +
            ") ib on ia.FSecurityCode = ib.FSecurityCode and ia.FStartDate = ib.FStartDate) i on a.FSecurityCode = i.FSecurityCode " +
            //----------------------------------------------------------------------------------------------
            " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) j on a.FCatCode = j.FCatCode" +
            //----------------------------------------------------------------------------------------------
            " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) k on a.FSubCatCode = k.FSubCatCode" +
            //----------------------------------------------------------------------------------------------
            " left join (select FCusCatCode,FCusCatName from " +
            pub.yssGetTableName("Tb_Para_CustomCategory") +
            " where FCheckState = 1) m on  a.FCusCatCode = m.FCusCatCode " +
            //----------------------------------------------------------------------------------------------
            " left join (select nb.* from (select FSectorCode, max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Sector") +
            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 group by FSectorCode) na join (select FSectorCode, FSectorName, FStartDate from " +
            pub.yssGetTableName("Tb_Para_Sector") +
            ") nb on na.FSectorCode = nb.FSectorCode and na.FStartDate = nb.FStartDate) n on a.FSecClsCode = n.FSectorCode " +
            //----------------------------------------------------------------------------------------------
            " left join Tb_Fun_Vocabulary f on a.FMktPriceCode = f.FVocCode and f.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_MTV_MARKETVALUE) +
            " left join Tb_Fun_Vocabulary h on " + dbl.sqlToChar("a.FMTVMethod") +
            " = h.FVocCode and h.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_MTV_MTVMETHOD) +
            /**add---shashijie 2013-3-30 STORY 3528 投资类型对应的词汇名称*/
            " left join Tb_Fun_Vocabulary i on a.FinvestmentType = i.FVocCode and i.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_MTV_INVESTMENTTYPE) +
			/**end---shashijie 2013-3-30 STORY 3528*/
            buildFilterSql() +
            ") y on x.FMTVCode = y.FMTVCode and x.FStartDate = y.FStartDate" +
            " left join (select FExRateSrcCode,FExRateSrcName from " +
            pub.yssGetTableName("Tb_Para_ExRateSource") +
            " where FCheckState =1) xx on y.FBaseRateSrcCode = xx.FExRateSrcCode" +
            " left join (select FExRateSrcCode,FExRateSrcName from " +
            pub.yssGetTableName("Tb_Para_ExRateSource") +
            " where FCheckState =1) xxx on y.FPortRateSrcCode = xxx.FExRateSrcCode" +
            " order by y.FCheckState, y.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     * 获取估值方法维护的全部数据
     * @return String
     */
    public String getListViewData4() throws YssException { //////////
        String strSql = "";

        strSql =
            "select a.*, f.FVocName as FMktPriceCodeValue," +
            " h.FVocName as FMTVMethodValue," +
            " d.FMktSrcName, i.FSecurityName,j.FCatName," +
            " k.FSubCatName,m.FCusCatName,n.FSectorName," +
            " xx.FExRateSrcName as FBaseRateSrcName," +
            " xxx.FExRateSrcName as FPortRateSrcName," +
            " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Para_MTVMethod") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FExRateSrcCode,FExRateSrcName from " +
            pub.yssGetTableName("Tb_Para_ExRateSource") +
            " where FCheckState =1) xx on a.FBaseRateSrcCode = xx.FExRateSrcCode" +
            " left join (select FExRateSrcCode,FExRateSrcName from " +
            pub.yssGetTableName("Tb_Para_ExRateSource") +
            " where FCheckState =1) xxx on a.FBaseRateSrcCode = xxx.FExRateSrcCode" +
            //----------------------------------------------------------------------------------------------
            " left join (select FMktSrcCode,FMktSrcName from " +
            pub.yssGetTableName("Tb_Para_MarketSource") +
            " ) d on a.FMktSrcCode = d.FMktSrcCode" +
            //----------------------------------------------------------------------------------------------
            " left join (select ib.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") +
            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 group by FSecurityCode) ia join (select FSecurityCode, FSecurityName, FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") +
            ") ib on ia.FSecurityCode = ib.FSecurityCode and ia.FStartDate = ib.FStartDate) i on a.FSecurityCode = i.FSecurityCode " +
            //----------------------------------------------------------------------------------------------
            " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) j on a.FCatCode = j.FCatCode" +
            //----------------------------------------------------------------------------------------------
            " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) k on a.FSubCatCode = k.FSubCatCode" +
            //----------------------------------------------------------------------------------------------
            " left join (select FCusCatCode,FCusCatName from " +
            pub.yssGetTableName("Tb_Para_CustomCategory") +
            " where FCheckState = 1) m on  a.FCusCatCode = m.FCusCatCode " +
            //----------------------------------------------------------------------------------------------
            " left join (select nb.* from (select FSectorCode, max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Sector") +
            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 group by FSectorCode) na join (select FSectorCode, FSectorName, FStartDate from " +
            pub.yssGetTableName("Tb_Para_Sector") +
            ") nb on na.FSectorCode = nb.FSectorCode and na.FStartDate = nb.FStartDate) n on a.FSecClsCode = n.FSectorCode " +
            //----------------------------------------------------------------------------------------------
            " left join Tb_Fun_Vocabulary f on a.FMktPriceCode = f.FVocCode and f.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_MTV_MARKETVALUE) +
            " left join Tb_Fun_Vocabulary h on " +
            dbl.sqlToChar("a.FMTVMethod") +
            " = h.FVocCode and h.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_MTV_MTVMETHOD) +
            buildFilterSql() +
            " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData2
     * 获取已审核的估值方法维护数据
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
            sHeader = "估值方法代码\t估值方法名称\t启用日期";
            if (this.isOnlyColumns.equalsIgnoreCase("0")) {
                strSql = "select y.* from " +
                    "(select FMTVCode,FCheckState,max(FStartDate) as FStartDate from " +
                    pub.yssGetTableName("Tb_Para_MTVMethod") + " " +
                    " where FStartDate <= " +
                    dbl.sqlDate(new java.util.Date()) +
                    "and FCheckState = 1 group by FMTVCode,FCheckState) x join" +
                    " (select a.*, d.FMktSrcName, i.FSecurityName,j.FCatName," +
                    " k.FSubCatName,m.FCusCatName,ex.FExRateSrcName,n.FSectorName," +
                    " b.FUserName as FCreatorName,c.FUserName as FCheckUserName,xx.FExRateSrcName as FBaseRateSrcName,xxx.FExRateSrcName as FPortRateSrcName from " +
                    pub.yssGetTableName("Tb_Para_MTVMethod") + " a " +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " left join (select FExRateSrcCode,FExRateSrcName from " +
                    pub.yssGetTableName("Tb_Para_ExRateSource") +
                    " where FCheckState =1) xx on a.FBaseRateSrcCode = xx.FExRateSrcCode" +
                    " left join (select FExRateSrcCode,FExRateSrcName from " +
                    pub.yssGetTableName("Tb_Para_ExRateSource") +
                    " where FCheckState =1) xxx on a.FPortRateSrcCode = xxx.FExRateSrcCode" +
                    //----------------------------------------------------------------------------------------------
                    " left join (select FMktSrcCode,FMktSrcName from " +
                    pub.yssGetTableName("Tb_Para_MarketSource") +
                    " ) d on a.FMktSrcCode = d.FMktSrcCode" +
                    //----------------------------------------------------------------------------------------------
                    " left join (select ib.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                    pub.yssGetTableName("Tb_Para_Security") +
                    " where FStartDate <= " +
                    dbl.sqlDate(new java.util.Date()) +
                    " and FCheckState = 1 group by FSecurityCode) ia join (select FSecurityCode, FSecurityName, FStartDate from " +
                    pub.yssGetTableName("Tb_Para_Security") +
                    ") ib on ia.FSecurityCode = ib.FSecurityCode and ia.FStartDate = ib.FStartDate) i on a.FSecurityCode = i.FSecurityCode " +
                    //----------------------------------------------------------------------------------------------
                    " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) j on a.FCatCode = j.FCatCode" +
                    //----------------------------------------------------------------------------------------------
                    " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) k on a.FSubCatCode = k.FSubCatCode" +
                    //----------------------------------------------------------------------------------------------
                    " left join (select FCusCatCode,FCusCatName from " +
                    pub.yssGetTableName("Tb_Para_CustomCategory") +
                    " where FCheckState = 1) m on  a.FCusCatCode = m.FCusCatCode " +
                    //----------------------------------------------------------------
                    //更改组合设置中估值方法报错，原因是未取出FExRateSrcName字段，2007-08-01，杨
                    " left join (select FExRateSrcCode,FExRateSrcName from " +
                    pub.yssGetTableName("Tb_Para_ExRateSource") +
                    " where FCheckState = 1) ex on a.FBaseRateSrcCode = ex.FExRateSrcCode" +
                    //----------------------------------------------------------------------------------------------
                    " left join (select nb.* from (select FSectorCode, max(FStartDate) as FStartDate from " +
                    pub.yssGetTableName("Tb_Para_Sector") +
                    " where FStartDate <= " +
                    dbl.sqlDate(new java.util.Date()) +
                    " and FCheckState = 1 group by FSectorCode) na join (select FSectorCode, FSectorName, FStartDate from " +
                    pub.yssGetTableName("Tb_Para_Sector") +
                    ") nb on na.FSectorCode = nb.FSectorCode and na.FStartDate = nb.FStartDate) n on a.FSecClsCode = n.FSectorCode " +
                    //----------------------------------------------------------------------------------------------
                    buildFilterSql() +
                    ") y on x.FMTVCode = y.FMTVCode and x.FStartDate = y.FStartDate" +
                    " order by y.FCheckState, y.FCreateTime desc";

                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FMTVCode") + "").trim()).
                        append("\t");
                    bufShow.append( (rs.getString("FMTVName") + "").trim()).
                        append("\t");
                    bufShow.append( (YssFun.formatDate(rs.getDate("FStartDate")))).
                        append(YssCons.YSS_LINESPLITMARK);

                    setMTVMethodAttr(rs);
                    bufAll.append(this.buildRowStr()).
                        append(YssCons.YSS_LINESPLITMARK);
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
            throw new YssException("获取估值方法维护数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() throws YssException {
        return "";
    }

    /**
     * getSetting
     *
     * @return IParaSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " +
                pub.yssGetTableName("Tb_Para_MTVMethod") +
                " where FMTVCode = " +
                dbl.sqlString(this.mTVCode) +
                " and FStartDate = (select max(FStartDate) from " +
                pub.yssGetTableName("Tb_Para_MTVMethod") +
                " where FMTVCode = " +
                dbl.sqlString(this.mTVCode) + ")";

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.mTVCode = rs.getString("FMTVCode") + "";
                this.mTVName = rs.getString("FMTVName") + "";
                this.startDate = rs.getDate("FStartDate");
                this.mktSrcCode = rs.getString("FMktSrcCode") + "";
                this.mktPriceCode = rs.getString("FMktPriceCode") + "";
                this.mTVMethod = rs.getString("FMTVMethod") + "";
                this.securityCode = rs.getString("FSecurityCode") + "";
                this.catCode = rs.getString("FCatCode") + "";
                this.subCatCode = rs.getString("FSubCatCode") + "";
                this.sectorCode = rs.getString("FSecClsCode") + "";
                this.cusCatCode = rs.getString("FCusCatCode") + "";
                //   this.exRateCode = rs.getString("FExRateCode") + "";
                //  this.exRateSrcCode = rs.getString("FExRateSrcCode") + "";
                this.desc = rs.getString("FDesc") + "";
                this.baseRateCode = rs.getString("FBaseRateCode");
                this.baseRateSrcCode = rs.getString("FBaseRateSrcCode");
                this.portRateSrcCode = rs.getString("FPortRateSrcCode");
                this.portRateCode = rs.getString("FPortRateCode");
                this.checkStateId = rs.getInt("FCheckState");
                this.checkStateName = YssFun.getCheckStateName(rs.getInt(
                    "FCheckState"));
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FcreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime") + "";
                /**add---shashijie 2013-3-30 STORY 3528 投资类型*/
                this.FInvestmentType = rs.getString("FInvestmentType");
        		/**end---shashijie 2013-3-30 STORY 3528*/
            }
        } catch (Exception e) {
            throw new YssException("获取估值方法信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
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
     * saveSetting
     * 设置估值方法
     * @param btOper byte
     */
    /*  public void saveSetting(byte btOper) throws YssException {
         String strSql = "";
         boolean bTrans = false;
         Connection conn = dbl.loadConnection();
         String sErr = "";
         try {
            if (btOper == YssCons.OP_ADD) {
     strSql = "insert into " + pub.yssGetTableName("Tb_Para_MTVMethod") +
                     "" +
     "(FMTVCode,FMTVName,FStartDate,FMktSrcCode,FMktPriceCode," +
     "FMTVMethod,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) values(" +
                     dbl.sqlString(this.mTVCode) + "," +
                     dbl.sqlString(this.mTVName) + "," +
                     dbl.sqlDate(this.startDate) + "," +
                     dbl.sqlString(this.mktSrcCode) + "," +
                     dbl.sqlString(this.mktPriceCode) + "," +
                     dbl.sqlString(this.mTVMethod) + "," +
                     dbl.sqlString(this.desc) + "," +
                     (pub.getSysCheckState() ? "0" : "1") + "," +
                     dbl.sqlString(this.creatorCode) + "," +
                     dbl.sqlString(this.creatorTime) + "," +
                     (pub.getSysCheckState() ? "' '" :
                      dbl.sqlString(this.creatorCode)) + ")";
            }
            else if (btOper == YssCons.OP_EDIT) {
               if (this.mTVMethodLinks.length() == 0)
               {
     strSql = "update " + pub.yssGetTableName("Tb_Para_MTVMethod") +
                        " set " +
                        " FMTVCode = " + dbl.sqlString(this.mTVCode) +
                        ",FMTVName = " + dbl.sqlString(this.mTVName) +
                        ",FStartDate = " + dbl.sqlDate(this.startDate) +
                        ",FMktSrcCode = " + dbl.sqlString(this.mktSrcCode) +
     ",FMktPriceCode = " + dbl.sqlString(this.mktPriceCode) +
                        ",FMTVMethod = " + dbl.sqlString(this.mTVMethod) +
                        ",FDesc = " + dbl.sqlString(this.desc) +
     ",FCheckState = " + (pub.getSysCheckState() ? "0" : "1") +
                        ",FCreator = " + dbl.sqlString(this.creatorCode) +
                        ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                        ",FCheckUser = " + (pub.getSysCheckState() ? "' '" :
                                            dbl.sqlString(this.creatorCode)) +
                        " where FMTVCode = " +
                        dbl.sqlString(this.oldMTVCode) +
                        " and FStartDate = " + dbl.sqlDate(this.oldStartDate);
               }
               else
               {
     strSql = "update " + pub.yssGetTableName("Tb_Para_MTVMethod") +
                        " set " +
                        " FCatCode = " + dbl.sqlString(this.catCode) +
                        ",FSubCatCode = " + dbl.sqlString(this.subCatCode) +
                        ",FCusCatCode = " + dbl.sqlString(this.cusCatCode) +
                        ",FSecClsCode = " + dbl.sqlString(this.sectorCode) +
     ",FSecurityCode = " + dbl.sqlString(this.securityCode) +
     ",FCheckState = " + (pub.getSysCheckState() ? "0" : "1") +
                        ",FCreator = " + dbl.sqlString(this.creatorCode) +
                        ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                        ",FCheckUser = " + (pub.getSysCheckState() ? "' '" :
                                            dbl.sqlString(this.creatorCode)) +
                        " where FMTVCode = " + dbl.sqlString(this.oldMTVCode) +
                        " and FStartDate = " + dbl.sqlDate(this.oldStartDate);
               }
            }
            else if (btOper == YssCons.OP_DEL) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_MTVMethod") +
                     " set FCheckState = " + this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                     "' where FMTVCode = " + dbl.sqlString(this.mTVCode) +
                     " and FStartDate = " + dbl.sqlDate(this.startDate);
            }
            else if (btOper == YssCons.OP_AUDIT) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_MTVMethod") +
                     " set FCheckState = " +
     this.checkStateId + ", FCheckUser = '" + pub.getUserCode() +
                     "', FCheckTime = '" +
                     YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where FMTVCode = " +
                     dbl.sqlString(this.mTVCode) +
                     " and FStartDate = " + dbl.sqlDate(this.startDate);
            }
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

// 关联处理
            if (btOper == YssCons.OP_EDIT &&
                (this.mTVCode != this.oldMTVCode ||
                 this.startDate != this.oldStartDate)) {
     strSql = "update " + pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                     " set FMTVCode = " + dbl.sqlString(this.mTVCode) +
                     ",FStartDate = " + dbl.sqlDate(this.startDate) +
                     " where FMTVCode = " + dbl.sqlString(this.oldMTVCode) +
                     " and FStartDate = " + dbl.sqlDate(this.oldStartDate);
               dbl.executeSql(strSql);
            }

            if (this.mTVMethodLinks.length() != 0 && btOper == YssCons.OP_EDIT)
            {
     MTVMethodLinkBean link = (MTVMethodLinkBean) pub.getParaSettingCtx().
                        getBean("mtvmethodlink");
               link.setYssPub(pub);
               link.saveMutliSetting(this.mTVMethodLinks, true);
            }
            if (btOper == YssCons.OP_DEL) {
     strSql = "update " + pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                     " set FCheckState = " + this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where FMTVCode = " + dbl.sqlString(this.mTVCode) +
                     " and FStartDate = " + dbl.sqlDate(this.startDate);
               dbl.executeSql(strSql);
            }
            if (btOper == YssCons.OP_AUDIT) {
     strSql = "update " + pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                     " set FCheckState = " + this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where FMTVCode = " + dbl.sqlString(this.mTVCode) +
                     " and FStartDate = " + dbl.sqlDate(this.startDate);
               dbl.executeSql(strSql);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

         }
         catch (Exception e) {
            throw new YssException("设置估值方法出错", e);
         }
         finally {
            dbl.endTransFinal(conn, bTrans);
         }
      }*/
    /**
     * MS00008 edit by 宋洁 2009-02-13
     * 若设置了筛选条件，还要保存筛选条件设置列表中的筛选条件信息到估值方法筛选条件表中
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

            //---MS00008 add by 宋洁 2009-02-19
            if (this.sMTVSelCondSet != null &&
                this.sMTVSelCondSet.trim().length() != 0) {
                MTVSelCondSetBean mtvSelCondSet = new MTVSelCondSetBean();
                mtvSelCondSet.setYssPub(pub);
                mtvSelCondSet.saveMutliSelCondData(this.sMTVSelCondSet, true, this); //添加估值方法筛选条件信息
            }
            //若点击复制按钮新建的估值方法，相应的估值方法链接信息也要被复制到新的估值方法代码相关的估值方法链接信息中
            if (!this.oldMTVCode.equals("") && this.oldMTVCode.length() > 0) {
                strSql = "insert into " +
                    pub.yssGetTableName("Tb_Para_MTVMethodLink")
                    +
                    //edit by songjie 2011.06.16 复制估值方法设置数据，修改估值方法代码后，点击确定 报 违反唯一键约束错误
                    "(fmtvcode,flinkcode,fstartdate,fcheckstate,fcreator,fcreatetime)(select distinct "
                    + dbl.sqlString(this.mTVCode) + "," + "flinkcode," +
                    dbl.sqlDate(this.startDate)
                    + "," + (pub.getSysCheckState() ? 0 : 1) + "," +
                    dbl.sqlString(this.creatorName)
                    + "," + dbl.sqlString(this.creatorTime) + " from " +
                    pub.yssGetTableName("tb_para_mtvmethodlink")
                    + " where fmtvcode=" + dbl.sqlString(this.oldMTVCode) +
                    ")";
                dbl.executeSql(strSql);
            }
            //---MS00008 add by 宋洁 2009-02-19
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_MTVMethod") +
                "" +
                "(FMTVCode,FMTVName,FStartDate,FMktSrcCode,FMktPriceCode," +
                "FMTVMethod,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser," +
                "FBaseRateSrcCode,FBaseRateCode,FPortRateSrcCode,FPortRateCode" +
                /**add---shashijie 2013-3-30 STORY 3528 投资类型*/
                " , FInvestmentType "+
        		/**end---shashijie 2013-3-30 STORY 3528*/
                " ) values(" +
                dbl.sqlString(this.mTVCode) + "," +
                dbl.sqlString(this.mTVName) + "," +
                dbl.sqlDate(this.startDate) + "," +
                dbl.sqlString(this.mktSrcCode) + "," +
                dbl.sqlString(this.mktPriceCode) + "," +
                this.mTVMethod + "," +
                //    dbl.sqlString(this.exRateCode) + "," +
                //   dbl.sqlString(this.exRateSrcCode) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + "," +
                dbl.sqlString(this.baseRateSrcCode) + "," +
                dbl.sqlString(this.baseRateCode) + "," +
                dbl.sqlString(this.portRateSrcCode) + "," +
                dbl.sqlString(this.portRateCode) +
                /**add---shashijie 2013-3-30 STORY 3528 投资类型*/
                " , " + dbl.sqlString(this.FInvestmentType) +
        		/**end---shashijie 2013-3-30 STORY 3528*/
                ")";

            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("增加估值方法信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
    }

    /**
     * MS00008 edit by 宋洁 2009-02-13
     * 若设置了筛选条件，还要保存筛选条件设置列表中的筛选条件信息到估值方法筛选条件表中
     * editSetting
     *
     * @return String
     */

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            MTVSelCondSetBean mtvSelCondSet = new MTVSelCondSetBean();
            mtvSelCondSet.setYssPub(pub);
            bTrans = true;
            if (this.mTVMethodLinks.length() == 0) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_MTVMethod") +
                    " set " +
                    " FMTVCode = " + dbl.sqlString(this.mTVCode) +
                    ",FMTVName = " + dbl.sqlString(this.mTVName) +
                    ",FStartDate = " + dbl.sqlDate(this.startDate) +
                    ",FMktSrcCode = " + dbl.sqlString(this.mktSrcCode) +
                    ",FMktPriceCode = " + dbl.sqlString(this.mktPriceCode) +
                    ",FMTVMethod = " + this.mTVMethod +
                    //     ",FExRateSrcCode = " + dbl.sqlString(this.exRateSrcCode) +
                    //   ",FExRateCode = " + dbl.sqlString(this.exRateCode) +
                    ",FDesc = " + dbl.sqlString(this.desc) +
                    ",FPortRateCode=" + dbl.sqlString(this.portRateCode) +
                    ",FPortRateSrcCode=" +
                    dbl.sqlString(this.portRateSrcCode) +
                    ",FBaseRateCode=" + dbl.sqlString(this.baseRateCode) +
                    ",FBaseRateSrcCode=" +
                    dbl.sqlString(this.baseRateSrcCode) +
                    ",FCheckState = " +
                    (pub.getSysCheckState() ? "0" : "1") +
                    ",FCreator = " + dbl.sqlString(this.creatorCode) +
                    ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                    ",FCheckUser = " + (pub.getSysCheckState() ? "' '" :
                                        dbl.sqlString(this.creatorCode)) +
                    /**add---shashijie 2013-3-30 STORY 3528 投资类型*/
                    " , FInvestmentType = " + dbl.sqlString(this.FInvestmentType) +
            		/**end---shashijie 2013-3-30 STORY 3528*/
                    " where FMTVCode = " +
                    dbl.sqlString(this.oldMTVCode) +
                    " and FStartDate = " + dbl.sqlDate(this.oldStartDate);
            } else {
                strSql = "update " + pub.yssGetTableName("Tb_Para_MTVMethod") +
                    " set " +
                    " FCatCode = " + dbl.sqlString(this.catCode) +
                    ",FSubCatCode = " + dbl.sqlString(this.subCatCode) +
                    ",FCusCatCode = " + dbl.sqlString(this.cusCatCode) +
                    ",FSecClsCode = " + dbl.sqlString(this.sectorCode) +
                    ",FPortRateCode=" + dbl.sqlString(this.portRateCode) +
                    ",FPortRateSrcCode=" +
                    dbl.sqlString(this.portRateSrcCode) +
                    ",FBaseRateCode=" + dbl.sqlString(this.baseRateCode) +
                    ",FBaseRateSrcCode=" +
                    dbl.sqlString(this.baseRateSrcCode) +
                    ",FSecurityCode = " + dbl.sqlString(this.securityCode) +
                    //    ",FExRateSrcCode = " + dbl.sqlString(this.exRateSrcCode) +
                    //   ",FExRateCode = " + dbl.sqlString(this.exRateCode) +
                    ",FCheckState = " +
                    (pub.getSysCheckState() ? "0" : "1") +
                    ",FCreator = " + dbl.sqlString(this.creatorCode) +
                    ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                    ",FCheckUser = " + (pub.getSysCheckState() ? "' '" :
                                        dbl.sqlString(this.creatorCode)) +
                    /**add---shashijie 2013-3-30 STORY 3528 投资类型*/
                    " , FInvestmentType = " + dbl.sqlString(this.FInvestmentType) +
            		/**end---shashijie 2013-3-30 STORY 3528*/
                    " where FMTVCode = " + dbl.sqlString(this.oldMTVCode) +
                    " and FStartDate = " + dbl.sqlDate(this.oldStartDate);
            }

            dbl.executeSql(strSql);
			/**shashijie 2012-7-2 STORY 2475 */
            if (!this.mTVCode.equals(this.oldMTVCode) ||
			/**end*/
                this.startDate != this.oldStartDate) {
                strSql = "update " +
                    pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                    " set FMTVCode = " + dbl.sqlString(this.mTVCode) +
                    ",FStartDate = " + dbl.sqlDate(this.startDate) +
                    " where FMTVCode = " + dbl.sqlString(this.oldMTVCode) +
                    " and FStartDate = " + dbl.sqlDate(this.oldStartDate);
                dbl.executeSql(strSql);
            }
            //------------------xuqiji 20100628 MS01343    估值方式设置中筛选条件无法删除 QDV4赢时胜(测试)2010年06月23日01_B ------------//
            strSql = "delete from " + pub.yssGetTableName("TB_PARA_MTVSELCONDSET") +
            		 " where FMTVCode=" + dbl.sqlString(this.mTVCode);

            dbl.executeSql(strSql); //在估值方法筛选条件表中先删除估值方法代码对应的所有筛选条件信息
            //-----------------------------------------ened-----------------------//
            //--MS00008 delete by 宋洁  2009-02-23 估值方法链接信息已经不在本类中保存
            /*if (this.mTVMethodLinks.length() != 0) {
                MTVMethodLinkBean link = (MTVMethodLinkBean) pub.
                                         getParaSettingCtx().
                                         getBean("mtvmethodlink");
                link.setYssPub(pub);
                link.saveMutliSetting(this.mTVMethodLinks, true);
                         }*/
            //-- MS00008 delete by 宋洁 2009-02-23 估值方法链接信息已经不在本类中保存
            //---MS00008 add by 宋洁 2009-02-19 修改估值方法筛选条件信息
            if (this.sMTVSelCondSet != null &&
                this.sMTVSelCondSet.trim().length() != 0) {
                MTVSelCondSetBean selCondSet = new MTVSelCondSetBean();
                selCondSet.setYssPub(pub);
                selCondSet.saveMutliSelCondData(this.sMTVSelCondSet, true, this);
            }

            MTVSelCondSetBean mtvBean = new MTVSelCondSetBean();
            mtvBean.setMTVCode(this.mTVCode);
            mtvSelCondSet.setFilterType(mtvBean);
            mtvBean = mtvSelCondSet.getFilterType();
            this.setASubData(mtvSelCondSet.getListViewData1());
            //---MS00008 add by 宋洁 2009-02-19 修改估值方法筛选条件信息

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改估值方法设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除估值方法的数据，即是放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Para_MTVMethod") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "' where FMTVCode = " + dbl.sqlString(this.mTVCode) +
                " and FStartDate = " + dbl.sqlDate(this.startDate);
            dbl.executeSql(strSql);

            strSql = "update " + pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FMTVCode = " + dbl.sqlString(this.mTVCode);
            //+ " and FStartDate = " + dbl.sqlDate(this.startDate);MS00008 delete by 宋洁 2009-02-24 启用日期
            dbl.executeSql(strSql);

            //--MS00008 add by 宋洁 2009-02-20 更新估值方法筛选条件审核状态
            strSql = "update " + pub.yssGetTableName("Tb_PARA_MTVSelCondSet") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "' where FMTVCODE = " + dbl.sqlString(this.mTVCode);
            dbl.executeSql(strSql);
            //--MS00008 add by 宋洁 2009-02-20 更新估值方法筛选条件审核状态

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除估值方法设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**修改时间：2008年3月20号
     *  修改人：单亮
     *  原方法功能：只能处估值方法的审核和未审核的单条信息。
     *  新方法功能：可以处理估值方法的审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     */
    public void checkSetting() throws YssException {
//      String strSql = "";
//      boolean bTrans = false; //代表是否开始了事务
//      Connection conn = dbl.loadConnection();
//      try {
//         conn.setAutoCommit(false);
//         bTrans = true;
//         strSql = "update " + pub.yssGetTableName("Tb_Para_MTVMethod") +
//               " set FCheckState = " +
//               this.checkStateId + ", FCheckUser = '" + pub.getUserCode() +
//               "', FCheckTime = '" +
//               YssFun.formatDatetime(new java.util.Date()) + "'" +
//               " where FMTVCode = " +
//               dbl.sqlString(this.mTVCode) +
//               " and FStartDate = " + dbl.sqlDate(this.startDate);
//         dbl.executeSql(strSql);
//         strSql = "update " + pub.yssGetTableName("Tb_Para_MTVMethodLink") +
//               " set FCheckState = " + this.checkStateId +
//               ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//               ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//               "'" +
//               " where FMTVCode = " + dbl.sqlString(this.mTVCode) +
//               " and FStartDate = " + dbl.sqlDate(this.startDate);
//         dbl.executeSql(strSql);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//      }
//
//      catch (Exception e) {
//         throw new YssException("审核估值方法设置信息出错", e);
//      }
//      finally {
//         dbl.endTransFinal(conn, bTrans);
//      }
        //修改后的方法
        //--------------------------------begin
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "update " +
                        pub.yssGetTableName("Tb_Para_MTVMethod") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = '" +
                        pub.getUserCode() +
                        "', FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FMTVCode = " +
                        dbl.sqlString(this.mTVCode) +
                        " and FStartDate = " + dbl.sqlDate(this.startDate);
                    dbl.executeSql(strSql);

                    strSql = "update " +
                        pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "'" +
                        " where FMTVCode = " + dbl.sqlString(this.mTVCode);
                    //+" and FStartDate = " + dbl.sqlDate(this.startDate); MS00008 delete by 宋洁 2009-02-24 启用日期已经没有用了
                    dbl.executeSql(strSql);

                    //--MS00008 add by 宋洁 2009-03-03 更新筛选条件审核状态
                    strSql = "update " +
                        pub.yssGetTableName("Tb_Para_MTVSelCondSet") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FMTVCode = " +
                        dbl.sqlString(this.mTVCode);
                    dbl.executeSql(strSql);
                    //--MS00008 add by 宋洁 2009-03-03 更新筛选条件审核状态
                }
            }
            //如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (mTVCode != null && !mTVCode.equalsIgnoreCase("")) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_MTVMethod") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = '" +
                    pub.getUserCode() +
                    "', FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FMTVCode = " +
                    dbl.sqlString(this.mTVCode) +
                    " and FStartDate = " + dbl.sqlDate(this.startDate);
                dbl.executeSql(strSql);

                strSql = "update " +
                    pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                    " set FCheckState = " + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "'" +
                    " where FMTVCode = " + dbl.sqlString(this.mTVCode);
                //+ " and FStartDate = " + dbl.sqlDate(this.startDate); MS00008 delete by 宋洁  2009-02-24 启用日期已经没有用了
                dbl.executeSql(strSql);

                //--MS00008 add by 宋洁 2009-03-03 更新筛选条件审核状态
                strSql = "update " +
                    pub.yssGetTableName("Tb_Para_MTVSelCondSet") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FMTVCode = " +
                    dbl.sqlString(this.mTVCode);
                dbl.executeSql(strSql);
                //--MS00008 add by 宋洁 2009-03-03 更新筛选条件审核状态
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("审核估值方法设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //--------------------------------------end
    }

    /**
     * 为各项变量赋值
     *
     */
    public void setMTVMethodAttr(ResultSet rs) throws SQLException {
        this.mTVCode = rs.getString("FMTVCode") + "";
        this.mTVName = rs.getString("FMTVName") + "";
        this.startDate = rs.getDate("FStartDate");
        this.mktPriceCode = rs.getString("FMktPriceCode") + "";
        this.mTVMethod = rs.getString("FMTVMethod") + "";
        this.mktSrcCode = rs.getString("FMktSrcCode") + "";
        this.mktSrcName = rs.getString("FMktSrcName") + "";
        this.catCode = rs.getString("FCatCode") + "";
        this.catName = rs.getString("FCatName") + "";
        this.subCatCode = rs.getString("FSubCatCode") + "";
        this.subCatName = rs.getString("FSubCatName") + "";
        this.sectorCode = rs.getString("FSecClsCode") + "";
        this.sectorName = rs.getString("FSectorName") + "";
        this.cusCatCode = rs.getString("FCusCatCode") + "";
        this.cusCatName = rs.getString("FCusCatName") + "";
        this.securityCode = rs.getString("FSecurityCode") + "";
        this.securityName = rs.getString("FSecurityName") + "";
        //   this.exRateCode = rs.getString("FExRateCode") + "";
        //    this.exRateSrcCode = rs.getString("FExRateSrcCode")+"";
        //   this.exRateSrcName = rs.getString("FExRateSrcName") + "";

        this.baseRateCode = rs.getString("FBaseRateCode");
        this.baseRateSrcCode = rs.getString("FBaseRateSrcCode");
        this.baseRateSrcName = rs.getString("FBaseRateSrcName");
        this.portRateCode = rs.getString("FPortRateCode");
        this.portRateSrcCode = rs.getString("FPortRateSrcCode");
        this.portRateSrcName = rs.getString("FPortRateSrcName");
        this.desc = rs.getString("FDesc") + "";
        /**add---shashijie 2013-3-30 STORY 3528 投资类型*/
        this.FInvestmentType = rs.getString("FInvestmentType") == null ? "No" : rs.getString("FInvestmentType");
		/**end---shashijie 2013-3-30 STORY 3528*/
        super.setRecLog(rs);
    }

    /**
     * MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009.04.08
     * 获取估值方法代码字符串对应的估值方法实例的ArrayList
     * @param mtvCodes String 估值方法代码字符串 各估值方法代码之间用逗号隔开
     * @return ArrayList
     * @throws YssException
     */
    public ArrayList getMTVInfo(String mtvCodes) throws YssException {
        ArrayList mtvList = new ArrayList(); //新建ArrayList用于储存各估值方法实例
        ResultSet rs = null; //声明结果集
        String[] mtvCode = null; //声明估值方法，用于储存拆分后的估值方法代码
        HashSet mtvMethodLinkcode = null; //用于储存估值方法链接代码级估值方法对应的证券代码
        String strSql = ""; //声明sql语句
        try {
            if (mtvCodes != null && mtvCodes.length() > 0) { //若估值方法不为空且有数据
                mtvCode = mtvCodes.split("\t"); //分割字符串
            }
            for (int i = 0; i < mtvCode.length; i++) { //循环各个估值方法代码
                strSql = " select FMtvCode,FStartDate,FMTVName,FMktSrcCode,FMktPriceCode,FBaseRateSrcCode,FBaseRateCode," +
                    " FPortRateSrcCode,FPortRateCode, FMTVMethod,FCatCode,FSubCatCode,FCusCatCode,FSecClsCode," +
                    " FSecurityCode,FDesc,FcheckState from " +
                    pub.yssGetTableName("tb_para_mtvmethod");

                if (!mtvCode[i].equals("")) { //若估值方法代码不为空
                    strSql += " where FMTVCode = " +
                        dbl.sqlString(mtvCode[i].trim()) +
                        " and FCheckState = 1";

                    rs = dbl.openResultSet(strSql);

                    while (rs.next()) {
                        MTVMethodBean mtvMethod = new MTVMethodBean(); //新建估值方法实例
                        MTVMethodLinkBean mtvMethodLink = new MTVMethodLinkBean(); //新建估值方法链接实例
                        mtvMethodLink.setYssPub(pub); //设置pub

                        mtvMethodLinkcode = mtvMethodLink.getAllMTVMethodLinkCode(mtvCode[i]); //获取估值方法代码对应的证券代码
                        mtvMethod.setMTVMethodLinkCode(mtvMethodLinkcode); //给估值方法实例设置证券代码信息的HashSet

                        mtvMethod.setMTVCode(rs.getString("FMTVCode")); //估值方法代码
                        mtvMethod.setStartDate(rs.getDate("FStartDate")); //起始日期
                        mtvMethod.setMTVName(rs.getString("FMTVName")); //估值方法名称
                        mtvMethod.setMktSrcCode(rs.getString("FMktSrcCode")); //行情来源代码
                        mtvMethod.setMktPriceCode(rs.getString("FMktPriceCode")); //行情字段
                        mtvMethod.setBaseRateSrcCode(rs.getString("FBaseRateSrcCode")); //基础汇率来源代码
                        mtvMethod.setBaseRateCode(rs.getString("FBaseRateCode")); //基础汇率字段
                        mtvMethod.setPortRateSrcCode(rs.getString("FPortRateSrcCode")); //组合汇率来源代码
                        mtvMethod.setPortRateCode(rs.getString("FPortRateCode")); //组合汇率字段
                        mtvMethod.setMTVMethod(rs.getString("FMTVMethod")); //估值方法代码
                        mtvMethod.setCatCode(rs.getString("FCatCode")); //品种类型代码
                        mtvMethod.setSubCatCode(rs.getString("FSubCatCode")); //品种子类型代码
                        mtvMethod.setCusCatCode(rs.getString("FCusCatCode")); //自定义子类型代码
                        mtvMethod.setSectorCode(rs.getString("FSecClsCode")); //证券板块代码
                        mtvMethod.setSecurityCode(rs.getString("FSecurityCode")); //证券代码
                        mtvMethod.setDesc(rs.getString("FDesc")); //描述信息
                        mtvMethod.setCheckStateId(rs.getInt("FcheckState")); //设置审核状态

                        mtvList.add(mtvMethod); //将估值方法实例添加到mtvList中
                    }
                }
                dbl.closeResultSetFinal(rs);
            }
            if (mtvList.size() == 0) { //若没有相关估值方法信息则返回null
                return null;
            }
        } catch (NullPointerException ex) {//STORY #2475 根据FindBugs工具，对系统进行全面检查  zhangjun
            throw new YssException(ex.getMessage());  
        } catch (Exception e) {
            throw new YssException("获取估值方法信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return mtvList;
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        MTVMethodBean befEditBean = new MTVMethodBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select y.* from " +
                "(select FMTVCode,FCheckState,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_MTVMethod") + " " +
                " where FStartDate <= " +
                dbl.sqlDate(new java.util.Date()) +
                "and FCheckState <> 2 group by FMTVCode,FCheckState) x join" +
                " (select a.*, f.FVocName as FMktPriceCodeValue," +
                " h.FVocName as FMTVMethodValue," +
                " d.FMktSrcName, i.FSecurityName,j.FCatName," +
                " k.FSubCatName,m.FCusCatName,n.FSectorName," +
                " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_MTVMethod") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //----------------------------------------------------------------------------------------------
                " left join (select FMktSrcCode,FMktSrcName from " +
                pub.yssGetTableName("Tb_Para_MarketSource") +
                " ) d on a.FMktSrcCode = d.FMktSrcCode" +
                //----------------------------------------------------------------------------------------------
                " left join (select ib.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) ia join (select FSecurityCode, FSecurityName, FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") ib on ia.FSecurityCode = ib.FSecurityCode and ia.FStartDate = ib.FStartDate) i on a.FSecurityCode = i.FSecurityCode " +
                //----------------------------------------------------------------------------------------------
                " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) j on a.FCatCode = j.FCatCode" +
                //----------------------------------------------------------------------------------------------
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) k on a.FSubCatCode = k.FSubCatCode" +
                //----------------------------------------------------------------------------------------------
                " left join (select FCusCatCode,FCusCatName from " +
                pub.yssGetTableName("Tb_Para_CustomCategory") +
                " where FCheckState = 1) m on  a.FCusCatCode = m.FCusCatCode " +
                //----------------------------------------------------------------------------------------------
                " left join (select nb.* from (select FSectorCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Sector") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSectorCode) na join (select FSectorCode, FSectorName, FStartDate from " +
                pub.yssGetTableName("Tb_Para_Sector") +
                ") nb on na.FSectorCode = nb.FSectorCode and na.FStartDate = nb.FStartDate) n on a.FSecClsCode = n.FSectorCode " +
                //----------------------------------------------------------------------------------------------
                " left join Tb_Fun_Vocabulary f on a.FMktPriceCode = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_MTV_MARKETVALUE) +
                " left join Tb_Fun_Vocabulary h on " +
                dbl.sqlToChar("a.FMTVMethod") +
                " = h.FVocCode and h.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_MTV_MTVMETHOD) +
                " where  FMTVCode =" + dbl.sqlString(this.oldMTVCode) +
                ") y on x.FMTVCode = y.FMTVCode and x.FStartDate = y.FStartDate" +
                " order by y.FCheckState, y.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.mTVCode = rs.getString("FMTVCode") + "";
                befEditBean.mTVName = rs.getString("FMTVName") + "";
                befEditBean.startDate = rs.getDate("FStartDate");
                befEditBean.mktPriceCode = rs.getString("FMktPriceCode") + "";
                befEditBean.mTVMethod = rs.getString("FMTVMethod") + "";
                befEditBean.mktSrcCode = rs.getString("FMktSrcCode") + "";
                befEditBean.mktSrcName = rs.getString("FMktSrcName") + "";
                befEditBean.catCode = rs.getString("FCatCode") + "";
                befEditBean.catName = rs.getString("FCatName") + "";
                befEditBean.subCatCode = rs.getString("FSubCatCode") + "";
                befEditBean.subCatName = rs.getString("FSubCatName") + "";
                befEditBean.sectorCode = rs.getString("FSecClsCode") + "";
                befEditBean.sectorName = rs.getString("FSectorName") + "";
                befEditBean.cusCatCode = rs.getString("FCusCatCode") + "";
                befEditBean.cusCatName = rs.getString("FCusCatName") + "";
                befEditBean.securityCode = rs.getString("FSecurityCode") + "";
                befEditBean.securityName = rs.getString("FSecurityName") + "";
                //    befEditBean.exRateCode = rs.getString("FExRateCode") + "";
                //  befEditBean.exRateSrcCode = rs.getString("FExRateSrcCode")+"";
                befEditBean.desc = rs.getString("FDesc") + "";

                befEditBean.baseRateCode = rs.getString("FBaseRateCode") + "";
                befEditBean.baseRateSrcCode = rs.getString("FBaseRateSrcCode") +
                    "";
                befEditBean.portRateCode = rs.getString("FPortRateCode") + "";
                befEditBean.portRateSrcCode = rs.getString("FPortRateSrcCode") +
                    "";
                /**add---shashijie 2013-3-30 STORY 3528 投资类型*/
                befEditBean.FInvestmentType = rs.getString("FInvestmentType");
        		/**end---shashijie 2013-3-30 STORY 3528*/
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    /**
     * 从估值方法中的回收站删除数据，即是彻底删除
     */
    /**
     * MS00008 edit by 宋洁 2009-02-13
     * 若要清空的估值方法中有筛选条件信息，
     * 还需要将相应的筛选条件信息从估值方法筛选条件表中删除
     * @throws YssException
     */

    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
        	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equals("")) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Para_MTVMethod") +
                        " where FMTVCode = " + dbl.sqlString(this.mTVCode) +
                        " and FStartDate = " + dbl.sqlDate(this.startDate);
                    //执行sql语句
                    dbl.executeSql(strSql);

                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                        " where FMTVCode = " + dbl.sqlString(this.mTVCode);
                    //+ " and FStartDate = " + dbl.sqlDate(this.startDate); MS00008 delete by 宋洁 2009-02-24 启用日期已经没有用了
                    //执行sql语句
                    dbl.executeSql(strSql);

                    //--MS00008 add by 宋洁 2009-02-20 删除相应的估值方法筛选条件信息
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Para_MTVSelCondSet") +
                        " where FMTVCode = " +
                        dbl.sqlString(this.mTVCode);
                    dbl.executeSql(strSql);
                    //--MS00008 add by 宋洁 2009-02-20 删除相应的估值方法筛选条件信息
                }

            }
            //sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (mTVCode != null && !mTVCode.equals("")) {
                strSql = "delete from " +
                    pub.yssGetTableName("tb_para_tradefeelink") +
                    " where FMTVCode = " + dbl.sqlString(this.mTVCode) +
                    " and FStartDate = " + dbl.sqlDate(this.startDate);
                //执行sql语句
                dbl.executeSql(strSql);

                //--MS00008 add by 宋洁 2009-02-20
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_MTVSelCondSet") +
                    " where FMTVCode = " +
                    dbl.sqlString(this.mTVCode);
                dbl.executeSql(strSql);
                //--MS00008 add by 宋洁 2009-02-20

            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        }

        catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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

	/**add---shashijie 2013-3-30 返回 fInvestmentType 的值*/
	public String getFInvestmentType() {
		return FInvestmentType;
	}

	/**add---shashijie 2013-3-30 传入fInvestmentType 设置  fInvestmentType 的值*/
	public void setFInvestmentType(String fInvestmentType) {
		FInvestmentType = fInvestmentType;
	}
}
