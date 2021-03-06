package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;
import oracle.sql.*;

/////
///凭证数据源设置
/////
public class VchSetVchDsBean
    extends BaseDataSettingBean implements IDataSetting {
    private String vchDsCode = "";
    private String vchDsName = "";
    private String vchAttrCode = "";
    private String vchAttrName = "";
    private String desc = "";
    private String dataSource = "";
    private String oldVchDsCode = "";
    private boolean bIsShow = false;
    private VchSetVchDsBean filterType = null;

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
            this.vchDsCode = reqAry[0];
            if (reqAry[0].length() == 0) {
                this.vchDsCode = " ";
            }
            this.vchDsName = reqAry[1];
            this.vchAttrCode = reqAry[2];
            this.vchAttrName = reqAry[3];
            this.desc = reqAry[4];
            this.dataSource = reqAry[5];
            this.checkStateId = Integer.parseInt(reqAry[6]);
            this.oldVchDsCode = reqAry[7];
            if (reqAry[7].length() == 0) {
                this.oldVchDsCode = " ";
            }
            if (reqAry[8].equalsIgnoreCase("true")) {
                this.bIsShow = true;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new VchSetVchDsBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析凭证数据源出错", e);
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.vchDsCode).append("\t");
        buf.append(this.vchDsName).append("\t");
        buf.append(this.vchAttrCode).append("\t");
        buf.append(this.vchAttrName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.dataSource).append("\t");
        // buf.append(this.oldVchDsCode).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Vch_DataSource"),
                               "FVchDsCode",
                               this.vchDsCode,
                               this.oldVchDsCode);

    }

    public String addSetting() throws YssException {
        Connection conn = null;
        ResultSet rs = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            //        stat =conn.createStatement();
            sqlStr = "insert into " + pub.yssGetTableName("Tb_Vch_DataSource") +
                " (FVchDsCode,FVchDsName,FAttrCode,FDataSource,FDesc,FCheckState,FCreator,FCreateTime) values( " +
                dbl.sqlString(this.vchDsCode) + "," +
                dbl.sqlString(this.vchDsName) + "," +
                dbl.sqlString(this.vchAttrCode) + "," +
                "EMPTY_CLOB()" + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + "," +
                "'" + YssFun.formatDatetime(new java.util.Date()) + "')";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            sqlStr = "select * from " + pub.yssGetTableName("Tb_Vch_DataSource") +
                " where FVchDscode =" + dbl.sqlString(this.vchDsCode);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
            	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
            	  // modify by jsc 20120809 连接池对大对象的特殊处理
            	CLOB clob = dbl.CastToCLOB(rs.getClob("FDataSource"));
                //CLOB clob = ( (oracle.jdbc.OracleResultSet) rs).getCLOB("FDataSource");
                clob.putString(1, this.dataSource);
                sqlStr = "update " + pub.yssGetTableName("Tb_Vch_DataSource") +
                    " set FDataSource = ? where FVchDsCode=" + dbl.sqlString(this.vchDsCode);
                PreparedStatement pst = conn.prepareStatement(sqlStr);
                pst.setClob(1, clob);
                pst.executeUpdate();
                pst.close();
            }
            rs.close();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增凭证数据源出错", e);
        } finally {
        	//add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
        	dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    private Clob getClob(String str) throws SQLException, YssException {
        try {
            CLOB clob = null;
            String sqlStr = "select FDataSource from " + pub.yssGetTableName("Tb_Vch_DataSource") +
                " where FVchDscode =" + dbl.sqlString(str);
            ResultSet rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
            	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
            	  // modify by jsc 20120809 连接池对大对象的特殊处理
            	clob = dbl.CastToCLOB(rs.getClob("FDataSource"));
                //clob = ( (oracle.jdbc.OracleResultSet) rs).getCLOB("FDataSource");
                clob.putString(1, this.dataSource);
            }
            rs.close();
            return clob;
        } catch (SQLException e) {
            throw new YssException(e);
        }
    }

    public String editSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            conn = dbl.loadConnection();
            sqlStr = " update " + pub.yssGetTableName("Tb_Vch_DataSource") + "  set " +
                " FVchDsCode =" + dbl.sqlString(this.vchDsCode) + "," +
                " FVchDsName =" + dbl.sqlString(this.vchDsName) + "," +
                " FDesc =" + dbl.sqlString(this.desc) + "," +
                " FAttrCode =" + dbl.sqlString(this.vchAttrCode) + "," +
                " FDataSource = EMPTY_CLOB()" +
                " where FVchDsCode =" + dbl.sqlString(this.oldVchDsCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            sqlStr = "select FDataSource from " + pub.yssGetTableName("Tb_Vch_DataSource") +
                " where FVchDscode =" + dbl.sqlString(this.oldVchDsCode);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
            	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
            	  // modify by jsc 20120809 连接池对大对象的特殊处理
            	CLOB clob = dbl.CastToCLOB(rs.getClob("FDataSource")); 
                //CLOB clob = ( (oracle.jdbc.OracleResultSet) rs).getCLOB("FDataSource");
                clob.putString(1, this.dataSource);
                sqlStr = "update " + pub.yssGetTableName("Tb_Vch_DataSource") +
                    " set FDataSource = ? where FVchDsCode=" + dbl.sqlString(this.oldVchDsCode);
                PreparedStatement pst = conn.prepareStatement(sqlStr);
                pst.setClob(1, clob);
                pst.executeUpdate();
                pst.close();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改凭证数据源出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
        }
        return "";
    }

    public void delSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            sqlStr = " update " + pub.yssGetTableName("Tb_Vch_DataSource") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FVchDsCode =" + dbl.sqlString(this.oldVchDsCode);
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除凭证数据源出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void checkSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            sqlStr = " update " + pub.yssGetTableName("Tb_Vch_DataSource") +
                " set FCheckState =" + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FVchDsCode =" + dbl.sqlString(this.oldVchDsCode);
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核凭证数据源出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    private void setVchDs(ResultSet rs) throws SQLException, YssException {
        this.vchDsCode = rs.getString("FVchDsCode");
        this.vchDsName = rs.getString("FVchDsName");
        this.vchAttrCode = rs.getString("FAttrCode");
        this.vchAttrName = rs.getString("FAttrName");
        this.desc = rs.getString("FDesc");
        if (this.bIsShow) {
            this.dataSource = dbl.clobStrValue(rs.getClob("FDataSource")).replaceAll("\t", "   ");
        } else {
            this.dataSource = "null";
        }
        super.setRecLog(rs);
    }

    private String builerFilter() {
        String sqlStr = "";
        if (this.filterType != null) {
            sqlStr = " where 1=1 ";
			/**shashijie 2012-7-2 STORY 2475 */
            if (filterType.vchDsCode != null && this.filterType.vchDsCode.length() != 0) {
                sqlStr += " and FVchDsCode like '" + filterType.vchDsCode.replaceAll("'", "''") + "%'";
            }
            if (filterType.vchDsName != null && filterType.vchDsName.length() != 0) {
                sqlStr += " and FChDsName like '" + filterType.vchDsName.replaceAll("'", "''") + "%'";
            }
            if (filterType.vchAttrCode != null && filterType.vchAttrCode.length() != 0) {
                sqlStr = " and FAttrCode like '" + filterType.vchAttrCode.replaceAll("'", "''") + "%'";
            }
            if (filterType.desc != null && filterType.desc.length() != 0) {
                sqlStr += " and FDesc like '" + filterType.desc.replaceAll("'", "''") + "%'";
            }
			/**end*/
        }
        return sqlStr;
    }

    public String getListViewData1() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = getListView1Headers();
            conn = dbl.loadConnection();
            sqlStr = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FAttrName as FAttrName from " +
                pub.yssGetTableName("Tb_Vch_DataSource") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join (select FAttrCode,FAttrName from " + pub.yssGetTableName("Tb_Vch_Attr") +
                " ) d on d.FAttrCode =a.FAttrCode " +
                " left join Tb_Fun_Vocabulary e on a.FVchDsCode = e.FVocCode and e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FUNCTION) +
                builerFilter() +
                " order by FCheckState, FCheckTime desc,FCreateTime desc";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setVchDs(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_FUNCTION);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取凭证数据源出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String getListViewData2() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "凭证数据源代码\t凭证数据源名称\t描述";
            conn = dbl.loadConnection();
            sqlStr = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FAttrName as FAttrName from " +
                pub.yssGetTableName("Tb_Vch_DataSource") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join (select FAttrCode,FAttrName from " + pub.yssGetTableName("Tb_Vch_Attr") +
                " ) d on d.FAttrCode =a.FAttrCode " +
                " where a.FCheckState =1 order by a.FVchDsCode";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FVchDsCode")).append("\t");
                bufShow.append(rs.getString("FVchDsName")).append("\t");
                bufShow.append(rs.getString("FDesc")).
                    append(YssCons.YSS_LINESPLITMARK);
                setVchDs(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取凭证数据源出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String getListViewData3() {
        return "";
    }

    public String getListViewData4() {
        return "";
    }

    public String getTreeViewData1() {
        return "";
    }

    public String getTreeViewData2() {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        String sShowDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer(); //用于显示的属性
        ResultSet rs = null;
        try {
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FAttrName as FAttrName from " +
                pub.yssGetTableName("Tb_Vch_DataSource") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FAttrCode,FAttrName from " + pub.yssGetTableName("Tb_Vch_Attr") +
                " ) d on d.FAttrCode =a.FAttrCode " +
                " where a.FVchDsCode = " + dbl.sqlString(oldVchDsCode) +
                " order by FCheckState, FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                // this.bIsShow=true;
                this.setVchDs(rs);
                bufShow.append(this.buildRowStr()).
                    append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            return sShowDataStr; //返回单条数据源代码
        } catch (Exception e) {
            throw new YssException("获取凭证数据源信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getAllSetting() {
        return "";
    }

    public String getBeforeEditData() {
        return "";
    }

    public IDataSetting getSetting() {
        return null;
    }

    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    protected String buildSql(String sDs) {
        String sInd = ""; //参数的标识
        String sDataType = ""; //数据类型的标识 S:字符型,I:数字型,D:日期型
        int iPos = 0;
        String sSqlValue = "";
        for (int i = 0; i < 100; i++) {
            sInd = "<" + (i + 1) + ">";
            iPos = sDs.indexOf(sInd);
            if (iPos <= 0) {
                sInd = " < " + (i + 1) + " >";
                iPos = sDs.indexOf(sInd);
            }
            if (iPos > 0) {
                sDataType = sDs.substring(iPos - 1, iPos);
                if (sDataType.equalsIgnoreCase("S")) {
                    sSqlValue = dbl.sqlString("");
                } else if (sDataType.equalsIgnoreCase("I")) {
                    sSqlValue = "0";
                } else if (sDataType.equalsIgnoreCase("D")) {
                    sSqlValue = dbl.sqlDate("1900-01-01");
                } else if (sDataType.equalsIgnoreCase("N")) {
                    sSqlValue = "''";
                }
                sDs = sDs.replaceAll(sDataType + sInd, sSqlValue);
            }
        }
        sDs = sDs.replace('[', ' ');
        sDs = sDs.replace(']', ' ');
        if (sDs.indexOf("<U>") > 0) {
            sDs = sDs.replaceAll("<U>", pub.getUserCode());
        } else if (sDs.indexOf("< U >") > 0) {
            sDs = sDs.replaceAll("< U >", pub.getUserCode());
        }
        if (sDs.indexOf("<Year>") > 0) { //把"<Year>"的标识替换成系统日期的年份
            sDs = sDs.replaceAll("<Year>", YssFun.formatDate(new java.util.Date(), "yyyy"));
        } else if (sDs.indexOf("< Year >") > 0) { //add by leeyu 080729
            sDs = sDs.replaceAll("< Year >", YssFun.formatDate(new java.util.Date(), "yyyy"));
        }
        if (sDs.indexOf("<Set>") > 0) { //把"<Year>"的标识替换成套帐号
            sDs = sDs.replaceAll("<Set>", "001");
        } else if (sDs.indexOf("< Set >") > 0) { // add by leeyu 080729
            sDs = sDs.replaceAll("< Set >", "001");
        }
        sDs = sDs.replaceAll("~Base", "0");
        return sDs;
    }

    public String getOperValue(String sType) throws YssException {
        String strSql = "", sReturn = "", sError = "";
        String sHeader = "", sShowDataStr = "";
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        try {
            if (sType.equalsIgnoreCase("getField")) {
                sError = "获取数据源字段信息出错";
                sHeader = "字段名称\t字段类型";
                strSql = this.dataSource.trim();
                strSql = buildSql(strSql);
                rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
                ResultSetMetaData rsmd = rs.getMetaData();
                int FieldsCount = rsmd.getColumnCount(); //原始表字段数
                int[] fDataType = new int[FieldsCount]; //记录数据字段数据类型
                String[] fDataTypeName = new String[FieldsCount];
                String[] fDataName = new String[FieldsCount]; //记录数据字段名称
                for (int i = 0; i < FieldsCount; i++) {
                    fDataName[i] = rsmd.getColumnName(i + 1);
                    fDataTypeName[i] = rsmd.getColumnTypeName(i + 1);
                    fDataType[i] = rsmd.getColumnType(i + 1);
                    buf.append(fDataName[i]).append("\t");
                    buf.append(fDataTypeName[i]).append(YssCons.YSS_LINESPLITMARK);
                }
                //      BaseReportBean rep = (BaseReportBean) pub.getOperDealCtx().getBean(this.beanID);
                //获得字段得数据类型 以及  字段名称
                //       String[] strData=rep.getReportFields1().split("\t");
                //       String[] fDataName = new String[strData.length]; //记录数据字段名称
                //       for (int i = 0; i < strData.length; i++) {
                //         buf.append(strData[i]).append("\t");
                //   buf.append("varchar").append(YssCons.YSS_LINESPLITMARK);
                //    }
                if (buf.toString().length() > 2) {
                    sShowDataStr = buf.toString().substring(0,
                        buf.toString().length() - 2);
                }
                sReturn = sHeader + "\r\f" + sShowDataStr + "\r\f" + sShowDataStr;
            }
            return sReturn;
        } catch (Exception e) {
            throw new YssException(sError + "\n\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public VchSetVchDsBean() {
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
