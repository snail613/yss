package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class CurrencyWayBean
    extends BaseDataSettingBean implements IDataSetting {
    public CurrencyWayBean() {
    }

    private String sPortCode = ""; //组合代码
    private String sPortName = ""; //组合名称
    private String sCuryCode = ""; //币种代码
    private String sCuryName = ""; //币种名称
    private double sFactor = -1; //投资货币对基准货币的报价因子 linjunyun 2008-11-21 bug:MS00011

    private String sMarkCury; //基准货币
    private String sMarkName; //基准货币
    private String sQuoteWay; //报价方向，99默认为所有记录
    private String sDesc = ""; //描述
    private String sOldCuryCode = "";
    private String sOldPortCode = ""; //组合代码
    private String sOldMarkCury = "";
    private String sRecycled = "";

    private CurrencyWayBean filterType;
    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = ""; //词汇类型对照字符串
        StringBuffer bufShow = new StringBuffer();

        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            String sql =
                "select a.*, b.FVocName as FQuoteWay,c.FPortName as FPortName ," +
                " d.FUserName as FCreatorName,e.FUserName as FCheckUserName ,f.FCuryName as FCuryName ,g.FCuryName as FMarkName from " +
                pub.yssGetTableName("Tb_Para_CurrencyWay") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " left join Tb_Fun_Vocabulary b on a.FQuoteWay  = b.FVocCode and b.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CRYW_QUOTEWAY) +
                " left join (select FPortCode, FPortName from " + pub.yssGetTableName("Tb_Para_Portfolio") + " where FCheckState =1)  c on a.FPortCode = c.FPortCode" +
                " left join (select FCuryCode, FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + " where FCheckState =1)  f on a.FCuryCode = f.FCuryCode" +
                " left join (select FCuryCode, FCuryName from " + pub.yssGetTableName("Tb_Para_Currency") + " where FCheckState =1)  g on a.FMarkCury = g.FCuryCode" +
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setCurrencyWayCfgAttr(rs);
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

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_CRYW_QUOTEWAY);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取币种配置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() {
        return "";
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
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_CurrencyWay") +
                "" +
                "(FPortCode,FCuryCode,FMarkCury,FQuoteWay,FFactor," +
                "FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.sPortCode) + "," +
                dbl.sqlString(this.sCuryCode) + "," +
                dbl.sqlString(this.sMarkCury) + "," +
                dbl.sqlString(this.sQuoteWay) + "," +
                this.sFactor + "," + //插入投资货币对基准货币的报价因子 linjunyun 2008-11-21 bug:MS00011
                dbl.sqlString(this.sDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加货币方向配置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Para_CurrencyWay")
                               ,
                               "FPortCode,FCuryCode,FMarkCury"
                               ,
                               this.sPortCode + "," + this.sCuryCode + "," + this.sMarkCury
                               ,
                               this.sOldPortCode + "," + this.sOldCuryCode + "," + this.sOldMarkCury
            );
    }

    /**修改时间：2008年3月20号
     *  修改人：单亮
     *  原方法功能：只能处理货币方向的审核和未审核的单条信息。
     *  新方法功能：可以处理货币方向审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     */

    public void checkSetting() throws YssException {
        //修改前的代码
//      String strSql = "";
//        boolean bTrans = false; //代表是否开始了事务
//        Connection conn = dbl.loadConnection();
//        try {
//           strSql = "update " + pub.yssGetTableName("Tb_Para_CurrencyWay") +
//                 " set FCheckState = " + this.checkStateId +
//                 ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//                 ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//                 "'" +
//                 " where FPortCode = " + dbl.sqlString(this.sPortCode) +
//                 " and FCuryCode = " + dbl.sqlString(this.sCuryCode) +
//                 " and FMarkCury = " + dbl.sqlString(this.sMarkCury);
//
//           conn.setAutoCommit(false);
//           bTrans = true;
//           dbl.executeSql(strSql);
//           conn.commit();
//           bTrans = false;
//           conn.setAutoCommit(true);
//        }

        //修改后的代码
        //--------------------------------
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {

            conn.setAutoCommit(false);
            arrData = sRecycled.split("\r\n");
            bTrans = true;
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);

                strSql = "update " + pub.yssGetTableName("Tb_Para_CurrencyWay") +
                    " set FCheckState = " + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "'" +
                    " where FPortCode = " + dbl.sqlString(this.sPortCode) +
                    " and FCuryCode = " + dbl.sqlString(this.sCuryCode) +
                    " and FMarkCury = " + dbl.sqlString(this.sMarkCury);
                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }
        //--------------------------------
        catch (Exception e) {
            throw new YssException("审核货币方向配置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 删除数据，即是放入回收站
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_CurrencyWay") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FPortCode = " + dbl.sqlString(this.sPortCode) +
                " and FCuryCode = " + dbl.sqlString(this.sCuryCode) +
                " and FMarkCury = " + dbl.sqlString(this.sMarkCury);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除货币方向配置出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_CurrencyWay") +
                " set FPortCode = " + dbl.sqlString(this.sPortCode) +
                ", FCuryCode = " + dbl.sqlString(this.sCuryCode) +
                ", FMarkCury = " + dbl.sqlString(this.sMarkCury) +
                ", FQuoteWay = " + dbl.sqlString(this.sQuoteWay) +
                //更新投资货币对基准货币的报价因子 linjunyun 2008-11-21 bug:MS00011
                ", FFactor = " + this.sFactor +
                ", FDesc = " + dbl.sqlString(this.sDesc) +
                ",FCheckState = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCreator = " + dbl.sqlString(this.creatorCode) +
                ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FPortCode = " + dbl.sqlString(this.sOldPortCode) +
                " and FCuryCode = " + dbl.sqlString(this.sOldCuryCode) +
                " and FMarkCury = " + dbl.sqlString(this.sOldMarkCury);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改货币方向配置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

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
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.sPortCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.sPortCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sCuryCode.length() != 0) {
                sResult = sResult + " and a.FCuryCode like '" +
                    filterType.sCuryCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sMarkCury.length() != 0) {
                sResult = sResult + " and a.FMarkCury like '" +
                    filterType.sMarkCury.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sQuoteWay.length() != 0 &&
                !this.filterType.sQuoteWay.equalsIgnoreCase("99")) {
                sResult = sResult + " and a.FQuoteWay like '" +
                    filterType.sQuoteWay.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.sDesc.replaceAll("'", "''") + "%'";
            }
            //增加筛选投资货币对基准货币的报价因子 linjunyun 2008-11-21 bug:MS00011
            if (this.filterType.sFactor != -1) {
                sResult = sResult + " and a.FFactor = " +
                    filterType.sFactor;
            }
        }
        return sResult;
    }

    public void setCurrencyWayCfgAttr(ResultSet rs) throws SQLException,
        YssException {

        this.sPortCode = rs.getString("FPortCode") + "";
        this.sPortName = rs.getString("FPortName") + "";

        this.sMarkCury = rs.getString("FMarkCury") + "";
        this.sMarkName = rs.getString("FMarkName") + "";
        this.sQuoteWay = rs.getString("FQuoteWay") + "";

        this.sCuryCode = rs.getString("FCuryCode") + "";
        this.sCuryName = rs.getString("FCuryName") + "";
        this.sFactor = rs.getDouble("FFactor"); //取得投资货币对基准货币的报价因子 linjunyun 2008-11-21 bug:MS00011
        this.sDesc = rs.getString("FDesc") + "";
        super.setRecLog(rs);
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
        buf.append(this.sPortCode).append("\t");
        buf.append(this.sPortName).append("\t");
        buf.append(this.sCuryCode).append("\t");
        buf.append(this.sCuryName).append("\t");
        buf.append(this.sMarkCury).append("\t");
        buf.append(this.sMarkName).append("\t");
        buf.append(this.sQuoteWay).append("\t");
        buf.append(this.sDesc).append("\t");
        buf.append(this.sFactor).append("\t"); //投资货币对基准货币的报价因子 linjunyun 2008-11-21 bug:MS00011
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

            this.sPortCode = reqAry[0];
            this.sPortName = reqAry[1];
            this.sCuryCode = reqAry[2];
            this.sCuryName = reqAry[3];
            this.sMarkCury = reqAry[4];
            this.sMarkName = reqAry[5];
            this.sQuoteWay = reqAry[6];
            this.sDesc = reqAry[7];
            this.checkStateId = Integer.parseInt(reqAry[8]);
            this.sOldPortCode = reqAry[9];
            this.sOldCuryCode = reqAry[10];
            this.sFactor = Double.parseDouble(reqAry[11]); //赋值投资货币对基准货币的报价因子 linjunyun 2008-11-21 bug:MS00011
            this.sOldMarkCury = reqAry[12];

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CurrencyWayBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析货币方向配置请求出错", e);
        }
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    /**
     * 从回收站删除数据，即是彻底删除
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
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
                strSql = "delete from " + pub.yssGetTableName("Tb_Para_CurrencyWay") +
                    " where FPortCode = " + dbl.sqlString(this.sPortCode) +
                    " and FCuryCode = " + dbl.sqlString(this.sCuryCode) +
                    " and FMarkCury = " + dbl.sqlString(this.sMarkCury);

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
