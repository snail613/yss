package com.yss.main.storagemanage;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;

/**
 * <p>Title: CostStorageBean </p>
 * <p>Description: 成本库存 </p>
 * add by wangzuochun 2009.11.05 MS00793  增加“库存管理-定期买入成本价库存”模块  QDV4赢时胜（深圳）2009年11月03日01_A   
 */

public class CostStorageBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strCashAccCode = ""; //帐户代码
    private String strCashAccName = ""; //帐户名称
    private String strStorageDate = ""; //库存日期
    private String strYearMonth = ""; //库存年月
    private String strAccBalance = ""; //帐户余额
    private String strPortCode = ""; //组合代码
    private String strPortName = ""; //组合名称
    private String strPortCuryRate = ""; //原币汇率
    private String strBaseCuryRate = ""; //基础汇率
    
    private int intStorageState = 2; //库存状态：0－自动计算（未锁定），1－自动计算（锁定），2－初始化
    private String strFAnalysisCode1 = ""; //辅助代码1
    private String strFAnalysisName1 = ""; //辅助名称1
    private String strFAnalysisCode2 = ""; //辅助代码2
    private String strFAnalysisName2 = ""; //辅助名称2
    private String strFAnalysisCode3 = ""; //辅助代码3
    private String strFAnalysisName3 = ""; //辅助名称3

    private String strBaseCuryBal = ""; //基础货币余额(新增)
    private String strPortCuryBal = ""; //组合货币余额(新增)
    private String strOldCashAccCode = "";
    private String strOldPortCode = "";
    private String strOldStorageDate = "";
    private String strOldFAnalysisCode1 = "";
    private String strOldFAnalysisCode2 = "";
    private String strOldFAnalysisCode3 = "";
    private String strIsOnlyColumn = "0";
    private String sRecycled = "";

    private CostStorageBean filterType;
    
    private String bBegin; //是否取期初数

    private String flag = "";
    private String strOldYearMonth="";
    public CostStorageBean() {
    }

    public int getIntStorageState() {
        return this.intStorageState;
    }

    public void setIntStorageState(int intStorageState) {
        this.intStorageState = intStorageState;
    }

    public String getStrAccBalance() {
        return this.strAccBalance;
    }

    public void setStrAccBalance(String strAccBalance) {
        this.strAccBalance = strAccBalance;
    }

    public String getStrBaseCuryRate() {
        return this.strBaseCuryRate;
    }

    public void setStrBaseCuryRate(String strBaseCuryRate) {
        this.strBaseCuryRate = strBaseCuryRate;
    }

    public String getStrCashAccCode() {
        return this.strCashAccCode;
    }

    public void setStrCashAccCode(String strCashAccCode) {
        this.strCashAccCode = strCashAccCode;
    }

    public String getStrCashAccName() {
        return this.strCashAccName;
    }

    public void setStrCashAccName(String strCashAccName) {
        this.strCashAccName = strCashAccName;
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

    public String getStrBaseCuryBal() {
        return this.strBaseCuryBal;
    }

    public void setStrBaseCuryBal(String strBaseCuryBal) {
        this.strBaseCuryBal = strBaseCuryBal;
    }

    public String getStrPortCuryBal() {
        return this.strPortCuryBal;
    }

    public void setStrPortCuryBal(String strPortCuryBal) {
        this.strPortCuryBal = strPortCuryBal;
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

    public String getStrStorageDate() {
        return this.strStorageDate;
    }
    
    public String getBBegin() {
        return bBegin;
    }

    public String getStrYearMonth() {
        return strYearMonth;
    }

    public String getStrOldYearMonth() {
        return strOldYearMonth;
    }

    public void setStrStorageDate(String strStorageDate) {
        this.strStorageDate = strStorageDate;
    }
    
    public void setBBegin(String bBegin) {
        this.bBegin = bBegin;
    }

    public void setStrYearMonth(String strYearMonth) {
        this.strYearMonth = strYearMonth;
    }

    public void setStrOldYearMonth(String strOldYearMonth) {
        this.strOldYearMonth = strOldYearMonth;
    }

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
            this.strCashAccCode = reqAry[0];
            this.strCashAccName = reqAry[1];
            this.strStorageDate = reqAry[2];
            this.strAccBalance = reqAry[3];
            //this.strCuryCode = reqAry[4];
            //this.strCuryName = reqAry[5];
            this.strPortCode = reqAry[4];
            this.strPortName = reqAry[5];
            this.strPortCuryRate = reqAry[6];
            this.strBaseCuryRate = reqAry[7];
            this.intStorageState = Integer.parseInt(reqAry[8]);
            this.strFAnalysisCode1 = reqAry[9];
            this.strFAnalysisName1 = reqAry[10];
            this.strFAnalysisCode2 = reqAry[11];
            this.strFAnalysisName2 = reqAry[12];
            this.strFAnalysisCode3 = reqAry[13];
            this.strFAnalysisName3 = reqAry[14];

            this.strPortCuryBal = reqAry[15];
            this.strBaseCuryBal = reqAry[16];
            this.checkStateId = Integer.parseInt(reqAry[17]);
            this.strOldCashAccCode = reqAry[18];
            this.strOldPortCode = reqAry[19];
            this.strOldStorageDate = reqAry[20];
            this.strOldFAnalysisCode1 = reqAry[21];
            this.strOldFAnalysisCode2 = reqAry[22];
            this.strOldFAnalysisCode3 = reqAry[23];
            this.strIsOnlyColumn = reqAry[24];
            this.bBegin = reqAry[25];
            this.strYearMonth = reqAry[26];
            this.strOldYearMonth = reqAry[27];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new CostStorageBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }

        } catch (Exception e) {
            throw new YssException("解析定期买入成本价库存请求信息出错", e);
        }
    }

    /**
     * buildRowStr
     * 返回属性信息
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strCashAccCode).append("\t");
        buf.append(this.strCashAccName).append("\t");
        buf.append(this.strStorageDate).append("\t");
        buf.append(this.strAccBalance).append("\t");
        buf.append(this.strPortCode).append("\t");
        buf.append(this.strPortName).append("\t");
        buf.append(this.strPortCuryRate).append("\t");
        buf.append(this.strBaseCuryRate).append("\t");
        buf.append(this.intStorageState).append("\t");
        buf.append(this.strFAnalysisCode1).append("\t");
        buf.append(this.strFAnalysisName1).append("\t");
        buf.append(this.strFAnalysisCode2).append("\t");
        buf.append(this.strFAnalysisName2).append("\t");
        buf.append(this.strFAnalysisCode3).append("\t");
        buf.append(this.strFAnalysisName3).append("\t");
        buf.append(this.strPortCuryBal).append("\t");
        buf.append(this.strBaseCuryBal).append("\t");
        buf.append(this.strYearMonth).append("\t");
        buf.append(this.bBegin).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * 新增一条库存信息
     * @throws YssException
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Stock_Cost") +
                "(FCostAccCode, FStorageDate, FPortCode, FAccBalance, " +
                " FPortCuryRate,FPortCuryBal, FBaseCuryRate,FBaseCuryBal,FAnalysisCode1, FAnalysisCode2, " +
                " FAnalysisCode3, FStorageInd, FYearMonth, FCheckState, FCreator, FCreateTime,FCheckUser) " +
                " values(" + dbl.sqlString(this.strCashAccCode) + "," +
                dbl.sqlDate(this.strStorageDate) + "," +
                dbl.sqlString(this.strPortCode) + "," +
                this.strAccBalance + "," +

                this.strPortCuryRate + "," +
                this.strPortCuryBal + "," +
                this.strBaseCuryRate + "," +
                this.strBaseCuryBal + "," +
                dbl.sqlString(this.strFAnalysisCode1.length() == 0 ? " " :
                              this.strFAnalysisCode1) + "," +
                dbl.sqlString(this.strFAnalysisCode2.length() == 0 ? " " :
                              this.strFAnalysisCode2) + "," +
                dbl.sqlString(this.strFAnalysisCode3.length() == 0 ? " " :
                              this.strFAnalysisCode3) + ",2," +
                (this.bBegin.equalsIgnoreCase("false") ?
                 dbl.sqlString(YssFun.formatDate(this.strStorageDate, "yyyyMM")) :
                 dbl.sqlString(this.strYearMonth)) + " , " +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) +
                ", " + dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                ")";
       
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql); //执行插入操作
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增定期买入成本价库存信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * checkInput
     * 数据验证
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        flag = Byte.toString(btOper);
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Stock_Cost"), "FCostAccCode,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FYearMonth",
        	//edit by guolongchao 20110713 BUG2157   QD赢时胜(测试)2011年6月23日04_B    解决报错提示：违反了唯一索引约束 ---------------------//
        		               //新值
                               (this.strCashAccCode.length() == 0 ? " " : this.strCashAccCode) + "," + 
                               (this.strPortCode .length() == 0 ? " " : this.strPortCode)+ "," +
                               (this.strFAnalysisCode1.length() == 0 ? " " : this.strFAnalysisCode1) + "," +
                               (this.strFAnalysisCode2.length() == 0 ? " " : this.strFAnalysisCode2) + "," +
                               (this.strFAnalysisCode3.length() == 0 ? " " : this.strFAnalysisCode3) + "," +
                               (this.strYearMonth.length() == 0 ? YssFun.formatDate(strYearMonth, "yyyyMM") : strYearMonth)
                                , //原始值                                                          
                               (this.strOldCashAccCode.length() == 0 ? " " : this.strOldCashAccCode) + "," + 
                               (this.strOldPortCode.length() == 0 ? " " : this.strOldPortCode) + "," +                               
                               (this.strOldFAnalysisCode1.length() == 0 ? " " : this.strOldFAnalysisCode1) + "," +
                               (this.strOldFAnalysisCode2.length() == 0 ? " " : this.strOldFAnalysisCode2) + "," +
                               (this.strOldFAnalysisCode3.length() == 0 ? " " : this.strOldFAnalysisCode3) + "," +
                               (this.strOldYearMonth.length() == 0 ? YssFun.formatDate(strOldStorageDate, "yyyyMM") : strOldYearMonth));
             //edit by guolongchao 20110713 BUG2157   QD赢时胜(测试)2011年6月23日04_B    解决报错提示：违反了唯一索引约束  end ---------------------//
    }

    /**
     * checkSetting
     * 功能：可以处理库存管理业务中定期买入成本价库存的设置审核、反审核、和回收站的还原功能。
     *      还原功能可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
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
                    strSql = "update " + pub.yssGetTableName("Tb_Stock_Cost") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where  FCostAccCode = " +
                        dbl.sqlString(this.strCashAccCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode);
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    if (!this.strFAnalysisCode1.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode1.length() > 0) {
                        strSql = strSql + " and FAnalysisCode1=" +
                            dbl.sqlString(this.strFAnalysisCode1);
                    }
                    if (!this.strFAnalysisCode2.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode2.length() > 0) {
                        strSql = strSql + " and FAnalysisCode2=" +
                            dbl.sqlString(this.strFAnalysisCode2);
                    }
                    if (!this.strFAnalysisCode3.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode3.length() > 0) {
                        strSql = strSql + " and FAnalysisCode3=" +
                            dbl.sqlString(this.strFAnalysisCode3);
                    }
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核定期买入成本价库存信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 删除一条定期买入成本价库存信息
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";

        try {
            strSql = "update " + pub.yssGetTableName("Tb_Stock_Cost") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FCostAccCode = " + dbl.sqlString(this.strCashAccCode) +
                " and FPortCode=" + dbl.sqlString(this.strPortCode);

            if (this.bBegin.equalsIgnoreCase("true")) {
                strSql = strSql + " and FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(this.strStorageDate, "yyyy") +
                                  "00");
            } else {
                strSql = strSql + " and FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(this.strStorageDate, "yyyyMM"));
            }
            
            if (!this.strFAnalysisCode1.equalsIgnoreCase("null") &&
                this.strFAnalysisCode1.length() > 0) {
                strSql = strSql + " and FAnalysisCode1=" +
                    dbl.sqlString(this.strFAnalysisCode1);
            }
            if (!this.strFAnalysisCode2.equalsIgnoreCase("null") &&
                this.strFAnalysisCode2.length() > 0) {
                strSql = strSql + " and FAnalysisCode2=" +
                    dbl.sqlString(this.strFAnalysisCode2);
            }
            if (!this.strFAnalysisCode3.equalsIgnoreCase("null") &&
                this.strFAnalysisCode3.length() > 0) {
                strSql = strSql + " and FAnalysisCode3=" +
                    dbl.sqlString(this.strFAnalysisCode3);
            }
            
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("删除定期买入成本价库存信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * editOperData
     * 编辑一条定期买入成本价库存信息
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";

        try {
            strSql = "update " + pub.yssGetTableName("Tb_Stock_Cost") +
                " set FCostAccCode = " +
                dbl.sqlString(this.strCashAccCode) + ", FStorageDate = "
                + dbl.sqlDate(this.strStorageDate) + ",FPortCode = "
                + dbl.sqlString(this.strPortCode) + ", FAccBalance ="
                + this.strAccBalance + ", FPortCuryRate="
                + this.strPortCuryRate + ",FPortCuryBal = "
                + this.strPortCuryBal + ",FBaseCuryRate ="
                + this.strBaseCuryRate + ",FBaseCuryBal = "
                + this.strBaseCuryBal + ",FAnalysisCode1 = "
                +
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
                ",FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCreator = "
                + dbl.sqlString(this.creatorCode) + " , FCreateTime = "
                + dbl.sqlString(this.creatorTime) +
                ",FYearMonth = " +
                (this.bBegin.equalsIgnoreCase("false") ?
                 dbl.sqlString(YssFun.formatDate(this.strStorageDate, "yyyyMM")) :
                 dbl.sqlString(this.strYearMonth)) +
                ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FCostAccCode = " +
                dbl.sqlString(this.strOldCashAccCode) +
                " and FPortCode=" + dbl.sqlString(this.strOldPortCode) +
                " and FYearMonth =" + dbl.sqlString(this.strOldYearMonth);
            
            if (!this.strOldFAnalysisCode1.equalsIgnoreCase("null") &&
                this.strOldFAnalysisCode1.length() > 0) {
                strSql = strSql + " and FAnalysisCode1=" +
                    dbl.sqlString(this.strOldFAnalysisCode1);
            }
            if (!this.strOldFAnalysisCode2.equalsIgnoreCase("null") &&
                this.strOldFAnalysisCode2.length() > 0) {
                strSql = strSql + " and FAnalysisCode2=" +
                    dbl.sqlString(this.strOldFAnalysisCode2);
            }
            if (!this.strOldFAnalysisCode3.equalsIgnoreCase("null") &&
                this.strOldFAnalysisCode3.length() > 0) {
                strSql = strSql + " and FAnalysisCode3=" +
                    dbl.sqlString(this.strOldFAnalysisCode3);
            }

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改定期买入成本价库存信息出错", e);
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
                this.filterType.strStorageDate = this.filterType.strStorageDate.
                    substring(0, 5) +
                    "01-01";
            }
            
            if (this.filterType.strIsOnlyColumn.equals("0")) {
                sResult = sResult + " and 1 = 2";
                return sResult;
            }

            if (this.filterType.strCashAccCode.length() != 0) {
                sResult = sResult + " and a.FCostAccCode like '" +
                    filterType.strCashAccCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strPortCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.strPortCode.replaceAll("'", "''") + "%'";
            } 
            
            if (this.filterType.strAccBalance.length() != 0) {
                sResult = sResult + " and a.FAccBalance like '" +
                    filterType.strAccBalance.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.strPortCuryRate.length() != 0) {
                sResult = sResult + " and a.FPortCuryRate = " +
                    dbl.sqlString(filterType.strPortCuryRate);
            }
            if (this.filterType.strPortCuryBal.length() != 0) {
                sResult = sResult + " and a.FPortCuryBal= " +
                    dbl.sqlString(filterType.strPortCuryBal);
            }

            if (this.filterType.strBaseCuryRate.length() != 0) {
                sResult = sResult + " and a.FBaseCuryRate = " +
                    dbl.sqlString(filterType.strBaseCuryRate);
            }

            if (this.filterType.strBaseCuryBal.length() != 0) {
                sResult = sResult + " and a.FBaseCuryBal = " +
                    dbl.sqlString(filterType.strBaseCuryBal);
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
                sResult = sResult + " and a.FStorageDate = " +
                    dbl.sqlDate(filterType.strStorageDate);
            }
        }
        return sResult;
    }

    /**
     * 获取辅助字段之查询Sql语句
     * @return String
     */
    public String FilterSql() throws YssException, SQLException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
            pub.yssGetTableName("Tb_Para_StorageCfg") +
            " where FCheckState = 1 and FStorageType = " +
            dbl.sqlString(YssOperCons.YSS_KCLX_Cash);
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
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
          
                    
                    " left join (select FExchangeCode,FExchangeName as FAnalysisName" +
                    i +
                    " from tb_base_exchange) e on a.FAnalysisCode" + i +
                    " = e.FExchangeCode " +
                    " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName3 from  " +
                    pub.yssGetTableName("tb_para_investmanager") +
                    " n where n.FCheckState = 1 ) exchange on a.FAnalysisCode" +
                    i + " = exchange.FInvMgrCode";
                    
                    
                    //end by lidaolong
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("004")) {
                    sResult = sResult +
                        " left join (select FCatCode,FCatName as FAnalysisName2 from Tb_Base_Category where FCheckState = 1) category on a.FAnalysisCode" +
                        i + " = category.FCatCode";
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("001")) {
                    sResult = sResult +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                    
                 /*       " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                        i +
                        "  from  (select FInvMgrCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " where FStartDate < " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FInvMgrCode )m " +
                        "join (select * from " +
                        pub.yssGetTableName("tb_para_investmanager") + ") n on m.FInvMgrCode = n.FInvMgrCode and m.FStartDate = n.FStartDate) invmgr on a.FAnalysisCode" +
                        i + " = invmgr.FInvMgrCode ";*/
                    
                    " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                    i +
                    "  from " +
                    pub.yssGetTableName("tb_para_investmanager") +
                    " n where n.FCheckState = 1 ) invmgr on a.FAnalysisCode" +
                    i + " = invmgr.FInvMgrCode ";
                    
                    //end by lidaolong
                }
                else {
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
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FPortName,d.FStorageInitDate, j.FCashAccName as FCostAccName, d.Fportcury as FPortCuryCode ";
            strSql = strSql +
                (FilterSql().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ") +
                " from " + pub.yssGetTableName("Tb_Stock_Cost") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
            /*    " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName,o.FStorageInitDate as FStorageInitDate,o.Fportcury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") + " o join " +
                "(select FPortCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") + " " +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 and FASSETGROUPCODE = " +
                dbl.sqlString(pub.getAssetGroupCode()) +
                " group by FPortCode) p " +
                " on o.FPortCode = p.FPortCode and o.FStartDate = p.FStartDate) d on a.FPortCode = d.FPortCode" +
               
               
                FilterSql() + " left join (select FCashAccCode, FCashAccName, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FCashAccCode, FCashAccName) j on a.FCostAccCode = j.FCashAccCode " +
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";*/
                " left join (select FPortCode, FPortName, FStorageInitDate,Fportcury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") + 
              
                " where  FCheckState = 1 and FASSETGROUPCODE = " +
                dbl.sqlString(pub.getAssetGroupCode()) +
               
                " ) d on a.FPortCode = d.FPortCode" +
               
               
                FilterSql() + " left join (select FCashAccCode, FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where  FCheckState = 1 ) j on a.FCostAccCode = j.FCashAccCode " +
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
            
            //end by lidaolong
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.strCashAccCode = rs.getString("FCostAccCode") + "";
                this.strCashAccName = rs.getString("FCostAccName") + "";
                this.strStorageDate = rs.getDate("FStorageDate") + "";
                this.strAccBalance = rs.getDouble("FAccBalance") + "";
                this.strPortCode = rs.getString("FPortCode") + "";
                this.strPortName = rs.getString("FPortName") + "";
                this.strPortCuryRate = rs.getDouble("FPortCuryRate") + "";
                this.strPortCuryBal = rs.getDouble("FPortCuryBal") + "";

                this.strBaseCuryRate = rs.getDouble("FBaseCuryRate") + "";

                this.strBaseCuryBal = rs.getDouble("FBaseCuryBal") + "";
                this.intStorageState = rs.getInt("FStorageInd");
                this.strFAnalysisCode1 = rs.getString("FAnalysisCode1") + "";
                this.strFAnalysisName1 = rs.getString("FAnalysisName1") + "";
                this.strFAnalysisCode2 = rs.getString("FAnalysisCode2") + "";
                this.strFAnalysisName2 = rs.getString("FAnalysisName2") + "";
                this.strFAnalysisCode3 = rs.getString("FAnalysisCode3") + "";
                this.strFAnalysisName3 = rs.getString("FAnalysisName3") + "";

                this.strYearMonth = rs.getString("FYearMonth") + "";

                super.setRecLog(rs);

                setResultSetAttr(rs);
                /**
                if (pub.getBaseCury().equalsIgnoreCase(rs.getString("FCuryCode"))) { //MS00177 如果原币等于基础货币，基础货币余额都取原币余额 byleeyu
                    strBaseCuryBal = strAccBalance;
                }
                if (rs.getString("FPortCuryCode").equalsIgnoreCase(rs.getString("FCuryCode"))) {
                    strPortCuryBal = strAccBalance;
                }
                **/
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }

            AssetStorageCfgBean assetgroupcfg = new AssetStorageCfgBean();
            assetgroupcfg.setYssPub(pub);
            sDateStr = assetgroupcfg.getPartSetting("Cash");

            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + sDateStr;
        } catch (Exception e) {
            throw new YssException("获取现金帐户库存信息出错 \r\n" + e.getMessage(), e);
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
    public String getListViewData3() {
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

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.strCashAccCode = rs.getString("FCostAccCode") + "";
        this.strStorageDate = rs.getDate("FStorageDate") + "";
        this.strAccBalance = rs.getDouble("FAccBalance") + "";
        this.strPortCode = rs.getString("FPortCode") + "";
        this.strPortCuryRate = rs.getDouble("FPortCuryRate") + "";
        this.strPortCuryBal = rs.getDouble("FPortCuryBal") + "";
        this.strBaseCuryRate = rs.getDouble("FBaseCuryRate") + "";
        this.strBaseCuryBal = rs.getDouble("FBaseCuryBal") + "";
        this.intStorageState = rs.getInt("FStorageInd");
        this.strFAnalysisCode1 = rs.getString("FAnalysisCode1") + "";
        this.strFAnalysisCode2 = rs.getString("FAnalysisCode2") + "";
        this.strFAnalysisCode3 = rs.getString("FAnalysisCode3") + "";
        this.strYearMonth = rs.getString("FYearMonth") + "";
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
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
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
        CostStorageBean befEditBean = new CostStorageBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FPortName,d.FStorageInitDate, j.FCashAccName as FCostAccName";
            strSql = strSql +
                (FilterSql().length() == 0 ?
                 ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
                 ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ") +
                " from " + pub.yssGetTableName("Tb_Stock_Cost") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
               /* " left join (select o.FPortCode as FPortCode,o.FPortName as FPortName,o.FStorageInitDate as FStorageInitDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") + " o join " +
                "(select FPortCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") + " " +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 and FASSETGROUPCODE = " +
                dbl.sqlString(pub.getAssetGroupCode()) +
                " group by FPortCode) p " +
                " on o.FPortCode = p.FPortCode and o.FStartDate = p.FStartDate) d on a.FPortCode = d.FPortCode" +
                
                FilterSql() + " left join (select FCashAccCode, FCashAccName, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FCashAccCode, FCashAccName) j on a.FCostAccCode = j.FCashAccCode " +
                " where a.FCostAccCode = " +
                dbl.sqlString(this.strOldCashAccCode) +
                " and a.FPortCode=" + dbl.sqlString(this.strOldPortCode) +
                " and a.FStorageDate=" + dbl.sqlDate(this.strOldStorageDate);*/
                
                " left join (select  FPortCode, FPortName, FStorageInitDate from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +               
                " where FCheckState = 1 and FASSETGROUPCODE = " +
                dbl.sqlString(pub.getAssetGroupCode()) +       
                " ) d on a.FPortCode = d.FPortCode" +
                
                FilterSql() + " left join (select FCashAccCode, FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where  FCheckState = 1 ) j on a.FCostAccCode = j.FCashAccCode " +
                " where a.FCostAccCode = " +
                dbl.sqlString(this.strOldCashAccCode) +
                " and a.FPortCode=" + dbl.sqlString(this.strOldPortCode) +
                " and a.FStorageDate=" + dbl.sqlDate(this.strOldStorageDate);
            
            //end by lidaolong 
            if (!this.strOldYearMonth.equalsIgnoreCase("null") &&
                    this.strOldYearMonth.length() > 0) {
                strSql = strSql + " and a.FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(this.strOldStorageDate,"yyyyMM"));
            }

            if (!this.strOldFAnalysisCode1.equalsIgnoreCase("null") &&
                this.strOldFAnalysisCode1.length() > 0) {
                strSql = strSql + " and a.FAnalysisCode1=" +
                    dbl.sqlString(this.strOldFAnalysisCode1);
            }
            if (!this.strOldFAnalysisCode2.equalsIgnoreCase("null") &&
                this.strOldFAnalysisCode2.length() > 0) {
                strSql = strSql + " and a.FAnalysisCode2=" +
                    dbl.sqlString(this.strOldFAnalysisCode2);
            }
            if (!this.strOldFAnalysisCode3.equalsIgnoreCase("null") &&
                this.strOldFAnalysisCode3.length() > 0) {
                strSql = strSql + " and a.FAnalysisCode3=" +
                    dbl.sqlString(this.strOldFAnalysisCode3);
            }

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.strCashAccCode = rs.getString("FCostAccCode") + "";
                befEditBean.strCashAccName = rs.getString("FCostAccName") + "";
                befEditBean.strStorageDate = rs.getDate("FStorageDate") + "";
                befEditBean.strAccBalance = rs.getDouble("FAccBalance") + "";
                befEditBean.strPortCode = rs.getString("FPortCode") + "";
                befEditBean.strPortName = rs.getString("FPortName") + "";
                befEditBean.strPortCuryRate = rs.getDouble("FPortCuryRate") + "";
                befEditBean.strPortCuryBal = rs.getDouble("FPortCuryBal") + "";
                befEditBean.strBaseCuryRate = rs.getDouble("FBaseCuryRate") + "";
                befEditBean.strBaseCuryBal = rs.getDouble("FBaseCuryBal") + "";
                befEditBean.intStorageState = rs.getInt("FStorageInd");
                befEditBean.strFAnalysisCode1 = rs.getString("FAnalysisCode1") + "";
                befEditBean.strFAnalysisName1 = rs.getString("FAnalysisName1") + "";
                befEditBean.strFAnalysisCode2 = rs.getString("FAnalysisCode2") + "";
                befEditBean.strFAnalysisName2 = rs.getString("FAnalysisName2") + "";
                befEditBean.strFAnalysisCode3 = rs.getString("FAnalysisCode3") + "";
                befEditBean.strFAnalysisName3 = rs.getString("FAnalysisName3") + "";

                befEditBean.strYearMonth = rs.getString("FYearMonth") + "";

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * deleteRecycleData
     * 功能：从数据库彻底删除数据
     * @throws YssException
     * 时间：2008年6月11号
     * 修改人：蒋春
     */
    public void deleteRecycleData() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
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
                    strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Cost") +
                        " where FCostAccCode = " +
                        dbl.sqlString(this.strCashAccCode) +
                        " and FPortCode=" + dbl.sqlString(this.strPortCode);
                    strSql = strSql + " and FYearMonth = " +
                        dbl.sqlString(this.strYearMonth);
                    if (!this.strFAnalysisCode1.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode1.length() > 0) {
                        strSql = strSql + " and FAnalysisCode1=" +
                            dbl.sqlString(this.strFAnalysisCode1);
                    }
                    if (!this.strFAnalysisCode2.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode2.length() > 0) {
                        strSql = strSql + " and FAnalysisCode2=" +
                            dbl.sqlString(this.strFAnalysisCode2);
                    }
                    if (!this.strFAnalysisCode3.equalsIgnoreCase("null") &&
                        this.strFAnalysisCode3.length() > 0) {
                        strSql = strSql + " and FAnalysisCode3=" +
                            dbl.sqlString(this.strFAnalysisCode3);
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
