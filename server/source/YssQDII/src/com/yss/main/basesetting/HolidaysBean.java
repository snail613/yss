package com.yss.main.basesetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class HolidaysBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strHolidaysCode; //节日群代码
    private String strHolidaysName; //节日群名称
    private String strDesc; //描述
    private String strOldHolidaysCode;
    private String hRecycled = ""; //回收站
    private HolidaysBean filterType;
    private String sRecycled = ""; //为增加还原和删除功能加的一个中介字符串变量 bug MS00149 QDV4南方2009年1月5日05_B 2009.01.14 方浩
    public HolidaysBean() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strHolidaysCode).append("\t");
        buf.append(this.strHolidaysName).append("\t");
        buf.append(this.strDesc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, "Tb_Base_Holidays", "FHolidaysCode",
                               this.strHolidaysCode, this.strOldHolidaysCode);
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
            if (this.filterType.strHolidaysCode.length() != 0) {
                sResult = sResult + " and a.FHolidaysCode like '" +
                    filterType.strHolidaysCode.replaceAll("'", "''") +
                    "%'";
            }
            if (this.filterType.strHolidaysName.length() != 0) {
                sResult = sResult + " and a.FHolidaysName like '" +
                    filterType.strHolidaysName.replaceAll("'", "''") +
                    "%'";
            }
            if (this.filterType.strDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.strDesc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取所有节假日群设置信息
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
            strSql = "select a.*, d.FUserName as FCreatorName,e.FUserName as FCheckUserName from Tb_Base_Holidays a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                buildFilterSql() +
                " order by a.FCheckState, a.FCreateTime desc"; // wdy modify 20070830

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.strHolidaysCode = rs.getString("FHolidaysCode") + "";
                this.strHolidaysName = rs.getString("FHolidaysName") + "";
                this.strDesc = rs.getString("FDesc") + "";//by guyichuan 2011.07.28 BUG2210
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
            throw new YssException("获取节假日群信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData2
     * 获取已审核的节假日群设置信息
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
            sHeader = "节假日群代码\t节假日群名称\t节假日群描述";
            strSql = "select a.*, d.FUserName as FCreatorName,e.FUserName as FCheckUserName from Tb_Base_Holidays a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " where a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FHolidaysCode") + "").trim()).
                    append(
                        "\t");
                bufShow.append( (rs.getString("FHolidaysName") + "").trim()).
                    append(
                        "\t");
                bufShow.append(YssFun.left( (rs.getString("FDesc") + "").trim(),
                                           40)).
                    append(YssCons.YSS_LINESPLITMARK);

                this.strHolidaysCode = rs.getString("FHolidaysCode") + "";
                this.strHolidaysName = rs.getString("FHolidaysName") + "";
                this.strDesc = YssFun.left(rs.getString("FDesc") + "", 40);

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
            throw new YssException("获取可用节假日群信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData3
     *获取所有已审核的节假日群代码  edit by yanghaiming 20100223 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
     * @return String
     */
    public String getListViewData3() throws YssException{
    	String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        try {
        	strSql = "select FHolidaysCode from Tb_Base_Holidays where FCheckState = 1";
        	rs = dbl.openResultSet(strSql);
        	while(rs.next()){
        		bufShow.append((rs.getString("FHolidaysCode")+"").trim()).append("\t");
        	}
        	if (bufShow.toString().length() > 2) {
        		sAllDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 1);
            }
        	return sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取可用节假日群信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
     * 解析节假日群设置
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
            this.hRecycled = sRowStr;
            this.strHolidaysCode = reqAry[0];
            this.strHolidaysName = reqAry[1];
            this.strDesc = reqAry[2];
            this.checkStateId = Integer.parseInt(reqAry[3]);
            this.strOldHolidaysCode = reqAry[4];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new HolidaysBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析节假日群设置请求出错", e);
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
     * 新增、修改、删除、审核
     * @param btOper byte
     */
    /*  public void saveSetting(byte btOper) throws YssException {
      String strSql = "";
      boolean bTrans = false; //代表是否开始了事务
      Connection conn = dbl.loadConnection();
      try {
         if (btOper == YssCons.OP_ADD) {
            strSql = "insert into Tb_Base_Holidays" +
                  "(FHolidaysCode,FHolidaysName,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                  " values(" + dbl.sqlString(this.strHolidaysCode) + "," +
                  dbl.sqlString(this.strHolidaysName) + "," +
                  dbl.sqlString(this.strDesc) + "," +
                  (pub.getSysCheckState()?"0":"1") + "," +
                  dbl.sqlString(this.creatorCode) + "," +
                  dbl.sqlString(this.creatorTime) + "," +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
         }
         else if (btOper == YssCons.OP_EDIT) {
            strSql = "update Tb_Base_Holidays set FHolidaysCode = " +
                  dbl.sqlString(this.strHolidaysCode) + ", FHolidaysName = " +
                  dbl.sqlString(this.strHolidaysName) + ", FDesc = " +
                  dbl.sqlString(this.strDesc) + ",FCheckState = " +
                  (pub.getSysCheckState()?"0":"1") + ", FCreator = " +
                  dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                  dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                  " where FHolidaysCode = " +
                  dbl.sqlString(this.strOldHolidaysCode);
         }
         else if (btOper == YssCons.OP_DEL) {
            strSql = "update Tb_Base_Holidays set FCheckState = 2" +
     " where FHolidaysCode = " + dbl.sqlString(this.strHolidaysCode);

         }
         else if (btOper == YssCons.OP_AUDIT) {
            strSql = "update Tb_Base_Holidays set FCheckState = " +
                  this.checkStateId + ", FCheckUser = " +
                  dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                  YssFun.formatDatetime(new java.util.Date()) + "'" +
     " where FHolidaysCode = " + dbl.sqlString(this.strHolidaysCode);
         }
         conn.setAutoCommit(false);
         bTrans = true;
         dbl.executeSql(strSql);

         if (btOper == YssCons.OP_EDIT && !this.strHolidaysCode.equalsIgnoreCase(this.strOldHolidaysCode)) {
            strSql = "update Tb_Base_ChildHoliday set FHolidaysCode = " +
                  dbl.sqlString(this.strHolidaysCode) + ", FCreator = " +
                  dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                  dbl.sqlString(this.creatorTime) + " where FHolidaysCode = " +
                  dbl.sqlString(this.strOldHolidaysCode);
            dbl.executeSql(strSql);
         }
         else if (btOper == YssCons.OP_DEL) {
            strSql = "update Tb_Base_ChildHoliday set FCheckState = 2" +
     " where FHolidaysCode = " + dbl.sqlString(this.strHolidaysCode);
            dbl.executeSql(strSql);
         }
         else if (btOper == YssCons.OP_AUDIT) {
            strSql = "update Tb_Base_ChildHoliday set FCheckState = " +
                  this.checkStateId + ", FCheckUser = " +
                  dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                  YssFun.formatDatetime(new java.util.Date()) + "'" +
     " where FHolidaysCode = " + dbl.sqlString(this.strHolidaysCode);
            dbl.executeSql(strSql);
         }
         conn.commit();
         bTrans = false;
         conn.setAutoCommit(true);
      }
      catch (Exception e) {
         throw new YssException("更新节假日群设置信息出错", e);
      }finally{
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
        ResultSet rs = null;
        try {
            strSql = "insert into Tb_Base_Holidays" +
                "(FHolidaysCode,FHolidaysName,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.strHolidaysCode) + "," +
                dbl.sqlString(this.strHolidaysName) + "," +
                dbl.sqlString(this.strDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //复制操作时，复制子表数据 edit by jc
            strSql =
                "select * from Tb_Base_ChildHoliday where FHolidaysCode = " +
                dbl.sqlString(this.strOldHolidaysCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                String sqlStr = "";
                sqlStr = "insert into Tb_Base_ChildHoliday" +
                    " (FHolidaysCode,FDate,FType,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                    " values(" + dbl.sqlString(this.strHolidaysCode) + "," +
                    dbl.sqlDate(rs.getDate("FDate")) + "," +
                    rs.getInt("FType") + "," +
                    dbl.sqlString( (rs.getString("FDesc") == null ? "' '" :
                                    rs.getString("FDesc"))) + "," +
                    (pub.getSysCheckState() ? "0" : "1") + "," +
                    dbl.sqlString(rs.getString("FCreator")) + "," +
                    dbl.sqlString(rs.getString("FCreateTime")) + "," +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) + ")";
                dbl.executeSql(sqlStr);
            }
            //---------------------------jc

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增节假日群设置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
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
        /*      String strSql = "";
              String strSqlChild = "";
              boolean bTrans = false; //代表是否开始了事务
              Connection conn = dbl.loadConnection();
              try {
                 strSql =
                       "update Tb_Base_Holidays set FCheckState = " +
                       this.checkStateId + ", FCheckUser = " +
                       dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                       YssFun.formatDatetime(new java.util.Date()) + "'" +
         " where FHolidaysCode = " + dbl.sqlString(this.strHolidaysCode);

                 //2008.01.15 添加 蒋锦
                 //审核和反审核节假日群的时候同时审核字表中的数据
                 strSqlChild =
                       "UPDATE Tb_Base_ChildHoliday SET FCheckState = " +
                       this.checkStateId + ", FCheckUser = " +
                       dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                       YssFun.formatDatetime(new java.util.Date()) + "'" +
         " where FHolidaysCode = " + dbl.sqlString(this.strHolidaysCode);

                 conn.setAutoCommit(false);
                 bTrans = true;
                 dbl.executeSql(strSql);
                 dbl.executeSql(strSqlChild);
                 conn.commit();
                 bTrans = false;
                 conn.setAutoCommit(true);
              }
              catch (Exception e) {
                 throw new YssException("审核节假日群设置信息出错", e);
              }
              finally {
                 dbl.endTransFinal(conn, bTrans);
              }
         */
        String strSql = ""; //定义一个字符串来放SQL语句
        String strSqlChild = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环还原
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //打开一个数据库联接
        try {
            conn.setAutoCommit(false); //开启一个事物
            bTrans = true; //代表是否关闭事务
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if ( sRecycled != null  && (!sRecycled.equalsIgnoreCase("")) ) { //判断传来的内容是否为空//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                arrData = sRecycled.split("\r\n"); //解析它，把它还原成条目放在数组里。
                for (int i = 0; i < arrData.length; i++) { //循环数组，也就是循环还原条目
                    if (arrData[i].length() == 0) {
                        continue; //如果数组里没有内容就执行下一个内容
                    }
                    this.parseRowStr(arrData[i]); //解析这个数组里的内容
                    strSql =
                        "update Tb_Base_Holidays set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FHolidaysCode = " +
                        dbl.sqlString(this.strHolidaysCode); //更新数据的SQL语句
                    dbl.executeSql(strSql); //执行更新操作
                    //2008.01.15 添加 蒋锦
                    //审核和反审核节假日群的时候同时审核字表中的数据
                    //edit by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A
                    if("revert".equals(pub.getDealStatus())){
                    	strSqlChild =
                    		"UPDATE Tb_Base_ChildHoliday SET FCheckState = " +
                    		this.checkStateId + ", FCheckUser = " +
                    		dbl.sqlString(pub.getUserCode()) +
                    		", FCheckTime = '" +
                    		YssFun.formatDatetime(new java.util.Date()) + "'" +
                    		" where FHolidaysCode = " +
                    		dbl.sqlString(this.strHolidaysCode);
                    	dbl.executeSql(strSqlChild);
                    }//edit by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A
                }
            }
            //如果sRecycled为空，而strHolidaysCode不为空，则按照strHolidaysCode来执行sql语句
            else if ( strHolidaysCode != null && (!strHolidaysCode.equalsIgnoreCase("")) ) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                strSql =
                    "update Tb_Base_Holidays set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FHolidaysCode = " +
                    dbl.sqlString(this.strHolidaysCode); //更新数据的SQL语句
                dbl.executeSql(strSql); //执行更新操作
                //2008.01.15 添加 蒋锦
                //审核和反审核节假日群的时候同时审核字表中的数据
                //edit by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A
                if("revert".equals(pub.getDealStatus())){
                	strSqlChild =
                		"UPDATE Tb_Base_ChildHoliday SET FCheckState = " +
                		this.checkStateId + ", FCheckUser = " +
                		dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                		YssFun.formatDatetime(new java.util.Date()) + "'" +
                		" where FHolidaysCode = " +
                		dbl.sqlString(this.strHolidaysCode);
                	dbl.executeSql(strSqlChild);
                }//edit by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A
            }
            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核节假日群设置信息出错", e);
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
            strSql = "update Tb_Base_Holidays set FCheckState = 2" +
                " where FHolidaysCode = " +
                dbl.sqlString(this.strHolidaysCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            //删除节假日子表中相对应的数据 edit by jc
            strSql = "update Tb_Base_ChildHoliday set FCheckState = 2" +
                " where FHolidaysCode = " +
                dbl.sqlString(this.strHolidaysCode);
            dbl.executeSql(strSql);
            //--------------------------------jc
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除节假日群设置信息出错", e);
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
            strSql = "update Tb_Base_Holidays set FHolidaysCode = " +
                dbl.sqlString(this.strHolidaysCode) + ", FHolidaysName = " +
                dbl.sqlString(this.strHolidaysName) + ", FDesc = " +
                dbl.sqlString(this.strDesc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FHolidaysCode = " +
                dbl.sqlString(this.strOldHolidaysCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            //添加修改节假日子表的数据 edit by jc
            strSql = "update Tb_Base_ChildHoliday set FHolidaysCode = " +
                dbl.sqlString(this.strHolidaysCode) +
                " where FHolidaysCode = " +
                dbl.sqlString(this.strOldHolidaysCode);
            dbl.executeSql(strSql);
            //----------------------------jc

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新节假日群设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        try {
            //QDV4南方2009年1月20日01_A  MS00210 增加查询节假日代码信息的功能 by leeyu 20090425
            if (sType != null && sType.equalsIgnoreCase("getHolMes")) {
                sqlStr = "select * from Tb_Base_Holidays where FCheckState = 1 and FHolidaysCode =" + dbl.sqlString(this.strHolidaysCode);
                rs = dbl.openResultSet(sqlStr);
                if (rs.next()) {
                    this.strHolidaysCode = rs.getString("FHolidaysCode") + "";
                    this.strHolidaysName = rs.getString("FHolidaysName") + "";
                    this.strDesc = rs.getString("FDesc") + "";
                }
                return this.buildRowStr();
            }
            return "";
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        //return "";
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        HolidaysBean befEditBean = new HolidaysBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*, d.FUserName as FCreatorName,e.FUserName as FCheckUserName from Tb_Base_Holidays a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " where  a.FHolidaysCode =" +
                dbl.sqlString(this.strOldHolidaysCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.strHolidaysCode = rs.getString("FHolidaysCode") +
                    "";
                befEditBean.strHolidaysName = rs.getString("FHolidaysName") +
                    "";
                befEditBean.strDesc = YssFun.left(rs.getString("FDesc") + "",
                                                  40);
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
     * Time: 2008-08-11
     * Creator: Mao Qiwen
     */
    public void deleteRecycleData() throws YssException {
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环删除
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //判断回收站是否为空，如果不为空则根据解析的字符串执行SQL语句删除数据
            if (hRecycled != null && hRecycled.length() != 0) {
                //按照规定的解析规则对数据进行解析
                arrData = hRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Base_Holidays") +
                        " where FHolidaysCode= " +
                        dbl.sqlString(this.strHolidaysCode);
                    dbl.executeSql(strSql);
                    
                    //---edit by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A start---//
                    strSql = "delete from " + pub.yssGetTableName("Tb_Base_ChildHoliday") +
                          " where FHolidaysCode = " + dbl.sqlString(this.strHolidaysCode);
                    dbl.executeSql(strSql);
                    //---edit by songjie 2012.02.07 STORY #2089 QDV4中银基金2011年12月29日01_A end---//
                }
                conn.commit(); //提交事物
                bTrans = false;
                conn.setAutoCommit(false);
            }
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
