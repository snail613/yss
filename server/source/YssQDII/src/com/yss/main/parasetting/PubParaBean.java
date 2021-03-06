package com.yss.main.parasetting;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

//TB_XXX_PARA_PUBPARA通用参数类型设定
public class PubParaBean
    extends BaseDataSettingBean implements IDataSetting {
    private String pubParaCode = ""; //参数编号
    private String pubParaName = ""; //参数名称
    private String paraGroupCode = ""; //参数组编号
    private String paraGroupName = ""; //参数组名称
    private int paraId; //参数值编号
    private String ctlGrpCode = ""; //控件组代码
    private String ctlGrpName = ""; //控件组名称

    //private int iDetail; //是否为叶节点
    private String orderCode = ""; //排序编号
    private String desc = ""; //描述
    private String ctlParas = ""; //---
    private ArrayList hCtlCodes = new ArrayList(); //控件集合
    private String oldPubParaCode = "";
    private String oldParaGroupCode = "";
    private String oldOrderCode = "";
    private int oldParaId;
    private PubParaBean filterType = null;

    public PubParaBean() {
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() {
        return "";
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
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "insert into " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                "(FPubParaCode,FPubParaName,FParaGroupCode,FParaId,FCtlGrpCode," +
                "FCtlCode,FCtlValue,FOrderCode,FDesc) values(" +
                dbl.sqlString(this.pubParaCode) + "," +
                dbl.sqlString(this.pubParaName) + "," +
                dbl.sqlString(this.paraGroupCode) + "," +
                this.paraId + "," + dbl.sqlString(this.ctlGrpCode) + "," +
                dbl.sqlString(" ") + "," + dbl.sqlString(" ") + "," +
                //this.iDetail + "," +
                dbl.sqlString(dbFun.treeBuildOrderCode(
                    pub.yssGetTableName("TB_PFOper_PUBPARA"), "FPubParaCode",
                    this.paraGroupCode, Integer.parseInt(orderCode))) + "," +
                dbl.sqlString(this.desc) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增通用参数类型设定出错", e);
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
    public void checkInput(byte btOper) throws YssException {
        String strSql = "";
        String tmpValue = "";
        if (!this.pubParaCode.equalsIgnoreCase(this.oldPubParaCode)) {
            strSql = "select FPubParaCode from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FPubParaCode = '" + this.pubParaCode + "'";
            tmpValue = dbFun.GetValuebySql(strSql);
            if (tmpValue.trim().length() > 0) {
                throw new YssException("参数编号【" + this.pubParaCode + "】已被参数编号【" +
                                       tmpValue + "】占用，请重新输入参数编号");
            }
        }
        if (!this.orderCode.equalsIgnoreCase(oldOrderCode)) {
            strSql = "select FOrderCode from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FOrderCode = '" + dbFun.treeBuildOrderCode(
                    pub.yssGetTableName("TB_PFOper_PUBPARA"), "FPubParaCode",
                    this.paraGroupCode, Integer.parseInt(orderCode)) + "'";
            tmpValue = dbFun.GetValuebySql(strSql);
            if (tmpValue.trim().length() > 0) {
                throw new YssException("排序号【" + this.orderCode +
                                       "】已被【" + tmpValue + "】占用，请重新输入排序号");
            }
        }
    }

    /**
     * checkSetting
     */
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
            throw new YssException("审核通用参数类型设定出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "delete from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FPubParaCode=" + dbl.sqlString(this.oldPubParaCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除通用参数类型设定出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        if (!this.orderCode.equalsIgnoreCase(oldOrderCode)) {
            dbFun.treeAdjustOrder(pub.yssGetTableName("TB_PFOper_PUBPARA"),
                                  "FPubParaCode",
                                  this.oldPubParaCode, Integer.parseInt(orderCode));
        }
        dbFun.treeAdjustParentCode(pub.yssGetTableName("TB_PFOper_PUBPARA"),
                                   "FParaGroupCode",
                                   this.oldPubParaCode, this.pubParaCode);
        try {
            conn = dbl.loadConnection();
            strSql = "delete from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FPubParaCode=" + dbl.sqlString(this.oldPubParaCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            addSetting();
        } catch (Exception e) {
            throw new YssException("修改通用参数类型设定出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public void setAttr(ResultSet rs) throws SQLException {
        this.pubParaCode = rs.getString("FPubParaCode");
        this.pubParaName = rs.getString("FPubParaName");
        this.paraGroupCode = rs.getString("FParaGroupCode");
        this.paraId = rs.getInt("FParaId");
        this.ctlGrpCode = rs.getString("FCtlGrpCode");
        //this.ctlCode = rs.getString("FCtlCode");
        //this.ctlValue = rs.getString("FCtlValue");
        //this.iDetail = rs.getInt("FDetail");
        this.orderCode = rs.getString("FOrderCode");
        this.desc = rs.getString("FDesc");
        //this.ctlParas =
        //super.setRecLog(rs);
    }

    public void setManagerAttr(ResultSet rs) throws SQLException {
        this.pubParaCode = rs.getString("FPubParaCode");
        this.pubParaName = rs.getString("FPubParaName");
        this.paraGroupCode = rs.getString("FParaGroupCode");
        this.paraId = rs.getInt("FParaId");
        this.ctlGrpCode = rs.getString("FCtlGrpCode");
        //this.ctlCode = rs.getString("FCtlCode");
        //this.ctlValue = rs.getString("FCtlValue");
        //this.iDetail = rs.getInt("FDetail");
        this.orderCode = rs.getString("FOrderCode");
        this.desc = rs.getString("FDesc");
        //this.ctlParas =
        super.setRecLog(rs);
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = " where 1=1 ";
        if (this.filterType != null) {
            if (this.filterType.pubParaCode.length() != 0) { //参数编号
                sResult = sResult + " and a.FPubParaCode = " +
                    dbl.sqlString(this.pubParaCode);
            }
            if (this.filterType.pubParaName.length() != 0) { //参数名称
                sResult = sResult + " and a.FPubParaName like '" +
                    filterType.pubParaName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.paraGroupCode.length() != 0) { //参数组编号
                sResult = sResult + " and a.FParaGroupCode = " +
                    dbl.sqlString(this.paraGroupCode);
            }
            if (this.filterType.paraId != 0) { //参数值编号
                sResult = sResult + " and a.FParaId = " + this.paraId;
            }
            if (this.filterType.ctlGrpCode.length() != 0) { //控件组代码
                sResult = sResult + " and a.FCtlGrpCode = " +
                    dbl.sqlString(this.ctlGrpCode);
            }
            //if (this.filterType.iDetail != 0) { //是否为叶节点
            //sResult = sResult + " and a.FDetail = " + this.iDetail;
            //}
            if (this.filterType.orderCode.length() != 0) { //排序编号
                sResult = sResult + " and a.FOrderCode = " +
                    dbl.sqlString(this.orderCode);
            }
            if (this.filterType.desc.length() != 0) { //描述
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
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
    public String getTreeViewData1() throws YssException {
        String strSql = "";
        String result = "";
        ResultSet rs = null;
        try {
            strSql = "select distinct(a.FPubParaCode) as FPubParaCode,b.FPubParaName as FPubParaName,a.FParaGroupCode as FParaGroupCode" +
                ",b.FCtlGrpCode as FCtlGrpCode,b.FOrderCode as FOrderCode,b.FDesc as FDesc,b.FParaId as FParaId" +
                ",c.FPubParaName as FParaGroupName,d.FCtlGrpName as FCtlGrpName from " +
                "(select FPubParaCode,FParaGroupCode from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " group by FPubParaCode,FParaGroupCode) a left join " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " b on a.FPubParaCode = b.FPubParaCode left join (select * from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                "  where FParaGroupCode in (select FPubParaCode from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                ")) c on a.FPubParaCode = c.FPubParaCode left join (select FCtlGrpCode,FCtlGrpName from Tb_PFSys_FaceCfgInfo" +
                " where FCtlGrpCode in (select FCtlGrpCode from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                ") group by FCtlGrpCode,FCtlGrpName) d on b.FCtlGrpCode = d.FCtlGrpCode where b.FParaId = 0 order by b.FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.pubParaCode = rs.getString("FPubParaCode") + "";
                this.pubParaName = rs.getString("FPubParaName") + "";
                this.paraGroupCode = rs.getString("FParaGroupCode") + "";
                this.ctlGrpCode = rs.getString("FCtlGrpCode");
                //this.iDetail = rs.getInt("FDetail");
                this.paraId = rs.getInt("FParaId");
                this.orderCode = Integer.parseInt(rs.getString("FOrderCode").
                                                  substring(rs.getString(
                    "FOrderCode").length() - 3)) + "";
                this.desc = rs.getString("FDesc") + "";
                this.paraGroupName = rs.getString("FParaGroupName") + "";
                this.ctlGrpName = rs.getString("FCtlGrpName") + "";
                result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (result.length() > 2) {
                return result.substring(0, result.length() - 2);
            } else {
                return "";
            }
        } catch (Exception ex) {
            throw new YssException("获取所有通用参数类型设定出错", ex);
        }
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
        buf.append(this.pubParaCode).append("\t");
        buf.append(this.pubParaName).append("\t");
        buf.append(this.paraGroupCode).append("\t");
        buf.append(this.paraGroupName).append("\t");
        buf.append(this.paraId).append("\t");
        buf.append(this.ctlGrpCode).append("\t");
        buf.append(this.ctlGrpName).append("\t");
        //buf.append(this.iDetail).append("\t");
        buf.append(this.orderCode).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(buildCtlCodes(hCtlCodes));
        //buf.append(this.ctlParas).append("\t");
        return buf.toString();
    }

    private String buildCtlCodes(ArrayList ctlCodes) throws YssException {
        CtlParaBean ctlPara = null;
        StringBuffer buf = null;
        String reStr = "";
        try {
            if (ctlCodes.size() == 0) {
                return "";
            }
            buf = new StringBuffer();
            for (int i = 0; i < ctlCodes.size(); i++) {
                ctlPara = (CtlParaBean) ctlCodes.get(i);
                buf.append(ctlPara.buildRowStr()).append("\r\n");
            }
            if (buf.length() > 0) {
                reStr = buf.toString();
            }
            reStr = reStr.substring(0, reStr.length() - 2);
        } catch (Exception e) {
            throw new YssException("编译通用参数类型设定出错", e);
        }
        return reStr;
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        String reStr = "";
        try {
            if (sType.equalsIgnoreCase("showfirst")) {
                reStr = showFirst();
            } else if (sType.equalsIgnoreCase("show")) { //显示含控件及其值的listView的item
                reStr = show();
            } else if (sType.equalsIgnoreCase("add")) {
                addCtls();
                reStr = show();
            } else if (sType.equalsIgnoreCase("del")) {
                delCtls(true);
                reStr = show();
            } else if (sType.equalsIgnoreCase("edit")) {
                editCtls();
                reStr = show();
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }

    }

    private String showFirst() throws YssException {
        String reStr = "";
        String header = "参数编号\t参数名称\t参数值编号";
        reStr = header + "\r\f" + "" + "\r\f" + "";
        return reStr;
    }

    private String show() throws YssException, SQLException {
        String sqlStr = "";
        ResultSet rs = null;
        ResultSet groupRs = null;
        String sqlGroupStr = "";
        String Header = "";
        String subHeader = "";
        String subshowData = "";
        String showData = "";
        String sAllDataStr = "";
        String ctls = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer allData = new StringBuffer();
        CtlParaBean ctlPara = null;
        boolean showHeader = false;
        try {
            Header = "参数编号\t参数名称\t参数值编号";
            this.hCtlCodes.clear();
            sqlGroupStr = "select FPubParaCode,FPubParaName,FParaGroupCode,FCtlGrpCode,FDesc,FParaId,FOrderCode from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FPubParaCode = " +
                dbl.sqlString(this.filterType.pubParaCode) +
                " and FParaGroupCode = " +
                dbl.sqlString(this.filterType.paraGroupCode) +
                " and FCtlGrpCode = " + dbl.sqlString(this.filterType.ctlGrpCode) +
                //" and FParaId <> 0 " +
                " group by FPubParaCode,FPubParaName,FParaGroupCode,FCtlGrpCode,FDesc,FParaId,FOrderCode order by FParaId";
            groupRs = dbl.openResultSet(sqlGroupStr); //为了以参数值编号进行循环
//---------------------------------------------------------------------------------------------------------------
            while (groupRs.next()) {
                //if (groupRs.getInt("FParaId") != 0) {
                //bufShow.append(super.buildRowShowStr(groupRs,
                //this.getListView1ShowCols())).
                //append(YssCons.
                //YSS_ITEMSPLITMARK1); //只包括那些固定的列
                //}
                if (groupRs.getInt("FParaId") == 0) {
                    continue;
                } else {
                    bufShow.append(super.buildRowShowStr(groupRs,
                        this.getListView1ShowCols())).
                        append(YssCons.
                               YSS_ITEMSPLITMARK1); //只包括那些固定的列

                }
                //----------------------------------------------------------------------------------
                //开始以每个参数值编号获取控件和其值
                /*sqlStr = "select FPubParaCode,FPubParaName,FParaGroupCode,FParaId," +
                      "pubpara.FCtlGrpCode,FDesc," +
                      dbl.sqlIsNull("pubpara.FCtlCode", "face.FCtlCode") +
                      " as FCtlCode, " +
                      dbl.sqlIsNull("pubpara.FCtlValue", dbl.sqlString(" ")) +
                      " as FCtlValue from " +
                 "(select * from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                      " where FPubParaCode = " +
                      dbl.sqlString(this.filterType.pubParaCode) +
                      " and FParaGroupCode = " +
                      dbl.sqlString(this.filterType.paraGroupCode) +
                      " and FCtlGrpCode = " +
                      dbl.sqlString(this.filterType.ctlGrpCode) +
                      " and FParaId = " + groupRs.getInt("FParaId") +
                      ") pubpara left join " +
                 " (select FCtlGrpCode,FCtlCode from Tb_PFSys_FaceCfgInfo where FCheckState =1 )" +
                      " face on pubpara.FCtlGrpCode = face.FCtlGrpCode";*/
                sqlStr = "select * from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                    " where FPubParaCode = " +
                    dbl.sqlString(this.filterType.pubParaCode) +
                    " and FParaGroupCode = " +
                    dbl.sqlString(this.filterType.paraGroupCode) +
                    " and FCtlGrpCode = " +
                    dbl.sqlString(this.filterType.ctlGrpCode) +
                    " and FParaId = " + groupRs.getInt("FParaId");
                rs = dbl.openResultSet(sqlStr);
                while (rs.next()) {
                    subHeader +=
                        //"控件" + rs.getString("FCtlCode") +
                        //YssCons.YSS_ITEMSPLITMARK1 +
                        "控件" +
                        rs.getString("FCtlCode") + "值" +
                        YssCons.YSS_ITEMSPLITMARK1; //循环获取控件的个数,以确定列数.
                    subshowData +=
                        //rs.getString("FCtlCode") +
                        //YssCons.YSS_ITEMSPLITMARK1 +
                        rs.getString("FCtlValue") + YssCons.YSS_ITEMSPLITMARK1; //循环获取需要显示的控件和其值.
                    //ctls += rs.getString("FCtlCode") + "\b" +
                    //rs.getString("FCtlValue") + "\r\n";
                    //------------------控件和其值,放入ctls属性,在前台时解析成hashtable放入listview的tag中。
                    ctlPara = new CtlParaBean();
                    ctlPara.setCtlCode(rs.getString("FCtlCode"));
                    ctlPara.setCtlValue(rs.getString("FCtlValue"));
                    ctlPara.setCtlInd("");
                    hCtlCodes.add(ctlPara);
                    //------------------------------------------------------------------------------
                }
                if (subHeader.length() > 0 && !showHeader) {
                    subHeader = subHeader.substring(0, subHeader.length() - 1);
                    Header = Header + YssCons.YSS_ITEMSPLITMARK1 + subHeader;
                    showHeader = true;
                    //subshowData = subshowData.substring(0, subshowData.length() - 1);
                    //this.ctlParas = ctls.substring(0, ctls.length() - 2); //为了在tag中能获取。
                }
                if (subshowData.length() > 0) {
                    subshowData = subshowData.substring(0, subshowData.length() - 1);
                }
                //if (subHeader.length() > 0 && showHeader) { //将固定的列和循环获取的列相衔接。
                //Header = Header + YssCons.YSS_ITEMSPLITMARK1 + subHeader;
                //}
                setAttr(groupRs);
                bufShow.append(subshowData).append(YssCons.YSS_LINESPLITMARK); //将控件和其值加入listView中。
                allData.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
                this.hCtlCodes.clear();
                subshowData = "";
            }
//--------------------------------------------------------------------------------------------------------
            if (bufShow.toString().length() > 2) {
                showData = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (allData.toString().length() > 2) {
                sAllDataStr = allData.toString().substring(0,
                    allData.toString().length() - 2);
            }
            return Header + "\r\f" + showData + "\r\f" + sAllDataStr;
            // + "\r\f" +
            // this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("显示数据出错！");
        } finally {
            dbl.closeResultSetFinal(groupRs);
            dbl.closeResultSetFinal(rs);
        }
    }

    private void addCtls() throws YssException {
        String sqlStr = "";
        java.sql.PreparedStatement ptmt = null;
        java.sql.Connection conn = dbl.loadConnection();
        CtlParaBean ctlpara = null;
        boolean bTrans = false;
        try {
            sqlStr = "insert into " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " (FPubParaCode,FPubParaName,FParaGroupCode,FParaId,FCtlGrpCode," +
                "FCtlCode,FCtlValue,FOrderCode,FDesc) values(" +
                "?,?,?,?,?" +
                ",?,?,?,?)";
            ptmt = conn.prepareStatement(sqlStr);
            conn.setAutoCommit(false);
            bTrans = true;
            //this.parseCtlCodes(this.ctlParas);//获取所有的控件及其值，以便循环的插入含控件及其值得记录
            //if (hCtlCodes.size() > 0) {
            for (int paras = 0; paras < hCtlCodes.size(); paras++) {
                //Iterator it = hCtlCodes.iterator();
                //while (it.hasNext()) {
                ctlpara = (CtlParaBean) hCtlCodes.get(paras);
                ptmt.setString(1, this.pubParaCode); //每条记录的相同的字段
                ptmt.setString(2, this.pubParaName);
                ptmt.setString(3, this.paraGroupCode);
                ptmt.setInt(4, this.paraId);
                ptmt.setString(5, this.ctlGrpCode);
                ptmt.setString(6, ctlpara.getCtlCode());
                ptmt.setString(7, ctlpara.getCtlValue());
                //ptmt.setInt(8, this.iDetail);
                ptmt.setInt(8, Integer.parseInt(this.orderCode));
                ptmt.setString(9, this.desc == null ? "" : this.desc);
                ptmt.executeUpdate();
                //}
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("插入控件值出错！");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    private void delCtls(boolean commit) throws YssException {
        java.sql.Connection conn = dbl.loadConnection();
        String sqlStr = "";
        boolean bTrans = false;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            sqlStr = "Delete from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                " and FParaGroupCode = " + dbl.sqlString(this.paraGroupCode) +
                " and FCtlGrpCode = " + dbl.sqlString(this.ctlGrpCode) +
                " and FParaId = " + this.paraId;
            dbl.executeSql(sqlStr);
            if (commit) {
                conn.commit();
            }
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除控件值出错！");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    private void editCtls() throws YssException {
        try {
            delCtls(false);
            addCtls();
        } catch (Exception e) {
            throw new YssException("修改控件值出错！");
        }
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
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.pubParaCode = reqAry[0];
            this.pubParaName = reqAry[1];
            this.paraGroupCode = reqAry[2];
            this.paraId = (reqAry[3].length() > 0 ? Integer.parseInt(reqAry[3]) :
                           0);
            this.ctlGrpCode = reqAry[4];
            //this.iDetail = (reqAry[5].length() > 0 ? Integer.parseInt(reqAry[5]) :
            //0);
            this.orderCode = reqAry[5];
            this.desc = reqAry[6];
            this.ctlParas = reqAry[7];
            this.oldPubParaCode = reqAry[8];
            this.oldParaGroupCode = reqAry[9];
            this.oldParaId = (reqAry[10].length() > 0 ? Integer.parseInt(reqAry[10]) :
                              0);
            this.oldOrderCode = reqAry[11];
            this.parseCtlCodes(this.ctlParas);
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new PubParaBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析通用参数类型设定出错", e);
        }
    }

    private void parseCtlCodes(String ctls) throws YssException {
        CtlParaBean ctlPara = null;
        try {
            if (ctls.length() == 0) {
                return;
            }
            String[] tmpAry = ctls.split("\r\n");
            for (int i = 0; i < tmpAry.length; i++) {
                ctlPara = new CtlParaBean();
                ctlPara.parseRowStr(tmpAry[i]);
                hCtlCodes.add(ctlPara);
            }
        } catch (Exception e) {
            throw new YssException("解析通用参数类型设定出错", e);
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

    public String getCtlGrpCode() {
        return ctlGrpCode;
    }

    public String getCtlGrpName() {
        return ctlGrpName;
    }

    public void setCtlGrpCode(String ctlGrpCode) {
        this.ctlGrpCode = ctlGrpCode;
    }

    public void setCtlGrpName(String ctlGrpName) {
        this.ctlGrpName = ctlGrpName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ArrayList getHCtlCodes() {
        return hCtlCodes;
    }

    public void setHCtlCodes(ArrayList hCtlCodes) {
        this.hCtlCodes = hCtlCodes;
    }

    //public int getIDetail() {
    //return iDetail;
    //}

    //public void setIDetail(int iDetail) {
    //this.iDetail = iDetail;
    //}

    public String getOldParaGroupCode() {
        return oldParaGroupCode;
    }

    public void setOldParaGroupCode(String oldParaGroupCode) {
        this.oldParaGroupCode = oldParaGroupCode;
    }

    public int getOldParaId() {
        return oldParaId;
    }

    public void setOldParaId(int oldParaId) {
        this.oldParaId = oldParaId;
    }

    public String getOldPubParaCode() {
        return oldPubParaCode;
    }

    public void setOldPubParaCode(String oldPubParaCode) {
        this.oldPubParaCode = oldPubParaCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOldOrderCode(String oldOrderCode) {
        this.oldOrderCode = oldOrderCode;
    }

    public String getOldOrderCode() {
        return oldOrderCode;
    }

    public String getParaGroupCode() {
        return paraGroupCode;
    }

    public void setParaGroupCode(String paraGroupCode) {
        this.paraGroupCode = paraGroupCode;
    }

    public void setParaGroupName(String paraGroupName) {
        this.paraGroupName = paraGroupName;
    }

    public String getParaGroupName() {
        return paraGroupName;
    }

    public int getParaId() {
        return paraId;
    }

    public void setParaId(int paraId) {
        this.paraId = paraId;
    }

    public void setPubParaCode(String pubParaCode) {
        this.pubParaCode = pubParaCode;
    }

    public String getPubParaCode() {
        return pubParaCode;
    }

    public String getPubParaName() {
        return pubParaName;
    }

    public void setPubParaName(String pubParaName) {
        this.pubParaName = pubParaName;
    }

    public String getCtlParas() {
        return ctlParas;
    }

    public void setCtlParas(String ctlParas) {
        this.ctlParas = ctlParas;
    }

    public PubParaBean getFilterType() {
        return filterType;
    }

    public void setFilterType(PubParaBean filterType) {
        this.filterType = filterType;
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
