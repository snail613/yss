package com.yss.main.storagemanage;

import java.sql.*;

import com.yss.commeach.EachGetPubPara;
import com.yss.dsub.*;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.operdata.BonusShareBean;
import com.yss.main.parasetting.*;
import com.yss.util.*;
import java.util.ArrayList;

public class SecurityStorageBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strSecurityCode = "";    //证券代码
    private String strSecurityName = "";    //证券名称
    private String strFreezeAmount = "";    //冻结数量
    private String strMStorageCost = "";    //管理成本
    private String strVStorageCost = "";    //估值成本
    private String strCuryCode = "";        //交易货币代码
    private String strCuryName = "";        //交易货币名称
    private String strFAnalysisCode1 = "";  //分析代码1
    private String strFAnalysisName1 = "";  //分析代码1名称
    private String strFAnalysisCode2 = "";  //分析代码2
    private String strFAnalysisName2 = "";  //分析代码2名称
    private String strFAnalysisCode3 = "";  //分析代码3
    private String strFAnalysisName3 = "";  //分析代码3名称
    private String strPortCode = "";        //组合代码
    private String strPortName = "";        //组合名称
    private String strPortCuryRate = "";    //组合货币汇率
    private String strBaseCuryRate = "";    //基础货币汇率
    private String strStorageAmount = "";   //库存数量
    private String strStorageCost = "";     //库存成本
    private String strOldStorageCode = "";
    private String strStorageDate = "";     //库存日期
    private String strYearMonth = "";       //库存年月
    private String strStorageStartDate = "";//库存初始日期
    private String strOldYearMonth = "";    //原始库存年月 modified libo 20090520 MS00445 修改证券库存中的记录为期初数时系统会报错 QDV4赢时胜上海2009年5月10日01_B
    private String strOldStorageStartDate = "";
    private String strOldPortCode = "";
    private String strOldFAnalysisCode1 = "";
    private String strOldFAnalysisCode2 = "";
    private String strOldFAnalysisCode3 = "";
    private String strOldStorageDate = "";

    private SecurityStorageBean filterType;
//    private String strIsOnlyColumn = "0";

    private String strPortCuryCost = "";    //组合货币核算成本
    private String strMPortCuryCost = "";   //组合货币管理成本
    private String strVPortCuryCost = "";   //组合货币核算成本
    private String strBaseCuryCost = "";    //基础货币核算成本
    private String strMBaseCuryCost = "";   //基础货币管理成本
    private String strVBaseCuryCost = "";   //基础货币估值成本
    private int intStorageState = 2;        //库存状态

    private String attrCode = "";   //属性代码
    private String attrName = "";   //属性代码名称
    private String catType = "";    //品种类型
    private String catTypeName = "";//品种类型名称

    private String oldAttrCode = "";
    private String oldCatType = "";

    private double bailMoney;

    private double effectiveRate;   //实际利率 2009.09.03 蒋锦 添加 MS00656 QDV4赢时胜(上海)2009年8月24日01_A

    private String sRecycled = "";

    private String flag = "";
    private String sSecRecPayBal = "";

    private String investType = ""; //投资类型 2009-08-14 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
    private String oldInvestType="";//保存旧的投资类型

    private String bBegin; //是否取期初数

    private SecurityBean securityBean = null; //用于储存证券的所有有效信息 MS00006 QDV4.1赢时胜上海2009年2月1日05_A add by songjie 2009.04.20

    //STORY #863 香港、美国股指期权交易区别  期权需求变更   add by jiangshichao 2011.06.20 start 
    EachGetPubPara pubPara = null;
    String sCostAccount_Para="";//期权是否核算成本参数
    //STORY #863 香港、美国股指期权交易区别  期权需求变更 end 
    
    public SecurityBean getSecurityBean() {
        return securityBean;
    }

    public void setSecurityBean(SecurityBean securityBean) {
        this.securityBean = securityBean;
    }
	//=====by xuxuming,20090916.MS00700,债券应收和转货基本数据要相应，不用重新输入.QDV4中保2009年09月15日02_B=================
    public void setFilterType(SecurityStorageBean filterType){
       this.filterType = filterType;
    }
//    public void setStrIsOnlyColumn(String strIsOnlyColumn){
//       this.strIsOnlyColumn = strIsOnlyColumn;
//    }
//   //=================================================================================================================
    public String getStrStorageCost() {
        return this.strStorageCost;
    }

    public void setStrStorageCost(String strStorageCost) {
        this.strStorageCost = strStorageCost;
    }

    public String getVStorageCost() {
        return strVStorageCost;
    }

    public void setFVStorageCost(String strVStorageCost) {
        this.strVStorageCost = strVStorageCost;
    }

    public SecurityStorageBean() {
    }

    public String getStrBaseCuryRate() {
        return this.strBaseCuryRate;
    }

    public void setStrBaseCuryRate(String strBaseCuryRate) {
        this.strBaseCuryRate = strBaseCuryRate;
    }

    public String getInvestType() {
        return investType;
    }

    public void setInvestType(String investType) {
        this.investType = investType;
    }

    public SecurityStorageBean getFilterType() {
        return this.filterType;
    }
    

