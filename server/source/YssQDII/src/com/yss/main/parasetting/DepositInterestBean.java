package com.yss.main.parasetting;

import java.math.*;
import java.sql.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class DepositInterestBean
    extends BaseDataSettingBean implements IDataSetting {
    private String bankCode = ""; //银行代码
    private String bankName = ""; //银行名称
    private String curyCode = ""; //货币代码
    private String curyName = ""; //货币名称
    private String depDurCode = ""; //存款期限代码
    private String depDurName = ""; //存款期限名称
    private BigDecimal intereset; //利率 MS00507:QDV4赢时胜（上海）2009年6月15日01_B libo
    private java.util.Date startTime;
    private String desc = "";
    private String oldBankCode = "";
    private String oldCuryCode = "";
    private String oldDepDurCode = "";
    private java.util.Date oldStartTime;
    private DepositInterestBean filterType;
    private String sRecycled = "";
    public String getDesc() {
        return desc;
    }

    public String getCuryName() {
        return curyName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getBankName() {
        return bankName;
    }

    public DepositInterestBean getFilterType() {
        return filterType;
    }

    public String getOldDepDurCode() {
        return oldDepDurCode;
    }

    public String getOldBankCode() {
        return oldBankCode;
    }

    public String getDepDurName() {
        return depDurName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public String getDepDurCode() {
        return depDurCode;
    }

    public void setOldCuryCode(String oldCuryCode) {
        this.oldCuryCode = oldCuryCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setCuryName(String curyName) {
        this.curyName = curyName;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setFilterType(DepositInterestBean filterType) {
        this.filterType = filterType;
    }

    public void setOldDepDurCode(String oldDepDurCode) {
        this.oldDepDurCode = oldDepDurCode;
    }

    public void setOldBankCode(String oldBankCode) {
        this.oldBankCode = oldBankCode;
    }

    public void setDepDurName(String depDurName) {
        this.depDurName = depDurName;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setDepDurCode(String depDurCode) {
        this.depDurCode = depDurCode;
    }

    public void setOldStartTime(Date oldStartTime) {
        this.oldStartTime = oldStartTime;
    }

    public void setIntereset(BigDecimal intereset) {
        this.intereset = intereset;
    }

    public String getOldCuryCode() {
        return oldCuryCode;
    }

    public Date getOldStartTime() {
        return oldStartTime;
    }

    public BigDecimal getIntereset() {
        return intereset;
    }

    public DepositInterestBean() {
    }

    /**
     * 此方法已被修改
     *修改时间：2008年2月20号
     * 修改人：单亮
     * 原方法的功能：查询出存款利息表的数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql =
            " select y.* from " +
            "( select FBankCode,FCuryCode,FDepDurCode ,max(FStartDate)as startTime from " + pub.yssGetTableName("Tb_Para_DepositInterest") +
            " where FStartDate<=" + dbl.sqlDate(new java.util.Date()) +
            "  group by FBankCode,FCuryCode,FDepDurCode)  x join ";
        strSql = strSql + "(select a.*,b.FUserName as creator,c.FUserName as checkuser, d.FBankName ,e.FCuryName ,f.FDepDurName " +
            " from " + pub.yssGetTableName("Tb_Para_DepositInterest") +
            " a " +
            " left join(select FUserCode,FUserName from  Tb_Sys_UserList)b on b.FUserCode=a.FCreator " +
            " left join(select FUserCode,FUserName from  Tb_Sys_UserList)c on c.FUserCode=a.FCheckUser " +
            " left join(select FBankCode,FBankName from " + pub.yssGetTableName("Tb_Para_Bank") + ")d on d.FBankCode=a.FBankCode " +
            " left join(select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + ")e on e.FCuryCode=a.FCuryCode " +
            " left join(select FDepDurCode,FDepDurName from " + pub.yssGetTableName("Tb_Para_DepositDuration") + ")f on f.FDepDurCode=a.FDepDurCode " +
            buildFilterSql() +
            ")y on y.FBankCode=x.FBankCode and y.FCuryCode=x.FCuryCode and y.FDepDurCode=x.FDepDurCode and y.FStartDate=x.startTime" +
            " order by  y.FCheckTime desc, y.FStartDate desc --y.FBankCode,y.FCuryCode,y.FDepDurCode";
        return builderListViewData(strSql);
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
                this.bankCode = rs.getString("FBankCode") + "";
                this.bankName = rs.getString("FBankName") + "";
                this.curyCode = rs.getString("FCuryCode");
                this.curyName = rs.getString("FCuryName") + "";
                this.depDurCode = rs.getString("FDepDurCode");
                this.depDurName = rs.getString("FDepDurName");
                this.intereset = rs.getBigDecimal("FIntereset"); //利率需求更长的数据来存放 MS00507:QDV4赢时胜（上海）2009年6月15日01_B edited by libo 20090710
                this.startTime = rs.getDate("FStartDate");
                this.desc = rs.getString("FDesc") + "";
                this.checkStateId = rs.getInt("FCheckState");
                this.checkStateName = YssFun.getCheckStateName(rs.getInt("FCheckState"));
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FCreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime") + "";

                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    public String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = "where 1=1";
            if (this.filterType.bankCode.length() > 0) {
                sResult = sResult + " and a.FBankCode like '" +
                    filterType.bankCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.curyCode.length() > 0) {
                sResult = sResult + " and a.FCuryCode like '" +
                    filterType.curyCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.depDurCode.length() > 0) {
                sResult = sResult + " and a.FDepDurCode like '" +
                    filterType.depDurCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc.length() > 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
            //20120320 modified by liubo.Bug #3757
            //将FStartDate<=改为FStartDate=
            //=============================
            if (this.filterType.startTime != null &&
                !this.filterType.startTime.equals(YssFun.toDate("9998-12-31"))) {
                sResult = sResult + " and a.FStartDate = " +
            //==============end===============
                    dbl.sqlDate(filterType.startTime);
            }

        }
        return sResult;

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
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql =
            "select a.*,b.FUserName as creator,c.FUserName as checkuser, d.FBankName ,e.FCuryName ,f.FDepDurName " +
            " from " + pub.yssGetTableName("Tb_Para_DepositInterest") +
            " a " +
            " left join(select FUserCode,FUserName from  Tb_Sys_UserList)b on b.FUserCode=a.FCreator " +
            " left join(select FUserCode,FUserName from  Tb_Sys_UserList)c on c.FUserCode=a.FCheckUser " +
            " left join(select FBankCode,FBankName from " + pub.yssGetTableName("Tb_Para_Bank") + ")d on d.FBankCode=a.FBankCode " +
            " left join(select FCuryCode,FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + ")e on e.FCuryCode=a.FCuryCode " +
            " left join(select FDepDurCode,FDepDurName from " + pub.yssGetTableName("Tb_Para_DepositDuration") + ")f on f.FDepDurCode=a.FDepDurCode " +
            buildFilterSql() +
            " order by a.FBankCode,a.FCuryCode,a.FDepDurCode";
        return this.builderListViewData(strSql);
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        try {
            con.setAutoCommit(false);
            bTrans = true;
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_DepositInterest") +
                "(FBankCode,FCuryCode,FDepDurCode,FIntereset,FStartDate,FDesc,FCheckState,FCreator,FCreateTime)" +
                " values(" + dbl.sqlString(this.bankCode) + "," +
                dbl.sqlString(this.curyCode) + "," +
                dbl.sqlString(this.depDurCode) + "," +
                this.intereset + "," +
                dbl.sqlDate(this.startTime) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + ")";
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("保存存款利率出错", e);
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_DepositInterest"), "FBankCode,FCuryCode,FDepDurCode,FStartDate",
                               this.bankCode + "," + this.curyCode + "," + this.depDurCode + "," + YssFun.formatDate(this.startTime),
                               this.oldBankCode + "," + this.oldCuryCode + "," + this.oldDepDurCode + "," + YssFun.formatDate(this.oldStartTime));
    }

    /**修改时间：2008年3月20号
     *  修改人：单亮
     *  原方法功能：只能处理存款利率的审核和未审核的单条信息。
     *  新方法功能：可以处理存款利率审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = " update " +
                        pub.yssGetTableName("Tb_Para_DepositInterest") +
                        " set FCheckState=" + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FBankCode = " + dbl.sqlString(this.bankCode) +
                        " and FCuryCode=" + dbl.sqlString(this.curyCode) +
                        " and FDepDurCode = " + dbl.sqlString(this.depDurCode) +
                        " and FStartDate=" + dbl.sqlDate(this.startTime);
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而strOldStorageType不为空，则按照strOldStorageType来执行sql语句
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (bankCode != null && !bankCode.equalsIgnoreCase("")) {
                strSql = " update " +
                    pub.yssGetTableName("Tb_Para_DepositInterest") +
                    " set FCheckState=" + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FBankCode = " + dbl.sqlString(this.bankCode) +
                    " and FCuryCode=" + dbl.sqlString(this.curyCode) +
                    " and FDepDurCode = " + dbl.sqlString(this.depDurCode) +
                    " and FStartDate=" + dbl.sqlDate(this.startTime);
                dbl.executeSql(strSql);
                //执行sql语句
                dbl.executeSql(strSql);

            }

            conn.commit();
            bTrans = true;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("审核存款利率出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 删除数据，即放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        try {
            con.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Para_DepositInterest") +
                " set FCheckState=" + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FBankCode = " + dbl.sqlString(this.bankCode) + " and FCuryCode=" + dbl.sqlString(this.curyCode) +
                " and FDepDurCode = " + dbl.sqlString(this.depDurCode) + " and FStartDate=" + dbl.sqlDate(this.startTime);
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除存款利率出错", e);
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        try {
            con.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Para_DepositInterest") +
                " set FBankCode=" + dbl.sqlString(this.bankCode) + "," +
                "FCuryCode=" + dbl.sqlString(this.curyCode) + "," +
                "FDepDurCode=" + dbl.sqlString(this.depDurCode) + "," +
                "FIntereset=" + this.intereset + "," +
                "FStartDate=" + dbl.sqlDate(this.startTime) + "," +
                "FDesc=" + dbl.sqlString(this.desc) + "," +
                "FCheckState = " + (pub.getSysCheckState() ? "0" : "1") + "," +
                "FCreator = " + dbl.sqlString(this.creatorCode) + "," +
                "FCreateTime = " + dbl.sqlString(this.creatorTime) + "," +
                "FCheckUser = " + (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FBankCode = " + dbl.sqlString(this.oldBankCode) + " and FCuryCode=" + dbl.sqlString(this.oldCuryCode) +
                " and FDepDurCode = " + dbl.sqlString(this.oldDepDurCode) + " and FStartDate=" + dbl.sqlDate(this.oldStartTime); // by ly 将startTime 改为 oldStartTime 080317
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改存款利率出错", e);
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";

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
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
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
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.bankCode).append("\t");
        buf.append(this.bankName).append("\t");
        buf.append(this.curyCode).append("\t");
        buf.append(this.curyName).append("\t");
        buf.append(this.depDurCode).append("\t");
        buf.append(this.depDurName).append("\t");
        buf.append(this.intereset).append("\t");
        buf.append(YssFun.formatDate(this.startTime)).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
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
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String sTemp = "";
        String[] req = null;
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
        if (sRowStr.indexOf("\r\t") > 0) {
            sTemp = sRowStr.split("\r\t")[0];
        } else {
            sTemp = sRowStr;
        }
        sRecycled = sRowStr;
        req = sTemp.split("\t");
        this.bankCode = req[0];
        this.curyCode = req[1];
        this.depDurCode = req[2];
        if (req[3].length() != 0) {
            this.intereset = new BigDecimal(req[3].trim().length() == 0 ? "0" : req[3]); //利率需求更长的数据来存放 MS00507:QDV4赢时胜（上海）2009年6月15日01_B edited by libo 20090710
        }
        this.startTime = YssFun.toDate(req[4]);
        this.desc = req[5];
        this.checkStateId = Integer.parseInt(req[6]);
        this.oldBankCode = req[7];
        this.oldCuryCode = req[8];
        this.oldDepDurCode = req[9];
        this.oldStartTime = YssFun.toDate(req[10]);
        super.parseRecLog();
        if (sRowStr.indexOf("\r\t") > 0) {
            this.filterType = new DepositInterestBean();
            this.filterType.setYssPub(pub);
            this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
        }
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        DepositInterestBean befDepositInter = new DepositInterestBean();
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Para_DepositInterest") +
                " where FBankCode = " + dbl.sqlString(this.oldBankCode) + " and FCuryCode=" + dbl.sqlString(this.oldCuryCode) +
                " and FDepDurCode = " + dbl.sqlString(this.oldDepDurCode) + " and FStartDate=" + dbl.sqlDate(this.oldStartTime);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befDepositInter.bankCode = rs.getString("FBankCode") + "";
                befDepositInter.curyCode = rs.getString("FCuryCode") + "";
                befDepositInter.depDurCode = rs.getString("FDepDurCode");
                befDepositInter.intereset = rs.getBigDecimal("FIntereset"); //利率需求更长的数据来存放 MS00507:QDV4赢时胜（上海）2009年6月15日01_B edited by libo 20090710
                befDepositInter.startTime = rs.getDate("FStartDate");
                befDepositInter.desc = rs.getString("FDesc") + "";
            }
            return befDepositInter.buildRowStr();
        } catch (Exception e) {
            throw new YssException("获取修改前数据出错", e);
        }

    }

    /**
     * 从存款利率回收站删除数据，即是彻底删除
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
                        pub.yssGetTableName("Tb_Para_DepositInterest") +
                        " where FBankCode = " + dbl.sqlString(this.bankCode) + " and FCuryCode=" + dbl.sqlString(this.curyCode) +
                        " and FDepDurCode = " + dbl.sqlString(this.depDurCode) + " and FStartDate=" + dbl.sqlDate(this.startTime);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            else if (bankCode != "" && bankCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_DepositInterest") +
                    " where FBankCode = " + dbl.sqlString(this.bankCode) + " and FCuryCode=" + dbl.sqlString(this.curyCode) +
                    " and FDepDurCode = " + dbl.sqlString(this.depDurCode) + " and FStartDate=" + dbl.sqlDate(this.startTime);
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
