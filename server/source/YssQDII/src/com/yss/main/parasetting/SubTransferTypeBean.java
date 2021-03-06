package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class SubTransferTypeBean
    extends BaseDataSettingBean implements IDataSetting {

    private String typeCode;
    private String typeName;
    private String superTypeCode;
    private String superTypeName;
    private String typeDesc;
    private String oldTypeCode;
    private SubTransferTypeBean filterType;

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {

        StringBuffer buf = new StringBuffer();
        buf.append(typeCode.trim()).append("\t");
        buf.append(typeName.trim()).append("\t");
        buf.append(superTypeCode.trim()).append("\t");
        buf.append(superTypeName.trim()).append("\t");
        buf.append(typeDesc.trim()).append("\t");

        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {

        try {
            dbFun.checkInputCommon(btOper,
                                   "Tb_Base_SubTransferType",
                                   "FSubTsfTypeCode",
                                   this.typeCode, this.oldTypeCode);
        } catch (YssException ex) {
        }

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
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = " select y.* from " +
            " (select FSubTsfTypeCode from Tb_Base_SubTransferType " +
            " where FCheckState <> 2 group by FSubTsfTypeCode) x join " +
            " (select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTsfTypeName  from " +
            " Tb_Base_SubTransferType a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
            " left join( select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType" +
            ")d on a.FTsfTypeCode=d.FTsfTypeCode " +
            buildFilterSql() +
            ") y on x.FSubTsfTypeCode =y.FSubTsfTypeCode " +
            "order by a.FCheckState, a.FCreateTime desc ";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {

        String strSql = "";
        strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTsfTypeName  from " +
            " Tb_Base_SubTransferType a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
            " left join( select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType) d on a.FTsfTypeCode=d.FTsfTypeCode " +
            ( (buildFilterSql().length() > 0) ? buildFilterSql() + " and " : " where ") + " a.FCheckState = 1 order by a.FSubTsfTypeCode, a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);
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
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";

        strSql = " select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FSubTsfTypeName from Tb_Base_SubTransferType a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
            " left join(select FSubTsfTypeCode,FSubTsfTypeName from Tb_Base_TransferType)d on a.FSubTsfTypeCode=d.FSubTsfTypeCode" +
            buildFilterSql() +
            " order by a.FCheckState, a.FCreateTime desc";
        return this.builderListViewData(strSql);

    }

    /**
     * getSetting
     *
     * @return IParaSetting
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
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {

        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.typeCode = reqAry[0];
            this.typeName = reqAry[1];
            this.superTypeCode = reqAry[2];
            this.superTypeName = reqAry[3];
            this.typeDesc = reqAry[4];
            super.checkStateId = Integer.parseInt(reqAry[5]);
            this.oldTypeCode = reqAry[6];

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new SubTransferTypeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析调拨子类型信息出错", e);
        }

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
     *
     * @param btOper byte
     */
    /* public void saveSetting(byte btOper) throws YssException{
        Connection conn = dbl.loadConnection();
     boolean bTrans = false;
     String strSql = "";
     try {
        if (btOper == YssCons.OP_ADD) {
           strSql =
                "insert into Tb_Base_SubTransferType(FSubTsfTypeCode,FSubTsfTypeName,FTsfTypeCode, " +
                 " FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                 " values(" + dbl.sqlString(this.typeCode) + "," +
                 dbl.sqlString(this.typeName) + "," +
                 dbl.sqlString(this.superTypeCode) + "," +
              //   dbl.sqlString(this.superTypeName) + "," +
                 dbl.sqlString(this.typeDesc) + "," +
                 (pub.getSysCheckState()?"0":"1") + "," +
                 dbl.sqlString(this.creatorCode) + ", " +
                 dbl.sqlString(this.creatorTime) + "," +
                 (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
        }
        else if (btOper == YssCons.OP_EDIT) {
         strSql = "update Tb_Base_SubTransferType set FSubTsfTypeCode=" +
                  dbl.sqlString(this.typeCode) + ",FSubTsfTypeName=" +
                  dbl.sqlString(this.typeName) + ",FTsfTypeCode="+
                  dbl.sqlString(this.superTypeCode) + ",FDesc="+
                  dbl.sqlString(this.typeDesc) + ",FCheckState = " +
                  (pub.getSysCheckState()?"0":"1") + ",FCreator=" +
                  dbl.sqlString(this.creatorCode) +",FCreateTime="+
                  dbl.sqlString(this.creatorTime)+ ",FCheckUser = " +
                  (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
          "where FSubTsfTypeCode=" + dbl.sqlString(this.oldTypeCode);
        }

        else if (btOper == YssCons.OP_DEL) {
           strSql = "update Tb_Base_SubTransferType set FCheckState = " +
                 this.checkStateId + ", FCheckUser = " +
                 dbl.sqlString(pub.getUserCode()) +
                 ", FCheckTime = '" +
                 YssFun.formatDatetime(new java.util.Date()) + "'" +
                 " where FSubTsfTypeCode = " +
                 dbl.sqlString(this.typeCode);

        }
        else if (btOper == YssCons.OP_AUDIT) {

           System.out.println(this.checkStateId);

           strSql = "update Tb_Base_SubTransferType set FCheckState = " +
                 this.checkStateId + ", FCheckUser = " +
                 dbl.sqlString(pub.getUserCode()) +
                 ",FCheckTime = '" +
                 YssFun.formatDatetime(new java.util.Date()) + "'" +
                 "where FSubTsfTypeCode="+ dbl.sqlString(this.typeCode);

        }
        conn.setAutoCommit(false);
        bTrans = true;
        dbl.executeSql(strSql);
        conn.commit();
        bTrans = false;
        conn.setAutoCommit(true);
     }
     catch (Exception e) {
        throw new YssException("设置调拨子类型信息出错！", e);
     }
     finally {
        dbl.endTransFinal(conn, bTrans);
     }

     }*/





    /**
     * saveSetting
     *
     * @param btOper byte
     */
    /* public void saveSetting(byte btOper) throws YssException{
        Connection conn = dbl.loadConnection();
     boolean bTrans = false;
     String strSql = "";
     try {
        if (btOper == YssCons.OP_ADD) {
           strSql =
                "insert into Tb_Base_SubTransferType(FSubTsfTypeCode,FSubTsfTypeName,FTsfTypeCode, " +
                 " FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                 " values(" + dbl.sqlString(this.typeCode) + "," +
                 dbl.sqlString(this.typeName) + "," +
                 dbl.sqlString(this.superTypeCode) + "," +
              //   dbl.sqlString(this.superTypeName) + "," +
                 dbl.sqlString(this.typeDesc) + "," +
                 (pub.getSysCheckState()?"0":"1") + "," +
                 dbl.sqlString(this.creatorCode) + ", " +
                 dbl.sqlString(this.creatorTime) + "," +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
        }
        else if (btOper == YssCons.OP_EDIT) {
         strSql = "update Tb_Base_SubTransferType set FSubTsfTypeCode=" +
                  dbl.sqlString(this.typeCode) + ",FSubTsfTypeName=" +
                  dbl.sqlString(this.typeName) + ",FTsfTypeCode="+
                  dbl.sqlString(this.superTypeCode) + ",FDesc="+
                  dbl.sqlString(this.typeDesc) + ",FCheckState = " +
                  (pub.getSysCheckState()?"0":"1") + ",FCreator=" +
                  dbl.sqlString(this.creatorCode) +",FCreateTime="+
                  dbl.sqlString(this.creatorTime)+ ",FCheckUser = " +
     (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
          "where FSubTsfTypeCode=" + dbl.sqlString(this.oldTypeCode);
        }

        else if (btOper == YssCons.OP_DEL) {
           strSql = "update Tb_Base_SubTransferType set FCheckState = " +
                 this.checkStateId + ", FCheckUser = " +
                 dbl.sqlString(pub.getUserCode()) +
                 ", FCheckTime = '" +
                 YssFun.formatDatetime(new java.util.Date()) + "'" +
                 " where FSubTsfTypeCode = " +
                 dbl.sqlString(this.typeCode);

        }
        else if (btOper == YssCons.OP_AUDIT) {

           System.out.println(this.checkStateId);

           strSql = "update Tb_Base_SubTransferType set FCheckState = " +
                 this.checkStateId + ", FCheckUser = " +
                 dbl.sqlString(pub.getUserCode()) +
                 ",FCheckTime = '" +
                 YssFun.formatDatetime(new java.util.Date()) + "'" +
                 "where FSubTsfTypeCode="+ dbl.sqlString(this.typeCode);

        }
        conn.setAutoCommit(false);
        bTrans = true;
        dbl.executeSql(strSql);
        conn.commit();
        bTrans = false;
        conn.setAutoCommit(true);
     }
     catch (Exception e) {
        throw new YssException("设置调拨子类型信息出错！", e);
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
                "insert into Tb_Base_SubTransferType(FSubTsfTypeCode,FSubTsfTypeName,FTsfTypeCode, " +
                " FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                " values(" + dbl.sqlString(this.typeCode) + "," +
                dbl.sqlString(this.typeName) + "," +
                dbl.sqlString(this.superTypeCode) + "," +
                //   dbl.sqlString(this.superTypeName) + "," +
                dbl.sqlString(this.typeDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + ", " +
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
            throw new YssException("增加调拨子类型信息出错", e);
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
            strSql = "update Tb_Base_SubTransferType set FSubTsfTypeCode=" +
                dbl.sqlString(this.typeCode) + ",FSubTsfTypeName=" +
                dbl.sqlString(this.typeName) + ",FTsfTypeCode=" +
                dbl.sqlString(this.superTypeCode) + ",FDesc=" +
                dbl.sqlString(this.typeDesc) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator=" +
                dbl.sqlString(this.creatorCode) + ",FCreateTime=" +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                "where FSubTsfTypeCode=" + dbl.sqlString(this.oldTypeCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改调拨子类型信息出错", e);
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
            strSql = "update Tb_Base_SubTransferType set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FSubTsfTypeCode = " +
                dbl.sqlString(this.typeCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除调拨子类型信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update Tb_Base_SubTransferType set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ",FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                "where FSubTsfTypeCode=" + dbl.sqlString(this.typeCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("审核调拨子类型信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    private String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.typeCode.length() != 0) {
                sResult = sResult + "and a.FSubTsfTypeCode like'" +
                    filterType.typeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.typeName.length() != 0) {
                sResult = sResult + "and a.FSubTsfTypeName like'" +
                    filterType.typeName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.superTypeCode.length() != 0) {
                sResult = sResult + "and a.FTsfTypeCode like'" +
                    filterType.superTypeCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.typeDesc.length() != 0) {
                sResult = sResult + "and a.FDesc like'" +
                    filterType.typeDesc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    public void setSecrityIssuserAttr(ResultSet rs) throws SQLException {
        this.typeCode = rs.getString("FSubTsfTypeCode") + "";
        this.typeName = rs.getString("FSubTsfTypeName") + "";
        this.superTypeCode = rs.getString("FTsfTypeCode") + "";

        this.superTypeName = rs.getString("FTsfTypeName") + "";
        this.typeDesc = rs.getString("FDesc") + "";

        super.setRecLog(rs);
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.typeCode = rs.getString("FSubTsfTypeCode") + "";
                this.typeName = rs.getString("FSubTsfTypeName") + "";
                this.superTypeCode = rs.getString("FTsfTypeCode") + "";
                this.superTypeName = rs.getString("FTsfTypeName") + "";
                this.typeDesc = rs.getString("FDesc") + "";

                super.setRecLog(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取调拨子类型信息出错", e);
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
        SubTransferTypeBean befEditBean = new SubTransferTypeBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = " select y.* from " +
                " (select FSubTsfTypeCode from Tb_Base_SubTransferType " +
                " where FCheckState <> 2 group by FSubTsfTypeCode) x join " +
                " (select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTsfTypeName  from " +
                " Tb_Base_SubTransferType a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join( select FTsfTypeCode,FTsfTypeName from Tb_Base_TransferType" +
                ")d on a.FTsfTypeCode=d.FTsfTypeCode " +
                " where a.FSubTsfTypeCode =" + dbl.sqlString(this.oldTypeCode) +
                ") y on x.FSubTsfTypeCode =y.FSubTsfTypeCode " +
                "order by y.FCheckState, y.FCreateTime desc ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.typeCode = rs.getString("FSubTsfTypeCode") + "";
                befEditBean.typeName = rs.getString("FSubTsfTypeName") + "";
                befEditBean.superTypeCode = rs.getString("FTsfTypeCode") + "";
                befEditBean.superTypeName = rs.getString("FTsfTypeName") + "";
                befEditBean.typeDesc = rs.getString("FDesc") + "";
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    /**
     * deleteRecycleData
     */
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
