package com.yss.main.basesetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title:SubAccountTypeBean </p>
 * <p>Description:帐户子类型设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class SubAccountTypeBean
    extends BaseDataSettingBean implements
    IDataSetting {
    private String subAccTypeCode = ""; //帐户子类型代码
    private String subAccTypeName = ""; //帐户子类型名称
    private String accTypeCode = ""; //帐户父类型代码
    private String accTypeName = ""; //帐户父类型名称
    private String desc = ""; //帐户子类型描述
    private String status = ""; //是否记入系统信息状态  lzp 11.30 add
    private String oldSubAccTypeCode = "";
    private SubAccountTypeBean filtertype;
    private String sRecycled = ""; //为增加还原和删除功能加的一个中介字符串变量 bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
    public SubAccountTypeBean() {
    }

    /**
     * parseRowStr
     * 解析帐户子类型数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
            reqAry = sRowStr.split("\t");
            this.subAccTypeCode = reqAry[0];
            this.subAccTypeName = reqAry[1];
            this.accTypeCode = reqAry[2];
            this.desc = reqAry[3];
            this.oldSubAccTypeCode = reqAry[4];
            this.checkStateId = Integer.parseInt(reqAry[5]);
            this.status = reqAry[6]; //lzp add 11.30
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filtertype == null) {
                    this.filtertype = new SubAccountTypeBean();
                    this.filtertype.setYssPub(pub);
                }
                this.filtertype.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析帐户子类型设置请求出错", e);
        }

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.subAccTypeCode.trim()).append("\t");
        buf.append(this.subAccTypeName.trim()).append("\t");
        buf.append(this.accTypeCode.trim()).append("\t");
        buf.append(this.accTypeName.trim()).append("\t");
        buf.append(this.desc.trim()).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, "Tb_Base_SubAccountType",
                               "FSubAccTypeCode",
                               this.subAccTypeCode, this.oldSubAccTypeCode);
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
        if (this.filtertype != null) {
            sResult = " where 1=1 ";
            if (this.filtertype.subAccTypeCode.length() != 0) {
                sResult = sResult + " and a.FSubAccTypeCode like '" +
                    this.filtertype.subAccTypeCode.replaceAll("'", "''") +
                    "%'";
            }
            if (this.filtertype.subAccTypeName.length() != 0) {
                sResult = sResult + " and a.FSubAccTypeName like '" +
                    this.filtertype.subAccTypeName.replaceAll("'", "''") +
                    "%'";
            }
            if (this.filtertype.accTypeCode.length() != 0) {
                sResult = sResult + " and a.FAccTypeCode like '" +
                    this.filtertype.accTypeCode.replaceAll("'", "''") +
                    "%'";
            }
            if (this.filtertype.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    this.filtertype.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取帐户子类型数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FAccTypeName from Tb_Base_SubAccountType a " +
                "left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                "left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                "left join (select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FCheckState = 1) d on a.FAccTypeCode = d.FAccTypeCode " +
                buildFilterSql() +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.subAccTypeCode = rs.getString("FSubAccTypeCode") + "";
                this.subAccTypeName = rs.getString("FSubAccTypeName") + "";
                this.accTypeCode = rs.getString("FAccTypeCode") + "";
                this.accTypeName = rs.getString("FAccTypeName") + "";
                this.desc = rs.getString("FDesc") + "";
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
                "\r\f" +
                this.getListView1ShowCols();

        } catch (Exception e) {
            throw new YssException("获取帐户子类型设置数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData2
     * 获取可用的帐户子类型数据
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
            sHeader = "帐户子类型代码\t帐户子类型名称";
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName, d.FAccTypeName from Tb_Base_SubAccountType a " +
                "left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                "left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                "left join (select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FCheckState = 1) d on a.FAccTypeCode = d.FAccTypeCode " +
                "where a.FCheckState = 1 ";
            if (this.filtertype != null) {
            	//--- MS01637 QDV4太平2010年08月23日01_AB 需要提供选择性项目，控制只显示定期存款账户。 add by jiangshichao -----
            	if(this.filtertype.accTypeCode.split(",").length>1){
            		strSql = strSql + " and a.FAccTypeCode in (" +
                    this.filtertype.accTypeCode + " )";
            	}
            	else if (this.filtertype.accTypeCode.length() != 0) {
                    strSql = strSql + " and a.FAccTypeCode='" +
                        this.filtertype.accTypeCode + "'";
                }
            	//--- MS01637 QDV4太平2010年08月23日01_AB 需要提供选择性项目，控制只显示定期存款账户。 end ---------------------
            }

            strSql = strSql + " order by a.FSubAccTypeCode, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FSubAccTypeCode") + "").trim()).
                    append(
                        "\t");
                bufShow.append( (rs.getString("FSubAccTypeName") + "").trim()).
                    append(
                        YssCons.YSS_LINESPLITMARK);

                this.subAccTypeCode = rs.getString("FSubAccTypeCode") + "";
                this.subAccTypeName = rs.getString("FSubAccTypeName") + "";
                this.accTypeCode = rs.getString("FAccTypeCode") + "";
                this.accTypeName = rs.getString("FAccTypeName") + "";
                this.desc = rs.getString("FDesc") + "";
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
            throw new YssException("获取帐户子类型设置数据出错", e);
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
              strSql = "insert into Tb_Base_SubAccountType" +
                    "(FSubAccTypeCode,FSubAccTypeName,FAccTypeCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                    " values(" + dbl.sqlString(this.subAccTypeCode) + "," +
                    dbl.sqlString(this.subAccTypeName) + "," +
                    dbl.sqlString(this.accTypeCode) + "," +
                    dbl.sqlString(this.desc) + "," +
                    (pub.getSysCheckState()?"0":"1") + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
           }
           else if (btOper == YssCons.OP_EDIT) {
              strSql = "update Tb_Base_SubAccountType set FSubAccTypeCode = " +
     dbl.sqlString(this.subAccTypeCode) + ", FSubAccTypeName = " +
                    dbl.sqlString(this.subAccTypeName) + ", FAccTypeCode = " +
                    dbl.sqlString(this.accTypeCode) + ", FDesc = " +
                    dbl.sqlString(this.desc) + ",FCheckState = " +
                    (pub.getSysCheckState()?"0":"1") + ", FCreator = " +
                    dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                    dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                    " where FSubAccTypeCode = " +
                    dbl.sqlString(this.oldSubAccTypeCode);
           }
           else if (btOper == YssCons.OP_DEL) {
              strSql = "update Tb_Base_SubAccountType set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
     "' where FSubAccTypeCode = " + dbl.sqlString(this.subAccTypeCode);
           }
           else if (btOper == YssCons.OP_AUDIT) {
              strSql = "update Tb_Base_SubAccountType set FCheckState = " +
                    this.checkStateId + ",FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ",FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FSubAccTypeCode = " +
                    dbl.sqlString(this.subAccTypeCode);
           }
           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("更新帐户子类型设置信息出错", e);
        }
        finally {
           dbl.endTransFinal(conn, bTrans);
        }

     }*/

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into Tb_Base_SubAccountType" +
                "(FSubAccTypeCode,FSubAccTypeName,FAccTypeCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.subAccTypeCode) + "," +
                dbl.sqlString(this.subAccTypeName) + "," +
                dbl.sqlString(this.accTypeCode) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.30
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("新增-帐户子类型设置");
                sysdata.setStrCode(this.subAccTypeCode);
                sysdata.setStrName(this.subAccTypeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增帐户子类型设置信息出错", e);
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
        /**  String strSql = "";
          boolean bTrans = false; //代表是否开始了事务
          Connection conn = dbl.loadConnection();
          try {
             strSql = "update Tb_Base_SubAccountType set FCheckState = " +
                   this.checkStateId + ",FCheckUser = " +
                   dbl.sqlString(pub.getUserCode()) + ",FCheckTime = '" +
                   YssFun.formatDatetime(new java.util.Date()) +
                   "' where FSubAccTypeCode = " +
                   dbl.sqlString(this.subAccTypeCode);

             conn.setAutoCommit(false);
             bTrans = true;
             dbl.executeSql(strSql);
             //---------lzp add 11.30
             if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                      funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                if (this.checkStateId == 1) {
                   sysdata.setStrFunName("审核-帐户子类型设置");
                }
                else {
                   sysdata.setStrFunName("反审核-帐户子类型设置");
                }

                sysdata.setStrCode(this.subAccTypeCode);
                sysdata.setStrName(this.subAccTypeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
             }
//-----------------------

             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
          }
          catch (Exception e) {
             throw new YssException("审核帐户子类型设置信息出错", e);
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
            if ( sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) { //判断传来的内容是否为空//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                arrData = sRecycled.split("\r\n"); //解析它，把它还原成条目放在数组里。
                for (int i = 0; i < arrData.length; i++) { //循环数组，也就是循环还原条目
                    if (arrData[i].length() == 0) {
                        continue; //如果数组里没有内容就执行下一个内容
                    }
                    this.parseRowStr(arrData[i]); //解析这个数组里的内容
                    strSql = "update Tb_Base_SubAccountType set FCheckState = " +
                        this.checkStateId + ",FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ",FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FSubAccTypeCode = " +
                        dbl.sqlString(this.subAccTypeCode); //更新数据的SQL语句

                    dbl.executeSql(strSql); //执行更新操作
                }
            }
            //如果sRecycled为空，而subAccTypeCode不为空，则按照subAccTypeCode来执行sql语句
            else if (subAccTypeCode != null && (!subAccTypeCode.equalsIgnoreCase("")) ) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                strSql = "update Tb_Base_SubAccountType set FCheckState = " +
                    this.checkStateId + ",FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ",FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FSubAccTypeCode = " +
                    dbl.sqlString(this.subAccTypeCode); //更新数据的SQL语句

                dbl.executeSql(strSql); //执行更新操作
            }
            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核帐户子类型设置信息出错", e);
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
            strSql = "update Tb_Base_SubAccountType set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "' where FSubAccTypeCode = " +
                dbl.sqlString(this.subAccTypeCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.30
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-帐户子类型设置");
                sysdata.setStrCode(this.subAccTypeCode);
                sysdata.setStrName(this.subAccTypeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除帐户子类型设置信息出错", e);
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
            strSql = "update Tb_Base_SubAccountType set FSubAccTypeCode = " +
                dbl.sqlString(this.subAccTypeCode) +
                ", FSubAccTypeName = " +
                dbl.sqlString(this.subAccTypeName) + ", FAccTypeCode = " +
                dbl.sqlString(this.accTypeCode) + ", FDesc = " +
                dbl.sqlString(this.desc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FSubAccTypeCode = " +
                dbl.sqlString(this.oldSubAccTypeCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.30
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("修改-帐户子类型设置");
                sysdata.setStrCode(this.subAccTypeCode);
                sysdata.setStrName(this.subAccTypeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新帐户子类型设置信息出错", e);
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
        SubAccountTypeBean befEditBean = new SubAccountTypeBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FAccTypeName from Tb_Base_SubAccountType a " +
                "left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                "left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                "left join (select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FCheckState = 1) d on a.FAccTypeCode = d.FAccTypeCode " +
                " where a.FSubAccTypeCode =" +
                dbl.sqlString(this.oldSubAccTypeCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.subAccTypeCode = rs.getString("FSubAccTypeCode") +
                    "";
                befEditBean.subAccTypeName = rs.getString("FSubAccTypeName") +
                    "";
                befEditBean.accTypeCode = rs.getString("FAccTypeCode") + "";
                befEditBean.accTypeName = rs.getString("FAccTypeName") + "";
                befEditBean.desc = rs.getString("FDesc") + "";
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }

    }

    public String getStatus() {
        return status;
    }

    /**
     * deleteRecycleData

        public void deleteRecycleData() {
        }
     */
    /**
     * bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
     * 修改人：方浩
     * 回收站的删除功能调用此方法deleteRecycleData()
     * 从数据库删除数据，即彻底删除数据,可以多个一删除
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环还原
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
                        pub.yssGetTableName("Tb_Base_SubAccountType") +
                        " where FSubAccTypeCode = " +
                        dbl.sqlString(this.subAccTypeCode);

                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而subAccTypeCode不为空，则按照subAccTypeCode来执行sql语句
            else if (subAccTypeCode != "" && subAccTypeCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Base_SubAccountType") +
                    " where FSubAccTypeCode = " +
                    dbl.sqlString(this.subAccTypeCode);

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
