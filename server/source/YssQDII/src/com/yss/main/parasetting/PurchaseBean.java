package com.yss.main.parasetting;

import java.math.*;
import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class PurchaseBean
    extends BaseDataSettingBean implements IDataSetting {
    private String securityCode = "";
    private String securityName = "";
    private String depDurCode = "";
    private String depDurName = "";
    private double purchaseRate;

    private String desc = "";
    private String oldSecurityCode = "";

    private String PurchaseType = "";
    private String PurchaseTypeName = "";

    private String PeriodCode = "";
    private String PeriodName = "";
    private String sRecycled = "";
    //--添加计息起始日类型的设置 sj modified MS00088  -----//
    private String sInBeginType = ""; //计息起始日类型
    private String sOldInBeginType = "";
    private String sInBeginTypeName = "";
    //-------------------------------------------------//
    private PurchaseBean filterType;
    // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
    private String strPortCode = ""; //组合代码
    private String strPortName = ""; //组合名称
    private String assetGroupCode = ""; //组合群代码
    private String assetGroupName = ""; //组合群名称
    private boolean bOverGroup =false;//判断是否跨组合群，MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090813
    private String sStartDate="";   //起始日期
    private String sEndDate="";     //截止日期
    //--------------------------------------------------------------------------------

    public String getSecurityName() {
        return securityName;
    }

    public String getDesc() {
        return desc;
    }

    public String getOldSecurityCode() {
        return oldSecurityCode;
    }

    public PurchaseBean getFilterType() {
        return filterType;
    }

    public double getPurchaseRate() {
        return purchaseRate;
    }

    public String getDepDurCode() {
        return depDurCode;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setDepDurName(String depDurName) {
        this.depDurName = depDurName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOldSecurityCode(String oldSecurityCode) {
        this.oldSecurityCode = oldSecurityCode;
    }

    public void setFilterType(PurchaseBean filterType) {
        this.filterType = filterType;
    }

    public void setPurchaseRate(double purchaseRate) {
        this.purchaseRate = purchaseRate;
    }

    public void setDepDurCode(String depDurCode) {
        this.depDurCode = depDurCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupName = assetGroupName;
    }

    public void setStrPortCode(String strPortCode) {
        this.strPortCode = strPortCode;
    }

    public void setStrPortName(String strPortName) {
        this.strPortName = strPortName;
    }

    public void setSEndDate(String sEndDate) {
        this.sEndDate = sEndDate;
    }

    public void setSStartDate(String sStartDate) {
        this.sStartDate = sStartDate;
    }

    public void setSTradeDateType(String sInBeginType) {
        this.sInBeginType = sInBeginType;
    }

    public void setPeriodCode(String PeriodCode) {
        this.PeriodCode = PeriodCode;
    }

    public void setPeriodName(String PeriodName) {
        this.PeriodName = PeriodName;
    }

    public void setPurchaseTypeName(String PurchaseTypeName) {
        this.PurchaseTypeName = PurchaseTypeName;
    }

    public void setPurchaseType(String PurchaseType) {
        this.PurchaseType = PurchaseType;
    }

    public String getDepDurName() {
        return depDurName;
    }

    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public String getStrPortCode() {
        return strPortCode;
    }

    public String getStrPortName() {
        return strPortName;
    }

    public String getSEndDate() {
        return sEndDate;
    }

    public String getSStartDate() {
        return sStartDate;
    }

    public String getSTradeDateType() {
        return this.sInBeginType;
    }

    public String getPeriodCode() {
        return PeriodCode;
    }

    public String getPeriodName() {
        return PeriodName;
    }

    public String getPurchaseTypeName() {
        return PurchaseTypeName;
    }

    public String getPurchaseType() {
        return PurchaseType;
    }

    public PurchaseBean() {
    }

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
            //---add by yangshaokai 2011.12.22 BUG 3427 QDV4赢时胜(测试)2011年12月16日03_B start---//
            //批量还原数据时，每条数据是用 \r\n 分割的  批量处理的数据先储存在 sRecycled中 具体的批量处理方法中会对 sRecycled进行解析
            if(sRowStr.indexOf("\r\n") > -1){
            	sRecycled = sRowStr;
            	return;
            }
            //---add by yangshaokai 2011.12.22 BUG 3427 QDV4赢时胜(测试)2011年12月16日03_B end---//
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.securityCode = reqAry[0];
            this.depDurCode = reqAry[1];
            this.purchaseRate = YssFun.toDouble(reqAry[2]);
            //---edit by songjie 2011.08.17 BUG 2355 QDV4赢时胜(测试)2011年8月2日05_B start---// 
            if(reqAry[3].indexOf("【Enter】") != -1){
            	this.desc = reqAry[3].replaceAll("【Enter】", "\r\n");
            }else{
            	this.desc = reqAry[3];
            }
            //---edit by songjie 2011.08.17 BUG 2355 QDV4赢时胜(测试)2011年8月2日05_B end---// 
            this.checkStateId = YssFun.toInt(reqAry[4]);
            this.oldSecurityCode = reqAry[5];
            this.PurchaseType = reqAry[6];
            this.PeriodCode = reqAry[7];
            //---------添加计息起始日 sj modify MS00088  ------
            this.sInBeginType = reqAry[8];
            this.sOldInBeginType = reqAry[9];
            //---------------------------------------------
            // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
            this.assetGroupCode = reqAry[10];
            this.assetGroupName = reqAry[11];
            this.strPortCode=reqAry[12];
            this.strPortName=reqAry[13];
            this.sStartDate=reqAry[14];
            this.sEndDate=reqAry[15];
            //---------------------------------------------------------------------------------------------
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new PurchaseBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析回购品种信息设置出错", e);
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.securityCode);
        buf.append("\t");
        buf.append(this.securityName);
        buf.append("\t");
        buf.append(this.depDurCode);
        buf.append("\t");
        buf.append(this.depDurName);
        buf.append("\t");
        buf.append(this.purchaseRate);
        buf.append("\t");
        buf.append(this.desc);
        buf.append("\t");
        buf.append(this.PurchaseType);
        buf.append("\t");
        buf.append(this.PurchaseTypeName);
        buf.append("\t");
        buf.append(this.PeriodCode);
        buf.append("\t");
        buf.append(this.PeriodName);
        buf.append("\t");
        //---------MS00088-----------------
        buf.append(this.sInBeginType);
        buf.append("\t");
        buf.append(this.sInBeginTypeName);
        //---------------------------------
        buf.append("\t");
        // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
        buf.append(this.assetGroupCode).append("\t");
        buf.append(this.assetGroupName).append("\t");
        //---------------------------------------------------------------------------------------------
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Purchase"),
                               "FSecurityCode,FInBeginType",
                               this.securityCode + "," + this.sInBeginType,
                               this.oldSecurityCode + "," + this.sOldInBeginType); //MS00088

    }

    public String getAllSetting() {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.filterType != null) {
                sResult = " where 1=1 ";
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.securityCode != null && this.filterType.securityCode.length() != 0) {
                    sResult = sResult + " and a.FSecurityCode like '" +
                        filterType.securityCode.replaceAll("'", "''") + "%'";
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.securityCode != null && this.filterType.depDurCode.length() != 0) {
                    sResult = sResult + " and a.FDepDurCode like '" +
                        filterType.depDurCode.replaceAll("'", "''") + "%'";
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.securityCode != null && this.filterType.desc.length() != 0) {
                    sResult = sResult + " and a.FDesc like '" +
                        filterType.desc.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.purchaseRate > 0) {
                    sResult = sResult + " and a.FPurchaseRate=" +
                        filterType.purchaseRate;
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.securityCode != null && this.filterType.PurchaseType.length() != 0) {
                    sResult = sResult + " and a.FPurchaseType like '" +
                        filterType.PurchaseType.replaceAll("'", "''") + "%'";
                }
                //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
                if (this.filterType.securityCode != null && this.filterType.PeriodCode.length() != 0) {
                    sResult = sResult + " and a.FPeriodCode like '" +
                        filterType.PeriodCode.replaceAll("'", "''") + "%'";
                }
                //-----------------增加计息起始日类型的设置 sj modified 20081215 MS00088 --//
                if (this.filterType.sInBeginType != null &&
                    this.filterType.sInBeginType.length() != 0 
                    //edit by songjie 2011.07.08 BUG 2119 QDV4赢时胜(测试)2011年6月20日01_B
                    && !this.filterType.sInBeginType.equals("99")) {
                    sResult = sResult + " and a.FInBeginType like '" +
                        filterType.sInBeginType.replaceAll("'", "''") + "%'";
                }
                //-----------------------------------------------------------------------
            }
        } catch (Exception e) {
            throw new YssException("筛选远期品种信息设置数据出错", e);
        }
        return sResult;
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
            if(this.bOverGroup){//跨组合群。MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090813
                sHeader = this.getListView3Headers();
            }else{
                sHeader = this.getListView1Headers();
            }
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (this.bOverGroup) { //跨组合群。MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090813
                    bufShow.append(super.buildRowShowStr(rs, this.getListView3ShowCols())).
                        append(YssCons.YSS_LINESPLITMARK);
                } else {
                    bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                        append(YssCons.YSS_LINESPLITMARK);
                }
                setSecurityAttr(rs);
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
            //-----------增加计息起始日的常量设置 MS00088 -----------------------------------------------------------//
            sVocStr = vocabulary.getVoc(YssCons.YSS_PARA_PurchaseType + "," + YssCons.YSS_PURCHASE_InBeginType);
            //---------------------------------------------------------------------------------------------------//
            if(this.bOverGroup){//跨组合群。MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090813
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                    this.getListView3ShowCols() + "\r\f" + "voc" + sVocStr;
            }else{
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                    this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
            }
        } catch (Exception e) {
            throw new YssException("获取回购品种信息设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 此方法已被修改
     * 修改时间：2008年2月25号
     * 修改人：单亮
     * 原方法的功能：查询出回购品种信息设置数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select y.*,h.fassetgroupcode, h.fassetgroupname from " +
            " (select FSecurityCode,FCheckState" +
            //------增加计息起始日类型的设置 sj modified 20081215 MS00088---//
            ",FInBeginType " +
            //----------------------------------------------------------//
            " from " +
            pub.yssGetTableName("Tb_Para_Purchase") +
            //修改前的代码
            //" where FCheckState <> 2 group by FSecurityCode,FCheckState) x join" +
            //修改后的代码
            //----------------------------begin
            //跨组合群修改前的代码 fanghaoln 会报错，这里用排序 "  group by FSecurityCode,FCheckState) x join" +
            "  ) x join" + //修改后的代码
            //----------------------------end
            " (select a.*, " +
            " b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FSecurityName," +
            " f.FDepDurName as FDepDurName, g.FVocName as FPurchaseTypeName, j.FPeriodName as FperiodName " +
            //-----------------MS00088 -------
            ",k.FVocName as FInBeginTypeName " +
            //--------------------------------
            " from " + pub.yssGetTableName("Tb_Para_Purchase") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FSecurityCode,FSecurityName from " +
            pub.yssGetTableName("Tb_Para_Security") +
            ") d  on a.FSecurityCode = d.FSecurityCode " +
            " left join (select FDepDurCode,FDepDurName from " +
            pub.yssGetTableName("Tb_Para_DepositDuration") +
            " where FCheckState = 1) f on f.FDepDurCode = a.FDepDurCode " +
            " left join Tb_Fun_Vocabulary g on a.FPurchaseType = g.FVocCode and g.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_PARA_PurchaseType) +
            //-----------------------增加计息起始日的常量设置 MS00088 ------------------------------------
            " left join Tb_Fun_Vocabulary k on a.FInBeginType = k.FVocCode and k.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_PURCHASE_InBeginType) +
            //-----------------------------------------------------------------------------------------
            " left join (select FPeriodCode,FPeriodName from " +
            pub.yssGetTableName("Tb_Para_Period") +
            " where FCheckState = 1 ) j on a.FPeriodCode = j.FPeriodCode" +
            buildFilterSql() +
            ") y on x.FSecurityCode = y.FSecurityCode " +
            " and x.FInBeginType = y.FInBeginType " +
            // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
            //=====================================================================================
            " left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '" +
            pub.getPrefixTB() + "' " +
            //===============================================================================
            " order by y.FCheckState, y.FCreateTime";
        return this.builderListViewData(strSql);
    }

    public String getListViewData4() throws YssException {
        String strSql = "";
        return strSql;
    }
    /**
     * 此方法做收益计提，查询回购数据
     * @return String
     * @throws YssException
     */
    public String getListViewData2() throws YssException {
        String strSql = "";
        strSql = "select y.*,h.fassetgroupcode, h.fassetgroupname from " +
            " (select FSecurityCode,FCheckState" +
            ",FInBeginType " +
            " from " +
            pub.yssGetTableName("Tb_Para_Purchase") +
            "  ) x join" +
            " (select a.*, " +
            " b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FSecurityName," +
            " f.FDepDurName as FDepDurName, g.FVocName as FPurchaseTypeName, j.FPeriodName as FperiodName " +
            ",k.FVocName as FInBeginTypeName,tt.FTotal" +
            " from " + pub.yssGetTableName("Tb_Para_Purchase") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FSecurityCode,FSecurityName from " +
            pub.yssGetTableName("Tb_Para_Security") +
            ") d  on a.FSecurityCode = d.FSecurityCode " +
            " left join (select FDepDurCode,FDepDurName from " +
            pub.yssGetTableName("Tb_Para_DepositDuration") +
            " where FCheckState = 1) f on f.FDepDurCode = a.FDepDurCode " +
            " left join Tb_Fun_Vocabulary g on a.FPurchaseType = g.FVocCode and g.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_PARA_PurchaseType) +
            " left join Tb_Fun_Vocabulary k on a.FInBeginType = k.FVocCode and k.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_PURCHASE_InBeginType) +
            " left join (select FPeriodCode,FPeriodName from " +
            pub.yssGetTableName("Tb_Para_Period") +
            " where FCheckState = 1 ) j on a.FPeriodCode = j.FPeriodCode" +
            " left join (select sum(aa.FTotal) as FTotal,aa.FSecurityCode,aa.FPortCode " +
            " from (select FTradeMoney as FTotal,FSecurityCode,FPortCode from " +
            " TB_" + pub.getPrefixTB() + "_Data_Purchase" +
            " where FCheckState = 1 and FBargainDate between " + dbl.sqlDate(this.filterType.sStartDate) +
            " and " + dbl.sqlDate(this.filterType.sEndDate) +
            " and FPortCode in (" + this.operSql.sqlCodes(this.strPortCode.trim().length()==0?this.filterType.strPortCode:this.strPortCode) + ")" +
            " union all " +
            " select FTradeAmount as FTotal,FSecurityCode,FPortCode from " +
            " TB_" + pub.getPrefixTB() + "_data_subtrade" +
            " where FCheckState = 1 and FBargainDate between " + dbl.sqlDate(this.filterType.sStartDate) +
            " and " + dbl.sqlDate(this.filterType.sEndDate) +
            //------ modify by wangzuochun 2009.12.11 MS00860 点击回购计息进行查询，查询不出结果 赢时胜(上海)2009年12月10日01_B 
            " and FTradeTypeCode in (" + dbl.sqlString(YssOperCons.YSS_JYLX_ZRE) + "," + dbl.sqlString(YssOperCons.YSS_JYLX_NRE)+","+dbl.sqlString(YssOperCons.YSS_JYLX_REMR)+","+dbl.sqlString(YssOperCons.YSS_JYLX_REMC) + ")" +
            //----------------
            " and FPortCode in (" + this.operSql.sqlCodes(this.strPortCode.trim().length()==0?this.filterType.strPortCode:this.strPortCode) + ")" +
            " union all " +
            " select FStorageAmount as FTotal,FSecurityCode,FPortCode from " +
            " TB_" + pub.getPrefixTB() + "_stock_security" +
            " where FCheckState = 1 and FStorageDate between " + dbl.sqlDate(YssFun.addDay(YssFun.parseDate(this.filterType.sStartDate), -1)) +
            " and " + dbl.sqlDate(this.filterType.sEndDate) +
            " and FPortCode in (" + this.operSql.sqlCodes(this.strPortCode.trim().length()==0?this.filterType.strPortCode:this.strPortCode) + ")" +
            " ) aa group by aa.FSecurityCode, aa.FPortCode) tt on tt.FSecurityCode = a.fsecuritycode" +
            buildFilterSql() +
            ") y on x.FSecurityCode = y.FSecurityCode " +
            " and x.FInBeginType = y.FInBeginType " +
            " left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '" +
            pub.getPrefixTB() + "' " +
            " order by y.FCheckState, y.FCreateTime";
        return this.builderListView2Data(strSql);
    }
    /**
     * 此方法做返回收益计提查询回购计息数据
	 * author : 徐启吉 20090917
     * @param strSql String
     * @return String
     * @throws YssException
     */
    public String builderListView2Data(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            if(this.bOverGroup){//跨组合群
                sHeader = this.getListView3Headers();
            }else{
                sHeader = this.getListView1Headers();
            }
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (rs.getDouble("FTotal") > 0) {
                    if (this.bOverGroup) { //跨组合群
                        bufShow.append(super.buildRowShowStr(rs, this.getListView3ShowCols())).
                            append(YssCons.YSS_LINESPLITMARK);
                    } else {
                        bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                            append(YssCons.YSS_LINESPLITMARK);
                    }
                    setSecurityAttr(rs);
                    bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                } else {
                    continue;
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
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_PARA_PurchaseType + "," + YssCons.YSS_PURCHASE_InBeginType);
            if(this.bOverGroup){
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                    this.getListView3ShowCols() + "\r\f" + "voc" + sVocStr;
            }else{
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                    this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
            }
        } catch (Exception e) {
            throw new YssException("获取回购品种信息设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData3() throws YssException {
        String strSql = "";
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
    //modified by liubo.Story #1916
    //跨组合群的调度方案设置
    //==========================================
    public String getTreeViewData1() throws YssException{
    	String strSql = "";
    	
    	String[] sAllAssetGroup = getAllAssetGroup().split("\t");
    	
    	strSql = "select * from (";
    	for(int i = 0; i < sAllAssetGroup.length; i++)
    	{
	        strSql = strSql + " select y.*, '" + sAllAssetGroup[i] + "' as FAssetGroupCode from " +
	            " (select FSecurityCode,FCheckState" +
	            ",FInBeginType " +
	            " from " +
	            "Tb_" + sAllAssetGroup[i] + "_Para_Purchase" +
	            "  ) x join" +
	            " (select a.*, " +
	            " b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FSecurityName," +
	            " f.FDepDurName as FDepDurName, g.FVocName as FPurchaseTypeName, j.FPeriodName as FperiodName " +
	            ",k.FVocName as FInBeginTypeName " +
	            " from " + "Tb_" + sAllAssetGroup[i] + "_Para_Purchase" + " a " +
	            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
	            " left join (select FSecurityCode,FSecurityName from " +
	            "Tb_" + sAllAssetGroup[i] + "_Para_Security" +
	            ") d  on a.FSecurityCode = d.FSecurityCode " +
	            " left join (select FDepDurCode,FDepDurName from " +
	            "Tb_" + sAllAssetGroup[i] + "_Para_DepositDuration" +
	            " where FCheckState = 1) f on f.FDepDurCode = a.FDepDurCode " +
	            " left join Tb_Fun_Vocabulary g on a.FPurchaseType = g.FVocCode and g.FVocTypeCode = " +
	            dbl.sqlString(YssCons.YSS_PARA_PurchaseType) +
	            " left join Tb_Fun_Vocabulary k on a.FInBeginType = k.FVocCode and k.FVocTypeCode = " +
	            dbl.sqlString(YssCons.YSS_PURCHASE_InBeginType) +
	            " left join (select FPeriodCode,FPeriodName from " +
	            "Tb_" + sAllAssetGroup[i] + "_Para_Period" +
	            " where FCheckState = 1 ) j on a.FPeriodCode = j.FPeriodCode" +
	            buildFilterSql() +
	            ") y on x.FSecurityCode = y.FSecurityCode " +
	            " and x.FInBeginType = y.FInBeginType " +
	            " left join Tb_Sys_Assetgroup h on h.fassetgroupcode  =  '" +
	            sAllAssetGroup[i] + "' " +
	            " union";
    	}

		strSql = strSql.substring(0,strSql.length() - 5);
    	strSql = strSql + ") allData order by allData.FAssetGroupCode,allData.FCreateTime";
        
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers() + "\t组合群代码";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                    bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).append("\t" + rs.getString("FAssetGroupCode")).
                        append(YssCons.YSS_LINESPLITMARK);
                setSecurityAttr(rs);
                this.assetGroupCode = rs.getString("FAssetGroupCode");
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
            //-----------增加计息起始日的常量设置 MS00088 -----------------------------------------------------------//
            sVocStr = vocabulary.getVoc(YssCons.YSS_PARA_PurchaseType + "," + YssCons.YSS_PURCHASE_InBeginType);
            //---------------------------------------------------------------------------------------------------//
            if(this.bOverGroup){//跨组合群。MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090813
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                    this.getListView3ShowCols() + "\r\f" + "voc" + sVocStr;
            }else{
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                    this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
            }
        } catch (Exception e) {
            throw new YssException("获取回购品种信息设置出错", e);
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

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_Purchase") +
                "(FSECURITYCODE, FDepDurCode, FPurchaseRate, FDesc,FPurchaseType,FPeriodCode," +
                //------增加计息起始日类型的设置 sj modified 20081215 MS00088---//
                "FInBeginType, " +
                //----------------------------------------------------------//
                " FCHECKSTATE, FCREATOR, FCREATETIME,FCheckUser) values(" +
                dbl.sqlString(this.securityCode) + "," +
                dbl.sqlString(this.depDurCode) + "," +
                this.purchaseRate + "," +
                dbl.sqlString(this.desc) + "," +
                dbl.sqlString(this.PurchaseType) + "," +
                dbl.sqlString(this.PeriodCode) + "," +
                //------------------------------------------
                dbl.sqlString(this.sInBeginType) + "," +
                //------------------------------------------
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加回购品种信息设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_Purchase") +
                " set " +
                "  FSECURITYCODE = " + dbl.sqlString(this.securityCode) +
                ", FDepDurCode = " + dbl.sqlString(this.depDurCode) +
                ", FPurchaseRate = " + this.purchaseRate +
                ", FDesc = " + dbl.sqlString(this.desc) +
                ", FPurchaseType = " + dbl.sqlString(this.PurchaseType) +
                ", FPeriodCode = " + dbl.sqlString(this.PeriodCode) +
                //--------------添加计息起始日 sj modify MS00088 ------------------
                ",FInBeginType = " + dbl.sqlString(this.sInBeginType) +
                //---------------------------------------------------------------
                ", FCHECKSTATE = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCreator = " + dbl.sqlString(this.creatorCode) +
                ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ", FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FSECURITYCODE = " +
                //edit by licai 20101109 BUG #331 在证券信息维护页面，修改回购信息后不能保存。 
                dbl.sqlString(this.oldSecurityCode) ;
                //-------------------------------------
                //" and FInBeginType = " + dbl.sqlString(this.sOldInBeginType);
               //edit by licai 20101109 BUG #331 在证券信息维护页面，修改回购信息后不能保存。--end- 

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改回购品种信息设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    /**
     * 删除数据即放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        int Count = 0;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_Purchase") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FSECURITYCODE = " +
                dbl.sqlString(this.oldSecurityCode) +
                " and FInBeginType = " + dbl.sqlString(this.sOldInBeginType);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除回购品种信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 修改时间：2008年3月25号
     * 修改人：单亮
     * 原方法功能：只能处理期间连接的审核和未审核的单条信息。
     * 新方法功能：可以处理回购品种信息设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理回购品种信息设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        //修改前的信息
//   String strSql = "";
//   boolean bTrans = false; //代表是否开始了事务
//   Connection conn = dbl.loadConnection();
//   try {
//      strSql = "update " + pub.yssGetTableName("Tb_Para_Purchase") +
//              " set FCheckState = " + this.checkStateId +
//              ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//              ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//              "' where FSECURITYCODE = " +
//              dbl.sqlString(this.oldSecurityCode);
//        conn.setAutoCommit(false);
//        bTrans = true;
//        dbl.executeSql(strSql);
//        conn.commit();
//        bTrans = false;
//        conn.setAutoCommit(true);
//      }
//      catch (Exception e) {
//           throw new YssException("审核回购品种信息出错", e);
//      }
//        finally {
//           dbl.endTransFinal(conn, bTrans);
//        }
        //修改后的代码
        //---------------begin
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
                //edit by yangshaokai 2011.12.22 BUG 3427 QDV4赢时胜(测试)2011年12月16日03_B
            	//批量处理的数据是由 \r\n 分割的 所以由 \r\t 改为 \r\n
            	arrData = sRecycled.split("\r\n"); //edited by zhouxiang MS01333    回购信息设置界面中描述信息中包含两次回车时不能反审核    QDV4赢时胜(测试)2010年6月21日1_B    
                
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Para_Purchase") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FSECURITYCODE = " +
                        dbl.sqlString(this.oldSecurityCode);
                    //------------------------------------------------------------
                    if (this.sOldInBeginType.equalsIgnoreCase("FromSecurityInfo")) { // 判断此次的审核指令的发出是否来自证券信息设置.若是,则不判断计息起始日期的设置内容. sj modified 20081226
                        strSql += "";
                    } else {
                        strSql += " and FInBeginType = " +
                            dbl.sqlString(this.sOldInBeginType);
                    }
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而oldSecurityCode不为空，则按照oldSecurityCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (oldSecurityCode != null && !oldSecurityCode.equalsIgnoreCase("")) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_Purchase") +
                    " set FCheckState = " + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FSECURITYCODE = " +
                    dbl.sqlString(this.oldSecurityCode);
                //------------------------------------------------------------
                if (this.sOldInBeginType.equalsIgnoreCase("FromSecurityInfo")) { // 判断此次的审核指令的发出是否来自证券信息设置.若是,则不判断计息起始日期的设置内容. sj modified 20081226
                    strSql += "";
                } else {
                    strSql += " and FInBeginType = " +
                        dbl.sqlString(this.sOldInBeginType);
                }
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核回购品种信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //----------------end
    }

    public void setSecurityAttr(ResultSet rs) throws SQLException {
        this.securityCode = rs.getString("FSecurityCode");
        this.securityName = rs.getString("FSecurityName");
        this.depDurCode = rs.getString("FDepDurCode");
        this.depDurName = rs.getString("FDepDurName");
        this.purchaseRate = rs.getDouble("FPurchaseRate");

        this.desc = rs.getString("FDesc");
        this.PurchaseType = rs.getString("FPurchaseType");
        this.PurchaseTypeName = rs.getString("FPurchaseTypeName");
        this.PeriodCode = rs.getString("FPeriodCode");
        this.PeriodName = rs.getString("FPeriodName");
        //---------------添加计息起始日 sj modify MS00088 -----------//
        this.sInBeginType = rs.getString("FInBeginType");
        this.sInBeginTypeName = rs.getString("FInBeginTypeName");
        //---------------------------------------------------------//
        // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090605
        ResultSetMetaData rsmd = rs.getMetaData(); //得到结果集里的内容
        for (int i = 1; i < rsmd.getColumnCount(); i++) { //循环字段名称
            if (rsmd.getColumnName(i).equals("FASSETGROUPCODE")) { //把字段名称进行对比看是否有当前字段名称
                this.assetGroupCode = rs.getString("fassetgroupcode") + ""; //给组合群代码赋值
                this.assetGroupName = rs.getString("fassetgroupname") + ""; //给组合群名称赋值
            }
        }
        //---------------------------------------------------------------------------------------------
        super.setRecLog(rs);
    }

    public String getOperValue(String sType) {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    /**
     * 从数据库删除数据，即彻底删除数据
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
                        pub.yssGetTableName("Tb_Para_Purchase") +
                        " where FSECURITYCODE = " +
                        dbl.sqlString(this.oldSecurityCode) +
                        //------------------------------------------------------------
                        " and FInBeginType = " + dbl.sqlString(this.sOldInBeginType);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而oldSecurityCode不为空，则按照oldSecurityCode来执行sql语句
            else if (oldSecurityCode != "" && oldSecurityCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_Purchase") +
                    " where FSECURITYCODE = " +
                    dbl.sqlString(this.oldSecurityCode) +
                    //------------------------------------------------------------
                    " and FInBeginType = " + dbl.sqlString(this.sOldInBeginType);
                //执行sql语句
                dbl.executeSql(strSql);
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

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

    /// <summary>
    /// 修改人：fanghaoln
    /// 修改人时间:20090512
    /// BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
    /// 从后台加载出我们跨组合群的内容
    public String getListViewGroupData1() throws YssException {
        this.bOverGroup = true;
        String sAllGroup = ""; //定义一个字符用来保存执行后的结果传到前台
        String sPrefixTB = pub.getPrefixTB(); //保存当前的组合群代码
        //按组合群的解析符解析组合群代码
        String[] assetGroupCodes = this.filterType.assetGroupCode.split(YssCons.YSS_GROUPSPLITMARK);
        //按组合群的解析符解析组合代码
        String[] strPortCodes = this.filterType.strPortCode.split(YssCons.YSS_GROUPSPLITMARK);
        try {
            for (int i = 0; i < assetGroupCodes.length; i++) { //循环遍历每一个组合群
                this.assetGroupCode = assetGroupCodes[i]; //得到一个组合群代码
                pub.setPrefixTB(this.assetGroupCode); //修改公共变量的当前组合群代码
                this.strPortCode = strPortCodes[i]; //得到一个组合群下的组合代码
                String sGroup = this.getListViewData1(); //调用以前的执行方法
                sAllGroup = sAllGroup + sGroup + YssCons.YSS_GROUPSPLITMARK; //组合得到的结果集
            }
            if (sAllGroup.length() > 7) { //去除尾部多余的组合群解析符
                sAllGroup = sAllGroup.substring(0, sAllGroup.length() - 7);
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            pub.setPrefixTB(sPrefixTB); //还原公共变的里的组合群代码
            this.bOverGroup = false;
        }
        return sAllGroup; //把结果返回到前台进行显示

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
     * 获取回购信息代码
     * add by lvhx 2010.06.24 MS01297 计息业务的明细通过业务日期和组合动态获取 
     * QDV4赢时胜（深圳）2010年06月02日01_A  
     */
    public String getIncomeTypeData() throws YssException {
    	String strSql = "";
		ResultSet rs = null;
		StringBuffer strResult = new StringBuffer();
		try {
        strSql = "select x.FSecurityCode from " +
            " (select FSecurityCode,FCheckState" +
            ",FInBeginType " +
            " from " +
            pub.yssGetTableName("Tb_Para_Purchase") +
            " where fcheckstate = '1' "+
            "  ) x " ;
        rs = dbl.openResultSet(strSql);
        while(rs.next()){
        	strResult.append(rs.getString("FSecurityCode")).append(",");
        }
        if(strResult.length() > 1){
    		strResult.delete(strResult.length() - 1, strResult.length());
    	}
        return strResult.toString();
    } catch (Exception e) {
        throw new YssException("获取回购信息代码出错！", e);
    } finally {
        dbl.closeResultSetFinal(rs);
    }
    }
    
    
    /**
     * add add baopingping STORY #1171 20110627 
     * 根据交易代码来获取期限代码
     * @throws SQLException 
     * @throws YssException 
     */
    public String getQurcode(String qurcode) throws SQLException, YssException{
    	String strSql = "";
		ResultSet rs = null;
		String FDurUnit="";
		try {
			strSql="select  *from "+pub.yssGetTableName("Tb_Para_Purchase")+
			" a inner join "+pub.yssGetTableName("Tb_Para_DepositDuration")+
			" b on a.FDePDURCODE=b.FDePDURCODE" +
			" where a.FSECURITYCODE= '" +qurcode+"'"; 
			rs=dbl.openResultSet(strSql);
			while(rs.next())
			{
				 FDurUnit=rs.getString("FDepDurName");
				 return FDurUnit;
			}
		}catch (Exception e)
		{
			 throw new YssException("获取期限代码出错！");
		}finally
		{
		dbl.closeResultSetFinal(rs);
		}
    	return FDurUnit;
    }
    /**
     * add add baopingping STORY #1171 20110627 
     * 根据组合代码和控件名称来获取参数设置的值
     * @throws SQLException 
     */
	public String  getQurMoney(String qurcode,String purName,String name) throws YssException {
	    String sql=null;
	    String strSql=null;
    	ResultSet rs = null;
    	ResultSet rsSet=null;
        String value=null;
  		try 
  		{
  	    	sql="select *from "+ pub.yssGetTableName("Tb_Pfoper_Pubpara") +
  	    	" where FpubParaCode='ChangOrHand'"+
  	    	" and FCtlCode='selPort'"+
  	    	" and FCtlValue like '"+qurcode+"%"
  	    	+name+"'";
  	    	rs=dbl.openResultSet(sql);
  			while(rs.next())
  			{
  				strSql="select *from "+ pub.yssGetTableName("Tb_Pfoper_Pubpara") +
  				" where FpubParaCode='ChangOrHand'"+
  	  	    	" and FCtlCode= '"+purName+"'"+
  	  	    	" and FCtlValue like '%'"+
  	  	    	" and FParaId="+rs.getString("FParaId")+"";
  				rsSet=dbl.openResultSet(strSql);
  				if(rsSet.next())
  				{
	  				String FCtlValue =rsSet.getString("FCtlValue");
	  				value=FCtlValue;
	  				return value;
  				}
  				dbl.closeResultSetFinal(rsSet);
  			}
  		} catch (Exception e1) 
  		{
  			throw new YssException("查询回购结算服务费和交易手续费参数出错！");
  		}finally
  		{
  			dbl.closeResultSetFinal(rs);
  			dbl.closeResultSetFinal(rsSet);	
  		}
  		return value;  
	}
	
    /**
	 * 20111205 modified by liubo.Story #1916
	 * 查询当前库中所有组合群
	 * return ResultSet
	 * @throws YssException 
	 */
	public String getAllAssetGroup() throws YssException{
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
	
	
}
