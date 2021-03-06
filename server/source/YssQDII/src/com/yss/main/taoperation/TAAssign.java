package com.yss.main.taoperation;

import java.io.*;
import java.sql.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class TAAssign
    extends BaseDataSettingBean implements IDataSetting, Serializable {
    private Date FAssignDate; //分盘日期
    private String FPortCode; //组合代码
    private String FPortName; //组合名称

    private String FPortClsCode; //分级代码
    private String FPortClsName; //分级名称
    private String FCalcWay; //计算方式

    private String FAnalysisName1; //分析名称1
    private String FAnalysisName2; //分析名称2
    private String FAnalysisName3; //分析名称3

    private String FAnalysisCode1; //分析代码1
    private String FAnalysisCode2; //分析代码2
    private String FAnalysisCode3; //分析代码3
    private double FAssginScale = 1.0; //分盘比例
    private String FDesc; //描述
    private int FCheckState; //审核状态
    private String FCreator; //创建人,修改人
    private String FCreateTime; //创建,修改时间
    private String FCheckUser; //复核人
    private String FCheckTime; //复核时间

    private Date OldAssignDate;
    private String OldPortCode;
    private String OldPortClsCode;
    private String OldAnalysisCode1;
    private String OldAnalysisCode2;
    private String OldAnalysisCode3;

    private TAAssign filterType;
    private String sRecycled = null; //保存未解析前的字符串

    public TAAssign() {
    }

    public String getFAnalysisCode1() {
        return FAnalysisCode1;
    }

    public String getFAnalysisCode2() {
        return FAnalysisCode2;
    }

    public String getFAnalysisCode3() {
        return FAnalysisCode3;
    }

    public double getFAssginScale() {
        return FAssginScale;
    }

    public Date getFAssignDate() {
        return FAssignDate;
    }

    public int getFCheckState() {
        return FCheckState;
    }

    public String getFCheckTime() {
        return FCheckTime;
    }

    public String getFCheckUser() {
        return FCheckUser;
    }

    public String getFCreateTime() {
        return FCreateTime;
    }

    public String getFCreator() {
        return FCreator;
    }

    public String getFDesc() {
        return FDesc;
    }

    public String getFPortCode() {
        return FPortCode;
    }

    public String getFPortClsCode() {
        return FPortClsCode;
    }

    public String getFCalcWay() {
        return FCalcWay;
    }

    public void setFAnalysisCode1(String FAnalysisCode1) {
        this.FAnalysisCode1 = FAnalysisCode1;
    }

    public void setFAnalysisCode2(String FAnalysisCode2) {
        this.FAnalysisCode2 = FAnalysisCode2;
    }

    public void setFAnalysisCode3(String FAnalysisCode3) {
        this.FAnalysisCode3 = FAnalysisCode3;
    }

    public void setFAssginScale(double FAssginScale) {
        this.FAssginScale = FAssginScale;
    }

    public void setFAssignDate(Date FAssignDate) {
        this.FAssignDate = FAssignDate;
    }

    public void setFCheckState(int FCheckState) {
        this.FCheckState = FCheckState;
    }

    public void setFCheckTime(String FCheckTime) {
        this.FCheckTime = FCheckTime;
    }

    public void setFCheckUser(String FCheckUser) {
        this.FCheckUser = FCheckUser;
    }

    public void setFCreateTime(String FCreateTime) {
        this.FCreateTime = FCreateTime;
    }

    public void setFCreator(String FCreator) {
        this.FCreator = FCreator;
    }

    public void setFDesc(String FDesc) {
        this.FDesc = FDesc;
    }

    public void setFPortCode(String FPortCode) {
        this.FPortCode = FPortCode;
    }

    public void setFPortClsCode(String FPortClsCode) {
        this.FPortClsCode = FPortClsCode;
    }

    public void setFCalcWay(String FCalcWay) {
        this.FCalcWay = FCalcWay;
    }

    public Date getOldAssignDate() {
        return OldAssignDate;
    }

    public void setOldAssignDate(Date OldAssignDate) {
        this.OldAssignDate = OldAssignDate;
    }

    public void setAssignAttr(ResultSet rs) throws YssException {
        try {
            boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "TA"); //判断分析代码存不存在
            boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "TA");
            boolean analy3 = operSql.storageAnalysis("FAnalysisCode3", "TA");
            if (analy1) {
                this.FAnalysisCode1 = rs.getString("FAnalysisCode1") + "";
                this.FAnalysisName1 = rs.getString("FInvMgrName") + "";
            }

            if (analy2) {
                this.FAnalysisCode2 = rs.getString("FAnalysisCode2") + "";
                this.FAnalysisName2 = rs.getString("FCatName") + "";
            }

            if (analy3) {
                this.FAnalysisCode3 = rs.getString("FAnalysisCode3") + "";
                //this.FAnalysisName3 = rs.getString("FBrokerName") + "";
            }

            this.FAssignDate = rs.getDate("FAssignDate");
            this.FPortCode = rs.getString("FPortCode");
            this.FPortName = rs.getString("FPortName");
            this.FAssginScale = rs.getDouble("FAssginScale");
            this.FDesc = rs.getString("FDesc");

            this.FPortClsCode = rs.getString("FPortClsCode");
            this.FPortClsName = rs.getString("FPortClsName");
            this.FCalcWay = rs.getString("FCalcWay");

            super.setRecLog(rs);

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_TA_Assign"),
                               "FAssignDate,FPortCode,FPortClsCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3",
                               YssFun.formatDate(this.FAssignDate, "yyyy-MM-dd")
                               + "," + this.FPortCode
                               + "," + this.FPortClsCode
                               + "," + this.FAnalysisCode1
                               + "," + this.FAnalysisCode2
                               + "," + this.FAnalysisCode3
                               ,
                               YssFun.formatDate(this.OldAssignDate, "yyyy-MM-dd")
                               + "," + this.OldPortCode
                               + "," + this.OldPortClsCode
                               + "," + this.OldAnalysisCode1
                               + "," + this.OldAnalysisCode2
                               + "," + this.OldAnalysisCode3
            );
    }

