package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class VchSubAttrBean
    extends BaseDataSettingBean implements IDataSetting {
    private String subAttrCode = ""; //科目属性代码
    private String subAttrName = ""; //科目属性名称
    private String subType = ""; //科目类别
    private String desc = ""; //描述
    private String oldSubAttrCode = "";
    private VchSubAttrBean filterType = null;
    public String getSubAttrCode() {
        return subAttrCode;
    }

    public String getSubAttrName() {
        return subAttrName;
    }

    public String getDesc() {
        return desc;
    }

    public String getSubType() {
        return subType;
    }

    public void setOldSubAttrCode(String oldSubAttrCode) {
        this.oldSubAttrCode = oldSubAttrCode;
    }

    public void setSubAttrCode(String subAttrCode) {
        this.subAttrCode = subAttrCode;
    }

    public void setSubAttrName(String subAttrName) {
        this.subAttrName = subAttrName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public void setFilterType(VchSubAttrBean filterType) {
        this.filterType = filterType;
    }

    public String getOldSubAttrCode() {
        return oldSubAttrCode;
    }

    public VchSubAttrBean getFilterType() {
        return filterType;
    }

    public VchSubAttrBean() {
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.subAttrCode).append("\t");
        buf.append(this.subAttrName).append("\t");
        buf.append(this.subType).append("\t");
        buf.append(this.desc).append("\t");
        //buf.append(this.subType).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\f") >= 0) {
                sTmpStr = sRowStr.split("\r\f")[0];
            } else {
                sTmpStr = sRowStr;
                reqAry = sTmpStr.split("\t");
            }
          //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            if(reqAry == null)
            	return ;
            //---end---
            this.subAttrCode = reqAry[0];
            this.subAttrName = reqAry[1];
            this.subType = reqAry[2];
            this.desc = reqAry[3];
            this.checkStateId = Integer.parseInt(reqAry[4]);
            this.oldSubAttrCode = reqAry[5];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new VchSubAttrBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析凭证科目属性设置出错!");
        }

    }

    public String addSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String strSql = "";
        try {
            conn = dbl.loadConnection();
            strSql = "insert into " + " Tb_Vch_SubAttr " +
                " (FSubAttrCode,FSubAttrName,FSubType,FDesc,FCheckState," +
                "FCreator,FCreateTime,FCheckUser,FCheckTime) values(" +
                dbl.sqlString(this.subAttrCode) + "," +
                dbl.sqlString(this.subAttrName) + "," +
                dbl.sqlString(this.subType) + "," +
                dbl.sqlString(this.desc) + "," +
                "0" + "," +
                (pub.getSysCheckState() ? "' '" : this.creatorCode) + "," +
                "'" + YssFun.formatDatetime(new java.util.Date()) + "'," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + " )";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增凭证科目属性设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public String editSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "update " + " Tb_Vch_SubAttr " +
                " set FSubAttrCode = " +
                dbl.sqlString(this.subAttrCode) + ", FSubAttrName = " +
                dbl.sqlString(this.subAttrName) + ", FSubType = " +
                dbl.sqlString(this.subType) + ", FDesc = " +
                dbl.sqlString(this.desc) + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FSubAttrCode = " +
                dbl.sqlString(this.oldSubAttrCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改凭证科目属性设置出错", e);
        }
        return "";
    }

    public void delSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "delete from " + " Tb_Vch_SubAttr " +
                " where FSubAttrCode = " + dbl.sqlString(this.oldSubAttrCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除凭证科目属性设置出错", e);
        }
    }

    public void checkSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核凭证科目属性设置出错", e);
        }
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    private String builerFilter() {
        String reSql = "";

        return reSql;
    }

    private void setVchAttr(ResultSet rs) throws SQLException {
        this.subAttrCode = rs.getString("FSubAttrCode");
        this.subAttrName = rs.getString("FSubAttrName");
        this.subType = rs.getString("FSubType");
        this.desc = rs.getString("FDesc");
        super.setRecLog(rs);
    }

    public String getListViewData1() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = ""; //词汇类型对照字符串
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = getListView1Headers();
            sqlStr =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FVocName as FSubTypeName from " +
                " Tb_Vch_SubAttr " +
                " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join Tb_Fun_Vocabulary d on a.FSubType = d.FVocCode and d.FVocTypeCode = " + //2007.11.29 修改 蒋锦 使用dbl.sqlToChar()处理"a.FTradeInd"，否则在使用DB2数据库时会报数据类型错误
                dbl.sqlString(YssCons.YSS_VCH_SUBATTR) +
                builerFilter() +
                " order by a.FCheckState, a.FCreateTime desc, a.FCheckTime desc";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setVchAttr(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_VCH_SUBATTR);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String getListViewData2() throws YssException {
        Connection conn = null;
        String sqlStr = "", sHeader = "", sShowDataStr = "", sAllDataStr = "";
        StringBuffer sData = new StringBuffer();
        StringBuffer sAllData = new StringBuffer();
        ResultSet rs = null;
        try {
            conn = dbl.loadConnection();
            sHeader = "属性代码\t属性名称\t描述";
            sqlStr =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                " Tb_Vch_SubAttr " +
                " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " where a.FCheckState = 1 order by 1";
            conn.setAutoCommit(false);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                sData.append(rs.getString("FSubAttrCode")).append("\t");
                sData.append(rs.getString("FSubAttrName")).append("\t");
                sData.append(rs.getString("FSubType")).append("\t");
                sData.append(rs.getString("FDesc")).append(YssCons.
                    YSS_LINESPLITMARK);
                this.setVchAttr(rs);
                sAllData.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (sData.toString().length() > 2) {
                sShowDataStr = sData.toString().substring(0,
                    sData.toString().length() - 2);
            }

            if (sAllData.toString().length() > 2) {
                sAllDataStr = sAllData.toString().substring(0,
                    sAllData.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;

        } catch (Exception e) {
            throw new YssException("获取科目属性出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getTreeViewData1() {
        return "";
    }

    public String getTreeViewData3() {
        return "";
    }

    public String getTreeViewData2() {
        return "";
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               "Tb_Vch_SubAttr",
                               "FSubAttrCode",
                               this.subAttrCode,
                               this.oldSubAttrCode);
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getOperValue(String sType) {
        return "";
    }

    public String getBeforeEditData() {
        return "";
    }

    public void deleteRecycleData() {
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
