package com.yss.main.taoperation;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class TASellTypeBean
    extends BaseDataSettingBean implements IDataSetting {
    private String sellTypeCode;
    private String sellTypeName;
    private String desc;
    private int cashInd = 0;
    private String oldSellTypeCode;
    private int amountInd = 0;
    private String amountIndName;
    private String cashIndName;
    private TASellTypeBean filterType;
    private String sRecycled = null; //保存未解析前的字符串

    public TASellTypeBean() {
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_TA_SellType"),
                               "FSellTypeCode",
                               this.sellTypeCode,
                               this.oldSellTypeCode);

    }

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = " insert into " + pub.yssGetTableName("Tb_TA_SellType") +
                " (FSellTypeCode,FSellTypeName,FDesc,FCashInd,FAmountInd,FCheckState,FCreator,FCreateTime) values("
                + dbl.sqlString(this.sellTypeCode) + ","
                + dbl.sqlString(this.sellTypeName) + ","
                + dbl.sqlString(this.desc) + ","
                + this.cashInd + ","
                + this.amountInd + ","
                + (pub.getSysCheckState() ? "0" : "1") + ","
                + dbl.sqlString(this.creatorCode) + ","
                + dbl.sqlString(this.creatorTime) + ")"; //BugNo:0000310 4 edit by jc
            //(pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorTime)) +
            //")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("增加销售类型设置出错", e);
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
            strSql = " update " + pub.yssGetTableName("Tb_TA_SellType") +
                " set FSellTypeCode=" + dbl.sqlString(this.sellTypeCode) + "," +
                " FSellTypeName=" + dbl.sqlString(this.sellTypeName) + "," +
                " FDesc=" + dbl.sqlString(this.desc) + "," +
                " FCashInd=" + this.cashInd + "," +
                " FAmountInd=" + this.amountInd + "," +
                " FCheckState=" + this.checkStateId +
                //BugNo:0000310 4 edit by jc
                ", FCreateTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCreator = " +
                dbl.sqlString(pub.getUserCode()) +
                //------------------------jc
                " where FSellTypeCode=" + dbl.sqlString(this.oldSellTypeCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改销售类型设置出错", e);
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
            strSql = " update " + pub.yssGetTableName("Tb_TA_SellType") +
                " set FCheckState=" + this.checkStateId +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FSellTypeCode=" + dbl.sqlString(this.oldSellTypeCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除销售类型设置出错", e);
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
            if (this.sRecycled != null || !this.sRecycled.equalsIgnoreCase("")) {
			/**end*/
                arrData = this.sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = " update " + pub.yssGetTableName("Tb_TA_SellType") +
                        " set FCheckState=" + this.checkStateId +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "', FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + //BugNo:0000310 4 edit by jc
                        " where FSellTypeCode=" +
                        dbl.sqlString(this.oldSellTypeCode);
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("TA销售类型删除出错", e);
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
            strSql = "select * from " + pub.yssGetTableName("Tb_TA_SellType") +
                " where FSellTypeCode=" + dbl.sqlString(this.sellTypeCode);
            rs = dbl.openResultSet(strSql);
//            rs.next(); //只有一行
//            this.sellTypeCode = rs.getString("FSellTypeCode");
//            this.sellTypeName = rs.getString("FSellTypeName");
//            this.cashInd = rs.getInt("FCashInd");
//            this.amountInd = rs.getInt("FAmountInd");
//            this.desc = rs.getString("FDesc");
            if(rs.next()) //2011.07.07 guolongchao  BUG2152 即使查询出的记录只有一条，也要进行if判断，否则当这条记录不存在时，就会出现：用尽的ResultSet错误提示。
            {
            	this.sellTypeCode = rs.getString("FSellTypeCode");
                this.sellTypeName = rs.getString("FSellTypeName");
                this.cashInd = rs.getInt("FCashInd");
                this.amountInd = rs.getInt("FAmountInd");
                this.desc = rs.getString("FDesc");
            }
            rs.close();
        } catch (Exception e) {
            throw new YssException(e.toString());
        }finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    private void setSellTypeAttr(ResultSet rs) throws SQLException, YssException {
        this.sellTypeCode = rs.getString("FSellTypeCode");
        this.sellTypeName = rs.getString("FSellTypeName");
        this.desc = rs.getString("FDesc");
        this.amountInd = rs.getInt("FAmountInd");
        this.amountIndName = rs.getString("FAmountIndName");
        this.cashInd = rs.getInt("FCashInd");
        this.cashIndName = rs.getString("FCashIndName");
        super.setRecLog(rs);
    }

    private String FilterStr() {
        String str = "";
        if (this.filterType != null) {
            str = " where 1=1 ";
			/**shashijie 2012-7-2 STORY 2475 */
            if (this.filterType.sellTypeCode != null &&
            		this.filterType.sellTypeCode.length() > 0) {
                str += " and a.FSellTypeCode like '" +
                    this.filterType.sellTypeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sellTypeName != null &&
            		this.filterType.sellTypeName.length() > 0) {
                str += " and a.FSellTypeName like '" +
                    this.filterType.sellTypeName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc != null && this.filterType.desc.length() > 0) {
                str += " and a.FDesc like '" +
                    this.filterType.desc.replaceAll("'", "''") + "%'";
            }
			/**end*/
            if (this.filterType.amountInd != 99) {
                str += " and a.FAmountInd = " + this.filterType.amountInd;
            }
            if (this.filterType.cashInd != 99) {
                str += " and a.FCashInd = " + this.filterType.cashInd;
            }
        }
        return str;
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
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql =
                " select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName, " +
                " d.FVocName as FCashIndName,e.FVocName as FAmountIndName " +
                " from " + pub.yssGetTableName("Tb_TA_SellType") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //2007.11.30 修改 蒋锦 使用dbl.sqlToChar()处理"a.FCashInd"，否则在使用DB2数据库时会报数据类型错误
                " left join Tb_Fun_Vocabulary d on " +
                dbl.sqlToChar("a.FCashInd") +
                " = d.FVocCode and d.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_TDT_CASHIND) +
                //2007.11.30 修改 蒋锦 使用dbl.sqlToChar()处理"a.FAmountInd"，否则在使用DB2数据库时会报数据类型错误
                " left join Tb_Fun_Vocabulary e on " +
                dbl.sqlToChar("a.FAmountInd") +
                " = e.FVocCode and e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_TDT_AMOUNTIND) +
                this.FilterStr() +
                " order by a.FCheckState ,a.FCheckTime desc,a.FCreateTime desc ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setSellTypeAttr(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_TDT_CASHIND + "," +
                                        YssCons.YSS_TDT_AMOUNTIND);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取TA销售类型设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "销售类型代码\t销售类型名称";
            strSql = " select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName,' ' as FAmountIndName,' ' as FCashIndName " +
                " from " + pub.yssGetTableName("Tb_TA_SellType") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                "  where a.FCheckState=1 order by a.FCheckState ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FSellTypeCode")).append("\t").append(
                    rs.getString("FSellTypeName"))
                    .append(YssCons.YSS_LINESPLITMARK);

                setSellTypeAttr(rs);

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
            throw new YssException("获取销售类型出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getListViewData3() throws YssException {
    	String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "销售类型代码\t销售类型名称";
            strSql = " select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName,' ' as FAmountIndName,' ' as FCashIndName " +
                " from " + pub.yssGetTableName("Tb_TA_SellType") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                "  where a.FCheckState=1 and a.FSELLTYPECODE in ('01','02','03','08') order by a.FCheckState ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FSellTypeCode")).append("\t").append(
                    rs.getString("FSellTypeName"))
                    .append(YssCons.YSS_LINESPLITMARK);

                setSellTypeAttr(rs);

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
            throw new YssException("获取销售类型出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.sellTypeCode = reqAry[0];
            this.sellTypeName = reqAry[1];
            this.desc = reqAry[2];
            if (reqAry[3].length() > 0) {
                this.cashInd = Integer.parseInt(reqAry[3]);
            }
            if (reqAry[4].length() > 0) {
                this.amountInd = Integer.parseInt(reqAry[4]);
            }
            this.checkStateId = Integer.parseInt(reqAry[5]);
            this.oldSellTypeCode = reqAry[6];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TASellTypeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析TA销售类型设置出错", e);
        }
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.sellTypeCode).append("\t");
        buf.append(this.sellTypeName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.cashInd).append("\t");
        buf.append(this.cashIndName).append("\t");
        buf.append(this.amountInd).append("\t");
        buf.append(this.amountIndName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public void setSellTypeCode(String sellTypeCode) {
        this.sellTypeCode = sellTypeCode;
    }

    public void setSellTypeName(String sellTypeName) {
        this.sellTypeName = sellTypeName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setCashInd(int cashInd) {
        this.cashInd = cashInd;
    }

    public void setAmountInd(int amountInd) {
        this.amountInd = amountInd;
    }

    public void setFilterType(TASellTypeBean filterType) {
        this.filterType = filterType;
    }

    public void setOldSellTypeCode(String oldSellTypeCode) {
        this.oldSellTypeCode = oldSellTypeCode;
    }

    public void setAmountIndName(String amountIndName) {
        this.amountIndName = amountIndName;
    }

    public void setCashIndName(String cashIndName) {
        this.cashIndName = cashIndName;
    }

    public String getSellTypeCode() {
        return sellTypeCode;
    }

    public String getSellTypeName() {
        return sellTypeName;
    }

    public String getDesc() {
        return desc;
    }

    public int getCashInd() {
        return cashInd;
    }

    public int getAmountInd() {
        return amountInd;
    }

    public TASellTypeBean getFilterType() {
        return filterType;
    }

    public String getOldSellTypeCode() {
        return oldSellTypeCode;
    }

    public String getAmountIndName() {
        return amountIndName;
    }

    public String getCashIndName() {
        return cashIndName;
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        String[] arrData = null;
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (sRecycled != null || ! ("").equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData.length == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_TA_SellType") +
                        " where FSellTypeCode = " +
                        dbl.sqlString(this.oldSellTypeCode);
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (NullPointerException ex) {
            throw new YssException(ex.getMessage()); 
        } catch (Exception e) {
            throw new YssException("TA销售类型清除出错", e);
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