//新增
    public String addSetting() throws YssException {
        Connection con = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务

        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_TA_Assign") +
                "(FAssignDate,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FPortClsCode," +
                "FAssginScale,FCalcWay,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" +
                dbl.sqlDate(this.FAssignDate) + "," +
                dbl.sqlString(this.FPortCode) + "," +
                dbl.sqlString(this.FAnalysisCode1.length() == 0 ? " " :
                              this.FAnalysisCode1) + "," +
                dbl.sqlString(this.FAnalysisCode2.length() == 0 ? " " :
                              this.FAnalysisCode2) + "," +
                dbl.sqlString(this.FAnalysisCode3.length() == 0 ? " " :
                              this.FAnalysisCode3) + "," +
                dbl.sqlString(this.FPortClsCode) + "," +
                this.FAssginScale + "," +
                dbl.sqlString(this.FCalcWay) + "," +
                dbl.sqlString(this.FDesc) + "," +
                (pub.getSysCheckState() ? 0 : 1) + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                dbl.sqlString( (pub.getSysCheckState() ? " " : this.creatorCode)) +
                ")";
            System.out.print(strSql);
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增TA分盘设置信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";
    }

//修改
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {

            strSql = " update " + pub.yssGetTableName("Tb_TA_Assign") +
                " set FAssignDate=" + dbl.sqlDate(this.FAssignDate) + "," +
                " FPortCode=" + dbl.sqlString(this.FPortCode) + "," +

                "  FAnalysisCode1= " +
                dbl.sqlString(this.FAnalysisCode1.length() != 0 ?
                              this.FAnalysisCode1 :
                              " ") + "," +
                "  FAnalysisCode2= " +
                dbl.sqlString(this.FAnalysisCode2.length() != 0 ?
                              this.FAnalysisCode2 :
                              " ") + "," +
                "  FAnalysisCode3= " +
                dbl.sqlString(this.FAnalysisCode3.length() != 0 ?
                              this.FAnalysisCode3 :
                              " ") + "," +
                " FPortClsCode=" + dbl.sqlString(this.FPortClsCode) + "," +
                " FAssginScale =" + this.FAssginScale + "," +
                " FCalcWay=" + dbl.sqlString(this.FCalcWay) + "," +
                " FDesc =" + dbl.sqlString(this.FDesc) + "," +
                " FCheckState=" + this.checkStateId + "," +
                " FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? dbl.sqlString(this.creatorCode) :
                 "' '") +
                " where FAssignDate=" + dbl.sqlDate(this.OldAssignDate) +
                ( (this.OldPortCode != null && this.OldPortCode.length() > 0) ?
                 " and FPortCode =" + dbl.sqlString(this.OldPortCode) : " ") +
                ( (this.OldPortClsCode != null &&
                   this.OldPortClsCode.length() > 0) ?
                 " and FPortClsCode =" + dbl.sqlString(this.OldPortClsCode) :
                 " ") +
                ( (!this.OldAnalysisCode1.equals("null") &&
                   this.OldAnalysisCode1.trim().length() > 0) ?
                 " and FAnalysisCode1 =" + dbl.sqlString(this.OldAnalysisCode1) :
                 " ") +
                ( (!this.OldAnalysisCode2.equals("null") &&
                   this.OldAnalysisCode2.trim().length() > 0) ?
                 " and FAnalysisCode2 =" + dbl.sqlString(this.OldAnalysisCode2) :
                 " ") +
                ( (!this.OldAnalysisCode3.equals("null") &&
                   this.OldAnalysisCode3.trim().length() > 0) ?
                 " and FAnalysisCode3 =" + dbl.sqlString(this.OldAnalysisCode3) :
                 " ");

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改TA分盘设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

