package com.yss.main.parasetting;

import java.sql.*;
import com.yss.dsub.*;
import com.yss.util.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;

/**
 * <p>Title: CashAccLinkBean</p>
 * <p>Description: 现金帐户链接信息设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */

public class CashAccLinkBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strCashAcctCode = ""; //现金帐户代码
    private String strCashAcctName = ""; //现金帐户名称
    private String sAuxiCashAcctCode = ""; //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
    private String sAuxiCashAcctName = ""; //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505

    private String strCatCode = ""; //投资品种类型代码
    private String strCatName = ""; //投资品种类型名称
    private String strSubCatCode = ""; //投资品种子类型代码
    private String strSubCatName = ""; //投资品种子类型名称
    private String strInvMgrCode = ""; //投资经理代码
    private String strInvMgrName = ""; //投资经理名称
    private String strPortCode = ""; //组合代码
    private String strPortName = ""; //组合名称
    private String strExchangeCode = ""; //交易所代码
    private String strExchangeName = ""; //交易所名称
    private String strBrokerCode = ""; //交易券商代码
    private String strBrokerName = ""; //交易券商名称
    private String strTradeTypeCode = ""; //交易方式代码
    private String strTradeTypeName = ""; //交易方式名称
    private String strDesc = ""; //描述
    private String strAcctState = ""; //帐户状态
    private String checkAccLinks = ""; //批量审核
    private String CuryCode = ""; //货币类型
    private String CuryName = ""; //货币名称
    private String sRecycled = "";

    private String strOldCashAcctCode = "";
    private String strOldCatCode = "";
    private String strOldSubCatCode = "";
    private String strOldInvMgrCode = "";
    private String strOldPortCode = "";
    private String strOldExchangeCode = "";
    private String strOldBrokerCode = "";
    private String strOldTradeTypeCode = "";
    private java.util.Date dtStartDate;
    private java.util.Date dtOldStartDate;
    private CashAccLinkBean filterType;
    private String strSecurityCode = ""; //用于获取链接信息时使用 sj add
    
    /**shashijie 2012-3-21 BUG 4005 */
	private String oldFCuryCode = "";
	/**end*/
    
    public void setStrAcctState(String strAcctState) {
        this.strAcctState = strAcctState;
    }

    public void setStrCashAcctCode(String strCashAcctCode) {
        this.strCashAcctCode = strCashAcctCode;
    }

    public void setStrOldInvMgrCode(String strOldInvMgrCode) {
        this.strOldInvMgrCode = strOldInvMgrCode;
    }

    public void setStrOldExchangeCode(String strOldExchangeCode) {
        this.strOldExchangeCode = strOldExchangeCode;
    }

    public void setDtStartDate(Date dtStartDate) {
        this.dtStartDate = dtStartDate;
    }

    public void setStrTradeTypeCode(String strTradeTypeCode) {
        this.strTradeTypeCode = strTradeTypeCode;
    }

    public void setStrPortCode(String strPortCode) {
        this.strPortCode = strPortCode;
    }

    public void setStrOldBrokerCode(String strOldBrokerCode) {
        this.strOldBrokerCode = strOldBrokerCode;
    }

    public void setStrSubCatName(String strSubCatName) {
        this.strSubCatName = strSubCatName;
    }

    public void setStrCashAcctName(String strCashAcctName) {
        this.strCashAcctName = strCashAcctName;
    }

    public void setStrInvMgrCode(String strInvMgrCode) {
        this.strInvMgrCode = strInvMgrCode;
    }

    public void setStrExchangeCode(String strExchangeCode) {
        this.strExchangeCode = strExchangeCode;
    }

    public void setStrOldPortCode(String strOldPortCode) {
        this.strOldPortCode = strOldPortCode;
    }

    public void setStrOldCashAcctCode(String strOldCashAcctCode) {
        this.strOldCashAcctCode = strOldCashAcctCode;
    }

    public void setStrTradeTypeName(String strTradeTypeName) {
        this.strTradeTypeName = strTradeTypeName;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public void setStrPortName(String strPortName) {
        this.strPortName = strPortName;
    }

    public void setFilterType(CashAccLinkBean filterType) {
        this.filterType = filterType;
    }

    public void setStrOldCatCode(String strOldCatCode) {
        this.strOldCatCode = strOldCatCode;
    }

    public void setStrCatCode(String strCatCode) {
        this.strCatCode = strCatCode;
    }

    public void setStrInvMgrName(String strInvMgrName) {
        this.strInvMgrName = strInvMgrName;
    }

    public void setCheckAccLinks(String checkAccLinks) {
        this.checkAccLinks = checkAccLinks;
    }

    public void setDtOldStartDate(Date dtOldStartDate) {
        this.dtOldStartDate = dtOldStartDate;
    }

    public void setStrBrokerName(String strBrokerName) {
        this.strBrokerName = strBrokerName;
    }

    public void setStrOldTradeTypeCode(String strOldTradeTypeCode) {
        this.strOldTradeTypeCode = strOldTradeTypeCode;
    }

    public void setStrSubCatCode(String strSubCatCode) {
        this.strSubCatCode = strSubCatCode;
    }

    public void setStrBrokerCode(String strBrokerCode) {
        this.strBrokerCode = strBrokerCode;
    }

    public void setStrCatName(String strCatName) {
        this.strCatName = strCatName;
    }

    public void setStrExchangeName(String strExchangeName) {
        this.strExchangeName = strExchangeName;
    }

    public void setStrOldSubCatCode(String strOldSubCatCode) {
        this.strOldSubCatCode = strOldSubCatCode;
    }

    public void setDtStartDate(java.util.Date dtStartDate) {
        this.dtStartDate = dtStartDate;
    }

    public void setStrSecurityCode(String strSecurityCode) {
        this.strSecurityCode = strSecurityCode;
    }

    public void setDtOldStartDate(java.util.Date dtOldStartDate) {
        this.dtOldStartDate = dtOldStartDate;
    }

    /**
     * 新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
     * @param sAuxiCashAcctName String
     */
    public void setSAuxiCashAcctName(String sAuxiCashAcctName) {
        this.sAuxiCashAcctName = sAuxiCashAcctName;
    }

    /**
     * 新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
     * @param sAuxiCashAcctCode String
     */
    public void setSAuxiCashAcctCode(String sAuxiCashAcctCode) {
        this.sAuxiCashAcctCode = sAuxiCashAcctCode;
    }

    public void setCuryCode(String CuryCode) {
        this.CuryCode = CuryCode;
    }

    public void setCuryName(String CuryName) {
        this.CuryName = CuryName;
    }

    public String getStrAcctState() {
        return strAcctState;
    }

    public String getStrCashAcctCode() {
        return strCashAcctCode;
    }

    public String getStrOldInvMgrCode() {
        return strOldInvMgrCode;
    }

    public String getStrOldExchangeCode() {
        return strOldExchangeCode;
    }

    public java.util.Date getDtStartDate() {
        return dtStartDate;
    }

    public String getStrTradeTypeCode() {
        return strTradeTypeCode;
    }

    public String getStrPortCode() {
        return strPortCode;
    }

    public String getStrOldBrokerCode() {
        return strOldBrokerCode;
    }

    public String getStrSubCatName() {
        return strSubCatName;
    }

    public String getStrCashAcctName() {
        return strCashAcctName;
    }

    public String getStrInvMgrCode() {
        return strInvMgrCode;
    }

    public String getStrExchangeCode() {
        return strExchangeCode;
    }

    public String getStrOldPortCode() {
        return strOldPortCode;
    }

    public String getStrOldCashAcctCode() {
        return strOldCashAcctCode;
    }

    public String getStrTradeTypeName() {
        return strTradeTypeName;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public String getStrPortName() {
        return strPortName;
    }

    public CashAccLinkBean getFilterType() {
        return filterType;
    }

    public String getStrOldCatCode() {
        return strOldCatCode;
    }

    public String getStrCatCode() {
        return strCatCode;
    }

    public String getStrInvMgrName() {
        return strInvMgrName;
    }

    public String getCheckAccLinks() {
        return checkAccLinks;
    }

    public java.util.Date getDtOldStartDate() {
        return dtOldStartDate;
    }

    public String getStrBrokerName() {
        return strBrokerName;
    }

    public String getStrOldTradeTypeCode() {
        return strOldTradeTypeCode;
    }

    public String getStrSubCatCode() {
        return strSubCatCode;
    }

    public String getStrBrokerCode() {
        return strBrokerCode;
    }

    public String getStrCatName() {
        return strCatName;
    }

    public String getStrExchangeName() {
        return strExchangeName;
    }

    public String getStrOldSubCatCode() {
        return strOldSubCatCode;
    }

    public String getStrSecurityCode() {
        return strSecurityCode;
    }

    /**
     * 新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
     * @return String
     */
    public String getSAuxiCashAcctName() {
        return sAuxiCashAcctName;
    }

    /**
     * 新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
     * @return String
     */
    public String getSAuxiCashAcctCode() {
        return sAuxiCashAcctCode;
    }

    public String getCuryCode() {
        return CuryCode;
    }

    public String getCuryName() {
        return CuryName;
    }

    public CashAccLinkBean() {
    }

    /**
     * parseRowStr
     * 解析现金帐户链接设置请求
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
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
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.strCashAcctCode = reqAry[0];
            this.dtStartDate = YssFun.toDate(reqAry[1]);
            System.out.print(this.dtStartDate.toString());
            this.strCatCode = reqAry[2];
            this.strSubCatCode = reqAry[3];
            this.strInvMgrCode = reqAry[4];
            this.strPortCode = reqAry[5];
            this.strExchangeCode = reqAry[6];
            this.strBrokerCode = reqAry[7];
            this.strTradeTypeCode = reqAry[8];
            this.strDesc = reqAry[9];
            this.strAcctState = reqAry[10];
            this.checkStateId = Integer.parseInt(reqAry[11]);
            this.strOldCashAcctCode = reqAry[12];
            this.strOldCatCode = reqAry[13];
            this.strOldSubCatCode = reqAry[14];
            this.strOldInvMgrCode = reqAry[15];
            this.strOldPortCode = reqAry[16];
            this.strOldExchangeCode = reqAry[17];
            this.strOldBrokerCode = reqAry[18];
            this.strOldTradeTypeCode = reqAry[19];
            this.dtOldStartDate = YssFun.toDate(reqAry[20]);
            this.checkAccLinks = reqAry[21]; //如果是审核和反审核，则把所有的信息都放入了数组的22位
            this.CuryCode = reqAry[22];
            this.CuryName = reqAry[23];
            this.sAuxiCashAcctCode = reqAry[24]; //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
            /**shashijie 2012-3-21 BUG 4005 */
			this.oldFCuryCode = reqAry[26];
			/**end*/
            this.checkAccLinks = this.checkAccLinks.replaceAll("\f", "\t"); //为了便于使用通用的解析过程
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CashAccLinkBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析现金帐户链接设置请求出错", e);
        }
    }

    /**
     * buildRowStr
     * 获取数据字符串
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strCashAcctCode.trim());
        buf.append("\t");
        buf.append(this.strCashAcctName.trim());
        buf.append("\t");
        buf.append(YssFun.formatDate(this.dtStartDate, YssCons.YSS_DATEFORMAT));
        buf.append("\t");
        buf.append(this.strCatCode.trim());
        buf.append("\t");
        buf.append(this.strCatName.trim());
        buf.append("\t");
        buf.append(this.strSubCatCode.trim());
        buf.append("\t");
        buf.append(this.strSubCatName.trim());
        buf.append("\t");
        buf.append(this.strInvMgrCode.trim());
        buf.append("\t");
        buf.append(this.strInvMgrName.trim());
        buf.append("\t");
        buf.append(this.strPortCode.trim());
        buf.append("\t");
        buf.append(this.strPortName.trim());
        buf.append("\t");
        buf.append(this.strExchangeCode.trim());
        buf.append("\t");
        buf.append(this.strExchangeName.trim());
        buf.append("\t");
        buf.append(this.strBrokerCode.trim());
        buf.append("\t");
        buf.append(this.strBrokerName.trim());
        buf.append("\t");
        buf.append(this.strTradeTypeCode.trim());
        buf.append("\t");
        buf.append(this.strTradeTypeName.trim());
        buf.append("\t");
        buf.append(this.strDesc.trim());
        buf.append("\t");
        buf.append(this.strAcctState.trim());
        buf.append("\t");
        buf.append(this.CuryCode.trim()).append("\t");
        buf.append(this.CuryName.trim()).append("\t");
        buf.append(this.sAuxiCashAcctCode).append("\t"); //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
        buf.append(this.sAuxiCashAcctName).append("\t"); //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查现金帐户链接设置数据是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
    	/**shashijie 2012-3-21 BUG 4005 增加币种的判断 */
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_CashAccLink"),
                               "FINVMGRCODE,FPORTCODE,FCATCODE,FSUBCATCODE," +
                               "FBROKERCODE,FTRADETYPECODE,FEXCHANGECODE," +
                               "FCASHACCCODE,FSTARTDATE,FCuryCode",
                               (this.strInvMgrCode.length() == 0 ? " " :
                                this.strInvMgrCode) + "," +
                               (this.strPortCode.length() == 0 ? " " :
                                this.strPortCode) + "," +
                               (this.strCatCode.length() == 0 ? " " :
                                this.strCatCode) + "," +
                               (this.strSubCatCode.length() == 0 ? " " :
                                this.strSubCatCode) + "," +
                               (this.strBrokerCode.length() == 0 ? " " :
                                this.strBrokerCode) + "," +
                               (this.strTradeTypeCode.length() == 0 ? " " :
                                this.strTradeTypeCode) + "," +
                               (this.strExchangeCode.length() == 0 ? " " :
                                this.strExchangeCode) + "," +
                               this.strCashAcctCode + "," +
                               YssFun.formatDate(this.dtStartDate) + "," + 
                               this.CuryCode ,
                               (this.strOldInvMgrCode.length() == 0 ? " " :
                                this.strOldInvMgrCode) + "," +
                               (this.strOldPortCode.length() == 0 ? " " :
                                this.strOldPortCode) + "," +
                               (this.strOldCatCode.length() == 0 ? " " :
                                this.strOldCatCode) + "," +
                               (this.strOldSubCatCode.length() == 0 ? " " :
                                this.strOldSubCatCode) + "," +
                               (this.strOldBrokerCode.length() == 0 ? " " :
                                this.strOldBrokerCode) + "," +
                               (this.strOldTradeTypeCode.length() == 0 ? " " :
                                this.strOldTradeTypeCode) + "," +
                               (this.strOldExchangeCode.length() == 0 ? " " :
                                this.strOldExchangeCode) + "," +
                               this.strOldCashAcctCode + "," +
                               YssFun.formatDate(this.dtOldStartDate) + "," +
                               this.oldFCuryCode );
		/**end*/
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = " where 1=1 ";
        try {
            if (this.filterType != null) {
                if (this.filterType.strCashAcctCode.length() != 0) {
                    sResult = sResult + " and a.FCashAccCode like '" +
                        filterType.strCashAcctCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strCatCode.length() != 0) {
                    sResult = sResult + " and a.FCatCode like '" +
                        filterType.strCatCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strSubCatCode.length() != 0) {
                    sResult = sResult + " and a.FSubCatCode like '" +
                        filterType.strSubCatCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strInvMgrCode.length() != 0) {
                    sResult = sResult + " and a.FInvMgrCode like '" +
                        filterType.strInvMgrCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.dtStartDate != null &&
                    !this.filterType.dtStartDate.equals(YssFun.toDate("9998-12-31"))) {
                    sResult = sResult + " and a.FStartDate <= " +
                        dbl.sqlDate(filterType.dtStartDate);
                }
                if (this.filterType.strExchangeCode.length() != 0) {
                    sResult = sResult + " and a.FExchangeCode like '" +
                        filterType.strExchangeCode.replaceAll("'", "''") + "%'";
                }
                if (!this.filterType.strAcctState.equalsIgnoreCase("99") &&
                    this.filterType.strAcctState.length() != 0) {
                    sResult = sResult + " and a.FLinkLevel =" +
                        filterType.strAcctState;
                }

                if (this.filterType.strPortCode.length() != 0) {
                    sResult = sResult + " and a.FPortCode like '" +
                        filterType.strPortCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strBrokerCode.length() != 0) {
                    sResult = sResult + " and a.FBrokerCode like '" +
                        filterType.strBrokerCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strTradeTypeCode.length() != 0) {
                    sResult = sResult + " and a.FTradeTypeCode like '" +
                        filterType.strTradeTypeCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strDesc.length() != 0) {
                    sResult = sResult + " and a.FDesc like '" +
                        filterType.strDesc.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.CuryCode.length() != 0) {
                    sResult += " and a.FCuryCode like '" +
                        filterType.CuryCode.replaceAll("'", "''") + "%'";
                }
                //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                if (this.filterType.sAuxiCashAcctCode.length() != 0) {
                    sResult += " and a.FAuxiCashAccCode like '" +
                        filterType.sAuxiCashAcctCode.replaceAll("'", "''") + "%'";
                }
            }
        } catch (Exception e) {
            throw new YssException("筛选现金帐户链接设置数据出错", e);
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
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String sVocStr = "";
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setCashAccLinkAttr(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_CNT_LINKLEVEL);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取现金帐户链接信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     *获取现金帐户链接设置数据
     * 此方法已被修改
     *修改时间：2008年2月23号
     * 修改人：单亮
     * 原方法的功能：查询出现价账户连接数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     */

    public String getListViewData1() throws YssException {
        String strSql = "";
        /**shashijie 2012-3-21 BUG 4005 币种作为主键未加入判断 */
        strSql = "select y.* from " +
            "(select FInvMgrCode,FPortCode,FCatCode,FSubCatCode,FBrokerCode,FTradeTypeCode,FExchangeCode," +
            "FCashAccCode,FCheckState,max(FStartDate) as FStartDate,FCuryCode from " +
            pub.yssGetTableName("Tb_Para_CashAccLink") + " " +
            " where FStartDate <= " +
            dbl.sqlDate(new java.util.Date()) +
            //修改前的代码
//            " and FCheckState <> 2 group by FInvMgrCode,FPortCode,FCatCode,FSubCatCode,FBrokerCode," +
            //修改后的代码
            //begin
            " group by FInvMgrCode,FPortCode,FCatCode,FSubCatCode,FBrokerCode," +
            //end
            "FTradeTypeCode,FExchangeCode,FCashAccCode,FCheckState,FCuryCode) x join" +
            " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FCatName, e.FSubCatName," +
            " auxi.FCashAccName as FAuxiCashAccName," + //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
            "f.FInvMgrName, g.FCashAccName, h.FPortName, i.FExchangeName, j.FBrokerName, k.FTradeTypeName, m.FVocName as FLinkLevelValue,h.FCuryName as FCuryName" +
            " from " + pub.yssGetTableName("Tb_Para_CashAccLink") + "  a " +
            //--------------------------------------------------------------------------------------------------------------------------------
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            //--------------------------------------------------------------------------------------------------------------------------------
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            //--------------------------------------------------------------------------------------------------------------------------------
            " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d " +
            " on a.FCatCode = d.FCatCode" +
            //--------------------------------------------------------------------------------------------------------------------------------
            " left join (select FCuryCode,FCuryName from " +
            pub.yssGetTableName("Tb_Para_Currency") +
            " where FCheckState = 1) h on a.FCuryCode = h.FCuryCode" +
            //----------------------------------------------------------------------------//
            " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) e " +
            " on a.FSubCatCode = e.FSubCatCode" +
            //--------------------------------------------------------------------------------------------------------------------------------
         // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
    
            " left join (select FInvMgrCode, FInvMgrName from " +
            pub.yssGetTableName("Tb_Para_InvestManager") +
            " where  FCheckState = 1) f on a.FInvMgrCode = f.FInvMgrCode " +
            
            //end by lidaolong 
            //--------------------------------------------------------------------------------------------------------------------------------
            // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
  
            " left join (select FCashAccCode, FCashAccName from " +
            pub.yssGetTableName("Tb_Para_CashAccount") +
            " where  FCheckState = 1) g on a.FCashAccCode = g.FCashAccCode " +
           
            //end by lidaolong
            //--------------------------------------------------------------------------------------------------------------------------------
            // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
  
            
            " left join (select FPortCode, FPortName from " +
            pub.yssGetTableName("Tb_Para_Portfolio") + " where  FCheckState = 1 ) h on a.FPortCode = h.FPortCode " +
           
            
            //end by lidaolong
            //--------------------------------------------------------------------------------------------------------------------------------
            " left join (select FExchangeCode, FExchangeName from Tb_Base_Exchange where FCheckState = 1) i on a.FExchangeCode = i.FExchangeCode" +
            //--------------------------------------------------------------------------------------------------------------------------------
         // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码

            " left join (select FBrokerCode, FBrokerName from " +
            pub.yssGetTableName("Tb_Para_Broker") + " where  FCheckState = 1 ) j on a.FBrokerCode = j.FBrokerCode " +
          
            
            //end by lidaolong
            //--------------------------------------------------------------------------------------------------------------------------------
            //新增FAuxiCashAccCode的关联表字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
            " left join " + pub.yssGetTableName("Tb_Para_CashAccount") + " auxi on a.FAuxiCashAccCode =auxi.FCashAccCode " +
            //QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
            " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) k on a.FTradeTypeCode = k.FTradeTypeCode" +
            //--------------------------------------------------------------------------------------------------------------------------------
            //2007.11.29 修改 蒋锦 使用dbl.sqlToChar()处理"a.FLinkLevel"，否则在使用DB2数据库时会报数据类型错误
            " left join Tb_Fun_Vocabulary m on " + dbl.sqlToChar("a.FLinkLevel") +
            " = m.FVocCode and m.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_CNT_LINKLEVEL) +
            //--------------------------------------------------------------------------------------------------------------------------------
            buildFilterSql() +
            ") y on x.FInvMgrCode = y.FInvMgrCode and x.FPortCode = y.FPortCode " +
            " and x.FCatCode = y.FCatCode and x.FSubCatCode = y.FSubCatCode " +
            " and x.FBrokerCode = y.FBrokerCode and x.FTradeTypeCode = y.FTradeTypeCode" +
            " and x.FExchangeCode = y.FExchangeCode and x.FCashAccCode = y.FCashAccCode " +
            " and x.FStartDate = y.FStartDate" +
            " and x.FCuryCode = y.FCuryCode "+
            " order by y.FCheckState, y.FCheckTime desc,y.FCreateTime desc";
		/**end*/
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     * 获取现金帐户链接设置的全部数据
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";
        /**shashijie 2012-3-21 BUG 4005 币种作为主键未加入判断 */
        strSql = "select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FCatName, e.FSubCatName,n.FCuryName as FCuryName, " +
            " auxi.FCashAccName as FAuxiCashAccName," + //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
            "f.FInvMgrName, g.FCashAccName, h.FPortName, i.FExchangeName, j.FBrokerName, k.FTradeTypeName, m.FVocName as FLinkLevelValue " +
            " from " + pub.yssGetTableName("Tb_Para_CashAccLink") + "  a " +
            " left join (select FCuryCode,FCuryName from " +
            pub.yssGetTableName("Tb_Para_Currency") +
            " where FCheckState = 1) n on a.FCuryCode = n.FCuryCode" +
            //--------------------------------------------------------------------------------------------------------------------------------
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            //--------------------------------------------------------------------------------------------------------------------------------
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            //--------------------------------------------------------------------------------------------------------------------------------
            " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d " +
            " on a.FCatCode = d.FCatCode" +
            //--------------------------------------------------------------------------------------------------------------------------------
            " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) e " +
            " on a.FSubCatCode = e.FSubCatCode" +
            //--------------------------------------------------------------------------------------------------------------------------------
            // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
    
            " left join (select FInvMgrCode, FInvMgrName from " +
            pub.yssGetTableName("Tb_Para_InvestManager") +
            " where  FCheckState = 1 ) f on a.FInvMgrCode = f.FInvMgrCode " +
         
            //end by lidaolong
            //--------------------------------------------------------------------------------------------------------------------------------
            // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
   
            " left join (select FCashAccCode, FCashAccName from " +
            pub.yssGetTableName("Tb_Para_CashAccount") +
            " where  FCheckState = 1) g on a.FCashAccCode = g.FCashAccCode " +
          
            //end by lidaolong
            //--------------------------------------------------------------------------------------------------------------------------------
            // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
     
            " left join (select FPortCode, FPortName from " +
            pub.yssGetTableName("Tb_Para_Portfolio") + " where  FCheckState = 1) h on a.FPortCode = h.FPortCode " +
         
            //end by lidaolong
            //--------------------------------------------------------------------------------------------------------------------------------
            // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
           /* " left join (select FExchangeCode, FExchangeName from Tb_Base_Exchange where FCheckState = 1) i on a.FExchangeCode = i.FExchangeCode" +
            //--------------------------------------------------------------------------------------------------------------------------------
                    
            " left join (select jb.* from (select FBrokerCode, max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Broker") + " where FStartDate <= " +
            dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 group by FBrokerCode) ja join (select FBrokerCode, FBrokerName, FStartDate from " +
            pub.yssGetTableName("Tb_Para_Broker") +
            ") jb on ja.FBrokerCode = jb.FBrokerCode and ja.FStartDate = jb.FStartDate) j on a.FBrokerCode = j.FBrokerCode " +
          */
            
            " left join (select FExchangeCode, FExchangeName from Tb_Base_Exchange where FCheckState = 1) i on a.FExchangeCode = i.FExchangeCode" +
            //--------------------------------------------------------------------------------------------------------------------------------
                    
            " left join (select FBrokerCode, FBrokerName from " +
            pub.yssGetTableName("Tb_Para_Broker") + " where  FCheckState = 1) j on a.FBrokerCode = j.FBrokerCode " +
          
            
            //end by lidaolong
            //--------------------------------------------------------------------------------------------------------------------------------
            //新增FAuxiCashAccCode的关联表字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
            " left join " + pub.yssGetTableName("Tb_Para_CashAccount") + " auxi on a.FAuxiCashAccCode =auxi.FCashAccCode " +
            //QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
            " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) k on a.FTradeTypeCode = k.FTradeTypeCode" +
            //--------------------------------------------------------------------------------------------------------------------------------
            " left join  Tb_Fun_Vocabulary  m on " + dbl.sqlToChar("a.FLinkLevel") + " = m.FVocCode and m.FVocTypeCode = " + // lzp modify
            dbl.sqlString(YssCons.YSS_CNT_LINKLEVEL) +
            //--------------------------------------------------------------------------------------------------------------------------------
            buildFilterSql() +
            " order by a.FCheckState, a.FCreateTime desc";
        /**end*/
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData2
     * 获取已审核的现金帐户链接设置数据
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
            sHeader = "帐户代码\t帐户名称\t启用日期";
            strSql = "select y.* from " +
                "(select FInvMgrCode,FPortCode,FCatCode,FSubCatCode,FBrokerCode,FTradeTypeCode,FExchangeCode," +
                "FCashAccCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccLink") + " " +
                " where FStartDate <= " +
                dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FInvMgrCode,FPortCode,FCatCode,FSubCatCode,FBrokerCode," +
                "FTradeTypeCode,FExchangeCode,FCashAccCode) x join" +
                " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FCatName, e.FSubCatName," +
                " auxi.FCashAccName as FAuxiCashAccName, " + //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                "f.FInvMgrName, g.FCashAccName, h.FPortName, i.FExchangeName, j.FBrokerName, k.FTradeTypeName " +
                " from " + pub.yssGetTableName("Tb_Para_CashAccLink") + "  a " +
                //--------------------------------------------------------------------------------------------------------------------------------
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                //--------------------------------------------------------------------------------------------------------------------------------
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //--------------------------------------------------------------------------------------------------------------------------------
                " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d " +
                " on a.FCatCode = d.FCatCode" +
                //--------------------------------------------------------------------------------------------------------------------------------
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) e " +
                " on a.FSubCatCode = e.FSubCatCode" +
                //--------------------------------------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
    
                " left join (select FInvMgrCode, FInvMgrName from " +
                pub.yssGetTableName("Tb_Para_InvestManager") +
                " where  FCheckState = 1) f on a.FInvMgrCode = f.FInvMgrCode " +
           
                //end by lidaolong
                //--------------------------------------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
           
                " left join (select FCashAccCode, FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where  FCheckState = 1) g on a.FCashAccCode = g.FCashAccCode " +
              
                
                //end by lidaolong
                //--------------------------------------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
          
                " left join (select FPortCode, FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1 ) h on a.FPortCode = h.FPortCode " +
               
                //end by lidaolong
                //--------------------------------------------------------------------------------------------------------------------------------
                " left join (select FExchangeCode, FExchangeName from Tb_Base_Exchange where FCheckState = 1) i on a.FExchangeCode = i.FExchangeCode" +
                //--------------------------------------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
            
                " left join (select FBrokerCode, FBrokerName from " +
                pub.yssGetTableName("Tb_Para_Broker") + " where FCheckState = 1 ) j on a.FBrokerCode = j.FBrokerCode " +
            
                
                //end by lidaolong
                //--------------------------------------------------------------------------------------------------------------------------------
                //添加FAuxiCashAccCode的关联表 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                " left join " + pub.yssGetTableName("Tb_para_CashAccount") + " auxi on a.FAuxiCashAccCode =auxi.FCashAccCode " +
                //QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) k on a.FTradeTypeCode = k.FTradeTypeCode" +
                //--------------------------------------------------------------------------------------------------------------------------------
                buildFilterSql() +
                ") y on x.FInvMgrCode = y.FInvMgrCode and x.FPortCode = y.FPortCode " +
                " and x.FCatCode = y.FCatCode and x.FSubCatCode = y.FSubCatCode " +
                " and x.FBrokerCode = y.FBrokerCode and x.FTradeTypeCode = y.FTradeTypeCode" +
                " and x.FExchangeCode = y.FExchangeCode and x.FCashAccCode = y.FCashAccCode " +
                " and x.FStartDate = y.FStartDate" +
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FCashAccCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FCashAccName") + "").trim()).append(
                    "\t");
                bufShow.append( (YssFun.formatDate(rs.getDate("FStartDate"),
                    YssCons.YSS_DATEFORMAT) + "").
                               trim()).append(YssCons.YSS_LINESPLITMARK);

                setCashAccLinkAttr(rs);
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
            throw new YssException("获取现金帐户链接设置数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() {
        return "";
    }

    /**
     * getSetting
     *
     * @return IParaSetting
     */
    public IDataSetting getSetting() {
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
     * 更新证券信息
     * @param btOper byte
     */
    /* public void saveSetting(byte btOper) throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        String sErr = "";
        try {
           if (btOper == YssCons.OP_ADD) {
     strSql = "insert into " + pub.yssGetTableName("Tb_Para_CashAccLink") +
                    "" +
     "(FCashAccCode, FStartDate, FCatCode, FSubCatCode, FInvMgrCode, " +
     " FPortCode, FExchangeCode, FTradeTypeCode, FBrokerCode,FLinkLevel, FDesc," +
                    "FCheckState,FCreator,FCreateTime,FCheckUser) values(" +
                    dbl.sqlString(this.strCashAcctCode) + "," +
                    dbl.sqlDate(this.dtStartDate) + "," +
                    dbl.sqlString(this.strCatCode.length() == 0 ? " " :
                                  this.strCatCode) + "," +
                    dbl.sqlString(this.strSubCatCode.length() == 0 ? " " :
                                  this.strSubCatCode) + "," +
                    dbl.sqlString(this.strInvMgrCode.length() == 0 ? " " :
                                  this.strInvMgrCode) + "," +
                    dbl.sqlString(this.strPortCode.length() == 0 ? " " :
                                  this.strPortCode) + "," +
                    dbl.sqlString(this.strExchangeCode.length() == 0 ? " " :
                                  this.strExchangeCode) + "," +
                    dbl.sqlString(this.strTradeTypeCode.length() == 0 ? " " :
                                  this.strTradeTypeCode) + "," +
                    dbl.sqlString(this.strBrokerCode.length() == 0 ? " " :
                                  this.strBrokerCode) + "," +
                    dbl.sqlString(this.strAcctState.length() == 0 ? " " :
                                  this.strAcctState) + "," +
                    dbl.sqlString(this.strDesc) + "," +
                    (pub.getSysCheckState() ? "0" : "1") + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) + ")";
           }
           else if (btOper == YssCons.OP_EDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_CashAccLink") +
                    " set " +
                    "  FCashAccCode = " + dbl.sqlString(this.strCashAcctCode) +
                    ", FStartDate = " + dbl.sqlDate(this.dtStartDate) +
                    ", FCatCode = " +
                    dbl.sqlString(this.strCatCode.length() == 0 ? " " :
                                  this.strCatCode) +
                    ", FSubCatCode = " +
                    dbl.sqlString(this.strSubCatCode.length() == 0 ? " " :
                                  this.strSubCatCode) +
                    ", FInvMgrCode = " +
                    dbl.sqlString(this.strInvMgrCode.length() == 0 ? " " :
                                  this.strInvMgrCode) +
                    ", FPortCode = " +
                    dbl.sqlString(this.strPortCode.length() == 0 ? " " :
                                  this.strPortCode) +
                    ", FExchangeCode = " +
                    dbl.sqlString(this.strExchangeCode.length() == 0 ? " " :
                                  this.strExchangeCode) +
                    ", FTradeTypeCode = " +
                    dbl.sqlString(this.strTradeTypeCode.length() == 0 ? " " :
                                  this.strTradeTypeCode) +
                    ", FBrokerCode = " +
                    dbl.sqlString(this.strBrokerCode.length() == 0 ? " " :
                                  this.strBrokerCode) +
     ", FLinkLevel = " + dbl.sqlString(this.strAcctState.length() == 0 ?" " :
                                                      this.strAcctState) +
                    ", FDesc = " + dbl.sqlString(this.strDesc) +
                    ", FCheckState = " + (pub.getSysCheckState() ? "0" : "1") +
                    ", FCreator = " + dbl.sqlString(this.creatorCode) +
                    ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                    ", FCheckUser = " +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) +
                    " where FCashAccCode = " +
                    dbl.sqlString(this.strOldCashAcctCode) +
                    " and FCatCode = " +
                    dbl.sqlString(this.strOldCatCode.length() == 0 ? " " :
                                  this.strOldCatCode) +
                    " and FSubCatCode = " +
                    dbl.sqlString(this.strOldSubCatCode.length() == 0 ? " " :
                                  this.strOldSubCatCode) +
                    " and FInvMgrCode = " +
                    dbl.sqlString(this.strOldInvMgrCode.length() == 0 ? " " :
                                  this.strOldInvMgrCode) +
                    " and FPortCode = " +
                    dbl.sqlString(this.strOldPortCode.length() == 0 ? " " :
                                  this.strOldPortCode) +
                    " and FExchangeCode = " +
                    dbl.sqlString(this.strOldExchangeCode.length() == 0 ? " " :
                                  this.strOldExchangeCode) +
                    " and FTradeTypeCode = " +
                    dbl.sqlString(this.strOldTradeTypeCode.length() == 0 ? " " :
                                  this.strOldTradeTypeCode) +
                    " and FBrokerCode = " +
                    dbl.sqlString(this.strOldBrokerCode.length() == 0 ? " " :
                                  this.strOldBrokerCode) +
                    " and FStartDate = " + dbl.sqlDate(this.dtOldStartDate);
           }
           else if (btOper == YssCons.OP_DEL) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_CashAccLink") +
                    " set FCheckState = " + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "'" +
                    " where FCashAccCode = " +
                    dbl.sqlString(this.strOldCashAcctCode) +
                    " and FCatCode = " +
                    dbl.sqlString(this.strOldCatCode.length() == 0 ? " " :
                                  this.strOldCatCode) +
                    " and FSubCatCode = " +
                    dbl.sqlString(this.strOldSubCatCode.length() == 0 ? " " :
                                  this.strOldSubCatCode) +
                    " and FInvMgrCode = " +
                    dbl.sqlString(this.strOldInvMgrCode.length() == 0 ? " " :
                                  this.strOldInvMgrCode) +
                    " and FPortCode = " +
                    dbl.sqlString(this.strOldPortCode.length() == 0 ? " " :
                                  this.strOldPortCode) +
                    " and FExchangeCode = " +
                    dbl.sqlString(this.strOldExchangeCode.length() == 0 ? " " :
                                  this.strOldExchangeCode) +
                    " and FTradeTypeCode = " +
                    dbl.sqlString(this.strOldTradeTypeCode.length() == 0 ? " " :
                                  this.strOldTradeTypeCode) +
                    " and FBrokerCode = " +
                    dbl.sqlString(this.strOldBrokerCode.length() == 0 ? " " :
                                  this.strOldBrokerCode) +
                    " and FStartDate = " + dbl.sqlDate(this.dtOldStartDate);
           }
           else if (btOper == YssCons.OP_AUDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_CashAccLink") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = '" + pub.getUserCode() +
                    "', FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FCashAccCode = " +
                    dbl.sqlString(this.strOldCashAcctCode) +
                    " and FCatCode = " +
                    dbl.sqlString(this.strOldCatCode.length() == 0 ? " " :
                                  this.strOldCatCode) +
                    " and FSubCatCode = " +
                    dbl.sqlString(this.strOldSubCatCode.length() == 0 ? " " :
                                  this.strOldSubCatCode) +
                    " and FInvMgrCode = " +
                    dbl.sqlString(this.strOldInvMgrCode.length() == 0 ? " " :
                                  this.strOldInvMgrCode) +
                    " and FPortCode = " +
                    dbl.sqlString(this.strOldPortCode.length() == 0 ? " " :
                                  this.strOldPortCode) +
                    " and FExchangeCode = " +
                    dbl.sqlString(this.strOldExchangeCode.length() == 0 ? " " :
                                  this.strOldExchangeCode) +
                    " and FTradeTypeCode = " +
                    dbl.sqlString(this.strOldTradeTypeCode.length() == 0 ? " " :
                                  this.strOldTradeTypeCode) +
                    " and FBrokerCode = " +
                    dbl.sqlString(this.strOldBrokerCode.length() == 0 ? " " :
                                  this.strOldBrokerCode) +
                    " and FStartDate = " + dbl.sqlDate(this.dtOldStartDate);
           }
           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("设置现金帐户链接数据出错", e);
        }
        finally {
           dbl.endTransFinal(conn, bTrans);
        }
     }
     */





    /**
     * saveSetting
     * 更新证券信息
     * @param btOper byte
     */
    /* public void saveSetting(byte btOper) throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        String sErr = "";
        try {
           if (btOper == YssCons.OP_ADD) {
     strSql = "insert into " + pub.yssGetTableName("Tb_Para_CashAccLink") +
                    "" +
     "(FCashAccCode, FStartDate, FCatCode, FSubCatCode, FInvMgrCode, " +
     " FPortCode, FExchangeCode, FTradeTypeCode, FBrokerCode,FLinkLevel, FDesc," +
                    "FCheckState,FCreator,FCreateTime,FCheckUser) values(" +
                    dbl.sqlString(this.strCashAcctCode) + "," +
                    dbl.sqlDate(this.dtStartDate) + "," +
                    dbl.sqlString(this.strCatCode.length() == 0 ? " " :
                                  this.strCatCode) + "," +
                    dbl.sqlString(this.strSubCatCode.length() == 0 ? " " :
                                  this.strSubCatCode) + "," +
                    dbl.sqlString(this.strInvMgrCode.length() == 0 ? " " :
                                  this.strInvMgrCode) + "," +
                    dbl.sqlString(this.strPortCode.length() == 0 ? " " :
                                  this.strPortCode) + "," +
                    dbl.sqlString(this.strExchangeCode.length() == 0 ? " " :
                                  this.strExchangeCode) + "," +
                    dbl.sqlString(this.strTradeTypeCode.length() == 0 ? " " :
                                  this.strTradeTypeCode) + "," +
                    dbl.sqlString(this.strBrokerCode.length() == 0 ? " " :
                                  this.strBrokerCode) + "," +
                    dbl.sqlString(this.strAcctState.length() == 0 ? " " :
                                  this.strAcctState) + "," +
                    dbl.sqlString(this.strDesc) + "," +
                    (pub.getSysCheckState() ? "0" : "1") + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) + ")";
           }
           else if (btOper == YssCons.OP_EDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_CashAccLink") +
                    " set " +
                    "  FCashAccCode = " + dbl.sqlString(this.strCashAcctCode) +
                    ", FStartDate = " + dbl.sqlDate(this.dtStartDate) +
                    ", FCatCode = " +
                    dbl.sqlString(this.strCatCode.length() == 0 ? " " :
                                  this.strCatCode) +
                    ", FSubCatCode = " +
                    dbl.sqlString(this.strSubCatCode.length() == 0 ? " " :
                                  this.strSubCatCode) +
                    ", FInvMgrCode = " +
                    dbl.sqlString(this.strInvMgrCode.length() == 0 ? " " :
                                  this.strInvMgrCode) +
                    ", FPortCode = " +
                    dbl.sqlString(this.strPortCode.length() == 0 ? " " :
                                  this.strPortCode) +
                    ", FExchangeCode = " +
                    dbl.sqlString(this.strExchangeCode.length() == 0 ? " " :
                                  this.strExchangeCode) +
                    ", FTradeTypeCode = " +
                    dbl.sqlString(this.strTradeTypeCode.length() == 0 ? " " :
                                  this.strTradeTypeCode) +
                    ", FBrokerCode = " +
                    dbl.sqlString(this.strBrokerCode.length() == 0 ? " " :
                                  this.strBrokerCode) +
     ", FLinkLevel = " + dbl.sqlString(this.strAcctState.length() == 0 ?" " :
                                                      this.strAcctState) +
                    ", FDesc = " + dbl.sqlString(this.strDesc) +
                    ", FCheckState = " + (pub.getSysCheckState() ? "0" : "1") +
                    ", FCreator = " + dbl.sqlString(this.creatorCode) +
                    ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                    ", FCheckUser = " +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) +
                    " where FCashAccCode = " +
                    dbl.sqlString(this.strOldCashAcctCode) +
                    " and FCatCode = " +
                    dbl.sqlString(this.strOldCatCode.length() == 0 ? " " :
                                  this.strOldCatCode) +
                    " and FSubCatCode = " +
                    dbl.sqlString(this.strOldSubCatCode.length() == 0 ? " " :
                                  this.strOldSubCatCode) +
                    " and FInvMgrCode = " +
                    dbl.sqlString(this.strOldInvMgrCode.length() == 0 ? " " :
                                  this.strOldInvMgrCode) +
                    " and FPortCode = " +
                    dbl.sqlString(this.strOldPortCode.length() == 0 ? " " :
                                  this.strOldPortCode) +
                    " and FExchangeCode = " +
                    dbl.sqlString(this.strOldExchangeCode.length() == 0 ? " " :
                                  this.strOldExchangeCode) +
                    " and FTradeTypeCode = " +
                    dbl.sqlString(this.strOldTradeTypeCode.length() == 0 ? " " :
                                  this.strOldTradeTypeCode) +
                    " and FBrokerCode = " +
                    dbl.sqlString(this.strOldBrokerCode.length() == 0 ? " " :
                                  this.strOldBrokerCode) +
                    " and FStartDate = " + dbl.sqlDate(this.dtOldStartDate);
           }
           else if (btOper == YssCons.OP_DEL) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_CashAccLink") +
                    " set FCheckState = " + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "'" +
                    " where FCashAccCode = " +
                    dbl.sqlString(this.strOldCashAcctCode) +
                    " and FCatCode = " +
                    dbl.sqlString(this.strOldCatCode.length() == 0 ? " " :
                                  this.strOldCatCode) +
                    " and FSubCatCode = " +
                    dbl.sqlString(this.strOldSubCatCode.length() == 0 ? " " :
                                  this.strOldSubCatCode) +
                    " and FInvMgrCode = " +
                    dbl.sqlString(this.strOldInvMgrCode.length() == 0 ? " " :
                                  this.strOldInvMgrCode) +
                    " and FPortCode = " +
                    dbl.sqlString(this.strOldPortCode.length() == 0 ? " " :
                                  this.strOldPortCode) +
                    " and FExchangeCode = " +
                    dbl.sqlString(this.strOldExchangeCode.length() == 0 ? " " :
                                  this.strOldExchangeCode) +
                    " and FTradeTypeCode = " +
                    dbl.sqlString(this.strOldTradeTypeCode.length() == 0 ? " " :
                                  this.strOldTradeTypeCode) +
                    " and FBrokerCode = " +
                    dbl.sqlString(this.strOldBrokerCode.length() == 0 ? " " :
                                  this.strOldBrokerCode) +
                    " and FStartDate = " + dbl.sqlDate(this.dtOldStartDate);
           }
           else if (btOper == YssCons.OP_AUDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_CashAccLink") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = '" + pub.getUserCode() +
                    "', FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FCashAccCode = " +
                    dbl.sqlString(this.strOldCashAcctCode) +
                    " and FCatCode = " +
                    dbl.sqlString(this.strOldCatCode.length() == 0 ? " " :
                                  this.strOldCatCode) +
                    " and FSubCatCode = " +
                    dbl.sqlString(this.strOldSubCatCode.length() == 0 ? " " :
                                  this.strOldSubCatCode) +
                    " and FInvMgrCode = " +
                    dbl.sqlString(this.strOldInvMgrCode.length() == 0 ? " " :
                                  this.strOldInvMgrCode) +
                    " and FPortCode = " +
                    dbl.sqlString(this.strOldPortCode.length() == 0 ? " " :
                                  this.strOldPortCode) +
                    " and FExchangeCode = " +
                    dbl.sqlString(this.strOldExchangeCode.length() == 0 ? " " :
                                  this.strOldExchangeCode) +
                    " and FTradeTypeCode = " +
                    dbl.sqlString(this.strOldTradeTypeCode.length() == 0 ? " " :
                                  this.strOldTradeTypeCode) +
                    " and FBrokerCode = " +
                    dbl.sqlString(this.strOldBrokerCode.length() == 0 ? " " :
                                  this.strOldBrokerCode) +
                    " and FStartDate = " + dbl.sqlDate(this.dtOldStartDate);
           }
           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("设置现金帐户链接数据出错", e);
        }
        finally {
           dbl.endTransFinal(conn, bTrans);
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
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_CashAccLink") +
                "" +
                "(FCashAccCode, FStartDate, FCatCode, FSubCatCode, FInvMgrCode, " +
                " FPortCode, FExchangeCode, FTradeTypeCode, FBrokerCode,FLinkLevel, FDesc," +
                " FAuxiCashAccCode," + //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                "FCheckState,FCreator,FCreateTime,FCheckUser,FCuryCode) values(" +
                dbl.sqlString(this.strCashAcctCode) + "," +
                dbl.sqlDate(this.dtStartDate) + "," +
                dbl.sqlString(this.strCatCode.length() == 0 ? " " :
                              this.strCatCode) + "," +
                dbl.sqlString(this.strSubCatCode.length() == 0 ? " " :
                              this.strSubCatCode) + "," +
                dbl.sqlString(this.strInvMgrCode.length() == 0 ? " " :
                              this.strInvMgrCode) + "," +
                dbl.sqlString(this.strPortCode.length() == 0 ? " " :
                              this.strPortCode) + "," +
                dbl.sqlString(this.strExchangeCode.length() == 0 ? " " :
                              this.strExchangeCode) + "," +
                dbl.sqlString(this.strTradeTypeCode.length() == 0 ? " " :
                              this.strTradeTypeCode) + "," +
                dbl.sqlString(this.strBrokerCode.length() == 0 ? " " :
                              this.strBrokerCode) + "," +
                (this.strAcctState.length() == 0 ? " " :
                 this.strAcctState) + "," +
                dbl.sqlString(this.strDesc) + "," +
                dbl.sqlString(this.sAuxiCashAcctCode) + "," + //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + "," +
                dbl.sqlString(this.CuryCode.length() == 0 ? " " : CuryCode) +
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加现金帐户链接信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_CashAccLink") +
                " set " +
                "  FCashAccCode = " + dbl.sqlString(this.strCashAcctCode) +
                ", FStartDate = " + dbl.sqlDate(this.dtStartDate) +
                ", FCatCode = " +
                dbl.sqlString(this.strCatCode.length() == 0 ? " " :
                              this.strCatCode) +
                ", FSubCatCode = " +
                dbl.sqlString(this.strSubCatCode.length() == 0 ? " " :
                              this.strSubCatCode) +
                ", FInvMgrCode = " +
                dbl.sqlString(this.strInvMgrCode.length() == 0 ? " " :
                              this.strInvMgrCode) +
                ", FPortCode = " +
                dbl.sqlString(this.strPortCode.length() == 0 ? " " :
                              this.strPortCode) +
                ", FExchangeCode = " +
                dbl.sqlString(this.strExchangeCode.length() == 0 ? " " :
                              this.strExchangeCode) +
                ", FTradeTypeCode = " +
                dbl.sqlString(this.strTradeTypeCode.length() == 0 ? " " :
                              this.strTradeTypeCode) +
                ", FAuxiCashAccCode =" + dbl.sqlString(this.sAuxiCashAcctCode) + //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                ", FCuryCode = " +
                dbl.sqlString(this.CuryCode.length() == 0 ? " " : CuryCode) +
                ", FBrokerCode = " +
                dbl.sqlString(this.strBrokerCode.length() == 0 ? " " :
                              this.strBrokerCode) +
                ", FLinkLevel = " + (this.strAcctState.length() == 0 ? " " :
                                     this.strAcctState) +
                ", FDesc = " + dbl.sqlString(this.strDesc) +
                ", FCheckState = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCreator = " + dbl.sqlString(this.creatorCode) +
                ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ", FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FCashAccCode = " +
                dbl.sqlString(this.strOldCashAcctCode) +
                " and FCatCode = " +
                dbl.sqlString(this.strOldCatCode.length() == 0 ? " " :
                              this.strOldCatCode) +
                " and FSubCatCode = " +
                dbl.sqlString(this.strOldSubCatCode.length() == 0 ? " " :
                              this.strOldSubCatCode) +
                " and FInvMgrCode = " +
                dbl.sqlString(this.strOldInvMgrCode.length() == 0 ? " " :
                              this.strOldInvMgrCode) +
                " and FPortCode = " +
                dbl.sqlString(this.strOldPortCode.length() == 0 ? " " :
                              this.strOldPortCode) +
                " and FExchangeCode = " +
                dbl.sqlString(this.strOldExchangeCode.length() == 0 ? " " :
                              this.strOldExchangeCode) +
                " and FTradeTypeCode = " +
                dbl.sqlString(this.strOldTradeTypeCode.length() == 0 ? " " :
                              this.strOldTradeTypeCode) +
                " and FBrokerCode = " +
                dbl.sqlString(this.strOldBrokerCode.length() == 0 ? " " :
                              this.strOldBrokerCode) +
                " and FStartDate = " + dbl.sqlDate(this.dtOldStartDate)+
                /**shashijie 2012-3-21 BUG 4005*/
				" and FCuryCode = " + dbl.sqlString(this.oldFCuryCode.length()==0 ? " " : this.oldFCuryCode)
				/**end*/
                ;
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改现金链接帐户信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_CashAccLink") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FCashAccCode = " +
                dbl.sqlString(this.strOldCashAcctCode) +
                " and FCatCode = " +
                dbl.sqlString(this.strOldCatCode.length() == 0 ? " " :
                              this.strOldCatCode) +
                " and FSubCatCode = " +
                dbl.sqlString(this.strOldSubCatCode.length() == 0 ? " " :
                              this.strOldSubCatCode) +
                " and FInvMgrCode = " +
                dbl.sqlString(this.strOldInvMgrCode.length() == 0 ? " " :
                              this.strOldInvMgrCode) +
                " and FPortCode = " +
                dbl.sqlString(this.strOldPortCode.length() == 0 ? " " :
                              this.strOldPortCode) +
                " and FExchangeCode = " +
                dbl.sqlString(this.strOldExchangeCode.length() == 0 ? " " :
                              this.strOldExchangeCode) +
                " and FTradeTypeCode = " +
                dbl.sqlString(this.strOldTradeTypeCode.length() == 0 ? " " :
                              this.strOldTradeTypeCode) +
                " and FBrokerCode = " +
                dbl.sqlString(this.strOldBrokerCode.length() == 0 ? " " :
                              this.strOldBrokerCode) +
                " and FStartDate = " + dbl.sqlDate(this.dtOldStartDate)+
                /**shashijie 2012-3-21 BUG 4005*/
				" and FCuryCode = " + dbl.sqlString(this.oldFCuryCode.length()==0 ? " " : this.oldFCuryCode)
				/**end*/
                ;
            conn.setAutoCommit(false);
            bTrans = true;
            System.out.print(strSql);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除现金帐户链接信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**修改时间：2008年3月20号
     *  修改人：单亮
     *  原方法功能：只能处理现金账户连接的审核和未审核的单条信息。
     *  新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     */

    public void checkSetting() throws YssException {
//      String strSql = "";
//      boolean bTrans = false; //代表是否开始了事务
//      Connection conn = dbl.loadConnection();
//      CashAccLinkBean cal = new CashAccLinkBean();
//      String[] sChkAccLinkAry = null;
//      try {
//         conn.setAutoCommit(false);
//         bTrans = true;
//         cal.setYssPub(pub);
//         sChkAccLinkAry = this.checkAccLinks.split("\r\n");
//         for (int i = 0; i < sChkAccLinkAry.length; i++) {
//            cal.parseRowStr(sChkAccLinkAry[i]);
//            strSql = "update " + pub.yssGetTableName("Tb_Para_CashAccLink") +
//                  " set FCheckState = " +
//                  cal.checkStateId + ", FCheckUser = '" + pub.getUserCode() +
//                  "', FCheckTime = '" +
//                  YssFun.formatDatetime(new java.util.Date()) + "'" +
//                  " where FCashAccCode = " +
//                  dbl.sqlString(cal.strCashAcctCode) +
//                  " and FCatCode = " +
//                  dbl.sqlString(cal.strCatCode.length() == 0 ? " " :
//                                cal.strCatCode) +
//                  " and FSubCatCode = " +
//                  dbl.sqlString(cal.strSubCatCode.length() == 0 ? " " :
//                                cal.strSubCatCode) +
//                  " and FInvMgrCode = " +
//                  dbl.sqlString(cal.strInvMgrCode.length() == 0 ? " " :
//                                cal.strInvMgrCode) +
//                  " and FPortCode = " +
//                  dbl.sqlString(cal.strPortCode.length() == 0 ? " " :
//                                cal.strPortCode) +
//                  " and FExchangeCode = " +
//                  dbl.sqlString(cal.strExchangeCode.length() == 0 ? " " :
//                                cal.strExchangeCode) +
//                  " and FTradeTypeCode = " +
//                  dbl.sqlString(cal.strTradeTypeCode.length() == 0 ? " " :
//                                cal.strTradeTypeCode) +
//                  " and FBrokerCode = " +
//                  dbl.sqlString(cal.strBrokerCode.length() == 0 ? " " :
//                                cal.strBrokerCode) +
//                  " and FStartDate = " + dbl.sqlDate(cal.dtStartDate) +
//                  " and FCuryCode = " +
//                  dbl.sqlString(cal.CuryCode.length() == 0 ? " " : cal.CuryCode);
//            System.out.print(strSql);
//            dbl.executeSql(strSql);
//         }
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//      }
//
//      catch (Exception e) {
//         throw new YssException("审核现金帐户链接信息出错", e);
//      }
//      finally {
//         dbl.endTransFinal(conn, bTrans);
//      }
//修改后的代码
        //---------------------------begin
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        CashAccLinkBean cal = new CashAccLinkBean();
        String[] sChkAccLinkAry = null;

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            cal.setYssPub(pub);
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (! (checkAccLinks == null || checkAccLinks.equalsIgnoreCase(""))) {
                sChkAccLinkAry = this.checkAccLinks.split("\r\n");
                for (int i = 0; i < sChkAccLinkAry.length; i++) {
                    cal.parseRowStr(sChkAccLinkAry[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Para_CashAccLink") +
                        " set FCheckState = " +
                        cal.checkStateId + ", FCheckUser = '" + pub.getUserCode() +
                        "', FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FCashAccCode = " +
                        dbl.sqlString(cal.strCashAcctCode) +
                        " and FCatCode = " +
                        dbl.sqlString(cal.strCatCode.length() == 0 ? " " :
                                      cal.strCatCode) +
                        " and FSubCatCode = " +
                        dbl.sqlString(cal.strSubCatCode.length() == 0 ? " " :
                                      cal.strSubCatCode) +
                        " and FInvMgrCode = " +
                        dbl.sqlString(cal.strInvMgrCode.length() == 0 ? " " :
                                      cal.strInvMgrCode) +
                        " and FPortCode = " +
                        dbl.sqlString(cal.strPortCode.length() == 0 ? " " :
                                      cal.strPortCode) +
                        " and FExchangeCode = " +
                        dbl.sqlString(cal.strExchangeCode.length() == 0 ? " " :
                                      cal.strExchangeCode) +
                        " and FTradeTypeCode = " +
                        dbl.sqlString(cal.strTradeTypeCode.length() == 0 ? " " :
                                      cal.strTradeTypeCode) +
                        " and FBrokerCode = " +
                        dbl.sqlString(cal.strBrokerCode.length() == 0 ? " " :
                                      cal.strBrokerCode) +
                        " and FStartDate = " + dbl.sqlDate(cal.dtStartDate) +
                        " and FCuryCode = " +
                        dbl.sqlString(cal.CuryCode.length() == 0 ? " " :
                                      cal.CuryCode);
                    System.out.print(strSql);
                    dbl.executeSql(strSql);
                }

            } else if (sRecycled != null && sRecycled.length() != 0) {
                sChkAccLinkAry = this.sRecycled.split("\r\n");
                for (int i = 0; i < sChkAccLinkAry.length; i++) {
                    cal.parseRowStr(sChkAccLinkAry[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Para_CashAccLink") +
                        " set FCheckState = " +
                        cal.checkStateId + ", FCheckUser = '" + pub.getUserCode() +
                        "', FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FCashAccCode = " +
                        dbl.sqlString(cal.strCashAcctCode) +
                        " and FCatCode = " +
                        dbl.sqlString(cal.strCatCode.length() == 0 ? " " :
                                      cal.strCatCode) +
                        " and FSubCatCode = " +
                        dbl.sqlString(cal.strSubCatCode.length() == 0 ? " " :
                                      cal.strSubCatCode) +
                        " and FInvMgrCode = " +
                        dbl.sqlString(cal.strInvMgrCode.length() == 0 ? " " :
                                      cal.strInvMgrCode) +
                        " and FPortCode = " +
                        dbl.sqlString(cal.strPortCode.length() == 0 ? " " :
                                      cal.strPortCode) +
                        " and FExchangeCode = " +
                        dbl.sqlString(cal.strExchangeCode.length() == 0 ? " " :
                                      cal.strExchangeCode) +
                        " and FTradeTypeCode = " +
                        dbl.sqlString(cal.strTradeTypeCode.length() == 0 ? " " :
                                      cal.strTradeTypeCode) +
                        " and FBrokerCode = " +
                        dbl.sqlString(cal.strBrokerCode.length() == 0 ? " " :
                                      cal.strBrokerCode) +
                        " and FStartDate = " + dbl.sqlDate(cal.dtStartDate) + //做到此了dtStartDate为空
                        " and FCuryCode = " +
                        dbl.sqlString(cal.CuryCode.length() == 0 ? " " :
                                      cal.CuryCode);
                    System.out.print(strSql);
                    dbl.executeSql(strSql);
                }

            } else if (strCashAcctCode != null && strCashAcctCode.length() != 0) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_CashAccLink") +
                    " set FCheckState = " +
                    cal.checkStateId + ", FCheckUser = '" + pub.getUserCode() +
                    "', FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FCashAccCode = " +
                    dbl.sqlString(this.strCashAcctCode) +
                    " and FCatCode = " +
                    dbl.sqlString(this.strCatCode.length() == 0 ? " " :
                                  this.strCatCode) +
                    " and FSubCatCode = " +
                    dbl.sqlString(this.strSubCatCode.length() == 0 ? " " :
                                  this.strSubCatCode) +
                    " and FInvMgrCode = " +
                    dbl.sqlString(this.strInvMgrCode.length() == 0 ? " " :
                                  this.strInvMgrCode) +
                    " and FPortCode = " +
                    dbl.sqlString(this.strPortCode.length() == 0 ? " " :
                                  this.strPortCode) +
                    " and FExchangeCode = " +
                    dbl.sqlString(this.strExchangeCode.length() == 0 ? " " :
                                  this.strExchangeCode) +
                    " and FTradeTypeCode = " +
                    dbl.sqlString(this.strTradeTypeCode.length() == 0 ? " " :
                                  this.strTradeTypeCode) +
                    " and FBrokerCode = " +
                    dbl.sqlString(this.strBrokerCode.length() == 0 ? " " :
                                  this.strBrokerCode) +
                    " and FStartDate = " + dbl.sqlDate(this.dtStartDate) + //做到此了dtStartDate为空
                    " and FCuryCode = " +
                    dbl.sqlString(this.CuryCode.length() == 0 ? " " :
                                  this.CuryCode);
                System.out.print(strSql);
                dbl.executeSql(strSql);

            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("审核现金帐户链接信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
//--------------------------------------end
    }

    /**
     * 为各项变量赋值
     *
     */
    public void setCashAccLinkAttr(ResultSet rs) throws SQLException {
        this.strCashAcctCode = rs.getString("FCashAccCode") + "";
        this.strCashAcctName = rs.getString("FCashAccName") + "";
        this.dtStartDate = rs.getDate("FStartDate");
        this.strCatCode = rs.getString("FCatCode") + "";
        this.strCatName = rs.getString("FCatName") + "";
        this.strSubCatCode = rs.getString("FSubCatCode") + "";
        this.strSubCatName = rs.getString("FSubCatName") + "";
        this.strInvMgrCode = rs.getString("FInvMgrCode") + "";
        this.strInvMgrName = rs.getString("FInvMgrName") + "";
        this.strPortCode = rs.getString("FPortCode") + "";
        this.strPortName = rs.getString("FPortName") + "";
        this.strExchangeCode = rs.getString("FExchangeCode") + "";
        this.strExchangeName = rs.getString("FExchangeName") + "";
        this.strTradeTypeCode = rs.getString("FTradeTypeCode") + "";
        this.strTradeTypeName = rs.getString("FTradeTypeName") + "";
        this.strBrokerCode = rs.getString("FBrokerCode") + "";
        this.strBrokerName = rs.getString("FBrokerName") + "";
        this.strDesc = rs.getString("FDesc") + "";
        this.strAcctState = rs.getString("FLinklevel") + "";
        this.CuryCode = rs.getString("FCuryCode") + "";
        this.CuryName = rs.getString("FCuryName") + "";
        this.sAuxiCashAcctCode = rs.getString("FAuxiCashAccCode"); //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
        this.sAuxiCashAcctName = rs.getString("FAuxiCashAccName"); //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
        super.setRecLog(rs);
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
        CashAccLinkBean befEditBean = new CashAccLinkBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                "(select FInvMgrCode,FPortCode,FCatCode,FSubCatCode,FBrokerCode,FTradeTypeCode,FExchangeCode," +
                "FCashAccCode,FCheckState,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccLink") + " " +
                " where FStartDate <= " +
                dbl.sqlDate(new java.util.Date()) +
                " and FCheckState <> 2 group by FInvMgrCode,FPortCode,FCatCode,FSubCatCode,FBrokerCode," +
                "FTradeTypeCode,FExchangeCode,FCashAccCode,FCheckState) x join" +
                " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FCatName, e.FSubCatName," +
                " auxi.FCashAccName as FAuxiCashAccName," + //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                "f.FInvMgrName, g.FCashAccName, h.FPortName, i.FExchangeName, j.FBrokerName, k.FTradeTypeName, m.FVocName as FLinkLevelValue,h.FCuryName as FCuryName" +
                " from " + pub.yssGetTableName("Tb_Para_CashAccLink") + "  a " +
                //--------------------------------------------------------------------------------------------------------------------------------
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                //--------------------------------------------------------------------------------------------------------------------------------
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //--------------------------------------------------------------------------------------------------------------------------------
                " left join (select FCatCode,FCatName from Tb_Base_Category where FCheckState = 1) d " +
                " on a.FCatCode = d.FCatCode" +
                //--------------------------------------------------------------------------------------------------------------------------------
                " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where FCheckState = 1) e " +
                " on a.FSubCatCode = e.FSubCatCode" +
                //--------------------------------------------------//
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) h on a.FCuryCode = h.FCuryCode" +
                //--------------------------------------------------------------------------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
       
                " left join (select FInvMgrCode, FInvMgrName from " +
                pub.yssGetTableName("Tb_Para_InvestManager") +
                " where  FCheckState = 1 ) f on a.FInvMgrCode = f.FInvMgrCode " +
               
                
                // end by lidaolong
                //--------------------------------------------------------------------------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
      
                
                " left join (select FCashAccCode, FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where  FCheckState = 1 ) g on a.FCashAccCode = g.FCashAccCode " +
                
                //end by lidaolong
                //--------------------------------------------------------------------------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
               /* 
                //添加FAuxiCashAccCode的关联名称字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                " left join " + pub.yssGetTableName("Tb_Para_CashAccount") + " auxi on a.FAuxiCashAccCode = auxi.FCashAccCode " +
                //QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                " left join (select hb.* from (select FPortCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FStartDate <= " +
                dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FPortCode) ha join (select FPortCode, FPortName, FStartDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                ") hb on ha.FPortCode = hb.FPortCode and ha.FStartDate = hb.FStartDate) h on a.FPortCode = h.FPortCode " +
               */
                
                
                //添加FAuxiCashAccCode的关联名称字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                " left join " + pub.yssGetTableName("Tb_Para_CashAccount") + " auxi on a.FAuxiCashAccCode = auxi.FCashAccCode " +
                //QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                " left join (select FPortCode, FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1 ) h on a.FPortCode = h.FPortCode " +
               
                //end by lidaolong
                //--------------------------------------------------------------------------------------------------------------------------------
                " left join (select FExchangeCode, FExchangeName from Tb_Base_Exchange where FCheckState = 1) i on a.FExchangeCode = i.FExchangeCode" +
                //--------------------------------------------------------------------------------------------------------------------------------
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
    
                " left join (select FBrokerCode, FBrokerName from " +
                pub.yssGetTableName("Tb_Para_Broker") + " where  FCheckState = 1 ) j on a.FBrokerCode = j.FBrokerCode " +
               
                //end by lidaolong
                //--------------------------------------------------------------------------------------------------------------------------------
                " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType where FCheckState = 1) k on a.FTradeTypeCode = k.FTradeTypeCode" +
                //--------------------------------------------------------------------------------------------------------------------------------
                " left join Tb_Fun_Vocabulary m on " +
                dbl.sqlToChar("a.FLinkLevel") +
                " = m.FVocCode and m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CNT_LINKLEVEL) +
                //--------------------------------------------------------------------------------------------------------------------------------
                " where  a.FCashAccCode =" +
                dbl.sqlString(this.strOldCashAcctCode) +
                ") y on x.FInvMgrCode = y.FInvMgrCode and x.FPortCode = y.FPortCode " +
                " and x.FCatCode = y.FCatCode and x.FSubCatCode = y.FSubCatCode " +
                " and x.FBrokerCode = y.FBrokerCode and x.FTradeTypeCode = y.FTradeTypeCode" +
                " and x.FExchangeCode = y.FExchangeCode and x.FCashAccCode = y.FCashAccCode " +
                " and x.FStartDate = y.FStartDate" +
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.strCashAcctCode = rs.getString("FCashAccCode") + "";
                befEditBean.strCashAcctName = rs.getString("FCashAccName") + "";
                befEditBean.dtStartDate = rs.getDate("FStartDate");
                befEditBean.strCatCode = rs.getString("FCatCode") + "";
                befEditBean.strCatName = rs.getString("FCatName") + "";
                befEditBean.strSubCatCode = rs.getString("FSubCatCode") + "";
                befEditBean.strSubCatName = rs.getString("FSubCatName") + "";
                befEditBean.strInvMgrCode = rs.getString("FInvMgrCode") + "";
                befEditBean.strInvMgrName = rs.getString("FInvMgrName") + "";
                befEditBean.strPortCode = rs.getString("FPortCode") + "";
                befEditBean.strPortName = rs.getString("FPortName") + "";
                befEditBean.strExchangeCode = rs.getString("FExchangeCode") + "";
                befEditBean.strExchangeName = rs.getString("FExchangeName") + "";
                befEditBean.strTradeTypeCode = rs.getString("FTradeTypeCode") + "";
                befEditBean.strTradeTypeName = rs.getString("FTradeTypeName") + "";
                befEditBean.strBrokerCode = rs.getString("FBrokerCode") + "";
                befEditBean.strBrokerName = rs.getString("FBrokerName") + "";
                befEditBean.strDesc = rs.getString("FDesc") + "";
                befEditBean.strAcctState = rs.getString("FLinklevel") + "";
                befEditBean.CuryCode = rs.getString("FCuryCode") + "";

                befEditBean.sAuxiCashAcctCode = rs.getString("FAuxiCashAccCode"); //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                befEditBean.sAuxiCashAcctName = rs.getString("FAuxiCashAccName"); //新增字段 QDV4招商证券2009年04月09日01_A MS00371 by leeyu 20090505
                //    befEditBean.CuryName = rs.getString("FCuryName") + "";

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }

    }

    /**
     * 从期间连接回收站删除数据，即是彻底删除
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
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
                        pub.yssGetTableName("Tb_Para_CashAccLink") +
                        " where FCashAccCode = " +
                        dbl.sqlString(this.strOldCashAcctCode) +
                        " and FCatCode = " +
                        dbl.sqlString(this.strOldCatCode.length() == 0 ? " " :
                                      this.strOldCatCode) +
                        " and FSubCatCode = " +
                        dbl.sqlString(this.strOldSubCatCode.length() == 0 ? " " :
                                      this.strOldSubCatCode) +
                        " and FInvMgrCode = " +
                        dbl.sqlString(this.strOldInvMgrCode.length() == 0 ? " " :
                                      this.strOldInvMgrCode) +
                        " and FPortCode = " +
                        dbl.sqlString(this.strOldPortCode.length() == 0 ? " " :
                                      this.strOldPortCode) +
                        " and FExchangeCode = " +
                        dbl.sqlString(this.strOldExchangeCode.length() == 0 ? " " :
                                      this.strOldExchangeCode) +
                        " and FTradeTypeCode = " +
                        dbl.sqlString(this.strOldTradeTypeCode.length() == 0 ? " " :
                                      this.strOldTradeTypeCode) +
                        " and FBrokerCode = " +
                        dbl.sqlString(this.strOldBrokerCode.length() == 0 ? " " :
                                      this.strOldBrokerCode) +
                        " and FStartDate = " + dbl.sqlDate(this.dtOldStartDate)+
                        /**shashijie 2012-3-21 BUG 4005*/
						" and FCuryCode = " + dbl.sqlString(this.oldFCuryCode.length()==0 ? " " : this.oldFCuryCode)
						/**end*/
                        ;
                    //执行sql语句
                    dbl.executeSql(strSql);

                }

            }
            //sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            else if (strOldCashAcctCode != "" && strOldCashAcctCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_CashAccLink") +
                    " where FCashAccCode = " +
                    dbl.sqlString(this.strOldCashAcctCode) +
                    " and FCatCode = " +
                    dbl.sqlString(this.strOldCatCode.length() == 0 ? " " :
                                  this.strOldCatCode) +
                    " and FSubCatCode = " +
                    dbl.sqlString(this.strOldSubCatCode.length() == 0 ? " " :
                                  this.strOldSubCatCode) +
                    " and FInvMgrCode = " +
                    dbl.sqlString(this.strOldInvMgrCode.length() == 0 ? " " :
                                  this.strOldInvMgrCode) +
                    " and FPortCode = " +
                    dbl.sqlString(this.strOldPortCode.length() == 0 ? " " :
                                  this.strOldPortCode) +
                    " and FExchangeCode = " +
                    dbl.sqlString(this.strOldExchangeCode.length() == 0 ? " " :
                                  this.strOldExchangeCode) +
                    " and FTradeTypeCode = " +
                    dbl.sqlString(this.strOldTradeTypeCode.length() == 0 ? " " :
                                  this.strOldTradeTypeCode) +
                    " and FBrokerCode = " +
                    dbl.sqlString(this.strOldBrokerCode.length() == 0 ? " " :
                                  this.strOldBrokerCode) +
                    " and FStartDate = " + dbl.sqlDate(this.dtOldStartDate)+
                    /**shashijie 2012-3-21 BUG 4005*/
					" and FCuryCode = " + dbl.sqlString(this.oldFCuryCode.length()==0 ? " " : this.oldFCuryCode)
					/**end*/
                    ;
                //执行sql语句
                dbl.executeSql(strSql);

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

	public String getOldFCuryCode() {
		return oldFCuryCode;
	}

	public void setOldFCuryCode(String oldFCuryCode) {
		this.oldFCuryCode = oldFCuryCode;
	}
}
