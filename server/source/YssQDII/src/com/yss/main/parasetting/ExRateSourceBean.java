package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title:ExRateSourceBean </p>
 * <p>Description:汇率来源设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ExRateSourceBean
    extends BaseDataSettingBean implements IDataSetting {

    private String exRateCode = ""; //汇率来源代码
    private String exRateName = ""; //汇率来源名称
    private String exRateDesc = ""; //汇率来源描述

    private ExRateSourceBean filterType;
    private String oldExRateCode = "";
    private String sRecycled = "";

    public ExRateSourceBean() {
    }

    /**
     * parseRowStr
     * 解析汇率来源设置
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
            this.exRateCode = reqAry[0];
            this.exRateName = reqAry[1];
            this.exRateDesc = reqAry[2];
            super.checkStateId = Integer.parseInt(reqAry[3]);
            this.oldExRateCode = reqAry[4];
            super.parseRecLog();

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new ExRateSourceBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析汇率来源设置请求出错", e);
        }

    }

    /**
     * buildRowStr
     * 生成汇率来源数据字符串
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.exRateCode).append("\t");
        buf.append(this.exRateName).append("\t");
        buf.append(this.exRateDesc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Para_ExRateSource"),
                               "FExRateSrcCode",
                               this.exRateCode, this.oldExRateCode);

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
     * buildFilterSql
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.exRateCode.length() != 0) {
                sResult = sResult + " and a.FExRateSrcCode like '" +
                    filterType.exRateCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.exRateName.length() != 0) {
                sResult = sResult + " and a.FExRateSrcName like '" +
                    filterType.exRateName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.exRateDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.exRateDesc.replaceAll("'", "''") + "%'";
            }
            return sResult;
        }

        return "";
    }

    /**
     * getListViewData1
     * 获取汇率来源数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        
        if (pub.getPrefixTB().length() == 0) {
        	//--- #580 建信上线需提供部分方案支持 add by jiangshichao 2011.03.24
            //return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        	strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_base_ExRateSource") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            " order by a.FCheckState, a.FCreateTime desc";
        	//--- #580 建信上线需提供部分方案支持 add by jiangshichao 2011.03.24 
        }else{
        strSql =
            "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Para_ExRateSource") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            " order by a.FCheckState, a.FCreateTime desc";
        }
        return this.builderListViewData(strSql);

    }

    /**
     * getListViewData2
     * 获取已审核的汇率来源数据
     * @return String
     */
    public String getListViewData2() throws YssException {
        /* String strSql = "";
         strSql =
         "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
               pub.yssGetTableName("Tb_Para_ExRateSource") + " a " +
               " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
               " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
               " where fcheckstate = 1 order by FCheckState, FCreateTime desc";
         return this.builderListViewData(strSql);*/
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "汇率来源代码\t汇率来源名称\t描述";
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_ExRateSource") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where a.fcheckstate = 1 order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FExRateSrcCode") + "").trim()).
                    append("\t");
                bufShow.append( (rs.getString("FExRateSrcName") + "").trim()).
                    append("\t");
                bufShow.append( (rs.getString("FDesc") + "").trim()).append(
                    YssCons.YSS_LINESPLITMARK);

                this.exRateCode = rs.getString("FExRateSrcCode") + "";
                this.exRateName = rs.getString("FExRateSrcName") + "";
                this.exRateDesc = rs.getString("FDesc") + "";
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
            throw new YssException("获取汇率来源信息出错！", e);
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

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.exRateCode = rs.getString("FExRateSrcCode") + "";
                this.exRateName = rs.getString("FExRateSrcName") + "";
                this.exRateDesc = rs.getString("FDesc") + "";
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取汇率来源信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

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
              strSql = "insert into " +
                    pub.yssGetTableName("Tb_Para_ExRateSource") +
                    "(FExRateSrcCode,FExRateSrcName,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) values(" +
                    dbl.sqlString(this.exRateCode) + "," +
                    dbl.sqlString(this.exRateName) + "," +
                    dbl.sqlString(this.exRateDesc) + "," +
                    (pub.getSysCheckState() ? "0" : "1") + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) + ")";
           }
           if (btOper == YssCons.OP_EDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_ExRateSource") +
                    " set FExRateSrcCode =" +
                    dbl.sqlString(this.exRateCode) + ",FExRateSrcName = " +
                    dbl.sqlString(this.exRateName) + ",FDesc = " +
                    dbl.sqlString(this.exRateDesc) + ",FCheckState = " +
                    (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                    dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                    dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) +
     " where FExRateSrcCode = " + dbl.sqlString(this.oldExRateCode);
           }
           else if (btOper == YssCons.OP_DEL) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_ExRateSource") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FExRateSrcCode = " +
                    dbl.sqlString(this.exRateCode);
           }

           else if (btOper == YssCons.OP_AUDIT) {
              System.out.println(this.checkStateId);
              strSql = "update " + pub.yssGetTableName("Tb_Para_ExRateSource") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FExRateSrcCode = " +
                    dbl.sqlString(this.exRateCode);

           }
           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);

        }
        catch (Exception e) {
           throw new YssException("设置汇率来源信息出错！", e);
        }
        finally {
           dbl.endTransFinal(conn, bTrans);
        }

     }*/






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
              strSql = "insert into " +
                    pub.yssGetTableName("Tb_Para_ExRateSource") +
                    "(FExRateSrcCode,FExRateSrcName,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) values(" +
                    dbl.sqlString(this.exRateCode) + "," +
                    dbl.sqlString(this.exRateName) + "," +
                    dbl.sqlString(this.exRateDesc) + "," +
                    (pub.getSysCheckState() ? "0" : "1") + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) + ")";
           }
           if (btOper == YssCons.OP_EDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_ExRateSource") +
                    " set FExRateSrcCode =" +
                    dbl.sqlString(this.exRateCode) + ",FExRateSrcName = " +
                    dbl.sqlString(this.exRateName) + ",FDesc = " +
                    dbl.sqlString(this.exRateDesc) + ",FCheckState = " +
                    (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                    dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                    dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) +
     " where FExRateSrcCode = " + dbl.sqlString(this.oldExRateCode);
           }
           else if (btOper == YssCons.OP_DEL) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_ExRateSource") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FExRateSrcCode = " +
                    dbl.sqlString(this.exRateCode);
           }

           else if (btOper == YssCons.OP_AUDIT) {
              System.out.println(this.checkStateId);
              strSql = "update " + pub.yssGetTableName("Tb_Para_ExRateSource") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FExRateSrcCode = " +
                    dbl.sqlString(this.exRateCode);

           }
           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);

        }
        catch (Exception e) {
           throw new YssException("设置汇率来源信息出错！", e);
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
            strSql = "insert into " +
                pub.yssGetTableName("Tb_Para_ExRateSource") +
                "(FExRateSrcCode,FExRateSrcName,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) values(" +
                dbl.sqlString(this.exRateCode) + "," +
                dbl.sqlString(this.exRateName) + "," +
                dbl.sqlString(this.exRateDesc) + "," +
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
            throw new YssException("增加汇率来源设置出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_ExRateSource") +
                " set FExRateSrcCode =" +
                dbl.sqlString(this.exRateCode) + ",FExRateSrcName = " +
                dbl.sqlString(this.exRateName) + ",FDesc = " +
                dbl.sqlString(this.exRateDesc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FExRateSrcCode = " + dbl.sqlString(this.oldExRateCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改汇率来源设置出错", e);
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
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_ExRateSource") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FExRateSrcCode = " +
                dbl.sqlString(this.exRateCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除汇率来源设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 修改时间：2008年3月23号
     * 修改人：单亮
     * 原方法功能：只能处理期间连接的审核和未审核的单条信息。
     * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理期间连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        //修改前的代码
//      String strSql = "";
//      boolean bTrans = false; //代表是否开始了事务
//      Connection conn = dbl.loadConnection();
//      try {
//         strSql = "update " + pub.yssGetTableName("Tb_Para_ExRateSource") +
//               " set FCheckState = " +
//               this.checkStateId + ", FCheckUser = " +
//               dbl.sqlString(pub.getUserCode()) +
//               ", FCheckTime = '" +
//               YssFun.formatDatetime(new java.util.Date()) + "'" +
//               " where FExRateSrcCode = " +
//               dbl.sqlString(this.exRateCode);
//         conn.setAutoCommit(false);
//         bTrans = true;
//         dbl.executeSql(strSql);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//      }
//
//      catch (Exception e) {
//         throw new YssException("审核汇率来源设置出错", e);
//      }
//      finally {
//         dbl.endTransFinal(conn, bTrans);
//      }
        //修改后的代码
        //-------------------begin
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

                    strSql = "update " + pub.yssGetTableName("Tb_Para_ExRateSource") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FExRateSrcCode = " +
                        dbl.sqlString(this.exRateCode);
                    dbl.executeSql(strSql);
                }
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            } else if (exRateCode != null && !exRateCode.equalsIgnoreCase("")) { //如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
                strSql = "update " + pub.yssGetTableName("Tb_Para_ExRateSource") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FExRateSrcCode = " +
                    dbl.sqlString(this.exRateCode);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核汇率来源设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //--------------------end
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
        ExRateSourceBean befEditBean = new ExRateSourceBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_ExRateSource") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where  a.FExRateSrcCode =" + dbl.sqlString(this.oldExRateCode) +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.exRateCode = rs.getString("FExRateSrcCode") + "";
                befEditBean.exRateName = rs.getString("FExRateSrcName") + "";
                befEditBean.exRateDesc = rs.getString("FDesc") + "";
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }

    }

    /**
     * 删除回收站的数据，即彻底从数据库删除数据
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
        	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && sRecycled != "") {
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
                        pub.yssGetTableName("Tb_Para_ExRateSource") +
                        " where FExRateSrcCode = " +
                        dbl.sqlString(this.exRateCode);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而exRateCode不为空，则按照exRateCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A 
            else if (exRateCode != null && exRateCode != "") {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_ExRateSource") +
                    " where FExRateSrcCode = " +
                    dbl.sqlString(this.exRateCode);
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
