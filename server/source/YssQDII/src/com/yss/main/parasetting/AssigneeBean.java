package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title: AssigneeBean </p>
 * <p>Description: 受托人</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class AssigneeBean
    extends BaseDataSettingBean implements IDataSetting {

    private String assigneeCode; //受托人代码
    private String assigneeName; //受托人名称
    private String asisgneeShortName; //受托人简称
    private String officeAddr; //办公地址
    private String PostalCode; //邮政编码
    private String Desc; //描述

    private String oldAssigneeCode;

    private java.util.Date startDate; //启用日期
    private java.util.Date oldStartDate;

    private String linkMans;

    private AssigneeBean filterType;
    private String sRecycled = "";

    public AssigneeBean() {
    }

    /**
     * 解析受托人数据
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpAry = "";
        try {
            if (sRowStr.length() == 0) {
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
                sTmpAry = sRowStr.split("\r\t")[0];
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new AssigneeBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
                if (sRowStr.split("\r\t").length == 3) {
                    this.linkMans = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpAry = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpAry.split("\t");
            this.assigneeCode = reqAry[0];
            this.assigneeName = reqAry[1];
            this.startDate = YssFun.toDate(reqAry[2]);
            this.asisgneeShortName = reqAry[3];
            this.officeAddr = reqAry[4];
            this.PostalCode = reqAry[5];
            //------ modify by wangzuochun 2010.05.28 MS01196 受托人设置描述信息中的信息如果存在换行，清除此信息时报错 QDV4赢时胜(测试)2010年5月14日2_B
            if (reqAry[6] != null ){
            	if (reqAry[6].indexOf("【Enter】") >= 0){
            		this.Desc = reqAry[6].replaceAll("【Enter】", "\r\n");
            	}
            	else{
            		this.Desc = reqAry[6];
            	}
            }
            //------------------MS01196----------------//
            super.checkStateId = Integer.parseInt(reqAry[7]);
            this.oldAssigneeCode = reqAry[8];
            this.oldStartDate = YssFun.toDate(reqAry[9]);
            super.parseRecLog();
        } catch (Exception e) {
            throw new YssException("解析受托人设置请求出错", e);
        }

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.assigneeCode.trim());
        buf.append("\t");
        buf.append(this.assigneeName.trim());
        buf.append("\t");
        buf.append(YssFun.formatDate(this.startDate, YssCons.YSS_DATEFORMAT));
        buf.append("\t");
        buf.append(this.asisgneeShortName.trim());
        buf.append("\t");
        buf.append(this.officeAddr.trim());
        buf.append("\t");
        buf.append(this.PostalCode.trim());
        buf.append("\t");
        buf.append(this.Desc.trim());
        buf.append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * buildFilterSql
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.assigneeCode.length() != 0) {
                sResult = sResult + " and a.FAssigneeCode like '" + // wdy add 20070903 添加表别名:a
                    filterType.assigneeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.assigneeName.length() != 0) {
                sResult = sResult + " and a.FAssigneeName like '" +
                    filterType.assigneeName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.startDate != null &&
                !this.filterType.startDate.equals(YssFun.toDate("9998-12-31"))
                //edit by songjie 2011.03.11 不以启用日期筛选数据
                && !this.filterType.startDate.equals(YssFun.toDate("1900-01-01"))) {
                sResult = sResult + " and a.FStartDate <= " +
                    dbl.sqlDate(filterType.startDate);
            }
            if (this.filterType.asisgneeShortName.length() != 0) {
                sResult = sResult + " and a.FAssigneeShortName like '" +
                    filterType.asisgneeShortName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.officeAddr.length() != 0) {
                sResult = sResult + " and a.FOfficeAddr like '" +
                    filterType.officeAddr.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.PostalCode.length() != 0) {
                sResult = sResult + " and a.FPostalCode like '" +
                    filterType.PostalCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.Desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.Desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * checkInput
     * 检查输入的数据是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
    	//----edit by songjie 2011.03.11 不以启用日期查询主键数据----//
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Assignee"),
                               "FAssigneeCode",
                               this.assigneeCode,
                               this.oldAssigneeCode);
        //----edit by songjie 2011.03.11 不以启用日期查询主键数据----//
    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = ""; //表头
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer buf = new StringBuffer(); //用于显示的属性
        StringBuffer buf1 = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                buf.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setAssigneeAttr(rs);
                buf1.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sShowDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            if (buf1.toString().length() > 2) {
                sAllDataStr = buf1.toString().substring(0,
                    buf1.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();

        } catch (Exception e) {
            throw new YssException("获取受托人设置数据出错", e);
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
            //----delete by songjie 2011.03.11 不以最大的启用日期查询受托人设置数据----//
//            "(select FAssigneeCode,FCheckState,max(FStartDate) as FStartDate from " +
//            pub.yssGetTableName("Tb_Para_Assignee") + " " +
//            " where FStartDate <= " +
//            dbl.sqlDate(new java.util.Date()) +
//            //修改前的代码
//            //"and FCheckState <> 2 group by FAssigneeCode,FCheckState) x join" +
//            //修改后的代码
//            //----------------------------begin
//            " group by FAssigneeCode,FCheckState) x join" +
            //----delete by songjie 2011.03.11 不以最大的启用日期查询受托人设置数据----//
            //----------------------------end
            " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Para_Assignee") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            ") y " +//edit by songjie 2011.03.11 不以最大的启用日期查询受托人设置数据
            " order by y.FCheckState, y.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     * 获取受托人全部数据
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";
        strSql =
            "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Para_Assignee") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData2
     * 获取已审核的受托人数据
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = ""; //表头
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer buf = new StringBuffer(); //用于显示的属性
        StringBuffer buf1 = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        String strSql = "";
        try {
            sHeader = "受托人代码\t受托人名称\t启用日期";
            strSql = "select y.* from " +
                //----delete by songjie 2011.03.11 不以最大的启用日期查询受托人设置数据----//
//                "(select FAssigneeCode,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_Assignee") + " " +
//                " where FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
//                "and FCheckState = 1 group by FAssigneeCode) x join" +
                //----delete by songjie 2011.03.11 不以最大的启用日期查询受托人设置数据----//
                " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                //edit by songjie 2011.03.11 不以最大的启用日期查询受托人设置数据
                " (select * from " + pub.yssGetTableName("Tb_Para_Assignee") + " where FCheckState = 1 ) a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                ") y " +//edit by songjie 2011.03.11 不以最大的启用日期查询受托人设置数据
                " order by y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                buf.append( (rs.getString("FAssigneeCode") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FAssigneeName") + "").trim());
                buf.append("\t");
                buf.append( (YssFun.formatDate(rs.getDate("FStartDate"),
                                               YssCons.YSS_DATEFORMAT) + "").trim());
                buf.append(YssCons.YSS_LINESPLITMARK);

                setAssigneeAttr(rs);
                buf1.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sShowDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }
            if (buf1.toString().length() > 2) {
                sAllDataStr = buf1.toString().substring(0,
                    buf1.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取可用受托人信息出错");
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
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
     *
        public String getTreeViewData1() {
       return "";
        }

        /**
      * getTreeViewData2
      *
      * @eturn String
      */

     //publc String getTreeViewData2() {
     //   return "";
     //}

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
     * 更新受托人信息
     * @param btOper byte
     */
    /* public void saveSetting(byte btOper) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
           conn.setAutoCommit(false);
           bTrans = true;
           if (btOper == YssCons.OP_ADD) {
     strSql = "insert into " + pub.yssGetTableName("Tb_Para_Assignee") + " " +
                    " (FAssigneeCode,FAssigneeName,FStartDate,FAssigneeShortName,FOfficeAddr,FPostalCode,FDesc," +
                    "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                    " values (" + dbl.sqlString(this.assigneeCode) + "," +
                    dbl.sqlString(this.assigneeName) + "," +
                    dbl.sqlDate(this.startDate) + "," +
                    dbl.sqlString(this.asisgneeShortName) + "," +
                    dbl.sqlString(this.officeAddr) + "," +
                    dbl.sqlString(this.PostalCode) + "," +
                    dbl.sqlString(this.Desc) + "," +
                    (pub.getSysCheckState()?"0":"1") + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
           }
           else if (btOper == YssCons.OP_EDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_Assignee") + " set FAssigneeCode = " +
                    dbl.sqlString(this.assigneeCode) + ",FAssigneeName = " +
                    dbl.sqlString(this.assigneeName) + ",FStartDate = " +
                    dbl.sqlDate(this.startDate) + ",FAssigneeShortName = " +
                    dbl.sqlString(this.asisgneeShortName) + ",FOfficeAddr = " +
                    dbl.sqlString(this.officeAddr) + ",FPostalCode = " +
                    dbl.sqlString(this.PostalCode) + ",FDesc = " +
                    dbl.sqlString(this.Desc) + ",FCheckState = " +
                    (pub.getSysCheckState()?"0":"1") + ",FCreator = " +
                    dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                    dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                    " where FAssigneeCode = " +
                    dbl.sqlString(this.oldAssigneeCode) +
                    " and FStartDate = " +
                    dbl.sqlDate(this.oldStartDate);
           }
           else if (btOper == YssCons.OP_DEL) {
              //     strSql = "delete from Tb_Para_Assignee where FAssigneeCode = " +
              //删除时将审核标志修改为2
              strSql = "update " + pub.yssGetTableName("Tb_Para_Assignee") + " set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "' where FAssigneeCode = " +
                    dbl.sqlString(this.assigneeCode) +
                    " and FStartDate = " +
                    dbl.sqlDate(this.startDate);
           }
           else if (btOper == YssCons.OP_AUDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_Assignee") + " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FAssigneeCode = " +
                    dbl.sqlString(this.assigneeCode) +
                    " and FStartDate = " +
                    dbl.sqlDate(this.startDate);
           }
           dbl.executeSql(strSql);
//联系人处理
     if (btOper == YssCons.OP_EDIT && (this.assigneeCode != this.oldAssigneeCode ||
     this.startDate != this.oldStartDate)) {
              strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set FRelaCode = " +
                    dbl.sqlString(this.assigneeCode) + ", FStartDate = " +
                    dbl.sqlDate(this.startDate) +
                    " where FRelaCode = " +
                    dbl.sqlString(this.oldAssigneeCode) +
                    " and FRelaType = 'Assignee'" +
                    " and FStartDate = " +
                    dbl.sqlDate(this.oldStartDate);
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
              //   strSql = "delete from tb_para_linkman where frelacode = " +
              //删除时将审核标志修改为2
              strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "' where fRelaCode = " +
                    dbl.sqlString(this.assigneeCode) +
                    " and frelatype = 'Assignee'" +
                    " and FStartDate = " +
                    dbl.sqlDate(this.startDate);
              dbl.executeSql(strSql);
           }
           if (btOper == YssCons.OP_AUDIT) {
              strSql = "update " + pub.yssGetTableName("tb_para_linkman") + " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where frelacode = " +
                    dbl.sqlString(this.assigneeCode) +
                    " and frelatype = 'Assignee'" +
                    " and FStartDate = " +
                    dbl.sqlDate(this.startDate);
              dbl.executeSql(strSql);
           }

           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("更新受托人信息出错", e);
        }
        finally {
           dbl.endTransFinal(conn, bTrans);
        }
     }*/

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     *
     * @param rs ResultSet
     * @throws SQLException
     */
    public void setAssigneeAttr(ResultSet rs) throws SQLException {
        this.assigneeCode = rs.getString("FAssigneeCode") + "";
        this.assigneeName = rs.getString("FAssigneeName") + "";
        this.startDate = rs.getDate("FStartDate");
        this.asisgneeShortName = rs.getString("FAssigneeShortName") + "";
        this.officeAddr = rs.getString("FOfficeAddr") + "";
        this.PostalCode = rs.getString("FPostalCode") + "";
        this.Desc = rs.getString("FDesc") + "";
        super.setRecLog(rs);
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
        String linkStr = "";
        //-------------------------------------------
        LinkManBean linkMan = new LinkManBean();
        linkMan.setYssPub(pub);
        //-------------------------------------------
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_Assignee") +
                " " +
                " (FAssigneeCode,FAssigneeName,FStartDate,FAssigneeShortName,FOfficeAddr,FPostalCode,FDesc," +
                "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values (" + dbl.sqlString(this.assigneeCode) + "," +
                dbl.sqlString(this.assigneeName) + "," +
                dbl.sqlDate(this.startDate) + "," +
                dbl.sqlString(this.asisgneeShortName) + "," +
                dbl.sqlString(this.officeAddr) + "," +
                dbl.sqlString(this.PostalCode) + "," +
                dbl.sqlString(this.Desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ")";
            if (this.linkMans != null) {
                //  linkMan.setYssPub(pub);
                linkMan.saveMutliSetting(this.linkMans);
            }
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            //------------------------------------------
            LinkManBean filterType = new LinkManBean();
            filterType.setRelaType("Assignee");
            filterType.setRelaCode(this.assigneeCode);
            filterType.setStartDate(this.startDate);
            linkMan.setFilterType(filterType);
            //filterType = linkMan.getFilterType();//STORY #2475 根据FindBugs工具，对系统进行全面检查  zhangjun
            this.setASubData(linkMan.getListViewData1());
            //------------------------------------------
        }

        catch (Exception e) {
            throw new YssException("增加受托人信息出错", e);
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
        //--------------------------------------
        LinkManBean linkMan = new LinkManBean();
        linkMan.setYssPub(pub);
        //--------------------------------------
        try {
            //------------------------------------------
            LinkManBean bFilterType = new LinkManBean();
            bFilterType.setRelaType("Assignee");
            //  bFilterType.setRelaCode(this.assigneeCode);

            bFilterType.setRelaCode(this.oldAssigneeCode);
            bFilterType.setStartDate(this.startDate);
            linkMan.setFilterType(bFilterType);
            bFilterType = linkMan.getFilterType();
            this.setBSubData(linkMan.getListViewData1());
            //------------------------------------------
            conn.setAutoCommit(false);
            bTrans = true;
			/**shashijie 2012-7-2 STORY 2475 */
            if (!this.assigneeCode.equals(this.oldAssigneeCode) ||
			/**end*/
                this.startDate != this.oldStartDate) {
                strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
                    " set FRelaCode = " +
                    dbl.sqlString(this.assigneeCode) + ", FStartDate = " +
                    dbl.sqlDate(this.startDate) +
                    " where FRelaCode = " +
                    dbl.sqlString(this.oldAssigneeCode) +
                    " and FRelaType = 'Assignee'" +
                    " and FStartDate = " +
                    dbl.sqlDate(this.oldStartDate);
                dbl.executeSql(strSql);
            }
            if (this.linkMans != null) {
                // linkMan.setYssPub(pub);
                linkMan.saveMutliSetting(this.linkMans);
            }

            strSql = "update " + pub.yssGetTableName("Tb_Para_Assignee") +
                " set FAssigneeCode = " +
                dbl.sqlString(this.assigneeCode) + ",FAssigneeName = " +
                dbl.sqlString(this.assigneeName) + ",FStartDate = " +
                dbl.sqlDate(this.startDate) + ",FAssigneeShortName = " +
                dbl.sqlString(this.asisgneeShortName) + ",FOfficeAddr = " +
                dbl.sqlString(this.officeAddr) + ",FPostalCode = " +
                dbl.sqlString(this.PostalCode) + ",FDesc = " +
                dbl.sqlString(this.Desc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FAssigneeCode = " +
                dbl.sqlString(this.oldAssigneeCode) +
                " and FStartDate = " +
                dbl.sqlDate(this.oldStartDate);

            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //------------------------------------------
            LinkManBean filterType1 = new LinkManBean();
            filterType1.setRelaType("Assignee");
            filterType1.setRelaCode(this.assigneeCode);
            filterType1.setStartDate(this.startDate);
            linkMan.setFilterType(filterType1);
            filterType1 = linkMan.getFilterType();//findbugs风险调整，局部变量名与成员变量名相同 胡坤 20120626
            this.setASubData(linkMan.getListViewData1());
            //------------------------------------------

        }

        catch (Exception e) {
            throw new YssException("修改受托人信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除数据即将信息放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        //--------------------------------------
        LinkManBean linkMan = new LinkManBean();
        linkMan.setYssPub(pub);
        //--------------------------------------
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //     strSql = "delete from Tb_Para_Assignee where FAssigneeCode = " +
            //删除时将审核标志修改为2
            strSql = "update " + pub.yssGetTableName("Tb_Para_Assignee") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FAssigneeCode = " +
                dbl.sqlString(this.assigneeCode) +
                " and FStartDate = " +
                dbl.sqlDate(this.startDate);
            dbl.executeSql(strSql);
            strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where fRelaCode = " +
                dbl.sqlString(this.assigneeCode) +
                " and frelatype = 'Assignee'" +
                " and FStartDate = " +
                dbl.sqlDate(this.startDate);

            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //------------------------------------------
            LinkManBean filterType = new LinkManBean();
            filterType.setRelaType("Assignee");
            filterType.setRelaCode(this.assigneeCode);
            filterType.setStartDate(this.startDate);
            linkMan.setFilterType(filterType);
            //filterType = linkMan.getFilterType();//STORY #2475 根据FindBugs工具，对系统进行全面检查  zhangjun
            this.setASubData(linkMan.getListViewData1());
            //------------------------------------------

        }

        catch (Exception e) {
            throw new YssException("删除受托人信息出错", e);
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
//      //--------------------------------------
//      LinkManBean linkMan = new LinkManBean();
//      linkMan.setYssPub(pub);
//      //--------------------------------------
//      try {
//         conn.setAutoCommit(false);
//         bTrans = true;
//         strSql = "update " + pub.yssGetTableName("Tb_Para_Assignee") +
//               " set FCheckState = " +
//               this.checkStateId + ", FCheckUser = " +
//               dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
//               YssFun.formatDatetime(new java.util.Date()) + "'" +
//               " where FAssigneeCode = " +
//               dbl.sqlString(this.assigneeCode) +
//               " and FStartDate = " +
//               dbl.sqlDate(this.startDate);
//         dbl.executeSql(strSql);
//         strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
//               " set FCheckState = " +
//               this.checkStateId + ", FCheckUser = " +
//               dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
//               YssFun.formatDatetime(new java.util.Date()) + "'" +
//               " where frelacode = " +
//               dbl.sqlString(this.assigneeCode) +
//               " and frelatype = 'Assignee'" +
//               " and FStartDate = " +
//               dbl.sqlDate(this.startDate);
//
//         dbl.executeSql(strSql);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//
//         //------------------------------------------
//         LinkManBean filterType = new LinkManBean();
//         filterType.setRelaType("Assignee");
//         filterType.setRelaCode(this.assigneeCode);
//         filterType.setStartDate(this.startDate);
//         linkMan.setFilterType(filterType);
//         filterType = linkMan.getFilterType();
//         this.setASubData(linkMan.getListViewData1());
//        //------------------------------------------
//
//      }
//
//      catch (Exception e) {
//         throw new YssException("审核受托人信息出错", e);
//      }
//      finally {
//         dbl.endTransFinal(conn, bTrans);
//      }
        //修改后的代码
        //----------------begin
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        //--------------------------------------
        LinkManBean linkMan = new LinkManBean();
        linkMan.setYssPub(pub);
        //--------------------------------------
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Para_Assignee") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FAssigneeCode = " +
                        dbl.sqlString(this.assigneeCode) +
                        " and FStartDate = " +
                        dbl.sqlDate(this.startDate);
                    dbl.executeSql(strSql);
                    strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
                        " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where frelacode = " +
                        dbl.sqlString(this.assigneeCode) +
                        " and frelatype = 'Assignee'" +
                        " and FStartDate = " +
                        dbl.sqlDate(this.startDate);

                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而assigneeCode不为空，则按照assigneeCode来执行sql语句
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (assigneeCode != null && (!assigneeCode.equalsIgnoreCase(""))) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_Assignee") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FAssigneeCode = " +
                    dbl.sqlString(this.assigneeCode) +
                    " and FStartDate = " +
                    dbl.sqlDate(this.startDate);
                dbl.executeSql(strSql);
                strSql = "update " + pub.yssGetTableName("tb_para_linkman") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where frelacode = " +
                    dbl.sqlString(this.assigneeCode) +
                    " and frelatype = 'Assignee'" +
                    " and FStartDate = " +
                    dbl.sqlDate(this.startDate);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            //------------------------------------------
            LinkManBean filterType = new LinkManBean();
            filterType.setRelaType("Assignee");
            filterType.setRelaCode(this.assigneeCode);
            filterType.setStartDate(this.startDate);
            linkMan.setFilterType(filterType);
            filterType = linkMan.getFilterType();
            this.setASubData(linkMan.getListViewData1());
            //------------------------------------------
        } catch (Exception e) {
            throw new YssException("审核受托人信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //-------------------end
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
        AssigneeBean befEditBean = new AssigneeBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                //----delete by songjie 2011.03.11 不以最大的启用日期查询受托人设置数据----//
//                "(select FAssigneeCode,FCheckState,max(FStartDate) as FStartDate from " +
//                pub.yssGetTableName("Tb_Para_Assignee") + " " +
//                " where FStartDate <= " +
//                dbl.sqlDate(new java.util.Date()) +
//                "and FCheckState <> 2 group by FAssigneeCode,FCheckState) x join" +
                //----delete by songjie 2011.03.11 不以最大的启用日期查询受托人设置数据----//
                " (select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                //edit by songjie 2011.03.11 不以最大的启用日期查询受托人设置数据
                " (select * from " + pub.yssGetTableName("Tb_Para_Assignee") + " where FCheckState <> 2 ) a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where  FAssigneeCode =" + dbl.sqlString(this.oldAssigneeCode) +
                ") y " +//edit by songjie 2011.03.11 不以最大的启用日期查询受托人设置数据
                " order by y.FCheckState, y.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.assigneeCode = rs.getString("FAssigneeCode") + "";
                befEditBean.assigneeName = rs.getString("FAssigneeName") + "";
                befEditBean.startDate = rs.getDate("FStartDate");
                befEditBean.asisgneeShortName = rs.getString("FAssigneeShortName") +
                    "";
                befEditBean.officeAddr = rs.getString("FOfficeAddr") + "";
                befEditBean.PostalCode = rs.getString("FPostalCode") + "";
                befEditBean.Desc = rs.getString("FDesc") + "";

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
                        pub.yssGetTableName("Tb_Para_Assignee") +
                        " where FAssigneeCode = " +
                        dbl.sqlString(this.assigneeCode) +
                        " and FStartDate = " +
                        dbl.sqlDate(this.startDate);

                    //执行sql语句
                    dbl.executeSql(strSql);
                    strSql = "delete from " +
                        pub.yssGetTableName("tb_para_linkman") +
                        " where fRelaCode = " +
                        dbl.sqlString(this.assigneeCode) +
                        " and frelatype = 'Assignee'" +
                        " and FStartDate = " +
                        dbl.sqlDate(this.startDate);

                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而assigneeCode不为空，则按照assigneeCode来执行sql语句
            else if (assigneeCode != "" && assigneeCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_Assignee") +
                    " where FAssigneeCode = " +
                    dbl.sqlString(this.assigneeCode) +
                    " and FStartDate = " +
                    dbl.sqlDate(this.startDate);

                //执行sql语句
                dbl.executeSql(strSql);
                strSql = "delete from " +
                    pub.yssGetTableName("tb_para_linkman") +
                    " where fRelaCode = " +
                    dbl.sqlString(this.assigneeCode) +
                    " and frelatype = 'Assignee'" +
                    " and FStartDate = " +
                    dbl.sqlDate(this.startDate);

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
