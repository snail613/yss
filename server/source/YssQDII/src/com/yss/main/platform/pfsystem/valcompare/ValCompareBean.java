package com.yss.main.platform.pfsystem.valcompare;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;
import oracle.jdbc.*;
import oracle.sql.*;

//==========================End MS00490======================================

public class ValCompareBean
    extends BaseDataSettingBean implements IDataSetting {
    private String comProjectCode = "";
    private String comProjectName = "";
    private String desc = "";
    private String comScript = "";
    private String oldComProjectCode = "";
    private String sRecycled = "";
    private ValCompareBean filterType = null;
    public String getDesc() {
        return desc;
    }

    public String getComProjectName() {
        return comProjectName;
    }

    public ValCompareBean getFilterType() {
        return filterType;
    }

    public String getOldComProjectCode() {
        return oldComProjectCode;
    }

    public String getComScript() {
        return comScript;
    }

    public String getComProjectCode() {
        return comProjectCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setComProjectName(String comProjectName) {
        this.comProjectName = comProjectName;
    }

    public void setFilterType(ValCompareBean filterType) {
        this.filterType = filterType;
    }

    public void setOldComProjectCode(String oldComProjectCode) {
        this.oldComProjectCode = oldComProjectCode;
    }

    public void setComScript(String comScript) {
        this.comScript = comScript;
    }

    public void setComProjectCode(String comProjectCode) {
        this.comProjectCode = comProjectCode;
    }

    public ValCompareBean() {
    }

//------------------------------------------------------------------------------------
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
            this.comProjectCode = reqAry[0];
            this.comProjectName = reqAry[1];
            this.comScript = reqAry[2].replaceAll("~@~", "\r\n"); //xuqiji 20090612 QDV4赢时胜（上海）2009年6月12日01_B MS00504    财务估值核对设置模块回收站还原和清除功能未实现

            this.desc = reqAry[3];
            this.checkStateId = YssFun.toInt(reqAry[4]);

            this.oldComProjectCode = reqAry[5];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new ValCompareBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析财务估值核对信息设置出错", e);
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.comProjectCode);
        buf.append("\t");
        buf.append(this.comProjectName);
        buf.append("\t");
        buf.append(this.comScript);
        buf.append("\t");
        buf.append(this.desc);
        buf.append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, "TB_PFSys_ValCompare",
                               "FComProjectCode",
                               this.comProjectCode,
                               this.oldComProjectCode);

    }

    public String getAllSetting() {
        return "";
    }

    //modify huangqirong 2011-10-31 bug 2230 筛选报错
    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.filterType != null) {
                sResult = " where 1=1 ";
				/**shashijie 2012-7-2 STORY 2475 */
                if (this.filterType.comProjectCode != null &&
                		this.filterType.comProjectCode.length() != 0) {
                    sResult = sResult + " and FComProjectCode like '" +
                        filterType.comProjectCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.comProjectName != null &&
                		this.filterType.comProjectName.length() != 0) {
                    sResult = sResult + " and FComProjectName like '" +
                        filterType.comProjectName.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.desc != null &&
                		this.filterType.desc.length() != 0) {
                    sResult = sResult + " and FDesc like '" +
                        filterType.desc.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.comScript != null &&
                		this.filterType.comScript.length() != 0) {
                    sResult = sResult + " and FComScript like '" +
                        filterType.comScript.replaceAll("'", "''") + "%'";
                }
				/**end*/
            } else {
                sResult = " WHERE 1 = 1";
            }
        } catch (Exception e) {
            throw new YssException("筛选财务估值核对信息设置数据出错", e);
        }
        return sResult;
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setSecurityAttr(rs);
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
            throw new YssException("获取财务估值核对信息设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select y.* from " +
            " (select FComProjectCode,FCheckState from " +
            " TB_PFSys_ValCompare " +
            //----------------------------begin
            "  group by FComProjectCode,FCheckState) x join" +
            //-----------------------------end
            " (select a.*, " +
            " b.FUserName as FCreatorName, c.FUserName as FCheckUserName" +
            " from TB_PFSys_ValCompare a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            ") y on x.FComProjectCode = y.FComProjectCode " +
            " order by y.FCheckState, y.FCreateTime";
        return this.builderListViewData(strSql);
    }

    public String getListViewData2() throws YssException {
        String strSql = "";
        strSql = "select y.* from " +
            " (select FComProjectCode,FCheckState from " +
            " TB_PFSys_ValCompare " +
            //----------------------------begin
            "  group by FComProjectCode,FCheckState) x join" +
            //-----------------------------end
            " (select a.*, " +
            " b.FUserName as FCreatorName, c.FUserName as FCheckUserName" +
            " from TB_PFSys_ValCompare a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() + " AND FCheckState = 1 " +
            ") y on x.FComProjectCode = y.FComProjectCode " +
            " order by y.FCheckState, y.FCreateTime";
        return this.builderListViewData(strSql);
    }

    public String getListViewData4() throws YssException {
        String strSql = "";
        return strSql;
    }

    public String getListViewData3() throws YssException {
        String strSql = "";
        return "";
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
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * 修改添加的方法
     * 因为TB_PFSys_ValCompare表的字段FComScript被更新为Clob类型，因此调整插入的方式
     * author : xuqiji
     * date   : 20090611
     * BugNO  : QDV4建行2009年6月9日01_B MS00490 财务估值核对脚本下拉条不起作用
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        try {
            //调整FComScript的插入位置，美化代码 modify by xuqiji
            strSql = "insert into TB_PFSys_ValCompare" +
                "(FComProjectCode, FComProjectName, FDesc," +
                " FCHECKSTATE, FCREATOR, FCREATETIME,FCheckUser,FComScript) values(" +
                dbl.sqlString(this.comProjectCode) + "," +
                dbl.sqlString(this.comProjectName) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + ",";

            //添加对clob类型字段的操作，oracle插入占位符 modify by xuqiji
            if (dbl.getDBType() == YssCons.DB_ORA) {
                strSql = strSql + "EMPTY_CLOB()" + ")";
            } else {
                strSql = strSql + dbl.sqlString(this.comScript) + ")";
            }

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            //下面代码是操作clob类型的数据入库 add by xuqiji
            if (dbl.getDBType() == YssCons.DB_ORA) {
                String str2 = "select FComScript from " +
                    " TB_PFSys_ValCompare where FComProjectCode=" +
                    dbl.sqlString(this.comProjectCode);
                rs = dbl.openResultSet(str2);
                if (rs.next()) {
                	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                	  // modify by jsc 20120809 连接池对大对象的特殊处理
                	CLOB bComScript = dbl.CastToCLOB(rs.getClob("FComScript"));
                    //CLOB bComScript = ( (OracleResultSet) rs).getCLOB("FComScript");
                    bComScript.putString(1, this.comScript);

                    String sql = "update TB_PFSys_ValCompare set FComScript=? where FComProjectCode=" + dbl.sqlString(this.comProjectCode);
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setClob(1, bComScript);
                    pstmt.executeUpdate();
                    pstmt.close();
                }
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("增加财务估值核对信息设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }

    /**
     * 修改修改的方法
     * 因为TB_PFSys_ValCompare表的字段FComScript被更新为Clob类型，因此调整更新的方式
     * author : xuqiji
     * date   : 20090611
     * BugNO  : QDV4建行2009年6月9日01_B MS00490 财务估值核对脚本下拉条不起作用
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        PreparedStatement pst = null;
        try {

            strSql = "update TB_PFSys_ValCompare set " +
                "  FComProjectCode = " + dbl.sqlString(this.comProjectCode) +
                ", FComProjectName = " + dbl.sqlString(this.comProjectName);

            //对oracle clob类型字段添加占位符处理 modify by xuqiji
            if (dbl.getDBType() == YssCons.DB_ORA) {
                strSql = strSql + ", FComScript =EMPTY_CLOB()";
            } else {
                strSql = strSql + ", FComScript =" + dbl.sqlString(this.comScript);
            }

            strSql = strSql + ", FDesc = " + dbl.sqlString(this.desc) +
                ", FCHECKSTATE = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCreator = " + dbl.sqlString(this.creatorCode) +
                ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ", FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FComProjectCode = " +
                dbl.sqlString(this.oldComProjectCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            //更新Oracle clob类型字段的值 modify by xuqiji
            if (dbl.dbType == YssCons.DB_ORA) {
                String strUpdate = "UPDATE " + pub.yssGetTableName("TB_PFSys_ValCompare") +
                    " SET FComScript = ? WHERE FComProjectCode = " + dbl.sqlString(this.comProjectCode);
                pst = conn.prepareStatement(strUpdate);

                strSql = "SELECT FComScript FROM " +
                    pub.yssGetTableName("TB_PFSys_ValCompare") +
                    " WHERE FComProjectCode = " + dbl.sqlString(this.comProjectCode);
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                	  // modify by jsc 20120809 连接池对大对象的特殊处理
                	CLOB clob = dbl.CastToCLOB(rs.getClob("FComScript"));
                    //CLOB clob = ( (OracleResultSet) rs).getCLOB("FComScript");
                    clob.putString(1, this.comScript);
                    pst.setClob(1, clob);
                    pst.executeUpdate();
                }
            }
            //------------------------------end------------------------//
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改财务估值核对信息设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);
        }
        return null;
    }

    /**
     * 删除数据即放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        int Count = 0;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update TB_PFSys_ValCompare " +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FComProjectCode = " +
                dbl.sqlString(this.oldComProjectCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除财务估值核对信息出错", e);
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
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            /**shashijie 2012-7-2 STORY 2475 */
			if ( sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) {
			/**end*/
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update TB_PFSys_ValCompare " +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FComProjectCode = " +
                        dbl.sqlString(this.oldComProjectCode);
                    dbl.executeSql(strSql);
                }
			/**shashijie 2012-7-2 STORY 2475 */
            } else if ( oldComProjectCode != null && (!oldComProjectCode.equalsIgnoreCase(""))) {
			/**end*/
                strSql = "update TB_PFSys_ValCompare" +
                    " set FCheckState = " + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FComProjectCode = " +
                    dbl.sqlString(this.oldComProjectCode);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核财务估值核对信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void setSecurityAttr(ResultSet rs) throws YssException, SQLException { //xuqiji 20090611 QDV4建行2009年6月9日01_B MS00490 财务估值核对脚本下拉条不起作用
        this.comProjectCode = rs.getString("FComProjectCode");
        this.comProjectName = rs.getString("FComProjectName");
        this.comScript = dbl.clobStrValue(rs.getClob("FComScript")); //xuqiji 20090611 QDV4建行2009年6月9日01_B MS00490 财务估值核对脚本下拉条不起作用
        this.desc = rs.getString("FDesc");
        super.setRecLog(rs);
    }

    public String getOperValue(String sType) throws YssException {
        try {
            //if (sType.equalsIgnoreCase("compare")) {
            //ValCompFormula compare = new ValCompFormula();
            //compare.calcFormula();
            //}
        } catch (Exception e) {
            throw new YssException("111", e);
        }
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    /**
     * 删除回收站的数据即从回收站彻底删除数据
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
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
                    strSql = "delete from " +
                        " TB_PFSys_ValCompare" +
                        " where FComProjectCode = " +
                        dbl.sqlString(this.oldComProjectCode);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            else if (oldComProjectCode != "" && oldComProjectCode != null) {
                strSql = "delete from " +
                    " TB_PFSys_ValCompare" +
                    " where FSECURITYCODE = " +
                    dbl.sqlString(this.oldComProjectCode);
                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        }

        catch (Exception e) {
            throw new YssException("清除财务估值核对数据出错", e);
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