//删除
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = " update " + pub.yssGetTableName("Tb_TA_Assign") +
                " set FCheckState=" + this.checkStateId +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? dbl.sqlString(this.creatorCode) :
                 "' '") +
                " where FAssignDate=" + dbl.sqlDate(this.OldAssignDate) +
                ( (this.OldPortCode != null && this.OldPortCode.length() > 0) ?
                 " and FPortCode =" + dbl.sqlString(this.OldPortCode) : " ") +
                ( (this.OldPortClsCode != null &&
                   this.OldPortClsCode.length() > 0) ?
                 " and FPortClsCode =" + dbl.sqlString(this.OldPortClsCode) :
                 " ") +
                ( (!this.OldAnalysisCode1.equals("null") &&
                   this.OldAnalysisCode1.trim().length() > 0) ?
                 " and FAnalysisCode1 =" + dbl.sqlString(this.OldAnalysisCode1) :
                 " ") +
                ( (!this.OldAnalysisCode2.equals("null") &&
                   this.OldAnalysisCode2.trim().length() > 0) ?
                 " and FAnalysisCode2 =" + dbl.sqlString(this.OldAnalysisCode2) :
                 " ") +
                ( (!this.OldAnalysisCode3.equals("null") &&
                   this.OldAnalysisCode3.trim().length() > 0) ?
                 " and FAnalysisCode3 =" + dbl.sqlString(this.OldAnalysisCode3) :
                 " ");

            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除TA分盘设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

