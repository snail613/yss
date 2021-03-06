package com.yss.main.basesetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title: AreaBean </p>
 * <p>Description: 地区设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class AreaBean
    extends BaseDataSettingBean implements IDataSetting {
    private String areaCode; //地区代码
    private String areaName; //地区名称
    private String countryCode; //国家代码
    private String countryName; //国家名称
    private String regionCode; //地域代码
    private String regionName; //地域名称
    private String desc; //国家描述
    private AreaBean filterType;
    private String sRecycled = ""; //为增加还原和删除功能加的一个中介字符串变量 bug MS00149 QDV4南方2009年1月5日05_B 2009.01.14 方浩
    private String oldAreaCode;

    public AreaBean() {
    }

    /**
     * parseRowStr
     * 解析地区设置
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
            sRecycled = sRowStr; //bug MS00149 QDV4南方2009年1月5日05_B 2009.01.13 方浩
            reqAry = sTmpStr.split("\t");
            this.areaCode = reqAry[0];
            this.areaName = reqAry[1];
            this.countryCode = reqAry[2];
            this.regionCode = reqAry[3];
            this.desc = reqAry[4];
            this.checkStateId = Integer.parseInt(reqAry[5]);
            this.oldAreaCode = reqAry[6];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new AreaBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析地区设置请求出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.areaCode).append("\t");
        buf.append(this.areaName).append("\t");
        buf.append(this.countryCode).append("\t");
        buf.append(this.countryName).append("\t");
        buf.append(this.regionCode).append("\t");
        buf.append(this.regionName).append("\t");
        buf.append(this.desc).append("\t");
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
            if (this.filterType.areaCode.length() != 0) { // wdy add 20070903 添加表别名:a
                sResult = sResult + " and a.FAreaCode like '" +
                    filterType.areaCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.areaName.length() != 0) {
                sResult = sResult + " and a.FAreaName like '" +
                    filterType.areaName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.countryCode.length() != 0) {
                sResult = sResult + " and a.FCountryCode = " +
                    dbl.sqlString(filterType.countryCode);
            }
            if (this.filterType.regionCode.length() != 0) {
                sResult = sResult + " and a.FRegionCode = " +
                    dbl.sqlString(filterType.regionCode);
            }
        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取地区设置信息
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.*,b.FCountryName, c.FRegionName, d.FUserName as FCreatorName,e.FUserName as FCheckUserName from Tb_Base_Area a " +
                " left join (select FCountryCode,FCountryName from Tb_Base_Country where FCheckState = 1) b on a.FCountryCode = b.FCountryCode" +
                " left join (select FRegionCode,FRegionName from Tb_Base_Region where FCheckState = 1) c on a.FRegionCode = c.FRegionCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                buildFilterSql() +
                " order by a.FCheckState, a.FCreateTime desc"; // wdy modify 20070830

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.areaCode = rs.getString("FAreaCode") + "";
                this.areaName = rs.getString("FAreaName") + "";
                this.regionCode = rs.getString("FRegionCode") + "";
                this.regionName = rs.getString("FRegionName") + "";
                this.countryCode = rs.getString("FCountryCode") + "";
                this.countryName = rs.getString("FCountryName") + "";
                this.desc = YssFun.left(rs.getString("FDesc") + "", 40);
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
            throw new YssException("获取地区信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData2
     * 获取已审核的地区设置信息
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
            sHeader = "地区代码\t地区名称\t国家名称\t地域名称";
            strSql = "select a.*,b.FCountryName, c.FRegionName, d.FUserName as FCreatorName,e.FUserName as FCheckUserName from Tb_Base_Area a " +
                " left join (select FCountryCode,FCountryName from Tb_Base_Country where FCheckState = 1) b on a.FCountryCode = b.FCountryCode" +
                " left join (select FRegionCode,FRegionName from Tb_Base_Region where FCheckState = 1) c on a.FRegionCode = c.FRegionCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " where a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FAreaCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FAreaName") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FCountryName") + "").trim()).
                    append(
                        "\t");
                bufShow.append( (rs.getString("FRegionName") + "").trim()).
                    append(
                        YssCons.YSS_LINESPLITMARK);

                this.areaCode = rs.getString("FAreaCode") + "";
                this.areaName = rs.getString("FAreaName") + "";
                this.regionCode = rs.getString("FRegionCode") + "";
                this.regionName = rs.getString("FRegionName") + "";
                this.countryCode = rs.getString("FCountryCode") + "";
                this.countryName = rs.getString("FCountryName") + "";
                this.desc = YssFun.left(rs.getString("FDesc") + "", 40);
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取可用地区信息出错", e);
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
    public IDataSetting getSetting() {
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
        dbFun.checkInputCommon(btOper, "Tb_Base_Area", "FAreaCode",
                               this.areaCode, this.oldAreaCode);
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
           strSql = "insert into Tb_Base_Area" +
                 "(FAreaCode,FAreaName,FCountryCode,FRegionCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                 " values(" + dbl.sqlString(this.areaCode) + "," +
                 dbl.sqlString(this.areaName) + "," +
                 dbl.sqlString(this.countryCode) + " ," +
                 dbl.sqlString(this.regionCode) + "," +
                 dbl.sqlString(this.desc) + "," +
                 (pub.getSysCheckState()?"0":"1") + "," +
                 dbl.sqlString(this.creatorCode) + "," +
                 dbl.sqlString(this.creatorTime) + "," +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
        }
        else if (btOper == YssCons.OP_EDIT) {
           strSql = "update Tb_Base_Area set FAreaCode = " +
                 dbl.sqlString(this.areaCode) + ", FAreaName = " +
                 dbl.sqlString(this.areaName) + " , FCountryCode = " +
                 dbl.sqlString(this.countryCode) + ", FRegionCode = " +
                 dbl.sqlString(this.regionCode) + ", FDesc = " +
                 dbl.sqlString(this.desc) + ",FCheckState = " +
                 (pub.getSysCheckState()?"0":"1") + ", FCreator = " +
                 dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                 dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                 " where FAreaCode = " +
                 dbl.sqlString(this.oldAreaCode);
        }
        else if (btOper == YssCons.OP_DEL) {
     strSql = "update Tb_Base_Area set FCheckState = " + this.checkStateId +
                       ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                       " where FAreaCode = " + dbl.sqlString(this.areaCode);

        }
        else if (btOper == YssCons.OP_AUDIT) {
           strSql = "update Tb_Base_Area set FCheckState = " +
                 this.checkStateId + ", FCheckUser = " +
                 dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                 YssFun.formatDatetime(new java.util.Date()) + "'" +
                 " where FAreaCode = " + dbl.sqlString(this.areaCode);
        }
        conn.setAutoCommit(false);
        bTrans = true;
        dbl.executeSql(strSql);
        conn.commit();
        bTrans = false;
        conn.setAutoCommit(true);
     }
     catch (Exception e) {
        throw new YssException("更新地区设置信息出错", e);
     }finally{
        dbl.endTransFinal(conn, bTrans);
     }
     }*/

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into Tb_Base_Area" +
                "(FAreaCode,FAreaName,FCountryCode,FRegionCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.areaCode) + "," +
                dbl.sqlString(this.areaName) + "," ;
           //edited by zhouxiang  MS01410    地区设置中的国家代码和地域代码应该设置为黄色。    QDV4赢时胜(测试)2010年7月06日1_B    
            if(this.countryCode.trim().length()>0){
            	strSql += dbl.sqlString(this.countryCode)+",";
            }
            else{
            	strSql += "' ',";
            }
            if(this.regionCode.trim().length()>0){
            	strSql += dbl.sqlString(this.regionCode)+",";
            }
            else{
            	strSql += "' ',";
            }
          
            strSql += dbl.sqlString(this.desc) + "," +
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
        } catch (Exception e) {
            throw new YssException("新增地区设置信息出错", e);
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
        /*  String strSql = "";
          boolean bTrans = false; //代表是否开始了事务
          Connection conn = dbl.loadConnection();
          try {
             strSql = "update Tb_Base_Area set FCheckState = " +
                      this.checkStateId + ", FCheckUser = " +
                      dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                      YssFun.formatDatetime(new java.util.Date()) + "'" +
                      " where FAreaCode = " + dbl.sqlString(this.areaCode);


             conn.setAutoCommit(false);
             bTrans = true;
             dbl.executeSql(strSql);
             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
          }
          catch (Exception e) {
             throw new YssException("审核地区设置信息出错", e);
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
                    strSql = "update Tb_Base_Area set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FAreaCode = " +
                        dbl.sqlString(this.areaCode); //更新数据的SQL语句
                    dbl.executeSql(strSql); //执行更新操作
                }
            }
            //如果sRecycled为空，而areaCode不为空，则按照areaCode来执行sql语句
            else if (areaCode != null && (!areaCode.equalsIgnoreCase(""))) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                strSql = "update Tb_Base_Area set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FAreaCode = " + dbl.sqlString(this.areaCode); //更新数据的SQL语句
                dbl.executeSql(strSql); //执行更新操作
            }
            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核地区设置信息出错", e);
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
            strSql = "update Tb_Base_Area set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FAreaCode = " + dbl.sqlString(this.areaCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除地区设置信息出错", e);
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
            strSql = "update Tb_Base_Area set FAreaCode = " +
                dbl.sqlString(this.areaCode) + ", FAreaName = " +
                dbl.sqlString(this.areaName) + " , FCountryCode = " +
                dbl.sqlString(this.countryCode) + ", FRegionCode = " +
            	//edit by yangshaokai 2011.12.27 BUG 3439 QDV4赢时胜(测试)2011年12月19日05_B
                ((this.regionCode.trim().length() == 0) ? "' '" : dbl.sqlString(this.regionCode)) +
                ", FDesc = " +
                dbl.sqlString(this.desc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FAreaCode = " +
                dbl.sqlString(this.oldAreaCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新地区设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

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
        AreaBean befEditBean = new AreaBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*,b.FCountryName, c.FRegionName, d.FUserName as FCreatorName,e.FUserName as FCheckUserName from Tb_Base_Area a " +
                " left join (select FCountryCode,FCountryName from Tb_Base_Country where FCheckState = 1) b on a.FCountryCode = b.FCountryCode" +
                " left join (select FRegionCode,FRegionName from Tb_Base_Region where FCheckState = 1) c on a.FRegionCode = c.FRegionCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " where a.FAreaCode =" + dbl.sqlString(this.oldAreaCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.areaCode = rs.getString("FAreaCode") + "";
                befEditBean.areaName = rs.getString("FAreaName") + "";
                befEditBean.regionCode = rs.getString("FRegionCode") + "";
                befEditBean.regionName = rs.getString("FRegionName") + "";
                befEditBean.countryCode = rs.getString("FCountryCode") + "";
                befEditBean.countryName = rs.getString("FCountryName") + "";
                befEditBean.desc = YssFun.left(rs.getString("FDesc") + "", 40);
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

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
                        pub.yssGetTableName("Tb_Base_Area") +
                        " where FAreaCode = " +
                        dbl.sqlString(this.areaCode);

                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而areaCode不为空，则按照areaCode来执行sql语句
            else if (areaCode != "" && areaCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Base_Area") +
                    " where FAreaCode = " + dbl.sqlString(this.areaCode);

                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
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
