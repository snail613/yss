package com.yss.main.funsetting;

import java.io.*;
import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class CommonParamsSubBean
    extends BaseDataSettingBean implements IDataSetting, Serializable {

    private String sCPTypeCode = ""; //参数类型代码
    private String sCPTypeName = ""; //参数类型名称
    private String sParamCode = "";
    private String sParamName = "";
    private String sParamValue = "";
    private String sValueDesc = "";
    private String sDesc = ""; //描述
    private String sOldCPTypeCode = "";
    private String sOldParamCode = "";

    public int checkStateId;
    private CommonParamsSubBean filterType;

    public void checkInput(byte btOper) throws YssException {

    }

    public String addSetting() throws YssException {
        return "";
    }

    public String editSetting() throws YssException {
        return "";
    }

    public void delSetting() throws YssException {

    }

    public void checkSetting() throws YssException {
    }

    public IDataSetting getSetting() throws YssException {
        return null;
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

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";

            if (this.filterType.sCPTypeCode.length() != 0) {
                sResult = sResult + " and a.FCPTypeCode = '" +
                    filterType.sCPTypeCode.replaceAll("'", "''") + "'";
            } else {
                sResult = sResult + " and 1=2 ";
            }

        }

        return sResult;

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
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }

            reqAry = sTmpStr.split("\t");

            this.sCPTypeCode = reqAry[0];
            this.sCPTypeName = reqAry[1];
            this.sParamCode = reqAry[2];
            this.sParamName = reqAry[3];
            this.sParamValue = reqAry[4];
            this.sValueDesc = reqAry[5];
            this.sDesc = reqAry[6];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new CommonParamsSubBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }

        } catch (Exception e) {
            throw new YssException("解析通用参数设置出错", e);
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();

        buf.append(this.sCPTypeCode).append("\t");
        buf.append(this.sCPTypeName).append("\t");
        buf.append(this.sParamCode).append("\t");
        buf.append(this.sParamName).append("\t");
        buf.append(this.sParamValue).append("\t");
        buf.append(this.sValueDesc).append("\t");
        buf.append(this.sDesc).append("\t");

        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            sHeader = this.getListView1Headers();
            strSql =
                "select  a.* from (select * from Tb_Fun_CommonParamsSub  where FCheckState <> 2) x join " +
                " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FCPTypeName as FCPTypeName,e.FCPAttrName as FParamName  " +
                " from Tb_Fun_CommonParamsSub a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCPTypeCode,FCPTypeName from Tb_Fun_CommonParams) d on a.FCPTypeCode = d.FCPTypeCode" +
                " left join (select FCPAttrCode,FCPAttrName from Tb_Fun_CommonParamsAttr) e on a.FParamCode = e.FCPAttrCode" +
                buildFilterSql() + ")a  on  x.FCPTypeCode =a.FCPTypeCode  and x.FParamCode=a.FParamCode order by a.FCPTypeCode";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.setCommonParamsAttr(rs);
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
            throw new YssException("获取通用子参数设置出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public void setCommonParamsAttr(ResultSet rs) throws YssException {
        try {
            this.sCPTypeCode = rs.getString("FCPTypeCode");
            this.sCPTypeName = rs.getString("FCPTypeName");
            this.sParamCode = rs.getString("FParamCode");
            this.sParamName = rs.getString("FParamName");
            this.sParamValue = rs.getString("FParamValue");
            this.sValueDesc = rs.getString("FValueDesc");

            this.sDesc = rs.getString("FDesc");

            super.setRecLog(rs);

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public String saveMutliSetting(String sMutilRowStr, int status, String statu) throws YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        try {
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            this.parseRowStr(sMutilRowAry[0]);
            strSql = "delete from  Tb_Fun_CommonParamsSub" +
                " where FCPTypeCode=" + dbl.sqlString(this.sCPTypeCode);

            dbl.executeSql(strSql);

            //---------------记入系统日志
            if (statu.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-通用子参数设置");
                sysdata.setStrCode(this.sCPTypeCode);
                sysdata.setStrName(this.sCPTypeName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }

            strSql =
                "insert into Tb_Fun_CommonParamsSub " +
                "(FCPTypeCode,FParamCode,FParamValue,FValueDesc,FDesc," +
                "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);
            for (int i = 0; i < sMutilRowAry.length; i++) {
                // if (i > 0) {
                this.parseRowStr(sMutilRowAry[i]);
                //  }
                if (this.sParamCode != null && !this.sParamCode.equals("")) {
                    pstmt.setString(1, this.sCPTypeCode);
                    pstmt.setString(2, this.sParamCode);
                    pstmt.setString(3, this.sParamValue);
                    pstmt.setString(4, this.sValueDesc);
                    pstmt.setString(5, this.sDesc);
                    pstmt.setInt(6, status);
                    pstmt.setString(7, this.creatorCode);
                    pstmt.setString(8, this.creatorTime);
                    pstmt.setString(9, (pub.getSysCheckState() ? " " : this.creatorCode));
                    pstmt.executeUpdate();

                    //---------------记入系统日志
                    if (statu.equalsIgnoreCase("1")) {
                        String sql = "insert into Tb_Fun_CommonParamsSub " +
                            "(FCPTypeCode,FParamCode,FParamValue,FValueDesc,FDesc," +
                            "FCheckState,FCreator,FCreateTime,FCheckUser)" +
                            " values(" + dbl.sqlString(this.sCPTypeCode) + "," + dbl.sqlString(this.sParamCode) +
                            "," + dbl.sqlString(this.sParamValue) + "," + dbl.sqlString(this.sValueDesc) + "," + dbl.sqlString(this.sDesc) +
                            "," + status + "," + dbl.sqlString(this.creatorCode) + "," +
                            dbl.sqlString(this.creatorTime) + "," +
                            (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + ")";

                        com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                            funsetting.SysDataBean();
                        sysdata.setYssPub(pub);
                        sysdata.setStrAssetGroupCode("Common");
                        sysdata.setStrFunName("新增-通用子参数设置");
                        sysdata.setStrCode(this.sCPTypeCode);
                        sysdata.setStrName(this.sCPTypeName);
                        sysdata.setStrUpdateSql(sql);
                        sysdata.setStrCreator(pub.getUserName());
                        sysdata.addSetting();
                    }
                }
            }

        } catch (Exception e) {
            throw new YssException("保存通用子参数设置信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pstmt);
            return "";
        }
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
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

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */


}
