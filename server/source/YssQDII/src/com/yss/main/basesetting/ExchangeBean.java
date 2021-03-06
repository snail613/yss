package com.yss.main.basesetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 *
 * <p>Title: ExchangeBean</p>
 * <p>Description: 交易所设置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class ExchangeBean
    extends BaseDataSettingBean implements IDataSetting {
    private String eRecycled = ""; //回收站  add 20080811 bug:0000355问题2
    private String strExchangeCode = ""; //交易所代码
    private String strExchangeName = ""; //交易所名称
    private String strCountryCode = ""; //国家代码
    private String strCountryName = ""; //国家名称
    private String strRegionCode = ""; //地域代码
    private String strRegionName = ""; //地域名称
    private String strAreaCode = ""; //地区代码
    private String strAreaName = ""; //地区名称
    private String strDvpInd = "1"; //是否DVP结算模式
    private String strDesc = ""; //描述
    private String strHolidaysCode = ""; //节假日代码
    private String strHolidaysName = ""; //节假日名称
    private String strIsOnlyCounts = "0"; //初加载时是否显示
    private String oldExchangeCode;
    private String settleDays = "";
    private String settleDayType = "";
    private String settleDayTypeName = "";
    private String status = ""; //是否记入系统信息状态  lzp 11.29 add
    private String sRecycled = ""; //为增加还原和删除功能加的一个中介字符串变量 bug MS00149 QDV4南方2009年1月5日05_B 2009.01.14 方浩
    private ExchangeBean filterType;
    public String getStrRegionCode() {
        return strRegionCode;
    }

    public String getStrAreaCode() {
        return strAreaCode;
    }

    public String getStrExchangeName() {
        return strExchangeName;
    }

    public String getStrDvpInd() {
        return strDvpInd;
    }

    public String getStrExchangeCode() {
        return strExchangeCode;
    }

    public void setStrCountryCode(String strCountryCode) {
        this.strCountryCode = strCountryCode;
    }

    public void setStrRegionCode(String strRegionCode) {
        this.strRegionCode = strRegionCode;
    }

    public void setStrAreaCode(String strAreaCode) {
        this.strAreaCode = strAreaCode;
    }

    public void setStrExchangeName(String strExchangeName) {
        this.strExchangeName = strExchangeName;
    }

    public void setStrDvpInd(String strDvpInd) {
        this.strDvpInd = strDvpInd;
    }

    public void setStrExchangeCode(String strExchangeCode) {
        this.strExchangeCode = strExchangeCode;
    }

    public void setStrIsOnlyCounts(String strIsOnlyCounts) {
        this.strIsOnlyCounts = strIsOnlyCounts;
    }

    public void setStrRegionName(String strRegionName) {
        this.strRegionName = strRegionName;
    }

    public void setStrHolidaysName(String strHolidaysName) {
        this.strHolidaysName = strHolidaysName;
    }

    public void setStrHolidaysCode(String strHolidaysCode) {
        this.strHolidaysCode = strHolidaysCode;
    }

    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public void setStrCountryName(String strCountryName) {
        this.strCountryName = strCountryName;
    }

    public void setStrAreaName(String strAreaName) {
        this.strAreaName = strAreaName;
    }

    public void setSettleDays(String settleDays) {
        this.settleDays = settleDays;
    }

    public void setSettleDayType(String settleDayType) {
        this.settleDayType = settleDayType;
    }

    public void setOldExchangeCode(String oldExchangeCode) {
        this.oldExchangeCode = oldExchangeCode;
    }

    public void setFilterType(ExchangeBean filterType) {
        this.filterType = filterType;
    }

    public void setSettleDayTypeName(String settleDayTypeName) {
        this.settleDayTypeName = settleDayTypeName;
    }

    public String getStrCountryCode() {
        return strCountryCode;
    }

    public String getStrIsOnlyCounts() {
        return strIsOnlyCounts;
    }

    public String getStrRegionName() {
        return strRegionName;
    }

    public String getStrHolidaysName() {
        return strHolidaysName;
    }

    public String getStrHolidaysCode() {
        return strHolidaysCode;
    }

    public String getStrDesc() {
        return strDesc;
    }

    public String getStrCountryName() {
        return strCountryName;
    }

    public String getStrAreaName() {
        return strAreaName;
    }

    public String getSettleDays() {
        return settleDays;
    }

    public String getSettleDayType() {
        return settleDayType;
    }

    public String getOldExchangeCode() {
        return oldExchangeCode;
    }

    public ExchangeBean getFilterType() {
        return filterType;
    }

    public String getSettleDayTypeName() {
        return settleDayTypeName;
    }

    public String getHolidaysCode() {
        return strHolidaysCode;
    }

    public void setHolidaysCode(String strHolidaysCode) {
        this.strHolidaysCode = strHolidaysCode;
    }

    public ExchangeBean() {
    }

    /**
     * 解析交易所设置数据
     * @param sRowStr String
     * @throws YssException
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
            sRecycled = sRowStr; //bug MS00149 QDV4南方2009年1月5日05_B 2009.01.13 方浩
            reqAry = sTmpStr.split("\t");
            this.eRecycled = sRowStr; //add 20080811 bug:0000355问题2
            this.strExchangeCode = reqAry[0];
            this.strExchangeName = reqAry[1];
            this.strCountryCode = reqAry[2];
            this.strCountryName = reqAry[3];
            this.strAreaCode = reqAry[4];
            this.strAreaName = reqAry[5];
            this.strRegionCode = reqAry[6];
            this.strRegionName = reqAry[7];
            this.strDvpInd = reqAry[8];
            this.strHolidaysCode = reqAry[9];
            this.strDesc = reqAry[10];
            this.checkStateId = Integer.parseInt(reqAry[11]);
            this.oldExchangeCode = reqAry[12];
            //  this.strIsOnlyCounts =reqAry[13];
            this.settleDayType = reqAry[13];
            this.settleDays = reqAry[14];
            this.status = reqAry[15]; //lzp add 11.29
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new ExchangeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析交易所设置请求出错", e);
        }

    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.strIsOnlyCounts.equals("1")) {
                sResult = " and 1=2 ";
            }
            if (this.filterType.strExchangeCode.length() != 0) {
                sResult = sResult + " and a.FExchangeCode like '" +
                    filterType.strExchangeCode.replaceAll("'", "''") +
                    "%'";
            }
            if (this.filterType.strExchangeName.length() != 0) {
                sResult = sResult + " and a.FExchangeName like '" +
                    filterType.strExchangeName.replaceAll("'", "''") +
                    "%'";
            }
            if (this.filterType.strCountryCode.length() != 0) {
                sResult = sResult + " and a.FCountryCode = " +
                    dbl.sqlString(filterType.strCountryCode);
            }
            if (this.filterType.strRegionCode.length() != 0) {
                sResult = sResult + " and a.FRegionCode = " +
                    dbl.sqlString(filterType.strRegionCode);
            }
            if (this.filterType.strAreaCode.length() != 0) {
                sResult = sResult + " and a.FAreaCode = " +
                    dbl.sqlString(filterType.strAreaCode);
            }
            if (this.filterType.strHolidaysCode.length() != 0) {
                sResult = sResult + " and a.FHolidaysCode = " +
                    dbl.sqlString(filterType.strHolidaysCode);
            }
            if (!this.filterType.strDvpInd.equalsIgnoreCase("99") &&
                this.filterType.strDvpInd.length() != 0) {
                sResult = sResult + " and a.FDVPInd = " +
                    filterType.strDvpInd;
            }
            if (this.filterType.strDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.strDesc.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.settleDays.length() != 0 &&
                !this.filterType.settleDays.equals("0")) {
                sResult += " and FSettleDays =" + this.filterType.settleDays;
            }
            if (!this.filterType.settleDayType.equalsIgnoreCase("99")
                && this.filterType.settleDayType.length() != 0) { //MS00008 edit by 宋洁 2009-02-24
                sResult += " and fSettleDayType = " +
                    dbl.sqlString(this.filterType.settleDayType); //MS00008 edit by 宋洁 2009-02-24
            }
        }
        return sResult;
    }

    /**
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.strExchangeCode).append("\t");
        buffer.append(this.strExchangeName).append("\t");
        buffer.append(this.strCountryCode).append("\t");
        buffer.append(this.strCountryName).append("\t");
        buffer.append(this.strAreaCode).append("\t");
        buffer.append(this.strAreaName).append("\t");
        buffer.append(this.strRegionCode).append("\t");
        buffer.append(this.strRegionName).append("\t");
        buffer.append(this.strDvpInd).append("\t");
        buffer.append(this.strHolidaysCode).append("\t");
        buffer.append(this.strHolidaysName).append("\t");
        buffer.append(this.strDesc).append("\t");
        buffer.append(this.settleDayType).append("\t");
        buffer.append(this.settleDays).append("\t");
        buffer.append(this.settleDayTypeName).append("\t");
        buffer.append(super.buildRecLog());
        return buffer.toString();
    }

    /**
     * 数据验证
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, "Tb_Base_Exchange", "FExchangeCode",
                               this.strExchangeCode, this.oldExchangeCode);
    }

    /**
     * 新增、修改、删除、审核
     * @param btOper byte 操作类型
     * @throws YssException
     */
    /* public void saveSetting(byte btOper) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
           if (btOper == YssCons.OP_ADD) {
              strSql = "insert into Tb_Base_Exchange(FExchangeCode,FExchangeName,FCountryCode,FRegionCode,FAreaCode," +
     "FHolidaysCode,FDVPInd,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)"
                    + " values(" + dbl.sqlString(this.strExchangeCode) + ", " +
                    dbl.sqlString(this.strExchangeName) + ", " +
                    dbl.sqlString(this.strCountryCode.length() != 0 ? this.strCountryCode : " ") + ", " +
                    dbl.sqlString(this.strRegionCode) + ", " +
                    dbl.sqlString(this.strAreaCode) + "," +
                    dbl.sqlString(this.strHolidaysCode) + "," +
                    dbl.sqlString(this.strDvpInd) + "," +
                    dbl.sqlString(this.strDesc) + "," +
                    (pub.getSysCheckState() ? "0" : "1") + "," +
                    dbl.sqlString(this.creatorCode) + ", " +
                    dbl.sqlString(this.creatorTime) + "," +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) + ")";
           }
           else if (btOper == YssCons.OP_EDIT) {
              strSql = "update Tb_Base_Exchange set FExchangeCode = " +
                    dbl.sqlString(this.strExchangeCode) +
     ", FExchangeName = " + dbl.sqlString(this.strExchangeName) +
                    ", FCountryCode = " + dbl.sqlString(this.strCountryCode.length() != 0 ? this.strCountryCode : " ") +
                    ", FRegionCode = " + dbl.sqlString(this.strRegionCode) +
                    ",FAreaCode=" + dbl.sqlString(this.strAreaCode) +
                    ",FHolidaysCode = " + dbl.sqlString(this.strHolidaysCode) +
                    ",FDVPInd = " + dbl.sqlString(this.strDvpInd) +
                    ", FDesc = " + dbl.sqlString(this.strDesc) +
                    ",FCheckState = " + (pub.getSysCheckState() ? "0" : "1") +
                    ", FCreator = " + dbl.sqlString(this.creatorCode) +
                    ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                    ",FCheckUser = " +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlString(this.creatorCode)) +
                    " where FExchangeCode = " +
                    dbl.sqlString(this.oldExchangeCode);
           }
           else if (btOper == YssCons.OP_DEL) {
              strSql = "update Tb_Base_Exchange set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "' where FExchangeCode = " +
                    dbl.sqlString(this.strExchangeCode);
           }
           else if (btOper == YssCons.OP_AUDIT) {
              strSql = "update Tb_Base_Exchange set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "'" +
                    " where FExchangeCode = " +
                    dbl.sqlString(this.strExchangeCode);
           }
           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("更新交易所信息出错！", e);
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
            strSql = "insert into Tb_Base_Exchange(FExchangeCode,FExchangeName,FCountryCode,FRegionCode,FAreaCode," +
                "FHolidaysCode,FDVPInd,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FSettleDays,FSettleDayType)"
                + " values(" + dbl.sqlString(this.strExchangeCode) + ", " +
                dbl.sqlString(this.strExchangeName) + ", " +
                dbl.sqlString(this.strCountryCode.length() != 0 ?
                              this.strCountryCode : " ") + ", " +
                dbl.sqlString(this.strRegionCode) + ", " +
                dbl.sqlString(this.strAreaCode) + "," +
                dbl.sqlString(this.strHolidaysCode) + "," +
                this.strDvpInd + "," +
                dbl.sqlString(this.strDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + ", " +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + "," +
                this.settleDays + "," +
                this.settleDayType + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("新增-交易所设置");
                sysdata.setStrCode(this.strExchangeCode);
                sysdata.setStrName(this.strExchangeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增交易所信息出错", e);
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
        /*     String strSql = "";
             boolean bTrans = false; //代表是否开始了事务
             Connection conn = dbl.loadConnection();
             try {
                strSql = "update Tb_Base_Exchange set FCheckState = " +
                      this.checkStateId +
                      ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
         ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                      "'" +
                      " where FExchangeCode = " +
                      dbl.sqlString(this.strExchangeCode);

                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);
                //---------lzp add 11.29
             if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                      funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                if(this.checkStateId==1){
                   sysdata.setStrFunName("审核-交易所信息");
               }else{
                    sysdata.setStrFunName("反审核-交易所信息");
               }

                sysdata.setStrCode(this.strExchangeCode);
                sysdata.setStrName(this.strExchangeName);
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
                throw new YssException("审核交易所信息出错", e);
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
                    strSql = "update Tb_Base_Exchange set FCheckState = " +
                        this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "'" +
                        " where FExchangeCode = " +
                        dbl.sqlString(this.strExchangeCode); //更新数据的SQL语句

                    dbl.executeSql(strSql); //执行更新操作
                }
            }
            //如果sRecycled为空，而strExchangeCode不为空，则按照strExchangeCode来执行sql语句
            else if (strExchangeCode != null && (!strExchangeCode.equalsIgnoreCase("")) ) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                strSql = "update Tb_Base_Exchange set FCheckState = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "'" +
                    " where FExchangeCode = " +
                    dbl.sqlString(this.strExchangeCode); //更新数据的SQL语句

                dbl.executeSql(strSql); //执行更新操作
            }
            if (this.status.equalsIgnoreCase("1")) { //判断status是否等于1,当传入1的时候就记录系统的信息状态
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub); //设置pub的值
                sysdata.setStrAssetGroupCode("Common"); //设置StrAssetGroupCode的值
                if (this.checkStateId == 1) { //如果checkStateId==1就是它要的状态是审核状态
                    sysdata.setStrFunName("审核-交易所信息"); //设置StrFunName的值
                } else {
                    sysdata.setStrFunName("反审核-交易所信息"); //设置StrFunName的值
                }

                sysdata.setStrCode(this.strExchangeCode); //设置StrCode的值
                sysdata.setStrName(this.strExchangeName); //设置StrName的值
                sysdata.setStrUpdateSql(strSql); //设置StrUpdateSql的值
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting(); //把这些以上数据添加到系统数据表Tb_Fun_SysData
            }
            //-----------------------

            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核交易所信息出错", e);
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
            strSql = "update Tb_Base_Exchange set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "' where FExchangeCode = " +
                dbl.sqlString(this.strExchangeCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-交易所设置");
                sysdata.setStrCode(this.strExchangeCode);
                sysdata.setStrName(this.strExchangeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除交易所信息出错", e);
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
            strSql = "update Tb_Base_Exchange set FExchangeCode = " +
                dbl.sqlString(this.strExchangeCode) +
                ", FExchangeName = " + dbl.sqlString(this.strExchangeName) +
                ", FCountryCode = " +
                dbl.sqlString(this.strCountryCode.length() != 0 ?
                              this.strCountryCode : " ") +
                ", FRegionCode = " + dbl.sqlString(this.strRegionCode) +
                ",FAreaCode=" + dbl.sqlString(this.strAreaCode) +
                ",FHolidaysCode = " + dbl.sqlString(this.strHolidaysCode) +
                ",FDVPInd = " + this.strDvpInd + // lzp add 12.6  用db2时候就有问题去掉了转换  ORACLE中也为NUMBER型
                ", FDesc = " + dbl.sqlString(this.strDesc) +
                ",FCheckState = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCreator = " + dbl.sqlString(this.creatorCode) +
                ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ",FSettleDays=" + this.settleDays +
                ",fSettleDayType=" + this.settleDayType +
                ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FExchangeCode = " +
                dbl.sqlString(this.oldExchangeCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("修改-交易所设置");
                sysdata.setStrCode(this.strExchangeCode);
                sysdata.setStrName(this.strExchangeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新交易所信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

    public String saveMliSetting(String sMutilRowStr) {
        return "";
    }

    public IDataSetting getSetting() {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from Tb_Base_Exchange" +
                " where FExchangeCode = " +
                dbl.sqlString(this.strExchangeCode);

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.strExchangeCode = rs.getString("FExchangeCode") + "";
                this.strExchangeName = rs.getString("fexchangename")+"";//STORY #863 香港、美国股指期权交易区别  期权需求变更  add by jiangshichao 2011.06.22
                this.strCountryCode = rs.getString("FCountryCode") + "";
                this.strRegionCode = rs.getString("FRegionCode") + "";
                this.strAreaCode = rs.getString("FAreaCode") + "";
                this.strDvpInd = rs.getString("FDvpInd") + "";
                this.strDesc = rs.getString("FDesc") + "";
                this.strHolidaysCode = rs.getString("FHolidaysCode");
                this.settleDays = rs.getString("FSettleDays");
                this.settleDayType = rs.getString("FSettleDayType");
                this.checkStateId = rs.getInt("FCheckState");
                this.checkStateName = YssFun.getCheckStateName(rs.getInt(
                    "FCheckState"));
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FcreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime") + "";
            }
        } catch (Exception e) {
            throw new YssException("获取交易所信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            return null;
        }
    }

    public String getAllSetting() {
        return "";
    }

    public String getPartSetting() {
        return "";
    }

    /**
     * 获取交易所数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.*, m.FVocName as FDVPIndValue,n.FVocName as FSettleDayTypeName, b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
                " d.FCountryName, e.FRegionName, f.FAreaName ,g.FHolidaysName from Tb_Base_Exchange a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCountryCode,FCountryName from Tb_Base_Country where FCheckState = 1) d on a.FCountryCode = d.FCountryCode" +
                " left join (select FRegionCode,FRegionName from Tb_Base_Region where FCheckState = 1) e on a.FRegionCode = e.FRegionCode" +
                " left join (select FAreaCode,FAreaName from Tb_Base_Area where FCheckState = 1) f on a.FAreaCode = f.FAreaCode" +
                " left join (select FHolidaysCode,FHolidaysName from Tb_Base_Holidays where FCheckState = 1) g on a.FHolidaysCode = g.FHolidaysCode" +
                " left join Tb_Fun_Vocabulary m on " +
                dbl.sqlToChar("a.FDVPInd") +
                " = m.FVocCode and m.FVocTypeCode = " + //2007.11.29 修改 蒋锦 使用dbl.sqlToChar()处理"a.FDVPInd"，否则在使用DB2数据库时会报数据类型错误
                dbl.sqlString(YssCons.YSS_ECG_DVPIND) +
                " left join Tb_Fun_Vocabulary n on " +
                dbl.sqlToChar("a.FSettleDayType") +
                " = n.FVocCode and n.FVocTypeCode = " + //2007.11.29 修改 蒋锦 使用dbl.sqlToChar()处理"a.FSettleDayType"，否则在使用DB2数据库时会报数据类型错误
                dbl.sqlString(YssCons.YSS_SCY_SDAYTYPE) +
                buildFilterSql() +
                " order by a.FCheckState, a.FCreateTime desc"; // wdy modify 20070830
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.strExchangeCode = rs.getString("FExchangeCode") + "";
                this.strExchangeName = rs.getString("FExchangeName") + "";
                this.strCountryCode = rs.getString("FCountryCode") + "";
                this.strCountryName = rs.getString("FCountryName") + "";
                this.strRegionCode = rs.getString("FRegionCode") + "";
                this.strRegionName = rs.getString("FRegionName") + "";
                this.strAreaCode = rs.getString("FAreaCode") + "";
                this.strAreaName = rs.getString("FAreaName") + "";
                this.strHolidaysCode = rs.getString("FHolidaysCode") + "";
                this.strHolidaysName = rs.getString("FHolidaysName") + "";
                this.settleDays = rs.getString("FSettleDays");
                this.settleDayType = rs.getString("FSettleDayType");
                this.settleDayTypeName = rs.getString("FsettleDayTypeName");
                this.strDvpInd = rs.getString("FDVPInd") + "";
                this.strDesc = rs.getString("FDesc") + "";
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

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_ECG_DVPIND + "," +
                                        YssCons.YSS_SCY_SDAYTYPE);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取交易所信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取已经审核的交易所数据
     * @throws YssException
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
            sHeader = "交易所代码\t交易所名称";
            strSql = "select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FCountryName, e.FRegionName, f.FAreaName, g.FHolidaysName from Tb_Base_Exchange a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCountryCode,FCountryName from Tb_Base_Country where FCheckState = 1) d on a.FCountryCode = d.FCountryCode" +
                " left join (select FRegionCode,FRegionName from Tb_Base_Region where FCheckState = 1) e on a.FRegionCode = e.FRegionCode" +
                " left join (select FAreaCode,FAreaName from Tb_Base_Area where FCheckState = 1) f on a.FAreaCode = f.FAreaCode" +
                " left join (select FHolidaysCode,FHolidaysName from Tb_Base_Holidays where FCheckState = 1) g on a.FHolidaysCode = g.FHolidaysCode" +
                " where a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FExchangeCode") + "").trim()).
                    append(
                        "\t");
                bufShow.append( (rs.getString("FExchangeName") + "").trim()).
                    append(
                        YssCons.YSS_LINESPLITMARK);

                this.strExchangeCode = rs.getString("FExchangeCode") + "";
                this.strExchangeName = rs.getString("FExchangeName") + "";
                this.strCountryCode = rs.getString("FCountryCode") + "";
                this.strCountryName = rs.getString("FCountryName") + "";
                this.strRegionCode = rs.getString("FRegionCode") + "";
                this.strRegionName = rs.getString("FRegionName") + "";
                this.strAreaCode = rs.getString("FAreaCode") + "";
                this.strAreaName = rs.getString("FAreaName") + "";
                this.strHolidaysCode = rs.getString("FHolidaysCode") + "";
                this.strHolidaysName = rs.getString("FHolidaysName") + "";
                this.strDvpInd = rs.getString("FDVPInd") + "";
                this.strDesc = rs.getString("FDesc") + "";
                this.settleDays = rs.getString("FSettleDays");
                this.settleDayType = rs.getString("FSettleDayType");
                // this.settleDayTypeName = rs.getString("FSettleDayTypeName");
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
            throw new YssException("获取可用交易所信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    //根据交易所获取结转天数,及结转天数类型;
    public String getListViewData3() throws YssException {
        String sShowDataStr = "";
        // String sAllDataStr = "";
        String strSql = "";
        //  String sVocStr="";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        //   StringBuffer bufAll = new StringBuffer();
        try {
            strSql = "select * from Tb_Base_Exchange " +
                " where FExchangeCode =" +
                dbl.sqlString(this.strExchangeCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FSettleDayType")).append("\t");
                bufShow.append(rs.getString("FSettleDays")).append("\t").
                    append(YssCons.YSS_LINESPLITMARK);
                // bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            return sShowDataStr;
        } catch (Exception e) {
            throw new YssException("获取交易所信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getTreeViewData1() {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() {
        return "";
    }

    public String saveMutliSetting(String sMutilRowStr) {
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
        ExchangeBean befEditBean = new ExchangeBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*, m.FVocName as FDVPIndValue,n.FVocName as FSettleDayTypeName, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FCountryName, e.FRegionName, f.FAreaName ,g.FHolidaysName from Tb_Base_Exchange a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCountryCode,FCountryName from Tb_Base_Country where FCheckState = 1) d on a.FCountryCode = d.FCountryCode" +
                " left join (select FRegionCode,FRegionName from Tb_Base_Region where FCheckState = 1) e on a.FRegionCode = e.FRegionCode" +
                " left join (select FAreaCode,FAreaName from Tb_Base_Area where FCheckState = 1) f on a.FAreaCode = f.FAreaCode" +
                " left join (select FHolidaysCode,FHolidaysName from Tb_Base_Holidays where FCheckState = 1) g on a.FHolidaysCode = g.FHolidaysCode" +
                " left join Tb_Fun_Vocabulary m on " +
                dbl.sqlToChar("a.FDVPInd") +
                " = m.FVocCode and m.FVocTypeCode = " + // lzp 修改
                dbl.sqlString(YssCons.YSS_ECG_DVPIND) +
                " left join Tb_Fun_Vocabulary n on " +
                dbl.sqlToChar("a.FSettleDayType") +
                " = n.FVocCode and n.FVocTypeCode = " + // lzp 修改
                dbl.sqlString(YssCons.YSS_SCY_SDAYTYPE) +
                " where a.fexchangecode=" +
                dbl.sqlString(this.oldExchangeCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.strExchangeCode = rs.getString("FExchangeCode") +
                    "";
                befEditBean.strExchangeName = rs.getString("FExchangeName") +
                    "";
                befEditBean.strCountryCode = rs.getString("FCountryCode") + "";
                befEditBean.strCountryName = rs.getString("FCountryName") + "";
                befEditBean.strRegionCode = rs.getString("FRegionCode") + "";
                befEditBean.strRegionName = rs.getString("FRegionName") + "";
                befEditBean.strAreaCode = rs.getString("FAreaCode") + "";
                befEditBean.strAreaName = rs.getString("FAreaName") + "";
                befEditBean.strHolidaysCode = rs.getString("FHolidaysCode") +
                    "";
                befEditBean.strHolidaysName = rs.getString("FHolidaysName") +
                    "";
                befEditBean.strDvpInd = rs.getString("FDVPInd") + "";
                befEditBean.strDesc = rs.getString("FDesc") + "";
                befEditBean.settleDays = rs.getString("FSettleDays");
                befEditBean.settleDayType = rs.getString("FSettleDayType");
                befEditBean.settleDayTypeName = rs.getString(
                    "FSettleDayTypeName");
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * deleteRecycleData方法
     * 功能：实现回收站中的“清除”功能，将数据从数据库中删除。
     * @throws YssException
     * Time: 2008-08-11
     * Creator: Mao Qiwen

        public void deleteRecycleData() throws YssException{
       String strSql = "";
       String[] arrData = null;
       boolean bTrans = false;
       Connection conn = dbl.loadConnection();
       try
       {
          //判断eRecycled是否为空，不为空就按eRecycled解析的字符串来执行SQL语句
          if(eRecycled != null && eRecycled.length()!= 0)
          {
             //按照规定的解析规则，对数据进行解析
             arrData = eRecycled.split("\r\n");
             conn.setAutoCommit(false);
             bTrans = true;
             for(int i=0;i < arrData.length;i++){
                if(arrData[i].length() == 0)
                   continue;
                this.parseRowStr(arrData[i]);
     strSql = "delete from " + pub.yssGetTableName("Tb_Base_Exchange") +
     " where FExchangeCode=" + dbl.sqlString(this.strExchangeCode);
                dbl.executeSql(strSql);
             }
             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
          }
       }
       catch (Exception e) {
          throw new YssException("清除数据出错", e);
       }
       finally {
          dbl.endTransFinal(conn, bTrans);
       }
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
                        pub.yssGetTableName("Tb_Base_Exchange") +
                        " where FExchangeCode = " +
                        dbl.sqlString(this.strExchangeCode);

                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而strExchangeCode不为空，则按照strExchangeCode来执行sql语句
            else if (strExchangeCode != "" && strExchangeCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Base_Exchange") +
                    " where FExchangeCode = " +
                    dbl.sqlString(this.strExchangeCode);

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
