package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 *
 * <p>Title: RoundingBean </p>
 * <p>Description: 舍入设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class RoundingBean
    extends BaseDataSettingBean implements IDataSetting {
    private String roundCode = ""; //舍入代码
    private String roundName = ""; //舍入名称
    private String roundSymbol = ""; //舍入符号
    private String roundRange = ""; //舍入范围
    private int roundDigit = -1; //舍入位数
    private String roundWay = ""; //舍入方法
    private String desc = ""; //舍入描述
    private RoundingBean filterType;
    private String sRecycled = "";
    private String oldRoundCode;
    public String getRoundSymbol() {
        return roundSymbol;
    }

    public String getRoundWay() {
        return roundWay;
    }

    public String getDesc() {
        return desc;
    }

    public String getRoundCode() {
        return roundCode;
    }

    public int getRoundDigit() {
        return roundDigit;
    }

    public String getRoundName() {
        return roundName;
    }

    public void setRoundRange(String roundRange) {
        this.roundRange = roundRange;
    }

    public void setRoundSymbol(String roundSymbol) {
        this.roundSymbol = roundSymbol;
    }

    public void setRoundWay(String roundWay) {
        this.roundWay = roundWay;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setRoundCode(String roundCode) {
        this.roundCode = roundCode;
    }

    public void setRoundDigit(int roundDigit) {
        this.roundDigit = roundDigit;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public String getRoundRange() {
        return roundRange;
    }

    public RoundingBean() {
    }

    /**
     * parseRowStr
     * 解析舍入设置
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
            this.roundCode = reqAry[0];
            this.roundName = reqAry[1];
            this.roundSymbol = reqAry[2];
            this.roundRange = reqAry[3];
            this.roundDigit = Integer.parseInt(reqAry[4]);
            this.roundWay = reqAry[5];
            //------ modify by wangzuochun 2011.06.03 BUG 2003 证券信息维护界面，维护一条证券信息，输入描述信息若含有回车符，清除/还原时报错 
            if (reqAry[6] != null ){
            	if (reqAry[6].indexOf("【Enter】") >= 0){
            		this.desc = reqAry[6].replaceAll("【Enter】", "\r\n");
            	}
            	else{
            		this.desc = reqAry[6];
            	}
            }
            //----------------- BUG 2003 ----------------//
            this.checkStateId = Integer.parseInt(reqAry[7]);
            this.oldRoundCode = reqAry[8];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new RoundingBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析舍入设置请求出错", e);
        }
    }

    /**
     * auditSetting
     */
    public void auditSetting() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.roundCode).append("\t");
        buf.append(this.roundName).append("\t");
        buf.append(this.roundSymbol).append("\t");
        buf.append(this.roundRange).append("\t");
        buf.append(this.roundDigit).append("\t");
        buf.append(this.roundWay).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     */
    public void checkInput() {
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
            if (this.filterType.roundCode.length() != 0) {
                sResult = sResult + " and a.FRoundCode like '" +
                    filterType.roundCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.roundName.length() != 0) {
                sResult = sResult + " and a.FRoundName like '" +
                    filterType.roundName.replaceAll("'", "''") + "%'";
            }

            if (!this.filterType.roundSymbol.equalsIgnoreCase("99") &&
                this.filterType.roundSymbol.length() != 0) {
                sResult = sResult + " and a.FRoundSymbol = " +
                    filterType.roundSymbol;
            }
            if (!this.filterType.roundRange.equalsIgnoreCase("99") &&
                this.filterType.roundRange.length() != 0) {
                sResult = sResult + " and a.FRoundRange = " +
                    filterType.roundRange;
            }
            if (this.filterType.roundDigit != -1) {
                sResult = sResult + " and a.FRoundDigit = " +
                    filterType.roundDigit;
            }
            if (!this.filterType.roundWay.equalsIgnoreCase("99") &&
                this.filterType.roundWay.length() != 0) {
                sResult = sResult + " and a.FRoundWay = " +
                    filterType.roundWay;
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%' ";
            }
        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取舍入设置信息
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sVocStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.*, f.FVocName as FRoundSymbolValue, g.FVocName as FRoundRangeValue, h.FVocName as FRoundWayValue," +
                " d.FUserName as FCreatorName,e.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_Rounding") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                //2007.12.01 修改 蒋锦 使用dbl.sqlToChar()处理"a.FRoundSymbol"，否则在使用DB2数据库时会报数据类型错误
                " left join Tb_Fun_Vocabulary f on " + dbl.sqlToChar("a.FRoundSymbol") + " = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_RND_SYMBOL) +
                //2007.12.01 修改 蒋锦 使用dbl.sqlToChar()处理"a.FRoundRange"，否则在使用DB2数据库时会报数据类型错误
                " left join Tb_Fun_Vocabulary g on " + dbl.sqlToChar("a.FRoundRange") + " = g.FVocCode and g.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_RND_RANGE) +
                //2007.12.01 修改 蒋锦 使用dbl.sqlToChar()处理"a.FRoundWay"，否则在使用DB2数据库时会报数据类型错误
                " left join Tb_Fun_Vocabulary h on " + dbl.sqlToChar("a.FRoundWay") + " = h.FVocCode and h.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_RND_WAY) +
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc"; // wdy modify 20070830

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.roundCode = rs.getString("FRoundCode") + "";
                this.roundName = rs.getString("FRoundName") + "";
                this.roundSymbol = rs.getString("FRoundSymbol") + "";
                this.roundRange = rs.getString("FRoundRange") + "";
                this.roundDigit = rs.getInt("FRoundDigit");
                this.roundWay = rs.getString("FRoundWay") + "";
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

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_RND_SYMBOL + "," +
                                        YssCons.YSS_RND_RANGE + "," +
                                        YssCons.YSS_RND_WAY);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取舍入信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData2
     * 获取已审核的舍入设置信息
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
            sHeader = "舍入代码\t舍入名称\t舍入描述";
            strSql =
                "select a.*, d.FUserName as FCreatorName,e.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_Rounding") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " where a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FRoundCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FRoundName") + "").trim()).append(
                    "\t");
                bufShow.append(YssFun.left( (rs.getString("FDesc") + "").trim(), 40)).
                    append(YssCons.YSS_LINESPLITMARK);

                this.roundCode = rs.getString("FRoundCode") + "";
                this.roundName = rs.getString("FRoundName") + "";
                this.roundSymbol = rs.getString("FRoundSymbol") + "";
                this.roundRange = rs.getString("FRoundRange") + "";
                this.roundDigit = rs.getInt("FRoundDigit");
                this.roundWay = rs.getString("FRoundWay") + "";
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
            throw new YssException("获取可用舍入信息出错", e);
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
     * getPartSetting
     *
     * @return String
     */
    public String getPartSetting() {
        return "";
    }

    /**
     * getSetting
     *
     * @return IBaseSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Para_Rounding") +
                " where FRoundCode = " + dbl.sqlString(this.roundCode) +
                " and FCheckState = 1";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.roundCode = rs.getString("FRoundCode");
                this.roundName = rs.getString("FRoundName");
                this.roundSymbol = rs.getString("FRoundSymbol");
                this.roundRange = rs.getString("FRoundRange");
                this.roundDigit = rs.getInt("FRoundDigit");
                this.roundWay = rs.getString("FRoundWay");
                this.desc = rs.getString("FDesc");
            }
        } catch (Exception e) {
            throw new YssException("获取舍入设置错误！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Rounding"),
                               "FRoundCode",
                               this.roundCode, this.oldRoundCode);
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
              strSql = "insert into " + pub.yssGetTableName("Tb_Para_Rounding") +
                    " " +
                    "(FRoundCode,FRoundName,FRoundSymbol,FRoundRange,FRoundDigit,FRoundWay,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                    " values(" + dbl.sqlString(this.roundCode) + "," +
                    dbl.sqlString(this.roundName) + "," +
                    this.roundSymbol + "," +
                    this.roundRange + " ," +
                    this.roundDigit + "," +
                    this.roundWay + "," +
                    dbl.sqlString(this.desc) + "," +
                    (pub.getSysCheckState() ? "0" : "1") + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) + ")";
           }
           else if (btOper == YssCons.OP_EDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_Rounding") +
                    "  set FRoundCode = " +
                    dbl.sqlString(this.roundCode) + " , FRoundName = " +
                    dbl.sqlString(this.roundName) + " , FRoundSymbol= " +
                    this.roundSymbol + " , FRoundRange = " +
                    this.roundRange + " , FRoundDigit = " +
                    this.roundDigit + ", FRoundWay = " +
                    this.roundWay + ", FDesc = " +
                    dbl.sqlString(this.desc) + ",FCheckState = " +
                    (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                    dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                    dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) +
                    " where FRoundCode = " +
                    dbl.sqlString(this.oldRoundCode);
           }
           else if (btOper == YssCons.OP_DEL) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_Rounding") +
                    "  set FCheckState = " + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "'" +
                    " where FRoundCode = " + dbl.sqlString(this.roundCode);

           }
           else if (btOper == YssCons.OP_AUDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_Rounding") +
                    "  set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FRoundCode = " + dbl.sqlString(this.roundCode);
           }
           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("更新舍入设置信息出错", e);
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
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_Rounding") +
                " " +
                "(FRoundCode,FRoundName,FRoundSymbol,FRoundRange,FRoundDigit,FRoundWay,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" + dbl.sqlString(this.roundCode) + "," +
                dbl.sqlString(this.roundName) + "," +
                this.roundSymbol + "," +
                this.roundRange + " ," +
                this.roundDigit + "," +
                this.roundWay + "," +
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
            throw new YssException("增加舍入设置信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_Rounding") +
                "  set FRoundCode = " +
                dbl.sqlString(this.roundCode) + " , FRoundName = " +
                dbl.sqlString(this.roundName) + " , FRoundSymbol= " +
                this.roundSymbol + " , FRoundRange = " +
                this.roundRange + " , FRoundDigit = " +
                this.roundDigit + ", FRoundWay = " +
                this.roundWay + ", FDesc = " +
                dbl.sqlString(this.desc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FRoundCode = " +
                dbl.sqlString(this.oldRoundCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改舍入设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;

    }

    /**
     * 删除数据，即是放入数据库
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_Rounding") +
                "  set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FRoundCode = " + dbl.sqlString(this.roundCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除舍入设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**修改时间：2008年3月20号
     *  修改人：单亮
     *  原方法功能：只能处理货币方向的审核和未审核的单条信息。
     *  新方法功能：可以处理货币方向审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     */

    public void checkSetting() throws YssException {
        //修改前的代码
//        String strSql = "";
//        boolean bTrans = false; //代表是否开始了事务
//        Connection conn = dbl.loadConnection();
//        try {
//           strSql = "update " + pub.yssGetTableName("Tb_Para_Rounding") +
//                   "  set FCheckState = " +
//                   this.checkStateId + ", FCheckUser = " +
//                   dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
//                   YssFun.formatDatetime(new java.util.Date()) + "'" +
//                   " where FRoundCode = " + dbl.sqlString(this.roundCode);
//           conn.setAutoCommit(false);
//           bTrans = true;
//           dbl.executeSql(strSql);
//           conn.commit();
//           bTrans = false;
//           conn.setAutoCommit(true);
//        }
//
//        catch (Exception e) {
//           throw new YssException("审核舍入设置信息出错", e);
//        }
//        finally {
//           dbl.endTransFinal(conn, bTrans);
//        }
        //修改后的代码
        //---------------------------
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

                strSql = "update " + pub.yssGetTableName("Tb_Para_Rounding") +
                    "  set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FRoundCode = " + dbl.sqlString(this.roundCode);

                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("审核舍入设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //---------------------------------------

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
        RoundingBean befEditBean = new RoundingBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*, f.FVocName as FRoundSymbolValue, g.FVocName as FRoundRangeValue, h.FVocName as FRoundWayValue," +
                " d.FUserName as FCreatorName,e.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_Rounding") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " left join Tb_Fun_Vocabulary f on " + dbl.sqlToChar("a.FRoundSymbol") + " = f.FVocCode and f.FVocTypeCode = " + //2007.12.06 修改 蒋锦 a.FRoundSymbol 在DB2 下需要类型转换
                dbl.sqlString(YssCons.YSS_RND_SYMBOL) +
                " left join Tb_Fun_Vocabulary g on " + dbl.sqlToChar("a.FRoundRange") + " = g.FVocCode and g.FVocTypeCode = " + //2007.12.06 修改 蒋锦 a.FRoundRange 在DB2 下需要类型转换
                dbl.sqlString(YssCons.YSS_RND_RANGE) +
                " left join Tb_Fun_Vocabulary h on " + dbl.sqlToChar("a.FRoundWay") + " = h.FVocCode and h.FVocTypeCode = " + //2007.12.06 修改 蒋锦 a.FRoundWay 在DB2 下需要类型转换
                dbl.sqlString(YssCons.YSS_RND_WAY) +
                " where  a.FRoundCode =" + dbl.sqlString(this.oldRoundCode) +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.roundCode = rs.getString("FRoundCode") + "";
                befEditBean.roundName = rs.getString("FRoundName") + "";
                befEditBean.roundSymbol = rs.getString("FRoundSymbol") + "";
                befEditBean.roundRange = rs.getString("FRoundRange") + "";
                befEditBean.roundDigit = rs.getInt("FRoundDigit");
                befEditBean.roundWay = rs.getString("FRoundWay") + "";
                befEditBean.desc = rs.getString("FDesc");

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

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
                strSql = "delete from " + pub.yssGetTableName("Tb_Para_Rounding") +
                    " where FRoundCode = " + dbl.sqlString(this.roundCode);

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
