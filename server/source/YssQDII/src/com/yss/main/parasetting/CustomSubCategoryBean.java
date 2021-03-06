package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class CustomSubCategoryBean
    extends BaseDataSettingBean implements IDataSetting {
    private String categoryCode = ""; //品种代码
    private String categoryName = ""; //品种名称
    private String CustomSubcategoryCode = ""; //自定义品种代码
    private String CustomSubcategoryName = ""; //自定义品种名称
    private String desc = ""; //自定义品种描述
    private String oldCustomSubcategoryCode = "";
    private String sRecycled = "";

    private CustomSubCategoryBean filterType;
    public CustomSubCategoryBean() {
    }

    /**
     * buildRowStr
     * 解析自定义品种设置信息
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.categoryCode).append("\t");
        buf.append(this.categoryName).append("\t");
        buf.append(this.CustomSubcategoryCode).append("\t");
        buf.append(this.CustomSubcategoryName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Para_CustomCategory"),
                               "FCusCatCode",
                               this.CustomSubcategoryCode,
                               this.oldCustomSubcategoryCode);
    }

    /**
     * buildFilterSql
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.categoryCode.length() != 0) { // wdy add 20070901 添加表别名：a
                sResult = sResult + " and a.FCatCode like '" +
                    filterType.categoryCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.CustomSubcategoryCode.length() != 0) {
                sResult = sResult + " and a.FCusCatCode like '" +
                    filterType.CustomSubcategoryCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.CustomSubcategoryName.length() != 0) {
            	//---edit by songjie 2011.11.04 BUG 2341 QDV4赢时胜(测试)2011年8月1日2_B start---//
                sResult = sResult + " and a.FCusCatName like '" +
                    filterType.CustomSubcategoryName.replaceAll("'", "''") + "%'";
                //---edit by songjie 2011.11.04 BUG 2341 QDV4赢时胜(测试)2011年8月1日2_B end---//
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%' ";
            }

        }
        return sResult;

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
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String strSql = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql =
                "select a.*,d.FCatName," +
                " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_CustomCategory") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCatCode,FCatName from tb_base_category) d on a.FCatCode = d.FCatCode" +
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.categoryCode = rs.getString("FCatCode");
                this.categoryName = rs.getString("FCatName");
                this.CustomSubcategoryCode = rs.getString("FCusCatCode");
                this.CustomSubcategoryName = rs.getString("FCusCatName");
                this.desc = rs.getString("FDesc");
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
            throw new YssException("获取自定义品种信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData2
     * 获取已审核的自定义品种设置
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String strSql = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "自定义品种代码\t自定义品种名称\t品种名称\t品种描述";
            strSql =
                "select a.*,d.FCatName," +
                " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_CustomCategory") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCatCode,FCatName from tb_base_category) d on a.FCatCode = d.FCatCode" +
                ( (buildFilterSql().length() > 0) ? buildFilterSql() + " and " :
                 " where ") +
                " a.fcheckstate = 1 order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FCusCatCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FCusCatName") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FCatName") + "").trim()).append(
                    "\t");
                bufShow.append(YssFun.left( (rs.getString("FDesc") + "").trim(), 40)).
                    append(YssCons.YSS_LINESPLITMARK);

                this.categoryCode = rs.getString("FCatCode");
                this.categoryName = rs.getString("FCatName");
                this.CustomSubcategoryCode = rs.getString("FCusCatCode");
                this.CustomSubcategoryName = rs.getString("FCusCatName");
                this.desc = rs.getString("FDesc");
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
            throw new YssException("获取自定义品种信息出错", e);
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
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
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
            this.categoryCode = reqAry[0];
            this.categoryName = reqAry[1];
            this.CustomSubcategoryCode = reqAry[2];
            this.CustomSubcategoryName = reqAry[3];
            this.desc = reqAry[4];
            super.checkStateId = Integer.parseInt(reqAry[5]);
            this.oldCustomSubcategoryCode = reqAry[6];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CustomSubCategoryBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析自定义品种设置请求出错", e);
        }

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
     *
     * @param btOper byte
     */
    /* public void saveSetting(byte btOper) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
           if (btOper == YssCons.OP_ADD) {
              strSql = "insert into " +
                    pub.yssGetTableName("Tb_Para_CustomCategory") + "" +
                    " (FCusCatCode,FCusCatName,FCATCode,FDesc," +
                    "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                    " values(" + dbl.sqlString(this.CustomSubcategoryCode) + "," +
                    dbl.sqlString(this.CustomSubcategoryName) + "," +
                    dbl.sqlString(this.categoryCode) + "," +
                    dbl.sqlString(this.desc) + "," +
                    (pub.getSysCheckState() ? "0" : "1") + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) + ")";
           }
           else if (btOper == YssCons.OP_EDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_CustomCategory") +
                    " set FCusCatCode = " +
                    dbl.sqlString(this.CustomSubcategoryCode) + ",FCusCatName = " +
                    dbl.sqlString(this.CustomSubcategoryName) + ",FCATCode = " +
                    dbl.sqlString(this.categoryCode) + ",FDesc = " +
                    dbl.sqlString(this.desc) + ",FCheckState = " +
                    (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                    dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                    dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) +
                    " where FCusCatCode = " +
                    dbl.sqlString(this.oldCustomSubcategoryCode);
           }
           else if (btOper == YssCons.OP_DEL) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_CustomCategory") +
                    " set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "'" +
                    " where FCusCatCode = " +
                    dbl.sqlString(this.CustomSubcategoryCode) +
                    " and FCatCode = " + dbl.sqlString(this.categoryCode);
           }
           else if (btOper == YssCons.OP_AUDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_CustomCategory") +
                    " set FCheckState = " +
                    this.checkStateId;
              strSql += ", FCheckUser = '" + pub.getUserCode() +
                    "', FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FCusCatCode = " +
                    dbl.sqlString(this.CustomSubcategoryCode) +
                    " and FCatCode = " + dbl.sqlString(this.categoryCode);
           }

           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("更新自定义品种设置信息出错", e);
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
                pub.yssGetTableName("Tb_Para_CustomCategory") + "" +
                " (FCusCatCode,FCusCatName,FCATCode,FDesc," +
                "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.CustomSubcategoryCode) + "," +
                dbl.sqlString(this.CustomSubcategoryName) + "," +
                dbl.sqlString(this.categoryCode) + "," +
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
            throw new YssException("增加自定义品种设置信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_CustomCategory") +
                " set FCusCatCode = " +
                dbl.sqlString(this.CustomSubcategoryCode) + ",FCusCatName = " +
                dbl.sqlString(this.CustomSubcategoryName) + ",FCATCode = " +
                dbl.sqlString(this.categoryCode) + ",FDesc = " +
                dbl.sqlString(this.desc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FCusCatCode = " +
                dbl.sqlString(this.oldCustomSubcategoryCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改自定义品种设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除数据，即放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_CustomCategory") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FCusCatCode = " +
                dbl.sqlString(this.CustomSubcategoryCode) +
                " and FCatCode = " + dbl.sqlString(this.categoryCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除自定义品种设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 修改时间：2008年3月26号
     * 修改人：单亮
     * 原方法功能：只能处理自定义品种设置的审核和未审核的单条信息。
     * 新方法功能：可以处理自定义品种设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理自定义品种设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */

    public void checkSetting() throws YssException {
        //修改前的代码
//    String strSql = "";
//    boolean bTrans = false; //代表是否开始了事务
//    Connection conn = dbl.loadConnection();
//    try {
//       strSql = "update " + pub.yssGetTableName("Tb_Para_CustomCategory") +
//                   " set FCheckState = " +
//                   this.checkStateId;
//             strSql += ", FCheckUser = '" + pub.getUserCode() +
//                   "', FCheckTime = '" +
//                   YssFun.formatDatetime(new java.util.Date()) + "'" +
//                   " where FCusCatCode = " +
//                   dbl.sqlString(this.CustomSubcategoryCode) +
//                   " and FCatCode = " + dbl.sqlString(this.categoryCode);
//       conn.setAutoCommit(false);
//       bTrans = true;
//       dbl.executeSql(strSql);
//       conn.commit();
//       bTrans = false;
//       conn.setAutoCommit(true);
//    }
//
//    catch (Exception e) {
//       throw new YssException("审核自定义品种设置信息出错", e);
//    }
//    finally {
//       dbl.endTransFinal(conn, bTrans);
//    }
        //修改后的代码
        //-----------------begin
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
                    strSql = "update " +
                        pub.yssGetTableName("Tb_Para_CustomCategory") +
                        " set FCheckState = " +
                        this.checkStateId;
                    strSql += ", FCheckUser = '" + pub.getUserCode() +
                        "', FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FCusCatCode = " +
                        dbl.sqlString(this.CustomSubcategoryCode) +
                        " and FCatCode = " + dbl.sqlString(this.categoryCode);
                    dbl.executeSql(strSql);
                }
            //如果sRecycled为空，而CustomSubcategoryCode不为空，则按照CustomSubcategoryCode来执行sql语句
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            } else if (CustomSubcategoryCode != null && !CustomSubcategoryCode.equalsIgnoreCase("")) { 
                strSql = "update " +
                    pub.yssGetTableName("Tb_Para_CustomCategory") +
                    " set FCheckState = " +
                    this.checkStateId;
                strSql += ", FCheckUser = '" + pub.getUserCode() +
                    "', FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FCusCatCode = " +
                    dbl.sqlString(this.CustomSubcategoryCode) +
                    " and FCatCode = " + dbl.sqlString(this.categoryCode);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核自定义品种设置信息出错", e);
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
        CustomSubCategoryBean befEditBean = new CustomSubCategoryBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select a.*,d.FCatName," +
                " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_CustomCategory") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCatCode,FCatName from tb_base_category) d on a.FCatCode = d.FCatCode" +
                " where  FCusCatCode =" +
                dbl.sqlString(this.oldCustomSubcategoryCode) +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.categoryCode = rs.getString("FCatCode");
                befEditBean.categoryName = rs.getString("FCatName");
                befEditBean.CustomSubcategoryCode = rs.getString("FCusCatCode");
                befEditBean.CustomSubcategoryName = rs.getString("FCusCatName");
                befEditBean.desc = rs.getString("FDesc");
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    /**
     * 从回收站删除数据，即从数据库彻底删除数据
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
                        pub.yssGetTableName("Tb_Para_CustomCategory") +
                        " where FCusCatCode = " +
                        dbl.sqlString(this.CustomSubcategoryCode) +
                        " and FCatCode = " + dbl.sqlString(this.categoryCode);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而CustomSubcategoryCode不为空，则按照CustomSubcategoryCode来执行sql语句
            else if (CustomSubcategoryCode != "" && CustomSubcategoryCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_CustomCategory") +
                    " where FCusCatCode = " +
                    dbl.sqlString(this.CustomSubcategoryCode) +
                    " and FCatCode = " + dbl.sqlString(this.categoryCode);
                //执行sql语句
                dbl.executeSql(strSql);
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
