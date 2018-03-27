package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title: KeeperBean 保管人</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class KeeperBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strCode; //保管人代码
    private String strName; //保管人名称
    private String strShortName; //保管人简称
    private String strStartDate; //启用日期
    private String strAddress; //办公地址
    private String strPostcode; //邮政编码
    private String strDesc; //描述

    private String linkMans;
    private String strOldCode;
    private String strOldStartDate;
    private KeeperBean filterType;
    private String sRecycled = "";

    public KeeperBean() {
    }

    /**
     * parseRowStr
     * 解析保管人数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
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
                if (sRowStr.split("\r\t").length == 3) {
                    this.linkMans = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.strCode = reqAry[0];
            this.strName = reqAry[1];
            this.strShortName = reqAry[2];
            this.strStartDate = reqAry[3];
            this.strAddress = reqAry[4];
            this.strPostcode = reqAry[5];
            this.strDesc = reqAry[6].replaceAll("\f<>", "\r\n");//edite by zhouxiang 编号：  MS01405  标题：  机构设置描述信息中包含回车换行符，删除记录后，在回收站中清除时会报错  
            this.checkStateId = Integer.parseInt(reqAry[7]);
            this.strOldCode = reqAry[8];
            this.strOldStartDate = reqAry[9];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new KeeperBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析保管人设置请求信息出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.strCode).append("\t");
        buffer.append(this.strName).append("\t");
        buffer.append(this.strShortName).append("\t");
        buffer.append(this.strStartDate).append("\t");
        buffer.append(this.strAddress).append("\t");
        buffer.append(this.strPostcode).append("\t");
        buffer.append(this.strDesc).append("\t");
        buffer.append(super.buildRecLog());
        return buffer.toString();
    }

    /**
     * checkInput
     * 检查保管人输入数据是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
    	//----edit by songjie 2011.03.11 不以启用日期作为查询主键数据的参数----//
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Keeper"), "FKeeperCode",
                               this.strCode,
                               this.strOldCode);
        //----edit by songjie 2011.03.11 不以启用日期作为查询主键数据的参数----//
    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() throws YssException {
        StringBuffer buf = new StringBuffer();
        String strSql = "", strResult = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Para_Keeper") + "" +
                " order by FCheckState, FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                buf.append( (rs.getString("FKeeperCode") + "").trim()).append("\t");
                buf.append( (rs.getString("FkeeperName") + "").trim()).append("\t");
                buf.append( (rs.getString("FKeeperShortName") + "").trim()).append(
                    "\t");
                buf.append(rs.getString("FStartDate")).append("\t");
                buf.append( (rs.getString("FOfficeAddr") + "").trim()).append("\t");
                buf.append( (rs.getString("FPostcode") + "").trim()).append("\t");
                buf.append( (rs.getString("FDesc") + "").trim()).append("\t");
                buf.append(super.buildRecLog(rs)).append(YssCons.YSS_LINESPLITMARK);

            }
            strResult = buf.toString();
            if (strResult.length() > 2) {
                strResult = strResult.substring(0, strResult.length() - 2);
            }
            return strResult;
        } catch (Exception e) {
            throw new YssException("获取保管人信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setKeeperAttr(rs);
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
            throw new YssException("获取保管人信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 此方法已被修改
     * 修改时间：2008年2月23号
     * 修改人：单亮
     * 原方法的功能：查询出费用连接数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select y.* from " +
            //----delete by songjie 2011.03.11 不以最大的启用日期查询保管人设置数据----//
//            "(select FKeeperCode,max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_Keeper") + " " +
//            " where FStartDate <= " +
//            dbl.sqlDate(new java.util.Date()) +
//            //修改前的代码
//            // "and FCheckState <> 2 group by FKeeperCode) x join" +
//            //修改后的代码
//            //----------------------------begin
//            " group by FKeeperCode) x join" +
            //----delete by songjie 2011.03.11 不以最大的启用日期查询保管人设置数据----//
            //----------------------------end
            " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Para_Keeper") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            ") y " +//edit by songjie 2011.03.11 不以最大的启用日期查询保管人设置数据
            " order by y.FCheckState, y.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     * 获取保管人全部数据
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";
        strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_Para_Keeper") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData2
     * 获取已审核的保管人数据
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "保管人代码\t保管人名称";//edit by songjie 2011.03.14 不显示启用日期
            strSql = "select y.* from " +
                //----delete by songjie 2011.03.11 不以最大的启用日期查询保管人设置数据----//
//                "(select FKeeperCode,max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_Keeper") + " " +
//                " where FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
//                "and FCheckState = 1 group by FKeeperCode) x join" +
                //----delete by songjie 2011.03.11 不以最大的启用日期查询保管人设置数据----//
                //----edit by songjie 2011.03.11 不以最大的启用日期查询保管人设置数据----//
                " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + 
                " (select * from " + pub.yssGetTableName("Tb_Para_Keeper") + " where FCheckState = 1 ) a" +
                //----edit by songjie 2011.03.11 不以最大的启用日期查询保管人设置数据----//
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                ") y " +//edit by songjie 2011.03.11 不以最大的启用日期查询保管人设置数据
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FKeeperCode") + "").trim());
                bufShow.append("\t");
                bufShow.append( (rs.getString("FKeeperName") + "").trim());
                bufShow.append(YssCons.YSS_LINESPLITMARK);//edit by songjie 2011.03.14 不显示启用日期
                //----delete by songjie 2011.03.14 不显示启用日期----//
//                bufShow.append( (YssFun.formatDate(rs.getDate("FStartDate"),
//                    YssCons.YSS_DATEFORMAT) + "").trim());
//                bufShow.append(YssCons.YSS_LINESPLITMARK);
                //----delete by songjie 2011.03.14 不显示启用日期----//
                setKeeperAttr(rs);
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
            throw new YssException("获取可用保管人信息出错！", e);
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
     * getetting
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
    /*  public void saveSetting(byte btOper) throws YssException {
         Connection conn = dbl.loadConnection();
         boolean bTrans = false;
         String strSql = "";
         try {
            if (btOper == YssCons.OP_ADD) {
               strSql =
                     "insert into " + pub.yssGetTableName("Tb_Para_Keeper") + "(FKeeperCode, FKeeperName, FKeeperShortName, " +
                     " FStartDate, FOfficeAddr, FPOSTALCODE, FDesc, FCheckState, FCreator, FCreateTime,FCheckUser) " +
                     " values(" + dbl.sqlString(this.strCode) + "," +
                     dbl.sqlString(this.strName) + "," +
                     dbl.sqlString(this.strShortName) + "," +
                     dbl.sqlDate(this.strStartDate) + "," +
                     dbl.sqlString(this.strAddress) + "," +
                     dbl.sqlString(this.strPostcode) + "," +
                     dbl.sqlString(this.strDesc) + "," +
                     (pub.getSysCheckState()?"0":"1") + "," +
                     dbl.sqlString(this.creatorCode) + ", " +
                     dbl.sqlString(this.creatorTime) + "," +
                     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
            }
            else if (btOper == YssCons.OP_EDIT) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_Keeper") + " set FKeeperCode = " +
                     dbl.sqlString(this.strCode) + ", FKeeperName = "
                     + dbl.sqlString(this.strName) + ",FKeeperShortName = " +
                     dbl.sqlString(this.strShortName) + ",FStartDate = "
                     + dbl.sqlDate(this.strStartDate) + ", FOfficeAddr = "
                     + dbl.sqlString(this.strAddress) + ", FPOSTALCODE = " +
                     dbl.sqlString(this.strPostcode) + ", FDesc=" +
                     dbl.sqlString(this.strDesc) + ",FCheckState = " +
                     (pub.getSysCheckState()?"0":"1") + ", FCreator = " +
                     dbl.sqlString(this.creatorCode) + " , FCreateTime = " +
                     dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                     " where FKeeperCode = " + dbl.sqlString(this.strOldCode) +
                     " and FStartDate=" + dbl.sqlDate(this.strOldStartDate);
            }
            else if (btOper == YssCons.OP_DEL) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_Keeper") + " set FCheckState = " +
                     this.checkStateId + ", FCheckUser = " +
                     dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" +
                     YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where FKeeperCode = " +
                     dbl.sqlString(this.strCode) +
                     " and FStartDate=" +
                     dbl.sqlDate(this.strStartDate);
            }
            else if (btOper == YssCons.OP_AUDIT) {
               strSql = "update " + pub.yssGetTableName("Tb_Para_Keeper") + " set FCheckState = " +
                     this.checkStateId + ", FCheckUser = " +
                     dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" +
                     YssFun.formatDatetime(new java.util.Date()) + "'" +
                     " where FKeeperCode = " +
                     dbl.sqlString(this.strCode) + " and FStartDate=" +
                     dbl.sqlDate(this.strStartDate);
            }
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
// 联系人处理
            if (btOper == YssCons.OP_EDIT && (this.strCode != this.strOldCode ||
                                              this.strStartDate != this.strOldStartDate)) {
               strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set FRelaCode = " +
                     dbl.sqlString(this.strCode) + ", FStartDate = " +
                     dbl.sqlDate(this.strStartDate) +
                     " where FRelaCode = " +
                     dbl.sqlString(this.strOldCode) +
                     " and FRelaType = 'Keeper'" +
                     " and FStartDate = " +
                     dbl.sqlDate(this.strOldStartDate);
               dbl.executeSql(strSql);
            }

            if (this.linkMans != null) {
               if (btOper == YssCons.OP_ADD || btOper == YssCons.OP_EDIT) {
                  LinkManBean linkMan = new LinkManBean();
                  linkMan.setYssPub(pub);
                  linkMan.saveMutliSetting(this.linkMans);
               }
            }
            if (btOper == YssCons.OP_DEL) {
               strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set FCheckState = " +
                     this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                     "' where fRelaCode = " +
                     dbl.sqlString(this.strCode) +
                     " and frelatype = 'Keeper'" +
                    " and FStartDate = " +
                    dbl.sqlDate(this.strStartDate);

               dbl.executeSql(strSql);
            }
            if (btOper == YssCons.OP_AUDIT) {
               strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set FCheckState = " +
                     this.checkStateId +
                     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                     "' where frelacode = " +
                     dbl.sqlString(this.strCode) +
                     " and frelatype = 'Keeper'" +
                    " and FStartDate = " +
                    dbl.sqlDate(this.strStartDate);

               dbl.executeSql(strSql);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
         }
         catch (Exception e) {
            throw new YssException("设置保管人信息出错", e);
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
        LinkManBean linkMan = new LinkManBean();
        linkMan.setYssPub(pub);
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Para_Keeper") + "(FKeeperCode, FKeeperName, FKeeperShortName, " +
                " FStartDate, FOfficeAddr, FPOSTALCODE, FDesc, FCheckState, FCreator, FCreateTime,FCheckUser) " +
                " values(" + dbl.sqlString(this.strCode) + "," +
                dbl.sqlString(this.strName) + "," +
                dbl.sqlString(this.strShortName) + "," +
                dbl.sqlDate(this.strStartDate) + "," +
                dbl.sqlString(this.strAddress) + "," +
                dbl.sqlString(this.strPostcode) + "," +
                dbl.sqlString(this.strDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + ", " +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + ")";
            dbl.executeSql(strSql);

            if (this.linkMans != null) {

                // LinkManBean linkMan = new LinkManBean();
                //  linkMan.setYssPub(pub);
                linkMan.saveMutliSetting(this.linkMans);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            LinkManBean filterType = new LinkManBean();
            filterType.setRelaType("Keeper");
            filterType.setRelaCode(this.strCode);
            filterType.setStartDate(YssFun.toDate(this.strStartDate));
            linkMan.setFilterType(filterType);
//            filterType = linkMan.getFilterType();
            this.setASubData(linkMan.getListViewData1());

        }

        catch (Exception e) {
            throw new YssException("增加保管人信息出错", e);
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
        LinkManBean linkMan = new LinkManBean();
        linkMan.setYssPub(pub);
        try {
            LinkManBean bFilterType = new LinkManBean();
            bFilterType.setRelaType("Keeper");
            //     bFilterType.setRelaCode(this.strCode);
            bFilterType.setRelaCode(this.strOldCode);
            bFilterType.setStartDate(YssFun.toDate(this.strStartDate));
            linkMan.setFilterType(bFilterType);
            bFilterType = linkMan.getFilterType();
            this.setBSubData(linkMan.getListViewData1());

            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Para_Keeper") + " set FKeeperCode = " +
                dbl.sqlString(this.strCode) + ", FKeeperName = "
                + dbl.sqlString(this.strName) + ",FKeeperShortName = " +
                dbl.sqlString(this.strShortName) + ",FStartDate = "
                + dbl.sqlDate(this.strStartDate) + ", FOfficeAddr = "
                + dbl.sqlString(this.strAddress) + ", FPOSTALCODE = " +
                dbl.sqlString(this.strPostcode) + ", FDesc=" +
                dbl.sqlString(this.strDesc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + " , FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FKeeperCode = " + dbl.sqlString(this.strOldCode) +
                " and FStartDate=" + dbl.sqlDate(this.strOldStartDate);
            dbl.executeSql(strSql);
            //modified by yeshenghong story 2457 修正之前的错误
            if (!this.strCode.equals(this.strOldCode )||
                !this.strStartDate.equals(this.strOldStartDate)) {
                strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
                    " set FRelaCode = " +
                    dbl.sqlString(this.strCode) + ", FStartDate = " +
                    dbl.sqlDate(this.strStartDate) +
                    " where FRelaCode = " +
                    dbl.sqlString(this.strOldCode) +
                    " and FRelaType = 'Keeper'" +
                    " and FStartDate = " +
                    dbl.sqlDate(this.strOldStartDate);
                dbl.executeSql(strSql);
            }

            if (this.linkMans != null) {
                //  LinkManBean linkMan = new LinkManBean();
                // linkMan.setYssPub(pub);
                linkMan.saveMutliSetting(this.linkMans);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            LinkManBean filterType = new LinkManBean();
            filterType.setRelaType("Keeper");
            filterType.setRelaCode(this.strCode);
            filterType.setStartDate(YssFun.toDate(this.strStartDate));
            linkMan.setFilterType(filterType);
//            filterType = linkMan.getFilterType();
            this.setASubData(linkMan.getListViewData1());

        }

        catch (Exception e) {
            throw new YssException("修改保管人信息出错", e);
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
        LinkManBean linkMan = new LinkManBean();
        linkMan.setYssPub(pub);
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Para_Keeper") + " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FKeeperCode = " +
                dbl.sqlString(this.strCode) +
                " and FStartDate=" +
                dbl.sqlDate(this.strStartDate);
            dbl.executeSql(strSql);
            strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where fRelaCode = " +
                dbl.sqlString(this.strCode) +
                " and frelatype = 'Keeper'" +
                " and FStartDate = " +
                dbl.sqlDate(this.strStartDate);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            LinkManBean filterType = new LinkManBean();
            filterType.setRelaType("Keeper");
            filterType.setRelaCode(this.strCode);
            filterType.setStartDate(YssFun.toDate(this.strStartDate));
            linkMan.setFilterType(filterType);
//            filterType = linkMan.getFilterType();
            this.setASubData(linkMan.getListViewData1());

        }

        catch (Exception e) {
            throw new YssException("删除保管人信息出错", e);
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
        //       String strSql = "";
        //       boolean bTrans = false; //代表是否开始了事务
        //       Connection conn = dbl.loadConnection();
        //       try {
        //          conn.setAutoCommit(false);
        //          bTrans = true;
        //          strSql = "update " + pub.yssGetTableName("Tb_Para_Keeper") + " set FCheckState = " +
        //                     this.checkStateId + ", FCheckUser = " +
        //                     dbl.sqlString(pub.getUserCode()) +
        //                     ", FCheckTime = '" +
        //                     YssFun.formatDatetime(new java.util.Date()) + "'" +
        //                     " where FKeeperCode = " +
        //                     dbl.sqlString(this.strCode) + " and FStartDate=" +
        //                     dbl.sqlDate(this.strStartDate);
        //           dbl.executeSql(strSql);
        //           strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set FCheckState = " +
        //              this.checkStateId +
        //              ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
        //              ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
        //              "' where frelacode = " +
        //              dbl.sqlString(this.strCode) +
        //              " and frelatype = 'Keeper'" +
        //             " and FStartDate = " +
        //             dbl.sqlDate(this.strStartDate);
        //               dbl.executeSql(strSql);
        //          conn.commit();
        //          bTrans = false;
        //          conn.setAutoCommit(true);
        //       }
        //
        //       catch (Exception e) {
        //          throw new YssException("审核保管人信息出错", e);
        //       }
        //       finally {
        //          dbl.endTransFinal(conn, bTrans);
        //       }
        //修改后的代码
        //---------------------begin
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "update " + pub.yssGetTableName("Tb_Para_Keeper") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FKeeperCode = " +
                        dbl.sqlString(this.strCode) + " and FStartDate=" +
                        dbl.sqlDate(this.strStartDate);
                    dbl.executeSql(strSql);
                    strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
                        " set FCheckState = " +
                        this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where frelacode = " +
                        dbl.sqlString(this.strCode) +
                        " and frelatype = 'Keeper'" +
                        " and FStartDate = " +
                        dbl.sqlDate(this.strStartDate);
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而strCode不为空，则按照strCode来执行sql语句
            else if ( strCode != null && !strCode.equalsIgnoreCase("")) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_Keeper") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FKeeperCode = " +
                    dbl.sqlString(this.strCode) + " and FStartDate=" +
                    dbl.sqlDate(this.strStartDate);
                dbl.executeSql(strSql);
                strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
                    " set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where frelacode = " +
                    dbl.sqlString(this.strCode) +
                    " and frelatype = 'Keeper'" +
                    " and FStartDate = " +
                    dbl.sqlDate(this.strStartDate);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核保管人信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //-------------------end
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
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.strCode != null && this.filterType.strCode.length() != 0) {
                sResult = sResult + " and a.FKeeperCode like '" +
                    filterType.strCode.replaceAll("'", "''") + "%'";
            }
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.strName != null && this.filterType.strName.length() != 0) {
                sResult = sResult + " and a.FKeeperName like '" +
                    filterType.strName.replaceAll("'", "''") + "%'";
            }
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.strShortName != null && this.filterType.strShortName.length() != 0) {
                sResult = sResult + " and a.FKeeperShortName like '" +
                    filterType.strShortName.replaceAll("'", "''") + "%'";
            }
            //----delete by songjie 2011.03.11 不以最大的启用日期查询保管人设置数据----//
//            if (this.filterType.strStartDate.length() != 0 &&
//                !this.filterType.strStartDate.equals("9998-12-31")) {
//                sResult = sResult + " and a.FStartdate <= " +
//                    dbl.sqlDate(filterType.strStartDate);
//            }
            //----delete by songjie 2011.03.11 不以最大的启用日期查询保管人设置数据----//
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.strAddress != null && this.filterType.strAddress.length() != 0) {
                sResult = sResult + " and a.FOfficeAddr like '" +
                    filterType.strAddress.replaceAll("'", "''") + "%'";
            }
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.strPostcode != null && this.filterType.strPostcode.length() != 0) {
                sResult = sResult + " and a.FPostalCode like '" +
                    filterType.strPostcode.replaceAll("'", "''") + "%'";
            }
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (this.filterType.strDesc != null && this.filterType.strDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.strDesc.replaceAll("'", "''") + "%'";
            }

        }
        return sResult;
    }

    public void setKeeperAttr(ResultSet rs) throws SQLException {
        this.strCode = rs.getString("FKeeperCode") + "";
        this.strName = rs.getString("FKeeperName") + "";
        this.strShortName = rs.getString("FKeeperShortName") + "";
        this.strStartDate = YssFun.formatDate(rs.getDate("FStartDate"));
        this.strAddress = rs.getString("FOfficeAddr") + "";
        this.strPostcode = rs.getString("FPostalcode") + "";
        this.strDesc = rs.getString("FDesc") + "";
        super.setRecLog(rs);
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
        KeeperBean befEditBean = new KeeperBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                //----delete by songjie 2011.03.11 不以最大的启用日期查询保管人设置数据----//
//                "(select FKeeperCode,max(FStartDate) as FStartDate from " + pub.yssGetTableName("Tb_Para_Keeper") + " " +
//                " where FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
//                "and FCheckState <> 2 group by FKeeperCode) x join" +
                //----delete by songjie 2011.03.11 不以最大的启用日期查询保管人设置数据----//
                //----edit by songjie 2011.03.11 不以最大的启用日期查询保管人设置数据----//
                " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " + 
                " ( select * from " + pub.yssGetTableName("Tb_Para_Keeper") + " where FCheckState <> 2 ) a" +
                //----edit by songjie 2011.03.11 不以最大的启用日期查询保管人设置数据----//
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where  FKeeperCode =" + dbl.sqlString(this.strOldCode) +
                ") y " +//edit by songjie 2011.03.11 不以最大的启用日期查询保管人设置数据
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.strCode = rs.getString("FKeeperCode") + "";
                befEditBean.strName = rs.getString("FKeeperName") + "";
                befEditBean.strShortName = rs.getString("FKeeperShortName") + "";
                befEditBean.strStartDate = YssFun.formatDate(rs.getDate("FStartDate"));
                befEditBean.strAddress = rs.getString("FOfficeAddr") + "";
                befEditBean.strPostcode = rs.getString("FPostalcode") + "";
                befEditBean.strDesc = rs.getString("FDesc") + "";
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }

    }

    /**
     * 从回收站删除数据，即数据库彻底删除数据
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
                        pub.yssGetTableName("Tb_Para_Keeper") +
                        " where FKeeperCode = " +
                        dbl.sqlString(this.strCode) +
                        " and FStartDate=" +
                        dbl.sqlDate(this.strStartDate);
                    //执行sql语句
                    dbl.executeSql(strSql);
                    strSql = "delete from " +
                        pub.yssGetTableName("tb_para_linkman") +
                        " where fRelaCode = " +
                        dbl.sqlString(this.strCode) +
                        " and frelatype = 'Keeper'" +
                        " and FStartDate = " +
                        dbl.sqlDate(this.strStartDate);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而strCode不为空，则按照strCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (strCode != null && !strCode.equals("")) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_Keeper") +
                    " where FKeeperCode = " +
                    dbl.sqlString(this.strCode) +
                    " and FStartDate=" +
                    dbl.sqlDate(this.strStartDate);
                //执行sql语句
                dbl.executeSql(strSql);
                strSql = "delete from " +
                    pub.yssGetTableName("tb_para_linkman") +
                    " where fRelaCode = " +
                    dbl.sqlString(this.strCode) +
                    " and frelatype = 'Keeper'" +
                    " and FStartDate = " +
                    dbl.sqlDate(this.strStartDate);
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
