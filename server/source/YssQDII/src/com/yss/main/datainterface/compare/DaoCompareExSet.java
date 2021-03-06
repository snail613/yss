package com.yss.main.datainterface.compare;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.datainterface.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 * 接口核对配置的POJO类
 * QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090430
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DaoCompareExSet
    extends BaseDataSettingBean implements
    IDataSetting {
    private String sCompCode = ""; //配置源代码
    private String sCompName = ""; //配置源名称
    private String sDpDcCode = ""; //数据源配置代码
    private String sDpDdCode = ""; //数据处理代码
    private String sDesc = ""; //描述
    private String sOldCompCode = ""; //主键
    private DaoCompareExSet filterType;
    private String sCompFields = ""; //保存字段设置的数据
    private String sRecycled = "";
    private DaoCompareField compField; //比对字段配置
    private String sMarkSource = ""; //基准预处理代码   徐启吉 2009 03 16 添加
    private String sTabName = ""; //核对临时表    xuqiji   2009 03 17 添加

    public DaoCompareExSet() {
        compField = new DaoCompareField();
    }

    /**
     * 解析数据，将用户请求的数据解析成对应的对象属性
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String[] reqAry = null;
        sRecycled = sRowStr; //xuqiji  赋值 2009 03 26
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
            this.sRecycled = sRowStr;
            this.sCompCode = reqAry[0];
            this.sCompName = reqAry[1];
            this.sTabName = reqAry[2]; //xuqiji----------------------
            this.sDpDcCode = reqAry[3];
            this.sDpDdCode = reqAry[4];
            this.sDesc = reqAry[5];
            this.sMarkSource = reqAry[6];
            this.sOldCompCode = reqAry[7];
            if (YssFun.isNumeric(reqAry[8])) {
                this.checkStateId = YssFun.toInt(reqAry[8]); //---------------
            }
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DaoCompareExSet();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equals("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
                if (sRowStr.split("\r\t").length > 2) {
                    this.sCompFields = sRowStr.split("\r\t")[2]; //将字段配置信息也传进来
                }
            }
        } catch (Exception e) {
            throw new YssException("解析数据核对信息出错！", e);
        }

    }

    /**
     * 将对象属性拼装成前台可解析的字符串
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(sCompCode).append("\t");
        buf.append(sCompName).append("\t");
        buf.append(sTabName).append("\t"); //xuqiji
        buf.append(sDpDcCode).append("\t");
        buf.append(sDpDdCode).append("\t");
        buf.append(sDesc).append("\t");
        buf.append(sMarkSource).append("\t"); //xuqiji
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * 检查输入的数据是否存在违反规则的
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Dao_Compare"),
                               "FCompCode",
                               this.sCompCode,
                               this.sOldCompCode);
    }

    /**
     * 新增
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans); ;
            bTrans = true;
            sqlStr = "insert into " + pub.yssGetTableName("Tb_Dao_Compare") +
                "(FCOMPCODE,FCOMPNAME,FTABNAME,FDPDCCODE,FDPDDCODE,FDESC,FMARKSOURCE,FCHECKSTATE,FCREATOR,FCREATETIME) values (" + //xuqiji
                dbl.sqlString(this.sCompCode) + "," +
                dbl.sqlString(this.sCompName) + "," +
                dbl.sqlString(this.sTabName) + "," + //核对临时表    xuqiji   2009 03 17 添加
                dbl.sqlString(this.sDpDcCode) + "," +
                dbl.sqlString(this.sDpDdCode) + "," +
                dbl.sqlString(this.sDesc) + "," +
                dbl.sqlString(this.sMarkSource) + "," + //基准预处理代码   徐启吉 2009 03 16 添加
                this.checkStateId + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + ")";
            dbl.executeSql(sqlStr);
            conn.setAutoCommit(bTrans);
            bTrans = false;
            compField.setYssPub(pub);
            compField.creatorCode = this.creatorCode;
            compField.creatorTime = this.creatorTime;
            compField.saveMutliSetting(sCompFields);
        }
        //-------------------徐启吉 2009 03 27-------------------
        catch (Exception ex) {
            throw new YssException("新增数据核对配置出错！");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * 编辑
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans); ;
            bTrans = true;
            sqlStr = "update " + pub.yssGetTableName("Tb_Dao_Compare") +
                " set FCheckState = " + this.checkStateId +
                ",FCOMPCODE=" + dbl.sqlString(this.sCompCode) +
                ",FCOMPNAME=" + dbl.sqlString(this.sCompName) +
                ",FTABNAME=" + dbl.sqlString(this.sTabName) + //核对临时表    xuqiji   2009 03 17 添加
                ",FDPDCCODE=" + dbl.sqlString(this.sDpDcCode) +
                ",FDPDDCODE=" + dbl.sqlString(this.sDpDdCode) +
                ",FDesc=" + dbl.sqlString(this.sDesc) +
                ",FMARKSOURCE=" + dbl.sqlString(this.sMarkSource) + //基准预处理代码   徐启吉 2009 03 16 添加
                ", FCreator = " + dbl.sqlString(pub.getUserCode()) +
                ", FCreateTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FCompCode =" + dbl.sqlString(this.sOldCompCode);
            dbl.executeSql(sqlStr);
            conn.setAutoCommit(bTrans);
            bTrans = false;
            compField.setYssPub(pub);
            compField.setSOldCompCode(this.sOldCompCode);
            compField.creatorCode = this.creatorCode;
            compField.creatorTime = this.creatorTime;
            compField.saveMutliSetting(sCompFields);
        } catch (Exception ex) {
            throw new YssException("修改数据核对配置出错！");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * 删除
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans); ;
            bTrans = true;
            sqlStr = "update " + pub.yssGetTableName("Tb_Dao_Compare") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FCompCode =" + dbl.sqlString(this.sCompCode);
            dbl.executeSql(sqlStr);
            conn.setAutoCommit(bTrans);
            bTrans = false;
            compField.setYssPub(pub);
            compField.checkStateId = this.checkStateId;
            compField.setSCompCode(this.sCompCode);
            compField.delSetting();
        } catch (Exception ex) {
            throw new YssException("删除数据核对配置出错！");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 回收站还原功能
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        String array[] = null;
        try {
            conn = dbl.loadConnection();
            if (sRecycled != "" && sRecycled != null) { //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句 xuqiji 2009 03 26
                array = sRecycled.split("\r\n"); //根据规定的符号，把多个数据分别放入数组  xuqiji 2009 03 26
                for (int i = 0; i < array.length; i++) { //循环执行这些还原语句  xuqiji 2009 03 26
                    if (array[i].length() == 0) { //判断数据长度 xuqiji 2009 03 26
                        continue;
                    }
                    this.parseRowStr(array[i]); //解析数据  xuqiji 2009 03 26
                    conn.setAutoCommit(bTrans); ;
                    bTrans = true;
                    sqlStr = "update " + pub.yssGetTableName("Tb_Dao_Compare") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FCompCode =" + dbl.sqlString(this.sCompCode);
                    dbl.executeSql(sqlStr);
                    compField.setYssPub(pub);
                    compField.checkStateId = this.checkStateId;
                    compField.setSCompCode(this.sCompCode);
                    compField.checkSetting();
                }
            }
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("审核数据核对配置出错！");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 保存多条数据
     * @param sMutilRowStr String
     * @return String
     * @throws YssException
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    /**
     * 获取单条设置信息
     * @return IDataSetting
     * @throws YssException
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Dao_Compare") +
                " where FCompCode=" + dbl.sqlString(this.sCompCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.sCompCode = rs.getString("FCOMPCODE");
                this.sCompName = rs.getString("FCOMPNAME");
                this.sTabName = rs.getString("FTABNAME");
                this.sDpDcCode = rs.getString("FDpDcCode");
                this.sDpDdCode = rs.getString("FDpDdCode");
                this.sDesc = rs.getString("FDesc");
                this.sMarkSource = rs.getString("FMARKSOURCE"); //xuqiji
            }
        } catch (Exception ex) {
            throw new YssException("获取单条核对数据出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    /************
     * 回收站清除功能， xuqiji 2009 03 26
     *
     */
    public void deleteRecycleData() throws YssException {
        String sql = "";
        String array[] = null;
        Connection conn = dbl.loadConnection();
        Statement st = null;
        boolean bTrans = true;
        try {
            if (sRecycled != "" && sRecycled != null) { //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
                st = conn.createStatement();
                array = sRecycled.split("\r\n"); //根据规定的符号，把多个数句分别放入数组
                for (int i = 0; i < array.length; i++) { //循环执行这些删除语句
                    if (array[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(array[i]);
                    bTrans = true;
                    sql = "delete from " + pub.yssGetTableName("Tb_Dao_Compare") + //此SQL清除表Tb_Dao_Compare中的数据
                        " where FCompCode=" + dbl.sqlString(this.sCompCode);
                    st.addBatch(sql);
                    sql = "delete from " + pub.yssGetTableName("Tb_Dao_Compfield") + //此SQL清除关联表Tb_Dao_Compfield中的数据
                        " where FCompCode=" + dbl.sqlString(this.sCompCode);
                    st.addBatch(sql);
                }
                conn.setAutoCommit(false);
                st.executeBatch(); //批处理删除
                conn.commit();
                conn.setAutoCommit(true);
                bTrans = false;
            }
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(conn, bTrans);
        }
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

    /**
     * 通过结果集给字段赋值
     * @param rs ResultSet
     * @throws SQLException
     */
    private void setFieldValue(ResultSet rs) throws SQLException {
        this.sCompCode = rs.getString("FCOMPCODE");
        this.sCompName = rs.getString("FCOMPNAME");
        this.sTabName = rs.getString("FTABNAME");
        this.sDpDcCode = rs.getString("FDpDcCode");
        this.sDpDdCode = rs.getString("FDpDdCode");
        this.sDesc = rs.getString("FDesc");
        this.sMarkSource = rs.getString("FMARKSOURCE"); //xuqiji
        super.setRecLog(rs);
    }

    /**
     * 创建筛选的sql语句
     * @return String
     * @throws YssException
     */
    private String builerFilter() throws YssException {
        String filterStr = "";
        if (filterType != null) {
            filterStr = " where 1=1 ";
            if (filterType.sCompCode != null &&
                filterType.sCompCode.trim().length() != 0) {
                filterStr += " and a.FCompCode like '" +
                    filterType.sCompCode.replaceAll("'", "''") + "%'";
            }
            // add by guolongchao BUG21781 增加可按照 ：(配置源名称，核对临时表名称，描述 ) 这三个条件进行筛选查找   
            if (filterType.sCompName!= null &&
                    filterType.sCompName.trim().length() != 0) {
                    filterStr += " and a.FCompName like '" +
                        filterType.sCompName.replaceAll("'", "''") + "%'";
                }
            if (filterType.sTabName!= null &&
                    filterType.sTabName.trim().length() != 0) {
                    filterStr += " and a.FTabName like '" +
                        filterType.sTabName.replaceAll("'", "''") + "%'";
                }
            if (filterType.sDesc!= null &&
                    filterType.sDesc.trim().length() != 0) {
                    filterStr += " and a.FDesc like '" +
                        filterType.sDesc.replaceAll("'", "''") + "%'";
                }
        }
//        return "";
        return filterStr;    //update by guolongchao 20110714 BUG21781  原来代码返回值为空串，导致筛选功能失效
    }

    /**
     * 获取ListView中的数据
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
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
            sqlStr =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Dao_Compare") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                builerFilter() +
                " order by a.FCheckState, a.FCheckTime desc, a.FCreateTime desc";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                setFieldValue(rs);
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_DAO_ACCOUNTTYPE + "," +
                                        YssCons.YSS_DAO_DSTYPE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取接口数据核对配置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 遍历核对数据  xuqiji 2009 03 19
     * @return String
     * @throws YssException
     */
    public String getListViewData2() throws YssException {
        String sHeader = "核对配置代码\t核对配置名称";
        String strSql = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Dao_compare") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " where a.FCheckState=1 " + (sCompCode.length() == 0 ? " " : " and FcompCode in(" + operSql.sqlCodes(this.sCompCode) + ")"); //"and FcompCode in("+operSql.sqlCodes(this.sCompCode)+")";
            //sql语句中，增加“配置源代码”（sCompCode）的判断，如果长度为0就传空值
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                setFieldValue(rs); //保存数据
                bufShow.append(rs.getString("FcompCode")).append("\t");
                bufShow.append(rs.getString("FcompName")).append(YssCons.YSS_LINESPLITMARK);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            //防止索引越界
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception ex) {
            throw new YssException("获取接口数据核对配置出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    /**
     * 返回基准预处理的数据源
     * @return String
     * @throws YssException
     */
    public String getMarkPrepSQL() throws YssException {
        String sSQLRes = ""; //返回基准数据源的SQL
        DaoPretreatBean daoPret = null;
        try {
            this.getSetting();
            daoPret = new DaoPretreatBean();
            daoPret.setYssPub(pub);
            daoPret.setDPDsCode(this.sMarkSource);
            daoPret.getSetting();
            sSQLRes = daoPret.getDataSource();
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
        return sSQLRes;
    }

    public String getSOldCompCode() {
        return sOldCompCode;
    }

    public String getSDpDdCode() {
        return sDpDdCode;
    }

    public String getSDpDcCode() {
        return sDpDcCode;
    }

    public String getSDesc() {
        return sDesc;
    }

    public String getSCompName() {
        return sCompName;
    }

    public String getSCompFields() {
        return sCompFields;
    }

    public String getSCompCode() {
        return sCompCode;
    }

    public DaoCompareExSet getFilterType() {
        return filterType;
    }

    public void setCompField(DaoCompareField compField) {
        this.compField = compField;
    }

    public void setSOldCompCode(String sOldCompCode) {
        this.sOldCompCode = sOldCompCode;
    }

    public void setSDpDdCode(String sDpDdCode) {
        this.sDpDdCode = sDpDdCode;
    }

    public void setSDpDcCode(String sDpDcCode) {
        this.sDpDcCode = sDpDcCode;
    }

    public void setSDesc(String sDesc) {
        this.sDesc = sDesc;
    }

    public void setSCompName(String sCompName) {
        this.sCompName = sCompName;
    }

    public void setSCompFields(String sCompFields) {
        this.sCompFields = sCompFields;
    }

    public void setSCompCode(String sCompCode) {
        this.sCompCode = sCompCode;
    }

    public void setFilterType(DaoCompareExSet filterType) {
        this.filterType = filterType;
    }

    public DaoCompareField getCompField() {
        return compField;
    }

    public void setSMarkSource(String sMarkSource) {
        this.sMarkSource = sMarkSource;
    }

    public String getSMarkSource() {
        return sMarkSource;
    }

    public void setSTabName(String sTabName) {
        this.sTabName = sTabName;
    }

    public String getSTabName() {
        return sTabName;
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
