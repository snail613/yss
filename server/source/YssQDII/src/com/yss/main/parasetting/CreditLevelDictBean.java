package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * 信用评级字典设置
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class CreditLevelDictBean
    extends BaseDataSettingBean implements IDataSetting {
    private String sRecycled = "";
    public String getStrDesc() {
        return strDesc;
    }

    public String getStrOldCreditLevelCode() {
        return strOldCreditLevelCode;
    }

    public String getStrCreditLevelCode() {
        return strCreditLevelCode;
    }

    public int getILevel() {
        return iLevel;
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.FilterType != null) {
            sResult += " where 1=1";

            if (this.FilterType.strCreditLevelCode.length() != 0) {
                sResult += " and a.FCreditLevelCode like '"
                    + this.FilterType.strCreditLevelCode.replaceAll("'", "''") +
                    "%'";
            }
            if (this.FilterType.strCreditLevelName.length() != 0) {
                sResult += " and a.FCreditLevelName like '" +
                    this.FilterType.strCreditLevelName.replaceAll("'", "''") +
                    "%'";
            }
            if (this.FilterType.strDesc.length() != 0) {
                sResult += " and a.FDesc like '" +
                    this.FilterType.strDesc.replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.iLevel != 100) {//xuqiji 20100326 MS01047 QDV4赢时胜(测试)2010年03月22日01_B 
                sResult += " and a.FLevel =" + this.FilterType.iLevel;
            }
        }
        return sResult;
    }

    public CreditLevelDictBean getFilterType() {
        return FilterType;
    }

    public void setStrCreditLevelName(String strCreditLevelName) {
        this.strCreditLevelName = strCreditLevelName;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public void setStrOldCreditLevelCode(String strOldCreditLevelCode) {
        this.strOldCreditLevelCode = strOldCreditLevelCode;
    }

    public void setStrCreditLevelCode(String strCreditLevelCode) {
        this.strCreditLevelCode = strCreditLevelCode;
    }

    public void setILevel(int iLevel) {
        this.iLevel = iLevel;
    }

    public void setFilterType(CreditLevelDictBean FilterType) {
        this.FilterType = FilterType;
    }

    public String getStrCreditLevelName() {
        return strCreditLevelName;
    }

    ///数据信用评级字典设置
    public CreditLevelDictBean() {
    }

    private String strCreditLevelCode = ""; //评级代码
    private String strCreditLevelName = ""; //评级名称
    private int iLevel = 0; //等级
    private String strDesc; //描述
    private String strOldCreditLevelCode = "";
    private CreditLevelDictBean FilterType = null;

    /**
     * 此方法已被修改
     * 修改时间：2008年2月26号
     * 修改人：单亮
     * 原方法的功能：查询出信用评级字典设置数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select y.* from " +
            "(select FCreditLevelCode,FCheckState from " +
            pub.yssGetTableName("Tb_Para_CreditLevelDict") + " " +
            //修改前的代码
            //" where FCheckState <> 2 order by FCreditLevelCode) x join" +
            //修改后的代码
            //----------------------------begin
            " order by FCreditLevelCode) x join" +
            //----------------------------end
            " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Para_CreditLevelDict") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            ") y on x.FCreditLevelCode = y.FCreditLevelCode " +
            " order by y.FCheckState, y.FCreateTime desc ";
        return this.builderListViewData(strSql);
    }

    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "评级代码\t评级名称\t评级等级";

            strSql =
                "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName " +
                "from " +
                pub.yssGetTableName("Tb_Para_CreditLevelDict") +
                " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " where a.FCheckState =1 order by a.FCreditLevelCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FCreditLevelCode") + " ".trim()).append("\t");
                bufShow.append(rs.getString("FCreditLevelName") + " ".trim()).append("\t");
                bufShow.append(rs.getString("FLevel") + " ".trim()).append("\t");
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                this.setSecurityAttr(rs);
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
            rs.close();
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取数据时出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " +
                pub.yssGetTableName("Tb_Para_CreditLevelDict") +
                " (FCreditLevelCode,FCreditLevelName,FLevel," +
                "FDesc,FCheckState,FCreator,FCreateTime) values(" +
                dbl.sqlString(this.strCreditLevelCode) + " ," +
                dbl.sqlString(this.strCreditLevelName) + " ," +
                this.iLevel + " ," + // lzp modify  20080123  此为数字型
                dbl.sqlString(this.strDesc) + " ," +
                (pub.getSysCheckState() ? "0" : "1") + " ," +
                dbl.sqlString(this.creatorCode + " ") + " ," +
                dbl.sqlString(this.creatorTime + " ") + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增信用评级字典信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";

    }

    public void setSecurityAttr(ResultSet rs) throws SQLException {
        this.strCreditLevelCode = rs.getString("FCreditLevelCode");
        this.strCreditLevelName = rs.getString("FCreditLevelName");
        this.iLevel = Integer.parseInt(rs.getString("FLevel"));
        this.strDesc = rs.getString("FDesc");
        super.setRecLog(rs);
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

                setSecurityAttr(rs);
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
            rs.close();
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取信用评级设置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strCreditLevelCode).append("\t");
        buf.append(this.strCreditLevelName).append("\t");
        buf.append(this.iLevel).append("\t");
        buf.append(this.strDesc).append("\t");
        buf.append(this.strOldCreditLevelCode).append("\t");
        buf.append(this.checkStateId).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();

    }

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
            this.strCreditLevelCode = reqAry[0];
            this.strCreditLevelName = reqAry[1];
            this.iLevel = Integer.parseInt(reqAry[2]);
            this.strDesc = reqAry[3];
            this.checkStateId = Integer.parseInt(reqAry[4]);
            this.strOldCreditLevelCode = reqAry[5];
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new CreditLevelDictBean();
                    this.FilterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }

        } catch (Exception e) {
            throw new YssException("解析信用评级字典信息出错", e);
        }

    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_CreditLevelDict"),
                               "FCreditLevelCode",
                               this.strCreditLevelCode,
                               this.strOldCreditLevelCode);
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_CreditLevelDict") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode() + " ") +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FCreditLevelCode = " +
                dbl.sqlString(this.strOldCreditLevelCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除信用评级字典信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_CreditLevelDict") +
                " set " +
                " FCreditLevelCode = " + dbl.sqlString(this.strCreditLevelCode) +
                " ," +
                " FCreditLevelName = " + dbl.sqlString(this.strCreditLevelName) +
                " ," +
                " FLevel = " + this.iLevel + " ," + // lzp modify  20080123  此为数字型
                " FDesc = " + dbl.sqlString(this.strDesc) + " ," +
                " FCheckState = " + (pub.getSysCheckState() ? "0" : "1") + " ," +
                " FCreator = '" +
                (pub.getSysCheckState() ? " " :
                 dbl.sqlString(this.creatorCode + " ")) + "' ," +
                " FCreateTime = " + dbl.sqlString(this.creatorTime + " ") + " ," +
                " FCheckUser = '" +
                (pub.getSysCheckState() ? " " :
                 dbl.sqlString(this.checkUserCode + " ")) + "' ," +
                " FCheckTime = " + dbl.sqlString(this.checkTime + " ") +
                " where FCreditLevelCode =" +
                dbl.sqlString(this.strOldCreditLevelCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改信用评级信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";

    }

    public String getAllSetting() {
        return "";
    }

    public String getTreeViewData1() {
        return "";
    }

    public String getTreeViewData2() {
        return "";
    }

    public String getTreeViewData3() {
        return "";
    }

    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    /**
     * 修改时间：2008年3月25号
     * 修改人：单亮
     * 原方法功能：只能处理信用评级字典设置的审核和未审核的单条信息。
     * 新方法功能：可以处理信用评级字典设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理信用评级字典设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */

    public void checkSetting() throws YssException {
        //修改前的代码
//      String strSql = "";
//      Connection conn = dbl.loadConnection();
//      boolean bTrans = false;
//      try {
//         strSql = "update " + pub.yssGetTableName("Tb_Para_CreditLevelDict") +
//               " set FCheckstate =" + this.checkStateId + "," +
//               " FCreator='" + pub.getUserCode() + " '," +
//               " FCreateTime='" + YssFun.formatDatetime(new java.util.Date()) +
//               " ' " +
//               " where FCreditLevelCode =" +
//               dbl.sqlString(this.strOldCreditLevelCode);
//         conn.setAutoCommit(false);
//         bTrans = true;
//         dbl.executeSql(strSql);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//      }
//      catch (Exception e) {
//         throw new YssException("审核信用评级字典信息出错", e);
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
                        pub.yssGetTableName("Tb_Para_CreditLevelDict") +
                        " set FCheckstate =" + this.checkStateId + "," +
                        " FCreator='" + pub.getUserCode() + " '," +
                        " FCreateTime='" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        " ' " +
                        " where FCreditLevelCode =" +
                        dbl.sqlString(this.strOldCreditLevelCode);
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而strOldCreditLevelCode不为空，则按照strOldCreditLevelCode来执行sql语句
            //edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (strOldCreditLevelCode != null && !strOldCreditLevelCode.equalsIgnoreCase("")) {
                strSql = "update " +
                    pub.yssGetTableName("Tb_Para_CreditLevelDict") +
                    " set FCheckstate =" + this.checkStateId + "," +
                    " FCreator='" + pub.getUserCode() + " '," +
                    " FCreateTime='" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    " ' " +
                    " where FCreditLevelCode =" +
                    dbl.sqlString(this.strOldCreditLevelCode);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核信用评级字典信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
//---------------------end
    }

    public IDataSetting getSetting() {
        return null;
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
                        pub.yssGetTableName("Tb_Para_CreditLevelDict") +
                        " where FCreditLevelCode = " +
                        dbl.sqlString(this.strOldCreditLevelCode);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而strOldCreditLevelCode不为空，则按照strOldCreditLevelCode来执行sql语句
            else if (strOldCreditLevelCode != "" && strOldCreditLevelCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_CreditLevelDict") +
                    " where FCreditLevelCode = " +
                    dbl.sqlString(this.strOldCreditLevelCode);
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
