package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title: IndexBean </p>
 * <p>Description: 指数信息设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */

public class IndexBean
    extends BaseDataSettingBean implements IDataSetting {
    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public void setStrOldIndexCode(String StrOldIndexCode) {
        this.StrOldIndexCode = StrOldIndexCode;
    }

    public String getStrOldIndexCode() {
        return StrOldIndexCode;
    }

    public void setStrExchangeName(String StrExchangeName) {
        this.StrExchangeName = StrExchangeName;
    }

    public String getStrExchangeName() {
        return StrExchangeName;
    }

    public void setStrIndexName(String StrIndexName) {
        this.StrIndexName = StrIndexName;
    }

    public String getStrIndexName() {
        return StrIndexName;
    }

    public void setStrIndexCode(String StrIndexCode) {
        this.StrIndexCode = StrIndexCode;
    }

    public String getStrIndexCode() {
        return StrIndexCode;
    }

    public void setStrOldExchangeCode(String StrOldExchangeCode) {
        this.StrOldExchangeCode = StrOldExchangeCode;
    }

    public String getStrOldExchangeCode() {
        return StrOldExchangeCode;
    }

    public void setStrExchangeCode(String StrExchangeCode) {
        this.StrExchangeCode = StrExchangeCode;
    }

    public String getStrExchangeCode() {
        return StrExchangeCode;
    }

    public void setStrIsOnlyColumns(String StrIsOnlyColumns) {
        this.StrIsOnlyColumns = StrIsOnlyColumns;
    }

    public String getStrIsOnlyColumns() {
        return StrIsOnlyColumns;
    }

    public IndexBean() {
    }

    private String StrIndexCode = "";
    private String StrIndexName = "";
    private String StrExchangeCode = "";
    private String StrExchangeName = "";
    private String strDesc = "";
    private String StrOldIndexCode = "";
    private String StrOldExchangeCode = "";
    private String StrIsOnlyColumns = "0"; //在初始登陆时是否只显示列，不查询数据
    private IndexBean filterType;
    private String sRecycled = "";

    /**
     * 此方法已被修改
     *修改时间：2008年2月23号
     * 修改人：单亮
     * 原方法的功能：查询出费用连接数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
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
            strSql = "select y.* from " +
                "(select FIndexCode from " + pub.yssGetTableName("Tb_Para_Index") +
                " " +
                //修改前的代码
                //" where FCheckState <> 2 group by FIndexCode) x join" +
                //修改后的代码
                //----------------------------begin
                "  group by FIndexCode) x join" +
                //----------------------------end
                "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FExchangeName as FExchangeName" +
                " from " + pub.yssGetTableName("Tb_Para_Index") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FExchangecode,FExchangeName from tb_base_exchange) d on a.FExchangecode=d.FExchangecode" +
                buildFilterSql() + " order by FCheckState, FCreateTime desc, FCheckTime desc)" +
                " y on x.FindexCode=y.FIndexCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setResultSetAttr(rs);
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
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取指数信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.StrIsOnlyColumns != null && this.filterType.StrIsOnlyColumns.equals("1")) {
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.StrIndexCode != null && this.filterType.StrIndexCode.length() != 0) {
                sResult = sResult + " and a.FIndexCode like '" +
                    filterType.StrIndexCode.replaceAll("'", "''") + "%'";
            }
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.StrExchangeCode != null && this.filterType.StrExchangeCode.length() != 0) {
                sResult = sResult + " and a.FExchangeCode like '" +
                    filterType.StrExchangeCode.replaceAll("'", "''") + "%'";
            }
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.strDesc != null && this.filterType.strDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.strDesc.replaceAll("'", "''") + "%'";
            }

        }
        return sResult;

    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.StrIndexCode = rs.getString("FIndexCode") + "";
        this.StrIndexName = rs.getString("FIndexName") + "";
        this.StrExchangeCode = rs.getString("FExchangeCode") + "";
        this.StrExchangeName = rs.getString("FExchangeName") + "";
        this.strDesc = rs.getString("FDesc") + "";
        super.setRecLog(rs);
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sDateStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "指数代码\t指数名称";
            strSql = "select y.* from " +
                "(select FIndexCode from " + pub.yssGetTableName("Tb_Para_Index") +
                " " +
                " where FCheckState = 1 group by FIndexCode) x join" +
                "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FExchangeName as FExchangeName" +
                " from " + pub.yssGetTableName("Tb_Para_Index") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FExchangecode,FExchangeName from tb_base_exchange) d on a.FExchangecode=d.FExchangecode" +
                buildFilterSql() + " order by FCheckState, FCreateTime desc) y " +
                "on x.FindexCode=y.FIndexCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FIndexCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FIndexName") + "").trim()).append(
                    YssCons.YSS_LINESPLITMARK);
                setResultSetAttr(rs);
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
            throw new YssException("获取指数信息出错！", e);
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
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
    }

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
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_Index") + "(FIndexCode,FIndexName,FExchangeCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)"
                + " values(" + dbl.sqlString(this.StrIndexCode) + ", " +
                dbl.sqlString(this.StrIndexName) + "," +
                dbl.sqlString(this.StrExchangeCode) + "," +
                dbl.sqlString(this.strDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + ", " +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增指数信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Index"),
                               "FIndexCode",
                               this.StrIndexCode, this.StrOldIndexCode);
    }

    /**修改时间：2008年3月25号
     *  修改人：单亮
     *  原方法功能：只能处理指数信息设置的审核和未审核的单条信息。
     *  新方法功能：可以处理指数信息设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     */

    public void checkSetting() throws YssException {
        //修改前的代码
//      String strSql = "";
//      boolean bTrans = false; //代表是否开始了事务
//      Connection conn = dbl.loadConnection();
//      try {
//         strSql = "update " + pub.yssGetTableName("Tb_Para_Index") +
//               " set FCheckState = " +
//               this.checkStateId +
//               ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//               ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//               "'" +
//               " where FIndexCode = " +
//               dbl.sqlString(this.StrOldIndexCode);
//
//         conn.setAutoCommit(false);
//         bTrans = true;
//         dbl.executeSql(strSql);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//      }
//      catch (Exception e) {
//         throw new YssException("审核指数信息出错", e);
//      }
//      finally {
//         dbl.endTransFinal(conn, bTrans);
//      }
        //修改后的代码
        //--------------------begin
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
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Para_Index") +
                        " set FCheckState = " +
                        this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "'" +
                        " where FIndexCode = " +
                        dbl.sqlString(this.StrOldIndexCode);
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (StrOldIndexCode != null && !StrOldIndexCode.equalsIgnoreCase("")) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_Index") +
                    " set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "'" +
                    " where FIndexCode = " +
                    dbl.sqlString(this.StrOldIndexCode);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核指数信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //--------------------end

    }

    /**
     * 删除数据即放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_Index") +
                " set FCheckState=" + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FIndexCode = " +
                dbl.sqlString(this.StrOldIndexCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("刪除指数信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_Index") +
                " set FIndexCode = " +
                dbl.sqlString(this.StrIndexCode) +
                ", FIndexName = " + dbl.sqlString(this.StrIndexName) +
                ", FExchangeCode = " + dbl.sqlString(this.StrExchangeCode) +
                ", FDesc = " + dbl.sqlString(this.strDesc) +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FIndexCode = " +
                dbl.sqlString(this.StrOldIndexCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新指数信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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
        buf.append(this.StrIndexCode).append("\t");
        buf.append(this.StrIndexName).append("\t");

        buf.append(this.StrExchangeCode).append("\t");
        buf.append(this.StrExchangeName).append("\t");

        buf.append(this.strDesc).append("\t");

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
        String[] reqAry = null;
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
            reqAry = sTmpStr.split("\t");
            this.StrIndexCode = reqAry[0];
            this.StrIndexName = reqAry[1];
            this.StrExchangeCode = reqAry[2];
            this.StrExchangeName = reqAry[3];
            //---edit by songjie 2011.08.17 BUG 2355 QDV4赢时胜(测试)2011年8月2日05_B start---//
            if(reqAry[4].indexOf("【Enter】") != -1){
            	this.strDesc = reqAry[4].replaceAll("【Enter】", "\r\n");
            }else{
            	this.strDesc = reqAry[4];
            }
            //---edit by songjie 2011.08.17 BUG 2355 QDV4赢时胜(测试)2011年8月2日05_B end---//
            this.checkStateId = Integer.parseInt(reqAry[5]);
            this.StrOldIndexCode = reqAry[6];
            this.StrOldExchangeCode = reqAry[7];
            this.StrIsOnlyColumns = reqAry[8];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new IndexBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析指数信息出错", e);
        }

    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        IndexBean befIndexBean = new IndexBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FExchangeName as FExchangeName" +
                " from " + pub.yssGetTableName("Tb_Para_Index") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FExchangecode,FExchangeName from tb_base_exchange) d on a.FExchangecode=d.FExchangecode" +
                " where a.FIndexCode=" + dbl.sqlString(this.StrOldIndexCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befIndexBean.StrIndexCode = rs.getString("FIndexCode") + "";
                befIndexBean.StrIndexName = rs.getString("FIndexName") + "";
                befIndexBean.StrExchangeCode = rs.getString("FExchangeCode") + "";
                befIndexBean.StrExchangeName = rs.getString("FExchangeName") + "";
                befIndexBean.strDesc = rs.getString("FDesc") + "";

            }
            return befIndexBean.buildRowStr();

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    /**
     * 删除回收站的数，即从数据库彻底删除数据
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
        	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equals("")) {
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
                        pub.yssGetTableName("Tb_Para_Index") +
                        " where FIndexCode = " +
                        dbl.sqlString(this.StrOldIndexCode);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而StrOldIndexCode不为空，则按照StrOldIndexCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (StrOldIndexCode != null && !StrOldIndexCode.equals("")) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_Index") +
                    " where FIndexCode = " +
                    dbl.sqlString(this.StrOldIndexCode);
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
}
