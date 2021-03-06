package com.yss.main.taoperation;

import java.sql.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class TaCashAccLinkBean
    extends BaseDataSettingBean implements IDataSetting {
    private String sellNetCode = ""; //销售网点代码
    private String sellNetName = "";

    private String portClsCode = ""; //组合分级代码
    private String portClsName = "";

    private String portCode = ""; //组合代码
    private String portName = "";

    private String sellTypeCode = ""; //销售类型代码
    private String sellTypeName = "";

    private String curyCode = ""; //货币
    private String curyName = "";

    private String cashAccCode = ""; //现金账户
    private String cashAccName = "";

    private String startDate = ""; //启用日期
    private String desc = "";

    private String oldSellNetCode = "";
    private String oldPortClsCode = "";
    private String oldPortCode = "";
    private String oldSellTypeCode = "";
    private String oldCuryCode = "";
    private Date oldStartDate; //BugNo:0000344 edit by jc

    private String sRecycled = null; //保存未解析前的字符串

    private TaCashAccLinkBean filterType;
    public String getPortClsName() {
        return portClsName;
    }

    public String getCuryName() {
        return curyName;
    }

    public String getDesc() {
        return desc;
    }

    public String getPortName() {
        return portName;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getSellNetCode() {
        return sellNetCode;
    }

    public String getCashAccCode() {
        return cashAccCode;
    }

    public TaCashAccLinkBean getFilterType() {
        return filterType;
    }

    public String getCashAccName() {
        return cashAccName;
    }

    public String getSellTypeCode() {
        return sellTypeCode;
    }

    public String getPortClsCode() {
        return portClsCode;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public String getSellTypeName() {
        return sellTypeName;
    }

    public String getOldSellNetCode() {
        return oldSellNetCode;
    }

    public String getOldSellTypeCode() {
        return oldSellTypeCode;
    }

    public String getOldPortClsCode() {
        return oldPortClsCode;
    }

    public String getOldPortCode() {
        return oldPortCode;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getOldCuryCode() {
        return oldCuryCode;
    }

    public void setSellNetName(String sellNetName) {
        this.sellNetName = sellNetName;
    }

    public void setPortClsName(String portClsName) {
        this.portClsName = portClsName;
    }

    public void setCuryName(String curyName) {
        this.curyName = curyName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setPortTypeName(String portName) {
        this.portName = portName;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setSellNetCode(String sellNetCode) {
        this.sellNetCode = sellNetCode;
    }

    public void setCashAccCode(String cashAccCode) {
        this.cashAccCode = cashAccCode;
    }

    public void setFilterType(TaCashAccLinkBean filterType) {
        this.filterType = filterType;
    }

    public void setCashAccName(String cashAccName) {
        this.cashAccName = cashAccName;
    }

    public void setSellTypeCode(String sellTypeCode) {
        this.sellTypeCode = sellTypeCode;
    }

    public void setPortClsCode(String portClsCode) {
        this.portClsCode = portClsCode;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setSellTypeName(String sellTypeName) {
        this.sellTypeName = sellTypeName;
    }

    public void setOldSellNetCode(String oldSellNetCode) {
        this.oldSellNetCode = oldSellNetCode;
    }

    public void setOldSellTypeCode(String oldSellTypeCode) {
        this.oldSellTypeCode = oldSellTypeCode;
    }

    public void setOldPortClsCode(String oldPortClsCode) {
        this.oldPortClsCode = oldPortClsCode;
    }

    public void setOldPortCode(String oldPortCode) {
        this.oldPortCode = oldPortCode;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setOldCuryCode(String oldCuryCode) {
        this.oldCuryCode = oldCuryCode;
    }

    public void setOldStartDate(Date oldStartDate) {
        this.oldStartDate = oldStartDate;
    }

    public String getSellNetName() {
        return sellNetName;
    }

    public Date getOldStartDate() {
        return oldStartDate;
    }

    //------------------------------------------------------------------------------
    public TaCashAccLinkBean() {
    }

//------------------------------------------------------------------------------
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_TA_CashAccLink"),
                               "FSellNetCode,FPortClsCode,FPortCode,FSellTypeCode,FCuryCode,FStartDate",
                               this.sellNetCode + "," + this.portClsCode + "," +
                               this.portCode + "," + this.sellTypeCode + "," +
                               this.curyCode + "," + this.startDate, //BugNo:0000344 edit by jc
                               this.oldSellNetCode + "," + this.oldPortClsCode + "," +
                               this.oldPortCode + "," + this.oldSellTypeCode + "," +
                               this.oldCuryCode + "," +
                               YssFun.formatDate(oldStartDate, "yyyy-MM-dd")); //BugNo:0000344 edit by jc
    }

    public String addSetting() throws YssException {
        Connection con = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务

        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_TA_CashAccLink") +
                "(FSellNetCode,FPortClsCode,FPortCode,FSellTypeCode,FCuryCode,FCashAccCode," +
                " FDesc,FStartDate," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(" +
                dbl.sqlString(this.sellNetCode.trim().length() == 0 ? " " :
                              this.sellNetCode) + "," +
                dbl.sqlString(this.portClsCode.trim().length() == 0 ? " " :
                              this.portClsCode) + "," +
                dbl.sqlString(this.portCode.trim().length() == 0 ? " " :
                              this.portCode) + "," +
                dbl.sqlString(this.sellTypeCode.trim().length() == 0 ? " " :
                              this.sellTypeCode) + "," +
                dbl.sqlString(this.curyCode.trim().length() == 0 ? " " :
                              this.curyCode) + "," +
                dbl.sqlString(this.cashAccCode.trim().length() == 0 ? " " :
                              this.cashAccCode) + "," +
                dbl.sqlString(this.desc) + "," +
                dbl.sqlDate(this.startDate) + "," +
                (pub.getSysCheckState() ? 0 : 1) + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                dbl.sqlString( (pub.getSysCheckState() ? " " : this.creatorCode)) +
                ")";
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增TA现金账户链接设置信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";

    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_TA_CashAccLink") +
                " set FPortClsCode = " +
                dbl.sqlString(this.portClsCode) + ", FsellNetCode = " +
                dbl.sqlString(this.sellNetCode) + ", FportCode = " +
                dbl.sqlString(this.portCode) + ", FsellTypeCode = " +
                dbl.sqlString(this.sellTypeCode) + ", FcuryCode = " +
                dbl.sqlString(this.curyCode) + ", FcashAccCode = " +
                dbl.sqlString(this.cashAccCode) + ", FDesc = " +
                dbl.sqlString(this.desc) + ", FStartDate = " +
                dbl.sqlDate(this.startDate) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                //   " where FPortClsCode = " +
                //    dbl.sqlString(this.oldPortClsCode);                             //20071123   chenyibo  根据主键做修改
                " where FSellNetCode = " + dbl.sqlString(this.oldSellNetCode) +
                " and FPortClsCode = " + dbl.sqlString(this.oldPortClsCode) +
                " and FPortCode = " + dbl.sqlString(this.oldPortCode) +
                " and FSellTypeCode = " + dbl.sqlString(this.oldSellTypeCode) +
                " and FCuryCode = " + dbl.sqlString(this.oldCuryCode) +
                " and FStartDate = " + dbl.sqlDate(this.oldStartDate);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新TA现金账户链接设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";

    }

    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {

            strSql = "update " + pub.yssGetTableName("Tb_TA_CashAccLink") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FSellNetCode = " + dbl.sqlString(this.oldSellNetCode) +
                " and FPortClsCode = " + dbl.sqlString(this.oldPortClsCode) +
                " and FPortCode = " + dbl.sqlString(this.oldPortCode) +
                " and FSellTypeCode = " + dbl.sqlString(this.oldSellTypeCode) +
                " and FCuryCode = " + dbl.sqlString(this.oldCuryCode) +
                " and FStartDate=" + dbl.sqlDate(this.oldStartDate);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除TA现金账户链接设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public void checkSetting() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
			/**shashijie 2012-7-2 STORY 2475 */
            if (sRecycled != null || !sRecycled.equalsIgnoreCase("")) {
			/**end*/
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_TA_CashAccLink")
                        + " set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FSellNetCode = " + dbl.sqlString(this.sellNetCode) +
                        " and FPortClsCode = " + dbl.sqlString(this.portClsCode) +
                        " and FPortCode = " + dbl.sqlString(this.portCode) +
                        " and FSellTypeCode = " + dbl.sqlString(this.sellTypeCode) +
                        " and FCuryCode = " + dbl.sqlString(this.curyCode) +
                        " and FStartDate = " + dbl.sqlDate(this.startDate);
                    dbl.executeSql(strSql);

                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核TA现金账户链接设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select FCashAccCode from " +
                pub.yssGetTableName("Tb_TA_CashAccLink") +
                " where (FSellNetCode = " + dbl.sqlString(this.sellNetCode) +
                " or FsellNetCode=' ')" +
                " and (FPortClsCode = " + dbl.sqlString(this.portClsCode) +
                " or FPortClsCode =' ')" +
                " and (FPortCode = " + dbl.sqlString(this.portCode) +
                " or FPortCode =' ')" +
                " and (FSellTypeCode = " + dbl.sqlString(this.sellTypeCode) +
                " or FSellTypeCode=' ')" +
                " and (FCuryCode = " + dbl.sqlString(this.curyCode) +
                " or FCuryCode=' ')";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.cashAccCode = rs.getString("FCashAccCode");
            }
            return null;
        } catch (Exception e) {
            throw new YssException("获取现金帐户信息出错", e);
        }
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
    }

    public String getListViewData1() throws YssException {
        String strSql =
            "select distinct y.* from " +
            "(select * from " +
            pub.yssGetTableName("Tb_TA_CashAccLink") + " " +
//            " where FCheckState <> 2) x join" +   修改 邱健， 为了让回收站内能有数据显示
            " ) x join" +
            " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FSellNetName as FSellNetName,e.FPortClsName as FPortClsName, " +
            "  f.FPortName as FPortName,g.FSellTypeName as FSellTypeName,h.FCuryName as FCuryName,i.FCashAccName as FCashAccName  from " +
            pub.yssGetTableName("Tb_TA_CashAccLink") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FSellNetCode,FSellNetName from " +
            pub.yssGetTableName("Tb_TA_SellNet") +
            " where FCheckState = 1 ) d on a.FSellNetCode = d.FSellNetCode" +
            " left join (select FPortClsCode,FPortClsName from " +
            pub.yssGetTableName("Tb_TA_PortCls") +
            " where FCheckState = 1 ) e on a.FPortClsCode = e.FPortClsCode" +
            " left join (select FPortCode,FPortName from " +
            pub.yssGetTableName("Tb_Para_Portfolio") +
            " where FCheckState = 1 ) f on f.FPortCode= a.FPortCode " +
//            " left join (select FPortTypeCode,FPortTypeName from " +   pub.yssGetTableName("Tb_TA_PortType") +
//            " ) f on a.FPortTypeCode = f.FPortTypeCode" +
            " left join (select FSellTypeCode,FSellTypeName from " +
            pub.yssGetTableName("Tb_TA_SellType") +
            " where FCheckState = 1 ) g on a.FSellTypeCode = g.FSellTypeCode" +
            " left join (select FCuryCode,FCuryName from " +
            pub.yssGetTableName("Tb_Para_Currency") +
            " where FCheckState = 1 ) h on a.FCuryCode = h.FCuryCode" +
            " left join (select FCashAccCode,FCashAccName from " +
            pub.yssGetTableName("Tb_Para_CashAccount") +
            " where FCheckState = 1 ) i on a.FCashAccCode = i.FCashAccCode" +
            buildFilterSql() +
            " )y on y.FSellNetCode=x.FSellNetCode and y.FPortClsCode = x.FPortClsCode and y.FPortCode = x.FPortCode and y.FSellTypeCode = x.FSellTypeCode and y.FCuryCode = x.FCuryCode " +
            " order by y.FSellNetCode,y.FPortCode,y.FPortClsCode,y.FSellTypeCode,y.FCuryCode";
        return builderListViewData(strSql);

    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setTACashLinkAttr(rs);
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

            //  VocabularyBean vocabulary = new VocabularyBean();
            //vocabulary.setYssPub(pub);

            // sVocStr = vocabulary.getVoc(YssCons.YSS_TA_SellNetType);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
            //+"\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取TA现金账户链接设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            /**shashijie 2011-10-19 BUG 2959 */
            if (this.filterType.portClsCode.trim().length() != 0) {
            	//edit by songjie 2011.07.15 筛选时报未明确定义列错误
                sResult = sResult + " and a.FPortClsCode like '" +
                    filterType.portClsCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sellNetCode.trim().length() != 0) {
            	//edit by songjie 2011.07.15 筛选时报未明确定义列错误
                sResult = sResult + " and a.FsellNetCode like '" +
                    filterType.sellNetCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.portCode.trim().length() != 0) {
            	//edit by songjie 2011.07.15 筛选时报未明确定义列错误
                sResult = sResult + " and a.FportCode like '" +
                    filterType.portCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sellTypeCode.trim().length() != 0) {
            	//edit by songjie 2011.07.15 筛选时报未明确定义列错误
                sResult = sResult + " and a.FsellTypeCode like '" +
                    filterType.sellTypeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.curyCode.trim().length() != 0) {
            	//edit by songjie 2011.07.15 筛选时报未明确定义列错误
                sResult = sResult + " and a.FcuryCode like '" +
                    filterType.curyCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.cashAccCode.trim().length() != 0) {
                sResult = sResult + " and a.FcashAccCode like '" +
                    filterType.cashAccCode.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.startDate.trim().equals("9998-12-31")) {
            	//edit by songjie 2011.07.15 筛选时报未明确定义列错误
                sResult = sResult + " and a.FStartDate = " +
                    dbl.sqlDate(filterType.startDate);
            }
            if (this.filterType.desc.trim().length() != 0) {
            	//edit by songjie 2011.07.15 筛选时报未明确定义列错误
                sResult = sResult + " and a.Fdesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
            /**end*/
        }
        return sResult;
    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
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
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t", -1);
            this.sellNetCode = reqAry[0];
            this.portClsCode = reqAry[1];
            this.portCode = reqAry[2];
            this.sellTypeCode = reqAry[3];
            this.curyCode = reqAry[4];
            this.cashAccCode = reqAry[5];
            this.desc = reqAry[6];
            this.startDate = reqAry[7];
            this.checkStateId = YssFun.toInt(reqAry[8]);
            this.oldSellNetCode = reqAry[9];
            this.oldPortClsCode = reqAry[10];
            this.oldPortCode = reqAry[11];
            this.oldSellTypeCode = reqAry[12];
            this.oldCuryCode = reqAry[13];
            this.oldStartDate = YssFun.toDate(reqAry[14]); //BugNo:0000344 edit by jc

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TaCashAccLinkBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析TA现金账户链接设置出错");
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.sellNetCode).append("\t");
        buf.append(this.sellNetName).append("\t");
        buf.append(this.portClsCode).append("\t");
        buf.append(this.portClsName).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.sellTypeCode).append("\t");
        buf.append(this.sellTypeName).append("\t");
        buf.append(this.curyCode).append("\t");
        buf.append(this.curyName).append("\t");
        buf.append(this.cashAccCode).append("\t");
        buf.append(this.cashAccName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.startDate).append("\t");

        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void setTACashLinkAttr(ResultSet rs) throws YssException {
        try {
            this.sellNetCode = rs.getString("FsellNetCode");
            this.sellNetName = rs.getString("FsellNetName");
            this.portClsCode = rs.getString("FportClsCode");
            this.portClsName = rs.getString("FportClsName");
            this.portCode = rs.getString("FportCode");
            this.portName = rs.getString("FportName");
            this.sellTypeCode = rs.getString("FsellTypeCode");
            this.sellTypeName = rs.getString("FsellTypeName");
            this.curyCode = rs.getString("FcuryCode");
            this.curyName = rs.getString("FcuryName");
            this.cashAccCode = rs.getString("FcashAccCode");
            this.cashAccName = rs.getString("FcashAccName");
            this.desc = rs.getString("FDesc");
            this.startDate = YssFun.formatDate(rs.getDate("FStartDate"),
                                               "yyyy-MM-dd");

            super.setRecLog(rs);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
			/**shashijie 2012-7-2 STORY 2475 */
            if (sRecycled != null || !sRecycled.equalsIgnoreCase("")) {
			/**end*/
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_TA_CashAccLink") +
                        " where FSellNetCode = " + dbl.sqlString(this.sellNetCode) +
                        " and FPortClsCode = " + dbl.sqlString(this.portClsCode) +
                        " and FPortCode = " + dbl.sqlString(this.portCode) +
                        " and FSellTypeCode = " + dbl.sqlString(this.sellTypeCode) +
                        " and FCuryCode = " + dbl.sqlString(this.curyCode) +
                        " and FStartDate = " + dbl.sqlDate(this.startDate);
                    dbl.executeSql(strSql);

                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除TA现金账户链接设置信息出错", e);
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
