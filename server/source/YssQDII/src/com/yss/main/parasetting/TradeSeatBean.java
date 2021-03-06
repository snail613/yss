package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 *
 * <p>Title: TradeSeatBean</p>
 * <p>Description:交易席位设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class TradeSeatBean
    extends BaseDataSettingBean implements IDataSetting {
    private String seatCode; //席位代码
    private String seatName; //席位名称  lzp 12.3 add
    private String seatType; //席位类型
    private String Type; //席位类型
    //---------lzp 12.3 add
    // private String startDate; //启用日期
    private String seatNum; //启用日期
    //------------
    private String exchangeCode; //交易所代码
    private String exchangeName; //交易所名称
    private String brokerCode; //券商代码
    private String brokerName; //券商名称
    private String desc; //描述
    private String oldseatCode;
    private TradeSeatBean filterType;
    private String sRecycled = "";
    // private String oldStartDate;

    /**
     * parseRowStr
     * 解析席位设置数据
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
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.seatCode = reqAry[0];
            this.seatName = reqAry[1];
            this.Type = reqAry[2];
            this.seatType = reqAry[3];
            this.seatNum = reqAry[4];
            this.exchangeCode = reqAry[5];
            this.exchangeName = reqAry[6];
            this.brokerCode = reqAry[7];
            this.brokerName = reqAry[8];
            this.desc = reqAry[9];
            this.checkStateId = Integer.parseInt(reqAry[10]);
            this.oldseatCode = reqAry[11];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TradeSeatBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析席位设置请求信息出错", e);
        }

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.seatCode.trim()).append("\t");
        buffer.append(this.seatName.trim()).append("\t"); // lzp add 12.3
        buffer.append(this.Type.trim()).append("\t");
        buffer.append(this.seatType.trim()).append("\t");
        buffer.append(this.seatNum.trim()).append("\t");
        buffer.append(this.brokerCode.trim()).append("\t");
        buffer.append(this.brokerName.trim()).append("\t");
        buffer.append(this.exchangeCode.trim()).append("\t");
        buffer.append(this.exchangeName.trim()).append("\t");
        buffer.append(this.desc.trim()).append("\t");
        buffer.append(super.buildRecLog());
        return buffer.toString();

    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {

        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_TradeSeat"), "FSeatCode",
                               this.seatCode,
                               this.oldseatCode);
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
        String sHeader = "";
        String sShowDataStr = "";
        String sVocStr = ""; //词汇类型对照字符串
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

                setTradeSeatAttr(rs);

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
            sVocStr = vocabulary.getVoc(YssCons.YSS_TDS_SEATTYPE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取席位信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 此方法已被修改
     *修改时间：2008年
     * 修改人：单亮
     * 原方法的功能：查询出当前席位数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     */

    public String getListViewData1() throws YssException {
        String strSql = "";
        //修改前的代码
//      strSql = "select y.* from " +
//           "(select FSeatCode,FCheckState,FSeatNum from " + pub.yssGetTableName("Tb_Para_TradeSeat") + " " +
//           " where FCheckState <> 2 group by FSeatCode,FCheckState,FSeatNum) x join" +
//           " (select a.*, f.FVocName as FSeatTypeValue, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FExchangeName,e.FBrokerName from " + pub.yssGetTableName("Tb_Para_TradeSeat") + " a" +
//           " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
//           " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange where FCheckState = 1) d on a.FExchangeCode = d.FExchangeCode" +
//           " left join (select FBrokerCode,FBrokerName from " + pub.yssGetTableName("Tb_Para_Broker") + " where FCheckState = 1) e on a.FBrokerCode = e.FBrokerCode" +
//           " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
//           " left join Tb_Fun_Vocabulary f on a.FSeatType = f.FVocCode and f.FVocTypeCode = " +
//               dbl.sqlString(YssCons.YSS_TDS_SEATTYPE) +
//           buildFilterSql() +
//           ") y on x.FSeatCode = y.FSeatCode" +
//           " order by y.FSeatType,y.FCheckState, y.FCreateTime desc";
        //修改后的代码
        //edit by rujiangpeng 20100429 MS01105 QDV4国内（测试）2010年04月19日01_B  (distinct)
        strSql = "select y.* from " +
            "(select FSeatCode,FCheckState,FSeatNum from " + pub.yssGetTableName("Tb_Para_TradeSeat") + " " +
            " group by FSeatCode,FCheckState,FSeatNum) x join" +
            " (select a.*, f.FVocName as FSeatTypeValue, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FExchangeName,e.FBrokerName from " +
            pub.yssGetTableName("Tb_Para_TradeSeat") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange where FCheckState = 1) d on a.FExchangeCode = d.FExchangeCode" +
            " left join (select distinct FBrokerCode,FBrokerName from " + pub.yssGetTableName("Tb_Para_Broker") + " where FCheckState = 1) e on a.FBrokerCode = e.FBrokerCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join Tb_Fun_Vocabulary f on a.FSeatType = f.FVocCode and f.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_TDS_SEATTYPE) +
            buildFilterSql() +
            ") y on x.FSeatCode = y.FSeatCode" +
            " order by y.FSeatType,y.FCheckState, y.FCreateTime desc";

        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData4
     * 获取所有席位数据
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";

        strSql = "select a.*, f.FVocName as FSeatTypeValue, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FExchangeName,e.FBrokerName from " +
            pub.yssGetTableName("Tb_Para_TradeSeat") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange where FCheckState = 1) d on a.FExchangeCode = d.FExchangeCode" +
            " left join (select distinct FBrokerCode,FBrokerName from " + pub.yssGetTableName("Tb_Para_Broker") + "  where FCheckState = 1) e on a.FBrokerCode = e.FBrokerCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join Tb_Fun_Vocabulary f on a.FSeatType = f.FVocCode and f.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_TDS_SEATTYPE) +
            buildFilterSql() +
            " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * buildFilterSql
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.seatCode.length() != 0) {
                sResult = sResult + " and a.FSeatCode like '" +
                    filterType.seatCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.seatName.length() != 0) {
                sResult = sResult + " and a.FSeatName like '" +
                    filterType.seatName.replaceAll("'", "''") + "%'";
            }

            if (!this.filterType.Type.equalsIgnoreCase("99") && this.filterType.Type.length() != 0) { //2008-5-27 单亮将seatType改为type
                sResult = sResult + " and F.FVocName like '" + //2008-5-22  单亮
                    filterType.seatType.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.exchangeCode.length() != 0) {
                sResult = sResult + " and a.FExchangeCode like '" +
                    filterType.exchangeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.brokerCode.length() != 0) {
                sResult = sResult + " and a.FBrokerCode like '" +
                    filterType.brokerCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.seatNum.length() != 0) {
                sResult = sResult + " and a.FSeatNum like '" +
                    filterType.seatNum.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * getListViewData2
     * 获取席位设置数据
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
            sHeader = "席位代码\t席位名称";
            strSql = "select y.* from " +
                "(select FSeatCode,FSeatName from " + pub.yssGetTableName("Tb_Para_TradeSeat") +
                "  where FCheckState <> 2 group by FSeatCode,FSeatName,FCheckState,FSeatNum) x join" + //LZP modify 20080112 兼容DB2
                " (select a.*, f.FVocName as FSeatTypeValue, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FExchangeName,e.FBrokerName from " +
                pub.yssGetTableName("Tb_Para_TradeSeat") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange where FCheckState = 1) d on a.FExchangeCode = d.FExchangeCode" +
                " left join (select FBrokerCode,FBrokerName from " + pub.yssGetTableName("Tb_Para_Broker") + " where FCheckState = 1) e on a.FBrokerCode = e.FBrokerCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join Tb_Fun_Vocabulary f on a.FSeatType = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_TDS_SEATTYPE) +
                ") y on x.FSeatCode = y.FSeatCode  where y.FCheckState=1 " +
                " order by y.FSeatType, y.FCheckState, y.FCreateTime desc ";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FSeatCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FSeatName") + "").trim()).
                    append("\t").append(YssCons.YSS_LINESPLITMARK);

                setTradeSeatAttr(rs);
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
            throw new YssException("获取席位信息出错！", e);
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
     * @return IParaSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {

            strSql = " select * from " + pub.yssGetTableName("Tb_Para_TradeSeat") +
                " where FSeatCode=" + dbl.sqlString(this.seatCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.brokerCode = rs.getString("FBrokerCode");
            }
            return null;
        } catch (Exception e) {
            throw new YssException("获取席位信息出错!", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
           if (btOper == YssCons.OP_ADD) {
              strSql =
                    "insert into " + pub.yssGetTableName("Tb_Para_TradeSeat") + "(FSeatCode, FSeatType, FStartDate, " +
                    " FBrokerCode, FExchangeCode, FDesc, FCheckState, FCreator, FCreateTime,FCheckUser) " +
                    " values(" + dbl.sqlString(this.seatCode) + "," +
                    dbl.sqlString(this.seatType) + "," +
                    dbl.sqlDate(this.startDate) + "," +
                    dbl.sqlString(this.brokerCode) + "," +
                    dbl.sqlString(this.exchangeCode.length()==0 ? " " : this.exchangeCode) + "," +
                    dbl.sqlString(this.desc) + "," +
                    (pub.getSysCheckState()?"0":"1") + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
                    (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
           }
           else if (btOper == YssCons.OP_EDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_TradeSeat") + " set FSeatCode = " +
                    dbl.sqlString(this.seatCode) + ", FSeatType = "
                    + dbl.sqlString(this.seatType) + ",FStartDate = "
                    + dbl.sqlDate(this.startDate) + ", FBrokerCode = "
                    + dbl.sqlString(this.brokerCode) + ", FExchangeCode =" +
                    dbl.sqlString(this.exchangeCode.length()==0 ? " " : this.exchangeCode) + ", FDesc=" +
                    dbl.sqlString(this.desc) + ",FCheckState = " +
                    (pub.getSysCheckState()?"0":"1") + ",FCreator = " +
                    dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                    dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                    (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                    " where FSeatCode = " + dbl.sqlString(this.oldseatCode) +
                    " and FStartDate=" + dbl.sqlDate(this.oldStartDate);
           }
           else if (btOper == YssCons.OP_DEL) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_TradeSeat") + " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FSeatCode = " +
                    dbl.sqlString(this.seatCode) + " and FStartDate=" +
                    dbl.sqlDate(this.startDate);

           }
           else if (btOper == YssCons.OP_AUDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_TradeSeat") + " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FSeatCode = " +
                    dbl.sqlString(this.seatCode) + " and FStartDate=" +
                    dbl.sqlDate(this.startDate);
           }
           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("设置席位信息出错！", e);
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
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Para_TradeSeat") + "(FSeatCode,FSeatName, FSeatType, FSeatNum, " +
                " FBrokerCode, FExchangeCode, FDesc, FCheckState, FCreator, FCreateTime,FCheckUser) " +
                " values(" + dbl.sqlString(this.seatCode) + "," +
                dbl.sqlString(this.seatName) + "," +
                dbl.sqlString(this.Type) + "," +
                dbl.sqlString(this.seatNum) + "," +
                dbl.sqlString(this.brokerCode) + "," +
                dbl.sqlString(this.exchangeCode.length() == 0 ? " " : this.exchangeCode) + "," +
                dbl.sqlString(this.desc) + "," +
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
            throw new YssException("增加席位信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_TradeSeat") + " set FSeatCode = " +
                dbl.sqlString(this.seatCode) + ", FSeatName = "
                + dbl.sqlString(this.seatName) + ", FSeatType = "
                + dbl.sqlString(this.Type) + ",FseatNum = "
                + dbl.sqlString(this.seatNum) + ", FBrokerCode = "
                + dbl.sqlString(this.brokerCode) + ", FExchangeCode =" +
                dbl.sqlString(this.exchangeCode.length() == 0 ? " " : this.exchangeCode) + ", FDesc=" +
                dbl.sqlString(this.desc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FSeatCode = " + dbl.sqlString(this.oldseatCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改席位信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_TradeSeat") + " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FSeatCode = " +
                dbl.sqlString(this.seatCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除席位信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 此方法已被修改
     * 修改时间：2008年3月20号
     * 修改人：单亮
     * 原方法功能：只能处理审核和未审核的单条信息。
     * 新方法功能：可以处理审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 修改后不影响原方法的功能
     */

    public void checkSetting() throws YssException {
        Connection conn = null;
        String[] arrData = null;
        boolean bTrans = false;
        PreparedStatement stm = null;
        String strSql = "";

        try {
            conn = dbl.loadConnection();
            arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);
            bTrans = true;

            strSql = "update " + pub.yssGetTableName("Tb_Para_TradeSeat") + " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FSeatCode =? ";

            stm = dbl.openPreparedStatement(strSql);
            //循环执行这些更新语句
            conn.setAutoCommit(false);
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                stm.setString(1, this.seatCode);
                stm.executeUpdate();
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("审核席位信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     *
     * @param rs ResultSet
     * @throws SQLException
     */
    public void setTradeSeatAttr(ResultSet rs) throws SQLException {
        this.seatCode = rs.getString("FSeatCode") + "";
        this.seatName = rs.getString("FSeatName") + "";
        this.Type = rs.getString("FSeatType") + "";
        this.seatType = rs.getString("FSeatTypeValue") + "";
        this.seatNum = rs.getString("FSeatNum") + "";
        this.brokerCode = rs.getString("FBrokerCode") + "";
        this.brokerName = rs.getString("FBrokerName") + "";
        this.exchangeCode = rs.getString("FExchangeCode") + "";
        this.exchangeName = rs.getString("FExchangeName") + "";
        this.desc = rs.getString("FDesc") + "";
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
        TradeSeatBean befEditBean = new TradeSeatBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                "(select FSeatCode,FCheckState,FSeatName from " + pub.yssGetTableName("Tb_Para_TradeSeat") + " " +
                //edit by songjie 2011.06.16 修改席位设置数据保存报错 将 group by FSeatCode,FCheckState,FSeatNum 去掉
                " where FCheckState <> 2 ) x join" +
                " (select a.*, f.FVocName as FSeatTypeValue, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FExchangeName,e.FBrokerName from " +
                pub.yssGetTableName("Tb_Para_TradeSeat") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange where FCheckState = 1) d on a.FExchangeCode = d.FExchangeCode" +
                " left join (select FBrokerCode,FBrokerName from " + pub.yssGetTableName("Tb_Para_Broker") + " where FCheckState = 1) e on a.FBrokerCode = e.FBrokerCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join Tb_Fun_Vocabulary f on a.FSeatType = f.FVocCode and f.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_TDS_SEATTYPE) +
                " where  a.FSeatCode =" + dbl.sqlString(this.oldseatCode) +
                ") y on x.FSeatCode = y.FSeatCode" +
                " order by y.FSeatType,y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.seatCode = rs.getString("FSeatCode") + "";
                befEditBean.seatName = rs.getString("FSeatName") + "";
                befEditBean.Type = rs.getString("FSeatType") + "";
                befEditBean.seatType = rs.getString("FSeatTypeValue") + "";
                befEditBean.seatNum = rs.getString("FSeatNum") + "";
                befEditBean.brokerCode = rs.getString("FBrokerCode") + "";
                befEditBean.brokerName = rs.getString("FBrokerName") + "";
                befEditBean.exchangeCode = rs.getString("FExchangeCode") + "";
                befEditBean.exchangeName = rs.getString("FExchangeName") + "";
                befEditBean.desc = rs.getString("FDesc") + "";

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    /**
     * 从回收站中彻底删除数据
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        PreparedStatement stm = null;
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //根据规定的符号，把多个sql语句分别放入数组
            arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "delete from " + pub.yssGetTableName("Tb_Para_TradeSeat") + //Tb_Para_Currency什么意思
                " where FSeatCode = ?";
            //把sql语句付给PreparedStatement
            stm = dbl.openPreparedStatement(strSql);
            //循环执行这些删除语句
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                stm.setString(1, this.seatCode);
                stm.executeUpdate();
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
