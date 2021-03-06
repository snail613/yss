package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 *
 * <p>Title: PeriodBean </p>
 * <p>Description: 期间设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class PeriodBean
    extends BaseDataSettingBean implements IDataSetting {
    private String periodCode = ""; //期间代码
    private String periodName = ""; //期间名称
    private int dayOfYear = -1; //每年天数
    private int dayOfMonth = -1; //每月天数
    private String dayInd = "0"; //计算方法
    private String desc = ""; //期间描述
    private PeriodBean filterType;
    private String sRecycled = "";
    private String oldPeriodCode;

    //--------- MS00211 QDV4中保2009年01月22日01_A  sj modified 20090124 --------------
    private int periodType; //期间类型

    public int getPeriodType() {
        return periodType;
    }

    public void setPeriodType(int intPeriodType) {
        periodType = intPeriodType;
    }

    //---------------------------------------------------------------------------------

    public int getDayOfYear() {
        return dayOfYear;
    }

    public String getDesc() {
        return desc;
    }

    public String getOldPeriodCode() {
        return oldPeriodCode;
    }

    public String getDayInd() {
        return dayInd;
    }

    public PeriodBean getFilterType() {
        return filterType;
    }

    public String getPeriodCode() {
        return periodCode;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOldPeriodCode(String oldPeriodCode) {
        this.oldPeriodCode = oldPeriodCode;
    }

    public void setDayInd(String dayInd) {
        this.dayInd = dayInd;
    }

    public void setFilterType(PeriodBean filterType) {
        this.filterType = filterType;
    }

    public void setPeriodCode(String periodCode) {
        this.periodCode = periodCode;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getPeriodName() {
        return periodName;
    }

    public PeriodBean() {
    }

    /**
     * parseRowStr
     * 解析期间设置
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String[] reqAry = null;

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
            this.periodCode = reqAry[0];
            this.periodName = reqAry[1];
            this.dayOfYear = Integer.parseInt(reqAry[2]);
            this.dayOfMonth = Integer.parseInt(reqAry[3]);
            this.dayInd = reqAry[4];
            //------ modify by wangzuochun 2011.06.03 BUG 2003 证券信息维护界面，维护一条证券信息，输入描述信息若含有回车符，清除/还原时报错 
            if (reqAry[5] != null ){
            	if (reqAry[5].indexOf("【Enter】") >= 0){
            		this.desc = reqAry[5].replaceAll("【Enter】", "\r\n");
            	}
            	else{
            		this.desc = reqAry[5];
            	}
            }
            //----------------- BUG 2003 ----------------//
            this.checkStateId = Integer.parseInt(reqAry[6]);
            this.oldPeriodCode = reqAry[7];
            this.periodType = Integer.parseInt(reqAry[8]); //MS00211 QDV4中保2009年01月22日01_A  sj modified 20090124
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new PeriodBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析期间设置请求出错", e);
        }
    }

    /**
     * auditSetting
     */
    public void auditSetting() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.periodCode).append("\t");
        buf.append(this.periodName).append("\t");
        buf.append(this.dayOfYear).append("\t");
        buf.append(this.dayOfMonth).append("\t");
        buf.append(this.dayInd).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.periodType).append("\t"); //MS00211 QDV4中保2009年01月22日01_A  sj modified 20090124
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     */
    public void checkInput() {
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
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.periodCode.length() != 0) {
                sResult = sResult + " and a.FPeriodCode like '" +
                    filterType.periodCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.periodName.length() != 0) {
                sResult = sResult + " and a.FPeriodName like '" +
                    filterType.periodName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.dayOfYear != -1) {
                sResult = sResult + " and a.FDayOfYear = " +
                    filterType.dayOfYear;
            }
            if (this.filterType.dayOfMonth != -1) {
                sResult = sResult + " and a.FDayOfMonth = " +
                    filterType.dayOfMonth;
            }
            if (!this.filterType.dayInd.equalsIgnoreCase("99") &&
                this.filterType.dayInd.length() != 0) {
                sResult = sResult + " and a.FDayInd = " +
                    filterType.dayInd;
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%' ";
            }
            //------MS00211 QDV4中保2009年01月22日01_A  sj modified 20090124 ------------
            if (this.filterType.periodType != -1 && this.filterType.periodType != 99) { //edit by xuqiji 20090430 :QDV4赢时胜上海2009年04月27日03_B MS00417    基本的几项业务参数设置时某些字段长度在界面输入上未做限制
                sResult = sResult + " and a.FPeriodType = " +
                    filterType.periodType;
            }
            //-------------------------------------------------------------------------
        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取期间设置信息
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.*, f.FVocName as FDayIndValue, d.FUserName as FCreatorName,e.FUserName as FCheckUserName" +
                //-------------MS00211 QDV4中保2009年01月22日01_A  sj modified 20090124
                " ,g.FVocName as FPeriodTypeName " +
                //-------------------------------------------------------------------
                " from " +
                pub.yssGetTableName("Tb_Para_Period") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                //2007.11.30 修改 蒋锦 使用dbl.sqlToChar()处理"a.FDayInd"，否则在使用DB2数据库时会报数据类型错误
                " left join Tb_Fun_Vocabulary f on " + dbl.sqlToChar("a.FDayInd") + " = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PRD_DAYIND) +
                //---------------------------MS00211 QDV4中保2009年01月22日01_A  sj modified 20090124 ---------------------------
                " left join Tb_Fun_Vocabulary g on " + dbl.sqlToChar("a.FPeriodType") + " = g.FVocCode and g.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PARA_PERIODTYPE) +
                //-------------------------------------------------------------------------------------------------------------
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc"; // wdy modify 20070830

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.periodCode = rs.getString("FPeriodCode") + "";
                this.periodName = rs.getString("FPeriodName") + "";
                this.dayOfYear = rs.getInt("FDayOfYear");
                this.dayOfMonth = rs.getInt("FDayOfMonth");
                this.dayInd = rs.getString("FDayInd") + "";
                this.desc = rs.getString("FDesc");
                this.periodType = rs.getInt("FPeriodType"); //MS00211 QDV4中保2009年01月22日01_A  sj modified 20090124
                super.setRecLog(rs);
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
//         sVocStr = vocabulary.getVoc(YssCons.YSS_PRD_DAYIND);
            //----------MS00211 QDV4中保2009年01月22日01_A  sj modified 20090124  --------------------------
            sVocStr = vocabulary.getVoc(YssCons.YSS_PRD_DAYIND + "," + YssCons.YSS_PARA_PERIODTYPE);
            //---------------------------------------------------------------------------------------------

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取期间信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData2
     * 获取已审核的期间设置信息
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "期间代码\t期间名称\t期间描述";
            strSql =
                "select a.*, d.FUserName as FCreatorName,e.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_Period") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " where a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FPeriodCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FPeriodName") + "").trim()).append(
                    "\t");
                bufShow.append(YssFun.left( (rs.getString("FDesc") + "").trim(), 40)).
                    append(YssCons.YSS_LINESPLITMARK);

                this.periodCode = rs.getString("FPeriodCode") + "";
                this.periodName = rs.getString("FPeriodName") + "";
                this.dayOfYear = rs.getInt("FDayOfYear");
                this.dayOfMonth = rs.getInt("FDayOfMonth");
                this.dayInd = rs.getString("FDayInd") + "";
                this.desc = rs.getString("FDesc");
                this.periodType = rs.getInt("FPeriodType"); //MS00211 QDV4中保2009年01月22日01_A  sj modified 20090124
                super.setRecLog(rs);
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
            throw new YssException("获取可用期间信息出错", e);
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
     * getPartSetting
     *
     * @return String
     */
    public String getPartSetting() {
        return "";
    }

    /**
     * getSetting
     *
     * @return IBaseSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Para_Period") +
                " where FPeriodCode = " + dbl.sqlString(this.periodCode) +
                " and FCheckState = 1";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.periodCode = rs.getString("FPeriodCode") + "";
                this.periodName = rs.getString("FPeriodName") + "";
                this.dayOfYear = rs.getInt("FDayOfYear");
                this.dayOfMonth = rs.getInt("FDayOfMonth");
                this.dayInd = rs.getString("FDayInd") + "";
                this.desc = rs.getString("FDesc") + "";
                this.periodType = rs.getInt("FPeriodType"); //MS00211 QDV4中保2009年01月22日01_A  sj modified 20090124
            }
        } catch (Exception e) {
            throw new YssException("获取期间信息出错", e);
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
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Period"),
                               "FPeriodCode",
                               this.periodCode, this.oldPeriodCode);
    }

    /**
     * saveSetting
     * 新增、修改、删除、审核
     * @param btOper byte
     */
    /*  public void saveSetting(byte btOper) throws YssException {
         String strSql = "";
         boolean bTrans = false; //代表是否开始了事务
         Connection conn = dbl.loadConnection();
         try {
            if (btOper == YssCons.OP_ADD) {
               strSql = "insert into " + pub.yssGetTableName("Tb_Para_Period") +
                     " (FPeriodCode,FPeriodName,FDayOfYear,FDayOfMonth,FDayInd,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                     " values(" + dbl.sqlString(this.periodCode) + "," +
                     dbl.sqlString(this.periodName) + "," +
                     this.dayOfYear + " ," +
                     this.dayOfMonth + "," +
                     this.dayInd + "," +
                     dbl.sqlString(this.desc) + "," +
                     (pub.getSysCheckState() ? "0" : "1") + "," +
                     dbl.sqlString(this.creatorCode) + "," +
                     dbl.sqlString(this.creatorTime) + "," +
                     (pub.getSysCheckState() ? "' '" :
                      dbl.sqlString(this.creatorCode)) + ")";
            }
            else if (btOper == YssCons.OP_EDIT) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_Period") +
                     " set FPeriodCode = " +
                     dbl.sqlString(this.periodCode) + ", FPeriodName = " +
                     dbl.sqlString(this.periodName) + " , FDayOfYear = " +
                     this.dayOfYear + " , FDayOfMonth = " +
                     this.dayOfMonth + ", FDayInd = " +
                     this.dayInd + ", FDesc = " +
                     dbl.sqlString(this.desc) + ",FCheckState = " +
                     (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                     dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                     dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                     (pub.getSysCheckState() ? "' '" :
                      dbl.sqlString(this.creatorCode)) +
                     " where FPeriodCode = " +
                     dbl.sqlString(this.oldPeriodCode);
            }
            else if (btOper == YssCons.OP_DEL) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_Period") +
                     " set FCheckState = " + this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                     "'" +
                     " where FPeriodCode = " + dbl.sqlString(this.periodCode);

            }
            else if (btOper == YssCons.OP_AUDIT) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_Period") +
                     " set FCheckState = " +
                     this.checkStateId + ", FCheckUser = " +
                     dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                     YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where FPeriodCode = " + dbl.sqlString(this.periodCode);
            }
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
         }
         catch (Exception e) {
            throw new YssException("更新期间设置信息出错", e);
         }
         finally {
            dbl.endTransFinal(conn, bTrans);
         }
      }*/
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
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_Period") +
                " (FPeriodCode,FPeriodName,FDayOfYear,FDayOfMonth,FDayInd," +
                "FPeriodType," + //MS00211 QDV4中保2009年01月22日01_A  sj modified 20090124
                "FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.periodCode) + "," +
                dbl.sqlString(this.periodName) + "," +
                this.dayOfYear + " ," +
                this.dayOfMonth + "," +
                this.dayInd + "," +
                //--------MS00211 QDV4中保2009年01月22日01_A  sj modified 20090124  ----------
                this.periodType + "," +
                dbl.sqlString(this.desc) + "," +
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
            throw new YssException("增加期间设置信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_Period") +
                " set FPeriodCode = " +
                dbl.sqlString(this.periodCode) + ", FPeriodName = " +
                dbl.sqlString(this.periodName) + " , FDayOfYear = " +
                this.dayOfYear + " , FDayOfMonth = " +
                this.dayOfMonth + ", FDayInd = " +
                //--------MS00211 QDV4中保2009年01月22日01_A  sj modified 20090124 --------
                this.dayInd + ",FPeriodType = " +
                this.periodType + ", FDesc = " +
                //------------------------------------------------------------------------
                dbl.sqlString(this.desc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FPeriodCode = " +
                dbl.sqlString(this.oldPeriodCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改期间设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除数据，即是放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_Period") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FPeriodCode = " + dbl.sqlString(this.periodCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除期间设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**修改时间：2008年3月20号
     *  修改人：单亮
     *  原方法功能：只能处理货币方向的审核和未审核的单条信息。
     *  新方法功能：可以处理货币方向审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     */

    public void checkSetting() throws YssException {
        //修改前的代码
//      String strSql = "";
//      boolean bTrans = false; //代表是否开始了事务
//      Connection conn = dbl.loadConnection();
//      try {
//         strSql = "update " + pub.yssGetTableName("Tb_Para_Period") +
//                    " set FCheckState = " +
//                    this.checkStateId + ", FCheckUser = " +
//                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
//                    YssFun.formatDatetime(new java.util.Date()) + "'" +
//                    " where FPeriodCode = " + dbl.sqlString(this.periodCode);
//         conn.setAutoCommit(false);
//         bTrans = true;
//         dbl.executeSql(strSql);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//      }
//
//      catch (Exception e) {
//         throw new YssException("审核期间设置信息出错", e);
//      }
//      finally {
//         dbl.endTransFinal(conn, bTrans);
//      }
        //修改后的代码
        //-----------------------------------
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "update " + pub.yssGetTableName("Tb_Para_Period") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FPeriodCode = " + dbl.sqlString(this.periodCode);

                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而periodCode不为空，则按照periodCode来执行sql语句
            else if (periodCode != "" && periodCode != null) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_Period") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FPeriodCode = " + dbl.sqlString(this.periodCode);
                dbl.executeSql(strSql);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("审核期间设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);

        }
        //-----------------------------

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
        PeriodBean befEditBean = new PeriodBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*, f.FVocName as FDayIndValue, d.FUserName as FCreatorName,e.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_Period") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " left join Tb_Fun_Vocabulary f on " + dbl.sqlToChar("a.FDayInd") + " = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PRD_DAYIND) +
                //---------------------------MS00211 QDV4中保2009年01月22日01_A  sj modified 20090124 -----------------------
                " left join Tb_Fun_Vocabulary g on " + dbl.sqlToChar("a.FPeriodType") + " = g.FVocCode and g.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PARA_PERIODTYPE) +
                //-----------------------------------------------------------------------------------------------------------
                " where  a.FPeriodCode =" + dbl.sqlString(this.oldPeriodCode) +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.periodCode = rs.getString("FPeriodCode") + "";
                befEditBean.periodName = rs.getString("FPeriodName") + "";
                befEditBean.dayOfYear = rs.getInt("FDayOfYear");
                befEditBean.dayOfMonth = rs.getInt("FDayOfMonth");
                befEditBean.dayInd = rs.getString("FDayInd") + "";
                befEditBean.desc = rs.getString("FDesc");
                befEditBean.periodType = rs.getInt("FPeriodType"); //MS00211 QDV4中保2009年01月22日01_A  sj modified 20090124
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    /**
     * 从回收站删除数据，即是彻底删除
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            if (! (sRecycled == "" || sRecycled == null)) {
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
                    strSql = "delete from " + pub.yssGetTableName("Tb_Para_Period") +
                        " where FPeriodCode = " + dbl.sqlString(this.periodCode);

                    dbl.executeSql(strSql);
                }

            } else if (! (periodCode == "" || periodCode == null)) {
                strSql = "delete from " + pub.yssGetTableName("Tb_Para_Period") +
                    " where FPeriodCode = " + dbl.sqlString(this.periodCode);
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
}
