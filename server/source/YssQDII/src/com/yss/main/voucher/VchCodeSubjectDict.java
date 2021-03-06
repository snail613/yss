package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class VchCodeSubjectDict
    extends BaseDataSettingBean implements IDataSetting {
    private String strIndCode = ""; //标识代码
    private String strCnvConent = ""; //转换内容
    private String strDictCode = ""; //代码类型
    private String strDictName = "";
    private String Desc = ""; //描述
    private String SubDesc = ""; //子描述    //BugNo:0000275 edit by jc
    private String oldDictCode = ""; //
    private String isOnlyColumn = "0"; //操作标识
    private String strPortCode = ""; //专用组合 ,新增字段
    private String strPortName = "";

    private String sSubData = "";
    private VchCodeSubjectDict filterType;
    private String sRecycled = ""; //增加回收站处理功能 by leeyu 2008-10-21 BUG:0000491
    public VchCodeSubjectDict() {
    }

    public void checkInput(byte bType) throws YssException {
        dbFun.checkInputCommon(bType,
                               pub.yssGetTableName("Tb_Vch_Dict"),
                               "FDictCode",
                               this.strDictCode,
                               this.oldDictCode);
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
                if (sRowStr.split("\r\t").length > 2) {
                    sSubData = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }

            reqAry = sTmpStr.split("\t");
            sRecycled = sTmpStr;
            this.strIndCode = reqAry[0];
            this.strCnvConent = reqAry[1];
            this.strDictCode = reqAry[2];
            this.strDictName = reqAry[3];
            this.Desc = reqAry[4];
            this.SubDesc = reqAry[5]; //BugNo:0000275 edit by jc
            this.oldDictCode = reqAry[6];
            this.isOnlyColumn = reqAry[7];
            this.checkStateId = Integer.parseInt(reqAry[8]);
            this.strPortCode = reqAry[9];
            if (this.strPortCode.trim().length() == 0) {
                this.strPortCode = " ";
            }
            this.strPortName = reqAry[10];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0 &&
                !sRowStr.split("\r\t")[1].equals("[null]")) {
                if (this.filterType == null) {
                    this.filterType = new VchCodeSubjectDict();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析代码科目字典设置信息出错!");
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strIndCode).append("\t");

        buf.append(this.strCnvConent).append("\t");
        buf.append(this.strDictCode).append("\t");
        buf.append(this.strDictName).append("\t");
        buf.append(this.Desc).append("\t");
        buf.append(this.SubDesc).append("\t"); //BugNo:0000275 edit by jc
        buf.append(this.checkStateId).append("\t");
        buf.append(this.strPortCode).append("\t");
        buf.append(this.strPortName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void checkSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            //====增加回收站处理的功能 by leeyu BUG:0000491 2008-10-21
            String[] arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(false);
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                sqlStr = "update " + pub.yssGetTableName("Tb_Vch_Dict") +
                    " set FCheckState=" + this.checkStateId + "," +
                    " FCheckUser=" + dbl.sqlString(pub.getUserCode()) + "," +
                    " FCheckTime='" + YssFun.formatDatetime(new java.util.Date()) +
                    "'" +
                    " where FDictCode = " + dbl.sqlString(this.strDictCode);
                dbl.executeSql(sqlStr);
            }
            //conn.setAutoCommit(false);
            bTrans = true;
//         dbl.executeSql(sqlStr);
            //===2008-10-21
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核代码科目字典设置数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String addSetting() throws YssException {
        return saveMutliSetting("");
    }

    public String editSetting() throws YssException {
        return saveMutliSetting("");
    }

    public void delSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            sqlStr = "update " + pub.yssGetTableName("Tb_Vch_Dict") +
                " set FCheckState =" + this.checkStateId + "," +
                " FCreator=" + dbl.sqlString(this.creatorCode + " ") + "," +
                " FCreateTime=" + dbl.sqlString(this.creatorTime + " ") +
                " where FDictCode = " + dbl.sqlString(this.strDictCode);
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

    /**
     * buildFilterSql
     * 根据isOnlyColumn确定是模糊查询还是精确查询
     * @throws YssException
     * 修改时间：2008年7月14日
     * 修改人：蒋春
     */
    private String buildFilterSql() throws YssException {
        String sqlStr = "";
        if (this.filterType != null) {
            sqlStr += " where 1=1";
            if (this.isOnlyColumn.equalsIgnoreCase("1")) {
                sqlStr += " and 1=2";
            } else if (this.isOnlyColumn.equalsIgnoreCase("-1")) {
            	/**shashijie 2012-7-2 STORY 2475 */
                if (this.filterType.strIndCode != null &&
                		this.filterType.strIndCode.length() != 0) {
                    sqlStr += " and a.FIndCode = '" +
                        (this.filterType.strIndCode).replaceAll("'", "''") + "'";
                }
                if (this.filterType.strCnvConent != null &&
                		this.filterType.strCnvConent.length() != 0) {
                    sqlStr += " and a.FCnvConent = '" +
                        (this.filterType.strCnvConent).replaceAll("'", "''") +
                        "'";
                }
                if (this.filterType.strDictCode != null &&
                		this.filterType.strDictCode.length() != 0) {
                /**end*/
                    sqlStr += " and a.FDictCode = '" +
                        (this.filterType.strDictCode).replaceAll("'", "''") + "'";
                }
                if (this.filterType.strPortCode != null &&
                    filterType.strPortCode.trim().length() != 0) {
                    sqlStr += " and a.FPortCode = '" +
                        filterType.strPortCode.replaceAll("'", "''") + "'";
                }
            } else {
				/**shashijie 2012-7-2 STORY 2475 */
                if (this.filterType.strIndCode != null &&
                		this.filterType.strIndCode.length() != 0) {
                    sqlStr += " and a.FIndCode like '" +
                        (this.filterType.strIndCode).replaceAll("'", "''") + "%'";
                }
                if (this.filterType.strCnvConent != null &&
                		this.filterType.strCnvConent.length() != 0) {
                    sqlStr += " and a.FCnvConent like '" +
                        (this.filterType.strCnvConent).replaceAll("'", "''") +
                        "%'";
                }
                if (this.filterType.strDictCode != null &&
                		this.filterType.strDictCode.length() != 0) {
                    sqlStr += " and a.FDictCode like '" +
                        (this.filterType.strDictCode).replaceAll("'", "''") + "%'";
                }
				/**end*/
                if (this.filterType.strPortCode != null &&
                    filterType.strPortCode.trim().length() != 0) {
                    sqlStr += " and a.FPortCode like '" +
                        filterType.strPortCode.replaceAll("'", "''") + "%'";
                }
            }
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
        try {
            sHeader = "字典代码\t字典名称\t字典描述";
            strSql = //BugNo:0000275 edit by jc 添加a.FDesc
                " select distinct(a.FDictCode)as FDictCode,a.FDictName,a.FDesc,a.FCheckState,a.FCreator  " +		//20111010 modified by liubo.Bug #2853。获取某条记录的创建人
                " from " + pub.yssGetTableName("tb_vch_dict") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator=b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser=c.FUserCode "
                + this.buildFilterSql() +
                " order by a.FDictCode,a.FCheckState desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FDictCode")).append("\t");
                bufShow.append(rs.getString("FDictName")).append("\t");
                bufShow.append(rs.getString("FDesc"));
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                this.strDictCode = rs.getString("FDictCode");
                this.strDictName = rs.getString("FDictName");
                this.Desc = rs.getString("FDesc");
                this.checkStateId = rs.getInt("FCheckState");
                super.creatorCode = rs.getString("FCreator");		//20111010 added by liubo.Bug #2853.将创建人返回给前台，前台获得某条记录的准确的创建人时，审核数据不会出现第一次能审核，反审核回来后第二次就不能审核的情况
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
                "FDictCode\tFDictName\tFDesc";
        } catch (Exception e) {
            throw new YssException("获取资源凭证数据信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        try {
            sHeader = "凭证标识代码\t凭证转换内容\t描述";
            strSql = "select distinct FIndCode,FCnvConent,FSubDesc from " +
                pub.yssGetTableName("Tb_Vch_Dict") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator=b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser=c.FUserCode" +
                " where a.FCheckState=1";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FIndCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FCnvConent") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FSubDesc") + "").trim()).append(
                    "\t").append(YssCons.YSS_LINESPLITMARK); ;

                this.strDictCode = rs.getString("FIndCode");
                this.strDictName = rs.getString("FCnvConent");
                this.SubDesc = rs.getString("FSubDesc");
                //     this.Code=rs.getString("FIndCode");

                //     this.checkStateId =rs.getInt("FCheckState");
                //     this.SubjectCode=rs.getString("FCnvConent");
                //     this.Desc=rs.getString("FDesc");

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
                "FIndCode\tFCnvConent\tFSubDesc";
        } catch (Exception e) {
            throw new YssException("获取资源凭证数据信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.strCnvConent = rs.getString("FCnvConent");
        this.strDictCode = rs.getString("FDictCode");
        this.strDictName = rs.getString("FDictName");
        this.strPortCode = rs.getString("FPortCode");
        this.strPortName = rs.getString("FPortName");
        this.strIndCode = rs.getString("FIndCode");
        this.Desc = rs.getString("FDesc");
        this.SubDesc = rs.getString("FSubDesc"); //BugNo:0000275 edit by jc
        super.setRecLog(rs);
    }

    public String getListViewData3() throws YssException {
        String strSql = "";
        String sHeader = "";
        String sShowDataStr = "", sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "字典代码\t标识代码\t转换内容\t描述\t专用组合代码\t专用组合名称";
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,p.FPortName from " +
                pub.yssGetTableName("tb_vch_dict") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator=b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator=c.FUserCode " +
                " left join (select FPortCode,FPortName from " +
                pub.yssGetTableName("tb_para_portfolio") +
                ") p on a.FPortCode=p.FPortCode " +
                this.buildFilterSql() +
                " order by a.FCheckState desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FDictCode")).append("\t");
                bufShow.append(rs.getString("FIndCode")).append("\t");
                bufShow.append(rs.getString("FCnvConent")).append("\t");
                //bufShow.append(rs.getString("FDesc")).append("\t"); //BugNo:0000275 edit by jc
                bufShow.append(rs.getString("FSubDesc")).append("\t"); //BugNo:0000275 edit by jc
                bufShow.append(rs.getString("FPortCode")).append("\t");
                bufShow.append(rs.getString("FPortName")).append(YssCons.
                    YSS_LINESPLITMARK);
                setResultSetAttr(rs);
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
                //BugNo:0000275 edit by jc
                "FCode\tFSubjectCode\tFSubjectName\tFSubDesc\tFportCode\tFPortName";
        } catch (Exception e) {
            throw new YssException("获取资源凭证数据信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String[] arrData = null;
        String reStr = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            reStr = "delete from " + pub.yssGetTableName("Tb_vch_dict") +
                " where FDIctCode =" + dbl.sqlString(this.oldDictCode);
            if (sSubData.length() > 0) {
                dbl.executeSql(reStr);
            }
            arrData = sSubData.split("\r\f");
            PreparedStatement pst = dbl.openPreparedStatement(
                "insert into " + pub.yssGetTableName("Tb_Vch_Dict") +
                "(FIndCode,FCnvConent,FDictCode,FDictName,FDesc,FSubDesc," +
                "FCheckState,FCreator,FCreateTime,FPortCode)" +
                " values(?,?,?,?,?,?,?,?,?,?)");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                pst.setString(1, strIndCode);
                pst.setString(2, strCnvConent);
                pst.setString(3, strDictCode);
                pst.setString(4, strDictName);
                pst.setString(5, Desc);
                pst.setString(6, SubDesc);
                pst.setInt(7, (pub.getSysCheckState() ? 0 : 1));
                //20111010 modiyied by liubo.Bug #2853
                //向数据表中插入准确的创建人，前台获得某条记录的准确的创建人时，审核数据不会出现第一次能审核，反审核回来后第二次就不能审核的情况
                //==================================
//                pst.setString(8,
//                              (pub.getSysCheckState() ? "' '" :
//                               dbl.sqlString(creatorCode)));
//                pst.setString(9,
//                              (pub.getSysCheckState() ? "' '" :
//                               dbl.sqlDate(new java.util.Date())));
                pst.setString(8, creatorCode);
                pst.setString(9, YssFun.formatDatetime(new java.util.Date()));
                //===============end======================
                pst.setString(10, strPortCode);
                pst.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            try {
                conn.rollback();
                throw new YssException("新增代码科目字典设置数据出错", e);
            } catch (Exception ex) {
            }
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    public String getOperValue(String sType) throws YssException {
        return null;
    }

    /**
     * 回收站处理功能 by leeyu 2008-10-21 BUG:0000491
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            String[] arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(bTrans);
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                sqlStr = "delete " + pub.yssGetTableName("Tb_Vch_Dict") +
                    " where FDictCode = " + dbl.sqlString(this.strDictCode);
                dbl.executeSql(sqlStr);
            }
            bTrans = true;
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("清除代码科目字典设置数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String getStrPortName() {
        return strPortName;
    }

    public String getStrPortCode() {
        return strPortCode;
    }

    public String getStrIndCode() {
        return strIndCode;
    }

    public String getStrDictName() {
        return strDictName;
    }

    public String getStrDictCode() {
        return strDictCode;
    }

    public String getStrCnvConent() {
        return strCnvConent;
    }

    public String getOldDictCode() {
        return oldDictCode;
    }

    public void setFilterType(VchCodeSubjectDict filterType) {
        this.filterType = filterType;
    }

    public void setStrPortName(String strPortName) {
        this.strPortName = strPortName;
    }

    public void setStrPortCode(String strPortCode) {
        this.strPortCode = strPortCode;
    }

    public void setStrIndCode(String strIndCode) {
        this.strIndCode = strIndCode;
    }

    public void setStrDictName(String strDictName) {
        this.strDictName = strDictName;
    }

    public void setStrDictCode(String strDictCode) {
        this.strDictCode = strDictCode;
    }

    public void setStrCnvConent(String strCnvConent) {
        this.strCnvConent = strCnvConent;
    }

    public void setOldDictCode(String oldDictCode) {
        this.oldDictCode = oldDictCode;
    }

    public void setIsOnlyColumn(String isOnlyColumn) {
        this.isOnlyColumn = isOnlyColumn;
    }

    public void setSubDesc(String SubDesc) {
        this.SubDesc = SubDesc;
    }

    public VchCodeSubjectDict getFilterType() {
        return filterType;
    }

    public String getIsOnlyColumn() {
        return isOnlyColumn;
    }

    public String getSubDesc() {
        return SubDesc;
    }

    public void setDesc(String Desc) {
        this.Desc = Desc;
    }

    public String getDesc() {
        return Desc;
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
