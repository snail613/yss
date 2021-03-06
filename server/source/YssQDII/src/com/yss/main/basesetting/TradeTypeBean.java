package com.yss.main.basesetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 *
 * <p>Title:TradeTypeBean </p>
 * <p>Description: 交易类型设置</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class TradeTypeBean
    extends BaseDataSettingBean implements IDataSetting {
    private String tradeTypeCode = ""; //交易类型代码
    private String tradeTypeName = ""; //交易类型名称
    private String serviceType = ""; //业务类型代码 hjj 20090630 add
    private int cashInd; //资金方向
    private int amountInd; //数量方向
    private String tradeTypeDesc = ""; //交易类型描述
    private String status = ""; //是否记入系统信息状态  lzp 11.30 add
    private String oldTradeTypeCode;
    private TradeTypeBean filterType;
    private String sRecycled = ""; //为增加还原和删除功能加的一个中介字符串变量 bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
  
    public TradeTypeBean() {
    }

    /**
     * parseRowStr
     * 解析及交易类型数据
     * @param sRowStr String
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
            sRecycled = sRowStr; //bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
            reqAry = sRowStr.split("\t");

            this.tradeTypeCode = reqAry[0];
            this.tradeTypeName = reqAry[1];
            this.serviceType = reqAry[2]; //hjj 20090701 add
            this.cashInd = YssFun.toInt(reqAry[3]); //hjj 20090701 upd 2 -> 3 | 编号皆向后+1
            this.amountInd = YssFun.toInt(reqAry[4]);
            this.tradeTypeDesc = reqAry[5];
            this.checkStateId = Integer.parseInt(reqAry[6]);
            this.oldTradeTypeCode = reqAry[7];
            this.status = reqAry[8]; //lzp add 11.30
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TradeTypeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析交易类型请出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.tradeTypeCode.trim()).append("\t");
        buf.append(this.tradeTypeName.trim()).append("\t");
        buf.append(this.serviceType.trim()).append("\t"); //hjj 20090701 add
        buf.append(getOperTypeNameByCode(this.serviceType.trim())).append("\t"); //hjj 20090701 add
        buf.append(this.cashInd).append("\t");
        buf.append(this.amountInd).append("\t");
        buf.append(this.tradeTypeDesc.trim()).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * 传入以逗号隔开的操作类型代码，返回以逗号隔开的操作类型名称。
     * @param sOperTypeCode String
     * @return String
     * @throws YssException
     */
    public String getOperTypeNameByCode(String sOperTypeCode) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "";
        try {
            if (sOperTypeCode == null) {
                return "";
            }
            strSql = "select FVocName from Tb_Fun_Vocabulary where FVocCode IN (" + operSql.sqlCodes(sOperTypeCode) + ")" +
                " and FCheckState=1 and FVocTypeCode = " + dbl.sqlString(YssCons.YSS_TRADETYPE_SERVICE);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult += (rs.getString("FVocName") + ",");
            }
            if (sResult.length() > 0) {
                sResult = sResult.substring(0, sResult.length() - 1);
            }
        } catch (Exception e) {
            throw new YssException("获取业务类型名出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {

        dbFun.checkInputCommon(btOper, "Tb_Base_TradeType", "FTradeTypeCode",
                               this.tradeTypeCode, this.oldTradeTypeCode);
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
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.tradeTypeCode.length() != 0) { // wdy add 20070901 添加表别名:a
                // modify by wangzuochun 2009.06.22
                // MS00014  国内回购业务  QDV4.1赢时胜（上海）2009年4月20日14_A
                //sResult = sResult + " and a.FTradeTypeCode like '" +
                //          filterType.tradeTypeCode.replaceAll("'", "''") + "%'";
                sResult = sResult + " and a.FTradeTypecode in(" + //libo MS00484 QDV4招商证券2009年06月04日01_A
                    operSql.sqlCodes(filterType.tradeTypeCode) + ")";
            }
            if (this.filterType.tradeTypeName.length() != 0) {
                sResult = sResult + " and a.FTradeTypeName like '" +
                    filterType.tradeTypeName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.tradeTypeDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '%" + // wdy 改为模糊查询like '%XXX%'
                    filterType.tradeTypeDesc.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.cashInd != 99) {
                sResult = sResult + " and a.FCashInd = " +
                    filterType.cashInd;
            }
            if (this.filterType.amountInd != 99) {
                sResult = sResult + " and a.FAmountInd = " +
                    filterType.amountInd;
            }
            if (this.filterType.serviceType.length() != 0) {
                sResult = sResult + " and a.FServiceType like '%" +
                    filterType.serviceType + "%'";
            }

        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取交易类型数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        String sVocStr = ""; //词汇类型对照字符串
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName," +
                "d.FVocName as FCashIndValue,e.FVocName as FAmountIndValue from Tb_Base_TradeType a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //2007.11.29 修改 蒋锦 使用dbl.sqlToChar()处理"a.FCashInd"，否则在使用DB2数据库时会报数据类型错误
                " left join Tb_Fun_Vocabulary d on " +
                dbl.sqlToChar("a.FCashInd") +
                " = d.FVocCode and d.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_TDT_CASHIND) +
                //2007.11.29 修改 蒋锦 使用dbl.sqlToChar()处理"a.FAmountInd"，否则在使用DB2数据库时会报数据类型错误
                " left join Tb_Fun_Vocabulary e on " +
                dbl.sqlToChar("a.FAmountInd") +
                " = e.FVocCode and e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_TDT_AMOUNTIND) +
                buildFilterSql() +
                " order by a.FCheckTime desc, a.FCreateTime desc, a.FCheckState, a.FTradeTypeCode";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.tradeTypeCode = rs.getString("FTradeTypeCode") + "";
                this.tradeTypeName = rs.getString("FTradeTypeName") + "";
                this.serviceType = rs.getString("FServiceType") == null ? "" : rs.getString("FServiceType"); //hjj 20090701 add
                this.cashInd = rs.getInt("FCashInd");
                this.amountInd = rs.getInt("FAmountInd");
                this.tradeTypeDesc = rs.getString("FDesc") + "";
                this.checkStateId = rs.getInt("FCheckState");
                this.checkStateName = YssFun.getCheckStateName(rs.getInt(
                    "FCheckState"));
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FCreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime") + "";
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

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_TDT_CASHIND + "," +
                                        YssCons.YSS_TDT_AMOUNTIND);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取交易类型出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData2
     * 获取已审核的地域设置数据
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "交易类型代码\t交易类型名称";
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from Tb_Base_TradeType a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                ( (buildFilterSql().length() > 0) ?
                 buildFilterSql() + " and " :
                 " where ") +
                " FCheckState = 1 order by a.FTradeTypeCode, a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            bufShow = new StringBuffer();
            bufAll = new StringBuffer();
            while (rs.next()) {
                bufShow.append( (rs.getString("FTradeTypeCode") + "").trim()).
                    append(
                        "\t");
                bufShow.append( (rs.getString("FTradeTypeName") + "").trim()).
                    append(
                        YssCons.YSS_LINESPLITMARK);

                this.tradeTypeCode = rs.getString("FTradeTypeCode") + "";
                this.tradeTypeName = rs.getString("FTradeTypeName") + "";
                this.serviceType = rs.getString("FServiceType") == null ? "" : rs.getString("FServiceType"); //hjj 20090702 add
                this.cashInd = rs.getInt("FCashInd");
                this.amountInd = rs.getInt("FAmountInd");
                this.tradeTypeDesc = rs.getString("FDesc") + "";
                this.checkStateId = rs.getInt("FCheckState");
                this.checkStateName = YssFun.getCheckStateName(rs.getInt(
                    "FCheckState"));
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FCreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime") + "";
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取可用交易类型数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * MS01354   add by zhangfa 20100712 MS01354    QDV4赢时胜(上海)2010年06月25日01_A    
     * getListViewData2
     * 获取  配股、配股缴款、权证
     * @return String
     */
    //
    public String getListViewData4() throws YssException {
    	String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "交易类型代码\t交易类型名称";
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from Tb_Base_TradeType a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                ( (buildFilterSql().length() > 0) ?
                 buildFilterSql() + " and " :
                 " where ") +"Ftradetypecode in( "+"'08',"+"'23',"+"'22'"+") and"+
                " FCheckState = 1 order by a.FTradeTypeCode, a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            bufShow = new StringBuffer();
            bufAll = new StringBuffer();
            while (rs.next()) {
                bufShow.append( (rs.getString("FTradeTypeCode") + "").trim()).
                    append(
                        "\t");
                bufShow.append( (rs.getString("FTradeTypeName") + "").trim()).
                    append(
                        YssCons.YSS_LINESPLITMARK);

                this.tradeTypeCode = rs.getString("FTradeTypeCode") + "";
                this.tradeTypeName = rs.getString("FTradeTypeName") + "";
                this.serviceType = rs.getString("FServiceType") == null ? "" : rs.getString("FServiceType"); //hjj 20090702 add
                this.cashInd = rs.getInt("FCashInd");
                this.amountInd = rs.getInt("FAmountInd");
                this.tradeTypeDesc = rs.getString("FDesc") + "";
                this.checkStateId = rs.getInt("FCheckState");
                this.checkStateName = YssFun.getCheckStateName(rs.getInt(
                    "FCheckState"));
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FCreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime") + "";
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取可用交易类型数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * getListViewData3
     * 获取已审核的股指期货交易类型数据
     * @return String
     * 修改人：蒋春
     * 修改时间：2008-09-04
     */
    public String getListViewData3() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        String potionMode = "";  //期货持仓模式
        try {
            sHeader = "交易类型代码\t交易类型名称";
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from Tb_Base_TradeType a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where FTradeTypeCode in ('20','21','01','02') and " +  //modified by zhaoxianlin 20121207 #STORY #3371 增加01‘02类型
                " FCheckState = 1 order by a.FTradeTypeCode, a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            bufShow = new StringBuffer();
            bufAll = new StringBuffer();
            while (rs.next()) {
                bufShow.append( (rs.getString("FTradeTypeCode") + "").trim()).
                    append(
                        "\t");
                bufShow.append( (rs.getString("FTradeTypeName") + "").trim()).
                    append(
                        YssCons.YSS_LINESPLITMARK);

                this.tradeTypeCode = rs.getString("FTradeTypeCode") + "";
                this.tradeTypeName = rs.getString("FTradeTypeName") + "";
                this.serviceType = rs.getString("FServiceType") == null ? "" : rs.getString("FServiceType"); //hjj 20090702 add
                this.cashInd = rs.getInt("FCashInd");
                this.amountInd = rs.getInt("FAmountInd");
                this.tradeTypeDesc = rs.getString("FDesc") + "";
                this.checkStateId = rs.getInt("FCheckState");
                this.checkStateName = YssFun.getCheckStateName(rs.getInt(
                    "FCheckState"));
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FCreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime") + "";
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取可用股指期货交易类型数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    public String getListViewData5() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "交易类型代码\t交易类型名称";
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from Tb_Base_TradeType a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where FTradeTypeCode in ('01','02') and " +
                " FCheckState = 1 order by a.FTradeTypeCode, a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            bufShow = new StringBuffer();
            bufAll = new StringBuffer();
            while (rs.next()) {
                bufShow.append( (rs.getString("FTradeTypeCode") + "").trim()).
                    append(
                        "\t");
                bufShow.append( (rs.getString("FTradeTypeName") + "").trim()).
                    append(
                        YssCons.YSS_LINESPLITMARK);

                this.tradeTypeCode = rs.getString("FTradeTypeCode") + "";
                this.tradeTypeName = rs.getString("FTradeTypeName") + "";
                this.serviceType = rs.getString("FServiceType") == null ? "" : rs.getString("FServiceType"); //hjj 20090702 add
                this.cashInd = rs.getInt("FCashInd");
                this.amountInd = rs.getInt("FAmountInd");
                this.tradeTypeDesc = rs.getString("FDesc") + "";
                this.checkStateId = rs.getInt("FCheckState");
                this.checkStateName = YssFun.getCheckStateName(rs.getInt(
                    "FCheckState"));
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FCreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime") + "";
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取可用股指期货交易类型数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getSetting
     * 2008-02-19 蒋锦 实现
     * @return IBaseSetting
     */
    public IDataSetting getSetting() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = "SELECT * FROM Tb_Base_TradeType WHERE FTradeTypeCode = " +
                dbl.sqlString(this.tradeTypeCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.tradeTypeCode = rs.getString("FTradeTypeCode") + "";
                this.tradeTypeName = rs.getString("FTradeTypeName") + "";
                this.cashInd = rs.getInt("FCashInd");
                this.amountInd = rs.getInt("FAmountInd");
                this.tradeTypeDesc = rs.getString("FDesc") + "";
                this.checkStateId = rs.getInt("FCheckState");
                this.creatorCode = rs.getString("FCreator");
                this.creatorTime = rs.getString("FCreateTime");
                this.checkUserCode = rs.getString("FCheckUser");
                this.checkTime = rs.getString("FCheckTime");
            }
        } catch (Exception e) {
            throw new YssException("获取交易类型信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return this;
    }

    /**
     * getTreeViewData1
     * 20120813 added by liubo.Story #2754.为通参“期权成本核算设置”的“交易方式”控件提供数据
     * @return String
     */
    public String getTreeViewData1() throws YssException{
    	String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "交易类型代码\t交易类型名称";
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from Tb_Base_TradeType a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                ( (buildFilterSql().length() > 0) ?
                 buildFilterSql() + " and " :
                 " where ") +"Ftradetypecode in( "+"'01','02'"+") and"+
                " FCheckState = 1 order by a.FTradeTypeCode, a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            bufShow = new StringBuffer();
            bufAll = new StringBuffer();
            while (rs.next()) {
                bufShow.append( (rs.getString("FTradeTypeCode") + "").trim()).
                    append(
                        "\t");
                bufShow.append( (rs.getString("FTradeTypeName") + "").trim()).
                    append(
                        YssCons.YSS_LINESPLITMARK);

                this.tradeTypeCode = rs.getString("FTradeTypeCode") + "";
                this.tradeTypeName = rs.getString("FTradeTypeName") + "";
                this.serviceType = rs.getString("FServiceType") == null ? "" : rs.getString("FServiceType"); //hjj 20090702 add
                this.cashInd = rs.getInt("FCashInd");
                this.amountInd = rs.getInt("FAmountInd");
                this.tradeTypeDesc = rs.getString("FDesc") + "";
                this.checkStateId = rs.getInt("FCheckState");
                this.checkStateName = YssFun.getCheckStateName(rs.getInt(
                    "FCheckState"));
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FCreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime") + "";
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取可用交易类型数据出错", e);
        } finally {
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
     * 新增、修改、删除、审核
     * @param btOper byte
     */
    /* public void saveSetting(byte btOper) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
           if (btOper == YssCons.OP_ADD) {

              strSql = "insert into Tb_Base_TradeType" +
                    "(FTradeTypeCode,FTradeTypeName,FCashInd,FAmountInd,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                    " values(" + dbl.sqlString(this.tradeTypeCode) + "," +
                    dbl.sqlString(this.tradeTypeName) + "," +
                    this.cashInd + "," +
                    this.amountInd + "," +
                    dbl.sqlString(this.tradeTypeDesc) + "," +
                    (pub.getSysCheckState() ? "0" : "1") + "," +
                    dbl.sqlString(this.creatorName) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) + ")";

           }
           else if (btOper == YssCons.OP_EDIT) {

              strSql = "update Tb_Base_TradeType set FTradeTypeCode = " +
                    dbl.sqlString(this.tradeTypeCode) + ", FTradeTypeName = " +
                    dbl.sqlString(this.tradeTypeName) + ",FCashInd = " +
                    this.cashInd + ",FAmountInd = " +
                    this.amountInd + ", FDesc = " +
                    dbl.sqlString(this.tradeTypeDesc) + ",FCheckState = " +
                    (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                    dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                    dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) +
                    " where FTradeTypeCode = " +
                    dbl.sqlString(this.oldTradeTypeCode);
           }
           else if (btOper == YssCons.OP_DEL) {

              strSql = "update Tb_Base_TradeType set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "' where FTradeTypeCode = " +
                    dbl.sqlString(this.tradeTypeCode);
           }
           else if (btOper == YssCons.OP_AUDIT) {

              strSql = "update Tb_Base_TradeType set FCheckState = " +
                    this.checkStateId + ",FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ",FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FTradeTypeCode = " +
                    dbl.sqlString(this.tradeTypeCode);

           }
           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("更新交易类型信息出错", e);
        }
        finally {
           dbl.endTransFinal(conn, bTrans);
        }

     }*/
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into Tb_Base_TradeType" +
                "(FTradeTypeCode,FTradeTypeName,FServiceType,FCashInd,FAmountInd,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.tradeTypeCode) + "," +
                dbl.sqlString(this.tradeTypeName) + "," +
                dbl.sqlString(this.serviceType) + "," + //hjj 10090701 add
                this.cashInd + "," +
                this.amountInd + "," +
                dbl.sqlString(this.tradeTypeDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorName) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.30
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("新增-交易类型设置");
                sysdata.setStrCode(this.tradeTypeCode);
                sysdata.setStrName(this.tradeTypeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增交易类型设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

    /**
     * bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
     * 修改人：方浩
     * 原方法功能：只能处理期间连接的审核和未审核的单条信息。
     * 新方法功能：可以处理回购品种信息设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */

    public void checkSetting() throws YssException {
        /*    String strSql = "";
            boolean bTrans = false; //代表是否开始了事务
            Connection conn = dbl.loadConnection();
            try {
               strSql = "update Tb_Base_TradeType set FCheckState = " +
                      this.checkStateId + ",FCheckUser = " +
                      dbl.sqlString(pub.getUserCode()) + ",FCheckTime = '" +
                      YssFun.formatDatetime(new java.util.Date()) +
                      "' where FTradeTypeCode = " +
                      dbl.sqlString(this.tradeTypeCode);

               conn.setAutoCommit(false);
               bTrans = true;
               dbl.executeSql(strSql);
               //---------lzp add 11.30
          if (this.status.equalsIgnoreCase("1")) {
           com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                 funsetting.SysDataBean();
           sysdata.setYssPub(pub);
           sysdata.setStrAssetGroupCode("Common");
           if(this.checkStateId==1){
                   sysdata.setStrFunName("审核-交易类型设置");
               }else{
                    sysdata.setStrFunName("反审核-交易类型设置");
               }

           sysdata.setStrCode(this.tradeTypeCode);
           sysdata.setStrName(this.tradeTypeName);
           sysdata.setStrUpdateSql(strSql);
           sysdata.setStrCreator(pub.getUserName());
           sysdata.addSetting();
          }
//-----------------------

               conn.commit();
               bTrans = false;
               conn.setAutoCommit(true);
            }
            catch (Exception e) {
               throw new YssException("审核交易类型设置信息出错", e);
            }
            finally {
               dbl.endTransFinal(conn, bTrans);
            }
         */
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环还原
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //打开一个数据库联接
        try {
            conn.setAutoCommit(false); //开启一个事物
            bTrans = true; //代表是否关闭事务
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) { //判断传来的内容是否为空//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                arrData = sRecycled.split("\r\n"); //解析它，把它还原成条目放在数组里。
                for (int i = 0; i < arrData.length; i++) { //循环数组，也就是循环还原条目
                    if (arrData[i].length() == 0) {
                        continue; //如果数组里没有内容就执行下一个内容
                    }
                    this.parseRowStr(arrData[i]); //解析这个数组里的内容
                    strSql = "update Tb_Base_TradeType set FCheckState = " +
                        this.checkStateId + ",FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ",FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FTradeTypeCode = " +
                        dbl.sqlString(this.tradeTypeCode); //更新数据的SQL语句
                    dbl.executeSql(strSql); //执行更新操作
                }
            }
            //如果sRecycled为空，而tradeTypeCode不为空，则按照tradeTypeCode来执行sql语句
            else if ( tradeTypeCode != null && (!tradeTypeCode.equalsIgnoreCase("")) ) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                strSql = "update Tb_Base_TradeType set FCheckState = " +
                    this.checkStateId + ",FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ",FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FTradeTypeCode = " +
                    dbl.sqlString(this.tradeTypeCode); //更新数据的SQL语句
                dbl.executeSql(strSql); //执行更新操作
            }
            if (this.status.equalsIgnoreCase("1")) { //判断status是否等于1,当传入1的时候就记录系统的信息状态
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub); //设置pub的值
                sysdata.setStrAssetGroupCode("Common"); //设置StrAssetGroupCode的值
                if (this.checkStateId == 1) { //如果checkStateId==1就是它要的状态是审核状
                    sysdata.setStrFunName("审核-交易类型设置"); //设置StrFunName的值
                } else {
                    sysdata.setStrFunName("反审核-交易类型设置"); //设置StrFunName的值
                }

                sysdata.setStrCode(this.tradeTypeCode); //设置StrCode的值
                sysdata.setStrName(this.tradeTypeName); //设置StrName的值
                sysdata.setStrUpdateSql(strSql); //设置StrUpdateSql的值
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting(); //把这些以上数据添加到系统数据表Tb_Fun_SysData
            }
//-----------------------

            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核交易类型设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }
//----------------end

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update Tb_Base_TradeType set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "' where FTradeTypeCode = " +
                dbl.sqlString(this.tradeTypeCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.30
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-交易类型设置");
                sysdata.setStrCode(this.tradeTypeCode);
                sysdata.setStrName(this.tradeTypeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除交易类型设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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
            strSql = "update Tb_Base_TradeType set FTradeTypeCode = " +
                dbl.sqlString(this.tradeTypeCode) + ", FTradeTypeName = " +
                dbl.sqlString(this.tradeTypeName) + ", FServiceType = " +
                dbl.sqlString(this.serviceType) + ",FCashInd = " +
                this.cashInd + ",FAmountInd = " +
                this.amountInd + ", FDesc = " +
                dbl.sqlString(this.tradeTypeDesc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FTradeTypeCode = " +
                dbl.sqlString(this.oldTradeTypeCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.30
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("修改-交易类型设置");
                sysdata.setStrCode(this.tradeTypeCode);
                sysdata.setStrName(this.tradeTypeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新交易类型设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

    

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        String strRtn = "";
        try {
            //----------2008.02.19 添加 蒋锦--------------//
            if (sType.equalsIgnoreCase("getsetting")) {
                this.getSetting();
                strRtn = this.buildRowStr();
            }
            if (sType.equalsIgnoreCase("listview5")) {
                strRtn = this.getListViewData5();
            }
            //-------------------------------------------//
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return strRtn;
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        TradeTypeBean befEditBean = new TradeTypeBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName," +
                "d.FVocName as FCashIndValue,e.FVocName as FAmountIndValue from Tb_Base_TradeType a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join Tb_Fun_Vocabulary d on " +
                dbl.sqlToChar("a.FCashInd") +
                " = d.FVocCode and d.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_TDT_CASHIND) +
                " left join Tb_Fun_Vocabulary e on " +
                dbl.sqlToChar("a.FAmountInd") +
                " = e.FVocCode and e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_TDT_AMOUNTIND) +
                " where  a.FTradeTypeCode =" +
                dbl.sqlString(this.oldTradeTypeCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.tradeTypeCode = rs.getString("FTradeTypeCode") + "";
                befEditBean.tradeTypeName = rs.getString("FTradeTypeName") + "";
                befEditBean.serviceType = rs.getString("FServiceType") == null ? "" : rs.getString("FServiceType"); //hjj 20090701 add
                befEditBean.cashInd = rs.getInt("FCashInd");
                befEditBean.amountInd = rs.getInt("FAmountInd");
                befEditBean.tradeTypeDesc = rs.getString("FDesc") == null ? "" : rs.getString("FDesc"); //hjj 20090701 upd old：befEditBean.tradeTypeDesc =  rs.getString("FDesc") + "";
            }
            befEditBean.setYssPub(pub);
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }

    }

    public String getStatus() {
        return status;
    }

    /**
     * deleteRecycleData

         public void deleteRecycleData() {
         }
     */
    /**
     * bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
     * 修改人：方浩
     * 回收站的删除功能调用此方法deleteRecycleData()
     * 从数据库删除数据，即彻底删除数据,可以多个一删除
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环删除
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
                        pub.yssGetTableName("Tb_Base_TradeType") +
                        " where FTradeTypeCode = " +
                        dbl.sqlString(this.tradeTypeCode);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而tradeTypeCode不为空，则按照tradeTypeCode来执行sql语句
            else if (tradeTypeCode != "" && tradeTypeCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Base_TradeType") +
                    " where FTradeTypeCode = " +
                    dbl.sqlString(this.tradeTypeCode);
                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true); //提交事物
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
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
    
}
