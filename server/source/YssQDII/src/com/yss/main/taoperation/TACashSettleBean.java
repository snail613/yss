package com.yss.main.taoperation;

import java.sql.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class TACashSettleBean
    extends BaseDataSettingBean implements IDataSetting {
    private String sellNetCode; //销售网点代码
    private String sellNetName;
    private String portClsCode; //组合分级代码
    private String portClsName;
    private String portCode;
    private String portName;
    private String sellTypeCode;
    private String sellTypeName;
    private String curyCode;
    private String curyName;
    private int settleDayType = 0;
    private String settleDayTypeName;
    private int settleDays = 0;
    private Date startDate;
    private String desc;
    private int ConfirmDays = 0; //  添加确认延迟天数 add liyu 1029
    private String oldSellNetCode;
    private String oldPortClsCode;
    private String oldPortCode;
    private String oldSellTypeCode;
    private String oldCuryCode;
    private Date oldStartDate;

    private String sRecycled = null; //保存未解析前的字符串

    private String strHolidaysCode = ""; //节假日群代码
    private String strHolidaysName = ""; //节假日群名称

    private TACashSettleBean filterType;
    public TACashSettleBean() {
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_TA_CashSettle"),
                               "FSellNetCode,FPortClsCode,FPortCode,FSellTypeCode,FCuryCode,FStartDate",
                               sellNetCode + "," + portClsCode + "," + portCode +
                               "," + sellTypeCode + "," + curyCode + "," +
                               YssFun.formatDate(startDate, "yyyy-MM-dd"),
                               oldSellNetCode + "," + oldPortClsCode + "," +
                               oldPortCode + "," + oldSellTypeCode + "," +
                               oldCuryCode + "," +
                               YssFun.formatDate(oldStartDate, "yyyy-MM-dd"));
    }

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = " insert into " + pub.yssGetTableName("Tb_TA_CashSettle") +
                " (FSellNetCode,FPortClsCode,FDesc,FPortCode,FSellTypeCode,FCuryCode," +
                " FSettleDayType,FSettleDays,FHolidaysCode," +
                " FCheckState,FCreator,FCreateTime,FStartDate,FConfirmDays) values("
                + dbl.sqlString(this.sellNetCode) + ","
                + dbl.sqlString(this.portClsCode) + ","
                + dbl.sqlString(this.desc) + ","
                + dbl.sqlString(this.portCode) + ","
                + dbl.sqlString(this.sellTypeCode) + ","
                + dbl.sqlString(this.curyCode) + ","
                + this.settleDayType + ","
                + this.settleDays + ","
                + dbl.sqlString(this.strHolidaysCode) + ","
                + (pub.getSysCheckState() ? "0" : "1") + ","
                + dbl.sqlString(this.creatorCode) + ","
                +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorTime)) +
                "," +
                dbl.sqlDate(this.startDate) + "," +
                this.ConfirmDays + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("增加TA现金结算链接设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = " update " + pub.yssGetTableName("Tb_TA_CashSettle") +
                " set FSellNetCode=" + dbl.sqlString(this.sellNetCode) + "," +
                " FPortClsCode=" + dbl.sqlString(this.portClsCode) + "," +
                " FPortCode=" + dbl.sqlString(this.portCode) + "," +
                " FSellTypeCode=" + dbl.sqlString(this.sellTypeCode) + "," +
                " FCuryCode =" + dbl.sqlString(this.curyCode) + "," +
                " FSettleDayType=" + this.settleDayType + "," +
                " FSettleDays=" + this.settleDays + "," +
                " FDesc=" + dbl.sqlString(this.desc) + "," +
                " FCheckState=" + this.checkStateId + "," +
                " FStartDate= " + dbl.sqlDate(this.startDate) + "," +
                " FConfirmDays=" + this.ConfirmDays + "," + // add liyu 1029
                " FHolidaysCode= " + dbl.sqlString(this.strHolidaysCode) + "," +
                " FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FSellNetCode=" + dbl.sqlString(this.oldSellNetCode) +
                " and FPortClsCode=" + dbl.sqlString(this.oldPortClsCode) +
                " and FPortCode=" + dbl.sqlString(this.oldPortCode) +
                " and FSellTypeCode=" + dbl.sqlString(this.oldSellTypeCode) +
                " and FCuryCode=" + dbl.sqlString(this.oldCuryCode) +
                " and FStartDate=" + dbl.sqlDate(this.oldStartDate);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改TA现金结算链接设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = " update " + pub.yssGetTableName("Tb_TA_CashSettle") +
                " set FCheckState=" + this.checkStateId +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FSellNetCode=" + dbl.sqlString(this.oldSellNetCode) +
                " and FPortClsCode=" + dbl.sqlString(this.oldPortClsCode) +
                " and FPortCode=" + dbl.sqlString(this.oldPortCode) +
                " and FSellTypeCode=" + dbl.sqlString(this.oldSellTypeCode) +
                " and FCuryCode=" + dbl.sqlString(this.oldCuryCode) +
                " and FStartDate=" + dbl.sqlDate(this.oldStartDate);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除TA现金结算链接设置出错", e);
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
            bTrans = true;
            conn.setAutoCommit(false);
			/**shashijie 2012-7-2 STORY 2475 */
            if (sRecycled != null || !sRecycled.equalsIgnoreCase("")) {
			/**end*/
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = " update " + pub.yssGetTableName("Tb_TA_CashSettle") +
                        " set FCheckState=" + this.checkStateId +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "', FCheckUser = " +
                        (pub.getSysCheckState() ? "' '" :
                         dbl.sqlString(this.creatorCode)) +
                        " where FSellNetCode=" + dbl.sqlString(this.sellNetCode) +
                        //" and FPortClsCode="+dbl.sqlString(this.oldPortClsCode) + 修改 邱健，oldPortClsCode 无值
                        " and FPortClsCode=" + dbl.sqlString(this.portClsCode) +
                        //" and FPortCode="+dbl.sqlString(this.oldPortCode) + 修改 邱健，oldPortCode 无值
                        " and FPortCode=" + dbl.sqlString(this.portCode) +
                        //" and FSellTypeCode="+dbl.sqlString(this.oldSellTypeCode) + 修改 邱健，oldSellTypeCode 无值
                        " and FSellTypeCode=" + dbl.sqlString(this.sellTypeCode) +
                        //" and FCuryCode="+dbl.sqlString(this.oldCuryCode) + 修改 邱健，oldCuryCode 无值
                        " and FCuryCode=" + dbl.sqlString(this.curyCode) +
                        //" and FStartDate="+dbl.sqlDate( this.oldStartDate); 修改 邱健，oldStartDate 无值
                        " and FStartDate=" + dbl.sqlDate(this.startDate);
                    dbl.executeSql(strSql);
                }
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核TA现金结算链接设置出错", e);
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
            strSql = "select * from " + pub.yssGetTableName("Tb_TA_CashSettle") +
                " where 1=1 " +
                (this.sellNetCode.length() > 0 ?
                 " and FSellNetCode = " + dbl.sqlString(this.sellNetCode) : "") +
                (this.portClsCode.length() > 0 ?
                 " and FPortClsCode = " + dbl.sqlString(this.portClsCode) : "") +
                (this.portCode.length() > 0 ?
                 " and FPortCode = " + dbl.sqlString(this.portCode) : "") +
                (this.sellTypeCode.length() > 0 ?
                 " and FSellTypeCode = " + dbl.sqlString(this.sellTypeCode) : "") +
                (this.curyCode.length() > 0 ?
                 " and FCuryCode = " + dbl.sqlString(this.curyCode) : "") +
                (this.startDate == null ? "" :
                 " and FStartDate = " + dbl.sqlDate(this.startDate));
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.sellNetCode = rs.getString("FSellNetCode") + "";
                this.portClsCode = rs.getString("FPortClsCode") + "";
                this.portCode = rs.getString("FPortCode") + "";
                this.sellTypeCode = rs.getString("FSellTypeCode") + "";
                this.curyCode = rs.getString("FCuryCode") + "";
                this.settleDayType = rs.getInt("FSettleDayType");
                this.settleDays = rs.getInt("FSettleDays");
                this.ConfirmDays = rs.getInt("FConfirmDays");
                this.strHolidaysCode = rs.getString("FHolidaysCode") + "";
                this.desc = rs.getString("FDesc") + "";
                this.checkStateId = rs.getInt("FCheckState");
                this.checkStateName = YssFun.getCheckStateName(rs.getInt(
                    "FCheckState"));
                this.creatorCode = rs.getString("FCreator") + "";
                this.creatorTime = rs.getString("FcreateTime") + "";
                this.checkUserCode = rs.getString("FCheckUser") + "";
                this.checkTime = rs.getString("FCheckTime") + "";
                this.ConfirmDays = rs.getInt("FConfirmDays");
            }
            return null;
        } catch (Exception e) {
            throw new YssException("获取TA现金结算链接出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
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

    private void setCSAttr(ResultSet rs) throws SQLException, YssException {
        this.sellNetCode = rs.getString("FSellNetCode");
        this.sellNetName = rs.getString("FSellNetName");
        this.desc = rs.getString("FDesc");
        this.portCode = rs.getString("FPortCode");
        this.portName = rs.getString("FPortName");
        this.sellTypeCode = rs.getString("FSellTypeCode");
        this.sellTypeName = rs.getString("FSellTypeName");
        this.curyCode = rs.getString("FCuryCode");
        this.curyName = rs.getString("FCuryName");
        this.portClsCode = rs.getString("FPortClsCode");
        this.portClsName = rs.getString("FPortClsName");
        this.settleDayType = rs.getInt("FSettleDayType");
        this.settleDays = rs.getInt("FSettleDays");
        this.startDate = rs.getDate("FStartDate");
        this.strHolidaysCode = rs.getString("FHolidaysCode");
        this.strHolidaysName = rs.getString("FHolidaysName");
        this.settleDayTypeName = rs.getString("FSettleDayTypeName");
        this.ConfirmDays = rs.getInt("FConfirmDays");
        super.setRecLog(rs);

    }

    private String FilterStr() throws YssException {
        String str = "";
        if (this.filterType != null) {
            str = " where 1=1 ";
            /**shashijie 2011-10-14 BUG 2959 TA现金账户连接设置筛选不出数据*/
            if (this.filterType.sellNetCode != null &&
            		this.filterType.sellNetCode.trim().length() > 0 ) {
            	//edit by songjie 2011.07.15 筛选时报未明确到定义列错误
                str += " and a.FSellNetCode like '" +
                    filterType.sellNetCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc != null && this.filterType.desc.trim().length() > 0) {
            	//edit by songjie 2011.07.15 筛选时报未明确到定义列错误
                str += " and a.FDesc like '" + filterType.desc.replaceAll("'", "''") +
                    "%'";
            }
            if (this.filterType.portCode != null && this.filterType.portCode.trim().length() != 0) {
            	//edit by songjie 2011.07.15 筛选时报未明确到定义列错误
            	str += " and a.FPortCode like '" +
                    filterType.portCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sellTypeCode != null &&
            		this.filterType.sellTypeCode.trim().length() != 0) {
            	//edit by songjie 2011.07.15 筛选时报未明确到定义列错误
            	str += " and a.FSellTypeCode like '" +
                    filterType.sellTypeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.curyCode != null && this.filterType.curyCode.trim().length() != 0) {
            	//edit by songjie 2011.07.15 筛选时报未明确到定义列错误
            	str += " and a.FCuryCode like '" +
                    filterType.curyCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strHolidaysCode != null &&
            		this.filterType.strHolidaysCode.trim().length() != 0) {
            	//edit by songjie 2011.07.15 筛选时报未明确到定义列错误
            	str += " and a.FHolidaysCode like '" +
                    filterType.strHolidaysCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.settleDayType != 0 && this.filterType.settleDayType != 99) {
            	//edit by songjie 2011.07.15 筛选时报未明确到定义列错误
            	str += " and a.FSettleDayType =" + filterType.settleDayType;
            }
            if (this.filterType.settleDays != 0 && this.filterType.settleDays != 99) {
            	//edit by songjie 2011.07.15 筛选时报未明确到定义列错误
            	str += " and a.FSettleDays =" + filterType.settleDays;
            }
            if (this.filterType.startDate != null &&
                !this.filterType.startDate.equals(YssFun.toDate("9998-12-31"))) {
            	//edit by songjie 2011.07.15 筛选时报未明确到定义列错误 日期格式有误
            	str += " and a.FStartDate =" + dbl.sqlDate(filterType.startDate);
            }
            if (this.filterType.portClsCode != null &&
            		this.filterType.portClsCode.trim().length() != 0) {
            	//edit by songjie 2011.07.15 筛选时报未明确到定义列错误
            	str += " and a.FPortClsCode like '" +
                    filterType.portClsCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.ConfirmDays != 0 && this.filterType.ConfirmDays != 99) {
            	//edit by songjie 2011.07.15 筛选时报未明确到定义列错误
            	str += " and a.FConfirmDays =" + filterType.ConfirmDays;
            }
            /**end*/
        }
        return str;
    }

    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FSellNetName as FSellNetName, " +
                " e.FPortClsName as FPortClsName,f.FPortName as FPortName,g.FSellTypeName as FSellTypeName," +
                " h.FCuryName as FCuryName,i.FVocName as FSettleDayTypeName,j.FHolidaysName " +
                " from " + pub.yssGetTableName("Tb_TA_CashSettle") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join (select FSellNetCode,FSellNetName from " +
                pub.yssGetTableName("Tb_TA_SellNet") +
                ") d on a.FSellNetCode=d.FSellNetCode " +
                " left join (select FPortClsCode,FPortClsName from " +
                pub.yssGetTableName("Tb_TA_PortCls") +
                " ) e on a.FPortClsCode = e.FPortClsCode " +                
                
                //------ modify by wangzuochun  2010.07.19  MS01449    组合代码相同而启用日期不同的组合时，新建买入证券据，进行库存统计后，现金库存会增倍 QDV4赢时胜(测试)2010年7月15日01_B 
                //----------------------------------------------------------------------------------------------------
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
     
                " left join (select FPortCode, FPortName, FPortCury from " +
                pub.yssGetTableName("Tb_Para_Portfolio") +
                " where  FCheckState = 1 ) f on a.FPortCode = f.FPortCode " +
             
                
                //end by lidaolong
                //-------------------------------------------- MS01449 -------------------------------------------//
                
                " left join (select FSellTypeCode,FSellTypeName from " +
                pub.yssGetTableName("Tb_TA_SellType") +
                " ) g on g.FSellTypeCode= a.FSellTypeCode " +
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " ) h on h.FCuryCode=a.FCuryCode " +
                " left join (select FHolidaysCode,FHolidaysName from Tb_Base_Holidays " +
                ") j on j.FHolidaysCode=a.FHolidaysCode " +
                //2007.11.30 修改 蒋锦 使用dbl.sqlToChar()处理"a.FSettleDayType"，否则在使用DB2数据库时会报数据类型错误
                " left join Tb_Fun_Vocabulary i on " +
                dbl.sqlToChar("a.FSettleDayType") +
                " = i.FVocCode and i.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_SCY_SDAYTYPE) +
                FilterStr() +
                " order by a.FCheckState,a.FCheckTime desc,a.FCreateTime desc ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setCSAttr(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_SCY_SDAYTYPE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取TA现金结算链接设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
        String reqAry[] = null;
        String sTmpStr = "";
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
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            //alter by sunny 当没有值的时候 插入一个空字符串
            this.sellNetCode = reqAry[0].trim().length() == 0 ? " " : reqAry[0];
            this.desc = reqAry[1];
            this.portCode = reqAry[2].trim().length() == 0 ? " " : reqAry[2];
            this.sellTypeCode = reqAry[3].trim().length() == 0 ? " " : reqAry[3];
            this.curyCode = reqAry[4].trim().length() == 0 ? " " : reqAry[4];
            this.portClsCode = reqAry[5].trim().length() == 0 ? " " : reqAry[5];
            if (reqAry[6].length() > 0) {
                this.settleDayType = Integer.parseInt(reqAry[6]);
            }
            if (reqAry[7].length() > 0) {
                this.settleDays = Integer.parseInt(reqAry[7]);
            }
            this.startDate = YssFun.toDate(reqAry[8]);
            this.checkStateId = Integer.parseInt(reqAry[9]);
            this.oldSellNetCode = reqAry[10];
            this.oldPortClsCode = reqAry[11];
            this.oldPortCode = reqAry[12];
            this.oldSellTypeCode = reqAry[13];
            this.oldCuryCode = reqAry[14];
            this.strHolidaysCode = reqAry[15];
            this.oldStartDate = YssFun.toDate(reqAry[16]);
            if (YssFun.isNumeric(reqAry[17])) {
                this.ConfirmDays = Integer.parseInt(reqAry[17]);
            }
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TACashSettleBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析现金结算链接设置出错", e);
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.sellNetCode).append("\t");
        buf.append(this.sellNetName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.sellTypeCode).append("\t");
        buf.append(this.sellTypeName).append("\t");
        buf.append(this.curyCode).append("\t");
        buf.append(this.curyName).append("\t");
        buf.append(this.portClsCode).append("\t");
        buf.append(this.portClsName).append("\t");
        buf.append(this.settleDayType).append("\t");
        buf.append(this.settleDays).append("\t");
        buf.append(YssFun.formatDate(this.startDate)).append("\t"); //modify by fangjiang 2010.10.28 BUG #194 QDV4赢时胜(测试)2010年10月25日04_B.xls
        buf.append(this.strHolidaysCode).append("\t");
        buf.append(this.strHolidaysName).append("\t");
        buf.append(this.settleDayTypeName).append("\t");
        buf.append(this.ConfirmDays).append("\t");

        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public void setSellNetCode(String sellNetCode) {
        this.sellNetCode = sellNetCode;
    }

    public void setSellNetName(String sellNetName) {
        this.sellNetName = sellNetName;
    }

    public void setPortClsName(String portClsName) {
        this.portClsName = portClsName;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setSellTypeCode(String sellTypeCode) {
        this.sellTypeCode = sellTypeCode;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setCuryName(String curyName) {
        this.curyName = curyName;
    }

    public void setSettleDayType(int settleDayType) {
        this.settleDayType = settleDayType;
    }

    public void setSettleDays(int settleDays) {
        this.settleDays = settleDays;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setFilterType(TACashSettleBean filterType) {
        this.filterType = filterType;
    }

    public void setOldSellNetCode(String oldSellNetCode) {
        this.oldSellNetCode = oldSellNetCode;
    }

    public void setOldPortClsCode(String oldPortClsCode) {
        this.oldPortClsCode = oldPortClsCode;
    }

    public void setOldPortCode(String oldPortCode) {
        this.oldPortCode = oldPortCode;
    }

    public void setOldSellTypeCode(String oldSellTypeCode) {
        this.oldSellTypeCode = oldSellTypeCode;
    }

    public void setOldCuryCode(String oldCuryCode) {
        this.oldCuryCode = oldCuryCode;
    }

    public void setOldStartDate(Date oldStartDate) {
        this.oldStartDate = oldStartDate;
    }

    public void setSellTypeName(String FSellTypeName) {
        this.sellTypeName = FSellTypeName;
    }

    public void setSettleDayTypeName(String settleDayTypeName) {
        this.settleDayTypeName = settleDayTypeName;
    }

    public void setStrHolidaysCode(String strHolidaysCode) {
        this.strHolidaysCode = strHolidaysCode;
    }

    public void setStrHolidaysName(String strHolidaysName) {
        this.strHolidaysName = strHolidaysName;
    }

    public void setPortClsCode(String portClsCode) {
        this.portClsCode = portClsCode;
    }

    public void setConfirmDays(int Confirm) {
        this.ConfirmDays = Confirm;//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
    }

    public String getSellNetCode() {
        return sellNetCode;
    }

    public String getSellNetName() {
        return sellNetName;
    }

    public String getPortClsCode() {
        return portClsCode;
    }

    public String getPortClsName() {
        return portClsName;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getPortName() {
        return portName;
    }

    public String getSellTypeCode() {
        return sellTypeCode;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public String getCuryName() {
        return curyName;
    }

    public int getSettleDayType() {
        return settleDayType;
    }

    public int getSettleDays() {
        return settleDays;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getDesc() {
        return desc;
    }

    public TACashSettleBean getFilterType() {
        return filterType;
    }

    public String getOldSellNetCode() {
        return oldSellNetCode;
    }

    public String getOldPortClsCode() {
        return oldPortClsCode;
    }

    public String getOldPortCode() {
        return oldPortCode;
    }

    public String getOldSellTypeCode() {
        return oldSellTypeCode;
    }

    public String getOldCuryCode() {
        return oldCuryCode;
    }

    public Date getOldStartDate() {
        return oldStartDate;
    }

    public String getSellTypeName() {
        return sellTypeName;
    }

    public String getSettleDayTypeName() {
        return settleDayTypeName;
    }

    public String getStrHolidaysCode() {
        return strHolidaysCode;
    }

    public String getStrHolidaysName() {
        return strHolidaysName;
    }

    public int getConfirmDays() {
        return ConfirmDays;
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            /**shashijie 2012-7-2 STORY 2475 */
			if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
			/**end*/
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
                    strSql = "delete from " + pub.yssGetTableName("Tb_TA_CashSettle") +
                        " where FSellNetCode=" + dbl.sqlString(this.sellNetCode) +
                        //" and FPortClsCode="+dbl.sqlString(this.oldPortClsCode) + 修改 邱健，oldPortClsCode 无值
                        " and FPortClsCode=" + dbl.sqlString(this.portClsCode) +
                        //" and FPortCode="+dbl.sqlString(this.oldPortCode) + 修改 邱健，oldPortCode 无值
                        " and FPortCode=" + dbl.sqlString(this.portCode) +
                        //" and FSellTypeCode="+dbl.sqlString(this.oldSellTypeCode) + 修改 邱健，oldSellTypeCode 无值
                        " and FSellTypeCode=" + dbl.sqlString(this.sellTypeCode) +
                        //" and FCuryCode="+dbl.sqlString(this.oldCuryCode) + 修改 邱健，oldCuryCode 无值
                        " and FCuryCode=" + dbl.sqlString(this.curyCode) +
                        //" and FStartDate="+dbl.sqlDate( this.oldStartDate); 修改 邱健，oldStartDate 无值
                        " and FStartDate=" + dbl.sqlDate(this.startDate);
                    dbl.executeSql(strSql);
                }
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
