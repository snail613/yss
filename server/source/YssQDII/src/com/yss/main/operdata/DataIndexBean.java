package com.yss.main.operdata;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title: MarketValueBean </p>
 * <p>Description: 指数行情 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */

public class DataIndexBean
    extends BaseDataSettingBean implements IDataSetting {
    private String sRecycled = ""; //保存未解析前的字符串
    public double getDblMarkupValue() {
        return dblMarkupValue;
    }

    public String getStrIndexName() {
        return strIndexName;
    }

    public double getDblTopValue() {
        return dblTopValue;
    }

    public String getStrIndexCode() {
        return strIndexCode;
    }

    public double getDblNewValue() {
        return dblNewValue;
    }

    /*  public String getstrDataSource() {
         return strDataSource;
      }*/

    public String getStrIndexSrcCode() {
        return strIndexSrcCode;
    }

//    public String getisOnlyColumns() {
//        return isOnlyColumns;
//    }

    public String getStrIndexDate() {
        return strIndexDate;
    }

    public double getDblMarkupPercent() {
        return dblMarkupPercent;
    }

    public String getStrIndexTime() {
        return strIndexTime;
    }

    public String getStrIndexSrcName() {
        return strIndexSrcName;
    }

    public String getStrExchangeName() {
        return strExchangeName;
    }

    public double getDblOpenValue() {
        return dblOpenValue;
    }

    public double getDblLowValue() {
        return dblLowValue;
    }

    public double getDblAvgValue() {
        return dblAvgValue;
    }

    public double getDblClosedValue() {
        return dblClosedValue;
    }

    public void setStrExchangeCode(String strExchangeCode) {
        this.strExchangeCode = strExchangeCode;
    }

    public void setDblMarkupValue(double dblMarkupValue) {
        this.dblMarkupValue = dblMarkupValue;
    }

    public void setStrIndexName(String strIndexName) {
        this.strIndexName = strIndexName;
    }

    public void setDblTopValue(double dblTopValue) {
        this.dblTopValue = dblTopValue;
    }

    public void setStrIndexCode(String strIndexCode) {
        this.strIndexCode = strIndexCode;
    }

    public void setDblNewValue(double dblNewValue) {
        this.dblNewValue = dblNewValue;
    }

    /*public void setstrDataSource(String strDataSource) {
       this.strDataSource = strDataSource;
        }*/

    public void setStrIndexSrcCode(String strIndexSrcCode) {
        this.strIndexSrcCode = strIndexSrcCode;
    }

//    public void setisOnlyColumns(String isOnlyColumns) {
//        this.isOnlyColumns = isOnlyColumns;
//    }

    public void setStrIndexDate(String strIndexDate) {
        this.strIndexDate = strIndexDate;
    }

    public void setDblMarkupPercent(double dblMarkupPercent) {
        this.dblMarkupPercent = dblMarkupPercent;
    }

    public void setStrIndexTime(String strIndexTime) {
        this.strIndexTime = strIndexTime;
    }

    public void setStrIndexSrcName(String strIndexSrcName) {
        this.strIndexSrcName = strIndexSrcName;
    }

    public void setStrExchangeName(String strExchangeName) {
        this.strExchangeName = strExchangeName;
    }

    public void setDblOpenValue(double dblOpenValue) {
        this.dblOpenValue = dblOpenValue;
    }

    public void setDblLowValue(double dblLowValue) {
        this.dblLowValue = dblLowValue;
    }

    public void setDblAvgValue(double dblAvgValue) {
        this.dblAvgValue = dblAvgValue;
    }

    public void setDblClosedValue(double dblClosedValue) {
        this.dblClosedValue = dblClosedValue;
    }

    public void setStrOldIndexDate(String strOldIndexDate) {
        this.strOldIndexDate = strOldIndexDate;
    }

    public void setStrOldIndexTime(String strOldIndexTime) {
        this.strOldIndexTime = strOldIndexTime;
    }

//    public void setisOnlyColumns(String isOnlyColumns) {
//        this.isOnlyColumns = isOnlyColumns;
//    }

    public void setStrOldIndexSrcCode(String strOldIndexSrcCode) {
        this.strOldIndexSrcCode = strOldIndexSrcCode;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public String getStrExchangeCode() {
        return strExchangeCode;
    }

    public String getStrOldIndexDate() {
        return strOldIndexDate;
    }

    public String getStrOldIndexTime() {
        return strOldIndexTime;
    }

//    public String getisOnlyColumns() {
//        return isOnlyColumns;
//    }

    public String getStrOldIndexSrcCode() {
        return strOldIndexSrcCode;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public DataIndexBean() {
    }

    private String strIndexCode = ""; //指数代码
    private String strIndexName = ""; //指数名称
    private String strIndexSrcCode = ""; //指数来源代码
    private String strIndexSrcName = ""; //指数来源名称
    private String strExchangeCode = ""; //交易所代码
    private String strExchangeName = ""; //交易所名称
    private String strIndexDate = "1900-01-01"; //指数日期
    private String strIndexTime = "00:00:00"; //指数时间
    private double dblTopValue; //最高指数
    private double dblLowValue; //最低指数
    private double dblOpenValue; //开盘指数
    private double dblClosedValue; //收盘指数
    private double dblNewValue; //最新指数
    private double dblAvgValue; //平均指数
    private double dblMarkupValue; //涨跌点数
    private double dblMarkupPercent; //涨跌百分比
    private String strDesc = "";
    // private String strDataSource="";//数据来源
    private String strOldIndexCode = "";
    private String strOldIndexSrcCode = "";
    private String strOldIndexDate = "";
    private String strOldIndexTime = "";
//    private SisOnlyColumnsolumns = "0"; //在初始登陆时是否只显示列，不查询数据
    private DataIndexBean filterType;

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
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sDateStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            //修改前的代码
            //strSql ="select y.* from " +
            //修改后的代码
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if (this.filterType.isOnlyColumns.equals("1")&&!(pub.isBrown())) {
            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\f"+ yssPageInationBean.buildRowStr()+"\r\f";//QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            //----------------------------begin
            strSql = "select distinct y.* from " +
                //----------------------------end
                "(select FIndexCode from " + pub.yssGetTableName("Tb_Data_Index") + " " +
                //修改前的代码
                //" where FCheckState <> 2 group by FIndexCode) x join"  +
                //修改后的代码
                //----------------------------begin
                "  group by FIndexCode) x join" +
                //----------------------------end
                "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
                " d.FIndexName as FIndexName,d.FExchangeCode as FExchangeCode,d.FExchangeName as FExchangeName,e.FIndexSrcName as FIndexSrcName,o.FVocName as FDataSourceValue" +
                " from " + pub.yssGetTableName("Tb_Data_Index") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select da.*,dc.FExchangeName  from (select FIndexCode,FIndexName,FExchangeCode from "
                + pub.yssGetTableName("Tb_Para_Index") + ") da " +
                "left join (select FExchangecode,FExchangeName from tb_base_exchange)  dc on da.FExchangeCode =dc.FExchangecode) d on a.FIndexCode=d.FIndexCode" +
                " left join (select FIndexSrcCode,FIndexSrcName from " + pub.yssGetTableName("Tb_Para_IndexSource") + ") e on a.FIndexSource=e.FIndexSrcCode" +
                " left join Tb_Fun_Vocabulary o on " + dbl.sqlToChar("a.FDataSource") + "= o.FVocCode and o.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_IDX_DATASOURCE) +
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc) y on x.FindexCode=y.FIndexCode"; // wdy modify 20070830
            //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("Index");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setResultSetAttr(rs);
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
                this.getListView1ShowCols()+ "\r\f"+ yssPageInationBean.buildRowStr()+"\r\f";//QDV4赢时胜上海2010年03月15日06_B MS00884 by xuqiji
        } catch (Exception e) {
            throw new YssException("获取指数行情信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.strIndexCode = rs.getString("FIndexCode") + "";
        this.strIndexName = rs.getString("FIndexName") + "";
        this.strIndexSrcCode = rs.getString("FIndexSource") + "";
        this.strIndexSrcName = rs.getString("FIndexSrcName") + "";
        this.strExchangeCode = rs.getString("FExchangeCode") + "";
        this.strExchangeName = rs.getString("FExchangeName") + "";
        this.strDesc = rs.getString("FDesc") + "";
        //this.strDataSource=rs.getInt("FDataSource") + "";
        this.strIndexDate = rs.getDate("FDate") + "";
        this.strIndexTime = rs.getString("FTime") + "";
        this.dblTopValue = rs.getDouble("FTopValue");
        this.dblLowValue = rs.getDouble("FLowValue");
        this.dblOpenValue = rs.getDouble("FOpenValue");
        this.dblClosedValue = rs.getDouble("FClosedValue");
        this.dblNewValue = rs.getDouble("FNewValue");
        this.dblAvgValue = rs.getDouble("FAvgValue");
        this.dblMarkupValue = rs.getDouble("FMarkupValue");
        this.dblMarkupPercent = rs.getDouble("FMarkupPercent");
        super.setRecLog(rs);

    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if(pub.isBrown()==true) //add by ysh 20111025 STORY 1285  如果要浏览数据，则直接返回
			return " where 1=1";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.isOnlyColumns.equals("1")) {
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }
            if (this.filterType.strIndexCode.length() != 0) {
                sResult = sResult + " and a.FIndexCode like '" +
                    filterType.strIndexCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.strIndexSrcCode.length() != 0) {
                sResult = sResult + " and a.FIndexSource like '" +
                    filterType.strIndexSrcCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.strIndexDate.length() != 0 &&
                !this.filterType.strIndexDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FDate = " +
                    dbl.sqlDate(filterType.strIndexDate);
            }
            /*  if (this.filterType.strIndexTime.length() != 0 &&
                  !this.filterType.strIndexTime.equals("00:00:00")) {
                 sResult = sResult + " and FTime = " +
                       dbl.sqlString(filterType.strIndexTime);
              }
             */
        }
        return sResult;

    }

    /**
     * getListViewData2
     *
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
            sHeader = "指数行情代码\t指数行情名称";
            strSql = strSql = "select y.* from " +
                "(select FIndexCode from " + pub.yssGetTableName("Tb_Data_Index") + " " +
                " where FCheckState =1  group by FIndexCode) x join" +
                "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
                " d.FIndexName as FIndexName,d.FExchangeCode as FExchangeCode,d.FExchangeName as FExchangeName,e.FIndexSrcName as FIndexSrcName,o.FVocName as FDataSourceValue" +
                " from " + pub.yssGetTableName("Tb_Data_Index") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select da.*,dc.FExchangeName  from (select FIndexCode,FIndexName,FExchangeCode from "
                + pub.yssGetTableName("Tb_Para_Index") + ") da " +
                "left join (select FExchangecode,FExchangeName from tb_base_exchange)  dc on da.FExchangeCode =dc.FExchangecode) d on a.FIndexCode=d.FIndexCode" +
                " left join (select FIndexSrcCode,FIndexSrcName from " + pub.yssGetTableName("Tb_Para_IndexSource") + ") e on a.FIndexSource=e.FIndexSrcCode" +
                " left join Tb_Fun_Vocabulary o on " + dbl.sqlToChar("a.FDataSource") + "= o.FVocCode and o.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_IDX_DATASOURCE) +
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc) y on x.FindexCode=y.FIndexCode"; // wdy modify 20070830

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FIndexCode") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FIndexName") + "").trim()).append(YssCons.YSS_LINESPLITMARK);
                setResultSetAttr(rs);
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
            throw new YssException("获取分红权益设置信息出错！", e);
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
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql =
                " insert into " + pub.yssGetTableName("Tb_Data_Index") +
                "(FIndexCode,FIndexSource,FDate,FTime,FTopValue,FLowValue,FOpenValue," +
                "FClosedValue,FNewValue,FAvgValue,FMarkupValue,FMarkupPercent,FDataSource,FDesc," +
                " FCheckState, FCreator, FCreateTime,FCheckUser) " +
                " values(" + dbl.sqlString(this.strIndexCode) + "," +
                dbl.sqlString(this.strIndexSrcCode) + "," +
                dbl.sqlDate(this.strIndexDate) + "," +
                dbl.sqlString(this.strIndexTime) + "," +
                this.dblTopValue + "," +
                this.dblLowValue + "," +
                this.dblOpenValue + "," +
                this.dblClosedValue + "," +
                this.dblNewValue + "," +
                this.dblAvgValue + "," +
                this.dblMarkupValue + "," +
                this.dblMarkupPercent + "," +
                0 + "," +
                dbl.sqlString(this.strDesc) + "," +
                //   dbl.sqlString(this.strDataSource) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增指数行情信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Data_Index"),
                               "FIndexCode,FIndexSource,FDate,FTime",
                               this.strIndexCode + "," + (this.strIndexSrcCode.length() == 0 ? " " : this.strIndexSrcCode) +
                               "," +
                               this.strIndexDate + "," +
                               this.strIndexTime,
                               this.strOldIndexCode + "," +
                               this.strOldIndexSrcCode +
                               "," + this.strOldIndexDate + "," +
                               this.strOldIndexTime);

    }

    /**
     * 修改时间：2008年3月27号
     * 修改人：单亮
     * 原方法功能：只能处理指数行情的审核和未审核的单条信息。
     * 新方法功能：可以处理指数行情审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理指数行情审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        //修改前的代码
//      Connection conn = dbl.loadConnection();
//      boolean bTrans = false;
//      String strSql = "";
//      try {
//         strSql = "update " + pub.yssGetTableName("Tb_Data_Index") +
//               " set FCheckState = " + this.checkStateId +
//               ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//               ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//               "'" + " where FIndexCode=" + dbl.sqlString(this.strOldIndexCode) +
//              " and FIndexSource=" +  dbl.sqlString(this.strOldIndexSrcCode) +
//              " and FDate=" + dbl.sqlDate(this.strOldIndexDate) +
//               " and FTime=" + dbl.sqlString(this.strOldIndexTime);
//
//         conn.setAutoCommit(false);
//         bTrans = true;
//         dbl.executeSql(strSql);
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//
//      }
//      catch (Exception e) {
//         throw new YssException("修改指数行情信息出错", e);
//      }
//      finally {
//         dbl.endTransFinal(conn, bTrans);
//      }
        //修改后的代码
        //--------------begin
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if ( sRecycled != null&&(!sRecycled.equalsIgnoreCase(""))) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "update " + pub.yssGetTableName("Tb_Data_Index") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "'" + " where FIndexCode=" +
                        dbl.sqlString(this.strOldIndexCode) +
                        " and FIndexSource=" +
                        dbl.sqlString(this.strOldIndexSrcCode) +
                        " and FDate=" + dbl.sqlDate(this.strOldIndexDate) +
                        " and FTime=" + dbl.sqlString(this.strOldIndexTime);
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而strOldIndexCode不为空，则按照strOldIndexCode来执行sql语句
            else if ( strOldIndexCode != null&&(!strOldIndexCode.equalsIgnoreCase(""))) {
                strSql = "update " + pub.yssGetTableName("Tb_Data_Index") +
                    " set FCheckState = " + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "'" + " where FIndexCode=" +
                    dbl.sqlString(this.strOldIndexCode) +
                    " and FIndexSource=" +
                    dbl.sqlString(this.strOldIndexSrcCode) +
                    " and FDate=" + dbl.sqlDate(this.strOldIndexDate) +
                    " and FTime=" + dbl.sqlString(this.strOldIndexTime);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改指数行情信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
//----------end
    }

    /**
     * 将数据放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Data_Index") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" + " where FIndexCode=" + dbl.sqlString(this.strOldIndexCode) +
                " and FIndexSource=" + dbl.sqlString(this.strOldIndexSrcCode) +
                " and FDate=" + dbl.sqlDate(this.strOldIndexDate) +
                " and FTime=" + dbl.sqlString(this.strOldIndexTime);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("修改指数行情信息出错", e);
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
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Data_Index") +
                " set FIndexCode = " + dbl.sqlString(this.strIndexCode) + ",FIndexSource = " +
                dbl.sqlString(this.strIndexSrcCode) + ",FDate=" +
                dbl.sqlDate(this.strIndexDate) + ",FTime=" +
                dbl.sqlString(this.strIndexTime) + ",FTopValue=" +
                this.dblTopValue + ",FLowValue=" +
                this.dblLowValue + ",FOpenValue=" +
                this.dblOpenValue + ",FClosedValue=" +
                this.dblClosedValue + ",FNewValue=" +
                this.dblNewValue + ",FAvgValue=" +
                this.dblAvgValue + ",FMarkupValue=" +
                this.dblMarkupValue + ",FMarkupPercent=" +
                this.dblMarkupPercent + ", FDesc=" +
                dbl.sqlString(this.strDesc) +
                ", FCreator = " //+",FDataSource=" +
                // this.strDataSource
                + dbl.sqlString(this.creatorCode) + " , FCreateTime = "
                + dbl.sqlString(this.creatorTime) +
                " where FIndexCode=" + dbl.sqlString(this.strOldIndexCode) +
                " and FIndexSource=" + dbl.sqlString(this.strOldIndexSrcCode) +
                " and FDate=" + dbl.sqlDate(this.strOldIndexDate) +
                " and FTime=" + dbl.sqlString(this.strOldIndexTime);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改指数行情信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
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
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strIndexCode).append("\t");
        buf.append(this.strIndexName).append("\t");
        buf.append(this.strIndexSrcCode).append("\t");
        buf.append(this.strIndexSrcName).append("\t");
        buf.append(this.strIndexDate).append("\t");
        buf.append(this.strIndexTime).append("\t");
        buf.append(this.strExchangeCode).append("\t");
        buf.append(this.strExchangeName).append("\t");
        buf.append(this.dblTopValue).append("\t");
        buf.append(this.dblLowValue).append("\t");
        buf.append(this.dblOpenValue).append("\t");
        buf.append(this.dblClosedValue).append("\t");
        buf.append(this.dblNewValue).append("\t");
        buf.append(this.dblAvgValue).append("\t");
        buf.append(this.dblMarkupValue).append("\t");
        buf.append(this.dblMarkupPercent).append("\t");
        buf.append(this.strDesc).append("\t");
        //buf.append(this.strDataSource.equals("") ? " " : this.strDataSource).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();

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
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
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
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.strIndexCode = reqAry[0];
            this.strIndexName = reqAry[1];
            this.strIndexSrcCode = reqAry[2];
            this.strIndexSrcName = reqAry[3];
            this.strIndexDate = reqAry[4];
            this.strIndexTime = reqAry[5];
            this.strExchangeCode = reqAry[6];
            this.strExchangeName = reqAry[7];
            if (YssFun.isNumeric(reqAry[8])) {
                this.dblTopValue = Double.parseDouble(reqAry[8]);
            }
            if (YssFun.isNumeric(reqAry[9])) {
                this.dblLowValue = Double.parseDouble(reqAry[9]);
            }
            if (YssFun.isNumeric(reqAry[10])) {
                this.dblOpenValue = Double.parseDouble(reqAry[10]);
            }
            if (YssFun.isNumeric(reqAry[11])) {
                this.dblClosedValue = Double.parseDouble(reqAry[11]);
            }
            if (YssFun.isNumeric(reqAry[12])) {
                this.dblNewValue = Double.parseDouble(reqAry[12]);
            }
            if (YssFun.isNumeric(reqAry[13])) {
                this.dblAvgValue = Double.parseDouble(reqAry[13]);
            }
            if (YssFun.isNumeric(reqAry[14])) {
                this.dblMarkupValue = Double.parseDouble(reqAry[14]);
            }
            if (YssFun.isNumeric(reqAry[15])) {
                this.dblMarkupPercent = Double.parseDouble(reqAry[15]);
            }
            //------ modify by nimengjing 2010.12.02 BUG #535 指数行情设置界面描述字段中存在回车符时，清除/还原报错
	         if (reqAry[16] != null ){
	        	 if (reqAry[16].indexOf("【Enter】") >= 0){
	        		 this.strDesc= reqAry[16].replaceAll("【Enter】", "\r\n");
	             }
	             else{
	            	 this.strDesc = reqAry[16];
	             }
	         }
	         //----------------- BUG #533 ----------------//
            // this.strDataSource=reqAry[16];
            this.checkStateId = Integer.parseInt(reqAry[17]);
            this.strOldIndexCode = reqAry[18];
            this.strOldIndexSrcCode = reqAry[19];
            this.strOldIndexDate = reqAry[20];
            this.strOldIndexTime = reqAry[21];
            this.isOnlyColumns = reqAry[22];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DataIndexBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析指数行情请求信息出错", e);
        }

    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        DataIndexBean befEditBean = new DataIndexBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                "(select FIndexCode from " + pub.yssGetTableName("Tb_Data_Index") + " " +
                " where FCheckState <> 2" +
                " and   FIndexCode =" + dbl.sqlString(this.strOldIndexCode) +
                " and FIndexSource=" + dbl.sqlString(this.strOldIndexSrcCode) +
                " and FDate=" + dbl.sqlDate(this.strOldIndexDate) +
                " and FTime=" + dbl.sqlString(this.strOldIndexTime) +
                " group by FIndexCode) x join" +
                "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
                " d.FIndexName as FIndexName,d.FExchangeCode as FExchangeCode,d.FExchangeName as FExchangeName,e.FIndexSrcName as FIndexSrcName,o.FVocName as FDataSourceValue" +
                " from " + pub.yssGetTableName("Tb_Data_Index") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select da.*,dc.FExchangeName  from (select FIndexCode,FIndexName,FExchangeCode from "
                + pub.yssGetTableName("Tb_Para_Index") + ") da " +
                "left join (select FExchangecode,FExchangeName from tb_base_exchange)  dc on da.FExchangeCode =dc.FExchangecode) d on a.FIndexCode=d.FIndexCode" +
                " left join (select FIndexSrcCode,FIndexSrcName from " + pub.yssGetTableName("Tb_Para_IndexSource") + ") e on a.FIndexSource=e.FIndexSrcCode" +
                " left join Tb_Fun_Vocabulary o on " + dbl.sqlToChar("a.FDataSource") + " = o.FVocCode and o.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_IDX_DATASOURCE) +
                ") y on x.FindexCode=y.FIndexCode";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.strIndexCode = rs.getString("FIndexCode") + "";
                befEditBean.strIndexName = rs.getString("FIndexName") + "";
                befEditBean.strIndexSrcCode = rs.getString("FIndexSource") + "";
                befEditBean.strIndexSrcName = rs.getString("FIndexSrcName") + "";
                befEditBean.strExchangeCode = rs.getString("FExchangeCode") + "";
                befEditBean.strExchangeName = rs.getString("FExchangeName") + "";
                befEditBean.strIndexDate = rs.getDate("FDate") + "";
                befEditBean.strIndexTime = rs.getString("FTime") + "";
                befEditBean.dblTopValue = rs.getDouble("FTopValue");
                befEditBean.dblLowValue = rs.getDouble("FLowValue");
                befEditBean.dblNewValue = rs.getDouble("FOpenValue");
                befEditBean.dblAvgValue = rs.getDouble("FClosedValue");
                befEditBean.dblOpenValue = rs.getDouble("FNewValue");
                befEditBean.dblClosedValue = rs.getDouble("FAvgValue");
                befEditBean.dblMarkupValue = rs.getDouble("FMarkupValue");
                befEditBean.dblMarkupPercent = rs.getDouble("FMarkupPercent");
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
     * 删除回收站的数据，即从数据库彻底删除数据
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
                        pub.yssGetTableName("Tb_Data_Index") +
                        " where FIndexCode=" + dbl.sqlString(this.strOldIndexCode) +
                        " and FIndexSource=" +
                        dbl.sqlString(this.strOldIndexSrcCode) +
                        " and FDate=" + dbl.sqlDate(this.strOldIndexDate) +
                        " and FTime=" + dbl.sqlString(this.strOldIndexTime);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            else if (strOldIndexCode != "" && strOldIndexCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_Index") +
                    " where FIndexCode=" + dbl.sqlString(this.strOldIndexCode) +
                    " and FIndexSource=" +
                    dbl.sqlString(this.strOldIndexSrcCode) +
                    " and FDate=" + dbl.sqlDate(this.strOldIndexDate) +
                    " and FTime=" + dbl.sqlString(this.strOldIndexTime);
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
