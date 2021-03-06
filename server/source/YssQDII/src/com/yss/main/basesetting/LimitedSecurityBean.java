package com.yss.main.basesetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title: LimitedSecurityBean</p>
 *
 * <p>Description:流通受限证券信息维护 by xuqiji 2009 04 03  QDV4建行v4[1].1.2.9  </p>
 * MS00337    监控中也添加那个区分出流通受限证券及维护该证券锁定日期的功能
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class LimitedSecurityBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strSecurityCode = ""; //证券代码
    private String strSecurityName = ""; //证券名称
    private String strCategoryCode = ""; //品种代码
    private String strCategoryName = ""; //品种名称
    private String strSubCategoryCode = ""; //品种明细代码
    private String strSubCategoryName = ""; //品种明细名称
    private String strCusCatCode = ""; //自定义品种代码
    private String strCusCatName = ""; //自定义品种名称
    private java.util.Date dtLockStartDate; //锁定期起始日
    private java.util.Date dtLockEndDate; //锁定期截止日
    private String strDesc = ""; //描述
    private String strOldLimitedSecurityCode = ""; //保存证券代码

    private String sRecycled = ""; //回收站
    private LimitedSecurityBean filterType;
    public LimitedSecurityBean() {
    }

    /**
     * 检查新建，修改，复制数据时，是否该数据已经存在 by xuqiji 2009 04 07
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {

        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Base_LimitedSecurity"),
                               "FSecurityCode",
                               this.strSecurityCode,
                               this.strOldLimitedSecurityCode);
    }

    /**
     * by xuqiji 2009 04 03 增加数据
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        String strSql = null;
        boolean bTrans = true; //代表是否开始事务
        Connection conn = dbl.loadConnection(); //数据库连接
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Base_LimitedSecurity") +
                "(FSECURITYCODE,FCATCODE,FSUBCATCODE,FCUSCATCODE,FLOCKSTARTDATE,FLOCKENDDATE,FDESC," +
                "FCHECKSTATE,FCREATOR, FCREATETIME,FCheckUser) values(" +
                dbl.sqlString(this.strSecurityCode) + "," +
                dbl.sqlString(this.strCategoryCode) + "," +
                dbl.sqlString(this.strSubCategoryCode) + "," +
                dbl.sqlString(this.strCusCatCode) + "," +
                dbl.sqlDate(this.dtLockStartDate) + "," +
                dbl.sqlDate(this.dtLockEndDate) + "," +
                dbl.sqlString(this.strDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + ")";
            conn.setAutoCommit(false);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("增加流通受限证券信息设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return strSql;
    }

    /**
     * 修改数据 by xuqiji 2009 04 03
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        String strSql = null;
        boolean bTrans = true;
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Base_LimitedSecurity") +
                " set " +
                "  FSECURITYCODE = " + dbl.sqlString(this.strSecurityCode) +
                ", FCATCODE = " + dbl.sqlString(this.strCategoryCode) +
                ", FSUBCATCODE = " + dbl.sqlString(this.strSubCategoryCode) +
                ", FCUSCATCODE = " + dbl.sqlString(this.strCusCatCode) +
                ", FLOCKSTARTDATE = " + dbl.sqlDate(this.dtLockStartDate) +
                ", FLOCKENDDATE= " + dbl.sqlDate(this.dtLockEndDate) +
                ", FDesc = " + dbl.sqlString(this.strDesc) +
                ", FCHECKSTATE = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCreator = " + dbl.sqlString(this.creatorCode) +
                ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ", FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FSECURITYCODE = " +
                dbl.sqlString(this.strOldLimitedSecurityCode);
            conn.setAutoCommit(false);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改流通受限证券信息设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return strSql;
    }

    /**
     * 删除数据即放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = null;
        boolean bTrans = true;
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Base_LimitedSecurity") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FSECURITYCODE = " +
                dbl.sqlString(this.strOldLimitedSecurityCode);
            conn.setAutoCommit(false);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 回收站还原数据功能
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = null;
        String[] array = null;
        boolean bTrans = true;
        Connection conn = dbl.loadConnection();
        Statement st = null;
        try {
            conn.setAutoCommit(false);
            if (null != sRecycled && !"".equalsIgnoreCase(sRecycled.trim())) { //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
                array = sRecycled.split("\r\n");
                st = conn.createStatement();
                for (int i = 0; i < array.length; i++) { //循环执行数据还原
                    if (array[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(array[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Base_LimitedSecurity") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FSECURITYCODE = " +
                        dbl.sqlString(this.strSecurityCode);
                    st.addBatch(strSql);
                }
                st.executeBatch();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("还原流通受限证券信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(st);
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

    /**
     * 回收站清除功能 by xuqiji 2009 04 03
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = null;
        String[] array = null;
        boolean bTrans = true; //代表是否开始事务
        Statement st = null;
        Connection conn = dbl.loadConnection();
        try {
            if (null != sRecycled && !"".equalsIgnoreCase(sRecycled.trim())) { //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
                array = sRecycled.split("\r\n"); //根据规定的符号，把多个sql语句分别放入数组
                conn.setAutoCommit(false);
                st = conn.createStatement();
                for (int i = 0; i < array.length; i++) { //循环执行这些删除语句
                    if (array[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(array[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_Base_LimitedSecurity") +
                        " where FSECURITYCODE= " + dbl.sqlString(this.strSecurityCode);
                    st.addBatch(strSql);
                }
                st.executeBatch();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(st);
        }
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

    /**
     * 获取流通受限证券信息
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        String strSql = null;
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql = "select y.* from " +
                " (select FSecurityCode,FCheckState from " +
                pub.yssGetTableName("Tb_Base_LimitedSecurity") +
                "  group by FSecurityCode,FCheckState) x join" +
                " (select a.*, " +
                " b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FSecurityName," +
                " e.FCatName as FCatName,f.FSubCatName as FSubCatName,g.FCusCatName as FCusCatName  " +
                " from " + pub.yssGetTableName("Tb_Base_LimitedSecurity") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FSecurityCode,FSecurityName from " + pub.yssGetTableName("Tb_Para_Security") + ") d  on a.FSecurityCode = d.FSecurityCode " +
                " left join (select FCatCode,FCatName from " + pub.yssGetTableName("Tb_Base_Category") + " where FCheckState = 1) e on e.FCatCode = a.FCatCode " +
                " left join (select FSubCatCode,FSubCatName from " + pub.yssGetTableName("Tb_Base_SubCategory") + " where FCheckState = 1) f on f.FSubCatCode = a.FSubCatCode " +
                " left join (select FCusCatCode,FCusCatName from " + pub.yssGetTableName("Tb_Para_CustomCategory") + " where FCheckState = 1) g on g.FCusCatCode = a.FCusCatCode " +
                buildFilterSql() +
                ") y on x.FSecurityCode = y.FSecurityCode " +
                " order by y.FCheckState, y.FCreateTime";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.strSecurityCode = rs.getString("FSecurityCode");
                this.strSecurityName = rs.getString("FSecurityName");
                this.strCategoryCode = rs.getString("FCatCode");
                this.strCategoryName = rs.getString("FCatName");
                this.strSubCategoryCode = rs.getString("FSubCatCode");
                this.strSubCategoryName = rs.getString("FSubCatName");
                this.strCusCatCode = rs.getString("FCusCatCode");
                this.strCusCatName = rs.getString("FCusCatName");
                this.dtLockStartDate = rs.getDate("FLockStartDate");
                this.dtLockEndDate = rs.getDate("FLockEndDate");
                this.strDesc = rs.getString("FDesc");
                super.setRecLog(rs);
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
                "\r\f" + this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取流通受限证券信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 筛选条件
     * @return String
     * @throws YssException
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.filterType != null) {
                sResult = " where 1=1 ";
                if (this.filterType.strSecurityCode != null && this.filterType.strSecurityCode.length() != 0) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                    sResult = sResult + " and a.FSecurityCode like '" +
                        filterType.strSecurityCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strSecurityCode != null && this.filterType.strCategoryCode.length() != 0 ) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                    sResult = sResult + " and a.FCatCode like '" +
                        filterType.strCategoryCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strSecurityCode != null && this.filterType.strSubCategoryCode.length() != 0) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                    sResult = sResult + " and a.FSubCatCode like '" +
                        filterType.strSubCategoryCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strSecurityCode != null && this.filterType.strDesc.length() != 0) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                    sResult = sResult + " and a.FDesc like '" +
                        filterType.strDesc.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strSecurityCode != null && this.filterType.strCusCatCode.length() != 0 ) { //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                    sResult = sResult + " and a.FCusCatCode like '" +
                        filterType.strCusCatCode.replaceAll("'", "''") + "%'";
                }
                //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                if (this.filterType.dtLockStartDate != null && this.filterType.dtLockStartDate.toString().length() == 0 &&	
                		YssFun.formatDate(this.filterType.dtLockStartDate,"yyyy-MM-dd").equals("1900-01-01")) { //此处是对锁定起始日的设定，要求不按照日期查询 xuqiji 2009 04 07
                    sResult = sResult + " and a.dtLockStartDate like '" +
                        this.filterType.dtLockStartDate.toString().replaceAll("'", "''") + "%'";
                }
                //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                if (this.filterType.dtLockEndDate != null &&this.filterType.dtLockEndDate.toString().length() == 0 &&
                    YssFun.formatDate(this.filterType.dtLockEndDate, "yyyy-MM-dd").equals("1900-01-01")) { //此处是对锁定截止日的设定，要求不按照日期查询 xuqiji 04 07
                    sResult = sResult + " and a.dtLockEndDate like '" +
                        this.filterType.dtLockEndDate.toString().replaceAll("'", "''") + "%'";
                }
            }
        } catch (Exception e) {
            throw new YssException("筛选流通受限证券信息设置数据出错", e);
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

    /**
     * parseRowStr
     * 解析前台传来的流通受限证券信息设置的数据
     * 修改日期：2009 03 31
     * 徐启吉
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String array[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
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
            array = sTmpStr.split("\t");
            this.strSecurityCode = array[0];
            this.strCategoryCode = array[1];
            this.strSubCategoryCode = array[2];
            this.strCusCatCode = array[3];
            this.dtLockStartDate = YssFun.toDate(array[4]);
            this.dtLockEndDate = YssFun.toDate(array[5]);
            this.strDesc = array[6];
            this.checkStateId = YssFun.toInt(array[7]);
            this.strOldLimitedSecurityCode = array[8];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new LimitedSecurityBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析流通受限证券信息设置出错", e);
        }
    }

    /**
     * buildRowStr
     * 获取数据字符串 by xuqiji 2009 03 31
     * @return String
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strSecurityCode).append("\t");
        buf.append(this.strSecurityName).append("\t");
        buf.append(this.strCategoryCode).append("\t");
        buf.append(this.strCategoryName).append("\t");
        buf.append(this.strSubCategoryCode).append("\t");
        buf.append(this.strSubCategoryName).append("\t");
        buf.append(this.strCusCatCode).append("\t");
        buf.append(this.strCusCatName).append("\t");
        buf.append(YssFun.formatDate(this.dtLockStartDate, YssCons.YSS_DATEFORMAT)).append("\t");
        buf.append(YssFun.formatDate(this.dtLockEndDate, YssCons.YSS_DATEFORMAT)).append("\t");
        buf.append(this.strDesc).append("\t");
        buf.append(super.buildRecLog());

        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public java.util.Date getDtLockEndDate() {
        return dtLockEndDate;
    }

    public java.util.Date getDtLockStartDate() {
        return dtLockStartDate;
    }

    public String getStrCategoryCode() {
        return strCategoryCode;
    }

    public String getStrCategoryName() {
        return strCategoryName;
    }

    public String getStrCusCatCode() {
        return strCusCatCode;
    }

    public String getStrCusCatName() {
        return strCusCatName;
    }

    public String getStrSecurityCode() {
        return strSecurityCode;
    }

    public String getStrSecurityName() {
        return strSecurityName;
    }

    public String getStrSubCategoryCode() {
        return strSubCategoryCode;
    }

    public String getStrSubCategoryName() {
        return strSubCategoryName;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public String getStrOldLimitedSecurityCode() {
        return strOldLimitedSecurityCode;
    }

    public void setStrSubCategoryName(String strSubCategoryName) {
        this.strSubCategoryName = strSubCategoryName;
    }

    public void setStrSubCategoryCode(String strSubCategoryCode) {
        this.strSubCategoryCode = strSubCategoryCode;
    }

    public void setStrSecurityName(String strSecurityName) {
        this.strSecurityName = strSecurityName;
    }

    public void setStrSecurityCode(String strSecurityCode) {
        this.strSecurityCode = strSecurityCode;
    }

    public void setStrCusCatName(String strCusCatName) {
        this.strCusCatName = strCusCatName;
    }

    public void setStrCusCatCode(String strCusCatCode) {
        this.strCusCatCode = strCusCatCode;
    }

    public void setStrCategoryName(String strCategoryName) {
        this.strCategoryName = strCategoryName;
    }

    public void setStrCategoryCode(String strCategoryCode) {
        this.strCategoryCode = strCategoryCode;
    }

    public void setDtLockEndDate(java.util.Date dtLockEndDate) {
        this.dtLockEndDate = dtLockEndDate;
    }

    public void setDtLockStartDate(java.util.Date dtLockStartDate) {
        this.dtLockStartDate = dtLockStartDate;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public void setStrOldLimitedSecurityCode(String strOldLimitedSecurityCode) {
        this.strOldLimitedSecurityCode = strOldLimitedSecurityCode;
    }

    public LimitedSecurityBean getFilterType() {
        return filterType;
    }

    public void setFilterType(LimitedSecurityBean filterType) {
        this.filterType = filterType;
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
