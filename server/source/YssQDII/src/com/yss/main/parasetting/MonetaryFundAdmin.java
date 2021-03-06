package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.parasetting.pojo.*;
import com.yss.util.*;

/**
 *
 * <p>Title: 货币基金信息设置</p>
 *
 * <p>Description: QDV4.1赢时胜（上海）2009年4月20日13_A 蒋锦 添加 MS00013</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MonetaryFundAdmin
    extends BaseDataSettingBean implements IDataSetting {

    private MonetaryFundBean monetaryFundBean = null;
    private String assetGroupCode = "";
    private String recycled = "";

    public String getRecycled() {
        return recycled;
    }

    public MonetaryFundBean getMonetaryFundBean() {
        return monetaryFundBean;
    }

    public void setRecycled(String recycled) {
        this.recycled = recycled;
    }

    public void setMonetaryFundBean(MonetaryFundBean monetaryFundBean) {
        this.monetaryFundBean = monetaryFundBean;
    }

    public MonetaryFundAdmin() {
    }

    /**
     * 根据 filtertype 创建 Sql Where 条件
     * @return String
     * @throws YssException
     */
    public String getFilterTypeWhere() throws YssException {
        String sqlWhere = "";
        if (monetaryFundBean.getFilterType() == null) {
            return "";
        }
        sqlWhere = " WHERE 1=1";
        if (monetaryFundBean.getFilterType().getSecurityCode() != null &&
            monetaryFundBean.getFilterType().getSecurityCode().trim().length() > 0) {
            sqlWhere += " AND a.FSecurityCode = " +
                dbl.sqlString(monetaryFundBean.getFilterType().getSecurityCode().trim()); //modify by fangjiang 2010.10.11 BUG #301 证券信息维护界面的其它属性未根据证券子品种弹出货币基金设置界面
        }
        if (monetaryFundBean.getFilterType().getClosedType() != null &&
            !monetaryFundBean.getFilterType().getClosedType().equalsIgnoreCase("99") &&
            monetaryFundBean.getFilterType().getClosedType().trim().length() > 0) {
            sqlWhere += " AND a.FClosedType = " + dbl.sqlString(monetaryFundBean.getClosedType().trim());
        }
        if (monetaryFundBean.getFilterType().getInterestType() != null &&
            !monetaryFundBean.getFilterType().getInterestType().equalsIgnoreCase("99") &&
            monetaryFundBean.getFilterType().getInterestType().trim().length() > 0) {
            sqlWhere += " AND a.FInterestType = " + dbl.sqlString(monetaryFundBean.getInterestType().trim());
        }

        return sqlWhere;
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Para_Monetaryfund"),
                               "FSecurityCode",
                               monetaryFundBean.getSecurityCode(),
                               monetaryFundBean.getOldSecurityCode());

    }

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            strSql = "INSERT INTO " + pub.yssGetTableName("Tb_Para_Monetaryfund") +
                "(FSecurityCode, FClosedType, FInterestType, FDesc, FCheckState, FCreator, FCreateTime, FCheckUser) VALUES(" +
                dbl.sqlString(monetaryFundBean.getSecurityCode()) + "," +
                dbl.sqlString(monetaryFundBean.getClosedType()) + "," +
                dbl.sqlString(monetaryFundBean.getInterestType()) + "," +
                dbl.sqlString(monetaryFundBean.getDesc()) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(monetaryFundBean.creatorCode) + "," +
                dbl.sqlString(monetaryFundBean.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException("新增货币基金信息设置出错！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            strSql = "UPDATE " + pub.yssGetTableName("Tb_Para_Monetaryfund") +
                " SET FSecurityCode = " + dbl.sqlString(monetaryFundBean.getSecurityCode()) + "," +
                " FClosedType = " + dbl.sqlString(monetaryFundBean.getClosedType()) + "," +
                " FInterestType = " + dbl.sqlString(monetaryFundBean.getInterestType()) + "," +
                " FDesc = " + dbl.sqlString(monetaryFundBean.getDesc()) +
                " WHERE FSecurityCode = " + dbl.sqlString(monetaryFundBean.getOldSecurityCode());
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException("修改货币基金信息设置出错！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public void delSetting() throws YssException {
    }

    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = null;
        String[] arrData = null;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            arrData = recycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                strSql = "update " + pub.yssGetTableName("Tb_Para_Monetaryfund") +
                    " set FCheckState=" + monetaryFundBean.checkStateId + "," +
                    " FCheckUser = " + dbl.sqlString(monetaryFundBean.checkUserCode) + "," +
                    " FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                    " where FSecurityCode = " + dbl.sqlString(monetaryFundBean.getSecurityCode());
                dbl.executeSql(strSql);
            }
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("审核货币基金信息出错！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public void deleteRecycleData() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        String[] arrData = null;
        try {
            conn = dbl.loadConnection();
            arrData = recycled.split("\r\n");
            conn.setAutoCommit(bTrans);
            bTrans = true;
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                sqlStr = "delete from " + pub.yssGetTableName("Tb_Para_Monetaryfund") +
                    " where FSecurityCode = " + dbl.sqlString(monetaryFundBean.getSecurityCode());
                dbl.executeSql(sqlStr);
            }
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("删除货币基金信息出错!");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewData1() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            strSql = "SELECT a.*, b.FSECURITYNAME, fiv.FVocName AS FCloseTypeName, ins.FVocName AS FInterestTypeName," +
                " b.FUserName as FCreatorName, c.FUserName as FCheckUserName" +
                " FROM " + pub.yssGetTableName("Tb_Para_Monetaryfund") +
                " a LEFT JOIN " + pub.yssGetTableName("TB_Para_Security") + " b ON a.fsecuritycode = b.FSECURITYCODE" +
                " LEFT JOIN (SELECT FUserCode, FUserName" +
                " FROM Tb_Sys_UserList) b ON a.FCreator = b.FUserCode" +
                " LEFT JOIN (SELECT FUserCode, FUserName" +
                " FROM Tb_Sys_UserList) c ON a.FCheckUser = c.FUserCode" +
                " LEFT JOIN Tb_Fun_Vocabulary fiv on a.fclosedtype = fiv.FVocCode" +
                " and fiv.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PARA_MONETARYFUND_CLOSEDTYPE) +
                " LEFT JOIN Tb_Fun_Vocabulary ins on a.FInterestType = ins.FVocCode" +
                " and ins.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PARA_MONETARYFUND_INTERESTTYPE) +
                getFilterTypeWhere() +
                " ORDER BY a.FSecurityCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(this.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.monetaryFundBean.setMonetaryFundArr(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_PARA_MONETARYFUND_CLOSEDTYPE +
                                        "," + YssCons.YSS_PARA_MONETARYFUND_INTERESTTYPE);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;
        } catch (Exception ex) {
            throw new YssException("获取货币基金信息出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData2() throws YssException {
        return "";
    }

    /**
     * 用于收益计提的查询显示
     * @return String
     * @throws YssException
     */
    public String getListViewData3() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView3Headers();
            strSql = "SELECT a.*, b.FSECURITYNAME, fiv.FVocName AS FCloseTypeName, ins.FVocName AS FInterestTypeName," +
                " b.FUserName as FCreatorName, c.FUserName as FCheckUserName, h.FAssetGroupCode, h.FAssetGroupName" +
                " FROM " + pub.yssGetTableName("Tb_Para_Monetaryfund") +
                " a LEFT JOIN " + pub.yssGetTableName("TB_Para_Security") + " b ON a.fsecuritycode = b.FSECURITYCODE" +
                " LEFT JOIN (SELECT FUserCode, FUserName" +
                " FROM Tb_Sys_UserList) b ON a.FCreator = b.FUserCode" +
                " LEFT JOIN (SELECT FUserCode, FUserName" +
                " FROM Tb_Sys_UserList) c ON a.FCheckUser = c.FUserCode" +
                " LEFT JOIN Tb_Fun_Vocabulary fiv on a.fclosedtype = fiv.FVocCode" +
                " and fiv.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PARA_MONETARYFUND_CLOSEDTYPE) +
                " LEFT JOIN Tb_Fun_Vocabulary ins on a.FInterestType = ins.FVocCode" +
                " and ins.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PARA_MONETARYFUND_INTERESTTYPE) +
                " JOIN (SELECT FSecurityCode FROM " + pub.yssGetTableName("Tb_Stock_Security") +
                " WHERE FCheckState = 1" +
                " AND FPortCode IN (" + operSql.sqlCodes(monetaryFundBean.getPortCodes()) + ")" +
                " AND FStorageDate BETWEEN " + dbl.sqlDate(YssFun.addDay(monetaryFundBean.getStartDate(), -1)) +
                " AND " + dbl.sqlDate(YssFun.addDay(monetaryFundBean.getEndDate(), -1)) +
                " UNION" +
                " SELECT FSecurityCode FROM " + pub.yssGetTableName("Tb_Data_Subtrade") +
                " WHERE FCheckState = 1" +
                " AND FPortCode IN (" + operSql.sqlCodes(monetaryFundBean.getPortCodes()) + ")" +
                " AND FBargainDate BETWEEN " + dbl.sqlDate(monetaryFundBean.getStartDate()) +
                " AND " + dbl.sqlDate(monetaryFundBean.getEndDate()) +
                " UNION" +
                " SELECT FSecurityCode FROM " + pub.yssGetTableName("tb_data_integrated") +
                " WHERE FCheckState = 1" +
                " AND FPortCode IN (" + operSql.sqlCodes(monetaryFundBean.getPortCodes()) + ")" +
                " AND FExchangeDate BETWEEN " + dbl.sqlDate(monetaryFundBean.getStartDate()) +
                " AND " + dbl.sqlDate(monetaryFundBean.getEndDate()) +
                " ) sto ON a.FSECURITYCODE = sto.FSECURITYCODE" +
                " LEFT JOIN Tb_Sys_Assetgroup h on h.fassetgroupcode  =  " + dbl.sqlString(pub.getPrefixTB()) +
                " WHERE a.FCheckState = 1" +
                " ORDER BY a.FSecurityCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(this.buildRowShowStr(rs, this.getListView3ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.monetaryFundBean.setMonetaryFundArrIncludeAssetGroup(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_PARA_MONETARYFUND_CLOSEDTYPE +
                                        "," + YssCons.YSS_PARA_MONETARYFUND_INTERESTTYPE);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;

        } catch (Exception ex) {
            throw new YssException("查询在选中日期有库存的货币基金出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getListViewGroupData1() throws YssException {
        return "";
    }

    public String getListViewGroupData2() throws YssException {
        return "";
    }

    public String getListViewGroupData3() throws YssException {
        String sGroups = "";
        String sPrefixTB = pub.getPrefixTB();
        String[] assetGroupCodes = this.monetaryFundBean.getAssetGroupCode().split(YssCons.YSS_GROUPSPLITMARK);
        String[] strPortCodes = this.monetaryFundBean.getPortCodes().split(YssCons.YSS_GROUPSPLITMARK);
        try {
            for (int i = 0; i < assetGroupCodes.length; i++) {
                this.assetGroupCode = assetGroupCodes[i];
                pub.setPrefixTB(this.assetGroupCode);
                this.monetaryFundBean.setPortCodes(strPortCodes[i]);
                String sGroup = this.getListViewData3();
                sGroups = sGroups + sGroup + YssCons.YSS_GROUPSPLITMARK;
            }
            if (sGroups.length() > 7) {
                sGroups = sGroups.substring(0, sGroups.length() - 7);
            }
            return sGroups;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            pub.setPrefixTB(sPrefixTB);
        }

    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        this.monetaryFundBean = new MonetaryFundBean();
        if(pub == null){
            pub = new YssPub();
        }
        monetaryFundBean.setYssPub(pub);

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
        this.monetaryFundBean.parseRowStr(sRowStr);
        this.recycled = sRowStr;
    }

    public String buildRowStr() throws YssException {
        return this.monetaryFundBean.buildRowStr();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }
}
