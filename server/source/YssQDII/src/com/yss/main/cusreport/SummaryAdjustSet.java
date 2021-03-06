package com.yss.main.cusreport;

import java.sql.*;
import com.yss.util.*;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import java.util.Date;
import java.math.BigDecimal;

/*
 * Description:
 * 该类主要负责summary的信息操作，主要功能如下:
 * <br>
 * 1.数据的展开金额修改操作
 * </br>
 * </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: Ysstech</p>
 *
 * @author libo
 */
public class SummaryAdjustSet
    extends BaseDataSettingBean implements IDataSetting {
    private String sRecycled = ""; //回收站数据

    private String sCode = ""; //项目代码
    private String sName = ""; //项目名
    private String sCatCode = ""; //品种类型
    private String sSubCatCode = ""; //品种子类型
    private String sCuryCode = ""; //货币代码

    private double dProportion = 0; //调整数据

    private int dSummaryType; //数字

    private String sPortCode = ""; //组合代码
    private java.util.Date dtDate; //日期
    private BigDecimal dBal; //金额
    private BigDecimal dBaseCuryBal; //基础金额

    private String sEntityDate = ""; //实体数据
    private SummaryAdjustSet filterType; //parseRowStr方法中 用于筛选
    public SummaryAdjustSet() {
    }

    public void checkInput(byte btOper) throws YssException {
    }

    public String addSetting() throws YssException {
        return "";
    }

    /**
     * 修改金额的方法
     * 为了方便以后批量处理的扩展，此处使用批量处理的方法处理
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        Connection conn = null;         //数据库连接
        String strSql = "";             //用于存储sql语句的字符串
        String[] arrData = null;        //用于存储每条要更新的数据
        Statement st = null;
        boolean bTrans = true;          //事物控制，默认为true代表自动回滚事物，操作成功后将变为false
        try {
            conn = dbl.loadConnection();    //获取数据库连接
            conn.setAutoCommit(false);      //阻止事物自动提交
            st = conn.createStatement();    //通过连接对象创建Statement对象，用于批处理

            arrData = sEntityDate.split("\r\f"); //解析数据

            for (int i = 0; i < arrData.length; i++) {

                this.parseRowStr(arrData[i]);   //将单条数据解析成对应的类属性

                //通过实体类的属性更新Summary表的数据
                //通过组合代码、日期、项目代码进行匹配，更新金额、基础金额
                strSql = "update " + pub.yssGetTableName("TB_Data_Summary") +
                    " set FBal = " + this.dBal +                    //金额
                    " ,FBaseCuryBal = " + this.dBaseCuryBal +       //基础金额
                    " where FPortCode = " + dbl.sqlString(this.sPortCode) + //组合代码
                    " and FDate= " + dbl.sqlDate(this.dtDate) +     //日期
                    " and FCode = " + dbl.sqlString(this.sCode);    //代码
                st.addBatch(strSql);                                //添加到批处理
            }
            st.executeBatch();

            //提交事物处理
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("修改Summary报表信息出错！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(st);//modified by yeshenghong for CCB security check 20121018 
        }
        return "";
    }

    public void delSetting() throws YssException {
    }

    public void checkSetting() throws YssException {
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public void deleteRecycleData() throws YssException {
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

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

    /**
     * 获取主界面的信息
     * @throws YssException
     */
    public String getListViewData1() throws YssException { //主界面显示
        String sqlStr = ""; //用于存储SQL语句
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer(); //显示字段
        StringBuffer bufAll = new StringBuffer(); //全显示字段

        try {
            sHeader = "组合代码\t日期"; //前台listview头

            //从Summary表中查询组合代码、业务日期，并通过日期、组合代码排序
            sqlStr = "select distinct FPortCode,FDate from " + //组合代码和日期查出来
                pub.yssGetTableName("TB_Data_Summary") + " a" + //Summary数据表
                buildFilterSql() +  //添加筛选条件的过滤，避免将所有数据查询出来 libo 20090704 MS00559:QDV4中保2009年07月03日01_B libo
                " order by FDate,FPortCode ";

            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FPortCode")).append("\t"); //组合代码
                bufShow.append(YssFun.formatDate(rs.getDate("FDate"))).append(YssCons.YSS_LINESPLITMARK); //日期
                setResultSetAttr(rs); //建一个方法
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            //下面两个判断均是为了删除字符串结尾的“\f\f”
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.delete(bufShow.length() - 2, bufShow.length()).toString();
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.delete(bufAll.length() - 2, bufAll.length()).toString();
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + "FPortCode\tFDate"; //把数据返向前台
        } catch (Exception e) {
            throw new YssException("获取Sammay报表数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取对应的修改的信息界面
     * @throws YssException
     */
    public String getListViewData2() throws YssException {
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer(); //显示字段
        StringBuffer bufAll = new StringBuffer(); //全显示字段

        try {
            //=========调整金额数据的显示 保留两位小数，右对齐 MS00559:QDV4中保2009年07月03日01_B libo 20090705==========
            sHeader = "项目代码\t项目名称\t原币金额;R\t港币金额;R";//
            sqlStr =
                "select FCode,FName,FPortCode,FDate,FBal,FBaseCuryBal from " + //项目代码 项目名称 原币金额 港币金额
                pub.yssGetTableName("TB_Data_Summary") + buildFilterSql(); //summary表
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FCode")).append("\t"); //项目代码
                bufShow.append(rs.getString("FName")).append("\t"); //项目名称
                bufShow.append(YssFun.formatNumber(rs.getDouble("FBal"), "#,##0.##")).append("\t"); //原币金额，保留两位小数
                bufShow.append(YssFun.formatNumber(rs.getDouble("FBaseCuryBal"), "#,##0.##")).append("\t").append(YssCons.YSS_LINESPLITMARK); //港币金额，保留两位小数
                setResultSetAttr1(rs); //建一个方法
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            //======================End MS00559====================================================================

            //将两个返回前台的字符串结尾的"\f\f"删除掉
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.delete(bufShow.length() - 2, bufShow.length()).toString();
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.delete(bufAll.length() - 2, bufAll.length()).toString();
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + "FCode\tFName\tFBal\tFBaseCuryBal"; //把数据返向前台
        } catch (Exception e) {
            throw new YssException("获取信息出错", e);
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

    public String getBeforeEditData() throws YssException {
        return "";
    }

    /**
     *解析前台数据
     * @param sRowStr String
     * @throws YssException
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
            this.sRecycled = sRowStr;

            reqAry = sTmpStr.split("\t");
            this.sPortCode = reqAry[0]; //组合代码
            this.dtDate = YssFun.parseDate(reqAry[1]); //日期
            this.dBal = new BigDecimal(reqAry[2].trim().length()==0?"0":reqAry[2]); //金额
            this.dBaseCuryBal = new BigDecimal(reqAry[3].trim().length()==0?"0":reqAry[3]); //基础金额

            this.sCode = reqAry[4]; //项目代码
            this.sName = reqAry[5]; //项目名称

            // super.parseRecLog();/时间等
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new SummaryAdjustSet();
                    this.filterType.setYssPub(pub); //全局的东西
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
                if (sRowStr.split("\r\t").length > 2) {
                    this.sEntityDate = sRowStr.split("\r\t")[2]; //实体传入
                }
            }
        } catch (Exception e) {
            throw new YssException("解析信息出错", e);
        }

    }

    /**
     *传向前台数据
     * @param sRowStr String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();

        buf.append(this.sPortCode.trim()).append("\t");
        buf.append(this.dtDate).append("\t"); //日期
        //=========调整金额数据的显示 只显示两位小数 对BigDecimal数据格式化 MS00559:QDV4中保2009年07月03日01_B libo 20090708==========
        if (this.dBal != null) { //当主界面不取出数据时,不进行格式化
	    buf.append((this.dBal).setScale(2, BigDecimal.ROUND_HALF_UP)).append("\t");
        } else {
            buf.append(this.dBal).append("\t");
        }
        if (this.dBaseCuryBal != null) { //当主界面不取出数据时,不进行格式化
	    buf.append((this.dBaseCuryBal).setScale(2, BigDecimal.ROUND_HALF_UP)).append("\t");
        } else {
            buf.append(this.dBaseCuryBal).append("\t");
       }
        //======================End MS00559====================================================================
        buf.append(this.sCode.trim()).append("\t"); //项目代码
        buf.append(this.sName).append("\t"); //项目名称
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    /**
     * 获取筛选条件与数据
     * @return String
     * @throws YssException
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = sResult + " where 1=1";

            if (this.filterType.sPortCode.length() != 0) { //组合代码
                sResult = sResult + " and FPortCode like '" +
                    filterType.sPortCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.dtDate != null && !YssFun.formatDate(filterType.dtDate, "yyyy-MM-dd").equals("9998-12-31")) { //日期
                sResult = sResult + " and FDate = " + dbl.sqlDate(filterType.dtDate);
            }
        }
        return sResult;
    }
    /**
     * 获得参数值
     * @throws YssException
     */
    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException,
        SQLException {
        this.sPortCode = rs.getString("FPortCode");//组合代码
        this.dtDate = rs.getDate("FDate");//日期
    }
    /**
     * 获得参数值
     * @throws YssException
     */
    public void setResultSetAttr1(ResultSet rs) throws SQLException, YssException,
        SQLException {
        this.sCode = rs.getString("FCode");//代码
        this.sName = rs.getString("FName");//名称
        this.sPortCode = rs.getString("FPortCode");//组合代码
        this.dtDate = rs.getDate("FDate");//日期
        this.dBal = rs.getBigDecimal("FBal");//金额
        this.dBaseCuryBal = rs.getBigDecimal("FBaseCuryBal");//基础金额
    }

    public String getSSubCatCode() {
        return sSubCatCode;
    }

    public double getDProportion() {
        return dProportion;
    }

    public String getSPortCode() {
        return sPortCode;
    }

    public String getSName() {
        return sName;
    }

    public String getSCuryCode() {
        return sCuryCode;
    }

    public String getSCode() {
        return sCode;
    }

    public String getSCatCode() {
        return sCatCode;
    }

    public SummaryAdjustSet getFilterType() {
        return filterType;
    }

    public Date getDtDate() {
        return dtDate;
    }

    public int getDSummaryType() {
        return dSummaryType;
    }

    public BigDecimal getDBaseCuryBal() {
        return dBaseCuryBal;
    }

    public void setDBal(BigDecimal dBal) {
        this.dBal = dBal;
    }

    public void setSSubCatCode(String sSubCatCode) {
        this.sSubCatCode = sSubCatCode;
    }

    public void setDProportion(double dProportion) {
        this.dProportion = dProportion;
    }

    public void setSPortCode(String sPortCode) {
        this.sPortCode = sPortCode;
    }

    public void setSName(String sName) {
        this.sName = sName;
    }

    public void setSCuryCode(String sCuryCode) {
        this.sCuryCode = sCuryCode;
    }

    public void setSCode(String sCode) {
        this.sCode = sCode;
    }

    public void setSCatCode(String sCatCode) {
        this.sCatCode = sCatCode;
    }

    public void setFilterType(SummaryAdjustSet filterType) {
        this.filterType = filterType;
    }

    public void setDtDate(Date dtDate) {
        this.dtDate = dtDate;
    }

    public void setDSummaryType(int dSummaryType) {
        this.dSummaryType = dSummaryType;
    }

    public void setDBaseCuryBal(BigDecimal dBaseCuryBal) {
        this.dBaseCuryBal = dBaseCuryBal;
    }

    public void setSRecycled(String sRecycled) {
        this.sRecycled = sRecycled;
    }

    public void setSEntityDate(String sEntityDate) {
        this.sEntityDate = sEntityDate;
    }

    public BigDecimal getDBal() {
        return dBal;
    }

    public String getSRecycled() {
        return sRecycled;
    }

    public String getSEntityDate() {
        return sEntityDate;
    }

}