//审核
    public void checkSetting() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
			/**shashijie 2012-7-2 STORY 2475 */
            if (sRecycled != null || !sRecycled.equalsIgnoreCase("")) {
			/**end*/
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = " update " + pub.yssGetTableName("Tb_TA_Assign") +
                        " set FCheckState=" + this.checkStateId +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "', FCheckUser = " +
                        (pub.getSysCheckState() ? dbl.sqlString(this.creatorCode) :
                         "' '") +
//                     修改 邱健 2008.4.18
//                     " where FAssignDate=" + dbl.sqlDate(this.OldAssignDate) +
//                     ( (this.OldPortCode != null &&
//                        this.OldPortCode.length() != 0) ?
//                      " and FPortCode =" + dbl.sqlString(this.OldPortCode) :
//                      " ") +
//                     ( (this.OldPortClsCode != null &&
//                        this.OldPortClsCode.length() > 0) ?
//                      " and FPortClsCode =" + dbl.sqlString(this.OldPortClsCode) :
//                      " ") +
//                     ( (!this.OldAnalysisCode1.equals("null") &&
//                        this.OldAnalysisCode1.trim().length() > 0) ?
//                      " and FAnalysisCode1 =" +
//                      dbl.sqlString(this.OldAnalysisCode1) : " ") +
//                     ( (!this.OldAnalysisCode2.equals("null") &&
//                        this.OldAnalysisCode2.trim().length() > 0) ?
//                      " and FAnalysisCode2 =" +
//                      dbl.sqlString(this.OldAnalysisCode2) : " ") +
//                     ( (!this.OldAnalysisCode3.equals("null") &&
//                        this.OldAnalysisCode3.trim().length() > 0) ?
//                      " and FAnalysisCode3 =" +
//                      dbl.sqlString(this.OldAnalysisCode3) : " ");
                        " where FAssignDate=" + dbl.sqlDate(this.FAssignDate) +
                        ( (this.FPortCode != null &&
                           this.FPortCode.length() != 0) ?
                         " and FPortCode =" + dbl.sqlString(this.FPortCode) :
                         " ") +
                        ( (this.FPortClsCode != null &&
                           this.FPortClsCode.length() > 0) ?
                         " and FPortClsCode =" + dbl.sqlString(this.FPortClsCode) :
                         " ") +
                        ( (!this.FAnalysisCode1.equals("null") &&
                           this.FAnalysisCode1.trim().length() > 0) ?
                         " and FAnalysisCode1 =" +
                         dbl.sqlString(this.FAnalysisCode1) : " ") +
                        ( (!this.FAnalysisCode2.equals("null") &&
                           this.FAnalysisCode2.trim().length() > 0) ?
                         " and FAnalysisCode2 =" +
                         dbl.sqlString(this.FAnalysisCode2) : " ") +
                        ( (!this.FAnalysisCode3.equals("null") &&
                           this.FAnalysisCode3.trim().length() > 0) ?
                         " and FAnalysisCode3 =" +
                         dbl.sqlString(this.FAnalysisCode3) : " ");
                    dbl.executeSql(strSql);
                }
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核TA分盘设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";

            if (this.filterType.FAssignDate != null &&
                !this.filterType.FAssignDate.equals(YssFun.toDate("9998-12-31"))) {
                sResult += " and a.FAssignDate =" +
                    dbl.sqlDate(this.filterType.FAssignDate);
            }

            if (this.filterType.FPortCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.FPortCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.FAnalysisCode1.length() != 0 &&
                !this.filterType.FAnalysisCode1.equalsIgnoreCase(" ")) {
                sResult = sResult + " and a.FAnalysisCode1 ='" +
                    filterType.FAnalysisCode1 + "'";
            }

            if (this.filterType.FAnalysisCode2.length() != 0 &&
                !this.filterType.FAnalysisCode2.equalsIgnoreCase(" ")) {
                sResult = sResult + " and a.FAnalysisCode2 ='" +
                    filterType.FAnalysisCode2 + "'";
            }
            if (this.filterType.FAnalysisCode3.length() != 0 &&
                !this.filterType.FAnalysisCode3.equalsIgnoreCase(" ")) {
                sResult = sResult + " and a.FAnalysisCode3 ='" +
                    filterType.FAnalysisCode3 + "'";
            }
            if (this.filterType.FPortClsCode.length() != 0) {
                sResult = sResult + " and a.FPortClsCode like '" +
                    filterType.FPortClsCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.FAssginScale != 0) {
                sResult = sResult + " and a.FAssginScale ='" +
                    filterType.FAssginScale + "'";
            }

            if (this.filterType.FCalcWay.length() != 0 &&
                !this.filterType.FCalcWay.equalsIgnoreCase("99")) {
                sResult = sResult + " and a.FCalcWay ='" +
                    filterType.FCalcWay + "'";
            }

            if (this.filterType.FDesc.length() != 0) {
                sResult = sResult + " and a.FDesc ='" +
                    filterType.FDesc + "'";
            }

        }
        return sResult;

    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            sRecycled = sRowStr;
            if (YssFun.isDate(reqAry[0])) {
                this.FAssignDate = YssFun.toDate(reqAry[0]);
            }
            this.FPortCode = reqAry[1];
            this.FPortName = reqAry[2];
            this.FAnalysisCode1 = reqAry[3];
            if (reqAry[3].equalsIgnoreCase("null") ||
                reqAry[3].trim().length() == 0) {
                this.FAnalysisCode1 = " ";
            }
            this.FAnalysisName1 = reqAry[4];
            this.FAnalysisCode2 = reqAry[5];
            if (reqAry[5].equalsIgnoreCase("null") ||
                reqAry[5].trim().length() == 0) {
                this.FAnalysisCode2 = " ";
            }
            this.FAnalysisName2 = reqAry[6];
            this.FAnalysisCode3 = reqAry[7];
            if (reqAry[7].equalsIgnoreCase("null") ||
                reqAry[7].trim().length() == 0) {
                this.FAnalysisCode3 = " ";
            }
            this.FAnalysisName3 = reqAry[8];
            if (reqAry[9].length() != 0) {
                this.FAssginScale = Double.parseDouble(
                    reqAry[9]);
            }
            this.FDesc = reqAry[10];
            this.checkStateId = YssFun.toInt(reqAry[11]);
            this.FPortClsCode = reqAry[12];
            this.FPortClsName = reqAry[13];
            this.FCalcWay = reqAry[14];
            if (YssFun.isDate(reqAry[15])) {
                this.OldAssignDate = YssFun.toDate(reqAry[15]);
            } else {
                this.OldAssignDate = YssFun.toDate("1900-01-01");
            }
            this.OldPortCode = reqAry[16];
            this.OldPortClsCode = reqAry[17];
            this.OldAnalysisCode1 = reqAry[18];
            this.OldAnalysisCode2 = reqAry[19];
            this.OldAnalysisCode3 = reqAry[20];
            if (reqAry[18].equalsIgnoreCase("null") ||
                reqAry[18].trim().length() == 0) {
                this.OldAnalysisCode1 = " ";
            }
            if (reqAry[19].equalsIgnoreCase("null") ||
                reqAry[19].trim().length() == 0) {
                this.OldAnalysisCode2 = " ";
            }
            if (reqAry[20].equalsIgnoreCase("null") ||
                reqAry[20].trim().length() == 0) {
                this.OldAnalysisCode3 = " ";
            }

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TAAssign();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析TA分盘设置出错", e);
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();

        if (this.FAssignDate != null) {
            buf.append(YssFun.formatDate(this.FAssignDate)).append("\t");
        } else {
            buf.append(" ").append("\t");
        }

        buf.append(this.FPortCode).append("\t");
        buf.append(this.FPortName).append("\t");
        buf.append(this.FAnalysisCode1).append("\t");
        buf.append(this.FAnalysisName1).append("\t");
        buf.append(this.FAnalysisCode2).append("\t");
        buf.append(this.FAnalysisName2).append("\t");
        buf.append(this.FAnalysisCode3).append("\t");
        buf.append(this.FAnalysisName3).append("\t");

        buf.append(this.FAssginScale).append("\t");
        buf.append(this.FDesc).append("\t");

        buf.append(this.FPortClsCode).append("\t");
        buf.append(this.FPortClsName).append("\t");
        buf.append(this.FCalcWay).append("\t");

        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
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
        String strSql = "";
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        String sAry[] = null;

        sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_TA); //获得分析代码

        try {
            sHeader = this.getListView1Headers();
            strSql =
                "select  a.* from (select * from " +
                pub.yssGetTableName("Tb_TA_Assign") +
                //" where FCheckState <> 2) x join " +  修改 邱健，为了使前台回收站能够显示删除数据
                ") x join " +
                "(select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FPortName as FPortName,e.FPortClsName as FPortClsName,f.FVocName as FVocName,f.FVocTypeCode as FVocTypeCode " +
                sAry[0] +
                " from " + pub.yssGetTableName("Tb_TA_Assign") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                
                //------ modify by wangzuochun  2010.08.21  MS01604    启用日期不同的组合，导致新建产生多比数据    QDV4赢时胜(测试)2010年08月12日05_B    
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                
        
                
                " left join (select FPortCode, FPortName, FPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1 ) d on a.FPortCode = d.FPortCode " +
            
                //end by lidaolong
                //-------------------------------------------- MS01604 -------------------------------------------//
                
                " left join (select FPortClsCode, FPortClsName from " +
                pub.yssGetTableName("Tb_TA_PortCls") +
                " where FCheckState =1)  e on a.FPortClsCode = e.FPortClsCode" +
                " left join Tb_Fun_Vocabulary f on a.FCalcWay = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_TA_CalcWay) +
                sAry[1] +
                buildFilterSql() + ")a  on a.FPortCode =x.FPortCode and a.FPortClsCode =x.FPortClsCode and a.FCalcWay = x.FCalcWay and a.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_TA_CalcWay) + "and x.fassigndate =a.fassigndate and x.fanalysiscode1 = a.fanalysiscode1 and x.fanalysiscode2 = a.fanalysiscode2 and x.fanalysiscode3 = a.fanalysiscode3 order by a.FPortCode";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.setAssignAttr(rs);
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

            sVocStr = vocabulary.getVoc(YssCons.YSS_TA_CalcWay);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取TA分盘信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException,
        IOException {
        ois.defaultReadObject();
    }

    public String getFAnalysisName1() {
        return FAnalysisName1;
    }

    public String getFAnalysisName2() {
        return FAnalysisName2;
    }

    public String getFAnalysisName3() {
        return FAnalysisName3;
    }

    public void setFAnalysisName1(String FAnalysisName1) {
        this.FAnalysisName1 = FAnalysisName1;
    }

    public void setFAnalysisName2(String FAnalysisName2) {
        this.FAnalysisName2 = FAnalysisName2;
    }

    public void setFAnalysisName3(String FAnalysisName3) {
        this.FAnalysisName3 = FAnalysisName3;
    }

    public String getFPortName() {
        return FPortName;
    }

    public void setFPortName(String FPortName) {
        this.FPortName = FPortName;
    }

    public String getOldAnalysisCode1() {
        return OldAnalysisCode1;
    }

    public String getOldAnalysisCode2() {
        return OldAnalysisCode2;
    }

    public String getOldAnalysisCode3() {
        return OldAnalysisCode3;
    }

    public void setOldAnalysisCode1(String OldAnalysisCode1) {
        this.OldAnalysisCode1 = OldAnalysisCode1;
    }

    public void setOldAnalysisCode2(String OldAnalysisCode2) {
        this.OldAnalysisCode2 = OldAnalysisCode2;
    }

    public void setOldAnalysisCode3(String OldAnalysisCode3) {
        this.OldAnalysisCode3 = OldAnalysisCode3;
    }

    public String getOldPortClsCode() {
        return OldPortClsCode;
    }

    public void setOldPortClsCode(String OldPortClsCode) {
        this.OldPortClsCode = OldPortClsCode;
    }

    public String getOldPortCode() {
        return OldPortCode;
    }

    public void setOldPortCode(String OldPortCode) {
        this.OldPortCode = OldPortCode;
    }

    public String getFPortClsName() {
        return FPortClsName;
    }

    public void setFPortClsName(String FPortClsName) {
        this.FPortClsName = FPortClsName;
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        String[] arrData = null;
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (sRecycled != null || ! ("").equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData.length == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_TA_Assign") +
                        " where FAssignDate=" + dbl.sqlDate(this.FAssignDate) +
                        ( (this.FPortCode != null &&
                           this.FPortCode.length() != 0) ?
                         " and FPortCode =" + dbl.sqlString(this.FPortCode) :
                         " ") +
                        ( (this.FPortClsCode != null &&
                           this.FPortClsCode.length() > 0) ?
                         " and FPortClsCode =" + dbl.sqlString(this.FPortClsCode) :
                         " ") +
                        ( (!this.FAnalysisCode1.equals("null") &&
                           this.FAnalysisCode1.trim().length() > 0) ?
                         " and FAnalysisCode1 =" +
                         dbl.sqlString(this.FAnalysisCode1) : " ") +
                        ( (!this.FAnalysisCode2.equals("null") &&
                           this.FAnalysisCode2.trim().length() > 0) ?
                         " and FAnalysisCode2 =" +
                         dbl.sqlString(this.FAnalysisCode2) : " ") +
                        ( (!this.FAnalysisCode3.equals("null") &&
                           this.FAnalysisCode3.trim().length() > 0) ?
                         " and FAnalysisCode3 =" +
                         dbl.sqlString(this.FAnalysisCode3) : " ");
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (NullPointerException ex) {
            throw new YssException(ex.getMessage()); 
        } catch (Exception e) {
            throw new YssException("TA分盘设置清除出错", e);
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
