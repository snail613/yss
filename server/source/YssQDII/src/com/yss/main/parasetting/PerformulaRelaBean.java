package com.yss.main.parasetting;

//edit by xuqiji 20090430 :QDV4赢时胜上海2009年04月27日03_B MS00417    基本的几项业务参数设置时某些字段长度在界面输入上未做限制
import java.math.*;
import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class PerformulaRelaBean
    extends BaseDataSettingBean implements IDataSetting {

    private String formulaCode = ""; //比率公式代码
    private java.util.Date rangeDate; //日期范围
    private BigDecimal rangeMoney; //金额范围 //edit by xuqiji 20090430 :QDV4赢时胜上海2009年04月27日03_B MS00417    基本的几项业务参数设置时某些字段长度在界面输入上未做限制
    private String perType = "0"; //比率类型
    private double perValue; //比率值
    private double fixValue; //固定值
    private double leastValue; //最小值
    private double maxValue; //最大值

    private String oldFormulaCode = "";
    private java.util.Date oldRangeDate;
    private String oldRangeMoney = "0";
    private String oldPerType = "0";
    private String sRecycled = "";

    private PerformulaRelaBean filterType;

    public double getLeastValue() {
        return leastValue;
    }

    public String getFormulaCode() {
        return formulaCode;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getPerValue() {
        return perValue;
    }

    public double getFixValue() {
        return fixValue;
    }

    public BigDecimal getRangeMoney() { //edit by xuqiji 20090430 :QDV4赢时胜上海2009年04月27日03_B MS00417    基本的几项业务参数设置时某些字段长度在界面输入上未做限制
        return rangeMoney;
    }

    public void setFixValue(double fixValue) {
        this.fixValue = fixValue;
    }

    public void setLeastValue(double leastValue) {
        this.leastValue = leastValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public void setFormulaCode(String formulaCode) {
        this.formulaCode = formulaCode;
    }

    public void setPerValue(double perValue) {
        this.perValue = perValue;
    }

    public void setRangeMoney(BigDecimal rangeMoney) { //edit by xuqiji 20090430 :QDV4赢时胜上海2009年04月27日03_B MS00417    基本的几项业务参数设置时某些字段长度在界面输入上未做限制
        this.rangeMoney = rangeMoney;
    }

    public PerformulaRelaBean() {
    }

    /**
     * parseRowStr
     * 解析比率公式设置信息
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
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.formulaCode = reqAry[0];
            this.rangeDate = YssFun.toDate(reqAry[1]);
            if (reqAry[2].length() != 0) {
                this.rangeMoney = new BigDecimal(reqAry[2]); //edit by xuqiji 20090430 :QDV4赢时胜上海2009年04月27日03_B MS00417    基本的几项业务参数设置时某些字段长度在界面输入上未做限制
            }
            this.perType = reqAry[3];
            if (reqAry[4].length() != 0) {
                this.perValue = Double.parseDouble(reqAry[4]);
            }
            if (reqAry[5].length() != 0) {
                this.fixValue = Double.parseDouble(reqAry[5]);
            }
            if (reqAry[6].length() != 0) {
                this.leastValue = Double.parseDouble(reqAry[6]);
            }
            if (reqAry[7].length() != 0) {
                this.maxValue = Double.parseDouble(reqAry[7]);
            }
            super.checkStateId = Integer.parseInt(reqAry[8]);
            this.oldFormulaCode = reqAry[9];
            this.oldRangeDate = YssFun.toDate(reqAry[10]);
            this.oldRangeMoney = reqAry[11];
            this.oldPerType = reqAry[12];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new PerformulaRelaBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析比率公式设置请求出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.formulaCode.trim()).append("\t");
        buf.append(YssFun.formatDate(this.rangeDate, YssCons.YSS_DATEFORMAT)).
            append("\t");
        buf.append(this.rangeMoney).append("\t");
        buf.append(this.perType.trim()).append("\t");
        buf.append(this.perValue).append("\t");
        buf.append(this.fixValue).append("\t");
        buf.append(this.leastValue).append("\t");
        buf.append(this.maxValue).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * checkInput
     * 检查比率公式输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
//      dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_Performula_Rela"), "FFormulaCode",
//                             this.formulaCode, this.oldFormulaCode);
    }

    /**
     * saveSetting
     * 更新比率公式设置信息
     * @param btOper byte
     */
    /* public void saveSetting(byte btOper) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
           if (btOper == YssCons.OP_ADD) {
              strSql = "insert into " +
                    pub.yssGetTableName("Tb_Para_Performula_Rela") + "" +
                    " (FFormulaCode,FRangeDate,FRangeMoney,FPerType,FPerValue,FFixValue,FLeastValue,FMaxValue," +
                    "fcheckstate,fcreator,fcreatetime,FCheckUser)" +
                    " values(" + dbl.sqlString(this.formulaCode) + "," +
                    dbl.sqlDate(this.rangeDate) + "," +
                    this.rangeMoney + "," +
                    this.perType + "," +
                    this.perValue + "," +
                    this.fixValue + "," +
                    this.leastValue + "," +
                    this.maxValue + "," +
                    (pub.getSysCheckState()?"0":"1") + "," +
                    dbl.sqlString(this.creatorCode) + "," +
                    dbl.sqlString(this.creatorTime) + "," +
                    (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) + ")";
           }
           else if (btOper == YssCons.OP_EDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
                    " set FFormulaCode = " +
                    dbl.sqlString(this.formulaCode) + ",FRangeDate = " +
                    dbl.sqlDate(this.rangeDate) + ",FRangeMoney = " +
                    this.rangeMoney + ",FPerType = " +
                    this.perType + ",FPerValue = " +
                    this.perValue + ",FFixValue = " +
                    this.fixValue + ",FLeastValue = " +
                    this.leastValue + ",FMaxValue = " +
                    this.maxValue + ",fcheckstate = " +
                    (pub.getSysCheckState()?"0":"1") + ",FCreator = " +
                    this.creatorCode + ",FCreateTime = " +
                    this.creatorTime + ",FCheckUser = " +
                    (pub.getSysCheckState()?"' '":dbl.sqlString(this.creatorCode)) +
                    " where FFormulaCode = " +
                    dbl.sqlString(this.oldFormulaCode) +
                    " and FRangeDate = " +
                    dbl.sqlDate(this.oldRangeDate) +
                    " and FRangeMoney = " +
                    this.oldRangeMoney +
                    " and FPerType = " +
                    this.oldPerType;

           }
           else if (btOper == YssCons.OP_DEL) {
              //删除时将审核标志修改为2
              strSql = "update " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
                    " set fcheckstate = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    " where FFormulaCode = " +
                    dbl.sqlString(this.formulaCode) +
                    " and FRangeDate = " +
                    dbl.sqlDate(this.rangeDate) +
                    " and FRangeMoney = " +
                    this.rangeMoney +
                    " and FPerType = " +
                    this.perType;

           }
           else if (btOper == YssCons.OP_AUDIT) {
              strSql = "update " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
                    " set fcheckstate = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    " where FormulaCode = " +
                    dbl.sqlString(this.formulaCode) +
                    " and FRangeDate = " +
                    dbl.sqlDate(this.rangeDate) +
                    " and FRangeMoney = " +
                    this.rangeMoney +
                    " and FPerType = " +
                    this.perType;
           }
           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
        }
        catch (Exception e) {
           throw new YssException("更新比率公式信息出错", e);
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
            strSql = "insert into " +
                pub.yssGetTableName("Tb_Para_Performula_Rela") + "" +
                " (FFormulaCode,FRangeDate,FRangeMoney,FPerType,FPerValue,FFixValue,FLeastValue,FMaxValue," +
                "fcheckstate,fcreator,fcreatetime,FCheckUser)" +
                " values(" + dbl.sqlString(this.formulaCode) + "," +
                dbl.sqlDate(this.rangeDate) + "," +
                this.rangeMoney + "," +
                this.perType + "," +
                this.perValue + "," +
                this.fixValue + "," +
                this.leastValue + "," +
                this.maxValue + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
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
            throw new YssException("增加比率公式关联设置信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
                " set FFormulaCode = " +
                dbl.sqlString(this.formulaCode) + ",FRangeDate = " +
                dbl.sqlDate(this.rangeDate) + ",FRangeMoney = " +
                this.rangeMoney + ",FPerType = " +
                this.perType + ",FPerValue = " +
                this.perValue + ",FFixValue = " +
                this.fixValue + ",FLeastValue = " +
                this.leastValue + ",FMaxValue = " +
                this.maxValue + ",fcheckstate = " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                this.creatorCode + ",FCreateTime = " +
                this.creatorTime + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FFormulaCode = " +
                dbl.sqlString(this.oldFormulaCode) +
                " and FRangeDate = " +
                dbl.sqlDate(this.oldRangeDate) +
                " and FRangeMoney = " +
                this.oldRangeMoney +
                " and FPerType = " +
                this.oldPerType;
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("修改比率公式关联设置信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
                " set fcheckstate = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                " where FFormulaCode = " +
                dbl.sqlString(this.formulaCode) +
                " and FRangeDate = " +
                dbl.sqlDate(this.rangeDate) +
                " and FRangeMoney = " +
                this.rangeMoney +
                " and FPerType = " +
                this.perType;

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("删除比率公式关联设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**修改时间：2008年3月20号
     *  修改人：单亮
     *  原方法功能：只能处理货币方向的审核和未审核的单条信息。
     *  新方法功能：可以处理货币方向审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     */

    public void checkSetting() throws YssException {
        //修改前的代码
//     String strSql = "";
//     boolean bTrans = false; //代表是否开始了事务
//     Connection conn = dbl.loadConnection();
//     try {
//        strSql = "update " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
//                   " set fcheckstate = " +
//                   this.checkStateId +
//                   ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//                   ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//                   " where FormulaCode = " +
//                   dbl.sqlString(this.formulaCode) +
//                   " and FRangeDate = " +
//                   dbl.sqlDate(this.rangeDate) +
//                   " and FRangeMoney = " +
//                   this.rangeMoney +
//                   " and FPerType = " +
//                   this.perType;
//        conn.setAutoCommit(false);
//        bTrans = true;
//        dbl.executeSql(strSql);
//        conn.commit();
//        bTrans = false;
//        conn.setAutoCommit(true);
        //修改后的代码
        //------------------------------
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            arrData = sRecycled.split("\r\n");
            bTrans = true;
            conn.setAutoCommit(false);

            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);

                strSql = "update " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
                    " set fcheckstate = " +
                    this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    " where FormulaCode = " +
                    dbl.sqlString(this.formulaCode) +
                    " and FRangeDate = " +
                    dbl.sqlDate(this.rangeDate) +
                    " and FRangeMoney = " +
                    this.rangeMoney +
                    " and FPerType = " +
                    this.perType;

                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            //------------------------------
        }

        catch (Exception e) {
            throw new YssException("审核比率公式关联设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        try {
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            this.parseRowStr(sMutilRowAry[0]);
            strSql = "delete from " +
                pub.yssGetTableName("Tb_Para_Performula_Rela") +
                " where FFormulaCode = " +
                dbl.sqlString(this.formulaCode);

            dbl.executeSql(strSql);

            strSql =
                "insert into " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
                " (FFormulaCode,FRangeDate,FRangeMoney," +
                "FPerType,FPerValue,FFixValue,FLeastValue,FMaxValue," +
                "FCheckState,FCreator,FCreateTime) values (?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);

            for (int i = 0; i < sMutilRowAry.length; i++) {
                if (i > 0) {
                    this.parseRowStr(sMutilRowAry[i]);
                }
                if (this.formulaCode.trim().length() > 0) {
                    pstmt.setString(1, this.formulaCode);
                    pstmt.setDate(2, YssFun.toSqlDate(this.rangeDate));
                    pstmt.setBigDecimal(3, this.rangeMoney); //edit by xuqiji 20090430 :QDV4赢时胜上海2009年04月27日03_B MS00417    基本的几项业务参数设置时某些字段长度在界面输入上未做限制
                    pstmt.setString(4, this.perType);
                    pstmt.setDouble(5, this.perValue);
                    pstmt.setDouble(6, this.fixValue);
                    pstmt.setDouble(7, this.leastValue);
                    pstmt.setDouble(8, this.maxValue);
                    pstmt.setInt(9, pub.getSysCheckState() ? 0 : 1);
                    pstmt.setString(10, this.creatorCode);
                    pstmt.setString(11, this.creatorTime);
                    pstmt.executeUpdate();
                }
            }
            return "";
        } catch (Exception e) {
            throw new YssException("保存比率公式出错", e);
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
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
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * getListViewData1
     * 获取比率公式数据
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";

        StringBuffer buf = new StringBuffer(); //用于显示的属性
        StringBuffer buf1 = new StringBuffer(); //所有的属性
        try {
//         sHeader = "日期范围\t金额范围\t比率类型\t比率值\t固定值\t最小值\t最大值";
            sHeader = "日期范围\t金额范围\t比率值\t固定值\t最小值\t最大值";
//               \t状态";
            String sql =
                "select a.*, d.FVocName as FPerTypeValue, b.fusername as fcreatorname,c.fusername as fcheckusername from " +
                pub.yssGetTableName("Tb_Para_Performula_Rela") + " a" +
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                " left join Tb_Fun_Vocabulary d on " + dbl.sqlToChar("a.FPerType") + " = d.FVocCode and d.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_PFM_RL_PERTYPE) +
                " where a.FFormulaCode = " +
                dbl.sqlString(this.filterType.formulaCode) +
                " order by a.FCheckState, a.FCreateTime desc";
            ResultSet rs = dbl.openResultSet(sql);
            while (rs.next()) {
                buf.append(YssFun.formatDate(rs.getDate("FRangeDate"),
                                             YssCons.YSS_DATEFORMAT));
                buf.append("\t");
                buf.append(YssFun.formatNumber(rs.getDouble("FRangeMoney"), "#,##0.############"));
                buf.append("\t");
//            buf.append( (rs.getString("FPerTypeValue") + "").trim());
//            buf.append("\t");
                buf.append(YssFun.formatNumber(rs.getDouble("FPerValue"), "#,##0.############"));
                buf.append("\t");
                buf.append(YssFun.formatNumber(rs.getDouble("FFixValue"), "#,##0.############"));
                buf.append("\t");
                buf.append(YssFun.formatNumber(rs.getDouble("FLeastValue"), "#,##0.############"));
                buf.append("\t");
                buf.append(YssFun.formatNumber(rs.getDouble("FMaxValue"), "#,##0.############"));
                buf.append(YssCons.YSS_LINESPLITMARK);

                setPerformulaRelaAttr(rs);

                buf1.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sShowDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            if (buf1.toString().length() > 2) {
                sAllDataStr = buf1.toString().substring(0,
                    buf1.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();

        } catch (Exception e) {
            throw new YssException("获取比率公式数据出错", e);
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
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
    }

    public void setPerformulaRelaAttr(ResultSet rs) throws SQLException {
        this.formulaCode = rs.getString("FFormulaCode") + "";
        this.rangeDate = rs.getDate("FRangeDate");
        this.rangeMoney = rs.getBigDecimal("FRangeMoney"); //edit by xuqiji 20090430 :QDV4赢时胜上海2009年04月27日03_B MS00417    基本的几项业务参数设置时某些字段长度在界面输入上未做限制
        this.perType = rs.getString("FPerType") + "";
        this.perValue = rs.getDouble("FPerValue");
        this.fixValue = rs.getDouble("FFixValue");
        this.leastValue = rs.getDouble("FLeastValue");
        this.maxValue = rs.getDouble("FMaxValue");
        super.setRecLog(rs);
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
        PerformulaRelaBean befEditBean = new PerformulaRelaBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*, d.FVocName as FPerTypeValue, b.fusername as fcreatorname,c.fusername as fcheckusername from " +
                pub.yssGetTableName("Tb_Para_Performula_Rela") + " a" +
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                " left join Tb_Fun_Vocabulary d on " + dbl.sqlToChar("a.FPerType") + " = d.FVocCode and d.FVocTypeCode = " + //lzp 修改 12。6
                dbl.sqlString(YssCons.YSS_PFM_RL_PERTYPE) +
                " where a.FFormulaCode = " +
                dbl.sqlString(this.oldFormulaCode) +
                " order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.formulaCode = rs.getString("FFormulaCode") + "";
                befEditBean.rangeDate = rs.getDate("FRangeDate");
                befEditBean.rangeMoney = rs.getBigDecimal("FRangeMoney"); //edit by xuqiji 20090430 :QDV4赢时胜上海2009年04月27日03_B MS00417    基本的几项业务参数设置时某些字段长度在界面输入上未做限制
                befEditBean.perType = rs.getString("FPerType") + "";
                befEditBean.perValue = rs.getDouble("FPerValue");
                befEditBean.fixValue = rs.getDouble("FFixValue");
                befEditBean.leastValue = rs.getDouble("FLeastValue");
                befEditBean.maxValue = rs.getDouble("FMaxValue");

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    /**
     * 从回收站删除数据，即是彻底删除
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
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
                strSql = "delete from " + pub.yssGetTableName("Tb_Para_Performula_Rela") +
                    " where FFormulaCode = " +
                    dbl.sqlString(this.formulaCode) +
                    " and FRangeDate = " +
                    dbl.sqlDate(this.rangeDate) +
                    " and FRangeMoney = " +
                    this.rangeMoney +
                    " and FPerType = " +
                    this.perType;

                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("清除数据出错", e);
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
