package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 *
 * <p>Title: CurrencyBean </p>
 * <p>Description: 币种配置 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class CurrencyBean
    extends BaseDataSettingBean implements IDataSetting {
    private String curyCode = ""; //币种代码
    private String curyName = ""; //币种名称
    private String curySymbol = ""; //币种符号
    private String tradeInd = "1"; //是否可兑换
    private double factor = -1; //报价因子
    private int invertInd; //报价方向
    private double initRate = -1; //初始汇率
    private java.util.Date dtInitDate; //初始日期
    private String initTime = ""; //初始时间
    private String desc = ""; //币种描述
    private String portCode = ""; //组合代码
    private String assetGroupCode = "";//组合群代码 panjunfang add 20090903
    private String rateWay = ""; //汇率方式
    private CurrencyBean filterType;
    private String oldCuryCode;
    private String sRecycled = "";

    public String getRateWay() {
        return rateWay;
    }

    public void setRateWay(String rateWay) {
        this.rateWay = rateWay;
    }

    public String getTradeInd() {
        return tradeInd;
    }

    public java.util.Date getDtInitDate() {
        return dtInitDate;
    }

    public String getCuryName() {
        return curyName;
    }

    public String getDesc() {
        return desc;
    }

    public double getInitRate() {
        return initRate;
    }

    public CurrencyBean getFilterType() {
        return filterType;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public String getCurySymbol() {
        return curySymbol;
    }

    public int getInvertInd() {
        return invertInd;
    }

    public String getInitTime() {
        return initTime;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public void setTradeInd(String tradeInd) {
        this.tradeInd = tradeInd;
    }

    public void setDtInitDate(java.util.Date dtInitDate) {
        this.dtInitDate = dtInitDate;
    }

    public void setCuryName(String curyName) {
        this.curyName = curyName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setFilterType(CurrencyBean filterType) {
        this.filterType = filterType;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setCurySymbol(String curySymbol) {
        this.curySymbol = curySymbol;
    }

    public void setInvertInd(int invertInd) {
        this.invertInd = invertInd;
    }

    public void setInitTime(String initTime) {
        this.initTime = initTime;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setInitRate(double initRate) {
        this.initRate = initRate;
    }

    public void setOldCuryCode(String oldCuryCode) {
        this.oldCuryCode = oldCuryCode;
    }

    public double getFactor() {
        return factor;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getOldCuryCode() {
        return oldCuryCode;
    }

    public CurrencyBean() {
    }

    /**
     * parseRowStr
     * 解析币种配置
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
            this.curyCode = reqAry[0];
            this.curyName = reqAry[1];
            this.curySymbol = reqAry[2];
            this.tradeInd = reqAry[3];
            this.factor = Double.parseDouble(reqAry[4]);
            this.invertInd = Integer.parseInt(reqAry[5]);
            this.initRate = Double.parseDouble(reqAry[6]);
            this.dtInitDate = YssFun.toDate(reqAry[7]);
            this.initTime = reqAry[8];
            this.desc = reqAry[9];
            this.checkStateId = Integer.parseInt(reqAry[10]);
            this.oldCuryCode = reqAry[11];
            this.rateWay = reqAry[12];
            this.portCode = reqAry[13];
            this.assetGroupCode = reqAry[14];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CurrencyBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析币种配置请求出错", e);
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
        buf.append(this.curyCode).append("\t");
        buf.append(this.curyName).append("\t");
        buf.append(this.curySymbol).append("\t");
        buf.append(this.tradeInd).append("\t");
        buf.append(this.factor).append("\t");
        buf.append(this.invertInd).append("\t");
        buf.append(this.initRate).append("\t");
        buf.append(YssFun.formatDate(this.dtInitDate, YssCons.YSS_DATEFORMAT)).
            append("\t");
        buf.append(this.initTime).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.rateWay).append("\t"); // wdy add 20070822
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
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.curyCode.length() != 0) {
                sResult = sResult + " and a.FCuryCode like '" +
                    filterType.curyCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.curyName.length() != 0) {
                sResult = sResult + " and a.FCuryName like '" +
                    filterType.curyName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.curySymbol.length() != 0) {
                sResult = sResult + " and a.FCurySymbol like '" +
                    filterType.curySymbol.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.tradeInd.equalsIgnoreCase("99") &&
                this.filterType.tradeInd.length() != 0) {
                sResult = sResult + " and a.FTradeInd = " +
                    filterType.tradeInd;
            }
            if (this.filterType.factor != -1) {
                sResult = sResult + " and a.FFactor = " +
                    filterType.factor;
            }
            if (this.filterType.invertInd != 99) {
                sResult = sResult + " and a.FInvertInd = " +
                    filterType.invertInd;
            }
            if (this.filterType.initRate != -1) {
                sResult = sResult + " and a.FInitRate = " +
                    filterType.initRate;
            }
            if (this.filterType.dtInitDate != null &&
                !this.filterType.dtInitDate.equals(YssFun.toDate("9998-12-31"))) {
                sResult = sResult + " and a.FInitDate = " +
                    dbl.sqlDate(filterType.dtInitDate);
            }
            if (!this.filterType.initTime.equalsIgnoreCase("00:00:00")) {
                sResult = sResult + " and a.FInitTime = " +
                    dbl.sqlString(filterType.initTime);
            }
            //fangholn 20100409 MS01071 QDV4赢时胜(测试)2010年4月6日2_B
            if (!this.filterType.rateWay.equalsIgnoreCase("99")&&
                    !this.filterType.rateWay.equalsIgnoreCase("2") &&
                    this.filterType.rateWay.length() != 0 ) {
                    sResult = sResult + " and a.FrateWay = " +
                        filterType.rateWay;//增加汇率方式筛选条件
                }
            //-------------------end ----MS01071-------------------------------
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%' ";
            }
        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取币种配置信息
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
                "select a.*, b.FVocName as FTradeIndValue, c.FVocName as FInvertIndValue," +
                " d.FUserName as FCreatorName,e.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_Currency") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " left join Tb_Fun_Vocabulary b on " + dbl.sqlToChar("a.FTradeInd") + " = b.FVocCode and b.FVocTypeCode = " + //2007.11.29 修改 蒋锦 使用dbl.sqlToChar()处理"a.FTradeInd"，否则在使用DB2数据库时会报数据类型错误
                dbl.sqlString(YssCons.YSS_CRY_TRADEIND) +
                " left join Tb_Fun_Vocabulary c on " + dbl.sqlToChar("a.FInvertInd") + " = c.FVocCode and c.FVocTypeCode = " + //2007.11.29 修改 蒋锦 使用dbl.sqlToChar()处理"a.FInvertInd"，否则在使用DB2数据库时会报数据类型错误
                dbl.sqlString(YssCons.YSS_CRY_INVERTIND) +
                buildFilterSql() + " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setCurrencyCfgAttr(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_CRY_INVERTIND + "," +
                                        YssCons.YSS_CRY_RATEWAY + "," +
                                        YssCons.YSS_CRY_TRADEIND);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取币种配置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getListViewData2
     * 获取已审核的币种配置信息
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String sql = "";
        try {
            sHeader = "币种代码\t币种名称\t币种描述";
            if (pub.getPrefixTB().length() == 0) {
            	//--- #580 建信上线需提供部分方案支持 add by jiangshichao 2011.03.24
                //return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
            	sql = "select a.*, " +
                " d.FUserName as FCreatorName,e.FUserName as FCheckUserName from Tb_base_Currency a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                ( (buildFilterSql().length() == 0) ? " where " :
                 buildFilterSql() + " and ") +
                " FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";
            	//--- #580 建信上线需提供部分方案支持 add by jiangshichao 2011.03.24 
            }else{
            	sql = "select a.*, " +
                " d.FUserName as FCreatorName,e.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_Currency") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                ( (buildFilterSql().length() == 0) ? " where " :
                 buildFilterSql() + " and ") +
                " FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";
            }
           

            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FCuryCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FCuryName") + "").trim()).append(
                    "\t");
                bufShow.append(YssFun.left( (rs.getString("FDesc") + "").trim(), 40)).
                    append(YssCons.YSS_LINESPLITMARK);

                setCurrencyCfgAttr(rs);
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
            throw new YssException("获取可用币种配置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData3
     * 获取估值时需要的组合货币与基础货币信息
     * @return String
     */
    public String getListViewData3() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "币种代码\t币种名称\t币种描述";
            if (pub.getPrefixTB().length() == 0) {
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
            }
            String sql = "select a.*, " +
                " d.FUserName as FCreatorName,e.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_Currency") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " where a.FCuryCode = " + dbl.sqlString(pub.getPortBaseCury(this.portCode)) +//edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                " or a.FCuryCode in (select FPortCury from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FPortCode = " + dbl.sqlString(this.portCode) +
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
             /*   " and FStartDate = (select max(FStartDate) from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FPortCode = " + dbl.sqlString(this.portCode) + "))" +
                */
                //end by lidaolongb
                " and a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FCuryCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FCuryName") + "").trim()).append(
                    "\t");
                bufShow.append(YssFun.left( (rs.getString("FDesc") + "").trim(), 40)).
                    append(YssCons.YSS_LINESPLITMARK);

                setCurrencyCfgAttr(rs);
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
            throw new YssException("获取可用币种配置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Currency"),
                               "FCuryCode",
                               this.curyCode, this.oldCuryCode);
    }

    /**
     * saveSetting
     * 新增、修改、删除、审核
     * @param btOper byte
     */
    /*public void saveSetting(byte btOper) throws YssException {
       String strSql = "";
       boolean bTrans = false; //代表是否开始了事务
       Connection conn = dbl.loadConnection();
       try {
          if (btOper == YssCons.OP_ADD) {
             strSql = "insert into " + pub.yssGetTableName("Tb_Para_Currency") +
                   "" +
                   "(FCuryCode,FCuryName,FCurySymbol,FTradeInd,FFactor,FInvertInd," +
                   "FInitRate,FInitDate,FInitTime,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                   " values(" + dbl.sqlString(this.curyCode) + "," +
                   dbl.sqlString(this.curyName) + "," +
                   dbl.sqlString(this.curySymbol) + "," +
                   this.tradeInd + " ," +
                   this.factor + " ," +
                   this.invertInd + "," +
                   this.initRate + "," +
                   dbl.sqlDate(this.dtInitDate) + "," +
                   dbl.sqlString(this.initTime) + "," +
                   dbl.sqlString(this.desc) + "," +
                   (pub.getSysCheckState() ? "0" : "1") + "," +
                   dbl.sqlString(this.creatorCode) + "," +
                   dbl.sqlString(this.creatorTime) + "," +
                   (pub.getSysCheckState() ? "' '" :
                    dbl.sqlString(this.creatorCode)) + ")";
          }
          else if (btOper == YssCons.OP_EDIT) {
             strSql = "update " + pub.yssGetTableName("Tb_Para_Currency") +
                   " set FCuryCode = " +
                   dbl.sqlString(this.curyCode) + ", FCuryName = " +
                   dbl.sqlString(this.curyName) + ", FCurySymbol = " +
                   dbl.sqlString(this.curySymbol) + ", FTradeInd = " +
                   this.tradeInd + ", FFactor = " +
                   this.factor + ", FInvertInd = " +
                   this.invertInd + ", FInitRate = " +
                   this.initRate + ", FInitDate = " +
                   dbl.sqlDate(this.dtInitDate) + ", FInitTime = " +
                   dbl.sqlString(this.initTime) + ", FDesc = " +
                   dbl.sqlString(this.desc) + ",FCheckState = " +
                   (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                   dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                   dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                   (pub.getSysCheckState() ? "' '" :
                    dbl.sqlString(this.creatorCode)) +
                   " where FCuryCode = " +
                   dbl.sqlString(this.oldCuryCode);
          }
          else if (btOper == YssCons.OP_DEL) {
             strSql = "update " + pub.yssGetTableName("Tb_Para_Currency") +
                   " set FCheckState = " + this.checkStateId +
                   ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                   ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                   "'" +
                   " where FCuryCode = " + dbl.sqlString(this.curyCode);

          }
          else if (btOper == YssCons.OP_AUDIT) {
             strSql = "update " + pub.yssGetTableName("Tb_Para_Currency") +
                   " set FCheckState = " +
                   this.checkStateId + ", FCheckUser = " +
                   dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                   YssFun.formatDatetime(new java.util.Date()) + "'" +
                   " where FCuryCode = " + dbl.sqlString(this.curyCode);
          }
          conn.setAutoCommit(false);
          bTrans = true;
          dbl.executeSql(strSql);
          conn.commit();
          bTrans = false;
          conn.setAutoCommit(true);
       }
       catch (Exception e) {
          throw new YssException("更新币种配置信息出错", e);
       }
       finally {
          dbl.endTransFinal(conn, bTrans);
       }
        }
     */





    /**
     * saveSetting
     * 新增、修改、删除、审核
     * @param btOper byte
     */
    /*public void saveSetting(byte btOper) throws YssException {
       String strSql = "";
       boolean bTrans = false; //代表是否开始了事务
       Connection conn = dbl.loadConnection();
       try {
          if (btOper == YssCons.OP_ADD) {
             strSql = "insert into " + pub.yssGetTableName("Tb_Para_Currency") +
                   "" +
     "(FCuryCode,FCuryName,FCurySymbol,FTradeInd,FFactor,FInvertInd," +
                   "FInitRate,FInitDate,FInitTime,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                   " values(" + dbl.sqlString(this.curyCode) + "," +
                   dbl.sqlString(this.curyName) + "," +
                   dbl.sqlString(this.curySymbol) + "," +
                   this.tradeInd + " ," +
                   this.factor + " ," +
                   this.invertInd + "," +
                   this.initRate + "," +
                   dbl.sqlDate(this.dtInitDate) + "," +
                   dbl.sqlString(this.initTime) + "," +
                   dbl.sqlString(this.desc) + "," +
                   (pub.getSysCheckState() ? "0" : "1") + "," +
                   dbl.sqlString(this.creatorCode) + "," +
                   dbl.sqlString(this.creatorTime) + "," +
                   (pub.getSysCheckState() ? "' '" :
                    dbl.sqlString(this.creatorCode)) + ")";
          }
          else if (btOper == YssCons.OP_EDIT) {
             strSql = "update " + pub.yssGetTableName("Tb_Para_Currency") +
                   " set FCuryCode = " +
                   dbl.sqlString(this.curyCode) + ", FCuryName = " +
                   dbl.sqlString(this.curyName) + ", FCurySymbol = " +
                   dbl.sqlString(this.curySymbol) + ", FTradeInd = " +
                   this.tradeInd + ", FFactor = " +
                   this.factor + ", FInvertInd = " +
                   this.invertInd + ", FInitRate = " +
                   this.initRate + ", FInitDate = " +
                   dbl.sqlDate(this.dtInitDate) + ", FInitTime = " +
                   dbl.sqlString(this.initTime) + ", FDesc = " +
                   dbl.sqlString(this.desc) + ",FCheckState = " +
                   (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                   dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                   dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                   (pub.getSysCheckState() ? "' '" :
                    dbl.sqlString(this.creatorCode)) +
                   " where FCuryCode = " +
                   dbl.sqlString(this.oldCuryCode);
          }
          else if (btOper == YssCons.OP_DEL) {
             strSql = "update " + pub.yssGetTableName("Tb_Para_Currency") +
                   " set FCheckState = " + this.checkStateId +
                   ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
     ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                   "'" +
                   " where FCuryCode = " + dbl.sqlString(this.curyCode);

          }
          else if (btOper == YssCons.OP_AUDIT) {
             strSql = "update " + pub.yssGetTableName("Tb_Para_Currency") +
                   " set FCheckState = " +
                   this.checkStateId + ", FCheckUser = " +
                   dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                   YssFun.formatDatetime(new java.util.Date()) + "'" +
                   " where FCuryCode = " + dbl.sqlString(this.curyCode);
          }
          conn.setAutoCommit(false);
          bTrans = true;
          dbl.executeSql(strSql);
          conn.commit();
          bTrans = false;
          conn.setAutoCommit(true);
       }
       catch (Exception e) {
          throw new YssException("更新币种配置信息出错", e);
       }
       finally {
          dbl.endTransFinal(conn, bTrans);
       }
        }
     */
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
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_Currency") +
                "" +
                "(FCuryCode,FCuryName,FCurySymbol,FTradeInd,FFactor,FInvertInd," +
                "FInitRate,FInitDate,FInitTime,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FRateWay)" + // wdy add 20070821
                " values(" + dbl.sqlString(this.curyCode) + "," +
                dbl.sqlString(this.curyName) + "," +
                dbl.sqlString(this.curySymbol) + "," +
                this.tradeInd + " ," +
                this.factor + " ," +
                this.invertInd + "," +
                this.initRate + "," +
                dbl.sqlDate(this.dtInitDate) + "," +
                dbl.sqlString(this.initTime) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + "," +
                this.rateWay + ")"; // wdy add 20070821
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true); 
        }

        catch (Exception e) {
            throw new YssException("增加币种信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_Currency") +
                " set FCuryCode = " +
                dbl.sqlString(this.curyCode) + ", FCuryName = " +
                dbl.sqlString(this.curyName) + ", FCurySymbol = " +
                dbl.sqlString(this.curySymbol) + ", FTradeInd = " +
                this.tradeInd + ", FFactor = " +
                this.factor + ", FInvertInd = " +
                this.invertInd + ", FInitRate = " +
                this.initRate + ", FInitDate = " +
                dbl.sqlDate(this.dtInitDate) + ", FInitTime = " +
                dbl.sqlString(this.initTime) + ", FDesc = " +
                dbl.sqlString(this.desc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " , FRateWay = " + this.rateWay + " where FCuryCode = " + // wdy add 20070821
                dbl.sqlString(this.oldCuryCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
         
         
        }

        catch (Exception e) {
            throw new YssException("修改币种信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_Currency") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FCuryCode = " + dbl.sqlString(this.curyCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除币种信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        PreparedStatement stm = null;
        Connection conn = dbl.loadConnection();
        try {
            arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Para_Currency") +
                " set FCheckState = ?, FCheckUser = ?, FCheckTime = ?" +
                " where FCuryCode = ?";
            stm = dbl.openPreparedStatement(strSql);
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                stm.setInt(1, this.checkStateId);
                stm.setString(2, pub.getUserCode());
                stm.setString(3, YssFun.formatDatetime(new java.util.Date()));
                stm.setString(4, this.curyCode);
                stm.executeUpdate();
            }
            //strSql = "update " + pub.yssGetTableName("Tb_Para_Currency") +
            //" set FCheckState = " +
            //this.checkStateId + ", FCheckUser = " +
            //dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
            //YssFun.formatDatetime(new java.util.Date()) + "'" +
            //" where FCuryCode = " + dbl.sqlString(this.curyCode);
            // conn.setAutoCommit(false);
            // bTrans = true;
            // dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("审核币种信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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
     * 为各项变量赋值
     *
     */
    public void setCurrencyCfgAttr(ResultSet rs) throws SQLException,
        YssException {
        this.curyCode = rs.getString("FCuryCode") + "";
        this.curyName = rs.getString("FCuryName") + "";
        this.curySymbol = rs.getString("FCurySymbol") + "";
        this.tradeInd = rs.getString("FTradeInd") + "";
        this.factor = rs.getDouble("FFactor");
        this.invertInd = rs.getInt("FInvertInd");
        this.initRate = rs.getDouble("FInitRate");
        this.rateWay = rs.getString("FRateWay");
        if (rs.getDate("FInitDate") == null) {
            this.dtInitDate = YssFun.toDate("1900-01-01");
        } else {
            this.dtInitDate = rs.getDate("FInitDate");
        }
        if (rs.getString("FInitTime") == null) {
            this.initTime = "00:00:00";
        } else {
            this.initTime = rs.getString("FInitTime") + "";
        }
        this.desc = rs.getString("FDesc") + "";
        super.setRecLog(rs);
    }

    public void getCurySetting(String sCuryCode) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Para_Currency")
                + " where FCuryCode = " + dbl.sqlString(sCuryCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.curyCode = rs.getString("FCuryCode");
                this.curyName = rs.getString("FCuryName");
                this.curySymbol = rs.getString("FCurySymbol");
                this.tradeInd = rs.getString("FTradeInd");
                this.factor = rs.getInt("FFactor");
                this.invertInd = rs.getInt("FInvertInd");
                this.initRate = rs.getDouble("FInitRate");
                this.dtInitDate = rs.getDate("FInitDate");
                this.initTime = rs.getString("FInitTime");
                this.desc = rs.getString("FDesc");
            }
        } catch (Exception e) {
            throw new YssException("获取货币设置信息出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
        CurrencyBean befEditBean = new CurrencyBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select a.*, b.FVocName as FTradeIndValue, c.FVocName as FInvertIndValue," +
                " d.FUserName as FCreatorName,e.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Para_Currency") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCreator = d.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCheckUser = e.FUserCode" +
                " left join Tb_Fun_Vocabulary b on " + dbl.sqlToChar("a.FTradeInd") + " = b.FVocCode and b.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CRY_TRADEIND) +
                " left join Tb_Fun_Vocabulary c on " + dbl.sqlToChar("a.FInvertInd") + " = c.FVocCode and c.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_CRY_INVERTIND) +
                " where  a.FCuryCode =" + dbl.sqlString(this.oldCuryCode) +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.curyCode = rs.getString("FCuryCode") + "";
                befEditBean.curyName = rs.getString("FCuryName") + "";
                befEditBean.curySymbol = rs.getString("FCurySymbol") + "";
                befEditBean.tradeInd = rs.getString("FTradeInd") + "";
                befEditBean.factor = rs.getDouble("FFactor");
                befEditBean.invertInd = rs.getInt("FInvertInd");
                befEditBean.initRate = rs.getDouble("FInitRate");
                if (rs.getDate("FInitDate") == null) {
                    befEditBean.dtInitDate = YssFun.toDate("1900-01-01");
                } else {
                    befEditBean.dtInitDate = rs.getDate("FInitDate");
                }
                if (rs.getString("FInitTime") == null) {
                    befEditBean.initTime = "00:00:00";
                } else {
                    befEditBean.initTime = rs.getString("FInitTime") + "";
                }
                befEditBean.desc = rs.getString("FDesc") + "";

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        PreparedStatement stm = null;
        Connection conn = dbl.loadConnection();
        try {
            arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "delete from " + pub.yssGetTableName("Tb_Para_Currency") +
                " where FCuryCode = ?";
            stm = dbl.openPreparedStatement(strSql);
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                stm.setString(1, this.curyCode);
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
        String strRe = "";//存放返回到前台的字符串
        String sPrefixTB = pub.getPrefixTB(); //保存当前的组合群代码即表前缀
        try{
            pub.setPrefixTB(this.assetGroupCode);//将前台传过来的组合群代码设置为表前缀
            strRe = this.getListViewData2();//将该组合群对应的币种列表返回至前台
        }catch(Exception e){
            throw new YssException("获取可用币种配置信息出错", e);
        }finally{
            pub.setPrefixTB(sPrefixTB);//还原公共变的里的组合群代码即表前缀
        }
        return strRe;
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
