package com.yss.main.cusreport;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.pojo.sys.*;
import com.yss.util.*;

public class RepParamCtlBean
    extends BaseDataSettingBean implements IDataSetting {

    private String CtlGrpCode = ""; //参数控件组代码
    private String CtlCode = ""; //参数控件代码
    private int ParamIndex; //参数索引
    private int CtlType; //控件类型
    private String Param = ""; //参数

    private RepParamCtlBean filterType;

    public RepParamCtlBean() {
    }

    public void setCtlGrpCode(String ctlGrpCode) {
        this.CtlGrpCode = ctlGrpCode;
    }

    public void setCtlCode(String ctlCode) {
        this.CtlCode = ctlCode;
    }

    public void setParamIndex(int paramIndex) {
        this.ParamIndex = paramIndex;
    }

    public void setCtlType(int ctlType) {
        this.CtlType = ctlType;
    }

    public void setParam(String param) {
        this.Param = param;
    }

    public String getCtlGrpCode() {
        return this.CtlGrpCode;
    }

    public String getCtlCode() {
        return this.CtlCode;
    }

    public int getParamIndex() {
        return this.ParamIndex;
    }

    public int getCtlType() {
        return this.CtlType;
    }

    public String getParam() {
        return this.Param;
    }

    public void setRepAttr(ResultSet rs) throws SQLException {
        this.CtlGrpCode = rs.getString("FCtlGrpCode") + "";
        this.CtlCode = rs.getString("FCtlCode") + "";
        this.ParamIndex = rs.getInt("FParamIndex");
        this.CtlType = rs.getInt("FCtlType");
        this.Param = rs.getString("FParam") + "";
        super.setRecLog(rs);
    }

    //配置ListView显示列时对特殊列的操作
    public void beforeBuildRowShowStr(YssCancel bCancel, String sColName,
                                      ResultSet rs, StringBuffer buf) throws
        SQLException {
        String sParam = "";
        String[] sParamAry = null;
        String sText = "";
        if (sColName.indexOf("FText") >= 0) {
            sParam = rs.getString("FParam");
            if (sParam != null && sParam.length() > 0) {
                sParamAry = sParam.split("\n");
                sText = sParamAry[sParamAry.length - 4];
            }
            buf.append(sText).append("\t");
            bCancel.setCancel(true);
        }
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.setRepAttr(rs);
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取参数控件信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
            pub.yssGetTableName("Tb_Rep_ParamCtl") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " where a.FCheckState <> 2 and a.FCtlGrpCode = " + dbl.sqlString(this.CtlGrpCode) +
            " order by a.FParamIndex";
        return this.builderListViewData(strSql);
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() {
        return "";
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
    public String getListViewData4() {
        return "";
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() {
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {
    }

    /**
     * checkSetting
     */
    public void checkSetting() {
    }

    /**
     * delSetting
     */
    public void delSetting() {
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() {
        return "";
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
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return saveMutliSetting(sMutilRowStr, false, "");
    }

    public String saveMutliSetting(String sMutilRowStr, boolean bIsTrans,
                                   String sOldCtlGrpCode) throws YssException {
        String[] sMutilRowAry = null;
        boolean bTrans = false;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        String oldCtlGrpCode = "";

        try {
            if (sMutilRowStr.length() == 0) {
                return "";
            }
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            this.parseRowStr(sMutilRowAry[0]);

            if (sOldCtlGrpCode.length() > 0) {
                oldCtlGrpCode = sOldCtlGrpCode;
            } else {
                oldCtlGrpCode = this.CtlGrpCode;
            }

            strSql = "delete from " + pub.yssGetTableName("Tb_Rep_ParamCtl") +
                " where FCtlGrpCode = " + dbl.sqlString(oldCtlGrpCode);

            if (!bIsTrans) {
                conn.setAutoCommit(false);
                bTrans = true;
            }
            dbl.executeSql(strSql);

            strSql =
                "insert into " + pub.yssGetTableName("Tb_Rep_ParamCtl") +
                "(FCtlGrpCode,FCtlCode,FParamIndex,FCtlType,FParam," +
                " FCheckState, FCreator, FCreateTime,FCheckUser)" +
                " Values (?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);

            for (int i = 0; i < sMutilRowAry.length; i++) {
                if (i > 0) {
                    this.parseRowStr(sMutilRowAry[i]);
                }
                if (this.CtlCode.trim().length() > 0) {
                    pstmt.setString(1, this.CtlGrpCode);
                    pstmt.setString(2, this.CtlCode);
                    pstmt.setInt(3, this.ParamIndex);
                    pstmt.setInt(4, this.CtlType);
                    pstmt.setString(5, this.Param);
                    pstmt.setString(6, (pub.getSysCheckState() ? "0" : "1"));
                    pstmt.setString(7, this.creatorCode);
                    pstmt.setString(8, this.creatorTime);
                    pstmt.setString(9, pub.getSysCheckState() ? " " : this.creatorCode);
                    pstmt.executeUpdate();
                }
            }

            if (!bIsTrans) {
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
            return "";
        } catch (Exception e) {
            throw new YssException("保存参数控件出错", e);
        } finally {
            dbl.closeStatementFinal(pstmt);
            if (!bIsTrans) {
                dbl.endTransFinal(conn, bTrans);
            }
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
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.CtlGrpCode).append("\t");
        buf.append(this.CtlCode).append("\t");
        buf.append(this.ParamIndex).append("\t");
        buf.append(this.CtlType).append("\t");
        buf.append(this.Param).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        if (sType.equalsIgnoreCase("Title")) {
            return this.getListView1Headers() + "\r\f\r\f\r\f" +
                this.getListView1ShowCols();
        }
        if (sType.equalsIgnoreCase("RepCtls")) {
            String strSql = "";
            String sAllDataStr = "";
            StringBuffer bufAll = new StringBuffer();
            ResultSet rs = null;
            try {
                strSql = "select a.*, b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                    pub.yssGetTableName("Tb_Rep_ParamCtl") + " a " +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " where a.FCheckState <> 2 and a.FCtlGrpCode = " + dbl.sqlString(this.CtlGrpCode) +
                    " order by a.FCheckState, a.FCreateTime desc";

                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    this.setRepAttr(rs);
                    bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }

                if (bufAll.toString().length() > 2) {
                    sAllDataStr = bufAll.toString().substring(0,
                        bufAll.toString().length() - 2);
                }

                return sAllDataStr;
            } catch (Exception e) {
                throw new YssException("获取参数控件信息出错", e);
            } finally {
                dbl.closeResultSetFinal(rs);
            }
        }

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
            this.CtlGrpCode = reqAry[0];
            this.CtlCode = reqAry[1];
            if (YssFun.isNumeric(reqAry[2])) {
                this.ParamIndex = Integer.parseInt(reqAry[2]);
            }
            if (YssFun.isNumeric(reqAry[3])) {
                this.CtlType = Integer.parseInt(reqAry[3]);
            }
            this.Param = reqAry[4];
            if (YssFun.isNumeric(reqAry[5])) {
                this.checkStateId = Integer.parseInt(reqAry[5]);
            }
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new RepParamCtlBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析参数控件设置请求出错", e);
        }
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
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
