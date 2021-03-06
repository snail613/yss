package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title: CustomCategoryBean </p>
 * <p>Description: 自定义品种设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class CustomCategoryBean
    extends BaseDataSettingBean implements IDataSetting {
    private String categoryCode = ""; //品种代码
    private String categoryName = ""; //品种名称
    private String parentCode = ""; //上级代码
    private int orderCode; //排序编码
    private String rootCode = ""; //顶级品种代码
    private String rootName = ""; //顶级品种名称
    private String desc = ""; //品种描述
    private String oldCategoryCode = "";
    private int oldOrderCode;
    private int isAuditSubNode; //是否同时审核所有子结点，1为审核，0为不审核

    public CustomCategoryBean() {
    }

    /**
     * parseRowStr
     * 解析品种设置信息
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

            reqAry = sTmpStr.split("\t");
            this.categoryCode = reqAry[0];
            this.categoryName = reqAry[1];
            this.parentCode = reqAry[2];
            this.orderCode = Integer.parseInt(reqAry[3]);
            this.desc = reqAry[4];
            super.checkStateId = Integer.parseInt(reqAry[5]);
            this.oldCategoryCode = reqAry[6];
            this.oldOrderCode = Integer.parseInt(reqAry[7]);
            this.isAuditSubNode = Integer.parseInt(reqAry[8]);
            super.parseRecLog();

        } catch (Exception e) {
            throw new YssException("解析自定义品种设置请求出错", e);
        }

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.categoryCode).append("\t");
        buf.append(this.categoryName).append("\t");
        buf.append(this.parentCode).append("\t");
        buf.append(this.orderCode).append("\t");
        buf.append(this.rootCode).append("\t");
        buf.append(this.rootName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        String sql = "";
        String tmpValue = "";
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_CustomCategory"), "FCusCatCode",
                               this.categoryCode, this.oldCategoryCode);

        if ( (btOper == YssCons.OP_EDIT || btOper == YssCons.OP_ADD) &&
            this.oldOrderCode != this.orderCode) {
            sql =
                "select FCusCatName from " + pub.yssGetTableName("Tb_Para_CustomCategory") + " where FOrderCode = '" +
                dbFun.treeBuildOrderCode(pub.yssGetTableName("Tb_Para_CustomCategory"), "FCusCatCode",
                                         this.parentCode,
                                         this.orderCode) + "'";
            tmpValue = dbFun.GetValuebySql(sql);
            if (tmpValue.trim().length() > 0) {
                throw new YssException("品种排序号【" + this.orderCode +
                                       "】已被【" + tmpValue + "】占用，请重新输入品种排序号");
            }
        }

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
    public String getListViewData1() {
        return "";
    }

    /**
     * getListViewData2
     * 获取已经审核的顶级品种设置信息
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
            sHeader = "品种代码\t品种名称\t品种描述";
            strSql =
                "select a.*,FCusCatCode as FRootCode,FCusCatName as FRootName," +
                " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Para_CustomCategory") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where a.FCheckState = 1  and a.FParentCode = '[root]' order by a.FOrderCode";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FRootCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FRootName") + "").trim()).append(
                    "\t");
                bufShow.append(YssFun.left( (rs.getString("FDesc") + "").trim(), 40)).
                    append(YssCons.YSS_LINESPLITMARK);

                this.categoryCode = rs.getString("FCusCatCode");
                this.categoryName = rs.getString("FCusCatName");
                this.rootCode = rs.getString("FRootCode");
                this.rootName = rs.getString("FRootName");
                this.parentCode = rs.getString("FParentCode");
                this.desc = rs.getString("FDesc");
                this.orderCode = Integer.parseInt(YssFun.right(rs.getString(
                    "FOrderCode"), 3));

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
            throw new YssException("获取可用顶级自定义品种信息出错", e);
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
     * 获取自定义品种设置全部数据
     * @return String
     */
    public String getListViewData4() throws YssException {
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
     * 获取所有自定义品种设置信息
     * @return String
     */
    public String getTreeViewData1() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        StringBuffer buf = new StringBuffer();
        String sResult = "";
        try {
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Para_CustomCategory") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " order by a.FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.categoryCode = rs.getString("FCusCatCode");
                this.categoryName = rs.getString("FCusCatName");
                this.parentCode = rs.getString("FParentCode");
                this.desc = rs.getString("FDesc");
                this.orderCode = Integer.parseInt(YssFun.right(rs.getString(
                    "FOrderCode"), 3));
                super.setRecLog(rs);
                buf.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sResult = buf.toString().substring(0, buf.toString().length() - 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取自定义品种信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getTreeViewData2
     * 获取已经审核的品种设置信息
     * @return String
     */
    public String getTreeViewData2() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        StringBuffer buf = new StringBuffer();
        String sResult = "";
        try {
            strSql = "select a.*,FRootCode,FRootName," +
                " b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Para_CustomCategory") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCusCatCode as FRootCode,FOrderCode as FRootOrderCode," +
                " FCusCatName as FRootName from " + pub.yssGetTableName("Tb_Para_CustomCategory") + "" +
                " where FParentCode = '[root]') d on " +
                dbl.sqlLeft("a.FOrderCode", 3) + " = d.FRootOrderCode" +
                " where a.FCheckState = 1 order by a.FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.categoryCode = rs.getString("FCusCatCode");
                this.categoryName = rs.getString("FCusCatName");
                this.rootCode = rs.getString("FRootCode");
                this.rootName = rs.getString("FRootName");
                this.parentCode = rs.getString("FParentCode");
                this.desc = rs.getString("FDesc");
                this.orderCode = Integer.parseInt(YssFun.right(rs.getString(
                    "FOrderCode"), 3));
                super.setRecLog(rs);
                buf.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sResult = buf.toString().substring(0, buf.toString().length() - 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取自定义品种信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

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
        String strOrderCode = dbFun.treeBuildOrderCode(pub.yssGetTableName("Tb_Para_CustomCategory"),
              "FCusCatCode", this.parentCode, this.orderCode);
        if (strOrderCode.length() > 6) {
           throw new YssException("只能新建一级和二级自定义品种");
        }

        try {
           if (btOper == YssCons.OP_ADD) {
              strSql = "insert into " + pub.yssGetTableName("Tb_Para_CustomCategory") + "" +
                    " (FCusCatCode,FCusCatName,FParentCode,FOrderCode,FDesc," +
                    "FCheckState,FCreator,FCreateTime)" +
                    " values(" + dbl.sqlString(this.categoryCode) + "," +
                    dbl.sqlString(this.categoryName) + "," +
                    dbl.sqlString(this.parentCode) + "," +
                    dbl.sqlString(strOrderCode) + "," +
                    dbl.sqlString(this.desc) + "," +
                    this.checkStateId + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + ")";
           }
           else if (btOper == YssCons.OP_EDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_CustomCategory") + " set FCusCatCode = " +
                    dbl.sqlString(this.categoryCode) + ",FCusCatName = " +
                    dbl.sqlString(this.categoryName) + ",FParentCode = " +
                    dbl.sqlString(this.parentCode) + ",FOrderCode = " +
                    dbl.sqlString(strOrderCode) + ",FDesc = " +
                    dbl.sqlString(this.desc) + ", FCreator = " +
                    dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                    dbl.sqlString(this.creatorTime) +
                    " where FCusCatCode = " +
                    dbl.sqlString(this.oldCategoryCode);
           }
           else if (btOper == YssCons.OP_DEL) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_CustomCategory") + " set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "'" +
                    " where FCusCatCode = " + dbl.sqlString(this.categoryCode);
           }
           else if (btOper == YssCons.OP_AUDIT) {
              //对节点本身与所有子节点都进行审核
              strSql = "update " + pub.yssGetTableName("Tb_Para_CustomCategory") + " set FCheckState = " +
                    this.checkStateId;
                 strSql += ", FCheckUser = '" + pub.getUserCode() +
                       "', FCheckTime = '" +
                       YssFun.formatDatetime(new java.util.Date()) + "'";
                 if (this.isAuditSubNode == 1) {
                    strSql += " where FCheckState <> 2 and FOrderCode like '" +
                          strOrderCode + "%'";
                 }
                 else {
                    strSql += " where FCheckState <> 2 and FOrderCode = '" +
                          strOrderCode + "'";
                 }
              //审核时，对所有父节点也进行审核
              if (this.checkStateId == 1 && strOrderCode.length() > 3) {
                 strOrderCode = strOrderCode.substring(0,
                       strOrderCode.length() - 3);
                 String strParentCodeList = dbl.sqlString(strOrderCode);
                 while (strOrderCode.length() > 3) {
                    strParentCodeList += ",";
                    strOrderCode = strOrderCode.substring(0,
                          strOrderCode.length() - 3);
                    strParentCodeList += dbl.sqlString(strOrderCode);
                 }
                 strSql += " OR FOrderCode in (" + strParentCodeList + ")";
              }

           }

           //修改子节点
           if (btOper == YssCons.OP_EDIT) {
              if (this.orderCode != this.oldOrderCode) {
                 dbFun.treeAdjustOrder(pub.yssGetTableName("Tb_Para_CustomCategory"), "FCusCatCode",
                                       this.oldCategoryCode, this.orderCode);
              }
              dbFun.treeAdjustParentCode(pub.yssGetTableName("Tb_Para_CustomCategory"), "fparentcode",
                                         this.oldCategoryCode, this.categoryCode);
              dbFun.treeChangeParentCode(pub.yssGetTableName("Tb_Para_CustomCategory"), "FCusCatCode",
                                         this.oldCategoryCode, this.parentCode);
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
        String strOrderCode = dbFun.treeBuildOrderCode(pub.yssGetTableName(
            "Tb_Para_CustomCategory"),
            "FCusCatCode", this.parentCode, this.orderCode);
        if (strOrderCode.length() > 6) {
            throw new YssException("只能新建一级和二级自定义品种");
        }

        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_CustomCategory") + "" +
                " (FCusCatCode,FCusCatName,FParentCode,FOrderCode,FDesc," +
                "FCheckState,FCreator,FCreateTime)" +
                " values(" + dbl.sqlString(this.categoryCode) + "," +
                dbl.sqlString(this.categoryName) + "," +
                dbl.sqlString(this.parentCode) + "," +
                dbl.sqlString(strOrderCode) + "," +
                dbl.sqlString(this.desc) + "," +
                this.checkStateId + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加品种类型信息出错", e);
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
        String strOrderCode = dbFun.treeBuildOrderCode(pub.yssGetTableName(
            "Tb_Para_CustomCategory"),
            "FCusCatCode", this.parentCode, this.orderCode);
        if (strOrderCode.length() > 6) {
            throw new YssException("只能新建一级和二级自定义品种");
        }

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Para_CustomCategory") + " set FCusCatCode = " +
                dbl.sqlString(this.categoryCode) + ",FCusCatName = " +
                dbl.sqlString(this.categoryName) + ",FParentCode = " +
                dbl.sqlString(this.parentCode) + ",FOrderCode = " +
                dbl.sqlString(strOrderCode) + ",FDesc = " +
                dbl.sqlString(this.desc) + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) +
                " where FCusCatCode = " +
                dbl.sqlString(this.oldCategoryCode);
            dbl.executeSql(strSql);
            if (this.orderCode != this.oldOrderCode) {
                dbFun.treeAdjustOrder(pub.yssGetTableName("Tb_Para_CustomCategory"), "FCusCatCode",
                                      this.oldCategoryCode, this.orderCode);
            }
            dbFun.treeAdjustParentCode(pub.yssGetTableName("Tb_Para_CustomCategory"), "fparentcode",
                                       this.oldCategoryCode, this.categoryCode);
            dbFun.treeChangeParentCode(pub.yssGetTableName("Tb_Para_CustomCategory"), "FCusCatCode",
                                       this.oldCategoryCode, this.parentCode);

            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改品种类型信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_CustomCategory") + " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FCusCatCode = " + dbl.sqlString(this.categoryCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除品种类型信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        String strOrderCode = dbFun.treeBuildOrderCode(pub.yssGetTableName(
            "Tb_Para_CustomCategory"),
            "FCusCatCode", this.parentCode, this.orderCode);
        if (strOrderCode.length() > 6) {
            throw new YssException("只能新建一级和二级自定义品种");
        }

        try {
            //对节点本身与所有子节点都进行审核
            strSql = "update " + pub.yssGetTableName("Tb_Para_CustomCategory") + " set FCheckState = " +
                this.checkStateId;
            strSql += ", FCheckUser = '" + pub.getUserCode() +
                "', FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'";
            if (this.isAuditSubNode == 1) {
                strSql += " where FCheckState <> 2 and FOrderCode like '" +
                    strOrderCode + "%'";
            } else {
                strSql += " where FCheckState <> 2 and FOrderCode = '" +
                    strOrderCode + "'";
            }
            //审核时，对所有父节点也进行审核
            if (this.checkStateId == 1 && strOrderCode.length() > 3) {
                strOrderCode = strOrderCode.substring(0,
                    strOrderCode.length() - 3);
                String strParentCodeList = dbl.sqlString(strOrderCode);
                while (strOrderCode.length() > 3) {
                    strParentCodeList += ",";
                    strOrderCode = strOrderCode.substring(0,
                        strOrderCode.length() - 3);
                    strParentCodeList += dbl.sqlString(strOrderCode);
                }
                strSql += " OR FOrderCode in (" + strParentCodeList + ")";
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        }

        catch (Exception e) {
            throw new YssException("审核品种类型信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

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
        CustomCategoryBean befEditBean = new CustomCategoryBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_CustomCategory") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where  a.FCusCatCode =" + dbl.sqlString(this.oldCategoryCode) +
                " order by a.FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.categoryCode = rs.getString("FCusCatCode");
                befEditBean.categoryName = rs.getString("FCusCatName");
                befEditBean.parentCode = rs.getString("FParentCode");
                befEditBean.desc = rs.getString("FDesc");
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
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
