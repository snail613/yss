package com.yss.main.cusreport;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class RepDsFieldBean
    extends BaseDataSettingBean implements IDataSetting {

    private String DsCode = "";
    private String OrderIndex = "";
    private String DsField = "";
    private String IsTotalInd = "";
    private String IsTotal = "";

    private String OldDsCode = "";
    private String OldOrderIndex = "";
    private RepDsFieldBean FilterType;
    private String strDsField = "";
    private String strColKey = "";
    public void setStrDsField(String strDsField) {
        this.strDsField = strDsField;
    }

    public void setStrColKey(String strColKey) {
        this.strColKey = strColKey;
    }

    public String getStrDsField() {
        return strDsField;
    }

    public String getStrColKey() {
        return strColKey;
    }

    public void setIsTotal(String IsTotal) {
        this.IsTotal = IsTotal;
    }

    public String getIsTotal() {
        return IsTotal;
    }

    public void setIsTotalInd(String IsTotalInd) {
        this.IsTotalInd = IsTotalInd;
    }

    public String getIsTotalInd() {
        return IsTotalInd;
    }

    public void setDsField(String DsField) {
        this.DsField = DsField;
    }

    public String getDsField() {
        return DsField;
    }

    public RepDsFieldBean() {
    }

    public void setDsCode(String dsCode) {
        this.DsCode = dsCode;
    }

    public void setOrderIndex(String orderIndex) {
        this.OrderIndex = orderIndex;
    }

    public String getDsCode() {
        return this.DsCode;
    }

    public String getOrderIndex() {
        return this.OrderIndex;
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String dataSource[] = null;
        String[] reqAry = null;

        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.strDsField = sRowStr.split("\r\t")[2];
                }

            } else {
                sTmpStr = sRowStr;
            }

            reqAry = sTmpStr.split("\t");
            this.DsCode = reqAry[0];
            this.OrderIndex = reqAry[1];
            this.DsField = reqAry[2];
            this.IsTotalInd = reqAry[3];
            this.IsTotal = reqAry[4];
            this.checkStateId = Integer.parseInt(reqAry[5]);
            this.OldDsCode = reqAry[6];
            this.OldOrderIndex = reqAry[7];
            this.strColKey = reqAry[8];
            super.parseRecLog();

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new RepDsFieldBean();
                    this.FilterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析数据源字段设置请求出错", e);
        }
    }

    private String buildFilterSql() {
        String sResult = "";
        if (this.FilterType != null) {
            sResult = " where 1=1";
            if (this.FilterType.DsCode.length() != 0) {
                sResult = sResult + " and a.FRepDsCode = '" +
                    this.FilterType.DsCode + "'";
            }
            if (this.FilterType.OrderIndex.length() != 0) {
                sResult = sResult + " and a.FOrderIndex = " +
                    this.FilterType.OrderIndex;
            }
            if (this.FilterType.DsField.length() != 0) {
                sResult = sResult + " and a.FDsField = '" +
                    this.FilterType.DsField + "'";
            }

            if (this.FilterType.IsTotal.length() != 0) {
                sResult = sResult + " and a.FIsTotal = " +
                    this.FilterType.IsTotal.trim() + "";
            }
        }
        return sResult;
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        int i = 0;
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = "";
        String sTabCellInfo = "";
        StringBuffer bufShow = new StringBuffer(); //用于显示的属性
        StringBuffer bufAll = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Rep_DsField") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                buildFilterSql() + " order by a.FOrderIndex ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
//            bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
//                  append(YssCons.YSS_LINESPLITMARK);
                bufShow.append(rs.getString("FDsField")).append("\t");
                bufShow.append(rs.getInt("FIsTotal") == 1 ? "√" : "").append("\t");
                bufShow.append(rs.getString("FTotalInd")).append(YssCons.
                    YSS_LINESPLITMARK);
                // bufShow.append(rs.getInt("FIsTotalInd")==1?"√":"").append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                i++;
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_REP_ALIGN + "," +
                                        YssCons.YSS_REP_ISTOTAL);

            sTabCellInfo = getTabCellInfo(i);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr + "\r\f" +
                sTabCellInfo;
        } catch (Exception e) {
            throw new YssException("获取数据源字段设置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getTabCellInfo(int iCols) throws YssException {
        String strSql = "", strReturn = "";
        StringBuffer buf = new StringBuffer();
        RepTabCellBean tabCell = new RepTabCellBean();
        tabCell.setYssPub(pub);
        ResultSet rs = null;
        boolean bTmp = false;

        try {
            //获取报表信息
            //把客户端参数分割成表名和期间
            //先加载当前套账相关信息，以及表名、期间信息...打印的权限
            //因为报表页面是htm，第一次进入参数是提前存到session，htm加载后loadrpt要自动读取这些设置并回传给htm

            buf.append(pub.getAssetGroupCode()).append("\f\t");
            buf.append(pub.getAssetGroupName()).append("\f\t");
            buf.append(pub.getUserName()).append("\f\t");
            buf.append(this.FilterType.DsCode).append("\f\t");
            //因为打印直接在客户端进行，打印时就不提交了。所以这里也传上打印权限
            //可写权限、客户端可实施写保护
            // if(!new UserRights(pubVar).checkRights(YwCons.uoCw_RptPrt))
            buf.append("1").append("\f\t");
            //if(!new UserRights(pubVar).checkRights(YwCons.uoCw_RptMod))
            buf.append("0").append("\f\f");
            buf.append(this.FilterType.DsCode).append("\f\t"); //RepCode
            buf.append("").append("\f\t"); //RepName
            buf.append("2").append("\f\t"); //Rows
            buf.append(iCols).append("\f\t"); //Cols
            buf.append("0").append("\f\t"); //FixRows
            buf.append("0").append("\f\t"); //FixCols
            buf.append("315,0,1;315,0,1|;").append("\f\t"); //RCSize
            buf.append(" ").append("\f\t"); //Author
            buf.append(" ").append("\f\t"); //Desc
            buf.append("").append("\f\t"); //Merge
            buf.append("0").append("\f\f"); //Print

            //获取表结构
            strSql = " select * from " + pub.yssGetTableName("Tb_Rep_Cell") +
                " where FRelaCode=" + dbl.sqlString(this.FilterType.DsCode) +
                " and FRelaType = 'DSF' ";

            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
                if (bTmp) {
                    buf.append("\f\r\n");

                }
                tabCell.setRow(String.valueOf(rs.getInt("FRow") * -1 - 1));
                tabCell.setCol(rs.getString("FCol"));
                tabCell.setContent(rs.getString("FContent") == null ? "" :
                                   YssFun.rTrim(rs.getString("FContent")));
                tabCell.setLColor(rs.getString("FLColor"));
                tabCell.setLLine(rs.getString("FLLine"));
                tabCell.setTColor(rs.getString("FTColor"));
                tabCell.setTLine(rs.getString("FTLine"));
                tabCell.setBColor(rs.getString("FBColor"));
                tabCell.setBLine(rs.getString("FBLine"));
                tabCell.setRColor(rs.getString("FRColor"));
                tabCell.setRLine(rs.getString("FRLine"));
                tabCell.setBackColor(rs.getString("FBackColor"));
                tabCell.setForeColor(rs.getString("FForeColor"));
                tabCell.setFontName(rs.getString("FFontName"));
                tabCell.setFontSize(String.valueOf(rs.getFloat("FFontSize")));
                tabCell.setFontStyle(rs.getString("FFontStyle"));
                tabCell.setDataType(rs.getString("FDataType"));
                tabCell.setFormat(rs.getString("FFormat") == null ? "" :
                                  rs.getString("FFormat")); //这里其实包含\t间隔的五段
                tabCell.setIMerge(rs.getInt("FIsMergeCol"));
                buf.append(tabCell.buildRowStr());

                if (!bTmp) {
                    bTmp = true;
                }
            }

            if (bTmp) {
                return buf.toString();
            } else {
                return "";
            }

        } catch (Exception e) {
            throw new YssException("获取报表格式详细设计信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
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
    public String getListViewData4() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        ResultSet rs2 = null; //保存获得的品种代码
        String fdatasources[] = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        sHeader = "";
        try {

            strSql = "";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FRepDsCode") + "").trim()).
                    append("\t");
                bufShow.append( (rs.getString("FRepDsName") + "").trim()).
                    append(YssCons.YSS_LINESPLITMARK);
                RepDataSourceBean datasource = new RepDataSourceBean();
                datasource.setYssPub(pub);
                datasource.setResultSetAttr(rs);

                bufAll.append(datasource.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }

            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        }

        catch (Exception e) {
            throw new YssException("获取数据源字段信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);

        }
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Rep_DsField") +
                "(FRepDsCode, FOrderIndex, FDsField, FTotalInd, FIsTotal, " +
                " FCheckState,FCreator,FCreateTime,FCheckUser,FColKey)" +
                " values(" + dbl.sqlString(this.DsCode) + "," +
                this.OrderIndex + "," +
                dbl.sqlString(this.DsField) + "," +
                dbl.sqlString(this.IsTotalInd) + "," +
                this.IsTotal + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                "," +
                dbl.sqlString(this.strColKey) +
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增数据源字段设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Rep_DsField") +
                " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FRepDsCode = " + dbl.sqlString(this.DsCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核数据源字段信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Rep_DsField") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FRepDsCode = " + dbl.sqlString(this.DsCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除数据源字段信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql =
                "update " + pub.yssGetTableName("Tb_Rep_DsField") +
                " set FRepDsCode = " + dbl.sqlString(this.DsCode) +
                ",FOrderIndex = " + this.OrderIndex +
                ",FDsField=" + dbl.sqlString(this.DsField) +
                ",FTotalInd = " + this.IsTotalInd +
                ",FColKey = " + this.strColKey +
                ",FIsTotal = " + this.IsTotal +
                ",FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
                ",FCreator = " + dbl.sqlString(this.creatorCode) +
                ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FRepDsCode = " + dbl.sqlString(this.OldDsCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改数据源字段信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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
                                   String strDsCode) throws YssException {
        String[] sMutilRowAry = null;
        String[] sMutilRowStrAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";

        try {
            if (!bIsTrans) {
                conn.setAutoCommit(false);
                bTrans = true;
            }
            sMutilRowStrAry = sMutilRowStr.split("\r\f\n");
            sMutilRowAry = sMutilRowStrAry[0].split(YssCons.YSS_LINESPLITMARK);
            this.parseRowStr(sMutilRowAry[0]);
            strSql = "delete from " + pub.yssGetTableName("Tb_Rep_DsField") +
                " where FRepDsCode = " +
                dbl.sqlString(this.DsCode);
            dbl.executeSql(strSql);

            strSql =
                "insert into " + pub.yssGetTableName("Tb_Rep_DsField") +
                "(FRepDsCode, FOrderIndex, FDsField, FTotalInd, FIsTotal," +
                " FCheckState,FCreator,FCreateTime,FCheckUser,FColKey)" +
                " values (?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);

            for (int i = 0; i < sMutilRowAry.length; i++) {

                this.parseRowStr(sMutilRowAry[i]);

                pstmt.setString(1, this.DsCode);
                pstmt.setInt(2, YssFun.toInt(this.OrderIndex));
                //BugNo:0000259 edit by jc
                if (this.DsField != null && this.DsField.trim().length() > 0) {
                    pstmt.setString(3, this.DsField);
                } else {
                    pstmt.setString(3, " ");
                }
                //-----------------------jc
                pstmt.setString(4,
                                (this.IsTotalInd.equalsIgnoreCase("") ? " " : this.IsTotalInd));
                //  pstmt.setInt(4, YssFun.toInt(this.IsTotalInd));
                pstmt.setInt(5, YssFun.toInt(this.IsTotal));
                pstmt.setInt(6, (pub.getSysCheckState() ? 0 : 1));
                pstmt.setString(7, this.creatorCode);
                pstmt.setString(8, this.creatorTime);
                pstmt.setString(9,
                                (pub.getSysCheckState() ? " " : this.creatorCode));
                pstmt.setString(10, this.strColKey);
                pstmt.executeUpdate();

            }

            if (sMutilRowStrAry.length > 1 && sMutilRowStrAry[1].length() > 0) {
                RepTabCellBean tabcell = new RepTabCellBean(this.DsCode,
                    this.DsCode);
                tabcell.setYssPub(pub);
                tabcell.setRelaType("DSF");
                tabcell.setIsDsFieldSet(true);
                tabcell.saveMutliSetting(sMutilRowStrAry[1]);
            }

            if (!bIsTrans) {
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
            return "";
        } catch (SQLException e) {
            throw new YssException("保存数据源字段设置信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pstmt);
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
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.DsCode).append("\t");
        buf.append(this.OrderIndex).append("\t");
        buf.append(this.DsField).append("\t");
        buf.append(this.IsTotalInd).append("\t");
        buf.append(this.IsTotal).append("\t");
        buf.append(this.strColKey).append("\t");
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
        String strSql = "", sReturn = "";
        ResultSet rs = null;
        try {
            if (sType.trim().equalsIgnoreCase("get")) {
            	sReturn = ""; //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            }
            return sReturn;
        } catch (Exception e) {
            throw new YssException("");
        } finally {
            dbl.closeResultSetFinal(rs);
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

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.DsCode = rs.getString("FRepDsCode") + "";
        this.OrderIndex = rs.getString("FOrderIndex") + "";
        this.DsField = rs.getString("FDsField") + "";
        this.IsTotalInd = rs.getString("FTotalInd") + "";
        this.IsTotal = rs.getString("FIsTotal") + "";
        this.strColKey = rs.getString("FColKey") + "";
        super.setRecLog(rs);
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
