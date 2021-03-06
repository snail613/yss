package com.yss.main.funsetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class SysDataBean
    extends BaseDataSettingBean implements IDataSetting {

    private String strAssetGroupCode = ""; //组合群代码
    private String strAssetGroupName = ""; //组合群名称
    private String strFunName = ""; ///功能名称
    private String strCode = ""; //代码
    private String strName = ""; //名称
    private String strUpdateSql = ""; //更新SQL
    private String strCreator = ""; //创建人、修改人
    private java.util.Date strCreateDate; //创建日期
    private String strCreateTime = ""; //创建时间
    private String sNum = "";
    private String allNum = ""; //被删除时的所有主键
    private String StartDate = "";
    private String EndDate = "";
    private SysDataBean filterType;
    private String strIsOnlyColumns = "0";  //add by fangjiang 2010.10.09 MS01787 QDV4赢时胜(上海开发部)2010年09月09日03_B

    public SysDataBean() {
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
        // String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        
        try {
            sHeader = this.getListView1Headers();
            //modify by fangjiang 2010.10.09 MS01787 QDV4赢时胜(上海开发部)2010年09月09日03_B 
            strSql =
                "select a.*,0 as FCheckState,b.FUserName as FCreatorName, b.FUserName as FCheckUserName,a.FCreator as FCheckUser,a.FCreateTime as FCheckTime,c.FAssetGroupName as FAssetGroupName" +
                " from TB_FUN_SYSDATA a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FAssetGroupCode,FAssetGroupName from TB_SYS_ASSETGROUP) c on a.FAssetGroupCode = c.FAssetGroupCode" +
                buildFilterSql() + " order by a.FNum";
            //----------------
            //add by fangjiang 2010.10.09 MS01787 QDV4赢时胜(上海开发部)2010年09月09日03_B 
            if (this.filterType!=null && this.filterType.strIsOnlyColumns.equals("1")) {
            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr();
            }
            yssPageInationBean.setsQuerySQL(strSql);
			yssPageInationBean.setsTableName("SYSDATA");
			rs = dbl.openResultSet_PageInation(yssPageInationBean);
            //------------------
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.setSysDataAttr(rs);
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
            //modify by fangjiang 2010.10.09 MS01787 QDV4赢时胜(上海开发部)2010年09月09日03_B
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr();
            //-------------------

        } catch (Exception e) {
            throw new YssException("获取TA分盘信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            //add by fangjiang 2010.10.09 MS01787 QDV4赢时胜(上海开发部)2010年09月09日03_B
            if (this.filterType!=null && this.filterType.strIsOnlyColumns.equals("1")) {
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }
            //------------
            if (this.filterType.strAssetGroupCode.length() != 0) {
                sResult = sResult + " and a.FAssetGroupCode like '%" +
                    filterType.strAssetGroupCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strFunName.length() != 0) {
                sResult = sResult + " and a.FFunName like '%" +
                    filterType.strFunName.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.strCode.length() != 0) {
                sResult = sResult + " and a.FCode like '%" +
                    filterType.strCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.strName.length() != 0) {
                sResult = sResult + " and a.FName like '%" +
                    filterType.strName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strUpdateSql.length() != 0) {
                sResult = sResult + " and a.FUpdateSql like '%" +
                    filterType.strUpdateSql.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.StartDate.length() != 0 &&
                this.filterType.EndDate.length() != 0) {
                sResult += " and a.fcreatedate Between " +
                    dbl.sqlDate(this.filterType.StartDate) + " and "
                    + dbl.sqlDate(this.filterType.EndDate);
            }
            if (!filterType.strCreateTime.equalsIgnoreCase("00:00:00") && filterType.strCreateTime.length() > 0) {
                sResult += " and a.FCreateTime =" + dbl.sqlString(filterType.strCreateTime);
            }
            if (filterType.strCreateDate != null && !YssFun.formatDate(filterType.strCreateDate).equals("9998-12-31")) {
                sResult += " and a.FCreateDate =" + dbl.sqlDate(filterType.strCreateDate);
            }

        }
        return sResult;

    }

    //解析
    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String[] reqAry = null;

        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");

            this.strAssetGroupCode = reqAry[0];
            this.strAssetGroupName = reqAry[1];
            this.strFunName = reqAry[2];
            this.strCode = reqAry[3];
            this.strName = reqAry[4];
            this.strUpdateSql = reqAry[5];
            this.strCreator = reqAry[6];
            if (!reqAry[7].equalsIgnoreCase("")) {
                this.strCreateDate = YssFun.toDate(reqAry[7]);
            }
            this.strCreateTime = reqAry[8];
            this.StartDate = reqAry[9];
            this.EndDate = reqAry[10];
            this.sNum = reqAry[11];
            //modify by fangjiang 2010.10.09 MS01787 QDV4赢时胜(上海开发部)2010年09月09日03_B
            this.strIsOnlyColumns = reqAry[12];
            if (reqAry[13].length() > 0) {
                this.allNum = reqAry[12].substring(0, reqAry[12].length() - 1);
            }
            //---------------
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new SysDataBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析系统数据信息出错", e);
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strAssetGroupCode).append("\t");
        buf.append(this.strAssetGroupName).append("\t");
        buf.append(this.strFunName).append("\t");
        buf.append(this.strCode).append("\t");
        buf.append(this.strName).append("\t");
        buf.append(this.strUpdateSql).append("\t");
        buf.append(this.strCreator).append("\t");

        if (this.strCreateDate != null) {
            buf.append(YssFun.formatDate(this.strCreateDate)).append("\t");
        }
        buf.append(this.strCreateTime).append("\t");
        buf.append(this.sNum).append("\t");

        return buf.toString();
    }

    public void setSysDataAttr(ResultSet rs) throws YssException {
        try

        {
            this.strAssetGroupCode = rs.getString("FAssetGroupCode");
            this.strAssetGroupName = rs.getString("FAssetGroupName");
            this.strFunName = rs.getString("FFunName");
            this.strCode = rs.getString("FCode");
            this.strName = rs.getString("FName");
            this.strUpdateSql = rs.getString("FUpdateSql");
            this.strCreator = rs.getString("FCreator");
            this.strCreateDate = rs.getDate("FCreateDate");
            this.strCreateTime = rs.getString("FCreateTime");
            this.sNum = rs.getString("FNum");
            super.setRecLog(rs);

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

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
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String sqlStr = "";
        try {
            this.strCreateDate = YssFun.toDate(YssFun.formatDate(new java.util.Date(), "yyyy-MM-dd"));
            if (this.sNum.equals("")) {
                this.sNum = "D" +
                    YssFun.formatDate(this.strCreateDate, "yyyyMMdd") +
                    dbFun.getNextInnerCode("Tb_Fun_SysData", dbl.sqlRight("FNUM", 6), "000001");
            }
            sqlStr = "insert into Tb_Fun_SysData(FNum,FAssetGroupCode,FFunName,FCode,FName,FUpdateSql,FCreator,FCreateDate,FCreateTime) values(" +
                dbl.sqlString(this.sNum) + ", " +
                dbl.sqlString(this.strAssetGroupCode) + ", " +
                dbl.sqlString(this.strFunName) + ", " +
                dbl.sqlString(this.strCode) + ", " +
                dbl.sqlString(this.strName) + ", " +
                dbl.sqlString(this.strUpdateSql) + ", " +
                dbl.sqlString(this.strCreator) + ", " +
                dbl.sqlDate(this.strCreateDate) + "," +
                dbl.sqlString(YssFun.formatDate(new java.util.Date(), "HH:mm:ss")) + ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("添加【" + strFunName + "," + strCode + "】的SQL语句到系统数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "delete from TB_FUN_SYSDATA where FNum in (" + this.allNum + ")";
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除系统数据信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

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
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
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
    public String getBeforeEditData() {
        return "";
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String EndDate) {
        this.EndDate = EndDate;
    }

    public SysDataBean getFilterType() {
        return filterType;
    }

    public void setFilterType(SysDataBean filterType) {
        this.filterType = filterType;
    }

    public String getStrAssetGroupCode() {
        return strAssetGroupCode;
    }

    public String getStrCode() {
        return strCode;
    }

    public String getStrCreateTime() {
        return strCreateTime;
    }

    public String getStrCreator() {
        return strCreator;
    }

    public String getStrFunName() {
        return strFunName;
    }

    public String getStrName() {
        return strName;
    }

    public String getStrUpdateSql() {
        return strUpdateSql;
    }

    public void setStrUpdateSql(String strUpdateSql) {
        this.strUpdateSql = strUpdateSql;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public void setStrFunName(String strFunName) {
        this.strFunName = strFunName;
    }

    public void setStrCreator(String strCreator) {
        this.strCreator = strCreator;
    }

    public void setStrCreateTime(String strCreateTime) {
        this.strCreateTime = strCreateTime;
    }

    public void setStrCode(String strCode) {
        this.strCode = strCode;
    }

    public void setStrAssetGroupCode(String strAssetGroupCode) {
        this.strAssetGroupCode = strAssetGroupCode;
    }

    public void setStartDate(String StartDate) {
        this.StartDate = StartDate;
    }

    public String getStartDate() {
        return StartDate;
    }

    public java.util.Date getStrCreateDate() {
        return strCreateDate;
    }

    public void setStrCreateDate(java.util.Date strCreateDate) {
        this.strCreateDate = strCreateDate;
    }

    public String getStrAssetGroupName() {
        return strAssetGroupName;
    }

    public String getSNum() {
        return sNum;
    }

    public String getAllNum() {
        return allNum;
    }

    public void setStrAssetGroupName(String strAssetGroupName) {
        this.strAssetGroupName = strAssetGroupName;
    }

    public void setSNum(String sNum) {
        this.sNum = sNum;
    }

    public void setAllNum(String allNum) {
        this.allNum = allNum;
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
