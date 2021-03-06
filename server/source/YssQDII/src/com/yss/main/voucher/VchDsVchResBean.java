package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class VchDsVchResBean
    extends BaseDataSettingBean implements IDataSetting {
    ///凭证资源表
    private String resTabName = ""; //资源表名
    private String resTabDesc = ""; //资源表描述
    private String fieldName = ""; //字段名
    private String fieldDesc = ""; //字段描述
    private int relaField = 0; //关联字段
    private String fieldType = ""; //字段类型
    private String desc = "";
    private String oldResTabName = "";
    private String oldFieldName = "";
    private VchDsVchResBean filterType = null;
    private String sDsVchResSub = ""; //存放详细
    private String sRecycled = ""; //回收站处理字段 add by leeyu 2008-10-21 BUG:0000491
    public String getDesc() {
        return desc;
    }

    public String getOldResTabName() {
        return oldResTabName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getRelaField() {
        return relaField;
    }

    public VchDsVchResBean getFilterType() {
        return filterType;
    }

    public String getResTabDesc() {
        return resTabDesc;
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public String getOldFieldName() {
        return oldFieldName;
    }

    public void setResTabName(String resTabName) {
        this.resTabName = resTabName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOldResTabName(String oldResTabName) {
        this.oldResTabName = oldResTabName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setRelaField(int relaField) {
        this.relaField = relaField;
    }

    public void setFilterType(VchDsVchResBean filterType) {
        this.filterType = filterType;
    }

    public void setResTabDesc(String resTabDesc) {
        this.resTabDesc = resTabDesc;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public void setOldFieldName(String oldFieldName) {
        this.oldFieldName = oldFieldName;
    }

    public String getResTabName() {
        return resTabName;
    }

    public String getOperValue(String sType) {
        return "";
    }

    public void checkInput(byte bType) throws YssException {
        dbFun.checkInputCommon(bType,
                               pub.yssGetTableName("Tb_Vch_ResTab"),
                               "FResTabName",
                               this.resTabName,
                               this.oldResTabName);
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.sDsVchResSub = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            sRecycled = sTmpStr; // add by leeyu 2008-10-21 BUG:0000491
            this.resTabName = reqAry[0];
            if (reqAry[0].length() == 0) {
                this.resTabName = " ";
            }
            this.resTabDesc = reqAry[1];
            if (reqAry[1].length() == 0) {
                this.resTabDesc = " ";
            }
            this.fieldName = reqAry[2];
            if (reqAry[2].length() == 0) {
                this.fieldName = " ";
            }
            this.fieldDesc = reqAry[3];
            if (reqAry[3].length() == 0) {
                this.fieldDesc = " ";
            }
            this.relaField = Integer.parseInt(reqAry[4]);
            this.fieldType = reqAry[5];
            if (reqAry[5].length() == 0) {
                this.fieldType = " ";
            }
            this.desc = reqAry[6];
            if (reqAry[6].length() == 0) {
                this.desc = " ";
            }
            this.oldResTabName = reqAry[7];
            this.oldFieldName = reqAry[8];
            this.checkStateId = Integer.parseInt(reqAry[9]);
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new VchDsVchResBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析凭证资源信息出错!");
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.resTabName).append("\t");
        buf.append(this.resTabDesc).append("\t");
        buf.append(this.fieldName).append("\t");
        buf.append(this.fieldDesc).append("\t");
        buf.append(this.relaField).append("\t");
        buf.append(this.fieldType).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.oldResTabName).append("\t");
        buf.append(this.oldFieldName).append("\t");
        buf.append(this.checkStateId).append("\t");
        //    buf.append(super.buildRecLog());
        return buf.toString();
    }

    private String getDsVchresAttr(String sqlStr) throws YssException {
        ResultSet rs = null;
        String sResult = "";
        try {
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                this.resTabName = rs.getString("FResTabName") + "";
                this.resTabDesc = rs.getString("FResTableDesc") + "";
                this.fieldName = rs.getString("FFieldName") + "";
                this.fieldDesc = rs.getString("FFieldDesc") + "";
                this.relaField = rs.getInt("FRelaField");
                this.fieldType = rs.getString("FFieldType") + "";
                this.desc = rs.getString("FDesc");
                sResult += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (sResult.length() > 2) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;

        } catch (Exception e) {
            throw new YssException("访问资源凭证报表出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public void checkSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            //=====增加回收站处理功能  by leeyu 2008-10-21 BUG:0000491
            conn.setAutoCommit(false);
            String[] arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                sqlStr = "update " + pub.yssGetTableName("Tb_Vch_ResTab") +
                    " set FCheckState=" + this.checkStateId + "," +
                    " FCheckUser=" + dbl.sqlString(pub.getUserCode()) + "," +
                    " FCheckTime='" + YssFun.formatDatetime(new java.util.Date()) +
                    "'" +
                    " where FResTabName=" + dbl.sqlString(this.resTabName);
                dbl.executeSql(sqlStr);
            }
            //conn.setAutoCommit(false);
            bTrans = true;
            //dbl.executeSql(sqlStr);
            //=======2008-10-21
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核资源凭证数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String addSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        int iRow = 0;
        try {
            conn = dbl.loadConnection();
            sqlStr = "select count(*) from " + pub.yssGetTableName("Tb_Vch_ResTab") +
                " where FResTabName =" + dbl.sqlString(this.resTabName);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                iRow = rs.getInt(1);
            }
            if (iRow == 0) {
                sqlStr = "insert into " + pub.yssGetTableName("Tb_Vch_ResTab") +
                    "(FResTabName,FResTabDesc,FFieldName,FFieldDesc," +
                    "FRelaField,FFieldType,FDesc,FCheckState,FCreator,FCreateTime) values(" +
                    dbl.sqlString(this.resTabName) + "," +
                    dbl.sqlString(this.resTabDesc) + "," +
                    dbl.sqlString(this.fieldName) + "," +
                    dbl.sqlString(this.fieldDesc) + "," +
                    this.relaField + "," +
                    dbl.sqlString(this.fieldType) + "," +
                    dbl.sqlString(this.desc) + "," +
                    (pub.getSysCheckState() ? "0" : "1") + "," +
                    (pub.getSysCheckState() ? "' '" : dbl.sqlString(creatorCode)) +
                    "," +
                    (pub.getSysCheckState() ? "' '" :
                     dbl.sqlDate(new java.util.Date())) + ")";
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(sqlStr);
                if (sDsVchResSub.trim().length() > 0) {
                    VchDsVchResSubBean sub = new VchDsVchResSubBean();
                    sub.setYssPub(pub);
                    sub.setResTabName(this.resTabName);
                    //   sqlStr="delete from "+pub.yssGetTableName("Tb_Vch_ResTab")+
                    //         " where FResTabName="+dbl.sqlString(this.resTabName);
                    //   dbl.executeSql(sqlStr);
                    sub.saveMutliSetting(sDsVchResSub);
                }
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            } else {
                this.editSetting();
            }
        } catch (Exception e) {
            throw new YssException("新增资源凭证数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //close the cursor finally modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
            dbl.endTransFinal(conn, bTrans);
        }
        return "";

        // return this.editSetting();
    }

    public String editSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            sqlStr = "update " + pub.yssGetTableName("Tb_Vch_ResTab") +
                " set FresTabName=" + dbl.sqlString(this.resTabName) + "," +
                " FResTabDesc=" + dbl.sqlString(this.resTabDesc) +
                " where FResTabName=" + dbl.sqlString(this.oldResTabName);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改资源凭证数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public void delSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            sqlStr = "update " + pub.yssGetTableName("Tb_Vch_ResTab") +
                " set FCheckState =" + this.checkStateId + "," +
                " FCreator=" + dbl.sqlString(this.creatorCode + " ") + "," +
                " FCreateTime=" + dbl.sqlString(this.creatorTime + " ") +
                " where FResTabName=" + dbl.sqlString(this.oldResTabName);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除资源凭证数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    private String buildFilterSql() throws YssException {
        String sqlStr = "";
        if (this.filterType != null) {
            sqlStr += " where 1=1";
			/**shashijie 2012-7-2 STORY 2475 */
            if (this.filterType.resTabName != null && this.filterType.resTabName.length() != 0) {
                sqlStr += " and a.FResTabName like '" + dbl.sqlString(this.filterType.resTabName).replaceAll("'", "''") + "%'";
            }
            if (this.filterType.resTabDesc != null && this.filterType.resTabDesc.length() != 0) {
                sqlStr += " and a.FResTabDesc like '" + dbl.sqlString(this.filterType.resTabDesc).replaceAll("'", "''") + "%'";
            }
			/**end*/
        }
        return sqlStr;
    }

    public String getListViewData1() throws YssException {
        String strSql = "";
        String sHeader = "";
        String sShowDataStr = "", sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String sVocStr = "";
        try {
            //sHeader = this.getListView1Headers();
            sHeader = "资源表名\t资源表描述";
            strSql = "select distinct FResTabName,FCheckState,FResTabDesc from " + pub.yssGetTableName("Tb_Vch_ResTab") +
                " order by FResTabName,FCheckState";
            /*  "select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,n.Fvocname as FFieldTypes from " +
              pub.yssGetTableName("Tb_Vch_ResTab") + " a"
              + " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator=b.FUserCode"
              + " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser=c.FUserCode"
              +" left join Tb_Fun_Vocabulary n on a.FFieldType = n.FVocCode and n.FVocTypeCode = "
              + dbl.sqlString(YssCons.YSS_FFieldType)
              + this.buildFilterSql() +
              " order by FCheckState,FCreateTime desc";*/
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FResTabName")).append("\t");
                bufShow.append(rs.getString("FResTabDesc")).append("\t");
                bufShow.
                    append(YssCons.YSS_LINESPLITMARK);
                this.resTabName = rs.getString("FResTabName");
                this.resTabDesc = rs.getString("FResTabDesc");
                this.fieldName = "";
                this.fieldDesc = "";
                //   this.relaField = rs.getInt("FRelaField");
                //    this.fieldType = rs.getString("FFieldTypes");
                this.desc = "";
                this.checkStateId = rs.getInt("FCheckState");
                //this.setResultSetAttr(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_FFieldType + "," +
                                        YssCons.YSS_FKey);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                "FResTabName\tFResTabDesc\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取资源凭证数据信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.resTabName = rs.getString("FResTabName");
        this.resTabDesc = rs.getString("FResTabDesc");
        this.fieldName = rs.getString("FFieldName");
        this.fieldDesc = rs.getString("FFieldDesc");
        this.relaField = rs.getInt("FRelaField");
        this.fieldType = rs.getString("FFieldType");
        this.desc = rs.getString("FDesc");
        super.setRecLog(rs);
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

    public String getTreeViewData2() {
        return "";
    }

    public String getTreeViewData3() {
        return "";
    }

    public String getBeforeEditData() {
        return "";
    }

    public String getAllSetting() {
        return "";
    }

    public IDataSetting getSetting() {
        return null;
    }

    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    public VchDsVchResBean() {
    }

    /**
     * deleteRecycleData BUG：0000491 增加回收站的功能 by leeyu 2008-10-21
     */
    public void deleteRecycleData() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            String[] arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                sqlStr = "delete " + pub.yssGetTableName("Tb_Vch_ResTab") +
                    " where FResTabName=" + dbl.sqlString(this.resTabName);
                dbl.executeSql(sqlStr);
            }
            bTrans = true;
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除资源凭证数据出错", e);
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
