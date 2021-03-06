package com.yss.main.storagemanage;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;

/*
 * add by wangzuochun 2009.11.19 MS00818 增加“库存管理-债券买入利息”模块 QDV4中保2009年11月19日01_A 
 */
public class PurBondInterestBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strSecurityCode = "";    //证券代码
    private String strSecurityName = "";    //证券名称

    private String strMStorageCost = "";    //管理成本
    private String strVStorageCost = "";    //估值成本
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

    private String strOldYearMonth = "";    //原始库存年月

    private String strOldPortCode = "";
    private String strOldFAnalysisCode1 = "";
    private String strOldFAnalysisCode2 = "";
    private String strOldFAnalysisCode3 = "";
    private String strOldStorageDate = "";

    private PurBondInterestBean filterType;
    private String strIsOnlyColumn = "0";

    private String strPortCuryCost = "";    //组合货币核算成本
    private String strMPortCuryCost = "";   //组合货币管理成本
    private String strVPortCuryCost = "";   //组合货币核算成本
    private String strBaseCuryCost = "";    //基础货币核算成本
    private String strMBaseCuryCost = "";   //基础货币管理成本
    private String strVBaseCuryCost = "";   //基础货币估值成本


    private String attrCode = "";   //属性代码
    private String attrName = "";   //属性代码名称

    private String oldAttrCode = "";

    private String sRecycled = "";

    private String bBegin; //是否取期初数

    private PurBondInterestBean bondBean = null;

    public PurBondInterestBean getSecurityBean() {
        return bondBean;
    }

    public void setSecurityBean(PurBondInterestBean bondBean) {
        this.bondBean = bondBean;
    }

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

    public PurBondInterestBean() {
    }

    public String getStrBaseCuryRate() {
        return this.strBaseCuryRate;
    }

    public void setStrBaseCuryRate(String strBaseCuryRate) {
        this.strBaseCuryRate = strBaseCuryRate;
    }
    public PurBondInterestBean getFilterType() {
        return this.filterType;
    }

    public void setFilterType(PurBondInterestBean filterType) {
        this.filterType = filterType;
    }

    public String getStrIsOnlyColumn() {
        return this.strIsOnlyColumn;
    }

    public void setStrIsOnlyColumn(String strIsOnlyColumn) {
        this.strIsOnlyColumn = strIsOnlyColumn;
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

    /**
     * addOperData
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Stock_PurBond") +
                "(FSecurityCode,FYearMonth,FStorageDate,FPortCode,FStorageAmount,FStorageCost," +
                " FMStorageCost,FVStorageCost,FPortCuryRate,FPortCuryCost,FMPortCuryCost,FVPortCuryCost,FBaseCuryRate,FBaseCuryCost,FMBaseCuryCost,FVBaseCuryCost,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FStorageInd,FAttrClsCode," +
                " FCheckState, FCreator, FCreateTime,FCheckUser) " +
                " values(" + dbl.sqlString(this.strSecurityCode) + "," +
                (this.bBegin.equalsIgnoreCase("false") ?
                 dbl.sqlString(YssFun.formatDate(this.strStorageDate, "yyyyMM")) : 
                 dbl.sqlString(this.strYearMonth)) + " , " +
                dbl.sqlDate(this.strStorageDate) + "," +
                dbl.sqlString(this.strPortCode) + "," +
                this.strStorageAmount + "," +
                this.strStorageCost + "," +

                this.strMStorageCost + "," +
                this.strVStorageCost + "," +

                this.strPortCuryRate + "," + 
                this.strPortCuryCost + "," +
                this.strMPortCuryCost + "," +
                this.strVPortCuryCost + "," +
                this.strBaseCuryRate + "," +
                this.strBaseCuryCost + "," +
                this.strMBaseCuryCost + "," +
                this.strVBaseCuryCost + "," +

                dbl.sqlString(this.strFAnalysisCode1.length() == 0 ? " " :
                              this.strFAnalysisCode1) + "," +
                dbl.sqlString(this.strFAnalysisCode2.length() == 0 ? " " :
                              this.strFAnalysisCode2) + "," +
                dbl.sqlString(this.strFAnalysisCode3.length() == 0 ? " " :
                              this.strFAnalysisCode3) + ",2," + //2不能带单引号
                (this.attrCode.length() > 0 ? dbl.sqlString(this.attrCode) :
                 dbl.sqlString(" ")) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) +
                ", " + dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增债券买入利息设置信息出错", e);
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

        buf.append(this.strStorageDate).append("\t");
        buf.append(this.strYearMonth).append("\t");
        buf.append(this.bBegin).append("\t");
        buf.append(this.attrCode).append("\t");
        buf.append(this.attrName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * checkInput
     * 数据验证
     * @desc  调整调用checkInputCommon方法时OldValue的值
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {

        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Stock_PurBond"),
                               "FSecurityCode,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAttrClsCode,FYearMonth",

                               this.strSecurityCode + "," + this.strPortCode + "," +
                               this.strFAnalysisCode1 + "," +
                               this.strFAnalysisCode2 + "," +
                               this.strFAnalysisCode3 + "," +
                               this.attrCode + "," + 
                               (this.strYearMonth.length() == 0 ?
                                (this.bBegin.equalsIgnoreCase("false") ? YssFun.formatDate(strStorageDate, "yyyyMM") : strStorageDate.substring(0, 4) + "00") : strYearMonth),

                               this.strOldStorageCode + "," + this.strOldPortCode + "," +
                               this.strOldFAnalysisCode1 + "," +
                               this.strOldFAnalysisCode2 + "," +
                               this.strOldFAnalysisCode3 + "," +
                               this.oldAttrCode + "," +
                               //对原始的YearMonth判断，如果存在原始的YearMonth，直接取，否则取老的债券买入利息日期的年月
                               (this.strOldYearMonth.length() == 0 ? YssFun.formatDate(strOldStorageDate, "yyyyMM") : strOldYearMonth));
    }

    /**
     * checkSetting
     * 功能：可以处理债券买入利息的设置审核、反审核、和回收站的还原功能。
     *      还原功能可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {

        Connection conn = dbl.loadConnection(); //获取一个连接
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        String[] arrData = null;

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
                    strSql = "update " + pub.yssGetTableName("Tb_Stock_PurBond") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FSecurityCode = " +
                        dbl.sqlString(this.strSecurityCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FAttrClsCode = " + dbl.sqlString(this.attrCode);
                    //=修改它的更新条件，还原功能没有状态，这个状态并没有保存在数据库，数据在前台已经判断了，后台没必要在判断========
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
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
            throw new YssException("审核债券买入利息设置信息出错", e);
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
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Stock_PurBond") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FSecurityCode = " + dbl.sqlString(this.strSecurityCode) +
                " and FPortCode=" + dbl.sqlString(this.strPortCode) +
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

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
        } catch (Exception e) {
            throw new YssException("删除债券买入利息设置信息出错", e);
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
        try {
 
            strSql = "update " + pub.yssGetTableName("Tb_Stock_PurBond") +
                " set FSecurityCode = "
                + dbl.sqlString(this.strSecurityCode) + ", FStorageDate = "
                + dbl.sqlDate(this.strStorageDate) + ",FPortCode = "
                + dbl.sqlString(this.strPortCode) + ", FStorageAmount = "
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
                ",FAttrClsCode = "
                +
                (this.attrCode.length() > 0 ? dbl.sqlString(this.attrCode) :
                 dbl.sqlString(" ")) + ",FCreator = "
                + dbl.sqlString(this.creatorCode) + " , FCreateTime = "
                + dbl.sqlString(this.creatorTime) +
                ",FYearMonth = " + dbl.sqlString(this.strYearMonth) +
                " where FSecurityCode = " + dbl.sqlString(this.strOldStorageCode) +
                " and FPortCode=" + dbl.sqlString(this.strOldPortCode) +
                " and FAnalysisCode1=" + dbl.sqlString(this.strOldFAnalysisCode1) +
                " and FAnalysisCode2=" + dbl.sqlString(this.strOldFAnalysisCode2) +
                " and FAnalysisCode3=" + dbl.sqlString(this.strOldFAnalysisCode3);
            strSql = strSql + " and FYearMonth = " + dbl.sqlString(this.strOldYearMonth);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改债券买入利息设置信息出错", e);
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
        if (this.filterType != null) {
            if (this.filterType.bBegin.equalsIgnoreCase("false")) {
                sResult = " where 1=1 and " + dbl.sqlRight("FYearMonth", 2) +
                    " <> '00'";
            } else if (this.filterType.bBegin.equalsIgnoreCase("true")) {
                sResult = " where 1=1 and a.FYearMonth = '" +
                    this.filterType.strStorageDate.substring(0, 4) + "00'";
                this.filterType.strStorageDate = this.filterType.strStorageDate;
            }
            if (this.filterType.strIsOnlyColumn.equals("0")) {
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
                //根据查询内容是否为期初数来确定是否使用日期筛选，期初数不适用日期进行筛选
                if ("false".equalsIgnoreCase(this.filterType.bBegin)) {
                    sResult = sResult + " and a.FStorageDate = " +
                        dbl.sqlDate(filterType.strStorageDate);
                }
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
                    " from  " +
                    pub.yssGetTableName("tb_para_broker") +
                    " y where y.FCheckState = 1) broker on a.FAnalysisCode" +
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
                    "  from  " +
                    pub.yssGetTableName("tb_para_investmanager") +
                    " n where n.FCheckState = 1 ) invmgr on a.FAnalysisCode" +
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
        dbl.closeResultSetFinal(rs);
        return sResult;
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sDateStr = "";
        String strSql = "";
        ResultSet rs = null;
        boolean bTrans = false;
        String strSql1 = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            System.out.println("表头:" + sHeader);
            strSql =
                "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, " +
                " f.FSecurityName,h.FPortName as FPortName,j.FAttrClsName as FAttrClsName";
            strSql = strSql +
                (storageAnalysis().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ") +
                " from " + pub.yssGetTableName("Tb_Stock_PurBond") +
                " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                storageAnalysis() +

                " left join (select o.FSecurityCode as FSecurityCode,o.FSecurityName as FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + " o join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) f on a.FSecurityCode = f.FSecurityCode" +

                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
     
                " left join (select FPortCode ,FPortName,FStorageInitDate from " +
                pub.yssGetTableName("tb_para_portfolio") + " " +
                " where  FCheckState = 1 ) h on a.FPortCode = h.FPortCode" +
             
                
             //end by lidaolong   
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                ") j on a.FAttrClsCode = j.FAttrClsCode " +

                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.strSecurityCode = rs.getString("FSecurityCode") + "";
                this.strSecurityName = rs.getString("FSecurityName") + "";

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
                this.strStorageDate = rs.getDate("FStorageDate") + "";
                this.strYearMonth = rs.getString("FYearMonth") + "";
                this.attrCode = rs.getString("FAttrClsCode") + "";
                this.attrName = rs.getString("FAttrClsName") + "";
                super.setRecLog(rs);

                setResultSetAttr(rs);

                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }

            AssetStorageCfgBean assetgroupcfg = new AssetStorageCfgBean();
            assetgroupcfg.setYssPub(pub);
            sDateStr = assetgroupcfg.getPartSetting("Security");

            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + this.getListView1ShowCols() + "\r\f" + sDateStr ;
        } catch (Exception e) {
            throw new YssException("获取证券债券买入利息信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
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
            } else {
                sTmpStr = sRowStr;
            }
            this.sRecycled = sRowStr;

            reqAry = sTmpStr.split("\t");

            this.strSecurityCode = reqAry[0];
            this.strSecurityName = reqAry[1];

            this.strFAnalysisCode1 = reqAry[2];
            this.strFAnalysisName1 = reqAry[3];
            this.strFAnalysisCode2 = reqAry[4];
            this.strFAnalysisName2 = reqAry[5];
            this.strFAnalysisCode3 = reqAry[6];
            this.strFAnalysisName3 = reqAry[7];
            this.strPortCode = reqAry[8];
            this.strPortName = reqAry[9];

            this.strPortCuryRate = reqAry[10];
            this.strPortCuryCost = reqAry[11];
            this.strMPortCuryCost = reqAry[12];
            this.strVPortCuryCost = reqAry[13];

            this.strBaseCuryRate = reqAry[14];
            this.strBaseCuryCost = reqAry[15];
            this.strMBaseCuryCost = reqAry[16];
            this.strVBaseCuryCost = reqAry[17];
            this.strStorageAmount = reqAry[18];
            this.strStorageCost = reqAry[19];
            this.strMStorageCost = reqAry[20];
            //解析估值 成本
            this.strVStorageCost = reqAry[21];

            this.checkStateId = Integer.parseInt(reqAry[22]);

            this.strStorageDate = reqAry[23];

            this.strOldStorageCode = reqAry[24];
            this.strOldPortCode = reqAry[25];

            this.strOldFAnalysisCode1 = reqAry[26];
            this.strOldFAnalysisCode2 = reqAry[27];
            this.strOldFAnalysisCode3 = reqAry[28];
            this.strOldStorageDate = reqAry[29];
            this.strIsOnlyColumn = reqAry[30];
            this.bBegin = reqAry[31];
            this.strYearMonth = reqAry[32];
            this.attrCode = reqAry[33];
            this.oldAttrCode = reqAry[34];
            this.strOldYearMonth = reqAry[35];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new PurBondInterestBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析债券买入利息设置请求出错", e);
        }
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException {
        this.strSecurityCode = rs.getString("FSecurityCode") + "";
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
        this.strStorageDate = rs.getDate("FStorageDate") + "";
        this.strYearMonth = rs.getString("FYearMonth") + "";
        this.attrCode = rs.getString("FAttrClsCode") + "";
        this.attrName = rs.getString("FAttrClsName") + "";
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
    public String getOperValue(String sType) {
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

    public void setStrVBaseCuryCost(String strVBaseCuryCost) {
        this.strVBaseCuryCost = strVBaseCuryCost;
    }

    public void setBBegin(String bBegin) {
        this.bBegin = bBegin;
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
     * 根据筛选条件获取债券买入利息数据的一个实例信息
     * @return IDataSetting
     */
    public IDataSetting getSetting() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = "SELECT * FROM " + pub.yssGetTableName("Tb_Stock_PurBond") + " a "
                + buildFilterSql();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.strSecurityCode = rs.getString("FSecurityCode") + "";
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
                this.strStorageDate = rs.getDate("FStorageDate") + "";
                this.strYearMonth = rs.getString("FYearMonth") + "";
                this.attrCode = rs.getString("FAttrClsCode") + "";
                this.checkStateId = rs.getInt("FCheckState");
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FCreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime");
            }
        } catch (Exception e) {
            throw new YssException("获取债券买入利息信息出错！", e);
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
    	PurBondInterestBean befEditBean = new PurBondInterestBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, " +
                " f.FSecurityName,h.FPortName as FPortName,j.FAttrClsName as FAttrClsName";
            strSql = strSql +
                (storageAnalysis().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ") +
                " from " + pub.yssGetTableName("Tb_Stock_PurBond") +
                " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                
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
                dbl.sqlString(this.strOldStorageCode) + 
                " and FStorageDate=" + dbl.sqlDate(this.strOldStorageDate) +
                " and a.FPortCode=" + dbl.sqlString(this.strOldPortCode) + 
                " order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.strSecurityCode = rs.getString("FSecurityCode") + "";
                befEditBean.strSecurityName = rs.getString("FSecurityName") + "";

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
                befEditBean.strStorageDate = rs.getDate("FStorageDate") + "";
                befEditBean.strYearMonth = rs.getString("FYearMonth") + "";
                befEditBean.attrCode = rs.getString("FAttrClsCode") + "";
                befEditBean.attrName = rs.getString("FAttrClsName") + "";
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getAttrCode() {
        return attrCode;
    }

    public String getAttrName() {
        return attrName;
    }

    public String getStrYearMonth() {
        return strYearMonth;
    }

    public String getOldAttrCode() {
        return oldAttrCode;
    }

    public String getStrOldYearMonth() {
        return strOldYearMonth;
    }

    public void setAttrCode(String attrCode) {
        this.attrCode = attrCode;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public void setStrYearMonth(String strYearMonth) {
        this.strYearMonth = strYearMonth;
    }

    public void setOldAttrCode(String oldAttrCode) {
        this.oldAttrCode = oldAttrCode;
    }

    public void setStrOldYearMonth(String strOldYearMonth) {
        this.strOldYearMonth = strOldYearMonth;
    }
    public PurBondInterestBean getStorageCost(java.util.Date dDate,
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
        double dReCost = 0;
        boolean analy1;
        boolean analy2;
        boolean analy3;
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            //---------------------------------------------------------------
            if (YssFun.getMonth(dDate) == 1 && YssFun.getDay(dDate) == 1) {
                strTmpSql = strTmpSql + " and fyearmonth ='" +
                    YssFun.formatDate(dDate, "yyyy") + "00'";
            } else {
                strTmpSql = strTmpSql + " and fyearmonth <>'" +
                    YssFun.formatDate(dDate, "yyyy") + "00'" +
                    " and FStorageDate = " + dbl.sqlDate(YssFun.addDay(dDate, -1));
            }
            //-----------------------------------------------------------------------------------------------------
            strSql = "select sum(t.FStorageAmount) as FSumStorageAmount, sum(t.FStorageCost) as FSumStorageCost " +
                " from ( select sum(" +
                dbl.sqlIsNull("a.FStorageAmount", "0") +
                ") as FStorageAmount " + addSumField(sCostType) + " from " +
                pub.yssGetTableName("Tb_Stock_PurBond") +
                " a where FCheckState = 1 and FPortCode= " +
                dbl.sqlString(sPortCode) +
                " and FSecurityCode = " + dbl.sqlString(sSecurityCode) +
                strTmpSql
                +
                ( (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) ?
                 " and FAnalysisCode1=" + dbl.sqlString(sAnalysisCode1) : " ") +
                ( (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) ?
                 " and FAnalysisCode1=" + dbl.sqlString(sAnalysisCode1) : " ") +
                ( (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) ?
                 " and FAnalysisCode1=" + dbl.sqlString(sAnalysisCode1) : " ") +
                ( (attrCode != null && attrCode.trim().length() > 0) ?
                 " and FAttrClsCode =" + dbl.sqlString(attrCode) : " ");
            strSql = strSql +
                " union all select sum( m.FTradeAmount * n.FAmountInd) as FStorageAmount," +
                " sum( m.FTotalCost*n.FCashInd ) as FStorageCost " +
                "from(select fTradeTypeCode, FTradeAmount, FTotalCost from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FCheckState=1 and FPortCode=" + dbl.sqlString(sPortCode) +
                " and FSecurityCode =" + dbl.sqlString(sSecurityCode) +
                " and FBargainDate= " +
                dbl.sqlDate(dDate) +
                ( (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) ?
                 " and FAnalysisCode1=" + dbl.sqlString(sAnalysisCode1) : " ") +
                ( (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) ?
                 " and FAnalysisCode1=" + dbl.sqlString(sAnalysisCode1) : " ") +
                ( (sAnalysisCode1 != null && sAnalysisCode1.trim().length() > 0) ?
                 " and FAnalysisCode1=" + dbl.sqlString(sAnalysisCode1) : " ") +
                ( (attrCode != null && attrCode.trim().length() > 0) ?
                 " and FAttrClsCode =" + dbl.sqlString(attrCode) : " ");
            strSql = strSql +
                " ) m join (select FTradeTypeCode, FCashInd, FAmountInd from " +
                " Tb_Base_TradeType where FCheckState=1)n on m.FTradeTypeCode = n.FTradeTypeCode) t";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.setStrStorageCost(YssFun.formatNumber(YssFun.roundIt(rs.
                    getDouble("FSumStorageCost"), 2), "#,##0.##"));
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
                        pub.yssGetTableName("Tb_Stock_PurBond") +
                        " where FSecurityCode = " +
                        dbl.sqlString(this.strSecurityCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode) +
                        " and FAttrClsCode = " + dbl.sqlString(this.attrCode);
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
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
}