//    public String getStrIsOnlyColumn() {
//        return this.strIsOnlyColumn;
//    }
    

    public String getStrCuryCode() {
        return this.strCuryCode;
    }

    public void setStrCuryCode(String strCuryCode) {
        this.strCuryCode = strCuryCode;
    }

    public String getStrCuryName() {
        return this.strCuryName;
    }

    public void setStrCuryName(String strCuryName) {
        this.strCuryName = strCuryName;
    }

    public String getStrFAnalysisCode1() {
        return this.strFAnalysisCode1;
    }

    public void setStrFAnalysisCode1(String strFAnalysisCode1) {
        this.strFAnalysisCode1 = strFAnalysisCode1;
    }

    public String getStrFAnalysisCode2() {
        return this.strFAnalysisCode2;
    }

    public void setStrFAnalysisCode2(String strFAnalysisCode2) {
        this.strFAnalysisCode2 = strFAnalysisCode2;
    }

    public String getStrFAnalysisCode3() {
        return this.strFAnalysisCode3;
    }

    public void setStrFAnalysisCode3(String strFAnalysisCode3) {
        this.strFAnalysisCode3 = strFAnalysisCode3;
    }

    public String getStrFAnalysisName1() {
        return this.strFAnalysisName1;
    }

    public void setStrFAnalysisName1(String strFAnalysisName1) {
        this.strFAnalysisName1 = strFAnalysisName1;
    }

    public String getStrFAnalysisName2() {
        return this.strFAnalysisName2;
    }

    public void setStrFAnalysisName2(String strFAnalysisName2) {
        this.strFAnalysisName2 = strFAnalysisName2;
    }

    public String getStrFAnalysisName3() {
        return this.strFAnalysisName3;
    }

    public void setStrFAnalysisName3(String strFAnalysisName3) {
        this.strFAnalysisName3 = strFAnalysisName3;
    }

    public String getStrFreezeAmount() {
        return this.strFreezeAmount;
    }

    public void setStrFreezeAmount(String strFreezeAmount) {
        this.strFreezeAmount = strFreezeAmount;
    }

    public String getStrMStorageCost() {
        return this.strMStorageCost;
    }

    public void setStrMStorageCost(String strMStorageCost) {
        this.strMStorageCost = strMStorageCost;
    }

    public String getStrVStorageCost() {
        return this.strVStorageCost;
    }

    public String getStrPortCode() {
        return this.strPortCode;
    }

    public void setStrPortCode(String strPortCode) {
        this.strPortCode = strPortCode;
    }

    public String getStrPortCuryRate() {
        return this.strPortCuryRate;
    }

    public void setStrPortCuryRate(String strPortCuryRate) {
        this.strPortCuryRate = strPortCuryRate;
    }

    public String getStrPortName() {
        return this.strPortName;
    }

    public void setStrPortName(String strPortName) {
        this.strPortName = strPortName;
    }

    public String getStrSecurityCode() {
        return this.strSecurityCode;
    }

    public void setStrSecurityCode(String strSecurityCode) {
        this.strSecurityCode = strSecurityCode;
    }

    public String getStrSecurityName() {
        return this.strSecurityName;
    }

    public void setStrSecurityName(String strSecurityName) {
        this.strSecurityName = strSecurityName;
    }

    public String getStrStorageAmount() {
        return this.strStorageAmount;
    }

    public void setStrStorageAmount(String strStorageAmount) {
        this.strStorageAmount = strStorageAmount;
    }

    public String getVStrStorageCost() {
        return this.strVStorageCost;
    }

    public void setStrVStorageCost(String strVStorageCost) {
        this.strVStorageCost = strVStorageCost;
    }

    public String getStrStorageDate() {
        return this.strStorageDate;
    }

    public void setStrStorageDate(String strStorageDate) {
        this.strStorageDate = strStorageDate;
    }

    public String getStrStorageStartDate() {
        return this.strStorageStartDate;
    }

    public void setStrStorageStartDate(String strStorageStartDate) {
        this.strStorageStartDate = strStorageStartDate;
    }

    /**
     * addOperData
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        SecRecPayBalBean secRecPayBal = new SecRecPayBalBean();
        secRecPayBal.setYssPub(pub);
        try {
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Stock_Security") +
                "(FSecurityCode,FYearMonth,FStorageDate, FPortCode,FCuryCode,FStorageAmount,FStorageCost, FFreezeAmount," +
                " FMStorageCost,FVStorageCost,FPortCuryRate,FPortCuryCost,FMPortCuryCost,FVPortCuryCost,FBaseCuryRate,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FStorageInd,FCatType,FAttrClsCode," +
                " FCheckState, FCreator, FCreateTime,FCheckUser,FInvestType) " + // 添加字段FInvestType, modify by wangzuochun 2009.08.14 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                " values(" + dbl.sqlString(this.strSecurityCode) + "," +
                (this.bBegin.equalsIgnoreCase("false") ?
                 dbl.sqlString(YssFun.formatDate(this.strStorageDate, "yyyyMM")) : //lzp modify
                 dbl.sqlString(this.strYearMonth)) + " , " +
                dbl.sqlDate(this.strStorageDate) + "," +
                dbl.sqlString(this.strPortCode) + "," +
                dbl.sqlString(this.strCuryCode) + "," +
                // ----------lzp  modify 2007 12.12
                this.strStorageAmount + "," +
                this.strStorageCost + "," +
                this.strFreezeAmount + "," +

                this.strMStorageCost + "," +
                this.strVStorageCost + "," +

                this.strPortCuryRate + "," + //改了
                this.strPortCuryCost + "," +
                this.strMPortCuryCost + "," +
                this.strVPortCuryCost + "," +
                this.strBaseCuryRate + "," +
                this.strBaseCuryCost + "," +
                this.strMBaseCuryCost + "," +
                this.strVBaseCuryCost + "," +
                //-----------------
                dbl.sqlString(this.strFAnalysisCode1.length() == 0 ? " " :
                              this.strFAnalysisCode1) + "," +
                dbl.sqlString(this.strFAnalysisCode2.length() == 0 ? " " :
                              this.strFAnalysisCode2) + "," +
                dbl.sqlString(this.strFAnalysisCode3.length() == 0 ? " " :
                              this.strFAnalysisCode3) + ",2," + //2不能带单引号  lzp  modify 20080123
                (this.catType.length() > 0 ? dbl.sqlString(this.catType) :
                 dbl.sqlString(" ")) + "," +
                (this.attrCode.length() > 0 ? dbl.sqlString(this.attrCode) :
                 dbl.sqlString(" ")) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) +
                ", " + dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                //------ add by wangzuochun 2009.08.14 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -------//
                "," +
                dbl.sqlString(this.investType) + //投资类型
                //--------------------------------------------------------------------------------------------------//
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            if (this.sSecRecPayBal != null &&
                this.sSecRecPayBal.trim().length() != 0) {
                SecRecPayBalBean secpaybal = new SecRecPayBalBean();
                secpaybal.setYssPub(pub);
                secpaybal.saveMutliStorageData(this.sSecRecPayBal, true, this);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            /**shashijie 2012-7-2 STORY 2475 */
            SecRecPayBalBean filType = new SecRecPayBalBean();
            filType.setSYearMonth(this.strYearMonth);
            filType.setDtStorageDate(YssFun.toDate(this.strStorageDate));
            filType.setSPortCode(this.strPortCode);
            filType.setSAnalysisCode1(this.strFAnalysisCode1);
            filType.setSAnalysisCode2(this.strFAnalysisCode2);
            filType.setSAnalysisCode3(this.strFAnalysisCode3);
            filType.setSSecurityCode(this.strSecurityCode);
            filType.setBBegin("false"); //------------------------
            secRecPayBal.setFilterType(filType);
            filType = secRecPayBal.getFilterType();
            /**end*/
            this.setASubData(secRecPayBal.getListViewData1());

            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增库存设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strSecurityCode).append("\t");
        buf.append(this.strSecurityName).append("\t");
        buf.append(this.strFreezeAmount).append("\t");
        buf.append(this.strStorageStartDate).append("\t");
        buf.append(this.strFAnalysisCode1).append("\t");
        buf.append(this.strFAnalysisName1).append("\t");
        buf.append(this.strFAnalysisCode2).append("\t");
        buf.append(this.strFAnalysisName2).append("\t");
        buf.append(this.strFAnalysisCode3).append("\t");
        buf.append(this.strFAnalysisName3).append("\t");
        buf.append(this.strPortCode).append("\t");
        buf.append(this.strPortName).append("\t");
        buf.append(this.strStorageAmount).append("\t");
        buf.append(this.strStorageCost).append("\t");
        buf.append(this.strMStorageCost).append("\t");
        //加入的估值成本
        buf.append(this.strVStorageCost).append("\t");

        buf.append(this.strPortCuryRate).append("\t");
        //加入组合货币的核算成本
        buf.append(this.strPortCuryCost).append("\t");
        buf.append(this.strMPortCuryCost).append("\t");
        buf.append(this.strVPortCuryCost).append("\t");

        //加入基础货币的核算成本
        buf.append(this.strBaseCuryRate).append("\t");
        buf.append(this.strBaseCuryCost).append("\t");
        buf.append(this.strMBaseCuryCost).append("\t");
        buf.append(this.strVBaseCuryCost).append("\t");

        buf.append(this.strCuryCode).append("\t");
        buf.append(this.strCuryName).append("\t");
        buf.append(this.strStorageDate).append("\t");
        buf.append(this.strYearMonth).append("\t");
        buf.append(this.bBegin).append("\t");
        buf.append(this.catType).append("\t");
        buf.append(this.catTypeName).append("\t");
        buf.append(this.attrCode).append("\t");
        buf.append(this.attrName).append("\t");
        //------ 添加投资类型 add by wangzuochun 2009.08.14 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -------//
        buf.append(this.investType).append("\t");
        //-------------------------------------------------------------------------------------------------------//
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * checkInput
     * 数据验证
     * @version 20090617 modify by sunkey
     * @bugNO MS00445 QDV4赢时胜上海2009年5月10日01_B
     * @desc  调整调用checkInputCommon方法时OldValue的值
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {

        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Stock_Security"),
                               //----- modify by wangzuochun 2009.08.22 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
                               "FSecurityCode,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FStorageDate,FAttrClsCode,FYearMonth,FInvestType",
                               //-------------------------------------------------------------------------------------------------------------//
                               this.strSecurityCode + "," + this.strPortCode + "," +
                               this.strFAnalysisCode1 + "," +
                               this.strFAnalysisCode2 + "," +
                               this.strFAnalysisCode3 + "," +
                               this.strStorageDate + "," + this.attrCode + "," + //--MS00338 add by songjie 2009.03.26
                               (this.strYearMonth.length() == 0 ?
                                (this.bBegin.equalsIgnoreCase("false") ? YssFun.formatDate(strStorageDate, "yyyyMM") : strStorageDate.substring(0, 4) + "00") : strYearMonth)
                                //add by yangheng MS01603 QDV4赢时胜(测试)2010年08月12日04_B  复制证券库存出错 2010.09.09
                                +","
                                //-------------
                                +this.investType, //--MS00338 add by songjie 2009.03.26

                               this.strOldStorageCode + "," + this.strOldPortCode + "," +
                               this.strOldFAnalysisCode1 + "," +
                               this.strOldFAnalysisCode2 + "," +
                               this.strOldFAnalysisCode3 + "," +
                               this.strOldStorageDate + "," + this.oldAttrCode + "," +
                               //对原始的YearMonth判断，如果存在原始的YearMonth，直接取，否则取老的库存日期的年月
                               (this.strOldYearMonth.length() == 0 ? YssFun.formatDate(strOldStorageDate, "yyyyMM") : strOldYearMonth) + "," + this.oldInvestType); //----- modify by wangzuochun 2009.08.22 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
    }

    /**
     * checkSetting
     * 功能：可以处理库存管理业务中证券库存的设置审核、反审核、和回收站的还原功能。
     *      还原功能可以同时处理多条信息
     * @throws YssException
     * 修改时间：2008年6月6号
     * 修改人：蒋春
     */
    public void checkSetting() throws YssException {

        Connection conn = dbl.loadConnection(); //获取一个连接
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        String[] arrData = null;
        SecRecPayBalBean secRecPayBal = new SecRecPayBalBean();
        secRecPayBal.setYssPub(pub);
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (this.sRecycled != null && this.sRecycled.trim().length() > 0) {
                arrData = this.sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Stock_Security") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FSecurityCode = " +
                        dbl.sqlString(this.strSecurityCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate) +
                        " and FAttrClsCode = " + dbl.sqlString(this.attrCode) +
                        " and FInvestType = " + dbl.sqlString(this.investType); //modify by fangjiang 2011.07.25 story 1156
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    //================================================fanghaoln end=========================================
                    //------ modify by wangzuochun 2010.07.08 MS01412 证券库存中两笔数据选中其中一条数据，点击反审核时，另外一条也反审核了 
                    if (this.strFAnalysisCode1 != null &&
                        this.strFAnalysisCode1.length() > 0) {
                        strSql = strSql + " and FAnalysisCode1 =" +
                            dbl.sqlString(this.strFAnalysisCode1);
                    }
                    if (this.strFAnalysisCode2 != null &&
                        this.strFAnalysisCode2.length() > 0) {
                        strSql = strSql + " and FAnalysisCode2 =" +
                            dbl.sqlString(this.strFAnalysisCode2);
                    }
                    if (this.strFAnalysisCode3 != null &&
                        this.strFAnalysisCode3.length() > 0) {
                        strSql = strSql + " and FAnalysisCode3 =" +
                            dbl.sqlString(this.strFAnalysisCode3);
                    }
                    //------------------MS01412--------------------//
                    dbl.executeSql(strSql);

                    strSql = "update " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FSecurityCode = " +
                        dbl.sqlString(this.strSecurityCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate) +
                        " and FAttrClsCode = " + dbl.sqlString(this.attrCode) +
                        " and FInvestType = " + dbl.sqlString(this.investType); //modify by fangjiang 2011.07.25 story 1156
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    //================================================fanghaoln end=========================================

                    if (this.strFAnalysisCode1 != null &&
                        this.strFAnalysisCode1.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode1 =" +
                            dbl.sqlString(this.strFAnalysisCode1.trim());
                    }
                    if (this.strFAnalysisCode2 != null &&
                        this.strFAnalysisCode2.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode2 =" +
                            dbl.sqlString(this.strFAnalysisCode2.trim());
                    }
                    if (this.strFAnalysisCode3 != null &&
                        this.strFAnalysisCode3.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode3 =" +
                            dbl.sqlString(this.strFAnalysisCode3.trim());
                    }

                    dbl.executeSql(strSql);
                }
            } else {
                if (this.strSecurityCode != null &&
                    this.strSecurityCode.trim().length() > 0) {
                    strSql = "update " + pub.yssGetTableName("Tb_Stock_Security") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FSecurityCode = " +
                        dbl.sqlString(this.strSecurityCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate) +
                        " and FAttrClsCode = " + dbl.sqlString(this.attrCode) +
                        " and FInvestType = " + dbl.sqlString(this.investType); //modify by fangjiang 2011.07.25 story 1156
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    //================================================fanghaoln end=========================================
                    if (this.strFAnalysisCode1 != null &&
                        this.strFAnalysisCode1.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode1 =" +
                            dbl.sqlString(this.strFAnalysisCode1.trim());
                    }
                    if (this.strFAnalysisCode2 != null &&
                        this.strFAnalysisCode2.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode2 =" +
                            dbl.sqlString(this.strFAnalysisCode2.trim());
                    }
                    if (this.strFAnalysisCode3 != null &&
                        this.strFAnalysisCode3.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode3 =" +
                            dbl.sqlString(this.strFAnalysisCode3.trim());
                    }

                    dbl.executeSql(strSql);

                    strSql = "update " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FSecurityCode = " +
                        dbl.sqlString(this.strSecurityCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate) +
                        " and FAttrClsCode = " + dbl.sqlString(this.attrCode) +
                        " and FInvestType = " + dbl.sqlString(this.investType); //modify by fangjiang 2011.07.25 story 1156
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    //================================================fanghaoln end=========================================

                    if (this.strFAnalysisCode1 != null &&
                        this.strFAnalysisCode1.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode1 =" +
                            dbl.sqlString(this.strFAnalysisCode1.trim());
                    }
                    if (this.strFAnalysisCode2 != null &&
                        this.strFAnalysisCode2.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode2 =" +
                            dbl.sqlString(this.strFAnalysisCode2.trim());
                    }
                    if (this.strFAnalysisCode3 != null &&
                        this.strFAnalysisCode3.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode3 =" +
                            dbl.sqlString(this.strFAnalysisCode3.trim());
                    }

                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            //--------------------------------------------------
            SecRecPayBalBean filterType = new SecRecPayBalBean();
            filterType.setSYearMonth(this.strYearMonth);
            filterType.setDtStorageDate(YssFun.toDate(this.strStorageDate));
            filterType.setSPortCode(this.strPortCode);
            filterType.setSAnalysisCode1(this.strFAnalysisCode1);
            filterType.setSAnalysisCode2(this.strFAnalysisCode2);
            filterType.setSAnalysisCode3(this.strFAnalysisCode3);
            filterType.setSSecurityCode(this.strSecurityCode);
            filterType.setBBegin("false");
            secRecPayBal.setFilterType(filterType);
            filterType = secRecPayBal.getFilterType();
            this.setASubData(secRecPayBal.getListViewData1());
            //----------------------------------------------------
        } catch (Exception e) {
            throw new YssException("审核库存设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * delOperData
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        SecRecPayBalBean secRecPayBal = new SecRecPayBalBean();
        secRecPayBal.setYssPub(pub);
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Stock_Security") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FSecurityCode = " + dbl.sqlString(this.strSecurityCode) +
                " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                " and FStorageDate=" + dbl.sqlDate(this.strStorageDate)
                + " and FAttrClsCode = " + dbl.sqlString(this.attrCode) +
                " and FInvestType = " + dbl.sqlString(this.investType); //modify by fangjiang 2011.07.25 story 1156
            if (this.bBegin.equalsIgnoreCase("true")) {
                strSql = strSql + " and FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(this.strStorageDate, "yyyy") +
                                  "00");
            } else {
                strSql = strSql + " and FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(this.strStorageDate, "yyyyMM"));
            }
            //------ modify by wangzuochun 2010.07.08 MS01412 证券库存中两笔数据选中其中一条数据，点击反审核时，另外一条也反审核了 
            if (this.strFAnalysisCode1 != null &&
                this.strFAnalysisCode1.length() > 0) {
                strSql = strSql + " and FAnalysisCode1 =" +
                    dbl.sqlString(this.strFAnalysisCode1);
            }
            if (this.strFAnalysisCode2 != null &&
                this.strFAnalysisCode2.length() > 0) {
                strSql = strSql + " and FAnalysisCode2 =" +
                    dbl.sqlString(this.strFAnalysisCode2);
            }
            if (this.strFAnalysisCode3 != null &&
                this.strFAnalysisCode3.length() > 0) {
                strSql = strSql + " and FAnalysisCode3 =" +
                    dbl.sqlString(this.strFAnalysisCode3);
            }
            //------------------MS01412------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            strSql = "update " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FSecurityCode = " + dbl.sqlString(this.strSecurityCode) +
                " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                " and FStorageDate=" + dbl.sqlDate(this.strStorageDate) +
                " and FAttrClsCode = " + dbl.sqlString(this.attrCode);
            if (this.bBegin.equalsIgnoreCase("true")) {
                strSql = strSql + " and FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(this.strStorageDate, "yyyy") +
                                  "00");
            } else {
                strSql = strSql + " and FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(this.strStorageDate, "yyyyMM"));
            }

            if (this.strFAnalysisCode1 != null &&
                this.strFAnalysisCode1.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode1 =" +
                    dbl.sqlString(this.strFAnalysisCode1.trim());
            }
            if (this.strFAnalysisCode2 != null &&
                this.strFAnalysisCode2.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode2 =" +
                    dbl.sqlString(this.strFAnalysisCode2.trim());
            }
            if (this.strFAnalysisCode3 != null &&
                this.strFAnalysisCode3.trim().length() > 0) {
                strSql = strSql + " and FAnalysisCode3 =" +
                    dbl.sqlString(this.strFAnalysisCode3.trim());
            }
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //--------------------------------------------------
            /**shashijie 2012-7-2 STORY 2475 */
            SecRecPayBalBean filType = new SecRecPayBalBean();
            filType.setSYearMonth(this.strYearMonth);
            filType.setDtStorageDate(YssFun.toDate(this.strStorageDate));
            filType.setSPortCode(this.strPortCode);
            filType.setSAnalysisCode1(this.strFAnalysisCode1);
            filType.setSAnalysisCode2(this.strFAnalysisCode2);
            filType.setSAnalysisCode3(this.strFAnalysisCode3);
            filType.setSSecurityCode(this.strSecurityCode);
            filType.setBBegin("false"); //------------------------
            secRecPayBal.setFilterType(filType);
            filType = secRecPayBal.getFilterType();
            /**end*/
            this.setASubData(secRecPayBal.getListViewData1());
            //-----------------------------------------------------------

        } catch (Exception e) {
            throw new YssException("删除库存设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * editOperData
     *
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        SecRecPayBalBean secRecPayBal = new SecRecPayBalBean();
        secRecPayBal.setYssPub(pub);
        try {
            //--------------------------------------------------
            SecRecPayBalBean filterType2 = new SecRecPayBalBean();
            filterType2.setSYearMonth(this.strYearMonth);
            filterType2.setDtStorageDate(YssFun.toDate(this.strStorageDate));
            filterType2.setSPortCode(this.strPortCode);
            filterType2.setSAnalysisCode1(this.strFAnalysisCode1);
            filterType2.setSAnalysisCode2(this.strFAnalysisCode2);
            filterType2.setSAnalysisCode3(this.strFAnalysisCode3);
            filterType2.setSSecurityCode(this.strSecurityCode);
            filterType2.setBBegin("false"); //------------------------
            secRecPayBal.setFilterType(filterType2);
            filterType2 = secRecPayBal.getFilterType();
            this.setBSubData(secRecPayBal.getListViewData1());
            //-----------------------------------------------------------

            strSql = "update " + pub.yssGetTableName("Tb_Stock_Security") +
                " set FSecurityCode = "
                + dbl.sqlString(this.strSecurityCode) + ", FStorageDate = "
                + dbl.sqlDate(this.strStorageDate) + ",FPortCode = "
                + dbl.sqlString(this.strPortCode) + ", FCuryCode = "
                + dbl.sqlString(this.strCuryCode) + ", FStorageAmount ="
                //------------- lzp 2007 12.12  modify
                + this.strStorageAmount + ", FStorageCost="
                + this.strStorageCost + ",FMStorageCost = " +
                this.strMStorageCost + ",FVStorageCost = " +
                this.strVStorageCost + ", FPortCuryCost = " +
                //加入组合货币的核算成本
                this.strPortCuryCost + ", FMPortCuryCost = " +
                this.strMPortCuryCost + ", FVPortCuryCost = " +
                this.strVPortCuryCost + ", FPortCuryRate = " +

                this.strPortCuryRate + ", FBaseCuryCost= " +
                this.strBaseCuryCost + ", FMBaseCuryCost = " +
                this.strMBaseCuryCost + ", FVBaseCuryCost = " +
                this.strVBaseCuryCost + ", FBaseCuryRate = " +

                this.strBaseCuryRate + ", FAnalysisCode1 = " +
                //----------------------------------------------------
                dbl.sqlString(this.strFAnalysisCode1.length() == 0 ? " " :
                              this.strFAnalysisCode1) +
                ", FAnalysisCode2="
                +
                dbl.sqlString(this.strFAnalysisCode2.length() == 0 ? " " :
                              this.strFAnalysisCode2) +
                ", FAnalysisCode3="
                +
                dbl.sqlString(this.strFAnalysisCode3.length() == 0 ? " " :
                              this.strFAnalysisCode3) +
                ", FFreezeAmount="
                + this.strFreezeAmount + ",FCatType = "
                +
                (this.catType.length() > 0 ? dbl.sqlString(this.catType) :
                 dbl.sqlString(" ")) + ",FAttrClsCode = "
                +
                (this.attrCode.length() > 0 ? dbl.sqlString(this.attrCode) :
                 dbl.sqlString(" ")) + ",FCreator = "
                + dbl.sqlString(this.creatorCode) + " , FCreateTime = "
                + dbl.sqlString(this.creatorTime) +
                ",FYearMonth = " + dbl.sqlString(this.strYearMonth) +
                //------ add by wangzuochun 2009.08.14 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -------//
                ",FInvestType = " + dbl.sqlString(this.investType) +
                //-------------------------------------------------------------------------------------------------------//
                " where FSecurityCode = " + dbl.sqlString(this.strOldStorageCode) +
                " and FPortCode=" + dbl.sqlString(this.strOldPortCode) +
                " and FStorageDate=" + dbl.sqlDate(this.strOldStorageDate) +
                " and FAnalysisCode1=" + dbl.sqlString(this.strOldFAnalysisCode1) +
                " and FAnalysisCode2=" + dbl.sqlString(this.strOldFAnalysisCode2) +
                " and FAnalysisCode3=" + dbl.sqlString(this.strOldFAnalysisCode3)+
            	" and FAttrclsCode=" + dbl.sqlString(this.oldAttrCode) + //fanghaoln 20100505 MS01125 QDV4赢时胜上海2010年04月27日01_AB
            	" and FInvestType = " + dbl.sqlString(this.oldInvestType); //modify by fangjiang 2011.07.25 story 1156
            //原来的判断无法满足更新普通库存为期初库存，或将期初库存更新为普通库存的需要，重新修改为使用原始YearMonth判断
            //modified libo 20090520 MS00445 修改证券库存中的记录为期初数时系统会报错 QDV4赢时胜上海2009年5月10日01_B
            strSql = strSql + " and FYearMonth = " + dbl.sqlString(this.strOldYearMonth);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            if (this.sSecRecPayBal != null &&
                this.sSecRecPayBal.trim().length() != 0) {
                SecRecPayBalBean secpaybal = new SecRecPayBalBean();
                secpaybal.setYssPub(pub);
                secpaybal.saveMutliStorageData(this.sSecRecPayBal, true, this);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            /**shashijie 2012-7-2 STORY 2475 */
            SecRecPayBalBean filType = new SecRecPayBalBean();
            filType.setSYearMonth(this.strYearMonth);
            filType.setDtStorageDate(YssFun.toDate(this.strStorageDate));
            filType.setSPortCode(this.strPortCode);
            filType.setSAnalysisCode1(this.strFAnalysisCode1);
            filType.setSAnalysisCode2(this.strFAnalysisCode2);
            filType.setSAnalysisCode3(this.strFAnalysisCode3);
            filType.setSSecurityCode(this.strSecurityCode);
            filType.setBBegin("false");
            secRecPayBal.setFilterType(filType);
            filType = secRecPayBal.getFilterType();
            /**end*/
            this.setASubData(secRecPayBal.getListViewData1());

            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改库存设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
      //edit by rujiangpeng 20100426 MS01066 QDV4赢时胜上海2010年04月02日02_AB
        if (this.filterType != null) {
            if (this.filterType.bBegin.equalsIgnoreCase("false")) {
//            	if(!this.filterType.strStorageDate.equalsIgnoreCase("9998-12-31"))
//            	{
            		sResult = " where 1=1 and " + dbl.sqlRight("FYearMonth", 2) +
                    " <> '00'";
//            	}                
            } else{
            	if(!this.filterType.strStorageDate.equalsIgnoreCase("9998-12-31"))
            	{
            		 sResult = " where 1=1 and a.FYearMonth = '" +
                     this.filterType.strStorageDate.substring(0, 4) + "00'";
                 this.filterType.strStorageDate = this.filterType.strStorageDate; //徐启吉 2009 04 02 MS00341  新建证券库存数据期初数后保存日期建议修改为实际日期 原因：查询日期为库存日期
            	}else{
            		sResult = " where 1=1 and a.FYearMonth like '____00'";
            	}
               
            }
            
            if (this.filterType.isOnlyColumns.equals("0")) {
                sResult = sResult + " and 1 = 2";
                return sResult;
            }

            if (this.filterType.strPortCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.strPortCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strSecurityCode.length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.strSecurityCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strFreezeAmount.length() != 0) {
                sResult = sResult + " and a.FFreezeAmount =" +
                    filterType.strFreezeAmount;
            }
            if (this.filterType.strVStorageCost.length() != 0) {
                sResult = sResult + " and a.FVStorageCost =" +
                    filterType.strVStorageCost;
            }

            if (this.filterType.strMStorageCost.length() != 0) {
                sResult = sResult + " and a.FMStorageCost = " +
                    filterType.strMStorageCost;
            }
            if (this.filterType.strPortCuryRate.length() != 0) {
                sResult = sResult + " and a.FPortCuryRate = " +
                    filterType.strPortCuryRate;
            }
            if (this.filterType.strBaseCuryRate.length() != 0) {
                sResult = sResult + " and a.FBaseCuryRate = " +
                    filterType.strBaseCuryRate;
            }
            if (this.filterType.strStorageAmount.length() != 0) {
                sResult = sResult + " and a.FStorageAmount = " +
                    filterType.strStorageAmount;
            }
            if (this.filterType.strStorageCost.length() != 0) {
                sResult = sResult + " and a.FStorageCost = " +
                    filterType.strStorageCost;
            }
            if (this.filterType.strCuryCode.length() != 0) {
                sResult = sResult + " and a.FCuryCode like '" +
                    filterType.strCuryCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strFAnalysisCode1.length() != 0) {
                sResult = sResult + " and a.FAnalysisCode1 like '" +
                    filterType.strFAnalysisCode1.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strFAnalysisCode2.length() != 0) {
                sResult = sResult + " and a.FAnalysisCode2 like '" +
                    filterType.strFAnalysisCode2.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strFAnalysisCode3.length() != 0 &&
                !this.filterType.strFAnalysisCode3.equals("0")) {
                sResult = sResult + " and a.FAnalysisCode3 like '" +
                    filterType.strFAnalysisCode3.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strStorageDate.length() != 0 &&
                !this.filterType.strStorageDate.equals("9998-12-31")) {
                //-------------by xuqiji 20090505 :QDV4中保2009年05月04日01_B  MS00430    证券库存查询期初数条件进行调整 ------------//
                //根据查询内容是否为期初数来确定是否使用日期筛选，期初数不适用日期进行筛选
                if ("false".equalsIgnoreCase(this.filterType.bBegin)) {
                    sResult = sResult + " and a.FStorageDate = " +
                        dbl.sqlDate(filterType.strStorageDate);
                }
                //----------------------------end xuqiji 20090505--------------------------------------------------------------//
            }
            if (this.filterType.catType.length() != 0) {
                sResult = sResult + " and a.FCatType like '" +
                    filterType.catType.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.attrCode.length() != 0) {
                sResult = sResult + " and a.FAttrClsCode like '" +
                    filterType.attrCode.replaceAll("'", "''") + "%'";
            }

        }
        System.out.println("sql语句的条件：" + sResult);
        return sResult;
    }

    /**
     * 获取辅助字段之查询Sql语句
     * @return String
     */
    public String storageAnalysis() throws YssException, SQLException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
            pub.yssGetTableName("Tb_Para_StorageCfg") +
            " where FCheckState = 1 and FStorageType = " +
            dbl.sqlString(YssOperCons.YSS_KCLX_Security);
        rs = dbl.openResultSet(strSql);
        if (rs.next()) {
            for (int i = 1; i <= 3; i++) {
                if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                    rs.getString("FAnalysisCode" + String.valueOf(i)).
                    equalsIgnoreCase("002")) {
                    sResult = sResult +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                 
                    
                    " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" +
                    i +
                    " from " +
                    pub.yssGetTableName("tb_para_broker") +
                    " y where y.FCheckState = 1 ) broker on a.FAnalysisCode" +
                    i + " = broker.FBrokerCode";
                    
                    
                    //end by lidaolong
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("003")) {
                    sResult = sResult +
                        " left join (select m.FExchangeCode ,m.FExchangeName  as FAnalysisName" +
                        i +
                        " from  (select FExchangeCode from tb_base_exchange " +

                        " where  FCheckState = 1 group by FExchangeCode )x " +
                        " join (select * from tb_base_exchange " +
                        ") m on x.FExchangeCode = m.FExchangeCode) exchange on a.FAnalysisCode" +
                        i + " = exchange.FExchangeCode";

                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("001")) {
                    sResult = sResult +

                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
               
                    
                    " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                    i +
                    "  from   " +
                    pub.yssGetTableName("tb_para_investmanager") +
                    " n where n.FCheckState = 1) invmgr on a.FAnalysisCode" +
                    i + " = invmgr.FInvMgrCode ";
                    
                    //end by lidaolong
                } else {
                    sResult = sResult +
                        " left join (select '' as FAnalysisNull , '' as FAnalysisName" +
                        i + " from  " +
                        pub.yssGetTableName("Tb_Para_StorageCfg") +
                        " where 1=2) tn" + i + " on a.FAnalysisCode" + i + " = tn" +
                        i + ".FAnalysisNull ";
                }
            }
        }

        //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        dbl.closeResultSetFinal(rs);

        return sResult;

    }

    /**
     * 根据筛选条件获取所有数据，包含审核、未审核、回收站中的数据
     * 修改：
     * 1.添加投资类型的处理 2009-08-14 add by wangzuochun MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";    //词汇类型对照字符串
        String sDateStr = "";
        String strSql = "";
        ResultSet rs = null;
        boolean bTrans = false;
        String strSql1 = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        //STORY #863 香港、美国股指期权交易区别  期权需求变更
        pubPara = new EachGetPubPara();
        pubPara.setYssPub(pub);
        //STORY #863 香港、美国股指期权交易区别  期权需求变更
        try {
            sHeader = this.getListView1Headers();
            System.out.println("表头:" + sHeader);
            //Adding by Story #1506
            //==============================
            if (this.filterType.isOnlyColumns.equals("0")) {
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                       this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr() + 
                       sDateStr + "\r\f";
            }
            //=============end=================
            
            strSql =
                "select a.*, Nvl(opt.FTradeTypeCode,'none') as tradeType,b.FUserName as FCreatorName, c.FUserName as FCheckUserName, " +
                " g.FCuryName,f.FSecurityName,h.FPortName as FPortName,h.FStorageInitDate as FStorageInitDate,j.FAttrClsName as FAttrClsName,f.fcatcode" ;//STORY #863 香港、美国股指期权交易区别  期权需求变更 add by jiangshicao
               
            strSql = strSql +
                (storageAnalysis().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                //Modifying of Story #1506
                //===============================
                ", nvl(FAnalysisName1, ' ') as FAnalysisName1, nvl(FAnalysisName2, ' ') as FAnalysisName2, nvl(FAnalysisName3, ' ') as FAnalysisName3 ") +
                //================end===============
                " from " + pub.yssGetTableName("Tb_Stock_Security") +
                " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) g on a.FCuryCode = g.FCuryCode" +
                storageAnalysis() +

                " left join (select o.FSecurityCode as FSecurityCode,o.FSecurityName as FSecurityName , o.fcatcode as fcatcode from "+//STORY #863 香港、美国股指期权交易区别  期权需求变更 add by jiangshicao
                
                pub.yssGetTableName("Tb_Para_Security") + " o join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) f on a.FSecurityCode = f.FSecurityCode" +

                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
                //20120822 added by liubo.Story #2754
                //增加关联期权交易表，获取期权交易方式。若期权交易表中不存在关联数据，则交易方式字段赋值none
                //==============================================
                " left join (select distinct FTradeTypeCode,FSecurityCode,FBargainDate from " + pub.yssGetTableName("TB_Data_OptionsTrade") + ") opt on a.FSecurityCode = opt.FSecurityCode and a.FStorageDate = opt.FBargainDate " +

                //=====================end=========================
                " left join (select FPortCode ,FPortName,FStorageInitDate  from " +
                pub.yssGetTableName("tb_para_portfolio") + " " +
                " where  FCheckState = 1 ) h on a.FPortCode = h.FPortCode" +
              
                //end by lidaolong
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                ") j on a.FAttrClsCode = j.FAttrClsCode " +

                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
            //Modifying of Story #1506
            //==========================
            //rs = dbl.openResultSet(strSql);    
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("CashStorage");
            rs =dbl.openResultSet(yssPageInationBean);
            //============end==============
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                
                
                
                this.strSecurityCode = rs.getString("FSecurityCode") + "";
                this.strSecurityName = rs.getString("FSecurityName") + "";
                this.strFreezeAmount = rs.getString("FFreezeAmount") + "";
                this.strStorageStartDate = rs.getDate("FStorageInitDate") + "";

                this.strFAnalysisCode1 = rs.getString("FAnalysisCode1") + "";
                this.strFAnalysisName1 = rs.getString("FAnalysisName1") + "";
                this.strFAnalysisCode2 = rs.getString("FAnalysisCode2") + "";
                this.strFAnalysisName2 = rs.getString("FAnalysisName2") + "";
                this.strFAnalysisCode3 = rs.getString("FAnalysisCode3") + "";
                this.strFAnalysisName3 = rs.getString("FAnalysisName3") + "";

                this.strPortCode = rs.getString("FPortCode") + "";
                this.strPortName = rs.getString("FPortName") + "";

                this.strPortCuryRate = rs.getString("FPortCuryRate") + "";
                this.strPortCuryCost = rs.getString("FPortCuryCost") + "";
                this.strMPortCuryCost = rs.getString("FMPortCuryCost") + "";
                this.strVPortCuryCost = rs.getString("FVPortCuryCost") + "";
                this.strBaseCuryRate = rs.getString("FBaseCuryRate") + "";
                this.strBaseCuryCost = rs.getString("FBaseCuryCost") + "";
                this.strMBaseCuryCost = rs.getString("FMBaseCuryCost") + "";
                this.strVBaseCuryCost = rs.getString("FVBaseCuryCost") + "";

                this.strStorageAmount = rs.getString("FStorageAmount") + "";
                this.strStorageCost = rs.getString("FStorageCost") + "";
                this.strMStorageCost = rs.getString("FMStorageCost") + "";
                this.strVStorageCost = rs.getString("fvstoragecost") + "";
                this.strCuryCode = rs.getString("FCuryCode") + "";
                this.strCuryName = rs.getString("FCuryName") + "";
                this.strStorageDate = rs.getDate("FStorageDate") + "";
                this.strYearMonth = rs.getString("FYearMonth") + "";
                this.attrCode = rs.getString("FAttrClsCode") + "";
                this.attrName = rs.getString("FAttrClsName") + "";
                super.setRecLog(rs);

                setResultSetAttr(rs);

                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            //获取投资类型词汇信息 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_InvestType);
            //=============== End MS00024 2009-08-14 add by wangzuochun  =============

            AssetStorageCfgBean assetgroupcfg = new AssetStorageCfgBean();
            assetgroupcfg.setYssPub(pub);
            sDateStr = assetgroupcfg.getPartSetting("Security");

            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }
            //Modifying of Story #1506
            //================================
            //return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + this.getListView1ShowCols() + "\r\f" + sDateStr + "\r\fvoc" + sVocStr;
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr() + "\r\f" + sDateStr;
            //=================end===============
        } catch (Exception e) {
            throw new YssException("获取证券库存信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() {
        return "";
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
     * getOperData
     */
    public void getStorageData() {
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
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.sSecRecPayBal = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            this.sRecycled = sRowStr;

            reqAry = sTmpStr.split("\t");

            this.strSecurityCode = reqAry[0];
            this.strSecurityName = reqAry[1];
            this.strFreezeAmount = reqAry[2];
            this.strStorageStartDate = reqAry[3];
            this.strFAnalysisCode1 = reqAry[4];
            this.strFAnalysisName1 = reqAry[5];
            this.strFAnalysisCode2 = reqAry[6];
            this.strFAnalysisName2 = reqAry[7];
            this.strFAnalysisCode3 = reqAry[8];
            this.strFAnalysisName3 = reqAry[9];
            this.strPortCode = reqAry[10];
            this.strPortName = reqAry[11];

            this.strPortCuryRate = reqAry[12];
            this.strPortCuryCost = reqAry[13];
            this.strMPortCuryCost = reqAry[14];
            this.strVPortCuryCost = reqAry[15];

            this.strBaseCuryRate = reqAry[16];
            this.strBaseCuryCost = reqAry[17];
            this.strMBaseCuryCost = reqAry[18];
            this.strVBaseCuryCost = reqAry[19];

            this.strStorageAmount = reqAry[20];
            this.strStorageCost = reqAry[21];
            this.strMStorageCost = reqAry[22];
            //解析估值 成本
            this.strVStorageCost = reqAry[23];

            this.checkStateId = Integer.parseInt(reqAry[24]);

            this.strCuryCode = reqAry[25];
            this.strCuryName = reqAry[26];
            this.strStorageDate = reqAry[27];

            this.strOldStorageCode = reqAry[28];
            this.strOldPortCode = reqAry[29];
            this.strOldStorageStartDate = reqAry[30];

            this.strOldFAnalysisCode1 = reqAry[31];
            this.strOldFAnalysisCode2 = reqAry[32];
            this.strOldFAnalysisCode3 = reqAry[33];
            this.strOldStorageDate = reqAry[34];
            this.isOnlyColumns = reqAry[35];
            this.bBegin = reqAry[36];
            this.strYearMonth = reqAry[37];
            this.catType = reqAry[38];
            this.attrCode = reqAry[39];
            this.oldAttrCode = reqAry[40];
            this.strOldYearMonth = reqAry[41]; //modified libo 20090520 MS00445 修改证券库存中的记录为期初数时系统会报错 QDV4赢时胜上海2009年5月10日01_B
            //------ add by wangzuochun 2009.08.14 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -------//
            this.investType = reqAry[42];
            this.oldInvestType=reqAry[43];
            //--------------------------------------------------------------------------------------------------//
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new SecurityStorageBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析证券库存设置请求出错", e);
        }
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
    	
    	
        //STORY #863 香港、美国股指期权交易区别  期权需求变更   add by jiangshichao 2011.06.20 start 
    	sCostAccount_Para = "";
		//edit by songjie 2011.06.25 查询证券库存报空指针异常
    	if(pubPara != null && rs.getString("fcatcode") != null && 
    	   rs.getString("fcatcode").equalsIgnoreCase("FP")){
    		pubPara.setSPortCode(rs.getString("FPortCode"));
    		pubPara.setSPubPara(rs.getString("FSecurityCode"));
    		pubPara.setsDate(YssFun.formatDate(rs.getDate("fstoragedate")));
    		//20120822 added by liubo.Story #2754
    		//设置获取“期权成本核算设置”通参的控件值为“是否核算成本”，同时传入期权交易方式
    		//================================
    		pubPara.setCtlFlag("cboAccountCost");
    		pubPara.setTradeType(rs.getString("tradeType"));
    		//================end================
    		sCostAccount_Para = pubPara.getOptCostAccountSet();
    	}
        //STORY #863 香港、美国股指期权交易区别  期权需求变更 end 
    	
        this.strSecurityCode = rs.getString("FSecurityCode") + "";
        this.strFreezeAmount = rs.getString("FFreezeAmount") + "";
        
        this.strStorageStartDate = rs.getDate("FStorageInitDate") + "";
        this.strFAnalysisCode1 = rs.getString("FAnalysisCode1") + "";
        this.strFAnalysisCode2 = rs.getString("FAnalysisCode2") + "";
        this.strFAnalysisCode3 = rs.getString("FAnalysisCode3") + "";
        this.strPortCode = rs.getString("FPortCode") + "";
        this.strPortCuryRate = rs.getString("FPortCuryRate") + "";
       
        
        this.strBaseCuryRate = rs.getString("FBaseCuryRate") + "";
        
        this.strStorageAmount = rs.getString("FStorageAmount") + "";
        
        this.strCuryCode = rs.getString("FCuryCode") + "";
        this.strStorageDate = rs.getDate("FStorageDate") + "";
        this.strYearMonth = rs.getString("FYearMonth") + "";
        this.attrCode = rs.getString("FAttrClsCode") + "";
        this.attrName = rs.getString("FAttrClsName") + "";
        //------ add by wangzuochun 2009.08.14 MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A -------//
        this.investType = rs.getString("FInvestType");
        //--------------------------------------------------------------------------------------------------//
        if(sCostAccount_Para.trim().length()!=0&&sCostAccount_Para.equalsIgnoreCase("false")){
        	this.strStorageCost = "0";
            this.strMStorageCost = "0";
            this.strVStorageCost = "0";
            this.strPortCuryCost = "0";
            this.strMPortCuryCost = "0";
            this.strVPortCuryCost = "0";
            this.strBaseCuryCost = "0";
            this.strMBaseCuryCost = "0";
            this.strVBaseCuryCost = "0";
        }else{
        	this.strStorageCost = rs.getString("FStorageCost") + "";
            this.strMStorageCost = rs.getString("FMStorageCost") + "";
            this.strVStorageCost = rs.getString("fvstoragecost") + "";
            this.strPortCuryCost = rs.getString("FPortCuryCost") + "";
            this.strMPortCuryCost = rs.getString("FMPortCuryCost") + "";
            this.strVPortCuryCost = rs.getString("FVPortCuryCost") + "";
            this.strBaseCuryCost = rs.getString("FBaseCuryCost") + "";
            this.strMBaseCuryCost = rs.getString("FMBaseCuryCost") + "";
            this.strVBaseCuryCost = rs.getString("FVBaseCuryCost") + "";
        }
        
        
    }

    /**
     * saveMutliOperData
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliStorageData(String sMutilRowStr) throws YssException {
        return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
    	//add by fangjiang 2010.10.18 MS01849 QDV4赢时胜（深圳）2010年10月13日01_B 
    	try{
    		if (sType.equalsIgnoreCase("getYesDateOptionsStorage")) {
        		return String.valueOf(this.getYesDateOptionsStorage());
        	}
    	}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
    	//------------------
        return "";
    }

    public String getStrPortCuryCost() {
        return strPortCuryCost;
    }

    public void setStrPortCuryCost(String strPortCuryCost) {
        this.strPortCuryCost = strPortCuryCost;
    }

    public String getStrMPortCuryCost() {
        return strMPortCuryCost;
    }

    public void setStrMPortCuryCost(String strMPortCuryCost) {
        this.strMPortCuryCost = strMPortCuryCost;
    }

    public String getStrVPortCuryCost() {
        return strVPortCuryCost;
    }

    public void setStrVPortCuryCost(String strVPortCuryCost) {
        this.strVPortCuryCost = strVPortCuryCost;
    }

    public String getStrBaseCuryCost() {
        return strBaseCuryCost;
    }

    public void setStrBaseCuryCost(String strBaseCuryCost) {
        this.strBaseCuryCost = strBaseCuryCost;
    }

    public String getStrMBaseCuryCost() {
        return strMBaseCuryCost;
    }

    public void setStrMBaseCuryCost(String strMBaseCuryCost) {
        this.strMBaseCuryCost = strMBaseCuryCost;
    }

    public String getStrVBaseCuryCost() {
        return strVBaseCuryCost;
    }

    public String getBBegin() {
        return bBegin;
    }

    public double getBailMoney() {
        return bailMoney;
    }

    public void setStrVBaseCuryCost(String strVBaseCuryCost) {
        this.strVBaseCuryCost = strVBaseCuryCost;
    }

    public void setBBegin(String bBegin) {
        this.bBegin = bBegin;
    }

    public void setBailMoney(double bailMoney) {
        this.bailMoney = bailMoney;
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
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
     * 根据筛选条件获取证券库存数据的一个实例信息
     * 20090828. implement by xuxuming MS00473:QDV4国泰2009年6月01日01_A
     * @return IDataSetting
     */
    public IDataSetting getSetting() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = "SELECT * FROM " + pub.yssGetTableName("Tb_Stock_Security") + " a "
                + buildFilterSql();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strSecurityCode = rs.getString("FSecurityCode") + "";
                this.strFreezeAmount = rs.getString("FFreezeAmount") + "";
                this.strMStorageCost = rs.getString("FMStorageCost") + "";
                this.strFAnalysisCode1 = rs.getString("FAnalysisCode1") + "";
                this.strFAnalysisCode2 = rs.getString("FAnalysisCode2") + "";
                this.strFAnalysisCode3 = rs.getString("FAnalysisCode3") + "";
                this.strPortCode = rs.getString("FPortCode") + "";
                this.strPortCuryRate = rs.getString("FPortCuryRate") + "";
                this.strPortCuryCost = rs.getString("FPortCuryCost") + "";
                this.strMPortCuryCost = rs.getString("FMPortCuryCost") + "";
                this.strVPortCuryCost = rs.getString("FVPortCuryCost") + "";
                this.strBaseCuryRate = rs.getString("FBaseCuryRate") + "";
                this.strBaseCuryCost = rs.getString("FBaseCuryCost") + "";
                this.strMBaseCuryCost = rs.getString("FMBaseCuryCost") + "";
                this.strVBaseCuryCost = rs.getString("FVBaseCuryCost") + "";
                this.strStorageAmount = rs.getString("FStorageAmount") + "";
                this.strStorageCost = rs.getString("FStorageCost") + "";
                this.strVStorageCost = rs.getString("FVStorageCost") + "";
                this.strCuryCode = rs.getString("FCuryCode") + "";
                this.strStorageDate = rs.getDate("FStorageDate") + "";
                this.strYearMonth = rs.getString("FYearMonth") + "";
                this.attrCode = rs.getString("FAttrClsCode") + "";
                this.investType = rs.getString("FInvestType") + "";
                this.checkStateId = rs.getInt("FCheckState");
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FCreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime");

            }
        } catch (Exception e) {
            throw new YssException("获取证券库存信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return this;
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
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        SecurityStorageBean befEditBean = new SecurityStorageBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, " +
                " g.FCuryName,f.FSecurityName,h.FPortName as FPortName,h.FStorageInitDate as FStorageInitDate,j.FAttrClsName as FAttrClsName";
            strSql = strSql +
                (storageAnalysis().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ") +
                " from " + pub.yssGetTableName("Tb_Stock_Security") +
                " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("tb_para_currency") +
                " where FCheckState = 1) g on a.FCuryCode = g.FCuryCode" +
                storageAnalysis() +

                " left join (select o.FSecurityCode as FSecurityCode,o.FSecurityName as FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + " o join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) f on a.FSecurityCode = f.FSecurityCode" +

                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
      
                
                " left join (select FPortCode ,FPortName,FStorageInitDate  from " +
                pub.yssGetTableName("tb_para_portfolio") + 
                " where  FCheckState = 1 ) h on a.FPortCode = h.FPortCode" +
                
                
                //end by lidaolong
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                ") j on a.FAttrClsCode = j.FAttrClsCode " +

                " where  a.FSecurityCode =" +
                dbl.sqlString(this.strOldStorageCode) + //lzp modify 2008 0123 FSecurityCode  改为a.FSecurityCode
                " and FStorageDate=" + dbl.sqlDate(this.strOldStorageDate) +
                " and a.FPortCode=" + dbl.sqlString(this.strOldPortCode) + // wdy 添加表别名：a
                " order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.strSecurityCode = rs.getString("FSecurityCode") + "";
                befEditBean.strSecurityName = rs.getString("FSecurityName") + "";
                befEditBean.strFreezeAmount = rs.getString("FFreezeAmount") + "";
                befEditBean.strStorageStartDate = rs.getDate("FStorageInitDate") +
                    "";

                befEditBean.strFAnalysisCode1 = rs.getString("FAnalysisCode1") + "";
                befEditBean.strFAnalysisName1 = rs.getString("FAnalysisName1") + "";
                befEditBean.strFAnalysisCode2 = rs.getString("FAnalysisCode2") + "";
                befEditBean.strFAnalysisName2 = rs.getString("FAnalysisName2") + "";
                befEditBean.strFAnalysisCode3 = rs.getString("FAnalysisCode3") + "";
                befEditBean.strFAnalysisName3 = rs.getString("FAnalysisName3") + "";

                befEditBean.strPortCode = rs.getString("FPortCode") + "";
                befEditBean.strPortName = rs.getString("FPortName") + "";

                befEditBean.strPortCuryRate = rs.getString("FPortCuryRate") + "";
                befEditBean.strPortCuryCost = rs.getString("FPortCuryCost") + "";
                befEditBean.strMPortCuryCost = rs.getString("FMPortCuryCost") + "";
                befEditBean.strVPortCuryCost = rs.getString("FVPortCuryCost") + "";
                befEditBean.strBaseCuryRate = rs.getString("FBaseCuryRate") + "";
                befEditBean.strBaseCuryCost = rs.getString("FBaseCuryCost") + "";
                befEditBean.strMBaseCuryCost = rs.getString("FMBaseCuryCost") + "";
                befEditBean.strVBaseCuryCost = rs.getString("FVBaseCuryCost") + "";

                befEditBean.strStorageAmount = rs.getString("FStorageAmount") + "";
                befEditBean.strStorageCost = rs.getString("FStorageCost") + "";
                befEditBean.strMStorageCost = rs.getString("FMStorageCost") + "";
                befEditBean.strVStorageCost = rs.getString("fvstoragecost") + "";
                befEditBean.strCuryCode = rs.getString("FCuryCode") + "";
                befEditBean.strCuryName = rs.getString("FCuryName") + "";
                befEditBean.strStorageDate = rs.getDate("FStorageDate") + "";
                befEditBean.strYearMonth = rs.getString("FYearMonth") + "";
                befEditBean.attrCode = rs.getString("FAttrClsCode") + "";
                befEditBean.attrName = rs.getString("FAttrClsName") + "";
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //Close the resultSet finally sunkey@Modify 20090908
        }
    }

    public int getIntStorageState() {
        return intStorageState;
    }

    public String getCatType() {
        return catType;
    }

    public String getAttrCode() {
        return attrCode;
    }

    public String getAttrName() {
        return attrName;
    }

    public String getCatTypeName() {
        return catTypeName;
    }

    public String getStrYearMonth() {
        return strYearMonth;
    }

    public String getOldCatType() {
        return oldCatType;
    }

    public String getOldAttrCode() {
        return oldAttrCode;
    }

    public String getStrOldYearMonth() {
        return strOldYearMonth;
    }

    public double getEffectiveRate() {
        return effectiveRate;
    }

    public String getOldInvestType() {
        return oldInvestType;
    }

    public void setIntStorageState(int intStorageState) {
        this.intStorageState = intStorageState;
    }

    public void setCatType(String catType) {
        this.catType = catType;
    }

    public void setAttrCode(String attrCode) {
        this.attrCode = attrCode;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public void setCatTypeName(String catTypeName) {
        this.catTypeName = catTypeName;
    }

    public void setStrYearMonth(String strYearMonth) {
        this.strYearMonth = strYearMonth;
    }

    public void setOldCatType(String oldCatType) {
        this.oldCatType = oldCatType;
    }

    public void setOldAttrCode(String oldAttrCode) {
        this.oldAttrCode = oldAttrCode;
    }

    public void setStrOldYearMonth(String strOldYearMonth) {
        this.strOldYearMonth = strOldYearMonth;
    }

    public void setEffectiveRate(double effectiveRate) {
        this.effectiveRate = effectiveRate;
    }

    public void setOldInvestType(String oldInvestType) {
        this.oldInvestType = oldInvestType;
    }

    /**
     * 此方法判断传入的日期当天的库存=传日日期前一天库存+当天的发生
     * @param dDate Date 传入日期
     * @param sSecurityCode String 证券代码
     * @param sPortCode String 组合代码
     * @param sAnalysisCode1 String 分析代码1
     * @param sAnalysisCode2 String 分析代码2
     * @param sAnalysisCode3 String 分析代码3
     * @param sCostType String 核算类型
     * @param attrCode String 标示
     * @return SecurityStorageBean 返回证券库存bean
     * @throws YssException
     * xuqiji 20090914 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
     */
    public SecurityStorageBean getStorageCost(java.util.Date dDate,
                                              String sSecurityCode,
                                              String sPortCode,
                                              String sAnalysisCode1,
                                              String sAnalysisCode2,
                                              String sAnalysisCode3,
                                              String sCostType,
                                              String attrCode) throws
        YssException {
        String strSql = "", strError = "", strTmpSql = "";
        ResultSet rs = null;
        
        //------ add by wangzuochun 2011.02.10 BUG #1071 系统计算出的基金分红金额与实际的分红金额有误差 
        ResultSet rsMax = null;
        int iParaID = 0;
        int maxID = 0;
        ArrayList listCateCode = new ArrayList(); 
        String strCateCode = ""; //当前证券库存信息品种类型代码
        String strPortCode = ""; //存放通用参数中的组合代码
        String[] arrCateCode = null; //存放通用参数中的证券品种代码
        boolean bFlag = false; //存放通用参数中 是否保留小数位的布尔值，默认为否
        /**add---huhuichao 2013-12-6 Bug 85064 60版本的股票分红数据，如果证券库存不足1的话，系统按照1来计算 */
        //boolean bRight = false; //通用参数中三个参数与当前证券库存信息是否匹配，
        boolean bRight = true; //“基金分红是否保留小数位数”通参的默认值，修改为“是”，保留位数。
        /**end---huhuichao 2013-12-6 BUG  85064*/
        //-------- BUG #1071 -------//
                
//        double dReCost = 0; 无用、删除 sunkey@Delete 20090908
//        boolean analy1;
//        boolean analy2;
//        boolean analy3;
        try {
//            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security"); 无用、删除 sunkey@Delete 20090908
//            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
//            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
        	
        	//------ add by wangzuochun 2011.02.10 BUG #1071 系统计算出的基金分红金额与实际的分红金额有误差 
        	
        	strSql = " select * from " + pub.yssGetTableName("tb_para_security")
        			+ " where FSecurityCode = " + dbl.sqlString(sSecurityCode)
        			+ " and FCheckState = 1";
        	
        	rs = dbl.openResultSet(strSql);
        	
        	if (rs.next()){
        		strCateCode = rs.getString("FCatCode");
        	}
        	
        	dbl.closeResultSetFinal(rs);
        	
        	String strSqlMax = " select max(FParaID) as FParaID from "
					+ pub.yssGetTableName("TB_PFOper_PUBPARA")
					+ " WHERE FPubParaCode = 'ParaRoundStorage'";
        	
			rsMax = dbl.openResultSet(strSqlMax);
			if (rsMax.next()) {
				maxID = rsMax.getInt("FParaID");
			}
			
			dbl.closeResultSetFinal(rsMax);
			
			while(true){
				
				//使用 FParaId 字段的累加作为查询条件,如果查询不到数据则跳出查询
                iParaID++;
                
                if (maxID == 0 || iParaID > maxID){
                	break;
                } 
                
                strSql = "SELECT b.FCtlGrpCode, b.FCtlCode, b.FCtlType, b.FFunModules, a.FPubParaCode, a.FCtlValue, a.FParaID " +
                	" FROM " + pub.yssGetTableName("TB_PFOper_PUBPARA") + " a " +
                	" LEFT JOIN Tb_PFSys_FaceCfgInfo b ON a.FCtlGrpCode = b.FCtlGrpCode" +
                	" AND a.FCtlCode = b.FCtlCode " +
                	" WHERE FPubParaCode = 'ParaRoundStorage'" +
                	" AND FParaID = " + String.valueOf(iParaID);
                
                rs = dbl.openResultSet_antReadonly(strSql);
                
                if (rs.next()){
                	rs.beforeFirst();
                	
                	while(rs.next()){
                    	if("selPort".equals(rs.getString("FCtlCode"))){
                    		strPortCode = rs.getString("FCtlValue").split("[|]")[0];
                    	}
                    	else if ("selCategory".equals(rs.getString("FCtlCode"))){
                    		arrCateCode = rs.getString("FCtlValue").split("[|]")[0].split(",");
                    	}
                    	else if ("cboYesOrNo".equals(rs.getString("FCtlCode"))){
                    		bFlag = "1".equals(rs.getString("FCtlValue").split(",")[0]);
                    	}
                    }
                	
                	for(int i=0; i<arrCateCode.length; i++){
                		if(arrCateCode[i] != null && arrCateCode[i].length() > 0){
                			listCateCode.add(arrCateCode[i]);
                		}
                	}
                	
                	if(sPortCode != null && sPortCode.length() > 0 && sPortCode.equals(strPortCode)
                			&& listCateCode.contains(strCateCode)){
                		
                		bRight = bFlag;
                		listCateCode.clear();
                		break;
                	}
                	else{
                		listCateCode.clear();
                		bRight = false;
                	}
                }
                dbl.closeResultSetFinal(rs);
			}
        	
			//--------------------------------- BUG #1071 --------------------------------//
			
            if (YssFun.getMonth(dDate) == 1 && YssFun.getDay(dDate) == 1) {
                strTmpSql = strTmpSql + " and fyearmonth ='" +
                    YssFun.formatDate(dDate, "yyyy") + "00'";
            } else {
                strTmpSql = strTmpSql + " and fyearmonth <>'" +
                    YssFun.formatDate(dDate, "yyyy") + "00'" +
                    " and FStorageDate = " + dbl.sqlDate(YssFun.addDay(dDate, -1));
            }

            strSql = "select sum(t.FStorageAmount) as FSumStorageAmount, sum(t.FStorageCost) as FSumStorageCost, " +
                //add by songjie 2012.04.11 STORY #2320 QDV4长信基金2012年02月28日01_A 统计 基础货币成本  和  组合货币成本
                " sum(t.FBaseCuryCost) as FSumBaseCuryCost, sum(t.FPortCuryCost) as FSumPortCuryCost " +
                " from ( select sum(" +
                dbl.sqlIsNull("a.FStorageAmount", "0") +
                ") as FStorageAmount " + addSumField(sCostType) + 
                //edit by songjie 2012.04.11 STORY #2320 QDV4长信基金2012年02月28日01_A 统计 基础货币成本  和  组合货币成本
                " ,sum(NVL(a.FBaseCuryCost, 0)) as FBaseCuryCost, sum(NVL(a.FPortCuryCost, 0)) as FPortCuryCost from " +
                pub.yssGetTableName("Tb_Stock_Security") +
                " a where FCheckState = 1 and FPortCode= " +
                dbl.sqlString(sPortCode) +
                " and FSecurityCode = " + dbl.sqlString(sSecurityCode) +
                strTmpSql
                + //xuqiji 20090721 以下修改因为：分析代码写错  QDV4.1赢时胜（上海）2009年4月20日15_A MS00015    国内权益处理
                ( (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) ?
                 " and FAnalysisCode1=" + dbl.sqlString(sAnalysisCode1) : " ") +
                ( (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) ?
                 " and FAnalysisCode2=" + dbl.sqlString(sAnalysisCode2) : " ") +
                ( (sAnalysisCode3 != null && sAnalysisCode3.trim().length() > 0) ?
                 " and FAnalysisCode3=" + dbl.sqlString(sAnalysisCode3) : " ") +
                ( (attrCode != null && attrCode.trim().length() > 0) ?
                 " and FAttrClsCode =" + dbl.sqlString(attrCode) : " ");
            strSql = strSql +
                " union all select sum( m.FTradeAmount * n.FAmountInd) as FStorageAmount," +
                " sum( m.FTotalCost*n.FCashInd ) as FStorageCost " +
                //---edit by songjie 2012.04.11 STORY #2320 QDV4长信基金2012年02月28日01_A 统计 基础货币成本  和  组合货币成本 start---//
                " ,sum(round(m.FTotalCost * n.FCashInd * m.FBaseCuryRate, 2)) as FBaseCuryCost, " +
                " sum(round(m.FTotalCost * n.FCashInd * m.FBaseCuryRate / m.FPortCuryRate, 2)) as FPortCuryCost " +
                
                "from(select fTradeTypeCode, FTradeAmount, FTotalCost, FBaseCuryRate, FPortCuryRate from " +
                //---edit by songjie 2012.04.11 STORY #2320 QDV4长信基金2012年02月28日01_A 统计 基础货币成本  和  组合货币成本 end---//
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FCheckState=1 and FPortCode=" + dbl.sqlString(sPortCode) +
                " and FSecurityCode =" + dbl.sqlString(sSecurityCode) +
                " and FBargainDate= " +
                dbl.sqlDate(dDate) +
                ( (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) ?
                 " and FInvMgrCode=" + dbl.sqlString(sAnalysisCode1) : " ") +
                ( (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) ?
                 " and FBrokerCode=" + dbl.sqlString(sAnalysisCode2) : " ") +
                ( (attrCode != null && attrCode.trim().length() > 0) ?
                 " and FAttrClsCode =" + dbl.sqlString(attrCode) : " ");
            strSql = strSql +
                " ) m join (select FTradeTypeCode, FCashInd, FAmountInd from " +
                " Tb_Base_TradeType where FCheckState=1)n on m.FTradeTypeCode = n.FTradeTypeCode";
            //TASK #1474::证券借贷业务需求 - 权益处理
            strSql = strSql +
            " union all select sum( m.FTradeAmount * n.FAmountInd) as FStorageAmount," +
            " sum( m.FTotalCost*n.FCashInd ) as FStorageCost " +
            //add by songjie 2012.04.11 STORY #2320 QDV4长信基金2012年02月28日01_A 统计 基础货币成本  和  组合货币成本
            " ,sum(m.FTotalCost * n.FCashInd) as FBaseCuryCost, sum(m.FTotalCost * n.FCashInd) as FPortCuryCost " + 
            "from(select fTradeTypeCode, FTradeAmount, FTotalCost from " +
            pub.yssGetTableName("tb_data_seclendtrade") +
            " where FCheckState=1 and FPortCode=" + dbl.sqlString(sPortCode) +
            " and FTradeTypeCode in ("+operSql.sqlCodes(YssOperCons.YSS_SECLEND_JYLX_Borrow+","+YssOperCons.YSS_SECLEND_JYLX_Rcb+","+YssOperCons.YSS_SECLEND_JYLX_Rbsb+","+YssOperCons.YSS_SECLEND_JYLX_Awrb+","+YssOperCons.YSS_SECLEND_JYLX_Lpwr)+" )"+
            " and FSecurityCode =" + dbl.sqlString(sSecurityCode) +
            " and FBargainDate= " +
            dbl.sqlDate(dDate) +
            ( (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) ?
             " and FInvMgrCode=" + dbl.sqlString(sAnalysisCode1) : " ") +
            ( (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) ?
             " and FBrokerCode=" + dbl.sqlString(sAnalysisCode2) : " ") +
            ( (attrCode != null && attrCode.trim().length() > 0) ?
             " and FAttrClsCode =" + dbl.sqlString(attrCode) : " ");
        strSql = strSql +
            " ) m join (select FTradeTypeCode, FCashInd, FAmountInd from " +
            " Tb_Base_TradeType where FCheckState=1)n on m.FTradeTypeCode = n.FTradeTypeCode";
            //---------end TASK #1474--------------
//            //MS01074 分红、送股、配股不应对未结算的交易进行权益处理 ，因此在统计含权数量时需将权益确认日（含）前没有结算的交易数据剔除在外 panjunfang modify 20100416
//            strSql = strSql +
//                " union all select -sum(ms.FTradeAmount * ns.FAmountInd) as FStorageAmount," +
//                " -sum(ms.FTotalCost*ns.FCashInd ) as FStorageCost " +
//                "from(select fTradeTypeCode, FTradeAmount, FTotalCost from " + pub.yssGetTableName("Tb_Data_SubTrade") +
//                " where FCheckState=1 and FPortCode=" + dbl.sqlString(sPortCode) +    
//                " and FSecurityCode =" + dbl.sqlString(sSecurityCode) +
//                " and FBargainDate <= " + dbl.sqlDate(dDate) +
//                " and FSettleDate > " + dbl.sqlDate(dDate) +
//                ( (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) ?
//                 " and FInvMgrCode=" + dbl.sqlString(sAnalysisCode1) : " ") +
//                ( (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) ?
//                 " and FBrokerCode=" + dbl.sqlString(sAnalysisCode2) : " ") +
//                ( (attrCode != null && attrCode.trim().length() > 0) ?
//                 " and FAttrClsCode =" + dbl.sqlString(attrCode) : " ") + 
//                " ) ms join (select FTradeTypeCode, FCashInd, FAmountInd from " +
//                " Tb_Base_TradeType where FCheckState=1)ns on ms.FTradeTypeCode = ns.FTradeTypeCode ";
            //这里还需要加上综合业务的成本 by leeyu 20100907 合并太平版本后调整代码
            strSql = strSql +
            " union all select sum(FAmount) as FStorageAmount" +
            addSumField(sCostType)+
            //add by songjie 2012.04.11 STORY #2320 QDV4长信基金2012年02月28日01_A 统计 基础货币成本  和  组合货币成本
            " ,sum(NVL(a.FBaseEXCost, 0)) as FBaseCuryCost, sum(NVL(a.FPortEXCost, 0)) as FPortCuryCost " + 
            " from (select FSecurityCode, FAmount, FExchangeCost as FStorageCost,FMExCost as FMStorageCost,FVExCost as FVStorageCost " +
            //edit by songjie 2012.04.11 STORY #2320 QDV4长信基金2012年02月28日01_A 统计 基础货币成本  和  组合货币成本
            " ,FBaseEXCost,FPortEXCost from " + pub.yssGetTableName("Tb_Data_Integrated") +
            " where fcheckstate = 1 and FPortCode="+dbl.sqlString(sPortCode)+
            " and FSecurityCode = " + dbl.sqlString(sSecurityCode) +
            " and FOperDate = " + dbl.sqlDate(dDate);
	     if (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) {
	          strSql = strSql + " and FAnalysisCode1=" + dbl.sqlString(sAnalysisCode1);
	     }
	     if (sAnalysisCode2 != null && sAnalysisCode2.trim().length() > 0) {
	         strSql = strSql + " and FAnalysisCode2= " + dbl.sqlString(sAnalysisCode2);
	     }
	     if (sAnalysisCode3 != null && sAnalysisCode3.trim().length() > 0) {
	         strSql = strSql + " and FAnalysisCode3=" + dbl.sqlString(sAnalysisCode3);
	     }
	     if (attrCode != null && attrCode.trim().length() > 0) {
	         strSql = strSql + " and FAttrClsCode=" +dbl.sqlString(attrCode);
	     }
	     strSql+=") a ";
	     //这里还需要加上综合业务的成本 by leeyu 20100907 合并太平版本后调整代码
            strSql = strSql + ") t";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.setStrStorageCost(YssFun.formatNumber(YssFun.roundIt(rs.
                    getDouble("FSumStorageCost"), 2), "#,##0.##"));
                
                //---add by songjie 2012.04.11 STORY #2320 QDV4长信基金2012年02月28日01_A 统计 基础货币成本  和  组合货币成本 start---//
                this.setStrBaseCuryCost(YssFun.formatNumber(YssFun.roundIt(rs.
                    getDouble("FSumBaseCuryCost"), 2), "#,##0.##"));
                this.setStrPortCuryCost(YssFun.formatNumber(YssFun.roundIt(rs.
                    getDouble("FSumPortCuryCost"), 2), "#,##0.##"));
                //---add by songjie 2012.04.11 STORY #2320 QDV4长信基金2012年02月28日01_A 统计 基础货币成本  和  组合货币成本 end---//
                
                //------ modify by wangzuochun 2011.02.10 BUG #1071 系统计算出的基金分红金额与实际的分红金额有误差 
                if(!bRight){
                	this.setStrStorageAmount(YssFun.formatNumber(YssFun.roundIt(rs.
                            getDouble("FSumStorageAmount"), 0)
                            , "#,##0.##"));
                	
                    //---add by songjie 2012.04.11 STORY #2320 QDV4长信基金2012年02月28日01_A 统计 基础货币成本  和  组合货币成本 start---//
                    this.setStrBaseCuryCost(YssFun.formatNumber(YssFun.roundIt(rs.
                        getDouble("FSumBaseCuryCost"), 0), "#,##0.##"));
                    this.setStrPortCuryCost(YssFun.formatNumber(YssFun.roundIt(rs.
                        getDouble("FSumPortCuryCost"), 0), "#,##0.##"));
                    //---add by songjie 2012.04.11 STORY #2320 QDV4长信基金2012年02月28日01_A 统计 基础货币成本  和  组合货币成本 end---//
                }
                else{
                	this.setStrStorageAmount(YssFun.formatNumber(rs.getDouble("FSumStorageAmount"), "#,##0.##"));
                	
                    //---add by songjie 2012.04.11 STORY #2320 QDV4长信基金2012年02月28日01_A 统计 基础货币成本  和  组合货币成本 start---//
                    this.setStrBaseCuryCost(YssFun.formatNumber(rs.getDouble("FSumBaseCuryCost"), "#,##0.##"));
                    this.setStrPortCuryCost(YssFun.formatNumber(rs.getDouble("FSumPortCuryCost"), "#,##0.##"));
                    //---add by songjie 2012.04.11 STORY #2320 QDV4长信基金2012年02月28日01_A 统计 基础货币成本  和  组合货币成本 end---//
                }
                //-------------------------------------- BUG #1071 --------------------------------------//
            }
            return this;
        } catch (Exception e) {
            throw new YssException(strError + "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String addSumField(String Fields) {
        String strSql = "";
        String[] fields = null;
        if (Fields.length() > 0) {
            fields = Fields.split(",");
            if (fields.length > 0) {
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].equalsIgnoreCase("C")) { //核算成本
                        strSql = strSql + ",sum(" +
                            dbl.sqlIsNull("a.FStorageCost", "0") +
                            ") as FStorageCost ";
                    }
                    if (fields[i].equalsIgnoreCase("M")) { //管理成本
                        strSql = strSql + ",sum(" +
                            dbl.sqlIsNull("a.FMStorageCost", "0") +
                            ") as FStorageCost ";
                    }
                    if (fields[i].equalsIgnoreCase("V")) { //估值成本
                        strSql = strSql + ",sum(" +
                            dbl.sqlIsNull("a.FVStorageCost", "0") +
                            ") as FStorageCost ";
                    }
                }
            }
        }
        return strSql;
    }

    /**
     * deleteRecycleData
     * 功能：从数据库彻底删除数据
     * @throws YssException
     * 时间：2008年6月6号
     * 修改人：蒋春
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (this.sRecycled != null && this.sRecycled.trim().length() > 0) {
                arrData = this.sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Stock_Security") +
                        " where FSecurityCode = " +
                        dbl.sqlString(this.strSecurityCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate) +
                        " and FAttrClsCode = " + dbl.sqlString(this.attrCode) +
                        " and FInvestType = " + dbl.sqlString(this.investType); //modify by fangjiang 2011.07.25 story 1156
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    //================================================fanghaoln end=========================================
                    //------ modify by wangzuochun 2010.07.08 MS01412 证券库存中两笔数据选中其中一条数据，点击反审核时，另外一条也反审核了 
                    if (this.strFAnalysisCode1 != null &&
                        this.strFAnalysisCode1.length() > 0) {
                        strSql = strSql + " and FAnalysisCode1 =" +
                            dbl.sqlString(this.strFAnalysisCode1);
                    }
                    if (this.strFAnalysisCode2 != null &&
                        this.strFAnalysisCode2.length() > 0) {
                        strSql = strSql + " and FAnalysisCode2 =" +
                            dbl.sqlString(this.strFAnalysisCode2);
                    }
                    if (this.strFAnalysisCode3 != null &&
                        this.strFAnalysisCode3.length() > 0) {
                        strSql = strSql + " and FAnalysisCode3 =" +
                            dbl.sqlString(this.strFAnalysisCode3);
                    }
                    //----------------------MS01412----------------------//
                    dbl.executeSql(strSql);

                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Stock_SecRecPay") +
                        " where FSecurityCode = " +
                        dbl.sqlString(this.strSecurityCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate) +
                        " and FAttrClsCode = " + dbl.sqlString(this.attrCode);
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    //================================================fanghaoln end=========================================
                    if (this.strFAnalysisCode1 != null &&
                        this.strFAnalysisCode1.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode1 =" +
                            dbl.sqlString(this.strFAnalysisCode1.trim());
                    }
                    if (this.strFAnalysisCode2 != null &&
                        this.strFAnalysisCode2.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode2 =" +
                            dbl.sqlString(this.strFAnalysisCode2.trim());
                    }
                    if (this.strFAnalysisCode3 != null &&
                        this.strFAnalysisCode3.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode3 =" +
                            dbl.sqlString(this.strFAnalysisCode3.trim());
                    }

                    dbl.executeSql(strSql);
                }
            } else {
                if (this.strSecurityCode != null &&
                    this.strSecurityCode.trim().length() > 0) {
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Stock_Security") +
                        " where FSecurityCode = " +
                        dbl.sqlString(this.strSecurityCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate) +
                        " and FAttrClsCode = " + dbl.sqlString(this.attrCode);
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    //================================================fanghaoln end=========================================
                    if (this.strFAnalysisCode1 != null &&
                        this.strFAnalysisCode1.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode1 =" +
                            dbl.sqlString(this.strFAnalysisCode1.trim());
                    }
                    if (this.strFAnalysisCode2 != null &&
                        this.strFAnalysisCode2.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode2 =" +
                            dbl.sqlString(this.strFAnalysisCode2.trim());
                    }
                    if (this.strFAnalysisCode3 != null &&
                        this.strFAnalysisCode3.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode3 =" +
                            dbl.sqlString(this.strFAnalysisCode3.trim());
                    }

                    dbl.executeSql(strSql);

                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Stock_SecRecPay") +
                        " where FSecurityCode = " +
                        dbl.sqlString(this.strSecurityCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FStorageDate=" + dbl.sqlDate(this.strStorageDate) +
                        " and FAttrClsCode = " + dbl.sqlString(this.attrCode);
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    //fanghaoln 20090821 MS00545 QDV4赢时胜（上海）2009年6月24日01_B 修改证券库存中的记录为期初数时系统会报错
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    //================================================fanghaoln end=========================================

                    if (this.strFAnalysisCode1 != null &&
                        this.strFAnalysisCode1.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode1 =" +
                            dbl.sqlString(this.strFAnalysisCode1.trim());
                    }
                    if (this.strFAnalysisCode2 != null &&
                        this.strFAnalysisCode2.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode2 =" +
                            dbl.sqlString(this.strFAnalysisCode2.trim());
                    }
                    if (this.strFAnalysisCode3 != null &&
                        this.strFAnalysisCode3.trim().length() > 0) {
                        strSql = strSql + " and FAnalysisCode3 =" +
                            dbl.sqlString(this.strFAnalysisCode3.trim());
                    }
                    dbl.executeSql(strSql);
                }
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
    
    //获取昨日期权库存数量 add by fangjiang 2010.10.18 MS01849 QDV4赢时胜（深圳）2010年10月13日01_B 
    public double getYesDateOptionsStorage() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double yesDateOptionsStorage = 0;
        try {
            strSql =
                " select FStorageAmount from " + pub.yssGetTableName("Tb_Stock_Security") + " where FCheckState = 1 and FStorageDate = "
                + dbl.sqlDate(YssFun.addDay(YssFun.parseDate(this.strStorageDate), -1)) + " and FSecurityCode = " + dbl.sqlString(this.strSecurityCode)
                + " and FPortCode = " + dbl.sqlString(this.strPortCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	yesDateOptionsStorage = rs.getDouble("FStorageAmount");
            }
        } catch (Exception e) {
        	throw new YssException("获取昨日期权库存数量出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return yesDateOptionsStorage;
    }
    //------------------------
}
