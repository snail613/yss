package com.yss.main.basesetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class LanguageBean
    extends BaseDataSettingBean implements IDataSetting {
    private String languageCode = ""; //语言代码
    private String languageName = ""; //语言名称
    private String languageSymbol = ""; //语言符号
    private String desc = ""; //语言描述

    private String oldLanguageCode;
    private LanguageBean filterType;
    private String sRecycled = ""; //为增加还原和删除功能加的一个中介字符串变量 bug MS00149 QDV4南方2009年1月5日05_B 2009.01.14 方浩
    public LanguageBean() {
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
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //bug MS00149 QDV4南方2009年1月5日05_B 2009.01.13 方浩
            reqAry = sRowStr.split("\t");
            this.languageCode = reqAry[0];
            this.languageName = reqAry[1];
            this.languageSymbol = reqAry[2];
            this.desc = reqAry[3];
            this.checkStateId = Integer.parseInt(reqAry[4]);
            this.oldLanguageCode = reqAry[5];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new LanguageBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析地域设置请求出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.languageCode).append("\t");
        buf.append(this.languageName).append("\t");
        buf.append(this.languageSymbol).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.languageCode.length() != 0) {
                sResult = sResult + " and a.FLangCode like '" +
                    filterType.languageCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.languageName.length() != 0) {
                sResult = sResult + " and a.FLangName like '" +
                    filterType.languageName.replaceAll("'", "''") + "%'";
            }
            //by guyichuan 2011.07.27 BUG2208 语言符号，不支持模糊查询
            if (this.filterType.languageSymbol.length() != 0) {
                sResult = sResult + " and a.FLangSymbol like '%" +
                    filterType.languageSymbol+"%'";
            }

            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +//by guyichuan 2011.08.05 bug2208
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, "Tb_Base_Language", "FLangCode",
                               this.languageCode, this.oldLanguageCode);
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
     * 获取语言设置数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from Tb_Base_Language a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                buildFilterSql() +
                " order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.languageCode = rs.getString("FLangCode") + "";
                this.languageName = rs.getString("FLangName") + "";
                this.languageSymbol = rs.getString("FLangSymbol") + "";
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
                "\r\f" + this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取语言设置数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData2
     * 获取已审核的语言设置数据
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "语言代码\t语言名称\t语言符号";
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from Tb_Base_Language a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FLangCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FLangName") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FLangSymbol") + "").trim()).
                    append(
                        YssCons.YSS_LINESPLITMARK);

                this.languageCode = rs.getString("FLangCode") + "";
                this.languageName = rs.getString("FLangName") + "";
                this.languageSymbol = rs.getString("FLangSymbol") + "";
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
            throw new YssException("获取可用语言设置数据出错", e);
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
                 strSql = "insert into Tb_Base_Language" +
                       "(FLangCode,FCheckState,FLangName,FLangSymbol,FDesc,FCreator,FCreateTime,FCheckUser)" +
                       " values(" + dbl.sqlString(this.languageCode) + "," +
                       (pub.getSysCheckState()?"0":"1") + "," +
                       dbl.sqlString(this.languageName ) + "," +
                       dbl.sqlString(this.languageSymbol) + "," +
                       dbl.sqlString(this.desc) + "," +
                       dbl.sqlString(this.creatorCode) + "," +
                       dbl.sqlString(this.creatorTime) + "," +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
              }
              else if (btOper == YssCons.OP_EDIT) {
                 strSql = "update Tb_Base_Language set FLangCode = " +
                       dbl.sqlString(this.languageCode) +
                       ", FLangName = " + dbl.sqlString(this.languageName) +
                       ",FLangSymbol = " + dbl.sqlString(this.languageSymbol) +
                       ",FDesc = " + dbl.sqlString(this.desc) +
                       ",FCheckState = " + (pub.getSysCheckState()?"0":"1") +
                       ",FCreator = " + dbl.sqlString(this.creatorCode) +
                       ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                       ",FCheckUser = " + (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                       " where FLangCode = " +
                       dbl.sqlString(this.oldLanguageCode);
              }
              else if (btOper == YssCons.OP_DEL) {
     strSql = "update Tb_Base_Language set FCheckState = " + this.checkStateId +
     ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
     " where FLangCode = " + dbl.sqlString(this.languageCode);
              }
              else if (btOper == YssCons.OP_AUDIT) {
     strSql = "update Tb_Base_Language set FCheckState = " + this.checkStateId +
                       ",FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ",FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
     "' where FLangCode = " + dbl.sqlString(this.languageCode);
           }
           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("更新语言设置信息出错", e);
        }
        finally
        {
              dbl.endTransFinal(conn,bTrans);
        }

     }*/
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into Tb_Base_Language" +
                "(FLangCode,FCheckState,FLangName,FLangSymbol,FDesc,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.languageCode) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.languageName) + "," +
                dbl.sqlString(this.languageSymbol) + "," +
                dbl.sqlString(this.desc) + "," +
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
            throw new YssException("新增语言设置信息出错", e);
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
         strSql = "update Tb_Base_Language set FCheckState = " + this.checkStateId +
         ",FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
         ",FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
         "' where FLangCode = " + dbl.sqlString(this.languageCode);



             conn.setAutoCommit(false);
             bTrans = true;
             dbl.executeSql(strSql);
             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
          }
          catch (Exception e) {
             throw new YssException("审核语言设置信息出错", e);
          }
          finally {
             dbl.endTransFinal(conn, bTrans);
          }
         */
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环还原
//定义一个字符数组来循环删除
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //打开一个数据库联接
        try {
            conn.setAutoCommit(false); //开启一个事物
            bTrans = true; //代表是否关闭事务
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if ( sRecycled != null && (!sRecycled.equalsIgnoreCase("")) ) { //判断传来的内容是否为空//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                arrData = sRecycled.split("\r\n"); //解析它，把它还原成条目放在数组里。
                for (int i = 0; i < arrData.length; i++) { //循环数组，也就是循环还原条目
                    if (arrData[i].length() == 0) {
                        continue; //如果数组里没有内容就执行下一个内容
                    }
                    this.parseRowStr(arrData[i]); //解析这个数组里的内容
                    strSql = "update Tb_Base_Language set FCheckState = " +
                        this.checkStateId +
                        ",FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ",FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FLangCode = " +
                        dbl.sqlString(this.languageCode); //更新数据的SQL语句

                    dbl.executeSql(strSql); //执行更新操作
                }
            }
            //如果sRecycled为空，而languageCode不为空，则按照languageCode来执行sql语句
            else if ( languageCode != null && (!languageCode.equalsIgnoreCase("")) ) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                strSql = "update Tb_Base_Language set FCheckState = " +
                    this.checkStateId +
                    ",FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ",FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FLangCode = " +
                    dbl.sqlString(this.languageCode); //更新数据的SQL语句

                dbl.executeSql(strSql); //执行更新操作
            }
            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核语言设置信息出错", e);
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
            strSql = "update Tb_Base_Language set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FLangCode = " + dbl.sqlString(this.languageCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除语言设置信息出错", e);
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
            strSql = "update Tb_Base_Language set FLangCode = " +
                dbl.sqlString(this.languageCode) +
                ", FLangName = " + dbl.sqlString(this.languageName) +
                ",FLangSymbol = " + dbl.sqlString(this.languageSymbol) +
                ",FDesc = " + dbl.sqlString(this.desc) +
                ",FCheckState = " + (pub.getSysCheckState() ? "0" : "1") +
                ",FCreator = " + dbl.sqlString(this.creatorCode) +
                ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FLangCode = " +
                dbl.sqlString(this.oldLanguageCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新语言设置信息出错", e);
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
        LanguageBean befEditBean = new LanguageBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from Tb_Base_Language a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where a.FLangCode =" +
                dbl.sqlString(this.oldLanguageCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.languageCode = rs.getString("FLangCode") + "";
                befEditBean.languageName = rs.getString("FLangName") + "";
                befEditBean.languageSymbol = rs.getString("FLangSymbol") + "";
                befEditBean.desc = rs.getString("FDesc") + "";
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
                        pub.yssGetTableName("Tb_Base_Language") +
                        " where FLangCode = " +
                        dbl.sqlString(this.languageCode);

                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而languageCode不为空，则按照languageCode来执行sql语句
            else if (languageCode != "" && languageCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Base_Language") +
                    " where FLangCode = " +
                    dbl.sqlString(this.languageCode);

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
